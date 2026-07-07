<!--
  页面内容布局：根据路由 meta.ignoreCache 决定是否使用 keep-alive 缓存子页面。
-->
<template>
  <router-view v-slot="{ Component, route }">
    <transition name="fade" mode="out-in" appear>
      <component
        :is="Component"
        v-if="route.meta.ignoreCache"
        :key="route.fullPath"
      />
      <keep-alive v-else :include="cacheList">
        <component :is="Component" :key="route.fullPath" />
      </keep-alive>
    </transition>
  </router-view>
</template>

<script lang="ts" setup>
  /**
   * 路由出口布局
   * 与 TabBar store 联动，对未标记 ignoreCache 的页面进行 keep-alive 缓存。
   */
  import { computed } from 'vue';
  import { useTabBarStore } from '@/store';

  const tabBarStore = useTabBarStore();

  /** 当前需要 keep-alive 包含的路由组件名列表 */
  const cacheList = computed(() => tabBarStore.getCacheList);
</script>

<style scoped lang="less"></style>
