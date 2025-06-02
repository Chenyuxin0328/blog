import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// ✅ 引入 vue-echarts 和 echarts 核心模块
import VueECharts from 'vue-echarts'
import { use } from 'echarts/core'

// ✅ 按需引入 ECharts 模块
import {
    CanvasRenderer
} from 'echarts/renderers'
import {
    BarChart,
    LineChart
} from 'echarts/charts'
import {
    TitleComponent,
    TooltipComponent,
    GridComponent,
    LegendComponent
} from 'echarts/components'

// ✅ 注册组件
use([
    CanvasRenderer,
    BarChart,
    LineChart,
    TitleComponent,
    TooltipComponent,
    GridComponent,
    LegendComponent
])

const app = createApp(App)
const pinia = createPinia()

// 注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// ✅ 全局注册 v-chart 组件
app.component('v-chart', VueECharts)

app.use(pinia)
app.use(router)
app.use(ElementPlus)
app.mount('#app')
