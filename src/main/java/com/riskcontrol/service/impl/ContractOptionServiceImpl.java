package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.ContractOptionMapper;
import com.riskcontrol.domain.ContractOption;
import com.riskcontrol.service.IContractOptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 期权合约希腊值数据Service业务层处理
 *
 * @author zpc
 * @date 2026-06-17
 */
@Slf4j
@Service
public class ContractOptionServiceImpl extends ServiceImpl<ContractOptionMapper, ContractOption> implements IContractOptionService {

    @Override
    public boolean saveOrUpdateContractOption(ContractOption contractOption) {
        LambdaQueryWrapper<ContractOption> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ContractOption::getConid, contractOption.getConid());

        long count = this.count(queryWrapper);
        if (count > 0) {
            return this.update(contractOption, queryWrapper);
        } else {
            return this.save(contractOption);
        }
    }
}
