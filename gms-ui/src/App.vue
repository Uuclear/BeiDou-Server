<!--
  根组件：为整个应用提供 Arco Design 国际化配置，
  渲染路由视图，并挂载全局设置面板（主题、布局等）。
-->
<template>
  <a-config-provider :locale="locale">
    <router-view />
    <global-setting />
  </a-config-provider>
</template>

<script lang="ts" setup>
  /**
   * 应用根组件
   * 根据当前 i18n 语言切换 Arco Design 组件库的中英文 locale。
   */
  import { computed } from 'vue';
  import enUS from '@arco-design/web-vue/es/locale/lang/en-us';
  import zhCN from '@arco-design/web-vue/es/locale/lang/zh-cn';
  import GlobalSetting from '@/components/global-setting/index.vue';
  import useLocale from '@/hooks/locale';

  const { currentLocale } = useLocale();
  /** 根据当前语言返回对应的 Arco Design locale 对象 */
  const locale = computed(() => {
    switch (currentLocale.value) {
      case 'zh-CN':
        return zhCN;
      case 'en-US':
        return enUS;
      default:
        return enUS;
    }
  });
</script>

<style lang="less">
  .container {
    padding: 0 20px 20px 20px;
  }

  :deep(.arco-table-th) {
    &:last-child {
      .arco-table-th-item-title {
        margin-left: 16px;
      }
    }
  }

  .arco-row {
    margin-bottom: 10px;
  }
</style>
