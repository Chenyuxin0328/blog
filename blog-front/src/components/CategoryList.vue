<template>
  <div class="category-list" v-bind="attrs">
    <div class="category-header">
      <img src="@/assets/icons/文章分类.png" alt="分类" class="category-icon" />
      <h3>分类</h3>
    </div>

    <div v-if="loading" class="category-loading">
      <el-skeleton :rows="5" animated style="width: 100%" />
    </div>

    <ul v-else class="categories">
      <li v-if="categories.length === 0" class="category-empty">暂无分类</li>
      <li v-for="category in categories" :key="category.id" class="category-item">
        <a
          href="#"
          class="category-link"
          :class="{ 'category-active': activeId === category.id }"
          @click.prevent="handleCategoryClick(category)"
        >
          {{ category.categoryName }}
          <span class="category-count">({{ category.count }})</span>
        </a>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { useAttrs } from 'vue';

const attrs = useAttrs();
const emit = defineEmits(['select']);

const props = defineProps({
  categories: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  activeId: {
    type: [Number, String, null],
    default: null
  }
});

// 点击分类
const handleCategoryClick = (category) => {
  emit('select', category.id);
};

// 使组件可以继承父组件传递的类
defineOptions({
  inheritAttrs: false
});
</script>

<style scoped>
.category-list {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8), rgba(255, 255, 255, 0.6));
  border-radius: 16px;
  padding: 20px;
  box-shadow: 
    0 4px 15px rgba(0, 0, 0, 0.05),
    0 8px 25px rgba(0, 0, 0, 0.03);
  backdrop-filter: blur(10px);
  transition: all 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.category-list:hover {
  transform: translateY(-5px);
  box-shadow: 
    0 8px 25px rgba(0, 0, 0, 0.08),
    0 12px 35px rgba(0, 0, 0, 0.06);
}

.category-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 12px;
  border-bottom: 2px solid rgba(0, 0, 0, 0.05);
  margin-left: 0;
  margin-right: 0;
}

.category-icon {
  width: 24px;
  height: 24px;
  margin-right: 12px;
  transition: transform 0.3s ease;
}

.category-list:hover .category-icon {
  transform: rotate(15deg);
}

.category-header h3 {
  margin: 0;
  font-size: 1.2rem;
  color: #2c3e50;
  font-weight: 600;
}

.category-loading {
  padding: 10px 0;
}

.category-empty {
  padding: 10px 0;
  text-align: center;
  color: #909399;
  font-size: 14px;
}

.categories {
  list-style: none;
  padding: 0;
  margin: 0;
}

.category-item {
  padding: 8px 1px;
  border-bottom: 1px solid rgba(238, 238, 238, 0.7);
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.category-item:last-child {
  border-bottom: none;
}

.category-link {
  text-decoration: none;
  color: #2c3e50;
  font-size: 0.95rem;
  font-weight: 500;
  transition: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(5px);
  position: relative;
  overflow: hidden;
}

.category-count {
  color: #94a3b8;
  font-size: 0.85rem;
  font-weight: normal;
  transition: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
  background: rgba(255, 255, 255, 0.4);
  padding: 2px 8px;
  border-radius: 12px;
  position: relative;
  z-index: 1;
}

.category-link:hover {
  color: #2c3e50;
  background: rgba(255, 255, 255, 0.5);
  transform: translateX(8px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.category-link:hover .category-count {
  background: rgba(255, 174, 210, 0.2);
  color: #ff6b9d;
  transform: scale(1.1) translateX(-4px);
}

.category-link::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 3px;
  height: 100%;
  background: linear-gradient(to bottom, #ffaed2, #ff6b9d);
  opacity: 0;
  transition: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
  transform: scaleY(0.5);
}

.category-link::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 174, 210, 0.1), transparent);
  opacity: 0;
  transition: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.category-link:hover::before {
  opacity: 1;
  transform: scaleY(1);
}

.category-link:hover::after {
  opacity: 1;
}

.category-active {
  color: #ffffff !important;
  background: linear-gradient(135deg, #ffaed2, #ff6b9d) !important;
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(255, 174, 210, 0.3) !important;
  transform: translateX(8px);
}

.category-active .category-count {
  background: rgba(255, 255, 255, 0.2);
  color: rgba(255, 255, 255, 0.9);
  transform: scale(1.05);
}

.category-active:hover {
  transform: translateX(12px);
  box-shadow: 0 6px 20px rgba(255, 174, 210, 0.4) !important;
}

.category-active:hover .category-count {
  background: rgba(255, 255, 255, 0.25);
  color: #ffffff;
  transform: scale(1.15) translateX(-4px);
}

@keyframes shine {
  0% {
    background-position: -100% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

.category-active::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.2),
    transparent
  );
  background-size: 200% 100%;
  animation: shine 2s infinite linear;
  pointer-events: none;
}
</style>
