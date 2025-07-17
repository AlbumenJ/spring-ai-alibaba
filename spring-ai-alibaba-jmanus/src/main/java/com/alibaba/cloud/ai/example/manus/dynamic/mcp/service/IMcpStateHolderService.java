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
package com.alibaba.cloud.ai.example.manus.dynamic.mcp.service;

import com.alibaba.cloud.ai.example.manus.dynamic.mcp.model.vo.McpState;

/**
 * MCP状态持有服务接口，管理MCP状态
 */
public interface IMcpStateHolderService {

	/**
	 * 获取MCP状态
	 * @param key 状态键
	 * @return MCP状态
	 */
	McpState getMcpState(String key);

	/**
	 * 设置MCP状态
	 * @param key 状态键
	 * @param state MCP状态
	 */
	void setMcpState(String key, McpState state);

	/**
	 * 移除MCP状态
	 * @param key 状态键
	 */
	void removeMcpState(String key);

}
