package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.ContractExecutionMapper;
import com.riskcontrol.domain.ContractExecution;
import com.riskcontrol.domain.ContractExecutionHistory;
import com.riskcontrol.domain.vo.contractexecution.ContractExecutionAllocateModify;
import com.riskcontrol.domain.vo.contractexecution.ContractExecutionPage;
import com.riskcontrol.domain.vo.contractexecution.ContractExecutionQuery;
import com.riskcontrol.service.IContractExecutionHistoryService;
import com.riskcontrol.service.IContractExecutionService;
import com.riskcontrol.service.IPositionRelationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
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
public class ContractExecutionServiceImpl extends ServiceImpl<ContractExecutionMapper, ContractExecution> implements IContractExecutionService {

    private final IContractExecutionHistoryService contractExecutionHistoryService;

    private final IPositionRelationService positionRelationService;

    @Override
    public boolean saveOrUpdateByExecId(ContractExecution contractExecution) {
        LambdaQueryWrapper<ContractExecution> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContractExecution::getExecId, contractExecution.getExecId());

        long count = this.count(queryWrapper);
        if (count > 0) {
            return this.update(contractExecution, queryWrapper);
        } else {
            return this.save(contractExecution);
        }
    }

    @Override
    public IPage<ContractExecutionPage> queryPage(ContractExecutionQuery query) {
        Page<ContractExecution> page = new Page<>(query.getPageNum(), query.getPageSize());
        
        LambdaQueryWrapper<ContractExecution> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(query.getOrderId() != null, ContractExecution::getOrderId, query.getOrderId())
                .eq(query.getClientId() != null, ContractExecution::getClientId, query.getClientId())
                .eq(StringUtils.hasText(query.getExecId()), ContractExecution::getExecId, query.getExecId())
                .eq(StringUtils.hasText(query.getAcctNumber()), ContractExecution::getAcctNumber, query.getAcctNumber())
                .eq(StringUtils.hasText(query.getExchange()), ContractExecution::getExchange, query.getExchange())
                .eq(StringUtils.hasText(query.getSide()), ContractExecution::getSide, query.getSide())
                .eq(query.getPermId() != null, ContractExecution::getPermId, query.getPermId())
                .eq(StringUtils.hasText(query.getModelCode()), ContractExecution::getModelCode, query.getModelCode())
                .eq(StringUtils.hasText(query.getSubmitter()), ContractExecution::getSubmitter, query.getSubmitter())
                .eq(query.getStatus() != null, ContractExecution::getStatus, query.getStatus())
                .orderByDesc(ContractExecution::getCreateTime);

        IPage<ContractExecution> entityPage = this.page(page, queryWrapper);

        return entityPage.convert(entity -> {
            ContractExecutionPage vo = new ContractExecutionPage();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void allocate(ContractExecutionAllocateModify request) {
        if (!StringUtils.hasText(request.getExecId()) || CollectionUtils.isEmpty(request.getDetails())) {
            throw new IllegalArgumentException("成交ID和分配明细不能为空");
        }

        // 查询成交记录
        ContractExecution contractExecution = this.getById(request.getId());
        if (contractExecution == null) {
            throw new IllegalArgumentException("成交记录不存在");
        }

        BigDecimal qty = contractExecution.getShares();
        
        // 计算本次分配总量
        BigDecimal totalAllocateQty = request.getDetails().stream()
                .map(ContractExecutionHistory::getQty)
                .filter(q -> q != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 验证分配数量不超过剩余数量
        if (totalAllocateQty.compareTo(qty) > 0) {
            throw new IllegalArgumentException("分配数量超过剩余可分配数量");
        }

        // 构建历史记录列表
        for (ContractExecutionHistory history : request.getDetails()) {

            contractExecutionHistoryService.saveOrUpdate(history);
        }

        this.calQty(request.getId());
    }

    private void calQty(Long contractExecutionId){
        List<ContractExecutionHistory> contractExecutionHistories = contractExecutionHistoryService.listContractExecutionHistoryByContractExecutionId(contractExecutionId);

        // 计算本次分配总量
        BigDecimal totalAllocateQty = contractExecutionHistories.stream()
                .map(ContractExecutionHistory::getQty)
                .filter(q -> q != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ContractExecution contractExecution = this.getById(contractExecutionId);

        String side = contractExecution.getSide();// 操作方式  BOT买 SLD卖

        BigDecimal shares = contractExecution.getShares();

        contractExecution.setRemainQty(shares.subtract(totalAllocateQty));

        if (shares.compareTo(totalAllocateQty) == 0) {
            contractExecution.setStatus(2);
        } else {
            contractExecution.setStatus(1);
        }

        this.updateById(contractExecution);

        // 根据side更新position_relation表
        // BOT买入：增加持仓；SLD卖出：减少持仓
        for (ContractExecutionHistory history : contractExecutionHistories) {
            BigDecimal qty = history.getQty();
            if ("SLD".equals(side)) {
                // 卖出时，持仓数量减少（传入负数）
                qty = qty.negate();
            }
            // BOT买入时，持仓数量增加（传入正数）
            positionRelationService.saveOrUpdatePositionQty(
                    contractExecution.getAcctNumber(),
                    contractExecution.getConid(),
                    history.getStrategyName(),
                    history.getTraderName(),
                    qty
            );
        }
    }
}