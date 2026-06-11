package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.AccountDailyPnlMapper;
import com.riskcontrol.domain.AccountDailyPnl;
import com.riskcontrol.domain.AccountSummaryCurrency;
import com.riskcontrol.service.IAccountDailyPnlService;
import lombok.RequiredArgsConstructor;
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
public class AccountDailyPnlServiceImpl extends ServiceImpl<AccountDailyPnlMapper, AccountDailyPnl> implements IAccountDailyPnlService {


    @Override
    public boolean saveOrUpdateAccountDailyPnl(AccountDailyPnl accountDailyPnl) {
        LambdaQueryWrapper<AccountDailyPnl> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(AccountDailyPnl::getAccountCode, accountDailyPnl.getAccountCode());
        queryWrapper.eq(AccountDailyPnl::getDailyDate, accountDailyPnl.getDailyDate());

        long count = this.count(queryWrapper);
        if (count > 0) {
            // 存在则更新
            return this.update(accountDailyPnl, queryWrapper);
        } else {
            // 不存在则新增
            return this.save(accountDailyPnl);
        }
    }
}
