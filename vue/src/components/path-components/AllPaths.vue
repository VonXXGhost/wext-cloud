<template>
    <div class="all-paths-container">
        <div class="path-table">
            <el-table :data="paths" stripe>
                <el-table-column prop="nodeName" label="话题节点名称"></el-table-column>
                <el-table-column prop="fullPath" label="完整路径"></el-table-column>
                <el-table-column prop="createdTime" label="创建时间" width="150"></el-table-column>
                <el-table-column
                        label="操作"
                        width="140">
                    <template slot-scope="scope">
                        <router-link :to="'/path' + scope.row.fullPath">
                            <el-button type="text" size="small">
                                话题页
                            </el-button>
                        </router-link>
                        &emsp;
                        <router-link :to="'/all-path' + scope.row.fullPath">
                            <el-button type="text" size="small">
                                子节点列表
                            </el-button>
                        </router-link>
                    </template>
                </el-table-column>
            </el-table>
            <footer>
                <el-row>
                    <el-col :span="18">
                        <el-pagination
                                background
                                layout="prev, pager, next"
                                :page-count="totalPages"
                                @current-change="loadPaths">
                        </el-pagination>
                    </el-col>
                    <el-col :span="6">
                        <div class="requester">
                            <el-button type="text" size="small" @click="dialogFormVisible = true">
                                申请新话题节点
                            </el-button>
                            <router-link to="/path_request">
                                <el-button type="text" size="small">
                                    已提交的申请
                                </el-button>
                            </router-link>

                            <el-dialog title="申请话题节点" :visible.sync="dialogFormVisible">
                                <el-form :model="form">
                                    <el-form-item label="新节点名" label-width="80px">
                                        <el-input v-model="form.fullPath" autocomplete="off">
                                            <template slot="prepend">{{shortFullPath}}</template>
                                        </el-input>
                                    </el-form-item>
                                    <el-form-item label="申请备注" label-width="80px">
                                        <el-input v-model="form.comment" minlength="30" maxlength="1500"
                                                  show-word-limit :autosize="{minRows: 3}"
                                                  type="textarea"></el-input>
                                    </el-form-item>
                                </el-form>
                                <div slot="footer" class="dialog-footer">
                                    <el-button @click="dialogFormVisible = false">取 消</el-button>
                                    <el-button type="primary" @click="postPathRequest">提 交</el-button>
                                </div>
                            </el-dialog>
                        </div>
                    </el-col>
                </el-row>
            </footer>


        </div>
    </div>
</template>

<script>
    import apiConfig from "@/api/apiConfig";
    import {Loading} from 'element-ui';
    import moment from "moment";

    export default {
        name: "AllPaths",
        props: ['fullPath'],
        computed: {
            shortFullPath() {
                const maxLength = 32;
                if (this.fullPath.length > maxLength) {
                    return this.fullPath.substr(0, 18) +
                        '......' +
                        this.fullPath.substr(this.fullPath.length - 10) + '/'
                } else if (this.fullPath === '/') {
                    return this.fullPath;
                } else {
                    return this.fullPath + '/';
                }
            }
        },
        data() {
            return {
                totalPages: 1,
                pageToLoad: 1,
                paths: [],
                dialogFormVisible: false,
                form: {
                    fullPath: '',
                    comment: ''
                }
            }
        },
        methods: {
            loadPaths(page) {
                let loadingInstance = Loading.service({target: '.all-paths-container'});
                this.paths = [];
                this.$axios.get(apiConfig.pathGenel(this.fullPath), {
                    params: {
                        page: page || this.pageToLoad,
                        pageSize: 50
                    }
                })
                    .then(resp => {
                        this.pageToLoad = page + 1 || this.pageToLoad + 1;
                        this.paths = resp.data.data.children.paths;
                        this.totalPages = resp.data.data.children.total_pages;

                        this.paths = this.paths.map(path => {
                                path.createdTime = moment(path.createdTime).format('YYYY-MM-DD HH:mm:ss');
                                return path;
                            }
                        );
                    })
                    .catch(error => {
                        console.error(error);
                        this.$notify.error({
                            title: '无法获取路径列表',
                            message: error.msg ? error.msg : '无法连接到服务器',
                            duration: 10000
                        });
                    })
                    .finally(() => loadingInstance.close());
            },
            postPathRequest() {
                let loadingInstance = Loading.service({target: '.requester'});
                let request = JSON.parse(JSON.stringify(this.form));
                if (this.fullPath.lastIndexOf('/') === (this.fullPath.length - 1)) {
                    request.fullPath = this.fullPath + request.fullPath;
                } else {
                    request.fullPath = this.fullPath + '/' + request.fullPath;
                }
                this.$axios.put(apiConfig.prPut, request)
                    .then(() => {
                        this.$message.info("已成功提交申请");
                        this.dialogFormVisible = false;
                        this.form = {
                            fullPath: '',
                            comment: ''
                        };
                    })
                    .catch(error => {
                        console.error(error);
                        this.$notify.error({
                            title: '申请提交失败',
                            message: error.msg ? error.msg : '无法连接到服务器',
                            duration: 10000
                        });
                    })
                    .finally(() => loadingInstance.close());
            }
        },
        watch: {
            fullPath: {
                handler() {
                    this.loadPaths(1);
                },
                immediate: true
            }
        }
    }
</script>

<style scoped>
    footer {
        margin: 16px 8px;
    }

    .all-paths-container {
        padding: 8px 64px;
    }

    .requester .el-button {
        margin: 0 4px;
    }
</style>
