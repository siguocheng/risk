package com.riskcontrol.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.riskcontrol.domain.PositionRelationHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 策略和交易员和账号和持仓之间的关系历史Mapper接口
 *
 * @author zpc
 * @date 2026-06-26
 */
public interface PositionRelationHistoryMapper extends BaseMapper<PositionRelationHistory> {

    List<PositionRelationHistory> sumPnlByDate(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
