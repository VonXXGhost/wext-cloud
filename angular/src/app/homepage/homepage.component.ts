import { Component, OnInit } from '@angular/core';
import {AuthService} from '../service/auth.service';
import {MatButtonModule} from '@angular/material/button';
import {Router} from "@angular/router";

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css']
})
export class HomepageComponent implements OnInit {

  constructor(
    public authService: AuthService,
    private matButtonModule: MatButtonModule,
    private route: Router
  ) { }

  ngOnInit() {
    if (this.authService.isLogin()) {
      this.route.navigateByUrl('/home');
    }
  }

}
