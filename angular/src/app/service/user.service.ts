import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams} from "@angular/common/http";
import { apiUrl} from '../config/apiUrl';
import {BaseResponse} from "../class/base-response";
import {catchError} from "rxjs/operators";
import {Handler} from "./handle-error";
import {AuthService} from "./auth.service";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(
    private http: HttpClient,
    private auth: AuthService
  ) { }

  getLoginUser() {
    return this.http.get<BaseResponse>(apiUrl.selfInfo)
      .pipe(
        catchError(Handler.handleError)
      );
  }

  getUser(username: string | number) {
    return this.http.get<BaseResponse>(apiUrl.userInfo(username))
      .pipe(
        catchError(Handler.handleError)
      );
  }

  putNewFollow(username: string) {
    return this.http.put(apiUrl.userFollow(username), null)
      .pipe(
        catchError(Handler.handleError)
      );
  }

  deleteFollow(username: string) {
    return this.http.delete(apiUrl.userFollow(username))
      .pipe(
        catchError(Handler.handleError)
      );
  }

  getFollowings(username: string, page: number) {
    const httpOptions = {
      params: new HttpParams().set('page', page.toString())
    };
    return this.http.get<BaseResponse>(apiUrl.userFollowing(username), httpOptions)
      .pipe(
        catchError(Handler.handleError)
      );
  }

  getFollowers(username: string, page: number) {
    const httpOptions = {
      params: new HttpParams().set('page', page.toString())
    };
    return this.http.get<BaseResponse>(apiUrl.userFollowers(username), httpOptions)
      .pipe(
        catchError(Handler.handleError)
      );
  }

  getLikes(username: string, page: number) {
    const httpOptions = {
      params: new HttpParams().set('page', page.toString())
    };
    return this.http.get<BaseResponse>(apiUrl.userLike(username), httpOptions)
      .pipe(
        catchError(Handler.handleError)
      );
  }
}
