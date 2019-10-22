import {Injectable} from "@angular/core";
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpSentEvent,
  HttpHeaderResponse,
  HttpProgressEvent, HttpResponse, HttpUserEvent, HttpEvent
} from "@angular/common/http";
import {Observable} from "rxjs";
import {tap} from "rxjs/operators";

@Injectable()
export class JWTInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler):
    Observable<HttpEvent<any>> {
    const token$ = localStorage.getItem('jwtToken');
    let nreq;

    if (token$) {
      nreq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token$}`,
          'Access-Control-Allow-Origin': '*'
        }
      });
    } else {
      nreq = req.clone({
        setHeaders: {
          'Access-Control-Allow-Origin': '*'
        }
      });
    }

    return next
      .handle(nreq)
      .pipe(
        tap(
          event => {},
          error => {
            console.debug(error);
          }
        )
      );
  }
}
