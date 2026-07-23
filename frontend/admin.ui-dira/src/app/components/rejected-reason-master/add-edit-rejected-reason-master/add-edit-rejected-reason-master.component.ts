import { Component, Inject, ViewChild } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, UntypedFormArray, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import * as RadiusConstants from 'src/app/RadiusUtils/RadiusConstants';
import { WhiteeSpaceValidator } from '../../shared/custom-validators';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';

export interface RejectedReasonDialogData {
    rejectedReasonData?: any;
    rejectedSubReasonList?: any[];
    isEdit: boolean;
    createAcS: boolean;
    editAcs: boolean;
    title?: string;
    description?: string;
    yesLabel?: string;
    noLabel?: string;
}

@Component({
    selector: 'app-add-edit-rejected-reason-master',
    templateUrl: './add-edit-rejected-reason-master.component.html',
    styleUrls: ['./add-edit-rejected-reason-master.component.css'],
    standalone: false
})
export class AddEditRejectedReasonMasterComponent {

    rejectedReasonFormGroup!: UntypedFormGroup;
    rejectedSubReasonMappingForm!: UntypedFormGroup;
    rejectedSubReasonMapping!: UntypedFormArray;
    statusOptions = RadiusConstants.status;

    submitted = false;
    createAccess = true;
    editAccess = false;
    isRejectedReasonEdit = false;

    subReasonDisplayedColumns: string[] = ['subReason', 'delete'];
    subReasonDataSource = new MatTableDataSource<any>([]);
    rejectedSubReasonDeletedIds: number[] = [];
    rejectedSubReasonItemsPerPage = 5;

    @ViewChild('subReasonPaginator') subReasonPaginator!: MatPaginator;

    constructor(
        public dialogRef: MatDialogRef<AddEditRejectedReasonMasterComponent>,
        @Inject(MAT_DIALOG_DATA) public data: RejectedReasonDialogData,
        private fb: UntypedFormBuilder
    ) { }

    ngOnInit(): void {
        this.rejectedReasonFormGroup = this.fb.group({
            name: ['', [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            status: ['', Validators.required]
        });

        this.rejectedSubReasonMappingForm = this.fb.group({
            name: ['', Validators.required]
        });

        this.rejectedSubReasonMapping = this.fb.array([]);

        if (this.data.isEdit === true) {
            this.isRejectedReasonEdit = true;
            this.editAccess = this.data.editAcs;
            this.rejectedReasonFormGroup.patchValue({
                name: this.data.rejectedReasonData?.name || '',
                status: this.data.rejectedReasonData?.status || ''
            });

            if (this.data.rejectedSubReasonList && this.data.rejectedSubReasonList.length > 0) {
                this.data.rejectedSubReasonList.forEach(subReason => {
                    this.rejectedSubReasonMapping.push(this.fb.group(subReason));
                });
            }
        }

        this.updateSubReasonTable();
    }

    onAddRejectedSubReasonMappingField(): void {
        if (this.rejectedSubReasonMappingForm.valid) {
            this.rejectedSubReasonMapping.push(this.fb.group({ name: this.rejectedSubReasonMappingForm.value.name }));
            this.updateSubReasonTable();
            this.rejectedSubReasonMappingForm.reset();
        }
    }

    deleteConfirmonRejectedSubReasonMappingField(index: number, id?: number): void {
        if (id) {
            this.rejectedSubReasonDeletedIds.push(id);
        }
        this.rejectedSubReasonMapping.removeAt(index);
        this.updateSubReasonTable();
    }

    updateSubReasonTable(): void {
        this.subReasonDataSource.data = this.rejectedSubReasonMapping.controls.map(c => c.value);
        if (this.subReasonPaginator) {
            this.subReasonDataSource.paginator = this.subReasonPaginator;
        }
    }

    pageChangedleadSubSourceOnView(event: PageEvent): void {
        this.rejectedSubReasonItemsPerPage = event.pageSize;
        if (this.subReasonPaginator) {
            this.subReasonDataSource.paginator = this.subReasonPaginator;
        }
    }

    onCancel(): void {
        this.dialogRef.close(null);
    }

    onSubmit(): void {
        this.submitted = true;
        if (this.rejectedReasonFormGroup.valid) {
            const formValue = this.rejectedReasonFormGroup.value;
            const result = {
                ...formValue,
                rejectSubReasonList: this.rejectedSubReasonMapping.value,
                rejectedSubReasonDeletedIds: this.rejectedSubReasonDeletedIds
            };
            this.dialogRef.close(result);
        } else {
            this.rejectedReasonFormGroup.markAllAsTouched();
        }
    }
}
