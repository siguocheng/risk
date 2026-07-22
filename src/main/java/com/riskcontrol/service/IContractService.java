package com.riskcontrol.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.Contract;
import com.riskcontrol.domain.bo.ContractBo;

/**
 * 合约Service接口
 *
 * @author zpc
 * @date 2026-06-26
 */
public interface IContractService extends IService<Contract> {

    Contract getByConid(Integer conid);

    boolean saveOrUpdateByConid(Contract contract);

    IPage<Contract> queryPage(ContractBo query);
}