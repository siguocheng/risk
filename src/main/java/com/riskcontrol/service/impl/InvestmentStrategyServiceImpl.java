package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.domain.InvestmentStrategy;
import com.riskcontrol.domain.vo.investmentstrategy.InvestmentStrategyModify;
import com.riskcontrol.domain.vo.investmentstrategy.InvestmentStrategyPage;
import com.riskcontrol.domain.vo.investmentstrategy.InvestmentStrategyQuery;
import com.riskcontrol.dao.InvestmentStrategyMapper;
import com.riskcontrol.domain.vo.investmentstrategy.InvestmentStrategysModify;
import com.riskcontrol.service.IInvestmentStrategyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 投资策略Service实现类
 *
 * @author zpc
 * @date 2026-06-19
 */
@Slf4j
@Service
public class InvestmentStrategyServiceImpl extends ServiceImpl<InvestmentStrategyMapper, InvestmentStrategy> implements IInvestmentStrategyService {

    @Override
    public IPage<InvestmentStrategyPage> queryPage(InvestmentStrategyQuery query) {
        return this.page(query.build(), new LambdaQueryWrapper<InvestmentStrategy>()
                .like(query.getStrategyName() != null, InvestmentStrategy::getStrategyName, query.getStrategyName()))
                .convert(investmentStrategy -> {
                    InvestmentStrategyPage page = new InvestmentStrategyPage();
                    page.setId(investmentStrategy.getId());
                    page.setStrategyName(investmentStrategy.getStrategyName());
                    return page;
                });
    }

    @Override
    public Long create(InvestmentStrategyModify modify) {
        InvestmentStrategy investmentStrategy = new InvestmentStrategy();
        investmentStrategy.setStrategyName(modify.getStrategyName());
        this.save(investmentStrategy);
        return investmentStrategy.getId();
    }

    @Override
    public Integer update(InvestmentStrategysModify modify) {

        List<InvestmentStrategy> investmentStrategys = modify.getInvestmentStrategys();

        List<Long> idList = investmentStrategys.stream().filter(data -> data.getId() != null)
                .map(InvestmentStrategy::getId)
                .collect(Collectors.toList());


        LambdaQueryWrapper<InvestmentStrategy> wrapper = new LambdaQueryWrapper<>();
        wrapper.notIn(InvestmentStrategy::getId, idList);

        this.remove(wrapper);

        for (InvestmentStrategy investmentStrategy : investmentStrategys) {
            this.saveOrUpdate(investmentStrategy);
        }
        

        return investmentStrategys.size();
    }

    @Override
    public Long delete(Long id) {
        InvestmentStrategy investmentStrategy = this.getById(id);
        if (investmentStrategy == null) {
            throw new RuntimeException("投资策略不存在");
        }
        investmentStrategy.setDeleted(false);
        this.updateById(investmentStrategy);
        return id;
    }
}
