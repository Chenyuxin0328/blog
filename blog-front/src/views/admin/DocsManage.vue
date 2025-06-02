<template>
  <div class="friends-manage">
    <div class="page-header">
      <h3>
        <img src="@/assets/admin/doc.png" alt="文档" class="header-icon" />
        技术文档管理
      </h3>
    </div>

    <div class="content-wrapper">
      <div class="main-content">
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
            <div class="role-filter" @click="toggleCategoryDropdown" ref="categoryFilterRef">
              <div class="role-selected">
                <img src="@/assets/admin/category.png" alt="分类" class="role-icon" />
                <span>{{ selectedCategoryText }}</span>
                <span class="dropdown-arrow" :class="{ 'arrow-up': showCategoryDropdown }">▼</span>
              </div>
              <div class="role-dropdown" v-if="showCategoryDropdown">
                <div 
                  class="role-option" 
                  :class="{ active: selectedCategoryId === null }"
                  @click="selectCategory(null)"
                >
                  全部分类
                </div>
                <div 
                  v-for="category in categories" 
                  :key="category.id"
                  class="role-option" 
                  :class="{ active: selectedCategoryId === category.id }"
                  @click="selectCategory(category.id)"
                >
                  {{ category.categoryName }}
                </div>
              </div>
            </div>
            <button @click="handleAdd" class="add-button">
              <span class="button-text">发布文档</span>
            </button>
          </div>
        </div>

        <!-- 数据表格 -->
        <div class="friends-table" v-loading="loading">
          <table>
            <thead>
            <tr>
              <th style="width: 60px;">ID</th>
              <th style="width: 150px;">标题</th>
              <th style="width: 180px;">背景图片</th>
              <th style="width: 200px;">网址</th>
              <th style="width: 120px;">分类</th>
              <th style="width: 80px;">权限范围</th>
              <th style="width: 150px;">创建时间</th>
              <th style="width: 80px;">操作</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="row in friendsList" :key="row.id">
              <td>{{ row.id }}</td>
              <td class="title-cell">{{ row.title }}</td>
              <td class="image-cell">
                <div class="image-wrapper">
                  <img 
                    v-if="row.backgroundUrl" 
                    :src="row.backgroundUrl" 
                    class="cover-image"
                    @click="previewImage(row.backgroundUrl)"
                  />
                  <div v-else class="no-image">
                    <img src="@/assets/admin/image.png" alt="无图片" class="no-image-icon" />
                  </div>
                </div>
              </td>
              <td class="url-cell">
                <a v-if="row.url" :href="row.url" target="_blank" class="doc-url">{{ row.url }}</a>
                <span v-else class="no-url">暂无链接</span>
              </td>
              <td>
                <span class="category-tag">{{ row.categoryName || '未分类' }}</span>
              </td>
              <td>
                <span class="scope-tag" :class="'scope-' + getScopeClass(row.viewScope)">
                  {{ getScopeText(row.viewScope) }}
                </span>
              </td>
              <td class="time-cell">
                {{ row.writeTime }}
              </td>
              <td>
                <div class="operation-buttons-vertical">
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
            <div class="page-numbers">
              <button
                  v-for="num in displayedPages"
                  :key="num"
                  :class="['page-button', { active: currentPage === num }]"
                  @click="handleCurrentChange(num)"
              >
                {{ num }}
              </button>
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
      </div>

      <!-- 分类管理侧边栏 -->
      <div class="category-sidebar">
        <div class="category-header">
          <h3>
            <img src="@/assets/admin/category.png" alt="分类" class="header-icon" />
            分类管理
          </h3>
        </div>
        <div class="category-list">
          <div class="category-item" v-for="category in categories" :key="category.id">
            <div class="category-info">
              <span class="category-name">{{ category.categoryName }}</span>
              <span class="category-count">{{ category.count }}篇</span>
            </div>
            <div class="category-actions">
              <button class="edit-button" @click="handleEditCategory(category)">修改</button>
              <button class="delete-button" @click="handleDeleteCategory(category)">删除</button>
            </div>
          </div>
        </div>
        <button class="add-category-button" @click="handleAddCategory">
          <img src="@/assets/admin/plus.png" alt="添加分类" class="button-icon" />
          <span>添加分类</span>
        </button>
      </div>
    </div>

    <!-- 自定义表单弹窗 -->
    <Teleport to="body">
      <div v-if="showForm" class="modal-container">
        <div class="modal-backdrop"></div>
        <div class="modal-dialog">
          <div class="modal-header">
            <h3>{{ isEdit ? '编辑文档' : '发布文档' }}</h3>
            <button class="close-button" @click="handleCloseForm">×</button>
          </div>
          <DocForm
              :initial-data="formData"
              :is-edit="isEdit"
              :categories="categories"
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
            <h3 class="delete-modal-title">确定要删除这篇文档吗？</h3>
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

    <!-- 分类表单弹窗 -->
    <Teleport to="body">
      <div v-if="showCategoryForm" class="modal-container">
        <div class="modal-backdrop" @click="showCategoryForm = false"></div>
        <div class="modal-dialog category-form-dialog">
          <div class="modal-header">
            <h3>{{ isEditingCategory ? '编辑分类' : '添加分类' }}</h3>
            <button class="close-button" @click="showCategoryForm = false">×</button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label>分类名称</label>
              <input
                v-model="categoryFormData.categoryName"
                type="text"
                class="form-input"
                placeholder="请输入分类名称"
              />
            </div>
          </div>
          <div class="modal-footer">
            <button class="cancel-button" @click="showCategoryForm = false">取消</button>
            <button class="submit-button" @click="submitCategory">确定</button>
          </div>
        </div>
      </div>
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
import DocForm from '@/components/DocForm.vue'

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
  id: null,
  title: '',
  general: '',
  url: '',
  backgroundUrl: '',
  categoryId: '',
  viewScope: '1',
  writeTime: new Date().toISOString().split('T')[0]
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

