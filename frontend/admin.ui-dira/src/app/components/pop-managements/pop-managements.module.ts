import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { PopManagementsRoutingModule } from "./pop-managements-routing.module";
import { PopManagementsComponent } from "./pop-managements/pop-managements.component";
import { SharedModule } from "src/app/shared/shared.module";
import { DialogModule } from "primeng/dialog";
import { FormsModule } from "@angular/forms";
import { MatCardModule } from "@angular/material/card";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { MatTableModule } from "@angular/material/table";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatSelectModule } from "@angular/material/select";
import { DeactivateService } from "src/app/service/deactivate.service";
import { MatDialogModule } from "@angular/material/dialog";

import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { CommonInventoryManagementComponent } from "../common-inventory-management/common-inventory-management.component";

const routes = [
    { path: "", component: PopManagementsComponent, canDeactivate: [DeactivateService], }
];
@NgModule({
    declarations: [PopManagementsComponent, CommonInventoryManagementComponent]
    ,
    exports: [
        CommonInventoryManagementComponent
    ],
    imports: [
        CommonModule,
        PopManagementsRoutingModule,
        SharedModule,
        DialogModule,
        FormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule,
        MatButtonModule,
        MatTableModule,
        MatPaginatorModule,
        MatSelectModule,
        MatDialogModule, MatDatepickerModule, MatNativeDateModule
    ]
})
export class PopManagementsModule { }
