<template>
  <div class="landing-page">
    <!-- 上方标题区域 -->
    <div class="header-section">
      <div class="logo-container">
        <img src="@/assets/icons/logo.png" alt="Logo" class="logo" />
      </div>

      <h1 class="title">
        <span class="text-gradient">Code & Life</span>
      </h1>

      <div class="subtitle">
        分享技术，记录生活
      </div>

      <div class="scroll-hint">
        向下滑动探索更多
        <div class="scroll-arrow">↓</div>
      </div>
    </div>

    <!-- 介绍部分 -->
    <div class="intro-section animate-on-scroll text-scale-section">
      <div class="text-content">
        <h2 class="section-title">关于我</h2>
        <p class="intro-text">
          大家好，我是陈宇新，目前是一名大二学生。欢迎来到 <strong>Code & Life</strong>，这是我独立设计与开发的个人网站。创建这个网站的初衷是想记录我的学习过程，同时分享生活中的点滴。
        </p>
        <p class="intro-text">
          我对 Web 开发充满兴趣，主攻后端方向，也会一些前端技术。在这里，你可以看到我平时学习中的总结、项目实践过程，以及我对技术和生活的思考与感悟。
        </p>
        <p class="intro-text">
          希望这个空间能吸引更多热爱编程的朋友，一起交流、成长、进步。如果你对技术感兴趣，欢迎联系我一起学习！
        </p>
      </div>
    </div>

    <!-- 技术探索区域 -->
    <div class="section-wrapper tech-section animate-on-scroll">
      <div class="text-container text-scale-section">
        <div class="text-content">
          <h2 class="content-title">技术探索</h2>
          <p class="description">
            这是一个集技术与生活于一体的个人空间。在这里，我会分享自己的代码、学习经验、开发感悟，<br></br>也会记录每一个成长的瞬间。
          </p>
          <p class="description">
            无论是新手阶段的第一个"Hello World"，还是项目开发中的每一次调试，都是我成长路上的足迹。
          </p>
          <p class="description">
            我正在持续学习与探索以下技术方向：
          </p>
          <ul class="feature-list">
            <li>前端开发（HTML, CSS, JavaScript, Vue, React 等）</li>
            <li>后端开发（Java 是主力，Spring Boot 等框架）</li>
            <li>项目实战与个人作品展示</li>
            <li>数据库与常用开发工具</li>
            <li>学习方法与效率工具分享</li>
          </ul>
        </div>
      </div>
      <div class="image-container right-image">
        <img
          src="@/assets/icons/dog2.png"
          alt="Tech"
          class="illustration styled-illustration"
          @mouseover="rotateDogHead('tech')"
          @mouseleave="stopRotation('tech')"
          :class="{ 'rotate-animation': techDogRotating }"
        />
      </div>
    </div>

    <!-- 生活感悟区域 -->
    <div class="section-wrapper life-section animate-on-scroll">
      <div class="image-container life-image">
        <img
          src="@/assets/icons/dog1.png"
          alt="Life"
          class="illustration styled-illustration"
          @mouseover="rotateDogHead('life')"
          @mouseleave="stopRotation('life')"
          :class="{ 'rotate-animation': lifeDogRotating }"
        />
      </div>
      <div class="text-container text-scale-section">
        <div class="text-content">
          <h2 class="content-title">生活感悟</h2>
          <p class="description">
            编程是生活的一部分，而生活也塑造着我对技术的理解。除了代码，我也喜欢记录日常的思考与成长过程。
          </p>
          <p class="description">
            在这里，我会分享一些学习中的心得、生活中的小故事，以及大学生活中遇到的人与事。
          </p>
          <p class="description">
            我关心的不只是技术本身，也包括：
          </p>
          <ul class="feature-list">
            <li>大学生涯的点滴记录</li>
            <li>技术与生活之间的平衡</li>
            <li>编程学习的心态管理</li>
            <li>结识志同道合的朋友</li>
            <li>个人成长与未来规划</li>
          </ul>
        </div>
      </div>
    </div>

    <!-- 更多内容区域 -->
    <div class="more-section animate-on-scroll text-scale-section">
      <div class="text-content">
        <h2 class="section-title">期待与你分享</h2>
        <p class="more-text">
          在 <strong>Code & Life</strong>，我不仅希望记录和分享，也希望结识更多朋友。如果你愿意交流学习经验，或者有好的想法想要一起合作，非常欢迎你联系我！
        </p>
        <p class="more-text">
          每一段代码都是成长的见证，每一次分享都是自我的升华。期待与你在编程与生活的旅途中相遇，一起书写属于我们的 Code & Life。
        </p>
      </div>

      <div class="button-container">
        <button class="start-button" @click="handleExplore">
          开始探索
          <img src="@/assets/icons/point.png" alt="探索" class="button-icon" />
        </button>
      </div>
    </div>

    <div class="background"></div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElNotification } from 'element-plus';
import { getVisitCount } from '@/api/stats';

const router = useRouter();
const techDogRotating = ref(false);
const lifeDogRotating = ref(false);

