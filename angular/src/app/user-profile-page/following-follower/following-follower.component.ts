import {Component, Input, OnInit} from '@angular/core';
import {UserInfoItem} from "../../class/user-info-item";
import {switchMap} from "rxjs/operators";
import {ActivatedRoute, ParamMap} from "@angular/router";
import {UserService} from "../../service/user.service";

@Component({
  selector: 'app-following-follower',
  templateUrl: './following-follower.component.html',
  styleUrls: ['./following-follower.component.css']
})
export class FollowingFollowerComponent implements OnInit {
  pageType: string;
  users: UserInfoItem[];
  private userInfo: UserInfoItem;
  private page: number;
  private username: string;
  empty: boolean;

  constructor(
    private route: ActivatedRoute,
    private userService: UserService
  ) {
    this.page = 1;
    this.empty = false;
  }

  ngOnInit() {
    this.route.data.subscribe(
      data => {
        this.pageType = data.pageType;
      }
    );

    this.route.parent.paramMap.pipe(
      switchMap((params: ParamMap) => {
        this.username = params.get('username');
        return [];
      })
    ).subscribe();

    this.route.paramMap.pipe(
      switchMap((params: ParamMap) => {
        this.page =  parseInt(params.get('page') || '1', 10);

        this.userService.getUser(this.username)
          .subscribe(
            response => {
              this.userInfo = response.data;
              console.debug(this.userInfo);
            }
          );
        if (this.pageType === 'following') {
          this.userService.getFollowings(this.username, this.page)
            .subscribe(
              resp => {
                this.users = resp.data;
                this.empty = (this.users.length === 0);
              }
            );
        } else if (this.pageType === 'follower') {
          this.userService.getFollowers(this.username, this.page)
            .subscribe(
              resp => {
                this.users = resp.data;
                this.empty = (this.users.length === 0);
              }
            );
        }
        return [];
      })
    ).subscribe();
  }

}
