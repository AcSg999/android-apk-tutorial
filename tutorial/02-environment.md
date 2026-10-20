# 02 · 从没有开发环境开始

[返回目录](../README.md) · [上一章](01-stack.md) · [下一章：第一次运行](03-first-run.md)

## 2.1 准备清单

- Windows 10 / 11 的 64 位电脑，或受当前 Android Studio 支持的 macOS / Linux。
- 建议 16 GB 内存、SSD、预留约 20—30 GB 磁盘空间。空间预算包含缓存和模拟器，项目源码本身小得多。
- 一台 Android 8.0 或更新的手机，以及能传数据的 USB 线；也可使用模拟器。
- 能访问 Android、Google Maven、Maven Central 和 Gradle 分发地址的网络。

这里的硬件数字是实践预算。安装前查看 [Android Studio 官方系统要求](https://developer.android.com/studio/install)，尤其注意当前系统与 CPU 支持情况。

路径建议：`D:\AndroidProjects\PocketTasks`。中文和空格未必有问题，但简短英文路径能减少一些第三方工具的兼容问题。不要把工程放进系统受保护目录、临时目录或自动同步的网盘文件夹。

## 2.2 安装 Android Studio

1. 打开 [官方下载页](https://developer.android.com/studio)。根据系统选择正式版安装包，核对域名。
2. 安装 Android Studio。Windows 上按向导安装；macOS 拖入 Applications；Linux 按官方安装说明解压启动。
3. 首次打开，选择标准安装或按下面清单自定义。SDK 安装时阅读并接受你同意的许可条款。
4. 在欢迎页打开 `More Actions → SDK Manager`；已打开工程时可从 `Tools → SDK Manager` 进入。菜单名称可能随版本微调。
5. 记下 **Android SDK Location**。Windows 常见路径为 `%LOCALAPPDATA%\Android\Sdk`，但以你的实际路径为准。

若官网在当前网络打不开，先排查连接、DNS、系统时间与代理。不要从不明下载站安装修改版 IDE 或 SDK。

## 2.3 安装项目需要的 SDK

SDK Manager 的 `SDK Platforms` 页：安装 **Android 16 / API 36 的平台包**。本项目不需要 API 36.1，也不要求模拟器必须使用同一个 API 版本。

`SDK Tools` 页勾选 `Show Package Details` 后，安装：

| 项目 | 用途 | 是否必须 |
| --- | --- | --- |
| Android SDK Build-Tools 35.0.0 | 本项目固定的打包、签名工具 | 是 |
| Android SDK Platform-Tools | 包含 adb，用于连接设备 | 真机与命令调试需要 |
| Android SDK Command-line Tools | 包含 sdkmanager 等命令 | 推荐，CI / 命令行管理需要 |
| Android Emulator | 在电脑上模拟手机 | 用模拟器才需要 |
| 模拟器系统镜像 | 模拟手机中运行的 Android 系统 | 用模拟器才需要 |
| Google USB Driver | 某些 Windows 设备的 USB 调试驱动 | 按设备需要 |

平台 API 和 Build Tools 的编号不同，因此“API 36 + Build Tools 35.0.0”并不矛盾。其他 Build Tools 可以并存；缺少固定版本时要额外安装。

## 2.4 配置 JDK：最容易混淆的一步

JDK 是电脑上的工具运行环境；手机不需要你另装 JDK。终端默认的 Java 与 Android Studio 使用的 Java，可能不是同一个。

1. 安装维护中的 JDK 17，例如可信发行商提供的 OpenJDK 17，或使用电脑上已有的 JDK 17。
2. Android Studio 打开 `Settings → Build, Execution, Deployment → Build Tools → Gradle`，将 `Gradle JDK` 设为该 JDK 17 的目录。
3. IDE 自身可以使用它附带的 JetBrains Runtime；这里选择的是**构建所用 JDK**，不是要求更换 IDE 自身运行环境。
4. PowerShell 中临时设置，路径改成你实际安装的位置：

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
java -version
```

预期输出含 `17`。这个设置仅影响当前终端及它启动的程序，关掉终端后失效。

若希望永久配置，在 Windows“编辑系统环境变量 → 环境变量”里添加用户变量 `JAVA_HOME`，并在用户 `Path` 加 `%JAVA_HOME%\bin`。重新打开终端后检查。不要覆盖整个已有 Path。

macOS 可使用已安装 JDK：

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"
java -version
```

Linux 根据发行版安装 OpenJDK 17，再把 `JAVA_HOME` 指向真实安装目录。不要照抄其他电脑的 JDK 路径。

## 2.5 自己创建 Android 项目

1. 在欢迎页选择 `New Project → Empty Activity`，确认是使用 Jetpack Compose 的模板，不选 `Empty Views Activity`。
2. Name 填 `PocketTasks`，Package name 填 `com.example.pockettasks`，Save location 选你自己的工作目录，Minimum SDK 选 API 26，构建脚本选 Kotlin DSL。后面开发自己的产品时换成自己的名称和包名。
3. 点击 Finish。Android Studio 创建源码、资源、Gradle 配置和 Wrapper 启动文件。
4. 等待 Gradle Sync：此时才会在**你的电脑**下载模板需要的 Gradle、插件和依赖。若新模板需要额外 SDK，按提示自行安装，或在第 04 章统一切换到本教程参考版本。
5. 首先使用模板支持的构建 JDK 与工具组合验证环境。采用第 04 章固定配置后，再确认构建 JDK 为 17、Gradle 为 8.13；不要把新模板的 AGP 9 配置只改一半。

本教程仓库仅提供文档与参考文本，不能代替这一步。已经有项目时，在 Android Studio 中打开**你自己的工程根目录**，即包含 `settings.gradle.kts` 的目录，不是教程仓库根目录或其中的 app 子目录。

Android Studio 通常会创建本机专用的 `local.properties`。没有自动创建时，在你自己的 Android 项目根目录新建：

```properties
sdk.dir=C:/Users/YourName/AppData/Local/Android/Sdk
```

将 `YourName` 和目录换成实际路径。Windows 在 `.properties` 中用正斜杠可以避免反斜杠转义问题。此文件不要提交给他人，因为每台电脑的路径不同。

可选：当前 PowerShell 为 adb 配置路径：

```powershell
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
$env:PATH = "$env:ANDROID_HOME\platform-tools;$env:PATH"
adb version
```

若你的 SDK 不在默认位置，第一行改成真实目录。不要同时保留相互矛盾的 `ANDROID_HOME`、`ANDROID_SDK_ROOT` 和 `local.properties`。

## 2.6 确认 Gradle Wrapper 正常

Wrapper 是工程自带的“小启动器”：下载并运行项目固定的 Gradle，因此无需再全局安装 Gradle。

在 Android Studio 的 Terminal，确保当前目录是你刚创建的 Android 项目根目录：

```powershell
./gradlew.bat --version
```

预期看到模板所选的 Gradle 版本；按照第 04 章切到参考配置后，应看到 `Gradle 8.13`，启动 JVM 为 Java 17。首次启动会下载 Gradle，之后会复用本机缓存。`--version` 中显示的内置 Kotlin 版本不等于应用构建插件使用的 Kotlin 版本。

macOS / Linux 使用：

```bash
bash ./gradlew --version
```

这三个启动文件由你创建 Android 工程时生成或获取，不由本教程打包提供。若新项目中缺失，先检查是否打开了正确目录、是否完整解压项目，必要时重新创建一个空模板作对照。

实际应用仓库通常应该提交 Wrapper 启动脚本与 JAR，让协作者复现构建；本仓库是文档仓库，所以不携带这些工具文件。项目不会提交 Gradle 分发压缩包、SDK 或 `.gradle` 缓存。生产项目还可根据 [Gradle 官方校验说明](https://docs.gradle.org/current/userguide/gradle_wrapper.html#sec:verification) 配置分发包 SHA-256。

## 2.7 网络与代理

构建可能分别访问 `services.gradle.org`、`dl.google.com`、`repo.maven.apache.org`、`plugins.gradle.org`，下载链路还可能重定向。IDE 能打开网页，不代表 Gradle 已使用相同代理。

若你已经有合法可用的本机 HTTP 代理，可在**用户目录**的 `.gradle/gradle.properties` 配置，端口按真实值修改：

```properties
systemProp.http.proxyHost=127.0.0.1
systemProp.http.proxyPort=7890
systemProp.https.proxyHost=127.0.0.1
systemProp.https.proxyPort=7890
```

没有该代理就不要复制。代理停止运行后也要移除这些设置。带密码的配置不得放入仓库。不要靠关闭 TLS 证书验证、随意改成 HTTP 仓库来修复下载。

## 2.8 这一章验收

- Android Studio 已安装，Gradle JDK 使用 17。
- SDK 中存在 `platforms/android-36` 与 `build-tools/35.0.0`。
- `local.properties` 指向本机 SDK，Gradle Sync 成功。
- `gradlew --version` 显示正确版本。

现在去下一章，让手机真正运行应用。
