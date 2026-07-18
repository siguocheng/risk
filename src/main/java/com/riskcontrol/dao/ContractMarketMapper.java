package com.riskcontrol.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.riskcontrol.domain.ContractMarket;

import java.util.List;

/**
 * 合约Mapper接口
 *
 * @author zpc
 * @date 2026-06-26
 */
public interface ContractMarketMapper extends BaseMapper<ContractMarket> {

    List<ContractMarket> listOptWithoutStk();
}