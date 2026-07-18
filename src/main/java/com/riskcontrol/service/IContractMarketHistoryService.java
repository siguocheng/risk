package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.ContractMarketHistory;

import java.math.BigDecimal;
import java.util.List;

/**
 * 代码模板Service接口
 *
 * @author zpc
 * @date 2026-06-01
 */
public interface IContractMarketHistoryService extends IService<ContractMarketHistory> {
    boolean saveOrUpdateContractMarket(ContractMarketHistory contractMarket);

    double[] queryContractMarketPriceCloseByConid(int conid);

    List<ContractMarketHistory> listContractMarketHistoryByConidAndDate(List<Integer> conids, String startDate, String endDate);

    BigDecimal getMarketPriceByConidAndDate(int conid, String dailyDate);

    BigDecimal getStkPriceCloseBySymbolAndDate(String symbol, String dailyDate);
}