// 分类筛选相关
const showCategoryDropdown = ref(false)
const categoryFilterRef = ref(null)
const selectedCategoryId = ref(null)

const selectedCategoryText = computed(() => {
  if (!selectedCategoryId.value) return '全部分类'
  const category = categories.value.find(c => c.id === selectedCategoryId.value)
  return category ? category.categoryName : '全部分类'
})

const toggleCategoryDropdown = () => {
  showCategoryDropdown.value = !showCategoryDropdown.value
}

const selectCategory = (categoryId) => {
  selectedCategoryId.value = categoryId
  showCategoryDropdown.value = false
  currentPage.value = 1
  loadData()
}

// 点击外部关闭下拉框
const handleClickOutside = (event) => {
  if (categoryFilterRef.value && !categoryFilterRef.value.contains(event.target)) {
    showCategoryDropdown.value = false
  }
  if (pageSizeSelectRef.value && !pageSizeSelectRef.value.contains(event.target)) {
    showPageSizeDropdown.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  loadCategories()
  loadData()
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

// 加载数据的方法
const loadData = async () => {
  loading.value = true;
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    };
    
    if (searchQuery.value) {
      params.search = searchQuery.value;
    }
    
    if (selectedCategoryId.value) {
      params.categoryId = selectedCategoryId.value;
    }

    const res = await get('/api/doc/admin/page', params);

    if (res.code === 200) {
      // 转换数据结构以匹配前端展示需求
      friendsList.value = res.data.records.map(item => ({
        id: item.id,
        title: item.title,
        general: item.general || '',  // 确保有默认值
        url: item.url || '',
        backgroundUrl: item.backgroundUrl,
        backgroundId: item.backgroundId,
        categoryId: item.categoryId,
        categoryName: item.categoryName,
        writeTime: item.writeTime || new Date().toISOString().split('T')[0],
        viewScope: item.viewScope
      }));
      
      total.value = res.data.total;
      currentPage.value = res.data.pageNum;
      pageSize.value = res.data.pageSize;
    } else {
      ElMessage.error(res.message || '加载失败');
    }
  } catch (error) {
    console.error('加载数据失败:', error);
    ElMessage.error('加载数据失败');
  } finally {
    loading.value = false;
  }
};

// 处理分页
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1; // 切换每页条数时重置为第一页
  loadData();
};

const handleCurrentChange = (page) => {
  currentPage.value = page;
  loadData();
};

// 监听搜索输入
watch(searchQuery, debounce(() => {
  currentPage.value = 1 // 重置到第一页
  loadData()
}, 300))

