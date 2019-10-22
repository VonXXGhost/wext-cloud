import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, NavigationStart, ParamMap, Router} from "@angular/router";
import {switchMap} from "rxjs/operators";
import {WextService} from "../service/wext.service";

@Component({
  selector: 'app-path-page',
  templateUrl: './path-page.component.html',
  styleUrls: ['./path-page.component.css']
})
export class PathPageComponent implements OnInit {
  private path: string;
  private page: number;
  private wextContent: string;

  constructor(
    private router: Router,
    private wextService: WextService
  ) {
    this.path = router.url.substring(5, router.url.indexOf(';') > 0 ? router.url.indexOf(';') : router.url.length) || '';
    this.page = 1;
    this.wextContent = '';
  }

  ngOnInit() {
    this.router.events.subscribe((event: any) => {
      if (event instanceof NavigationStart
        && event.url.startsWith('/path')) {
        this.path = event.url.substring(5, event.url.indexOf(';') > 0 ? event.url.indexOf(';') : event.url.length);
      }
    });
  }

  doWext() {
    const body = {
      content: this.wextContent,
      picURLs: null,
      path: this.path
    } as WextNewRequest;
    this.wextService.putNewWext(body).subscribe(
      resp => {
        this.wextContent = '';
        alert('发布成功');
        location.reload();
      }
    );
  }

}
