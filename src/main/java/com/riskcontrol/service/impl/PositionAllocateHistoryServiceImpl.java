package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.PositionAllocateHistoryMapper;
import com.riskcontrol.domain.PositionAllocateHistory;
import com.riskcontrol.service.IPositionAllocateHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 持仓分配历史Service业务层处理
 *
 * @author zpc
 * @date 2026-06-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PositionAllocateHistoryServiceImpl extends ServiceImpl<PositionAllocateHistoryMapper, PositionAllocateHistory> implements IPositionAllocateHistoryService {

}
