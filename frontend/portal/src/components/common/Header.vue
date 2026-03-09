<template>
  <header class="app-header" :class="{ scrolled: isScrolled }">
    <div class="container">
      <div class="header-content">
        <!-- Logo -->
        <div class="logo" @click="router.push('/')">
          <el-icon :size="28" color="#9b59b6"><star /></el-icon>
          <span>{{ t('app.name') }}</span>
        </div>

        <!-- Desktop nav -->
        <nav class="nav-menu">
          <a
            v-for="item in visibleMenuItems"
            :key="item.name"
            :class="['nav-item', { active: isActive(item.name) }]"
            @click="router.push({ name: item.name })"
          >
            {{ t(item.label) }}
          </a>
        </nav>

        <!-- Right actions -->
        <div class="header-actions">
          <LangSwitcher />

          <template v-if="authStore.isLogin">
            <el-dropdown trigger="click" @command="handleCommand">
              <div class="user-info">
                <el-avatar :size="34" :src="authStore.userInfo?.avatar || undefined">
                  {{ authStore.userName.charAt(0) }}
                </el-avatar>
                <span class="user-name">{{ authStore.userName }}</span>
                <el-icon><arrow-down /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><user /></el-icon>{{ t('menu.profile') }}
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><switch-button /></el-icon>{{ t('menu.logout') }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>

          <template v-else>
            <el-button @click="router.push('/login')">{{ t('menu.login') }}</el-button>
            <el-button type="primary" @click="router.push('/register')">{{ t('menu.register') }}</el-button>
          </template>
        </div>

        <!-- Mobile hamburger -->
        <el-icon class="mobile-menu-btn" :size="24" @click="mobileMenuOpen = !mobileMenuOpen">
          <operation />
        </el-icon>
      </div>
    </div>

    <!-- Mobile menu -->
    <div v-if="mobileMenuOpen" class="mobile-menu">
      <a
        v-for="item in visibleMenuItems"
        :key="item.name"
        class="mobile-nav-item"
        @click="router.push({ name: item.name }); mobileMenuOpen = false"
      >
        {{ t(item.label) }}
      </a>
      <div class="mobile-actions">
        <template v-if="authStore.isLogin">
          <el-button @click="handleCommand('profile'); mobileMenuOpen = false" style="width: 100%; margin-bottom: 8px">
            {{ t('menu.profile') }}
          </el-button>
          <el-button type="danger" @click="handleCommand('logout'); mobileMenuOpen = false" style="width: 100%">
            {{ t('menu.logout') }}
          </el-button>
        </template>
        <template v-else>
          <el-button @click="router.push('/login'); mobileMenuOpen = false" style="width: 100%; margin-bottom: 8px">
            {{ t('menu.login') }}
          </el-button>
          <el-button type="primary" @click="router.push('/register'); mobileMenuOpen = false" style="width: 100%">
            {{ t('menu.register') }}
          </el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import LangSwitcher from './LangSwitcher.vue'

const router = useRouter()
const route = useRoute()
const { t } = useI18n()
const authStore = useAuthStore()

const isScrolled = ref(false)
const mobileMenuOpen = ref(false)

const allMenuItems = [
  { name: 'Home', label: 'menu.home', roles: null },
  { name: 'Venue', label: 'menu.venue', roles: null },
  { name: 'Schedule', label: 'menu.schedule', roles: null },
  { name: 'Submission', label: 'menu.submission', roles: ['speaker'] },
  { name: 'Review', label: 'menu.review', roles: ['reviewer'] },
  { name: 'Tickets', label: 'menu.tickets', roles: ['attendee'] },
  { name: 'OrderList', label: 'menu.orders', roles: ['attendee'] },
  { name: 'Profile', label: 'menu.profile', roles: ['speaker', 'reviewer', 'attendee'] }
]

const visibleMenuItems = computed(() => {
  return allMenuItems.filter(item => {
    if (!item.roles) return true
    if (!authStore.isLogin) return false
    return item.roles.includes(authStore.userRole)
  })
})

const isActive = (name: string) => {
  return route.name === name || route.path.startsWith(`/${name.toLowerCase()}`)
}

const handleCommand = (cmd: string) => {
  if (cmd === 'profile') {
    router.push({ name: 'Profile' })
  } else if (cmd === 'logout') {
    ElMessageBox.confirm('确认退出登录？', '提示', {
      confirmButtonText: '退出',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      authStore.logout()
      router.push({ name: 'Home' })
    }).catch(() => {/* cancelled */})
  }
}

const handleScroll = () => {
  isScrolled.value = window.scrollY > 20
}

onMounted(() => window.addEventListener('scroll', handleScroll))
onUnmounted(() => window.removeEventListener('scroll', handleScroll))
</script>

<style scoped lang="scss">
.app-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid $border-color;
  box-shadow: $shadow-sm;
  transition: box-shadow 0.3s;

  &.scrolled {
    box-shadow: $shadow-md;
  }

  .header-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: $spacing-4 0;
    gap: $spacing-6;
  }

  .logo {
    display: flex;
    align-items: center;
    gap: $spacing-2;
    font-size: $font-size-xl;
    font-weight: $font-weight-bold;
    color: $primary-color;
    cursor: pointer;
    flex-shrink: 0;
    transition: opacity 0.2s;

    &:hover { opacity: 0.85; }
  }

  .nav-menu {
    display: flex;
    gap: $spacing-1;
    flex: 1;
    justify-content: center;

    @include respond-to(lg) {
      display: none;
    }

    .nav-item {
      padding: $spacing-2 $spacing-4;
      color: $text-primary;
      border-radius: $radius-base;
      transition: all 0.2s;
      cursor: pointer;
      font-size: $font-size-sm;
      white-space: nowrap;

      &:hover {
        color: $primary-color;
        background: $primary-lighter;
      }

      &.active {
        color: $primary-color;
        font-weight: $font-weight-semibold;
        background: rgba($primary-color, 0.08);
      }
    }
  }

  .header-actions {
    display: flex;
    align-items: center;
    gap: $spacing-3;
    flex-shrink: 0;

    @include respond-to(lg) {
      display: none;
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: $spacing-2;
      padding: $spacing-1 $spacing-3;
      border-radius: $radius-full;
      cursor: pointer;
      transition: all 0.2s;

      &:hover { background: $bg-secondary; }

      .user-name {
        font-size: $font-size-sm;
        font-weight: $font-weight-medium;
        max-width: 100px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }

  .mobile-menu-btn {
    display: none;
    cursor: pointer;
    color: $text-primary;

    @include respond-to(lg) {
      display: flex;
    }
  }

  .mobile-menu {
    border-top: 1px solid $border-color;
    padding: $spacing-4;
    background: white;

    .mobile-nav-item {
      display: block;
      padding: $spacing-3 $spacing-4;
      color: $text-primary;
      border-radius: $radius-base;
      cursor: pointer;
      transition: all 0.2s;
      margin-bottom: $spacing-1;

      &:hover {
        color: $primary-color;
        background: $primary-lighter;
      }
    }

    .mobile-actions {
      margin-top: $spacing-4;
      padding-top: $spacing-4;
      border-top: 1px solid $divider-color;
    }
  }
}
</style>
