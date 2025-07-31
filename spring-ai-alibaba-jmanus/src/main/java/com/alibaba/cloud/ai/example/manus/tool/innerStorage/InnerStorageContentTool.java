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
package com.alibaba.cloud.ai.example.manus.tool.innerStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.alibaba.cloud.ai.example.manus.recorder.PlanExecutionRecorder;
import com.alibaba.cloud.ai.example.manus.tool.AbstractBaseTool;
import com.alibaba.cloud.ai.example.manus.tool.code.ToolExecuteResult;
import com.alibaba.cloud.ai.example.manus.tool.filesystem.UnifiedDirectoryManager;
import com.alibaba.cloud.ai.example.manus.workflow.SummaryWorkflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.api.OpenAiApi;

/**
 * Internal storage content retrieval tool specialized for intelligent content extraction
 * and structured output, supporting AI intelligent analysis and data extraction functions
 */
public class InnerStorageContentTool extends AbstractBaseTool<InnerStorageContentTool.InnerStorageContentInput> {

	private static final Logger log = LoggerFactory.getLogger(InnerStorageContentTool.class);

	/**
	 * Internal storage content retrieval input class
	 */
	public static class InnerStorageContentInput {

		private String action;

		@com.fasterxml.jackson.annotation.JsonProperty("file_name")
		private String fileName;

		@com.fasterxml.jackson.annotation.JsonProperty("folder_name")
		private String folderName;

		@com.fasterxml.jackson.annotation.JsonProperty("query_key")
		private String queryKey;

		private List<String> columns;

		@com.fasterxml.jackson.annotation.JsonProperty("start_line")
		private Integer startLine;

		@com.fasterxml.jackson.annotation.JsonProperty("end_line")
		private Integer endLine;

		public InnerStorageContentInput() {
		}

		public String getAction() {
			return action;
		}

		public void setAction(String action) {
			this.action = action;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getFolderName() {
			return folderName;
		}

		public void setFolderName(String folderName) {
			this.folderName = folderName;
		}

		public String getQueryKey() {
			return queryKey;
		}

		public void setQueryKey(String queryKey) {
			this.queryKey = queryKey;
		}

		public List<String> getColumns() {
			return columns;
		}

		public void setColumns(List<String> columns) {
			this.columns = columns;
		}

		public Integer getStartLine() {
			return startLine;
		}

		public void setStartLine(Integer startLine) {
			this.startLine = startLine;
		}

		public Integer getEndLine() {
			return endLine;
		}

		public void setEndLine(Integer endLine) {
			this.endLine = endLine;
		}

	}

	private final UnifiedDirectoryManager directoryManager;

	private final SummaryWorkflow summaryWorkflow;

	private final PlanExecutionRecorder planExecutionRecorder;

	public InnerStorageContentTool(UnifiedDirectoryManager directoryManager, SummaryWorkflow summaryWorkflow,
			PlanExecutionRecorder planExecutionRecorder) {
		this.directoryManager = directoryManager;
		this.summaryWorkflow = summaryWorkflow;
		this.planExecutionRecorder = planExecutionRecorder;
	}

	private static final String TOOL_NAME = "inner_storage_content_tool";

	private static final String TOOL_DESCRIPTION = """
			Internal storage content retrieval tool specialized for intelligent content extraction and structured output.
			Intelligent content extraction mode: Get detailed content based on file name, **must provide** query_key and columns parameters for intelligent extraction and structured output

			Supports two operation modes:
			1. get_content: Get content from single file (exact filename match or relative path)
			2. get_folder_content: Get content from all files in specified folder
			""";

	private static final String PARAMETERS = """
			{
				"oneOf": [
					{
						"type": "object",
						"properties": {
							"action": {
								"type": "string",
								"const": "get_content",
								"description": "Get content from single file"
							},
							"file_name": {
								"type": "string",
								"description": "Filename (with extension) or relative path, supports exact matching"
							},
							"query_key": {
								"type": "string",
								"description": "Related questions or content keywords to extract, must be provided"
							},
							"columns": {
								"type": "array",
								"items": {
									"type": "string"
								},
								"description": "Column names for return results, used for structured output, must be provided. The returned result can be a list"
							}
						},
						"required": ["action", "file_name", "query_key", "columns"],
						"additionalProperties": false
					},
					{
						"type": "object",
						"properties": {
							"action": {
								"type": "string",
								"const": "get_folder_content",
								"description": "Get content from all files in specified folder"
							},
							"folder_name": {
								"type": "string",
								"description": "Folder name or relative path"
							},
							"query_key": {
								"type": "string",
								"description": "Related questions or content keywords to extract, must be provided"
							},
							"columns": {
								"type": "array",
								"items": {
									"type": "string"
								},
								"description": "Column names for return results, used for structured output, must be provided. The returned result can be a list"
							}
						},
						"required": ["action", "folder_name", "query_key", "columns"],
						"additionalProperties": false
					}
				]
			}
			""";

