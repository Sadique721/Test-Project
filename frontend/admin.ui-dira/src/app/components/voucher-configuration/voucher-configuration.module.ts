import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { VoucherConfigurationComponent } from "./voucher-configuration.component";
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
import { VoucherBatchComponent } from "../voucher-batch/voucher-batch.component";
import { VoucherComponent } from "../voucher/voucher.component";
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatInputModule } from '@angular/material/input';
import { MatTimepickerModule } from "@angular/material/timepicker";
import { MatFormFieldModule } from '@angular/material/form-field';
import { DatePipe } from '@angular/common';
import { MatAutocompleteModule } from '@angular/material/autocomplete';

const routes = [
    { path: "", component: VoucherConfigurationComponent },
];

@NgModule({
    declarations: [VoucherConfigurationComponent, VoucherBatchComponent, VoucherComponent
    ],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule,
        , MatTableModule
        , MatPaginatorModule, MatSortModule, MatIconModule, MatButtonModule, MatCardContent, MatCardModule, MatPaginator, MatDialogModule, MatCheckbox,
        MatSlideToggleModule, MatTabsModule, MatDatepickerModule, MatNativeDateModule, MatInputModule, MatFormFieldModule, MatTimepickerModule
        , DatePipe, MatAutocompleteModule],
})
export class VoucherConfigurationModule { }
