<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-box" :class="{ 'user-matched': matchedUser && activeTab === 'login' }">
        <div class="tabs">
          <div
            :class="['tab', { active: activeTab === 'login' }]"
            @click="activeTab = 'login'"
          >
            登录
          </div>
          <div
            :class="['tab', { active: activeTab === 'register' }]"
            @click="activeTab = 'register'"
          >
            注册
          </div>
        </div>

        <div class="form-display-area">
          <div class="form-transition-wrapper">
            <Transition mode="out-in">
              <!-- 登录表单 -->
              <el-form v-if="activeTab === 'login'" @submit.prevent="handleLogin" class="form login-form" :model="loginForm">
                <el-form-item class="form-item">
                  <div class="input-wrapper">
                    <label :class="['floating-label', { 'is-float': loginForm.username || isFocused.username }]">
                      用户名
                    </label>
                    <el-input
                      v-model="loginForm.username"
                      @input="searchUserByUsername"
                      @focus="handleFocus('username')"
                      @blur="handleBlur('username')"
                    />
                  </div>
                </el-form-item>
                <el-form-item class="form-item">
                  <div class="input-wrapper">
                    <label :class="['floating-label', { 'is-float': loginForm.password || isFocused.password }]">
                      密码
                    </label>
                    <el-input
                      v-model="loginForm.password"
                      type="password"
                      show-password
                      @focus="handleFocus('password')"
                      @blur="handleBlur('password')"
                    />
                  </div>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" native-type="submit" :loading="loading" class="submit-btn">
                    登录
                  </el-button>
                </el-form-item>
              </el-form>

              <!-- 注册表单 -->
              <el-form v-else @submit.prevent="handleRegister" class="form register-form" :model="registerForm">
                <el-form-item class="form-item">
                  <div class="input-wrapper">
                    <label :class="['floating-label', { 'is-float': registerForm.username || isFocused.regUsername }]">
                      用户名
                    </label>
                    <el-input
                      v-model="registerForm.username"
                      @focus="handleFocus('regUsername')"
                      @blur="handleBlur('regUsername')"
                    />
                  </div>
                </el-form-item>
                <el-form-item class="form-item">
                  <div class="input-wrapper">
                    <label :class="['floating-label', { 'is-float': registerForm.password || isFocused.regPassword }]">
                      密码
                    </label>
                    <el-input
                      v-model="registerForm.password"
                      type="password"
                      show-password
                      @focus="handleFocus('regPassword')"
                      @blur="handleBlur('regPassword')"
                    />
                  </div>
                </el-form-item>
                <el-form-item class="form-item">
                  <div class="input-wrapper">
                    <label :class="['floating-label', { 'is-float': registerForm.confirmPassword || isFocused.confirmPassword }]">
                      确认密码
                    </label>
                    <el-input
                      v-model="registerForm.confirmPassword"
                      type="password"
                      show-password
                      @focus="handleFocus('confirmPassword')"
                      @blur="handleBlur('confirmPassword')"
                    />
                  </div>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" native-type="submit" :loading="loading" class="submit-btn">
                    注册
                  </el-button>
                </el-form-item>
              </el-form>
            </Transition>
          </div>
        </div>
      </div>

      <!-- 把头像移到这里，login-box外面 -->
      <div v-if="matchedUser && activeTab === 'login'" class="matched-user-profile">
        <img :src="matchedUser.avatar" alt="User Avatar" class="profile-avatar" />
        <span class="profile-username">{{ matchedUser.username }}</span>
      </div>
    </div>

    <!-- 头像选择弹窗 -->
    <el-dialog
      v-model="showAvatarDialog"
      title="选择你的头像"
      width="70%"
      :show-close="false"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <div class="avatar-selection-container">
        <div class="avatar-grid">
          <div
            v-for="(avatar, index) in avatarList"
            :key="index"
            class="avatar-item"
            :class="{ 'selected': selectedAvatarIndex === index }"
            @click="selectAvatar(index)"
          >
            <img :src="avatar.url" :alt="avatar.name" class="avatar-option" />
            <div class="avatar-name">{{ avatar.name }}</div>
          </div>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="confirmAvatarSelection" :disabled="selectedAvatarIndex === null">
            确认选择
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeTab = ref('login')
const loading = ref(false)
const matchedUser = ref(null)

const isFocused = reactive({
  username: false,
  password: false,
  regUsername: false,
  regPassword: false,
  confirmPassword: false
})

