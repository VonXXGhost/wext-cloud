<template>
    <div class="user-profile-page">
        <user-info-panel :user-info="userInfo" class="user-info-panel"></user-info-panel>
        <el-divider></el-divider>
        <timeline-stream :timeline-items="timelineItems" class="timeline-stream"></timeline-stream>
        <unknown-end-pages :empty-result="emptyResult"></unknown-end-pages>
    </div>
</template>

<script>
    import UserInfoPanel from "@/components/user-components/UserInfoPanel";
    import {Loading} from 'element-ui';
    import axios from 'axios';
    import apiConfig from "@/api/apiConfig";
    import TimelineStream from "@/components/TimelineStream";
    import UnknownEndPages from "@/components/UnknownEndPages";

    export default {
        name: "UserProfilePage",
        components: {UnknownEndPages, TimelineStream, UserInfoPanel},
        props:['userID'],
        data () {
            return {
                userInfo: null,
                timelineItems: [],
                emptyResult: true
            };
        },
        methods: {
            updateItems(userID) {
                let streamLoading = Loading.service({target: '.timeline-stream'});
                axios.get(apiConfig.userProfileFeed(userID), {
                    params: {page: this.$route.query.page || 1}
                })
                    .then(resp => {
                        this.timelineItems = resp.data.data;
                        this.emptyResult = this.timelineItems.length === 0;
                    })
                    .catch(error => {
                        console.error(error);
                        this.$notify.error({
                            title: '错误',
                            message: '无法获取用户时间线',
                            duration: 0
                        });
                        this.emptyResult = true;
                    })
                    .finally(() => streamLoading.close());
            }
        },
        watch: {
            userID: {
                // eslint-disable-next-line no-unused-vars
                handler(newV, oldV) {
                    let infoLoading = Loading.service({target: '.user-info-panel'});
                    this.timelineItems = [];
                    this.updateItems(newV);
                    axios.get(apiConfig.userInfo(newV))
                        .then(resp => {
                            this.userInfo = resp.data.data;
                        })
                        .catch(error => {
                            console.error(error);
                            this.$notify.error({
                                title: '错误',
                                message: '无法获取用户信息',
                                duration: 0
                            });
                            this.emptyResult = true;
                        })
                        .finally(() => infoLoading.close());
                },
                immediate: true
            },
            $route: {
                handler(newV, oldV) {
                    if (newV && oldV && newV.query.page === oldV.query.page) {
                        return;
                    }
                    this.updateItems(this.userID);
                }
            }
        }
    }
</script>

<style scoped>

</style>
