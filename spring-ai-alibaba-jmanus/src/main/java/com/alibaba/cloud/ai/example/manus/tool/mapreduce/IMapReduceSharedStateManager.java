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
package com.alibaba.cloud.ai.example.manus.tool.mapreduce;

import java.util.List;
import java.util.Map;

/**
 * MapReduce tool shared state manager interface for managing shared state information
 * between different Agent instances
 */
public interface IMapReduceSharedStateManager {

	/**
	 * Get or create plan state
	 * @param planId Plan ID
	 * @return Plan state
	 */
	MapReduceSharedStateManager.PlanState getOrCreatePlanState(String planId);

	/**
	 * Get plan state
	 * @param planId Plan ID
	 * @return Plan state
	 */
	MapReduceSharedStateManager.PlanState getPlanState(String planId);

	/**
	 * Clean up plan state
	 * @param planId Plan ID
	 */
	void cleanupPlanState(String planId);

	/**
	 * Get next task ID
	 * @param planId Plan ID
	 * @return Next task ID
	 */
	String getNextTaskId(String planId);

	/**
	 * Add split result
	 * @param planId Plan ID
	 * @param taskDirectory Task directory
	 */
	void addSplitResult(String planId, String taskDirectory);

	/**
	 * Get split results
	 * @param planId Plan ID
	 * @return Split results list
	 */
	List<String> getSplitResults(String planId);

	/**
	 * Set split results
	 * @param planId Plan ID
	 * @param splitResults Split results list
	 */
	void setSplitResults(String planId, List<String> splitResults);

	/**
	 * Record Map task status
	 * @param planId Plan ID
	 * @param taskId Task ID
	 * @param taskStatus Task status
	 */
	void recordMapTaskStatus(String planId, String taskId, MapReduceSharedStateManager.TaskStatus taskStatus);

	/**
	 * Get Map task status
	 * @param planId Plan ID
	 * @param taskId Task ID
	 * @return Task status
	 */
	MapReduceSharedStateManager.TaskStatus getMapTaskStatus(String planId, String taskId);

	/**
	 * Get all Map task statuses
	 * @param planId Plan ID
	 * @return All task statuses
	 */
	Map<String, MapReduceSharedStateManager.TaskStatus> getAllMapTaskStatuses(String planId);

	/**
	 * 设置最后操作结果
	 * @param planId 计划ID
	 * @param result 操作结果
	 */
	void setLastOperationResult(String planId, String result);

	/**
	 * 获取最后操作结果
	 * @param planId 计划ID
	 * @return 最后操作结果
	 */
	String getLastOperationResult(String planId);

	/**
	 * 设置最后处理的文件
	 * @param planId 计划ID
	 * @param filePath 文件路径
	 */
	void setLastProcessedFile(String planId, String filePath);

	/**
	 * 获取最后处理的文件
	 * @param planId 计划ID
	 * @return 最后处理的文件路径
	 */
	String getLastProcessedFile(String planId);

	/**
	 * 获取当前工具状态字符串
	 * @param planId 计划ID
	 * @return 当前工具状态字符串
	 */
	String getCurrentToolStateString(String planId);

	/**
	 * 获取所有计划概览
	 * @return 所有计划概览字符串
	 */
	String getAllPlansOverview();

	/**
	 * 清理所有计划状态
	 */
	void cleanupAllPlanStates();

}
