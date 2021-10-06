package com.zst.week9.service.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

// hmily不知为何需要启动mongodb，而且用的驱动版本跟当前Springboot版本冲突了，所以手动排除掉MongoDB的自动配置
@SpringBootApplication(exclude = MongoAutoConfiguration.class)
@EnableDubbo(scanBasePackages = "com.zst.week9.service.provider.module.*.service")
@MapperScan("com.zst.week9.service.provider.module.*.dao")
public class App {
    public static void main(String[] args) {
        /*
            由于路径有中文，导致Hmily代码里通过ClassLoader.getResource读取出来的classpath文件路径中文乱码拿不到，
            所以只能手动设置配置文件路径了
         */
        System.setProperty("hmily.conf", "G:\\J2EE\\Java训练营\\Week9\\q7\\service-provider\\target\\classes\\hmily.yml");
        SpringApplication.run(App.class, args);
    }
}
