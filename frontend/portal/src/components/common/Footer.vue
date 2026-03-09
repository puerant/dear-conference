<template>
  <footer class="app-footer">
    <div class="container">
      <div class="footer-main">
        <!-- Brand Section -->
        <div class="footer-brand">
          <router-link to="/" class="footer-logo">
            <div class="logo-icon">
              <svg viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect width="32" height="32" rx="8" fill="url(#footer-logo-gradient)"/>
                <path d="M8 16L14 10L20 16L14 22L8 16Z" fill="white" fill-opacity="0.9"/>
                <path d="M14 16L20 10L26 16L20 22L14 16Z" fill="white" fill-opacity="0.6"/>
                <defs>
                  <linearGradient id="footer-logo-gradient" x1="0" y1="0" x2="32" y2="32" gradientUnits="userSpaceOnUse">
                    <stop stop-color="#3B82F6"/>
                    <stop offset="1" stop-color="#8B5CF6"/>
                  </linearGradient>
                </defs>
              </svg>
            </div>
            <span>{{ t('app.name') }}</span>
          </router-link>
          <p class="footer-tagline">{{ t('footer.description') }}</p>
          <div class="social-links">
            <a href="#" class="social-link" title="WeChat">
              <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 01.213.665l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 00.167-.054l1.903-1.114a.864.864 0 01.717-.098 10.16 10.16 0 002.837.403c.276 0 .543-.027.811-.05-.857-2.578.157-4.972 1.932-6.446 1.703-1.415 3.882-1.98 5.853-1.838-.576-3.583-4.196-6.348-8.596-6.348zM5.785 5.991c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 01-1.162 1.178A1.17 1.17 0 014.623 7.17c0-.651.52-1.18 1.162-1.18zm5.813 0c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 01-1.162 1.178 1.17 1.17 0 01-1.162-1.178c0-.651.52-1.18 1.162-1.18zm5.34 2.867c-1.797-.052-3.746.512-5.28 1.786-1.72 1.428-2.687 3.72-1.78 6.22.942 2.453 3.666 4.229 6.884 4.229.826 0 1.622-.12 2.361-.336a.722.722 0 01.598.082l1.584.926a.272.272 0 00.14.045c.134 0 .24-.111.24-.247 0-.06-.023-.12-.038-.177l-.327-1.233a.582.582 0 01-.023-.156.49.49 0 01.201-.398C23.024 18.48 24 16.82 24 14.98c0-3.21-2.931-5.837-7.062-6.122zm-2.036 2.891c.535 0 .969.44.969.982a.976.976 0 01-.969.983.976.976 0 01-.969-.983c0-.542.434-.982.97-.982zm4.844 0c.535 0 .969.44.969.982a.976.976 0 01-.969.983.976.976 0 01-.969-.983c0-.542.434-.982.97-.982z"/>
              </svg>
            </a>
            <a href="#" class="social-link" title="Email">
              <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z"/>
              </svg>
            </a>
            <a href="#" class="social-link" title="Phone">
              <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="M6.62 10.79c1.44 2.83 3.76 5.14 6.59 6.59l2.2-2.2c.27-.27.67-.36 1.02-.24 1.12.37 2.33.57 3.57.57.55 0 1 .45 1 1V20c0 .55-.45 1-1 1-9.39 0-17-7.61-17-17 0-.55.45-1 1-1h3.5c.55 0 1 .45 1 1 0 1.25.2 2.45.57 3.57.11.35.03.74-.25 1.02l-2.2 2.2z"/>
              </svg>
            </a>
          </div>
        </div>

        <!-- Links Sections -->
        <div class="footer-links-grid">
          <div class="footer-section">
            <h4 class="footer-heading">{{ t('footer.quickLinks') }}</h4>
            <nav class="footer-nav">
              <router-link to="/" class="footer-link">{{ t('menu.home') }}</router-link>
              <router-link to="/venue" class="footer-link">{{ t('menu.venue') }}</router-link>
              <router-link to="/schedule" class="footer-link">{{ t('menu.schedule') }}</router-link>
              <router-link to="/conference" class="footer-link">{{ t('menu.conference') || '会议信息' }}</router-link>
            </nav>
          </div>

          <div class="footer-section">
            <h4 class="footer-heading">{{ t('footer.forParticipants') }}</h4>
            <nav class="footer-nav">
              <router-link to="/submission" class="footer-link">{{ t('menu.submission') }}</router-link>
              <router-link to="/tickets" class="footer-link">{{ t('menu.tickets') }}</router-link>
              <router-link to="/hotel" class="footer-link">{{ t('menu.hotel') || '酒店预订' }}</router-link>
              <router-link to="/expert" class="footer-link">{{ t('menu.experts') || '特邀专家' }}</router-link>
            </nav>
          </div>

          <div class="footer-section">
            <h4 class="footer-heading">{{ t('footer.contactUs') }}</h4>
            <div class="contact-list">
              <div class="contact-item">
                <svg class="contact-icon" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z"/>
                </svg>
                <span>contact@conference.com</span>
              </div>
              <div class="contact-item">
                <svg class="contact-icon" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M6.62 10.79c1.44 2.83 3.76 5.14 6.59 6.59l2.2-2.2c.27-.27.67-.36 1.02-.24 1.12.37 2.33.57 3.57.57.55 0 1 .45 1 1V20c0 .55-.45 1-1 1-9.39 0-17-7.61-17-17 0-.55.45-1 1-1h3.5c.55 0 1 .45 1 1 0 1.25.2 2.45.57 3.57.11.35.03.74-.25 1.02l-2.2 2.2z"/>
                </svg>
                <span>+86 10 1234 5678</span>
              </div>
              <div class="contact-item">
                <svg class="contact-icon" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
                </svg>
                <span>北京市海淀区</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Footer Bottom -->
      <div class="footer-bottom">
        <p class="copyright">
          © {{ currentYear }} {{ t('app.name') }}. {{ t('footer.rightsReserved') }}
        </p>
        <div class="footer-bottom-links">
          <a href="#">{{ t('footer.privacy') }}</a>
          <a href="#">{{ t('footer.terms') }}</a>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const currentYear = new Date().getFullYear()
