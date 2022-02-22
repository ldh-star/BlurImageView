# 安卓图像模糊-BlurImageView

[![](https://jitpack.io/v/ldh-star/BlurImageView.svg)](https://jitpack.io/#ldh-star/BlurImageView) ![](https://img.shields.io/badge/author-ldh-orange.svg) ![](https://img.shields.io/hexpm/l/plug.svg)

### 演示

![](https://github.com/ldh-star/BlurImageView/raw/master/source/demo.gif)

[下载apk](https://github.com/ldh-star/BlurImageView/raw/master/app/release/app-release.apk)

### 使用

#### 1.在 build.gradle 中添加依赖

```gradle
implementation 'com.github.ldh-star:BlurImageView:lastVersion'
```

#### 2.使用

```xml

<com.liangguo.imageblur.BlurImageView
    ...
    app:enableBlurInMainThread="false" 
    app:compressScale="0.5"
    app:blurRadius="10" 
    .../>
```

```kotlin
//压缩的倍率，取值[0, 1]， 值越大，模糊程度越高且性能越好，0则会显示原图
blurImageView.compressScale = 0.5f

//模糊半径，取值 [0, 25]， 模糊半径越大，模糊程度越高，0显示原图
blurImageView.blurRadius = 10f

//是否在主线程进行模糊任务，默认为false
blurImageView.isEnableBlurInMainThread = false
```

## Licenses

```
 Copyright 2022 original author or authors.
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.