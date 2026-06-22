package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.Position;
import com.riskcontrol.domain.vo.position.PositionAllocateRequest;

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
    boolean allocatePosition(PositionAllocateRequest request);
}
