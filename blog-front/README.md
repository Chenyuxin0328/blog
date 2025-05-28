# Code&Life 博客前端项目

基于Vue 3构建的个人技术博客前端项目，UI设计简洁现代，响应式布局。

## 项目功能

- 首页文章列表展示
- 技术分类浏览
- 搜索功能
- 朋友圈页面
- 技术文档页面
- 关于我页面

## 图标文件

项目需要以下图标文件，请将它们放在 `src/assets/icons` 目录下：

1. `logo.png` - 网站Logo图标
2. `friends.png` - 朋友圈图标
3. `docs.png` - 文档图标 
4. `about.png` - 关于我图标
5. `search.png` - 搜索图标
6. `category.png` - 分类图标
7. `recent.png` - 近期文章图标

## 项目设置

```sh
# 安装依赖
npm install

# 开发环境运行
npm run dev

# 生产环境构建
npm run build
```

## 技术栈

- Vue 3
- Vue Router
- Vite

## 目录结构

```
├── public/             # 静态资源
│   └── images/         # 图片资源
├── src/
│   ├── assets/         # 项目资源
│   │   ├── icons/      # 图标文件
│   │   └── images/     # 项目图片
│   ├── components/     # 组件
│   ├── router/         # 路由配置
│   ├── views/          # 页面视图
│   ├── App.vue         # 根组件
│   └── main.js         # 入口文件
├── index.html          # HTML模板
└── package.json        # 项目配置
```

## Recommended IDE Setup

[VSCode](https://code.visualstudio.com/) + [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur).

## Customize configuration

See [Vite Configuration Reference](https://vite.dev/config/).
