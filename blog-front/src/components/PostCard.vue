<template>
  <div class="post-card" @click="$emit('click')">
    <div class="post-image" v-if="image">
      <img :src="image" alt="Post Image" @error="handleImageError" />
    </div>
    <div class="post-image placeholder" v-else>
      <div class="placeholder-image">暂无图片</div>
    </div>
    <div class="post-content">
      <div class="title-row">
        <h3 class="post-title">{{ title }}</h3>
        <span class="post-date">{{ date }}</span>
      </div>
      <p class="post-description">{{ description }}</p>
      <div class="post-actions">
        <button class="read-more" @click.stop="$emit('click')">阅读全文</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  id: {
    type: Number,
    required: true
  },
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    default: ''
  },
  image: {
    type: String,
    default: ''
  },
  date: {
    type: String,
    default: '未知日期'
  }
});

const handleImageError = (e) => {
  // 图片加载失败时使用默认图片
  e.target.src = '/images/desert.png'
}
</script>

<style scoped>
.post-card {
  background-color: rgba(255, 255, 255, 0.6);
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  backdrop-filter: blur(5px);
  display: flex;
  flex-direction: column;
  min-height: 220px;
  margin: 0 auto;
  width: 100%;
  max-width: 800px;
  cursor: pointer;
}

.post-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.post-image {
  width: 98%;
  height: 180px;
  overflow: hidden;
  position: relative;
  margin: -8px auto 0 auto; /* 上边距-8px，往上移动 */
}

.post-image img {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 保证图片宽高撑满容器，保持比例，裁剪多余 */
  object-position: center; /* 裁剪时以图片中心为基准 */
  transition: transform 0.5s ease;
  position: relative;
}

.placeholder-image {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(245, 245, 245, 0.6);
  color: #999;
  border-radius: 15px;
}

.post-card:hover .post-image img {
  transform: scale(1.05);
}

.post-content {
  padding: 16px 10px;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  gap: 12px;
}

.title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  width: 100%;
}

.post-title {
  margin: 0;
  font-size: 1rem;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
}

.post-description {
  color: #666;
  font-size: 0.85rem;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.read-more {
  background-color: #f3a953;
  color: white;
  border: none;
  padding: 4px 10px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.8rem;
  transition: transform 0.3s ease, background-color 0.3s ease;
  align-self: flex-start;
}

.read-more:hover {
  background-color: #e69842;
  transform: scale(1.1); /* 鼠标悬停时放大按钮 */
}

.post-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.post-date {
  color: #888;
  font-size: 0.75rem;
  white-space: nowrap;
}

@media (max-width: 768px) {
  .post-card {
    min-height: 220px;
    max-width: 100%;
  }

  .post-image {
    height: 150px;
  }

  .post-content {
    padding: 16px 8px;
  }
}
</style>
