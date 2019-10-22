import { Component, OnInit } from '@angular/core';
import {AuthService} from "../service/auth.service";
import {Router} from "@angular/router";
import {DirectMessage} from "../class/direct-message";
import {DirectMessageService} from "../service/direct-message.service";
import {UserInfoItem} from "../class/user-info-item";
import {UserService} from "../service/user.service";

@Component({
  selector: 'app-direct-message-page',
  templateUrl: './direct-message-page.component.html',
  styleUrls: ['./direct-message-page.component.css']
})
export class DirectMessagePageComponent implements OnInit {
  private messages: DirectMessage[] | null = null;
  private dmType: DMType = DMType.receives;
  private page = 1;
  private loginUser: UserInfoItem;
  private DMTypes = DMType;

  constructor(
    private authService: AuthService,
    private dmService: DirectMessageService,
    private router: Router,
    private userService: UserService
  ) {

  }

  ngOnInit() {
    if (!this.authService.isLogin()) {
      this.router.navigateByUrl('/login');
    }
    this.loginUser = this.authService.getLoginUser();
    this.loadMsgs();
  }

  loadMsgs() {
    if (this.dmType === DMType.sends) {
      this.loadSends();
    } else if (this.dmType === DMType.receives) {
      this.loadReceives();
    }
  }

  loadReceives() {
    this.messages = null;
    this.dmService.getReceives(this.page).subscribe(
      resp => {
        console.debug(resp);
        this.messages = resp.data;
        this.loadUserInfo();
      }
    );
  }

  loadSends() {
    this.messages = null;
    this.dmService.getSends(this.page).subscribe(
      resp => {
        console.debug(resp);
        this.messages = resp.data;
        this.loadUserInfo();
      }
    );
  }

  switchToRec() {
    this.dmType = DMType.receives;
    this.page = 1;
    this.loadReceives();
  }

  switchToSends() {
    this.dmType = DMType.sends;
    this.page = 1;
    this.loadSends();
  }

  setAllRead() {
    const msgIDsToRead = this.messages.filter(msg => !msg.haveRead)
      .map(msg => msg.id);
    console.debug(msgIDsToRead);
    this.dmService.readDM(msgIDsToRead).subscribe(
      resp => {
        this.loadMsgs();
      }
    );
  }

  setRead(id: number) {
    this.dmService.readDM([id]).subscribe(
      resp => {
        this.loadMsgs();
      }
    );
  }

  loadUserInfo() {
    this.messages = this.messages.map(
      msg => {
        if (msg.userIdFrom === this.loginUser.id) {
          this.userService.getUser(msg.userIdTo).subscribe(
            resp => {
              msg.userInfoTo = resp.data.info;
            }
          );
        } else if (msg.userIdTo === this.loginUser.id) {
          this.userService.getUser(msg.userIdFrom).subscribe(
            resp => {
              msg.userInfoFrom = resp.data.info;
            }
          );
        }
        return msg;
      }
    );
  }

  delMsg(id: number) {
    this.dmService.deleteDM(id).subscribe(
      resp => {
        alert('删除成功');
        this.loadMsgs();
      }
    );
  }

  changePage(page: number) {
    this.page = page;
    this.loadMsgs();
  }
}

enum DMType {
  sends,
  receives
}
