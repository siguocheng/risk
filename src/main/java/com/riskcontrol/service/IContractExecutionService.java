package com.riskcontrol.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.ContractExecution;
import com.riskcontrol.domain.vo.contractexecution.ContractExecutionAllocateModify;
import com.riskcontrol.domain.vo.contractexecution.ContractExecutionPage;
import com.riskcontrol.domain.vo.contractexecution.ContractExecutionQuery;

/**
 * 成交明细Service接口
 *
 * @author zpc
 * @date 2026-06-18
 */
public interface IContractExecutionService extends IService<ContractExecution> {
    
    boolean saveOrUpdateByExecId(ContractExecution contractExecution);

    /**
     * 分页查询成交明细
     *
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<ContractExecutionPage> queryPage(ContractExecutionQuery query);

    /**
     * 分配成交数量
     *
     * @param request 分配请求
     * @return 是否成功
     */
    void allocate(ContractExecutionAllocateModify request);
}