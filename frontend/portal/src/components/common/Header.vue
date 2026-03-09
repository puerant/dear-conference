<template>
  <header class="app-header" :class="{ 'is-scrolled': isScrolled, 'is-transparent': isTransparent }">
    <div class="container">
      <div class="header-inner">
        <!-- Logo -->
        <router-link to="/" class="logo">
          <div class="logo-icon">
            <svg viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect width="32" height="32" rx="8" fill="url(#logo-gradient)"/>
              <path d="M8 16L14 10L20 16L14 22L8 16Z" fill="white" fill-opacity="0.9"/>
              <path d="M14 16L20 10L26 16L20 22L14 16Z" fill="white" fill-opacity="0.6"/>
              <defs>
                <linearGradient id="logo-gradient" x1="0" y1="0" x2="32" y2="32" gradientUnits="userSpaceOnUse">
                  <stop stop-color="#3B82F6"/>
                  <stop offset="1" stop-color="#8B5CF6"/>
                </linearGradient>
              </defs>
            </svg>
          </div>
          <span class="logo-text">{{ t('app.name') }}</span>
        </router-link>

        <!-- Desktop Navigation -->
        <nav class="nav-desktop">
          <router-link
            v-for="item in visibleMenuItems"
            :key="item.name"
            :to="{ name: item.name }"
            class="nav-link"
            :class="{ 'is-active': isActive(item.name) }"
          >
            {{ t(item.label) }}
          </router-link>
        </nav>

        <!-- Right Actions -->
        <div class="header-right">
          <LangSwitcher class="lang-switch" />

          <template v-if="authStore.isLogin">
            <el-dropdown trigger="click" placement="bottom-end" @command="handleCommand">
              <div class="user-dropdown">
                <el-avatar
                  :size="36"
                  :src="authStore.userInfo?.avatar || undefined"
                  class="user-avatar"
                >
                  {{ authStore.userName?.charAt(0)?.toUpperCase() }}
                </el-avatar>
                <span class="user-name">{{ authStore.userName }}</span>
                <el-icon class="dropdown-arrow"><arrow-down /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu class="user-menu">
                  <div class="menu-header">
                    <el-avatar :size="40" class="menu-avatar">
                      {{ authStore.userName?.charAt(0)?.toUpperCase() }}
                    </el-avatar>
                    <div class="menu-user-info">
                      <div class="menu-user-name">{{ authStore.userName }}</div>
                      <div class="menu-user-email">{{ authStore.userInfo?.email }}</div>
                    </div>
                  </div>
                  <el-dropdown-item command="profile" class="menu-item">
                    <el-icon><user /></el-icon>
                    <span>{{ t('menu.profile') }}</span>
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout" class="menu-item">
                    <el-icon><switch-button /></el-icon>
                    <span>{{ t('menu.logout') }}</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>

          <template v-else>
            <div class="auth-buttons">
              <el-button class="btn-login" @click="router.push('/login')">
                {{ t('menu.login') }}
              </el-button>
              <el-button type="primary" class="btn-register" @click="router.push('/register')">
                {{ t('menu.register') }}
              </el-button>
            </div>
          </template>

          <!-- Mobile Menu Button -->
          <button class="mobile-menu-btn" @click="toggleMobileMenu" :aria-expanded="mobileMenuOpen">
            <span class="hamburger" :class="{ 'is-open': mobileMenuOpen }">
              <span></span>
              <span></span>
              <span></span>
            </span>
          </button>
        </div>
      </div>
    </div>

    <!-- Mobile Menu -->
    <Transition name="mobile-menu">
      <div v-if="mobileMenuOpen" class="mobile-nav">
        <nav class="mobile-nav-inner">
          <router-link
            v-for="item in visibleMenuItems"
            :key="item.name"
            :to="{ name: item.name }"
            class="mobile-nav-link"
            @click="closeMobileMenu"
          >
            {{ t(item.label) }}
          </router-link>
        </nav>
        <div class="mobile-nav-footer">
          <template v-if="authStore.isLogin">
            <el-button class="mobile-btn" @click="goToProfile">
              {{ t('menu.profile') }}
            </el-button>
            <el-button type="danger" plain class="mobile-btn" @click="handleLogout">
              {{ t('menu.logout') }}
            </el-button>
          </template>
          <template v-else>
            <el-button class="mobile-btn" @click="router.push('/login'); closeMobileMenu()">
              {{ t('menu.login') }}
            </el-button>
            <el-button type="primary" class="mobile-btn" @click="router.push('/register'); closeMobileMenu()">
              {{ t('menu.register') }}
            </el-button>
          </template>
        </div>
      </div>
    </Transition>
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

