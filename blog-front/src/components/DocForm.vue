<template>
  <div class="doc-form">
    <div class="form-body">
      <!-- 文档标题 -->
      <div class="form-group">
        <label>标题<span class="required">*</span></label>
        <input
          v-model="formData.title"
          type="text"
          class="form-input"
          :class="{ 'error': showErrors && !formData.title?.trim() }"
          placeholder="请输入文档标题"
          @focus="hideError('title')"
        />
        <div v-if="showErrors && !formData.title?.trim()" class="error-message">
          请输入标题
        </div>
      </div>

      <!-- 文档内容 -->
      <div class="form-group">
        <label>内容<span class="required">*</span></label>
        <textarea
          v-model="formData.general"
          class="form-textarea"
          :class="{ 'error': showErrors && !formData.general?.trim() }"
          placeholder="请输入内容描述"
          rows="4"
          @focus="hideError('general')"
        ></textarea>
        <div v-if="showErrors && !formData.general?.trim()" class="error-message">
          请输入内容描述
        </div>
      </div>

      <!-- 文档网址 -->
      <div class="form-group">
        <label>网址<span class="required">*</span></label>
        <input
          v-model="formData.url"
          type="text"
          class="form-input"
          :class="{ 'error': showErrors && !formData.url?.trim() }"
          placeholder="请输入文档链接"
          @focus="hideError('url')"
        />
        <div v-if="showErrors && !formData.url?.trim()" class="error-message">
          请输入文档链接
        </div>
      </div>

      <!-- 背景图片 -->
      <div class="form-group">
        <label>背景图片</label>
        <div class="image-upload" :class="{ 'error': showErrors && !formData.backgroundUrl }">
          <div
            class="image-preview"
            :class="{ 'has-image': formData.backgroundUrl }"
            @click="triggerImageUpload"
          >
            <img
              v-if="formData.backgroundUrl"
              :src="formData.backgroundUrl"
              class="preview-img"
            />
            <div v-else class="upload-placeholder">
              <img src="@/assets/admin/image.png" alt="上传图片" class="upload-icon" />
              <span>点击上传图片</span>
            </div>
            <button 
              v-if="formData.backgroundUrl" 
              class="remove-image-btn"
              @click.stop="removeImage"
            >
              ×
            </button>
          </div>
          <input
            ref="fileInput"
            type="file"
            accept="image/*"
            class="file-input"
            @change="handleImageChange"
          />
        </div>
      </div>

      <!-- 将分类、权限范围、创建时间放在一行 -->
      <div class="form-row">
        <div class="form-group">
          <label>分类<span class="required">*</span></label>
          <div class="custom-select" @click="toggleCategoryDropdown" ref="categorySelectRef">
            <div class="select-selected" :class="{ 'error': showErrors && !formData.categoryId }">
              <span>{{ selectedCategoryText }}</span>
              <span class="dropdown-arrow" :class="{ 'arrow-up': showCategoryDropdown }">▼</span>
            </div>
            <div class="select-dropdown" v-if="showCategoryDropdown">
              <div 
                v-for="category in categories"
                :key="category.id"
                class="select-option"
                :class="{ active: formData.categoryId === category.id.toString() }"
                @click="selectCategory(category.id.toString())"
              >
                {{ category.categoryName }}
              </div>
            </div>
          </div>
          <div v-if="showErrors && !formData.categoryId" class="error-message">
            请选择分类
          </div>
        </div>

        <div class="form-group">
          <label>权限范围<span class="required">*</span></label>
          <div class="custom-select" @click="toggleScopeDropdown" ref="scopeSelectRef">
            <div class="select-selected" :class="{ 'error': showErrors && !formData.viewScope }">
              <span>{{ getScopeText(formData.viewScope) }}</span>
              <span class="dropdown-arrow" :class="{ 'arrow-up': showScopeDropdown }">▼</span>
            </div>
            <div class="select-dropdown" v-if="showScopeDropdown">
              <div 
                v-for="scope in scopeOptions" 
                :key="scope.value"
                class="select-option"
                :class="{ active: formData.viewScope === scope.value }"
                @click="selectScope(scope.value)"
              >
                {{ scope.label }}
              </div>
            </div>
          </div>
          <div v-if="showErrors && !formData.viewScope" class="error-message">
            请选择权限范围
          </div>
        </div>

        <div class="form-group">
          <label>创建时间<span class="required">*</span></label>
          <input
            v-model="formData.writeTime"
            type="date"
            class="form-input"
            :class="{ 'error': showErrors && !formData.writeTime }"
            @focus="hideError('writeTime')"
          />
          <div v-if="showErrors && !formData.writeTime" class="error-message">
            请选择创建时间
          </div>
        </div>
      </div>
    </div>

    <!-- 表单按钮 -->
    <div class="form-footer">
      <button class="cancel-btn" @click="$emit('cancel')">取消</button>
      <button class="submit-btn" @click="handleSubmit">{{ isEdit ? '保存' : '发布' }}</button>
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits, onMounted, onUnmounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { post } from '@/utils/request'