	@Override
	public String getName() {
		return TOOL_NAME;
	}

	@Override
	public String getDescription() {
		return TOOL_DESCRIPTION;
	}

	@Override
	public String getParameters() {
		return PARAMETERS;
	}

	@Override
	public Class<InnerStorageContentInput> getInputType() {
		return InnerStorageContentInput.class;
	}

	@Override
	public String getServiceGroup() {
		return "default-service-group";
	}

	public static OpenAiApi.FunctionTool getToolDefinition() {
		OpenAiApi.FunctionTool.Function function = new OpenAiApi.FunctionTool.Function(TOOL_DESCRIPTION, TOOL_NAME,
				PARAMETERS);
		return new OpenAiApi.FunctionTool(function);
	}

	/**
	 * Execute internal storage content retrieval operation
	 */
	@Override
	public ToolExecuteResult run(InnerStorageContentInput input) {
		log.info("InnerStorageContentTool input: action={}, fileName={}, folderName={}, queryKey={}, columns={}",
				input.getAction(), input.getFileName(), input.getFolderName(), input.getQueryKey(), input.getColumns());
		try {
			String action = input.getAction();
			if (action == null) {
				return new ToolExecuteResult("Error: action parameter is required");
			}

			return switch (action) {
				case "get_content" -> getStoredContent(input.getFileName(), input.getQueryKey(), input.getColumns());
				case "get_folder_content" ->
					getFolderContent(input.getFolderName(), input.getQueryKey(), input.getColumns());
				default -> new ToolExecuteResult("Error: Unsupported operation type '" + action
						+ "'. Supported operations: get_content, get_folder_content");
			};
		}
		catch (Exception e) {
			log.error("InnerStorageContentTool execution failed", e);
			return new ToolExecuteResult("Tool execution failed: " + e.getMessage());
		}
	}

	/**
	 * Get stored content by filename, supports AI intelligent extraction and structured output
	 */
	private ToolExecuteResult getStoredContent(String fileName, String queryKey, List<String> columns) {
		if (fileName == null || fileName.trim().isEmpty()) {
			return new ToolExecuteResult("Error: file_name parameter is required");
		}
		if (queryKey == null || queryKey.trim().isEmpty()) {
			return new ToolExecuteResult("Error: query_key parameter is required to specify content keywords to extract");
		}
		if (columns == null || columns.isEmpty()) {
			return new ToolExecuteResult("Error: columns parameter is required to specify structured column names for return results");
		}
		try {
			Path planDir = directoryManager.getRootPlanDirectory(rootPlanId);
			Path targetFile = null;

			// First try exact relative path matching
			if (fileName.contains("/")) {
				Path exactPath = planDir.resolve(fileName);
				if (Files.exists(exactPath) && Files.isRegularFile(exactPath)) {
					targetFile = exactPath;
				}
			}
			else {
				// If no path separator, exact match filename in root directory
				List<Path> files = Files.list(planDir).filter(Files::isRegularFile).toList();
				for (Path filePath : files) {
					if (filePath.getFileName().toString().equals(fileName)) {
						targetFile = filePath;
						break;
					}
				}
			}

			if (targetFile == null) {
				return new ToolExecuteResult("未找到文件 '" + fileName + "'。请提供精确的文件名或相对路径。");
			}

			String fileContent = Files.readString(targetFile);
			String actualFileName = planDir.relativize(targetFile).toString();

			log.info("委托给 SummaryWorkflow 处理文件内容提取：文件={}, 查询关键词={}", actualFileName, queryKey);
			Long thinkActRecordId = getCurrentThinkActRecordId();
			String terminateColumnsString = String.join(",", columns);
			String result = summaryWorkflow
				.executeSummaryWorkflow(rootPlanId, actualFileName, fileContent, queryKey, thinkActRecordId,
						terminateColumnsString)
				.get();
			return new ToolExecuteResult(result);
		}
		catch (IOException e) {
			log.error("获取存储内容失败", e);
			return new ToolExecuteResult("获取内容失败: " + e.getMessage());
		}
		catch (Exception e) {
			log.error("SummaryWorkflow 执行失败", e);
			return new ToolExecuteResult("内容处理失败: " + e.getMessage());
		}
	}

