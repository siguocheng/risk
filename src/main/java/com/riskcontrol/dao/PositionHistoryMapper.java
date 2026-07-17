package com.riskcontrol.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.riskcontrol.domain.PositionHistory;
import com.riskcontrol.domain.vo.position.PositionHistoryPage;
import com.riskcontrol.domain.vo.position.PositionHistoryQuery;
import org.apache.ibatis.annotations.Param;

/**
 * 持仓列表历史Mapper接口
 *
 * @author zpc
 * @date 2026-06-20
 */
public interface PositionHistoryMapper extends BaseMapper<PositionHistory> {

    IPage<PositionHistoryPage> queryPage(IPage<PositionHistoryPage> page, @Param("query") PositionHistoryQuery query);

}
