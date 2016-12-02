package com.jd.service;

import com.jd.Constant;
import com.jd.entity.Quantity;
import com.jd.entity.Sku;
import com.jd.rowmapper.SkuMapper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
 * Created by hanlei6 on 16-11-24.
 */
@Service
public class SkuService {
    @Value("${zk.url}")
    private String url;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private QuantityService quantityService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    public Sku get(String id) {
        List<Sku> skus = jdbcTemplate.query("SELECT id,sku,quantity from mall_sku t1 INNER JOIN mall_quantity t2 ON t1.id = t2.sku WHERE id=?", preparedStatement -> {
            preparedStatement.setString(1, id);
        }, new SkuMapper());
        if (skus.isEmpty()) {
            return null;
        }
        return skus.get(0);
    }

    public List<Sku> findAll() {
        List<Sku> skus = jdbcTemplate.query("SELECT id,sku,quantity from mall_sku t1 INNER JOIN mall_quantity t2 ON t1.id = t2.sku WHERE id=?", new SkuMapper());
        return skus;
    }

    public int insert(Sku sku) {
        int value = transactionTemplate.execute(transactionStatus -> {
            try {
                jdbcTemplate.update("INSERT INTO mall_sku VALUES (?,?)", preparedStatement -> {
                    preparedStatement.setString(1, sku.getId());
                    preparedStatement.setString(2, sku.getName());
                });
                Quantity quantity = quantityService.get(sku.getId());
                if (quantity != null) {
                    quantity.setQuantity(quantity.getQuantity() + sku.getQuantity());
                    quantityService.update(quantity);
                } else {
                    quantity = new Quantity();
                    quantity.setQuantity(sku.getQuantity());
                    quantity.setSku(sku.getId());
                    quantityService.insert(quantity);
                }
                return 1;
            } catch (Exception e) {
                e.printStackTrace();
                transactionStatus.setRollbackOnly();
                return 0;
            }
        });
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().connectString(url).retryPolicy(retryPolicy)
                .connectionTimeoutMs(30000).build();
        curatorFramework.start();
        try {
            curatorFramework.create().creatingParentContainersIfNeeded().forPath(Constant.ZK_PATH_SKU + sku.getId(), sku.getId().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(curatorFramework);
        }
        return value;
    }

    public int delete(Sku sku) {
        int value = jdbcTemplate.update("DELETE from mall_sku WHERE id=?", preparedStatement -> {
            preparedStatement.setString(1, sku.getId());
        });
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().connectString(url).retryPolicy(retryPolicy)
                .connectionTimeoutMs(30000).build();
        curatorFramework.start();
        try {
            curatorFramework.delete().inBackground().forPath(Constant.ZK_PATH_SKU + sku.getId());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(curatorFramework);
        }
        return value;
    }
}
