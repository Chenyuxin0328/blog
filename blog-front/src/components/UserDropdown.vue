<template>
  <div class="user-dropdown" v-click-outside="hideDropdown">
    <!-- 触发按钮 -->
    <div class="user-trigger" @click="toggleDropdown">
      <el-avatar
        :size="32"
        :src="currentUser.avatar"
        :default-src="defaultAvatar"
        class="user-avatar"
      />
      <span class="username">{{ currentUser.username || '匿名用户' }}</span>
      <i class="el-icon-arrow-down" :class="{ 'is-active': isVisible }"></i>
    </div>

    <!-- 下拉卡片 -->
    <div class="dropdown-card" v-show="isVisible">
      <div class="card-header">
        <el-avatar
          :size="60"
          :src="currentUser.avatar"
          :default-src="defaultAvatar"
          class="large-avatar"
        />
        <div class="user-info">
          <div class="name">{{ currentUser.username || '未知用户' }}</div>
          <div class="role">{{ currentUser.role === 'admin' ? '管理员' : '普通用户' }}</div>
        </div>
      </div>
      <div class="card-divider"></div>
      <div class="card-footer">
        <el-button type="danger" plain @click="handleLogout">
          退出登录
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import defaultAvatar from '@/assets/icons/default-avatar.png'

const router = useRouter()
const userStore = useUserStore()
const isVisible = ref(false)

// 获取当前用户信息
const currentUser = computed(() => {
  const userInfo = userStore.userInfo
  return userInfo?.user || {}
})

// 切换下拉框显示状态
const toggleDropdown = () => {
  isVisible.value = !isVisible.value
}

// 隐藏下拉框
const hideDropdown = () => {
  isVisible.value = false
}

// 处理退出登录
const handleLogout = async () => {
  try {
    await userStore.logout()
    ElMessage.success('退出登录成功')
    router.push('/login')
  } catch (error) {
    console.error('退出登录失败:', error)
    ElMessage.error('退出登录失败')
  }
}

// 自定义指令：点击外部关闭
const vClickOutside = {
  mounted(el, binding) {
    el._clickOutside = (event) => {
      if (!(el === event.target || el.contains(event.target))) {
        binding.value(event)
      }
    }
    document.addEventListener('click', el._clickOutside)
  },
  unmounted(el) {
    document.removeEventListener('click', el._clickOutside)
  }
}
</script>

<style scoped>
.user-dropdown {
  position: relative;
  display: inline-block;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.user-trigger:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

.user-avatar {
  border: 2px solid rgba(255, 255, 255, 0.6);
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.user-trigger:hover .user-avatar {
  transform: scale(1.2) rotate(5deg);
  border-color: #ff7eb9;
  box-shadow: 0 4px 15px rgba(255, 126, 185, 0.3);
}

.username {
  color: #333;
  font-weight: 600;
  font-size: 15px;
}

.el-icon-arrow-down {
  font-size: 12px;
  color: #666;
  transition: transform 0.3s ease;
}

.el-icon-arrow-down.is-active {
  transform: rotate(180deg);
}

.dropdown-card {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  width: 280px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  overflow: hidden;
  animation: slideDown 0.3s ease;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card-header {
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  background: linear-gradient(135deg, #fff5f7, #ffe0eb);
}

.large-avatar {
  border: 3px solid white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.large-avatar:hover {
  transform: scale(1.1) rotate(5deg);
  border-color: #ff7eb9;
  box-shadow: 0 8px 25px rgba(255, 126, 185, 0.4);
}

.user-info {
  flex: 1;
}

.name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.role {
  font-size: 13px;
  color: #666;
  background: rgba(0, 0, 0, 0.05);
  padding: 2px 8px;
  border-radius: 10px;
  display: inline-block;
}

.card-divider {
  height: 1px;
  background: #eee;
  margin: 0;
}

.card-footer {
  padding: 16px;
  display: flex;
  justify-content: center;
}

.card-footer .el-button {
  width: 80%;
  height: 36px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 18px;
  background: linear-gradient(135deg, #ffc2cf, #ffaed2);
  border: none;
  color: white;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  overflow: hidden;
}

.card-footer .el-button:hover {
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 8px 20px rgba(255, 126, 185, 0.2);
  background: linear-gradient(135deg, #ffb6c1, #ffa5c9);
}

.card-footer .el-button:active {
  transform: translateY(0) scale(0.98);
}

.card-footer .el-button::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    45deg,
    transparent,
    rgba(255, 255, 255, 0.3),
    transparent
  );
  transform: rotate(45deg);
  animation: buttonShine 2s infinite;
  pointer-events: none;
}

@keyframes buttonShine {
  0% {
    transform: translate(-50%, -50%) rotate(45deg);
  }
  100% {
    transform: translate(150%, 150%) rotate(45deg);
  }
}

.el-dropdown-menu {
  border-radius: 16px !important;
  padding: 8px !important;
  background: rgba(255, 255, 255, 0.95) !important;
  backdrop-filter: blur(10px) !important;
  border: 2px solid #ffd6db !important;
  box-shadow: 0 8px 32px rgba(255, 126, 185, 0.15) !important;
  transform-origin: top right !important;
  animation: dropdownAppear 0.3s cubic-bezier(0.34, 1.56, 0.64, 1) !important;
}

.el-dropdown-menu__item {
  padding: 12px 16px !important;
  border-radius: 8px !important;
  margin: 4px !important;
  font-size: 14px !important;
  color: #333 !important;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1) !important;
  position: relative !important;
  overflow: hidden !important;
}

.el-dropdown-menu__item:hover {
  background: linear-gradient(135deg, #fff1f5, #ffe4ea) !important;
  color: #ff4d8d !important;
  transform: translateX(4px) !important;
}

.el-dropdown-menu__item::before {
  content: '' !important;
  position: absolute !important;
  top: 0 !important;
  left: -100% !important;
  width: 100% !important;
  height: 100% !important;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.4),
    transparent
  ) !important;
  transition: 0.5s !important;
}

.el-dropdown-menu__item:hover::before {
  left: 100% !important;
}

.avatar-container {
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  transform-origin: center;
  position: relative;
}

.avatar-container:hover {
  transform: scale(1.1);
}

.avatar-container::after {
  content: '';
  position: absolute;
  top: -4px;
  left: -4px;
  right: -4px;
  bottom: -4px;
  border-radius: 50%;
  background: linear-gradient(45deg, #ffaed2, #ff7eb9);
  z-index: -1;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.avatar-container:hover::after {
  opacity: 1;
}

@keyframes dropdownAppear {
  from {
    opacity: 0;
    transform: scale(0.9) translateY(-10px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}
</style>
