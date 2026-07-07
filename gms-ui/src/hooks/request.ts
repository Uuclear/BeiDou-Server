/**
 * 数据请求组合式函数
 * 封装 API 调用、loading 状态与响应数据的 ref 绑定。
 *
 * 注意：不要使用 async 函数作为 api 参数，应使用 bind 绑定参数。
 * 示例：useRequest(api.bind(null, {}))
 */
import { ref, UnwrapRef } from 'vue';
import { AxiosResponse } from 'axios';
import { HttpResponse } from '@/api/interceptor';
import useLoading from './loading';

export default function useRequest<T>(
  api: () => Promise<AxiosResponse<HttpResponse>>,
  defaultValue = [] as unknown as T,
  isLoading = true
) {
  const { loading, setLoading } = useLoading(isLoading);
  const response = ref<T>(defaultValue);
  api()
    .then((res) => {
      response.value = res.data as unknown as UnwrapRef<T>;
    })
    .finally(() => {
      setLoading(false);
    });
  return { loading, response };
}
