# 04 · 从空项目开发完整应用

[返回目录](../README.md) · [上一章](03-first-run.md) · [下一章：理解代码](05-understand-code.md)

这一章让你理解一个工程为何需要这些文件。代码的完整、唯一版本直接链接到仓库文件，避免教程与源码出现两套不一致的内容。

## 4.1 两种练习方式

推荐使用第 02 章自己创建的 Empty Activity 项目，先确认模板能运行，再逐项完成本章。这样你学到的是创建项目与配置依赖的流程，而不是解压一个别人已经准备好的工具包。

参考源码位于 `examples/pocket-tasks/`，只用于对照。下面提到的路径均指**你自己的 Android 项目**，不要把本教程文档仓库当作构建根目录。先自己理解和填写配置，再对照完整文件检查遗漏。

新模板可能采用 AGP 9、内置 Kotlin 与版本目录。以下统一切换到 AGP 8.13.2 的教学组合：根构建脚本、settings、app 构建脚本和 Wrapper 版本一起对照。新模板原有 `libs.versions.toml` 如果不再被任何脚本引用，可以暂时保留；不要把旧的插件别名和新配置同时启用。

## 4.2 第一步：根目录构建配置

打开项目中下列文件，解释每项后按参考内容配置：

| 文件 | 完整内容 | 它解决的问题 |
| --- | --- | --- |
| `settings.gradle.kts` | [查看](../examples/pocket-tasks/settings.gradle.kts) | 仓库地址、项目名、包含 app 模块 |
| `build.gradle.kts` | [查看](../examples/pocket-tasks/build.gradle.kts) | 固定 AGP、Kotlin、Compose 插件版本 |
| `gradle.properties` | [查看](../examples/pocket-tasks/gradle.properties) | Java 内存、编码、AndroidX |
| `.gitignore` | [查看](../examples/pocket-tasks/.gitignore) | 防止缓存、本机路径、密钥进入代码仓库 |
| `gradle/wrapper/gradle-wrapper.properties` | [查看](../examples/pocket-tasks/gradle/wrapper/gradle-wrapper.properties) | 固定 Gradle 下载与校验配置 |

`.kts` 表示构建脚本使用 Kotlin 语法，它仍然是配置，不是手机界面代码。不要把使用 Groovy 的 `build.gradle` 示例直接混进 `.kts` 文件。

保留 Android Studio 创建项目时生成的 `gradlew`、`gradlew.bat` 和 `gradle/wrapper/gradle-wrapper.jar`。它们不是本教程提供的下载结果。修改 `gradle-wrapper.properties` 的 `distributionUrl` 为参考的 Gradle 8.13 地址，并使用相应的官方 SHA-256 校验值；首次执行由你电脑上的 Wrapper 下载该版本。

**检查点：** 在你自己的项目根目录执行 Wrapper 的 `--version`，应看到 Gradle 8.13。这一步还没有编译 Android 代码。若现在 Sync 报错，继续完成下一节 app 插件配置再 Sync，避免新旧配置处于中间状态。

## 4.3 第二步：声明 app 模块

创建 `app/build.gradle.kts`，复制 [完整模块配置](../examples/pocket-tasks/app/build.gradle.kts)。

先认识关键字段：

```kotlin
android {
    namespace = "com.example.pockettasks"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.example.pockettasks"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }
}
```

这是解释片段，完整文件还包括插件、依赖、签名与构建类型。

- `namespace`：生成的资源类等代码使用的命名空间。
- `applicationId`：系统和商店识别应用的身份。
- Kotlin 文件首行 `package`：源码类所在命名空间。

三者经常相同，但含义不同。Debug 额外拼接 `.debug` 是为了让测试版和正式版共存；源码包名保持不变。

首次公开发布前，把 `applicationId` 改成自己长期使用且控制的唯一标识，如 `io.yourname.pockettasks`。不要把 `yourname` 原样发布。学习阶段先保持示例值，避免同时改动太多地方。

**重要：** 本配置没有为 Release 自动套用 Debug 密钥。未提供正式密钥时，Release APK 可能是 `app-release-unsigned.apk`，不能作为正式可安装交付。后面有完整签名步骤。

