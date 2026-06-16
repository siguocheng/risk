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

import java.math.BigDecimal;
import java.util.List;

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

    @Override
    public double[] queryContractHistoryPriceCloseByConid(int conid) {

        LambdaQueryWrapper<ContractHistory> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ContractHistory::getConid, conid);

        List<ContractHistory> list = this.list(queryWrapper);

        // 转 Double 包装类型数组
        Double[] doubleObjArr = list.stream()
                .map(ContractHistory::getPriceClose)
                .map(BigDecimal::doubleValue)
                .toArray(Double[]::new);

        // 再转 基本类型 double[]
        double[] doubleArr = java.util.Arrays.stream(doubleObjArr)
                .mapToDouble(Double::doubleValue)
                .toArray();


        return doubleArr;
    }
}
