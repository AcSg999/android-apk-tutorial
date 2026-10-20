# 13 · 故障排查与最终清单

[返回目录](../README.md) · [上一章](12-china-and-operations.md) · [下一章：验证范围](14-validation.md)

这份手册覆盖常见和高影响难点，不可能列完所有设备、第三方 SDK 与未来政策组合。你要掌握的是定位方法，而不是背一串“万能修复命令”。

## 13.1 排查顺序

```text
工具能启动？→ 项目能解析？→ 能编译？→ 能安装？→ 能启动？→ 功能正确？→ 能更新？→ 能发布？
```

一次只解决当前最早失败的阶段。保留错误原文、执行命令、版本、所在目录和设备信息。复制求助内容前删除密码、令牌、私人路径与用户数据。

不要把“删所有缓存、重装所有软件”当第一步。它可能抹掉证据、耗费流量，且不会修复版本不兼容或权限错误。

## 13.2 环境、同步与下载

| 现象 / 错误 | 先理解什么 | 处理 |
| --- | --- | --- |
| 在教程根目录找不到 gradlew | 这是文档仓库 | 在 Android Studio 创建自己的工程，进入那个根目录 |
| `JAVA_HOME is not set` | 终端未找到正确 JDK | 设置 JDK 安装目录，不是 bin 子目录 |
| JVM 显示 Java 8 | 老 Java 抢在 Path 前面 | 当前终端设置 JAVA_HOME 与 PATH，重新检查 |
| `Unsupported class file major version` | Java 与构建工具版本不配套 | 查 AGP / Gradle / JDK 兼容表，一组调整 |
| IDE 能编译，终端不能 | 使用了不同 JDK / SDK / 代理 | 比较 Gradle JDK、JAVA_HOME、local.properties |
| `SDK location not found` | 项目找不到 SDK | 修正自己项目的 local.properties |
| `Failed to find Platform SDK ...` | 指定平台包没安装 | SDK Manager 安装对应 API 平台 |
| Build Tools 35.0.0 找不到 | 工具包与平台包不同 | Show Package Details 后安装确切版本 |
| `License ... not accepted` | 尚未完成相关 SDK 许可 | 在 SDK Manager 阅读并决定是否接受，不能靠删检查绕过 |
| Gradle 一直下载 | 初次缓存建立或网络不可达 | 看实际下载 URL、代理、磁盘，不要反复终止重试 |
| `handshake_failure` / TLS 错误 | 握手、证书、代理或 JDK 网络环境 | 检查系统时间、维护中的 JDK 更新、代理与企业证书信任；不要关闭证书校验 |
| `PKIX path building failed` | Java 不信任证书链 | 核查代理 / 企业 CA，使用正规证书部署和信任配置 |
| `Could not resolve` / 超时 | 依赖坐标、仓库或网络 | 先看哪个 URL 失败，再决定改配置还是网络 |
| Offline mode 下缺依赖 | 本地从未下载过所需版本 | 有网络时关闭 Offline，正常 Sync |
| 校验值不匹配 | 内容损坏或来源异常 | 核对官方摘要，不以移除校验作为修复 |
| Sync 成功但没有 APK | Sync 不是构建 | 执行 assembleDebug 或 Run |
| `Permission denied` 执行 gradlew | Unix 执行权限未保留 | 使用 `bash ./gradlew ...`，或在自己项目设置执行权限 |
| `.ps1` 被阻止 | PowerShell 执行策略 | 阅读脚本后仅对该子进程调整，不永久关闭系统策略 |
| 内存不足 / daemon 消失 | IDE、模拟器、Gradle 争抢资源 | 先关闭模拟器改用真机，检查日志与磁盘，再合理调内存 |

## 13.3 配置、Kotlin 与资源

