<template>
  <div class="friend-circle-form">
    <form @submit.prevent="handleSubmit" class="form-container">
      <div class="form-group">
        <label for="content">内容</label>
        <textarea
          id="content"
          v-model="formData.content"
          placeholder="写点什么吧..."
          :maxlength="500"
          required
          @blur="validateContent"
          :class="{ 'error': errors.content }"
        ></textarea>
        <span class="word-count">{{ formData.content.length }}/500</span>
        <div v-if="errors.content" class="error-message">
          <span class="error-icon">❣️</span>
          {{ errors.content }}
        </div>
      </div>

      <div class="form-group">
        <label>图片</label>
        <div class="image-upload-container" :class="{ 'error': errors.images }">
          <div 
            v-for="(image, index) in formData.images" 
            :key="index"
            class="image-preview-container"
          >
            <img :src="image" class="image-preview" />
            <button 
              type="button"
              class="remove-image"
              @click="removeImage(index)"
            >×</button>
          </div>
          <label 
            v-if="formData.images.length < 9"
            class="image-upload-button"
          >
            <span>+</span>
            <input
              type="file"
              ref="imageInput"
              accept="image/*"
              multiple
              @change="handleImageUpload"
              class="image-input"
            />
          </label>
        </div>
        <div v-if="errors.images" class="error-message">
          <span class="error-icon">❣️</span>
          {{ errors.images }}
        </div>
      </div>

      <div class="form-group">
        <label for="location">发布地点</label>
        <input
          id="location"
          type="text"
          v-model="formData.location"
          placeholder="请输入发布地点"
          maxlength="50"
          required
          @blur="validateLocation"
          :class="{ 'error': errors.location }"
        />
        <div v-if="errors.location" class="error-message">
          <span class="error-icon">❣️</span>
          {{ errors.location }}
        </div>
      </div>

      <div class="form-row">
        <div class="form-group half-width">
          <label for="publishTime">发布时间</label>
          <input
            id="publishTime"
            type="datetime-local"
            v-model="formData.publishTime"
            class="time-input"
            step="60"
            placeholder="选择发布时间（可选）"
          />
        </div>

        <div class="form-group half-width">
          <label>权限范围</label>
          <div class="custom-select" @click="toggleDropdown">
            <div class="select-display">
              {{ getScopeLabel(formData.scope) }}
              <span class="select-arrow">▼</span>
            </div>
            <div v-if="isDropdownOpen" class="select-options">
              <div 
                v-for="option in scopeOptions" 
                :key="option.value"
                class="select-option"
                :class="{ 'selected': formData.scope === option.value }"
                @click="selectScope(option.value)"
              >
                {{ option.label }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="form-actions">
        <button type="button" class="cancel-button" @click="handleCancel">取消</button>
        <button type="submit" class="submit-button">{{ isEdit ? '更新' : '发布' }}</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const props = defineProps({
  initialData: {
    type: Object,
    default: () => ({
      content: '',
      images: [],
      imageIds: [],
      location: '',
      publishTime: '',
      scope: 'public'
    })
  },
  isEdit: {
    type: Boolean,
    default: false
  }
})

console.log('Props initialData:', props.initialData)

const emit = defineEmits(['submit', 'cancel'])

const formData = ref({
  content: props.initialData.content || '',
  images: [...(props.initialData.images || [])],
  imageIds: [...(props.initialData.imageIds || [])].filter(id => id != null),
  location: props.initialData.location || '',
  publishTime: props.initialData.publishTime || '',
  scope: props.initialData.scope || 'public'
})

console.log('Initial formData:', formData.value)

const imageInput = ref(null)

const isDropdownOpen = ref(false)

const scopeOptions = [
  { label: '仅自己可见', value: 'private' },
  { label: '好友可见', value: 'friends' },
  { label: '公开', value: 'public' }
]

const errors = ref({
  content: '',
  images: '',
  location: ''
})

const triggerImageUpload = () => {
  if (imageInput.value) {
    imageInput.value.click()
  }
}

const handleImageUpload = async (event) => {
  const files = Array.from(event.target.files)
  if (!files.length) return

  // 检查是否超过总数限制
  const totalImagesAfterUpload = formData.value.images.length + files.length
  if (totalImagesAfterUpload > 9) {
    ElMessage.warning('最多只能上传9张图片')
    return
  }

  const promises = files.map(file => {
    return new Promise(async (resolve) => {
      if (file.size > 5 * 1024 * 1024) {
        ElMessage.warning('图片大小不能超过5MB')
        resolve()
        return
      }

      try {
        const formDataObj = new FormData()
        formDataObj.append('file', file)

        const response = await request({
          url: '/api/post/admin/img',
          method: 'post',
          headers: {
            'Content-Type': 'multipart/form-data',
            'Accept': 'application/json'
          },
          transformRequest: [(data) => data],
          data: formDataObj
        })

        console.log('图片上传响应:', response.data)

        // 确保数组存在且不为null
        if (!Array.isArray(formData.value.images)) formData.value.images = []
        if (!Array.isArray(formData.value.imageIds)) formData.value.imageIds = []

        // 只有当返回的数据有效时才添加
        if (response.data.imageUrl && response.data.imageId) {
          formData.value.images.push(response.data.imageUrl)
          formData.value.imageIds.push(response.data.imageId)
        }
        
        console.log('图片上传后的状态:', {
          images: formData.value.images,
          imageIds: formData.value.imageIds
        })
        
        resolve()
      } catch (error) {
        console.error('图片上传失败:', error)
        ElMessage.error('图片上传失败')
        resolve()
      }
    })
  })

  await Promise.all(promises)
  if (imageInput.value) {
    imageInput.value.value = ''
  }
}

const removeImage = (index) => {
  console.log('删除前的状态:', {
    images: formData.value.images,
    imageIds: formData.value.imageIds
  })
  
  // 同时删除图片URL和ID
  formData.value.images.splice(index, 1)
  formData.value.imageIds.splice(index, 1)
  
  // 过滤掉可能的null值
  formData.value.imageIds = formData.value.imageIds.filter(id => id != null)
  
  console.log('删除后的状态:', {
    images: formData.value.images,
    imageIds: formData.value.imageIds
  })
}

const validateContent = () => {
  if (!formData.value.content.trim()) {
    errors.value.content = '内容不能为空啦 (｡•́︿•̀｡)'
  } else if (formData.value.content.length < 2) {
    errors.value.content = '内容太短啦，多写一点吧 (◕‿◕✿)'
  } else {
    errors.value.content = ''
  }
}

const validateLocation = () => {
  if (!formData.value.location.trim()) {
    errors.value.location = '地点不能为空哦 (｡•́︿•̀｡)'
  } else {
    errors.value.location = ''
  }
}

const validateImages = () => {
  if (formData.value.images.length > 9) {
    errors.value.images = '最多只能上传9张图片啦 (◠‿◠✿)'
  } else {
    errors.value.images = ''
  }
}

const handleSubmit = () => {
  // 进行所有验证
  validateContent()
  validateLocation()
  validateImages()

  // 检查是否有错误
  if (Object.values(errors.value).some(error => error)) {
    ElMessage({
      message: '还有一些小问题需要修改哦 (｡•́︿•̀｡)',
      type: 'warning',
      customClass: 'cute-message'
    })
    return
  }

  // 过滤掉null值并去重
  const validImageIds = [...new Set(formData.value.imageIds.filter(id => id != null))]
  
  // 处理时间格式
  let formattedTime = null
  if (formData.value.publishTime) {
    // 检查时间字符串是否已经包含秒
    formattedTime = formData.value.publishTime.includes(':00') 
      ? formData.value.publishTime 
      : formData.value.publishTime + ':00'
  }
  
  if (props.isEdit) {
    // 更新请求
    const updateData = {
      id: props.initialData.id,
      text: formData.value.content,
      location: formData.value.location,
      viewScope: formData.value.scope === 'public' ? 1 : formData.value.scope === 'friends' ? 2 : 3,
      imageIds: validImageIds,
      createTime: formattedTime
    }
    
    console.log('提交的更新数据:', updateData)
    emit('submit', updateData)
  } else {
    // 新建请求
    const createData = {
      text: formData.value.content,
      location: formData.value.location,
      viewScope: formData.value.scope === 'public' ? 1 : formData.value.scope === 'friends' ? 2 : 3,
      imageIds: validImageIds,
      createTime: formattedTime
    }
    
    console.log('提交的创建数据:', createData)
    emit('submit', createData)
  }
}

const handleCancel = () => {
  emit('cancel')
}

const toggleDropdown = () => {
  isDropdownOpen.value = !isDropdownOpen.value
}

const selectScope = (value) => {
  formData.value.scope = value
  isDropdownOpen.value = false
}

const getScopeLabel = (value) => {
  return scopeOptions.find(option => option.value === value)?.label || ''
}

// 点击外部关闭下拉框
onMounted(() => {
  document.addEventListener('click', (e) => {
    const target = e.target;
    if (target && !target.closest('.custom-select')) {
      isDropdownOpen.value = false;
    }
  });
})
</script>

<style scoped>
:root {
  --primary-color: #ffb6c1;
  --primary-light: #ffd6e0;
  --primary-dark: #ff8da1;
  --hover-color: #ff69b4;
  --text-color: #333;
  --border-color: #ffe4e8;
  --background-light: #fff5f7;
}

.friend-circle-form {
  background: var(--background-light);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(255, 182, 193, 0.15);
  transition: all 0.3s ease;
  border: 2px solid var(--border-color);
}

.friend-circle-form:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(255, 182, 193, 0.25);
}

