<template>
  <div class="hotel-page">
    <section class="page-header-section">
      <div class="container">
        <h1 class="page-title">{{ t('hotel.title') }}</h1>
        <p class="page-subtitle">{{ t('hotel.subtitle') }}</p>
      </div>
    </section>

    <section class="hotels-section" v-loading="loading">
      <div class="container">
        <!-- 推荐酒店 -->
        <div v-if="recommendedHotels.length > 1" class="hotel-group">
          <h2 class="group-title">
            <el-icon><Star /></el-icon>
            推荐酒店
          </h2>
          <div class="hotels-grid">
            <div v-for="hotel in recommendedHotels" :key="hotel.id" class="hotel-card recommended">
              <div class="hotel-image">
                <el-image :src="hotel.imageUrl" fit="cover">
                  <template #error>
                    <div class="image-placeholder">
                      <el-icon :size="48"><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
                <div class="star-badge">
                  <el-rate v-model="hotel.starLevel" disabled />
                </div>
              </div>
              <div class="hotel-info">
                <h3 class="hotel-name">{{ hotel.name }}</h3>
                <p class="hotel-address">
                  <el-icon><Location /></el-icon>
                  {{ hotel.address }}
                </p>
                <p class="hotel-desc">{{ hotel.description }}</p>
                <div class="hotel-contact">
                  <el-icon><Phone /></el-icon>
                  {{ hotel.contactPhone }}
                </div>
                <div class="hotel-rooms">
                  <el-tag v-for="room in hotel.rooms?.slice(0, 3)" :key="room.id" size="small">
                    {{ room.roomType }} - ¥{{ room.price }}
                  </el-tag>
                </div>
                <div class="hotel-actions">
                  <el-button v-if="hotel.bookingUrl" type="primary" :href="hotel.bookingUrl" target="_blank">
                    立即预订
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 其他酒店 -->
        <div v-if="otherHotels.length > 1" class="hotel-group">
          <h2 class="group-title">
            <el-icon><OfficeBuilding /></el-icon>
            其他酒店
          </h2>
          <div class="hotels-grid">
            <div v-for="hotel in otherHotels" :key="hotel.id" class="hotel-card">
              <div class="hotel-image">
                <el-image :src="hotel.imageUrl" fit="cover">
                  <template #error>
                    <div class="image-placeholder">
                      <el-icon :size="48"><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
                <div class="star-badge">
                  <el-rate v-model="hotel.starLevel" disabled />
                </div>
              </div>
              <div class="hotel-info">
                <h3 class="hotel-name">{{ hotel.name }}</h3>
                <p class="hotel-address">
                  <el-icon><Location /></el-icon>
                  {{ hotel.address }}
                </p>
                <p class="hotel-desc">{{ hotel.description }}</p>
                <div class="hotel-contact">
                  <el-icon><Phone /></el-icon>
                  {{ hotel.contactPhone }}
                </div>
                <div class="hotel-rooms">
                  <el-tag v-for="room in hotel.rooms?.slice(0, 3)" :key="room.id" size="small" type="info">
                    {{ room.roomType }} - ¥{{ room.price }}
                  </el-tag>
                </div>
                <div class="hotel-actions">
                  <el-button v-if="hotel.bookingUrl" type="primary" plain :href="hotel.bookingUrl" target="_blank">
                    立即预订
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <el-empty v-if="!loading && recommendedHotels.length === 0 && otherHotels.length === 0" description="暂无酒店信息" />
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { Star, Location, Phone, Picture, OfficeBuilding } from '@element-plus/icons-vue'
import { getHotelList } from '@/api/conference'
import type { HotelVo } from '@/api/types'

const { t } = useI18n()

const loading = ref(false)
const allHotels = ref<HotelVo[]>([])

const recommendedHotels = computed(() => allHotels.value.filter(h => h.isRecommended === 1))
const otherHotels = computed(() => allHotels.value.filter(h => h.isRecommended !== 1))

const loadHotels = async () => {
  try {
    loading.value = true
    const data = await getHotelList()
    allHotels.value = data?.recommended || []
    allHotels.value = [...allHotels.value, ...(data?.hotels || [])]
  } catch (error) {
    console.error('加载酒店失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadHotels()
})
</script>

<style scoped lang="scss">
.hotel-page {}

.hotels-section {
  padding: $spacing-12 0;
}

.hotel-group {
  margin-bottom: $spacing-12;
}

.group-title {
  display: flex;
  align-items: center;
  gap: $spacing-2;
  font-size: $font-size-xl;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: $spacing-6;
}

.hotels-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: $spacing-6;
}

.hotel-card {
  background: white;
  border-radius: $radius-lg;
  overflow: hidden;
  transition: all 0.3s;
  box-shadow: $shadow-sm;

  &:hover {
    transform: translateY(-4px);
    box-shadow: $shadow-lg;
  }

  &.recommended {
    border: 2px solid $warning-color;
  }

  .hotel-image {
    height: 180px;
    position: relative;

    .el-image {
      width: 100%;
      height: 100%;
    }

    .image-placeholder {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      background: $bg-secondary;
      color: $text-placeholder;
    }

    .star-badge {
      position: absolute;
      bottom: $spacing-2;
      right: $spacing-2;
      background: rgba(255, 255, 255, 0.9);
      padding: $spacing-1 $spacing-2;
      border-radius: $radius-sm;
    }
  }

  .hotel-info {
    padding: $spacing-4;

    .hotel-name {
      font-size: $font-size-lg;
      font-weight: $font-weight-semibold;
      color: $text-primary;
      margin-bottom: $spacing-2;
    }

    .hotel-address {
      display: flex;
      align-items: center;
      gap: $spacing-1;
      font-size: $font-size-sm;
      color: $text-secondary;
      margin-bottom: $spacing-2;
    }

    .hotel-desc {
      font-size: $font-size-sm;
      color: $text-placeholder;
      line-height: $line-height-relaxed;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      margin-bottom: $spacing-2;
    }

    .hotel-contact {
      display: flex;
      align-items: center;
      gap: $spacing-1;
      font-size: $font-size-sm;
      color: $primary-color;
      margin-bottom: $spacing-3;
    }

    .hotel-rooms {
      display: flex;
      flex-wrap: wrap;
      gap: $spacing-2;
      margin-bottom: $spacing-3;
    }

    .hotel-actions {
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style>
