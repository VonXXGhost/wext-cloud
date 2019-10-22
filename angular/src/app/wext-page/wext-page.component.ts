import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, ParamMap, Router} from "@angular/router";
import {switchMap} from "rxjs/operators";
import {Wext} from "../class/wext";
import {WextService} from "../service/wext.service";
import {WextComment} from "../class/wext-comment";
import {UserInfoItem} from "../class/user-info-item";
import {UserService} from "../service/user.service";
import {MatIconRegistry} from "@angular/material";
import {DomSanitizer} from "@angular/platform-browser";
import {AuthService} from "../service/auth.service";
import {apiUrl} from "../config/apiUrl";

@Component({
  selector: 'app-wext-page',
  templateUrl: './wext-page.component.html',
  styleUrls: ['./wext-page.component.css']
})
export class WextPageComponent implements OnInit {
  private wextID: string;
  private wextDetail: Wext;
  private masterUser: UserInfoItem;
  private comments: WextComment[];
  private totalPage: number;
  private likeUsers: UserInfoItem[];
  private page: number;
  private type: string;
  private loginUser?: UserInfoItem;
  private hasRepost: boolean;
  private hasLike: boolean;
  private commentContent: string;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private wextService: WextService,
    private userService: UserService,
    private authService: AuthService,
    private iconRegistry: MatIconRegistry,
    private sanitizer: DomSanitizer
  ) {
    this.page = 1;
    iconRegistry.addSvgIcon(
      'thumb_up',
      sanitizer.bypassSecurityTrustResourceUrl('assets/icon/sharp-thumb_up.svg')
    );
    this.hasRepost = false;
    this.hasLike = false;
    this.commentContent = '';
  }

  ngOnInit() {
    this.type = 'comment';
    // if (this.authService.isLogin()) {
    //   this.userService.getLoginUser().subscribe(
    //     resp => this.loginUser = resp.data
    //   );
    // }
    this.loginUser = this.authService.getLoginUser();

    this.route.paramMap.pipe(
     switchMap((params: ParamMap) => {
       this.wextID = params.get('wextID');
       this.loadWext();
       this.setPage(parseInt(params.get('page') || '1', 10));

       this.wextService.hasLike(this.wextID).subscribe(
         resp => this.hasLike = resp.data
       );
       this.wextService.hasRepost(this.wextID).subscribe(
         resp => this.hasRepost = resp.data
       );
       return [];
     })
    ).subscribe();
  }

  loadWext() {
    this.wextService.getWextDetail(this.wextID).subscribe(
      resp => {
        console.debug(resp);
        this.wextDetail = resp.data.wext;
        console.debug(this.wextDetail);
        this.wextDetail.picList = this.wextDetail.picList.map(
          path => apiUrl.base + path.substr(1)
        );
        this.masterUser = resp.data.user;
      }
    );
  }

  loadComments() {
    this.comments = [];
    this.wextService.getCommentsOf(this.wextID, this.page).subscribe(
      resp => {
        this.totalPage = resp.data['total_pages'];
        this.comments = resp.data['comments'];
        this.comments.forEach(
          comment => this.userService.getUser(comment.userId).subscribe(
            resp1 => comment.userInfo = resp1.data['info']
          )
        );
      }
    );
  }

  loadLikes() {
    this.likeUsers = [];
    this.wextService.getLikesOf(this.wextID, this.page).subscribe(
      resp => {
        this.totalPage = resp.data['total_pages'];
        const likes: Like[] = resp.data['likes'];
        likes.forEach(
          like => this.userService.getUser(like.userId).subscribe(
            resp1 => this.likeUsers.push(resp1.data['info'])
          )
        );
      }
    );
  }

  switchToComment() {
    this.type = 'comment';
    this.page = 1;
    this.loadComments();
  }

  switchToLike() {
    this.type = 'like';
    this.page = 1;
    this.loadLikes();
  }

  setPage(p: number) {
    this.page = p;
    if (this.type === 'comment') {
      this.loadComments();
    } else if (this.type === 'like') {
      this.loadLikes();
    }
  }

  doRepost() {
    this.wextService.putRepost(this.wextDetail.id).subscribe(
      resp => {
        this.hasRepost = true;
        this.loadWext();
      }
    );
  }

  doUnrepost() {
    this.wextService.deleteRepost(this.wextDetail.id).subscribe(
      resp => {
        this.hasRepost = false;
        this.loadWext();
      }
    );
  }

  doLike() {
    this.wextService.putLike(this.wextDetail.id).subscribe(
      resp => {
        this.hasLike = true;
        this.loadWext();
        this.loadLikes();
      }
    );
  }

  doUnlike() {
    this.wextService.deleteLike(this.wextDetail.id).subscribe(
      resp => {
        this.hasLike = false;
        this.loadWext();
        this.loadLikes();
      }
    );
  }

  doComment() {
    this.wextService.putComment(this.wextID, this.commentContent).subscribe(
      resp => {
        this.commentContent = '';
        this.loadWext();
        this.loadComments();
      }
    );
  }

  deleteComment(cid: number) {
    const c = confirm('确认删除？');
    if (c) {
      this.wextService.deleteComment(this.wextID, cid).subscribe(
        resp => {
          this.loadWext();
          this.loadComments();
        }
      );
    }

  }

  deleteWext() {
    const c = confirm('确认删除？');
    if (c) {
      this.wextService.deleteWext(this.wextID).subscribe(
        resp => {
          alert('成功删除');
          this.router.navigateByUrl('/home');
        }
      );
    }
  }
}

class Like {
  wextId: string;
  userId: number;
  createdTime: string;
}
