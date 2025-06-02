<template>
  <div class="friends-manage">
    <div class="page-header">
      <h3>
        <img src="@/assets/admin/friends.png" alt="朋友圈" class="header-icon" />
        朋友圈管理
      </h3>
    </div>

    <!-- 搜索区域 -->
    <div class="filter-section">
      <div class="filter-content">
        <div class="search-box">
          <img src="@/assets/admin/search.png" alt="搜索" class="search-icon" />
          <input
          v-model="searchQuery"
            type="text"
          placeholder="搜索"
          class="search-input"
          />
        </div>
        <button @click="handleAdd" class="add-button">
          <span class="button-text">发布朋友圈</span>
        </button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="friends-table" v-loading="loading">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>朋友圈内容</th>
            <th>图片</th>
            <th>发布时间</th>
            <th>发布地点</th>
            <th>点赞数</th>
            <th>评论数</th>
            <th>权限范围</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in filteredFriendsList" :key="row.id">
            <td>{{ row.id }}</td>
            <td class="content-cell">
            <p class="content-text">{{ row.content }}</p>
            </td>
            <td>
          <div class="image-preview" v-if="row.images && row.images.length">
                <img
              :src="row.images[0]"
              class="preview-image"
                  @click="previewImages(row.images)"
                />
            <span v-if="row.images.length > 1" class="image-count">+{{ row.images.length - 1 }}</span>
          </div>
          <div v-else class="no-image">
                <span>无图片</span>
          </div>
            </td>
            <td>{{ formatDate(row.publishTime) }}</td>
            <td>{{ row.location }}</td>
            <td class="count-cell">
          <span class="count-badge">
                <img src="@/assets/admin/like.png" alt="点赞" class="icon-small" />
            {{ row.likesCount }}
          </span>
            </td>
            <td class="count-cell">
          <span class="count-badge">
                <img src="@/assets/admin/comment.png" alt="评论" class="icon-small" />
            {{ row.commentsCount }}
          </span>
            </td>
            <td>
              <span :class="['scope-tag', `scope-${getScopeClass(row.viewScope)}`]">
            {{ getScopeText(row.viewScope) }}
              </span>
            </td>
            <td>
          <div class="operation-buttons">
                <button class="edit-button" @click="handleEdit(row)">修改</button>
                <button class="delete-button" @click="handleDelete(row)">删除</button>
          </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <div class="pagination-info">
        共 {{ total }} 条
    </div>
      <div class="pagination-controls">
        <button
          :disabled="currentPage === 1"
          @click="handleCurrentChange(currentPage - 1)"
          class="page-button"
            >
          上一页
        </button>

        <!-- 页码按钮组 -->
        <div class="page-numbers">
        <button
            v-if="currentPage > 3"
            class="page-button"
            @click="handleCurrentChange(1)"
          >1</button>
          <span v-if="currentPage > 4">...</span>

          <button
            v-for="num in pageNumbers"
            :key="num"
            :class="['page-button', { active: currentPage === num }]"
            @click="handleCurrentChange(num)"
          >{{ num }}</button>

          <span v-if="currentPage < totalPages - 3">...</span>
          <button
            v-if="currentPage < totalPages - 2"
            class="page-button"
            @click="handleCurrentChange(totalPages)"
          >{{ totalPages }}</button>
        </div>

        <button
          :disabled="currentPage >= totalPages"
          @click="handleCurrentChange(currentPage + 1)"
          class="page-button"
        >
          下一页
        </button>
                  </div>
      <!-- 自定义下拉框 -->
      <div class="custom-select" @click="togglePageSizeDropdown" ref="pageSizeSelectRef">
        <div class="select-selected">
          <span>{{ pageSize }}条/页</span>
          <span class="select-arrow" :class="{ 'arrow-up': showPageSizeDropdown }">▼</span>
        </div>
        <div class="select-dropdown" v-if="showPageSizeDropdown">
          <div 
            v-for="size in [5, 10]" 
            :key="size"
            class="select-option"
            :class="{ active: pageSize === size }"
            @click="handleSizeChange(size)"
          >
            {{ size }}条/页
          </div>
        </div>
      </div>
    </div>

    <!-- 自定义表单弹窗 -->
    <Teleport to="body">
      <div v-if="showForm" class="modal-container">
        <div class="modal-backdrop"></div>
        <div class="modal-dialog">
          <div class="modal-header">
            <h3>{{ isEdit ? '编辑朋友圈' : '发布朋友圈' }}</h3>
            <button class="close-button" @click="handleCloseForm">×</button>
          </div>
          <FriendCircleForm
            :initial-data="formData"
            :is-edit="isEdit"
            @submit="handleSubmit"
            @cancel="handleCloseForm"
          />
        </div>
      </div>
    </Teleport>

    <!-- 自定义删除确认弹窗 -->
    <Teleport to="body">
      <transition name="delete-modal">
        <div v-if="showDeleteConfirm" class="delete-confirm-modal">
          <div class="delete-modal-backdrop" @click="cancelDelete"></div>
          <div class="delete-modal-content">
            <div class="delete-modal-icon">
              <img src="@/assets/admin/delete.png" alt="删除" class="delete-icon-large" />
            </div>
            <h3 class="delete-modal-title">确定要删除这条朋友圈吗？</h3>
            <p class="delete-modal-text">删除后将无法恢复哦 (｡•́︿•̀｡)</p>
            <div class="delete-modal-buttons">
              <button class="delete-cancel-btn" @click="cancelDelete">
                再想想
              </button>
              <button class="delete-confirm-btn" @click="confirmDelete">
                删除啦！
              </button>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { API_BASE_URL } from '@/config'
