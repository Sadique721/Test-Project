import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { BrowserModule, Title } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ConfirmationPopoverModule } from "angular-confirmation-popover";
import { AppRoutingModule } from "./app-routing.module";
import { AppComponent } from "./app.component";
import { BlankComponent } from "./components/blank/blank.component";
import { FooterComponent } from "./components/footer/footer.component";
import { AclGernericComponentComponent } from "./components/generic-component/acl/acl-gerneric-component/acl-gerneric-component.component";
import { HomeComponent } from "./components/home/home.component";
import { MapsComponent } from "./components/maps/maps.component";
import { SharedModule } from "./shared/shared.module";
import { ConfirmationService, MessageService } from "primeng/api";
import { DeactivateService } from "./service/deactivate.service";
import { NavMasterComponent } from "./components/nav-master/nav-master.component";
import { VendorManagementComponent } from "./components/vendor-management/vendor-management.component";
import { LoginComponent } from "./components/login/login.component";
import { CacheInterceptorProvider } from "./service/cache-interceptor";
import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { InMemoryCacheProvider } from "./service/cache-in-memory";
import { FullCalendarModule } from "@fullcalendar/angular";
import { CustomerPayComponent } from "./components/customer-pay/customer-pay.component";
import { CustomerVerifyListComponent } from "./components/customer-verify-list/customer-verify-list.component";
import { ResetPasswordComponent } from "./components/reset-password/reset-password.component";
import { GoogleMapsModule } from "@angular/google-maps";
import { MaterialModule } from "./material.module";
import { NgScrollbarModule } from "ngx-scrollbar";
import { LogoContainerComponent } from "./sidebar/components/logo-container/logo-container.component";
import { AppBreadcrumbComponent } from "./shared/components/breadcrumb/breadcrumb.component";
import { ReactiveFormsModule } from "@angular/forms";
import { AppHorizontalHeaderComponent } from "./components/home/horizontal/header/header.component";
import { AppHorizontalNavItemComponent } from "./components/home/horizontal/sidebar/nav-item/nav-item.component";
import { AppHorizontalSidebarComponent } from "./components/home/horizontal/sidebar/sidebar.component";
import { RouterModule } from "@angular/router";
import { CustomizerComponent } from "./components/customizer/customizer.component";
import { HeaderComponent } from "./components/home/vertical/header/header.component";
import { AppNavItemComponent } from "./components/home/vertical/sidebar/nav-item/nav-item.component";
import { BrandingComponent } from "./components/home/vertical/sidebar/branding.component";
import { SidebarComponent } from "./components/home/vertical/sidebar/sidebar.component";
import { ToastrModule } from "ngx-toastr";
import { provideNativeDateAdapter } from "@angular/material/core";

@NgModule({
    declarations: [
        LoginComponent,
        AclGernericComponentComponent,
        AppComponent,
        BlankComponent,
        FooterComponent,
        HomeComponent,
        MapsComponent,
        SidebarComponent,
        AppNavItemComponent,
        CustomerPayComponent,
        CustomerVerifyListComponent,
        ResetPasswordComponent,
        HeaderComponent,
        LogoContainerComponent,
        BrandingComponent,
        AppBreadcrumbComponent,
        CustomizerComponent,
        AppHorizontalHeaderComponent,
        AppHorizontalNavItemComponent,
        AppHorizontalSidebarComponent,
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
            preventDuplicates: true,
        }),
    ],
    providers: [
        ConfirmationService,
        MessageService,
        DeactivateService,
        InMemoryCacheProvider,
        CacheInterceptorProvider,
        Title,
        provideHttpClient(withInterceptorsFromDi()),
        provideNativeDateAdapter(),
    ],
    bootstrap: [AppComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class AppModule { }
