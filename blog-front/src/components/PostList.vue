<!-- components/PostList.vue -->
<template>
  <div class="friends-circle">
    <div
        class="friends-post"
        v-for="item in posts"
        :key="item.id"
    >
      <div class="post-header">
        <div class="user-profile">
          <img :src="item.avatar" alt="用户头像" class="avatar" />
          <div class="user-info">
            <h3>{{ item.username }}</h3>
            <p>{{ item.time }}</p>
          </div>
        </div>
      </div>

      <div class="post-content">
        <p class="post-text" style="white-space: pre-wrap;">{{ item.content }}</p>
        <div class="image-gallery" v-if="item.images && item.images.length > 0">
          <div
              class="image-row"
              v-for="(row, rowIndex) in splitImages(item.images)"
              :key="rowIndex"
              :class="{ 'single-image-row': item.images.length === 1 }"
          >
            <img
                v-for="(img, i) in row"
                :key="i"
                :src="img"
                alt="风景照"
                class="gallery-image"
                :class="{ 'single-image': item.images.length === 1 }"
                @click="previewImage(img)"
            />
          </div>
        </div>

      </div>
      <div class="post-footer">
        <div class="location">
          <i class="location-icon"></i>
          <span style="font-weight:500">{{ item.location }}</span>
        </div>

        <div class="post-actions">
          <div class="action-button like-button" @click="handleLike(item.id)">
            <img src="@/assets/icons/like.png" alt="点赞" class="action-icon" />
            <span>{{ item.likes }}</span>
          </div>
          <div class="action-button" @click="toggleCommentInput(item.id)">
            <img src="@/assets/icons/comment.png" alt="评论" class="action-icon" />
            <span>{{ item.comments?.length || 0 }}</span>
          </div>
        </div>
      </div>

      <!-- 评论输入框 -->
      <div class="comment-input-container" v-if="activeCommentId === item.id">
        <input
          type="text"
          class="comment-input"
          v-model="commentText"
          placeholder="写下你的评论..."
          @keyup.enter="submitComment(item.id)"
        />
        <button class="comment-submit" @click="submitComment(item.id)">发送</button>
      </div>

      <div class="comments-section" v-if="item.comments && item.comments.length > 0">
        <div
            class="comment"
            v-for="(comment, index) in item.comments"
            :key="comment.id || index"
        >
          <img :src="comment.avatar" alt="用户头像" class="comment-avatar" />
          <div class="comment-content">
            <span class="comment-username">{{ comment.username }}</span>
            <span class="comment-text">{{ comment.content }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 图片预览组件 -->
    <ImagePreview
      v-model:visible="previewVisible"
      :image-url="currentPreviewImage"
    />
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits, onUnmounted } from 'vue'
import axios from 'axios'
import ImagePreview from './ImagePreview.vue'
import { API_BASE_URL } from '@/config'

const props = defineProps({
  posts: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['comment'])

// 当前激活的评论输入框对应的帖子ID
const activeCommentId = ref(null)
// 评论内容
const commentText = ref('')

// 用于存储每个帖子的点赞计数器
const likeCounters = ref({})
// 用于存储防抖定时器
const likeTimers = ref({})

const previewVisible = ref(false)
const currentPreviewImage = ref('')

// 切换评论输入框的显示状态
const toggleCommentInput = (postId) => {
  if (activeCommentId.value === postId) {
    activeCommentId.value = null
  } else {
    activeCommentId.value = postId
    commentText.value = ''
  }
}

// 提交评论
const submitComment = (postId) => {
  if (!commentText.value.trim()) return

  // 向父组件发送评论事件
  emit('comment', {
    postId,
    content: commentText.value.trim()
  })

  // 清空评论内容
  commentText.value = ''
  // 隐藏评论输入框
  activeCommentId.value = null
}

// 处理图片点击
const previewImage = (imageUrl) => {
  currentPreviewImage.value = imageUrl
  previewVisible.value = true
}

// 修改图片分组逻辑
const splitImages = (images) => {
  if (!images || images.length === 0) return []

  // 如果只有一张图片，直接返回单个数组
  if (images.length === 1) {
    return [images]
  }

  // 多张图片保持原有的5张一行逻辑
  const rows = []
  for (let i = 0; i < images.length; i += 5) {
    rows.push(images.slice(i, i + 5))
  }
  return rows
}

// 处理点赞
const handleLike = (postId) => {
  // 初始化计数器（如果不存在）
  if (!likeCounters.value[postId]) {
    likeCounters.value[postId] = 0
  }

  // 增加本地计数
  likeCounters.value[postId]++

  // 更新UI显示
  const post = props.posts.find(p => p.id === postId)
  if (post) {
    post.likes++
  }

  // 清除之前的定时器（如果存在）
  if (likeTimers.value[postId]) {
    clearTimeout(likeTimers.value[postId])
  }

  // 设置新的定时器
  likeTimers.value[postId] = setTimeout(async () => {
    try {
      // 发送累积的点赞数到后端
      await axios.post(`${API_BASE_URL}/api/post/like`, {
        postId: postId,
        count: likeCounters.value[postId]
      })

      // 重置计数器
      likeCounters.value[postId] = 0
    } catch (error) {
      console.error('发送点赞请求失败:', error)
    }
  }, 1000) // 1秒延迟
}

// 在组件卸载时清理所有定时器
onUnmounted(() => {
  Object.values(likeTimers.value).forEach(timer => {
    clearTimeout(timer)
  })
})
</script>

<style scoped>
.friends-circle {
  max-width: 1500px;
  margin: 0 auto;
  padding: 5px 15px; /* 修改顶部内边距为0 */
  min-height: 100vh;
  background-color: #fff; /* 白色背景 */
  border-radius: 16px; /* 可以加圆角 */
  box-shadow: 0 0 10px rgba(0,0,0,0.5); /* 轻微阴影让框更明显 */
  border: 4px solid #ffd6db; /* 浅粉色边框 */
}
.comments-section {
  background-color: #FFF8F8;
  border-radius: 8px;
  padding: 12px;
  margin-top: 15px;
  border: 2px solid #ffd6db; /* 粉红色边框 */
}


.friends-post {
  background-color: #FFEFEF;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 15px;
  margin-bottom: 10px;
  margin-top: 10px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2px solid transparent;
  position: relative;
  overflow: hidden;
}

.friends-post::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 174, 210, 0.1), rgba(255, 126, 185, 0.05));
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.friends-post:hover {
  transform: translateY(-5px) scale(1.01);
  box-shadow: 0 8px 20px rgba(255, 126, 185, 0.2);
  border-color: #ffd6db;
}

