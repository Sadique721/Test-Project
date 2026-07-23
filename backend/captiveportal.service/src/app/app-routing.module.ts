import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { LoginComponent } from "src/app/components/login/login.component";
import { DashboardComponent } from "src/app/components/dashboard/dashboard.component";
import { LogoutComponent } from "./components/logout/logout.component";
import { TwitterComponent } from "./components/twitter/twitter.component";
const routes: Routes = [
  {
    path: "",
    pathMatch: "full",
    component: LoginComponent
  },
  {
    path: "portal",
    component: LoginComponent,
  },
  {
    path: "home",
    component: DashboardComponent,
  },

  {
    path: "logout",
    component: LogoutComponent,
  },
  {
    path: "twitter",
    component: TwitterComponent,
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
