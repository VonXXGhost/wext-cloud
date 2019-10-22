const baseUrl = 'http://localhost:9100/';

export const apiUrl = {
  base: baseUrl,
  pathWext: baseUrl + 'timeline/path',
  homeFeed: baseUrl + 'timeline/home',
  userProfileFeed: (username: string) => baseUrl + `timeline/user/${username}`,
  login: baseUrl + 'auth/login',
  signUp: baseUrl + 'auth/signup',
  selfInfo: baseUrl + 'user/me',
  userInfo: (username: string | number) => baseUrl + `user/${username}/info`,
  userFollow: (username: string) => baseUrl + `user/${username}/follow`,
  userFollowing: (username: string) => baseUrl + `user/${username}/following`,
  userFollowers: (username: string) => baseUrl + `user/${username}/followers`,
  userLike: (username: string) => baseUrl + `user/${username}/likes`,
  wextDetail: (id: string) => baseUrl + `wext/${id}`,
  wextComments: (id: string) => baseUrl + `wext/${id}/comments`,
  wextLikes: (id: string) => baseUrl + `wext/${id}/likes`,
  wextLike: (id: string) => baseUrl + `wext/${id}/like`,
  wextHasLike: (id: string) => baseUrl + `wext/${id}/like/has`,
  wextPut: baseUrl + 'wext/',
  wextRepost: (id: string) => baseUrl + `wext/${id}/repost`,
  wextHasRepost: (id: string) => baseUrl + `wext/${id}/repost/has`,
  wextCommentOp: (id: string, cid?: number) => cid ?
    baseUrl + `wext/${id}/comment/${cid}` :
    baseUrl + `wext/${id}/comment`,
  uploadPic: baseUrl + 'upload/image',
  dmReceives: baseUrl + 'message/receives',
  dmSends: baseUrl + 'message/sends',
  dmRead: baseUrl + 'message/read',
  dmNewPost: baseUrl + 'message/',
  dmDetail: (id: number) => baseUrl + `message/message/${id}`
};
