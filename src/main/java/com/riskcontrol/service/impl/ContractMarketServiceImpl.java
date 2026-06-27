package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.ContractMarketMapper;
import com.riskcontrol.domain.ContractMarket;
import com.riskcontrol.service.IContractMarketService;
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
public class ContractMarketServiceImpl extends ServiceImpl<ContractMarketMapper, ContractMarket> implements IContractMarketService {

    @Override
    public ContractMarket getByConid(Integer conid) {
        LambdaQueryWrapper<ContractMarket> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContractMarket::getConid, conid);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean saveOrUpdateByConid(ContractMarket contractMarket) {
        LambdaQueryWrapper<ContractMarket> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContractMarket::getConid, contractMarket.getConid());
        long count = this.count(queryWrapper);
        if (count > 0) {
            return this.update(contractMarket, queryWrapper);
        } else {
            return this.save(contractMarket);
        }
    }
}