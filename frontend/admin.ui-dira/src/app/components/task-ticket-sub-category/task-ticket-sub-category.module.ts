import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { DeactivateService } from "src/app/service/deactivate.service";
import { TaskTicketSubCategoryComponent } from "./task-ticket-sub-category.component";

import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator, MatPaginatorModule } from "@angular/material/paginator";
import { MatDialogModule } from '@angular/material/dialog';
import { MatCheckbox } from "@angular/material/checkbox";
import { ReactiveFormsModule } from "@angular/forms";
import { Component, ViewChild, TemplateRef } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import {JsonPipe} from '@angular/common';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {FormsModule} from '@angular/forms';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import { MatTableModule } from "@angular/material/table";
const routes = [
    { path: "", component: TaskTicketSubCategoryComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
    declarations: [TaskTicketSubCategoryComponent],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule,
      MatCardContent, MatCardModule, MatPaginator, MatDialogModule ,MatTableModule, MatPaginatorModule ,
    MatInputModule,
    FormsModule,
    MatSlideToggleModule,
    MatPaginatorModule,
    JsonPipe,],
})
export class TaskTicketSubCategoryModule { }
