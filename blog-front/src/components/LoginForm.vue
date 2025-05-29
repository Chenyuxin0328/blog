<template>
  <el-form @submit.prevent="handleSubmit" class="form login-form" :model="form">
    <el-form-item class="form-item">
      <div class="input-wrapper">
        <label :class="['floating-label', { 'is-float': form.username || isFocused.username }]">
          用户名
        </label>
        <el-input
          v-model="form.username"
          @input="handleUsernameInput"
          @focus="handleFocus('username')"
          @blur="handleBlur('username')"
        />
      </div>
    </el-form-item>
    <el-form-item class="form-item">
      <div class="input-wrapper">
        <label :class="['floating-label', { 'is-float': form.password || isFocused.password }]">
          密码
        </label>
        <el-input
          v-model="form.password"
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
</template>

<script setup>
import { reactive, ref, defineEmits } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const emit = defineEmits(['submit', 'username-input'])
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const form = reactive({
  username: '',
  password: ''
})

const isFocused = reactive({
  username: false,
  password: false
})

const handleFocus = (field) => {
  isFocused[field] = true
}

const handleBlur = (field) => {
  isFocused[field] = false
}

const handleUsernameInput = () => {
  emit('username-input', form.username)
}

const handleSubmit = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请填写完整的登录信息')
    return
  }

  loading.value = true
  try {
    await userStore.login(form.username, form.password)
    ElMessage.success('登录成功')
    router.push('/home')
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error(error.response?.data?.message || error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.form {
  display: flex;
  flex-direction: column;
  gap: 24px;
  opacity: 1;
  transform: translateX(0);
  transition: all 0.3s ease;
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
</style> 