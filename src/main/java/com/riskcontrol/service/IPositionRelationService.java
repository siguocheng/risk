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

    Long create(PositionRelationModify modify);

    Long update(PositionRelationModify modify);

    Long delete(Long id);

    /**
     * 根据账户、合约、策略、交易员更新持仓数量
     *
     * @param accountCode   账户代码
     * @param conid         合约ID
     * @param strategyName  策略名称
     * @param traderName    交易员名称
     * @param qty           持仓数量（正数增加，负数减少）
     * @return 是否成功
     */
    boolean saveOrUpdatePositionQty(String accountCode, Integer conid, String strategyName, String traderName, BigDecimal qty);
}
