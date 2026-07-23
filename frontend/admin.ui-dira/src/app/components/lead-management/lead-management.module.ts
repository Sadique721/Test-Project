import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { LeadManagementComponent } from "./lead-management.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { FormsModule } from '@angular/forms'; // For ngModel and @if directive
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatPaginator } from "@angular/material/paginator";
import { MatStep, MatStepper } from "@angular/material/stepper";
import { ReactiveFormsModule } from '@angular/forms'; // For reactive forms
import { MatStepperModule } from '@angular/material/stepper'; // Stepper
import { MatDialogModule } from '@angular/material/dialog';
import { MaterialModule } from "src/app/material.module";
import { ViewDetailCardComponent } from "src/app/shared/components/view-detail-card/view-detail-card.component";
import { MatTimepickerModule } from "@angular/material/timepicker";
import { SelectBuildingDialogModule } from "../common/select-building-dialog/select-building-dialog.module";
import { SelectorDialogModule } from "../common/selector-dialog/selector-dialog.module";

const routes = [
    { path: "", component: LeadManagementComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
    declarations: [LeadManagementComponent],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule, FormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        SelectorDialogModule,
        MatButtonModule,
        MatIconModule,
        MatDatepickerModule,
        MatTimepickerModule,
        MaterialModule,
        MatNativeDateModule, MatPaginator, MatStep, MatStepper, ReactiveFormsModule, MatStepperModule, MatDialogModule, SelectBuildingDialogModule],
})
export class LeadManagementModule { }