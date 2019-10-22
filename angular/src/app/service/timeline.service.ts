import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpParams} from '@angular/common/http';
import { apiUrl} from '../config/apiUrl';
import {catchError, retry} from 'rxjs/operators';
import {throwError} from 'rxjs';
import { HttpHeaders } from '@angular/common/http';
import { BaseResponse } from "../class/base-response";
import {Handler} from "./handle-error";
import {parseArguments} from "@angular/cli/models/parser";

@Injectable({
  providedIn: 'root'
})
export class TimelineService {
  apiUrl = apiUrl;

  constructor(
    private http: HttpClient,
  ) { }

  getItemsOfPath(path: string, page: number) {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      }),
      params: new HttpParams().set('page', page.toString())
    };
    return this.http.get<BaseResponse>(
      this.apiUrl.pathWext + path,
      httpOptions
    ).pipe(
      // retry(2),
      catchError(Handler.handleError)
    );
  }

  getItemsOfHome(page: number) {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      }),
      params: new HttpParams().set('page', page.toString())
    };
    return this.http.get<BaseResponse>(
      this.apiUrl.homeFeed,
      httpOptions
    ).pipe(
      // retry(2),
      catchError(Handler.handleError)
    );
  }

  getItemsOfUser(username, page: number) {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      }),
      params: new HttpParams().set('page', page.toString())
    };
    return this.http.get<BaseResponse>(
      this.apiUrl.userProfileFeed(username),
      httpOptions
    ).pipe(
      // retry(2),
      catchError(Handler.handleError)
    );
  }

}
