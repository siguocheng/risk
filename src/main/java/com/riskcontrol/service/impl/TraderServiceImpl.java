package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.TraderMapper;
import com.riskcontrol.domain.Trader;
import com.riskcontrol.domain.vo.trader.TraderModify;
import com.riskcontrol.domain.vo.trader.TraderPage;
import com.riskcontrol.domain.vo.trader.TraderQuery;
import com.riskcontrol.service.ITraderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 交易员Service业务层处理
 *
 * @author zpc
 * @date 2026-06-18
 */
@Slf4j
@Service
public class TraderServiceImpl extends ServiceImpl<TraderMapper, Trader> implements ITraderService {

    @Override
    public IPage<TraderPage> queryPage(TraderQuery query) {
        return this.page(query.build(), new LambdaQueryWrapper<Trader>()
                .like(query.getTraderName() != null, Trader::getTraderName, query.getTraderName())
                .isNull(Trader::getDeleted))
                .convert(trader -> {
                    TraderPage page = new TraderPage();
                    page.setId(trader.getId());
                    page.setTraderName(trader.getTraderName());
                    return page;
                });
    }

    @Override
    @Transactional
    public Long create(TraderModify modify) {
        // 创建交易员
        Trader trader = new Trader();
        trader.setTraderName(modify.getTraderName());
        this.save(trader);

        return trader.getId();
    }

    @Override
    @Transactional
    public Long update(TraderModify modify) {
        // 更新交易员信息
        Trader trader = this.getById(modify.getId());
        if (trader == null) {
            throw new RuntimeException("交易员不存在");
        }
        trader.setTraderName(modify.getTraderName());
        this.updateById(trader);

        return modify.getId();
    }

    @Override
    @Transactional
    public Long delete(Long id) {
        // 删除交易员
        Trader trader = this.getById(id);
        if (trader == null) {
            throw new RuntimeException("交易员不存在");
        }
        trader.setDeleted(false);
        this.updateById(trader);

        return id;
    }
}