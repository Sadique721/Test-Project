import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { AreaManagementComponent } from "./area-management.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { MatCardModule } from '@angular/material/card';
import { MatCardContent } from "@angular/material/card";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialogModule } from '@angular/material/dialog';
import { Component } from '@angular/core';
import { MatPaginatorModule } from '@angular/material/paginator';
import { JsonPipe } from '@angular/common';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';

import { MatButtonModule } from '@angular/material/button';
import { ReactiveFormsModule } from '@angular/forms';


// import { AddEditAreaManagementComponent } from "./add-edit-area-management/add-edit-area-management.component";



import { MatSelectModule } from '@angular/material/select';


const routes = [
    { path: "", component: AreaManagementComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
    declarations: [AreaManagementComponent],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule, MatCardModule,
        MatCardContent,
        MatPaginator,
        MatDialogModule,
        MatFormFieldModule,
        MatInputModule, ReactiveFormsModule,
        MatButtonModule,
        FormsModule,
        MatSlideToggleModule, MatSelectModule,
        MatPaginatorModule,
        JsonPipe,
    ],
})
export class AreaManagementModule { }
