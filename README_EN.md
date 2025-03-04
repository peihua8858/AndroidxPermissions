# Androidx permissions
AndroidxPermissions is a Kotlin permission tool library that facilitates Android development to request permissions.
[简体中文🇨🇳](README.md)

[![Jitpack](https://jitpack.io/v/peihua8858/AndroidxPermissions.svg)](https://github.com/peihua8858)
[![PRs Welcome](https://img.shields.io/badge/PRs-Welcome-brightgreen.svg)](https://github.com/peihua8858)
[![Star](https://img.shields.io/github/stars/peihua8858/AndroidxPermissions.svg)](https://github.com/peihua8858/AndroidxPermissions)


## Contents
-[Latest version](https://github.com/peihua8858/AndroidxPermissions/releases/tag/1.1.1-beta32)<br>
-[Download](#Download)<br>
-[Usage](#Usage)<br>
-[Issues](https://github.com/peihua8858/PictureSelector/wiki/%E5%A6%82%E4%BD%95%E6%8F%90Issues%3F)<br>
-[License](#License)<br>


## Download

Use Gradle

```sh
repositories {
  google()
  mavenCentral()
  maven { url 'https://jitpack.io' }
}

dependencies {
  // AndroidxPermissions
  implementation 'com.github.peihua8858.AndroidxPermissions:permissions-core:${latestVersion}'
  implementation 'com.github.peihua8858.AndroidxPermissions:permissions-fragment:${latestVersion}'
  implementation 'com.github.peihua8858.AndroidxPermissions:permissions-compose:${latestVersion}'
}
```
## Usage

A simple use case is shown below:

1、Permission DSL
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
