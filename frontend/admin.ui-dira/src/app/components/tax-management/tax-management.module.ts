import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { TaxManagementComponent } from "./tax-management.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { MaterialModule } from "src/app/material.module";

const routes = [
  { path: "", component: TaxManagementComponent, canDeactivate: [DeactivateService] }
];

@NgModule({
  declarations: [TaxManagementComponent],
  imports: [CommonModule, RouterModule.forChild(routes), SharedModule, MaterialModule]
})
export class TaxManagementModule {}
