package com.zst.week9.service.provider.module.account.service;

import com.zst.week9.service.api.module.account.service.AccountRpcService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class AccountRpcServiceImpl implements AccountRpcService {
    @Override
    public String hello(String name) {
        return "hello, " + name;
    }
}
