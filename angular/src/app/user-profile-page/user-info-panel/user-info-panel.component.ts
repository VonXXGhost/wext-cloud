import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {UserInfoWithRelation} from 'src/app/class/user-info-with-relation';
import {UserService} from "../../service/user.service";
import {AuthService} from "../../service/auth.service";

@Component({
  selector: 'app-user-info-panel',
  templateUrl: './user-info-panel.component.html',
  styleUrls: ['./user-info-panel.component.css']
})
export class UserInfoPanelComponent implements OnInit {
  @Input() userInfo: UserInfoWithRelation;
  private relationShow;

  constructor(
    private userService: UserService,
    private authService: AuthService
  ) {
    this.relationShow = {
      following: '正在关注',
        follower: '追随者',
        friend: '互相关注'
    };
  }

  ngOnInit() {
  }

  followUser() {
    this.userService.putNewFollow(this.userInfo.info.screenName)
      .subscribe(
        resp => {
          this.userInfo.relationship = 'following';
          this.userInfo.info.followers += 1;
        }
      );
  }

  unfollowUser() {
    this.userService.deleteFollow(this.userInfo.info.screenName)
      .subscribe(
        resp => {
          this.userService.getUser(this.userInfo.info.screenName)
            .subscribe(
              data => {
                this.userInfo = data.data;
              }
            );
        }
      );
  }

}
