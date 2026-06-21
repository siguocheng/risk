package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.domain.PositionRelation;
import com.riskcontrol.domain.Position;
import com.riskcontrol.domain.vo.positionrelation.PositionRelationModify;
import com.riskcontrol.domain.vo.positionrelation.PositionRelationPage;
import com.riskcontrol.domain.vo.positionrelation.PositionRelationQuery;
import com.riskcontrol.mapper.PositionRelationMapper;
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

    @Resource
    IPositionService positionService;

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
    public Long create(PositionRelationModify modify) {
        // 验证持仓数量
        validatePositionQty(modify.getAccountCode(), modify.getConid(), null, modify.getPositionQty());

        PositionRelation compositeRelation = new PositionRelation();
        compositeRelation.setAccountCode(modify.getAccountCode());
        compositeRelation.setConid(modify.getConid());
        compositeRelation.setStrategyName(modify.getStrategyName());
        compositeRelation.setTraderName(modify.getTraderName());
        compositeRelation.setPositionQty(modify.getPositionQty());
        this.save(compositeRelation);
        return compositeRelation.getId();
    }

    @Override
    public Long update(PositionRelationModify modify) {
        PositionRelation compositeRelation = this.getById(modify.getId());
        if (compositeRelation == null) {
            throw new RuntimeException("综合关系不存在");
        }

        // 获取原有的持仓数量
        BigDecimal oldPositionQty = compositeRelation.getPositionQty();

        // 验证持仓数量
        validatePositionQty(modify.getAccountCode(), modify.getConid(), oldPositionQty, modify.getPositionQty());

        compositeRelation.setAccountCode(modify.getAccountCode());
        compositeRelation.setConid(modify.getConid());
        compositeRelation.setStrategyName(modify.getStrategyName());
        compositeRelation.setTraderName(modify.getTraderName());
        compositeRelation.setPositionQty(modify.getPositionQty());
        this.updateById(compositeRelation);
        return modify.getId();
    }

    @Override
    public Long delete(Long id) {
        PositionRelation compositeRelation = this.getById(id);
        if (compositeRelation == null) {
            throw new RuntimeException("综合关系不存在");
        }
        compositeRelation.setDeleted(false);
        this.updateById(compositeRelation);
        return id;
    }

    /**
     * 验证持仓数量是否超过总持仓
     *
     * @param accountCode    账号代码
     * @param conid          合约ID
     * @param oldPositionQty 原有的持仓数量（更新时使用，新增时为null）
     * @param newPositionQty 新的持仓数量
     */
    private void validatePositionQty(String accountCode, Integer conid, BigDecimal oldPositionQty, BigDecimal newPositionQty) {
        // 1. 获取position表中的总持仓数量
        LambdaQueryWrapper<Position> positionQuery = new LambdaQueryWrapper<>();
        positionQuery.eq(Position::getAccountCode, accountCode);
        positionQuery.eq(Position::getConid, conid);
        Position position = positionService.getOne(positionQuery);
        if (position == null || position.getPositionQty() == null) {
            throw new RuntimeException("未找到对应的持仓记录");
        }
        BigDecimal totalPositionQty = position.getPositionQty();

        // 2. 计算已分配的持仓数量（排除当前记录的原有数量）
        LambdaQueryWrapper<PositionRelation> relationQuery = new LambdaQueryWrapper<>();
        relationQuery.eq(PositionRelation::getAccountCode, accountCode);
        relationQuery.eq(PositionRelation::getConid, conid);
        relationQuery.isNull(PositionRelation::getDeleted);
        List<PositionRelation> existingRelations = this.list(relationQuery);

        BigDecimal allocatedQty = BigDecimal.ZERO;
        for (PositionRelation relation : existingRelations) {
            if (relation.getPositionQty() != null) {
                allocatedQty = allocatedQty.add(relation.getPositionQty());
            }
        }

        // 如果是更新操作，需要减去原有的数量
        if (oldPositionQty != null) {
            allocatedQty = allocatedQty.subtract(oldPositionQty);
        }

        // 3. 验证新的持仓数量是否超过剩余可分配数量
        BigDecimal remainingQty = totalPositionQty.subtract(allocatedQty);
        if (newPositionQty.compareTo(remainingQty) > 0) {
            throw new RuntimeException(String.format("持仓数量超过限制，剩余可分配数量为：%s", remainingQty));
        }
    }
}
