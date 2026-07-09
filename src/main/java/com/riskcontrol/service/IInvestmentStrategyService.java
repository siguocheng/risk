package com.riskcontrol.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.InvestmentStrategy;
import com.riskcontrol.domain.vo.investmentstrategy.InvestmentStrategyModify;
import com.riskcontrol.domain.vo.investmentstrategy.InvestmentStrategyPage;
import com.riskcontrol.domain.vo.investmentstrategy.InvestmentStrategyQuery;
import com.riskcontrol.domain.vo.investmentstrategy.InvestmentStrategysModify;

/**
 * 投资策略Service接口
 *
 * @author zpc
 * @date 2026-06-19
 */
public interface IInvestmentStrategyService extends IService<InvestmentStrategy> {

    IPage<InvestmentStrategyPage> queryPage(InvestmentStrategyQuery query);

    Long create(InvestmentStrategyModify modify);

    Integer update(InvestmentStrategysModify modify);

    Long delete(Long id);
}
