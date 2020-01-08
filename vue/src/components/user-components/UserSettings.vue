<template>
    <div class="user-settings-container">
        <h2>
            修改个人设置
        </h2>
        <p class="comment">
            只填写需要修改的项目，不需要修改的项目请留空
        </p>
        <el-form :model="settingsForm" :rules="rules" ref="settingsForm"
                 label-width="100px" class="settings-form">
            <el-form-item label="昵称" prop="nickname">
                <el-input v-model="settingsForm.nickname" :placeholder="s_nickname"
                          :maxlength="16" show-word-limit></el-input>
            </el-form-item>
            <el-form-item label="简介" prop="profile">
                <el-input v-model="settingsForm.profile" :placeholder="s_profile" name="profile"
                          :maxlength="255" show-word-limit></el-input>
            </el-form-item>
            <el-form-item label="头像" prop="icon">
                <el-upload
                        :http-request="uploadPicConfirm" accept="image/png,image/jpeg" ref="picUpload"
                        list-type="picture-card"
                        :on-remove="showUploadController" :on-progress="hideUploadController"
                        :on-exceed="exceedHandle" :class="{hidden: !this.showUpload, 'pic-upload-controller': true}"
                        :limit="1" action="">
                    <i class="el-icon-plus"></i>
                </el-upload>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="submitForm('settingsForm')">提交</el-button>
            </el-form-item>
        </el-form>

        <el-divider></el-divider>

        <el-form :model="pwForm" :rules="rules" ref="pwForm"
                 label-width="100px" class="pwForm">
            <el-form-item label="更改密码">
            </el-form-item>
            <el-form-item label="旧密码" prop="old_password" :required="true">
                <el-input v-model="pwForm.old_password" placeholder="输入旧密码" show-password
                          autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="新密码" prop="password">
                <el-input v-model="pwForm.password" show-password autocomplete="off" placeholder="6~18位不包含特殊字符"
                          :disabled="pwForm.old_password === ''"></el-input>
            </el-form-item>
            <el-form-item label="确认密码" prop="password_confirm">
                <el-input v-model="pwForm.password_confirm" show-password placeholder="重复新密码"
                          :disabled="pwForm.password === ''"></el-input>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="submitForm('pwForm')">提交</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
    import apiConfig from "@/api/apiConfig";
    import {Loading} from 'element-ui';

    export default {
        name: "UserSettings",
        computed: {
            s_nickname() {
                return this.$store.state.auth.loginUser.nickname
            },
            s_profile() {
                return this.$store.state.auth.loginUser.profile
            }
        },
        data() {
            let validatePass = (rule, value, callback) => {
                console.debug(rule);
                if (value === '') {
                    callback(new Error('请输入密码'));
                } else if (rule.pattern && !rule.pattern.test(value)) {
                    callback(new Error('密码格式错误'));
                } else {
                    if (this.pwForm.password_confirm !== '') {
                        this.$refs.pwForm.validateField('password_confirm');
                    }
                    callback();
                }
            };
            let validatePass2 = (rule, value, callback) => {
                if (value === '') {
                    callback(new Error('请再次输入密码'));
                } else if (value !== this.pwForm.password) {
                    callback(new Error('两次输入密码不一致!'));
                } else {
                    callback();
                }
            };
            return {
                showUpload: true,
                settingsForm: {
                    nickname: '',
                    profile: ''
                },
                pwForm: {
                    old_password: '',
                    password: '',
                    password_confirm: ''
                },
                rules: {
                    nickname: [
                        {
                            required: false, trigger: 'blur', message: '请输入正确的昵称',
                            pattern: /^\S{1,16}$/
                        }
                    ],
                    password: [
                        {
                            required: true, message: '请输入正确格式的密码', trigger: 'blur',
                            // pattern: /^[a-zA-Z0-9`~!@#$%^&*()_+\-={}|[\]\\;':"<>?,./]{6,18}$/
                            pattern: /^[!-~]{6,18}$/, validator: validatePass
                        }
                    ],
                    password_confirm: [
                        {
                            required: true, message: '输入密码不一致', trigger: 'blur',
                            validator: validatePass2
                        }
                    ]
                }
            }
        },
        methods: {
            uploadPic(param) {
                this.hideUploadController();
                let loading = Loading.service({target: '.pic-upload-controller'});
                let file = param.file;
                const isLt1M = file.size / 1024 / 1024 < 1;
                if (!isLt1M) {
                    this.$message.error('上传图片大小不能超过 1MB!');
                }

                let formData = new FormData();
                formData.append('file', file, file.name);
                this.$axios.post(apiConfig.uploadIcon, formData)
                    .then(resp => {
                        let userInfo = this.$store.state.auth.loginUser;
                        userInfo.iconUrl = resp.data.data.iconPath;
                        this.$notify.info({
                            title: '上传成功',
                            message: '头像已更新',
                            duration: 3000
                        });
                    })
                    .catch(error => {
                        this.showUploadController();
                        this.$notify.error({
                            title: '头像上传失败',
                            message: error.msg ? error.msg : '无法连接到服务器',
                            duration: 10000
                        });
                        this.$refs['picUpload'].clearFiles();
                    })
                    .finally(() => loading.close());
            },
            uploadPicConfirm(param) {
                this.$confirm('确定后将直接上传更新，是否确定？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                })
                    .then(() => this.uploadPic(param))
                    .catch(() => {
                    });
            },
            submitForm(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        if (formName === 'settingsForm') {
                            let form = this.$data.settingsForm;
                            Object.keys(form).forEach(value => {    // 清空未填写更改项
                                if (form[value] === '') {
                                    console.debug('delete ' + value);
                                    delete form[value];
                                }
                            });
                            if (Object.keys(form).length === 0) {
                                this.$message.warning('没有项目被修改');
                            } else {
                                let loading = Loading.service({target: '.settings-form'});
                                this.$axios.post(apiConfig.selfInfo, form)
                                    .then(resp => {
                                        this.$store.commit('auth/saveLoginUser', resp.data.data);
                                        this.$notify.info({
                                            title: '操作成功',
                                            message: '设置修改成功',
                                            duration: 3000
                                        });
                                    })
                                    .catch(error => {
                                        console.error(error);
                                        this.$notify.error({
                                            title: '修改失败',
                                            message: error.msg ? error.msg : '无法连接到服务器',
                                            duration: 5000
                                        });
                                    })
                                    .finally(() => loading.close());
                            }
                        } else if (formName === 'pwForm') {
                            let form = this.$data.pwForm;
                            let pw_data = {
                                oldPassword: form.old_password,
                                newPassword: form.password
                            };
                            let loading = Loading.service({target: '.pwForm'});
                            this.$axios.post(apiConfig.changePassword, pw_data)
                                .then(resp => {
                                    this.$store.commit('auth/saveToken', resp.data.data.token);
                                    this.$notify.info({
                                        title: '操作成功',
                                        message: '密码修改成功',
                                        duration: 3000
                                    });
                                })
                                .catch(error => {
                                    console.error(error);
                                    this.$notify.error({
                                        title: '修改失败',
                                        message: error.msg ? error.msg : '无法连接到服务器',
                                        duration: 5000
                                    });
                                })
                                .finally(() => loading.close());
                        }

                    } else {
                        this.$message.error('提交失败');
                        return false;
                    }
                })
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
        }
    }
</script>

<style scoped>
    p.comment {
        padding: 8px 100px;
        text-align: left;
        font-size: small;
    }

    .user-settings-container {
        margin: 18px 25%;
    }
</style>
