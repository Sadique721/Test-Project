import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { CustomerDocumentsComponent } from "./customer-documents.component";
import { RadioButtonModule } from "primeng/radiobutton";
import { DialogModule } from "primeng/dialog";
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatDialogModule } from '@angular/material/dialog';
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatFormFieldModule } from '@angular/material/form-field';
import { MAT_DATE_FORMATS, MAT_DATE_LOCALE } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';

const routes = [{ path: "", component: CustomerDocumentsComponent }];
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
    declarations: [CustomerDocumentsComponent],
    imports: [
        MatNativeDateModule,
        MatDatepickerModule,
        MatFormFieldModule,
        MatCardContent, MatCardModule,
        MatPaginatorModule,
        MatDialogModule,
        MatCheckboxModule,
        CommonModule,
        DialogModule,
        MatDialogModule,
        // RadioButtonModule,
        RouterModule.forChild(routes),
        SharedModule,
    ],
})
export class CustomerDocumentsModule { }
