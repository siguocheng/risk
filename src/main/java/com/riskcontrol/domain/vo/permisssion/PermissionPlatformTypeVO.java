package com.riskcontrol.domain.vo.permisssion;

import lombok.Data;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author huan.dong
 * @since 2023/10/30
 */
@Data
public class PermissionPlatformTypeVO {

    private List<PermissionTableVO> pc;
    private List<PermissionTableVO> app;
}
