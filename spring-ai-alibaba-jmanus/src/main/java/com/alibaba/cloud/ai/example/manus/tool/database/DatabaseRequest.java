/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.ai.example.manus.tool.database;

/**
 * Database tool request object for encapsulating database operation request parameters
 *
 * <p>
 * This object contains all parameters required for database operations, supporting multiple database operation types, including SQL execution, table information queries, index queries, etc.
 * </p>
 *
 * @author Spring AI Alibaba Team
 * @since 1.0.0
 */
public class DatabaseRequest {

	/**
	 * Database operation type
	 *
	 * <p>
	 * Supported operation types include:
	 * </p>
	 * <ul>
	 * <li>{@code execute_sql} - Execute SQL queries</li>
	 * <li>{@code get_table_name} - Get table name list</li>
	 * <li>{@code get_table_meta} - Get table metadata information</li>
	 * <li>{@code get_table_index} - Get table index information</li>
	 * <li>{@code get_datasource_info} - Get data source information</li>
	 * </ul>
	 */
	private String action;

	/**
	 * SQL query statement
	 *
	 * <p>
	 * Used when operation type is {@code execute_sql}. Contains the SQL query statement to execute.
	 * </p>
	 */
	private String query;

	/**
	 * Text parameter
	 *
	 * <p>
	 * Used to specify table names, comments or other text information. Used for filtering when operation type is {@code get_table_name}, {@code get_table_meta},
	 * {@code get_table_index}.
	 * </p>
	 */
	private String text;

	/**
	 * Data source name
	 *
	 * <p>
	 * Specifies the data source name to use. If empty or not specified, the default data source will be used. Supports data source switching in multi-data source environments.
	 * </p>
	 */
	private String datasourceName;

	/**
	 * 获取数据库操作类型
	 * @return 操作类型字符串，如 "execute_sql"、"get_table_name" 等
	 */
	public String getAction() {
		return action;
	}

	/**
	 * 设置数据库操作类型
	 * @param action 操作类型字符串，不能为null
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * 获取SQL查询语句
	 * @return SQL查询语句，可能为null
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * 设置SQL查询语句
	 * @param query SQL查询语句，当操作类型为 "execute_sql" 时使用
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * 获取文本参数
	 * @return 文本参数，用于表名过滤等，可能为null
	 */
	public String getText() {
		return text;
	}

	/**
	 * 设置文本参数
	 * @param text 文本参数，用于指定表名、注释或其他过滤条件
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * 获取数据源名称
	 * @return 数据源名称，如果为null或空则使用默认数据源
	 */
	public String getDatasourceName() {
		return datasourceName;
	}

	/**
	 * 设置数据源名称
	 * @param datasourceName 数据源名称，用于指定要使用的数据源
	 */
	public void setDatasourceName(String datasourceName) {
		this.datasourceName = datasourceName;
	}

}
