<script setup>
import Header from './components/Header.vue';
import { onMounted } from 'vue';

// 页面加载完成后添加动画类
onMounted(() => {
  // 给body添加已加载标记，用于触发动画
  document.body.classList.add('content-loaded');
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
  background: linear-gradient(to right bottom, #ffffff 0%, #ffd6dd 100%);
  color: #333;
  line-height: 1.6;
  min-height: 100vh;
}

.app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-content {
  flex-grow: 1;
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
