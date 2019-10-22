import {HttpErrorResponse} from "@angular/common/http";
import {throwError} from "rxjs";

export class Handler {
  public static handleError(error: HttpErrorResponse) {
    if (error.error instanceof ErrorEvent) {
      // A client-side or network error occurred. Handle it accordingly.
      console.error('An error occurred:', error.error.message);
    } else {
      console.error(
        `Backend returned code ${error.status}, ` +
        `body was: ${JSON.stringify(error.error)}`);
      if (error.status === 403) {
        console.info('无效登录');
      }
    }
    return throwError(
      error.error['msg'] || error.error.message || '未知错误'
    );
  }
}
