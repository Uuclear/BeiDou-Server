/**
 * 用户状态 Store
 * 管理当前登录用户的账户信息、角色权限及登录/登出流程。
 */
import { defineStore } from 'pinia';
import {
  login as userLogin,
  logout as userLogout,
  getUserInfo,
  LoginData,
} from '@/api/user';
import { setToken, clearToken } from '@/utils/auth';
import { removeRouteListener } from '@/utils/route-listener';
import { UserState } from './types';
import useAppStore from '../app';

const useUserStore = defineStore('user', {
  state: (): UserState => ({
    id: undefined,
    name: undefined,
    pin: undefined,
    pic: undefined,
    loggedin: undefined,
    lastlogin: undefined,
    createdat: undefined,
    birthday: undefined,
    banned: undefined,
    banreason: undefined,
    macs: undefined,
    nxCredit: undefined,
    maplePoint: undefined,
    nxPrepaid: undefined,
    characterslots: undefined,
    gender: undefined,
    tempban: undefined,
    greason: undefined,
    tos: undefined,
    sitelogged: undefined,
    webadmin: undefined,
    nick: undefined,
    mute: undefined,
    email: undefined,
    ip: undefined,
    rewardpoints: undefined,
    votepoints: undefined,
    hwid: undefined,
    language: undefined,
    role: '',
    avatar: undefined,
  }),

  getters: {
    /** 返回当前用户信息的浅拷贝 */
    userInfo(state: UserState): UserState {
      return { ...state };
    },
  },

  actions: {
    /** 切换用户角色（调试用） */
    switchRoles() {
      return new Promise((resolve) => {
        this.role = this.role === 'user' ? 'admin' : 'user';
        resolve(this.role);
      });
    },
    /** 设置用户信息，并根据 webadmin 字段推导 role */
    setInfo(partial: Partial<UserState>) {
      partial.role = partial.webadmin ? 'admin' : 'user';
      this.$patch(partial);
    },

    /** 重置用户信息到初始状态 */
    resetInfo() {
      this.$reset();
    },

    /** 从服务端拉取并更新当前用户信息 */
    async info() {
      const res = await getUserInfo();

      this.setInfo(res.data);
    },

    /** 用户登录：调用登录 API 并持久化 Token */
    async login(loginForm: LoginData) {
      try {
        const res = await userLogin(loginForm);
        setToken(res.data.token);
      } catch (err) {
        clearToken();
        throw err;
      }
    },
    /** 登出后的本地清理：重置用户信息、清除 Token、移除路由监听、清空服务端菜单 */
    logoutCallBack() {
      const appStore = useAppStore();
      this.resetInfo();
      clearToken();
      removeRouteListener();
      appStore.clearServerMenu();
    },
    /** 用户登出：调用登出 API 后执行本地清理 */
    async logout() {
      try {
        await userLogout();
      } finally {
        this.logoutCallBack();
      }
    },
  },
});

export default useUserStore;
