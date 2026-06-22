package com.riskcontrol.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.riskcontrol.domain.PositionRelation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 策略和交易员和账号和持仓之间的关系Mapper接口
 *
 * @author zpc
 * @date 2026-06-19
 */
@Mapper
public interface PositionRelationMapper extends BaseMapper<PositionRelation> {

}
