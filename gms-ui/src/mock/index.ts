/**
 * Mock 数据入口
 * 注册各模块 Mock 拦截规则并配置 Mock.js 响应延迟。
 */
import Mock from 'mockjs';

import './user';
import './message-box';

import '@/views/dashboard/workplace/mock';

Mock.setup({
  timeout: '600-1000',
});
