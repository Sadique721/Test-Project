import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";

import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { BusinessVerticalManagementComponent } from "./business-vertical-management.component";
import { DeactivateService } from "src/app/service/deactivate.service";

import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialogModule } from '@angular/material/dialog';
import { MatCheckbox } from "@angular/material/checkbox";
import { ReactiveFormsModule } from "@angular/forms";

const routes = [
    { path: "", component: BusinessVerticalManagementComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
    declarations: [BusinessVerticalManagementComponent],
    imports: [CommonModule, ReactiveFormsModule, RouterModule.forChild(routes), SharedModule, MatCardContent, MatCardModule,
        MatPaginator, MatDialogModule, MatCheckbox],
})
export class BusinessVerticalManagementModule { }
