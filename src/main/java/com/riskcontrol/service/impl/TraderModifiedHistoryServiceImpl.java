package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.TraderModifiedHistoryMapper;
import com.riskcontrol.domain.TraderModifiedHistory;
import com.riskcontrol.service.ITraderModifiedHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TraderModifiedHistoryServiceImpl extends ServiceImpl<TraderModifiedHistoryMapper, TraderModifiedHistory> implements ITraderModifiedHistoryService {

}