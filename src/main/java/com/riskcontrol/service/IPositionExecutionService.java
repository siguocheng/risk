package com.riskcontrol.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.PositionExecution;
import com.riskcontrol.domain.vo.positionexecution.PositionExecutionPage;
import com.riskcontrol.domain.vo.positionexecution.PositionExecutionQuery;

import java.util.List;

/**
 * 成交明细Service接口
 *
 * @author zpc
 * @date 2026-06-18
 */
public interface IPositionExecutionService extends IService<PositionExecution> {
    
    boolean saveOrUpdateByExecId(PositionExecution positionExecution);

    /**
     * 分页查询成交明细
     *
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<PositionExecutionPage> queryPage(PositionExecutionQuery query);

    List<PositionExecution> listPositionExecutionByKey(String accountCode, int conid, String executionDate);

    String importPositionExecution(java.io.InputStream inputStream);
}