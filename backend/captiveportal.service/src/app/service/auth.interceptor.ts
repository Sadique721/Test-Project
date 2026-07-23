import {
  HttpEvent,
  HttpHandler,
  HttpHeaders,
  HttpInterceptor,
  HttpRequest,
} from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { LoginService } from "./login.service";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private loginService: LoginService) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    let newRequest = req;
    let token = this.loginService.getToken();
    if (token != null) {
      // const headers = new HttpHeaders({
      //     'Authorization': `Bearer ${token}`,
      //     'Access-Control-Allow-Origin': '*',
      //     'Access-Control-Allow-Methods': 'GET, PUT, POST, DELETE,OPTIONS',
      //     'Content-Type': 'application/json'
      // });

      newRequest = newRequest.clone({
        setHeaders: { Authorization: `Bearer ${token}`, requestFrom: `gui` },
      });
      //newRequest = newRequest.clone({ headers });
    }
    //newRequest = newRequest.clone({ setHeaders: { Authorization: `Basic YWRtaW46YWRtaW4xMjM=` } })
    return next.handle(newRequest);
  }
}
