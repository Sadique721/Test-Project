import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { CustomerCafComponent } from "./customer-caf.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { DialogModule } from "primeng/dialog";
import { FormsModule } from "@angular/forms";
import { MatInputModule } from "@angular/material/input";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatStepperModule } from "@angular/material/stepper";
import { ReactiveFormsModule } from "@angular/forms";
import { MatSelectModule } from "@angular/material/select";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatNativeDateModule } from "@angular/material/core";
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from "@angular/material/dialog";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatTableModule } from "@angular/material/table";
import { MatSortModule } from "@angular/material/sort";
import { CustomerCAFHomeComponent } from "./customer-caf-home/customer-caf-home.component";
// import { WorkflowAuditDetailsModalComponent } from "../workflow-audit-details-modal/workflow-audit-details-modal.component";
import { MatChip, MatChipsModule } from "@angular/material/chips";
import { MaterialModule } from "src/app/material.module";

import { CustTicketsComponent } from "../customer/cust-tickets/cust-tickets.component";
import { CustomerPaymentComponent } from "../customer/customer-payment/customer-payment.component";
import { CustomerInvoiceComponent } from "../customer/customer-invoice/customer-invoice.component";
import { CustomerRoutingModule } from "../customer/customer-routing.module";
import { CustomerDetailsComponent } from "../customer/customer-details/customer-details.component";
import { CustomerPlansComponent } from "../customer/customer-plans/customer-plans.component";
import { CustChargeManagementComponent } from "../customer/cust-charge-management/cust-charge-management.component";
import { MatTimepickerModule } from "@angular/material/timepicker";
import { CustomerWalletComponent } from "../customer/customer-wallet/customer-wallet.component";
import { SelectBuildingDialogModule } from "../common/select-building-dialog/select-building-dialog.module";
import { SelectorDialogModule } from "../common/selector-dialog/selector-dialog.module";


const routes = [{ path: "", component: CustomerCafComponent, canDeactivate: [DeactivateService] }];

@NgModule({
    providers: [
        { provide: MAT_DIALOG_DATA, useValue: {} },
        { provide: MatDialogRef, useValue: {} }
    ]
    ,
    declarations: [CustomerCafComponent, CustomerCAFHomeComponent, CustTicketsComponent, CustomerPaymentComponent, CustomerWalletComponent, CustomerDetailsComponent, CustChargeManagementComponent],
    imports: [
        CommonModule,
        RouterModule.forChild(routes),
        SharedModule,
        SelectorDialogModule,
        DialogModule,
        FormsModule,
        MatInputModule,
        MatFormFieldModule,
        MatIconModule,
        MatButtonModule,
        MatCardModule,
        MatStepperModule,
        ReactiveFormsModule,
        MatSelectModule,
        MatCheckboxModule,
        MatDatepickerModule,
        SelectBuildingDialogModule,
        MatNativeDateModule,
        MatDialogModule,
        MatPaginatorModule,
        MatTableModule,
        MatSortModule,
        MatChipsModule,
        MaterialModule,
        MatTimepickerModule,
    ]
})
export class CustomerCafModule { }
