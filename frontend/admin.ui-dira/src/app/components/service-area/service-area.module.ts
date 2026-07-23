import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { ServiceAreaComponent } from "./service-area.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { GoogleMapsModule } from "@angular/google-maps";
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator, MatPaginatorModule } from "@angular/material/paginator";
import { MatDialogModule } from "@angular/material/dialog";
import { MatTooltip } from "@angular/material/tooltip";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { MatMenuModule } from "@angular/material/menu";
import { MatRadioModule } from "@angular/material/radio";

import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';


import { AddEditServiceAreaManagmentComponent } from "./add-edit-service-area-managment/add-edit-service-area-managment.component";
import { ReactiveFormsModule } from "@angular/forms";
import { CommonInventoryManagementComponent } from "../common-inventory-management/common-inventory-management.component";
import { MatCheckboxModule } from '@angular/material/checkbox';

const routes = [{ path: "", component: ServiceAreaComponent, canDeactivate: [DeactivateService] }];

@NgModule({
    declarations: [ServiceAreaComponent, AddEditServiceAreaManagmentComponent, CommonInventoryManagementComponent],
    imports: [
        CommonModule,
        RouterModule.forChild(routes),
        SharedModule,
        GoogleMapsModule,
        MatCardContent,
        MatCardModule,
        GoogleMapsModule,
        MatPaginator,
        MatDialogModule,
        MatTooltip,
        MatMenuModule,
        MatIconModule,
        MatButtonModule,
        MatRadioModule, MatDatepickerModule,
        MatInputModule,
        MatNativeDateModule,
        MatFormFieldModule,
        ReactiveFormsModule, MatPaginatorModule,
        MatCheckboxModule
    ],
})
export class ServiceAreaModule { }
