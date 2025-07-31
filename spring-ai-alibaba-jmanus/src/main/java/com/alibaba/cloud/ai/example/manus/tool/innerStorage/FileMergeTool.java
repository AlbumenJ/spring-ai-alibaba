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
import java.nio.file.StandardCopyOption;
import java.util.List;

import com.alibaba.cloud.ai.example.manus.tool.AbstractBaseTool;
import com.alibaba.cloud.ai.example.manus.tool.code.ToolExecuteResult;
import com.alibaba.cloud.ai.example.manus.tool.filesystem.UnifiedDirectoryManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.api.OpenAiApi;

/**
 * File merge tool for merging single files into specified target folders, merging one
 * file per call
 */
public class FileMergeTool extends AbstractBaseTool<FileMergeTool.FileMergeInput> {

	private static final Logger log = LoggerFactory.getLogger(FileMergeTool.class);

	/**
	 * File merge input class
	 */
	public static class FileMergeInput {

		private String action;

		@com.fasterxml.jackson.annotation.JsonProperty("file_name")
		private String fileName;

		@com.fasterxml.jackson.annotation.JsonProperty("target_folder")
		private String targetFolder;

		public FileMergeInput() {
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

		public String getTargetFolder() {
			return targetFolder;
		}

		public void setTargetFolder(String targetFolder) {
			this.targetFolder = targetFolder;
		}

	}

	private final UnifiedDirectoryManager directoryManager;

	public FileMergeTool(UnifiedDirectoryManager directoryManager) {
		this.directoryManager = directoryManager;
	}

	private static final String TOOL_NAME = "file_merge_tool";

	private static final String TOOL_DESCRIPTION = """
			File merge tool for merging single files into specified target folders.
			Each call merges one file to the target folder, supports fuzzy filename matching.
			""";

	private static final String PARAMETERS = """
			{
				"type": "object",
				"properties": {
					"action": {
						"type": "string",
						"enum": ["merge_file"],
						"description": "Operation type, currently supports merge_file"
					},
					"file_name": {
						"type": "string",
						"description": "Filename to merge (supports fuzzy matching)"
					},
					"target_folder": {
						"type": "string",
						"description": "Target folder path where the file will be copied"
					}
				},
				"required": ["action", "file_name", "target_folder"],
				"additionalProperties": false
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
	public Class<FileMergeInput> getInputType() {
		return FileMergeInput.class;
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
	 * Execute file merge operation
	 */
	@Override
	public ToolExecuteResult run(FileMergeInput input) {
		log.info("FileMergeTool input: action={}, fileName={}, targetFolder={}", input.getAction(), input.getFileName(),
				input.getTargetFolder());
		try {
			return mergeFile(input.getFileName(), input.getTargetFolder());
		}
		catch (Exception e) {
			log.error("FileMergeTool execution failed", e);
			return new ToolExecuteResult("Tool execution failed: " + e.getMessage());
		}
	}

	/**
	 * Merge single file to specified folder
	 */
	private ToolExecuteResult mergeFile(String fileName, String targetFolder) {
		if (fileName == null || fileName.trim().isEmpty()) {
			return new ToolExecuteResult("Error: file_name parameter is required");
		}
		if (targetFolder == null || targetFolder.trim().isEmpty()) {
			return new ToolExecuteResult("Error: target_folder parameter is required");
		}

		try {
			Path planDir = directoryManager.getRootPlanDirectory(rootPlanId);
			Path targetDir = planDir.resolve(targetFolder);

			// Ensure target folder exists
			Files.createDirectories(targetDir);

			// Find matching files
			String actualFileName = null;
			Path sourceFile = null;
			List<Path> files = Files.list(planDir).filter(Files::isRegularFile).toList();

			for (Path filePath : files) {
				if (filePath.getFileName().toString().contains(fileName)) {
					sourceFile = filePath;
					actualFileName = filePath.getFileName().toString();
					break;
				}
			}

			if (sourceFile == null) {
				return new ToolExecuteResult("未找到文件名为 '" + fileName + "' 的文件。请使用文件名的一部分来查找文件。");
			}

			// 复制文件到目标文件夹
			Path targetFile = targetDir.resolve(actualFileName);
			Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);

			log.info("文件合并完成：{} -> {}", actualFileName, targetFolder);

			StringBuilder result = new StringBuilder();
			result.append("文件合并成功\n");
			result.append("源文件：").append(actualFileName).append("\n");
			result.append("目标文件夹：").append(targetFolder).append("\n");
			result.append("目标文件路径：").append(targetFile.toString()).append("\n");

			return new ToolExecuteResult(result.toString());

		}
		catch (IOException e) {
			log.error("文件合并失败", e);
			return new ToolExecuteResult("文件合并失败: " + e.getMessage());
		}
		catch (Exception e) {
			log.error("文件合并操作失败", e);
			return new ToolExecuteResult("文件合并操作失败: " + e.getMessage());
		}
	}

	@Override
	public String getCurrentToolStateString() {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("FileMerge 当前状态:\n");
			sb.append("- 存储根目录: ").append(directoryManager.getRootPlanDirectory(rootPlanId)).append("\n");
			Path planDir = directoryManager.getRootPlanDirectory(rootPlanId);
			List<Path> files = Files.exists(planDir) ? Files.list(planDir).filter(Files::isRegularFile).toList()
					: List.of();
			if (files.isEmpty()) {
				sb.append("- 可用文件: 无\n");
			}
			else {
				sb.append("- 可用文件 (").append(files.size()).append("个): ");
				for (int i = 0; i < Math.min(files.size(), 5); i++) {
					sb.append(files.get(i).getFileName().toString());
					if (i < Math.min(files.size(), 5) - 1) {
						sb.append(", ");
					}
				}
				if (files.size() > 5) {
					sb.append("...");
				}
				sb.append("\n");
			}
			return sb.toString();
		}
		catch (Exception e) {
			log.error("获取工具状态失败", e);
			return "FileMerge 状态获取失败: " + e.getMessage();
		}
	}

	@Override
	public void cleanup(String planId) {
		// 文件合并工具不需要执行清理操作
		log.info("FileMergeTool cleanup for plan: {}", planId);
	}

}
