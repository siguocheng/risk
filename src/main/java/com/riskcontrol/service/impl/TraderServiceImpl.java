package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.TraderAccountMapper;
import com.riskcontrol.dao.TraderMapper;
import com.riskcontrol.domain.Trader;
import com.riskcontrol.domain.TraderAccount;
import com.riskcontrol.domain.vo.trader.TraderModify;
import com.riskcontrol.domain.vo.trader.TraderPage;
import com.riskcontrol.domain.vo.trader.TraderQuery;
import com.riskcontrol.service.ITraderAccountService;
import com.riskcontrol.service.ITraderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 交易员Service业务层处理
 *
 * @author zpc
 * @date 2026-06-18
 */
@Slf4j
@Service
public class TraderServiceImpl extends ServiceImpl<TraderMapper, Trader> implements ITraderService {

    @Resource
    ITraderAccountService traderAccountService;

    @Override
    public IPage<TraderPage> queryPage(TraderQuery query) {
        return this.page(query.build(), new LambdaQueryWrapper<Trader>()
                .like(query.getTraderName() != null, Trader::getTraderName, query.getTraderName())
                .isNull(Trader::getDeleted))
                .convert(trader -> {
                    TraderPage page = new TraderPage();
                    page.setId(trader.getId());
                    page.setTraderName(trader.getTraderName());
                    return page;
                });
    }

    @Override
    @Transactional
    public Long create(TraderModify modify) {
        // 创建交易员
        Trader trader = new Trader();
        trader.setTraderName(modify.getTraderName());
        this.save(trader);

        // 维护交易员和账号的关系
        saveTraderAccounts(trader.getId(), trader.getTraderName(), modify.getAccountCodes());

        return trader.getId();
    }

    @Override
    @Transactional
    public Long update(TraderModify modify) {
        // 更新交易员信息
        Trader trader = this.getById(modify.getId());
        if (trader == null) {
            throw new RuntimeException("交易员不存在");
        }
        trader.setTraderName(modify.getTraderName());
        this.updateById(trader);

        // 删除原有关联关系
        traderAccountService.remove(new LambdaQueryWrapper<TraderAccount>()
                .eq(TraderAccount::getTraderId, modify.getId()));

        // 重新维护交易员和账号的关系
        saveTraderAccounts(modify.getId(), modify.getTraderName(), modify.getAccountCodes());

        return modify.getId();
    }

    @Override
    @Transactional
    public Long delete(Long id) {
        // 删除交易员
        Trader trader = this.getById(id);
        if (trader == null) {
            throw new RuntimeException("交易员不存在");
        }
        trader.setDeleted(false);
        this.updateById(trader);

        // 删除关联关系
        traderAccountService.remove(new LambdaQueryWrapper<TraderAccount>()
                .eq(TraderAccount::getTraderId, id));

        return id;
    }

    /**
     * 保存交易员和账号的关系
     */
    private void saveTraderAccounts(Long traderId, String traderName, List<String> accountCodes) {
        if (accountCodes != null && !accountCodes.isEmpty()) {
            for (String accountCode : accountCodes) {
                TraderAccount traderAccount = new TraderAccount();
                traderAccount.setTraderId(traderId);
                traderAccount.setTraderName(traderName);
                traderAccount.setAccountCode(accountCode);
                traderAccountService.save(traderAccount);
            }
        }
    }
}