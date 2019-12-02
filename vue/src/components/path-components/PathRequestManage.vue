<template>
    <div class="pr-manage-container">
        <div class="pr-manage-login" v-if="!loginManager">
            <el-form :model="loginForm" :rules="rules" ref="loginForm"
                     label-width="100px" class="login-form">
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="loginForm.username" placeholder="请输入管理员用户名"></el-input>
                </el-form-item>
                <el-form-item label="密码" prop="password">
                    <el-input v-model="loginForm.password" placeholder="请输入密码"
                              id="password" name="password" show-password></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="submitForm('loginForm')">登录</el-button>
                </el-form-item>
            </el-form>
        </div>

        <div class="pr-manage-table" v-else>
            <el-row>
                <el-col :span="3" :offset="18">
                    <el-select v-model="stateChoose" clearable @change="loadPRs(1)"
                               placeholder="选择申请状态" value="">
                        <el-option v-for="item in stateOptions"
                                   :key="item" :label="item" :value="item">
                        </el-option>
                    </el-select>
                </el-col>
                <el-col :span="3">
                    <el-select v-model="timeOrderChoose" clearable @change="loadPRs(1)"
                               placeholder="选择时间排序" value="">
                        <el-option v-for="item in ['时间升序','时间降序']"
                                   :key="item" :label="item" :value="item">
                        </el-option>
                    </el-select>
                </el-col>
            </el-row>
            <el-table :data="prs" stripe>
                <el-table-column prop="fullPath" label="完整路径"></el-table-column>
                <el-table-column prop="state" label="申请状态" width="120"></el-table-column>
                <el-table-column prop="id" label="申请单ID"></el-table-column>
                <el-table-column prop="userID" label="申请用户ID"></el-table-column>
                <el-table-column prop="requestComment" label="申请备注" width="300"></el-table-column>
                <el-table-column prop="managerName" label="处理人"></el-table-column>
                <el-table-column prop="manageComment" label="处理备注" width="300"></el-table-column>
                <el-table-column prop="createdTime" label="创建时间"></el-table-column>
                <el-table-column prop="lastUpdateTime" label="更新时间"></el-table-column>
                <el-table-column
                        fixed="right"
                        label="操作"
                        width="140">
                    <template slot-scope="scope">
                        <el-button type="text" size="small" @click="choosePR(scope.row.id, 'success')">
                            同意
                        </el-button>
                        &emsp;
                        <el-button type="text" size="small" @click="choosePR(scope.row.id, 'reject')">
                            拒绝
                        </el-button>

                        <el-dialog title="更改请求状态"
                                   :visible.sync="dialogFormVisible" :modal-append-to-body='false'>
                            <el-form>
                                <p>
                                    将申请单ID：{{prIDChoose}} 状态更改为 {{stateToSet}}
                                </p>
                                <el-form-item label="管理备注" label-width="80px">
                                    <el-input v-model="manageComment" maxlength="1500"
                                              show-word-limit :autosize="{minRows: 3}"
                                              type="textarea"></el-input>
                                </el-form-item>
                            </el-form>
                            <div slot="footer" class="dialog-footer">
                                <el-button @click="cancelChoose">取 消</el-button>
                                <el-button type="primary" @click="setState">确 定</el-button>
                            </div>
                        </el-dialog>
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
                                @current-change="loadPRs">
                        </el-pagination>
                    </el-col>
                    <el-col :span="6">
                        <el-button @click="logOut()" type="text">
                            退出管理员登录
                        </el-button>
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
        name: "PathRequestManage",
        computed: {
            loginManager() {
                return this.$store.state.auth.loginManager;
            }
        },
        data() {
            return {
                loginForm: {
                    username: '',
                    password: ''
                },
                rules: {
                    username: [
                        {required: true, message: '请输入用户名', trigger: 'blur'}
                    ],
                    password: [
                        {required: true, message: '请输入密码', trigger: 'blur'}
                    ]
                },
                prs: [],
                totalPages: 1,
                pageToLoad: 1,
                stateOptions: ['waiting', 'processing', 'success', 'reject'],
                stateChoose: '',
                timeOrderChoose: '',
                dialogFormVisible: false,
                prIDChoose: null,
                stateToSet: null,
                manageComment: ''
            }
        },
        methods: {
            submitForm(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        let form = this.$data.loginForm;
                        this.$store.dispatch('auth/sendManagerLogin', {
                                "username": form.username,
                                "password": form.password
                            }
                        )
                            .catch(error =>
                                this.$message.error(
                                    '登陆失败: ' + error.msg ? error.msg : '连接服务器错误'
                                ))
                    } else {
                        this.$message.error('submit failed.');
                        return false;
                    }
                })
            },
            logOut() {
                this.$store.commit('auth/logOutManager');
            },
            loadPRs(page) {
                let loadingInstance = Loading.service({target: '.pr-manage-table'});
                this.prs = [];
                this.$axios.get(apiConfig.prManageRequests, {
                    params: {
                        page: page || this.pageToLoad,
                        pageSize: 30,
                        asc: this.timeOrderChoose === '时间升序',
                        state: this.stateChoose.length > 0 ? this.stateChoose : undefined
                    }
                })
                    .then(resp => {
                        this.pageToLoad = page + 1 || this.pageToLoad + 1;
                        this.prs = resp.data.data.requests;
                        this.totalPages = resp.data.data.total_pages;

                        this.prs = this.prs.map(request => {
                                request.createdTime = moment(request.createdTime).format('YYYY-MM-DD HH:mm:ss');
                                request.lastUpdateTime = moment(request.lastUpdateTime).format('YYYY-MM-DD HH:mm:ss');
                                if (request.managerID) {
                                    request.managerName = resp.data.data.managers[request.managerID].name;
                                }
                                return request;
                            }
                        );
                    })
                    .catch(error => {
                        console.error(error);
                        this.$notify.error({
                            title: '无法获取PR列表',
                            message: error.msg ? error.msg : '无法连接到服务器',
                            duration: 10000
                        });
                    })
                    .finally(() => loadingInstance.close());
            },
            choosePR(prID, state) {
                this.prIDChoose = prID;
                this.stateToSet = state;
                this.dialogFormVisible = true;
            },
            cancelChoose() {
                this.prIDChoose = null;
                this.stateToSet = null;
                this.dialogFormVisible = false;
            },
            setToAllow() {
                let loadingInstance = Loading.service({target: '.pr-manage-table'});
                this.$axios.post(apiConfig.prManageAllow(this.prIDChoose),
                    {
                        comment: this.manageComment
                    }, {
                        headers: {
                            Authorization: `Bearer ${this.$store.state.auth.managerToken}`
                        }
                    })
                    .then(() => {
                        this.$notify.info('更改成功');
                        this.setLocal(this.prIDChoose, 'success', this.loginManager.name, this.manageComment);
                        this.cancelChoose();
                    })
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
            setToReject() {
                let loadingInstance = Loading.service({target: '.pr-manage-table'});
                this.$axios.post(apiConfig.prManageReject(this.prIDChoose),
                    {
                        comment: this.manageComment
                    }, {
                        headers: {
                            Authorization: `Bearer ${this.$store.state.auth.managerToken}`
                        }
                    })
                    .then(() => {
                        this.$notify.info('更改成功');
                        this.setLocal(this.prIDChoose, 'reject', this.loginManager.name, this.manageComment);
                        this.cancelChoose();
                    })
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
            setState() {
                if (this.stateToSet === 'reject') {
                    this.setToReject();
                } else {
                    this.setToAllow();
                }
            },
            setLocal(id, state, manager, comment) {
                this.prs.forEach(pr => {
                    if (pr.id === id) {
                        pr.state = state;
                        pr.managerName = manager;
                        pr.manageComment = comment;
                    }
                })
            }
        },
        created() {
            this.loadPRs(1);
        }
    }
</script>

<style scoped>

</style>