const props = defineProps({
  initialData: {
    type: Object,
    default: () => ({
      title: '',
      general: '',
      url: '',
      backgroundUrl: '',
      backgroundId: null,
      categoryId: '',
      viewScope: '1',
      writeTime: new Date().toISOString().split('T')[0]
    })
  },
  isEdit: {
    type: Boolean,
    default: false
  },
  categories: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['submit', 'cancel'])

const formData = ref({ ...props.initialData })
const fileInput = ref(null)
const showErrors = ref(false)

// 添加下拉框相关的状态和方法
const showCategoryDropdown = ref(false)
const showScopeDropdown = ref(false)
const categorySelectRef = ref(null)
const scopeSelectRef = ref(null)

// 权限范围选项
const scopeOptions = [
  { value: '1', label: '公开' },
  { value: '2', label: '好友可见' },
  { value: '3', label: '仅自己可见' }
]

// 计算选中的分类文本
const selectedCategoryText = computed(() => {
  if (!formData.value.categoryId) return '请选择分类'
  const category = props.categories.find(c => c.id.toString() === formData.value.categoryId)
  return category ? category.categoryName : '请选择分类'
})

// 获取权限范围文本
const getScopeText = (scope) => {
  const option = scopeOptions.find(o => o.value === scope)
  return option ? option.label : '请选择权限范围'
}

// 切换下拉框显示状态
const toggleCategoryDropdown = () => {
  showCategoryDropdown.value = !showCategoryDropdown.value
  if (showCategoryDropdown.value) {
    showScopeDropdown.value = false
  }
}

const toggleScopeDropdown = () => {
  showScopeDropdown.value = !showScopeDropdown.value
  if (showScopeDropdown.value) {
    showCategoryDropdown.value = false
  }
}

// 选择分类
const selectCategory = (categoryId) => {
  formData.value.categoryId = categoryId
  showCategoryDropdown.value = false
  hideError('categoryId')
}

// 选择权限范围
const selectScope = (scope) => {
  formData.value.viewScope = scope
  showScopeDropdown.value = false
  hideError('viewScope')
}

// 点击外部关闭下拉框
const handleClickOutside = (event) => {
  if (categorySelectRef.value && !categorySelectRef.value.contains(event.target)) {
    showCategoryDropdown.value = false
  }
  if (scopeSelectRef.value && !scopeSelectRef.value.contains(event.target)) {
    showScopeDropdown.value = false
  }
}

// 监听点击事件
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  if (props.initialData) {
    formData.value = {
      id: props.initialData.id,
      title: props.initialData.title || '',
      general: props.initialData.general || '',
      url: props.initialData.url || '',
      backgroundUrl: props.initialData.backgroundUrl || '',
      backgroundId: props.initialData.backgroundId || null,
      categoryId: props.initialData.categoryId?.toString() || '',
      viewScope: props.initialData.viewScope?.toString() || '1',
      writeTime: props.initialData.writeTime || new Date().toISOString().split('T')[0]
    };
  }
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

const hideError = (field) => {
  if (showErrors.value) {
    showErrors.value = false
  }
}

const triggerImageUpload = () => {
  fileInput.value.click()
}

const handleImageChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  // 验证文件类型和大小
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB！')
    return
  }

  try {
    // 创建 FormData
    const formDataObj = new FormData()
    formDataObj.append('file', file)

    // 调用上传接口
    const res = await post('/api/doc/admin/background', formDataObj, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    if (res.code === 200) {
      formData.value.backgroundUrl = res.data.imageUrl
      formData.value.backgroundId = res.data.imageId
      ElMessage.success('上传成功')
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error('上传失败')
  } finally {
    // 清空文件输入框，这样同一个文件可以重复上传
    event.target.value = ''
  }
}

const removeImage = (e) => {
  e.preventDefault()
  formData.value.backgroundUrl = ''
  formData.value.backgroundId = null
}

const handleSubmit = () => {
  showErrors.value = true

  // 表单验证
  const validationErrors = [];

  if (!formData.value.title?.trim()) {
    validationErrors.push('请输入标题');
  }

  if (!formData.value.general?.trim()) {
    validationErrors.push('请输入内容描述');
  }

  if (!formData.value.url?.trim()) {
    validationErrors.push('请输入文档链接');
  }

  if (!formData.value.categoryId) {
    validationErrors.push('请选择分类');
  }

  if (!formData.value.writeTime) {
    validationErrors.push('请选择创建时间');
  }

  if (validationErrors.length > 0) {
    ElMessage({
      message: validationErrors.join('\n'),
      type: 'warning',
      duration: 5000,
      showClose: true,
      grouping: true
    });
    return;
  }

  // 构造提交数据
  const submitData = {
    ...formData.value,
    id: props.isEdit ? formData.value.id : undefined,
    categoryId: formData.value.categoryId,
    backgroundId: formData.value.backgroundId || null,
    viewScope: parseInt(formData.value.viewScope),
    writeTime: formData.value.writeTime
  };

  // 移除不需要的字段
  delete submitData.backgroundUrl;

  emit('submit', submitData);
};

// 添加表单重置方法
const resetForm = () => {
  formData.value = {
    id: null,
    title: '',
    general: '',
    url: '',
    backgroundUrl: '',
    backgroundId: null,
    categoryId: '',
    viewScope: '1',
    writeTime: new Date().toISOString().split('T')[0]
  };
};
</script>

<style scoped>
.doc-form {
  padding: 24px;
  background: #fff5f7;
  border-radius: 0 0 20px 20px;
}

.form-body {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  position: relative;
}

.form-group label {
  font-size: 14px;
  color: #ff6b81;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
}

.form-input,
.form-textarea,
.form-select {
  padding: 10px 14px;
  border: 2px solid #ffe4e8;
  border-radius: 12px;
  font-size: 14px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255, 255, 255, 0.8);
  color: #333;
  backdrop-filter: blur(8px);
}

