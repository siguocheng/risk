package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.ContractMarket;

/**
 * 代码模板Service接口
 *
 * @author zpc
 * @date 2026-06-01
 */
public interface IContractMarketService extends IService<ContractMarket> {
    boolean saveOrUpdateContractMarket(ContractMarket contractMarket);

    double[] queryContractMarketPriceCloseByConid(int conid);
}
