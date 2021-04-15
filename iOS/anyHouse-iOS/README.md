# anyHouse - iOS

#### 前言
Clubhouse是一个新的社交网络应用程序，提供了实时音频聊天互动方式，给用户创造了打破由社会圈层壁垒所导致的信息传播和人际链接壁垒的可能性。Clubhouse通常被昵称为“硅谷最热门的初创企业”，将自己定位为一个“独家”和“另类”社交网络，吸引了各种名人和只想互相交谈的人。

 [App Store 下载地址](https://apps.apple.com/cn/app/anyhouse/id1560103105)

 [Github开源下载地址](https://github.com/anyRTC-UseCase/anyHouse)

##### 开发环境
- 开发工具：Xcode12   真机运行
- 开发语言：Swift
- SDK：[ARtcKit_iOS](https://docs.anyrtc.io/download)
#### 效果展示
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210415171330738.gif#pic_center)


#### 核心框架
```swift
platform :ios, '9.0'
use_frameworks!

target 'anyHouse-iOS' do
    #anyRTC 音视频库
    pod 'ARtcKit_iOS', '~> 4.1.4.1'
    #anyRTC 实时消息库
    pod 'ARtmKit_iOS', '~> 1.0.1.4'
end
```

#### 项目文件目录结构
![项目结构](https://img-blog.csdnimg.cn/20210415104728522.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDY1NjM0MA==,size_16,color_FFFFFF,t_70#pic_center)
 功能目录：

Main：

- ARMainViewController：主页面，房间列表；

- ARMineViewController：我的，包含修改昵称、隐私协议、版本信息等等；

- ARCreateRoomViewController：创建房间，包含创建公开/私密房间、添加话题。

Audio：

- ARAudioViewController：语音房间，包含语音聊天、上下麦等功能；

- ARMicViewController：请求连麦列表；

- ARReportViewController：举报功能。

#### 流程图

![](https://img-blog.csdnimg.cn/2021032513170967.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzE1NzQxNjAz,size_16,color_FFFFFF,t_70)