.form-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  position: relative;
  transition: all 0.3s ease;
  padding: 8px;
  border-radius: 12px;
}

.form-group:hover {
  background: #fff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 182, 193, 0.1);
}

label {
  font-weight: 600;
  color: var(--text-color);
  font-size: 14px;
  transition: all 0.3s ease;
}

.form-group:hover label {
  color: var(--hover-color);
  transform: translateX(4px);
}

/* 统一输入框样式 */
input[type="text"],
input[type="datetime-local"],
textarea,
.select-display {
  padding: 8px 16px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 14px;
  color: #666;
  background: white;
  transition: all 0.3s ease;
}

input[type="text"]:hover,
input[type="datetime-local"]:hover,
textarea:hover,
.select-display:hover {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(255, 182, 193, 0.1);
  transform: translateY(-1px);
}

input[type="text"]:focus,
input[type="datetime-local"]:focus,
textarea:focus,
.select-display:focus {
  outline: none;
  border-color: var(--hover-color);
  box-shadow: 0 0 0 3px rgba(255, 182, 193, 0.2);
}

/* 特别调整文本域的样式 */
textarea {
  min-height: 120px;
  resize: vertical;
  line-height: 1.5;
}

/* 调整选择框的特殊样式 */
.select-display {
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.select-arrow {
  color: var(--primary-color);
  transition: transform 0.3s ease;
  margin-left: 8px;
}

.word-count {
  position: absolute;
  right: 16px;
  bottom: 16px;
  font-size: 12px;
  color: var(--primary-dark);
  transition: all 0.3s ease;
  padding: 4px 8px;
  border-radius: 12px;
  background: var(--background-light);
}

.form-group:hover .word-count {
  background: var(--primary-light);
}

.image-upload-container {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  padding: 16px;
  border: 2px dashed var(--border-color);
  border-radius: 12px;
  min-height: 100px;
  transition: all 0.3s ease;
  background: #fff;
}

.image-upload-container:hover {
  border-color: var(--primary-color);
  background: var(--background-light);
}

.image-preview-container {
  position: relative;
  width: 80px;
  height: 80px;
  transition: all 0.3s ease;
  border-radius: 8px;
  overflow: hidden;
}

.image-preview-container:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(255, 182, 193, 0.2);
}

