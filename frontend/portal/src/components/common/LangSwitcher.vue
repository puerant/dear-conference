<template>
  <el-dropdown trigger="click" @command="switchLang">
    <span class="lang-btn">
      {{ currentLang?.flag }} {{ currentLang?.name }}
      <el-icon><arrow-down /></el-icon>
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="lang in languages"
          :key="lang.code"
          :command="lang.code"
          :class="{ 'is-active': lang.code === locale }"
        >
          {{ lang.flag }} {{ lang.name }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { languages } from '@/i18n'

const { locale } = useI18n()

const currentLang = computed(() => languages.find(l => l.code === locale.value))

const switchLang = (code: string) => {
  locale.value = code
  localStorage.setItem('locale', code)
}
</script>

<style scoped lang="scss">
.lang-btn {
  display: flex;
  align-items: center;
  gap: $spacing-1;
  padding: $spacing-1 $spacing-3;
  border-radius: $radius-base;
  cursor: pointer;
  font-size: $font-size-sm;
  color: $text-secondary;
  transition: all 0.2s;

  &:hover {
    background: $bg-secondary;
    color: $primary-color;
  }
}
</style>
