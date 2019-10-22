import {Wext} from './wext';
import {UserInfoItem} from './user-info-item';

export class TimelineItem {
  public wext: Wext;
  public repost: boolean;
  public user: UserInfoItem;
  public repostUser: UserInfoItem;
  public createdTime: string;
}