const handleExplore = () => {
  const hour = new Date().getHours();
  let greeting = '晚上好';
  if (hour >= 5 && hour < 12) {
    greeting = '早上好';
  } else if (hour >= 12 && hour < 18) {
    greeting = '下午好';
  }

  ElNotification({
    title: `${greeting} 👋`,
    message: `欢迎来到「Code&Life」！<br>期待和你一起探索有趣的代码与生活~`,
    type: 'success',
    duration: 3000,
    dangerouslyUseHTMLString: true
  });

  // 异步发送访客统计请求，不等待响应
  getVisitCount()
    .then(() => {
      console.log('访客统计更新成功');
    })
    .catch(error => {
      console.error('访客统计更新失败:', error);
    });

  // 直接跳转到首页
  router.push('/home');
};

// 处理狗头旋转动画
const rotateDogHead = (type) => {
  if (type === 'tech') {
    techDogRotating.value = true;
  } else if (type === 'life') {
    lifeDogRotating.value = true;
  }
};

// 停止旋转动画
const stopRotation = (type) => {
  if (type === 'tech') {
    techDogRotating.value = false;
  } else if (type === 'life') {
    lifeDogRotating.value = false;
  }
};

onMounted(() => {
  // 监听滚动事件，触发动画
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('visible');
      }
    });
  }, { threshold: 0.1 });

  // 为所有带有animate-on-scroll类的元素添加观察
  document.querySelectorAll('.animate-on-scroll').forEach((el, index) => {
    // 添加延迟，使元素按顺序显示
    el.style.transitionDelay = `${index * 0.1}s`;
    observer.observe(el);
  });
});
</script>

<style scoped>
.landing-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  padding: 50px 80px;
  background: linear-gradient(135deg, #fff5f7, #ffe0eb);
  position: relative;
  overflow: hidden;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  text-align: left;
}

/* 上方标题区域 */
.header-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 60px;
  z-index: 10;
  height: 90vh;
  justify-content: center;
}

.logo-container {
  margin-bottom: 20px;
}

