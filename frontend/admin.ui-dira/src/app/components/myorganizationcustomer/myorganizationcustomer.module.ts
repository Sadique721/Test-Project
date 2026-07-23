import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { MyorganizationcustomerComponent } from "./myorganizationcustomer.component";
import { DialogModule } from "primeng/dialog";
import { TableModule } from "primeng/table";
import { CheckboxModule } from "primeng/checkbox";
import { PaginatorModule } from "primeng/paginator";
import { MatCardModule } from "@angular/material/card";
import { ReactiveFormsModule, FormsModule } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatDialogModule } from "@angular/material/dialog";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatSelectModule } from "@angular/material/select";
import { MatSortModule } from "@angular/material/sort";
import { MatTableModule } from "@angular/material/table";
import { MatTabsModule } from "@angular/material/tabs";
import { MatTimepickerModule } from "@angular/material/timepicker";
import { MatDatepickerModule } from "@angular/material/datepicker";

const routes = [{ path: "", component: MyorganizationcustomerComponent }];

@NgModule({
  declarations: [MyorganizationcustomerComponent],
  imports: [CommonModule,
    RouterModule.forChild(routes),
    SharedModule,

    // PrimeNG modules
    DialogModule,
    TableModule,
    CheckboxModule,
    PaginatorModule,

    // Angular Material modules
    MatCardModule,
    MatTableModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatInputModule,
    MatTabsModule,
    MatSelectModule,
    MatIconModule,
    MatButtonModule,
    MatSortModule,
    MatDialogModule,
    MatDatepickerModule,
    MatCheckboxModule,
    MatTimepickerModule,
    ReactiveFormsModule,
    FormsModule],
})
export class MyorganizationcustomerModule { }