.image-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.image-preview-container:hover .image-preview {
  transform: scale(1.1);
}

.remove-image {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: var(--primary-dark);
  color: white;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  transition: all 0.3s ease;
  opacity: 0;
  transform: scale(0.8);
}

.image-preview-container:hover .remove-image {
  opacity: 1;
  transform: scale(1);
}

.remove-image:hover {
  background: var(--hover-color);
  transform: scale(1.1) rotate(90deg);
}

.image-upload-button {
  width: 80px;
  height: 80px;
  border: 2px dashed var(--border-color);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  position: relative;
  transition: all 0.3s ease;
  background: #fff;
}

.image-upload-button:hover {
  border-color: var(--hover-color);
  background: var(--background-light);
  transform: scale(1.05);
}

.image-upload-button span {
  font-size: 24px;
  color: var(--primary-color);
  transition: all 0.3s ease;
  pointer-events: none;
}

.image-upload-button:hover span {
  color: var(--hover-color);
  transform: rotate(180deg);
}

.image-input {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
  z-index: 1;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 2px solid var(--border-color);
}

.submit-button,
.cancel-button {
  padding: 12px 28px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  position: relative;
  overflow: hidden;
}

.submit-button {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--hover-color) 100%);
  color: white;
}

.submit-button:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 8px 20px rgba(255, 105, 180, 0.3);
}

