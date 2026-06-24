package com.riskcontrol.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.riskcontrol.domain.PositionRelation;
import com.riskcontrol.domain.bo.PortfolioOverviewBo;
import com.riskcontrol.domain.vo.PortfolioOverviewDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 策略和交易员和账号和持仓之间的关系Mapper接口
 *
 * @author zpc
 * @date 2026-06-19
 */
@Mapper
public interface PositionRelationMapper extends BaseMapper<PositionRelation> {

    List<PortfolioOverviewDetail> listPortfolioOverviewDetail(PortfolioOverviewBo portfolioOverviewBo);
}
