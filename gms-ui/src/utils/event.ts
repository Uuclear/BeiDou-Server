/**
 * DOM 事件监听工具
 * 封装 addEventListener / removeEventListener，兼容不支持的环境。
 */

/** 安全地为目标元素添加事件监听 */
export function addEventListen(
  target: Window | HTMLElement,
  event: string,
  handler: EventListenerOrEventListenerObject,
  capture = false
) {
  if (
    target.addEventListener &&
    typeof target.addEventListener === 'function'
  ) {
    target.addEventListener(event, handler, capture);
  }
}

/** 安全地移除目标元素的事件监听 */
export function removeEventListen(
  target: Window | HTMLElement,
  event: string,
  handler: EventListenerOrEventListenerObject,
  capture = false
) {
  if (
    target.removeEventListener &&
    typeof target.removeEventListener === 'function'
  ) {
    target.removeEventListener(event, handler, capture);
  }
}
