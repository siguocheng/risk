package com.riskcontrol.domain.vo.permisssion;

import com.riskcontrol.domain.Permission;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class PermissionTableVO extends Permission implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<PermissionTableVO> sonList;

}
