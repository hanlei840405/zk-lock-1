package com.jd.controller;

import com.jd.Constant;
import com.jd.entity.Quantity;
import com.jd.entity.Sku;
import com.jd.service.QuantityService;
import com.jd.service.SkuService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Created by hanlei6 on 16-11-29.
 */
@RestController
@RequestMapping("/sku")
public class SkuController {
    @Autowired
    private SkuService skuService;
    @Autowired
    private QuantityService quantityService;

    @RequestMapping("/addSku")
    public String addSku(String name, @RequestParam int quantity) {
        Sku sku = new Sku();
        sku.setId(UUID.randomUUID().toString());
        sku.setName(name);
        sku.setQuantity(quantity);
        skuService.insert(sku);
        return sku.getId();
    }

    @RequestMapping("/updateSkuQuantity")
    public String updateSkuQuantity(String sku, @RequestParam int quantity) {
        Quantity quantity1 = new Quantity();
        quantity1.setQuantity(quantity);
        quantity1.setSku(sku);
        quantityService.update(quantity1);
        return sku;
    }

    @RequestMapping("/test")
    public String test() throws Exception {

        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181").retryPolicy(retryPolicy)
                .connectionTimeoutMs(30000).build();
        client.start();

        NodeCache nodeCache = new NodeCache(client, "/aaaa", false);
        nodeCache.getListenable().addListener(() -> {
            Stat stat = client.checkExists().forPath("/aaaa");
            if (stat == null) {
                client.close();
            }
        });
        if (nodeCache.getCurrentData() != null) {
            nodeCache.start();
        }
        return "";
    }
}
