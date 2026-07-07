/**
 * 标签页（Tab Bar）状态 Store
 * 管理多页签导航的标签列表与 keep-alive 缓存列表。
 */
import type { RouteLocationNormalized } from 'vue-router';
import { defineStore } from 'pinia';
import {
  DEFAULT_ROUTE,
  DEFAULT_ROUTE_NAME,
  REDIRECT_ROUTE_NAME,
} from '@/router/constants';
import { isString } from '@/utils/is';
import { TabBarState, TagProps } from './types';

/** 将路由对象格式化为标签页属性 */
const formatTag = (route: RouteLocationNormalized): TagProps => {
  const { name, meta, fullPath, query } = route;
  return {
    title: meta.locale || '',
    name: String(name),
    fullPath,
    query,
    ignoreCache: meta.ignoreCache,
  };
};

/** 不加入标签页的路由名称黑名单 */
const BAN_LIST = [REDIRECT_ROUTE_NAME];

const useAppStore = defineStore('tabBar', {
  state: (): TabBarState => ({
    cacheTabList: new Set([DEFAULT_ROUTE_NAME]),
    tagList: [DEFAULT_ROUTE],
  }),

  getters: {
    /** 当前打开的标签页列表 */
    getTabList(): TagProps[] {
      return this.tagList;
    },
    /** 需要 keep-alive 缓存的路由名称列表 */
    getCacheList(): string[] {
      return Array.from(this.cacheTabList);
    },
  },

  actions: {
    /** 路由切换时新增标签页，并按需加入缓存列表 */
    updateTabList(route: RouteLocationNormalized) {
      if (BAN_LIST.includes(route.name as string)) return;
      this.tagList.push(formatTag(route));
      if (!route.meta.ignoreCache) {
        this.cacheTabList.add(route.name as string);
      }
    },
    /** 关闭指定标签页并从缓存中移除 */
    deleteTag(idx: number, tag: TagProps) {
      this.tagList.splice(idx, 1);
      this.cacheTabList.delete(tag.name);
    },
    /** 手动将路由名称加入缓存列表 */
    addCache(name: string) {
      if (isString(name) && name !== '') this.cacheTabList.add(name);
    },
    /** 从缓存列表中移除指定标签对应的路由 */
    deleteCache(tag: TagProps) {
      this.cacheTabList.delete(tag.name);
    },
    /** 用新的标签列表替换当前列表，并重建缓存集合 */
    freshTabList(tags: TagProps[]) {
      this.tagList = tags;
      this.cacheTabList.clear();
      // 要先判断ignoreCache
      this.tagList
        .filter((el) => !el.ignoreCache)
        .map((el) => el.name)
        .forEach((x) => this.cacheTabList.add(x));
    },
    /** 重置为仅保留默认工作台标签 */
    resetTabList() {
      this.tagList = [DEFAULT_ROUTE];
      this.cacheTabList.clear();
      this.cacheTabList.add(DEFAULT_ROUTE_NAME);
    },
  },
});

export default useAppStore;
