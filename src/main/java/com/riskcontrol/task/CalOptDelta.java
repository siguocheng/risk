package com.riskcontrol.task;

import com.riskcontrol.domain.Contract;
import com.riskcontrol.domain.ContractMarket;
import com.riskcontrol.domain.ContractMarketHistory;
import com.riskcontrol.service.IContractMarketHistoryService;
import com.riskcontrol.service.IContractMarketService;
import com.riskcontrol.service.IContractService;
import com.riskcontrol.service.ISystemConfigService;
import com.riskcontrol.util.BlackScholesUtil;
import com.riskcontrol.util.DateUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
public class CalOptDelta {

    @Resource
    IContractMarketService contractMarketService;

    @Resource
    IContractMarketHistoryService contractMarketHistoryService;

    @Resource
    IContractService contractService;

    @Resource
    ISystemConfigService systemConfigService;

    public void execute(String date){
        String riskFreeRateStr = systemConfigService.getValueByKey("risk_free_rate");
        BigDecimal riskFreeRate = BigDecimal.ZERO;
        if (riskFreeRateStr != null && !riskFreeRateStr.isEmpty()) {
            try {
                riskFreeRate = new BigDecimal(riskFreeRateStr);
                log.info("获取到无风险利率:{}", riskFreeRate);
            } catch (NumberFormatException e) {
                log.warn("解析无风险利率失败，使用默认值0:{}", e.getMessage());
                riskFreeRate = BigDecimal.ZERO;
            }
        } else {
            log.warn("未配置risk_free_rate，使用默认值0");
        }

        List<ContractMarket> optList = contractMarketService.listOptWithoutStk();
        log.info("查询到期权数量:{}", optList.size());

        for (ContractMarket contractMarket : optList) {
            log.info("期权信息: conid={}, symbol={}, secType={}", contractMarket.getConid(), contractMarket.getSymbol(), contractMarket.getSecType());

            BigDecimal stkPriceClose = contractMarketHistoryService.getStkPriceCloseBySymbolAndDate(contractMarket.getSymbol(), date);
            if (stkPriceClose == null) {
                log.warn("期权{}未找到对应股票STK在日期{}的收盘价，跳过", contractMarket.getSymbol(), date);
                continue;
            }
            log.info("期权{}对应股票STK收盘价:{}", contractMarket.getSymbol(), stkPriceClose);

            Contract contract = contractService.getByConid(contractMarket.getConid());
            if (contract == null) {
                log.warn("期权{} conid={}未找到对应的contract信息，跳过", contractMarket.getSymbol(), contractMarket.getConid());
                continue;
            }

            BigDecimal strike = contract.getStrike();
            String lastTradeDateOrContractMonth = contract.getLastTradeDateOrContractMonth();
            String optRight = contract.getOptRight();

            if (lastTradeDateOrContractMonth == null || lastTradeDateOrContractMonth.length() < 8) {
                log.warn("期权{} conid={}到期日为空或格式不正确，跳过", contractMarket.getSymbol(), contractMarket.getConid());
                continue;
            }

            long daysToExpiry;
            try {
                LocalDate expiryDate = DateUtil.stringToLocalDate(lastTradeDateOrContractMonth, "yyyyMMdd");
                LocalDate currentDate = DateUtil.stringToLocalDate(date);
                daysToExpiry = ChronoUnit.DAYS.between(currentDate, expiryDate);
            } catch (Exception e) {
                log.warn("期权{} conid={}计算到期天数失败:{}，跳过", contractMarket.getSymbol(), contractMarket.getConid(), e.getMessage());
                continue;
            }

            if (daysToExpiry <= 0) {
                log.warn("期权{} conid={}已到期或即将到期(daysToExpiry={})，跳过", contractMarket.getSymbol(), contractMarket.getConid(), daysToExpiry);
                continue;
            }

            BigDecimal optPriceClose = contractMarketHistoryService.getMarketPriceByConidAndDate(contractMarket.getConid(), date);
            if (optPriceClose == null || optPriceClose.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("期权{} conid={}未找到有效的收盘价，跳过", contractMarket.getSymbol(), contractMarket.getConid());
                continue;
            }

            boolean isCall = "C".equalsIgnoreCase(optRight);
            double timeToExpiry = daysToExpiry / 365.0;

            double impliedVol = BlackScholesUtil.calculateImpliedVolatility(
                    stkPriceClose.doubleValue(),
                    strike.doubleValue(),
                    riskFreeRate.doubleValue(),
                    timeToExpiry,
                    optPriceClose.doubleValue(),
                    isCall
            );

            BigDecimal impliedVolBD;
            if (Double.isNaN(impliedVol) || impliedVol <= 0) {
                log.warn("期权{} conid={}计算隐含波动率失败，使用默认值0.3", contractMarket.getSymbol(), contractMarket.getConid());
                impliedVolBD = new BigDecimal("0.3");
                impliedVol = 0.3;
            } else {
                impliedVolBD = BigDecimal.valueOf(impliedVol).setScale(4, RoundingMode.HALF_UP);
            }

            double delta = BlackScholesUtil.calculateDelta(
                    stkPriceClose.doubleValue(),
                    strike.doubleValue(),
                    riskFreeRate.doubleValue(),
                    timeToExpiry,
                    impliedVol,
                    isCall
            );
            BigDecimal deltaBD = BigDecimal.valueOf(delta).setScale(4, RoundingMode.HALF_UP);

            log.info("期权{} conid={} 行权价:{} 到期日:{} 期权类型:{} 距到期天数:{} 无风险利率:{} 隐含波动率:{} Delta:{}",
                    contractMarket.getSymbol(), contractMarket.getConid(), strike, lastTradeDateOrContractMonth, optRight, daysToExpiry, riskFreeRate, impliedVolBD, deltaBD);

            updateContractMarketHistory(contractMarket.getConid(), date, deltaBD, impliedVolBD);
        }
    }

    private void updateContractMarketHistory(Integer conid, String date, BigDecimal delta, BigDecimal impliedVol) {
        try {
            ContractMarketHistory history = new ContractMarketHistory();
            history.setConid(conid);
            history.setDailyDate(date);
            history.setDelta(delta);
            history.setImpliedVol(impliedVol);

            boolean success = contractMarketHistoryService.saveOrUpdateContractMarket(history);
            if (success) {
                log.info("更新contract_market_history成功 conid={} date={} delta={} impliedVol={}", conid, date, delta, impliedVol);
            } else {
                log.warn("contract_market_history不存在 conid={} date={}，跳过更新", conid, date);
            }
        } catch (Exception e) {
            log.error("更新contract_market_history失败 conid={} date={}:{}", conid, date, e.getMessage());
        }
    }
}
