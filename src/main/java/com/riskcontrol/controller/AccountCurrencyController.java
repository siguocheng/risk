package com.riskcontrol.controller;

import com.riskcontrol.service.IAccountCurrencyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 账户币种控制器
 *
 * @author zpc
 * @date 2026-06-10
 */
@Tag(name = "账户币种管理")
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/account-currency")
public class AccountCurrencyController extends BaseController {

    private final IAccountCurrencyService accountCurrencyService;


}
