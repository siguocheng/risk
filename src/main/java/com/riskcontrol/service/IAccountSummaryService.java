package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.AccountSummary;

import java.util.List;

/**
 * 代码模板Service接口
 *
 * @author zpc
 * @date 2026-06-01
 */
public interface IAccountSummaryService extends IService<AccountSummary> {

    boolean saveOrUpdateAccountSummary(AccountSummary accountSummary);
    List<AccountSummary> queryAccountSummary(List<String> accountCodes);
}
