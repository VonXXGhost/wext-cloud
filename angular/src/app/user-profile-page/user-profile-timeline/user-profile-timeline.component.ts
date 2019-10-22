import { Component, OnInit } from '@angular/core';
import {switchMap} from "rxjs/operators";
import {ActivatedRoute, ParamMap} from "@angular/router";
import {UserService} from "../../service/user.service";
import {UserInfoWithRelation} from "../../class/user-info-with-relation";

@Component({
  selector: 'app-user-profile-timeline',
  templateUrl: './user-profile-timeline.component.html',
  styleUrls: ['./user-profile-timeline.component.css']
})
export class UserProfileTimelineComponent implements OnInit {
  private username: string;

  constructor(
    private route: ActivatedRoute,
    private userService: UserService
  ) {}

  ngOnInit() {
    this.route.paramMap.pipe(
      switchMap((params: ParamMap) => {
        this.username = params.get('username');
        return [];
      })
    ).subscribe();
  }

}
