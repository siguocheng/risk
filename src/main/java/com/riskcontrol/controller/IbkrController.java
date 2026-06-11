package com.riskcontrol.controller;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.riskcontrol.annotation.ResourceMethod;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.config.IbkrSynConfig;
import com.riskcontrol.domain.bo.ibkr.AccountSummaryBo;
import com.riskcontrol.domain.bo.ibkr.PositionBo;
import com.riskcontrol.domain.vo.ibkr.AccountSummaryVo;
import com.riskcontrol.domain.vo.ibkr.PositionCallbackVo;
import com.riskcontrol.service.IbkrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Tag(description = "IB接口", name = "IB接口")
@RestController
@RequestMapping("/ibkr")
public class IbkrController {

    @Resource
    EClientSocket m_client;

    @Resource
    IbkrSynConfig ibkrSynConfig;

    @Resource
    IbkrService ibkrService;

    @Operation(summary = "账户信息", description = "账户信息")
    @PostMapping(value = {"/pc/account-summary"})
    @ResourceMethod(btnCode = "btn-pc-ibkr-account-summary", level = 3)
    public ResultBean<AccountSummaryVo> reqAccountSummary(@RequestBody AccountSummaryBo accountSummaryBo) throws ExecutionException, InterruptedException, TimeoutException {
        return new ResultBean<>(ibkrService.reqAccountSummary(accountSummaryBo));
    }


    @Operation(summary = "持仓列表", description = "持仓列表")
    @PostMapping(value = {"/pc/position"})
    @ResourceMethod(btnCode = "btn-pc-ibkr-account-summary", level = 3)
    public ResultBean<List<PositionCallbackVo>> reqPosition(@RequestBody PositionBo positionBo) throws ExecutionException, InterruptedException, TimeoutException {
        return new ResultBean<>(ibkrService.reqPosition(positionBo));
    }

    @Operation(summary = "投资组合", description = "投资组合")
    @PostMapping(value = {"/pc/portfolio"})
    @ResourceMethod(btnCode = "btn-pc-ibkr-account-summary", level = 3)
    public ResultBean<List<PositionCallbackVo>> reqPortfolio(@RequestBody PositionBo positionBo) throws ExecutionException, InterruptedException, TimeoutException {
        return new ResultBean<>(ibkrService.reqPortfolio(positionBo));
    }

    @Operation(summary = "历史数据", description = "历史数据")
    @PostMapping(value = {"/pc/history-data"})
    @ResourceMethod(btnCode = "btn-pc-ibkr-account-summary", level = 3)
    public ResultBean<List<PositionCallbackVo>> historyData(@RequestBody PositionBo positionBo) throws ExecutionException, InterruptedException, TimeoutException {

        int reqId = 996;
        Contract contract = new Contract();
        contract.symbol("AAPL");
        contract.secType("STK");   // 股票 STK，期货 FUT，期权 OPT
        contract.exchange("SMART"); // NASDAQ
        contract.currency("USD");

        String endDateTime = "";          // 空 = 取最新数据 20260608 23:59:59
        String durationStr = "1 M";       // 回溯 1 个月 1 D(1 天)、1 W(1 周)、1 M(1 月)、1 Y(1 年)
        String barSize = "1 day";         // 日K线 1 secs / 1 min / 5 mins / 1 hour / 1 day
        String whatToShow = "TRADES";     // 取成交价格 MIDPOINT(中间价)、BID、ASK、TRADES(成交)
        int useRTH = 1;                   // 1仅常规交易时段 0包含盘前盘后交易时段

        int formatDate = 1;
        boolean keepUpToDate = false;// 不持续更新
        m_client.reqHistoricalData(reqId, contract, endDateTime, durationStr, barSize, whatToShow, useRTH, formatDate, keepUpToDate, null);

        return new ResultBean<>(null);
    }
}
