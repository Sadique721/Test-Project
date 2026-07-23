import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { DeactivateService } from "src/app/service/deactivate.service";
import { BuidingConfigManagement } from "./building-config-management.component";
import { MatCardModule } from "@angular/material/card";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatDialogModule } from '@angular/material/dialog';
import { MatCheckboxModule } from "@angular/material/checkbox";
import { ReactiveFormsModule } from "@angular/forms";
import { AddEditBuildingConfigManagementComponent } from "./add-edit-building-config-management/add-edit-building-config-management.component";


const routes = [
  { path: "", component: BuidingConfigManagement, canDeactivate: [DeactivateService] },
];


@NgModule({
  declarations: [BuidingConfigManagement, AddEditBuildingConfigManagementComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes),
    SharedModule,
    MatCardModule,
    MatPaginatorModule,
    MatDialogModule,
    MatCheckboxModule,
  ],
})
export class BuildingConfigManagementModule { }
