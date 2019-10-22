import { Component, OnInit } from '@angular/core';
import { AuthService, AuthenticationRequest } from "../../service/auth.service";
import { BaseResponse } from "../../class/base-response";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  errorMsg: string;
  request: AuthenticationRequest;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    this.request = new AuthenticationRequest();
  }

  ngOnInit() {}
  login() {
    this.authService.sendLogin(this.request)
      .subscribe(
      resp => {
        console.log(JSON.stringify(resp));
        this.authService.saveToken(resp.data['token']);
        this.router.navigateByUrl('home');
      },
        error => {
        console.error(error);
        this.errorMsg = error || '未知错误';
        }
    );
  }

}
