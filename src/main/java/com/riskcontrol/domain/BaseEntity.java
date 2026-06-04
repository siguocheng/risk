package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.riskcontrol.constant.BaseConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 */
@Data
public abstract class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.AUTO)
	protected Long id;

	/**
	 * 删除标识(0未删除,1已删除)
	 */
	@Schema(description = "删除标识", hidden = true)
	@TableLogic
	@TableField(fill = FieldFill.INSERT)
	protected Boolean deleted;

	@Schema(description = "创建人ID", hidden = true)
	@JsonIgnore
    @TableField(fill = FieldFill.INSERT)
	protected Long createId;

	@Schema(description = "创建时间", hidden = true)
	@JsonIgnore
    @TableField(fill = FieldFill.INSERT)
	protected LocalDateTime createTime;

	@Schema(description = "修改人ID", hidden = true)
	@JsonIgnore
    @TableField(fill = FieldFill.INSERT_UPDATE)
	protected Long modifiedId;

	@Schema(description = "更新时间", hidden = true)
	@JsonIgnore
	@TableField(fill = FieldFill.INSERT_UPDATE)
	protected LocalDateTime modifiedTime;


	public static void setEntityBaseInfo(BaseEntity entity,Long userId) {
		if (null == entity.getId()) {
			setBaseInfo(entity,userId);
		} else {
			setUpdate(entity,userId);
		}
	}

	private static void setBaseInfo(BaseEntity entity, Long userId) {
		entity.setDeleted(BaseConstant.NORMAL);
		entity.setCreateTime(LocalDateTime.now());
		entity.setCreateId(userId);
		setUpdate(entity, userId);
	}

	private static void setUpdate(BaseEntity entity, Long userId) {
		entity.setModifiedId(userId);
		entity.setModifiedTime(LocalDateTime.now());
	}


}
