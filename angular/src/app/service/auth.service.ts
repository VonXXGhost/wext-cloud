import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http";
import { apiUrl } from "../config/apiUrl";
import {catchError, map, share, tap} from "rxjs/operators";
import { BaseResponse } from "../class/base-response";
import {Handler} from "./handle-error";
import {UserInfoItem} from "../class/user-info-item";
import {UserService} from "./user.service";

export class AuthenticationRequest {
  public username: string;
  public password: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  tokenKey = 'jwtToken';
  loginUserKey = 'loginUser';
  token: string | null;

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(
    private http: HttpClient
  ) {
    this.token = localStorage.getItem(this.tokenKey);
    if (!this.getLoginUser()) {
      this.saveLoginUser();
    }
  }

  isLogin() {
    return localStorage.getItem(this.tokenKey) !== null;
  }

  saveToken(t: string) {
    localStorage.setItem(this.tokenKey, t);
    this.saveLoginUser();
  }

  logOut() {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.loginUserKey);
  }

  sendLogin(request: AuthenticationRequest) {
    return this.http.post<BaseResponse>(apiUrl.login, request, this.httpOptions)
      .pipe(
        catchError(Handler.handleError)
        // tap(() => this.saveLoginUser())
      );
  }

  saveLoginUser() {
    this.http.get<BaseResponse>(apiUrl.selfInfo).subscribe(
      resp => {
        localStorage.setItem(this.loginUserKey, JSON.stringify(resp.data));
      },
      error1 => {
        localStorage.removeItem(this.loginUserKey);
      }
    );
  }

  getLoginUser(): UserInfoItem {
    return JSON.parse(localStorage.getItem(this.loginUserKey));
  }
}