// Check if header should be transparent (on home page)
const isTransparent = computed(() => route.name === 'Home' && !isScrolled.value)

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
    handleLogout()
  }
}

const handleLogout = () => {
  ElMessageBox.confirm('确认退出登录？', '提示', {
    confirmButtonText: '退出',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    authStore.logout()
    closeMobileMenu()
    router.push({ name: 'Home' })
  }).catch(() => {})
}

const goToProfile = () => {
  closeMobileMenu()
  router.push({ name: 'Profile' })
}

const toggleMobileMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value
}

const closeMobileMenu = () => {
  mobileMenuOpen.value = false
}

const handleScroll = () => {
  isScrolled.value = window.scrollY > 20
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  handleScroll()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped lang="scss">
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 72px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid transparent;
  transition: all $transition-base;

  &.is-scrolled {
    background: rgba(255, 255, 255, 0.95);
    border-bottom-color: $border-light;
    box-shadow: $shadow-sm;
  }

  &.is-transparent {
    // Keep high contrast even on light hero backgrounds.
    background: rgba(255, 255, 255, 0.72);
    border-bottom-color: rgba(229, 231, 235, 0.65);
    box-shadow: $shadow-xs;
  }

  .header-inner {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 72px;
    gap: $spacing-8;
  }
}

// Logo
.logo {
  display: flex;
  align-items: center;
  gap: $spacing-3;
  text-decoration: none;
  flex-shrink: 0;

  .logo-icon {
    width: 40px;
    height: 40px;

    svg {
      width: 100%;
      height: 100%;
    }
  }

  .logo-text {
    font-size: $font-size-xl;
    font-weight: $font-weight-bold;
    color: $text-primary;
    letter-spacing: -0.02em;
  }

  &:hover .logo-text {
    color: $primary-500;
  }
}

// Desktop Navigation
.nav-desktop {
  display: flex;
  align-items: center;
  gap: $spacing-1;

  @include respond-to(lg) {
    display: none;
  }

  .nav-link {
    padding: $spacing-2 $spacing-4;
    font-size: $font-size-sm;
    font-weight: $font-weight-medium;
    color: $text-secondary;
    text-decoration: none;
    border-radius: $radius-lg;
    transition: all $transition-fast;

    &:hover {
      color: $text-primary;
      background: $gray-100;
    }

    &.is-active {
      color: $primary-500;
      background: $primary-50;
      font-weight: $font-weight-semibold;
    }
  }
}

// Header Right
.header-right {
  display: flex;
  align-items: center;
  gap: $spacing-4;

  .lang-switch {
    @include respond-to(md) {
      display: none;
    }
  }
}

// Auth Buttons
.auth-buttons {
  display: flex;
  align-items: center;
  gap: $spacing-3;

  @include respond-to(lg) {
    display: none;
  }

  .btn-login {
    padding: $spacing-2 $spacing-5;
    font-weight: $font-weight-medium;
    border-radius: $radius-lg;
  }

  .btn-register {
    padding: $spacing-2 $spacing-5;
    font-weight: $font-weight-medium;
    border-radius: $radius-lg;
    background: $primary-500;
    border-color: $primary-500;

    &:hover {
      background: $primary-600;
      border-color: $primary-600;
    }
  }
}

