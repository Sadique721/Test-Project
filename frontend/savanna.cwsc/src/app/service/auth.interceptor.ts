import {
  HttpEvent,
  HttpHandler,
  HttpHeaders,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http'
import { Injectable } from '@angular/core'
import { Observable, throwError } from 'rxjs'
import { catchError, finalize } from 'rxjs/operators'
import { LoginService } from './login.service'
import { ActivatedRoute, Router, RouterModule } from '@angular/router'
import * as CryptoJS from "crypto-js";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { NgxSpinnerService } from 'ngx-spinner'

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  secretKey = RadiusConstants.SECRET_KEY;
      COUNT: number = 0;
  constructor(private loginService: LoginService, private router: Router,private spinner: NgxSpinnerService,) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler,
  ): Observable<HttpEvent<any>> {
    this.spinner.show();
        req ? this.COUNT++ : "";
        
    let newRequest = req
    let token = this.loginService.getToken()
    if (token != null) {
      // const headers = new HttpHeaders({
      //     'Authorization': `Bearer ${token}`,
      //     'Access-Control-Allow-Origin': '*',
      //     'Access-Control-Allow-Methods': 'GET, PUT, POST, DELETE,OPTIONS',
      //     'Content-Type': 'application/json'
      // });

      token = this.loginService.getToken();
      const payload = req.body ? JSON.stringify(req.body) : "";
      const currentMilliseconds = Date.now();
      let hash = "";
      // Compute HMAC SHA256 hash
      if (payload != "" && payload != "{}") {
        const queryString = req.urlWithParams.split("?")[1];
        const queryPart = queryString ? queryString : "";

        const combinedData = payload + queryPart + currentMilliseconds;

        const secretKeyUtf8 = CryptoJS.enc.Utf8.parse(this.secretKey);
        const wordArray = CryptoJS.HmacSHA256(combinedData, secretKeyUtf8);
        hash = CryptoJS.enc.Base64.stringify(wordArray);
      } else if (req.body instanceof FormData) {
        let formDataString = "";
        const queryString = req.urlWithParams.split("?")[1];
        const queryPart = queryString ? queryString : "";
        req.body.forEach((value: any, key: string) => {
          if (value instanceof File) return; // skip file
          const encodedValue = typeof value === "object" ? JSON.stringify(value) : value;
          formDataString += `${key}=${encodedValue}&`;
        });
        formDataString = formDataString.slice(0, -1); // remove trailing '&'
        const combinedData = formDataString + queryPart + currentMilliseconds;

        const secretKeyUtf8 = CryptoJS.enc.Utf8.parse(this.secretKey);
        const wordArray = CryptoJS.HmacSHA256(combinedData, secretKeyUtf8);
        hash = CryptoJS.enc.Base64.stringify(wordArray);
      } else if (req.urlWithParams.includes("?")) {
        const queryString = req.urlWithParams.split("?")[1];
        console.log("calll 1 queryString :::::::::: ", queryString);
        const combinedData = queryString + currentMilliseconds;

          const secretKeyUtf8 = CryptoJS.enc.Utf8.parse(this.secretKey);
          const wordArray = CryptoJS.HmacSHA256(combinedData, secretKeyUtf8);
          hash = CryptoJS.enc.Base64.stringify(wordArray);
        } else {
          const urlParts = req.url.split("/");
          const idFromUrl = urlParts[urlParts.length - 1];
          if (idFromUrl) {
            const secretKeyUtf8 = CryptoJS.enc.Utf8.parse(this.secretKey);
            const wordArray = CryptoJS.HmacSHA256(
              idFromUrl + currentMilliseconds,
              secretKeyUtf8
            );
            hash = CryptoJS.enc.Base64.stringify(wordArray);
          } else {
            console.warn("❗ Could not extract ID from URL for hashing");
          }
        }
      newRequest = newRequest.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
          requestFrom: `gui`,
          rf: "cw",
          "X-HMAC-SIGNATURE": hash,
          "X-REQUEST-MILLISEC": currentMilliseconds.toString(),
        },
      });
      //newRequest = newRequest.clone({ headers });
    } else {
      token = this.loginService.getToken();
      const payload = req.body ? JSON.stringify(req.body) : "";
      const currentMilliseconds = Date.now();
      let hash = "";
      // Compute HMAC SHA256 hash

      if (payload != "") {
     
        const queryString = req.url.split("?")[1];
        const queryPart = queryString ? queryString : "";
        const combinedData = payload + queryPart + currentMilliseconds;

        const secretKeyUtf8 = CryptoJS.enc.Utf8.parse(this.secretKey);
        const wordArray = CryptoJS.HmacSHA256(combinedData, secretKeyUtf8);
        hash = CryptoJS.enc.Base64.stringify(wordArray);
      } else if (req.url.includes("?")) {
      
        const queryString = req.url.split("?")[1];
        const combinedData = queryString + currentMilliseconds;

        const secretKeyUtf8 = CryptoJS.enc.Utf8.parse(this.secretKey);
        const wordArray = CryptoJS.HmacSHA256(combinedData, secretKeyUtf8);
        hash = CryptoJS.enc.Base64.stringify(wordArray);
      } else {
        const urlParts = req.url.split("/");
        const idFromUrl = urlParts[urlParts.length - 1];

        if (idFromUrl) {
          const secretKeyUtf8 = CryptoJS.enc.Utf8.parse(this.secretKey);
          const wordArray = CryptoJS.HmacSHA256(
            idFromUrl + currentMilliseconds,
            secretKeyUtf8
          );
          hash = CryptoJS.enc.Base64.stringify(wordArray);
        } else {
          console.warn("❗ Could not extract ID from URL for hashing");
        }
      }
      let header = {
        requestFrom: `gui`,
        "X-HMAC-SIGNATURE": hash,
        "X-REQUEST-MILLISEC": currentMilliseconds.toString()
      };
      newRequest = req.clone({
        setHeaders: header
      });
    }
    //newRequest = newRequest.clone({ setHeaders: { Authorization: `Basic YWRtaW46YWRtaW4xMjM=` } })
    return next.handle(newRequest).pipe(
      catchError((error) => {
        if (error.status) {
          let errorData
          if (error.status == 401) {
            errorData = {
              errorMessage: 'Session expired please login again!',
            }
            error.error = { ...error.error, ...errorData }
            setTimeout(() => {
              this.router.navigate(['/login'])
            }, 1000)
          }
          return throwError(error)
        }
      }),
       finalize(() => {
                this.COUNT--;
                return this.COUNT == 0 ? this.spinner.hide() : this.spinner.show();
            })
    )
  }
}
