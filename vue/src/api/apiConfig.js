const baseUrl = 'http://localhost:9100/';

export default {
    base: baseUrl,
    pathFeed: path => baseUrl + 'timeline/path' + path,
    homeFeed: baseUrl + 'timeline/home',
    userProfileFeed: username => baseUrl + `timeline/user/${username}`,
    login: baseUrl + 'auth/login',
    signUp: baseUrl + 'auth/signup',
    changePassword: baseUrl + 'auth/password',
    selfInfo: baseUrl + 'user/me',
    userInfo: username => baseUrl + `user/${username}/info`,
    userFollow: username => baseUrl + `user/${username}/follow`,
    userFollowing: username => baseUrl + `user/${username}/following`,
    userFollowers: username => baseUrl + `user/${username}/followers`,
    userLike: username => baseUrl + `user/${username}/likes`,
    wextDetail: id => baseUrl + `wext/${id}`,
    wextComments: id => baseUrl + `wext/${id}/comments`,
    wextLikes: id => baseUrl + `wext/${id}/likes`,
    wextLike: id => baseUrl + `wext/${id}/like`,
    wextHasLike: id => baseUrl + `wext/${id}/like/has`,
    wextPut: baseUrl + 'wext/',
    wextReposts: id => baseUrl + `wext/${id}/reposts`,
    wextRepost: id => baseUrl + `wext/${id}/repost`,
    wextHasRepost: id => baseUrl + `wext/${id}/repost/has`,
    wextCommentOp: (id, cid) => cid ?
                                baseUrl + `wext/${id}/comment/${cid}` :
                                baseUrl + `wext/${id}/comment`,
    uploadPic: baseUrl + 'upload/image',
    uploadIcon: baseUrl + 'upload/profile_icon',
    dmReceives: baseUrl + 'message/receives',
    dmSends: baseUrl + 'message/sends',
    dmRead: baseUrl + 'message/read',
    dmNewPost: baseUrl + 'message/',
    dmDetail: id => baseUrl + `message/message/${id}`,
    pathGenel: path => baseUrl + `path/general${path}`,
    pathHot: path => baseUrl + `path/hot${path}`,
    prPut: baseUrl + 'path_request/',
    prRequesting: baseUrl + 'path_request/requesting',
    prManageRequests: baseUrl + 'path_request/manage/requests',
    prManageDispatch: baseUrl + 'path_request/manage/dispatch',
    prManageAllow: prID => baseUrl + `path_request/manage/${prID}/allow`,
    prManageReject: prID => baseUrl + `path_request/manage/${prID}/reject`,
    managerLogin: baseUrl + 'auth/manager/login'
}