.form-input:hover,
.form-textarea:hover,
.form-select:hover {
  border-color: #ffbec5;
  background: rgba(255, 255, 255, 0.9);
}

.form-input:focus,
.form-textarea:focus,
.form-select:focus {
  outline: none;
  border-color: #ff69b4;
  box-shadow: 0 0 0 3px rgba(255, 105, 180, 0.1);
  transform: translateY(-1px);
  background: rgba(255, 255, 255, 0.95);
}

.form-textarea {
  resize: vertical;
  min-height: 120px;
  line-height: 1.6;
}

.image-upload {
  position: relative;
  margin-top: 4px;
}

.image-preview {
  width: 100%;
  height: 140px;
  border: 2px dashed #ffe4e8;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.6);
  position: relative;
  backdrop-filter: blur(4px);
}

.image-preview:hover {
  border-color: #ff69b4;
  background: rgba(255, 255, 255, 0.8);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 105, 180, 0.1);
}

.preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.3s ease;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: #ff8da1;
  font-size: 14px;
  transition: all 0.3s ease;
}

.upload-icon {
  width: 40px;
  height: 40px;
  opacity: 0.7;
  transition: all 0.3s ease;
}

.image-preview:hover .upload-icon {
  opacity: 1;
  transform: scale(1.1);
}

.file-input {
  display: none;
}

.form-footer {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  padding-top: 20px;
  border-top: 2px solid rgba(255, 228, 232, 0.6);
}

