<template>
  <div v-if="show" class="docs-alert-overlay" @click="closeAlert">
    <div class="docs-alert-content" @click.stop>
      <div class="alert-header">
        <h3>📢 小小提醒~</h3>
      </div>
      <div class="alert-body">
        <p>咱实验室要求在飞书文档里写博客，所以点开文章后会跳转到飞书哈～感谢理解！</p>
        <p>这些内容都是我自己一点点写出来的，参考了自己的项目和学习经历，花了不少时间琢磨～不是网上随手复制的哦～</p>
        <p>希望能对你有所启发，也欢迎来交流！</p>
      </div>
      <div class="alert-footer">
        <button class="read-btn" @click="closeAlert">👀 开始阅读！</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DocsAlert',
  data() {
    return {
      show: false
    }
  },
  methods: {
    showAlert() {
      this.show = true
    },
    closeAlert() {
      this.show = false
      const alertElement = document.getElementById('docs-alert')
      if (alertElement) {
        alertElement.remove()
      }
    }
  }
}
</script>

<style scoped>
.docs-alert-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
  backdrop-filter: blur(3px);
}

.docs-alert-content {
  background: white;
  padding: 30px;
  border-radius: 20px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  transform: scale(0.9);
  opacity: 0;
  animation: popIn 0.5s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
}

.alert-header {
  text-align: center;
  margin-bottom: 20px;
}

.alert-header h3 {
  margin: 0;
  color: #333;
  font-size: 1.6em;
  font-weight: 600;
  background: linear-gradient(45deg, #ff9a9e, #fad0c4);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: floating 3s ease-in-out infinite;
}

.alert-body {
  margin: 20px 0;
  line-height: 1.8;
}

.alert-body p {
  margin: 15px 0;
  color: #666;
  text-align: left;
  transform: translateY(20px);
  opacity: 0;
  animation: slideUp 0.5s ease forwards;
}

.alert-body p:nth-child(1) { animation-delay: 0.1s; }
.alert-body p:nth-child(2) { animation-delay: 0.2s; }
.alert-body p:nth-child(3) { animation-delay: 0.3s; }

.alert-footer {
  display: flex;
  justify-content: center;
  margin-top: 25px;
}

.read-btn {
  padding: 12px 35px;
  border-radius: 30px;
  border: none;
  background: linear-gradient(45deg, #ff9a9e 0%, #fad0c4 100%);
  color: white;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow: 0 5px 15px rgba(255, 154, 158, 0.4);
  position: relative;
  overflow: hidden;
}

.read-btn:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 8px 20px rgba(255, 154, 158, 0.6);
  letter-spacing: 1px;
}

.read-btn:active {
  transform: translateY(-1px) scale(1.02);
}

.read-btn::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: rgba(255, 255, 255, 0.2);
  transform: rotate(45deg);
  animation: shimmer 3s infinite;
  z-index: 1;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.read-btn:hover::before {
  opacity: 1;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes popIn {
  from {
    transform: scale(0.9);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

@keyframes floating {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

@keyframes shimmer {
  0% {
    transform: translate(-50%, -50%) rotate(45deg);
  }
  100% {
    transform: translate(150%, 150%) rotate(45deg);
  }
}
</style>
