/**
 * 用户操作组合式函数
 * 封装登出流程：调用 store 登出、提示成功并跳转登录页。
 */
import { useRouter } from 'vue-router';
import { Message } from '@arco-design/web-vue';

import { useUserStore } from '@/store';
import { useI18n } from 'vue-i18n';

export default function useUser() {
  const router = useRouter();
  const userStore = useUserStore();
  const { t } = useI18n();

  /** 登出并跳转到登录页，保留 redirect 查询参数 */
  const logout = async (logoutTo?: string) => {
    await userStore.logout();
    const currentRoute = router.currentRoute.value;
    Message.success(t('message.logout.success'));
    router.push({
      name: logoutTo && typeof logoutTo === 'string' ? logoutTo : 'login',
      query: {
        ...router.currentRoute.value.query,
        redirect: currentRoute.name as string,
      },
    });
  };
  return {
    logout,
  };
}
