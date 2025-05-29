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
              <LoginForm
                v-if="activeTab === 'login'"
                @submit="handleLogin"
                @username-input="searchUserByUsername"
              />
              <RegisterForm
                v-else
                @submit="handleRegister"
              />
            </Transition>
          </div>
        </div>
      </div>

      <UserProfile
        v-if="matchedUser && activeTab === 'login'"
        :visible="true"
        :avatar="matchedUser.avatar"
        :username="matchedUser.username"
      />
    </div>

    <AvatarSelectionDialog
      v-model:show="showAvatarDialog"
      :avatar-list="avatarList"
      @select="handleAvatarSelect"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { API_BASE_URL } from '@/config'
import { useUserStore } from '@/stores/user'
import LoginForm from '@/components/LoginForm.vue'
import RegisterForm from '@/components/RegisterForm.vue'
import UserProfile from '@/components/UserProfile.vue'
import AvatarSelectionDialog from '@/components/AvatarSelectionDialog.vue'

const router = useRouter()
const userStore = useUserStore()
const activeTab = ref('login')
const matchedUser = ref(null)
const showAvatarDialog = ref(false)
const userId = ref(null)

// 使用 Vite 的动态导入
const avatarList = ref([
  { url: new URL('../assets/head/a1.png', import.meta.url).href, name: '边牧',filename:'a1.png' },
  { url: new URL('../assets/head/a2.png', import.meta.url).href, name: '白猫',filename:'a2.png' },
  { url: new URL('../assets/head/a3.png', import.meta.url).href, name: '布偶猫' ,filename:'a3.png'},
  { url: new URL('../assets/head/a4.png', import.meta.url).href, name: '仓鼠',filename:'a4.png' },
  { url: new URL('../assets/head/a5.png', import.meta.url).href, name: '藏獒' ,filename:'a5.png'},
  { url: new URL('../assets/head/a6.png', import.meta.url).href, name: '柴犬',filename:'a6.png' },
  { url: new URL('../assets/head/a7.png', import.meta.url).href, name: '法斗' ,filename:'a7.png'},
  { url: new URL('../assets/head/a8.png', import.meta.url).href, name: '哈士奇' ,filename:'a8.png'},
  { url: new URL('../assets/head/a9.png', import.meta.url).href, name: '荷兰猪',filename:'a9.png' },
  { url: new URL('../assets/head/a10.png', import.meta.url).href, name: '黑猫' ,filename:'a10.png'},
  { url: new URL('../assets/head/a11.png', import.meta.url).href, name: '金毛' ,filename:'a11.png'},
  { url: new URL('../assets/head/a12.png', import.meta.url).href, name: '橘猫',filename:'a12.png' },
  { url: new URL('../assets/head/a13.png', import.meta.url).href, name: '柯基' ,filename:'a13.png'},
  { url: new URL('../assets/head/a14.png', import.meta.url).href, name: '可达鸭',filename:'a14.png' },
  { url: new URL('../assets/head/a15.png', import.meta.url).href, name: '腊肠犬' ,filename:'a15.png'},
  { url: new URL('../assets/head/a16.png', import.meta.url).href, name: '蓝猫' ,filename:'a16.png'},
  { url: new URL('../assets/head/a17.png', import.meta.url).href, name: '奶牛猫' ,filename:'a17.png'},
  { url: new URL('../assets/head/a18.png', import.meta.url).href, name: '三花猫' ,filename:'a18.png'},
  { url: new URL('../assets/head/a19.png', import.meta.url).href, name: '田园犬' ,filename:'a19.png'},
  { url: new URL('../assets/head/a20.png', import.meta.url).href, name: '无毛猫' ,filename:'a20.png'},
  { url: new URL('../assets/head/a21.png', import.meta.url).href, name: '暹罗猫',filename:'a21.png' },
  { url: new URL('../assets/head/a22.png', import.meta.url).href, name: '羊' ,filename:'a22.png'}
])

const searchUserByUsername = async (username) => {
  if (!username) {
    matchedUser.value = null
    return
  }

  try {
    const response = await fetch(`${API_BASE_URL}/api/user/username?username=${encodeURIComponent(username)}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    })

    const result = await response.json()

    if (result.code === 200) {
      if (result.data) {
        matchedUser.value = {
          username: result.data.username,
          avatar: result.data.avatar
        }
      } else {
        matchedUser.value = null
      }
    } else {
      matchedUser.value = null
      console.error('查询用户失败:', result.message)
    }
  } catch (error) {
    console.error('查询用户出错:', error)
    matchedUser.value = null
  }
}

const handleLogin = async (formData) => {
  try {
    await userStore.login(formData.username, formData.password)
    ElMessage.success('登录成功')
    router.push('/home')
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error(error.response?.data?.message || '登录失败，请重试')
  }
}

const handleRegister = async (formData) => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/user/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(formData)
    })

    const result = await response.json()

    if (result.code === 200) {
      userId.value = result.data
      ElMessage.success('注册成功，请选择你的头像')
      showAvatarDialog.value = true
    } else if (result.code === 504) {
      ElMessage.error('用户名已存在，请更换用户名')
    } else {
      ElMessage.error(result.message || '注册失败，请重试')
    }
  } catch (error) {
    console.error('注册出错:', error)
    ElMessage.error('注册失败，请重试')
  }
}

const handleAvatarSelect = async (index) => {
  if (index === null || !userId.value) return

  try {
    const selectedAvatar = avatarList.value[index]
    // 从URL中提取文件名
    const avatarFileName = selectedAvatar.filename
    const response = await fetch(`${API_BASE_URL}/api/user/avatar`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        avatar: avatarFileName,
        userId: userId.value
      })
    })

    const result = await response.json()

    if (result.code === 200) {
      ElMessage.success('头像设置成功')
      showAvatarDialog.value = false
      activeTab.value = 'login'
      userId.value = null
    } else {
      ElMessage.error(result.message || '头像设置失败，请重试')
    }
  } catch (error) {
    console.error('设置头像失败:', error)
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
  height: calc(100vh - 80px);
  position: absolute;
  top: 80px;
  left: 0;
  z-index: 1;
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

/* 表单移动动画 */
.user-matched.login-box {
  transform: translateX(-120px) !important;
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

/* Ensure the Transition component itself has a class for targeting */
.form-display-area > .form-transition-wrapper > .v-transition {
  width: 100%;
}
</style>

