#  Androidx permissions
AndroidxPermissions是一个Kotlin权限工具库，方便Android开发请求权限。

[English](README_EN.md)

[![Jitpack](https://jitpack.io/v/peihua8858/AndroidxPermissions.svg)](https://github.com/peihua8858)
[![PRs Welcome](https://img.shields.io/badge/PRs-Welcome-brightgreen.svg)](https://github.com/peihua8858)
[![Star](https://img.shields.io/github/stars/peihua8858/kotlinCommonUtils.svg)](https://github.com/peihua8858/AndroidxPermissions)


## 目录
-[最新版本](https://github.com/peihua8858/AndroidxPermissions/releases/tag/1.0.0)<br>
-[如何引用](#如何引用)<br>
-[进阶使用](#进阶使用)<br>
-[如何提Issues](https://github.com/peihua8858/AndroidxPermissions/wiki/%E5%A6%82%E4%BD%95%E6%8F%90Issues%3F)<br>
-[License](#License)<br>


## 如何引用
* 把 `maven { url 'https://jitpack.io' }` 加入到 repositories 中
* 添加如下依赖，末尾的「latestVersion」指的是AndroidxPermissions [![Download](https://jitpack.io/v/peihua8858/AndroidxPermissions.svg)](https://jitpack.io/#peihua8858/AndroidxPermissions) 里的版本名称，请自行替换。
使用 Gradle

```sh
repositories {
  google()
  mavenCentral()
}

dependencies {
  // AndroidxPermissions
  implementation 'com.github.peihua8858.AndroidxPermissions:permissions-core:${latestVersion}'
  implementation 'com.github.peihua8858.AndroidxPermissions:permissions-fragment:${latestVersion}'
  implementation 'com.github.peihua8858.AndroidxPermissions:permissions-compose:${latestVersion}'
}
```
## 进阶使用

一个简单的用例如下所示：

1、权限 DSL用法
```kotlin
import com.peihua8858.permissions.core.requestPermission
requestPermissionsDsl(Manifest.permission.POST_NOTIFICATIONS) {
    onDenied {
         showToast("Denied")
    }
    onShowRationale {
         showToast("Denied,show rationale")
    }
    onGranted {
        showToast("Granted")
    }
}
```
## License

```sh
Copyright 2025 peihua

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
