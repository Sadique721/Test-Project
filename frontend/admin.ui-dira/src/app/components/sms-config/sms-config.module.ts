import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { SmsConfigComponent } from "./sms-config.component";
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialogModule } from '@angular/material/dialog';
import { MatCheckbox } from "@angular/material/checkbox";
import { ReactiveFormsModule } from "@angular/forms";
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { OrganizationChartModule } from 'primeng/organizationchart';
const routes = [{ path: "", component: SmsConfigComponent }];

@NgModule({
    declarations: [SmsConfigComponent],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule, MatCardContent, MatCardModule, MatPaginator, MatDialogModule, MatCheckbox, ReactiveFormsModule,
        MatPaginatorModule, MatTableModule, MatSortModule, MatIconModule, MatButtonModule, OrganizationChartModule],
})
export class SmsConfigModule { }
