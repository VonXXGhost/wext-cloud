import VueRouter from "vue-router";
import Vue from 'vue';
import HomePage from "@/components/HomePage";
import Login from "@/components/auth-components/Login";
import store from '@/store';
import SignUp from "@/components/auth-components/SignUp";
import PathPage from "@/components/path-components/PathPage";
import UserProfilePage from "@/components/user-components/UserProfilePage";
import WextDetailPage from "@/components/wext-components/WextDetailPage";
import HomeTimelinePage from "@/components/user-components/HomeTimelinePage";
import AllPaths from "@/components/path-components/AllPaths";
import PathRequestUser from "@/components/path-components/PathRequestUser";
import PathRequestManage from "@/components/path-components/PathRequestManage";
import DMPage from "@/components/DMPage";
import UserSettings from "@/components/user-components/UserSettings";

Vue.use(VueRouter);

const routes = [
    {
        path: '/',
        name: 'home-page',
        component: HomePage,
    },
    {
        path: '/login',
        name: 'login',
        component: Login,
    },
    {
        path: '/signUp',
        name: 'signUp',
        component: SignUp,
    },
    {
        path: '/path:fullPath(/.*)',
        name: 'path',
        component: PathPage,
        props: true,
    },
    {
        path: '/path',
        redirect: '/path/'
    },
    {
        path: '/all-path:fullPath(/.*)',
        name: 'all-path',
        component: AllPaths,
        props: true,
    },
    {
        path: '/all-path',
        redirect: '/all-path/'
    },
    {
        path: '/user/:userID',
        name: 'user-profile',
        component: UserProfilePage,
        props: true,
    },
    {
        path: '/wext/:wextID',
        name: 'wext-page',
        component: WextDetailPage,
        props: true,
    },
    {
        path: '/home',
        name: 'home-timeline',
        component: HomeTimelinePage,
        meta: {
            needAuth: true
        }
    },
    {
        path: '/path_request',
        name: 'path-request-user-view',
        component: PathRequestUser,
        meta: {
            needAuth: true
        }
    },
    {
        path: '/path_request/manage',
        name: 'path-request-manage',
        component: PathRequestManage,
    },
    {
        path: '/dm',
        name: 'direct-message',
        component: DMPage,
        meta: {
            needAuth: true
        }
    },
    {
        path: '/settings',
        name: 'user-settings',
        component: UserSettings,
        meta: {
            needAuth: true
        }
    }
];

const _router = new VueRouter({routes});

_router.beforeEach((to, from, next) => {
    //matched的数组中包含$route对象的检查元字段
    //arr.some() 表示判断该数组是否有元素符合相应的条件, 返回布尔值
    if (to.matched.some(record => record.meta.needAuth)) {
        if (!store.state.auth.token) {
            next({
                path: '/login',
                query: { redirect: to.fullPath }    // 登陆后重定向访问路径
            })
        } else {
            next()
        }
        next()
    } else {
        next()
    }
});

export const router = _router;
