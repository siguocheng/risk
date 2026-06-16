package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.ContractMapper;
import com.riskcontrol.domain.AccountSummary;
import com.riskcontrol.domain.Contract;
import com.riskcontrol.service.IContractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 代码模板Service业务层处理
 *
 * @author zpc
 * @date 2026-04-07
 */
@Slf4j
@Service
public class ContractServiceImpl extends ServiceImpl<ContractMapper, Contract> implements IContractService {


    @Override
    public boolean saveOrUpdateContract(Contract contract) {
        LambdaQueryWrapper<Contract> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Contract::getAccountCode, contract.getAccountCode());
        queryWrapper.eq(Contract::getConid, contract.getConid());

        long count = this.count(queryWrapper);
        if (count > 0) {
            // 存在则更新
            return this.update(contract, queryWrapper);
        } else {
            // 不存在则新增
            return this.save(contract);
        }
    }
}
