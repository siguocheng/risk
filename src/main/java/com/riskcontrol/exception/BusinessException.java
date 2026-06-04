 /*
  * Copyright (C), 2018-2018, 杭州物源科技有限公司
  * FileName: BusinessException
  * Author:   Charlie
  * Date:     2018/9/29 12:09
  * Description: 基础业务异常
  */
 package com.riskcontrol.exception;

 /**
  * 基础业务异常
  *
  * @author Charlie
  * @date 2018/9/29 12:09
  */
 public class BusinessException extends RuntimeException {

     /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BusinessException() {
         super();
     }

     public BusinessException(String message) {
         super(message);
     }

 }
