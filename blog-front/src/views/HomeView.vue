<template>
  <div class="main-content">
    <div class="home">
      <div class="content-wrapper">
        <!-- 加载状态 -->
        <div v-if="loading && docs.length === 0" class="loading animate-item">
          <el-skeleton :rows="5" animated />
        </div>
        
        <!-- 错误提示 -->
        <el-alert
          v-if="error"
          title="加载失败"
          type="error"
          description="无法加载技术文档，请稍后重试"
          show-icon
          class="animate-item"
        />
        
        <div v-if="!loading || docs.length > 0" class="posts-container">
          <div v-if="docs.length === 0" class="no-data animate-item">
            <el-empty description="暂无技术文档" />
          </div>
          
          <div class="post-row" v-for="(postPair, index) in chunkedDocs" :key="index">
            <PostCard
              v-for="doc in postPair"
              :key="doc.id"
              :id="doc.id"
              :title="doc.title || '无标题'"
              :description="(doc.general || '暂无描述') + '...'"
              :image="doc.backgroundUrl || '/images/desert.png'"
              :date="doc.writeTime || '未知日期'"
              class="animate-item"
              :style="{ animationDelay: `${index * 0.15 + 0.1}s` }"
              @click="viewDoc(doc.id)"
            />
          </div>
          
          <!-- 底部加载更多提示 -->
          <div v-if="loading && docs.length > 0" class="loading-more animate-item">
            <el-skeleton :rows="1" animated />
          </div>
          
          <!-- 全部加载完毕提示 -->
          <div v-if="!loading && !hasMore && docs.length > 0" class="no-more animate-item">
            已经到底啦，没有更多内容了~
          </div>
        </div>
        
        <div class="sidebar">
          <SearchBox 
            class="animate-item sidebar-item" 
            style="animation-delay: 0.1s" 
            @search="handleSearch"
          />
          <CategoryList 
            :categories="categories" 
            :loading="loadingCategories"
            :active-id="selectedCategoryId"
            @select="handleCategorySelect"
            class="animate-item sidebar-item" 
            style="animation-delay: 0.2s" 
          />
          <RecentPosts class="animate-item sidebar-item" style="animation-delay: 0.3s" />
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import PostCard from "@/components/PostCard.vue";
import SearchBox from "@/components/SearchBox.vue";
import CategoryList from "@/components/CategoryList.vue";
import RecentPosts from "@/components/RecentPosts.vue";
import { doc } from '@/api';

// 数据加载状态
const loading = ref(true);
const error = ref(false);
const docs = ref([]);
const total = ref(0);
const hasMore = ref(true); // 是否还有更多数据
const categories = ref([]); // 分类数据
const loadingCategories = ref(true); // 分类加载状态

// 分页参数
const pageNum = ref(1);
const pageSize = ref(6); // 每次加载6条数据

// 当前选择的分类ID和搜索关键词
const selectedCategoryId = ref(null);
const searchKeyword = ref('');

// 获取文档列表数据（首次加载）
const fetchDocList = async () => {
  loading.value = true;
  error.value = false;
  
  try {
    // 构建查询参数
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    };
    
    // 添加分类参数
    if (selectedCategoryId.value) {
      params.categoryId = selectedCategoryId.value;
    }
    
    // 添加搜索参数
    if (searchKeyword.value) {
      params.search = searchKeyword.value;
    }
    
    const res = await doc.getDocPage(params);
    console.log('API响应数据:', res); // 添加调试日志
    
    if (pageNum.value === 1) {
      // 首次加载或重置分页
      docs.value = res.data.records || [];
    } else {
      // 加载更多
      docs.value = [...docs.value, ...(res.data.records || [])];
    }
    console.log('处理后的文档数据:', docs.value); // 添加调试日志
    
    total.value = res.data.total || 0;
    hasMore.value = docs.value.length < total.value;
  } catch (err) {
    console.error('获取技术文档列表失败:', err);
    error.value = true;
    ElMessage.error('获取技术文档列表失败');
  } finally {
    loading.value = false;
  }
};

