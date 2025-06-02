# 创建管理端布局
<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <h2>
          <img src="@/assets/admin/home.png" alt="首页" class="header-icon" />
          管理后台
        </h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        router
        :background-color="'#fff5f7'"
        :text-color="'#333'"
        :active-text-color="'#333'"
      >
        <el-menu-item index="/admin/friends">
          <img src="@/assets/admin/friends.png" alt="朋友圈" class="menu-icon" />
          <span>朋友圈管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/docs">
          <img src="@/assets/admin/doc.png" alt="文档" class="menu-icon" />
          <span>技术文档管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <img src="@/assets/admin/user.png" alt="用户" class="menu-icon" />
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>
      <div class="sidebar-footer">
        <span>🌸 Code & Life 后台</span>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="content">
      <div class="content-header">
        <el-breadcrumb>
          <el-breadcrumb-item>
            <img src="@/assets/admin/home.png" alt="首页" class="breadcrumb-icon" />
            管理后台
          </el-breadcrumb-item>
          <el-breadcrumb-item>
            <img
              v-if="route.path === '/admin/friends'"
              src="@/assets/admin/friends.png"
              alt="朋友圈"
              class="breadcrumb-icon"
            />
            <img
              v-else-if="route.path === '/admin/docs'"
              src="@/assets/admin/doc.png"
              alt="文档"
              class="breadcrumb-icon"
            />
            <img
              v-else-if="route.path === '/admin/users'"
              src="@/assets/admin/user.png"
              alt="用户"
              class="breadcrumb-icon"
            />
            {{ currentPageTitle }}
          </el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <div class="content-body">
        <router-view v-slot="{ Component }">
          <transition 
            name="fade-slide" 
            mode="out-in"
            appear
          >
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

// 当前激活的菜单项
const activeMenu = computed(() => route.path)

// 当前页面标题
const currentPageTitle = computed(() => {
  const pathMap = {
    '/admin/friends': '朋友圈管理',
    '/admin/docs': '技术文档管理',
    '/admin/users': '用户管理'
  }
  return pathMap[route.path] || '管理后台'
})
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: calc(100vh - 76px);
  margin: 8px 16px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(255, 154, 158, 0.1);
  position: relative;
  z-index: 1;
  overflow: hidden;
}

.sidebar {
  width: 240px;
  border-right: 1px solid #ffe4e6;
  display: flex;
  flex-direction: column;
  background: #fff5f7;
}

.sidebar-header {
  padding: 24px 20px;
  border-bottom: 1px solid #ffe4e6;
  background: linear-gradient(135deg, #fff5f7 0%, #ffd6dd 100%);
}

.sidebar-header h2 {
  margin: 0;
  font-size: 1.5rem;
  color: #333;
  text-align: center;
  font-weight: 650;
  letter-spacing: 1px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 8px 0;
}

.sidebar-header h2:hover {
  transform: translateY(-2px);
  color: #333;
}

.sidebar-header h2:hover .header-icon {
  transform: rotate(10deg) scale(1.1);
}

.header-icon {
  width: 30px;
  height: 30px;
  vertical-align: middle;
  transition: all 0.3s ease;
}

.sidebar-menu {
  border-right: none;
  flex: 1;
}

.sidebar-menu :deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  margin: 8px 0;
  border-radius: 0 25px 25px 0;
  margin-right: 16px;
  transition: all 0.3s ease;
  overflow: hidden;
  position: relative;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, #ffd6dd 0%, #fff5f7 100%);
  color: #333 !important;
  font-weight: 700;
}

.sidebar-menu :deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, rgba(255, 214, 221, 0.8) 0%, rgba(255, 245, 247, 0.8) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.sidebar-menu :deep(.el-menu-item.is-active:hover::before) {
  opacity: 1;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background: #ffeef0 !important;
  transform: translateX(5px);
  color: #333 !important;
  box-shadow: 0 4px 12px rgba(255, 182, 193, 0.2);
}

.sidebar-menu :deep(.el-menu-item:hover .menu-icon) {
  transform: rotate(10deg) scale(1.1);
}

.sidebar-menu :deep(.el-menu-item) span {
  font-size: 1.1rem;
  font-weight: 600;
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
}

.sidebar-footer {
  padding: 16px;
  text-align: center;
  color: #333;
  font-size: 0.9rem;
  border-top: 1px solid #ffe4e6;
  background: #fff5f7;
  cursor: pointer;
  transition: all 0.3s ease;
}

.sidebar-footer:hover {
  background: #ffeef0;
  color: #333;
  transform: translateY(-2px);
}

.content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #fff;
}

.content-header {
  padding: 20px;
  border-bottom: 1px solid #ffe4e6;
  background: #fff;
}

.content-header :deep(.el-breadcrumb__item) {
  display: flex;
  align-items: center;
  font-size: 1rem;
  font-weight: 600;
  color: #333 !important;
  transition: all 0.3s ease;
}

.content-header :deep(.el-breadcrumb__item:hover) {
  transform: scale(1.05);
}

.content-header :deep(.el-breadcrumb__inner) {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #333 !important;
  font-weight: 600 !important;
}

.content-header :deep(.el-breadcrumb__separator) {
  color: #333;
  margin: 0 12px;
  font-weight: 600;
}

.breadcrumb-icon {
  width: 24px;
  height: 24px;
  vertical-align: middle;
  transition: all 0.3s ease;
}

.content-header :deep(.el-breadcrumb__item:hover) .breadcrumb-icon {
  transform: rotate(10deg) scale(1.1);
}

.content-body {
  flex: 1;
  padding: 20px;
  overflow: hidden;
  background: #fff;
  position: relative;
}

.content-body > div {
  height: 100%;
  width: 100%;
  position: relative;
}

.content-body :deep(*::-webkit-scrollbar) {
  width: 8px;
}

.content-body :deep(*::-webkit-scrollbar-track) {
  background: #fff5f7;
  border-radius: 4px;
}

.content-body :deep(*::-webkit-scrollbar-thumb) {
  background: #ffd6dd;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.content-body :deep(*::-webkit-scrollbar-thumb:hover) {
  background: #ff9a9e;
}

.menu-icon {
  width: 20px;
  height: 20px;
  margin-right: 8px;
  vertical-align: middle;
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
}

/* 添加路由过渡动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(30px) scale(0.95);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-30px) scale(0.95);
}
</style>
