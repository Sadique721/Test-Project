import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { IntegrationConfigurationComponent } from "./integration-configuration.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatDialogModule } from '@angular/material/dialog';
import { MatCheckboxModule } from "@angular/material/checkbox";
import { ReactiveFormsModule } from "@angular/forms";
import { AddEditIntegrationConfigurationComponent } from "./add-edit-integration-configuration/add-edit-integration-configuration.component";

const routes = [
  { path: "", component: IntegrationConfigurationComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
  declarations: [IntegrationConfigurationComponent, AddEditIntegrationConfigurationComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes),
    SharedModule,
    MatCardModule,
    MatCardContent,
    MatPaginatorModule,
    MatDialogModule,
    MatCheckboxModule,
  ],
})
export class IntegrationConfigurationModule {}
