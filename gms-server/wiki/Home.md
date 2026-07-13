# BeiDou (北斗) 冒险岛服务端 Wiki

欢迎来到BeiDou（北斗）冒险岛私服服务端的Wiki文档！

## 项目简介

**BeiDou（北斗）** 是一个基于Java开发的开源MapleStory（冒险岛）v83版本私服服务端，基于[Cosmic](https://github.com/P0nk/Cosmic)项目进行汉化和优化而来。项目以中国自主研发的北斗卫星导航系统命名，寓意做更优秀、更强大的冒险岛服务端。

## 核心特性

- 🎮 **完整的游戏服务端** - 支持冒险岛v83版本客户端
- 🌐 **Web管理后台** - 内置Spring Boot提供RESTful API管理接口
- 🔌 **高性能网络层** - 基于Netty实现的高性能网络通信
- 📜 **JavaScript脚本支持** - 使用GraalVM JS引擎，支持NPC、任务、事件等脚本化开发
- 🌍 **多语言支持** - 支持中文/英文等多语言切换
- 🗄️ **自动数据库初始化** - 首次启动自动创建数据库并执行迁移脚本
- 📖 **Swagger API文档** - 内置API文档，方便开发调试
- 🔐 **JWT认证** - Web API采用Spring Security + JWT进行身份认证
- 🔧 **GM命令系统** - 完善的GM管理命令

## 快速导航

### 新手指南
- [快速开始](https://github.com/BeiDouMS/BeiDou-Server/wiki/快速开始) - 环境搭建、服务端启动
- [项目架构](https://github.com/BeiDouMS/BeiDou-Server/wiki/项目架构) - 了解整体架构设计
- [目录结构](https://github.com/BeiDouMS/BeiDou-Server/wiki/目录结构) - 项目文件结构说明

### 开发者文档
- [核心模块详解](https://github.com/BeiDouMS/BeiDou-Server/wiki/核心模块详解) - 各核心模块详细说明
- [二次开发指南](https://github.com/BeiDouMS/BeiDou-Server/wiki/二次开发指南) - 如何进行二次开发
- [脚本开发指南](https://github.com/BeiDouMS/BeiDou-Server/wiki/脚本开发指南) - NPC/任务/事件脚本开发
- [API接口文档](https://github.com/BeiDouMS/BeiDou-Server/wiki/API接口文档) - Web管理API说明
- [数据库设计](https://github.com/BeiDouMS/BeiDou-Server/wiki/数据库设计) - 数据库表结构说明

### 进阶内容
- [网络协议详解](https://github.com/BeiDouMS/BeiDou-Server/wiki/网络协议详解) - 冒险岛通信协议
- [WZ资源解析](https://github.com/BeiDouMS/BeiDou-Server/wiki/WZ资源解析) - 游戏资源加载机制
- [扩展开发方向](https://github.com/BeiDouMS/BeiDou-Server/wiki/扩展开发方向) - 未来可开发功能
- [常见问题](https://github.com/BeiDouMS/BeiDou-Server/wiki/常见问题) - FAQ

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 21 | 开发语言 |
| Spring Boot | 3.2.3 | Web框架 |
| Netty | 4.1.109 | 网络通信 |
| MyBatis-Flex | 1.8.9 | ORM框架 |
| Druid | 1.2.22 | 数据库连接池 |
| Flyway | 9.15.2 | 数据库版本迁移 |
| GraalVM JS | 24.0.1 | JavaScript脚本引擎 |
| MySQL | 8.0+ | 数据库 |
| Lombok | 1.18.30 | 代码简化 |
| SpringDoc/Swagger | 2.5.0 | API文档 |

## 项目起源

> 北斗卫星导航系统（BDS）是中国自行研制的全球卫星导航系统，也是继GPS、GLONASS之后的第三个成熟的卫星导航系统。北斗这一词对于中国来说，有着特殊的意义。既然小伙伴说这个项目也要整个天体的名字，想了半天，就叫北斗好了！这也意味着我们要做的比HeavenMS和Cosmic更加优秀和强大！

## 开源协议

本项目基于GNU Affero General Public License v3.0开源协议。
