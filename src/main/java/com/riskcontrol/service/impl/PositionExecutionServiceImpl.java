package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.PositionExecutionMapper;
import com.riskcontrol.domain.AccountContract;
import com.riskcontrol.domain.Contract;
import com.riskcontrol.domain.PositionAllocateHistory;
import com.riskcontrol.domain.PositionExecution;
import com.riskcontrol.domain.vo.positionexecution.PositionExecutionPage;
import com.riskcontrol.domain.vo.positionexecution.PositionExecutionQuery;
import com.riskcontrol.service.IAccountContractService;
import com.riskcontrol.service.IContractService;
import com.riskcontrol.service.IPositionAllocateHistoryService;
import com.riskcontrol.service.IPositionExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
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

    private final IContractService contractService;

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
        if (!CollectionUtils.isEmpty(query.getAccountCodes())) {
            queryWrapper.in(PositionExecution::getAccountCode, query.getAccountCodes());
        }
        if (!CollectionUtils.isEmpty(query.getConids())) {
            queryWrapper.in(PositionExecution::getConid, query.getConids());
        }
        queryWrapper.orderByAsc(PositionExecution::getTime);

        IPage<PositionExecution> entityPage = this.page(page, queryWrapper); // 取得符合条件的交易


        IPage<PositionExecutionPage> pageList = entityPage.convert(entity -> {
            PositionExecutionPage vo = new PositionExecutionPage();
            BeanUtils.copyProperties(entity, vo);

            List<PositionAllocateHistory> positionAllocateHistories = positionAllocateHistoryService.listPositionAllocateHistoryByKey(vo.getId(), null);
            // 求和，空字段当作0处理
            BigDecimal sum = positionAllocateHistories.stream()
                    .map(item -> item.getAllocateQty() == null ? BigDecimal.ZERO : item.getAllocateQty())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Contract contract = contractService.getByConid(vo.getConid());

            vo.setMultiplier(contract.getMultiplier());
            vo.setPositionAllocateDetails(positionAllocateHistoryService.listPositionAllocateHistoryByKey(null, entity.getId()));

            return vo;
        });

        return pageList;
    }

    @Override
    public List<PositionExecution> listPositionExecutionByKey(String accountCode, int conid, String executionDate) {
        LambdaQueryWrapper<PositionExecution> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PositionExecution::getAccountCode, accountCode)
                .eq(PositionExecution::getConid, conid)
                .eq(PositionExecution::getExecutionDate, executionDate);

        return this.list(wrapper);
    }
}