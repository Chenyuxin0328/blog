<template>
  <div class="friends-manage">
    <div class="page-header">
      <h3>
        <img src="@/assets/admin/user.png" alt="用户" class="header-icon" />
        用户管理
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
              placeholder="搜索用户名"
              class="search-input"
          />
        </div>
        <div class="role-filter" @click="toggleRoleDropdown" ref="roleFilterRef">
          <div class="role-selected">
            <img src="@/assets/admin/role.png" alt="角色" class="role-icon" />
            <span>{{ selectedRoleText }}</span>
            <span class="dropdown-arrow" :class="{ 'arrow-up': showRoleDropdown }">▼</span>
          </div>
          <div class="role-dropdown" v-if="showRoleDropdown">
            <div 
              class="role-option" 
              :class="{ active: selectedRole === '' }"
              @click="selectRole('')"
            >
              全部
            </div>
            <div 
              class="role-option" 
              :class="{ active: selectedRole === 'admin' }"
              @click="selectRole('admin')"
            >
              管理员
            </div>
            <div 
              class="role-option" 
              :class="{ active: selectedRole === 'user' }"
              @click="selectRole('user')"
            >
              普通用户
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="friends-table" v-loading="loading">
      <table>
        <thead>
        <tr>
          <th>ID</th>
          <th>用户名</th>
          <th>头像</th>
          <th>密码</th>
          <th>角色</th>
          <th>注册时间</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="row in filteredUsersList" :key="row.id">
          <td>{{ row.id }}</td>
          <td>{{ row.username }}</td>
          <td>
            <div class="avatar-preview">
              <img 
                :src="row.avatar || '/default-avatar.png'" 
                :alt="row.username"
                class="avatar-image"
              />
            </div>
          </td>
          <td>{{ row.password }}</td>
          <td>
            <span :class="['role-tag', `role-${row.role}`]">
              {{ getRoleText(row.role) }}
            </span>
          </td>
          <td>{{ formatDate(row.createTime) }}</td>
          <td>
            <div class="operation-buttons">
              <button 
                class="role-button" 
                :class="row.role === 'admin' ? 'set-user' : 'set-admin'"
                @click="handleRoleChange(row)"
              >
                {{ row.role === 'admin' ? '设置为普通用户' : '设置为管理员' }}
              </button>
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

    <!-- 自定义删除确认弹窗 -->
    <Teleport to="body">
      <transition name="delete-modal">
        <div v-if="showDeleteConfirm" class="delete-confirm-modal">
          <div class="delete-modal-backdrop" @click="cancelDelete"></div>
          <div class="delete-modal-content">
            <div class="delete-modal-icon">
              <img src="@/assets/admin/delete.png" alt="删除" class="delete-icon-large" />
            </div>
            <h3 class="delete-modal-title">确定要删除该用户吗？</h3>
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
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { debounce } from 'lodash'
import { get, del } from '@/utils/request'

const userStore = useUserStore()

// 查询参数
const searchQuery = ref('')
const selectedRole = ref('')
const currentPage = ref(1)
const pageSize = ref(5)
const total = ref(0)
const loading = ref(false)
const usersList = ref([])

// 加载数据的方法
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      search: searchQuery.value,
      role: selectedRole.value,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }
    
    const response = await get('/api/user/admin/page', params)
    if (response.code === 200) {
      usersList.value = response.data.records
      total.value = response.data.total
      currentPage.value = response.data.pageNum
      pageSize.value = response.data.pageSize
    } else {
      ElMessage.error('获取用户列表失败 (｡•́︿•̀｡)')
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('获取用户列表失败 (｡•́︿•̀｡)')
  } finally {
    loading.value = false
  }
}

// 监听搜索输入
watch(searchQuery, debounce(() => {
  currentPage.value = 1
  loadData()
}, 300))

// 监听角色选择变化
watch(selectedRole, () => {
  currentPage.value = 1
  loadData()
})

// 处理分页
const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  currentPage.value = 1
  loadData()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadData()
}

// 处理角色变更
const handleRoleChange = async (row) => {
  try {
    const newRole = row.role === 'admin' ? 'user' : 'admin'
    const response = await get(`/api/user/admin/${row.id}`, { role: newRole })
    
    if (response.code === 200) {
      ElMessage.success(`已将用户 ${row.username} 设置为${getRoleText(newRole)}`)
      // 重新加载数据
      loadData()
    } else {
      ElMessage.error('设置失败了呢 (´;ω;｀)')
    }
  } catch (error) {
    console.error('角色更新失败:', error)
    ElMessage.error('设置失败了呢 (´;ω;｀)')
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
    const userId = currentDeleteItem.value.id
    const response = await del(`/api/user/admin/${userId}`)
    
    if (response.code === 200) {
      ElMessage.success('删除成功啦 (｡･ω･｡)ﾉ')
      showDeleteConfirm.value = false
      currentDeleteItem.value = null
      // 重新加载数据
      loadData()
    } else {
      ElMessage.error('删除失败了呢 (´;ω;｀)')
    }
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败了呢 (´;ω;｀)')
  }
}

// 获取角色文本
const getRoleText = (role) => {
  switch (role.toLowerCase()) {
    case 'admin':
      return '管理员'
    case 'user':
      return '普通用户'
    default:
      return '未知'
  }
}

// 计算要显示的页码数组
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

