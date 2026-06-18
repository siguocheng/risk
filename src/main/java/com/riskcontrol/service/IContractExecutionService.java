package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.ContractExecution;

/**
 * 成交明细Service接口
 *
 * @author zpc
 * @date 2026-06-18
 */
public interface IContractExecutionService extends IService<ContractExecution> {
    boolean saveOrUpdateByExecId(ContractExecution contractExecution);
}