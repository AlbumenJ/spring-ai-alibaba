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
package com.alibaba.cloud.ai.example.manus.llm;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;

/**
 * LLM服务接口，提供聊天客户端和内存管理功能
 */
public interface ILlmService {

	/**
	 * 获取Agent聊天客户端
	 * @return ChatClient
	 */
	ChatClient getAgentChatClient();

	/**
	 * 获取动态聊天客户端
	 * @param host 主机地址
	 * @param apiKey API密钥
	 * @param modelName 模型名称
	 * @return ChatClient
	 */
	ChatClient getDynamicChatClient(String host, String apiKey, String modelName);

	/**
	 * 获取Agent内存
	 * @param maxMessages 最大消息数
	 * @return ChatMemory
	 */
	ChatMemory getAgentMemory(Integer maxMessages);

	/**
	 * 清除Agent内存
	 * @param planId 计划ID
	 */
	void clearAgentMemory(String planId);

	/**
	 * 获取规划聊天客户端
	 * @return ChatClient
	 */
	ChatClient getPlanningChatClient();

	/**
	 * 清除对话内存
	 * @param planId 计划ID
	 */
	void clearConversationMemory(String planId);

	/**
	 * 获取最终化聊天客户端
	 * @return ChatClient
	 */
	ChatClient getFinalizeChatClient();

	/**
	 * 获取聊天模型
	 * @return ChatModel
	 */
	ChatModel getChatModel();

	/**
	 * 获取对话内存
	 * @param maxMessages 最大消息数
	 * @return ChatMemory
	 */
	ChatMemory getConversationMemory(Integer maxMessages);

}
