package com.riskcontrol.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.PositionRelation;
import com.riskcontrol.domain.vo.positionrelation.PositionRelationModify;
import com.riskcontrol.domain.vo.positionrelation.PositionRelationPage;
import com.riskcontrol.domain.vo.positionrelation.PositionRelationQuery;

import java.math.BigDecimal;

/**
 * 策略和交易员和账号和持仓之间的关系Service接口
 *
 * @author zpc
 * @date 2026-06-19
 */
public interface IPositionRelationService extends IService<PositionRelation> {

    IPage<PositionRelationPage> queryPage(PositionRelationQuery query);

    boolean saveOrUpdatePositionQty(String accountCode, Integer conid, String strategyName, String traderName, BigDecimal qty);

    PositionRelation getPositionRelationByKey(String accountCode, Integer conid, String strategyName, String traderName);

}
