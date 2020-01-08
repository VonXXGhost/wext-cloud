<template>
    <div class="signup_container">
        <el-form :model="signUpForm" :rules="rules" ref="signUpForm"
                 label-width="100px" class="signup-form">
            <el-form-item label="用户名" prop="screenName">
                <el-input v-model="signUpForm.screenName" placeholder="请输入用户名"></el-input>
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
                <el-input v-model="signUpForm.email" placeholder="请输入邮箱"></el-input>
            </el-form-item>
            <el-form-item label="昵称" prop="nickname">
                <el-input v-model="signUpForm.nickname" placeholder="请输入昵称"></el-input>
            </el-form-item>
            <el-form-item label="密码" prop="password">
                <el-input v-model="signUpForm.password" placeholder="请输入密码" show-password></el-input>
            </el-form-item>
            <el-form-item label="确认密码" prop="password_confirm">
                <el-input v-model="signUpForm.password_confirm" show-password></el-input>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="submitForm('signUpForm')">注册</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
    export default {
        name: "SignUp",
        data() {
            let validatePass = (rule, value, callback) => {
                if (value === '') {
                    callback(new Error('请输入密码'));
                } else if (rule.pattern && !rule.pattern.test(value)) {
                    callback(new Error('密码格式错误'));
                }
                else {
                    if (this.signUpForm.password_confirm !== '') {
                        this.$refs.signUpForm.validateField('password_confirm');
                    }
                    callback();
                }
            };
            let validatePass2 = (rule, value, callback) => {
                if (value === '') {
                    callback(new Error('请再次输入密码'));
                } else if (value !== this.signUpForm.password) {
                    callback(new Error('两次输入密码不一致!'));
                } else {
                    callback();
                }
            };
            return {
                signUpForm: {
                    screenName: '',
                    password: '',
                    email: '',
                    nickname: '',
                    password_confirm: ''
                },
                rules: {
                    screenName: [
                        {
                            required: true, trigger: 'blur', message: '请输入正确的用户名',
                            pattern: /^[a-zA-Z]\w{3,11}$/
                        }
                    ],
                    password: [
                        {
                            required: true, message: '请输入正确格式的密码', trigger: 'blur',
                            // pattern: /^[a-zA-Z0-9`~!@#$%^&*()_+\-={}|[\]\\;':"<>?,./]{6,18}$/
                            pattern: /^[!-~]{6,18}$/, validator: validatePass
                        }
                    ],
                    nickname: [
                        {
                            required: true, trigger: 'blur', message: '请输入正确的昵称',
                            pattern: /^\S{1,16}$/
                        }
                    ],
                    email: [
                        {
                            required: true, trigger: 'blur', message: '请输入正确的邮箱',
                            type: 'email'
                        }
                    ],
                    password_confirm: [
                        {
                            required: true, trigger: 'blur',
                            validator: validatePass2
                        }
                    ]
                }
            }
        },
        methods: {
            submitForm(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        let form = this.$data.signUpForm;
                        delete form.password_confirm;
                        this.$store.dispatch('auth/sendSignUp', form)
                            .catch(reason => console.error(reason));
                    } else {
                        alert('submit failed.');
                        return false;
                    }
                })
            }
        }
    }
</script>

<style scoped>
    .signup_container {
        margin: 28px 25%;
    }
</style>
