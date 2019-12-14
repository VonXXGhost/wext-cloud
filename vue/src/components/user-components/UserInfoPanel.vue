<template>
    <div class="user-info-panel" v-if="userInfo_c">
        <div class="user-info-header">
            <el-avatar class="user-icon" icon="el-icon-user-solid" v-if="!userInfo_c.info.iconUrl"
                       :size="70"></el-avatar>
            <el-avatar class="user-icon" :src="baseAPIUrl + userInfo_c.info.iconUrl" v-if="userInfo_c.info.iconUrl"
                       :size="70"></el-avatar>
            <div class="user-names">
                <div class="user-nickname">
                    {{userInfo_c.info.nickname}}
                </div>
                <div class="user-screenName">
                    @{{userInfo_c.info.screenName}}
                </div>
            </div>
        </div>

        <div class="user-info-description">
            <p class="user-profile">
                {{userInfo_c.info.profile || '用户暂无简介'}}
            </p>
            <div class="follow-data">
                <span class="following">
                    正在关注:{{userInfo_c.info.followings}}
                </span>
                <span class="follower">
                    追随者:{{userInfo_c.info.followers}}
                 </span>
                <span class="relationship"
                      v-if="userInfo_c.relationship && userInfo_c.relationship !== 'stranger'">
                     -&ensp;{{relationShow[userInfo_c.relationship]}}&ensp;-
                 </span>
            </div>
        </div>

        <div class="user-info-actions" v-if="loginUser && loginUser.id !== userInfo_c.info.id">
            <el-button v-if="userInfo_c.relationship === 'following'" type="warning" @click="doFollow(true)">
                取消关注
            </el-button>
            <el-button v-if="userInfo_c.relationship && userInfo_c.relationship !== 'following'"
                       type="primary" @click="doFollow(false)">
                关注
            </el-button>
            <el-button @click="dmVisible = true">
                私信
            </el-button>

            <el-dialog title="发送私信" :visible.sync="dmVisible" class="dm-dialog">
                <el-form>
                    <el-form-item label="私信内容：" label-width="90px">
                        <el-input v-model="dmContent" maxlength="1000"
                                  show-word-limit :autosize="{minRows: 3}"
                                  type="textarea"></el-input>
                    </el-form-item>
                </el-form>
                <div slot="footer" class="dialog-footer">
                    <el-button @click="dmVisible = false">取 消</el-button>
                    <el-button type="primary" @click="sendDM">发 送</el-button>
                </div>
            </el-dialog>
        </div>
        <div class="user-info-actions" v-if="loginUser && loginUser.id === userInfo_c.info.id">
            <router-link to="/settings">
                <el-button>
                    修改个人设置
                </el-button>
            </router-link>
        </div>
    </div>
</template>

<script>
    import {Loading} from 'element-ui';
    import apiConfig from "@/api/apiConfig";

    export default {
        name: "UserInfoPanel",
        props: ['userInfo'],
        computed: {
            loginUser() {
                return this.$store.state.auth.loginUser
            }
        },
        data() {
            return {
                userInfo_c: this.userInfo,
                relationShow: {
                    following: '正在关注',
                    follower: '在关注你',
                    friend: '互相关注'
                },
                baseAPIUrl: apiConfig.base.substr(0, apiConfig.base.length - 1),
                dmVisible: false,
                dmContent: ''
            }
        },
        methods: {
            doFollow(isFollowing) {
                let loading = Loading.service({target: '.user-info-actions'});
                if (isFollowing) {
                    // 取关操作
                    this.$axios.delete(apiConfig.userFollow(this.userInfo_c.info.id))
                        .then(() => {
                            this.userInfo_c.info.followers--;
                            if (this.userInfo_c.relationship === 'friend') {
                                this.userInfo_c.relationship = 'follower';
                            } else {
                                this.userInfo_c.relationship = 'stranger';
                            }
                        })
                        .catch(error => {
                            this.showUploadController();
                            this.$notify.error({
                                title: '错误',
                                message: '取消关注失败：' +
                                    (error.msg ? error.msg : '无法连接到服务器'),
                                duration: 10000
                            })
                        })
                        .finally(() => loading.close());
                } else {
                    // 关注操作
                    this.$axios.put(apiConfig.userFollow(this.userInfo_c.info.id))
                        .then(() => {
                            this.userInfo_c.info.followers++;
                            if (this.userInfo_c.relationship === 'follower') {
                                this.userInfo_c.relationship = 'friend';
                            } else {
                                this.userInfo_c.relationship = 'following';
                            }
                        })
                        .catch(error => {
                            this.showUploadController();
                            this.$notify.error({
                                title: '错误',
                                message: '关注失败：' +
                                    (error.msg ? error.msg : '无法连接到服务器'),
                                duration: 10000
                            })
                        })
                        .finally(() => loading.close());
                }
            },
            sendDM() {
                let loading = Loading.service({target: '.dm-dialog'});

                this.$axios.put(apiConfig.dmNewPost, {
                    id: this.userInfo_c.info.id,
                    content: this.dmContent
                })
                    .then(() => {
                        this.$message.info('发送成功');
                        this.dmVisible = false;
                    })
                    .catch(error => {
                        console.error(error);
                        this.$notify.error({
                            title: '发送失败',
                            message: error.msg ? error.msg : '无法连接到服务器',
                            duration: 10000
                        });
                    })
                    .finally(() => loading.close());
            }
        },
        watch: {
            userInfo: {
                handler(newV) {
                    this.userInfo_c = newV;
                },
                immediate: true
            }
        }
    }
</script>

<style scoped>
    .user-info-panel {
        border: #fffadd solid 4px;
        margin: 16px 32px;
        padding: 16px;
    }

    .user-icon {
        margin: 4px 8px;
    }

    .user-names {
        display: flex;
        justify-content: center;
        margin: 4px;
    }

    .user-nickname {
        font-weight: bold;
        margin: 0 8px;
    }

    .user-screenName {
        font-style: italic;
        color: darkgrey;
        margin: 0 8px;
    }

    .follow-data {
        padding: 4px;
        margin: 0 0 6px;
    }

    .follow-data > span {
        margin: 4px 8px;
    }

    .relationship {
        color: mediumseagreen;
    }
</style>
