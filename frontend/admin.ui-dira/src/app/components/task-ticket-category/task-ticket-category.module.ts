import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { DeactivateService } from "src/app/service/deactivate.service";
import { TaskTicketCategoryComponent } from "./task-ticket-category.component";

// import { CountryManagementComponent } from "./country-management.component";
// import { DeactivateService } from "src/app/service/deactivate.service";
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator, MatPaginatorModule } from "@angular/material/paginator";
import { MatDialogModule } from '@angular/material/dialog';
import { MatCheckbox } from "@angular/material/checkbox";
import { ReactiveFormsModule } from "@angular/forms";
import { Component, ViewChild, TemplateRef } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
// import { AddEditCountryManagmentComponent } from "./add-edit-country-managment/add-edit-country-managment.component";
import {JsonPipe} from '@angular/common';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {FormsModule} from '@angular/forms';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import { MatTableModule } from "@angular/material/table";
const routes = [
    { path: "", component: TaskTicketCategoryComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
    declarations: [TaskTicketCategoryComponent],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule,
      MatCardContent, MatCardModule, MatPaginator, MatDialogModule ,MatTableModule, MatPaginatorModule ,
    MatInputModule,
    FormsModule,
    MatSlideToggleModule,
    MatPaginatorModule,
    JsonPipe,
    ],
})
export class TaskTicketCategoryModule { }