	/**
	 * 从指定文件夹下的所有文件中获取信息
	 */
	private ToolExecuteResult getFolderContent(String folderName, String queryKey, List<String> columns) {
		if (folderName == null || folderName.trim().isEmpty()) {
			return new ToolExecuteResult("错误：folder_name参数是必需的");
		}
		if (queryKey == null || queryKey.trim().isEmpty()) {
			return new ToolExecuteResult("错误：query_key参数是必需的，用于指定要提取的内容关键词");
		}
		if (columns == null || columns.isEmpty()) {
			return new ToolExecuteResult("错误：columns参数是必需的，用于指定返回结果的结构化列名");
		}
		try {
			Path planDir = directoryManager.getRootPlanDirectory(rootPlanId);
			Path targetFolder = planDir.resolve(folderName);

			if (!Files.exists(targetFolder)) {
				return new ToolExecuteResult("文件夹 '" + folderName + "' 不存在。");
			}

			if (!Files.isDirectory(targetFolder)) {
				return new ToolExecuteResult("'" + folderName + "' 不是一个文件夹。");
			}

			// 获取文件夹下的所有文件
			List<Path> files = Files.list(targetFolder).filter(Files::isRegularFile).toList();

			if (files.isEmpty()) {
				return new ToolExecuteResult("文件夹 '" + folderName + "' 中没有文件。");
			}

			// 合并所有文件内容
			StringBuilder combinedContent = new StringBuilder();
			for (Path file : files) {
				String relativePath = planDir.relativize(file).toString();
				combinedContent.append("=== 文件: ").append(relativePath).append(" ===\n");
				combinedContent.append(Files.readString(file));
				combinedContent.append("\n\n");
			}

			log.info("委托给 SummaryWorkflow 处理文件夹内容提取：文件夹={}, 文件数量={}, 查询关键词={}", folderName, files.size(), queryKey);

			Long thinkActRecordId = getCurrentThinkActRecordId();
			String terminateColumnsString = String.join(",", columns);
			String result = summaryWorkflow
				.executeSummaryWorkflow(rootPlanId, folderName, combinedContent.toString(), queryKey, thinkActRecordId,
						terminateColumnsString)
				.get();
			return new ToolExecuteResult(result);

		}
		catch (IOException e) {
			log.error("获取文件夹内容失败", e);
			return new ToolExecuteResult("获取文件夹内容失败: " + e.getMessage());
		}
		catch (Exception e) {
			log.error("SummaryWorkflow 执行失败", e);
			return new ToolExecuteResult("内容处理失败: " + e.getMessage());
		}
	}

	/**
	 * 获取当前的 think-act 记录ID
	 * @return 当前 think-act 记录ID，如果没有则返回 null
	 */
	private Long getCurrentThinkActRecordId() {
		try {
			Long thinkActRecordId = planExecutionRecorder.getCurrentThinkActRecordId(currentPlanId, rootPlanId);
			if (thinkActRecordId != null) {
				log.info("当前 think-act 记录ID: {}", thinkActRecordId);
				return thinkActRecordId;
			}
			else {
				log.warn("当前没有 think-act 记录ID");
			}
		}
		catch (Exception e) {
			log.warn("Failed to get current think-act record ID: {}", e.getMessage());
		}

		return null;
	}

	@Override
	public String getCurrentToolStateString() {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("InnerStorageContent 当前状态:\n");
			sb.append("- 存储根目录: ").append(directoryManager.getRootPlanDirectory(rootPlanId)).append("\n");
			Path planDir = directoryManager.getRootPlanDirectory(rootPlanId);
			List<Path> files = Files.exists(planDir) ? Files.list(planDir).filter(Files::isRegularFile).toList()
					: List.of();
			if (files.isEmpty()) {
				sb.append("- 内部文件: 无\n");
			}
			else {
				sb.append("- 内部文件 (").append(files.size()).append("个)\n");
			}
			return sb.toString();
		}
		catch (Exception e) {
			log.error("获取工具状态失败", e);
			return "InnerStorageContent 状态获取失败: " + e.getMessage();
		}
	}

	@Override
	public void cleanup(String planId) {
		// 内容获取工具不需要执行清理操作
		log.info("InnerStorageContentTool cleanup for plan: {}", planId);
	}

}
