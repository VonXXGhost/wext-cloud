<template>
    <div class="unknown-end-page">
        <router-link :to="{query: {page: 1}}">
            <el-button>
                首页
            </el-button>
        </router-link>
        <router-link :to="{query: {page: page - 1}}" v-if="page > 1">
            <el-button>
                上一页
            </el-button>
        </router-link>
        <router-link :to="{query: {page: page + 1}}" v-if="!emptyResult">
            <el-button>
                下一页
            </el-button>
        </router-link>
    </div>
</template>

<script>
    export default {
        name: "UnknownEndPages",
        props: ['emptyResult'],
        computed: {
            page() {
                if (typeof(this.$route.query.page) === 'string') {
                    return parseInt(this.$route.query.page);
                }
                return this.$route.query.page || 1;
            }
        }
    }
</script>

<style scoped>

</style>