.friends-post:hover::before {
  opacity: 1;
}

.friends-post:hover .post-header .avatar {
  transform: scale(1.05) rotate(5deg);
  border-color: #ff7eb9;
}

.friends-post:hover .post-text {
  color: #ff4d8d;
}

.friends-post:hover .gallery-image {
  filter: brightness(1.05);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.user-profile {
  display: flex;
  align-items: center;
}

.avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #ff7a93;
  margin-right: 12px;
  transition: all 0.3s ease;
}

.user-info h3 {
  margin: 0;
  font-size: 1.3rem;
  font-weight: 700;
}

.user-info p {
  margin: 3px 0 0;
  font-size: 0.8rem;
  color: #888;
}

.post-content {
  margin-bottom: 20px;
}

.post-text {
  font-size: 1.1rem;
  line-height: 1.6;
  margin-bottom: 15px;
  font-weight: 500;
  transition: color 0.3s ease;
  white-space: pre-wrap;
  word-wrap: break-word;
  word-break: break-word;
}

.image-gallery {
  display: flex;
  flex-direction: column;
  gap: 5px;
  margin: 15px 0;
}

.image-row {
  display: flex;
  gap: 5px;
}

.single-image-row {
  justify-content: flex-start;
}

.gallery-image {
  width: calc(20% - 4px);
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.single-image {
  width: 70%;
  height: 300px;
  object-fit: cover;
}

.gallery-image:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(255, 126, 185, 0.3);
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
  font-size: 0.85rem;
}

.location {
  display: flex;
  align-items: center;
  font-size: 0.9rem;
  color: #5c7cfa;
  margin-top: 10px;
}

.location-icon {
  width: 25px;
  height: 25px;
  background-image: url('@/assets/icons/location.png');
  background-size: contain;
  margin-right: 5px;
}

.post-actions {
  display: flex;
  gap: 20px;
  margin-top: 10px;
}

.action-button {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 15px;
  transition: all 0.3s ease;
  background-color: rgba(255, 255, 255, 0.5);
}

.action-button:hover {
  background-color: rgba(255, 105, 180, 0.1);
  transform: scale(1.05);
}

.action-icon {
  width: 20px;
  height: 20px;
  opacity: 0.8;
}

.action-button:hover .action-icon {
  opacity: 1;
}

.action-button span {
  color: #666;
  font-weight: 500;
}

.action-button:hover span {
  color: #ff69b4;
}

.comment-input-container {
  display: flex;
  margin-top: 15px;
  margin-bottom: 10px;
}

.comment-input {
  flex: 1;
  padding: 10px 15px;
  border: 2px solid #ffd6db;
  border-radius: 25px;
  background-color: white;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s;
}

.comment-input:focus {
  border-color: #ff7eb9;
}

.comment-submit {
  margin-left: 10px;
  padding: 8px 15px;
  background-color: #ffaed2;
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.3s;
}

.comment-submit:hover {
  background-color: #ff7eb9;
}

.comments-section {
  background-color: #FFF8F8;
  border-radius: 8px;
  padding: 12px;
  margin-top: 15px;
}

.comment {
  display: flex;
  align-items: center;  /* 垂直居中对齐 */
  margin-bottom: 10px;
}

.comment-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  margin-right: 10px;
}

.comment-content {
  flex: 1;
}

.comment-username {
  font-weight: 600;
  color: #5c7cfa;
  margin-right: 5px;
}

.comment-text {
  color: #333;
  font-weight: 500;
}

/* 针对移动端的响应式设计 */
@media (max-width: 768px) {
  .single-image {
    width: 100%;
    height: 200px;
  }

  .image-row {
    flex-direction: column;
  }

  .gallery-image {
    width: 100%;
    margin-bottom: 5px;
  }
}

.like-button {
  cursor: pointer;
  transition: all 0.3s ease;
}

.like-button:hover {
  transform: scale(1.1);
}

.like-button:active {
  transform: scale(0.95);
}

.action-icon {
  transition: transform 0.3s ease;
}

.like-button:hover .action-icon {
  transform: scale(1.2);
}

</style>
