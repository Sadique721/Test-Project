import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";

import { ExternalItemManagementRoutingModule } from "./external-item-management-routing.module";
import { ExternalItemManagementComponent } from "./external-item-management/external-item-management.component";
import { SharedModule } from "src/app/shared/shared.module";
import { DialogModule } from "primeng/dialog";
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialogModule } from "@angular/material/dialog";
import { MatCheckbox } from "@angular/material/checkbox";
import { MatRadioButton } from "@angular/material/radio";
@NgModule({
  declarations: [ExternalItemManagementComponent],
  imports: [MatCardContent, MatCardModule,
    MatPaginator, MatDialogModule, MatCheckbox, CommonModule, ExternalItemManagementRoutingModule, SharedModule, DialogModule, MatRadioButton],
})
export class ExternalItemManagementModule {}
