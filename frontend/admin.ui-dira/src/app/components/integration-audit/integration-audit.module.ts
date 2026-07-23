import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { IntegrationAuditComponent } from "./integration-audit.component";
import { DeactivateService } from "src/app/service/deactivate.service";

import { MatCardModule } from "@angular/material/card";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatDialogModule } from '@angular/material/dialog';
import { MatCheckboxModule } from "@angular/material/checkbox";
import { ReactiveFormsModule } from "@angular/forms";
import { MatTooltipModule } from "@angular/material/tooltip";
import { OverlayModule } from '@angular/cdk/overlay';
import { PortalModule } from '@angular/cdk/portal';

const routes = [
  {
    path: "",
    component: IntegrationAuditComponent,
    canDeactivate: [DeactivateService],
  },
];

@NgModule({
  declarations: [IntegrationAuditComponent],
  imports: [
    CommonModule,PortalModule,
    ReactiveFormsModule,OverlayModule,
    RouterModule.forChild(routes),
    SharedModule,MatTooltipModule,
    MatCardModule,
    MatPaginatorModule,
    MatDialogModule,
    MatCheckboxModule,
  ],
})
export class IntegrationAuditModule {}