.submit-btn,
.cancel-btn {
  padding: 10px 28px;
  border-radius: 20px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.submit-btn {
  background: linear-gradient(135deg, #ff69b4 0%, #ff8da1 100%);
  color: white;
  border: none;
  box-shadow: 0 4px 12px rgba(255, 105, 180, 0.2);
}

.submit-btn:hover {
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 6px 16px rgba(255, 105, 180, 0.3);
  background: linear-gradient(135deg, #ff8da1 0%, #ff69b4 100%);
}

.submit-btn:active {
  transform: translateY(0) scale(0.98);
}

.cancel-btn {
  background: rgba(255, 255, 255, 0.8);
  color: #ff69b4;
  border: 2px solid #ffd6db;
  backdrop-filter: blur(4px);
}

.cancel-btn:hover {
  background: rgba(255, 245, 247, 0.9);
  transform: translateY(-2px);
  border-color: #ff8da1;
}

.cancel-btn:active {
  transform: translateY(0);
}

.has-image {
  border-style: solid;
  border-width: 2px;
  border-color: #ffe4e8;
  background: rgba(255, 255, 255, 0.8);
}

.has-image:hover {
  border-color: #ff69b4;
}

.remove-image-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  border: 2px solid #ffd6db;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 18px;
  color: #ff4d4f;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 2;
  backdrop-filter: blur(4px);
}

.remove-image-btn:hover {
  background: #fff;
  transform: scale(1.1) rotate(90deg);
  border-color: #ff4d4f;
  box-shadow: 0 2px 8px rgba(255, 77, 79, 0.2);
}

.required {
  color: #ff4d4f;
  margin-left: 4px;
  font-size: 14px;
}

.error {
  border-color: #ff4d4f !important;
}

.error:focus {
  box-shadow: 0 0 0 3px rgba(255, 77, 79, 0.1) !important;
}

.error-message {
  color: #ff4d4f;
  font-size: 12px;
  margin-top: 4px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 4px;
}

.form-input::placeholder,
.form-textarea::placeholder {
  color: #ffbec5;
  transition: all 0.3s;
}

.form-input:focus::placeholder,
.form-textarea:focus::placeholder {
  color: #ffd6db;
}

.form-select {
  color: #666;
  cursor: pointer;
}

.form-select option {
  color: #333;
  padding: 8px;
  background: #fff5f7;
}

.form-select option:first-child {
  color: #ffbec5;
}

/* 横向布局样式优化 */
.form-row {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
}

.form-row .form-group {
  flex: 1;
  min-width: 0;
}

.form-row .form-select,
.form-row .form-input {
  width: 100%;
}

.form-row .error-message {
  position: absolute;
  bottom: -20px;
  left: 0;
  white-space: nowrap;
}

/* 日期选择器样式优化 */
.form-input[type="date"] {
  height: 42px;
  padding: 0 14px;
  border-radius: 12px;
  border: 2px solid #ffe4e8;
  background: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  color: #333;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(4px);
}

.form-input[type="date"]::-webkit-calendar-picker-indicator {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  opacity: 0.6;
  transition: all 0.3s;
  filter: invert(73%) sepia(32%) saturate(7499%) hue-rotate(297deg) brightness(101%) contrast(101%);
}

.form-input[type="date"]::-webkit-calendar-picker-indicator:hover {
  opacity: 1;
  transform: translateY(-50%) scale(1.1);
}

/* 添加表单组件的动画效果 */
.form-group {
  transform: translateY(0);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.form-group:hover {
  transform: translateY(-2px);
}

/* 优化选择框样式 */
.form-select {
  appearance: none;
  padding-right: 32px;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='%23ff69b4' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 8px center;
  background-size: 16px;
}

.form-select:hover {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='%23ff8da1' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E");
}

/* 添加表单组件的焦点动画 */
.form-input:focus,
.form-textarea:focus,
.form-select:focus {
  animation: input-focus 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes input-focus {
  0% {
    transform: scale(0.98);
  }
  50% {
    transform: scale(1.02);
  }
  100% {
    transform: scale(1);
  }
}

/* 自定义下拉框样式 */
.custom-select {
  position: relative;
  width: 100%;
  cursor: pointer;
  user-select: none;
}

.select-selected {
  height: 42px;
  padding: 0 14px;
  background: rgba(255, 255, 255, 0.8);
  border: 2px solid #ffe4e8;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  color: #333;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(8px);
}

.select-selected:hover {
  border-color: #ffbec5;
  background: rgba(255, 255, 255, 0.9);
  transform: translateY(-1px);
}

.select-selected.error {
  border-color: #ff4d4f !important;
}

.dropdown-arrow {
  color: #ff8da1;
  font-size: 12px;
  transition: all 0.3s ease;
  margin-left: 8px;
  transform: rotate(180deg);
}

.arrow-up {
  transform: rotate(0deg);
}

.select-dropdown {
  position: absolute;
  bottom: calc(100% + 8px);
  left: 0;
  width: 100%;
  background: rgba(255, 255, 255, 0.95);
  border: 2px solid #ffe4e8;
  border-radius: 12px;
  padding: 8px 0;
  z-index: 1000;
  box-shadow: 0 -4px 12px rgba(255, 105, 180, 0.1);
  backdrop-filter: blur(8px);
  animation: dropdownFadeIn 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  transform-origin: bottom center;
}

.select-option {
  padding: 10px 14px;
  font-size: 14px;
  color: #666;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
}

.select-option:hover {
  background: #fff5f7;
  color: #ff69b4;
}

.select-option.active {
  background: #fff5f7;
  color: #ff69b4;
  font-weight: 500;
}

@keyframes dropdownFadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 移除原生下拉框样式 */
select {
  display: none;
}
</style> 