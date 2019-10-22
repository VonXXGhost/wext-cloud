import {Component, OnInit, Input} from '@angular/core';
import { TimelineService } from '../service/timeline.service';
import { TimelineItem } from '../class/timeline-item';
import {ActivatedRoute, NavigationStart, ParamMap, Router} from '@angular/router';
import { DomSanitizer } from '@angular/platform-browser';
import { MatIconRegistry } from '@angular/material';
import {switchMap} from "rxjs/operators";
import {AuthService} from "../service/auth.service";
import {UserInfoItem} from "../class/user-info-item";
import {UserService} from "../service/user.service";
import {apiUrl} from "../config/apiUrl";

@Component({
  selector: 'app-timeline-stream',
  templateUrl: './timeline-stream.component.html',
  styleUrls: ['./timeline-stream.component.css']
})
export class TimelineStreamComponent implements OnInit {
  items: TimelineItem[];
  empty: boolean;
  @Input() pageType: string;
  @Input() username?: string;
  @Input() path?: string;
  page: number;
  masterUser: UserInfoItem;
  baseAPIUrl: string;

  constructor(
    private timelineService: TimelineService,
    private route: ActivatedRoute,
    private iconRegistry: MatIconRegistry,
    private sanitizer: DomSanitizer,
    private authService: AuthService
  ) {
    iconRegistry.addSvgIcon(
      'thumb_up',
      sanitizer.bypassSecurityTrustResourceUrl('assets/icon/sharp-thumb_up.svg')
    );
    this.empty = false;
    this.baseAPIUrl = apiUrl.base;
  }

  ngOnInit() {
    // this.userService.getLoginUser().subscribe(
    //   resp => {
    //     this.masterUser = resp.data;
    //   }
    // );
    this.masterUser = this.authService.getLoginUser();

    this.route.paramMap.pipe(
      switchMap((params: ParamMap) => {
        console.debug(params);
        this.page = parseInt(params.get('page') || '1', 10);

        this.items = [];  // 清空以显示加载提示
        if (this.pageType === 'path') {
          this.loadPathWexts(this.path, this.page);
        } else if (this.pageType === 'user-profile') {
          this.loadProfileWexts(this.username, this.page);
        } else if (this.pageType === 'user-home') {
          this.loadHomeWexts(this.page);
        }
        return [];
      })
    ).subscribe();
  }

  loadPathWexts(path: string, page: number) {
    this.getByPath(path, page);
  }

  getByPath(path: string, page: number) {
    this.timelineService.getItemsOfPath(path, page)
      .subscribe(
        data => {
          console.log(data);
          this.items = data.data;
          this.empty = (data.data.length === 0);
        },
        error => alert(error)
      );
  }

  loadHomeWexts(page: number) {
    this.timelineService.getItemsOfHome(page)
      .subscribe(
        data => {
          console.log(data);
          this.items = data.data;
          this.empty = (data.data.length === 0);
        },
        error => alert(error)
      );
  }

  loadProfileWexts(username: string, page: number) {
    this.timelineService.getItemsOfUser(username, page)
      .subscribe(
        data => {
          console.log(data);
          this.items = data.data;
          this.empty = (data.data.length === 0);
        },
        error => alert(error)
      );
  }
}
