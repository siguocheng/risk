package com.riskcontrol.service;

import com.riskcontrol.domain.bo.PortfolioOverviewBo;
import com.riskcontrol.domain.vo.PortfolioOverviewData;
import com.riskcontrol.domain.vo.PortfolioOverviewVo;
import com.riskcontrol.domain.vo.dashboard.RiskControlQuery;
import com.riskcontrol.domain.vo.dashboard.RiskControlVarModule;

import java.util.List;

public interface IPortfolioOverviewService {

    PortfolioOverviewData queryPortfolioOverview(PortfolioOverviewBo portfolioOverviewBo);

    RiskControlVarModule queryVar(RiskControlQuery query);
}
