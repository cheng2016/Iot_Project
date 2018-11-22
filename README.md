# Iot_Project
一个物联网的原型开发模型.



## The main function

- App 采用 MVP 架构
- 使用 Socket 与服务器进行 TCP 通信
- 集成 Netty 框架优化 TCP 通信
- 通过高德接口获取天气
- 集成高德地图自定义定位功能
- 自定义高德地图mark、以及行车轨迹功能
- 集成及其强大的PullToRefresh下拉刷新库
- 使用 GreenDao 数据库
- 集成 Retrofit2 + okhttp3 + rxjava 的 Http 请求框架
- 集成EventBus实现全局通用广播
- 使用 bindService 模式启动 Service
- 使用 IntentService 通过 TCP 协议上传文件
- 使用 broadcast 进行 activity 与 service 的数据交互 
- 使用懒加载的Fragment基类
- 使用一个通用的Loading页面
- 兼容性的拍照、相册功能的实现
- 微信第三方登录的实现
- 集成Picasso图片加载库，并进行全局定义缓存路径
- 集成微信查看大图功能
- 集成YjPlayer播放器
- 一个带进度条的webview
- 集成FlowLayout流布局
- 集成PickerView选择器视图 ，拥有3D滚动效果
- 自定义录音功能的实现
- 多图片动画效果实现
- 拥有一个及其稳定的文件日志功能
- 拥有崩溃日志收集功能
- 拥有权限8.0兼容功能
- 不同页面状态栏的背景更改功能
- 集成bga-qrcode-zxing二维码扫描功能，以及自定义扫码界面
- 集成joda-time来简化对时间的处理



## Thanks

[loadinglayout](https://github.com/czy1121/loadinglayout)

[Android-PickerView](https://github.com/Bigkoo/Android-PickerView)

[PullToRefresh](https://github.com/cheng2016/PullToRefresh)

[yjPlay](https://github.com/yangchaojiang/yjPlay)

[AndServer轻量级Android内置http服务器](https://github.com/yanzhenjie/AndServer)

[FlowLayout](https://github.com/hongyangAndroid/FlowLayout)

[CustomDialog](https://github.com/cheng2016/CustomDialog)



## Statement

代码仅供技术交流参考，请勿整体挪为商用，否则后果自负。



## License

```
Copyright 2018 chengzj

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

