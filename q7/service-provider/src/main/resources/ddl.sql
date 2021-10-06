CREATE TABLE t_account (
	id bigint NOT NULL,
	user_id bigint NOT NULL COMMENT '用户id',
	balance varchar(20) NOT NULL COMMENT '余额',
	status int NOT NULL COMMENT '账户状态',
	currency_unit_type int NOT NULL COMMENT '货币单位',
	PRIMARY KEY (id),
	KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户表';

CREATE TABLE t_frozen_balance (
    id bigint NOT NULL,
    user_id bigint NOT NULL COMMENT '用户id',
    account_id bigint NOT NULL COMMENT '账户id',
    serial_number bigint NOT NULL COMMENT '交易序列号',
    amount varchar(20) NOT NULL COMMENT '数量',
    currency_unit_type int NOT NULL COMMENT '货币单位',
    status int NOT NULL COMMENT '状态',
    create_time bigint NOT NULL COMMENT '创建时间',
    update_time bigint NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_account_id (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户冻结表';