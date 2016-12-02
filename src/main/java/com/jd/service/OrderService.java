package com.jd.service;

import com.jd.Constant;
import com.jd.entity.Order;
import com.jd.entity.Quantity;
import com.jd.rowmapper.OrderMapper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by hanlei6 on 16-11-24.
 */
@Service
public class OrderService {
    private final static Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Value("${zk.url}")
    private String url;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private QuantityService quantityService;
//    @Autowired
//    private CuratorFramework client;

    public Order get(String id) {
        List<Order> orders = jdbcTemplate.query("SELECT t1.id,t1.sku,t1.quantity,t2.name AS skuName, created, modified FROM mall_order t1 INNER JOIN mall_sku t2 ON t1.sku = t2.id WHERE t1.id=?", preparedStatement -> {
            preparedStatement.setString(1, id);
        }, new OrderMapper());
        if (orders.isEmpty()) {
            return null;
        }
        return orders.get(0);
    }

    public List<Order> findAll() {
        List<Order> orders = jdbcTemplate.query("SELECT t1.id,t1.sku,t1.quantity,t2.name AS skuName, created, modified FROM mall_order t1 INNER JOIN mall_sku t2 ON t1.sku = t2.id ORDER BY created DESC", new OrderMapper());
        return orders;
    }

    public int insert(String thread, Order order) {
//        logger.info("begin: {}", new SimpleDateFormat("yyyy-MM HH:mm:ss.sss").format(new Date()));
        try {
            ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
            CuratorFramework client = CuratorFrameworkFactory.builder().connectString(url).retryPolicy(retryPolicy)
                    .connectionTimeoutMs(30000).build();
            client.start();
            String myselfPath = client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(Constant.ZK_PATH_SKU + order.getSku() + "/" + Constant.WRITE);
            List<String> children = client.getChildren().forPath(Constant.ZK_PATH_SKU + order.getSku());
            Collections.sort(children);
            String[] nodeNames = myselfPath.split("/");
            String shortPath = nodeNames[nodeNames.length - 1];
            int index = children.indexOf(shortPath);

            if (index == 0) { //得到锁
                boolean flag = doInsert(thread, order);
                if (flag) {
                    logger.info("{} is inserted.", shortPath);
                } else {
                    logger.info("because the quantity is not enough, {} fails to insert.and the children's size is {}", shortPath, children.size());
                }
                client.close();
            } else { // 监听小于自己的节点集合中最大序号的节点
                String lastWriteNodeBeforeMe = children.get(index - 1);
                logger.info("{} is watching {}.", shortPath, lastWriteNodeBeforeMe);
                try {
                    NodeCache nodeCache = new NodeCache(client, Constant.ZK_PATH_SKU + order.getSku() + "/" + lastWriteNodeBeforeMe, false);
                    nodeCache.getListenable().addListener(() -> {
                        Stat stat = client.checkExists().forPath(Constant.ZK_PATH_SKU + order.getSku() + "/" + lastWriteNodeBeforeMe);
                        if (stat == null) {
                            boolean flag = doInsert(thread, order);
                            if (flag) {
                                logger.info("{} is inserted after {}.", shortPath, lastWriteNodeBeforeMe);
                            } else {
                                logger.info("because the quantity is not enough, {} fails to insert by watching {}.", shortPath, lastWriteNodeBeforeMe);
                            }
                            client.close();
                        }
                    });
                    Stat stat = client.checkExists().forPath(Constant.ZK_PATH_SKU + order.getSku() + "/" + lastWriteNodeBeforeMe);
                    if (stat != null) {
                        logger.info("stat is {}.", stat);
                        nodeCache.start();
                    }else {
                        boolean flag = doInsert(thread, order);
                        if (flag) {
                            logger.info("{} is inserted after {}.", shortPath, lastWriteNodeBeforeMe);
                        } else {
                            logger.info("because the quantity is not enough, {} fails to insert by {} is {}.", shortPath, lastWriteNodeBeforeMe, stat);
                        }
                        client.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private boolean doInsert(String thread, Order order) {
        order.setId(UUID.randomUUID().toString());
        Quantity quantity = quantityService.get(order.getSku());
        if (quantity.getQuantity().intValue() - order.getQuantity().intValue() >= 0) {
            return transactionTemplate.execute(transactionStatus -> {
                try {
                    quantity.setQuantity(quantity.getQuantity() - order.getQuantity());
                    quantityService.update(quantity);
                    jdbcTemplate.update("INSERT INTO mall_order(`id`,`sku`,`quantity`,`created`,`modified`) VALUES (?,?,?,now(),now())", preparedStatement -> {
                        preparedStatement.setString(1, order.getId());
                        preparedStatement.setString(2, order.getSku());
                        preparedStatement.setInt(3, order.getQuantity());
                    });
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    transactionStatus.setRollbackOnly();
                    return false;
                }
            });
        } else {
            return false;
        }
    }

    public int update(Order order) {
        int value = jdbcTemplate.update("UPDATE mall_order SET quantity=? WHERE id=?", preparedStatement -> {
            preparedStatement.setInt(1, order.getQuantity());
            preparedStatement.setString(2, order.getId());
        });
        return value;
    }

    public int delete(String id) {
        int value = jdbcTemplate.update("DELETE FROM mall_order WHERE id=?", preparedStatement -> {
            preparedStatement.setString(1, id);
        });
        return value;
    }
}
