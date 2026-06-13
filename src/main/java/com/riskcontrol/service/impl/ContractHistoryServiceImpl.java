package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.ContractHistoryMapper;
import com.riskcontrol.dao.ContractMapper;
import com.riskcontrol.domain.Contract;
import com.riskcontrol.domain.ContractHistory;
import com.riskcontrol.service.IContractHistoryService;
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
public class ContractHistoryServiceImpl extends ServiceImpl<ContractHistoryMapper, ContractHistory> implements IContractHistoryService {


    @Override
    public boolean saveOrUpdateContractHistory(ContractHistory contractHistory) {
        LambdaQueryWrapper<ContractHistory> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ContractHistory::getTime, contractHistory.getTime());
        queryWrapper.eq(ContractHistory::getConid, contractHistory.getConid());

        long count = this.count(queryWrapper);
        if (count > 0) {
            // 存在则更新
            return this.update(contractHistory, queryWrapper);
        } else {
            // 不存在则新增
            return this.save(contractHistory);
        }
    }
}
