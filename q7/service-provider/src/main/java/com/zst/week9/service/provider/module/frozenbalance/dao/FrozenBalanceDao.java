package com.zst.week9.service.provider.module.frozenbalance.dao;

import com.zst.week9.service.provider.module.frozenbalance.entity.FrozenBalance;

public interface FrozenBalanceDao {
    void save(FrozenBalance entity);

    void update(FrozenBalance entity);

    FrozenBalance get(long id);
}
