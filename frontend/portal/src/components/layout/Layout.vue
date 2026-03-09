<template>
  <div class="portal-layout">
    <AppHeader />
    <main class="main-content">
      <router-view v-slot="{ Component, route }">
        <transition name="page" mode="out-in">
          <component :is="Component" :key="route?.path" />
        </transition>
      </router-view>
    </main>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import AppHeader from '@/components/common/Header.vue'
import AppFooter from '@/components/common/Footer.vue'
</script>

<style scoped lang="scss">
.portal-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: $bg-page;
}

.main-content {
  flex: 1;
  padding-top: 72px;

  &.full-width {
    padding-top: 0;
  }
}
</style>
