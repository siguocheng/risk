package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.ContractMarketMapper;
import com.riskcontrol.domain.ContractMarket;
import com.riskcontrol.service.IContractMarketService;
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
public class ContractMarketServiceImpl extends ServiceImpl<ContractMarketMapper, ContractMarket> implements IContractMarketService {


    @Override
    public boolean saveOrUpdateContractMarket(ContractMarket contractMarket) {
        LambdaQueryWrapper<ContractMarket> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ContractMarket::getTime, contractMarket.getTime());
        queryWrapper.eq(ContractMarket::getConid, contractMarket.getConid());

        long count = this.count(queryWrapper);
        if (count > 0) {
            // 存在则更新
            return this.update(contractMarket, queryWrapper);
        } else {
            // 不存在则新增
            return this.save(contractMarket);
        }
    }

    @Override
    public double[] queryContractMarketPriceCloseByConid(int conid) {

        LambdaQueryWrapper<ContractMarket> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ContractMarket::getConid, conid);

        List<ContractMarket> list = this.list(queryWrapper);

        // 转 Double 包装类型数组
        Double[] doubleObjArr = list.stream()
                .map(ContractMarket::getPriceClose)
                .map(BigDecimal::doubleValue)
                .toArray(Double[]::new);

        // 再转 基本类型 double[]
        double[] doubleArr = java.util.Arrays.stream(doubleObjArr)
                .mapToDouble(Double::doubleValue)
                .toArray();


        return doubleArr;
    }
}
