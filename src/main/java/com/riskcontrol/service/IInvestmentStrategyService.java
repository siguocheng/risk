package com.riskcontrol.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.InvestmentStrategy;
import com.riskcontrol.domain.vo.investmentstrategy.InvestmentStrategyModify;
import com.riskcontrol.domain.vo.investmentstrategy.InvestmentStrategyPage;
import com.riskcontrol.domain.vo.investmentstrategy.InvestmentStrategyQuery;

/**
 * 投资策略Service接口
 *
 * @author zpc
 * @date 2026-06-19
 */
public interface IInvestmentStrategyService extends IService<InvestmentStrategy> {

    IPage<InvestmentStrategyPage> queryPage(InvestmentStrategyQuery query);

    Long create(InvestmentStrategyModify modify);

    Long update(InvestmentStrategyModify modify);

    Long delete(Long id);
}
