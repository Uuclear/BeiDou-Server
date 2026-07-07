<template>
  <!-- 面包屑导航，根据当前路由 matched 记录展示层级路径。 -->
  <a-breadcrumb class="container-breadcrumb">
    <a-breadcrumb-item>
      <icon-apps />
    </a-breadcrumb-item>
    <a-breadcrumb-item
      v-for="item in router.currentRoute.value.matched"
      :key="item.name"
    >
      <a-link
        v-if="
          item.name !== router.currentRoute.value.name &&
          item.children.length === 0
        "
        @click="$router.push({ name: item.name })"
      >
        {{ $t(item.meta.locale || '') }}
      </a-link>
      <span v-else>
        {{ $t(item.meta.locale || '') }}
      </span>
    </a-breadcrumb-item>
  </a-breadcrumb>
</template>

<script lang="ts" setup>
  /**
   * 面包屑组件，将路由 meta.locale 转为可点击的导航链路。
   */
  // @ts-check
  import { PropType } from 'vue';
  import router from '@/router';

  defineProps({
    items: {
      type: Array as PropType<string[]>,
      default() {
        return [];
      },
    },
  });
</script>

<style scoped lang="less">
  .container-breadcrumb {
    margin: 16px 0;
    :deep(.arco-breadcrumb-item) {
      color: rgb(var(--gray-6));
      &:last-child {
        color: rgb(var(--gray-8));
      }
    }
  }
</style>
