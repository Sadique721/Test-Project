import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { DashbordComponent } from "./dashbord.component";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { MatTabsModule } from "@angular/material/tabs";
import { MatTableModule } from "@angular/material/table";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatCardModule } from "@angular/material/card";
import { MatTooltipModule } from "@angular/material/tooltip";
import { MatIconModule } from "@angular/material/icon";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { provideNativeDateAdapter } from "@angular/material/core";
import { MatTimepickerModule } from "@angular/material/timepicker";
import { MatRadioModule } from '@angular/material/radio';
import { MatMenuModule } from '@angular/material/menu';


import {
    MatDialog,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogModule,
    MatDialogTitle
} from "@angular/material/dialog";
import { MatButtonModule } from "@angular/material/button";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { ToastrModule } from "ngx-toastr";

const routes = [{ path: "", component: DashbordComponent }];

@NgModule({
    declarations: [DashbordComponent],
    providers: [provideNativeDateAdapter()],
    imports: [
        MatRadioModule,
        CommonModule,
        RouterModule.forChild(routes),
        SharedModule,
        ToastrModule,
        MatTabsModule,
        MatTableModule,
        MatPaginatorModule,
        MatCardModule,
        MatTooltipModule,
        ReactiveFormsModule,
        FormsModule,
        MatIconModule,
        MatDatepickerModule,
        MatDialogTitle,
        MatDialogContent,
        MatDialogActions,
        MatDialogClose,
        MatButtonModule,
        MatTimepickerModule,
        MatMenuModule
    ]
})
export class DashbordModule { }