// 加载更多数据
const loadMore = async () => {
  // 如果正在加载、没有更多数据或出错，则不加载
  if (loading.value || !hasMore.value || error.value) return;
  
  loading.value = true;
  pageNum.value += 1;
  
  try {
    // 构建查询参数
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    };
    
    // 添加分类参数
    if (selectedCategoryId.value) {
      params.categoryId = selectedCategoryId.value;
    }
    
    // 添加搜索参数
    if (searchKeyword.value) {
      params.search = searchKeyword.value;
    }
    
    const res = await doc.getDocPage(params);
    const newDocs = res.data.records || [];
    
    // 追加新数据
    docs.value = [...docs.value, ...newDocs];
    
    // 判断是否还有更多数据
    hasMore.value = docs.value.length < total.value;
  } catch (err) {
    console.error('加载更多文档失败:', err);
    ElMessage.error('加载更多文档失败');
    pageNum.value -= 1; // 恢复页码
  } finally {
    loading.value = false;
  }
};

// 滚动到底部检测
const handleScroll = () => {
  // 如果正在加载或没有更多数据，则不处理
  if (loading.value || !hasMore.value) return;
  
  const scrollHeight = document.documentElement.scrollHeight;
  const scrollTop = document.documentElement.scrollTop;
  const clientHeight = document.documentElement.clientHeight;
  
  // 当滚动到距离底部200px时，加载更多数据
  if (scrollTop + clientHeight >= scrollHeight - 200) {
    loadMore();
  }
};

// 查看文档详情
const viewDoc = (id) => {
  // 找到对应ID的文档
  const targetDoc = docs.value.find(doc => doc.id === id);
  if (targetDoc && targetDoc.url) {
    // 在新标签页中打开文档URL
    window.open(targetDoc.url, '_blank');
  } else {
    ElMessage({
      message: '无法打开文档链接',
      type: 'warning'
    });
  }
};

// 将文档列表每两个一组
function chunkArray(array, size) {
  const chunks = [];
  for (let i = 0; i < array.length; i += size) {
    chunks.push(array.slice(i, i + size));
  }
  return chunks;
}

const chunkedDocs = computed(() => {
  const chunks = chunkArray(docs.value, 2);
  console.log('分组后的文档数据:', chunks); // 添加调试日志
  return chunks;
});

// 获取分类列表
const fetchCategories = async () => {
  loadingCategories.value = true;
  
  try {
    const res = await doc.getDocCategories();
    categories.value = res.data || [];
  } catch (err) {
    console.error('获取文档分类失败:', err);
    ElMessage.error('获取文档分类失败');
  } finally {
    loadingCategories.value = false;
  }
};

// 最近文档，取前5篇文档
const recentDocs = computed(() => {
  return docs.value.slice(0, 5).map(doc => ({
    id: doc.id,
    date: formatDate(doc.writeTime),
    title: doc.title || '无标题',
    url: doc.url
  }));
});

