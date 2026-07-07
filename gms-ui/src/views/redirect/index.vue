<template>
  <!-- 路由重定向中转页，将 /redirect/:path 解析并 replace 到目标路径。 -->
  <div></div>
</template>

<script lang="ts" setup>
  /**
   * 路由重定向组件，用于刷新页面或带参跳转场景。
   */
  import { useRouter, useRoute } from 'vue-router';

  const router = useRouter();
  const route = useRoute();

  const gotoPath = route.params.path as string;

  router.replace({ path: gotoPath });
</script>

<style scoped lang="less"></style>
