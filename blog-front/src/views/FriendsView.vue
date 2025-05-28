<!-- views/PostPage.vue -->
<template>
  <div class="main-container">
    <div class="left-panel">
      <div class="content-wrapper">
        <!-- 加载状态 -->
        <div v-if="loading && posts.length === 0" class="loading-wrapper">
          <el-skeleton :rows="5" animated style="padding: 20px;" />
        </div>
        
        <!-- 错误提示 -->
        <el-alert
          v-if="error"
          title="加载失败"
          type="error"
          description="无法加载朋友圈内容，请稍后重试"
          show-icon
          style="margin-bottom: 20px;"
        />
        
        <!-- 朋友圈列表 -->
        <PostList 
          v-if="posts.length > 0" 
          :posts="posts" 
          @comment="handleComment"
        />
        
        <!-- 底部加载更多提示 -->
        <div v-if="loading && posts.length > 0" class="loading-more">
          <el-skeleton :rows="1" animated />
        </div>
        
        <!-- 全部加载完毕提示 -->
        <div v-if="!loading && !hasMore && posts.length > 0" class="no-more">
          已经到底啦，没有更多内容了~
        </div>
      </div>
    </div>
    <div class="right-panel">
      <div class="content-wrapper">
        <PostChart />
      </div>
    </div>
  </div>
</template>


<script setup>
import PostList from '@/components/PostList.vue'
import PostChart from '@/components/PostChart.vue'
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { post } from '@/api'

// 数据加载状态
const loading = ref(false);
const error = ref(false);
const posts = ref([]);
const total = ref(0);
const hasMore = ref(true); // 是否还有更多数据

// 分页参数
const pageNum = ref(1);
const pageSize = ref(5); // 每次加载5条朋友圈

// 获取朋友圈列表数据
const fetchPosts = async () => {
  if (loading.value) return;
  
  loading.value = true;
  error.value = false;
  
  try {
    // 构建查询参数
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    };
    
    const res = await post.getFriendsPosts(params);
    
    // 转换后端数据结构为前端需要的格式
    const formattedPosts = (res.data.records || []).map(item => ({
      id: item.id,
      username: item.username || 'YuXin.Dev', // 如果后端没有提供，使用默认值
      avatar: item.avatar || user, // 如果后端没有提供，使用默认头像
      time: formatTime(item.createTime),
      content: item.text,
      images: item.imageUrls || [],
      location: item.location,
      likes: item.likeCount,
      comments: (item.commentVos || []).map(comment => ({
        id: comment.commentId,
        username: comment.username || '匿名用户',
        avatar: comment.avatar ? comment.avatar : anonymousImg,
        content: comment.content
      }))
    }));
    
    if (pageNum.value === 1) {
      // 首次加载或重置
      posts.value = formattedPosts;
    } else {
      // 加载更多
      posts.value = [...posts.value, ...formattedPosts];
    }
    
    total.value = res.data.total || 0;
    
    // 判断是否还有更多数据
    hasMore.value = posts.value.length < total.value;
  } catch (err) {
    console.error('获取朋友圈列表失败:', err);
    error.value = true;
    ElMessage.error('获取朋友圈列表失败');
    
    // 如果API未就绪或出错，使用模拟数据(仅开发用)
    if (pageNum.value === 1) {
      posts.value = mockPosts;
      hasMore.value = false;
    }
  } finally {
    loading.value = false;
  }
};

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '未知时间';
  
  try {
    const date = new Date(timeStr);
    
    // 格式化为"2025年1月13日 19点30分"格式
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const hours = date.getHours();
    const minutes = date.getMinutes();
    
    return `${year}年${month}月${day}日 ${hours}点${minutes}分`;
  } catch (e) {
    console.error('日期格式化错误:', e);
    return timeStr; // 如果格式化失败，返回原始字符串
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
    pageNum.value += 1;
    fetchPosts();
  }
};

// 页面加载时获取数据并添加滚动监听
onMounted(() => {
  fetchPosts();
  
  // 添加滚动事件监听
  window.addEventListener('scroll', handleScroll);
});

// 页面卸载时移除滚动监听
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});

// 模拟数据(用于开发阶段，当API未就绪时使用)
import logoImg from '@/assets/icons/logo.png'
import anonymousImg from '@/assets/icons/anonymous.png'
import user from '@/assets/icons/user.png'

const mockPosts = [
  {
    id: 1,
    username: 'YuXin.Dev',
    avatar: user,
    time: '2025年1月13日 19点30分',
    content: '"一双石塔是东西，舟子传言是乌镇。"',
    images: [logoImg, logoImg, logoImg, logoImg, logoImg,logoImg,logoImg],
    location: '浙江绍兴',
    likes: 12,
    comments: [
      {
        username: '匿名用户',
        avatar: anonymousImg,
        content: '新鲜666！！！'
      }
    ]
  },
  {
    id: 2,
    username: 'YuXin.Dev',
    avatar: user,
    time: '2025年1月12日 17点00分',
    content: '今天的阳光真不错 ☀️红红火火恍恍惚惚哈哈哈哈好哈哈哈好好好好哈哈哈哈好好好好好好哈哈哈哈哈好好好好哈哈哈哈哈哈好好好火火恍恍惚惚好好好',
    images: [logoImg, logoImg, logoImg],
    location: '浙江杭州',
    likes: 8,
    comments: []
  }
]

// 处理评论提交
const handleComment = async (commentData) => {
  try {
    await post.makeComment({
      postId: commentData.postId,
      content: commentData.content
    });
    
    // 评论成功后刷新数据
    ElMessage.success('评论发表成功');
    pageNum.value = 1; // 重置到第一页
    await fetchPosts(); // 重新加载数据
  } catch (error) {
    console.error('评论发表失败:', error);
    ElMessage.error('评论发表失败：' + (error.message || '未知错误'));
  }
};
</script>

<style scoped>
.main-container {
  display: flex;
  width: 100%;
  max-width: 1800px;
  margin: 0 auto;
  padding: 20px;
}

.left-panel,
.right-panel {
  display: flex;
}

.left-panel {
  width: 66.66%;
  justify-content: center; /* 子项居中 */
}

.right-panel {
  width: 33.33%;
  justify-content: center; /* 子项居中 */
}

.content-wrapper {
  width: 100%;
  max-width: 800px; /* 限制每块内容最大宽度 */
}

.loading-wrapper {
  background-color: #fff;
  border-radius: 16px;
  box-shadow: 0 0 10px rgba(0,0,0,0.5);
  border: 4px solid #ffd6db;
  margin-bottom: 20px;
}

.loading-more {
  padding: 20px;
  text-align: center;
  background-color: #fff;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 0 10px rgba(0,0,0,0.1);
  border: 2px solid #ffd6db;
}

.no-more {
  padding: 15px;
  text-align: center;
  color: #ffaed2;
  font-size: 14px;
  background-color: #fff;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 0 10px rgba(0,0,0,0.1);
  border: 2px solid #ffd6db;
}
</style>
