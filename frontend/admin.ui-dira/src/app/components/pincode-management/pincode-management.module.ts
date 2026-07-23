import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { PincodeManagementComponent } from "./pincode-management.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialogModule } from "@angular/material/dialog";
import { ReactiveFormsModule } from "@angular/forms";
import { AddEditPincodeManagmentComponent } from "./add-edit-pincode-managment/add-edit-pincode-managment.component";

const routes = [
    { path: "", component: PincodeManagementComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
    declarations: [PincodeManagementComponent, AddEditPincodeManagmentComponent],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule, MatCardContent, MatCardModule,
        MatPaginator, MatDialogModule, ReactiveFormsModule
    ],
})
export class PincodeManagementModule { }
