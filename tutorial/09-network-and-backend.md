# 09 · 联网、后端、登录与数据库

[返回目录](../README.md) · [上一章](08-distribution.md) · [下一章：应用类型](10-app-types.md)

离线主线已经能独立发布。本章先做一个可运行的联网小实验，再解释真正多人产品需要增加哪些系统。**实验只读取公开提示，不是完整账号与同步后端。**

## 9.1 手机怎样向服务器请求数据

先定义这些术语：客户端是手机应用；服务端是接收请求的程序；API 是双方约定的交互接口；HTTP 是请求与响应协议；JSON 是本章响应使用的数据格式。

```text
手机点击 → GET /tip → 电脑上的服务 → JSON 响应 → 手机解析 → 显示文字
```

一个 HTTP 请求至少关心：URL、方法、请求头、可能的请求体。响应包含状态码、响应头和响应体。

- `GET`：读取资源。
- `POST`：创建或执行操作。
- `PATCH`：局部更新。
- `DELETE`：删除。
- `200`：本次请求成功；`400`：请求不合要求；`401`：未通过身份认证；`403`：无权限；`404`：资源不存在；`429`：过于频繁；`5xx`：服务端出错。

这些是常见约定，具体含义还要看 API 合同。收到 HTTP 200 不代表业务一定成功，也要校验响应字段。

## 9.2 实验第一步：电脑启动一个真实 API

要求 Python 3。在仓库根目录：

```powershell
python extras/network-lab/server.py
```

另开终端检查：

```powershell
Invoke-RestMethod http://127.0.0.1:8000/health
Invoke-RestMethod http://127.0.0.1:8000/tip
```

预期分别得到 `status=ok` 和一句中文。服务仅用标准库，无需 pip 安装依赖。

完整代码在 [server.py](../extras/network-lab/server.py)。想验证 HTTP 接口：

```powershell
python -m unittest discover -s extras/network-lab -p "test_*.py" -v
```

此服务只用于本地练习，不提供 HTTPS、账号、限流、持久数据库或生产部署能力，不要把它直接暴露到公网。

## 9.3 第二步：理解 localhost 的陷阱

`127.0.0.1` / `localhost` 指“当前运行程序所在的设备自身”。

| 从哪里发请求 | 访问开发电脑服务的常见地址 |
| --- | --- |
| 电脑自己 | `http://127.0.0.1:8000` |
| Android 官方模拟器 | `http://10.0.2.2:8000` |
| USB 连接的真机，设置反向端口后 | `http://127.0.0.1:8000` |
| 同一 Wi-Fi 的真机 | `http://电脑实际局域网IP:8000` |
| 普通用户的手机 | 已部署服务器的公开 HTTPS 域名 |

