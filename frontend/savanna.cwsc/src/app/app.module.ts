import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from "@angular/core";
import { BrowserModule, Title } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ConfirmationPopoverModule } from "angular-confirmation-popover";
import { AppRoutingModule } from "./app-routing.module";
import { AppComponent } from "./app.component";
// import { BlankComponent } from "./components/blank/blank.component";
// import { FooterComponent } from "./components/footer/footer.component";
// import { AclGernericComponentComponent } from "./components/generic-component/acl/acl-gerneric-component/acl-gerneric-component.component";
// import { MapsComponent } from "./components/maps/maps.component";
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { FullCalendarModule } from "@fullcalendar/angular";
import { ConfirmationService, MessageService } from "primeng/api";
import { SharedModule } from "./shared/shared.module";
// import { CustomerPayComponent } from "./components/customer-pay/customer-pay.component";
// import { CustomerVerifyListComponent } from "./components/customer-verify-list/customer-verify-list.component";
// import { ResetPasswordComponent } from "./components/reset-password/reset-password.component";
import { HashLocationStrategy, LocationStrategy } from "@angular/common";
import { ReactiveFormsModule } from "@angular/forms";
import { GoogleMapsModule } from "@angular/google-maps";
import { provideNativeDateAdapter } from "@angular/material/core";
import { RouterModule } from "@angular/router";
import { NgScrollbarModule } from "ngx-scrollbar";
import { ToastrModule } from "ngx-toastr";
import { AuthguardGuard } from "./authguard.guard";
import { MaterialModule } from "./material.module";
import { AuthInterceptor } from "./service/auth.interceptor";

@NgModule({
  declarations: [
    // LoginComponent,
    // AclGernericComponentComponent,
    AppComponent,
    // BlankComponent,
    // FooterComponent,
    // HomeComponent,
    // MapsComponent,
    // SidebarComponent,
    // AppNavItemComponent,
    // CustomerPayComponent,
    // CustomerVerifyListComponent,
    // ResetPasswordComponent,
    // HeaderComponent,
    // LogoContainerComponent,
    // BrandingComponent,
    // AppBreadcrumbComponent,
    // CustomizerComponent,
    // AppHorizontalHeaderComponent,
    // AppHorizontalNavItemComponent,
    // AppHorizontalSidebarComponent,

  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    MaterialModule,
    AppRoutingModule,
    NgScrollbarModule,
    ReactiveFormsModule,
    SharedModule,
    ConfirmationPopoverModule.forRoot({ confirmButtonType: "danger" }),
    GoogleMapsModule,
    FullCalendarModule,
    RouterModule,
    ToastrModule.forRoot({
      timeOut: 3000,
      positionClass: "toast-top-right",
      preventDuplicates: true
    })
  ],
  providers: [
    AuthguardGuard,
    [{ provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }],
    { provide: LocationStrategy, useClass: HashLocationStrategy },
    ConfirmationService,
    MessageService,
    Title,
    provideHttpClient(withInterceptorsFromDi()),
    provideNativeDateAdapter()
  ],
  bootstrap: [AppComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AppModule {}
