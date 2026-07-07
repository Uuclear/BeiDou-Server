/**
 * Loading 状态组合式函数
 * 管理 loading 布尔状态，并在 loading 结束时尝试刷新 JWT Token。
 */
import { ref } from 'vue';
import { refreshToken } from '@/api/user';
import { clearToken, getToken, setToken } from '@/utils/auth';

export default function useLoading(initValue = false) {
  const loading = ref(initValue);
  /**
   * 在 loading 结束时异步刷新 Token
   * 因各 API 未统一封装，只能在 setLoading(false) 时触发
   */
  const handleRefreshToken = async (value: boolean) => {
    if (value) {
      return;
    }

    try {
      if (getToken() == null) {
        return;
      }
      const res = await refreshToken();
      setToken(res.data.token);
    } catch (err) {
      clearToken();
      throw err;
    }
  };
  const setLoading = (value: boolean) => {
    handleRefreshToken(value);
    loading.value = value;
  };
  const toggle = () => {
    loading.value = !loading.value;
  };
  return {
    loading,
    setLoading,
    toggle,
  };
}
