import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";

import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { RouterModule, Routes } from "@angular/router";
import { LoginComponent } from "./login.component";

const routes = [{ path: "login", component: LoginComponent }];
@NgModule({
  declarations: [
    // LoginComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    RouterModule.forRoot(routes),
  ],
  exports: [RouterModule],
  bootstrap: [LoginComponent],
})
export class LoginModule {}
