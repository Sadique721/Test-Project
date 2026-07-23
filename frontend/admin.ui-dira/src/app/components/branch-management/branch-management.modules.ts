import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";

import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { BranchManagementComponent } from "./branch-management.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatDialogModule } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
// import {Component} from '@angular/core';
// import { MatPaginatorModule} from '@angular/material/paginator';
import { JsonPipe } from '@angular/common';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ReactiveFormsModule } from "@angular/forms";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatChipsModule } from "@angular/material/chips";
const routes = [
    { path: "", component: BranchManagementComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
    declarations: [BranchManagementComponent],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule,
        MatCardContent, MatCardModule, MatPaginator, MatDialogModule, MatTableModule, MatPaginatorModule,
        MatInputModule,
        FormsModule, ReactiveFormsModule,
        MatSlideToggleModule,
        MatCheckboxModule,
        MatPaginatorModule,
        MatChipsModule,
        JsonPipe,]
})
export class BranchManagementModule { }
