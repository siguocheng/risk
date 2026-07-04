package com.riskcontrol.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.Position;
import com.riskcontrol.domain.vo.position.PositionAllocateRequest;
import com.riskcontrol.domain.vo.position.PositionPage;
import com.riskcontrol.domain.vo.position.PositionQuery;

/**
 * 持仓列表Service接口
 *
 * @author zpc
 * @date 2026-06-10
 */
public interface IPositionService extends IService<Position>  {

    boolean saveOrUpdatePosition(Position position);

    /**
     * 维护持仓分配记录
     *
     * @param request 分配请求
     * @return 是否成功
     */
    Boolean allocatePosition(PositionAllocateRequest request);

    /**
     * 分页查询持仓列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    IPage<PositionPage> queryPage(PositionQuery query);

    Position getPositionByConid(String accountCode, int conid);
}
