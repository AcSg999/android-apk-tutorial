# “口袋待办”参考源码

这是通用教程的可选教学样例，**不是已经安装好工具与依赖的开箱即用工程包**。没有 Gradle Wrapper 启动文件和 JAR，也没有 SDK、本机路径、密钥或 APK。

## 推荐用法

1. 按 [环境章节](../../tutorial/02-environment.md) 自己安装 Android Studio、JDK 与 SDK。
2. 在 Android Studio 新建 **Empty Activity（Compose）** 工程，Name 为 `PocketTasks`，Package name 为 `com.example.pockettasks`，Minimum SDK 26，使用 Kotlin DSL。
3. 先运行 IDE 生成的原始工程，确认本机环境可用。这会在你电脑上生成 / 获取该项目的 Wrapper 和依赖。
4. 按 [从空项目开发](../../tutorial/04-build-from-zero.md) 对照这里的文本配置，配置为本教程固定版本，再逐项复制或手写源码。
5. 保留 IDE 为你项目生成的 `gradlew`、`gradlew.bat` 和 Wrapper JAR；把 `gradle-wrapper.properties` 中版本与校验值按参考配置调整。
6. Gradle Sync 会在你的电脑上下载依赖，不需要把任何下载结果提交到教程仓库。
7. 在你自己的 Android 项目中运行单元测试、Lint 与构建，然后真机验收。

`scripts/build-release.ps1` 也是可选参考。将它复制到**自己项目**的 `scripts/` 后按签名章节操作。应用包名、版本、主题、隐私说明与发布密钥要由你自行确定。

示例存储小型离线列表，不包含登录、云同步、导出 / 导入或全部应用类型的实现。不要只修改名称就声称已具备这些功能。
