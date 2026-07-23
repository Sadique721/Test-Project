import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { StateManagementComponent } from "./state-management.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatDialogModule } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { ReactiveFormsModule } from "@angular/forms";
import { AddEditStateManagmentComponent } from "./add-edit-state-managment/add-edit-state-managment.component";

const routes = [
    { path: "", component: StateManagementComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
    declarations: [StateManagementComponent, AddEditStateManagmentComponent],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule,
        MatCardContent, MatCardModule, MatPaginator, MatDialogModule, ReactiveFormsModule
    ],
})
export class StateManagementModule { }
