import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { DeactivateService } from "src/app/service/deactivate.service";
import { BuildingManagementComponent } from "./building-management.component";
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialogModule } from "@angular/material/dialog";
import { MatCheckbox } from "@angular/material/checkbox";
import { ReactiveFormsModule } from "@angular/forms";
import { AddEditHomePassComponent } from './add-edit-home-pass/add-edit-home-pass.component';
import { SelectBuildingDialogModule } from "../common/select-building-dialog/select-building-dialog.module";
import { MatInputModule } from "@angular/material/input";

const routes = [
    { path: "", component: BuildingManagementComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
    declarations: [BuildingManagementComponent, AddEditHomePassComponent],
    imports: [CommonModule, ReactiveFormsModule, RouterModule.forChild(routes), SharedModule, MatCardContent, MatCardModule,
        MatPaginator, MatDialogModule, MatInputModule, MatCheckbox, SelectBuildingDialogModule],
})
export class BuildingManagementModule { }