</script>

<style scoped lang="scss">
.app-footer {
  background: $gray-900;
  color: $gray-300;
  padding: $spacing-16 0 $spacing-8;
  margin-top: auto;
}

.footer-main {
  display: grid;
  grid-template-columns: 1.2fr 2fr;
  gap: $spacing-16;
  padding-bottom: $spacing-12;
  border-bottom: 1px solid $gray-700;

  @include respond-to(lg) {
    grid-template-columns: 1fr;
    gap: $spacing-10;
  }
}

// Brand Section
.footer-brand {
  .footer-logo {
    display: inline-flex;
    align-items: center;
    gap: $spacing-3;
    text-decoration: none;
    margin-bottom: $spacing-4;

    .logo-icon {
      width: 36px;
      height: 36px;

      svg {
        width: 100%;
        height: 100%;
      }
    }

    span {
      font-size: $font-size-xl;
      font-weight: $font-weight-bold;
      color: white;
    }
  }

  .footer-tagline {
    font-size: $font-size-sm;
    line-height: $line-height-relaxed;
    color: $gray-400;
    margin-bottom: $spacing-6;
    max-width: 280px;
  }
}

.social-links {
  display: flex;
  gap: $spacing-3;

  .social-link {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: $gray-800;
    border-radius: $radius-lg;
    color: $gray-400;
    transition: all $transition-base;

    svg {
      width: 20px;
      height: 20px;
    }

    &:hover {
      background: $primary-500;
      color: white;
      transform: translateY(-2px);
    }
  }
}

// Links Grid
.footer-links-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-8;

  @include respond-to(md) {
    grid-template-columns: repeat(2, 1fr);
  }

  @include respond-to(sm) {
    grid-template-columns: 1fr;
  }
}

.footer-section {
  .footer-heading {
    font-size: $font-size-sm;
    font-weight: $font-weight-semibold;
    color: white;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    margin-bottom: $spacing-5;
  }

  .footer-nav {
    display: flex;
    flex-direction: column;
    gap: $spacing-3;
  }

  .footer-link {
    font-size: $font-size-sm;
    color: $gray-400;
    text-decoration: none;
    transition: color $transition-fast;

    &:hover {
      color: $primary-400;
    }
  }
}

.contact-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-3;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: $spacing-3;
  font-size: $font-size-sm;
  color: $gray-400;

  .contact-icon {
    width: 18px;
    height: 18px;
    flex-shrink: 0;
    color: $gray-500;
  }
}

// Footer Bottom
.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: $spacing-8;

  @include respond-to(sm) {
    flex-direction: column;
    gap: $spacing-4;
    text-align: center;
  }

  .copyright {
    font-size: $font-size-sm;
    color: $gray-500;
  }

  .footer-bottom-links {
    display: flex;
    gap: $spacing-6;

    a {
      font-size: $font-size-sm;
      color: $gray-500;
      text-decoration: none;
      transition: color $transition-fast;

      &:hover {
        color: $primary-400;
      }
    }
  }
}
</style>
