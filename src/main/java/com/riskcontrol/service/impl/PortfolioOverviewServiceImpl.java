package com.riskcontrol.service.impl;

import com.riskcontrol.domain.bo.PortfolioOverviewBo;
import com.riskcontrol.domain.vo.PortfolioOverviewVo;
import com.riskcontrol.service.IPortfolioOverviewService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioOverviewServiceImpl implements IPortfolioOverviewService {


    @Override
    public List<PortfolioOverviewVo> queryPortfolioOverview(PortfolioOverviewBo portfolioOverviewBo) {
        return null;
    }
}
