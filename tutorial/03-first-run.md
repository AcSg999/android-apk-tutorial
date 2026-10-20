# 03 · 第一次把应用装到手机

[返回目录](../README.md) · [上一章](02-environment.md) · [下一章：从空项目重建](04-build-from-zero.md)

## 3.1 先认识两个按钮

- **Sync**：让 IDE 理解 Gradle 配置并解析依赖。Sync 成功不代表 APK 已经编译成功。
- **Run**：编译应用、生成测试包、安装到所选设备、启动页面。

工具栏选择 `app` 运行配置，再选择设备，点击绿色运行三角。如果还没有设备，走下面任一路线。

第一次先运行第 02 章新建的 Empty Activity，看到模板的 Hello 页面就算设备链路成功。**第 3.4—3.7 节的待办命令与验收，在第 04 章实现参考应用之后执行。** 自己做其他应用时替换包名、入口类和验收用例。

## 3.2 路线 A：使用自己的 Android 手机

1. 手机打开“设置 → 关于手机”，连续点击“版本号”直到提示已启用开发者选项。不同厂商位置不同，可能需要输入锁屏密码。
2. 返回设置，进入开发者选项，开启 **USB 调试**。
3. 用能传数据的 USB 线连接电脑。只支持充电的线会让你看见充电却找不到设备。
4. 手机如有 USB 用途提示，可选择文件传输；关键是让调试连接被识别。
5. 手机弹出 RSA 指纹授权时，核对这是你自己的电脑，再允许调试。公共电脑不要选择永久信任。
6. Android Studio 设备列表选择该手机，点击 Run。
7. 某些厂商还要求开启“USB 安装”或确认安装提示。跟随本机说明处理。

Windows 找不到设备时，查看设备管理器与厂商官方 USB 驱动。不要随意安装不明“万能驱动”。

若已配置 adb：

```powershell
adb devices
```

| 结果 | 含义 | 下一步 |
| --- | --- | --- |
| 序列号后是 `device` | 连接正常 | 可安装运行 |
| `unauthorized` | 手机未授权电脑 | 解锁手机，确认调试授权 |
| `offline` | 连接异常 | 拔插数据线，必要时重启 adb |
| 没有设备 | 线、驱动、USB 口或调试设置问题 | 先解决连接，再看代码 |

多个设备时，在命令里用 `adb -s 实际序列号 ...` 指定一个，避免误装。

## 3.3 路线 B：使用模拟器

1. Android Studio 打开 `Tools → Device Manager`。
2. 创建虚拟设备，先选普通手机尺寸。
3. 下载正式稳定的 Android 系统镜像。建议准备 API 36 镜像测试目标系统行为；以后再增加 API 26 镜像测试最低系统。
4. 镜像 CPU 架构与电脑匹配：常见 Intel / AMD 使用 x86_64，Apple Silicon 使用相应 ARM64 镜像。
5. 启动模拟器，等待系统完全进入桌面，再 Run。

若提示虚拟化不可用，按 [官方模拟器加速说明](https://developer.android.com/studio/run/emulator-acceleration) 检查 BIOS / UEFI 虚拟化和操作系统虚拟化组件。不要仅为排障盲目关闭系统安全设置。

内存不足时先用真机。模拟器适合覆盖系统版本，真机适合验证触摸、摄像头、电量、网络和厂商行为，两者互补。

## 3.4 命令行完整构建

在你自己的 Android 项目根目录执行（已完成第 04 章参考配置）：

```powershell
./gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

参数逐个解释：

- `:app:`：指定 app 模块。模块是项目的一部分，参考应用只有一个应用模块。
- `testDebugUnitTest`：执行 Debug 版本的本地单元测试。
- `lintDebug`：静态检查可能的代码、资源、平台兼容问题。
- `assembleDebug`：编译打包可安装的 Debug APK。

预期最后出现 `BUILD SUCCESSFUL`，且存在：

```text
app/build/outputs/apk/debug/app-debug.apk
```

使用 adb 安装并启动：

```powershell
adb install -r ./app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.example.pockettasks.debug/com.example.pockettasks.MainActivity
```

这里有两个相似但不同的名字：

- `com.example.pockettasks.debug` 是 Debug 安装包的应用 ID。
- `com.example.pockettasks.MainActivity` 是源码类的完整名称。

`-r` 表示尝试保留数据覆盖安装；它不能绕过签名不一致、版本限制或系统保护。

也可以执行 `./gradlew.bat :app:installDebug` 自动安装。Run 按钮会进一步帮你启动页面。

## 3.5 第一次验收：照着点

| 操作 | 期望 |
| --- | --- |
| 第一次打开 | 显示 0 项，不弹权限申请 |
| 输入空格 | 添加按钮不可用 |
| 添加“买牛奶”和“学习 Android” | 共 2 项，新任务在前 |
| 勾选“买牛奶” | 待完成减少，文字出现划线 |
| 切换未完成 | 只剩“学习 Android” |
| 切换已完成 | 只看到“买牛奶” |
| 点删除后取消 | 数据仍在 |
| 再删除并确认 | 该任务消失 |
| 旋转手机 | 已保存任务保留，输入框内容尽量恢复 |
| 强制停止后重开 | 已保存任务保留 |
| 开飞行模式再操作 | 添加、勾选、删除均正常 |
| 切深色模式 | 文字与背景仍清楚 |

“划掉最近任务”与“强制停止”不是所有厂商上都等价。可从系统应用设置强制停止后再点图标。**不要用清除数据或卸载来测试“重启”，那会删除本地记录。**

建议把执行结果填入 [测试模板](../templates/test-plan.md)，记录手机型号、系统 API、应用版本和时间。

## 3.6 第一次定位错误

构建失败看 Android Studio 的 Build 输出；运行闪退看 Logcat。它们解决不同阶段的问题。

```powershell
adb logcat -s AndroidRuntime
```

清楚复现一次问题，找 `FATAL EXCEPTION` 后最接近自己代码的第一条堆栈。不要把“最后一行红字”自动当成根因。

若错误与网络下载相关，先不用改 Kotlin。若 `SDK location not found`，先修 SDK 路径。若应用根本装不上，先看安装错误码。更完整对照表在 [故障排查](13-troubleshooting.md)。

## 3.7 改一句话，证明你掌握了反馈循环

打开 [MainActivity.kt](../examples/pocket-tasks/app/src/main/java/com/example/pockettasks/MainActivity.kt)，把首页标题 `Text("口袋待办", ...)` 改成自己喜欢的文字，再运行。

手机首页能看到新标题，说明你已经走通：编辑 → 编译 → 安装 → 验证。桌面图标名称由 `strings.xml` 的 `app_name` 控制，它与首页标题是两处独立内容。
