package com.riskcontrol.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.domain.*;
import com.riskcontrol.domain.bo.AccountCodeBo;
import com.riskcontrol.domain.vo.AccountSelectVo;
import com.riskcontrol.domain.vo.ContractQuery;
import com.riskcontrol.domain.vo.ReferenceIndexQuery;
import com.riskcontrol.domain.vo.TraderSelectVo;
import com.riskcontrol.domain.vo.investmentstrategy.InvestmentStrategySelectQuery;
import com.riskcontrol.domain.vo.position.PositionInfoVo;
import com.riskcontrol.domain.vo.positionrelation.PositionRelationSelectQuery;
import com.riskcontrol.domain.vo.positionrelation.PositionRelationSelectVo;
import com.riskcontrol.domain.vo.role.RoleQuery;
import com.riskcontrol.domain.vo.trader.InvestmentStrategySelectVo;
import com.riskcontrol.domain.vo.trader.TraderSelectQuery;
import com.riskcontrol.domain.vo.user.UserQuery;
import com.riskcontrol.domain.ExchangeZone;
import com.riskcontrol.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private IPositionRelationService positionRelationService;

    @Operation(summary ="取得角色")
    @PostMapping("/pc/role/query-list")
    public ResultBean<List<Role>> roleQueryList(@RequestBody RoleQuery roleQuery){

        return new ResultBean<>(roleService.queryList(roleQuery));
    }

    @Operation(summary ="用户下拉")
    @PostMapping({"/pc/user"})
    public ResultBean<List<User>> listWorker(@RequestBody UserQuery query, HttpServletRequest request) {
        List<User> workerSelect = userService.list();
        return new ResultBean<>(workerSelect);
    }

    @Operation(summary = "取得账号信息")
    @PostMapping("/pc/account")
    public ResultBean<List<SelectVo>> getAccountCurrencyList(@RequestBody AccountCodeBo query) {
        LambdaQueryWrapper<AccountCurrency> queryWrapper = new LambdaQueryWrapper<>();
        if (query.getAccountCode() != null && !query.getAccountCode().isEmpty()) {
            queryWrapper.eq(AccountCurrency::getAccountCode, query.getAccountCode());
        }
        List<AccountCurrency> list = accountCurrencyService.list(queryWrapper);

        List<SelectVo> newList = new ArrayList<>();
        for (AccountCurrency accountCurrency : list) {
            SelectVo data = new SelectVo();
            data.setLabel(accountCurrency.getAccountCode());
            data.setValue(accountCurrency.getAccountCode());
            newList.add(data);
        }

        return new ResultBean<>(newList);
    }

    @Operation(summary = "取得交易员")
    @PostMapping("/pc/trader")
    public ResultBean<List<SelectVo>> getTraderByAccountCodes(@RequestBody TraderSelectQuery query) {

        LambdaQueryWrapper<Trader> queryWrapper = new LambdaQueryWrapper<>();
        if (query.getTraderName() != null && !query.getTraderName().isEmpty()) {
            queryWrapper.eq(Trader::getTraderName, query.getTraderName());
        }

        List<SelectVo> traderNames = new ArrayList<>();
        List<Trader> list = traderService.list();
        for (Trader trader : list) {
            SelectVo data = new SelectVo();
            data.setLabel(trader.getTraderName());
            data.setValue(trader.getTraderName());
            traderNames.add(data);
        }
        return new ResultBean<>(traderNames);
    }

    @Operation(summary = "取得投资策略")
    @PostMapping("/pc/investment-strategy")
    public ResultBean<List<SelectVo>> getInvestmentStrategyByConditions(@RequestBody InvestmentStrategySelectQuery query) {

        List<SelectVo> result = new ArrayList<>();
        LambdaQueryWrapper<InvestmentStrategy> queryWrapper = new LambdaQueryWrapper<>();
        if (query.getStrategyName() != null && !query.getStrategyName().isEmpty()) {
            queryWrapper.eq(InvestmentStrategy::getStrategyName, query.getStrategyName());
        }
        List<InvestmentStrategy> strategies = investmentStrategyService.list(queryWrapper);

        for (InvestmentStrategy strategy : strategies) {
            SelectVo data = new SelectVo();
            data.setLabel(strategy.getStrategyName());
            data.setValue(strategy.getStrategyName());
            result.add(data);
        }

        return new ResultBean<>(result);
    }

    @Operation(summary = "账号、策略、交易员联动接口")
    @PostMapping("/pc/position-relation")
    public ResultBean<List<SelectVo>> getInvestmentStrategyByConditions(@RequestBody PositionRelationSelectQuery query) {
        LambdaQueryWrapper<PositionRelation> queryWrapper = new LambdaQueryWrapper<>();
        if (!CollectionUtils.isEmpty(query.getAccountCodes())) {
            queryWrapper.in(PositionRelation::getAccountCode, query.getAccountCodes());
        }
        if (!CollectionUtils.isEmpty(query.getStrategyNames())) {
            queryWrapper.in(PositionRelation::getStrategyName, query.getStrategyNames());
        }
        if (!CollectionUtils.isEmpty(query.getTraderNames())) {
            queryWrapper.in(PositionRelation::getTraderName, query.getTraderNames());
        }

        List<PositionRelation> list = positionRelationService.list(queryWrapper);

        list = list.stream().distinct().collect(Collectors.toList());

        List<SelectVo> result = new ArrayList<>();
        for (PositionRelation positionRelation : list) {
            SelectVo data = new SelectVo();
//            BeanUtils.copyProperties(positionRelation, data);
            if (query.getQueryType() == 1) {
                data.setValue(positionRelation.getTraderName());
                data.setLabel(positionRelation.getTraderName());
            } else if (query.getQueryType() == 2) {
                data.setValue(positionRelation.getStrategyName());
                data.setLabel(positionRelation.getStrategyName());
            }
            result.add(data);
        }
        
        return new ResultBean<>(result);
    }

    @Resource
    IContractMarketService contractMarketService;

    @Resource
    IContractService contractService;

    @Resource
    IContractSectorService contractSectorService;

    @Resource
    IExchangeZoneService exchangeZoneService;

    @Operation(summary = "取得对标指数")
    @PostMapping("/pc/reference-index")
    public ResultBean<List<SelectVo>> referenceIndex(@RequestBody ReferenceIndexQuery query) {
        LambdaQueryWrapper<ContractMarket> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContractMarket::getReferenceIndex, 1);
        List<ContractMarket> list = contractMarketService.list(queryWrapper);

        List<SelectVo> result = new ArrayList<>();
        for (ContractMarket contractMarket : list) {
            SelectVo data = new SelectVo();
            data.setLabel(contractMarket.getSymbol());
            data.setValue(contractMarket.getConid() + "");
            result.add(data);
        }

        return new ResultBean<>(result);
    }

    @Operation(summary = "取得合约")
    @PostMapping("/pc/contract")
    public ResultBean<List<SelectVo>> getContractList(@RequestBody ContractQuery query) {
        LambdaQueryWrapper<Contract> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(query.getSymbol())) {
            queryWrapper.like(Contract::getSymbol, query.getSymbol());
        }
        queryWrapper.orderByAsc(Contract::getId);

        List<Contract> list = contractService.list(queryWrapper);

        List<SelectVo> result = new ArrayList<>();
        for (Contract contract : list) {
            SelectVo data = new SelectVo();
            data.setLabel(contract.getSymbol());
            data.setValue(contract.getConid() + "");
            result.add(data);
        }

        return new ResultBean<>(result);
    }

    @Operation(summary = "取得行业板块")
    @PostMapping("/pc/contract-sector")
    public ResultBean<List<SelectVo>> getContractSectorList() {
        LambdaQueryWrapper<ContractSector> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ContractSector::getType, 1);
        queryWrapper.select(ContractSector::getSector).groupBy(ContractSector::getSector);
        List<ContractSector> list = contractSectorService.list(queryWrapper);

        list = list.stream().filter(data -> data != null && StringUtils.isNotEmpty(data.getSector())).distinct().collect(Collectors.toList());

        List<SelectVo> result = new ArrayList<>();
        for (ContractSector contractSector : list) {
            SelectVo data = new SelectVo();
            data.setLabel(contractSector.getSector());
            data.setValue(contractSector.getSector());
            result.add(data);
        }

        return new ResultBean<>(result);
    }

    @Operation(summary = "取得地区")
    @GetMapping("/pc/zone")
    public ResultBean<List<SelectVo>> getZoneList() {
        LambdaQueryWrapper<ExchangeZone> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(ExchangeZone::getZone, ExchangeZone::getZoneValue).groupBy(ExchangeZone::getZone, ExchangeZone::getZoneValue);
        List<ExchangeZone> list = exchangeZoneService.list(queryWrapper);

        List<SelectVo> result = new ArrayList<>();
        for (ExchangeZone exchangeZone : list) {
            SelectVo data = new SelectVo();
            data.setLabel(exchangeZone.getZone());
            data.setValue(exchangeZone.getZoneValue());
            result.add(data);
        }

        return new ResultBean<>(result);
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
            List<PositionRelation> relations = positionRelationService.list(relationQuery);

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
