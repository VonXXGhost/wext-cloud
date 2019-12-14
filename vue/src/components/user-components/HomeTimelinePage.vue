<template>
    <div class="home-timeline-page">
        <timeline-stream :timeline-items="timelineItems" class="timeline-stream"></timeline-stream>
        <unknown-end-pages :empty-result="emptyResult"></unknown-end-pages>
    </div>
</template>

<script>
    import {Loading} from 'element-ui';
    import axios from 'axios';
    import apiConfig from "@/api/apiConfig";
    import UnknownEndPages from "@/components/UnknownEndPages";
    import TimelineStream from "@/components/TimelineStream";

    export default {
        name: "HomeTimelinePage",
        components: {UnknownEndPages, TimelineStream},
        data() {
            return {
                timelineItems: [],
                emptyResult: true
            }
        },
        methods: {
            loadWexts() {
                let loadingInstance = Loading.service({target: '.timeline-stream'});
                this.timelineItems = [];
                axios.get(apiConfig.homeFeed, {
                    params: {page: this.$route.query.page || 1}
                })
                    .then(resp => {
                        this.timelineItems = resp.data.data;
                        this.emptyResult = this.timelineItems.length === 0;
                    })
                    .catch(error => {
                        console.error(error);
                        this.$notify.error({
                            title: '无法获取首页时间线',
                            message: error.msg ? error.msg : '无法连接到服务器',
                            duration: 10000
                        });
                        this.emptyResult = true;
                    })
                    .finally(() => loadingInstance.close());
            }
        },
        mounted() {
            this.loadWexts();
        },
        watch: {
            $route() {
                this.loadWexts();
            }
        }
    }
</script>

<style scoped>

</style>
