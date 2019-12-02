<template>
    <div id="home-navigation">
        <el-header>
            <el-row :gutter="20">
                <el-col :span="3">
                    <router-link to="/">
                        <div class="titleLogo">Wext</div>
                    </router-link>
                </el-col>
                <el-col :span="10">
                    <el-menu class="top_menu" mode="horizontal">
                        <el-menu-item v-for="item of menuItems" :key="item.router">
                            <router-link :to="item.router">
                                {{item.name}}
                            </router-link>
                        </el-menu-item>
                    </el-menu>
                </el-col>
                <el-col :span="6" :offset="5" class="right_kit" v-if="loginUser">
                    <router-link :to="{name:'user-profile', params:{userID:loginUser.id}}">
                        <span class="login_user">
                            <el-avatar class="user-icon" icon="el-icon-user-solid" v-if="!loginUser.iconUrl"></el-avatar>
                            <el-avatar class="user-icon" :src="baseAPIUrl + loginUser.iconUrl" v-if="loginUser.iconUrl"></el-avatar>
                            <span class="user-screen-name">
                                {{loginUser.screenName}}
                            </span>
                        </span>
                    </router-link>
                    <el-button @click="logOut">
                        退出登录
                    </el-button>

                </el-col>
                <el-col :span="6" :offset="5" class="right_kit" v-if="!loginUser">
                    <router-link to="/login">
                        <el-button>
                            登录
                        </el-button>
                    </router-link>
                    <router-link to="/signUp">
                        <el-button type="primary">
                            注册
                        </el-button>
                    </router-link>
                </el-col>
            </el-row>
        </el-header>
    </div>
</template>

<script>
    import apiConfig from "@/api/apiConfig";

    export default {
        name: "HomeNavigation",
        computed: {
            loginUser() {
                return this.$store.state.auth.loginUser
            }
        },
        data () {
            return {
                menuItems: [
                    {
                        name: '话题节点',
                        router: '/path/'
                    },
                    {
                        name: '个人关注',
                        router: '/home'
                    },
                    {
                        name: '私信信息',
                        router: '/dm'
                    },
                    {
                        name: '关于本站',
                        router: '/about'
                    },
                ],
                baseAPIUrl: apiConfig.base.substr(0, apiConfig.base.length - 1)
            }
        },
        methods: {
            logOut() {
                this.$store.commit('auth/logOut');
            }
        }
    }
</script>

<style scoped>
    a {
        text-decoration-line: none;
    }

    .titleLogo {
        color: deepskyblue;
        font-weight: bold;
        font-size: 2.8em;
        margin-block-start: 0.2em;
        margin-block-end: 0.2em;
    }

    .right_kit {
        display: flex;
        justify-content: flex-end;
        text-align: right;
        margin-block-start: 0.65em;
        margin-block-end: 0.65em;
    }

    .right_kit > a {
        margin: 0 2px;
        display: flex;
    }

    .login_user {
        margin-right: 8px;
        display: flex;
        align-items: center;
    }

    .user-icon {
        margin: 4px 8px;
    }

    .user-screen-name {
        font-style: italic;
        color: #858585;
        margin: auto 0.5em;
    }
</style>
