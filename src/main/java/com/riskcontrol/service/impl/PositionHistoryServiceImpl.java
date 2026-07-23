package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.PositionHistoryMapper;
import com.riskcontrol.domain.PositionHistory;
import com.riskcontrol.domain.vo.position.PositionHistoryPage;
import com.riskcontrol.domain.vo.position.PositionHistoryQuery;
import com.riskcontrol.domain.vo.positionrelation.PositionRelationHistoryQuery;
import com.riskcontrol.service.IPositionHistoryService;
import com.riskcontrol.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * 持仓列表历史Service业务层处理
 *
 * @author zpc
 * @date 2026-06-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PositionHistoryServiceImpl extends ServiceImpl<PositionHistoryMapper, PositionHistory> implements IPositionHistoryService {

    @Override
    public boolean saveOrUpdatePositionHistory(PositionHistory positionHistory) {
        LambdaQueryWrapper<PositionHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PositionHistory::getAccountCode, positionHistory.getAccountCode())
                .eq(PositionHistory::getConid, positionHistory.getConid())
                .eq(PositionHistory::getPositionDate, positionHistory.getPositionDate());

        long count = this.count(queryWrapper);
        if (count > 0) {
            return this.update(positionHistory, queryWrapper);
        } else {
            positionHistory.setId(null);
            return this.save(positionHistory);
        }
    }

    @Override
    public PositionHistory getPositionHistoryByKey(String positionDate, Integer conid, String accountCode) {
        LambdaQueryWrapper<PositionHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PositionHistory::getAccountCode, accountCode)
                .eq(PositionHistory::getConid, conid)
                .eq(PositionHistory::getPositionDate, positionDate);

        return this.getOne(queryWrapper);
    }

    @Override
    public IPage<PositionHistoryPage> queryPage(PositionHistoryQuery query) {

        this.handleStartEndDate(query);
        IPage<PositionHistoryPage> data = this.baseMapper.queryPage(query.build(), query);
        for (PositionHistoryPage record : data.getRecords()) {
            record.setCalMarketValue(record.getCalMarketPrice().multiply(record.getCalPositionQty()));
        }
        return data;
    }

    private void handleStartEndDate(PositionHistoryQuery query){
        if (query.getDateType() != null) {
            // 当日或者近7日
            if (query.getDateType() == 1) {
                query.setEndDate(DateUtil.localDateToString(LocalDate.now()));
                query.setStartDate(DateUtil.localDateToString(LocalDate.now()));
            } else if (query.getDateType() == 7) {
                query.setEndDate(DateUtil.localDateToString(LocalDate.now()));
                query.setStartDate(DateUtil.localDateToString(LocalDate.now().minusDays(6)));
            }
            // 当年1月1日开始
            else if (query.getDateType() == 11) {
                query.setEndDate(DateUtil.localDateToString(LocalDate.now()));
                query.setStartDate(DateUtil.localDateToString(LocalDate.now().with(TemporalAdjusters.firstDayOfYear())));
            }
            // 近1年
            else if (query.getDateType() == 365) {
                query.setEndDate(DateUtil.localDateToString(LocalDate.now()));
                query.setStartDate(DateUtil.localDateToString(LocalDate.now().minusDays(365)));
            }
            // 近30天
            else if (query.getDateType() == 30) {
                query.setEndDate(DateUtil.localDateToString(LocalDate.now()));
                query.setStartDate(DateUtil.localDateToString(LocalDate.now().minusDays(30)));
            }
        }
    }
}