## 4.3.1 自己添加依赖，而不是等待别人替你装好

依赖是项目引用的外部库。通常不用先到网页下载一个 JAR 再复制进工程，而是声明坐标，让构建工具在你本机下载并缓存。

坐标由三段组成，先区分它们：`group` 是组织命名空间，`name` 是库名，`version` 是具体版本。Gradle 中写作 `group:name:version`。

添加一个库的完整步骤：

1. 在该库官方文档确认 Android 支持、最低 SDK、Kotlin / AGP 兼容性、许可证和安装说明。
2. 在 `settings.gradle.kts` 确认声明了对应可信仓库。本例 Android 库使用 `google()`，其他常用库使用 `mavenCentral()`。
3. 在 **app 模块**的 `build.gradle.kts` 的 `dependencies` 内加入 `implementation(...)`，不要误放到根插件配置里。
4. 点击 Sync Now，等待你电脑自行解析并下载。
5. 在源文件导入 API，写一个最小调用，再编译和运行。
6. 查看合并后的 Manifest 和依赖说明，确认有没有新增权限、数据收集和原生库。
7. 固定版本并记录升级原因，不用 `+` 让下次构建悄悄变成新版本。

例如主线的存储库配置是：

```kotlin
dependencies {
    implementation("androidx.datastore:datastore-preferences:1.1.7")
}
```

Compose 的 BOM 是一组版本约束，所以部分 Compose 库不单写版本：

```kotlin
dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.09.01"))
    implementation("androidx.compose.material3:material3")
}
```

BOM 不管理 Activity、Lifecycle、Kotlin 插件等全部版本，它们仍要单独声明。`implementation` 引入运行所需库；`testImplementation` 引入本地测试库；`debugImplementation` 只在 Debug 参与。构建插件本身则在 `plugins` 区域配置，不是普通业务依赖。

有的模板使用 `gradle/libs.versions.toml` 版本目录，通过 `libs.xxx` 别名引用。那是同一类信息的集中管理方式，不是“已经安装好了”。初学阶段按本例显式坐标配置即可，不要同时保留同一库的两个冲突版本。

常见失败依次检查：坐标拼写 → 版本是否真实发布 → 仓库是否正确 → 网络与代理 → 最低 SDK / 编译工具约束。`Could not resolve` 可能是网络问题，`requires compileSdk ...` 才是在要求调整平台兼容；不要看到红字就全量升级。

## 4.4 第三步：应用清单与资源

建立目录，并放入这些文件：

| 路径 | 完整内容 | 用途 |
| --- | --- | --- |
| `app/src/main/AndroidManifest.xml` | [查看](../examples/pocket-tasks/app/src/main/AndroidManifest.xml) | 声明应用和启动页面 |
| `app/src/main/res/values/strings.xml` | [查看](../examples/pocket-tasks/app/src/main/res/values/strings.xml) | 桌面名称 |
| `app/src/main/res/values/themes.xml` | [查看](../examples/pocket-tasks/app/src/main/res/values/themes.xml) | Android 窗口基础主题 |
| `app/src/main/res/drawable/ic_launcher.xml` | [查看](../examples/pocket-tasks/app/src/main/res/drawable/ic_launcher.xml) | 简单矢量启动图标 |

Manifest 相当于应用向 Android 提交的“组件声明表”。其中：

- `MAIN + LAUNCHER` 告诉桌面从哪个 Activity 启动。
- `android:exported="true"` 允许系统启动入口页面；不要给不需要外部访问的内部组件随意开启 exported。
- 没有 `INTERNET` 权限，所以主线不会联网。
- `allowBackup="false"` 关闭常规系统云备份路径；设备厂商的迁移行为仍需实测，不能把它当成任何设备上绝不迁移的保证。

`res` 下资源文件名用小写、数字和下划线。文件名中的大写字母、空格与连字符可能导致资源编译失败。

## 4.5 第四步：定义数据与业务规则

创建 `app/src/main/java/com/example/pockettasks/`，放入 [Todo.kt](../examples/pocket-tasks/app/src/main/java/com/example/pockettasks/Todo.kt)。

