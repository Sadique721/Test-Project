import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { SharedModule } from "src/app/shared/shared.module";
import { RouterModule } from "@angular/router";
import { DeactivateService } from "src/app/service/deactivate.service";
import { CardModule } from "primeng/card";
import { CustomerRoutingModule } from "./cust-details-menu-routing.module";
import { CustomerDetailsComponent } from "../customer-details/customer-details.component";
import { CustomerWalletComponent } from "../customer-wallet/customer-wallet.component";
import { CustomerLedgerComponent } from "../customer-ledger/customer-ledger.component";
import { CustomerPlansComponent } from "../customer-plans/customer-plans.component";
import { CustomerInvoiceComponent } from "../customer-invoice/customer-invoice.component";
import { CustDetailsMenuComponent } from "./cust-details-menu.component";
import { CustomerPaymentComponent } from "../customer-payment/customer-payment.component";
import { CustomerChangeDiscountComponent } from "../customer-change-discount/customer-change-discount.component";
import { CustChangePlanComponent } from "../cust-change_plan/cust-change_plan.component";
import { CustomerChangeStatusComponent } from "../customer-change-status/customer-change-status.component";
import { CustInventoryManagementComponent } from "../cust-inventory-management/cust-inventory-management.component";
import { SplitterModule } from "primeng/splitter";
import { CustServiceManagementComponent } from "../cust-service-management/cust-service-management.component";
import { AddServiceComponent } from "../add-service/add-service.component";
import { AccordionModule } from "primeng/accordion";
import { CustSessionHistoryComponent } from "../cust-session-history/cust-session-history.component";
import { CustTicketsComponent } from "../cust-tickets/cust-tickets.component";
import { CustChargeManagementComponent } from "../cust-charge-management/cust-charge-management.component";
import { CustCreditNoteComponent } from "../cust-credit-note/cust-credit-note.component";
import { CustDBRReportComponent } from "../cust-dbr-report/cust-dbr-report.component";
import { CustWorkflowAuditComponent } from "../cust-workflow-audit/cust-workflow-audit.component";
import { CustAuditDetailsComponent } from "../cust-audit-details/cust-audit-details.component";
import { CustDunningManagementComponent } from "../cust-dunning-management/cust-dunning-management.component";
import { CustNotificationManagementComponent } from "../cust-notification-management/cust-notification-management.component";
import { ChildCustComponent } from "../child-cust/child-cust.component";
import { CustShiftLocationComponent } from "../cust-shift-location/cust-shift-location.component";
import { DialogModule } from "primeng/dialog";
import { StaffSelectModelComponent } from "./../../staff-select-model/staff-select-model.component";
import { TableModule } from "primeng/table";
import { ButtonModule } from "primeng/button";
// const routes = [{ path: "", component: CustomerComponent, canDeactivate: [DeactivateService] }];
import { CustipManagementComponent } from "../cust-ip-management/cust-ip-management.component";
import { CustmacManagementComponent } from "../cust-mac-management/cust-mac-management.component";
import { CustTaskAuditComponent } from "../cust-task-audit/cust-task-audit.component";
import { CustomerNotesComponent } from "../customer-notes/customer-notes.component";
import { CustomerFeedbackComponent } from "../customer-feedback/customer-feedback.component";
import { ChildManagementComponent } from "../child-management/child-management.component";
import { TicketManagementModule } from "../../ticket-management/ticket-management.module";
import { CustomerHomeComponent } from "../customer-home/customer-home.component";
import { MatCardContent, MatCardModule } from "@angular/material/card";
// import { CallDetailsComponent } from "../../call-details/call-details.component";


import { MatTableModule } from "@angular/material/table";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatSortModule } from "@angular/material/sort";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatTabsModule } from "@angular/material/tabs";
import { MatTooltipModule } from "@angular/material/tooltip";
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

import { MatExpansionModule } from '@angular/material/expansion';
import { MatRadioModule } from '@angular/material/radio';
import { MatMenuModule } from '@angular/material/menu';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSliderModule } from "@angular/material/slider";



@NgModule({
    imports: [
        CommonModule,
        CustomerRoutingModule,
        SharedModule,
        DialogModule,
        CardModule,
        SplitterModule,
        AccordionModule,
        TableModule,
        ButtonModule,
        MatCardModule,
        MatPaginatorModule,
        MatCheckboxModule,
        MatDatepickerModule,
        MatDialogModule,
        MatButtonModule,
        MatTableModule,
        MatTimepickerModule,
        MatSortModule,
        MatIconModule,
        MatTabsModule,
        MatTooltipModule,
        MatExpansionModule,
        MatRadioModule,
        MatMenuModule,
        MatChipsModule,
        MatProgressSpinnerModule,
        MatNativeDateModule,
        MatStepperModule,
        MatInputModule,
        MatSliderModule,
        TicketManagementModule
    ],
    declarations: [
        CustDetailsMenuComponent,
        CustomerDetailsComponent,
        CustomerWalletComponent,
        CustomerLedgerComponent,
        CustomerPlansComponent,
        CustomerInvoiceComponent,
        CustomerPaymentComponent,
        CustomerChangeDiscountComponent,
        CustChangePlanComponent,
        CustomerChangeStatusComponent,
        CustServiceManagementComponent,
        AddServiceComponent,
        CustSessionHistoryComponent,
        CustTicketsComponent,
        CustChargeManagementComponent,
        CustCreditNoteComponent,
        CustDBRReportComponent,
        CustWorkflowAuditComponent,
        CustAuditDetailsComponent,
        CustDunningManagementComponent,
        CustNotificationManagementComponent,
        ChildCustComponent,
        CustShiftLocationComponent,
        CustipManagementComponent,
        CustmacManagementComponent,
        CustTaskAuditComponent,
        CustomerNotesComponent,
        CustomerFeedbackComponent,
        ChildManagementComponent,
        CustomerHomeComponent,
    ]
})
export class CustDetailsMenuModule { }
