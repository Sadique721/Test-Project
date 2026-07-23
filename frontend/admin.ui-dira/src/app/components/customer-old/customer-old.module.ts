import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { SharedModule } from "src/app/shared/shared.module";
import { CustomerOldComponent } from "./customer-old.component";
import { RouterModule } from "@angular/router";
import { DeactivateService } from "src/app/service/deactivate.service";
import { CardModule } from "primeng/card";

const routes = [{ path: "", component: CustomerOldComponent, canDeactivate: [DeactivateService] }];

@NgModule({
  declarations: [CustomerOldComponent],
  // imports: [CommonModule, RouterModule, SharedModule],
  imports: [CommonModule, RouterModule.forChild(routes), SharedModule, CardModule],
  exports: [CustomerOldComponent],
  // providers: [CacheInterceptorProvider],
})
export class CustomerOldModule {}
