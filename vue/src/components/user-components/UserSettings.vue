<template>
    <div class="user-settings-container">
        <h2>
            修改个人设置
        </h2>
        <el-form :model="settingsForm" :rules="rules" ref="settingsForm"
                 label-width="100px" class="settings-form">
            <el-form-item label="昵称" prop="nickname">
                <el-input v-model="settingsForm.nickname" :placeholder="s_nickname"></el-input>
            </el-form-item>
            <el-form-item label="简介" prop="profile">
                <el-input v-model="settingsForm.profile" :placeholder="s_profile" name="profile"></el-input>
            </el-form-item>
            <el-form-item label="密码" prop="password">
                <el-input v-model="settingsForm.password" placeholder="请输入密码" show-password autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="确认密码" prop="password_confirm">
                <el-input v-model="settingsForm.password_confirm" show-password></el-input>
            </el-form-item>
            <el-upload
                    :http-request="uploadPic" accept="image/png,image/jpeg" ref="picUpload"
                    list-type="picture-card"
                    :on-remove="showUploadController" :on-progress="hideUploadController"
                    :on-exceed="exceedHandle" :class="{hidden: !this.showUpload, 'pic-upload-controller': true}"
                    :limit="1" action="">
                <i class="el-icon-plus"></i>
            </el-upload>
            <el-form-item>
                <el-button type="primary" >提交</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
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
            return {
                settingsForm: {
                    nickname: '',
                    password: '',
                    password_confirm: '',
                    iconUrl: '',
                    profile: ''
                }
            }
        }
    }
</script>

<style scoped>

</style>
