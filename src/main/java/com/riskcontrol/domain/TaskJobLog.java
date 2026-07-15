package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 定时任务执行日志实体类
 *
 * @author zpc
 * @date 2026-07-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("task_job_log")
public class TaskJobLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "执行时间")
    @TableField(value = "execute_time")
    private LocalDateTime executeTime;

    @Schema(description = "任务名称")
    @TableField(value = "job_name")
    private String jobName;

    @Schema(description = "执行结果：0-失败，1-成功")
    @TableField(value = "execute_result")
    private String executeResult;

    @Schema(description = "执行结果：0-失败，1-成功")
    @TableField(value = "execute_id")
    private String executeId;

}