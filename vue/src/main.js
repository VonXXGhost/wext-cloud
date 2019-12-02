import Vue from 'vue';
import App from './App.vue';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import {router} from "./routes";
import store from './store';
import axios from 'axios';
import moment from "moment";

Vue.config.productionTip = false;
Vue.use(ElementUI);

Vue.filter('dateformat', function (dataStr, pattern = 'YYYY-MM-DD HH:mm:ss') {
    return moment(dataStr).format(pattern)
});

Vue.prototype.$axios = axios;

axios.interceptors.request.use(
    config => {
        if (store.state.auth.token && !config.headers.Authorization) {  // 判断是否存在token，如果存在的话，则每个http header都加上token
            config.headers.Authorization = `Bearer ${store.state.auth.token}`;
        }
        return config;
    },
    err => {
        return Promise.reject(err);
    });

// http response 拦截器
axios.interceptors.response.use(
    response => {
        return response;
    },
    error => {
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    // 返回 401 清除token信息并跳转到登录页面
                    if (!error.request.responseURL.endsWith('auth/manager/login')) {
                        store.commit('auth/logOut');
                        router.replace({
                            path: '/login',
                            query: {redirect: router.currentRoute.fullPath}
                        });
                    } else {
                        store.commit('auth/logOutManager');
                    }
            }
            return Promise.reject(error.response.data);   // 返回接口返回的错误信息
        }
        return Promise.reject(error);
    });

new Vue({
    render: h => h(App),
    router,
    store
}).$mount('#app');
