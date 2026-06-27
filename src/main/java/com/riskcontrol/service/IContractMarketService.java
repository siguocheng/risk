package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.ContractMarket;

/**
 * 合约Service接口
 *
 * @author zpc
 * @date 2026-06-26
 */
public interface IContractMarketService extends IService<ContractMarket> {

    ContractMarket getByConid(Integer conid);

    boolean saveOrUpdateByConid(ContractMarket contractMarket);
}