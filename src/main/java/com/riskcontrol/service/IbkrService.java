package com.riskcontrol.service;

import com.riskcontrol.domain.bo.ibkr.AccountSummaryBo;
import com.riskcontrol.domain.bo.ibkr.PositionBo;
import com.riskcontrol.domain.vo.ibkr.AccountSummaryVo;
import com.riskcontrol.domain.vo.ibkr.PositionCallbackVo;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface IbkrService {

    AccountSummaryVo reqAccountSummary(AccountSummaryBo accountSummaryBo) throws ExecutionException, InterruptedException, TimeoutException;

    List<PositionCallbackVo> reqPosition(PositionBo positionBo) throws ExecutionException, InterruptedException, TimeoutException;

    List<PositionCallbackVo> reqPortfolio(PositionBo positionBo) throws ExecutionException, InterruptedException, TimeoutException;
}
