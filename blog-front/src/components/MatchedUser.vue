<template>
  <div v-if="user" class="matched-user-profile">
    <img :src="user.avatar" :alt="user.username" class="profile-avatar" />
    <span class="profile-username">{{ user.username }}</span>
  </div>
</template>

<script setup>
defineProps({
  user: {
    type: Object,
    default: null
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
  animation: profileAppear 0.5s ease forwards;
}

.profile-avatar {
  margin-left: -100px;
  width: 180px;
  height: 180px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #ffb6c1;
  box-shadow: 0 4px 15px rgba(255, 182, 193, 0.3);
  animation: avatarAppear 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
}

.profile-username {
  margin-left: -100px;
  font-size: 26px;
  font-weight: 600;
  color: #333;
  margin-top: 20px;
  text-align: center;
  white-space: nowrap;
  cursor: pointer;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  animation: usernameAppear 0.5s ease 0.3s forwards;
  opacity: 0;
  transform: translateY(20px);
}

.profile-avatar:hover {
  transform: scale(1.1);
  border-color: #ff8da1;
  box-shadow: 0 8px 25px rgba(255, 182, 193, 0.4);
}

.profile-username:hover {
  transform: scale(1.1);
  color: #ffb6c1;
}

@keyframes profileAppear {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes avatarAppear {
  0% {
    opacity: 0;
    transform: scale(0.3) translateX(50px);
    filter: blur(10px);
  }
  100% {
    opacity: 1;
    transform: scale(1) translateX(0);
    filter: blur(0);
  }
}

@keyframes usernameAppear {
  0% {
    opacity: 0;
    transform: translateY(20px);
    filter: blur(5px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
    filter: blur(0);
  }
}
</style> 