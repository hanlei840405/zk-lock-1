package com.jd.service.callable;

import org.apache.curator.framework.CuratorFramework;

import java.util.concurrent.Callable;

/**
 * Created by hanlei6 on 16-11-29.
 */
public class OrderCallable implements Callable<Integer> {
    private CuratorFramework curatorFramework;
    private String path;

    public OrderCallable(CuratorFramework curatorFramework, String path) {
        this.curatorFramework = curatorFramework;
        this.path = path;
    }

    @Override
    public Integer call() throws Exception {

        return null;
    }
}
