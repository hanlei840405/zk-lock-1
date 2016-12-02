package com.jd.controller;

import com.jd.entity.Order;
import com.jd.service.OrderService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Created by hanlei6 on 16-11-29.
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    private final static Logger logger = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    private OrderService orderService;

    @RequestMapping("addOrder")
    public String addOrder(String sku, @RequestParam int quantity) {
        Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setSku(sku);
        order.setQuantity(quantity);
        int value = orderService.insert(this.getClass().getName(), order);
        if (value == 1) {
            return "SUCCESS";
        }
        return "FAILURE";

    }

    @RequestMapping("addOrderAsync")
    public String addOrderAsync(String sku, @RequestParam int loop) {
        for (int i = 0; i < loop; i++) {
            new Thread(this.getClass().getName() + "_" + i) {
                @Override
                public void run() {
                    Order order = new Order();
                    order.setSku(sku);
                    order.setQuantity(1);
                    orderService.insert(this.getName(), order);
                }
            }.start();
        }

        return "SUCCESS";

    }
}
