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
package com.alibaba.cloud.ai.example.manus.workflow;

import java.util.concurrent.CompletableFuture;

/**
 * 总结工作流接口，提供总结工作流功能
 */
public interface ISummaryWorkflow {

	/**
	 * 执行总结工作流
	 * @param parentPlanId 父计划ID
	 * @param fileName 文件名
	 * @param content 内容
	 * @param queryKey 查询键
	 * @param thinkActRecordId 思考行动记录ID
	 * @param terminateColumnsString 终止列字符串
	 * @return 异步总结结果
	 */
	CompletableFuture<String> executeSummaryWorkflow(String parentPlanId, String fileName, String content,
			String queryKey, Long thinkActRecordId, String terminateColumnsString);

}
