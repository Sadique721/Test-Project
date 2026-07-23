import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from 'src/app/material.module';
import { FormsModule } from '@angular/forms';
import { SelectorDialogComponent } from './selector-dialog.component';

@NgModule({
    declarations: [SelectorDialogComponent],
    exports: [SelectorDialogComponent],
    imports: [
        CommonModule,
        MaterialModule,
        FormsModule
    ],
})
export class SelectorDialogModule { }
