import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { VoucherBatchComponent } from "./voucher-batch.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialogModule } from '@angular/material/dialog';
import { MatCheckbox } from "@angular/material/checkbox";
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatTabsModule } from "@angular/material/tabs";
import { VoucherComponent } from "../voucher/voucher.component";
import { MatTimepickerModule } from "@angular/material/timepicker";
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule } from '@angular/material/core';


const routes = [
    { path: "", component: VoucherBatchComponent },
];

@NgModule({
    declarations: [VoucherBatchComponent],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule
        , MatTableModule
        , MatPaginatorModule, MatSortModule, MatIconModule, MatButtonModule, MatCardContent, MatCardModule, MatPaginator, MatDialogModule, MatCheckbox,
        MatSlideToggleModule, MatTabsModule, MatTimepickerModule, MatDatepickerModule, MatFormFieldModule, MatInputModule, MatNativeDateModule
    ],
})
export class VoucherBatchModule { }
