import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { PlanGroupComponent } from "./plan-group.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { TableModule } from "primeng/table";
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator } from "@angular/material/paginator";
import { MatCheckbox } from "@angular/material/checkbox";
import { ReactiveFormsModule } from "@angular/forms";
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MAT_DATE_FORMATS, MAT_DATE_LOCALE } from '@angular/material/core';
import { WorkflowAuditDetailsModalComponent } from "../workflow-audit-details-modal/workflow-audit-details-modal.component";

const routes = [{ path: "", component: PlanGroupComponent, canDeactivate: [DeactivateService] }];
export const MY_DATE_FORMATS = {
    parse: {
        dateInput: 'DD-MM-YYYY',
    },
    display: {
        dateInput: 'dd-MM-yyyy',
        monthYearLabel: 'MMM YYYY',
        dateA11yLabel: 'LL',
        monthYearA11yLabel: 'MMMM YYYY',
    },
};

@NgModule({
    declarations: [PlanGroupComponent],
    imports: [CommonModule, TableModule, RouterModule.forChild(routes), SharedModule,
        MatCardContent, MatCardModule, MatButtonModule, MatIconModule, MatPaginatorModule, MatPaginator,
        MatDialogModule, MatCheckbox, ReactiveFormsModule, MatSortModule, MatTableModule, MatDatepickerModule,
        MatNativeDateModule, MatFormFieldModule,
    ],
    providers: [
        {
            provide: MAT_DIALOG_DATA,
            useValue: {},
        },
        {
            provide: MatDialogRef,
            useValue: {},
        },
    ]
})
export class PlanGroupModule { }
