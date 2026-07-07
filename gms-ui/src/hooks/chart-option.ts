/**
 * ECharts 图表配置组合式函数
 * 根据当前明暗主题动态计算 chartOption，便于在 option 工厂函数中使用 isDark。
 */
import { computed } from 'vue';
import { EChartsOption } from 'echarts';
import { useAppStore } from '@/store';

// for code hints
// import { SeriesOption } from 'echarts';
// Because there are so many configuration items, this provides a relatively convenient code hint.
// When using vue, pay attention to the reactive issues. It is necessary to ensure that corresponding functions can be triggered, TypeScript does not report errors, and code writing is convenient.
/** 接收 isDark 并返回 EChartsOption 的配置工厂函数类型 */
interface optionsFn {
  (isDark: boolean): EChartsOption;
}

export default function useChartOption(sourceOption: optionsFn) {
  const appStore = useAppStore();
  const isDark = computed(() => {
    return appStore.theme === 'dark';
  });
  // echarts support https://echarts.apache.org/zh/theme-builder.html
  // It's not used here
  // TODO echarts themes
  /** 随主题变化的图表配置 computed */
  const chartOption = computed<EChartsOption>(() => {
    return sourceOption(isDark.value);
  });
  return {
    chartOption,
  };
}
