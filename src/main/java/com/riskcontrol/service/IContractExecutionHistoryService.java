package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.ContractExecutionHistory;

import java.util.List;

/**
 * 合约成交历史记录Service接口
 *
 * @author zpc
 * @date 2026-06-21
 */
public interface IContractExecutionHistoryService extends IService<ContractExecutionHistory> {

    /**
     * 根据execId保存或更新
     *
     * @param contractExecutionHistory 合约成交历史记录
     * @return 是否成功
     */
    boolean saveOrUpdateByExecId(ContractExecutionHistory contractExecutionHistory);

    List<ContractExecutionHistory> listContractExecutionHistoryByContractExecutionId(Long contractExecutionId);
}