import { useUserStore } from '@/stores/user'
import { Picture, Star, ChatDotRound, Plus, Location, Delete } from '@element-plus/icons-vue'
import FriendCircleForm from '@/components/FriendCircleForm.vue'
import { debounce } from 'lodash'
import { get, post, del } from '@/utils/request'

const userStore = useUserStore()

// 数据加载和分页
const friendsList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(5)
const loading = ref(false)
const searchQuery = ref('')

const formatDate = (date) => {
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 表单相关
const showForm = ref(false)
const isEdit = ref(false)
const formData = ref({
  content: '',
  images: [],
  location: '',
  scope: 'public'
})

// 过滤后的列表数据
const filteredFriendsList = computed(() => {
  if (!searchQuery.value) return friendsList.value
  const query = searchQuery.value.toLowerCase()
  return friendsList.value.filter(item =>
    item.content.toLowerCase().includes(query) ||
    item.location.toLowerCase().includes(query)
  )
})

// 加载数据的方法
const loadData = async () => {
  loading.value = true
  try {
    console.log('Loading data with pageSize:', pageSize.value)
    const params = {
      search: searchQuery.value,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }

    const res = await get('/api/post/admin/page', params)

    console.log('Backend response data:', res.data.records)

    // 转换数据结构以匹配前端展示需求
    friendsList.value = res.data.records.map(item => {
      console.log('Processing item:', item)
      // 从 imageDataVos 提取图片 URL 和 ID
      const images = item.imageDataVos?.map(img => img.url) || []
      const imageIds = item.imageDataVos?.map(img => img.id) || []

      return {
      id: item.id,
      content: item.text,
        images: images,
        imageIds: imageIds,
      location: item.location,
      publishTime: item.createTime,
      likesCount: item.likeCount,
      commentsCount: item.commentCount,
      viewScope: item.viewScope
      }
    })
    console.log('Processed friendsList:', friendsList.value)
    total.value = res.data.total
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 处理分页
const handleSizeChange = (newSize) => {
  console.log('Changing page size to:', newSize)
  pageSize.value = newSize
  currentPage.value = 1
  loadData()
}

const handleCurrentChange = (val) => {
  currentPage.value = parseInt(val) || 1 // 确保是数字
  loadData()
}

// 监听搜索输入
watch(searchQuery, debounce(() => {
  currentPage.value = 1 // 重置到第一页
  loadData()
}, 300))

// 处理新增
const handleAdd = () => {
  isEdit.value = false
  formData.value = {
    content: '',
    images: [],
    location: '',
    scope: 'public'
  }
  showForm.value = true
}

// 处理编辑
const handleEdit = (row) => {
  console.log('Edit row data:', row)
  isEdit.value = true
  formData.value = {
    id: row.id,
    content: row.content,
    images: row.images || [],
    imageIds: row.imageIds || [],
    location: row.location,
    publishTime: row.publishTime,
    scope: getScopeValue(row.viewScope)
  }
  console.log('Form data after edit setup:', formData.value)
  showForm.value = true
}


// 添加辅助方法
const getScopeValue = (viewScope) => {
  switch (viewScope) {
    case 1:
      return 'public'
    case 2:
      return 'friends'
    case 3:
      return 'private'
    default:
      return 'public'
  }
}

// 处理状态切换
const handleToggleStatus = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要${row.status === 'active' ? '禁用' : '启用'}该好友吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: row.status === 'active' ? 'warning' : 'success'
      }
    )
    // TODO: 调用API更新状态
    ElMessage.success(`${row.status === 'active' ? '禁用' : '启用'}成功`)
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 处理删除
const showDeleteConfirm = ref(false)
const currentDeleteItem = ref(null)

const handleDelete = (row) => {
  currentDeleteItem.value = row
  showDeleteConfirm.value = true
}

const cancelDelete = () => {
  showDeleteConfirm.value = false
  currentDeleteItem.value = null
}

const confirmDelete = async () => {
  try {
    await del(`/api/post/admin/${currentDeleteItem.value.id}`)
    ElMessage.success('删除成功啦 (｡･ω･｡)ﾉ')
    showDeleteConfirm.value = false
    currentDeleteItem.value = null
    loadData()
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败了呢 (´;ω;｀)')
  }
}

// 处理图片上传成功
const handleImageSuccess = (response, uploadFile) => {
  formData.value.images.push(response.data.url)
}

// 图片上传前的验证
const beforeImageUpload = (file) => {
  const isImage = /^image\/(jpeg|png|gif)$/.test(file.type)
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
  }
  return isImage && isLt5M
}

