import { createRouter, createWebHistory } from 'vue-router'
import { createApp } from 'vue'
import HomeView from '../views/HomeView.vue'
import LandingPage from '../views/LandingPage.vue'
import DocsAlert from '@/components/DocsAlert.vue'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    name: 'landing',
    component: LandingPage
  },
  {
    path: '/home',
    name: 'home',
    component: () => import('../views/FriendsView.vue')
  },
  {
    path: '/friends',
    name: 'friendsCircle',
    component: () => import('../views/FriendsView.vue')
  },
  {
    path: '/docs',
    name: 'docs',
    component: HomeView,
    beforeEnter: (to, from, next) => {
      // 检查是否已经显示过提醒
      const hasSeenAlert = localStorage.getItem('hasSeenDocsAlert')
      if (!hasSeenAlert) {
        // 创建提醒弹窗
        const docsAlert = document.createElement('div')
        docsAlert.id = 'docs-alert'
        document.body.appendChild(docsAlert)
        
        const alertComponent = createApp(DocsAlert).mount('#docs-alert')
        alertComponent.showAlert()
        
        // 记录已经显示过提醒
        localStorage.setItem('hasSeenDocsAlert', 'true')
      }
      next()
    }
  },
  {
    path: '/about',
    name: 'about',
    component: () => import('../views/AboutView.vue')
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue')
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        redirect: '/admin/friends'
      },
      {
        path: 'friends',
        component: () => import('@/views/admin/FriendsManage.vue'),
        meta: { requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'docs',
        component: () => import('@/views/admin/DocsManage.vue'),
        meta: { requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'users',
        component: () => import('@/views/admin/UsersManage.vue'),
        meta: { requiresAuth: true, requiresAdmin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 导航守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 检查是否需要认证
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!userStore.isLoggedIn) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }
    
    // 检查是否需要管理员权限
    if (to.matched.some(record => record.meta.requiresAdmin)) {
      if (userStore.userInfo?.user?.role !== 'admin') {
        next({ path: '/' })
        return
      }
    }
  }
  
  next()
})

export default router
