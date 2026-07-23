import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { AssignedInventoryComponent } from "./assigned-inventory.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { FormsModule } from "@angular/forms";
import { DialogModule } from "primeng/dialog";
import { TableModule } from "primeng/table";
import { MatTabGroup } from "@angular/material/tabs";
import { MaterialModule } from "src/app/material.module";
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialogModule } from '@angular/material/dialog';
import { MatCheckbox } from "@angular/material/checkbox";
import { MatTabsModule } from '@angular/material/tabs';

import { MatPaginatorModule } from '@angular/material/paginator';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from "@angular/material/form-field";


const routes = [{ path: "", component: AssignedInventoryComponent }];

@NgModule({
    declarations: [AssignedInventoryComponent],
    imports: [CommonModule, FormsModule, RouterModule.forChild(routes), SharedModule, DialogModule, TableModule,
        MatTabGroup, MaterialModule, MatCardContent, MatCardModule, MatPaginator, MatDialogModule,
        MatCheckbox, MatTabsModule, MatTableModule, MatTableModule, MatCheckboxModule, MatPaginatorModule, MatSortModule, MatFormFieldModule
    ],
})
export class AssignedInventoryModule { }