// 处理图片删除
const handleImageRemove = (file) => {
  const index = formData.value.images.indexOf(file.url)
  if (index !== -1) {
    formData.value.images.splice(index, 1)
  }
}

// 处理表单提交
const handleSubmit = async (data) => {
  try {
    if (isEdit.value) {
      // 发送更新请求
      await post('/api/post/admin/update', data)
      ElMessage.success('更新成功')
    } else {
      // 处理新建请求
      await post('/api/post/admin', data)
      ElMessage.success('发布成功')
    }
    showForm.value = false
    loadData() // 重新加载列表
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error(isEdit.value ? '更新失败' : '发布失败')
  }
}

const handleCloseForm = () => {
  showForm.value = false
}

const previewImages = (images) => {
  // TODO: 实现图片预览功能
}

// 辅助方法
const getScopeText = (viewScope) => {
  switch (viewScope) {
    case 1:
      return '公开'
    case 2:
      return '好友可见'
    case 3:
      return '仅自己可见'
    default:
      return '未知'
  }
}

const getScopeClass = (viewScope) => {
  switch (viewScope) {
    case 1:
      return 'public'
    case 2:
      return 'friends'
    case 3:
      return 'private'
    default:
      return 'private'
  }
}

// 添加跳转页码相关的变量和计算属性
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

// 计算要显示的页码数组
const pageNumbers = computed(() => {
  const current = currentPage.value
  const total = totalPages.value
  const delta = 2 // 当前页码前后显示的页码数

  let start = Math.max(1, current - delta)
  let end = Math.min(total, current + delta)

  // 调整start和end，确保显示5个页码
  if (end - start < 4) {
    if (start === 1) {
      end = Math.min(start + 4, total)
    } else if (end === total) {
      start = Math.max(end - 4, 1)
    }
  }

  return Array.from({ length: end - start + 1 }, (_, i) => start + i)
})

