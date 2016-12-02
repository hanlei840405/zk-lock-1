package com.jd.service;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by hanlei6 on 16-11-29.
 */
@Component
public class ZookeeperService {
    @Value("${zk.url}")
    private String url;

    public CuratorFramework get() {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().connectString(url).retryPolicy(retryPolicy)
                .connectionTimeoutMs(30000).build();
        return curatorFramework;
    }

    public void close(CuratorFramework curatorFramework) {
        CloseableUtils.closeQuietly(curatorFramework);
    }
}
