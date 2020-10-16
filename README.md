# Zbot

本项目基于[ProtobufBot](https://github.com/protobufbot)开发，使用`AGPLv3`开源，所有基于本项目（间接或直接）或参考了本项目实现的软件应使用`AGPLv3`开源

## 功能
- [x] 授权
- [x] 黑名单
- [x] 复读
- [x] web 登陆注册 
- [x] 开关
- [x] 入群欢迎
- [x] 生成魔方打乱
- [x] WCA 成绩查询
- [x] WCA 排名查询
- [x] 粗饼链接
- [x] 粗饼年度总结
- [ ] WCA成绩趋势图
- [ ] WCA群纪录
- [ ] WCA群排名
- [ ] 粗饼赛事查询
- [ ] 粗饼赛事选手查询
- [ ] 生成魔方图片
- [ ] 群管
- [ ] 学习
- [ ] 快递

## 使用方式
1. 下载 [Spring-Mirai-Client](https://github.com/ProtobufBot/Spring-Mirai-Client/releases)到服务器，并运行`java jar spring-mirai-client-版本.jar`，浏览器打开`http://服务器地址:9000`，输入管理账号密码(默认admin/123456)，登陆机器人并处理验证码
2. 打包 zbot 主程序，执行`gradle build`（需要JDK 1.8、Gradle、IDEA等环境）
3. 复制打包产物`build/libs/zbot-版本号.jar`到服务器
4. 创建`application-prod.yml`，配置 MySQL Redis service 等信息
5. 运行`java -jar zbot-版本号.jar`

## 注意
- 默认的 service 有 **QPS<=3** 限制，如果需要增加QPS，需要自己部署相关服务（TNOODLE、WCADS等），建议使用多台服务器分开进行部署
    - [WCADS](https://github.com/lz1998/wca-data-service) 需要4G内存，可能存在内存泄露
    - TNOODLE 需要500M内存
    - 其他服务加起来2G内存

## 其他
- 点击[这里](https://promotion.aliyun.com/ntms/yunparter/invite.html?userCode=a6mqitia)够买服务器