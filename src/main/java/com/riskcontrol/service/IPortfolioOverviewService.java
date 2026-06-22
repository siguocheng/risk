package com.riskcontrol.service;

import com.riskcontrol.domain.bo.PortfolioOverviewBo;
import com.riskcontrol.domain.vo.PortfolioOverviewVo;

import java.util.List;

public interface IPortfolioOverviewService {

    List<PortfolioOverviewVo> queryPortfolioOverview(PortfolioOverviewBo portfolioOverviewBo);
}
