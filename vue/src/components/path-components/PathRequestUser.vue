<template>
    <div class="path-request-user">
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
            <el-table-column prop="id" label="申请单ID"  width="120"></el-table-column>
            <el-table-column prop="requestComment" label="申请备注" width="300"></el-table-column>
            <el-table-column prop="managerName" label="处理人"></el-table-column>
            <el-table-column prop="manageComment" label="处理备注" width="300"></el-table-column>
            <el-table-column prop="createdTime" label="创建时间"></el-table-column>
            <el-table-column prop="lastUpdateTime" label="更新时间"></el-table-column>
        </el-table>
        <el-pagination
                background
                layout="prev, pager, next"
                :page-count="totalPages"
                @current-change="loadPRs">
        </el-pagination>
    </div>
</template>

<script>
    import apiConfig from "@/api/apiConfig";
    import {Loading} from 'element-ui';
    import moment from "moment";

    export default {
        name: "PathRequestUser",
        data() {
            return {
                prs: [],
                totalPages: 1,
                pageToLoad: 1,
                stateOptions: ['waiting', 'processing', 'success', 'reject'],
                stateChoose: '',
                timeOrderChoose: ''
            }
        },
        methods: {
            loadPRs(page) {
                let loadingInstance = Loading.service({target: '.path-request-user'});
                this.prs = [];
                this.$axios.get(apiConfig.prRequesting, {
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
            }
        },
        created() {
            this.loadPRs(1);
        }
    }
</script>

<style scoped>

</style>