const handleFocus = (field) => {
  isFocused[field] = true
}

const handleBlur = (field) => {
  isFocused[field] = false
}

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const showAvatarDialog = ref(false)
const selectedAvatarIndex = ref(null)

// 使用 Vite 的动态导入
const avatarList = ref([
  { url: new URL('../assets/head/a1.png', import.meta.url).href, name: '边牧' },
  { url: new URL('../assets/head/a2.png', import.meta.url).href, name: '白猫' },
  { url: new URL('../assets/head/a3.png', import.meta.url).href, name: '布偶猫' },
  { url: new URL('../assets/head/a4.png', import.meta.url).href, name: '仓鼠' },
  { url: new URL('../assets/head/a5.png', import.meta.url).href, name: '藏獒' },
  { url: new URL('../assets/head/a6.png', import.meta.url).href, name: '柴犬' },
  { url: new URL('../assets/head/a7.png', import.meta.url).href, name: '法斗' },
  { url: new URL('../assets/head/a8.png', import.meta.url).href, name: '哈士奇' },
  { url: new URL('../assets/head/a9.png', import.meta.url).href, name: '荷兰猪' },
  { url: new URL('../assets/head/a10.png', import.meta.url).href, name: '黑猫' },
  { url: new URL('../assets/head/a11.png', import.meta.url).href, name: '金毛' },
  { url: new URL('../assets/head/a12.png', import.meta.url).href, name: '橘猫' },
  { url: new URL('../assets/head/a13.png', import.meta.url).href, name: '柯基' },
  { url: new URL('../assets/head/a14.png', import.meta.url).href, name: '可达鸭' },
  { url: new URL('../assets/head/a15.png', import.meta.url).href, name: '腊肠犬' },
  { url: new URL('../assets/head/a16.png', import.meta.url).href, name: '蓝猫' },
  { url: new URL('../assets/head/a17.png', import.meta.url).href, name: '奶牛猫' },
  { url: new URL('../assets/head/a18.png', import.meta.url).href, name: '三花猫' },
  { url: new URL('../assets/head/a19.png', import.meta.url).href, name: '田园犬' },
  { url: new URL('../assets/head/a20.png', import.meta.url).href, name: '无毛猫' },
  { url: new URL('../assets/head/a21.png', import.meta.url).href, name: '暹罗猫' },
  { url: new URL('../assets/head/a22.png', import.meta.url).href, name: '羊' }
])

const searchUserByUsername = async () => {
  if (!loginForm.username) {
    matchedUser.value = null
    return
  }
  // 模拟 API 调用 - 在真实项目中，你需要替换为真实的后端 API 调用
  const usernameLower = loginForm.username.toLowerCase();

  if (usernameLower === 'admin') {
    matchedUser.value = {
      username: 'admin',
      avatar: 'https://via.placeholder.com/150/FFC0CB/000000?Text=Admin' // 示例头像
    }
  } else if (usernameLower === 'testuser') {
    matchedUser.value = {
      username: 'testuser',
      avatar: 'https://via.placeholder.com/150/ADD8E6/000000?Text=Test' // 另一个示例头像
    }
  } else if (usernameLower === 'chenyuxin') {
    try {
      // 动态导入本地图片
      // Vite 会处理这个路径，确保在构建时图片被正确包含
      // 注意：如果图片查找失败，import() 会抛出错误
      const imageUrl = new URL('@/assets/icons/user.png', import.meta.url).href;
      matchedUser.value = {
        username: 'chenyuxin',
        avatar: imageUrl
      };
    } catch (e) {
      console.error("Failed to load local avatar for chenyuxin:", e);
      // 可以设置一个备用头像或清除头像
      matchedUser.value = null;
    }
  } else {
    matchedUser.value = null
  }
}

const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请填写完整的登录信息')
    return
  }

  loading.value = true
  try {
    // TODO: 调用登录 API
    await new Promise(resolve => setTimeout(resolve, 1000)) // 模拟 API 调用
    ElMessage.success('登录成功')
    router.push('/home')
  } catch (error) {
    ElMessage.error('登录失败，请重试')
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  if (!registerForm.username || !registerForm.password || !registerForm.confirmPassword) {
    ElMessage.warning('请填写完整的注册信息')
    return
  }

  if (registerForm.password !== registerForm.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }

  loading.value = true
  try {
    // TODO: 调用注册 API
    await new Promise(resolve => setTimeout(resolve, 1000)) // 模拟 API 调用
    ElMessage.success('注册成功，请选择你的头像')
    showAvatarDialog.value = true // 显示头像选择弹窗
  } catch (error) {
    ElMessage.error('注册失败，请重试')
  } finally {
    loading.value = false
  }
}

