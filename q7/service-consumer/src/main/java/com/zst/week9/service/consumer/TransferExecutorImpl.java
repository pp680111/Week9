package com.zst.week9.service.consumer;

import com.zst.week9.service.api.consts.CurrencyUnitTypeConsts;
import com.zst.week9.service.api.module.account.service.AccountRpcService;
import com.zst.week9.service.api.module.account.service.AccountRpcServiceB;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.hmily.annotation.HmilyTCC;
import org.springframework.stereotype.Component;

@Component
public class TransferExecutorImpl implements TransferExecutor {
    @DubboReference(retries = 0)
    private AccountRpcService serviceA;

    // 由于同样的接口类有两个bean，导致hmily拿不到具体的bean，产生NPE，所以复制了一个AccountRpcServiceB接口类
    @DubboReference(retries = 0)
    private AccountRpcServiceB serviceB;

    @Override
    @HmilyTCC(confirmMethod = "confirm", cancelMethod = "cancel")
    public void doTransfer() {
        long serialNumber = System.currentTimeMillis();
        serviceA.transfer('a', "1", CurrencyUnitTypeConsts.USD,
                'a', CurrencyUnitTypeConsts.CNY, serialNumber);
        serviceB.transfer('b', "7", CurrencyUnitTypeConsts.CNY,
                'b', CurrencyUnitTypeConsts.USD, serialNumber);
    }

    public void confirm() {
        System.err.println("confirm");
    }

    public void cancel() {
        System.err.println("cancel");
    }
}
