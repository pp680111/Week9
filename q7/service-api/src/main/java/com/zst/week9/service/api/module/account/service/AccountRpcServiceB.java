package com.zst.week9.service.api.module.account.service;

import org.dromara.hmily.annotation.Hmily;

public interface AccountRpcServiceB {
    @Hmily
    void transfer(long sourceUserId, String value, int sourceCurrencyUnitType,
                  long targetUserId, int targetCurrencyUnitType, long serialNumber);
}
