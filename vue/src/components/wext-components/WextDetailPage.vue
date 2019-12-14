<template>
    <div class="wext-page">
        <div class="wext-detail" v-if="wextDetail">
            <div class="user-info">
                <router-link :to="{name: 'user-profile', params: {userID: wextDetail.user.screenName}}">
                    <el-avatar class="user-icon" icon="el-icon-user-solid" :size="50"
                               v-if="!wextDetail.user.iconUrl"></el-avatar>
                    <el-avatar class="user-icon" :src="baseAPIUrl + wextDetail.user.iconUrl" :size="50"
                               v-if="wextDetail.user.iconUrl"></el-avatar>
                    <div class="user-names">
                        <div class="user-nickname">
                            {{wextDetail.user.nickname}}
                        </div>
                        <div class="user-screenName">
                            @{{wextDetail.user.screenName}}
                        </div>
                    </div>
                </router-link>
            </div>
            <div class="content-path">
                <router-link :to="'/path' + wextDetail.wext.fullPath">
                    >>>&ensp;{{wextDetail.wext.fullPath}}
                </router-link>
            </div>

            <p class="wext-content">
                {{wextDetail.wext.text}}
            </p>
            <div class="content-pics" v-if="wextDetail.wext.picList && wextDetail.wext.picList.length > 0">
                <el-image :src="baseAPIUrl + wextDetail.wext.picList[0]"
                          :preview-src-list="wextDetail.wext.picList.map(img => baseAPIUrl + img)"
                          fit="contain">
                </el-image>
            </div>
            <div class="origin-time">
                <p>
                    {{wextDetail.wext.createdTime | dateformat}}
                </p>
            </div>

            <div class="wext-actions">
                <el-tooltip placement="top" content="双击以转发或取消转发">
                    <el-button round icon="el-icon-share" :class="{'has-do': hasRepost}"
                               @dblclick.native="repostButtonAct" @click="switchToReposts">
                        转发&ensp;{{wextDetail.wext.repostCount}}
                    </el-button>
                </el-tooltip>
                <el-button round icon="el-icon-s-comment" @click="switchToComments()">
                    评论&ensp;{{wextDetail.wext.commentCount}}
                </el-button>
                <el-tooltip placement="top" content="双击以点赞或取消点赞">
                    <el-button round icon="el-icon-star-off" :class="{'has-do': hasLike}"
                               @click="switchToLikes()" @dblclick.native="likeButtonAct">
                        点赞&ensp;{{wextDetail.wext.likeCount}}
                    </el-button>
                </el-tooltip>

                <el-button class="delete-wext" v-if="loginUser.id === wextDetail.wext.userId"
                           type="danger" @click="deleteWext">
                    删除
                </el-button>
            </div>
        </div>
        <div class="wext-detail" v-if="!wextDetail">
            wext加载中
        </div>

        <div class="repost_container" v-if="viewType === 'repost'">
            <div class="repost-list" v-if="repostsResp && repostsResp.reposts.length > 0">
                <div class="repost-card" v-for="(repost, index) of repostsResp.reposts" :key="index">
                    <div class="user-info repost-user-info" v-if="userInfos[repost.userId]">
                        <router-link :to="{name: 'user-profile', params: {userID: repost.userId}}">
                            <el-avatar class="user-icon" icon="el-icon-user-solid" :size="50"
                                       v-if="!userInfos[repost.userId].iconUrl"></el-avatar>
                            <el-avatar class="user-icon" :src="baseAPIUrl + userInfos[repost.userId].iconUrl" :size="50"
                                       v-if="userInfos[repost.userId].iconUrl"></el-avatar>
                            <div class="user-names">
                                <div class="user-nickname">
                                    {{userInfos[repost.userId].nickname}}
                                </div>
                                <div class="user-screenName">
                                    @{{userInfos[repost.userId].screenName}}
                                </div>
                            </div>
                        </router-link>

                    </div>
                    <div class="user-info repost-user-info" v-if="!userInfos[repost.userId]">
                        <el-avatar class="user-icon" icon="el-icon-user-solid" :size="50"></el-avatar>
                        <div class="user-names">
                            <div class="user-nickname">
                                用户加载中：{{repost.userId}}
                            </div>
                            <div class="user-screenName">
                                @加载中……
                            </div>
                        </div>
                    </div>
                </div>

                <el-pagination
                        background
                        layout="prev, pager, next"
                        :page-count="repostsResp.total_pages"
                        @current-change="loadReposts">
                </el-pagination>

            </div>
            <div class="repost-list" v-else>
                暂无人转发
            </div>
        </div>

        <div class="comment_container" v-if="viewType === 'comment'">
            <div class="comment-write-container">
                <el-input type="textarea" placeholder="发表你的评论"
                          v-model="newComment" minlength="1" maxlength="300" show-word-limit
                          :autosize="{minRows: 3}" class="comment-textarea"></el-input>
                <el-button @click="postComment">
                    发送评论
                </el-button>
            </div>
            <div class="comment-list" v-if="commentsResp && commentsResp.comments.length > 0">
                <div class="comment-card" v-for="comment of commentsResp.comments" :key="comment.id">
                    <div class="comment-card-header">
                        <div class="comment-user-info user-info" v-if="userInfos[comment.userId]">
                            <router-link :to="{name: 'user-profile', params: {userID: comment.userId}}">
                                <el-avatar class="user-icon" icon="el-icon-user-solid" :size="50"
                                           v-if="!userInfos[comment.userId].iconUrl"></el-avatar>
                                <el-avatar class="user-icon" :src="baseAPIUrl + userInfos[comment.userId].iconUrl"
                                           :size="50"
                                           v-if="userInfos[comment.userId].iconUrl"></el-avatar>
                                <div class="user-names">
                                    <div class="user-nickname">
                                        {{userInfos[comment.userId].nickname}}
                                    </div>
                                    <div class="user-screenName">
                                        @{{userInfos[comment.userId].screenName}}
                                    </div>
                                </div>
                            </router-link>
                        </div>
                        <div class="user-info comment-user-info" v-if="!userInfos[comment.userId]">
                            <el-avatar class="user-icon" icon="el-icon-user-solid" :size="50"></el-avatar>
                            <div class="user-names">
                                <div class="user-nickname">
                                    用户加载中：{{comment.userId}}
                                </div>
                                <div class="user-screenName">
                                    @加载中……
                                </div>
                            </div>
                        </div>
                        <div class="floor">
                            # {{comment.floor}}
                        </div>
                    </div>
                    <div class="comment-content">
                        {{comment.content}}
                    </div>
                    <div class="comment-footer origin-time">
                        {{comment.createdTime | dateformat}}
                    </div>
                    <el-button class="delete-comment" type="danger" @click="deleteComment(comment.id)"
                               v-if="loginUser && loginUser.id === comment.userId">
                        删除评论
                    </el-button>
                    <el-divider></el-divider>
                </div>
                <el-pagination
                        background
                        layout="prev, pager, next"
                        :page-count="commentsResp.total_pages"
                        @current-change="loadComments">
                </el-pagination>
            </div>
            <div class="comment-list" v-else>
                暂无评论
            </div>
        </div>

        <div class="like_container" v-if="viewType === 'like'">
            <div class="like-list" v-if="likesResp && likesResp.likes.length > 0">
                <div class="like-card" v-for="(like,index) of likesResp.likes" :key="index">
                    <div class="user-info like-user-info" v-if="userInfos[like.userId]">
                        <router-link :to="{name: 'user-profile', params: {userID: like.userId}}">
                            <el-avatar class="user-icon" icon="el-icon-user-solid" :size="50"
                                       v-if="!userInfos[like.userId].iconUrl"></el-avatar>
                            <el-avatar class="user-icon" :src="baseAPIUrl + userInfos[like.userId].iconUrl" :size="50"
                                       v-if="userInfos[like.userId].iconUrl"></el-avatar>
                            <div class="user-names">
                                <div class="user-nickname">
                                    {{userInfos[like.userId].nickname}}
                                </div>
                                <div class="user-screenName">
                                    @{{userInfos[like.userId].screenName}}
                                </div>
                            </div>
                        </router-link>

                    </div>
                    <div class="user-info like-user-info" v-if="!userInfos[like.userId]">
                        <el-avatar class="user-icon" icon="el-icon-user-solid" :size="50"></el-avatar>
                        <div class="user-names">
                            <div class="user-nickname">
                                用户加载中：{{like.userId}}
                            </div>
                            <div class="user-screenName">
                                @加载中……
                            </div>
                        </div>
                    </div>
                </div>

                <el-pagination
                        background
                        layout="prev, pager, next"
                        :page-count="likesResp.total_pages"
                        @current-change="loadLikes">
                </el-pagination>
            </div>
            <div class="like-list" v-else>
                暂无人点赞
            </div>
        </div>
    </div>
