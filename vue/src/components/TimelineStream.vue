<template>
    <div class="timeline-stream">
        <div class="stream" v-if="timelineItems && timelineItems.length > 0">
            <div v-for="item of timelineItems" class="timeline-card" :key="item.id">
                <div class="repost-panel" v-if="item.repostUser">
                    <a class="repost user">
                        {{item.repostUser.nickname}}
                    </a>
                    &emsp;转发了
                </div>

                <div class="card-header">
                    <router-link :to="{name: 'user-profile', params: {userID: item.user.screenName}}">
                        <el-avatar class="user-icon" icon="el-icon-user-solid" v-if="!item.user.iconUrl"></el-avatar>
                        <el-avatar class="user-icon" :src="baseAPIUrl + item.user.iconUrl"
                                   v-if="item.user.iconUrl"></el-avatar>
                        <div class="user-names">
                            <div class="user-nickname">
                                {{item.user.nickname}}
                            </div>
                            <div class="user-screenName">
                                @{{item.user.screenName}}
                            </div>
                        </div>
                    </router-link>
                </div>

                <div class="card-content">
                    <div class="content-path">
                        <router-link :to="'/path' + item.wext.fullPath">
                            >>>&ensp;{{item.wext.fullPath}}
                        </router-link>
                    </div>
                    <p class="content-text">
                        {{item.wext.text}}
                    </p>
                    <div class="content-pic" v-if="item.wext.picList && item.wext.picList.length > 0">
                        <el-image fit="contain"
                                  :src="baseAPIUrl + item.wext.picList[0]"
                                  :preview-src-list="item.wext.picList.map(p => baseAPIUrl + p)">
                        </el-image>
                    </div>
                </div>

                <div class="origin-time">
                    <p>
                        {{item.wext.createdTime | dateformat}}
                    </p>
                </div>

                <div class="card-actions">
                    <router-link :to="{name:'wext-page', params: {wextID: item.wext.id, view: 'repost'}}">
                        <el-button size="mini" round icon="el-icon-share">
                            转发&ensp;{{item.wext.repostCount}}
                        </el-button>
                    </router-link>
                    <router-link :to="{name:'wext-page', params: {wextID: item.wext.id, view: 'comment'}}">
                        <el-button size="mini" round icon="el-icon-s-comment">
                            评论&ensp;{{item.wext.commentCount}}
                        </el-button>
                    </router-link>
                    <router-link :to="{name:'wext-page', params: {wextID: item.wext.id, view: 'like'}}">
                        <el-button size="mini" round icon="el-icon-star-off">
                            点赞&ensp;{{item.wext.likeCount}}
                        </el-button>
                    </router-link>

                </div>

                <el-divider></el-divider>
            </div>
        </div>
        <div class="stream" v-if="!timelineItems || timelineItems.length === 0">
            <div class="timeline-card">
                <p>
                    当前节点已无条目
                </p>
            </div>
        </div>
    </div>

</template>

<script>
    import apiConfig from "@/api/apiConfig";

    export default {
        name: "TimelineStream",
        props: ['timelineItems'],
        data() {
            return {
                baseAPIUrl: apiConfig.base.substr(0, apiConfig.base.length - 1)
            }
        }
    }
</script>

<style scoped>
    .timeline-card {
        text-align: left;
        margin: 25px 5%;
    }

    .repost-panel {
        margin: 5px 15px;
        font-size: 0.95em;
        color: #27d9f5;
        font-weight: lighter;
    }

    .card-header {
        display: flex;
        padding: 5px;
        border-left: #9bffd0 solid 8px;
        margin: 4px 0;
    }

    .card-header a {
        text-decoration: none;
        color: inherit;
        display: flex;
    }

    .user-icon {
        margin: 4px 8px;
    }

    .user-names {
        margin: 4px;
    }

    .user-nickname {
        font-weight: bold;
    }

    .user-screenName {
        font-style: italic;
        font-size: 0.85em;
        color: darkgrey;
        margin: 4px 0;
    }

    .card-content {
        margin: 2px;
        padding: 6px;
    }

    .content-path {
        font-size: 0.9em;
        font-style: italic;
        color: #59b6e6;
        margin: 0 4px;
    }

    .content-path a {
        text-decoration-line: none;
    }

    .content-text {
        text-indent: 1em;
        word-break: break-word;
    }

    .content-pic {
        height: 150px;
    }

    .content-pic .el-image {
        width: 100%;
        height: 100%;
    }

    .origin-time {
        display: flex;
        justify-content: flex-end;
        margin: 0 32px;
        color: darkgrey;
        font-size: 0.9em;
    }

</style>
