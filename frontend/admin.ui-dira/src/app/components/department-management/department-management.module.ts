import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { DepartmentManagementComponent } from "./department-management.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialogModule } from "@angular/material/dialog";
import { ReactiveFormsModule } from "@angular/forms";
import { AddEditDepartmentManagmentComponent } from "./add-edit-department-managment/add-edit-department-managment.component";

const routes = [
    { path: "", component: DepartmentManagementComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
    declarations: [DepartmentManagementComponent, AddEditDepartmentManagmentComponent],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule,
        MatCardContent, MatCardModule, MatPaginator, MatDialogModule, ReactiveFormsModule
    ],
})
export class DepartmentManagementModule { }
