<!-- 页脚：展示应用标题/版权信息。 -->
<template>
  <a-layout-footer class="footer">{{ $t('title') }}</a-layout-footer>
</template>

<script lang="ts" setup>
  /** 全局页脚组件，仅展示国际化标题文本。 */
</script>

<style lang="less" scoped>
  .footer {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 40px;
    color: var(--color-text-2);
    text-align: center;
  }
</style>
