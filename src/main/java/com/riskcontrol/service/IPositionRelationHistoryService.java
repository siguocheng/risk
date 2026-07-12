package com.riskcontrol.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.PositionRelationHistory;
import com.riskcontrol.domain.bo.PortfolioOverviewBo;
import com.riskcontrol.domain.vo.dashboard.AssetSecTypeRatio;
import com.riskcontrol.domain.vo.dashboard.DailyProfitQuery;
import com.riskcontrol.domain.vo.dashboard.DailyProfitTop10;
import com.riskcontrol.domain.vo.dashboard.RiskControlQuery;
import com.riskcontrol.domain.vo.positionrelation.PositionRelationHistoryPage;
import com.riskcontrol.domain.vo.positionrelation.PositionRelationHistoryQuery;

import java.util.List;

/**
 * 策略和交易员和账号和持仓之间的关系历史Service接口
 *
 * @author zpc
 * @date 2026-06-26
 */
public interface IPositionRelationHistoryService extends IService<PositionRelationHistory> {

    List<PositionRelationHistory> listByKey(String dailyDate, String accountCode, Integer conid, String strategyName, String traderName);

    PositionRelationHistory getPositionRelationHistoryByKey(String dailyDate, String accountCode, Integer conid, String strategyName, String traderName);


    boolean saveOrUpdateByKey(PositionRelationHistory history);

    List<PositionRelationHistory> listByDateRange(PortfolioOverviewBo portfolioOverviewBo);

    IPage<PositionRelationHistoryPage> queryPage(PositionRelationHistoryQuery query);

    List<DailyProfitTop10> getTop10Profit(RiskControlQuery query);

    List<AssetSecTypeRatio> getAssetSecTypeRatio(RiskControlQuery query);
}
