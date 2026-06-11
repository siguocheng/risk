package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.ContractDailyPnlMapper;
import com.riskcontrol.dao.ContractMapper;
import com.riskcontrol.domain.Contract;
import com.riskcontrol.domain.ContractDailyPnl;
import com.riskcontrol.service.IContractDailyPnlService;
import com.riskcontrol.service.IContractService;
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
public class ContractDailyPnlServiceImpl extends ServiceImpl<ContractDailyPnlMapper, ContractDailyPnl> implements IContractDailyPnlService {


    @Override
    public boolean saveOrUpdateContractDailyPnl(ContractDailyPnl contractDailyPnl) {
        LambdaQueryWrapper<ContractDailyPnl> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ContractDailyPnl::getAccountCode, contractDailyPnl.getAccountCode());
        queryWrapper.eq(ContractDailyPnl::getConid, contractDailyPnl.getConid());
        queryWrapper.eq(ContractDailyPnl::getDailyDate, contractDailyPnl.getDailyDate());

        long count = this.count(queryWrapper);
        if (count > 0) {
            // 存在则更新
            return this.update(contractDailyPnl, queryWrapper);
        } else {
            // 不存在则新增
            return this.save(contractDailyPnl);
        }
    }
}
