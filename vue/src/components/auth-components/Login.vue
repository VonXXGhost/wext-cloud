<template>
    <div class="login_container">
        <el-form :model="loginForm" :rules="rules" ref="loginForm"
                 label-width="100px" class="login-form">
            <el-form-item label="用户名/邮箱" prop="username">
                <el-input v-model="loginForm.username" placeholder="请输入用户名或邮箱"></el-input>
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
</template>

<script>

    export default {
        name: "Login",
        data() {
            return {
                loginForm: {
                    username: '',
                    password: ''
                },
                rules: {
                    username: [
                        {required: true, message: '请输入用户名或邮箱', trigger: 'blur'}
                    ],
                    password: [
                        {required: true, message: '请输入密码', trigger: 'blur'}
                    ]
                }
            }
        },
        methods: {
            submitForm(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        let form = this.$data.loginForm;
                        this.$store.dispatch('auth/sendLogin', {
                                "username": form.username,
                                "password": form.password
                            }
                        )
                            .then(() => {
                                // 重定向到之前的页面
                                if (this.$route.query.redirect) {
                                    this.$router.push(decodeURI(this.$route.query.redirect));
                                } else {
                                    this.$router.push('/home');
                                }
                            })
                            .catch(error =>
                                this.$message.error(
                                    '登陆失败: ' + error.msg ? error.msg : '连接服务器错误'
                                ))
                    } else {
                        this.$message.error('submit failed.');
                        return false;
                    }
                })
            }
        }
    }
</script>

<style scoped>
    .login_container {
        margin: 28px 25%;
    }
</style>
