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

import java.io.IOException;
import java.util.List;

import com.alibaba.cloud.ai.example.manus.dynamic.mcp.model.vo.McpConfigRequestVO;
import com.alibaba.cloud.ai.example.manus.dynamic.mcp.model.po.McpConfigEntity;
import com.alibaba.cloud.ai.example.manus.dynamic.mcp.model.vo.McpServiceEntity;

/**
 * MCP服务接口，提供MCP服务管理功能
 */
public interface IMcpService {

	/**
	 * 添加MCP服务器
	 * @param mcpConfig MCP配置
	 * @throws IOException IO异常
	 */
	void addMcpServer(McpConfigRequestVO mcpConfig) throws IOException;

	/**
	 * 插入或更新MCP仓库
	 * @param mcpConfigVO MCP配置VO
	 * @return MCP配置实体列表
	 * @throws IOException IO异常
	 */
	List<McpConfigEntity> insertOrUpdateMcpRepo(McpConfigRequestVO mcpConfigVO) throws IOException;

	/**
	 * 移除MCP服务器
	 * @param id 服务器ID
	 */
	void removeMcpServer(long id);

	/**
	 * 移除MCP服务器
	 * @param mcpServerName 服务器名称
	 */
	void removeMcpServer(String mcpServerName);

	/**
	 * 获取MCP服务器列表
	 * @return MCP配置实体列表
	 */
	List<McpConfigEntity> getMcpServers();

	/**
	 * 获取函数回调
	 * @param planId 计划ID
	 * @return MCP服务实体列表
	 */
	List<McpServiceEntity> getFunctionCallbacks(String planId);

	/**
	 * 关闭指定计划的MCP服务
	 * @param planId 计划ID
	 */
	void close(String planId);

}