</template>

<script>
    import apiConfig from "@/api/apiConfig";
    import {Loading} from 'element-ui';

    export default {
        name: "WextDetailPage",
        props: ['wextID', 'view'],
        computed: {
            loginUser() {
                return this.$store.state.auth.loginUser
            }
        },
        methods: {
            switchToLikes() {
                this.viewType = 'like';
                this.loadLikes(1);
            },
            switchToComments() {
                this.viewType = 'comment';
                this.loadComments(1);
            },
            switchToReposts() {
                this.viewType = 'repost';
                this.loadReposts(1);
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
                            duration: 10000
                        });
                        this.emptyResult = true;
                    })
            },
            loadReposts(page) {
                let repostLoading = Loading.service({target: '.repost-list'});
                this.$axios.get(apiConfig.wextReposts(this.wextID), {
                    params: {
                        pageSize: 10,
                        page: page || 1
                    }
                })
                    .then(resp => {
                        this.repostsResp = resp.data.data;
                        this.repostsResp.reposts.forEach(
                            rep => this.getUserInfo(rep.userId)
                        );
                    })
                    .catch(error => {
                        console.error(error);
                        this.$notify.error({
                            title: '错误',
                            message: '无法获取转发列表',
                            duration: 10000
                        });
                    })
                    .finally(() => repostLoading.close());
            },
            loadComments(page) {
                let commentsLoading = Loading.service({target: '.comment-list'});
                this.$axios.get(apiConfig.wextComments(this.wextID), {
                    params: {
                        pageSize: 10,
                        page: page || 1
                    }
                })
                    .then(resp => {
                        this.commentsResp = resp.data.data;
                        this.commentsResp.comments.forEach(
                            com => this.getUserInfo(com.userId)
                        );
                    })
                    .catch(error => {
                        console.error(error);
                        this.$notify.error({
                            title: '错误',
                            message: '无法获取评论列表',
                            duration: 15000
                        });
                    })
                    .finally(() => commentsLoading.close());
            },
            loadLikes(page) {
                let likesLoading = Loading.service({target: '.like-list'});
                this.$axios.get(apiConfig.wextLikes(this.wextID), {
                    params: {
                        pageSize: 10,
                        page: page || 1
                    }
                })
                    .then(resp => {
                        this.likesResp = resp.data.data;
                        this.likesResp.likes.forEach(
                            like => this.getUserInfo(like.userId)
                        );
                    })
                    .catch(error => {
                        console.error(error);
                        this.$notify.error({
                            title: '错误',
                            message: '无法获取点赞列表',
                            duration: 10000
                        });
                    })
                    .finally(() => likesLoading.close());
            },
            loadHas() {
                if (this.$store.state.auth.loginUser) {
                    this.$axios.get(apiConfig.wextHasRepost(this.wextID))
                        .then(resp => {
                            this.hasRepost = resp.data.data;
                        })
                        .catch(error => {
                            console.error(error);
                        });
                    this.$axios.get(apiConfig.wextHasLike(this.wextID))
                        .then(resp => {
                            this.hasLike = resp.data.data;
                        })
                        .catch(error => {
                            console.error(error);
                        });
                }
            },
            deleteComment(cid) {
                this.$confirm('是否确定要删除此评论？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                })
                    .then(() => {
                        let commentsLoading = Loading.service({target: '.comment-list'});
                        this.$axios.delete(apiConfig.wextCommentOp(this.wextID, cid))
                            .then(() => {
                                this.$notify.info({
                                    title: '操作成功',
                                    message: '已删除评论',
                                    duration: 3000
                                });
                                this.commentsResp.comments = this.commentsResp.comments
                                    .filter(c => c.id !== cid); // 本地删除对应评论
                            })
                            .catch(error => {
                                console.error(error);
                                this.$notify.error({
                                    title: '错误',
                                    message: (error.response && error.response.data.message) ?
                                        error.response.data.message : '删除失败',
                                    duration: 10000
                                });
                            })
                            .finally(() => commentsLoading.close());
                    })
                    .catch(() => {
                    });   // 不catch的话console会报错
            },
            postComment() {
                if (!this.newComment || this.newComment.length === 0) {
                    this.$notify.warning({
                        title: '操作失败',
                        message: '评论不可为空',
                        duration: 10000
                    });
                    return;
                }
                let loading = Loading.service({target: '.comment-write-container'});
                this.$axios.put(apiConfig.wextCommentOp(this.wextID),
                    {content: this.newComment})
                    .then(resp => {
                        this.newComment = '';
                        this.wextDetail.wext.commentCount++;
                        this.$notify.info({
                            title: '操作成功',
                            message: '评论发送成功',
                            duration: 3000
                        });
                        if (this.commentsResp) {
                            this.commentsResp.comments.push(resp.data.data);
                        } else {
                            this.loadComments(1);
                        }
                    })
                    .catch(error => {
                        console.error(error);
                        this.$notify.error({
                            title: '错误',
                            message: (error.response && error.response.data.message) ?
                                error.response.data.message : '评论失败',
                            duration: 10000
                        });
                    })
                    .finally(() => loading.close());
            },
            doLike() {
                let loading = Loading.service({target: '.wext-actions'});
                this.$axios.put(apiConfig.wextLike(this.wextID))
                    .then(() => {
                        this.wextDetail.wext.likeCount++;
                        this.hasLike = true;
                        this.$notify.info({
                            title: '操作成功',
                            message: '已点赞此Wext',
                            duration: 3000
                        });
                        this.loadLikes(1);
                    })
                    .catch(error => {
                        console.error(error);
                        this.$notify.error({
                            title: '错误',
                            message: (error.response && error.response.data.message) ?
                                error.response.data.message : '点赞失败',
                            duration: 10000
                        });
                    })
                    .finally(() => loading.close());
            },
            deleteLike() {
                let loading = Loading.service({target: '.wext-actions'});
                this.$axios.delete(apiConfig.wextLike(this.wextID))
                    .then(() => {
                        this.wextDetail.wext.likeCount--;
                        this.hasLike = false;
                        this.$notify.info({
                            title: '操作成功',
                            message: '已取消点赞此Wext',
                            duration: 3000
                        });
                        this.loadLikes(1);
                    })
                    .catch(error => {
                        console.error(error);
                        this.$notify.error({
                            title: '错误',
                            message: (error.response && error.response.data.message) ?
                                error.response.data.message : '取消点赞失败',
                            duration: 10000
                        });
                    })
                    .finally(() => loading.close());
            },
            likeButtonAct() {
                if (this.hasLike) {
                    this.deleteLike();
                } else {
                    this.doLike();
                }
            },
            doRepost() {
                let loading = Loading.service({target: '.wext-actions'});
                this.$axios.put(apiConfig.wextRepost(this.wextID))
                    .then(() => {
                        this.wextDetail.wext.repostCount++;
                        this.hasRepost = true;
                        this.$notify.info({
                            title: '操作成功',
                            message: '已转发此Wext',
                            duration: 3000
                        });
                        this.loadReposts(1);
                    })
                    .catch(error => {
                        console.error(error);
                        this.$notify.error({
                            title: '错误',
                            message: (error.response && error.response.data.message) ?
                                error.response.data.message : '转发失败',
                            duration: 10000
                        });
                    })
                    .finally(() => loading.close());
            },
            deleteRepost() {
                let loading = Loading.service({target: '.wext-actions'});
                this.$axios.delete(apiConfig.wextRepost(this.wextID))
                    .then(() => {
                        this.wextDetail.wext.repostCount--;
                        this.hasRepost = false;
                        this.$notify.info({
                            title: '操作成功',
                            message: '已取消转发此Wext',
                            duration: 3000
                        });
                        this.loadReposts(1);
                    })
                    .catch(error => {
                        console.error(error);
                        this.$notify.error({
                            title: '错误',
                            message: (error.response && error.response.data.message) ?
                                error.response.data.message : '取消转发失败',
                            duration: 10000
                        });
                    })
                    .finally(() => loading.close());
            },
            repostButtonAct() {
                if (this.hasRepost) {
                    this.deleteRepost();
                } else {
                    this.doRepost();
                }
            },
            deleteWext() {
                this.$alert('删除Wext之后不可恢复，是否确认删除？', '确认删除')
                    .then(() => {
                        let loadingInstance = Loading.service({target: '.wext-page'});
                        this.$axios.delete(apiConfig.wextDetail(this.wextID))
                            .then(() => {
                                this.$router.push('/home');
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
                    })

            }
        },
        data() {
            return {
                viewType: this.view || 'comment',
                baseAPIUrl: apiConfig.base.substr(0, apiConfig.base.length - 1),
                newComment: '',
                wextDetail: null,
                repostsResp: null,
                commentsResp: null,
                userInfos: {},
                likesResp: null,
                hasRepost: false,
                hasLike: false
            }
        },
        mounted() {
            let detailLoading = Loading.service({target: '.wext-page'});
            this.$axios.get(apiConfig.wextDetail(this.wextID))
                .then(resp => {
                    this.wextDetail = resp.data.data;
                })
                .catch(error => {
                    console.error(error);
                    this.$notify.error({
                        title: '错误',
                        message: '无法获取Wext详情',
                        duration: 15000
                    });
                })
                .finally(() => detailLoading.close());
            // 加载
            if (this.viewType === 'comment') {
                this.loadComments(1);
            } else if (this.viewType === 'like') {
                this.loadLikes(1);
            }
            this.loadHas();
        }
    }
</script>

<style scoped>
    .wext-page {
        max-width: 1600px;
        margin: auto;
    }

    .user-info {
        display: flex;
        justify-content: left;
        margin: 4px 32px;
        padding: 8px;
        border-bottom: #27d9f5 4px solid;
        max-width: 320px;
    }

    .user-info a {
        text-decoration: none;
        color: inherit;
        display: flex;
    }

    .user-icon {
        margin: 4px 8px;
    }

    .user-names {
        margin: 4px;
        text-align: left;
    }

    .user-nickname {
        font-weight: bold;
        margin: 0 8px;
    }

    .user-screenName {
        font-style: italic;
        color: darkgrey;
        margin: 6px 4px;
    }

    .content-path {
        display: flex;
        justify-content: left;
        margin: 8px 40px;
        font-style: italic;
        color: #59b6e6;
        font-size: 0.9em;
    }

    .content-path a {
        text-decoration-line: none;
    }

    .wext-content {
        word-break: break-word;
        white-space: pre-wrap;
    }

    .origin-time {
        display: flex;
        justify-content: flex-end;
        margin: 0 32px;
        color: darkgrey;
        font-size: 0.9em;
    }

    .comment-write-container {
        margin: 24px 0;
        padding: 24px 0;
        border-bottom: darkgrey solid 2px;
        border-top: darkgrey solid 2px;
    }

    .comment-textarea {
        width: 75%;
        margin: 0 16px;
    }

    .comment-card-header {
        display: flex;
        justify-content: space-between;
    }

    .comment-card-header .floor {
        margin: auto 16px;
    }

    .comment-content {
        margin: 12px 6px;
        padding: 8px;
        text-align: left;
        text-indent: 2em;
        word-break: break-word;
        white-space: pre-wrap;
    }

    .like_container {
        margin-top: 16px;
    }

    .repost_container {
        margin-top: 16px;
    }

    .has-do {
        color: #7c65e6;
    }

</style>

<style>
    .wext-page .wext-detail .content-pics .el-image img {
        max-height: 600px;
    }
</style>
