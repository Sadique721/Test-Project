import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SharedModule } from 'src/app/shared/shared.module';
import { BankmanagementComponent } from './bankmanagement.component';
import { DeactivateService } from 'src/app/service/deactivate.service';

import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSortModule } from '@angular/material/sort';
import { MatDialogModule } from '@angular/material/dialog';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { DialogModule } from 'primeng/dialog';
import { TableModule } from 'primeng/table';
import { CheckboxModule } from 'primeng/checkbox';
import { PaginatorModule } from 'primeng/paginator';

const routes = [
    { path: '', component: BankmanagementComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
    declarations: [BankmanagementComponent],
    imports: [
        CommonModule,
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
        FormsModule,
    ],
})
export class BankmanagementModule { }
