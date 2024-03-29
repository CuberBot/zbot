# Zbot

本项目基于[ProtobufBot](https://github.com/protobufbot)开发，使用`AGPLv3`开源，所有基于本项目（间接或直接）或参考了本项目实现的软件应使用`AGPLv3`开源。推荐配合 [Go-Mirai-Client](https://github.com/ProtobufBot/Go-Mirai-Client/releases) 使用

## 功能
- [x] 授权 Auth
- [x] 黑名单 Black
- [x] 个人永久屏蔽 PBlack
- [x] 复读 Repeat
- [x] web 登陆注册 Security
- [x] 开关 Switch
- [x] 入群欢迎 Welcome
- [x] 生成魔方打乱 Scramble
- [x] 生成魔方多打乱 Tnos
- [x] 生成魔方中文打乱 Scramble
- [x] WCA 成绩查询 Wca
- [x] WCA 排名查询 Rank
- [x] 粗饼链接 Link
- [x] 粗饼年度总结 Summary
- [x] 粗饼赛事查询 Comp
- [x] 粗饼赛事选手查询 Cuber
- [x] 生成魔方图片 Cubepic
- [x] WCA成绩趋势图 Trend
- [x] 学习 Learn
- [x] 快递 Express
- [ ] WCA群纪录 GroupRecord
- [ ] WCA群排名 GroupRank
- [x] 群管 Admin

## 使用方式
1. 下载 [Go-Mirai-Client](https://github.com/ProtobufBot/Go-Mirai-Client/releases) / [Spring-Mirai-Client](https://github.com/ProtobufBot/Spring-Mirai-Client/releases) 到服务器，并运行 `chmod +x Go-Mirai-Client && ./Go-Mirai-Client` / `java jar spring-mirai-client-版本.jar` ，浏览器打开`http://服务器地址:9000`，输入管理账号密码(默认admin/123456)，登陆机器人并处理验证码
2. 打包 zbot 主程序，执行`gradle build`（需要JDK 1.8、Gradle、IDEA等环境）
3. 复制打包产物`build/libs/zbot-版本号.jar`到服务器
4. 创建`application-prod.yml`，配置 MySQL Redis service 等信息
5. 运行`java -jar zbot-版本号.jar`


## 衍生软件需声明引用

- 若使用 zbot 的软件包而不修改 zbot，则衍生项目需在描述的任意部位提及使用 zbot，并保持`JoinGroupPlugin`开启，告知用户本仓库地址(`https://github.com/lz1998/zbot`)。
- 若修改 zbot 源代码再发布，**或参考 zbot 内部实现发布另一个项目**，则衍生项目必须在**文章首部**或 'zbot' 相关内容**首次出现**的位置**明确声明**来源于本仓库 (`https://github.com/lz1998/zbot`)。不得扭曲或隐藏免费且开源的事实。

## 注意
- 默认的 service 有 **QPS<=5** 限制，如果需要增加QPS，需要自己部署相关服务（TNOODLE、WCADS等），建议使用多台服务器分开进行部署
    - [WCADS](https://github.com/lz1998/wca-data-service) 需要4G内存，可能存在内存泄露
    - TNOODLE 需要500M内存
    - 其他服务加起来2G内存

## 其他
- 点击[这里](https://promotion.aliyun.com/ntms/yunparter/invite.html?userCode=a6mqitia)购买服务器
