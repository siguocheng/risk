package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.AccountDailyPnl;

/**
 * 账户每日盈亏Service接口
 *
 * @author zpc
 * @date 2026-06-10
 */
public interface IAccountDailyPnlService extends IService<AccountDailyPnl> {

    boolean saveOrUpdateAccountDailyPnl(AccountDailyPnl accountDailyPnl);
}
