import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {BaseResponse} from "../class/base-response";
import {apiUrl} from "../config/apiUrl";
import {catchError} from "rxjs/operators";
import {Handler} from "./handle-error";

@Injectable({
  providedIn: 'root'
})
export class DirectMessageService {

  constructor(
    private http: HttpClient
  ) { }

  getDetail(id: number) {
    return this.http.get<BaseResponse>(apiUrl.dmDetail(id))
      .pipe(
        catchError(Handler.handleError)
      );
  }

  deleteDM(id: number) {
    return this.http.delete<BaseResponse>(apiUrl.dmDetail(id))
      .pipe(
        catchError(Handler.handleError)
      );
  }

  putNewDM(request: ContentRequest) {
    return this.http.put<BaseResponse>(apiUrl.dmNewPost, request)
      .pipe(
        catchError(Handler.handleError)
      );
  }

  getReceives(page: number) {
    const httpOptions = {
      params: new HttpParams().set('page', page.toString())
    };
    return this.http.get<BaseResponse>(apiUrl.dmReceives, httpOptions)
      .pipe(
        catchError(Handler.handleError)
      );
  }

  getSends(page: number) {
    const httpOptions = {
      params: new HttpParams().set('page', page.toString())
    };
    return this.http.get<BaseResponse>(apiUrl.dmSends, httpOptions)
      .pipe(
        catchError(Handler.handleError)
      );
  }

  readDM(ids: number[]) {
    return this.http.post<BaseResponse>(apiUrl.dmRead, {list: ids})
      .pipe(
        catchError(Handler.handleError)
      );
  }
}

export class ContentRequest {
  id?: number;
  screenName?: string;
  content?: string;
}
