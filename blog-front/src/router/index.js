import { createRouter, createWebHistory } from 'vue-router'
import { createApp } from 'vue'
import HomeView from '../views/HomeView.vue'
import LandingPage from '../views/LandingPage.vue'
import DocsAlert from '../components/DocsAlert.vue'

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
      const docsAlert = document.createElement('div')
      docsAlert.id = 'docs-alert'
      document.body.appendChild(docsAlert)
      
      const alertComponent = createApp(DocsAlert).mount('#docs-alert')
      alertComponent.showAlert()
      
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
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