// 格式化日期 YYYY-MM-DD
const formatDate = (dateStr) => {
  if (!dateStr) return '未知日期';
  // 如果日期格式已经是 YYYY-MM-DD，只提取月和日
  if (dateStr.includes('-')) {
    const parts = dateStr.split('-');
    if (parts.length >= 3) {
      return `${parts[1]}-${parts[2]}`;
    }
  }
  
  // 否则按照之前的方式处理
  const date = new Date(dateStr);
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${month}-${day}`;
};

// 处理分类选择
const handleCategorySelect = (categoryId) => {
  if (selectedCategoryId.value === categoryId) {
    // 如果点击的是已选中的分类，则取消筛选
    selectedCategoryId.value = null;
  } else {
    selectedCategoryId.value = categoryId;
  }
  
  // 重置分页并获取数据
  pageNum.value = 1;
  docs.value = [];
  fetchDocList();
};

// 处理搜索
const handleSearch = (keyword) => {
  searchKeyword.value = keyword;
  
  // 重置分页并获取数据
  pageNum.value = 1;
  docs.value = [];
  fetchDocList();
};

// 页面加载时获取数据并添加滚动监听
onMounted(() => {
  fetchDocList();
  fetchCategories(); // 获取分类数据
  
  // 添加滚动事件监听
  window.addEventListener('scroll', handleScroll);
});

// 页面卸载时移除滚动监听
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});
</script>

<style scoped>
.home {
  display: flex;
  justify-content: center;
  padding: 0px;
  width: 100%;
}

.content-wrapper {
  position: relative;
  width: 100%;
  max-width: 1400px; /* 增加整体宽度，给侧边栏更多空间 */
  padding-right: 400px; /* 增加右侧padding */
  margin: 0 auto;
}

.loading {
  width: 100%;
  padding: 20px 0;
}

.loading-more {
  width: 100%;
  padding: 20px 0;
  text-align: center;
}

.no-more {
  width: 100%;
  padding: 20px 0;
  text-align: center;
  color: #909399;
  font-size: 14px;
}

.no-data {
  width: 100%;
  margin: 30px 0;
}

/* 主内容区域居中 */
.posts-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start; /* 改为左对齐 */
  width: 100%;
  max-width: 1220px;
  margin: 0 auto; /* 整体容器居中 */
}

/* 每行的 PostCard */
.post-row {
  display: flex;
  gap: 25px;
  margin-bottom: 30px;
  width: 100%;
  justify-content: flex-start;
  flex-wrap: wrap; /* 允许换行 */
}

.post-row > * {
  flex: 0 0 calc(50% - 12.5px); /* 固定宽度，考虑间距 */
  min-width: 0;
  max-width: calc(50% - 12.5px); /* 确保不会超过预期宽度 */
  transition: all 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  overflow: hidden;
}

/* 光泽扫过效果 */
.post-row > *::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    45deg,
    transparent,
    rgba(255, 255, 255, 0.1),
    rgba(255, 255, 255, 0.5),
    rgba(255, 255, 255, 0.1),
    transparent
  );
  transform: rotate(45deg);
  animation: shimmer 3s infinite;
  z-index: 1;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.post-row > *:hover::before {
  opacity: 1;
}

@keyframes shimmer {
  0% {
    transform: translate(-50%, -50%) rotate(45deg);
  }
  100% {
    transform: translate(150%, 150%) rotate(45deg);
  }
}

.sidebar {
  position: absolute;
  right: 0;
  top: 0;
  width: 380px; /* 增加侧边栏宽度 */
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 25px;
  padding: 0 25px; /* 略微增加内边距 */
}

.sidebar > * {
  width: 100%;
  max-width: 330px; /* 增加组件最大宽度 */
}

/* 侧边栏项目悬停放大效果 */
.sidebar .sidebar-item {
  transition: all 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
  transform-origin: center;
  border-radius: 12px;
  will-change: transform;
  position: relative;
  overflow: hidden;
}

.sidebar .sidebar-item:hover {
  transform: translateY(-8px) scale(1.05);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
}

/* 光泽扫过效果 */
.sidebar .sidebar-item::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    45deg,
    transparent,
    rgba(255, 255, 255, 0.1),
    rgba(255, 255, 255, 0.5),
    rgba(255, 255, 255, 0.1),
    transparent
  );
  transform: rotate(45deg);
  animation: shimmer 3s infinite;
  z-index: 1;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.sidebar .sidebar-item:hover::before {
  opacity: 1;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .content-wrapper {
    padding-right: 0;
  }

  .sidebar {
    position: static;
    width: 100%;
    padding: 20px 0;
    margin-top: 20px;
  }

  .sidebar > * {
    max-width: 100%;
  }

  .post-row {
    flex-direction: column;
    gap: 20px;
    margin-bottom: 20px;
  }

  .post-row > * {
    flex: 0 0 100%; /* 移动端下占满宽度 */
  }

  .animate-item:hover {
    transform: translateY(-8px) scale(1.04);
  }
}

.animate-item {
  opacity: 0;
  transform: translateY(20px);
  animation: fadeInUp 0.8s cubic-bezier(0.4, 0, 0.2, 1) forwards;
  transition: all 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  overflow: hidden;
}

.animate-item:hover {
  transform: translateY(-12px) scale(1.08);
  box-shadow: 
    0 20px 35px rgba(0, 0, 0, 0.1),
    0 15px 15px rgba(0, 0, 0, 0.08);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
