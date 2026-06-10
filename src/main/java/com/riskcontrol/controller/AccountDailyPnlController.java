package com.riskcontrol.controller;

import com.riskcontrol.service.IAccountDailyPnlService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 账户每日盈亏控制器
 *
 * @author zpc
 * @date 2026-06-10
 */
@Tag(name = "账户每日盈亏管理")
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/account-daily-pnl")
public class AccountDailyPnlController extends BaseController {

    private final IAccountDailyPnlService accountDailyPnlService;


}
