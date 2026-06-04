package com.riskcontrol.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.riskcontrol.util.RequestContextHolderUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 自动赋值
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Resource
	RequestContextHolderUtil requestContextHolderUtil;

	@Override
	public void insertFill(MetaObject metaObject) {

		Long userId = requestContextHolderUtil.getUserId();

		this.strictInsertFill(metaObject, "createId", Long.class, userId);
		this.strictInsertFill(metaObject, "modifiedId", Long.class, userId);
		this.strictInsertFill(metaObject, "deleted", Boolean.class, Boolean.FALSE);
		//创建-修改时间
		updateFillCommon(metaObject,true);
	}

	@Override
	public void updateFill(MetaObject metaObject) {

		Long userId = requestContextHolderUtil.getUserId();
		this.strictInsertFill(metaObject, "modifiedId", Long.class, userId);
		//修改时间
		updateFillCommon(metaObject,false);
	}


	/**
	 * 方法提取优化
	 * @param metaObject
	 * @param isInsert
	 */
	private void updateFillCommon(MetaObject metaObject, Boolean isInsert) {
		if (isInsert) {
			// 处理 gmtCreate 字段
//			handleField(metaObject, "gmtCreate", LocalDateTime.now());
			// 处理 createTime 字段
			handleField(metaObject, "createTime", LocalDateTime.now());
		}

		// 处理 gmtModified 字段
//		handleField(metaObject, "gmtModified", LocalDateTime.now());
		// 处理 modifiedTime 字段
		handleField(metaObject, "modifiedTime", LocalDateTime.now());
	}

	private void handleField(MetaObject metaObject, String fieldName, LocalDateTime defaultValue) {
		try {
			// 检查是否有 setter 方法
			if (metaObject.hasSetter(fieldName)) {
				// 获取字段的类型
				String fieldType = metaObject.getGetterType(fieldName).getName();
				// 根据typeName类型赋值
				if (fieldType.contains("LocalDateTime")) {
					this.setFieldValByName(fieldName, LocalDateTime.now(), metaObject);
				} else if (fieldType.contains("Date")) {
					this.setFieldValByName(fieldName, Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()), metaObject);
				}
			} else {
				// 如果没有 setter 方法，则使用 strictInsertFill 进行填充
				this.strictInsertFill(metaObject, fieldName, LocalDateTime.class, defaultValue);
			}
		} catch (Exception e) {
			// 处理异常，例如记录日志或抛出自定义异常
			e.printStackTrace();
		}
	}

}
