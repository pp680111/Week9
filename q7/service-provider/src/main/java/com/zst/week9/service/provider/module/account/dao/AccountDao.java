package com.zst.week9.service.provider.module.account.dao;

import com.zst.week9.service.provider.module.account.entity.Account;

public interface AccountDao {
    void save(Account entity);

    void update(Account entity);

    Account getByUserIdAndCurrencyUnitType(long userId, int currencyUnitType);

    Account get(long id);
}