| 现象 | 常见原因 | 处理 |
| --- | --- | --- |
| 新模板改版本后大量插件错误 | AGP 9 内置 Kotlin 与旧配置混用 | 统一对照根插件、settings、app 插件与 Wrapper |
| `Unresolved reference: libs` | 删了版本目录仍使用别名 | 全部改为显式坐标，或恢复正确版本目录 |
| `Unresolved reference: ...compose` | 依赖、插件或 import 不完整 | 按官方依赖要求加库，Sync 后核对导入 |
| Compose 编译插件不匹配 | Kotlin 与 Compose 插件不同版本 | 使用配套版本，不把 BOM 数字写到编译插件 |
| `Duplicate class` | 重复库或互斥版本被同时加入 | 查看依赖树，找引入链，不盲目 exclude |
| `requires compileSdk ...` | 库需要更高编译平台 | 选兼容库版本，或成组升级 SDK / AGP 并测试 |
| `minSdkVersion ... cannot be smaller` | 库最低系统高于应用 | 换兼容库或提高 minSdk，不能只强行覆盖 |
| `Android resource linking failed` | XML、资源名或引用错误 | 从第一条具体文件与行号开始排查 |
| `@style/...` 找不到 | 清单引用旧模板资源 | 用实际存在的主题名，核对 res/values |
| `MainActivity` 找不到 | package、namespace 与清单类名不一致 | 核对完整类名，尤其改包名后 |
| AndroidX 错误 | AGP 8 工程未启用相应设置 | 确认 android.useAndroidX=true |

查看 Debug 依赖树可运行 `./gradlew.bat :app:dependencies --configuration debugRuntimeClasspath`。输出很长，重点查冲突库，不把全量日志直接贴到公开 issue。

## 13.4 设备连接与安装

| 现象 / 错误 | 原因方向 | 处理 |
| --- | --- | --- |
| adb 找不到 | platform-tools 没进 Path | 用完整路径或配置当前终端 |
| devices 为空 | 线、驱动、USB 调试、USB 口 | 先查硬件链路 |
| `unauthorized` | 手机没有信任当前电脑 | 解锁手机确认 RSA 授权 |
| `more than one device` | 多台设备在线 | 用 `adb -s 实际序列号` |
| `INSTALL_FAILED_UPDATE_INCOMPATIBLE` | 同包名但签名不兼容 | 找到正确旧密钥，别先卸载正式版 |
| `INSTALL_FAILED_VERSION_DOWNGRADE` | 新包内部版本更低 | 用更高 versionCode 构建修复版 |
| `INSTALL_FAILED_OLDER_SDK` | 手机系统低于 minSdk | 使用支持的设备，或评估降 minSdk 的代码兼容 |
| `NO_MATCHING_ABIS` | 原生库 CPU 架构不匹配 | 生成匹配或通用 APK，检查 SDK / 引擎配置 |
| `INSUFFICIENT_STORAGE` | 手机空间不足 | 清理可删除内容，别误清应用重要数据 |
| `NO_CERTIFICATES` / 签名验证失败 | 未签名、损坏或不兼容包 | 用 apksigner 验证正式 APK，重新可信下载 |
| 点 AAB 无法安装 | 格式不对 | 下载 APK，AAB 用市场或 bundletool 测试流程 |
| 下载是几个 split APK | 拆分分发结果 | 新手分享独立 APK，不让用户猜装哪些 split |
| 系统阻止来源安装 | 来源权限、设备管理、厂商规则 | 查看确切提示，按受支持路径安装，不关闭整体安全保护 |
| 开发者未验证提示 | 地区 / 渠道验证规则 | 查官方开发者验证要求，区分开发测试与公开分发 |
| 两个同名图标 | Debug 与 Release 包名不同 | 看系统应用信息分辨，不误认为数据丢失 |

如果为测试而卸载应用，先导出或确认可丢弃数据。卸载后重装成功，不代表同签名升级测试成功。

## 13.5 运行、数据与联网

