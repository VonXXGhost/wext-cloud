<template>
    <div class="path-page">
        <hot-paths-panel :path="fullPath"></hot-paths-panel>
        <div class="wext-write-container">
            <el-input type="textarea" placeholder="写下你的现在" resize="none"
                      v-model="newWext" minlength="1" maxlength="3000" show-word-limit
                      class="wext-textarea"></el-input>
            <el-upload
                    :http-request="uploadPic" accept="image/png,image/jpeg" ref="picUpload"
                    list-type="picture-card"
                    :on-remove="showUploadController" :on-progress="hideUploadController"
                    :on-exceed="exceedHandle" :class="{hidden: !this.showUpload, 'pic-upload-controller': true}"
                    :limit="1" action="">
                <i class="el-icon-plus"></i>
            </el-upload>
            <el-button @click="postWext" class="post-button">
                发表
            </el-button>
        </div>
        <el-divider></el-divider>
        <timeline-stream :timeline-items="timelineItems"></timeline-stream>
        <unknown-end-pages :empty-result="emptyResult"></unknown-end-pages>
    </div>
</template>

<script>
    import TimelineStream from "@/components/TimelineStream";
    import HotPathsPanel from "@/components/path-components/HotPathsPanel";
    import {Loading} from 'element-ui';
    import axios from 'axios';
    import apiConfig from "@/api/apiConfig";
    import UnknownEndPages from "@/components/UnknownEndPages";

    export default {
        name: "PathPage",
        components: {
            UnknownEndPages,
            HotPathsPanel,
            TimelineStream
        },
        props: ['fullPath'],
        data: function () {
            return {
                timelineItems: [],
                emptyResult: true,
                newWext: "",
                picPath: null,
                picUrl: null,
                showUpload: true,
            }
        },
        methods: {
            postWext() {
                if (!this.newWext || this.newWext.length === 0) {
                    this.$notify.warning({
                        title: '操作失败',
                        message: '内容不可为空',
                        duration: 10000
                    });
                    return;
                }
                let loading = Loading.service({target: '.wext-write-container'});
                this.$axios.put(apiConfig.wextPut,
                    {
                        content: this.newWext,
                        picURLs: this.picPath ? [this.picPath] : null,
                        path: this.fullPath
                    })
                    .then(resp => {
                        this.newWext = '';
                        this.picPath = null;
                        this.picUrl = null;
                        this.$notify.info({
                            title: '操作成功',
                            message: 'Wext发送成功',
                            duration: 3000
                        });
                        this.timelineItems = [resp.data.data].concat(this.timelineItems);
                    })
                    .catch(error => {
                        console.error(error);
                        this.$notify.error({
                            title: 'Wext发送失败',
                            message: error.msg ? error.msg : '无法连接到服务器',
                            duration: 10000
                        });
                    })
                    .finally(() => loading.close());
            },
            uploadPic(param) {
                this.hideUploadController();
                let loading = Loading.service({target: '.pic-upload-controller'});
                let file = param.file;
                const isLt10M = file.size / 1024 / 1024 < 10;
                if (!isLt10M) {
                    this.$message.error('上传图片大小不能超过 10MB!');
                }

                let formData = new FormData();
                formData.append('file', file, file.name);
                axios.post(apiConfig.uploadPic, formData)
                    .then(resp => {
                        this.picPath = resp.data.data.path;
                        this.picUrl = URL.createObjectURL(file);
                    })
                    .catch(error => {
                        this.showUploadController();
                        this.$notify.error({
                            title: '图片上传失败',
                            message: error.msg ? error.msg : '无法连接到服务器',
                            duration: 10000
                        });
                        this.$refs['picUpload'].clearFiles();
                    })
                    .finally(() => loading.close());
            },
            loadWexts() {
                this.timelineItems = [];
                let loadingInstance = Loading.service({target: '.timeline-stream'});

                axios.get(apiConfig.pathFeed(this.fullPath), {
                    params: {page: this.$route.query.page || 1}
                })
                    .then(resp => {
                        this.timelineItems = resp.data.data;
                        this.emptyResult = this.timelineItems.length === 0;
                    })
                    .catch(error => {
                        console.error(error);
                        this.$notify.error({
                            title: '无法获取节点时间线',
                            message: error.msg ? error.msg : '无法连接到服务器',
                            duration: 10000
                        });
                        this.emptyResult = true;
                    })
                    .finally(() => loadingInstance.close());
            },
            exceedHandle() {
                this.$notify.warning("只能上传一张图片");
            },
            hideUploadController() {
                this.showUpload = false;
            },
            showUploadController() {
                this.showUpload = true;
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
    .hot-paths-panel {
        margin: auto;
    }

    .wext-write-container {
        display: flex;
        margin: 12px 10%;
    }

    .uploaded-img {
        width: 148px;
        height: 148px;
        display: block;
    }
</style>

<style>
    .wext-textarea textarea {
        height: 100%;
    }

    .hidden .el-upload--picture-card {
        display: none;
    }

    .pic-upload-controller ul {
        margin-block-start: 0;
        margin-block-end: 0;
    }

    .pic-upload-controller ul li.el-upload-list__item {
        margin: 0;
    }
</style>
