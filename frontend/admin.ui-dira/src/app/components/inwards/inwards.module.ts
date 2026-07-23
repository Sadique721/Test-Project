import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { InwardsComponent } from "./inwards.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { DialogModule } from "primeng/dialog";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";


import { MatTableModule } from "@angular/material/table";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatSortModule } from "@angular/material/sort";
import { MatCardModule } from "@angular/material/card";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatTabsModule } from "@angular/material/tabs";
import { MatTooltipModule } from "@angular/material/tooltip";
import { MatCardContent } from "@angular/material/card";
import { MatCheckboxModule } from "@angular/material/checkbox";

import { MatPaginator } from "@angular/material/paginator";
import { MatCheckbox } from "@angular/material/checkbox";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatNativeDateModule, provideNativeDateAdapter } from "@angular/material/core";
import { MatTimepickerModule } from "@angular/material/timepicker";
import {
    MatDialog,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogModule,
    MatDialogTitle
} from "@angular/material/dialog";
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatStepperModule } from '@angular/material/stepper';
const routes = [{ path: "", component: InwardsComponent, canDeactivate: [DeactivateService] }];

@NgModule({
    declarations: [InwardsComponent],
    imports: [
        CommonModule,
        ReactiveFormsModule,
        DialogModule,
        FormsModule,
        RouterModule.forChild(routes),
        SharedModule, MatPaginator,
        MatCardModule,
        MatCheckbox,
        MatCardContent,
        MatDatepickerModule,
        MatDialogTitle,
        MatDialogContent,
        MatDialogActions,
        MatDialogClose,
        MatButtonModule,
        MatTableModule,
        MatTimepickerModule,
        MatPaginatorModule,
        MatSortModule,
        MatIconModule,
        MatTabsModule,
        MatTooltipModule,
        MatDialogModule, MatNativeDateModule,
        MatCheckboxModule, MatStepperModule, MatInputModule, MatFormFieldModule

    ],
})
export class InwardsModule { }
