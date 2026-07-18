package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.AccountSummaryMapper;
import com.riskcontrol.domain.AccountCurrency;
import com.riskcontrol.domain.AccountSummary;
import com.riskcontrol.service.IAccountSummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 账户币种Service业务层处理
 *
 * @author zpc
 * @date 2026-06-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountSummaryServiceImpl extends ServiceImpl<AccountSummaryMapper, AccountSummary>  implements IAccountSummaryService {


    @Override
    public boolean saveOrUpdateAccountSummary(AccountSummary accountSummary) {

        LambdaQueryWrapper<AccountSummary> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(AccountSummary::getAccountCode, accountSummary.getAccountCode());

        long count = this.count(queryWrapper);
        if (count > 0) {
            // 存在则更新
            return this.update(accountSummary, queryWrapper);
        } else {
            // 不存在则新增
            return this.save(accountSummary);
        }
    }

    @Override
    public List<AccountSummary> queryAccountSummary(List<String> accountCodes) {
        LambdaQueryWrapper<AccountSummary> queryWrapper = new LambdaQueryWrapper<>();
        if (accountCodes != null && accountCodes.size() > 0) {
            queryWrapper.in(AccountSummary::getAccountCode, accountCodes);
        }

        return this.list(queryWrapper);
    }
}
