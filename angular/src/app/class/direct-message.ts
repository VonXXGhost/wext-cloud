import {UserInfoItem} from "./user-info-item";

export class DirectMessage {
  public id: number;
  public userIdFrom: number;
  public userInfoFrom?: UserInfoItem;
  public userIdTo: number;
  public userInfoTo?: UserInfoItem;
  public content: string;
  public haveRead: boolean;
  public createdTime: string;
}
