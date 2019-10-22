import {UserInfoItem} from "./user-info-item";

export class WextComment {
  id: number;
  wextId: string;
  userId: number;
  userInfo: UserInfoItem;
  floor: number;
  content: string;
  createdTime: string;
}
