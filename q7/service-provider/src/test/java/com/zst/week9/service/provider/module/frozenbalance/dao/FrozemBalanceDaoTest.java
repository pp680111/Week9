package com.zst.week9.service.provider.module.frozenbalance.dao;

import com.zst.week9.service.provider.consts.CurrencyUnitTypeConsts;
import com.zst.week9.service.provider.module.frozenbalance.entity.FrozenBalance;
import com.zst.week9.service.provider.utils.IDGenerator;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
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
public class FrozemBalanceDaoTest {
    @Autowired
    private FrozenBalanceDao frozenBalanceDao;

    @Test
    public void testSave() {
        FrozenBalance entity = FrozenBalance.def();
        entity.setId(IDGenerator.get());
        entity.setUserId('a');
        entity.setAccountId(1);
        entity.setAmount("0");
        entity.setCreateTime(System.currentTimeMillis());
        entity.setCurrencyUnitType(CurrencyUnitTypeConsts.CNY);
        entity.setUpdateTime(System.currentTimeMillis());
        frozenBalanceDao.save(entity);

        FrozenBalance newEntity = frozenBalanceDao.get(entity.getId());
        Assertions.assertNotNull(newEntity);
        System.err.println(newEntity);
    }
    @Test
    public void testUpdate() {
        FrozenBalance entity = FrozenBalance.def();
        entity.setId(IDGenerator.get());
        entity.setUserId('a');
        entity.setAccountId(1);
        entity.setAmount("0");
        entity.setCreateTime(System.currentTimeMillis());
        entity.setCurrencyUnitType(CurrencyUnitTypeConsts.CNY);
        entity.setUpdateTime(System.currentTimeMillis());
        frozenBalanceDao.save(entity);

        entity.setStatus(2);
        frozenBalanceDao.update(entity);

        FrozenBalance newEntity = frozenBalanceDao.get(entity.getId());
        Assertions.assertEquals(2, newEntity.getStatus());
    }
}