// User Dropdown
.user-dropdown {
  display: flex;
  align-items: center;
  gap: $spacing-2;
  padding: $spacing-1 $spacing-2 $spacing-1 $spacing-1;
  border-radius: $radius-full;
  cursor: pointer;
  transition: all $transition-fast;

  &:hover {
    background: $gray-100;
  }

  .user-avatar {
    background: $brand-gradient;
    color: white;
    font-weight: $font-weight-semibold;
  }

  .user-name {
    font-size: $font-size-sm;
    font-weight: $font-weight-medium;
    color: $text-primary;
    max-width: 100px;
    @include text-ellipsis;
  }

  .dropdown-arrow {
    font-size: 12px;
    color: $text-tertiary;
    transition: transform $transition-fast;
  }
}

// Mobile Menu Button
.mobile-menu-btn {
  display: none;
  width: 40px;
  height: 40px;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  cursor: pointer;

  @include respond-to(lg) {
    display: flex;
  }

  .hamburger {
    width: 20px;
    height: 14px;
    position: relative;

    span {
      position: absolute;
      left: 0;
      width: 100%;
      height: 2px;
      background: $text-primary;
      border-radius: $radius-full;
      transition: all $transition-fast;

      &:nth-child(1) { top: 0; }
      &:nth-child(2) { top: 6px; }
      &:nth-child(3) { top: 12px; }
    }

    &.is-open span {
      &:nth-child(1) {
        transform: translateY(6px) rotate(45deg);
      }
      &:nth-child(2) {
        opacity: 0;
      }
      &:nth-child(3) {
        transform: translateY(-6px) rotate(-45deg);
      }
    }
  }
}

// Mobile Navigation
.mobile-nav {
  position: absolute;
  top: 72px;
  left: 0;
  right: 0;
  background: white;
  border-bottom: 1px solid $border-light;
  box-shadow: $shadow-lg;
  padding: $spacing-4;

  .mobile-nav-inner {
    display: flex;
    flex-direction: column;
    gap: $spacing-1;
    margin-bottom: $spacing-4;
  }

  .mobile-nav-link {
    padding: $spacing-3 $spacing-4;
    font-size: $font-size-base;
    font-weight: $font-weight-medium;
    color: $text-primary;
    text-decoration: none;
    border-radius: $radius-lg;
    transition: all $transition-fast;

    &:hover,
    &.router-link-active {
      background: $primary-50;
      color: $primary-500;
    }
  }

  .mobile-nav-footer {
    display: flex;
    flex-direction: column;
    gap: $spacing-2;
    padding-top: $spacing-4;
    border-top: 1px solid $border-light;

    .mobile-btn {
      width: 100%;
      justify-content: center;
    }
  }
}

// Mobile Menu Animation
.mobile-menu-enter-active,
.mobile-menu-leave-active {
  transition: all 0.3s ease;
}

.mobile-menu-enter-from,
.mobile-menu-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

// User Menu Dropdown Styles
:deep(.user-menu) {
  padding: $spacing-2;
  border-radius: $radius-xl;
  box-shadow: $shadow-xl;
  border: 1px solid $border-light;
  min-width: 220px;

  .menu-header {
    display: flex;
    align-items: center;
    gap: $spacing-3;
    padding: $spacing-3 $spacing-4;
    margin-bottom: $spacing-2;
    border-radius: $radius-lg;
    background: $gray-50;

    .menu-avatar {
      background: $brand-gradient;
      color: white;
      font-weight: $font-weight-semibold;
    }

    .menu-user-info {
      .menu-user-name {
        font-weight: $font-weight-semibold;
        color: $text-primary;
      }
      .menu-user-email {
        font-size: $font-size-xs;
        color: $text-tertiary;
      }
    }
  }

  .menu-item {
    padding: $spacing-3 $spacing-4;
    border-radius: $radius-lg;
    margin: $spacing-1 0;
    display: flex;
    align-items: center;
    gap: $spacing-3;

    .el-icon {
      font-size: 16px;
      color: $text-secondary;
    }

    &:hover {
      background: $primary-50;
      color: $primary-500;

      .el-icon {
        color: $primary-500;
      }
    }
  }
}
</style>
