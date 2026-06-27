package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.AccountContractMapper;
import com.riskcontrol.domain.AccountContract;
import com.riskcontrol.service.IAccountContractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 代码模板Service业务层处理
 *
 * @author zpc
 * @date 2026-04-07
 */
@Slf4j
@Service
public class AccountContractServiceImpl extends ServiceImpl<AccountContractMapper, AccountContract> implements IAccountContractService {


    @Override
    public boolean saveOrUpdateAccountContract(AccountContract contract) {
        LambdaQueryWrapper<AccountContract> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(AccountContract::getAccountCode, contract.getAccountCode());
        queryWrapper.eq(AccountContract::getConid, contract.getConid());

        long count = this.count(queryWrapper);
        if (count > 0) {
            // 存在则更新
            return this.update(contract, queryWrapper);
        } else {
            // 不存在则新增
            return this.save(contract);
        }
    }

    @Override
    public AccountContract getContractByConid(String accountCode, Integer conid) {
        LambdaQueryWrapper<AccountContract> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(AccountContract::getAccountCode, accountCode);
        queryWrapper.eq(AccountContract::getConid, conid);

        return this.getOne(queryWrapper);
    }
}
