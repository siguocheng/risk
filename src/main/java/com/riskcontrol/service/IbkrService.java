package com.riskcontrol.service;

import com.riskcontrol.domain.bo.ibkr.AccountSummaryBo;
import com.riskcontrol.domain.bo.ibkr.PositionBo;
import com.riskcontrol.domain.vo.ibkr.AccountSummaryVo;
import com.riskcontrol.domain.vo.ibkr.PositionVo;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface IbkrService {

    AccountSummaryVo reqAccountSummary(AccountSummaryBo accountSummaryBo) throws ExecutionException, InterruptedException, TimeoutException;

    List<PositionVo> reqPosition(PositionBo positionBo) throws ExecutionException, InterruptedException, TimeoutException;

    List<PositionVo> reqPortfolio(PositionBo positionBo) throws ExecutionException, InterruptedException, TimeoutException;
}
