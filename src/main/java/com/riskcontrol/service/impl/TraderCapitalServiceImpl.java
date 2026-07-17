package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.TraderCapitalMapper;
import com.riskcontrol.domain.TraderCapital;
import com.riskcontrol.service.ITraderCapitalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class TraderCapitalServiceImpl extends ServiceImpl<TraderCapitalMapper, TraderCapital> implements ITraderCapitalService {

    static Map<String, BigDecimal> map = new ConcurrentHashMap<>();

    @Override
    public BigDecimal getCapitalByTraderDate(String traderName, String dailyDate) {

        String key = dailyDate + "-" + traderName;
        BigDecimal capital = map.get(key);
        if (capital == null) {
            LambdaQueryWrapper<TraderCapital> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TraderCapital::getTraderName, traderName);
            queryWrapper.eq(TraderCapital::getDailyDate, dailyDate);

            TraderCapital one = this.getOne(queryWrapper);
            if (one != null) {
                capital = one.getCapital();
                map.put(key, capital);
            }
        }

        return capital;
    }

    @Override
    public void saveOrUpdateTraderCapital(TraderCapital traderCapital) {
        String key = traderCapital.getDailyDate() + "-" + traderCapital.getTraderName();

        LambdaQueryWrapper<TraderCapital> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TraderCapital::getTraderName, traderCapital.getTraderName());
        queryWrapper.eq(TraderCapital::getDailyDate, traderCapital.getDailyDate());

        TraderCapital existing = this.getOne(queryWrapper);
        if (existing != null) {
            traderCapital.setId(existing.getId());
            this.updateById(traderCapital);
        } else {
            this.save(traderCapital);
        }

        map.put(key, traderCapital.getCapital());
    }
}