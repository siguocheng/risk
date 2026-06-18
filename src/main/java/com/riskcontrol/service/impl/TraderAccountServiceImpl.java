package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.TraderAccountMapper;
import com.riskcontrol.domain.TraderAccount;
import com.riskcontrol.service.ITraderAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 交易员和账号关系Service业务层处理
 *
 * @author zpc
 * @date 2026-06-18
 */
@Slf4j
@Service
public class TraderAccountServiceImpl extends ServiceImpl<TraderAccountMapper, TraderAccount> implements ITraderAccountService {

}