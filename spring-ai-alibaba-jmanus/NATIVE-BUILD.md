# JManus Native Image 构建指南

本文档说明如何将 JManus 应用程序构建为 GraalVM Native Image 可执行文件。

## 前置条件

1. **GraalVM JDK 21** 已安装在 `/Library/Java/JavaVirtualMachines/graalvm-jdk-21.0.3+7.1/Contents/Home`
2. **Maven 3.6+** 
3. **native-image** 工具（如果没有安装，构建脚本会自动安装）

## 验证 GraalVM 安装

```bash
export JAVA_HOME="/Library/Java/JavaVirtualMachines/graalvm-jdk-21.0.3+7.1/Contents/Home"
java -version
```

应该看到类似这样的输出：
```
openjdk version "21.0.3" 2024-04-16
OpenJDK Runtime Environment GraalVM CE 21.0.3+7.1 (build 21.0.3+7-jvmci-23.1-b37)
OpenJDK 64-Bit Server VM GraalVM CE 21.0.3+7.1 (build 21.0.3+7-jvmci-23.1-b37, mixed mode, sharing)
```

## 快速构建

使用提供的构建脚本：

```bash
./build-native.sh
```

## 手动构建

### 1. 设置环境变量

```bash
export JAVA_HOME="/Library/Java/JavaVirtualMachines/graalvm-jdk-21.0.3+7.1/Contents/Home"
export GRAALVM_HOME="/Library/Java/JavaVirtualMachines/graalvm-jdk-21.0.3+7.1/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
```

### 2. 安装 native-image 工具（如需要）

```bash
gu install native-image
```

### 3. 清理并编译项目

```bash
mvn clean compile
```

### 4. 生成 AOT 处理后的代码并构建 Native Image

```bash
mvn -Pnative spring-boot:process-aot native:compile
```

## 运行 Native Image

构建完成后，可执行文件将在 `target/` 目录下：

```bash
./target/spring-ai-alibaba-jmanus
```

## 配置说明

### Native Image 构建参数

在 `pom.xml` 的 native profile 中配置了以下重要参数：

- `--no-fallback`: 禁用 fallback 到 JVM
- `--enable-url-protocols=http,https`: 启用 HTTP/HTTPS 协议
- `--enable-monitoring=heapdump`: 启用堆转储监控
- `--allow-incomplete-classpath`: 允许不完整的类路径
- `-H:+AddAllCharsets`: 包含所有字符集
- 各种资源包含模式，确保应用资源被正确包含

### 反射配置

项目包含以下 Native Image 配置文件：

- `reflect-config.json`: 反射配置
- `resource-config.json`: 资源配置  
- `proxy-config.json`: 动态代理配置
- `serialization-config.json`: 序列化配置
- `jni-config.json`: JNI 配置

## 故障排除

### 1. 类找不到异常

如果遇到 `ClassNotFoundException`，需要在 `reflect-config.json` 中添加相应的类配置。

### 2. 资源文件找不到

确保在 `resource-config.json` 中包含了所需的资源文件模式。

### 3. 运行时错误

使用以下参数进行调试构建：

```bash
mvn -Pnative -Dspring-boot.build-image.verboseLogging=true native:compile
```

### 4. 内存不足

可以增加构建时的内存：

```bash
export MAVEN_OPTS="-Xmx8g"
mvn -Pnative native:compile
```

## 性能优化

Native Image 通常具有以下优势：

- **启动时间**: 显著faster启动 (通常 < 100ms)
- **内存占用**: 更低的内存使用量
- **无需 JVM**: 独立可执行文件

## 已知限制

1. **Playwright**: 某些浏览器自动化功能可能在 Native Image 中受限
2. **动态类加载**: 避免使用反射和动态类加载
3. **第三方库**: 某些第三方库可能需要额外的 Native Image 配置

## 更多信息

- [GraalVM Native Image 文档](https://www.graalvm.org/latest/reference-manual/native-image/)
- [Spring Boot Native Image 支持](https://docs.spring.io/spring-boot/docs/current/reference/html/native-image.html)
- [Spring Native 文档](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/)
