package com.zst.week9.service.provider.configuration;

import com.zst.week9.service.api.module.account.service.AccountRpcService;
import com.zst.week9.service.api.module.account.service.AccountRpcServiceB;
import com.zst.week9.service.provider.module.account.service.AccountRpcServiceBImpl;
import com.zst.week9.service.provider.module.account.service.AccountRpcServiceImpl;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 根据不同的profile生成不同的Bean
 */
@Configuration
public class ProfileBeanConfiguration {
    @Profile("a")
    @DubboService
    @Bean
    public AccountRpcService accountRpcServiceA() {
        return new AccountRpcServiceImpl();
    }

    @Profile("b")
    @DubboService
    @Bean
    public AccountRpcServiceB accountRpcServiceB() {
        return new AccountRpcServiceBImpl();
    }
}
