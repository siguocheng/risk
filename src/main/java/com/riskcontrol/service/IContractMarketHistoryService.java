package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.ContractMarketHistory;

/**
 * 代码模板Service接口
 *
 * @author zpc
 * @date 2026-06-01
 */
public interface IContractMarketHistoryService extends IService<ContractMarketHistory> {
    boolean saveOrUpdateContractMarket(ContractMarketHistory contractMarket);

    double[] queryContractMarketPriceCloseByConid(int conid);
}
