package com.riskcontrol.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(description = "风控仪表盘", name = "风控仪表盘")
@RestController
@RequestMapping("/risk-dashboard")
public class RiskControlDashboardController {


}
