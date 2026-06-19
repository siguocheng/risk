package com.riskcontrol.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.domain.AccountCurrency;
import com.riskcontrol.domain.Role;
import com.riskcontrol.domain.User;
import com.riskcontrol.domain.vo.role.RoleQuery;
import com.riskcontrol.domain.vo.user.UserQuery;
import com.riskcontrol.service.IAccountCurrencyService;
import com.riskcontrol.service.IRoleService;
import com.riskcontrol.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(description = "下拉", name = "下拉")
@RestController
@RequestMapping("/select")
public class SelectController {

    @Resource
    IRoleService roleService;

    @Resource
    private IUserService userService;

    @Resource
    private IAccountCurrencyService accountCurrencyService;

    @Operation(summary ="取得角色")
    @PostMapping("/pc/role/query-list")
    public ResultBean<List<Role>> roleQueryList(@RequestBody RoleQuery roleQuery){

        return new ResultBean<>(roleService.queryList(roleQuery));
    }

    @Operation(summary ="用户下拉")
    @RequestMapping({"/pc/user"})
    public ResultBean<List<User>> listWorker(UserQuery query, HttpServletRequest request) {
        List<User> workerSelect = userService.list();
        return new ResultBean<>(workerSelect);
    }

    @Operation(summary = "取得账号信息")
    @GetMapping("/pc/account")
    public ResultBean<List<AccountCurrency>> getAccountCurrencyList(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "accountCode", required = false) String accountCode) {
        LambdaQueryWrapper<AccountCurrency> queryWrapper = new LambdaQueryWrapper<>();
        if (id != null) {
            queryWrapper.eq(AccountCurrency::getId, id);
        }
        if (accountCode != null && !accountCode.isEmpty()) {
            queryWrapper.eq(AccountCurrency::getAccountCode, accountCode);
        }
        List<AccountCurrency> list = accountCurrencyService.list(queryWrapper);
        return new ResultBean<>(list);
    }
}