.submit-button:active {
  transform: translateY(-2px) scale(0.98);
}

.cancel-button {
  background: #fff;
  color: var(--text-color);
  border: 2px solid var(--border-color);
}

.cancel-button:hover {
  background: var(--background-light);
  border-color: var(--primary-color);
  transform: translateY(-4px);
  color: var(--hover-color);
}

.cancel-button:active {
  transform: translateY(-2px);
}

/* 添加输入框占位符样式 */
::placeholder {
  color: #bbb;
  transition: all 0.3s ease;
}

:focus::placeholder {
  color: var(--primary-color);
  transform: translateX(4px);
}

/* 添加选择框样式 */
select {
  appearance: none;
  background-image: linear-gradient(45deg, transparent 50%, var(--primary-color) 50%),
    linear-gradient(135deg, var(--primary-color) 50%, transparent 50%);
  background-position: calc(100% - 20px) calc(1em + 2px),
    calc(100% - 15px) calc(1em + 2px);
  background-size: 5px 5px, 5px 5px;
  background-repeat: no-repeat;
}

select:hover {
  background-image: linear-gradient(45deg, transparent 50%, var(--hover-color) 50%),
    linear-gradient(135deg, var(--hover-color) 50%, transparent 50%);
}

/* 添加滚动条样式 */
textarea::-webkit-scrollbar {
  width: 8px;
}

textarea::-webkit-scrollbar-track {
  background: var(--background-light);
  border-radius: 4px;
}

textarea::-webkit-scrollbar-thumb {
  background: var(--primary-light);
  border-radius: 4px;
  transition: all 0.3s ease;
}

textarea::-webkit-scrollbar-thumb:hover {
  background: var(--primary-color);
}

/* 修改时间选择器样式 */
input[type="datetime-local"] {
  position: relative;
  padding: 10px 16px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 14px;
  color: #666;
  background: white;
  transition: all 0.3s ease;
  cursor: pointer;
  min-width: 200px;
}

/* 隐藏秒选择器 */
input[type="datetime-local"]::-webkit-datetime-edit-second-field {
  display: none;
}

input[type="datetime-local"]::-webkit-calendar-picker-indicator {
  position: absolute;
  right: 10px;
  width: 20px;
  height: 20px;
  padding: 0;
  margin: 0;
  cursor: pointer;
  opacity: 0.6;
  filter: invert(80%) sepia(29%) saturate(6764%) hue-rotate(297deg) brightness(101%) contrast(97%);
  transition: all 0.3s ease;
}

input[type="datetime-local"]::-webkit-calendar-picker-indicator:hover {
  opacity: 1;
  transform: scale(1.1);
}

.time-input {
  padding: 10px 12px;
  border: 1px solid #ffd6db;
  border-radius: 8px;
  font-size: 14px;
  color: #666;
  width: 100%;
  background: white;
  transition: all 0.3s ease;
}

.time-input:hover {
  border-color: #ff8da1;
}

.time-input:focus {
  outline: none;
  border-color: #ff69b4;
  box-shadow: 0 0 0 3px rgba(255, 105, 180, 0.1);
}

/* 自定义时间选择器样式 */
.time-input::-webkit-calendar-picker-indicator {
  background-color: transparent;
  padding: 4px;
  cursor: pointer;
  opacity: 0.6;
  transition: all 0.3s ease;
}

.time-input::-webkit-calendar-picker-indicator:hover {
  opacity: 1;
  transform: scale(1.1);
}

.form-row {
  display: flex;
  gap: 20px;
  margin: 0 -8px;
}

.half-width {
  flex: 1;
  margin: 0 8px;
}

.form-group select {
  padding: 10px 12px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 14px;
  color: #666;
  width: 100%;
  background: white;
  cursor: pointer;
  transition: all 0.3s ease;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12' fill='none'%3E%3Cpath d='M2.5 4.5L6 8L9.5 4.5' stroke='%23FFB6C1' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
  padding-right: 36px;
}

.form-group select:hover {
  border-color: var(--primary-color);
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12' fill='none'%3E%3Cpath d='M2.5 4.5L6 8L9.5 4.5' stroke='%23FF69B4' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E");
  transform: translateY(-1px);
}

.form-group select:focus {
  outline: none;
  border-color: var(--hover-color);
  box-shadow: 0 0 0 3px rgba(255, 105, 180, 0.1);
}

