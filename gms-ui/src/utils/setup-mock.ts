/**
 * Mock 数据工具
 * 在开发环境下按条件启用 Mock，并提供统一的成功/失败响应包装。
 */
import debug from './env';

/**
 * 按 debug 开关决定是否执行 Mock 注册逻辑
 * @param mock 是否启用 Mock
 * @param setup Mock 注册函数
 */
export default ({ mock, setup }: { mock?: boolean; setup: () => void }) => {
  if (mock && debug) setup();
};

/** 构造 Mock 成功响应体（code: 20000） */
export const successResponseWrap = (data: unknown) => {
  return {
    data,
    status: 'ok',
    msg: '请求成功',
    code: 20000,
  };
};

/** 构造 Mock 失败响应体 */
export const failResponseWrap = (data: unknown, msg: string, code = 50000) => {
  return {
    data,
    status: 'fail',
    msg,
    code,
  };
};
