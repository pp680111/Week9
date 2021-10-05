package com.zst.week9.service.provider.module.account.entity;

import lombok.Data;

@Data
public class Account {
    /** id*/
    private long id;
    /** 用户id*/
    private long userId;
    /** 余额*/
    private String balance;
    /** 状态*/
    private int status;
    /** 货币单位*/
    private int currencyUnitType;
}

