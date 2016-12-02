package com.jd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ZkLock1Application {
    private final static Logger logger = LoggerFactory.getLogger(ZkLock1Application.class);

    public static void main(String[] args) throws Exception {
        ApplicationContext ctx = SpringApplication.run(ZkLock1Application.class, args);
//        new Thread() {
//            @Override
//            public void run() {
//                DataSource dataSource = ctx.getBean(DataSource.class);
//                while (true) {
//                    logger.info("active connection : {}", dataSource.getActive());
//                    logger.info("dataSource info : {}", dataSource);
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }.start();
    }
}
