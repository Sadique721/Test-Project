import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";

import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { DeactivateService } from "src/app/service/deactivate.service";
import { InvestmentCodeComponent } from "./investment-code.component";
import { DialogModule } from "primeng/dialog";
import { TableModule } from "primeng/table";
import { CheckboxModule } from "primeng/checkbox";
import { PaginatorModule } from "primeng/paginator";
import { MatCardModule } from "@angular/material/card";
import { MatTableModule } from "@angular/material/table";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatSelectModule } from "@angular/material/select";
import { MatInputModule } from "@angular/material/input";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { MatSortModule } from "@angular/material/sort";
import { MatDialogModule } from "@angular/material/dialog";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";

const routes = [
  { path: "", component: InvestmentCodeComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
  declarations: [InvestmentCodeComponent],
  imports: 
  [CommonModule,
    RouterModule.forChild(routes),
    SharedModule,
    DialogModule,
    TableModule,
    CheckboxModule,
    PaginatorModule,
    MatCardModule,
    MatTableModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    MatButtonModule,
    MatSortModule,
    MatDialogModule,
    ReactiveFormsModule,
    FormsModule,],
})
export class InvestmentCodeModule {}
