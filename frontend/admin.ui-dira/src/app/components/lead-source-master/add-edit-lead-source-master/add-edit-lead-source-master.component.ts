import { Component, Inject, ViewChild, OnInit } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup, UntypedFormArray, Validators } from "@angular/forms";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { WhiteeSpaceValidator } from "../../shared/custom-validators";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatTableDataSource } from "@angular/material/table";

export interface LeadSourceDialogData {
    leadSourceData?: any;
    leadSubSourceList?: any[];
    isEdit: boolean;
    createAcS: boolean;
    editAcs: boolean;
    title?: string;
    description?: string;
    yesLabel?: string;
    noLabel?: string;
}

@Component({
    selector: "app-add-edit-lead-source-master",
    templateUrl: "./add-edit-lead-source-master.component.html",
    styleUrls: ["./add-edit-lead-source-master.component.css"],
    standalone: false,
})
export class AddEditLeadSourceMasterComponent implements OnInit {
    leadSourceFormGroup!: UntypedFormGroup;
    leadSubSourceMappingForm!: UntypedFormGroup;
    leadSubSourceMapping!: UntypedFormArray;
    statusOptions = RadiusConstants.status;

    submitted = false;
    createAccess = true;
    editAccess = false;
    isLeadSourceEdit = false;

    subSourceDisplayedColumns: string[] = ["leadSubSourceName", "delete"];
    subSourceDataSource = new MatTableDataSource<any>([]);
    leadSubSourceDeletedIds: number[] = [];
    leadSubSourceItemsPerPage = 5;

    @ViewChild("subSourcePaginator") subSourcePaginator!: MatPaginator;

    constructor(
        public dialogRef: MatDialogRef<AddEditLeadSourceMasterComponent>,
        @Inject(MAT_DIALOG_DATA) public data: LeadSourceDialogData,
        private fb: UntypedFormBuilder
    ) { }

    ngOnInit(): void {
        this.leadSourceFormGroup = this.fb.group({
            leadSourceName: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            status: ["", Validators.required],
            leadSubSourceMapping: this.leadSubSourceMapping
        });

        this.leadSubSourceMappingForm = this.fb.group({
            leadSubSourceName: ["", Validators.required],
        });

        this.leadSubSourceMapping = this.fb.array([]);

        if (this.data.isEdit === true) {
            this.isLeadSourceEdit = true;
            this.editAccess = this.data.editAcs;
            this.leadSourceFormGroup.patchValue({
                leadSourceName: this.data.leadSourceData?.leadSourceName || "",
                status: this.data.leadSourceData?.status || ""
            });

            if (this.data.leadSubSourceList && this.data.leadSubSourceList.length > 0) {
                this.data.leadSubSourceList.forEach(subSource => {
                    this.leadSubSourceMapping.push(
                        this.fb.group({
                            leadSubSourceName: [subSource.leadSubSourceName, Validators.required],
                            id: [subSource.id]
                        })
                    );
                });
            }

            this.updateSubSourceTable();
        }
    }

    private createLeadSubSourceGroup(leadSubSourceName: string = '', id?: number): UntypedFormGroup {
        return this.fb.group({
            leadSubSourceName: [leadSubSourceName, Validators.required],
            id: [id]
        });
    }

    onAddLeadSubSourceMappingField(): void {
        if (this.leadSubSourceMappingForm.valid) {
            // --- START CHANGE: Use the helper and clear the separate form ---
            const newName = this.leadSubSourceMappingForm.value.leadSubSourceName;
            // Push a new form group without an ID
            this.leadSubSourceMapping.push(this.createLeadSubSourceGroup(newName));

            this.updateSubSourceTable();
            this.leadSubSourceMappingForm.reset();
            // Manually set null on controls to clear validation errors visually if any
            Object.keys(this.leadSubSourceMappingForm.controls).forEach(key => {
                this.leadSubSourceMappingForm.get(key)?.setErrors(null);
            });
            // --- END CHANGE ---
        }
    }

    deleteConfirmonLeadSubSourceMappingField(index: number, id?: number): void {
        if (id) {
            this.leadSubSourceDeletedIds.push(id);
        }
        this.leadSubSourceMapping.removeAt(index);
        this.updateSubSourceTable();
    }

    updateSubSourceTable(): void {
        // Directly map the form control values, as they now consistently contain { leadSubSourceName, id }
        this.subSourceDataSource.data = this.leadSubSourceMapping.controls.map(c => c.value);

        if (this.subSourcePaginator) {
            this.subSourceDataSource.paginator = this.subSourcePaginator;
        }
    }

    pageChangedleadSubSourceOnView(event: PageEvent): void {
        this.leadSubSourceItemsPerPage = event.pageSize;
        if (this.subSourcePaginator) {
            this.subSourceDataSource.paginator = this.subSourcePaginator;
        }
    }

    onCancel(): void {
        this.dialogRef.close(null);
    }

    onSubmit(): void {
        this.submitted = true;
        if (this.leadSourceFormGroup.valid) {
            const formValue = this.leadSourceFormGroup.value;
            const result = {
                ...formValue,
                leadSubSourceList: this.leadSubSourceMapping.value,
                leadSubSourceDeletedIds: this.leadSubSourceDeletedIds,
            };
            this.dialogRef.close(result);
        } else {
            this.leadSourceFormGroup.markAllAsTouched();
        }
    }
}
