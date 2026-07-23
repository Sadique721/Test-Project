import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { DeactivateService } from "src/app/service/deactivate.service";
import { SubAreaManagementComponent } from "./subArea-management.component";
import { AddEditSubareaManagementComponent } from "./add-edit-subarea-management/add-edit-subarea-management.component";

import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatDialogModule } from '@angular/material/dialog';
import { MatCheckboxModule } from "@angular/material/checkbox";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { MatSortModule } from "@angular/material/sort";
import { MatTableModule } from "@angular/material/table";


const routes = [
  { path: "", component: SubAreaManagementComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
  declarations: [SubAreaManagementComponent, AddEditSubareaManagementComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes),
    SharedModule,
    MatCardContent,
    MatCardModule,
    MatPaginatorModule,
    MatDialogModule,
    MatCheckboxModule,
     CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatTableModule,
    MatSortModule,
    MatButtonModule,
  ],
})
export class SubAreaManagementModule { }
