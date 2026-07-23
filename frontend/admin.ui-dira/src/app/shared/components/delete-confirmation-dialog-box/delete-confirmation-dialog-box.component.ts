import { Component, Inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef } from '@angular/material/dialog';

export interface DeleteDialogData {
    title?: string;
    description: string;
    yesLabel?: string;
    noLabel?: string;
}


@Component({
    selector: 'app-delete-confirmation-dialog-box',
    imports: [MatDialogContent, MatDialogActions, MatButtonModule],
    templateUrl: './delete-confirmation-dialog-box.component.html',
    styleUrl: './delete-confirmation-dialog-box.component.css'
})
export class DeleteConfirmationDialogBoxComponent {

    constructor(
        public dialogRef: MatDialogRef<DeleteConfirmationDialogBoxComponent>,
        @Inject(MAT_DIALOG_DATA) public data: DeleteDialogData
    ) { }

    onConfirm(): void {
        this.dialogRef.close(true);
    }

    onCancel(): void {
        this.dialogRef.close(false);
    }

}