// 点击外部关闭下拉框
const handleClickOutside = (event) => {
  if (pageSizeSelectRef.value && !pageSizeSelectRef.value.contains(event.target)) {
    showPageSizeDropdown.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  loadData()
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

// 自定义下拉框相关
const showPageSizeDropdown = ref(false)
const pageSizeSelectRef = ref(null)

const togglePageSizeDropdown = () => {
  showPageSizeDropdown.value = !showPageSizeDropdown.value
}
</script>

<style scoped>
.friends-manage {
  padding: 20px;
  background: linear-gradient(135deg, #fff5f7 0%, #fff 100%);
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(255, 182, 193, 0.1);
  transition: all 0.3s ease;
}

.friends-manage:hover {
  transform: scale(1.01);
  box-shadow: 0 8px 24px rgba(255, 182, 193, 0.2);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #ffecef;
}

.page-header h3 {
  font-size: 24px;
  color: #333;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}
.button-text{
  font-size: 15px;
  font-weight: bold;
}
.add-button {
  background: linear-gradient(135deg, #ff8ea3 0%, #ffbec5 100%);
  border: none;
  padding: 8px 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 800;
  color: #ffffff;
  min-width: 100px;
  transform: scale(1);
}

.add-button:hover {
  transform: scale(1.08);
  background: linear-gradient(135deg, #ffbec5 0%, #ff8ea3 100%);
  box-shadow: 0 8px 20px rgba(255, 142, 163, 0.3);
}

.add-button:active {
  transform: scale(1);
  box-shadow: 0 4px 12px rgba(255, 142, 163, 0.2);
}

.filter-section {
  margin-bottom: 10px;
}

.filter-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.search-box {
  position: relative;
  max-width: 260px;
  transition: all 0.3s ease;
  transform-origin: left center;
}

.search-box:hover {
  transform: scale(1.15);
}

.search-box input {
  width: 100%;
  padding: 10px 12px 10px 40px;
  border: 1px solid #ddd;
  border-radius: 20px;
  font-size: 14px;
  transition: all 0.3s;
}

.search-box input:focus {
  outline: none;
  border-color: #ff69b4;
  box-shadow: 0 0 0 2px rgba(255, 105, 180, 0.1);
}

.search-box .search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  width: 20px;
  height: 20px;
  opacity: 0.5;
}

.friends-table {
  margin-top: 20px;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(255, 182, 193, 0.15);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.friends-table:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(255, 182, 193, 0.25);
}

table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  background: #fff;
}

th {
  padding: 16px;
  text-align: left;
  background: #fff5f7;
  color: #515151;
  font-weight: 600;
  font-size: 14px;
  border-bottom: 2px solid #ffe4e8;
  transition: all 0.3s ease;
}

td {
  padding: 16px;
  border-bottom: 1px solid #ffe4e8;
  font-size: 14px;
  color: #666;
  transition: all 0.3s ease;
}

tr {
  transition: all 0.3s ease;
  position: relative;
}

tr:hover td {
  background: #fff5f7;
  transform: scale(1.01);
  color: #ff69b4;
}

tr:hover .content-text {
  color: #ff69b4;
}

.content-cell {
  max-width: 300px;
  transition: all 0.3s ease;
}

.content-text {
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.5;
  color: #666;
  transition: all 0.3s ease;
}

.image-preview {
  width: 60px;
  height: 60px;
  position: relative;
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.image-preview:hover {
  transform: scale(1.1) rotate(2deg);
  box-shadow: 0 4px 12px rgba(255, 105, 180, 0.2);
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.image-count {
  position: absolute;
  right: 4px;
  bottom: 4px;
  background: rgba(255, 105, 180, 0.8);
  color: #fff;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  transition: all 0.3s ease;
}

.no-image {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff5f7;
  border-radius: 8px;
  color: #ffb6c1;
  font-size: 12px;
  border: 2px dashed #ffe4e8;
  transition: all 0.3s ease;
}

.no-image:hover {
  background: #ffe4e8;
  transform: scale(1.05);
}

.count-cell {
  text-align: center;
}

.count-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 8px;
  background: #fff5f7;
  border-radius: 12px;
  transition: all 0.3s ease;
}

tr:hover .count-badge {
  background: #ffe4e8;
  transform: scale(1.05);
}

.icon-small {
  width: 16px;
  height: 16px;
  opacity: 0.7;
  transition: all 0.3s ease;
}

tr:hover .icon-small {
  opacity: 1;
  transform: scale(1.1);
}

.scope-tag {
  display: inline-block;
  padding: 6px 12px;
  border-radius: 12px;
  font-size: 12px;
  transition: all 0.3s ease;
}

.scope-public {
  background: #e1f7e3;
  color: #52c41a;
}

.scope-friends {
  background: #fff7e6;
  color: #fa8c16;
}

.scope-private {
  background: #f5f5f5;
  color: #909399;
  border: 1px solid #e4e7ed;
}

tr:hover .scope-private {
  background: #e9e9eb;
  color: #606266;
}

.operation-buttons {
  display: flex;
  gap: 8px;
  opacity: 0.7;
  transition: all 0.3s ease;
}

tr:hover .operation-buttons {
  opacity: 1;
}

.edit-button,
.delete-button {
  padding: 6px 16px;
  border-radius: 12px;
  border: none;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.3s ease;
}

.edit-button {
  background: #fff5f7;
  color: #ff69b4;
}

.edit-button:hover {
  background: #ffe4e8;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 105, 180, 0.2);
}

.delete-button {
  background: #fff1f0;
  color: #ff4d4f;
}

.delete-button:hover {
  background: #ffd6d6;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 77, 79, 0.2);
}

.modal-container {
  position: fixed;
  inset: 0;
  z-index: 9999;
  overflow: hidden;
}

.modal-backdrop {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(5px);
}

.modal-dialog {
  position: fixed;
  top: 50vh;
  left: 50vw;
  transform: translate(-50%, -50%);
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  background: #fff5f7;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(255, 182, 193, 0.3);
  overflow-y: auto;
  z-index: 10000;
  border: 2px solid #ffe4e8;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-dialog:hover {
  transform: translate(-50%, -50%) scale(1.02);
  box-shadow: 0 15px 35px rgba(255, 182, 193, 0.4);
}

.modal-header {
  position: sticky;
  top: 0;
  background: #ffe4e8;
  padding: 20px 24px;
  border-bottom: 2px solid #ffd6db;
  border-radius: 20px 20px 0 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 1;
}

.modal-header h3 {
  margin: 0;
  font-size: 20px;
  color: #515151;
  font-weight: 600;
}

.close-button {
  background: none;
  border: none;
  font-size: 24px;
  color: #999;
  cursor: pointer;
  padding: 8px;
  line-height: 1;
  border-radius: 50%;
  transition: all 0.3s ease;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-button:hover {
  background: #ffe4e8;
  color: #ff69b4;
  transform: rotate(90deg);
}

/* 自定义滚动条样式 */
.modal-dialog::-webkit-scrollbar {
  width: 8px;
}

.modal-dialog::-webkit-scrollbar-track {
  background: #ffe4e8;
  border-radius: 4px;
}

.modal-dialog::-webkit-scrollbar-thumb {
  background: #ffb6c1;
  border-radius: 4px;
}

.modal-dialog::-webkit-scrollbar-thumb:hover {
  background: #ff8da1;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-numbers {
  display: flex;
  align-items: center;
  gap: 4px;
}

.page-button {
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: #fff;
  cursor: pointer;
  transition: all 0.3s;
  min-width: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.page-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-button:not(:disabled):hover {
  border-color: #ff69b4;
  color: #ff69b4;
}

.page-button.active {
  background: #ffe4e8;
  color: #ff8da1;
  border-color: #ffd6db;
}

.page-button.active:hover {
  background: #ffd6db;
  color: #ff8da1;
  border-color: #ffbec5;
}

/* 自定义下拉框样式 */
.custom-select {
  position: relative;
  cursor: pointer;
  user-select: none;
}

.select-selected {
  padding: 8px 36px 8px 16px;
  border: 1px solid #ffe4e8;
  border-radius: 20px;
  background: white;
  font-size: 14px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  position: relative;
  min-width: 100px;
}

.select-selected:hover {
  border-color: #ff8da1;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(255, 105, 180, 0.1);
}

.select-arrow {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #ff69b4;
  font-size: 12px;
  transition: transform 0.3s ease;
}

.arrow-up {
  transform: translateY(-50%) rotate(180deg);
}

.select-dropdown {
  position: absolute;
  bottom: calc(100% + 8px);
  left: 0;
  right: 0;
  background: white;
  border-radius: 12px;
  box-shadow: 0 -4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  z-index: 99999;
  animation: dropdownFadeIn 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  transform-origin: center bottom;
  border: 1px solid #ffe4e8;
}

.select-option {
  padding: 10px 16px;
  transition: all 0.2s ease;
  font-size: 14px;
  color: #666;
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

.header-icon {
  width: 28px;
  height: 28px;
  margin-right: 8px;
  vertical-align: middle;
}

.button-icon {
  width: 14px;
  height: 14px;
  vertical-align: middle;
}

.search-icon {
  width: 22px;
  height: 22px;
  vertical-align: middle;
  opacity: 0.7;
}

/* 标签样式 */
:deep(.el-tag--success) {
  background: #e1f7e3;
  border-color: #b7eabc;
  color: #67c23a;
}

:deep(.el-tag--info) {
  background: #f4f4f5;
  border-color: #e9e9eb;
  color: #909399;
}

/* 表单样式 */
:deep(.el-form-item__label) {
  color: #333;
  font-weight: 500;
}

:deep(.el-input__wrapper),
:deep(.el-textarea__inner) {
  border-radius: 8px;
  border: 1px solid #ffe4e8;
}

:deep(.el-input__wrapper:hover),
:deep(.el-textarea__inner:hover) {
  border-color: #ff8da1;
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-textarea__inner:focus) {
  border-color: #ff69b4;
  box-shadow: 0 0 0 2px rgba(255, 105, 180, 0.2);
}

/* 按钮样式 */
:deep(.el-button--primary) {
  background: linear-gradient(135deg, #ff69b4 0%, #ff8da1 100%);
  border: none;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #ff8da1 0%, #ff69b4 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 105, 180, 0.2);
}

/* 表单按钮样式 */
:deep(.form-actions) {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px;
  background: #fff5f7;
  border-top: 2px solid #ffe4e8;
  margin-top: 20px;
}

:deep(.submit-button),
:deep(.cancel-button) {
  padding: 10px 28px;
  border-radius: 20px;
  font-size: 15px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.submit-button) {
  background: #ff8da1;
  color: white;
  box-shadow: 0 4px 15px rgba(255, 105, 180, 0.2);
}

:deep(.submit-button:hover) {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 8px 20px rgba(255, 105, 180, 0.3);
  background: #ff69b4;
}

:deep(.submit-button:active) {
  transform: translateY(0) scale(0.98);
}

:deep(.cancel-button) {
  background: transparent;
  color: #ff69b4;
  border: 2px solid #ffd6db;
}

:deep(.cancel-button:hover) {
  background: rgba(255, 141, 161, 0.1);
  transform: translateY(-2px);
  border-color: #ff8da1;
}

:deep(.cancel-button:active) {
  transform: translateY(0);
  background: rgba(255, 141, 161, 0.2);
}

/* 删除确认弹窗样式 */
.delete-modal-enter-active,
.delete-modal-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.delete-modal-enter-from,
.delete-modal-leave-to {
  opacity: 0;
  transform: scale(0.9);
}

.delete-confirm-modal {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.delete-modal-backdrop {
  position: fixed;
  inset: 0;
  background-color: rgba(255, 192, 203, 0.15);
  backdrop-filter: blur(4px);
  animation: backdrop-fade 0.3s ease;
}

.delete-modal-content {
  position: relative;
  background: white;
  padding: 32px;
  border-radius: 24px;
  box-shadow: 0 10px 30px rgba(255, 182, 193, 0.3);
  text-align: center;
  max-width: 400px;
  width: 90%;
  animation: modal-pop 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  border: 2px solid #ffe4e8;
  transition: transform 0.3s ease;
}

.delete-modal-content:hover {
  transform: scale(1.02);
}

.delete-modal-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  background: #fff5f7;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: icon-bounce 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.delete-icon-large {
  width: 40px;
  height: 40px;
  opacity: 0.8;
}

.delete-modal-title {
  color: #ff6b81;
  font-size: 20px;
  margin: 0 0 12px;
  font-weight: 600;
}

.delete-modal-text {
  color: #666;
  margin: 0 0 24px;
  font-size: 15px;
}

.delete-modal-buttons {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.delete-cancel-btn,
.delete-confirm-btn {
  padding: 10px 24px;
  border-radius: 20px;
  border: none;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.delete-cancel-btn {
  background: #fff5f7;
  color: #ff6b81;
  border: 2px solid #ffd6db;
}

.delete-cancel-btn:hover {
  background: #ffe4e8;
  transform: translateY(-2px);
}

.delete-confirm-btn {
  background: #ff6b81;
  color: white;
}

.delete-confirm-btn:hover {
  background: #ff8da1;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 129, 0.3);
}

@keyframes backdrop-fade {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes modal-pop {
  from {
    opacity: 0;
    transform: scale(0.8);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes icon-bounce {
  0% {
    transform: scale(0.8);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}
</style>
