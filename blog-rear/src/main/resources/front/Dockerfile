# 使用官方 nginx 作为基础镜像
FROM nginx:alpine

# 删除默认 nginx 配置
RUN rm -rf /etc/nginx/conf.d/default.conf

# 拷贝自定义 nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 拷贝 dist 目录到 nginx html 目录
COPY dist/ /usr/share/nginx/html/

# 暴露端口 5173
EXPOSE 5173

# 启动 nginx
CMD ["nginx", "-g", "daemon off;"]
