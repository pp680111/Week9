package com.zst.week9.service.provider.module.account.service;

import com.zst.week9.service.api.consts.CurrencyUnitTypeConsts;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("a")
@Transactional
public class AccountRpcServiceImplTest {
    @Autowired
    private AccountRpcServiceImpl accountRpcService;

    @Test
    public void testCancelTransfer() {
        accountRpcService.cancelTransfer('a', "1", CurrencyUnitTypeConsts.USD,
                'a', CurrencyUnitTypeConsts.CNY, 1633496426428L);
    }
}