const selectAvatar = (index) => {
  selectedAvatarIndex.value = index
}

const confirmAvatarSelection = async () => {
  if (selectedAvatarIndex.value === null) return

  try {
    // TODO: 调用更新头像 API
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success('头像设置成功')
    showAvatarDialog.value = false
    activeTab.value = 'login'
    registerForm.username = ''
    registerForm.password = ''
    registerForm.confirmPassword = ''
    selectedAvatarIndex.value = null
  } catch (error) {
    ElMessage.error('头像设置失败，请重试')
  }
}
</script>

<style>
/* 全局样式，不使用 scoped */
html, body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100vh;
}

#app {
  height: 100vh;
}
</style>

<style scoped>
.login-page {
  width: 100%;
  height: calc(100vh - 80px);  /* 减去header的高度 */
  position: absolute;  /* 改为absolute */
  top: 80px;  /* header的高度 */
  left: 0;
  z-index: 1;  /* 确保不会覆盖header */
}

.login-container {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
  box-sizing: border-box;
}

.login-box {
  width: 100%;
  max-width: 360px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 24px;
  padding: 32px;
  padding-top: 20px;
  box-shadow: 0 10px 35px rgba(255, 182, 193, 0.35);
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: visible;
  position: relative;
  box-sizing: border-box;
}

.login-box:hover {
  transform: scale(1.05);
  box-shadow: 0 15px 45px rgba(255, 182, 193, 0.45);
}

.login-box:has(form:not(.login-form)) {
  max-width: 420px;
}

.tabs {
  display: flex;
  margin-bottom: 20px;
  position: relative;
  border: none;
}

.tab {
  flex: 1;
  text-align: center;
  padding: 12px;
  cursor: pointer;
  font-size: 22px;
  color: #999;
  transition: all 0.3s ease;
  border: none;
  position: relative;
}

.tab:hover {
  transform: scale(1.1);
  color: #ffb6c1;
}

.tab.active {
  color: #333;
  font-weight: 500;
}

.tab.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 2px;
  background: #ffb6c1;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 24px;
  opacity: 1;
  transform: translateX(0);
  transition: all 0.3s ease;
}

.form.v-enter-active,
.form.v-leave-active {
  transition: all 0.3s ease;
}

.form.v-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.form.v-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

.form-item {
  margin-bottom: 0;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
}

.input-wrapper {
  position: relative;
  padding-top: 16px;
}

.matched-avatar-container {
  position: absolute;
  top: -30px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 2;
  background: white;
  border-radius: 50%;
  padding: 3px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.matched-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
  display: block;
}

.floating-label {
  position: absolute;
  left: 0;
  padding-left: 3px;
  top: 25px;
  font-size: 18px;
  color: #999;
  transition: all 0.3s ease;
  background: transparent;
  pointer-events: none;
  z-index: 1;
}

.floating-label.is-float {
  top: -2px;
  font-size: 15px;
  color: #ffb6c1;
  background: rgba(255, 255, 255, 0.5);

  padding: 0 4px;
  margin-left: -4px;
}

:deep(.el-input__wrapper) {
  box-shadow: none !important;
  padding: 0;
  background: transparent;
  border: none;
}

:deep(.el-input__inner) {
  height: 36px !important;
  line-height: 36px;
  font-size: 20px;
  padding-top: 15px;
  color: #333;
}

:deep(.el-input__inner::placeholder) {
  color: transparent;
}

.submit-btn {
  margin-top: 8px;
  height: 50px;
  border-radius: 22px;
  font-size: 20px;
  font-weight: 700;
  background: #ffb6c1;
  border: none;
  color: white;
  transition: all 0.3s ease;
  width: 100%;
}

.submit-btn:hover {
  opacity: 0.9;
  transform: translateY(-2px);
}

:deep(.el-input__inner:focus) {
  outline: none;
}

:deep(.el-input.is-focus) {
  border-color: #ffb6c1;
}

