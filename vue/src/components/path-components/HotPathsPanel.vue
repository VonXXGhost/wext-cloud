<template>
    <div class="hot-paths-panel">
        <div class="hot-title" v-if="hotPaths.length > 0">
            热门子话题：
        </div>
        <div class="hot-title" v-if="hotPaths.length === 0">
            此话题暂无热门子话题
        </div>
        <div class="hot-path" v-for="(path, index) of hotPaths" :key="index">
            <el-badge :value="shortNum(path.count)" class="path-item">
                <router-link :to="'/path' + path.fullPath">
                    <el-button size="small" round type="primary">
                        {{path.fullPath}}
                    </el-button>
                </router-link>
            </el-badge>
        </div>
        <div class="hot-path view-all">
            <router-link :to="'/all-path' + path">
                <el-button size="small" round type="info">
                    查看所有子话题
                </el-button>
            </router-link>
        </div>
    </div>
</template>

<script>
    import axios from 'axios';
    import apiConfig from "@/api/apiConfig";
    import { Loading } from 'element-ui';

    export default {
        name: "HotPathsPanel",
        methods: {
            shortNum(num) {
                if (num >= 100000000) {
                    return (num/1000000).toFixed(1) + '亿';
                }
                if (num >= 1000000) {
                    return (num/1000000).toFixed(1) + '百万';
                }
                if (num >= 10000) {
                    return (num/10000).toFixed(1) + '万';
                }
                return num;
            }
        },
        props: ['path'],
        data() {
            return {
                hotPaths: []
            }
        },
        watch: {
            path: {
                handler(newV, oldV) {
                    let loadingInstance = Loading.service({target: '.hot-paths-panel'});
                    console.debug(newV, oldV);
                    this.hotPaths = [];
                    axios.get(apiConfig.pathHot(newV))
                        .then(resp => this.hotPaths = resp.data.data)
                        .catch(error => {
                            console.error(error);
                            this.$notify.error({
                                title: '无法获取热门路径信息',
                                message: error.msg ? error.msg : '无法连接到服务器',
                                duration: 10000
                            });
                        })
                        .finally(() => loadingInstance.close());
                },
                immediate: true
            }
        }
    }
</script>

<style scoped>
    .hot-paths-panel {
        display: flex;
        justify-items: left;
        flex-direction: row;
        flex-wrap: wrap;
        margin: 8px;
        padding: 12px;
        border-bottom: darkgrey solid 2px;
        width: 80%;
    }

    .hot-path {
        margin: 6px 12px;
    }

    .hot-title {
        margin: 4px 18px;
        font-weight: bold;
    }
</style>

<style>
    .el-badge__content.is-fixed {
        z-index: 1;
    }
</style>
