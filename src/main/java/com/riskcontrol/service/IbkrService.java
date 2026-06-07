package com.riskcontrol.service;

import com.riskcontrol.domain.bo.ibkr.AccountSummaryBo;
import com.riskcontrol.domain.vo.ibkr.AccountSummaryVo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface IbkrService {

    AccountSummaryVo reqAccountSummary(AccountSummaryBo accountSummaryBo) throws ExecutionException, InterruptedException, TimeoutException;
}
