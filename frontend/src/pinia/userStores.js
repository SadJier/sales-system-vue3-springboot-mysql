import { defineStore } from 'pinia';

export const useUserStore = defineStore('user',{
    state:() => ({
        userId: null,
        role: null,
        username: null,
        isLoggedIn: false,
    }),
    actions:{
        login(user_data){
            this.userId = user_data.userId;
            this.role = user_data.role;
            this.username = user_data.username;
            this.isLoggedIn = true;

            localStorage.setItem('userInfo', JSON.stringify(user_data));
            if(user_data.token) localStorage.setItem('token', user_data.token);
        },
        logout(){
            this.userId = null;
            this.role = null;
            this.username = null;
            this.isLoggedIn = false;

            localStorage.removeItem('userInfo');
            localStorage.removeItem('token');
        },
        // 从本地存储加载用户信息（用于页面刷新后）
        loadUserFromStorage() {
          const stored_user = localStorage.getItem('userInfo')
          if (stored_user) {
            const user_data = JSON.parse(stored_user);
            this.login(user_data);
          }
        }
    },
});
