<template>
  <div class="container">
      <div class="chart-container">
      <div class="chart-row">
        <!-- 技术文档数量统计 -->
        <div class="chart-card">
          <div class="chart-title">近期技术文档更新状态</div>
          <div class="chart" ref="docChart"></div>
        </div>

        <!-- 发帖热度统计 -->
        <div class="chart-card">
          <div class="chart-title">近期朋友圈更新状态</div>
          <div class="chart" ref="postChart"></div>
        </div>
      </div>

      <!-- 访客数量统计 -->
      <div class="chart-card">
        <div class="chart-title">近期访客数量统计</div>
        <div class="chart" ref="visitorChart"></div>
      </div>
    </div>

    <div class="custom-table">
      <div class="custom-row" v-for="(item, index) in summaryData" :key="index">
        <div class="custom-cell icon-text">
          <img :src="item.icon" class="stat-icon" />
          <span class="stat-title" style="font-weight: 500">{{ item.title }}</span>
        </div>
        <div class="custom-cell stat-count">{{ item.count }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import * as echarts from 'echarts';
import { getWeekStats, getTotalStats } from '@/api/stats';
import skill from '@/assets/icons/技术.png'
import blogView from '@/assets/icons/博客访问次数.png'
import friend from '@/assets/icons/朋友圈帖子总数.png'
import like from '@/assets/icons/博客喜欢.png'
import comment from '@/assets/icons/博客评论.png'

import viewIcon from '@/assets/icons/table-view.png'

const docChart = ref(null);
const postChart = ref(null);
const visitorChart = ref(null);

let docChartInstance = null;
let postChartInstance = null;
let visitorChartInstance = null;

const summaryData = ref([
  { title: '博客总访问量（次）', count: 0, icon: blogView },
  { title: '技术文档总数（篇）', count: 0, icon: skill },
  { title: '朋友圈帖子数（章）', count: 0, icon: friend },
  { title: '帖子总点赞数（次）', count: 0, icon: like },
  { title: '帖子总评论数（次）', count: 0, icon: comment },
])

// 获取图表数据
const fetchChartData = async () => {
  try {
    const res = await getWeekStats();
    const weekData = res.data;
    const dates = ['本周', '上周', '两周前', '三周前', '四周前', '五周前', '六周前'];

    // 处理技术文档数据
    const docData = {
      dates: dates,
      counts: weekData.map(item => item.docCount).reverse() // 反转数组使最新数据在右侧
    };
    initDocChart(docData);

    // 处理朋友圈数据
    const postData = {
      dates: dates,
      counts: weekData.map(item => item.postCount).reverse()
    };
    initPostChart(postData);

    // 处理访客数据
    const visitorData = {
      dates: dates,
      counts: weekData.map(item => item.visitorCount).reverse()
    };
    initVisitorChart(visitorData);
  } catch (error) {
    console.error('获取统计数据失败:', error);
    // 使用模拟数据
    const mockData = {
      dates: ['本周', '上周', '两周前', '三周前', '四周前', '五周前', '六周前'],
      counts: [5, 8, 3, 10, 6, 9, 4]
    };

    initDocChart(mockData);
    initPostChart(mockData);
    initVisitorChart(mockData);
  }
};

// 获取总计数据
const fetchTotalStats = async () => {
  try {
    const res = await getTotalStats();
    const totalData = res.data;
    summaryData.value = [
      { title: '博客总访问量（次）', count: totalData.total_view, icon: blogView },
      { title: '技术文档总数（篇）', count: totalData.total_doc, icon: skill },
      { title: '朋友圈帖子数（章）', count: totalData.total_post, icon: friend },
      { title: '帖子总点赞数（次）', count: totalData.total_like, icon: like },
      { title: '帖子总评论数（次）', count: totalData.total_comment, icon: comment },
    ];
  } catch (error) {
    console.error('获取总计数据失败:', error);
  }
};

// 初始化技术文档数量图表
const initDocChart = (data) => {
  if (!docChart.value) return;

  docChartInstance = echarts.init(docChart.value);

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
  xAxis: {
    type: 'category',
      data: data.dates,
      axisLabel: {
        color: '#666'
      }
  },
    yAxis: {
      type: 'value',
      minInterval: 1,
      splitNumber: 5,
      axisLabel: {
        color: '#666',
        formatter: function(value) {
          return Math.floor(value);
        }
      }
    },
    series: [{
      data: data.counts,
      type: 'bar',
      itemStyle: {
        color: '#ffaed2'
      },
      barWidth: '60%',
      emphasis: {
        itemStyle: {
          color: '#ff7eb9',
          borderRadius: [6, 6, 0, 0]
        },
        scale: true,
        scaleSize: 5
      },
      animationDuration: 300,
      animationEasing: 'cubicOut'
    }]
  };

  docChartInstance.setOption(option);
};