.logo {
  height: 90px;
  filter: drop-shadow(0 0 10px rgba(255, 105, 180, 0.3));
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.logo:hover {
  transform: rotate(15deg) scale(1.1);
  filter: drop-shadow(0 0 15px rgba(255, 105, 180, 0.5));
}

.title {
  font-size: 4rem;
  font-weight: 900;
  margin-bottom: 16px;
  letter-spacing: -1.2px;
  color: #ff1493;
  text-align: center;
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.title:hover {
  transform: scale(1.05);
  letter-spacing: 2px;
}

.text-gradient {
  background: linear-gradient(45deg, #ff69b4, #ff1493);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  user-select: none;
  display: inline-block;
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.text-gradient:hover {
  background: linear-gradient(45deg, #ff1493, #ff69b4);
  -webkit-background-clip: text;
  background-clip: text;
  transform: translateY(-5px);
  text-shadow: 0 5px 15px rgba(255, 20, 147, 0.3);
}

.subtitle {
  font-size: 2rem;
  font-weight: 600;
  margin-bottom: 24px;
  color: #ff69b4;
  user-select: none;
  text-align: left;
  transition: all 0.3s ease;
  position: relative;
}

.subtitle:hover {
  transform: translateX(10px);
  color: #ff1493;
}

.scroll-hint {
  margin-top: 40px;
  text-align: center;
  color: #ff69b4;
  font-size: 1.2rem;
  font-weight: 500;
  opacity: 0.8;
  animation: bounce 2s infinite;
}

.scroll-arrow {
  font-size: 1.8rem;
  margin-top: 10px;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0) scale(1);
    opacity: 0.8;
  }
  40% {
    transform: translateY(-15px) scale(1.1);
    opacity: 1;
  }
  60% {
    transform: translateY(-7px) scale(1.05);
    opacity: 0.9;
  }
}

/* 技术探索 & 生活感悟容器：水平排列 */
.section-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px; /* 减小间距，使图片更靠近文字 */
  margin: 80px 0;
  flex-wrap: wrap; /* 为响应式准备 */
}

.section-wrapper.reverse {
  flex-direction: row-reverse;
}

.content-text {
  flex: 1;
  padding: 0 30px;
}

.content-title {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 25px;
  color: #ff69b4;
}

.description {
  font-size: 1.1rem;
  line-height: 1.8;
  margin-bottom: 20px;
  color: #666;
}

.feature-list {
  margin-left: 20px;
  margin-bottom: 30px;
  color: #666;
  font-size: 1.1rem;
  line-height: 1.8;
}

.feature-list li {
  margin-bottom: 10px;
}

/* 图像样式统一 */
.illustration {
  max-width: 100%;
  max-height: 300px;
  user-select: none;
  filter: drop-shadow(0 0 20px rgba(255, 105, 180, 0.3));
  border-radius: 30px;
  object-fit: cover;
  margin: 0;
  flex: 1;
  cursor: pointer;
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.illustration:hover {
  transform: scale(1.05) translateY(-10px);
  filter: drop-shadow(0 15px 35px rgba(255, 105, 180, 0.5));
}

/* 更多内容区域 */
.more-section {
  margin-bottom: 80px;
  max-width: 900px;
  margin-left: auto;
  margin-right: auto;
  text-align: left;
}

.more-text {
  font-size: 1.25rem;
  line-height: 1.8;
  margin-bottom: 20px;
  color: #666;
}

.button-container {
  margin-top: 50px;
  display: flex;
  justify-content: center;
}

.start-button {
  background: linear-gradient(45deg, #ff69b4, #ff1493);
  color: white;
  font-weight: 700;
  padding: 16px 60px;
  font-size: 1.3rem;
  border-radius: 50px;
  box-shadow: 0 10px 25px rgba(255, 20, 147, 0.4);
  text-decoration: none;
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  user-select: none;
  position: relative;
  border: none;
  overflow: hidden;
  cursor: pointer;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.start-button:before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 200%;
  height: 100%;
  background: linear-gradient(90deg,
    transparent 0%,
    rgba(255, 255, 255, 0.2) 45%,
    rgba(255, 255, 255, 0.4) 50%,
    rgba(255, 255, 255, 0.2) 55%,
    transparent 100%
  );
  transform: skewX(-25deg);
  transition: all 0.7s ease;
}

.start-button:hover {
  box-shadow: 0 15px 35px rgba(255, 20, 147, 0.6);
  transform: translateY(-6px) scale(1.03);
  letter-spacing: 2px;
}

.start-button:hover:before {
  left: 100%;
}

.start-button:active {
  transform: translateY(-2px) scale(0.98);
  box-shadow: 0 8px 15px rgba(255, 20, 147, 0.4);
}

.button-icon {
  width: 24px;
  height: 24px;
  margin-left: 12px;
  filter: brightness(0) invert(1);
  transition: all 0.4s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

.start-button:hover .button-icon {
  transform: rotate(180deg) scale(1.2);
}

.arrow {
  margin-left: 12px;
  transition: transform 0.4s ease;
  display: inline-block;
}

.start-button:hover .arrow {
  transform: translateX(8px);
}

/* 背景装饰 */
.background {
  position: absolute;
  top: 0;
  left: 50%;
  width: 60vw;
  height: 100vh;
  background: radial-gradient(circle at center, rgba(255, 20, 147, 0.15), transparent 60%);
  filter: blur(120px);
  transform: translateX(-50%);
  z-index: 1;
  pointer-events: none;
}

/* 响应式 */
@media (max-width: 992px) {
  .landing-page {
    padding: 30px 20px;
  }

  .content-block,
  .content-block.reverse {
    flex-direction: column;
  }

  .content-text {
    padding: 0;
  }

  .illustration {
    margin: 20px 0;
    max-height: 200px;
  }

  .title {
    font-size: 3rem;
  }

  .subtitle {
    font-size: 1.5rem;
  }

  .section-title {
    font-size: 2rem;
  }

  .description, .intro-text, .more-text {
    font-size: 1rem;
  }

  .header-section {
    height: 70vh;
  }
}

.life-section .image-container {
  margin-right: 20px; /* 图片右侧添加边距 */
}

/* 生活感悟图片调整 */
.life-image {
  margin-left: 200px; /* 将图片向右移动 */
}
.right-image{
  margin-right: 250px;
}

/* 介绍部分 */
.intro-section {
  margin-bottom: 80px;
  max-width: 900px;
  margin-left: auto;
  margin-right: auto;
  text-align: left;
}

.section-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 30px;
  color: #ff1493;
}

.intro-text {
  font-size: 1.25rem;
  line-height: 1.8;
  margin-bottom: 20px;
  color: #666;
}

/* 动画 */
.animate-on-scroll {
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.8s ease;
}

.animate-on-scroll.visible {
  opacity: 1;
  transform: translateY(0);
}

/* 技术探索 & 生活感悟容器：水平排列 */

/* 狗头旋转动画 */
.rotate-animation {
  animation: dogRotate 3s cubic-bezier(0.4, 0, 0.2, 1) infinite;
  transform-origin: center;
  cursor: pointer;
}

@keyframes dogRotate {
  0% {
    transform: rotate(0deg) scale(1);
  }
  25% {
    transform: rotate(-15deg) scale(1.1);
  }
  50% {
    transform: rotate(10deg) scale(1.05);
  }
  75% {
    transform: rotate(-5deg) scale(1.02);
  }
  100% {
    transform: rotate(0deg) scale(1);
  }
}

/* 添加文字区域缩放效果的CSS */
.text-scale-section {
  position: relative;
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
}

.text-scale-section:hover {
  transform: scale(1.02) translateY(-5px);
  z-index: 5;
}

.text-content {
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 20px;
  border-radius: 15px;
  background-color: rgba(255, 255, 255, 0.1);
}

.text-scale-section:hover .text-content {
  background-color: rgba(255, 255, 255, 0.8);
  box-shadow: 0 10px 30px rgba(255, 105, 180, 0.2);
  transform: translateY(-5px);
}
</style>

