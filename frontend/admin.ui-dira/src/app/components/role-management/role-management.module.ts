import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { RoleManagementComponent } from "./role-management.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { CreateRoleComponent } from "./create-role/create-role.component";

import { MatCardModule } from "@angular/material/card";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatDialogModule } from '@angular/material/dialog';
import { MatCheckboxModule } from "@angular/material/checkbox";
import { ReactiveFormsModule } from "@angular/forms";
import { MatSelectModule } from "@angular/material/select";
import { MatTableModule } from "@angular/material/table";
import { MatIconModule } from "@angular/material/icon";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { MatTreeModule } from "@angular/material/tree";

const routes = [
  { path: "", component: RoleManagementComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
  declarations: [
    RoleManagementComponent,
    CreateRoleComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes),
    SharedModule,
    MatCardModule,
    MatPaginatorModule,
    MatDialogModule,
    MatCheckboxModule,
    MatTableModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatTreeModule,
    MatSelectModule,
    MatButtonModule,
  ],
})
export class RoleManagementModule {}
