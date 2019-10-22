import { Injectable } from '@angular/core';
import {BaseResponse} from "../class/base-response";
import {apiUrl} from "../config/apiUrl";
import {catchError} from "rxjs/operators";
import {Handler} from "./handle-error";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class WextService {

  constructor(
    private http: HttpClient
  ) { }

  getWextDetail(wextID: string) {
    return this.http.get<BaseResponse>(apiUrl.wextDetail(wextID))
      .pipe(
        catchError(Handler.handleError)
      );
  }

  getCommentsOf(wextID: string, page: number) {
    const httpOptions = {
      params: new HttpParams().set('page', page.toString())
    };
    return this.http.get<BaseResponse>(apiUrl.wextComments(wextID), httpOptions)
      .pipe(
        catchError(Handler.handleError)
      );
  }

  getLikesOf(wextID: string, page: number) {
    const httpOptions = {
      params: new HttpParams().set('page', page.toString())
    };
    return this.http.get<BaseResponse>(apiUrl.wextLikes(wextID), httpOptions)
      .pipe(
        catchError(Handler.handleError)
      );
  }

  putNewWext(wext: WextNewRequest) {
    return this.http.put<BaseResponse>(apiUrl.wextPut, wext)
      .pipe(
        catchError(Handler.handleError)
      );
  }

  deleteWext(wextID: string) {
    return this.http.delete<BaseResponse>(apiUrl.wextDetail(wextID))
      .pipe(
        catchError(Handler.handleError)
      );
  }

  putRepost(wextID: string) {
    return this.http.put<BaseResponse>(apiUrl.wextRepost(wextID), null)
      .pipe(
        catchError(Handler.handleError)
      );
  }

  deleteRepost(wextID: string) {
    return this.http.delete<BaseResponse>(apiUrl.wextRepost(wextID))
      .pipe(
        catchError(Handler.handleError)
      );
  }

  hasRepost(wextID: string) {
    return this.http.get<BaseResponse>(apiUrl.wextHasRepost(wextID))
      .pipe(
        catchError(Handler.handleError)
      );
  }

  putComment(wextID: string, content: string) {
    return this.http.put<BaseResponse>(apiUrl.wextCommentOp(wextID), {content: content})
      .pipe(
        catchError(Handler.handleError)
      );
  }

  deleteComment(wextID: string, commentID: number) {
    return this.http.delete<BaseResponse>(apiUrl.wextCommentOp(wextID, commentID))
      .pipe(
        catchError(Handler.handleError)
      );
  }

  putLike(wextID: string) {
    return this.http.put<BaseResponse>(apiUrl.wextLike(wextID), null)
      .pipe(
        catchError(Handler.handleError)
      );
  }

  deleteLike(wextID: string) {
    return this.http.delete<BaseResponse>(apiUrl.wextLike(wextID))
      .pipe(
        catchError(Handler.handleError)
      );
  }

  hasLike(wextID: string) {
    return this.http.get<BaseResponse>(apiUrl.wextHasLike(wextID))
      .pipe(
        catchError(Handler.handleError)
      );
  }

  uploadPic(file: File) {
    // const httpOptions = {
    //   mimeType: 'multipart/form-data'
    // };
    let formData = new FormData();
    formData.append('file', file, file.name);
    return this.http.post<BaseResponse>(apiUrl.uploadPic, formData)
      .pipe(
        catchError(Handler.handleError)
      );
  }
}
