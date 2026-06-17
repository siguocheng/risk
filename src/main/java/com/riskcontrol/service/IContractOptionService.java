package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.ContractOption;

/**
 * 期权合约希腊值数据Service接口
 *
 * @author zpc
 * @date 2026-06-17
 */
public interface IContractOptionService extends IService<ContractOption> {
    boolean saveOrUpdateContractOption(ContractOption contractOption);
}