.form-display-area {
  display: flex;
  align-items: flex-start;
  width: 100%;
  position: relative;
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.form-transition-wrapper {
  width: 100%;
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.matched-user-profile {
  position: absolute;
  left: 50%;
  margin-left: 260px;
  top: 48%;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 140px;
  visibility: hidden;
  z-index: 10;
}

/* 显示容器 */
.user-matched ~ .matched-user-profile {
  visibility: visible;
}

/* 表单移动动画 */
.user-matched.login-box {
  transform: translateX(-120px) !important;
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.profile-avatar {
  margin-left: -100px;
  width: 150px;
  height: 150px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid white;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  opacity: 0;
  transform: scale(0.3);
  filter: blur(10px);
  cursor: pointer;
  transition: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.user-matched ~ .matched-user-profile .profile-avatar {
  opacity: 1;
  transform: scale(1);
  filter: blur(0);
  animation: avatarAppear 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.user-matched ~ .matched-user-profile .profile-avatar:hover {
  transform: scale(1.1);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.profile-username {
  margin-left: -100px;
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-top: 16px;
  text-align: center;
  opacity: 0;
  transform: translateY(20px);
  white-space: nowrap;
  cursor: pointer;
  transition: all 0.3s ease;
}

.user-matched ~ .matched-user-profile .profile-username {
  opacity: 1;
  transform: translateY(0);
  animation: usernameAppear 0.5s cubic-bezier(0.34, 1.56, 0.64, 1) 0.2s both;
}

.user-matched ~ .matched-user-profile .profile-username:hover {
  transform: scale(1.1);
  color: #ffb6c1;
}

/* 定义动画关键帧 */
@keyframes avatarAppear {
  0% {
    opacity: 0;
    transform: scale(0.3) translateX(50px);
    filter: blur(10px);
  }
  100% {
    opacity: 1;
    transform: scale(1) translateX(0);
    filter: blur(0);
  }
}

@keyframes usernameAppear {
  0% {
    opacity: 0;
    transform: translateY(20px);
    filter: blur(5px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
    filter: blur(0);
  }
}

/* Ensure the Transition component itself has a class for targeting */
.form-display-area > .form-transition-wrapper > .v-transition {
  width: 100%;
}

/* Remove old matched-avatar styles if they still exist */
.matched-avatar-container, .matched-avatar {
  display: none !important;
}

/* Adjustments for the floating label when form shifts */
.form-display-area.user-matched .floating-label {
  /* Potentially adjust label positioning if needed after shift */
}

.avatar-selection-container {
  padding: 20px;
}

.avatar-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 24px;
  max-height: 60vh;
  overflow-y: auto;
  padding: 16px;
}

.avatar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 10px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  border: 3px solid transparent;
  background: white;
}

.avatar-item:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.avatar-item.selected {
  border-color: #ffb6c1;
  box-shadow: 0 0 0 3px rgba(255, 182, 193, 0.3);
}

.avatar-option {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 50%;
  border: 2px solid #f0f0f0;
  transition: all 0.3s ease;
}

.avatar-name {
  font-size: 14px;
  color: #666;
  text-align: center;
  margin-top: 4px;
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.avatar-item:hover .avatar-option {
  border-color: #ffb6c1;
}

/* 修改滚动条样式 */
.avatar-grid::-webkit-scrollbar {
  width: 6px;
}

.avatar-grid::-webkit-scrollbar-track {
  background: #f5f5f5;
  border-radius: 3px;
}

.avatar-grid::-webkit-scrollbar-thumb {
  background: #ffb6c1;
  border-radius: 3px;
}

.avatar-grid::-webkit-scrollbar-thumb:hover {
  background: #ff9eb5;
}

.dialog-footer {
  text-align: center;
  padding-top: 20px;
}

:deep(.el-dialog) {
  border-radius: 20px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  margin: 0;
  padding: 20px;
  text-align: center;
  background: #ffb6c1;
  color: white;
}

:deep(.el-dialog__title) {
  color: white;
  font-size: 20px;
  font-weight: 600;
}

:deep(.el-dialog__body) {
  padding: 0;
}

:deep(.el-button--primary) {
  background: #ffb6c1;
  border-color: #ffb6c1;
  padding: 12px 30px;
  font-size: 16px;
}

:deep(.el-button--primary:hover) {
  background: #ff9eb5;
  border-color: #ff9eb5;
}

:deep(.el-dialog__footer) {
  padding: 20px;
  border-top: 1px solid #f0f0f0;
}
</style>
