<template>
  <div class="recent-posts" v-bind="attrs">
    <div class="recent-header">
      <img src="@/assets/icons/近期文章.png" alt="近期文章" class="recent-icon" />
      <h3>近期文章</h3>
    </div>
    <div class="recent-year">
      {{ currentYear }}
    </div>
    <ul class="posts-list">
      <li v-for="post in recentPosts" :key="post.id" class="post-item" @click="handlePostClick(post)">
        <div class="post-meta">
          <span class="post-date">{{ post.writeTime }}</span>
          <span class="post-title">{{ post.title }}</span>
        </div>
        <div class="post-arrow">
          <i class="el-icon-arrow-right"></i>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useAttrs } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';
import { API_BASE_URL } from '@/config';

const attrs = useAttrs();
const recentPosts = ref([]);

// 获取当前年份
const currentYear = computed(() => {
  return new Date().getFullYear();
});

// 获取最近文章数据
const fetchRecentPosts = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/api/doc/recently`);
    if (response.data.code === 200) {
      recentPosts.value = response.data.data;
    } else {
      throw new Error(response.data.message || '获取最近文章失败');
    }
  } catch (error) {
    console.error('获取最近文章失败:', error);
    ElMessage.error('获取最近文章失败');
  }
};

// 处理文章点击
const handlePostClick = (post) => {
  if (post.url) {
    window.open(post.url, '_blank');
  } else {
    ElMessage({
      message: '无法打开文档链接',
      type: 'warning'
    });
  }
};

onMounted(() => {
  fetchRecentPosts();
});

// 使组件可以继承父组件传递的类
defineOptions({
  inheritAttrs: false
});
</script>

<style scoped>
.recent-posts {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8), rgba(255, 255, 255, 0.6));
  border-radius: 16px;
  padding: 20px;
  box-shadow: 
    0 4px 15px rgba(0, 0, 0, 0.05),
    0 8px 25px rgba(0, 0, 0, 0.03);
  backdrop-filter: blur(10px);
  transition: all 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.recent-posts:hover {
  transform: translateY(-5px);
  box-shadow: 
    0 8px 25px rgba(0, 0, 0, 0.08),
    0 12px 35px rgba(0, 0, 0, 0.06);
}

.recent-year {
  margin: 15px 0;
  color: #2c3e50;
  font-weight: 600;
  font-size: 0.9rem;
  letter-spacing: 0.5px;
}

.recent-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 12px;
  border-bottom: 2px solid rgba(0, 0, 0, 0.05);
}

.recent-icon {
  width: 24px;
  height: 24px;
  margin-right: 12px;
  transition: transform 0.3s ease;
}

.recent-posts:hover .recent-icon {
  transform: rotate(15deg);
}

.recent-header h3 {
  margin: 0;
  font-size: 1.2rem;
  color: #2c3e50;
  font-weight: 600;
}

.posts-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.post-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 12px 10px;
  margin: 6px 0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.5);
  user-select: none;
  min-height: 64px;
}

.post-item:hover {
  background: rgba(255, 255, 255, 0.9);
  transform: translateX(5px);
}

.post-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
  padding-right: 8px;
  min-width: 0;
}

.post-date {
  font-size: 0.8rem;
  color: #94a3b8;
  font-weight: 500;
  flex-shrink: 0;
}

.post-title {
  color: #334155;
  font-size: 0.95rem;
  font-weight: 500;
  transition: color 0.3s ease;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.5;
  word-break: normal;
  word-wrap: break-word;
  white-space: normal;
  min-width: 0;
}

.post-item:hover .post-title {
  color: #3b82f6;
}

.post-arrow {
  color: #94a3b8;
  font-size: 0.9rem;
  transition: transform 0.3s ease;
  padding-top: 4px;
  flex-shrink: 0;
  width: 16px;
  text-align: center;
  margin-left: 4px;
}

.post-item:hover .post-arrow {
  transform: translateX(3px);
  color: #3b82f6;
}
</style>
