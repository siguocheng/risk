package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.TraderCapitalMapper;
import com.riskcontrol.domain.TraderCapital;
import com.riskcontrol.service.ITraderCapitalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TraderCapitalServiceImpl extends ServiceImpl<TraderCapitalMapper, TraderCapital> implements ITraderCapitalService {

    static Map<String, BigDecimal> map = new HashMap<>();

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
}