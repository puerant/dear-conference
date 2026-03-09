<template>
  <div class="venue-page">
    <!-- Page Header -->
    <section class="page-header-section">
      <div class="container">
        <h1 class="page-title">{{ t('venue.title') }}</h1>
        <p class="page-subtitle">{{ t('venue.subtitle') }}</p>
      </div>
    </section>

    <!-- Map placeholder -->
    <section class="map-section">
      <div class="container">
        <div class="map-placeholder">
          <el-icon :size="64" color="#d4b7db"><location /></el-icon>
          <p>北京国际会议中心</p>
          <p class="map-address">北京市朝阳区天辰东路7号</p>
        </div>
      </div>
    </section>

    <!-- Hotels -->
    <section class="hotels-section">
      <div class="container">
        <h2 class="section-title">{{ t('venue.hotels') }}</h2>
        <div class="hotels-grid">
          <div
            v-for="hotel in hotels"
            :key="hotel.id"
            :class="['hotel-card', { 'hotel-main': hotel.type === 'main' }]"
          >
            <div class="hotel-image-placeholder">
              <el-icon :size="40" color="#9b59b6"><office-building /></el-icon>
              <span v-if="hotel.type === 'main'" class="main-badge">{{ t('venue.mainVenue') }}</span>
            </div>
            <div class="hotel-info">
              <h3 class="hotel-name">{{ hotel.name }}</h3>
              <div class="hotel-detail">
                <el-icon><location /></el-icon>
                <span>{{ hotel.address }}</span>
              </div>
              <div class="hotel-detail">
                <el-icon><timer /></el-icon>
                <span>{{ t('venue.distance') }}: {{ hotel.distance }}</span>
              </div>
              <div class="hotel-amenities">
                <el-tag v-for="a in hotel.amenities" :key="a" size="small" type="info">
                  {{ t(`amenities.${a}`) }}
                </el-tag>
              </div>
            </div>
            <div class="hotel-actions">
              <el-button type="primary" style="width: 100%">{{ t('venue.bookNow') }}</el-button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Transport -->
    <section class="transport-section">
      <div class="container">
        <h2 class="section-title">{{ t('venue.transport') }}</h2>
        <div class="transport-grid">
          <div v-for="item in transport" :key="item.type" class="transport-item">
            <div class="transport-icon">
              <el-icon :size="28"><component :is="item.icon" /></el-icon>
            </div>
            <div>
              <h4 class="transport-title">{{ item.from }} {{ t('venue.to') }} {{ item.to }}</h4>
              <p class="transport-time">{{ t('venue.duration') }} {{ item.time }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const hotels = ref([
  {
    id: 1,
    name: '国际会议中心酒店',
    type: 'main',
    address: '北京市朝阳区天辰东路7号',
    distance: '会场内',
    amenities: ['wifi', 'breakfast', 'parking', 'gym']
  },
  {
    id: 2,
    name: '北京希尔顿酒店',
    type: 'recommended',
    address: '北京市朝阳区建国路88号',
    distance: '步行5分钟',
    amenities: ['wifi', 'breakfast', 'pool']
  },
  {
    id: 3,
    name: '北京万豪酒店',
    type: 'recommended',
    address: '北京市朝阳区国贸路66号',
    distance: '步行10分钟',
    amenities: ['wifi', 'breakfast', 'spa']
  }
])

const transport = ref([
  { type: 'flight', icon: 'Promotion', from: '首都国际机场', to: '会场', time: '约45分钟' },
  { type: 'train', icon: 'Guide', from: '北京南站', to: '会场', time: '约30分钟' },
  { type: 'subway', icon: 'MapLocation', from: '国贸地铁站', to: '会场', time: '约5分钟' }
])
</script>

<style scoped lang="scss">
.venue-page {}

.map-section {
  padding: $spacing-16 0;
  background: $bg-secondary;

  .map-placeholder {
    height: 300px;
    border-radius: $radius-lg;
    background: white;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: $spacing-3;
    box-shadow: $shadow-base;
    color: $text-secondary;
    font-size: $font-size-lg;
    font-weight: $font-weight-medium;

    .map-address {
      font-size: $font-size-sm;
      color: $text-disabled;
    }
  }
}

.hotels-section {
  padding: $spacing-24 0;
}

.hotels-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-8;

  @include respond-to(lg) { grid-template-columns: repeat(2, 1fr); }
  @include respond-to(sm) { grid-template-columns: 1fr; }

  .hotel-card {
    background: white;
    border-radius: $radius-lg;
    overflow: hidden;
    box-shadow: $shadow-base;
    transition: all 0.3s;
    border: 2px solid transparent;

    &:hover {
      transform: translateY(-4px);
      box-shadow: $shadow-lg;
    }

    &.hotel-main {
      border-color: $primary-color;
    }

    .hotel-image-placeholder {
      height: 180px;
      background: linear-gradient(135deg, $primary-lighter, $bg-secondary);
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      position: relative;
      gap: $spacing-3;

      .main-badge {
        position: absolute;
        top: $spacing-3;
        left: $spacing-3;
        background: $primary-color;
        color: white;
        padding: $spacing-1 $spacing-3;
        border-radius: $radius-full;
        font-size: $font-size-xs;
        font-weight: $font-weight-semibold;
      }
    }

    .hotel-info {
      padding: $spacing-5;

      .hotel-name {
        font-size: $font-size-lg;
        font-weight: $font-weight-bold;
        margin-bottom: $spacing-3;
        color: $text-primary;
      }

      .hotel-detail {
        display: flex;
        align-items: center;
        gap: $spacing-2;
        color: $text-secondary;
        font-size: $font-size-sm;
        margin-bottom: $spacing-2;
      }

      .hotel-amenities {
        display: flex;
        flex-wrap: wrap;
        gap: $spacing-2;
        margin-top: $spacing-3;
      }
    }

    .hotel-actions {
      padding: $spacing-3 $spacing-5 $spacing-5;
    }
  }
}

.transport-section {
  padding: $spacing-24 0;
  background: $bg-secondary;
}

.transport-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-6;

  @include respond-to(md) { grid-template-columns: 1fr; }

  .transport-item {
    display: flex;
    align-items: center;
    gap: $spacing-5;
    padding: $spacing-6;
    background: white;
    border-radius: $radius-lg;
    box-shadow: $shadow-sm;

    .transport-icon {
      width: 60px;
      height: 60px;
      border-radius: $radius-full;
      background: $primary-lighter;
      color: $primary-color;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }

    .transport-title {
      font-size: $font-size-base;
      font-weight: $font-weight-semibold;
      color: $text-primary;
      margin-bottom: $spacing-1;
    }

    .transport-time {
      font-size: $font-size-sm;
      color: $text-secondary;
    }
  }
}
</style>
