package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.AccountDailyPnlMapper;
import com.riskcontrol.dao.AccountSummaryCurrencyMapper;
import com.riskcontrol.domain.AccountCurrency;
import com.riskcontrol.domain.AccountDailyPnl;
import com.riskcontrol.domain.AccountSummaryCurrency;
import com.riskcontrol.service.IAccountDailyPnlService;
import com.riskcontrol.service.IAccountSummaryCurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 账户每日盈亏Service业务层处理
 *
 * @author zpc
 * @date 2026-06-10
 */
@Slf4j
@Service
public class AccountSummaryCurrencyServiceImpl extends ServiceImpl<AccountSummaryCurrencyMapper, AccountSummaryCurrency> implements IAccountSummaryCurrencyService {


    @Override
    public boolean saveOrUpdateAccountSummaryCurrency(AccountSummaryCurrency accountSummaryCurrency) {

        LambdaQueryWrapper<AccountSummaryCurrency> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(AccountSummaryCurrency::getAccountCode, accountSummaryCurrency.getAccountCode());
        queryWrapper.eq(AccountSummaryCurrency::getCurrency, accountSummaryCurrency.getCurrency());

        long count = this.count(queryWrapper);
        if (count > 0) {
            // 存在则更新
            return this.update(accountSummaryCurrency, queryWrapper);
        } else {
            // 不存在则新增
            return this.save(accountSummaryCurrency);
        }
    }
}