先只关注这行：

```kotlin
data class Todo(val id: String, val title: String, val done: Boolean = false)
```

一个任务有稳定 ID、标题、完成状态。ID 不能用列表下标代替，因为删除或排序后，下标会改变。`done=false` 是新任务默认未完成。

这个文件还放置长度校验和筛选规则。把这些规则独立出来，可以在不启动 Android 手机的情况下测试。

## 4.6 第五步：写存储层

复制 [TodoRepository.kt](../examples/pocket-tasks/app/src/main/java/com/example/pockettasks/TodoRepository.kt)。Repository 即“数据访问入口”，把页面与文件格式隔开。

本实现把小任务列表编码为 JSON 字符串，再交给 Preferences DataStore 保存。JSON 是一种文本数据格式，例如：

```json
[
  { "id": "稳定标识", "title": "买牛奶", "done": false }
]
```

这不是关系数据库。它适合小示例，不适合大量搜索、复杂关联或高频局部更新。扩展到几千条任务、标签、附件、同步时迁移到 Room。

关键行为：添加 / 勾选 / 删除在 DataStore 的 `edit` 事务中读取最新值并保存，减少并发点击引起的“旧状态覆盖新状态”。文件读取失败时不自动变成空列表再写回，避免把损坏数据覆盖掉。

## 4.7 第六步：写状态管理层

复制 [TodoViewModel.kt](../examples/pocket-tasks/app/src/main/java/com/example/pockettasks/TodoViewModel.kt)。ViewModel 是页面状态与交互的管理者。

它完成：

1. 订阅存储中的任务变化。
2. 将读取中、读取失败、读取成功表达为 UI 状态。
3. 在协程中调用添加、勾选、删除。
4. 防止添加按钮重复点击。
5. 给界面提供保存失败提示。

保存成功后才清空输入，避免“看起来添加了，实际没写进去”。下一章会解释协程和 Flow。

## 4.8 第七步：写界面入口

复制 [MainActivity.kt](../examples/pocket-tasks/app/src/main/java/com/example/pockettasks/MainActivity.kt)。它是本应用唯一的 Activity，内部放置 Compose 组件。

界面从上到下：标题 → 统计 → 输入框 → 添加按钮 → 错误提示 → 筛选按钮 → 可滚动任务列表。任务行包括勾选框、标题、删除按钮；删除弹出确认对话框。

这里没有单独使用导航框架，因为只有一页。以后增加设置页、详情页时再引入 Navigation Compose，别为一个页面先造一套复杂导航系统。

## 4.9 第八步：加入规则测试

创建 `app/src/test/java/com/example/pockettasks/`，复制 [TodoRulesTest.kt](../examples/pocket-tasks/app/src/test/java/com/example/pockettasks/TodoRulesTest.kt)。

它验证空白处理、80 字符边界与筛选行为。它没有验证 Android 文件读写、屏幕布局、数据损坏处理或真机安装；这些需要其他测试与实际操作。

## 4.10 第九步：配置本机路径并运行

1. 用 Android Studio 打开新工程根目录。
2. 依照 [环境章节](02-environment.md) 配置 JDK 17、SDK 和 `local.properties`。
3. Sync 成功后运行测试与构建。
4. 连接设备 Run。
5. 执行上一章的验收表。

至此你已经手动创建了完整工程。接下来请做三项小修改：

- 把应用显示名称改为自己的名字。
- 修改主题主色，观察深色模式是否仍可读。
- 给空列表写一句自己的提示语。

每次只改一项、构建一次。遇到失败就用 Git 或编辑器撤销回到最后一个可运行状态。

## 4.11 给工程建立历史

安装 Git 后，在你新建的工程根目录：

```powershell
git init
git add .
git status
git diff --cached
git commit -m "Create offline task app"
```

提交前确认没有 `.jks`、密码、`local.properties` 或私人文件。Git 第一次提交若要求姓名与邮箱，按提示设置你的提交身份；无需把签名密钥作为 Git 身份。

Git 提交只是保存本地历史，不会自动上传 GitHub。第一次推送仓库前仍要检查内容是否适合公开。
