<template>
  <footer class="app-footer">
    <div class="container">
      <div class="footer-grid">
        <div class="footer-section">
          <h4 class="footer-title">{{ t('footer.aboutUs') }}</h4>
          <p class="footer-text">{{ t('footer.description') }}</p>
        </div>
        <div class="footer-section">
          <h4 class="footer-title">{{ t('footer.quickLinks') }}</h4>
          <ul class="footer-links">
            <li><a @click="router.push('/')">{{ t('menu.home') }}</a></li>
            <li><a @click="router.push('/venue')">{{ t('menu.venue') }}</a></li>
            <li><a @click="router.push('/schedule')">{{ t('menu.schedule') }}</a></li>
          </ul>
        </div>
        <div class="footer-section">
          <h4 class="footer-title">{{ t('footer.contactUs') }}</h4>
          <div class="contact-info">
            <p><el-icon><message /></el-icon> contact@conference.com</p>
            <p><el-icon><phone /></el-icon> +86 10 1234 5678</p>
          </div>
        </div>
        <div class="footer-section">
          <h4 class="footer-title">{{ t('footer.followUs') }}</h4>
          <div class="social-links">
            <a href="#" class="social-icon"><el-icon><chat-dot-round /></el-icon></a>
            <a href="#" class="social-icon"><el-icon><video-camera /></el-icon></a>
            <a href="#" class="social-icon"><el-icon><platform /></el-icon></a>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <p>© {{ currentYear }} {{ t('app.name') }}. {{ t('footer.rightsReserved') }}</p>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'

const { t } = useI18n()
const router = useRouter()
const currentYear = new Date().getFullYear()
</script>

<style scoped lang="scss">
.app-footer {
  background: $bg-tertiary;
  color: $text-secondary;
  padding: $spacing-16 0 0;

  .footer-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: $spacing-10;
    padding-bottom: $spacing-16;

    @include respond-to(md) {
      grid-template-columns: repeat(2, 1fr);
    }

    @include respond-to(sm) {
      grid-template-columns: 1fr;
    }
  }

  .footer-section {
    .footer-title {
      font-size: $font-size-base;
      font-weight: $font-weight-semibold;
      color: $text-primary;
      margin-bottom: $spacing-4;
    }

    .footer-text {
      font-size: $font-size-sm;
      line-height: $line-height-relaxed;
    }

    .footer-links {
      list-style: none;
      padding: 0;
      margin: 0;

      li {
        margin-bottom: $spacing-2;

        a {
          font-size: $font-size-sm;
          color: $text-secondary;
          cursor: pointer;
          transition: color 0.2s;

          &:hover { color: $primary-color; }
        }
      }
    }

    .contact-info {
      p {
        display: flex;
        align-items: center;
        gap: $spacing-2;
        font-size: $font-size-sm;
        margin-bottom: $spacing-2;
      }
    }

    .social-links {
      display: flex;
      gap: $spacing-3;

      .social-icon {
        width: 36px;
        height: 36px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: white;
        border-radius: $radius-base;
        color: $primary-color;
        transition: all 0.2s;

        &:hover {
          background: $primary-color;
          color: white;
          transform: translateY(-2px);
        }
      }
    }
  }

  .footer-bottom {
    border-top: 1px solid $border-color;
    padding: $spacing-6 0;
    text-align: center;
    font-size: $font-size-sm;
  }
}
</style>
