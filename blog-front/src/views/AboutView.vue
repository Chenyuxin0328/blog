<template>
  <div class="about-container">
    <div class="about-card">
      <div class="about-left">
        <div class="title-container">
          <img src="@/assets/icons/hello.png" alt="" class="hello-img">
          <h2>嗨，我是陈宇新！</h2>
        </div>
        <div class="paragraph-container">
          <p>一名来自福州大学的技术爱好者。<br />
            大学才开始接触电脑，却意外地爱上了敲代码的感觉。一个个字符就像拼图块，等我去排列组合，构建属于自己的小世界。
          </p>
          <p>
            平时写写后端，敲敲代码，偶尔在球场上打打羽毛球。
            喜欢把复杂的问题拆解开来，也喜欢用代码解决真实的挑战。
          </p>
          <p>
            我热衷于探索各种奇奇怪怪的技术，总觉得技术的世界充满了无限可能。
            踩过不少坑，也积累了一些经验，希望在这片小小的博客天地里，记录我的成长，分享我的思考。
          </p>
          <p>
            这里不仅是我的技术备忘录，也是一段和你交流的开始。
            欢迎你来一起探讨、分享，让我们在编程的世界里一起嬉游、进步！
          </p>
        </div>
      </div>

      <div class="about-right">
        <img src="@/assets/icons/user.png" alt="Dog" class="dog-img" />
        <h2>YuXin.Dev</h2>
        <div class="contact-links">
          <div class="contact-link" @mouseenter="scaleUp('github')" @mouseleave="scaleDown('github')" @click="goToGitHub">
            <img src="@/assets/icons/github.png" alt="GitHub" />
            <p>在GitHub上关注我</p>
          </div>
          <div class="contact-link" @mouseenter="scaleUp('wechat')" @mouseleave="scaleDown('wechat')" @click="showWechatImage">
            <img src="@/assets/icons/wechat.png" alt="WeChat" />
            <p>加个微信交流学习</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 微信图片弹窗 -->
    <div v-if="showWechat" class="wechat-modal">
      <img src="@/assets/icons/wechatCode.png" alt="微信二维码" class="wechat-qrcode" />
      <button @click="showWechat = false">关闭</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const showWechat = ref(false);

const scaleUp = (type) => {
  const element = document.querySelector(`.contact-link:nth-child(${type === 'github' ? 1 : 2})`);
  if (element) {
    element.style.transform = 'scale(1.1)';
    element.style.transition = 'transform 0.3s ease';
  }
};

const scaleDown = (type) => {
  const element = document.querySelector(`.contact-link:nth-child(${type === 'github' ? 1 : 2})`);
  if (element) {
    element.style.transform = 'scale(1)';
  }
};

const goToGitHub = () => {
  window.open('https://github.com/Chenyuxin0328', '_blank');
};

const showWechatImage = () => {
  showWechat.value = true;
};
</script>

<style scoped>
.about-container {
  padding: 0;
  margin: 0;
  width: 100%;
}

.about-card {
  display: flex;
  justify-content: center;
  gap: 30px;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.about-left {
  flex: 0 0 calc(65% - 15px);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  position: relative;
}

.title-container {
  position: relative;
  display: inline-block;
  margin-bottom: 30px;
  margin-top: 60px;
  animation: float 3s ease-in-out infinite;
}

.hello-img {
  position: absolute;
  width: 50px;
  left: -60px;
  top: 10px;
  transform: rotate(-20deg);
  animation: wave 2s ease-in-out infinite;
  transform-origin: bottom right;
  cursor: pointer;
  transition: filter 0.3s ease;
}

.hello-img:hover {
  filter: brightness(1.2);
  animation: wave 0.5s ease-in-out infinite;
}

.about-left h2 {
  font-size: 50px;
  margin-bottom: 18px;
  display: inline-block;
  margin-left: 10px;
  background: linear-gradient(45deg, #333, #666);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  transition: all 0.3s ease;
  position: relative;
}

.about-left h2::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 0;
  width: 0;
  height: 3px;
  background: linear-gradient(45deg, #ffaed2, #ff6b9d);
  transition: width 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.about-left h2:hover::after {
  width: 100%;
}

.paragraph-container {
  background-color: rgba(255, 255, 255, 0.4);
  padding: 40px;
  border-radius: 10px;
  text-align: left;
  transition: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  overflow: hidden;
  width: 100%;
}

.paragraph-container p {
  font-size: 20px;
  line-height: 1.8;
  margin-bottom: 28px;
  max-width: 95%;
  margin-left: auto;
  margin-right: auto;
  color: #757575;
  transition: transform 0.3s ease;
  position: relative;
  z-index: 1;
}

.paragraph-container p:hover {
  transform: scale(1.02);
  color: #333;
}

.paragraph-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, rgba(255, 174, 210, 0.1), transparent);
  opacity: 0;
  transition: opacity 0.5s ease;
}

.paragraph-container:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.paragraph-container:hover::before {
  opacity: 1;
}

.paragraph-container p:last-child {
  margin-bottom: 0;
}

.about-right {
  flex: 0 0 calc(35% - 15px);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.dog-img {
  width: 250px;
  margin-bottom: 20px;
  transform: rotate(10deg);
  transition: all 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
  cursor: pointer;
  filter: drop-shadow(0 8px 20px rgba(0, 0, 0, 0.1));
}

.dog-img:hover {
  transform: rotate(-5deg) scale(1.1) translateY(-10px);
  filter: drop-shadow(0 15px 30px rgba(0, 0, 0, 0.15));
}

@keyframes wave {
  0%, 100% { transform: rotate(-20deg); }
  50% { transform: rotate(-30deg); }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

@keyframes bounce {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.about-right h2 {
  margin-bottom: 50px;
  transition: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
  cursor: pointer;
}

.about-right h2:hover {
  transform: scale(1.1) translateY(-5px);
  text-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.contact-links {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.contact-link {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 10px 0;
  width: 100%;
  cursor: pointer;
}

.contact-link img {
  height: 24px;
  width: 24px;
  margin-right: 8px;
}

.contact-link p {
  font-size: 14px;
  margin: 0;
}

.wechat-modal {
  position: fixed;
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
  z-index: 1000;
}

.wechat-qrcode {
  width: 400px;
  height: 400px;
}

@media (max-width: 768px) {
  .about-card {
    flex-direction: column;
  }

  .about-left, .about-right {
    flex: 1 1 100%;
    max-width: 100%;
  }

  .about-left p {
    max-width: 100%;
  }

  .hello-img {
    position: static;
    margin-bottom: 10px;
    left: 0;
    top: 0;
    animation: bounce 2s ease-in-out infinite;
  }

  .title-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    animation: none;
}
}

</style>
