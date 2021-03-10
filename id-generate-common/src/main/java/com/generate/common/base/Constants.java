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


public interface Constants {

	String ID_SEPARATOR = "_";
	/**
	 * 删除
	 */
	String STATUS_DEL = "1";
	/**
	 * 正常
	 */
	String STATUS_NORMAL = "0";

	/**
	 * 锁定
	 */
	String STATUS_LOCK = "9";

	/**
	 * 菜单树根节点
	 */
	Integer MENU_TREE_ROOT_ID = -1;

	/**
	 * 菜单
	 */
	String MENU = "0";

	/**
	 * 编码
	 */
	String UTF8 = "UTF-8";

	/**
	 * JSON 资源
	 */
	String CONTENT_TYPE = "application/json; charset=utf-8";

	/**
	 * 前端工程名
	 */
	String FRONT_END_PROJECT = "pig-ui";

	/**
	 * 后端工程名
	 */
	String BACK_END_PROJECT = "pig";

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

	/**
	 * 验证码前缀
	 */
	String DEFAULT_CODE_KEY = "DEFAULT_CODE_KEY_";

	/**
	 * 当前页
	 */
	String CURRENT = "current";

	/**
	 * size
	 */
	String SIZE = "size";

	/**
	 * 排序方式：插入排序
	 */
	Byte ORDER_PATTERN_INSERT = 0;

	/**
	 * 排序方式：替换排序
	 */
	Byte ORDER_PATTERN_REPLACE = 1;

	/**
	 * 数量：无限制
	 */
	int NUMBER_LIMITLESS = -1;

	/**
	 * 是否为工作日：是
	 */
	int WORKDAY_FLAG_TRUE = 1;

	/**
	 * 是否为工作日：否
	 */
	int WORKDAY_FLAG_FALSE = 0;

	/**
	 * 会议室类型：标准
	 */
	Byte ROOM_TYPE_STANDARD = 0;

	/**
	 * 会议室类型：高级
	 */
	Byte ROOM_TYPE_ADVANCED = 1;

	/**
	 * 会议用品类型：标准
	 */
	Byte SUPPLY_TYPE_STANDARD = 0;

	/**
	 * 会议用品类型：高级
	 */
	Byte SUPPLY_TYPE_ADVANCED = 1;

	/**
	 * 是否通知IT：否
	 */
	Byte NOTICE_IT_FALSE = 0;

	/**
	 * 是否通知IT：是
	 */
	Byte NOTICE_IT_TRUE = 1;

	/**
	 * 是否失效：否
	 */
	Byte EXPIRE_FALSE = 0;

	/**
	 * 是否通知IT：是
	 */
	Byte EXPIRE_TRUE = 1;

	/**
	 * 默认开始工作时间
	 */
	String DEFAULT_START_WORKTIME = "09:00";

	/**
	 * 默认结束工作时间
	 */
	String DEFAULT_END_WORKTIME = "18:00";

	/**
	 * 默认空字符显示
	 */
	String DEFAULT_EMPTY_CONTENT = "无";

	/**
	 * 部门ID前缀：DEPT
	 */
	String PREFIX_DEPARTMENT_ID = "DEPT";

	/**
	 * 用户ID前缀：USER
	 */
	String PREFIX_USER_ID = "USER";
	String SPLIT_CHAR = "@";

	/**
	 * 失败最大重试次数
	 */
	int MAX_TRIES = 3;
}