// 处理新增
const handleAdd = () => {
  isEdit.value = false;
  formData.value = {
    id: null,
    title: '',
    general: '',
    url: '',
    backgroundUrl: '',
    categoryId: '',
    viewScope: '1',
    writeTime: new Date().toISOString().split('T')[0]
  };
  showForm.value = true;
};

// 处理编辑
const handleEdit = (row) => {
  isEdit.value = true;
  formData.value = {
    id: row.id,
    title: row.title,
    general: row.general || '',
    url: row.url || '',
    backgroundUrl: row.backgroundUrl,
    backgroundId: row.backgroundId,
    categoryId: row.categoryId?.toString() || '',
    viewScope: row.viewScope?.toString() || '1',
    writeTime: row.writeTime || new Date().toISOString().split('T')[0]
  };
  showForm.value = true;
};

// 处理表单提交
const handleSubmit = async (data) => {
  try {
    const submitData = {
      ...data,
      id: isEdit.value ? data.id : undefined,
      categoryId: data.categoryId ? parseInt(data.categoryId) : null,
      backgroundId: data.backgroundId ? parseInt(data.backgroundId) : null,
      viewScope: parseInt(data.viewScope),
      writeTime: data.writeTime
    };

    if (isEdit.value) {
      // 发送更新请求
      const res = await post('/api/doc/admin/update', submitData);
      if (res.code === 200) {
        ElMessage.success('更新成功');
        showForm.value = false;
        loadData(); // 重新加载列表
      } else {
        ElMessage.error(res.message || '更新失败');
      }
    } else {
      // 处理新建请求
      const res = await post('/api/doc/admin/create', submitData);
      if (res.code === 200) {
        ElMessage.success('发布成功');
        showForm.value = false;
        loadData(); // 重新加载列表
      } else {
        ElMessage.error(res.message || '发布失败');
      }
    }
  } catch (error) {
    console.error('操作失败:', error);
    ElMessage.error(isEdit.value ? '更新失败' : '发布失败');
  }
};

const handleCloseForm = () => {
  showForm.value = false
}

const previewImage = (url) => {
  if (!url) return;
  // 这里可以使用 Element Plus 的 ImageViewer 组件或其他图片预览组件
  ElMessageBox.alert(`<img src="${url}" style="max-width: 100%; height: auto;">`, '图片预览', {
    dangerouslyUseHTMLString: true,
    showClose: true,
    showConfirmButton: false,
    customClass: 'image-preview-dialog'
  });
};

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

// 计算要显示的页码
const displayedPages = computed(() => {
  const current = currentPage.value;
  const total = totalPages.value;
  const delta = 2; // 当前页码前后显示的页码数
  
  let start = Math.max(1, current - delta);
  let end = Math.min(total, current + delta);
  
  // 调整start和end，确保显示5个页码
  if (end - start < 4) {
    if (start === 1) {
      end = Math.min(5, total);
    } else if (end === total) {
      start = Math.max(total - 4, 1);
    }
  }
  
  return Array.from({ length: end - start + 1 }, (_, i) => start + i);
})

const categories = ref([])
const showCategoryForm = ref(false)
const categoryFormData = ref({
  id: null,
  categoryName: ''
})
const isEditingCategory = ref(false)

// 加载分类列表
const loadCategories = async () => {
  try {
    const res = await get('/api/doc/category')
    if (res.code === 200) {
      categories.value = res.data
    } else {
      ElMessage.error(res.message || '加载分类失败')
    }
  } catch (error) {
    console.error('加载分类失败:', error)
    ElMessage.error('加载分类失败')
  }
}

// 处理添加分类
const handleAddCategory = () => {
  isEditingCategory.value = false
  categoryFormData.value = {
    id: null,
    categoryName: ''
  }
  showCategoryForm.value = true
}

// 处理编辑分类
const handleEditCategory = (category) => {
  isEditingCategory.value = true
  categoryFormData.value = {
    id: category.id,
    categoryName: category.categoryName
  }
  showCategoryForm.value = true
}

