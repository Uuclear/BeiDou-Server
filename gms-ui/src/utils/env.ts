/**
 * 环境变量工具
 * 非 production 模式视为 debug 开发环境。
 */
const debug = import.meta.env.MODE !== 'production';

export default debug;
