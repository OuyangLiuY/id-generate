package com.generate.common.base;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;



/**
 * 响应信息主体
 *
 * @param <T>
 * @author xxl
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class R<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private int code;

	@Getter
	@Setter
	private String msg;


	@Getter
	@Setter
	private T data;

	public static <T> R<T> ok() {
		return restResult(null, Constants.SUCCESS, Constants.MSG_SUCCESS);
	}

	public static <T> R<T> ok(String msg) {
		return restResult(null, Constants.SUCCESS, msg);
	}

	public static <T> R<T> ok(T data) {
		return restResult(data, Constants.SUCCESS, Constants.MSG_SUCCESS);
	}

	public static <T> R<T> ok(T data, String msg) {
		return restResult(data, Constants.SUCCESS, msg);
	}

	public static <T> R<T> failed() {
		return restResult(null, Constants.FAIL, Constants.MSG_FAIL);
	}

	public static <T> R<T> failed(String msg) {
		return restResult(null, Constants.FAIL, msg);
	}

	public static <T> R<T> failed(T data) {
		return restResult(data, Constants.FAIL, Constants.MSG_FAIL);
	}

	public static <T> R<T> failed(T data, String msg) {
		return restResult(data, Constants.FAIL, msg);
	}

	private static <T> R<T> restResult(T data, int code, String msg) {
		R<T> apiResult = new R<>();
		apiResult.setCode(code);
		apiResult.setData(data);
		apiResult.setMsg(msg);
		return apiResult;
	}
}

