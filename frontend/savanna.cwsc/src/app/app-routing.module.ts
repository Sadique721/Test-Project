import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { AuthguardGuard } from "./authguard.guard";
import { HomeComponent } from "./components/home/home.component";
import { LoginComponent } from "./components/login/login.component";
import { CustomerDetailsComponent } from "./components/customer-details/customer-details.component";
import { TicketmanagementComponent } from "./components/ticketmanagement/ticketmanagement.component";
import { DashboardComponent } from "./components/dashboard/dashboard.component";
import { SubscriptionHistoryComponent } from "./components/subscription-history/subscription-history.component";
import { UsageHistoryComponent } from "./components/usage-history/usage-history.component";
import { ReceiptComponent } from "./components/receipt/receipt.component";

const routes: Routes = [
  { path: "", component: LoginComponent },
  {
    path: "login",
    component: LoginComponent
  },
  {
    path: "receipt",
    component: ReceiptComponent
  },
  {
    path: "home",
    loadComponent: () => import("./components/home/home.component").then(m => m.HomeComponent),
    canActivate: [AuthguardGuard],

    children: [
      {
        path: "dashboard",
        loadComponent: () =>
          import("./components/dashboard/dashboard.component").then(m => m.DashboardComponent)
      },
      {
        path: "customer",
        loadComponent: () =>
          import("./components/customer-details/customer-details.component").then(
            m => m.CustomerDetailsComponent
          )
      },
      {
        path: "ticketManagement",
        loadComponent: () =>
          import("./components/ticketmanagement/ticketmanagement.component").then(
            m => m.TicketmanagementComponent
          )
      },
      {
        path: "subscriptionHistory",
        loadComponent: () =>
          import("./components/subscription-history/subscription-history.component").then(
            m => m.SubscriptionHistoryComponent
          )
      },
      {
        path: "usageHistory",
        loadComponent: () =>
          import("./components/usage-history/usage-history.component").then(
            m => m.UsageHistoryComponent
          )
      },
      {
        path: "wallet",
        loadComponent: () =>
          import("./components/wallet/wallet.component").then(m => m.WalletComponent)
      },
      {
        path: "rating",
        loadComponent: () =>
          import("./components/rating/rating.component").then(m => m.RatingComponent)
      },
      {
        path: "changePassword",
        loadComponent: () =>
          import("./components/changePaaword/changePassword.component").then(
            m => m.ChangePasswordComponent
          )
      }
    ]
  }
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
