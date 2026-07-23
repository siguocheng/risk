package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.PositionExecutionMapper;
import com.riskcontrol.domain.AccountContract;
import com.riskcontrol.domain.Contract;
import com.riskcontrol.domain.PositionAllocateHistory;
import com.riskcontrol.domain.PositionExecution;
import com.riskcontrol.domain.bo.PortfolioOverviewBo;
import com.riskcontrol.domain.vo.positionexecution.PositionExecutionPage;
import com.riskcontrol.domain.vo.positionexecution.PositionExecutionQuery;
import com.riskcontrol.service.IAccountContractService;
import com.riskcontrol.service.IContractService;
import com.riskcontrol.service.IPositionAllocateHistoryService;
import com.riskcontrol.service.IPositionExecutionService;
import com.riskcontrol.util.DateUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.riskcontrol.domain.vo.positionexecution.PositionExecutionErrorVo;
import com.riskcontrol.domain.vo.positionexecution.PositionExecutionImportVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 成交明细Service业务层处理
 *
 * @author zpc
 * @date 2026-06-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PositionExecutionServiceImpl extends ServiceImpl<PositionExecutionMapper, PositionExecution> implements IPositionExecutionService {

    private final IPositionAllocateHistoryService positionAllocateHistoryService;

    private final IContractService contractService;

    @Override
    public boolean saveOrUpdateByExecId(PositionExecution positionExecution) {
        LambdaQueryWrapper<PositionExecution> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PositionExecution::getExecId, positionExecution.getExecId());

        long count = this.count(queryWrapper);
        if (count > 0) {
            return this.update(positionExecution, queryWrapper);
        } else {
            positionExecution.setStatus(0);
            return this.save(positionExecution);
        }
    }

    @Override
    public IPage<PositionExecutionPage> queryPage(PositionExecutionQuery query) {
        Page<PositionExecution> page = new Page<>(query.getPageNum(), query.getPageSize());

        this.handleStartEndDate(query);
        LambdaQueryWrapper<PositionExecution> queryWrapper = new LambdaQueryWrapper<>();
        if (!CollectionUtils.isEmpty(query.getAccountCodes())) {
            queryWrapper.in(PositionExecution::getAccountCode, query.getAccountCodes());
        }
        if (!CollectionUtils.isEmpty(query.getConids())) {
            queryWrapper.in(PositionExecution::getConid, query.getConids());
        }
        if (StringUtils.isNotEmpty(query.getSecType())) {
            queryWrapper.eq(PositionExecution::getSecType, query.getSecType());
        }
        if (StringUtils.isNotEmpty(query.getSecType())) {
            queryWrapper.eq(PositionExecution::getSecType, query.getSecType());
        }
        if (StringUtils.isNotEmpty(query.getStartDate())) {
            queryWrapper.ge(PositionExecution::getExecutionDate, query.getStartDate());
        }
        if (StringUtils.isNotEmpty(query.getEndDate())) {
            queryWrapper.le(PositionExecution::getExecutionDate, query.getEndDate());
        }
        queryWrapper.orderByAsc(PositionExecution::getTime);

        IPage<PositionExecution> entityPage = this.page(page, queryWrapper); // 取得符合条件的交易

        IPage<PositionExecutionPage> pageList = entityPage.convert(entity -> {
            PositionExecutionPage vo = new PositionExecutionPage();
            BeanUtils.copyProperties(entity, vo);

            List<PositionAllocateHistory> positionAllocateHistories = positionAllocateHistoryService.listPositionAllocateHistoryByKey(vo.getId(), null);
            // 求和，空字段当作0处理
            BigDecimal sum = positionAllocateHistories.stream()
                    .map(item -> item.getAllocateQty() == null ? BigDecimal.ZERO : item.getAllocateQty())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Contract contract = contractService.getByConid(vo.getConid());

            vo.setMultiplier(contract.getMultiplier());
            vo.setPositionAllocateDetails(positionAllocateHistoryService.listPositionAllocateHistoryByKey(null, entity.getId()));

            return vo;
        });

        return pageList;
    }

    private void handleStartEndDate(PositionExecutionQuery query){
        if (query.getDateType() != null) {
            // 当日或者近7日
            if (query.getDateType() == 1) {
                query.setEndDate(DateUtil.localDateToString(LocalDate.now()));
                query.setStartDate(DateUtil.localDateToString(LocalDate.now()));
            } else if (query.getDateType() == 7) {
                query.setEndDate(DateUtil.localDateToString(LocalDate.now()));
                query.setStartDate(DateUtil.localDateToString(LocalDate.now().minusDays(6)));
            }
            // 当年1月1日开始
            else if (query.getDateType() == 11) {
                query.setEndDate(DateUtil.localDateToString(LocalDate.now()));
                query.setStartDate(DateUtil.localDateToString(LocalDate.now().with(TemporalAdjusters.firstDayOfYear())));
            }
            // 近1年
            else if (query.getDateType() == 365) {
                query.setEndDate(DateUtil.localDateToString(LocalDate.now()));
                query.setStartDate(DateUtil.localDateToString(LocalDate.now().minusDays(365)));
            }
            // 近30天
            else if (query.getDateType() == 30) {
                query.setEndDate(DateUtil.localDateToString(LocalDate.now()));
                query.setStartDate(DateUtil.localDateToString(LocalDate.now().minusDays(30)));
            }
        }
    }

    @Override
    public List<PositionExecution> listPositionExecutionByKey(String accountCode, int conid, String executionDate) {
        LambdaQueryWrapper<PositionExecution> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PositionExecution::getAccountCode, accountCode)
                .eq(PositionExecution::getConid, conid)
                .eq(PositionExecution::getExecutionDate, executionDate);

        return this.list(wrapper);
    }

    @Override
    public String importPositionExecution(InputStream inputStream) {
        List<PositionExecutionImportVo> importList = new ArrayList<>();

        EasyExcel.read(inputStream, PositionExecutionImportVo.class, new AnalysisEventListener<PositionExecutionImportVo>() {
            @Override
            public void invoke(PositionExecutionImportVo data, AnalysisContext context) {
                importList.add(data);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                log.info("Excel解析完成，共{}条数据", importList.size());
            }
        }).sheet().doRead();

        if (CollectionUtils.isEmpty(importList)) {
            return null;
        }

        List<PositionExecution> successList = new ArrayList<>();
        List<PositionExecutionErrorVo> errorList = new ArrayList<>();

        for (int i = 0; i < importList.size(); i++) {
            PositionExecutionImportVo vo = importList.get(i);
            int rowNum = i + 2;
            StringBuilder errorMsg = new StringBuilder();

            if (vo.getAccountCode() == null) {
                errorMsg.append("来源不能为空;");
            }

            if (StringUtils.isEmpty(vo.getSymbol())) {
                errorMsg.append("合约不能为空;");
            }

            if (StringUtils.isEmpty(vo.getShorName())) {
                errorMsg.append("代码不能为空;");
            }

            if (vo.getTime() == null || vo.getTime().isEmpty()) {
                errorMsg.append("交易时间不能为空;");
            }

            if (vo.getShares() == null) {
                errorMsg.append("成交数量不能为空;");
            }

            if (vo.getPrice() == null) {
                errorMsg.append("成交价格不能为空;");
            }

            Contract contract = new Contract();
            if (StringUtils.isNotEmpty(vo.getSymbol()) && StringUtils.isNotEmpty(vo.getShorName())) {
                contract = contractService.getBySymbolAndShortName(vo.getSymbol(), vo.getShorName());
                if (contract == null) {
                    errorMsg.append("当前合约不存在;");
                }
            }

            if (errorMsg.length() > 0) {
                PositionExecutionErrorVo errorVo = new PositionExecutionErrorVo();
                BeanUtils.copyProperties(vo, errorVo);
                errorVo.setErrorMsg("第" + rowNum + "行: " + errorMsg.toString());
                errorList.add(errorVo);
            } else {
                PositionExecution entity = new PositionExecution();
                entity.setConid(contract.getConid());
                entity.setSecType(contract.getSecType());
                entity.setSymbol(vo.getSymbol());
                entity.setTime(vo.getTime());
                entity.setExecutionTime(vo.getTime());
                if (vo.getTime() != null && vo.getTime().length() >= 8) {
                    entity.setExecutionDate(DateUtil.localDateToString(DateUtil.stringToLocalDate(vo.getTime().substring(0, 8), "yyyyMMdd")));
                }
                entity.setShares(new BigDecimal(vo.getShares()));
                entity.setPrice(new BigDecimal(vo.getPrice()));
                entity.setRemainQty(new BigDecimal(vo.getShares()));
                entity.setAllocateRemainQty(new BigDecimal(vo.getShares()));
                entity.setStatus(0);
                successList.add(entity);
            }
        }

        for (PositionExecution entity : successList) {
            this.saveOrUpdateByExecId(entity);
        }

        if (!CollectionUtils.isEmpty(errorList)) {
            String errorFileName = "交易数据_错误_" + System.currentTimeMillis() + ".xlsx";
            String tempDir = System.getProperty("java.io.tmpdir");
            String filePath = tempDir + File.separator + errorFileName;

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                EasyExcel.write(fos, PositionExecutionErrorVo.class).sheet("错误数据").doWrite(errorList);
                log.info("错误文件已生成: {}", filePath);
                return "/position-execution/pc/download-error-file?fileName=" + errorFileName;
            } catch (IOException e) {
                log.error("生成错误文件失败", e);
                return null;
            }
        }

        return null;
    }
}