| 现象 | 重点看什么 | 处理 |
| --- | --- | --- |
| 一打开就闪退 | Logcat 的 FATAL EXCEPTION | 找最早业务异常和本项目文件行号 |
| 界面卡住 / ANR | 主线程有耗时操作 | 把阻塞 I/O 移到合适调度器，测真实耗时 |
| 列表重复添加 | 连点、重试或并发写入 | 防重复提交，服务端新增采用幂等设计 |
| 旋转后草稿丢失 | 状态存活范围 | 区分 remember、rememberSaveable、ViewModel 与持久化 |
| 重启后任务丢失 | 只保存在内存或写入失败 | 核对落盘、错误处理、是否换包名 / 清数据 |
| DataStore 多实例异常 | 同文件创建多个 DataStore | 每进程每文件保持正确单实例 |
| JSON 读取失败 | 文件格式、旧版本数据或损坏 | 停止覆盖，保留数据，设计迁移与恢复 |
| `NetworkOnMainThreadException` | 主线程阻塞网络请求 | IO 调度器或合适异步网络库 |
| `CLEARTEXT communication ... not permitted` | 使用 HTTP 被限制 | 开发按 Debug 配置放行，生产改 HTTPS |
| 手机 localhost 连不上电脑 | localhost 指手机自己 | 模拟器用 10.0.2.2，真机用反向端口或局域网地址 |
| Wi-Fi 可以，移动网络不行 | 服务器只有局域网地址 | 部署公开 HTTPS 服务并更新正式地址 |
| `401` | 认证缺失或令牌过期 | 走受支持的重新登录 / 刷新流程 |
| `403` | 无权操作 | 修正授权，不能靠客户端跳过校验 |
| `429` | 超出服务限流 | 退避、降低频率，检查费用与配额 |
| 只在 Release 出错 | 正式地址、签名、混淆、SDK 配置 | 检查实际 Release 产物而非 Debug |
| 后台计时 / 连接停止 | 系统后台限制 | 按 WorkManager / 前台服务等适用机制设计 |
| 大图片导致崩溃 | 解码占用远超文件大小 | 下采样、限制尺寸、流式处理 |
| 权限拒绝后崩溃 | 没有降级分支 | 把拒绝、永久拒绝作为正常状态处理 |

## 13.6 下载、CI 与市场

| 现象 | 处理方向 |
| --- | --- |
| GitHub 下载 404 | 检查仓库可见性、Release 是否发布、tag 与文件名大小写 |
| latest 链接指错版本 | 检查 Latest Release 设置、预发布状态与附件同名规则 |
| 下载到 HTML | 用二进制附件地址，确认不是 blob 页面或错误页 |
| 扫码下载旧版 | 版本 URL 不可变，检查 CDN / 浏览器缓存与二维码实际内容 |
| Artifact 过期或要登录 | 它是临时构建产物，公开分发改用 Release / 下载站 |
| CI 找不到 gradlew | 上传的是教程而非完整应用工程，或文件未提交 |
| 本地通过 CI 不过 | SDK / JDK / 版本不一致，文件名大小写差异或依赖本机未提交文件 |
| CI 签名密码泄露 | 停止使用泄露凭证，按平台处理密钥事件，不能只删最近提交 |
| 市场提示 API 过低 | 查询当前目标 API 政策，成组适配并测试行为变化 |
| 原生库 16 KB 不兼容 | 检查所有 `.so` 和第三方 SDK，升级或重新编译合适版本 |
| 隐私声明被拒 | 核对真实 SDK 行为、权限、告知、声明与删除路径 |
| 新个人账号不能正式发布 | 按当前封闭测试要求完成并申请权限 |
| 国内市场缺资质 | 按具体类目、主体和市场准备材料，不把网站下载当作自动豁免 |

## 13.7 最终交付清单

- [ ] 我知道目标用户、核心需求与第一版边界。
- [ ] 我能从官方入口安装环境，自己创建项目并说明版本兼容关系。
- [ ] 我知道依赖在哪声明、由谁下载、缓存为什么不入库。
- [ ] 自己的 Android 工程能构建，适用测试和 Lint 通过。
- [ ] 主流程、错误、空数据、权限拒绝和生命周期已测。
- [ ] 已核验包名、签名、版本号、最低系统与原生库兼容。
- [ ] 密钥有备份，没有进入 Git / APK / 公开日志。
- [ ] 正式 APK 签名验证通过，下载文件摘要与发布记录一致。
- [ ] 陌生设备可以通过真实链接下载、安装、使用。
- [ ] 同签名高版本覆盖安装保留数据。
- [ ] 隐私政策、权限和 SDK 清单与实际版本一致。
- [ ] 选择应用市场时已按当前规则完成账号、资料、测试和审核。
- [ ] 联网产品的后端、数据库、费用、备份和故障支持均有人维护。

## 13.8 怎样提出一个容易解决的问题

```text
目标：我想构建 / 安装 / 请求哪个接口。
环境：系统、IDE、JDK、AGP、Gradle、SDK、设备型号与 Android 版本。
目录：在哪个项目目录运行。
命令或操作：能精确复现的步骤。
预期：应该出现什么。
实际：出现什么，第一条完整错误是什么。
已经尝试：一次改了什么，结果是什么。
隐私处理：已去除密钥、令牌和真实用户资料。
```

能把问题定位到阶段，通常已经完成了一半排查工作。
