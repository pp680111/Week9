package com.zst.week9.service.provider;

import com.zst.week9.service.provider.zookeeper.EmbeddedZooKeeper;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo(scanBasePackages = "com.zst.week9.service.provider.module.*.service")
public class App {
    public static void main(String[] args) {
        // 先启动作为注册中心的ZooKeeper
        new EmbeddedZooKeeper(2181, false).start();

        SpringApplication.run(App.class, args);
    }
}
