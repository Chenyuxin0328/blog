import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useFriendCircleStore = defineStore('friendCircle', () => {
  const currentImageIds = ref([])
  const currentImages = ref([])

  // 设置当前编辑的图片ID列表
  const setCurrentImageIds = (imageIds) => {
    console.log('设置图片ID列表:', imageIds)
    currentImageIds.value = [...(imageIds || [])]
  }

  // 设置当前编辑的图片列表
  const setCurrentImages = (images) => {
    currentImages.value = [...(images || [])]
  }

  // 添加新图片
  const addImage = (imageUrl, imageId) => {
    console.log('添加前的图片ID列表:', currentImageIds.value)
    currentImages.value = [...currentImages.value, imageUrl]
    currentImageIds.value = [...currentImageIds.value, imageId]
    console.log('添加后的图片ID列表:', currentImageIds.value)
  }

  // 删除图片
  const removeImage = (index) => {
    console.log('删除前的图片ID列表:', currentImageIds.value)
    currentImages.value = currentImages.value.filter((_, i) => i !== index)
    currentImageIds.value = currentImageIds.value.filter((_, i) => i !== index)
    console.log('删除后的图片ID列表:', currentImageIds.value)
  }

  // 重置状态
  const reset = () => {
    currentImageIds.value = []
    currentImages.value = []
  }

  return {
    currentImageIds,
    currentImages,
    setCurrentImageIds,
    setCurrentImages,
    addImage,
    removeImage,
    reset
  }
}) 