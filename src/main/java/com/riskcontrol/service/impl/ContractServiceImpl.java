package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.ContractMapper;
import com.riskcontrol.domain.Contract;
import com.riskcontrol.domain.bo.ContractBo;
import com.riskcontrol.service.IContractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 合约Service业务层处理
 *
 * @author zpc
 * @date 2026-06-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractServiceImpl extends ServiceImpl<ContractMapper, Contract> implements IContractService {

    @Override
    public Contract getByConid(Integer conid) {
        LambdaQueryWrapper<Contract> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Contract::getConid, conid);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean saveOrUpdateByConid(Contract contract) {
        LambdaQueryWrapper<Contract> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Contract::getConid, contract.getConid());
        long count = this.count(queryWrapper);
        if (count > 0) {
            return true;
//            return this.update(contract, queryWrapper);
        } else {
            return this.save(contract);
        }
    }

    @Override
    public IPage<Contract> queryPage(ContractBo query) {
        Page<Contract> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Contract> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Contract::getId);
        return this.page(page, queryWrapper);
    }
}