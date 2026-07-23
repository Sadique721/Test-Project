import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";

import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { TimeBasePolicyComponent } from "./time-base-policy.component";
import { DeactivateService } from "src/app/service/deactivate.service";

import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialogModule } from "@angular/material/dialog";
import { MatCheckbox } from "@angular/material/checkbox";
import { MatSelectModule } from "@angular/material/select";
import { MatFormFieldModule } from "@angular/material/form-field";
// import { MatTimepickerModule } from "@angular/material/timepicker";
// import { MatDatepickerModule } from "@angular/material/datepicker";
// import { provideNativeDateAdapter } from "@angular/material/core";

const routes = [
    { path: "", component: TimeBasePolicyComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
    declarations: [TimeBasePolicyComponent],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule, MatCardContent, MatCardModule,
        MatPaginator, MatDialogModule, MatCheckbox, MatSelectModule, MatFormFieldModule],
})
export class TimebasepolicyModule { }
