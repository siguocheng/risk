package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.TraderMapper;
import com.riskcontrol.domain.Trader;
import com.riskcontrol.domain.TraderModifiedHistory;
import com.riskcontrol.domain.vo.TraderModifiedHistoryVo;
import com.riskcontrol.domain.vo.trader.TraderDetail;
import com.riskcontrol.domain.vo.trader.TraderModify;
import com.riskcontrol.domain.vo.trader.TraderPage;
import com.riskcontrol.domain.vo.trader.TraderQuery;
import com.riskcontrol.service.ITraderModifiedHistoryService;
import com.riskcontrol.service.ITraderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 交易员Service业务层处理
 *
 * @author zpc
 * @date 2026-06-18
 */
@Slf4j
@Service
public class TraderServiceImpl extends ServiceImpl<TraderMapper, Trader> implements ITraderService {

    @Resource
    private ITraderModifiedHistoryService traderModifiedHistoryService;

    @Override
    public IPage<TraderPage> queryPage(TraderQuery query) {
        return this.page(query.build(), new LambdaQueryWrapper<Trader>()
                .like(query.getTraderName() != null, Trader::getTraderName, query.getTraderName()))
                .convert(trader -> {
                    TraderPage page = new TraderPage();
                    page.setId(trader.getId());
                    page.setTraderName(trader.getTraderName());
                    page.setCapital(trader.getCapital());
                    page.setModifiedTime(trader.getModifiedTime());
                    return page;
                });
    }

    @Override
    @Transactional
    public Long create(TraderModify modify) {
        // 创建交易员
        Trader trader = new Trader();
        trader.setTraderName(modify.getTraderName());
        trader.setCapital(modify.getCapital());
        this.save(trader);

        return trader.getId();
    }

    @Override
    @Transactional
    public Long update(TraderModify modify) {
        Trader trader = this.getById(modify.getId());
        if (trader == null) {
            throw new RuntimeException("交易员不存在");
        }
        String orgTraderName = trader.getTraderName();
        java.math.BigDecimal orgCapital = trader.getCapital();
        trader.setTraderName(modify.getTraderName());
        trader.setCapital(modify.getCapital());
        this.updateById(trader);

        if ((orgTraderName != null && !orgTraderName.equals(modify.getTraderName()))
                || (modify.getTraderName() != null && !modify.getTraderName().equals(orgTraderName))
                || (orgCapital != null && orgCapital.compareTo(modify.getCapital()) != 0)
                || (modify.getCapital() != null && modify.getCapital().compareTo(orgCapital) != 0)) {
            TraderModifiedHistory history = new TraderModifiedHistory();
            history.setTraderId(modify.getId());
            history.setOrgTraderName(orgTraderName);
            history.setOrgCapital(orgCapital);
            history.setCurrentTraderName(modify.getTraderName());
            history.setCurrentCapital(modify.getCapital());
            traderModifiedHistoryService.save(history);
        }

        return modify.getId();
    }

    @Override
    @Transactional
    public Long delete(Long id) {
        Trader trader = this.getById(id);
        if (trader == null) {
            throw new RuntimeException("交易员不存在");
        }
        trader.setDeleted(false);
        this.updateById(trader);

        return id;
    }

    @Override
    public TraderDetail getDetail(Long id) {
        Trader trader = this.getById(id);
        if (trader == null) {
            throw new RuntimeException("交易员不存在");
        }
        TraderDetail detail = new TraderDetail();
        detail.setId(trader.getId());
        detail.setTraderName(trader.getTraderName());
        detail.setCapital(trader.getCapital());

        List<TraderModifiedHistory> list = traderModifiedHistoryService.list(new LambdaQueryWrapper<TraderModifiedHistory>()
                .eq(TraderModifiedHistory::getTraderId, id)
                .orderByDesc(TraderModifiedHistory::getCreateTime));

        List<TraderModifiedHistoryVo> modifiedHistoryList = new ArrayList<>();

        for (TraderModifiedHistory traderModifiedHistory : list) {
            TraderModifiedHistoryVo data = new TraderModifiedHistoryVo();
            BeanUtils.copyProperties(traderModifiedHistory, data);

            modifiedHistoryList.add(data);
        }

        detail.setModifiedHistoryList(modifiedHistoryList);
        return detail;
    }

    @Override
    public Trader getDetailByTrader(String traderName) {
        LambdaQueryWrapper<Trader> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Trader::getTraderName, traderName);
        return this.getOne(queryWrapper);
    }
}