<template>
  <router-view />
</template>

<style lang="scss">
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html {
  scroll-behavior: smooth;
}

body {
  font-family: $font-family-base;
  color: $text-primary;
  background: $bg-primary;
  line-height: $line-height-normal;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 $spacing-6;
}

a {
  text-decoration: none;
  color: inherit;
}
</style>
