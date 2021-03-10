/*
 *
 *  *  Copyright (c) 2019-2020, 冷冷 (wangiegie@gmail.com).
 *  *  <p>
 *  *  Licensed under the GNU Lesser General Public License 3.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *  <p>
 *  * https://www.gnu.org/licenses/lgpl.html
 *  *  <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.generate.common.base;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;



/**
 * 响应信息主体
 *
 * @param <T>
 * @author
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