模拟器 `10.0.2.2` 的含义见 [官方模拟器网络说明](https://developer.android.com/studio/run/emulator-networking)。第三方模拟器可能使用不同约定。

真机 USB 练习可运行：

```powershell
adb reverse tcp:8000 tcp:8000
```

这把设备上的 8000 端口请求转发到电脑的 8000 端口；随后客户端 URL 改成 `http://127.0.0.1:8000/tip`。它只服务开发连接，发给朋友后不会凭空访问你的电脑。结束后可运行 `adb reverse --remove tcp:8000`。

局域网真机练习则把服务改为 `python extras/network-lab/server.py --host 0.0.0.0`，使用电脑实际 IP，检查私人网络防火墙。同样不要开放公网路由端口。

## 9.4 第三步：让 Android 练习页面请求 API

先保存离线版源码提交，或复制一个练习工程。以下修改只用于实验。

1. 把 [NetworkLabScreen.kt](../extras/network-lab/NetworkLabScreen.kt) 复制到 `app/src/main/java/com/example/pockettasks/`。它使用主线已包含的依赖，不用额外加网络库。
2. 在主 Manifest 的 `<manifest>` 下、`<application>` 之前添加：

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

3. 开发实验使用 HTTP，而现代 Android 默认限制明文流量。只为 Debug 新建 `app/src/debug/AndroidManifest.xml`：

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application android:usesCleartextTraffic="true" />
</manifest>
```

它与主 Manifest 合并，只作用于 Debug。不要为省事把明文放行复制到 Release。网络安全配置详情见 [Android 官方文档](https://developer.android.com/privacy-and-security/security-config)。

4. 在 MainActivity 的 `setContent` 中，将 `Surface { TaskScreen() }` 对应内容替换为 `Surface(modifier = Modifier.fillMaxSize()) { NetworkLabScreen() }`，保留外层 MaterialTheme。
5. 使用官方模拟器时，保留示例 URL `http://10.0.2.2:8000/tip`。真机按上一节改地址。
6. 确认电脑 API 仍在运行，重新 Run Debug，点击“获取提示”。

预期显示服务器返回的中文。关闭服务器再点按钮，应出现错误提示，按钮恢复可用。客户端设置了连接与读取超时，并将阻塞 I/O 放在 IO 调度器。

这次实验没有修改待办数据存储。回到离线主线时，恢复 MainActivity 原入口，并删除练习添加的网络权限与 Debug 明文配置，避免发布说明与实际权限不一致。

## 9.5 从实验到真实联网产品，差了什么

生产系统至少需要决定：域名、HTTPS、服务器运行平台、数据库、身份认证、备份、限流、日志、更新与故障处理。托管后端如 Firebase / Supabase 可省去一部分基础设施；自建可选 Python FastAPI 等框架。先核对目标地区可达性、数据位置、功能和费用，再选择。

| 层 | 可以选择什么 | 你必须理解什么 |
| --- | --- | --- |
| Android 网络层 | Retrofit / OkHttp 等 | 超时、HTTP 错误、响应解析、取消 |
| 服务器接口 | FastAPI 等框架，或托管函数 | 参数校验、权限、错误响应 |
| 身份认证 | 托管认证、OIDC 等成熟方案 | 登录状态、令牌、过期、退出 |
| 数据库 | PostgreSQL 等，或托管数据库 | 表、主键、索引、备份、事务 |
| 文件 | 对象存储 | 上传授权、大小限制、访问权限 |
| 定时后台任务 | Android WorkManager / 服务端任务队列 | 延迟、重试、任务重复 |

这里列的是可选技术，不要求全装。本章没有预置一个可投入生产的认证系统。

## 9.6 登录的第一性原理

登录解决“服务器如何知道你是谁”。授权解决“知道你是谁之后，允许你操作什么”。二者不同。

典型流程：

1. 用户通过可信身份服务完成登录。
2. 客户端收到短期 access token（访问令牌），必要时还有 refresh token（刷新凭证）。
3. 客户端在 HTTPS 请求中携带令牌。
4. 服务器验证签发者、签名、有效期、目标服务与用户身份。
5. 服务器只允许访问该用户有权限的数据。
6. 访问令牌过期后按认证服务流程刷新；退出登录时清理本地凭证，并按服务端机制撤销会话。

**只在手机上隐藏按钮不是权限控制。** 例如用户 A 请求 `/tasks/用户B的任务ID`，服务端必须拒绝；不能相信请求体里自称的 `userId`。

不要自己发明密码加密算法或把用户密码直接存进数据库。初学项目优先采用维护良好的认证服务，并阅读其官方安全配置。

## 9.7 多人待办的最小数据模型

先定义字段：`id` 是任务主键；`owner_id` 是所有者；`title` 是标题；`done` 是完成状态；`updated_at` 是服务器更新时间；`version` 用于冲突检测。

```text
tasks(id, owner_id, title, done, updated_at, version)
```

建议最小接口合同：

| 方法与路径 | 作用 | 核心约束 |
| --- | --- | --- |
| GET /v1/tasks | 读取自己的任务 | 身份来自服务端验证的令牌 |
| POST /v1/tasks | 新增 | 校验长度、数量、请求幂等性 |
| PATCH /v1/tasks/{id} | 改标题或状态 | 校验所有权与版本 |
| DELETE /v1/tasks/{id} | 删除 | 校验所有权，可设计软删除 |

“幂等”指同一个操作因网络重试执行多次，不产生额外副作用。例如新增接口带客户端操作 ID，服务端记住已处理 ID，避免断网重试创建两条任务。

数据库事务让关联写入成功或失败保持一致；索引用于提高按 owner_id 等条件查询的效率；备份必须演练恢复，否则只是一个可能没用的文件。

## 9.8 离线同步比“请求一个接口”复杂

至少要回答：手机离线修改怎样排队？两台设备改同一项谁覆盖谁？删除怎样传播？手机时钟不准怎么办？重复上传如何去重？

初学者先做“联网才可编辑 + 本地只读缓存”，待流程可靠后再做离线同步。若需要离线优先，可采用 Room 本地数据库、待同步操作队列、服务端版本检查和冲突提示。

不要简单用“手机时间较新者覆盖”当作绝对正确规则。对重要数据，显式版本冲突比无声覆盖更容易保护用户。

## 9.9 后端上线的逐项验收

1. 选定运行平台并确认预算、地区、账号和数据存储要求。
2. 数据库不直接暴露给任意客户端；使用最小权限服务账号。
3. 密钥存环境或托管密钥服务，不写进镜像和 Git。
4. 数据库迁移在测试环境演练，正式执行前备份。
5. 域名 HTTPS 可访问，证书自动续期正常。
6. 健康检查、进程重启与日志可用，日志过滤个人信息。
7. 请求参数和文件大小有上限，认证与限流正常。
8. 测试两个账号，确保不能互读、互改私人数据。
9. 手机关闭 USB，切到移动网络，仍可访问生产接口。
10. 将 Release 配置切为正式 HTTPS 地址，重打包再测，之后才更新隐私说明并发布。

代码部署到服务端，与 APK 上传到下载页是两个独立交付动作。要让联网应用长期工作，两边都要维护。
