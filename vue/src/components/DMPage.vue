<template>
    <div class="dm-container">
        <el-container>
            <el-aside>
                <el-menu default-active="1">
                    <el-menu-item index="1" @click="pageType = 'r'">
                        <i class="el-icon-download"></i>
                        <span slot="title">收件箱</span>
                    </el-menu-item>
                    <el-menu-item index="2" @click="pageType = 's'">
                        <i class="el-icon-upload2"></i>
                        <span slot="title">发件箱</span>
                    </el-menu-item>
                </el-menu>
            </el-aside>
            <el-main>
                <div class="dm-cards" v-if="pageType === 'r'">
                    <div class="dm-card" v-for="dm of dms" :key="dm.id">
                        <div class="card-header">
                            <div class="user-info" v-if="userInfos[dm.userIdFrom]">
                                <router-link
                                        :to="{name: 'user-profile', params: {userID: userInfos[dm.userIdFrom].screenName}}">
                                    <el-avatar class="user-icon" icon="el-icon-user-solid"
                                               v-if="!userInfos[dm.userIdFrom].iconUrl"></el-avatar>
                                    <el-avatar class="user-icon" :src="baseAPIUrl + userInfos[dm.userIdFrom].iconUrl"
                                               v-if="userInfos[dm.userIdFrom].iconUrl"></el-avatar>
                                    <div class="user-names">
                                        <div class="user-nickname">
                                            {{userInfos[dm.userIdFrom].nickname}}
                                        </div>
                                        <div class="user-screenName">
                                            @{{userInfos[dm.userIdFrom].screenName}}
                                        </div>
                                    </div>
                                </router-link>
                            </div>
                            <div class="user-info" v-if="!userInfos[dm.userIdFrom]">
                                <router-link
                                        :to="{name: 'user-profile', params: {userID: dm.userIdFrom}}">
                                    <el-avatar class="user-icon" icon="el-icon-user-solid" :size="50"></el-avatar>
                                    <div class="user-names">
                                        <div class="user-nickname">
                                            用户加载中：{{dm.userIdFrom}}
                                        </div>
                                        <div class="user-screenName">
                                            @加载中……
                                        </div>
                                    </div>
                                </router-link>
                            </div>
                        </div>
                        <div class="card-main">
                            <p>
                                {{dm.content}}
                            </p>
                        </div>
                        <div class="card-footer">
                            <div class="actions">
                                <div class="have-read have-read-done" v-if="dm.haveRead">
                                    <el-button type="text">
                                        已读
                                    </el-button>
                                </div>
                                <div class="have-read have-read-unread" v-if="!dm.haveRead">
                                    <el-button type="text" @click="setToRead(dm.id)">
                                        设为已读
                                    </el-button>
                                </div>

                                <div class="delete-button">
                                    <el-button type="text" @click="deleteDMAction(dm.id)">
                                        删除
                                    </el-button>
                                </div>
                            </div>

                            <div class="origin-time">
                                {{dm.createdTime | dateformat}}
                            </div>
                        </div>
                        <el-divider></el-divider>
                    </div>
                </div>
                <div class="dm-cards" v-if="pageType === 's'">
                    <div class="dm-card" v-for="dm of dms" :key="dm.id">
                        <div class="card-header">
                            <div class="user-info" v-if="userInfos[dm.userIdTo]">
                                <router-link
                                        :to="{name: 'user-profile', params: {userID: userInfos[dm.userIdTo].screenName}}">
                                    <el-avatar class="user-icon" icon="el-icon-user-solid"
                                               v-if="!userInfos[dm.userIdTo].iconUrl"></el-avatar>
                                    <el-avatar class="user-icon" :src="baseAPIUrl + userInfos[dm.userIdTo].iconUrl"
                                               v-if="userInfos[dm.userIdTo].iconUrl"></el-avatar>
                                    <div class="user-names">
                                        <div class="user-nickname">
                                            {{userInfos[dm.userIdTo].nickname}}
                                        </div>
                                        <div class="user-screenName">
                                            @{{userInfos[dm.userIdTo].screenName}}
                                        </div>
                                    </div>
                                </router-link>
                            </div>
                            <div class="user-info" v-if="!userInfos[dm.userIdTo]">
                                <router-link
                                        :to="{name: 'user-profile', params: {userID: dm.userIdTo}}">
                                    <el-avatar class="user-icon" icon="el-icon-user-solid" :size="50"></el-avatar>
                                    <div class="user-names">
                                        <div class="user-nickname">
                                            用户加载中：{{dm.userIdTo}}
                                        </div>
                                        <div class="user-screenName">
                                            @加载中……
                                        </div>
                                    </div>
                                </router-link>
                            </div>
                        </div>
                        <div class="card-main">
                            <p>
                                {{dm.content}}
                            </p>
                        </div>
                        <div class="card-footer">
                            <div class="actions">
                                <div class="have-read have-read-done" v-if="dm.haveRead">
                                    <el-button type="text">
                                        已读
                                    </el-button>
                                </div>
                                <div class="have-read have-read-unread" v-if="!dm.haveRead">
                                    <el-button type="text">
                                        未读
                                    </el-button>
                                </div>

                                <div class="delete-button">
                                    <el-button type="text" @click="deleteDMAction(dm.id)">
                                        删除
                                    </el-button>
                                </div>
                            </div>

                            <div class="origin-time">
                                {{dm.createdTime | dateformat}}
                            </div>
                        </div>
                        <el-divider></el-divider>
                    </div>
                </div>

                <el-pagination
                        background
                        layout="prev, pager, next"
                        :page-count="totalPages"
                        @current-change="loadDMs">
                </el-pagination>
            </el-main>
        </el-container>
    </div>
