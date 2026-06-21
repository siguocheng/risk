package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.ContractExecutionHistoryMapper;
import com.riskcontrol.domain.ContractExecutionHistory;
import com.riskcontrol.service.IContractExecutionHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 合约成交历史记录Service业务层处理
 *
 * @author zpc
 * @date 2026-06-21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractExecutionHistoryServiceImpl extends ServiceImpl<ContractExecutionHistoryMapper, ContractExecutionHistory> implements IContractExecutionHistoryService {

    @Override
    public boolean saveOrUpdateByExecId(ContractExecutionHistory contractExecutionHistory) {
        LambdaQueryWrapper<ContractExecutionHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContractExecutionHistory::getContractExecutionId, contractExecutionHistory.getContractExecutionId());

        long count = this.count(queryWrapper);
        if (count > 0) {
            // 存在则更新
            return this.update(contractExecutionHistory, queryWrapper);
        } else {
            // 不存在则新增
            return this.save(contractExecutionHistory);
        }
    }

    @Override
    public List<ContractExecutionHistory> listContractExecutionHistoryByContractExecutionId(Long contractExecutionId) {

        LambdaQueryWrapper<ContractExecutionHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContractExecutionHistory::getContractExecutionId, contractExecutionId);
        return this.list(queryWrapper);
    }
}
