import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { DialogModule } from "primeng/dialog";
import { DeactivateService } from "src/app/service/deactivate.service";
import { ProductManagementComponent, ConfirmDeleteDialogComponent, ConfirmExitDialogComponent } from "./product-management.component";

import { MatTableModule } from "@angular/material/table";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatSortModule } from "@angular/material/sort";
import { MatCardModule } from "@angular/material/card";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatTooltipModule } from "@angular/material/tooltip";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatNativeDateModule } from "@angular/material/core";
import {
    MatDialogModule,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle
} from "@angular/material/dialog";
import { MatInputModule } from "@angular/material/input";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatSelectModule } from "@angular/material/select";
import { MatMenuModule } from "@angular/material/menu";
import { MatSnackBarModule } from "@angular/material/snack-bar";
import { MatPaginator } from "@angular/material/paginator";
import { MatStepperModule } from '@angular/material/stepper';


import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
const routes = [
    { path: "", component: ProductManagementComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
    declarations: [ProductManagementComponent, ConfirmDeleteDialogComponent,
        ConfirmExitDialogComponent],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule,
        DialogModule,
        MatTableModule,
        MatPaginatorModule,
        MatSortModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatTooltipModule,
        MatCheckboxModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatDialogModule,
        MatDialogTitle,
        MatDialogContent,
        MatDialogActions,
        MatDialogClose,
        MatInputModule,
        MatFormFieldModule,
        MatSelectModule,
        MatMenuModule,
        MatSnackBarModule,
        MatPaginator, MatStepperModule
    ],
})
export class ProductManagementModule { }
