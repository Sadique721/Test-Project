import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { SharedModule } from "src/app/shared/shared.module";
import { CardModule } from "primeng/card";
import { CustomerRoutingModule } from "./customer-routing.module";
import { CustomerListComponent } from "./customer-list/customer-list.component";
import { CustomerCreateComponent } from "./customer-create/customer-create.component";
import { CustomerComponent } from "./customer.component";
import { DialogModule } from "primeng/dialog";
import { ConfirmDialogModule } from 'primeng/confirmdialog';
// const routes = [{ path: "", component: CustomerComponent, canDeactivate: [DeactivateService] }];

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
import { CustomerPlansComponent } from "./customer-plans/customer-plans.component";
// import { TicketManagementModule } from "../ticket-management/ticket-management.module";
import { CustInventoryManagementComponent } from "./cust-inventory-management/cust-inventory-management.component";
import { MatSelectModule } from "@angular/material/select";
import { MatMenuModule } from "@angular/material/menu";
import { SelectBuildingDialogModule } from "../common/select-building-dialog/select-building-dialog.module";
import { SelectorDialogModule } from "../common/selector-dialog/selector-dialog.module";
import { TicketManagementModule } from "../ticket-management/ticket-management.module";

@NgModule({
    imports: [
        CommonModule,
        CustomerRoutingModule,
        SharedModule,
        CardModule,
        TicketManagementModule,
        ConfirmDialogModule,
        DialogModule,
        MatPaginator,
        MatCardModule,
        MatCheckbox,
        MatCardContent,
        MatDatepickerModule,
        MatDialogTitle,
        MatDialogContent,
        MatSelectModule,
        MatMenuModule,
        MatDialogActions,
        MatDialogClose,
        MatButtonModule,
        MatTableModule,
        MatTimepickerModule,
        SelectBuildingDialogModule,
        MatPaginatorModule,
        MatSortModule,
        MatIconModule,
        SelectorDialogModule,
        MatTabsModule,
        MatTooltipModule,
        MatDialogModule, MatNativeDateModule,
        MatCheckboxModule, MatStepperModule, MatInputModule, MatFormFieldModule,
        SelectBuildingDialogModule
    ],
    declarations: [CustomerComponent, CustomerListComponent, CustomerCreateComponent, CustInventoryManagementComponent],
})
export class CustomerModule { }
