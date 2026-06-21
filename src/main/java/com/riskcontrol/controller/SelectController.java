package com.riskcontrol.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.domain.AccountCurrency;
import com.riskcontrol.domain.InvestmentStrategy;
import com.riskcontrol.domain.Position;
import com.riskcontrol.domain.PositionRelation;
import com.riskcontrol.domain.Role;
import com.riskcontrol.domain.Trader;
import com.riskcontrol.domain.User;
import com.riskcontrol.domain.vo.investmentstrategy.InvestmentStrategySelectQuery;
import com.riskcontrol.domain.vo.position.PositionInfoVo;
import com.riskcontrol.domain.vo.role.RoleQuery;
import com.riskcontrol.domain.vo.trader.TraderSelectQuery;
import com.riskcontrol.domain.vo.user.UserQuery;
import com.riskcontrol.service.IAccountCurrencyService;
import com.riskcontrol.service.IPositionRelationService;
import com.riskcontrol.service.IInvestmentStrategyService;
import com.riskcontrol.service.IPositionService;
import com.riskcontrol.service.IRoleService;
import com.riskcontrol.service.ITraderService;
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

import java.math.BigDecimal;
import java.util.ArrayList;
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

    @Resource
    private ITraderService traderService;

    @Resource
    private IInvestmentStrategyService investmentStrategyService;

    @Resource
    private IPositionService positionService;

    @Resource
    private IPositionRelationService compositeRelationService;

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

    @Operation(summary = "根据账号或交易员名称取得交易员")
    @PostMapping("/pc/trader")
    public ResultBean<List<String>> getTraderByAccountCodes(@RequestBody TraderSelectQuery query) {
        List<String> traderNames;

        // 如果没有任何查询条件，返回所有交易员
        if ((query.getAccountCodes() == null || query.getAccountCodes().isEmpty()) 
                && (query.getTraderName() == null || query.getTraderName().isEmpty())) {
            List<Trader> traders = traderService.list();
            traderNames = traders.stream()
                    .map(Trader::getTraderName)
                    .filter(name -> name != null && !name.isEmpty())
                    .distinct()
                    .toList();
            return new ResultBean<>(traderNames);
        }

        // 从 position_relation 表查询关联的交易员
        LambdaQueryWrapper<PositionRelation> queryWrapper = new LambdaQueryWrapper<>();
        
        if (query.getAccountCodes() != null && !query.getAccountCodes().isEmpty()) {
            queryWrapper.in(PositionRelation::getAccountCode, query.getAccountCodes());
        }
        
        if (query.getTraderName() != null && !query.getTraderName().isEmpty()) {
            queryWrapper.like(PositionRelation::getTraderName, query.getTraderName());
        }
        
        queryWrapper.isNull(PositionRelation::getDeleted);
        queryWrapper.select(PositionRelation::getTraderName);
        List<PositionRelation> relations = compositeRelationService.list(queryWrapper);

        // 提取不重复的交易员名称
        traderNames = relations.stream()
                .map(PositionRelation::getTraderName)
                .filter(name -> name != null && !name.isEmpty())
                .distinct()
                .toList();

        return new ResultBean<>(traderNames);
    }

    @Operation(summary = "根据账号或交易员取得投资策略")
    @PostMapping("/pc/investment-strategy")
    public ResultBean<List<String>> getInvestmentStrategyByConditions(@RequestBody InvestmentStrategySelectQuery query) {
        List<String> strategyNames;

        // 如果没有任何查询条件，返回所有投资策略
        if ((query.getAccountCodes() == null || query.getAccountCodes().isEmpty()) 
                && (query.getTraderNames() == null || query.getTraderNames().isEmpty())) {
            List<InvestmentStrategy> strategies = investmentStrategyService.list();
            strategyNames = strategies.stream()
                    .map(InvestmentStrategy::getStrategyName)
                    .filter(name -> name != null && !name.isEmpty())
                    .distinct()
                    .toList();
            return new ResultBean<>(strategyNames);
        }

        // 从 position_relation 表查询关联的投资策略
        LambdaQueryWrapper<PositionRelation> queryWrapper = new LambdaQueryWrapper<>();
        
        if (query.getAccountCodes() != null && !query.getAccountCodes().isEmpty()) {
            queryWrapper.in(PositionRelation::getAccountCode, query.getAccountCodes());
        }
        
        if (query.getTraderNames() != null && !query.getTraderNames().isEmpty()) {
            queryWrapper.in(PositionRelation::getTraderName, query.getTraderNames());
        }
        
        queryWrapper.isNull(PositionRelation::getDeleted);
        queryWrapper.select(PositionRelation::getStrategyName);
        List<PositionRelation> relations = compositeRelationService.list(queryWrapper);

        // 提取不重复的策略名称
        strategyNames = relations.stream()
                .map(PositionRelation::getStrategyName)
                .filter(name -> name != null && !name.isEmpty())
                .distinct()
                .toList();

        return new ResultBean<>(strategyNames);
    }

    @Operation(summary = "根据账号取得持仓信息")
    @GetMapping("/pc/position")
    public ResultBean<List<PositionInfoVo>> getPositionByAccountCode(
            @RequestParam(value = "accountCode", required = false) String accountCode) {
        // 查询持仓列表
        LambdaQueryWrapper<Position> positionQuery = new LambdaQueryWrapper<>();
        if (accountCode != null && !accountCode.isEmpty()) {
            positionQuery.eq(Position::getAccountCode, accountCode);
        }
        List<Position> positions = positionService.list(positionQuery);

        // 转换为VO并计算已分配数量
        List<PositionInfoVo> result = new ArrayList<>();
        for (Position position : positions) {
            PositionInfoVo vo = new PositionInfoVo();
            vo.setId(position.getId());
            vo.setConid(position.getConid());
            vo.setAccountCode(position.getAccountCode());
            vo.setModelCode(position.getModelCode());
            vo.setTotalPositionQty(position.getPositionQty());
            vo.setAvgCost(position.getAvgCost());
            vo.setUnrealizedPnl(position.getUnrealizedPnl());
            vo.setMarketPrice(position.getMarketPrice());
            vo.setMarketValue(position.getMarketValue());
            vo.setRealizedPnl(position.getRealizedPnl());

            // 计算已分配数量
            LambdaQueryWrapper<PositionRelation> relationQuery = new LambdaQueryWrapper<>();
            relationQuery.eq(PositionRelation::getAccountCode, position.getAccountCode());
            relationQuery.eq(PositionRelation::getConid, position.getConid());
            relationQuery.isNull(PositionRelation::getDeleted);
            List<PositionRelation> relations = compositeRelationService.list(relationQuery);

            BigDecimal allocatedQty = BigDecimal.ZERO;
            for (PositionRelation relation : relations) {
                if (relation.getPositionQty() != null) {
                    allocatedQty = allocatedQty.add(relation.getPositionQty());
                }
            }
            vo.setAllocatedPositionQty(allocatedQty);

            // 计算剩余可分配数量
            BigDecimal totalQty = position.getPositionQty() != null ? position.getPositionQty() : BigDecimal.ZERO;
            vo.setRemainingPositionQty(totalQty.subtract(allocatedQty));

            result.add(vo);
        }

        return new ResultBean<>(result);
    }
}
