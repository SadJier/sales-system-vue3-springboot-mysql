import { createRouter, createWebHistory } from 'vue-router'

import salesShow from '@/components/sales_show.vue'
import salesManager from '@/components/sales_manager.vue'
import login from '@/components/login.vue'
import profile from '@/components/profile.vue'
import merchantManager from '@/components/merchant_manager.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login',
    },
    {path: '/login', component: login, meta: { requiresAuth: false }},
    {path: '/goods/show', component: salesShow, meta: { requiresAuth: true }},
    {path: '/goods/manage', component: salesManager, meta: { requiresAuth: true }},
    {path: '/merchants/manage', component: merchantManager, meta: { requiresAuth: true, requiresAdmin: true }},
    {path: '/profile', component: profile, meta: { requiresAuth: true }}
  ],
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  const stored_user = localStorage.getItem('userInfo')
  const is_logged_in = !!stored_user

  if (to.meta.requiresAuth && !is_logged_in) {
    next('/login')
  } else if (to.path === '/login' && is_logged_in) {
    next('/goods/manage')
  } else if (to.meta.requiresAdmin) {
    // 管理员专属页面权限校验
    const user_data = stored_user ? JSON.parse(stored_user) : null
    if (user_data && user_data.role === 'ADMIN') {
      next()
    } else {
      next('/goods/manage')
    }
  } else {
    next()
  }
})

export default router
