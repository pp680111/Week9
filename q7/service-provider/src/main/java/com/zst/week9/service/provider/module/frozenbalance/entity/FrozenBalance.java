package com.zst.week9.service.provider.module.frozenbalance.entity;

import lombok.Data;

@Data
public class FrozenBalance {
    /** id*/
    private long id;
    /** 用户id*/
    private long userId;
    /** 账户id*/
    private long accountId;
    /** 数量*/
    private String amount;
    /** 货币单位*/
    private int currencyUnitType;
    /** 状态*/
    private int Status;
}
