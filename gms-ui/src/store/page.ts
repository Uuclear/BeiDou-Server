/**
 * 通用分页响应数据结构
 * 对应后端分页接口返回的 records、页码、总数等字段。
 */
export interface PageState {
  records: any;
  pageNumber: number;
  pageSize: number;
  totalPage: number;
  totalRow: number;
}
