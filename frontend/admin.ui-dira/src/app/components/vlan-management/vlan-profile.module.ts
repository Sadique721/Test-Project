import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { VlanProfileComponent } from "./vlan-profile.component";
import { VlanManagementRoutingModule } from "./vlan-profile-routing.module";
import { VlanProfileListComponent } from "./vlan-profile-list/vlan-profile-list.component";
import { VlanProfileCreateComponent } from "./vlan-profile-create/vlan-profile-create.component";
import { VlanProfileBulkAddComponent } from "./vlan-profile-bulk-add/vlan-profile-bulk-add.component";
import { VlanAuditComponent } from "./vlan-audit/vlan-audit.component";

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
const routes = [{ path: "", component: VlanProfileComponent }];

@NgModule({
    declarations: [
        VlanProfileComponent,
        VlanProfileListComponent,
        VlanProfileCreateComponent,
        VlanProfileBulkAddComponent,
        VlanAuditComponent,],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule, VlanManagementRoutingModule, MatPaginator,
        MatCardModule,
        MatCheckbox,
        MatCardContent,
        MatDatepickerModule,
        MatDialogTitle,
        MatDialogContent,
        MatDialogActions,
        MatDialogClose,
        MatButtonModule,
        MatTableModule,
        MatTimepickerModule,
        MatPaginatorModule,
        MatSortModule,
        MatIconModule,
        MatTabsModule,
        MatTooltipModule,
        MatDialogModule, MatNativeDateModule,
        MatCheckboxModule, MatStepperModule, MatInputModule, MatFormFieldModule

    ]
})
export class VlanProfileModule { }
