<script setup>
import Header from './components/Header.vue';
import { onMounted } from 'vue';

// 页面加载完成后添加动画类
onMounted(() => {
  // 给body添加已加载标记，用于触发动画
  document.body.classList.add('content-loaded');
  // 设置默认缩放比例为80%
  document.body.style.zoom = '80%';
});
</script>

<template>
  <div class="app">
    <Header />
    <router-view v-slot="{ Component }">
      <transition 
        name="page-transition" 
        mode="out-in"
        appear
      >
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<style>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
  background: linear-gradient(135deg, #ffffff 0%, #ffd6dd 100%) fixed;
  background-size: cover;
  color: #333;
  line-height: 1.6;
  min-height: 100vh;
}

.app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  position: relative;
}

/* 添加一个半透明的遮罩层，让内容更容易阅读 */
.app::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(10px);
  z-index: 0;
  pointer-events: none;
}

.main-content {
  flex-grow: 1;
  position: relative;
  z-index: 1;
  padding: 20px;
  margin-top: 4px; /* 减小与header的间距 */
  overflow-y: auto;
  min-height: calc(100vh - 80px);
}

a {
  color: #3498db;
  text-decoration: none;
}

a:hover {
  text-decoration: underline;
}

h1, h2, h3, h4, h5, h6 {
  font-weight: 600;
  margin-bottom: 0.5rem;
}

button {
  cursor: pointer;
}

/* 页面过渡动画 */
.page-transition-enter-active,
.page-transition-leave-active {
  transition: opacity 0.5s ease, transform 0.5s ease;
}

.page-transition-enter-from,
.page-transition-leave-to {
  opacity: 0;
  transform: translateY(20px);
}
</style>
