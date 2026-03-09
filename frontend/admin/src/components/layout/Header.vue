<template>
  <header class="app-header">
    <div class="header-left">
      <el-button
        :icon="appStore.sidebarCollapsed ? Expand : Fold"
        text
        size="large"
        @click="appStore.toggleSidebar()"
      />
      <!-- Breadcrumb -->
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-if="currentTitle !== '控制台'">{{ currentTitle }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="header-right">
      <el-tooltip content="切换主题" placement="bottom">
        <el-button
          :icon="appStore.isDarkMode ? Sunny : Moon"
          text
          circle
          @click="appStore.toggleDarkMode()"
        />
      </el-tooltip>

      <el-dropdown trigger="click" @command="handleCommand">
        <div class="user-info">
          <el-avatar :size="32" :src="authStore.userInfo?.avatar || ''" class="user-avatar">
            {{ authStore.userName?.charAt(0) || 'A' }}
          </el-avatar>
          <span class="user-name">{{ authStore.userName || '管理员' }}</span>
          <el-icon><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="logout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </header>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { Fold, Expand, Moon, Sunny, ArrowDown, SwitchButton } from '@element-plus/icons-vue'
import { useAppStore } from '@/stores/app'
import { useAuthStore } from '@/stores/auth'
import { authApi } from '@/api/auth'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const authStore = useAuthStore()

const currentTitle = computed(() => (route.meta.title as string) || '')

async function handleCommand(command: string) {
  if (command === 'logout') {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    try {
      await authApi.logout()
    } catch {
      // ignore logout api errors
    } finally {
      authStore.logout()
      router.push('/login')
    }
  }
}
</script>

<style lang="scss" scoped>
.app-header {
  height: 60px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px 0 8px;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);

  .header-left {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 8px;

    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      padding: 4px 8px;
      border-radius: 6px;
      transition: background 0.2s;

      &:hover {
        background: #f5f5f5;
      }

      .user-avatar {
        background: #1677ff;
        font-size: 13px;
        font-weight: 600;
      }

      .user-name {
        font-size: 14px;
        color: #333;
      }
    }
  }
}
</style>
