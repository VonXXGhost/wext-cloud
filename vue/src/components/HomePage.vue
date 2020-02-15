<template>
    <div class="home_page">
        <div class="home logo">
            <img src="../assets/imgs/logo_transparent.png" alt="logo"/>
        </div>
        <div class="home description">
            Write nEXT
        </div>
        <div class="sign">
            <router-link to="/signUp">
                <el-button type="primary">
                    Sign Up
                </el-button>
            </router-link>
            <router-link to="/login">
                <el-button>
                    Log In
                </el-button>
            </router-link>
        </div>
    </div>
</template>

<script>
    export default {
        name: "HomePage",
        mounted() {
            if (this.$store.state.auth.loginUser) {
                this.$router.push('/home');
            }
        }
    }
</script>

<style scoped>
    .home_page {
        display: flex;
        align-items:center;
        justify-content:center;
        flex-direction: column;

        opacity: 0;
        animation: appearance 1s ease-out;
        animation-fill-mode: forwards;

        margin-bottom: 80px;
    }

    @keyframes appearance {
        form {
            opacity: 0;
        }
        to {
            opacity: 1;
        }
    }

    .home.logo {
        width: 55%;
        height: 55%;
        max-width: 600px;
    }

    .home.logo img {
        position: relative;
        width: 100%;
        height: 100%;
    }

    .home.description {
        font-size: 4.5em;
        font-style: italic;
        letter-spacing: 5px;
        text-shadow: 2px 2px 10px rgba(0,0,0,0.4);
        transform: translateY(-25%);
    }

    .sign {
        margin-top: 30px;
        width: 75%;
        display: flex;
        align-items:center;
        justify-content:center;
    }

    .sign button {
        font-size: 2em;
        padding: 15px;
    }

    .sign a {
        margin: 0 5%;
    }

</style>