/* 下拉选项样式 */
.form-group select option {
  padding: 12px 16px;
  font-size: 14px;
  color: #333;
  background: white;
  border: none;
  margin: 4px 0;
  transition: all 0.2s ease;
}

/* 自定义下拉列表样式 */
select::-webkit-listbox {
  background: white;
  border-radius: 12px;
  padding: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  border: 1px solid #f0f0f0;
}

/* 选项悬停和选中状态 */
.form-group select option:checked {
  background: #4080ff !important;
  color: white;
  font-weight: 500;
}

.form-group select option:hover {
  background: #f5f5f5;
}

/* 添加选项前的图标样式 */
.form-group select option[value="private"]::before {
  content: "🔒 ";
}

.form-group select option[value="friends"]::before {
  content: "👥 ";
}

.form-group select option[value="public"]::before {
  content: "🌎 ";
}

/* 美化下拉框打开时的样式 */
.form-group select:focus {
  border-color: var(--hover-color);
  box-shadow: 0 0 0 3px rgba(255, 105, 180, 0.1);
}

.custom-select {
  position: relative;
  width: 100%;
}

.select-display {
  padding: 8px 16px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 14px;
  color: #666;
  background: white;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.3s ease;
}

.select-display:hover {
  border-color: var(--primary-color);
}

.select-arrow {
  color: var(--primary-color);
  transition: transform 0.3s ease;
  margin-left: 8px;
}

.custom-select:has(.select-options) .select-arrow {
  transform: rotate(180deg);
}

.select-options {
  position: absolute;
  bottom: calc(100% + 8px);
  left: 0;
  right: 0;
  background: white;
  border-radius: 8px;
  border: 1px solid var(--border-color);
  box-shadow: 0 -4px 12px rgba(255, 182, 193, 0.2);
  z-index: 9999;
  display: flex;
  flex-direction: column;
  transform-origin: bottom center;
  animation: dropdownAppear 0.2s ease;
  padding: 4px 0;
  overflow: hidden;
}

@keyframes dropdownAppear {
  from {
    opacity: 0;
    transform: translateY(10px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.select-option {
  padding: 8px 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  color: #666;
  display: block;
  width: 100%;
  text-align: left;
  border: none;
  background: none;
  position: relative;
  transform-origin: center;
}

.select-option:hover {
  background: var(--background-light);
  color: var(--hover-color);
  transform: scale(1.05);
}

.select-option.selected {
  background: var(--primary-light);
  color: var(--primary-dark);
  font-weight: 500;
}

.select-option.selected:hover {
  transform: scale(1.05);
  background: var(--primary-light);
  color: var(--primary-dark);
}

/* 错误状态样式 */
.error {
  border-color: #ff9eb5 !important;
  background-color: #fff5f7 !important;
  animation: shake 0.5s ease-in-out;
}

.error:focus {
  border-color: #ff6b88 !important;
  box-shadow: 0 0 0 3px rgba(255, 155, 181, 0.2) !important;
}

.error-message {
  font-size: 13px;
  color: #ff6b88;
  margin-top: 6px;
  padding: 8px 12px;
  background: #fff5f7;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
  animation: slideIn 0.3s ease-out;
  border: 1px dashed #ffd6e1;
  transform-origin: top left;
}

.error-icon {
  font-size: 14px;
  animation: bounce 0.6s ease infinite;
}

/* 添加可爱的动画效果 */
@keyframes shake {
  0%, 100% { transform: translateX(0); }
  20%, 60% { transform: translateX(-4px); }
  40%, 80% { transform: translateX(4px); }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-10px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-3px); }
}

/* 自定义 Element Plus 消息提示样式 */
:deep(.cute-message) {
  background: #fff5f7 !important;
  border: 1px solid #ffd6e1 !important;
  border-radius: 12px !important;
  padding: 12px 20px !important;
  box-shadow: 0 4px 12px rgba(255, 155, 181, 0.2) !important;
}

:deep(.cute-message .el-message__content) {
  color: #ff6b88 !important;
  font-size: 14px !important;
}

/* 输入框占位符样式 */
.error::placeholder {
  color: #ff9eb5;
}

/* 图片上传错误状态 */
.image-upload-container.error {
  border-color: #ff9eb5;
  background-color: #fff5f7;
}

/* 表单组错误状态的悬浮效果 */
.form-group:has(.error):hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 155, 181, 0.15);
}

/* 错误消息悬浮效果 */
.error-message:hover {
  transform: scale(1.02);
  background: #fff0f3;
  border-color: #ffbecb;
}
</style> 