// 初始化发帖热度图表
const initPostChart = (data) => {
  if (!postChart.value) return;

  postChartInstance = echarts.init(postChart.value);

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.dates,
      axisLabel: {
        color: '#666'
      }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      splitNumber: 5,
      axisLabel: {
        color: '#666',
        formatter: function(value) {
          return Math.floor(value);
        }
      }
    },
    series: [{
      data: data.counts,
      type: 'bar',
      itemStyle: {
        color: '#ffaed2'
      },
      barWidth: '60%',
      emphasis: {
        itemStyle: {
          color: '#ff7eb9',
          borderRadius: [6, 6, 0, 0]
        },
        scale: true,
        scaleSize: 5
      },
      animationDuration: 300,
      animationEasing: 'cubicOut'
    }]
  };

  postChartInstance.setOption(option);
};

// 初始化访客数量图表
const initVisitorChart = (data) => {
  if (!visitorChart.value) return;

  visitorChartInstance = echarts.init(visitorChart.value);

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
  xAxis: {
    type: 'category',
      data: data.dates,
      axisLabel: {
        color: '#666'
      }
  },
    yAxis: {
      type: 'value',
      minInterval: 1,
      splitNumber: 5,
      axisLabel: {
        color: '#666',
        formatter: function(value) {
          return Math.floor(value);
        }
      }
    },
    series: [{
      data: data.counts,
      type: 'line',
      smooth: true,
      symbolSize: 8,
      itemStyle: {
        color: '#ffaed2'
      },
      lineStyle: {
        color: '#ffaed2',
        width: 3
      },
      emphasis: {
        scale: true,
        focus: 'series',
        itemStyle: {
          color: '#ff7eb9'
        },
        lineStyle: {
          width: 4
        }
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [{
            offset: 0,
            color: 'rgba(255, 174, 210, 0.3)'
          }, {
            offset: 1,
            color: 'rgba(255, 174, 210, 0.1)'
          }]
        }
      },
      animationDuration: 300,
      animationEasing: 'cubicOut'
    }]
  };

  visitorChartInstance.setOption(option);
};

// 处理窗口大小变化
const handleResize = () => {
  docChartInstance?.resize();
  postChartInstance?.resize();
  visitorChartInstance?.resize();
};

onMounted(async () => {
  await Promise.all([fetchChartData(), fetchTotalStats()]);
  
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  docChartInstance?.dispose();
  postChartInstance?.dispose();
  visitorChartInstance?.dispose();
  window.removeEventListener('resize', handleResize);
});
</script>

<style scoped>
.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  box-sizing: border-box;
}

.chart-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding: 0;
  width: 100%;
  box-sizing: border-box;
}

.chart-row {
  display: flex;
  gap: 15px;
  width: 100%;
}

.chart-card {
  background-color: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 8px 15px;
  box-shadow: 0 0 10px rgba(0,0,0,0.1);
  border: 2px solid #ffd6db;
  box-sizing: border-box;
  overflow: hidden;
  transition: all 0.3s ease;
}

.chart-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(255, 174, 210, 0.3);
  border-color: #ff7eb9;
}

.chart-row .chart-card {
  width: calc(50% - 7.5px);
  margin: 0;
}

.chart-container > .chart-card {
  width: calc(100% - 40px);
  margin: 0 auto;
}

.chart-title {
  font-size: 14px;
  color: #333;
  font-weight: bold;
  margin-top: 10px;
  margin-bottom: -30px;
  text-align: center;
  transform: translateY(2px);
}

.chart {
  height: 200px;
  width: 100%;
  transform: translateY(-8px);
}

.custom-table {
  width: calc(100% - 40px);
  margin: 20px auto 0;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 0 10px rgba(0,0,0,0.1);
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 2px solid #ffd6db;
  box-sizing: border-box;
}

.custom-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 14px;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
}

.custom-row:hover {
  background: rgba(255, 174, 210, 0.1);
  transform: translateX(5px);
  border-left: 4px solid #ffaed2;
}

.custom-row:hover .stat-icon {
  transform: scale(1.2) rotate(10deg);
}

.custom-row:hover .stat-count {
  color: #ff7eb9;
  transform: scale(1.1);
}

.custom-row:last-child {
  border-bottom: none;
}

.custom-cell {
  display: flex;
  align-items: center;
  justify-content: center;
}

.custom-row > .custom-cell:first-child {
  width: 50%;
}

.custom-row > .custom-cell:last-child {
  width: 50%;
}

.stat-count {
  font-weight: bold;
  transition: all 0.3s ease;
}

.icon-text {
  gap: 8px;
}

.stat-icon {
  width: 18px;
  height: 18px;
  object-fit: contain;
  transition: all 0.3s ease;
}

.stat-title {
  white-space: nowrap;
  transition: all 0.3s ease;
}

.custom-row:hover .stat-title {
  color: #ff7eb9;
}
</style>
