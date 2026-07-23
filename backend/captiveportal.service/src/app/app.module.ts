import { BrowserModule, Title } from "@angular/platform-browser";
import { AppRoutingModule } from "./app-routing.module";
import { AppComponent } from "./app.component";
import { HttpClientModule, HTTP_INTERCEPTORS } from "@angular/common/http";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ToastModule } from "primeng/toast";
import { MessageService } from "primeng/api";
import { DropdownModule } from "primeng/dropdown";
import { CheckboxModule } from "primeng/checkbox";
import { TabViewModule } from "primeng/tabview";
import { NgxSpinnerModule } from "ngx-spinner";
import { FocusTrapModule } from "primeng/focustrap";
import { ConfirmDialogModule } from "primeng/confirmdialog";
import { MultiSelectModule } from "primeng/multiselect";
import { MessagesModule } from "primeng/messages";
import { ConfirmationService } from "primeng/api";
import { LoginComponent } from "./components/login/login.component";
import { LogoutComponent } from "./components/logout/logout.component";
import { AuthInterceptor } from "./service/auth.interceptor";
import { DashboardComponent } from "./components/dashboard/dashboard.component";
import { DialogModule } from "primeng/dialog";
import { HashLocationStrategy, LocationStrategy } from "@angular/common";
import { AvatarModule } from "primeng/avatar";
//import { FlutterwaveModule } from "flutterwave-angular-v3";
import { CardModule } from "primeng/card";
import { RatingModule } from "primeng/rating";
import { NgModule } from "@angular/core";
import { ProgressBarModule } from "primeng/progressbar";
import { NgxQRCodeModule } from "ngx-qrcode2";
import { TwitterComponent } from './components/twitter/twitter.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    DashboardComponent,
    LogoutComponent,
    TwitterComponent,
  ],
  imports: [
    BrowserAnimationsModule,
    DialogModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserModule,
    AppRoutingModule,
    MultiSelectModule,
    TabViewModule,
    FocusTrapModule,
    ConfirmDialogModule,
    MessagesModule,
    ToastModule,
    DropdownModule,
    CheckboxModule,
    NgxSpinnerModule,
    AvatarModule,
   // FlutterwaveModule,
    CardModule,
    RatingModule,
    ProgressBarModule,
    NgxQRCodeModule,
  ],
  providers: [
    MessageService,
    Title,
    [{ provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }],
    { provide: LocationStrategy, useClass: HashLocationStrategy },
    ConfirmationService,
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
