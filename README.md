# 从零到发布：Android APK 开发通用教程

面向新手的中文端到端教程：从需求与技术栈选择开始，在**你自己的电脑上**安装工具、创建项目、添加依赖、编写功能、真机测试、签名打包，最后提供 APK 下载或提交应用市场。

本仓库是准备上传 GitHub 的**教程与参考源码**，不是已经配好环境的应用工程。不附带 SDK、JDK、Gradle 分发包、Wrapper JAR、预装依赖、签名密钥或编译好的 APK。读者按步骤自行安装和构建；版本号与代码仅供复现参考。

## 你会学到什么

- 如何判断应用是否需要服务器，以及如何选择原生、跨平台、Web 包装或游戏引擎。
- 如何独立安装环境、创建工程、配置依赖、理解 UI 与数据流。
- 如何从一个功能做起，处理生命周期、权限、网络、存储和失败状态。
- 如何生成正式签名 APK、验证更新、制作真正可下载的发布链接。
- 上架 Google Play 或国内应用市场还需要哪些测试、资料和流程。
- 相机、地图、AI、聊天、支付、蓝牙等应用各自增加哪些难点。

## 阅读路线

第一次做应用：按 00—08 完成离线应用与下载发布，再按需求读 09—12。已有项目的读者可从环境、签名、分发或排障章节进入。

| 章节 | 内容 |
| --- | --- |
| [00 目标与路线](tutorial/00-roadmap.md) | 第一性原理、术语、需求、验收 |
| [01 技术栈](tutorial/01-stack.md) | Kotlin、Flutter、Expo 等路线比较 |
| [02 安装环境](tutorial/02-environment.md) | 自己安装 IDE、JDK、SDK，理解依赖下载 |
| [03 第一次运行](tutorial/03-first-run.md) | 真机、模拟器、构建、调试、测试 |
| [04 从空项目开发](tutorial/04-build-from-zero.md) | 创建工程、配置依赖、实现完整示例 |
| [05 代码与原理](tutorial/05-understand-code.md) | Kotlin、状态、协程、存储、生命周期 |
| [06 产品质量](tutorial/06-product-quality.md) | 图标、权限、测试、隐私、数据保护 |
| [07 签名与更新](tutorial/07-signing-and-updates.md) | APK / AAB、密钥、正式包、升级 |
| [08 提供下载](tutorial/08-distribution.md) | 局域网、Releases、下载页、CI |
| [09 联网与后端](tutorial/09-network-and-backend.md) | 实验、登录、数据库、同步、部署 |
| [10 应用类型](tutorial/10-app-types.md) | 不同产品的最小版本与技术难点 |
| [11 Google Play](tutorial/11-google-play.md) | 账号、测试、素材、审核、发布 |
| [12 国内发布与运营](tutorial/12-china-and-operations.md) | 国内市场、备案、预算、维护 |
| [13 排障与清单](tutorial/13-troubleshooting.md) | 环境、编译、安装、运行与发布故障 |
| [14 验证范围](tutorial/14-validation.md) | 教程检查与尚需读者实测的项目 |
| [官方资料](tutorial/sources.md) | 版本与政策核验入口 |

## 如何使用参考代码

教程以“口袋待办”贯穿讲解，但通用流程适用于其他应用。示例支持添加、完成、筛选、确认删除和本机保存。

**先按第 02 章安装工具，并在 Android Studio 创建自己的 Empty Activity 项目。** 第 04 章再说明如何逐项配置依赖、编写代码，或对照 [参考源码](examples/pocket-tasks/README.md)。不应在本教程根目录直接执行 Gradle 构建。

```text
tutorial/                     通用流程与分章教材
examples/pocket-tasks/         可选 Kotlin 参考源码与构建配置
extras/network-lab/            可选本地联网实验，无预装依赖
templates/                    需求、测试、发布、隐私、下载页、CI 模板
scripts/check-tutorial.py      维护者使用的离线文档检查
```

命令约定：出现 Gradle、adb 打包命令时，在**读者自己创建的 Android 项目根目录**执行；出现 `python extras/...` 或 `python scripts/...` 时，在本教程仓库根目录执行。模板必须复制并调整后才生效，不会自动构建、发布或上架。

核验日期：**2026-10-20**。固定版本用于教学复现，不代表最新版本。市场政策、费用与控制台界面会变化，提交前重新查看官方要求。本教程没有托管在线 APK，也不承诺任何市场一定审核通过。
