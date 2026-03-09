<template>
  <canvas ref="canvasRef" :style="{ width: size + 'px', height: size + 'px' }" />
</template>

<script setup lang="ts">
import { ref, watchEffect } from 'vue'
import QRCode from 'qrcode'

const props = defineProps<{
  text: string
  size?: number
}>()

const canvasRef = ref<HTMLCanvasElement>()

watchEffect(() => {
  if (canvasRef.value && props.text) {
    QRCode.toCanvas(canvasRef.value, props.text, {
      width: props.size ?? 160,
      margin: 2
    })
  }
})
</script>
