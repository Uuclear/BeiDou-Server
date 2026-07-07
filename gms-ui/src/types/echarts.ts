/**
 * ECharts 图表相关类型扩展
 */
import { CallbackDataParams } from 'echarts/types/dist/shared';

/** 扩展 ECharts tooltip formatter 回调参数，补充坐标轴维度信息 */
export interface ToolTipFormatterParams extends CallbackDataParams {
  axisDim: string;
  axisIndex: number;
  axisType: string;
  axisId: string;
  axisValue: string;
  axisValueLabel: string;
}
