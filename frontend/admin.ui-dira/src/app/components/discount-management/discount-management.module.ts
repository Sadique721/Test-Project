import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { DiscountManagementComponent } from "./discount-management.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialogModule } from '@angular/material/dialog';
import { MatCheckbox } from "@angular/material/checkbox";
import { ReactiveFormsModule } from "@angular/forms";
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MAT_DATE_FORMATS, MAT_DATE_LOCALE } from '@angular/material/core';

const routes = [
    { path: "", component: DiscountManagementComponent, canDeactivate: [DeactivateService] },
];

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
    providers: [
        { provide: MAT_DATE_LOCALE, useValue: 'en-GB' },
        { provide: MAT_DATE_FORMATS, useValue: MY_DATE_FORMATS },
    ],
    declarations: [DiscountManagementComponent],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule, MatCardContent, MatCardModule, MatPaginator, MatDialogModule, MatCheckbox, ReactiveFormsModule,
        MatDatepickerModule, MatNativeDateModule, MatFormFieldModule,
    ]
})
export class DiscountManagementModule { }
