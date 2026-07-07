/**
 * MapleStory 外部资源 API 工具
 * 通过 maplestory.io 获取游戏物品图标等资源 URL。
 */

/**
 * 获取游戏物品/装备图标 URL
 * @param category 分类： item 或其他
 * @param id 物品id
 * @param location 地区：GMS（默认值）
 * @param version 版本：83（默认值）
 */
export function getIconUrl(
  category: string,
  id: string | number,
  location = 'GMS',
  version = '83'
): string {
  if (!id || id <= 0) return '';
  return `https://maplestory.io/api/${location}/${version}/${category}/${id}/icon`;
}

/** 占位函数，用于满足类型或模板引用要求 */
export function nothing() {
  return '占位用';
}
