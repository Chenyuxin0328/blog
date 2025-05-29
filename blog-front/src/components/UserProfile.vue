<template>
  <Transition name="fade">
    <div v-if="visible" class="matched-user-profile">
      <div class="profile-content">
        <img :src="avatar" :alt="username" class="profile-avatar" />
        <span class="profile-username">{{ username }}</span>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { defineProps } from 'vue'

defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  avatar: {
    type: String,
    required: true
  },
  username: {
    type: String,
    required: true
  }
})
</script>

<style scoped>
.matched-user-profile {
  position: absolute;
  left: 50%;
  margin-left: 280px;
  top: 48%;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 200px;
  z-index: 10;
}

.profile-content {
  animation: slideIn 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
}

.profile-avatar {
  margin-left: -100px;
  width: 180px;
  height: 180px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #ffb6c1;
  box-shadow: 0 4px 15px rgba(255, 182, 193, 0.3);
  animation: avatarAppear 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) 0.2s both;
}

.profile-avatar:hover {
  transform: scale(1.1) rotate(5deg);
  border-color: #ff8da1;
  box-shadow: 0 8px 25px rgba(255, 182, 193, 0.4);
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.profile-username {
  margin-left: -100px;
  font-size: 26px;
  font-weight: 600;
  color: #333;
  margin-top: 20px;
  text-align: center;
  display: block;
  white-space: nowrap;
  cursor: pointer;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  animation: usernameAppear 0.5s cubic-bezier(0.34, 1.56, 0.64, 1) 0.4s both;
}

.profile-username:hover {
  transform: scale(1.1);
  color: #ffb6c1;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

/* Fade transition */
.fade-enter-active {
  transition: opacity 0.3s ease;
}

.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Animations */
@keyframes slideIn {
  0% {
    opacity: 0;
    transform: translateX(50px);
  }
  100% {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes avatarAppear {
  0% {
    opacity: 0;
    transform: scale(0.3);
    filter: blur(10px);
  }
  100% {
    opacity: 1;
    transform: scale(1);
    filter: blur(0);
  }
}

@keyframes usernameAppear {
  0% {
    opacity: 0;
    transform: translateY(20px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes floatAnimation {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes glowAnimation {
  0%, 100% {
    box-shadow: 0 4px 15px rgba(255, 182, 193, 0.3);
  }
  50% {
    box-shadow: 0 4px 25px rgba(255, 182, 193, 0.5);
  }
}

.profile-content {
  animation: floatAnimation 3s ease-in-out infinite 1s;
}

.profile-avatar {
  animation: 
    avatarAppear 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) 0.2s both,
    glowAnimation 3s ease-in-out infinite 1s;
}
</style> 