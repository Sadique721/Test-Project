import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { PlanManagementComponent } from "./plan-management.component";
import { DeactivateService } from "src/app/service/deactivate.service";

import { DialogModule } from "primeng/dialog";

// Angular Material Modules
import { MatTableModule } from "@angular/material/table";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatSortModule } from "@angular/material/sort";
import { MatCardModule } from "@angular/material/card";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatTabsModule } from "@angular/material/tabs";
import { MatTooltipModule } from "@angular/material/tooltip";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatNativeDateModule } from "@angular/material/core";
import { MatDialogModule } from "@angular/material/dialog";
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatStepperModule } from '@angular/material/stepper';

// import { WorkflowAuditDetailsModalComponent } from "../workflow-audit-details-modal/workflow-audit-details-modal.component";

const routes = [
    { path: "", component: PlanManagementComponent, canDeactivate: [DeactivateService] }
];

@NgModule({
    declarations: [
        PlanManagementComponent
    ],
    exports: [

    ],
    imports: [
        CommonModule,
        RouterModule.forChild(routes),
        SharedModule,
        DialogModule,
        MatCardModule,
        MatButtonModule,
        MatTableModule,
        MatPaginatorModule,
        MatSortModule,
        MatIconModule,
        MatTabsModule,
        MatTooltipModule,
        MatCheckboxModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatDialogModule,
        MatInputModule,
        MatFormFieldModule,
        MatStepperModule
    ]
})
export class PlanManagementModule { }
