# 官方资料与核验入口

[返回目录](../README.md)

核验日期：2026-10-20。以下优先使用平台、项目维护方和主管机关资料。版本与政策不要只依据论坛旧文章；表格中的部分链接是扩展学习入口，不表示本教程已经实现其全部功能。

## 环境、构建与语言

| 资料 | 用途 |
| --- | --- |
| [Android Studio 下载](https://developer.android.com/studio) | 获取 IDE、确认系统支持 |
| [Android Studio 安装](https://developer.android.com/studio/install) | 系统要求与安装步骤 |
| [AGP 8.13 兼容表](https://developer.android.com/build/releases/agp-8-13-0-release-notes) | JDK、Gradle、Build Tools 及 API 支持 |
| [Android 构建与 JDK](https://developer.android.com/build/jdks) | IDE 与构建 JDK 的区别 |
| [Gradle Wrapper 与校验](https://docs.gradle.org/current/userguide/gradle_wrapper.html) | 生成、提交、下载与校验 |
| [Kotlin 基本语法](https://kotlinlang.org/docs/basic-syntax.html) | 语言入门 |
| [Kotlin 2.2.20](https://kotlinlang.org/docs/whatsnew2220.html) | 示例使用版本的发布说明 |
| [Compose 编译插件](https://developer.android.com/develop/ui/compose/compiler) | Kotlin 与 Compose 配置 |
| [Compose BOM](https://developer.android.com/develop/ui/compose/bom) | Compose 库的版本约束 |
| [Activity 版本](https://developer.android.com/jetpack/androidx/releases/activity) | Activity 依赖要求 |
| [Lifecycle 版本](https://developer.android.com/jetpack/androidx/releases/lifecycle) | 生命周期依赖要求 |
| [DataStore 版本](https://developer.android.com/jetpack/androidx/releases/datastore) | 存储依赖版本 |

## 实机、状态与数据

| 资料 | 用途 |
| --- | --- |
| [真机运行](https://developer.android.com/studio/run/device) | USB 调试与设备连接 |
| [adb](https://developer.android.com/tools/adb) | 安装、日志与端口转发 |
| [模拟器网络](https://developer.android.com/studio/run/emulator-networking) | 10.0.2.2 与网络地址 |
| [模拟器加速](https://developer.android.com/studio/run/emulator-acceleration) | 虚拟化与性能 |
| [Android 架构](https://developer.android.com/topic/architecture) | 状态与分层原则 |
| [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) | 小型持久化与单实例要求 |
| [Room](https://developer.android.com/training/data-storage/room) | 结构化本地数据库 |
| [保存 UI 状态](https://developer.android.com/develop/ui/compose/state-saving) | 生命周期与恢复边界 |
| [网络安全配置](https://developer.android.com/privacy-and-security/security-config) | HTTP、HTTPS 与 Debug 配置 |
| [文档与文件访问](https://developer.android.com/training/data-storage/shared/documents-files) | 用户选择导出 / 导入路径 |

## 签名、分发与市场政策

| 资料 | 用途 |
| --- | --- |
| [Android 应用签名](https://developer.android.com/studio/publish/app-signing) | 私钥、证书、上传密钥与 Play App Signing |
| [apksigner](https://developer.android.com/tools/apksigner) | 核验 APK 签名 |
| [App Bundle](https://developer.android.com/guide/app-bundle) | AAB 发布格式 |
| [16 KB 页大小](https://developer.android.com/guide/practices/page-sizes) | 原生库与系统兼容 |
| [GitHub Releases 链接](https://docs.github.com/en/repositories/releasing-projects-on-github/linking-to-releases) | 固定版本 / 最新附件下载 |
| [Actions Artifacts](https://docs.github.com/en/actions/concepts/workflows-and-actions/workflow-artifacts) | 临时构建产物与保留规则 |
| [Play 注册](https://support.google.com/googleplay/android-developer/answer/6112435) | 账号与注册费用 |
| [Play 素材规范](https://support.google.com/googleplay/android-developer/answer/9866151) | 图标、截图等 |
| [Play 目标 API](https://developer.android.com/google/play/requirements/target-sdk) | 每年变化的兼容门槛 |
| [新个人账号测试](https://support.google.com/googleplay/android-developer/answer/14151465) | 连续封闭测试条件 |
| [用户数据政策](https://support.google.com/googleplay/android-developer/answer/10144311) | 隐私、数据处理与账号 |
| [账号删除](https://support.google.com/googleplay/android-developer/answer/13327111) | 删除请求路径 |
| [Android 开发者验证](https://developer.android.com/developer-verification) | 分地区 / 渠道实施时间与账号入口 |
| [开发者验证 FAQ](https://support.google.com/android-developer-console/answer/16561738) | 分发范围、登记与个人测试 |
| [工信部 APP 备案通知](https://www.miit.gov.cn/jgsj/xgj/wjfb/art/2023/art_dd783a581c9644a4aee10afa582811db.html) | 国内备案适用工作要求 |
| [备案登记说明](https://xzca.miit.gov.cn/bsfw/bszn/art/2024/art_5d3cc7f452c04fdd97b6881a20b656f9.html) | 包名、公钥等资料字段 |
| [OPPO 发布说明](https://open.oppomobile.com/new/developmentDoc/info?id=10035) | 一家国内市场的具体提交流程 |

## 可选路线与能力

- [Flutter Android 发布](https://docs.flutter.dev/deployment/android)
- [Expo 首次构建](https://docs.expo.dev/build/setup/) 与 [APK 构建](https://docs.expo.dev/build-reference/apk/)
- [CameraX](https://developer.android.com/media/camera/camerax)、[Photo Picker](https://developer.android.com/training/data-storage/shared/photopicker)、[ML Kit](https://developers.google.com/ml-kit)
- [定位权限](https://developer.android.com/develop/sensors-and-location/location/permissions)、[BLE](https://developer.android.com/develop/connectivity/bluetooth/ble/ble-overview)
- [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)、[Media3](https://developer.android.com/media/media3)
- [Play Billing](https://developer.android.com/google/play/billing)
- [FastAPI](https://fastapi.tiangolo.com/)、[Firebase Authentication](https://firebase.google.com/docs/auth)、[Supabase Auth](https://supabase.com/docs/guides/auth)
- [LiteRT](https://ai.google.dev/edge/litert)、[ONNX Runtime Mobile](https://onnxruntime.ai/docs/tutorials/mobile/)
- [Godot Android 导出](https://docs.godotengine.org/en/stable/tutorials/export/exporting_for_android.html)、[Unity Android 构建](https://docs.unity3d.com/Manual/android-BuildProcess.html)

遇到域名迁移、页面失效或规则冲突，回到对应维护方官方站点搜索标题，优先检查发布日期、更新日期和适用账号 / 地区，而不是机械套用旧步骤。
