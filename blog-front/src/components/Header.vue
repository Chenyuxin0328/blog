<template>
  <header v-if="shouldShowHeader" class="header">
    <div class="logo">
      <router-link to="/">
        <img src="@/assets/icons/logo.png" alt="Logo" class="logo-icon" />
        <span class="title">Code&Life</span>
      </router-link>
    </div>
    <nav class="nav">
      <router-link to="/friends" class="nav-item">
        <img src="@/assets/icons/friends.png" alt="朋友圈" class="nav-icon" />
        朋友圈
      </router-link>
      <router-link to="/docs" class="nav-item">
        <img src="@/assets/icons/docs.png" alt="技术文档" class="nav-icon" />
        技术文档
      </router-link>
      <router-link to="/about" class="nav-item">
        <img src="@/assets/icons/about.png" alt="关于我" class="nav-icon" />
        关于我
      </router-link>
    </nav>
  </header>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();

// 在首页(LandingPage)不显示头部
const shouldShowHeader = computed(() => {
  return route.path !== '/';
});
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 16px; /* 更紧凑 */
  background: linear-gradient(to top, rgba(255, 200, 200, 0.7), rgba(255, 255, 255, 0.7));
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(8px);
  border-radius: 20px;
  margin: 10px;
  border-bottom: 2px solid rgba(255, 150, 150, 0.5);
}

.logo {
  display: flex;
  align-items: center;
}

.logo a {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: #333;
  font-size: 1.5rem;
  font-weight: 900;
  transform: scale(1.1, 1.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 5px 10px;
  border-radius: 8px;
}

.logo a:hover {
  transform: scale(1.15, 1.15);
  background: rgba(255, 174, 210, 0.2);
}

.logo a:hover .logo-icon {
  transform: rotate(15deg);
}

.logo-icon {
  height: 40px;
  margin-right: 10px;
  transition: transform 0.3s ease;
}

.title {
  font-weight: 600;
  background: linear-gradient(45deg, #ff69b4, #ff1493);
  background-clip: text;
  -webkit-background-clip: text;
  color: transparent;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.nav {
  display: flex;
  gap: 20px;
}

.nav-item {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: #333;
  padding: 8px 15px;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 1.2rem;
  font-weight: 600;
  position: relative;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.1);
}

.nav-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(45deg, rgba(255, 174, 210, 0.2), rgba(255, 105, 180, 0.2));
  opacity: 0;
  transition: opacity 0.3s ease;
}

.nav-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(255, 105, 180, 0.2);
}

.nav-item:hover::before {
  opacity: 1;
}

.nav-item:hover .nav-icon {
  transform: scale(1.2) rotate(10deg);
}

.nav-item.router-link-active {
  color: #ff1493;
  font-weight: bold;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 2px 8px rgba(255, 105, 180, 0.3);
}

.nav-icon {
  height: 25px;
  margin-right: 8px;
  transition: all 0.3s ease;
}

@media (max-width: 768px) {
  .header {
    flex-direction: column;
    gap: 15px;
    padding: 15px;
  }

  .nav {
    width: 100%;
    justify-content: space-around;
    gap: 10px;
  }

  .nav-item {
    padding: 8px 12px;
    font-size: 1rem;
  }
}
</style>
