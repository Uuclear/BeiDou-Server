/**
 * 游戏配置管理 API
 * 提供配置类型、配置项的增删改查及 YAML 导入导出接口。
 */
import axios from 'axios';
import { RequestOption } from '@arco-design/web-vue';

/** 配置项搜索条件 */
export interface ConfigSearch {
  type: string;
  subType: string;
  filter: string;
  pageNo: number;
  pageSize: number;
}

/** 配置项数据结构 */
export interface ConfigResult {
  id: number;
  configType: string;
  configSubType: string;
  configClazz: string;
  configCode: string;
  configValue: string;
  configDesc: string;
}

/** 获取所有配置类型列表 */
export function getConfigTypeList() {
  return axios.get('/config/v1/getConfigTypeList');
}

/** 分页查询配置项列表 */
export function getConfigList(data: ConfigSearch) {
  return axios.post('/config/v1/getConfigList', data);
}

/** 新增配置项 */
export function addConfig(data: ConfigResult) {
  return axios.post('/config/v1/addConfig', data);
}

/** 更新配置项 */
export function updateConfig(data: ConfigResult) {
  return axios.post('/config/v1/updateConfig', data);
}

/** 删除单个配置项 */
export function deleteConfig(id: number) {
  return axios.delete(`/config/v1/deleteConfig/${id}`);
}

/** 批量删除配置项 */
export function deleteConfigList(ids: number[]) {
  return axios.post(`/config/v1/deleteConfigList`, ids);
}

/** 上传 YAML 配置文件（multipart/form-data） */
export function importYml(option: RequestOption) {
  const formData = new FormData();
  formData.append('file', option.fileItem.file as Blob);
  return axios
    .post(option.action as string, formData, {
      headers: { 'Content-type': 'multipart/form-data' },
    })
    .then((response) => {
      option.onSuccess(response);
    });
}

/** 导出配置为 YAML 文件（Blob 下载） */
export function exportYml() {
  return axios.get(`/config/v1/exportYml`, {
    responseType: 'blob',
  });
}
