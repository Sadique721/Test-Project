import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { AuditLogComponent } from "./audit-log.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { MatCardModule, MatCardContent } from "@angular/material/card";
import { MatPaginator, MatPaginatorModule } from "@angular/material/paginator";
import { MatDialogModule } from '@angular/material/dialog';
import { MatCheckbox } from "@angular/material/checkbox";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule } from '@angular/material/core';

const routes = [
  { path: "", component: AuditLogComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
  declarations: [AuditLogComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule, FormsModule,
    RouterModule.forChild(routes),
    SharedModule,
    MatCardContent,
    MatCardModule,MatPaginatorModule,
    MatPaginator,
    MatDialogModule,
    MatCheckbox,
     MatDatepickerModule,    
    MatInputModule,          
    MatNativeDateModule 
  ],
  providers: [DeactivateService],
})
export class AuditLogModule { }
