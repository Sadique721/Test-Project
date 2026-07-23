import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { InvoiceSearchPaymentComponent } from "./invoice-search-payment.component";
import { DialogModule } from "primeng/dialog";
import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatRadioModule } from '@angular/material/radio';
import { MatListModule } from '@angular/material/list';
import { MaterialModule } from "src/app/material.module";
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';

const routes = [{ path: "", component: InvoiceSearchPaymentComponent }];

@NgModule({
    declarations: [InvoiceSearchPaymentComponent],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule, ReactiveFormsModule,
        MatCardModule,
        MatTableModule,
        MatSortModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule,
        MatButtonModule,
        MatPaginatorModule,
        MatRadioModule,
        MatListModule, MaterialModule, FormsModule, MatDatepickerModule, MatNativeDateModule],
})
export class InvoiceSearchPaymentModule { }
