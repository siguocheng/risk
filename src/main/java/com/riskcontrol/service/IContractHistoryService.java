package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.Contract;
import com.riskcontrol.domain.ContractHistory;

/**
 * 代码模板Service接口
 *
 * @author zpc
 * @date 2026-06-01
 */
public interface IContractHistoryService extends IService<ContractHistory> {
    boolean saveOrUpdateContractHistory(ContractHistory contractHistory);
}
