
package com.generate.common.base;


public interface Constants {

	String ID_SEPARATOR = "_";

	/**
	 * 成功标记
	 */
	Integer SUCCESS = 200;
	/**
	 * 失败标记
	 */
	Integer FAIL = 500;

	String MSG_SUCCESS= "success";
	String MSG_FAIL= "系统错误";

	String SPLIT_CHAR = "@";

	/**
	 * 失败最大重试次数
	 */
	int MAX_TRIES = 3;
	long SNOWFLAKE_DEFAULT_EPOCH = 1218124800000L;
}
