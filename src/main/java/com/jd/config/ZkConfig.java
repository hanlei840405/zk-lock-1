package com.jd.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by hanlei6 on 16-11-22.
 */
@Configuration
public class ZkConfig {

    @Value("${zk.url}")
    private String url;

    @Bean
    public CuratorFramework client() {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().connectString(url).retryPolicy(retryPolicy)
                .connectionTimeoutMs(30000).build();
        curatorFramework.start();
        return curatorFramework;
    }
}
