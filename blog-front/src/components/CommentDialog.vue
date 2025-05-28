<template>
  <el-dialog
    v-model="dialogVisible"
    title="发表评论"
    width="30%"
    :before-close="handleClose"
  >
    <el-form :model="form" ref="formRef">
      <el-form-item
        prop="content"
        :rules="[{ required: true, message: '请输入评论内容', trigger: 'blur' }]"
      >
        <el-input
          v-model="form.content"
          type="textarea"
          rows="4"
          placeholder="请输入您的评论"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="submitComment" :loading="loading">
          发表评论
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, defineEmits, defineProps, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { makeComment } from '../api/post'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  postId: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['update:visible', 'comment-success'])

const dialogVisible = ref(props.visible)
const loading = ref(false)
const formRef = ref(null)
const form = ref({
  content: ''
})

// 监听visible属性变化
watch(() => props.visible, (newVal) => {
  dialogVisible.value = newVal
})

// 监听对话框可见性变化
watch(dialogVisible, (newVal) => {
  emit('update:visible', newVal)
  if (!newVal) {
    form.value.content = ''
  }
})

const handleClose = () => {
  dialogVisible.value = false
}

const submitComment = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await makeComment({
          postId: props.postId,
          content: form.value.content
        })
        ElMessage.success('评论发表成功')
        emit('comment-success')
        handleClose()
      } catch (error) {
        ElMessage.error('评论发表失败：' + error.message)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 