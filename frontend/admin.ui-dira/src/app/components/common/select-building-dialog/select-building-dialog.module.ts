import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SelectBuildingDialogComponent } from './select-building-dialog.component';
import { MaterialModule } from 'src/app/material.module';
import { FormsModule } from '@angular/forms';



@NgModule({
    declarations: [SelectBuildingDialogComponent],
    exports: [SelectBuildingDialogComponent],
    imports: [
        CommonModule,
        MaterialModule,
        FormsModule
    ],
})
export class SelectBuildingDialogModule { }