</template>

<script>
    import apiConfig from "@/api/apiConfig";
    import {Loading} from 'element-ui';

    export default {
        name: "DMPage",
        data() {
            return {
                pageType: 'r',
                dms: [],
                totalPages: 1,
                userInfos: {},
                baseAPIUrl: apiConfig.base.substr(0, apiConfig.base.length - 1)
            }
        },
        methods: {
            loadDMs(page) {
                let loadingInstance = Loading.service({target: '.dm-cards'});

                this.$axios.get(this.pageType === 'r' ? apiConfig.dmReceives : apiConfig.dmSends,
                    {
                        params: {
                            page: page
                        }
                    })
                    .then(resp => {
                        this.totalPages = resp.data.data.total_pages;
                        this.dms = resp.data.data.dms;

                        this.dms.forEach(dm => {
                            if (this.pageType === 'r') {
                                // 收件箱读取发送人
                                this.getUserInfo(dm.userIdFrom);
                            } else {
                                this.getUserInfo(dm.userIdTo);
                            }
                        });
                    })
                    .catch(error => {
                        console.error(error);
                        this.$notify.error({
                            title: '无法获取私信列表',
                            message: error.msg ? error.msg : '无法连接到服务器',
                            duration: 10000
                        });
                    })
                    .finally(() => loadingInstance.close());
            },
            getUserInfo(userID) {
                if (this.userInfos[userID] !== undefined) {
                    return;
                }
                this.$set(this.userInfos, userID, null);  // 重复检测
                this.$axios.get(apiConfig.userInfo(userID))
                    .then(resp => {
                        this.userInfos[userID] = resp.data.data.info;
                    })
                    .catch(error => {
                        console.error(error);
                        this.$notify.error({
                            title: '错误',
                            message: `无法获取用户信息（id:${userID})`,
                            duration: 3000
                        });
                    })
            },
            setToRead(dmID) {
                let loadingInstance = Loading.service({target: '.dm-cards'});
                this.$axios.post(apiConfig.dmRead, {list: [dmID]})
                    .then(() => this.dms.filter(dm => dm.id === dmID).forEach(dm => dm.haveRead = true))
                    .catch(error => {
                        console.error(error);
                        this.$notify.error({
                            title: '设置错误',
                            message: error.msg ? error.msg : '无法连接到服务器',
                            duration: 10000
                        });
                    })
                    .finally(() => loadingInstance.close());
            },
            deleteDM(dmID) {
                let loadingInstance = Loading.service({target: '.dm-cards'});

                this.$axios.delete(apiConfig.dmDetail(dmID))
                    .then(() => {
                        this.dms = this.dms.filter(dm => dm.id !== dmID);
                        this.$message.info('私信删除成功');
                    })
                    .catch(error => {
                        console.error(error);
                        this.$notify.error({
                            title: '删除错误',
                            message: error.msg ? error.msg : '无法连接到服务器',
                            duration: 10000
                        });
                    })
                    .finally(() => loadingInstance.close());
            },
            deleteDMAction(dmID) {
                this.$alert('删除私信将会同时从双方发件/收件箱中删除，是否确认删除？', '确认删除')
                    .then(() => this.deleteDM(dmID));
            }
        },
        created() {
            this.loadDMs(1);
        },
        watch: {
            pageType: {
                handler(n) {
                    this.pageType = n;
                    this.loadDMs(1);
                },
                immediate: true
            }
        }
    }
</script>

<style scoped>
    .dm-cards {
        text-align: left;
        width: 100%;
    }

    .user-info {
        display: flex;
        justify-content: left;
        padding: 4px;
        max-width: 320px;
    }

    .user-info a {
        text-decoration: none;
        color: inherit;
        display: flex;
    }

    .user-icon {
        margin: 2px 4px;
    }

    .user-names {
        margin: 2px;
        text-align: left;
    }

    .user-nickname {
        font-weight: bold;
        margin: 0 4px;
    }

    .user-screenName {
        font-style: italic;
        color: darkgrey;
        margin: 6px 4px;
    }

    .card-footer {
        display: flex;
        justify-content: left;
        margin: 4px;
    }

    .origin-time {
        padding: 12px 20px;
        display: flex;
        justify-items: right;
        color: darkgrey;
        font-size: 0.9em;
    }

    .card-main {
        margin: 0 16px;
        text-indent: 1em;
        width: 90%;
        word-break: break-word;
        white-space: pre-wrap;
    }

    .actions {
        display: flex;
        justify-content: left;
    }

    .actions div {
        margin: 0 6px;
    }

    .have-read-done .el-button--text {
        color: gray;
    }
</style>
