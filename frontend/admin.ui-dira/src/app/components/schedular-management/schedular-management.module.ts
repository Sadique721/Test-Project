import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SharedModule } from 'src/app/shared/shared.module';
import { DeactivateService } from 'src/app/service/deactivate.service';
import { SchedularManagementComponent } from './schedular-management.component';

import { MatCardModule, MatCardContent } from '@angular/material/card';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatDialogModule } from '@angular/material/dialog';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { ReactiveFormsModule } from '@angular/forms';

import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTimepickerModule } from '@angular/material/timepicker';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatMomentDateModule } from '@angular/material-moment-adapter';

const routes = [
    { path: '', component: SchedularManagementComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
    declarations: [SchedularManagementComponent],
    imports: [
        CommonModule, MatDatepickerModule,
        MatMomentDateModule,
        ReactiveFormsModule, MatTimepickerModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        RouterModule.forChild(routes),
        SharedModule,
        MatCardModule,
        MatPaginatorModule,
        MatDialogModule,
        MatCheckboxModule,
        MatTableModule,
        MatSortModule,
        MatInputModule,
        MatIconModule,
        MatButtonModule,
    ],
})
export class SchedularManagementModule { }
