package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.Contract;
import com.riskcontrol.domain.Position;

import java.math.BigDecimal;
import java.util.List;

/**
 * 代码模板Service接口
 *
 * @author zpc
 * @date 2026-06-01
 */
public interface IContractService extends IService<Contract> {
    boolean saveOrUpdateContract(Contract contract);

    Contract getContractByConid(String accountCode, Integer conid);
}
