package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.TaskJobLogMapper;
import com.riskcontrol.domain.TaskJobLog;
import com.riskcontrol.service.ITaskJobLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 定时任务执行日志Service业务层处理
 *
 * @author zpc
 * @date 2026-07-15
 */
@Slf4j
@Service
public class TaskJobLogServiceImpl extends ServiceImpl<TaskJobLogMapper, TaskJobLog> implements ITaskJobLogService {

}