// 提交分类表单
const submitCategory = async () => {
  if (!categoryFormData.value.categoryName.trim()) {
    ElMessage.warning('请输入分类名称')
    return
  }

  try {
    if (isEditingCategory.value) {
      // 编辑分类
      await post('/api/doc/admin/category/update', {
        id: categoryFormData.value.id,
        categoryName: categoryFormData.value.categoryName
      })
      ElMessage.success('修改成功')
    } else {
      // 添加分类
      await post('/api/doc/admin/category', {
        categoryName: categoryFormData.value.categoryName
      })
      ElMessage.success('添加成功')
    }
    showCategoryForm.value = false
    loadCategories()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error(isEditingCategory.value ? '修改失败' : '添加失败')
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
    await del(`/api/doc/admin/${currentDeleteItem.value.id}`)
    ElMessage.success('删除成功啦 (｡･ω･｡)ﾉ')
    showDeleteConfirm.value = false
    currentDeleteItem.value = null
    loadData()
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败了呢 (´;ω;｀)')
  }
}

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

.content-wrapper {
  display: flex;
  gap: 16px;
  margin-top: 16px;
}

.main-content {
  flex: 1;
  min-width: 0;
}

.friends-table {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(255, 182, 193, 0.15);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  background: #fff;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 8px;
  margin-bottom: 0;
  border-bottom: 2px solid #ffecef;
}

.page-header h3 {
  font-size: 20px;
  color: #333;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.button-text {
  font-size: 15px;
  font-weight: bold;
}

.add-button {
  margin-left: auto;
  background: #fff;
  border: 1px solid #ffb6c1;
  padding: 0 16px;
  height: 32px;
  border-radius: 16px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
  color: #ff69b4;
  font-size: 13px;
}

.add-button:hover {
  background: #fff5f7;
  border-color: #ff69b4;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 105, 180, 0.1);
}

.add-button:active {
  transform: translateY(0);
}

.filter-section {
  margin-top: 5px;
  margin-bottom: 5px;
}

.category-sidebar {
  width: 280px;
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(255, 182, 193, 0.15);
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: fit-content;
  margin-top: 10px;
}

.category-header h3 {
  font-size: 16px;
  color: #333;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.category-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #fff5f7;
  border-radius: 8px;
  margin-bottom: 8px;
  transition: all 0.3s ease;
}

.category-item:hover {
  transform: translateX(5px);
  background: #ffe4e8;
}

.category-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.category-name {
  font-weight: 500;
  color: #333;
}

.category-count {
  font-size: 11.5px;
  color: #ff69b4;
  background: white;
  padding: 1px 7px;
  border-radius: 8px;
  border: 1px solid #ffd6db;
  line-height: 1.3;
}

.category-item .category-actions {
  display: none;
  gap: 4px;
}

.category-item .edit-button,
.category-item .delete-button {
  padding: 5px 9px;
  font-size: 12.5px;
  border-radius: 4px;
  min-width: 35px;
  font-weight: 500;
}

.category-item .edit-button {
  background: #fff5f7;
  color: #ff69b4;
  border: 1px solid #ffd6db;
}

.category-item .delete-button {
  background: #fff1f0;
  color: #ff4d4f;
  border: 1px solid #ffd6db;
}

.category-item .edit-button:hover {
  background: #ffe4e8;
  transform: translateY(-1px);
}

.category-item .delete-button:hover {
  background: #ffd6d6;
  transform: translateY(-1px);
}

.category-item:hover .category-actions {
  display: flex;
}

.add-category-button {
  margin-top: auto;
  padding: 8px;
  background: #fff5f7;
  border: 1px dashed #ffb6c1;
  border-radius: 8px;
  color: #ff69b4;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.add-category-button:hover {
  background: #ffe4e8;
}

.filter-content {
  display: flex;
  gap: 16px;
  align-items: center;
  position: relative;
  justify-content: flex-start;
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
  height: 32px;
  padding: 0 12px 0 40px;
  border: 1px solid #ddd;
  border-radius: 16px;
  font-size: 13px;
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
  width: 16px;
  height: 16px;
  opacity: 0.5;
}

th {
  padding: 11px;
  text-align: left;
  background: #fff5f7;
  color: #515151;
  font-weight: 600;
  font-size: 14px;
  border-bottom: 2px solid #ffe4e8;
  transition: all 0.3s ease;
}

td {
  padding: 11px;
  border-bottom: 1px solid #ffe4e8;
  font-size: 14px;
  color: #666;
  transition: all 0.3s ease;
  vertical-align: middle;
  line-height: 1.2;
}

