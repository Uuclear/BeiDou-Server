/**
 * 标签页（Tab Bar）相关类型定义
 */

/** 单个标签页的属性 */
export interface TagProps {
  title: string;
  name: string;
  fullPath: string;
  query?: any;
  ignoreCache?: boolean;
}

/** 标签页 Store 状态 */
export interface TabBarState {
  tagList: TagProps[];
  cacheTabList: Set<string>;
}
