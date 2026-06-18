package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.ContractExecutionMapper;
import com.riskcontrol.domain.ContractExecution;
import com.riskcontrol.service.IContractExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 成交明细Service业务层处理
 *
 * @author zpc
 * @date 2026-06-18
 */
@Slf4j
@Service
public class ContractExecutionServiceImpl extends ServiceImpl<ContractExecutionMapper, ContractExecution> implements IContractExecutionService {

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
}