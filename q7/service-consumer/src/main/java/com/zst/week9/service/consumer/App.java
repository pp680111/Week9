package com.zst.week9.service.consumer;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(exclude = MongoAutoConfiguration.class)
@EnableDubbo
public class App {
    public static void main(String[] args) {
        System.setProperty("hmily.conf", "G:\\J2EE\\Java训练营\\Week9\\q7\\service-consumer\\target\\classes\\hmily.yml");
        ApplicationContext context = SpringApplication.run(App.class, args);

        TransferExecutorImpl executor = context.getBean(TransferExecutorImpl.class);
        executor.doTransfer();
    }
}
