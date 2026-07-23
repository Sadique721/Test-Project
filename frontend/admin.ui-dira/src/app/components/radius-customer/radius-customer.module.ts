import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RadiusCustomerComponent } from "./radius-customer.component";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { AuthguardGuard } from "src/app/authguard.guard";
import { RadiusCustomerRoutingModule } from "./radius-customer-routing.module";
import { RadiusCustomerCreateComponent } from "./radius-customer-create/radius-customer-create.component";
import { RadiusCustomerListComponent } from "./radius-customer-list/radius-customer-list.component";
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator, MatPaginatorModule } from "@angular/material/paginator";
import { MatDialogActions, MatDialogClose, MatDialogContent, MatDialogModule, MatDialogTitle } from "@angular/material/dialog";
import { MatCheckbox } from "@angular/material/checkbox";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatTabsModule } from "@angular/material/tabs";
import { MatTableModule } from "@angular/material/table";
import { MatTooltipModule } from "@angular/material/tooltip";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { MatTimepickerModule } from "@angular/material/timepicker";
import { provideNativeDateAdapter } from "@angular/material/core";
import { MatStepperModule } from '@angular/material/stepper';
// import { RadiusCustomerDetailsComponent } from "./radius-customer-details/radius-customer-details.component";

// const routes = [{ path: "", component: RadiusCustomerComponent }];

@NgModule({
    declarations: [
        RadiusCustomerComponent,
        RadiusCustomerCreateComponent,
        RadiusCustomerListComponent,
        // RadiusCustomerDetailsComponent,
    ],
    providers: [provideNativeDateAdapter()],
    imports: [
        CommonModule,
        // RouterModule.forChild(routes),
        RadiusCustomerRoutingModule,
        SharedModule, MatCardContent, MatCardModule,
        MatPaginator, MatDialogModule, MatCheckbox, MatDatepickerModule,
        MatTabsModule,
        MatTableModule,
        MatPaginatorModule,
        MatCardModule,
        MatTooltipModule,
        ReactiveFormsModule,
        FormsModule,
        MatStepperModule,
        MatIconModule,
        MatDatepickerModule,
        MatDialogTitle,
        MatDialogContent,
        MatDialogActions,
        MatDialogClose,
        MatButtonModule,
        MatTimepickerModule
    ],
})
export class RadiusCustomerModule { }
