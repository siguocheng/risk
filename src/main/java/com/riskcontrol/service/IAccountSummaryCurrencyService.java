package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.AccountCurrency;
import com.riskcontrol.domain.AccountSummaryCurrency;

/**
 * 账户币种Service接口
 *
 * @author zpc
 * @date 2026-06-10
 */
public interface IAccountSummaryCurrencyService extends IService<AccountSummaryCurrency> {

    boolean saveOrUpdateAccountSummaryCurrency(AccountSummaryCurrency accountSummaryCurrency);
}
