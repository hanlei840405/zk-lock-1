package com.jd.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

/**
 * Created by hanlei6 on 16-11-24.
 */
@Configuration
public class ApplicationStartup implements CommandLineRunner {
    private final static Logger logger = LoggerFactory.getLogger(ApplicationStartup.class);
    @Value("${zk.url}")
    private String url;

    @Override
    public void run(String... args) {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().connectString(url).retryPolicy(retryPolicy)
                .connectionTimeoutMs(30000).build();
        curatorFramework.start();
        NodeCache nodeCache = new NodeCache(curatorFramework, "/order_id", false);
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                logger.info("node is delete");
            }
        });
        try {
            nodeCache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
