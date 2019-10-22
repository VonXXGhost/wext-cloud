import {UserInfoItem} from "./user-info-item";

export interface UserInfoWithRelation {
  info: UserInfoItem;
  relationship?: string;
}
