package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.domain.PositionRelation;
import com.riskcontrol.domain.Position;
import com.riskcontrol.domain.vo.positionrelation.PositionRelationModify;
import com.riskcontrol.domain.vo.positionrelation.PositionRelationPage;
import com.riskcontrol.domain.vo.positionrelation.PositionRelationQuery;
import com.riskcontrol.dao.PositionRelationMapper;
import com.riskcontrol.service.IPositionRelationHistoryService;
import com.riskcontrol.service.IPositionRelationService;
import com.riskcontrol.service.IPositionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 策略和交易员和账号和持仓之间的关系Service实现类
 *
 * @author zpc
 * @date 2026-06-19
 */
@Slf4j
@Service
public class PositionRelationServiceImpl extends ServiceImpl<PositionRelationMapper, PositionRelation> implements IPositionRelationService {

//    @Resource
//    IPositionService positionService;

    @Resource
    IPositionRelationHistoryService positionRelationHistoryService;

    @Override
    public IPage<PositionRelationPage> queryPage(PositionRelationQuery query) {
        LambdaQueryWrapper<PositionRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(query.getAccountCode() != null, PositionRelation::getAccountCode, query.getAccountCode());
        queryWrapper.eq(query.getConid() != null, PositionRelation::getConid, query.getConid());
        queryWrapper.like(query.getStrategyName() != null, PositionRelation::getStrategyName, query.getStrategyName());
        queryWrapper.like(query.getTraderName() != null, PositionRelation::getTraderName, query.getTraderName());
        queryWrapper.isNull(PositionRelation::getDeleted);

        return this.page(query.build(), queryWrapper).convert(compositeRelation -> {
            PositionRelationPage page = new PositionRelationPage();
            page.setId(compositeRelation.getId());
            page.setAccountCode(compositeRelation.getAccountCode());
            page.setConid(compositeRelation.getConid());
            page.setStrategyName(compositeRelation.getStrategyName());
            page.setTraderName(compositeRelation.getTraderName());
            page.setPositionQty(compositeRelation.getPositionQty());
            return page;
        });
    }

    @Override
    public PositionRelation getPositionRelationByKey(String accountCode, Integer conid, String strategyName, String traderName) {
        LambdaQueryWrapper<PositionRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PositionRelation::getAccountCode, accountCode)
                .eq(PositionRelation::getConid, conid)
                .eq(PositionRelation::getStrategyName, strategyName)
                .eq(PositionRelation::getTraderName, traderName);

        return this.getOne(queryWrapper);
    }

    @Override
    public boolean saveOrUpdatePositionQty(String accountCode, Integer conid, String strategyName, String traderName, BigDecimal qty) {
        LambdaQueryWrapper<PositionRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PositionRelation::getAccountCode, accountCode)
                .eq(PositionRelation::getConid, conid)
                .eq(PositionRelation::getStrategyName, strategyName)
                .eq(PositionRelation::getTraderName, traderName)
                .isNull(PositionRelation::getDeleted);

        PositionRelation positionRelation = this.getOne(queryWrapper);
        if (positionRelation == null) {
            throw new RuntimeException("未找到对应的持仓关系记录");
        }

        BigDecimal newQty = positionRelation.getPositionQty().add(qty);
        if (newQty.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("持仓数量不能为负数");
        }

        positionRelation.setPositionQty(newQty);
        return this.updateById(positionRelation);
    }
}
