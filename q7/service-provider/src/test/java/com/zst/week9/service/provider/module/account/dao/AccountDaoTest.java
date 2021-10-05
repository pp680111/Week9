package com.zst.week9.service.provider.module.account.dao;

import com.zst.week9.service.provider.consts.CurrencyUnitTypeConsts;
import com.zst.week9.service.provider.module.account.entity.Account;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("a")
public class AccountDaoTest {
    @Autowired
    private AccountDao accountDao;

    @Test
    public void testGet() {
        Account entity = accountDao.getByUserIdAndCurrencyUnitType('a', CurrencyUnitTypeConsts.CNY);
        Assertions.assertNotNull(entity);
        System.err.println(entity);

        entity = accountDao.getByUserIdAndCurrencyUnitType('a', CurrencyUnitTypeConsts.USD);
        Assertions.assertNotNull(entity);
        System.err.println(entity);

        entity = accountDao.get(entity.getId());
        Assertions.assertNotNull(entity);
        System.err.println(entity);
    }
}
