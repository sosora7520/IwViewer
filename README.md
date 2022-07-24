![Logo](/app/src/main/res/mipmap-xxhdpi/ducky.png)
# IwViewer

基于Jetpack Compose开发的 [iwara](https://iwara.tv) 安卓app, 采用Material You设计, 支持安卓6.0以上版本, 完全无多余权限请求
使用 JSoup/Retrofit 解析I站网页，提取数据并渲染为安卓原生界面，I站**任何内容与本作者无关**，app仅仅承担浏览器的功能   
使用请遵守你所在地区法律，请勿公开传播该APP


## 🚩 特性
* Material You设计
* 暴力自动重连
* 登录/查看个人信息
* 浏览订阅更新列表
* 播放视频
* 查看图片
* 查看评论
* 点赞
* 关注
* 评论
* 分享  
* 搜索
* 榜单
* 下载

## 🧭 常见问题
* **为什么不能查看自己关注了哪些人？**   
  答: 因为Iwara网站端禁用了这个功能，据说是因为这个功能会导致数据库负载增大导致网站宕机，如果以后iwara重新开放这个功能，我会加上的

* **APP支持哪些安卓版本？**   
  答: 目前支持Android 6.0 以上的所有版本
  
* **在APP上登录安全吗? 会不会泄露我的密码？**   
  答: 本项目完全开源，欢迎检查代码，插件只会和iwara通信

* **有iOS版吗?**
  答: 没有, iOS应用管理严格，做了也上架不了 

## 🎨 主要技术栈
* MVVM 架构
* 单Activity + 导航
* Jetpack Compose (构建UI)
* Kotlin Coroutine (协程)
* Okhttp + Jsoup (解析网页)
* Retrofit (访问Restful API)
* Hilt (依赖注入)
* Paging3 (分页加载)
* Navigation (导航)