const pageNumbers = computed(() => {
  const current = currentPage.value
  const total = totalPages.value
  const delta = 2

  let start = Math.max(1, current - delta)
  let end = Math.min(total, current + delta)

  if (end - start < 4) {
    if (start === 1) {
      end = Math.min(start + 4, total)
    } else if (end === total) {
      start = Math.max(end - 4, 1)
    }
  }

  return Array.from({ length: end - start + 1 }, (_, i) => start + i)
})

// 角色筛选相关
const showRoleDropdown = ref(false)
const roleFilterRef = ref(null)

const selectedRoleText = computed(() => {
  switch (selectedRole.value) {
    case 'admin':
      return '管理员'
    case 'user':
      return '普通用户'
    default:
      return '全部'
  }
})

const toggleRoleDropdown = () => {
  showRoleDropdown.value = !showRoleDropdown.value
}

const selectRole = (role) => {
  selectedRole.value = role
  showRoleDropdown.value = false
}

// 点击外部关闭下拉框
const handleClickOutside = (event) => {
  if (roleFilterRef.value && !roleFilterRef.value.contains(event.target)) {
    showRoleDropdown.value = false
  }
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

const formatDate = (date) => {
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const filteredUsersList = computed(() => usersList.value)

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
  z-index: 1;
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
  margin-bottom: 24px;
  position: relative;
  z-index: 2001;
}

.filter-content {
  display: flex;
  gap: 34px;
  align-items: center;
  position: relative;
}

.search-box {
  position: relative;
  max-width: 260px;
  transition: all 0.3s ease;
  transform-origin: left center;
  flex: 0 0 auto;
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

.role-filter {
  position: relative;
  min-width: 120px;
  cursor: pointer;
  transition: all 0.3s ease;
  transform-origin: left center;
  flex: 0 0 auto;
}

.role-selected {
  height: 41px;
  padding: 0 16px;
  background: white;
  border: 1px solid #ddd;
  border-radius: 20px;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  font-size: 14px;
}

.role-filter:hover .role-selected {
  border-color: #ddd;
  box-shadow: 0 0 0 2px rgba(0, 0, 0, 0.1);
  transform: scale(1.15);
}

.role-icon {
  width: 20px;
  height: 20px;
  opacity: 0.7;
  transition: all 0.3s ease;
}

.role-filter:hover .role-icon {
  opacity: 1;
}

.dropdown-arrow {
  margin-left: 4px;
  color: #999;
  font-size: 12px;
  transition: all 0.3s ease;
  transform: rotate(180deg);
}

.role-filter:hover .dropdown-arrow {
  color: #ff69b4;
}

.arrow-up {
  transform: rotate(0deg);
}

.role-dropdown {
  position: absolute;
  bottom: calc(100% + 8px);
  left: 50%;
  transform: translateX(-50%);
  min-width: 120px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 -4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  z-index: 99999;
  animation: dropdownFadeIn 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  transform-origin: center bottom;
}

@keyframes dropdownFadeIn {
  from {
    opacity: 0;
    transform: translateX(-50%) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateX(-50%) scale(1);
  }
}

.role-filter:hover .role-dropdown {
  min-width: 138px;
}

.role-option {
  padding: 8px 16px;
  transition: all 0.2s ease;
  font-size: 13px;
  white-space: nowrap;
  line-height: 1.2;
}

.role-option:hover {
  background: #fff5f7;
  color: #ff69b4;
}

.role-option.active {
  background: #ffe4e8;
  color: #ff69b4;
  font-weight: 500;
}

.friends-table {
  margin-top: 20px;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(255, 182, 193, 0.15);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  z-index: 2000;
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
  border: 1px solid #ffe4e8;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 -4px 12px rgba(255, 105, 180, 0.15);
  z-index: 9999;
  animation: dropdownFadeIn 0.2s ease;
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

/* 保持原有的样式,但添加用户状态标签样式 */
.status-tag {
  display: inline-block;
  padding: 6px 12px;
  border-radius: 12px;
  font-size: 12px;
  transition: all 0.3s ease;
}

.status-1 {
  background: #e1f7e3;
  color: #52c41a;
}

.status-0 {
  background: #fff1f0;
  color: #ff4d4f;
}

.avatar-preview {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(255, 182, 193, 0.2);
  transition: all 0.3s ease;
}

.avatar-preview:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(255, 182, 193, 0.3);
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.3s ease;
}

.password-mask {
  font-family: monospace;
  letter-spacing: 2px;
  color: #666;
}

.role-tag {
  display: inline-block;
  padding: 6px 12px;
  border-radius: 12px;
  font-size: 12px;
  transition: all 0.3s ease;
}

.role-admin {
  background: #fff7e6;
  color: #fa8c16;
  border: 1px solid #ffd591;
}

.role-user {
  background: #e6f7ff;
  color: #1890ff;
  border: 1px solid #91d5ff;
}

.role-button {
  padding: 6px 16px;
  border-radius: 12px;
  border: none;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.3s ease;
  min-width: 120px;
}

.set-admin {
  background: #fff7e6;
  color: #fa8c16;
  border: 1px solid #ffd591;
}

.set-admin:hover {
  background: #fff1e6;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(250, 140, 22, 0.2);
}

.set-user {
  background: #e6f7ff;
  color: #1890ff;
  border: 1px solid #91d5ff;
}

.set-user:hover {
  background: #e6f3ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.2);
}
</style>
