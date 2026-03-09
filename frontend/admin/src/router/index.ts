import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/login/index.vue'),
      meta: { title: '登录', requiresAuth: false }
    },
    {
      path: '/',
      component: () => import('@/components/layout/Layout.vue'),
      redirect: '/dashboard',
      meta: { requiresAuth: true },
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/dashboard/index.vue'),
          meta: { title: '控制台', icon: 'DataAnalysis', requiresAuth: true }
        },
        {
          path: 'user',
          name: 'User',
          component: () => import('@/views/user/index.vue'),
          meta: { title: '用户管理', icon: 'User', requiresAuth: true }
        },
        {
          path: 'submission',
          name: 'Submission',
          component: () => import('@/views/submission/index.vue'),
          meta: { title: '投稿管理', icon: 'Document', requiresAuth: true }
        },
        {
          path: 'submission/:id',
          name: 'SubmissionDetail',
          component: () => import('@/views/submission/detail.vue'),
          meta: { title: '投稿详情', requiresAuth: true }
        },
        {
          path: 'ticket',
          name: 'Ticket',
          component: () => import('@/views/ticket/index.vue'),
          meta: { title: '票务管理', icon: 'Ticket', requiresAuth: true }
        },
        {
          path: 'order',
          name: 'Order',
          component: () => import('@/views/order/index.vue'),
          meta: { title: '订单管理', icon: 'ShoppingCart', requiresAuth: true }
        },
        {
          path: 'order/:id',
          name: 'OrderDetail',
          component: () => import('@/views/order/detail.vue'),
          meta: { title: '订单详情', requiresAuth: true }
        },
        {
          path: 'credential',
          name: 'Credential',
          component: () => import('@/views/credential/index.vue'),
          meta: { title: '凭证管理', icon: 'Tickets', requiresAuth: true }
        },
        {
          path: 'dictionary',
          name: 'Dictionary',
          component: () => import('@/views/dictionary/index.vue'),
          meta: { title: '字典管理', icon: 'Setting', requiresAuth: true }
        },
        // 会议管理
        {
          path: 'conference',
          name: 'Conference',
          redirect: '/conference/info',
          meta: { title: '会议管理', icon: 'Calendar', requiresAuth: true },
          children: [
            {
              path: 'info',
              name: 'ConferenceInfo',
              component: () => import('@/views/conference/index.vue'),
              meta: { title: '会议信息', requiresAuth: true }
            },
            {
              path: 'expert',
              name: 'ConferenceExpert',
              component: () => import('@/views/expert/index.vue'),
              meta: { title: '专家管理', requiresAuth: true }
            },
            {
              path: 'schedule',
              name: 'ConferenceSchedule',
              component: () => import('@/views/schedule/index.vue'),
              meta: { title: '日程管理', requiresAuth: true }
            },
            {
              path: 'hotel',
              name: 'ConferenceHotel',
              component: () => import('@/views/hotel/index.vue'),
              meta: { title: '酒店管理', requiresAuth: true }
            }
          ]
        },
        // 系统管理
        {
          path: 'system',
          name: 'System',
          redirect: '/system/email',
          meta: { title: '系统管理', icon: 'Tools', requiresAuth: true },
          children: [
            {
              path: 'email',
              name: 'SystemEmail',
              component: () => import('@/views/system/email.vue'),
              meta: { title: '邮箱配置', requiresAuth: true }
            },
            {
              path: 'credential',
              name: 'SystemCredential',
              component: () => import('@/views/system/credential.vue'),
              meta: { title: '凭证配置', requiresAuth: true }
            },
            {
              path: 'payment',
              name: 'SystemPayment',
              component: () => import('@/views/system/payment.vue'),
              meta: { title: '支付配置', requiresAuth: true }
            },
            {
              path: 'task',
              name: 'SystemTask',
              component: () => import('@/views/system/task.vue'),
              meta: { title: '定时任务', requiresAuth: true }
            },
            {
              path: 'files',
              name: 'SystemFiles',
              component: () => import('@/views/system/files.vue'),
              meta: { title: '文件管理', requiresAuth: true }
            },
            {
              path: 'storage-providers',
              name: 'SystemStorageProviders',
              component: () => import('@/views/system/storage-providers.vue'),
              meta: { title: 'OSS供应商', requiresAuth: true }
            },
            {
              path: 'storage-buckets',
              name: 'SystemStorageBuckets',
              component: () => import('@/views/system/storage-buckets.vue'),
              meta: { title: '存储桶管理', requiresAuth: true }
            },
            {
              path: 'storage-policies',
              name: 'SystemStoragePolicies',
              component: () => import('@/views/system/storage-policies.vue'),
              meta: { title: '存储策略', requiresAuth: true }
            },
            {
              path: 'storage-migration',
              name: 'SystemStorageMigration',
              component: () => import('@/views/system/storage-migration.vue'),
              meta: { title: '文件迁移', requiresAuth: true }
            },
            {
              path: 'email-template',
              name: 'SystemEmailTemplate',
              component: () => import('@/views/system/email-template.vue'),
              meta: { title: '邮件模板', requiresAuth: true }
            },
            {
              path: 'email-log',
              name: 'SystemEmailLog',
              component: () => import('@/views/system/email-log.vue'),
              meta: { title: '邮件日志', requiresAuth: true }
            }
          ]
        }
      ]
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/'
    }
  ]
})

router.beforeEach((to, _from, next) => {
  // Set page title
  const title = to.meta.title as string | undefined
  document.title = title ? `${title} - 学术会议管理平台` : '学术会议管理平台'

  const authStore = useAuthStore()

  // Allow public pages
  if (to.meta.requiresAuth === false) {
    // Already logged in, redirect to dashboard
    if (to.name === 'Login' && authStore.isLogin) {
      next({ name: 'Dashboard' })
    } else {
      next()
    }
    return
  }

  // Require auth
  if (!authStore.isLogin) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }

  // Require admin role — any other role is rejected
  if (authStore.userInfo?.role !== 'admin') {
    authStore.logout()
    next({ name: 'Login' })
    return
  }

  next()
})

export default router
