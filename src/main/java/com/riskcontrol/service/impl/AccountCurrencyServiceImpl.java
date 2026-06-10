package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.AccountCurrencyMapper;
import com.riskcontrol.domain.AccountCurrency;
import com.riskcontrol.service.IAccountCurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 账户币种Service业务层处理
 *
 * @author zpc
 * @date 2026-06-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCurrencyServiceImpl extends ServiceImpl<AccountCurrencyMapper, AccountCurrency>  implements IAccountCurrencyService {


}
