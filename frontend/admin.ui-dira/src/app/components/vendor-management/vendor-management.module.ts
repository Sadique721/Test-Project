import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { VendorManagementComponent } from "./vendor-management.component";
import { RouterModule } from "@angular/router";
import { DeactivateService } from "src/app/service/deactivate.service";
import { SharedModule } from "src/app/shared/shared.module";

import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialogModule } from '@angular/material/dialog';
import { MatCheckbox } from "@angular/material/checkbox";
import { ReactiveFormsModule } from "@angular/forms";

const routes = [{ path: "", component: VendorManagementComponent }];

@NgModule({
  declarations: [VendorManagementComponent],
  imports: [CommonModule, ReactiveFormsModule, RouterModule.forChild(routes), SharedModule, MatCardContent, MatCardModule,
          MatPaginator, MatDialogModule, MatCheckbox],
})
export class VendorManagementModule {}
