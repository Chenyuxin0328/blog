# 个人博客系统

基于Vue3和SpringBoot3开发的现代化个人博客系统。

## 项目介绍

这是一个前后端分离的个人博客系统，采用现代化的技术栈和架构设计，提供博客内容管理、用户交互等功能。

## 在线访问

- 前端访问地址：[http://115.190.94.156:5173/](http://115.190.94.156:5173/)

## 技术栈

### 前端 (blog-front)
- Vue 3
- TypeScript
- Vue Router
- Pinia
- Element Plus
- Vite

### 后端 (blog-rear)
- Spring Boot 3
- MyBatis-Plus
- MySQL
- Minio

## 项目结构

```
blog/
├── blog-front/        # 前端项目
└── blog-rear/         # 后端项目
```

## 开发环境要求
- Node.js 18+
- JDK 17+
- MySQL 8.0+


## 本地开发

### 前端项目启动
```bash
cd blog-front
npm install
npm run dev
```

### 后端项目启动
```bash
cd blog-rear
./mvnw spring-boot:run
```

## 功能特性

- 📝 文章管理：支持Markdown编辑器
- 🏷️ 分类管理：文章分类和标签管理
- 🔍 全文检索：支持文章内容搜索
- 💬 评论系统：支持文章评论和回复
- 👤 用户系统：支持用户注册、登录
- 📊 仪表盘：访问统计和内容管理

## 贡献指南

欢迎提交问题和改进建议！

## 许可证

MIT License
