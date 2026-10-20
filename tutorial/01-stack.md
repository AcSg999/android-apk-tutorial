# 01 · 先选路线，再安装工具

[返回目录](../README.md) · [上一章](00-roadmap.md) · [下一章：环境](02-environment.md)

## 1.1 技术栈是什么

技术栈就是完成一条功能链时使用的一组技术。例如：Kotlin 写规则，Compose 画页面，DataStore 保存数据，Gradle 构建 APK。联网产品还可能需要服务器与数据库。

**先问三件事：只做 Android 吗？依赖哪些手机硬件？是否需要跨设备共享数据？** 再决定用什么工具。

| 你的目标 | 可选路线 | 额外学习 | 主要难点 |
| --- | --- | --- | --- |
| Android 专用工具、硬件控制 | Kotlin + Compose | Kotlin、Android 生命周期 | 权限、系统行为、机型兼容 |
| Android / iOS 共用大部分界面 | Flutter + Dart | Dart、Flutter 组件 | 插件原生能力、两个平台各自发布 |
| 已有 React / TypeScript 基础 | React Native + Expo | React、原生模块、构建配置 | JS 与原生边界、插件和 SDK 配套 |
| 已有网站、功能偏内容展示 | PWA，或 Capacitor 包装 | HTML/CSS/JS、原生桥接 | 离线、文件与硬件权限、商店审核 |
| 2D / 3D 游戏 | Godot / Unity 等游戏引擎 | 场景、渲染、资源、引擎脚本 | 性能、体积、原生插件、引擎授权 |
| 简单内部表单验证想法 | 低代码工具 | 平台规则与数据配置 | 导出能力、订阅、数据所有权、迁移 |

这张表是工程选择建议。实际采用前，检查目标功能是否有持续维护的官方或成熟插件。技术栈不会替你免除签名、隐私、商店规则。

## 1.2 本教程为什么选择原生 Kotlin

主线只要求 Android，因此可以直接使用平台能力，减少跨平台桥接这一层。Compose 用 Kotlin 描述界面，避免同时学习一套 XML 布局语法。

| 组件 | 负责什么 | 本教程固定版本 |
| --- | --- | --- |
| Kotlin | 业务代码语言 | 2.2.20 |
| Compose 编译插件 | 把 Compose 函数编译为可运行代码 | 2.2.20，与 Kotlin 同版本 |
| Compose BOM | 一张相互配套的 Compose 依赖版本表 | 2025.09.01 |
| Activity Compose | Android 页面入口与 Compose 的衔接 | 1.11.0 |
| Lifecycle | 状态在前后台、旋转时的生命周期管理 | 2.9.3 |
| Preferences DataStore | 小量异步本地存储 | 1.1.7 |
| Android Gradle Plugin（AGP） | Gradle 中负责 Android 构建的插件 | 8.13.2 |
| Gradle | 调度编译、测试、打包等构建任务 | 8.13 |
| JDK | 运行 Java / Gradle / 编译工具 | 使用 17；新装选择维护中的 JDK 17 更新版 |
| Android 平台 | 编译与目标行为版本 | compileSdk 36、targetSdk 36 |
| SDK Build Tools | APK 签名、打包等工具 | 35.0.0 |
| 最低手机系统 | 能安装的最低 Android 版本 | minSdk 26，即 Android 8.0 |

这些数字不是同一套编号。**AGP 8.13.2 不等于 Gradle 8.13，也不等于 Android 8。** Kotlin 插件版本和 Compose BOM 也不能互换。

官方 [AGP 兼容表](https://developer.android.com/build/releases/agp-8-13-0-release-notes) 给出了这条 AGP 分支所需的 Gradle、JDK、SDK 组合。本教程固定它们以减少环境变化，不要求使用最新模板生成的版本。

## 1.3 compileSdk、targetSdk、minSdk 的区别

先分别定义：

- `compileSdk`：编译时可引用到哪一版 Android 平台接口。
- `targetSdk`：声明应用已经适配哪一版平台行为；影响权限、后台限制、界面等。
- `minSdk`：允许安装的最低系统 API 等级。

本项目 `compileSdk=36`、`targetSdk=36`、`minSdk=26`。这不要求用户手机都是 API 36。若调用 API 26 之后才有的接口，仍须做系统版本判断或使用兼容库。

截至核验日，普通手机 / 平板新应用与更新提交 Google Play 需要以 API 36 或更高为目标。TV、手表、车载等规则不同；查看 [官方目标 API 要求](https://developer.android.com/google/play/requirements/target-sdk)。上架前重新核验，不能仅凭本表决定未来版本。

## 1.4 是否必须有服务器

| 功能 | 需要后端吗 | 原因 |
| --- | --- | --- |
| 本机待办、计算器、离线计时 | 通常不需要 | 输入和数据都在手机 |
| 调用公开、不需密钥的天气接口 | 可先直接请求 | 仍需评估接口许可、限流与隐私 |
| 跨设备同步、共享清单 | 通常需要 | 需要一个共同的数据来源 |
| 用户登录、订阅权益 | 需要可信服务 | 客户端可被修改，不能自行认定权限 |
| 付费 AI 接口 | 通常需要 | 保护提供商密钥，控制费用与滥用 |
| 端侧模型推理 | 不一定 | 模型与推理引擎在手机，但要考虑体积与性能 |

后端是远程计算程序；数据库是保存结构化数据的系统；域名是便于人记忆的访问名称；HTTPS 是加密的 HTTP 通信。它们与“APK 文件下载托管”是不同需求。

## 1.5 两条可选跨平台路线

以下是路线导览，不替代各自完整框架教程；本仓库应用代码仍是 Kotlin。

### Flutter

先按 [Flutter 官方 Android 发布文档](https://docs.flutter.dev/deployment/android) 安装 Flutter 与 Android 工具链。常见流程：

```text
flutter doctor
flutter create my_app
cd my_app
flutter run
flutter build apk --release
flutter build appbundle --release
```

`flutter doctor` 检查环境；`run` 开发运行；APK 用于直接安装；App Bundle 用于市场。仍需配置发布密钥，确认产物没有沿用调试签名。iOS 构建通常还需要 macOS 与 Apple 工具链。

### React Native + Expo

按 [Expo 首次构建指南](https://docs.expo.dev/build/setup/) 创建项目，先跑开发版本，再配置 EAS Build。Expo Go 是开发容器，不是你自己的最终 APK。EAS 的默认市场产物与可安装 APK 也要区分。

需要独立测试包时，可在项目 `eas.json` 配置：

```json
{
  "build": {
    "preview": {
      "distribution": "internal",
      "android": { "buildType": "apk" }
    }
  }
}
```

在已配置 EAS CLI 与账号的项目执行 `eas build -p android --profile preview`。详见 [Expo APK 官方说明](https://docs.expo.dev/build-reference/apk/)。云构建可能涉及额度、排队和账号，不把它当成永久免费的前提。

**选择完路线后，先只沿一条路线做到发布，再比较另一条。**
