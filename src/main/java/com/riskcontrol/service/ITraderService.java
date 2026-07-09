package com.riskcontrol.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.Trader;
import com.riskcontrol.domain.vo.trader.TraderDetail;
import com.riskcontrol.domain.vo.trader.TraderModify;
import com.riskcontrol.domain.vo.trader.TraderPage;
import com.riskcontrol.domain.vo.trader.TraderQuery;

/**
 * 交易员Service接口
 *
 * @author zpc
 * @date 2026-06-18
 */
public interface ITraderService extends IService<Trader> {
    IPage<TraderPage> queryPage(TraderQuery query);

    Long create(TraderModify modify);

    Long update(TraderModify modify);

    Long delete(Long id);

    TraderDetail getDetail(Long id);
}