<!-- 全局设置表单项：根据 type 渲染开关或数字输入框。 -->
<template>
  <a-input-number
    v-if="type === 'number'"
    :style="{ width: '80px' }"
    size="small"
    :default-value="(defaultValue as number)"
    @change="handleChange"
  />
  <a-switch
    v-else
    :default-checked="(defaultValue as boolean)"
    size="small"
    @change="handleChange"
  />
</template>

<script lang="ts" setup>
  /**
   * 设置面板表单项布局包装组件。
   */
  const props = defineProps({
    type: {
      type: String,
      default: '',
    },
    name: {
      type: String,
      default: '',
    },
    defaultValue: {
      type: [String, Boolean, Number],
      default: '',
    },
  });
  const emit = defineEmits(['inputChange']);
  /** 将控件值与配置项 key 一并向上抛出 */
  const handleChange = (value: unknown) => {
    emit('inputChange', {
      value,
      key: props.name,
    });
  };
</script>