tr {
  transition: all 0.3s ease;
  position: relative;
  height: auto;
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

.content-text, .writing-text {
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.2;
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
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  white-space: nowrap;
  text-align: center;
  min-width: 64px;
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
  margin-top: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  bottom: 0;
  z-index: 10;
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

.select-arrow.arrow-up {
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

.category-form-dialog {
  width: 400px;
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
}

.form-input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ffe4e8;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s;
}

.form-input:focus {
  outline: none;
  border-color: #ff69b4;
  box-shadow: 0 0 0 2px rgba(255, 105, 180, 0.1);
}

.modal-footer {
  padding: 16px 20px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  border-top: 1px solid #ffe4e8;
}

/* 移除之前的 category-filter 相关样式 */
/* 使用与用户管理页面完全相同的样式类名 */
.role-filter {
  position: relative;
  min-width: 120px;
  cursor: pointer;
  transition: all 0.3s ease;
  transform-origin: left center;
  flex: 0 0 auto;
  margin-left: 10px;
}

.role-selected {
  height: 32px;
  padding: 0 12px;
  background: white;
  border: 1px solid #ddd;
  border-radius: 16px;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  font-size: 13px;
}

.role-filter:hover .role-selected {
  border-color: #ddd;
  box-shadow: 0 0 0 2px rgba(0, 0, 0, 0.1);
  transform: scale(1.15);
}

.role-icon {
  width: 16px;
  height: 16px;
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
  transition: transform 0.3s ease;
  transform: rotate(0deg);
}

.role-filter:hover .dropdown-arrow {
  color: #ff69b4;
}

.arrow-up {
  transform: rotate(180deg);
}

.role-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  margin-top: 8px;
  min-width: 100%;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  z-index: 99999;
  animation: dropdownFadeIn 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  transform-origin: top center;
  border: 1px solid #ffe4e8;
}

@keyframes dropdownFadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.role-option {
  padding: 8px 12px;
  transition: all 0.2s ease;
  font-size: 13px;
  white-space: nowrap;
  line-height: 1.2;
  color: #666;
}

.role-option:hover {
  background: #fff5f7;
  color: #ff69b4;
}

.role-option.active {
  background: #fff5f7;
  color: #ff69b4;
  font-weight: 500;
}

.title-cell {
  font-weight: 500;
  color: #333;
}

.content-cell, .writing-cell {
  max-width: 200px;
}

.url-cell {
  max-width: 200px;
  padding: 11px;
}

.doc-url {
  color: #ff69b4;
  text-decoration: none;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.2;
  max-height: none;
  transition: all 0.3s ease;
  font-size: 13px;
  word-break: break-all;
}

.doc-url:hover {
  color: #ff8da1;
  text-decoration: underline;
}

.no-url {
  color: #999;
  font-size: 13px;
}

.category-tag {
  display: inline-block;
  padding: 4px 12px;
  background: #fff5f7;
  color: #ff69b4;
  border-radius: 12px;
  font-size: 13px;
  border: 1px solid #ffd6db;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
  max-width: 100%;
}

th {
  white-space: nowrap;
  padding: 11px;
}

td {
  padding: 11px;
}

.time-cell {
  color: #666;
  font-size: 13px;
  white-space: nowrap;
}

.image-cell {
  padding: 8px;
}

.image-wrapper {
  position: relative;
  width: 100%;
  padding-top: 33.33%; /* 3:1 aspect ratio */
  background: #f8f9fa;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.image-wrapper:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.cover-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  cursor: pointer;
}

.no-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff5f7;
  border: 1px dashed #ffd6db;
  border-radius: 8px;
}

.no-image-icon {
  width: 24px;
  height: 24px;
  opacity: 0.5;
}

.image-preview-dialog {
  max-width: 90vw;
  max-height: 90vh;
}

.image-preview-dialog .el-message-box__content {
  padding: 0;
  overflow: auto;
}

.operation-buttons-vertical {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: stretch;
}

.operation-buttons-vertical .edit-button,
.operation-buttons-vertical .delete-button {
  padding: 6px 0;
  width: 100%;
  min-width: 60px;
  text-align: center;
  border-radius: 6px;
  font-size: 13px;
}

.operation-buttons-vertical .edit-button:hover,
.operation-buttons-vertical .delete-button:hover {
  transform: translateX(2px);
}
</style>
