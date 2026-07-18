package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.ContractMarketHistoryMapper;
import com.riskcontrol.domain.ContractMarketHistory;
import com.riskcontrol.service.IContractMarketHistoryService;
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
public class ContractMarketHistoryServiceImpl extends ServiceImpl<ContractMarketHistoryMapper, ContractMarketHistory> implements IContractMarketHistoryService {


    @Override
    public boolean saveOrUpdateContractMarket(ContractMarketHistory contractMarket) {
        LambdaQueryWrapper<ContractMarketHistory> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ContractMarketHistory::getDailyDate, contractMarket.getDailyDate());
        queryWrapper.eq(ContractMarketHistory::getConid, contractMarket.getConid());

        ContractMarketHistory one = this.getOne(queryWrapper);
        if (one != null) {
            if (contractMarket.getDelta() != null) {
                one.setDelta(contractMarket.getDelta());
            }
            if (contractMarket.getImpliedVol() != null) {
                one.setImpliedVol(contractMarket.getImpliedVol());
            }
            if (contractMarket.getGamma() != null) {
                one.setGamma(contractMarket.getGamma());
            }
            if (contractMarket.getVega() != null) {
                one.setVega(contractMarket.getVega());
            }
            if (contractMarket.getTheta() != null) {
                one.setTheta(contractMarket.getTheta());
            }
            if (contractMarket.getPriceClose() != null) {
                one.setPriceClose(contractMarket.getPriceClose());
            }
            if (contractMarket.getPositionMarketPrice() != null) {
                one.setPositionMarketPrice(contractMarket.getPositionMarketPrice());
            }
            return this.updateById(one);
        } else {
            return false;
        }
    }

    @Override
    public double[] queryContractMarketPriceCloseByConid(int conid) {

        LambdaQueryWrapper<ContractMarketHistory> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ContractMarketHistory::getConid, conid);

        List<ContractMarketHistory> list = this.list(queryWrapper);

        // 转 Double 包装类型数组
        Double[] doubleObjArr = list.stream()
                .map(ContractMarketHistory::getPriceClose)
                .map(BigDecimal::doubleValue)
                .toArray(Double[]::new);

        // 再转 基本类型 double[]
        double[] doubleArr = java.util.Arrays.stream(doubleObjArr)
                .mapToDouble(Double::doubleValue)
                .toArray();


        return doubleArr;
    }

    @Override
    public List<ContractMarketHistory> listContractMarketHistoryByConidAndDate(List<Integer> conids, String startDate, String endDate) {

        LambdaQueryWrapper<ContractMarketHistory> queryWrapperMarket = new LambdaQueryWrapper<>();
        queryWrapperMarket.in(ContractMarketHistory::getConid, conids);
        queryWrapperMarket.ge(ContractMarketHistory::getDailyDate, startDate);
        queryWrapperMarket.le(ContractMarketHistory::getDailyDate, endDate);

        return this.list(queryWrapperMarket);
    }

    @Override
    public BigDecimal getMarketPriceByConidAndDate(int conid, String dailyDate) {

        LambdaQueryWrapper<ContractMarketHistory> queryWrapperMarket = new LambdaQueryWrapper<>();
        queryWrapperMarket.eq(ContractMarketHistory::getConid, conid);
        queryWrapperMarket.eq(ContractMarketHistory::getDailyDate, dailyDate);

        ContractMarketHistory contractMarketHistory = this.getOne(queryWrapperMarket);

        if (contractMarketHistory != null) {
            if (contractMarketHistory.getPriceClose() != null) {
                return contractMarketHistory.getPriceClose();
            } else {
                return contractMarketHistory.getPositionMarketPrice();
            }

        }

        return null;
    }

    @Override
    public BigDecimal getStkPriceCloseBySymbolAndDate(String symbol, String dailyDate) {
        LambdaQueryWrapper<ContractMarketHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContractMarketHistory::getSymbol, symbol);
        queryWrapper.eq(ContractMarketHistory::getSecType, "STK");
        queryWrapper.eq(ContractMarketHistory::getDailyDate, dailyDate);
        queryWrapper.last("limit 1");

        ContractMarketHistory contractMarketHistory = this.getOne(queryWrapper);
        if (contractMarketHistory != null) {
            if (contractMarketHistory.getPriceClose() != null) {
                return contractMarketHistory.getPriceClose();
            } else {
                return contractMarketHistory.getPositionMarketPrice();
            }
        }
        return null;
    }
}
