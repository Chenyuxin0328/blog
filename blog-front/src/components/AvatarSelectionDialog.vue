<template>
  <el-dialog
    v-model="dialogVisible"
    title="选择你的头像"
    width="70%"
    :show-close="false"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
  >
    <div class="avatar-selection-container">
      <div class="avatar-grid">
        <div
          v-for="(avatar, index) in avatarList"
          :key="index"
          class="avatar-item"
          :class="{ 'selected': selectedAvatarIndex === index }"
          @click="selectAvatar(index)"
        >
          <img :src="avatar.url" :alt="avatar.name" class="avatar-option" />
          <div class="avatar-name">{{ avatar.name }}</div>
        </div>
      </div>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="confirmSelection" :disabled="selectedAvatarIndex === null">
          确认选择
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, defineProps, defineEmits, watch } from 'vue'

const props = defineProps({
  show: {
    type: Boolean,
    required: true
  },
  avatarList: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['update:show', 'select'])

const selectedAvatarIndex = ref(null)
const dialogVisible = ref(props.show)

// Watch for changes in the show prop
watch(() => props.show, (newVal) => {
  dialogVisible.value = newVal
})

// Watch for changes in dialogVisible
watch(() => dialogVisible.value, (newVal) => {
  emit('update:show', newVal)
})

const selectAvatar = (index) => {
  selectedAvatarIndex.value = index
}

const confirmSelection = () => {
  if (selectedAvatarIndex.value !== null) {
    emit('select', selectedAvatarIndex.value)
    dialogVisible.value = false
    selectedAvatarIndex.value = null
  }
}
</script>

<style scoped>
.avatar-selection-container {
  padding: 20px;
}

.avatar-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 24px;
  max-height: 60vh;
  overflow-y: auto;
  padding: 16px;
}

.avatar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 10px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  border: 3px solid #ffd1db;
  background: rgba(255, 255, 255, 0.6);
  box-shadow: 0 2px 8px rgba(255, 141, 161, 0.2);
}

.avatar-item:hover {
  transform: scale(1.05);
  border-color: #ff8aa2;
  box-shadow: 0 4px 12px rgba(255, 141, 161, 0.3);
  background: rgba(255, 255, 255, 0.8);
}

.avatar-item.selected {
  border-color: #ff4d76;
  box-shadow: 0 6px 16px rgba(255, 141, 161, 0.4);
  background: rgba(255, 255, 255, 0.9);
}

.avatar-option {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.avatar-name {
  font-size: 14px;
  color: #666;
  text-align: center;
  margin-top: 4px;
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.avatar-grid::-webkit-scrollbar {
  width: 6px;
}

.avatar-grid::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
}

.avatar-grid::-webkit-scrollbar-thumb {
  background: #ffb6c1;
  border-radius: 3px;
}

.avatar-grid::-webkit-scrollbar-thumb:hover {
  background: #ff9eb5;
}

.dialog-footer {
  text-align: center;
  padding-top: 20px;
}

:deep(.el-dialog) {
  border-radius: 20px;
  overflow: hidden;
  background: linear-gradient(135deg, #ffffff 0%, #ffd6dd 100%);
}

:deep(.el-dialog__header) {
  margin: 0;
  padding: 20px;
  text-align: center;
  background: #ffb6c1;
  color: white;
  position: relative;
  overflow: hidden;
  border-radius: 20px 20px 0 0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.el-dialog__header:hover) {
  transform: scale(1.02);
  background: #ff8da1;
}

:deep(.el-dialog__title) {
  color: white;
  font-size: 20px;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

:deep(.el-dialog__header:hover .el-dialog__title) {
  transform: scale(1.05);
  letter-spacing: 1px;
}

:deep(.el-dialog__body) {
  padding: 0;
  background: transparent;
}

:deep(.el-dialog__footer) {
  padding: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.3);
  background: transparent;
  text-align: center;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #ffb6c1 0%, #ff8da1 100%);
  border: none;
  padding: 12px 30px;
  min-width: 120px;
  height: 46px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 23px;
  color: white;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 15px rgba(255, 182, 193, 0.3);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

:deep(.el-button--primary)::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    120deg,
    transparent,
    rgba(255, 255, 255, 0.3),
    transparent
  );
  transition: 0.5s;
}

:deep(.el-button--primary:hover) {
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 8px 20px rgba(255, 182, 193, 0.4);
  background: linear-gradient(135deg, #ff8da1 0%, #ffb6c1 100%);
  letter-spacing: 1px;
}

:deep(.el-button--primary:hover)::before {
  left: 100%;
}

:deep(.el-button--primary:active) {
  transform: translateY(0);
  box-shadow: 0 2px 10px rgba(255, 182, 193, 0.3);
}

:deep(.el-button--primary.is-disabled) {
  background: linear-gradient(135deg, #ffd6dd 0%, #ffcbd1 100%);
  opacity: 0.7;
  cursor: not-allowed;
}

:deep(.el-button--primary.is-disabled:hover) {
  transform: none;
  box-shadow: 0 4px 15px rgba(255, 182, 193, 0.3);
  letter-spacing: normal;
}

:deep(.el-button--primary.is-disabled:hover)::before {
  display: none;
}
</style> 