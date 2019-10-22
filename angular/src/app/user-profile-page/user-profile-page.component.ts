import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, NavigationStart, ParamMap, Router} from "@angular/router";
import {switchMap} from "rxjs/operators";
import {UserInfoItem} from "../class/user-info-item";
import {UserService} from "../service/user.service";
import {UserInfoWithRelation} from '../class/user-info-with-relation';
import {ContentRequest, DirectMessageService} from "../service/direct-message.service";
import {AuthService} from "../service/auth.service";

@Component({
  selector: 'app-user-profile-page',
  templateUrl: './user-profile-page.component.html',
  styleUrls: ['./user-profile-page.component.css']
})
export class UserProfilePageComponent implements OnInit {
  private username: string;
  private page = 1;
  private userInfo: UserInfoWithRelation;
  private msgContent = '';

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private userService: UserService,
    private dmService: DirectMessageService,
    private authService: AuthService
  ) {}

  ngOnInit() {

    this.route.paramMap.pipe(
      switchMap((params: ParamMap) => {
        this.username = params.get('username');
        this.userService.getUser(this.username)
          .subscribe(
            response => {
              this.userInfo = response.data;
              console.debug(this.userInfo);
            }
          );
        return [];
      })
    ).subscribe();
  }

  sendDM() {
    this.dmService.putNewDM({id: this.userInfo.info.id, content: this.msgContent} as ContentRequest).subscribe(
      resp => {
        this.msgContent = '';
        alert('成功发送');
      }
    );
  }
}
