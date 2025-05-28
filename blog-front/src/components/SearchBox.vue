<template>
  <div class="search-box" v-bind="attrs">
    <div class="search-header">
      <img src="@/assets/icons/文章搜索.png" alt="搜索" class="search-icon" />
      <h3>搜索</h3>
    </div>
    <div class="search-input-wrapper">
      <input
        type="text"
        class="search-input"
        placeholder="输入搜索词..."
        v-model="searchQuery"
        @input="onSearchInput"
        @keydown.enter="search"
      />
      <button v-if="searchQuery" class="clear-button" @click="clearSearch">
        <span>&times;</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useAttrs } from 'vue';

const attrs = useAttrs();
const searchQuery = ref('');
const emit = defineEmits(['search']);

// 搜索防抖定时器
let searchTimer = null;

// 搜索输入时触发
const onSearchInput = () => {
  // 清除之前的定时器
  if (searchTimer) {
    clearTimeout(searchTimer);
  }

  // 设置新的定时器，300ms后执行搜索
  searchTimer = setTimeout(() => {
    search();
  }, 300);
};

// 执行搜索
const search = () => {
  emit('search', searchQuery.value.trim());
};

// 清除搜索
const clearSearch = () => {
  searchQuery.value = '';
  emit('search', '');
};

// 使组件可以继承父组件传递的类
defineOptions({
  inheritAttrs: false
});
</script>

<style scoped>
.search-box {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8), rgba(255, 255, 255, 0.6));
  border-radius: 16px;
  padding: 20px;
  box-shadow: 
    0 4px 15px rgba(0, 0, 0, 0.05),
    0 8px 25px rgba(0, 0, 0, 0.03);
  backdrop-filter: blur(10px);
  transition: all 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.search-box:hover {
  transform: translateY(-5px);
  box-shadow: 
    0 8px 25px rgba(0, 0, 0, 0.08),
    0 12px 35px rgba(0, 0, 0, 0.06);
}

.search-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 12px;
  border-bottom: 2px solid rgba(0, 0, 0, 0.05);
}

.search-icon {
  width: 24px;
  height: 24px;
  margin-right: 12px;
  margin-left: 0;
  transition: transform 0.3s ease;
}

.search-box:hover .search-icon {
  transform: rotate(15deg);
}

.search-header h3 {
  margin: 0;
  font-size: 1.2rem;
  color: #2c3e50;
  font-weight: 600;
}

.search-input-wrapper {
  position: relative;
}

.search-input {
  width: 100%;
  padding: 10px 15px;
  padding-right: 35px; /* 为清除按钮留出空间 */
  border: none; /* ← 去掉边框 */
  border-radius: 4px;
  font-size: 0.9rem;
  outline: none;
  transition: border-color 0.3s ease;
  background-color: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(3px);
  color: #333;
}

.search-input::placeholder {
  color: #777;
}

.search-input:focus {
  border-color: #3498db;
  background-color: rgba(255, 255, 255, 0.4);
}

.clear-button {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  color: #999;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  border-radius: 50%;
}

.clear-button:hover {
  background-color: rgba(0, 0, 0, 0.1);
  color: #333;
}
</style>
