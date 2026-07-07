/**
 * 全局通用 TypeScript 类型定义
 */
export interface AnyObject {
  [key: string]: unknown;
}

export interface Options {
  value: unknown;
  label: string;
}

export interface NodeOptions extends Options {
  children?: NodeOptions[];
}

export interface GetParams {
  body: null;
  type: string;
  url: string;
}

export interface PostData {
  body: string;
  type: string;
  url: string;
}

/** 分页组件通用参数 */
export interface Pagination {
  current: number;
  pageSize: number;
  total?: number;
}

export type TimeRanger = [string, string];

/** 通用图表数据结构 */
export interface GeneralChart {
  xAxis: string[];
  data: Array<{ name: string; value: number[] }>;
}
