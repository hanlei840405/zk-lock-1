package com.jd.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

/**
 * Created by hanlei6 on 16-11-25.
 */
@Configuration
public class QuantityLockedApplicationStartup implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(QuantityLockedApplicationStartup.class);

    @Override
    public void run(String... args) throws Exception {
//        String path = client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/order_id", "order_id".getBytes());
//        InterProcessMutex lock = new InterProcessMutex(client, "/order_id");
//        CountDownLatch latch = new CountDownLatch(1);
//        for (int i = 0; i < 30; i++) {
//            new Thread() {
//                @Override
//                public void run() {
//                    try {
//                        latch.await();
//                        lock.acquire();
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
//                        System.out.println(sdf.format(new Date()));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        lock.release();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }.start();
//            latch.countDown();
//        }
    }
}
