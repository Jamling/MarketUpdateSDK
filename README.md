# MarketUpdateSDK
百度及360等国内市场上架必须集成的SDK

## 已集成的SDK
- 360
- 百度

## 360

文档：https://github.com/Jamling/MarketUpdateSDK/raw/master/doc/360%E6%9B%B4%E6%96%B0sdk7.0.3%E6%96%87%E6%A1%A3.docx

引入依赖库

```gradle
dependencies {
    compile "cn.ieclipse.aar-ref:360update:7.0.3"
}
```

App Manifest

```
        <!-- 360 -->
        <provider
            android:name="com.qihoo.appstore.updatelib.UpdateProvider"
            android:authorities="${applicationID}.musdk.360.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/update_apk_path"/>
        </provider>
```

## 百度

文档：https://github.com/Jamling/MarketUpdateSDK/raw/master/doc/%E7%99%BE%E5%BA%A6APP%E6%99%BA%E8%83%BD%E5%8D%87%E7%BA%A7SDKForAndroid%E5%8F%82%E8%80%83%E6%89%8B%E5%86%8C.pdf


引入依赖库

```gradle
dependencies {
    compile "cn.ieclipse.aar-ref:bd_autoupdate_sdk:1.3.1"
}
```

App Manifest

```
        <!-- baidu -->
        <provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="${applicationID}.musdk.baidu.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/bdp_update_filepaths"/>
        </provider>

        <meta-data
            android:name="BDAPPID"
            android:value="${BDAPPID}"
            tools:replace="android:value"/>
        <meta-data
            android:name="BDAPPKEY"
            android:value="${BDAPPKEY}"
            tools:replace="android:value"/>
        <!-- end baidu-->
```

## 注意事项

上传360市场，apk中不能包含百度升级sdk相关的代码

上传百度市场，apk中不能包含360升级sdk相关的代码

建议创建不同的代码分支来集成360和百度升级SDK
