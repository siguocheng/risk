package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.PositionExecutionMapper;
import com.riskcontrol.domain.AccountContract;
import com.riskcontrol.domain.PositionAllocateHistory;
import com.riskcontrol.domain.PositionExecution;
import com.riskcontrol.domain.vo.positionexecution.PositionExecutionPage;
import com.riskcontrol.domain.vo.positionexecution.PositionExecutionQuery;
import com.riskcontrol.service.IAccountContractService;
import com.riskcontrol.service.IPositionAllocateHistoryService;
import com.riskcontrol.service.IPositionExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 成交明细Service业务层处理
 *
 * @author zpc
 * @date 2026-06-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PositionExecutionServiceImpl extends ServiceImpl<PositionExecutionMapper, PositionExecution> implements IPositionExecutionService {

    private final IPositionAllocateHistoryService positionAllocateHistoryService;

    private final IAccountContractService contractService;

    @Override
    public boolean saveOrUpdateByExecId(PositionExecution positionExecution) {
        LambdaQueryWrapper<PositionExecution> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PositionExecution::getExecId, positionExecution.getExecId());

        long count = this.count(queryWrapper);
        if (count > 0) {
            return this.update(positionExecution, queryWrapper);
        } else {
            return this.save(positionExecution);
        }
    }

    @Override
    public IPage<PositionExecutionPage> queryPage(PositionExecutionQuery query) {
        Page<PositionExecution> page = new Page<>(query.getPageNum(), query.getPageSize());
        
        LambdaQueryWrapper<PositionExecution> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(query.getOrderId() != null, PositionExecution::getOrderId, query.getOrderId())
                .eq(query.getClientId() != null, PositionExecution::getClientId, query.getClientId())
                .eq(StringUtils.hasText(query.getExecId()), PositionExecution::getExecId, query.getExecId())
                .eq(StringUtils.hasText(query.getAccountCode()), PositionExecution::getAccountCode, query.getAccountCode())
                .eq(StringUtils.hasText(query.getExchange()), PositionExecution::getExchange, query.getExchange())
                .eq(StringUtils.hasText(query.getSide()), PositionExecution::getSide, query.getSide())
                .eq(query.getPermId() != null, PositionExecution::getPermId, query.getPermId())
                .eq(StringUtils.hasText(query.getModelCode()), PositionExecution::getModelCode, query.getModelCode())
                .eq(StringUtils.hasText(query.getSubmitter()), PositionExecution::getSubmitter, query.getSubmitter())
                .orderByDesc(PositionExecution::getCreateTime);

        IPage<PositionExecution> entityPage = this.page(page, queryWrapper);


        IPage<PositionExecutionPage> pageList = entityPage.convert(entity -> {
            PositionExecutionPage vo = new PositionExecutionPage();
            BeanUtils.copyProperties(entity, vo);

            List<PositionAllocateHistory> positionAllocateHistories = positionAllocateHistoryService.listPositionAllocateHistoryByKey(vo.getId(), null);
            // 求和，空字段当作0处理
            BigDecimal sum = positionAllocateHistories.stream()
                    .map(item -> item.getAllocateQty() == null ? BigDecimal.ZERO : item.getAllocateQty())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            vo.setRemainQty(vo.getShares().subtract(sum));

            AccountContract contract = contractService.getContractByConid(vo.getAccountCode(), vo.getConid());

            if (contract != null) {
                vo.setSymbol(contract.getSymbol());
            }

            return vo;
        });

        return pageList;
    }


}