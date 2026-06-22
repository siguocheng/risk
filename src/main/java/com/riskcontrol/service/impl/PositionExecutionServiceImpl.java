package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.PositionExecutionMapper;
import com.riskcontrol.domain.PositionExecution;
import com.riskcontrol.domain.vo.positionexecution.PositionExecutionPage;
import com.riskcontrol.domain.vo.positionexecution.PositionExecutionQuery;
import com.riskcontrol.service.IPositionExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
                .eq(StringUtils.hasText(query.getAcctNumber()), PositionExecution::getAcctNumber, query.getAcctNumber())
                .eq(StringUtils.hasText(query.getExchange()), PositionExecution::getExchange, query.getExchange())
                .eq(StringUtils.hasText(query.getSide()), PositionExecution::getSide, query.getSide())
                .eq(query.getPermId() != null, PositionExecution::getPermId, query.getPermId())
                .eq(StringUtils.hasText(query.getModelCode()), PositionExecution::getModelCode, query.getModelCode())
                .eq(StringUtils.hasText(query.getSubmitter()), PositionExecution::getSubmitter, query.getSubmitter())
                .eq(query.getStatus() != null, PositionExecution::getStatus, query.getStatus())
                .orderByDesc(PositionExecution::getCreateTime);

        IPage<PositionExecution> entityPage = this.page(page, queryWrapper);

        return entityPage.convert(entity -> {
            PositionExecutionPage vo = new PositionExecutionPage();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        });
    }


}