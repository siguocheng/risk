package com.riskcontrol.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.domain.Contract;
import com.riskcontrol.domain.ContractMarketHistory;
import com.riskcontrol.domain.bo.ContractBo;
import com.riskcontrol.domain.vo.ContractMarketHistoryQuery;
import com.riskcontrol.service.IContractMarketHistoryService;
import com.riskcontrol.service.IContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 合约控制器
 *
 * @author zpc
 * @date 2026-07-21
 */
@Tag(name = "合约管理")
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/contract")
public class ContractController extends BaseController {

    private final IContractService contractService;
    private final IContractMarketHistoryService contractMarketHistoryService;

    @Operation(summary = "合约列表分页查询")
    @PostMapping("/pc/query-page")
    public ResultBean<IPage<Contract>> queryList(@RequestBody ContractBo query) {
        return new ResultBean<>(contractService.queryPage(query));
    }

    @Operation(summary = "根据ID更新合约")
    @PostMapping("/pc/update")
    public ResultBean<Boolean> update(@RequestBody Contract contract) {
        return new ResultBean<>(contractService.updateById(contract));
    }

    @Operation(summary = "合约历史行情分页查询")
    @PostMapping("/pc/market-history")
    public ResultBean<IPage<ContractMarketHistory>> queryMarketHistory(@RequestBody ContractMarketHistoryQuery query) {
        return new ResultBean<>(contractMarketHistoryService.queryPageByConid(query.getConid(), query.getPageNum(), query.getPageSize()));
    }

}