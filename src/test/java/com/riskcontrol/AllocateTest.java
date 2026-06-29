package com.riskcontrol;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.riskcontrol.domain.*;
import com.riskcontrol.domain.vo.position.PositionAllocateItem;
import com.riskcontrol.domain.vo.position.PositionAllocateRequest;
import com.riskcontrol.service.*;
import jakarta.annotation.Resource;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootTest
public class AllocateTest {

    @Resource
    private IPositionService positionService;

    @Resource
    private ITraderService traderService;

    @Resource
    private IInvestmentStrategyService investmentStrategyService;

    @Resource
    private IPositionRelationService positionRelationService;

    @Resource
    private IPositionRelationHistoryService positionRelationHistoryService;

    @Test
    public void allocate(){
        // 取得position表的所有信息
        LambdaQueryWrapper<Position> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Position::getId);
        List<Position> positionList = positionService.list(queryWrapper);
        System.out.println("Position list size: " + positionList.size());

        // 取得所有交易员
        List<Trader> traderList = traderService.list();
        System.out.println("Trader list size: " + traderList.size());

        // 取得所有投资策略
        List<InvestmentStrategy> strategyList = investmentStrategyService.list();
        System.out.println("Strategy list size: " + strategyList.size());

        // 循环positionList，将positionQty分成3分，随机分配给交易员和交易策略
        Random random = new Random();
        List<PositionRelation> saveList = new ArrayList<>();

        for (Position position : positionList) {
            if (position.getPositionQty() == null || position.getPositionQty().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            BigDecimal totalQty = position.getPositionQty();
            // 将持仓数量分成3份
            BigDecimal[] parts = splitIntoThreeParts(totalQty, random);

            PositionAllocateRequest request = new PositionAllocateRequest();
            request.setId(position.getId());
            request.setOperateType(1);

            List<PositionAllocateItem> details = new ArrayList<>();
            request.setDetails(details);

            for (int i = 0; i < 3; i++) {
                // 随机选择交易员
                Trader trader = traderList.get(random.nextInt(traderList.size()));
                // 随机选择投资策略
                InvestmentStrategy strategy = strategyList.get(random.nextInt(strategyList.size()));

                PositionAllocateItem item = new PositionAllocateItem();
                item.setAccountCode(position.getAccountCode());
                item.setConid(position.getConid());
                item.setTraderName(trader.getTraderName());
                item.setStrategyName(strategy.getStrategyName());
                item.setAllocateQty(parts[i]);

                details.add(item);
            }

            positionService.allocatePosition(request);
        }
    }

    @Test
    public void listPositionRelation(){
        // 取得position_relation表的全量数据
        List<PositionRelation> positionRelationList = positionRelationService.list();
        System.out.println("PositionRelation list size: " + positionRelationList.size());

        // 循环365天，生成position_relation_history数据
        LocalDate startDate = LocalDate.now().minusDays(364);
        LocalDate endDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Random random = new Random();

        while (!startDate.isAfter(endDate)) {
            String dailyDate = startDate.format(formatter);
            System.out.println("Processing date: " + dailyDate);

            List<PositionRelationHistory> historyList = new ArrayList<>();

            for (PositionRelation relation : positionRelationList) {
                PositionRelationHistory history = new PositionRelationHistory();
                history.setDailyDate(dailyDate);
                history.setAccountCode(relation.getAccountCode());
                history.setConid(relation.getConid());
                history.setStrategyName(relation.getStrategyName());
                history.setTraderName(relation.getTraderName());
                history.setPositionQty(relation.getPositionQty());
                history.setRealizedPnl(relation.getRealizedPnl());

                BigDecimal originalUnrealizedPnl = relation.getUnrealizedPnl();
                if (originalUnrealizedPnl == null) {
                    originalUnrealizedPnl = BigDecimal.ZERO;
                }

                double randomDelta = (random.nextDouble() * 0.2) - 0.1;
                BigDecimal adjustedUnrealizedPnl = originalUnrealizedPnl
                        .add(BigDecimal.valueOf(randomDelta))
                        .setScale(8, BigDecimal.ROUND_HALF_UP);

                history.setUnrealizedPnl(adjustedUnrealizedPnl);
                historyList.add(history);
            }

            if (!historyList.isEmpty()) {
                positionRelationHistoryService.saveBatch(historyList);
                System.out.println("Saved " + historyList.size() + " records for date: " + dailyDate);
            }

            startDate = startDate.plusDays(1);
        }

        System.out.println("Generate position_relation_history completed!");
    }

    /**
     * 将持仓数量按整数随机分成3份
     */
    private BigDecimal[] splitIntoThreeParts(BigDecimal total, Random random) {
        BigDecimal[] parts = new BigDecimal[3];

        // 将总数转为整数进行处理
        long totalLong = total.longValue();
        if (totalLong <= 0) {
            parts[0] = BigDecimal.ZERO;
            parts[1] = BigDecimal.ZERO;
            parts[2] = BigDecimal.ZERO;
            return parts;
        }

        // 随机生成两个分割点（确保不为0且不同）
        long part1, part2, part3;
        if (totalLong == 1) {
            part1 = 1;
            part2 = 0;
            part3 = 0;
        } else if (totalLong == 2) {
            part1 = random.nextInt(2) + 1;
            part2 = totalLong - part1;
            part3 = 0;
        } else {
            // 生成两个不同的随机数作为分割点
            long rand1 = random.nextInt((int) totalLong) + 1;
            long rand2 = random.nextInt((int) totalLong) + 1;

            // 确保两个分割点不同
            while (rand1 == rand2) {
                rand2 = random.nextInt((int) totalLong) + 1;
            }

            long point1 = Math.min(rand1, rand2);
            long point2 = Math.max(rand1, rand2);

            part1 = point1;
            part2 = point2 - point1;
            part3 = totalLong - point2;
        }

        // 确保每份不为负数
        if (part1 < 0) part1 = 0;
        if (part2 < 0) part2 = 0;
        if (part3 < 0) part3 = 0;

        parts[0] = new BigDecimal(part1);
        parts[1] = new BigDecimal(part2);
        parts[2] = new BigDecimal(part3);

        return parts;
    }
}
