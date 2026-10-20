# 07 · 正式签名、导出 APK，以及以后怎样更新

[返回目录](../README.md) · [上一章](06-product-quality.md) · [下一章：提供下载](08-distribution.md)

## 7.1 三种文件不能混用

| 产物 | 用途 | 用户能直接点开安装吗 |
| --- | --- | --- |
| Debug APK | 开发与测试，自动用调试密钥签名 | 能，但不作为正式公开版本 |
| Release APK | 用正式应用密钥签名后直接分发 | 能 |
| AAB | 提交支持 App Bundle 的市场，由市场处理分发 | 不能当作 APK 直接安装 |

Debug APK 不是空壳，离线示例无需电脑开着也能使用。但 Debug 往往启用调试能力，密钥管理与正式发布不同，所以要单独生成正式包。

## 7.2 系统为什么关心签名

APK 签名关联到一张证书，证书对应开发者私钥。安装时系统验证包的完整性；更新时还要核对应用身份和签名关系。

初学阶段使用同一把密钥，不启用高级密钥轮换。普通覆盖更新的核心条件是：

- `applicationId` 与原安装相同。
- 签名与原版兼容；本教程保持同一密钥。
- `versionCode` 提高，并满足设备与市场规则。
- 新版本的数据结构能读取或迁移旧数据。

`versionName="1.0.1"` 给人看；`versionCode=2` 给系统和市场比较。改显示版本而不增加内部版本，不足以完成正规更新。

