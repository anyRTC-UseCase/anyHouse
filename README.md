## 高仿 CloubHouse

> 本文将介绍目前市面上比较流行的音频沙龙应用场景，并讲述基于 anyRTC  SDK,模仿 CloubHouse 的步骤。



近几年，语音社交产品也算是频频进入大家的视野，像早期的 YY，近期的 CloubHouse。语音聊天在泛娱乐社交行业中有着重要的地位，行业中很多佼佼者也都为用户提供了语音聊天室。有些语聊的应用中除了群聊社交之外还有带玩、叫醒服务、知识付费等。

看似简单的应用，有些优质的语音社交平台能达到每月1亿的流水，当下语聊市场已经被资本疯狂炒作，如何能够站在风口处，成为了当下热聊的话题。

### Demo 体验
![扫码体验](https://img-blog.csdnimg.cn/20210324210525628.png)
### 精仿的效果
![](https://img-blog.csdnimg.cn/20210324210600439.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p6dXp6bA==,size_16,color_FFFFFF,t_70)

![](https://img-blog.csdnimg.cn/20210324210552672.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p6dXp6bA==,size_16,color_FFFFFF,t_70)



### 代码下载

[Github](https://github.com/anyRTC-UseCase/anyHouse)代码下载慢，请移步至[码云](https://gitee.com/anyRTC_admin/anyHouse)下载

### 实现步骤

#### 业务部分

- 获取房间列表
- 创建房间/进入房间/退出房间
- 申请举手/取消举手/邀请请求/取消邀请/同意邀请
- 获取嘉宾列表/获取观众列表

#### RTM 实时消息部分

- 获取房间信息后-》登录RTM做上线操作，
- 加入进入频道
- 订阅主播状态：根据该状态可以知晓当前房间的主播在线状态，做页面提示
- 发送频道消息：自定义内容可做房间事件信令同步通知（游客进入房间，举手，邀请等）
- 退出频道
- 下线

#### RTC 实时音视频部分

- 设置房间类型：直播类型
- 设置AI降噪以及音乐模式场景：高音质全频带音质
- 设置角色：房主设置为主播，观众设置为游客，观众同意邀请后设置为主播，下台设置为游客
- 开关音频：是否静音
- 监听说话者回调：根据回调数据提示某人在说话，在头像上做动效
- 监听人员上下线：嘉宾栏人员变动更新
- 离开频道

### 代码

### 平台兼容

anyHouse 示例项目支持以下平台和版本：

- iOS 9 及以上。

- Android 4.4 及以上。

### 代码目录说明

- [Android 目录说明](https://github.com/anyRTC-UseCase/anyHouse/tree/master/Android/AnyHouse)

- iOS 目录说明~在飞速开发中

### 反馈与建议

联系电话：021-65650071

QQ咨询群：580477436

咨询邮箱：hi@dync.cc

加微信入技术群交流：
<img src="https://img-blog.csdnimg.cn/20210324215941588.png" style="zoom:50%;" />

获取更多帮助前往：[www.anyrtc.io](https://www.anyrtc.io/)
