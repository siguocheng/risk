package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.PositionRelationHistoryMapper;
import com.riskcontrol.domain.PositionRelationHistory;
import com.riskcontrol.domain.bo.PortfolioOverviewBo;
import com.riskcontrol.domain.vo.dashboard.AssetSecTypeRatio;
import com.riskcontrol.domain.vo.dashboard.DailyProfitQuery;
import com.riskcontrol.domain.vo.dashboard.DailyProfitTop10;
import com.riskcontrol.domain.vo.dashboard.RiskControlQuery;
import com.riskcontrol.domain.vo.positionrelation.PositionRelationHistoryPage;
import com.riskcontrol.domain.vo.positionrelation.PositionRelationHistoryQuery;
import com.riskcontrol.service.IPositionRelationHistoryService;
import com.riskcontrol.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 策略和交易员和账号和持仓之间的关系历史Service业务层处理
 *
 * @author zpc
 * @date 2026-06-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PositionRelationHistoryServiceImpl extends ServiceImpl<PositionRelationHistoryMapper, PositionRelationHistory> implements IPositionRelationHistoryService {

    @Override
    public List<PositionRelationHistory> listByKey(String dailyDate, String accountCode, Integer conid, String strategyName, String traderName) {
        LambdaQueryWrapper<PositionRelationHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(accountCode), PositionRelationHistory::getAccountCode, accountCode)
                .eq(conid != null, PositionRelationHistory::getConid, conid)
                .eq(StringUtils.hasText(strategyName), PositionRelationHistory::getStrategyName, strategyName)
                .eq(StringUtils.hasText(traderName), PositionRelationHistory::getTraderName, traderName)
                .eq(StringUtils.hasText(dailyDate), PositionRelationHistory::getDailyDate, dailyDate)
                .orderByDesc(PositionRelationHistory::getCreateTime);

        return this.list(queryWrapper);
    }

    @Override
    public PositionRelationHistory getPositionRelationHistoryByKey(String dailyDate, String accountCode, Integer conid, String strategyName, String traderName) {
        LambdaQueryWrapper<PositionRelationHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(accountCode), PositionRelationHistory::getAccountCode, accountCode)
                .eq(conid != null, PositionRelationHistory::getConid, conid)
                .eq(StringUtils.hasText(strategyName), PositionRelationHistory::getStrategyName, strategyName)
                .eq(StringUtils.hasText(traderName), PositionRelationHistory::getTraderName, traderName)
                .eq(StringUtils.hasText(dailyDate), PositionRelationHistory::getDailyDate, dailyDate)
                .orderByDesc(PositionRelationHistory::getCreateTime);

        return this.getOne(queryWrapper);
    }

    @Override
    public boolean saveOrUpdateByKey(PositionRelationHistory history) {
        LambdaQueryWrapper<PositionRelationHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PositionRelationHistory::getDailyDate, history.getDailyDate())
                .eq(PositionRelationHistory::getAccountCode, history.getAccountCode())
                .eq(PositionRelationHistory::getConid, history.getConid())
                .eq(PositionRelationHistory::getStrategyName, history.getStrategyName())
                .eq(PositionRelationHistory::getTraderName, history.getTraderName());

        long count = this.count(queryWrapper);
        if (count > 0) {
            return this.update(history, queryWrapper);
        } else {
            return this.save(history);
        }
    }

    @Override
    public List<PositionRelationHistory> listByDateRange(PortfolioOverviewBo portfolioOverviewBo) {
        LambdaQueryWrapper<PositionRelationHistory> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.ge(portfolioOverviewBo.getStartDate() != null, 
                PositionRelationHistory::getDailyDate, 
                portfolioOverviewBo.getStartDate())
                .le(portfolioOverviewBo.getEndDate() != null, 
                PositionRelationHistory::getDailyDate, 
                portfolioOverviewBo.getEndDate());

        if (!CollectionUtils.isEmpty(portfolioOverviewBo.getAccountCodes())) {
            queryWrapper.in(PositionRelationHistory::getAccountCode, portfolioOverviewBo.getAccountCodes());
        }

        if (!CollectionUtils.isEmpty(portfolioOverviewBo.getTradeNames())) {
            queryWrapper.in(PositionRelationHistory::getTraderName, portfolioOverviewBo.getTradeNames());
        }

        if (!CollectionUtils.isEmpty(portfolioOverviewBo.getStrategyNames())) {
            queryWrapper.in(PositionRelationHistory::getStrategyName, portfolioOverviewBo.getStrategyNames());
        }

        queryWrapper.orderByAsc(PositionRelationHistory::getDailyDate);

        return this.list(queryWrapper);
    }

    @Override
    public IPage<PositionRelationHistoryPage> queryPage(PositionRelationHistoryQuery query) {
        return this.baseMapper.queryPage(query.build(), query);
    }

    @Override
    public List<DailyProfitTop10> getTop10Profit(RiskControlQuery query) {
        return this.baseMapper.getTop10Profit(query);
    }

    @Override
    public List<AssetSecTypeRatio> getAssetSecTypeRatio(RiskControlQuery query) {

        query.setDailyDate(query.getEndDate());
        List<AssetSecTypeRatio> list = this.baseMapper.getAssetSecTypeRatio(query);

        Long sumCount = list.stream()
                // count为空则替换0，防止空指针
                .map(item -> item.getCount() == null ? 0L : item.getCount())
                .reduce(0L, Long::sum);

        for (AssetSecTypeRatio assetSecTypeRatio : list) {
            BigDecimal ratio = new BigDecimal(assetSecTypeRatio.getCount()).divide(new BigDecimal(sumCount), 2, RoundingMode.HALF_UP);
            assetSecTypeRatio.setRatio(ratio);
        }

        return list;
    }
}
