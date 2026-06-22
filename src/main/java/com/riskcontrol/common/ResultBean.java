package com.riskcontrol.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 返回对象包装类(带泛型)
 * 
 */
@Data
public class ResultBean<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int CHECK_FAIL = -1;

	public static final int UNKNOWN_EXCEPTION = -99;

	public static final int UN_LOGIN = 10;

	public static final int PERMISSION_CHANGED = 11;

	public static final ResultBean<?> EMPTY = new ResultBean<>();

	/**
	 * 返回的信息(主要出错的时候使用)
	 */
	@Schema(description = "描述信息")
	private String msg = "success";

	private String errorMsg;

	/**
	 * 接口返回码, 0表示成功, 其他看对应的定义
	 * 晓风轻推荐的做法是:
	 * 0   : 成功
	 * >0 : 表示已知的异常(例如提示错误等, 需要调用地方单独处理)
	 * <0 : 表示未知的异常(不需要单独处理, 调用方统一处理)
	 */
	@Schema(description = "返回码:0表示成功 小于0表示未知异常，大于0 业务异常",example = "0")
	private int code = 0;
	/**
	 * 返回的数据
	 */
	@Schema(description = "数据")
	private T data;

	public ResultBean() {
	}

	public ResultBean(T data) {
		this.data = data;
	}

	public ResultBean(T data, String msg) {
		this.data = data;
		this.msg = msg;
	}

	public ResultBean(int code, String msg){
		this.code = code;
		this.msg = msg;
	}

	public ResultBean(int code, T data, String msg){
		this.code = code;
		this.data = data;
		this.msg = msg;
	}

}
