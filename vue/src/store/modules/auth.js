import apiConfig from "@/api/apiConfig";
import axios from 'axios';

const state = {
    loginUser: localStorage.loginUser ? JSON.parse(localStorage.loginUser) : null,
    token: localStorage.jwtToken || null,
    loginManager: localStorage.loginManager ? JSON.parse(localStorage.loginManager) : null,
    managerToken: localStorage.managerToken || null,
};

// getters
const getters = {};

// actions
const actions = {
    sendLogin({commit}, loginInfo) {
        return axios.post(apiConfig.login, loginInfo)
            .then(resp => {
                commit('saveToken', resp.data.data.token);
                commit('saveLoginUser', resp.data.data.user);
            })
            .catch(error => {
                console.error(error);
                throw error;
            });
    },
    sendSignUp({commit}, userInfo) {
        return axios.post(apiConfig.signUp, userInfo)
            .then(resp => {
                commit('saveToken', resp.data.data.token);
                commit('saveLoginUser', resp.data.data.user);
            })
            .catch(error => {
                console.error(error);
                throw error;
            });
    },
    sendManagerLogin({commit}, loginInfo) {
        return axios.post(apiConfig.managerLogin, loginInfo)
            .then(resp => {
                console.debug(resp);
                commit('saveManagerToken', resp.data.data.token);
                commit('saveLoginManager', resp.data.data.manager);
            })
            .catch(error => {
                console.error(error);
                throw error;
            });
    }
};

// mutations
const mutations = {
    saveToken(state, token) {
        state.token = token;
        localStorage.jwtToken = token;
    },

    saveLoginUser(state, user) {
        state.loginUser = user;
        localStorage.loginUser = JSON.stringify(user);
    },

    logOut(state) {
        state.token = null;
        state.loginUser = null;
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('loginUser');
    },

    saveManagerToken(state, token) {
        state.managerToken = token;
        localStorage.managerToken = token;
    },

    saveLoginManager(state, manager) {
        state.loginManager = manager;
        localStorage.loginManager = JSON.stringify(manager);
    },

    logOutManager(state) {
        state.managerToken = null;
        state.loginManager = null;
        localStorage.removeItem('managerToken');
        localStorage.removeItem('loginManager');
    },
};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}
