package com.zst.week9.service.provider.module.account.service;

import com.zst.week9.service.api.consts.CurrencyUnitTypeConsts;
import com.zst.week9.service.api.module.account.service.AccountRpcServiceB;
import com.zst.week9.service.provider.module.account.dao.AccountDao;
import com.zst.week9.service.provider.module.account.entity.Account;
import com.zst.week9.service.provider.module.frozenbalance.dao.FrozenBalanceDao;
import com.zst.week9.service.provider.module.frozenbalance.entity.FrozenBalance;
import com.zst.week9.service.provider.utils.IDGenerator;
import org.dromara.hmily.annotation.HmilyTCC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

public class AccountRpcServiceBImpl implements AccountRpcServiceB {
    private static final Logger logger = LoggerFactory.getLogger(AccountRpcServiceImpl.class);

    @Autowired
    private AccountDao accountDao;
    @Autowired
    private FrozenBalanceDao frozenBalanceDao;

    @Value("${spring.profiles.active}")
    private String profile;

    /**
     * 转账
     * @param sourceUserId
     * @param value
     * @param sourceCurrencyUnitType
     * @param targetUserId
     * @param targetCurrencyUnitType
     * @param serialNumber
     */
    @Override
    @HmilyTCC(confirmMethod = "confirmTransfer", cancelMethod = "cancelTransfer")
    public void transfer(long sourceUserId, String value, int sourceCurrencyUnitType,
                         long targetUserId, int targetCurrencyUnitType, long serialNumber) {
        logger.info("start try, serialNumber = " + serialNumber);

        // TODO 这里缺少账户余额是否足够的检查
        Account account = accountDao.getByUserIdAndCurrencyUnitType(sourceUserId, sourceCurrencyUnitType);
        // TODO 这里需要看看这种字符串表述的金额是怎么处理的
        BigDecimal currentValue = new BigDecimal(account.getBalance());
        BigDecimal toMinusValue = new BigDecimal(value);
        account.setBalance(currentValue.subtract(toMinusValue).toString());
        accountDao.update(account);

        FrozenBalance frozenBalance = FrozenBalance.def();
        frozenBalance.setId(IDGenerator.get());
        frozenBalance.setUserId(sourceUserId);
        frozenBalance.setAccountId(account.getId());
        frozenBalance.setSerialNumber(serialNumber);
        frozenBalance.setAmount(value);
        frozenBalance.setCurrencyUnitType(sourceCurrencyUnitType);
        frozenBalance.setCreateTime(System.currentTimeMillis());
        frozenBalance.setUpdateTime(System.currentTimeMillis());
        frozenBalanceDao.save(frozenBalance);
        logger.info("try executed");
//        logger.info("try phase throw Exception");
//        throw new RuntimeException("throw exception on purpose");
    }

    public void confirmTransfer(long sourceUserId, String value, int sourceCurrencyUnitType,
                                long targetUserId, int targetCurrencyUnitType, long serialNumber) {
        logger.info("execute confirm start, serialNumber = " + serialNumber);
        Account sourceAccount = accountDao.getByUserIdAndCurrencyUnitType(sourceUserId, sourceCurrencyUnitType);
        FrozenBalance frozenBalance = frozenBalanceDao.getBySerialNumber(sourceUserId, sourceAccount.getId(), serialNumber);
        frozenBalance.setStatus(2);
        frozenBalanceDao.update(frozenBalance);

        Account targetAccount = accountDao.getByUserIdAndCurrencyUnitType(targetUserId, targetCurrencyUnitType);
        // TODO 这里需要看看这种字符串表述的金额是怎么处理的
        BigDecimal currentValue = new BigDecimal(targetAccount.getBalance());
        BigDecimal toAddValue = new BigDecimal(exchange(value, sourceCurrencyUnitType, targetCurrencyUnitType));
        targetAccount.setBalance(currentValue.add(toAddValue).toString());

        accountDao.update(targetAccount);

        logger.info("execute confirm complete");
    }

    public void cancelTransfer(long sourceUserId, String value, int sourceCurrencyUnitType,
                               long targetUserId, int targetCurrencyUnitType, long serialNumber) {
        logger.info("execute cancel start, serialNumber = " + serialNumber);
        Account sourceAccount = accountDao.getByUserIdAndCurrencyUnitType(sourceUserId, sourceCurrencyUnitType);
        FrozenBalance frozenBalance = frozenBalanceDao.getBySerialNumber(sourceUserId, sourceAccount.getId(), serialNumber);
        frozenBalance.setStatus(3);
        frozenBalanceDao.update(frozenBalance);

        BigDecimal currentValue = new BigDecimal(sourceAccount.getBalance());
        BigDecimal toAddValue = new BigDecimal(frozenBalance.getAmount());
        sourceAccount.setBalance(currentValue.add(toAddValue).toString());
        accountDao.update(sourceAccount);

        logger.info("execute cancel end");
    }

    private String exchange(String value, int sourceCurrencyUnitType, int targetCurrencyUnitType) {
        // TODO 此处省略掉一些细节的东西，在于如何实现TCC事务而不是怎么转换汇率
        BigDecimal source = new BigDecimal(value);
        if (sourceCurrencyUnitType == CurrencyUnitTypeConsts.CNY && targetCurrencyUnitType == CurrencyUnitTypeConsts.USD) {
            return source.divide(new BigDecimal(7)).toString();
        } else if (sourceCurrencyUnitType == CurrencyUnitTypeConsts.USD && targetCurrencyUnitType == CurrencyUnitTypeConsts.CNY){
            return source.multiply(new BigDecimal(7)).toString();
        } else {
            return value;
        }
    }
}
