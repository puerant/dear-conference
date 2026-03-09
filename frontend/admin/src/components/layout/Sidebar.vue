<template>
  <div class="sidebar" :class="{ collapsed: appStore.sidebarCollapsed }">
    <div class="sidebar-logo">
      <el-icon size="24" color="#fff"><Management /></el-icon>
      <span v-if="!appStore.sidebarCollapsed" class="logo-text">会务通管理端</span>
    </div>

    <el-menu
      :default-active="activeMenu"
      :collapse="appStore.sidebarCollapsed"
      background-color="#001529"
      text-color="rgba(255,255,255,0.65)"
      active-text-color="#fff"
      router
      class="sidebar-menu"
    >
      <el-menu-item index="/dashboard">
        <el-icon><DataAnalysis /></el-icon>
        <template #title>控制台</template>
      </el-menu-item>

      <el-menu-item index="/user">
        <el-icon><User /></el-icon>
        <template #title>用户管理</template>
      </el-menu-item>

      <el-menu-item index="/submission">
        <el-icon><Document /></el-icon>
        <template #title>投稿管理</template>
      </el-menu-item>

      <el-menu-item index="/ticket">
        <el-icon><Ticket /></el-icon>
        <template #title>票务管理</template>
      </el-menu-item>

      <el-menu-item index="/order">
        <el-icon><ShoppingCart /></el-icon>
        <template #title>订单管理</template>
      </el-menu-item>

      <el-menu-item index="/credential">
        <el-icon><Tickets /></el-icon>
        <template #title>凭证管理</template>
      </el-menu-item>

      <el-sub-menu index="/conference">
        <template #title>
          <el-icon><Calendar /></el-icon>
          <span>会议管理</span>
        </template>
        <el-menu-item index="/conference/info">
          <template #title>会议信息</template>
        </el-menu-item>
        <el-menu-item index="/conference/expert">
          <template #title>专家管理</template>
        </el-menu-item>
        <el-menu-item index="/conference/schedule">
          <template #title>日程管理</template>
        </el-menu-item>
        <el-menu-item index="/conference/hotel">
          <template #title>酒店管理</template>
        </el-menu-item>
      </el-sub-menu>

      <el-sub-menu index="/system">
        <template #title>
          <el-icon><Tools /></el-icon>
          <span>系统管理</span>
        </template>
        <el-menu-item index="/dictionary">
          <el-icon><Setting /></el-icon>
          <template #title>字典管理</template>
        </el-menu-item>
        <el-menu-item index="/system/credential">
          <el-icon><Postcard /></el-icon>
          <template #title>凭证配置</template>
        </el-menu-item>
        <el-menu-item index="/system/payment">
          <el-icon><Wallet /></el-icon>
          <template #title>支付配置</template>
        </el-menu-item>
        <el-menu-item index="/system/task">
          <el-icon><Timer /></el-icon>
          <template #title>定时任务</template>
        </el-menu-item>
        <el-sub-menu index="/system/email-management">
          <template #title>
            <el-icon><Message /></el-icon>
            <span>邮箱管理</span>
          </template>
          <el-menu-item index="/system/email">
            <template #title>邮箱配置</template>
          </el-menu-item>
          <el-menu-item index="/system/email-template">
            <template #title>邮箱模板</template>
          </el-menu-item>
          <el-menu-item index="/system/email-log">
            <template #title>邮箱日志</template>
          </el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/system/storage-management">
          <template #title>
            <el-icon><Folder /></el-icon>
            <span>存储管理</span>
          </template>
          <el-menu-item index="/system/files">
            <el-icon><Folder /></el-icon>
            <template #title>文件管理</template>
          </el-menu-item>
          <el-menu-item index="/system/storage-buckets">
            <el-icon><Coin /></el-icon>
            <template #title>存储桶管理</template>
          </el-menu-item>
          <el-menu-item index="/system/storage-policies">
            <el-icon><Operation /></el-icon>
            <template #title>存储策略</template>
          </el-menu-item>
          <el-menu-item index="/system/storage-migration">
            <el-icon><Refresh /></el-icon>
            <template #title>文件迁移</template>
          </el-menu-item>
          <el-menu-item index="/system/storage-providers">
            <el-icon><Connection /></el-icon>
            <template #title>OSS供应商</template>
          </el-menu-item>
        </el-sub-menu>
      </el-sub-menu>
    </el-menu>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/stores/app'
import {
  DataAnalysis,
  User,
  Document,
  Ticket,
  ShoppingCart,
  Tickets,
  Setting,
  Management,
  Tools,
  Message,
  Postcard,
  Wallet,
  Timer,
  Folder,
  Connection,
  Coin,
  Operation,
  Refresh,
  Calendar
} from '@element-plus/icons-vue'

const route = useRoute()
const appStore = useAppStore()

const activeMenu = computed(() => {
  const path = route.path
  // Highlight parent menu for detail pages
  if (path.startsWith('/submission/')) return '/submission'
  if (path.startsWith('/order/')) return '/order'
  if (path.startsWith('/system/')) return path
  return path
})
</script>

<style lang="scss" scoped>
.sidebar {
  width: 220px;
  height: 100vh;
  background: #001529;
  transition: width 0.3s;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  &.collapsed {
    width: 64px;

    .logo-text {
      display: none;
    }
  }

  .sidebar-logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    padding: 0 16px;
    cursor: pointer;

    .logo-text {
      color: #fff;
      font-size: 16px;
      font-weight: 600;
      white-space: nowrap;
      overflow: hidden;
    }
  }

  .sidebar-menu {
    border-right: none;
    flex: 1;
    min-height: 0;
    overflow-y: auto;
    overflow-x: hidden;

    :deep(.el-menu-item) {
      &.is-active {
        background-color: #1677ff !important;
      }

      &:hover {
        background-color: rgba(255, 255, 255, 0.08) !important;
      }
    }

    :deep(.el-sub-menu) {
      .el-sub-menu__title {
        &:hover {
          background-color: rgba(255, 255, 255, 0.08) !important;
        }
      }

      .el-menu-item {
        &.is-active {
          background-color: #1677ff !important;
        }

        &:hover {
          background-color: rgba(255, 255, 255, 0.08) !important;
        }
      }
    }
  }
}
</style>
