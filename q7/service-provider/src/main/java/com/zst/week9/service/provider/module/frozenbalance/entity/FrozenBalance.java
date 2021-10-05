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
    private int status;
    /** 创建时间*/
    private long createTime;
    /** 更新时间*/
    private long updateTime;

    public static FrozenBalance def() {
        FrozenBalance entity = new FrozenBalance();
        entity.setStatus(1);
        return entity;
    }
}