详见 [Android 官方应用签名说明](https://developer.android.com/studio/publish/app-signing)。签名不代表应用一定安全，也不等于获得商店审核批准。

## 7.3 先备份，再发布

至少记录：包名、密钥文件位置、alias、密码存储位置、证书 SHA-256 指纹、首次发布渠道。

私钥丢失后，普通直接分发应用通常无法继续以同一身份更新；Play 上传密钥重置与应用签名密钥恢复不是同一件事。不要以“到时再生成一把”作为备份方案。

建议密钥保存在仓库外，至少两份加密备份，密码放密码管理器，确认备份能读取。密钥和密码不要仅放在同一个公开网盘目录里。

## 7.4 路线 A：Android Studio 图形向导

1. 先确认包名与版本号。
2. 打开 `Build → Generate Signed App Bundle or APK`。菜单可能按 IDE 版本略有变化，使用动作搜索也能找到。
3. 需要浏览器下载包，选择 **APK**；需要 Play 提交文件，选择 **Android App Bundle**。
4. 首次选择 `Create new` 创建密钥库，路径放到仓库外。
5. 填写强密码、alias、有效期和证书主体资料；记录真实配置。长期发布应使用足够长的证书有效期。
6. 选择 `release`，按向导生成。
7. 记下向导显示的实际输出目录。图形向导目录未必与命令行默认目录相同。
8. 验证签名，在真机上测试这个产物，再提供下载。

向导中的 V1 / V2 等是不同 APK 签名方案。按当前工具与最低系统要求选择，通常保留工具推荐的现代签名方案；签名成功后再用 apksigner 核验。不要靠勾满未知选项来解决安装失败。

## 7.5 路线 B：本仓库命令行发布脚本

以下是 Windows PowerShell 完整操作，要求你已自行配置 JDK 17、SDK，并使自己项目的 Debug 构建成功。若要使用参考脚本，先把 [build-release.ps1](../examples/pocket-tasks/scripts/build-release.ps1) 复制到自己项目的 `scripts/` 目录；不要在本教程根目录执行发布构建。

### 第一步：创建密钥目录与密钥

```powershell
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\AndroidKeys"
keytool -genkeypair -v -keystore "$env:USERPROFILE\AndroidKeys\pocket-tasks.jks" -storetype JKS -alias pocket-tasks -keyalg RSA -keysize 2048 -validity 10000
```

- `keytool` 来自 JDK，找不到时检查 `JAVA_HOME` 与 Path。
- `keystore` 是存放私钥与证书的文件；alias 是文件中某把密钥的名字。
- `RSA 2048` 指密钥算法和位数；`validity 10000` 的单位是天。
- 命令会交互询问密码与证书信息，输入密码时不显示字符是正常现象。
- JKS 是这里明确选用的密钥库格式；JDK 若提示可迁移到 PKCS12，不代表创建失败。不要在已经发布后随意重建密钥。

一个密钥库可以容纳多把密钥，但本教程只使用一个 alias，降低混淆。

### 第二步：修改正式身份与版本

编辑 `app/build.gradle.kts`：首次学习可暂用示例包名；首次公开发布前使用自己的唯一包名。之后不要为了“升级”改包名。

```kotlin
applicationId = "io.yourname.pockettasks" // 示例，替换成自己的标识
versionCode = 1
versionName = "1.0.0"
```

只改 `applicationId` 可以保持源码 `package` 与 namespace 不变。若同时修改 namespace 和源码包名，需一并更新 MainActivity 的清单类名与相关引用；建议用 IDE 重构工具。

### 第三步：执行发布构建

在你自己的 Android 项目根目录：

```powershell
./scripts/build-release.ps1 -KeystorePath "$env:USERPROFILE\AndroidKeys\pocket-tasks.jks" -KeyAlias 'pocket-tasks'
```

脚本隐藏询问两个密码：密钥库密码、该 alias 的密钥密码。如果创建时选择使用同一密码，两次输入相同值。

若 Windows 执行策略阻止本地 `.ps1`，阅读并确认脚本内容后，仅对这次 PowerShell 子进程使用：

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File ./scripts/build-release.ps1 -KeystorePath "$env:USERPROFILE\AndroidKeys\pocket-tasks.jks" -KeyAlias 'pocket-tasks'
```

不需要永久降低整个电脑的执行策略。脚本会运行 Release 单元测试、Lint、APK 与 AAB 构建；构建失败即停止，不把旧文件当新版本。

成功后：

```text
dist/pocket-tasks.apk
dist/pocket-tasks.aab
dist/SHA256SUMS.txt
```

底层默认输出分别是 `app/build/outputs/apk/release/app-release.apk`、`app/build/outputs/bundle/release/app-release.aab`。

密码只暂时作为构建子进程所需的环境变量传递，脚本结束会恢复之前的进程变量。不写入仓库文件，但它仍不是抵御本机管理员或已入侵电脑的秘密保护方案。避免在共享电脑上做正式签名。

## 7.6 核验签名与文件摘要

设置 SDK 路径后执行，版本号与本项目 Build Tools 一致：

```powershell
$sdkPath = "$env:LOCALAPPDATA\Android\Sdk"
& "$sdkPath\build-tools\35.0.0\apksigner.bat" verify --verbose --print-certs ./dist/pocket-tasks.apk
Get-FileHash ./dist/pocket-tasks.apk -Algorithm SHA256
```

SDK 不在默认位置就改第一行。预期 apksigner 验证成功且退出码为 0。证书指纹应与预期密钥对应；可以用 `keytool -list -v -keystore ... -alias pocket-tasks` 查看。

两种 SHA-256 不要混淆：

- **证书 SHA-256 指纹**：识别签名身份，同一密钥的不同版本通常相同。
- **APK 文件 SHA-256**：识别这一份文件，版本内容或签名结果改变通常就不同。

SHA-256 是将任意文件映射为固定长度摘要的算法。用户可比对下载文件摘要；但若文件与校验值都来自同一个被篡改页面，摘要本身不能证明作者身份。

## 7.7 安装并验证正式包

```powershell
adb install -r ./dist/pocket-tasks.apk
```

本工程 Debug ID 带 `.debug`，Release ID 不带，因此手机可能出现两个同名图标，而且数据独立。这是预期行为，不是 Debug 任务丢失。通过系统应用信息或 adb 查看包名分辨。

在 Release 中重新执行测试表。确保离开电脑、断开 USB 后仍能正常使用。

## 7.8 必做的第一次升级演练

1. 正式版 1：`versionCode=1`，用正式密钥构建并安装。
2. 添加“升级后应保留”任务并勾选完成。
3. 源码改一句可见文案，设 `versionCode=2`、`versionName="1.0.1"`。
4. 使用**同一个密钥、同一个 applicationId** 再构建。
5. `adb install -r` 安装，或在手机点开新 APK 覆盖安装。
6. 验证新文案出现、旧任务与完成状态保留。
7. 将两个版本的源码标签、发布文件、摘要与测试记录分别归档。

不要先卸载旧版来“解决”升级失败；卸载会改变测试条件并可能删除用户数据。也不要把强制降级当作正式回滚。

有严重缺陷时，通常在上一份可靠代码基础上修复，使用更高 versionCode 发布一个修正版。若已经迁移数据结构，需要确认旧代码能否处理新结构，再决定回退策略。

## 7.9 同时走 Play 与网站下载时

Play App Signing 中，“上传密钥”和“给用户 APK 签名的应用签名密钥”可能不同。你本地上传 AAB 使用的密钥，不一定能签出与 Play 安装版互相覆盖的 APK。

在首次开启 Play App Signing 前决定多渠道策略：如需同包名跨渠道更新，应按官方流程确认使用你控制的应用签名身份，或者采用明确区分的渠道包名。由 Google 生成并保管的签名私钥通常不能像本地文件一样导出。

别在已经积累用户后才发现两条渠道签名不同。高级密钥轮换和升级兼容由官方机制处理，不属于简单“换一把新钥匙”。
