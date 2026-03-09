import { createRouter, createWebHistory } from 'vue-router'
import { storage } from '@/utils/storage'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: () => import('@/components/layout/Layout.vue'),
      children: [
        {
          path: '',
          name: 'Home',
          component: () => import('@/views/home/Index.vue'),
          meta: { title: '首页' }
        },
        {
          path: 'venue',
          name: 'Venue',
          component: () => import('@/views/venue/Index.vue'),
          meta: { title: '会场信息' }
        },
        {
          path: 'schedule',
          name: 'Schedule',
          component: () => import('@/views/schedule/Index.vue'),
          meta: { title: '会议日程' }
        },
        {
          path: 'submission',
          name: 'Submission',
          component: () => import('@/views/submission/Index.vue'),
          meta: { title: '我的投稿', requiresAuth: true, roles: ['speaker'] }
        },
        {
          path: 'submission/create',
          name: 'SubmissionCreate',
          component: () => import('@/views/submission/Create.vue'),
          meta: { title: '提交投稿', requiresAuth: true, roles: ['speaker'] }
        },
        {
          path: 'submission/:id',
          name: 'SubmissionDetail',
          component: () => import('@/views/submission/Detail.vue'),
          meta: { title: '投稿详情', requiresAuth: true, roles: ['speaker'] }
        },
        {
          path: 'submission/:id/edit',
          name: 'SubmissionEdit',
          component: () => import('@/views/submission/Edit.vue'),
          meta: { title: '编辑投稿', requiresAuth: true, roles: ['speaker'] }
        },
        {
          path: 'review',
          name: 'Review',
          component: () => import('@/views/review/Index.vue'),
          meta: { title: '审稿管理', requiresAuth: true, roles: ['reviewer'] }
        },
        {
          path: 'review/:id',
          name: 'ReviewDetail',
          component: () => import('@/views/review/Detail.vue'),
          meta: { title: '审稿详情', requiresAuth: true, roles: ['reviewer'] }
        },
        {
          path: 'order/tickets',
          name: 'Tickets',
          component: () => import('@/views/order/Tickets.vue'),
          meta: { title: '购买票务', requiresAuth: true, roles: ['attendee'] }
        },
        {
          path: 'order/list',
          name: 'OrderList',
          component: () => import('@/views/order/List.vue'),
          meta: { title: '我的订单', requiresAuth: true, roles: ['attendee'] }
        },
        {
          path: 'order/:id/group',
          name: 'GroupDetail',
          component: () => import('@/views/order/GroupDetail.vue'),
          meta: { title: '团体订单管理', requiresAuth: true, roles: ['attendee'] }
        },
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('@/views/profile/Index.vue'),
          meta: { title: '个人中心', requiresAuth: true }
        }
      ]
    },
    {
      // Alipay synchronous return URL — no auth required (payment already completed)
      path: '/payment/return',
      name: 'PaymentReturn',
      component: () => import('@/views/payment/PaymentReturn.vue'),
      meta: { title: '支付结果' }
    },
    {
      // Public group invite fill page — no auth required
      path: '/group/invite/:token',
      name: 'InviteFill',
      component: () => import('@/views/group/InviteFill.vue'),
      meta: { title: '填写入会信息' }
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/auth/Login.vue'),
      meta: { title: '登录' }
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/auth/Register.vue'),
      meta: { title: '注册' }
    },
    {
      path: '/verify-email',
      name: 'VerifyEmail',
      component: () => import('@/views/auth/VerifyEmail.vue'),
      meta: { title: '邮箱验证' }
    }
  ],
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to, _from, next) => {
  document.title = `${to.meta.title as string} - 学术会议系统`

  const token = storage.getToken()
  if (to.meta.requiresAuth && !token) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }

  if ((to.name === 'Login' || to.name === 'Register' || to.name === 'VerifyEmail') && token) {
    next({ name: 'Home' })
    return
  }

  // Enforce role-based access — redirect home if user lacks the required role
  const requiredRoles = to.meta.roles as string[] | undefined
  if (requiredRoles && requiredRoles.length > 0 && token) {
    const userInfo = storage.getUserInfo<{ role: string }>()
    if (!userInfo || !requiredRoles.includes(userInfo.role)) {
      next({ name: 'Home' })
      return
    }
  }

  next()
})

export default router
