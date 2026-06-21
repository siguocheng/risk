package com.riskcontrol.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.PositionRelation;
import com.riskcontrol.domain.vo.compositerelation.CompositeRelationModify;
import com.riskcontrol.domain.vo.compositerelation.CompositeRelationPage;
import com.riskcontrol.domain.vo.compositerelation.CompositeRelationQuery;

/**
 * 策略和交易员和账号和持仓之间的关系Service接口
 *
 * @author zpc
 * @date 2026-06-19
 */
public interface ICompositeRelationService extends IService<PositionRelation> {

    IPage<CompositeRelationPage> queryPage(CompositeRelationQuery query);

    Long create(CompositeRelationModify modify);

    Long update(CompositeRelationModify modify);

    Long delete(Long id);
}
