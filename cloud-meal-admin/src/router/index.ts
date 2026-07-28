import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: () => import('@/views/LoginView.vue') },
    {
      path: '/', component: () => import('@/layout/AdminLayout.vue'), redirect: '/dashboard',
      children: [
        { path: 'dashboard', component: () => import('@/views/DashboardView.vue') },
        { path: 'dishes', component: () => import('@/views/DishView.vue') },
        { path: 'orders', component: () => import('@/views/OrderView.vue') },
      ],
    },
  ],
})
router.beforeEach(to => {
  if (to.path !== '/login' && !localStorage.getItem('admin_token')) return '/login'
  if (to.path === '/login' && localStorage.getItem('admin_token')) return '/dashboard'
})
export default router
