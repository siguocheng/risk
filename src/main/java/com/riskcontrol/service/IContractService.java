package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.Contract;

/**
 * 合约Service接口
 *
 * @author zpc
 * @date 2026-06-26
 */
public interface IContractService extends IService<Contract> {

    Contract getByConid(Integer conid);

    boolean saveOrUpdateByConid(Contract contract);
}