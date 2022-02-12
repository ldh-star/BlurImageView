
# 安卓图像模糊-BlurImageView
![](https://api.bintray.com/packages/li-xiaojun/jrepo/xpopup/images/download.svg)  
![](https://img.shields.io/badge/author-ldh-brightgreen.svg) ![](https://img.shields.io/badge/compileSdkVersion-32-orange.svg) ![](https://img.shields.io/badge/minSdkVersion-21-orange.svg) ![](https://img.shields.io/hexpm/l/plug.svg)


### 演示
![](https://github.com/ldh-star/BlurImageView/raw/master/source/demo.gif)


[下载apk](https://github.com/ldh-star/BlurImageView/raw/master/app/release/app-release.apk)


### 使用

#### 1.在 build.gradle 中添加依赖


```gradle
implementation 'com.github.ldh-star:BlurImageView:1.0.1'
```

#### 2.使用
```xml
  <com.liangguo.imageblur.BlurImageView
            app:compressScale="0.5"
            app:blurRadius="10"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:src="@drawable/pink_winter" />
```

```kotlin
//压缩的倍率，取值[0, 1]， 值越大，模糊程度越高且性能越好，0则会显示原图
blurImageView.compressScale = 0.5f
//模糊半径，取值 [0, 25]， 模糊半径越大，模糊程度越高，0显示原图
blurImageView.blurRadius = 10f
```


#### 感谢
