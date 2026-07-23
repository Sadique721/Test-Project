import { Component, OnInit, TemplateRef, ViewChild, OnDestroy } from "@angular/core";
import { FormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator } from "@angular/material/paginator";
import { ToastrService } from "ngx-toastr";
import { MatDialog } from "@angular/material/dialog";
import { Subject, takeUntil } from "rxjs";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { PolicyService } from "./policy.service";


@Component({
    selector: "app-policy",
    templateUrl: "./policy.component.html",
    styleUrls: ["./policy.component.css"],
    standalone: false
})
export class PolicyComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    policyForm: UntypedFormGroup;
    isPolicyEdit = false;
    createAccess = true;
    editAccess = true;
    deleteAccess = true;

    statusOptions = RadiusConstants.status;
    displayedColumns: string[] = [
        "id",
        "name",
        "description",
        "qosSpeed",
        "type",
        "createByName",
        "action"
    ];

    dataSource = new MatTableDataSource<any>([]);

    @ViewChild(MatPaginator) paginator!: MatPaginator;
    totalRecords = 0;
    itemsPerPage = 5;
    currentPageIndex = 0;
    viewTrcData: any | null = null;
    submitted = false;
    editId: number | null = null;
    searchkey: string = '';
    constructor(
        private fb: FormBuilder,
        private service: PolicyService,
        private toastr: ToastrService,
        private dialog: MatDialog
    ) { }

    ngOnInit(): void {
        this.buildForm();
        this.loadData();
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    buildForm(): void {
        this.policyForm = this.fb.group({
            id: [''],
            name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
            description: ['', [Validators.required, Validators.maxLength(500)]],
            thPolicyName: ['', [Validators.required, Validators.maxLength(100)]],
            baseParam1: [''],
            baseParam2: [''],
            baseParam3: [''],
            thParam1: [''],
            thParam2: [''],
            thParam3: [''],
            basePolicyName: ['', [Validators.required, Validators.maxLength(100)]],
            mvnoid: [''],
            type: [''],
            qosSpeed: ['', [Validators.required, Validators.pattern(/^\d+$/)]],
            upstreamProfileUID: [''],
            downstreamProfileUID: ['']
        });
    }

    loadData(): void {
        this.service.getAll()
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (res: any) => {
                    this.dataSource.data = Array.isArray(res) ? res : [];
                    this.totalRecords = this.dataSource.data.length;
                },
                error: (error: any) => {
                    this.toastr.error(error?.error?.ERROR || 'Failed to load policies', "Failed!");
                    this.dataSource.data = [];
                }
            });
    }
    pageChangedTrcList(event: any): void {
        this.currentPageIndex = event.pageIndex;
        this.itemsPerPage = event.pageSize;
    }
    onSubmit(dialogRef: any): void {
        this.submitted = true;


        if (this.policyForm.invalid) {
            this.markFormGroupTouched(this.policyForm);
            this.toastr.error('Please fill all required fields correctly', 'Validation Error');
            return;
        }

        const formData = this.prepareFormData();
        const isEdit = this.editId !== null;



        // Validate against expected structure for create
        if (!isEdit) {
            this.validateCreatePayload(formData);
        }

        if (isEdit) {
            this.updatePolicy(formData, dialogRef);
        } else {
            this.createPolicy(formData, dialogRef);
        }
    }

    private validateCreatePayload(data: any): void {
        const requiredFields = ['name', 'description', 'thPolicyName', 'basePolicyName', 'qosSpeed'];
        const missingFields: string[] = [];

        requiredFields.forEach(field => {
            if (!data[field] || data[field] === '') {
                missingFields.push(field);
            }
        });

        if (missingFields.length > 0) {
            console.error('Missing required fields:', missingFields);
        } else {
        }
    }

    private getFormValidationErrors(): any {
        const formErrors: any = {};
        Object.keys(this.policyForm.controls).forEach(key => {
            const controlErrors = this.policyForm.get(key)?.errors;
            if (controlErrors) {
                formErrors[key] = controlErrors;
            }
        });
        return formErrors;
    }

    private prepareFormData() {
        const formValue = { ...this.policyForm.value };

        // For create operations, don't include ID
        const payload: any = {
            name: formValue.name?.trim() || '',
            description: formValue.description?.trim() || '',
            thPolicyName: formValue.thPolicyName?.trim() || '',
            baseParam1: formValue.baseParam1?.trim() || 'N/A',
            baseParam2: formValue.baseParam2?.trim() || 'N/A',
            baseParam3: formValue.baseParam3?.trim() || 'N/A',
            thParam1: formValue.thParam1?.trim() || 'N/A',
            thParam2: formValue.thParam2?.trim() || 'N/A',
            thParam3: formValue.thParam3?.trim() || 'N/A',
            basePolicyName: formValue.basePolicyName?.trim() || '',
            mvnoid: formValue.mvnoid ? parseInt(formValue.mvnoid, 10) : 5,
            type: formValue.type || null,
            qosSpeed: formValue.qosSpeed || '10',
            upstreamProfileUID: formValue.upstreamProfileUID ? parseInt(formValue.upstreamProfileUID, 10) : null,
            downstreamProfileUID: formValue.downstreamProfileUID ? parseInt(formValue.downstreamProfileUID, 10) : null,
            isDeleted: 0,
            createdByStaffid: null,
            lastModifiedByStaffId: null,
            createByName: null,
            updateByName: null
        };

        // Only include ID for update operations
        if (this.editId !== null) {
            payload.id = this.editId;
        }

        return payload;
    }

    private createPolicy(data: any, dialogRef: any): void {


        this.service.create(data)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (response: any) => {
                    this.handleApiResponse(response, 'Policy created successfully', dialogRef);
                },
                error: (error: any) => {
                    console.error('POLICY CREATE ERROR - Full error object:', error);
                    console.error('POLICY CREATE ERROR - Error status:', error.status);
                    console.error('POLICY CREATE ERROR - Error message:', error.message);
                    console.error('POLICY CREATE ERROR - Error body:', error.error);
                    this.handleApiError(error, 'Failed to create policy');
                }
            });
    }

    private updatePolicy(data: any, dialogRef: any): void {
        this.service.update(this.editId!, data)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (response: any) => {
                    this.handleApiResponse(response, 'Policy updated successfully', dialogRef);
                },
                error: (error: any) => {
                    this.handleApiError(error, 'Failed to update policy');
                }
            });
    }

    private handleApiResponse(response: any, successMessage: string, dialogRef: any): void {
        if (response.responseCode === 406) {
            this.toastr.error(response.responseMessage || 'Operation failed', "Failed!");
        } else if (response.responseCode === 417 || response.responseCode === 500) {
            this.toastr.error(response.responseMessage || 'Server error occurred', "Failed!");
        } else {
            this.toastr.success(successMessage, "Success!");
            dialogRef.close();
            this.refreshData();
            this.cancelForm();
        }
    }

    private handleApiError(error: any, defaultMessage: string): void {
        const errorMessage = error?.error?.ERROR || error?.error?.message || defaultMessage;
        this.toastr.error(errorMessage, "Failed!");
    }

    private refreshData(): void {
        if (this.searchkey && this.searchkey.trim() !== '') {
            this.getsearch();
        } else {
            this.loadData();
        }
    }

    private markFormGroupTouched(formGroup: UntypedFormGroup): void {
        Object.keys(formGroup.controls).forEach(key => {
            const control = formGroup.get(key);
            control?.markAsTouched();
        });
    }

    edit(row: any): void {
        this.isPolicyEdit = true;
        this.editId = row.id || null;

        this.service.getById(row.id!)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (res: any) => {
                    const policyData = Array.isArray(res) && res.length > 0 ? res[0] : res;
                    this.policyForm.patchValue({
                        id: policyData.id,
                        name: policyData.name,
                        description: policyData.description,
                        thPolicyName: policyData.thPolicyName,
                        baseParam1: policyData.baseParam1,
                        baseParam2: policyData.baseParam2,
                        baseParam3: policyData.baseParam3,
                        thParam1: policyData.thParam1,
                        thParam2: policyData.thParam2,
                        thParam3: policyData.thParam3,
                        basePolicyName: policyData.basePolicyName,
                        mvnoid: policyData.mvnoid,
                        type: policyData.type,
                        qosSpeed: policyData.qosSpeed,
                        upstreamProfileUID: policyData.upstreamProfileUID,
                        downstreamProfileUID: policyData.downstreamProfileUID
                    });
                    this.submitted = false;
                    this.dialog.open(this.addEditDialog, {
                        width: "80%",
                        disableClose: true
                    });
                },
                error: (error: any) => {
                    this.toastr.error(error?.error?.ERROR || 'Failed to load policy details', "Failed!");
                }
            });
    }

    delete(row: any): void {
        const dialogRef = this.dialog.open(this.confirmDialog, {
            width: '400px',
            data: {
                title: 'Delete Confirmation',
                description: `Do you want to delete policy "${row.name}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed()
            .pipe(takeUntil(this.destroy$))
            .subscribe((result) => {
                if (result === true) {
                    this.service.delete(row.id!)
                        .pipe(takeUntil(this.destroy$))
                        .subscribe({
                            next: (response: any) => {
                                if (response.responseCode === 406) {
                                    this.toastr.error(response.responseMessage || 'Delete failed', 'Failed!');
                                } else if (response.responseCode === 417) {
                                    this.toastr.info(response.responseMessage || 'Delete not allowed', 'Info!');
                                } else {
                                    this.toastr.success('Policy deleted successfully', 'Success!');
                                    this.refreshData();
                                }
                            },
                            error: (error: any) => {
                                this.toastr.error(error?.error?.ERROR || 'Failed to delete policy', "Failed!");
                            }
                        });
                }
            });
    }
    getsearch(): void {
        if (!this.searchkey || this.searchkey.trim() === '') {
            this.loadData();
            return;
        }

        this.service.getSearch(this.searchkey.trim())
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (res: any) => {
                    this.dataSource.data = Array.isArray(res) ? res : [];
                    this.totalRecords = this.dataSource.data.length;
                },
                error: (error: any) => {
                    this.dataSource.data = [];
                    this.toastr.error(error?.error?.ERROR || 'Search failed', "Failed!");
                }
            });
    }
    createData(): void {
        this.isPolicyEdit = false;
        this.editId = null;
        this.policyForm.reset();
        this.submitted = false;

        // Set default values
        this.policyForm.patchValue({
            baseParam1: 'N/A',
            baseParam2: 'N/A',
            baseParam3: 'N/A',
            thParam1: 'N/A',
            thParam2: 'N/A',
            thParam3: 'N/A',
            mvnoid: 5
        });

        this.dialog.open(this.addEditDialog, {
            width: "80%",
            disableClose: true
        });
    }

    cancelForm(): void {
        this.policyForm.reset();
        this.editId = null;
        this.isPolicyEdit = false;
        this.submitted = false;
    }

    listViewData(): void {
        this.editId = null;
        this.isPolicyEdit = false;
        this.submitted = false;
        this.policyForm.reset();
        this.searchkey = '';
        this.loadData();
    }

    policyDetailFields: any[] = [
        { label: 'Policy ID', key: 'id' },
        { label: 'Name', key: 'name' },
        { label: 'Description', key: 'description' },
        { label: 'TH Policy Name', key: 'thPolicyName' },
        { label: 'Base Policy Name', key: 'basePolicyName' },
        { label: 'Base Param 1', key: 'baseParam1' },
        { label: 'Base Param 2', key: 'baseParam2' },
        { label: 'Base Param 3', key: 'baseParam3' },
        { label: 'TH Param 1', key: 'thParam1' },
        { label: 'TH Param 2', key: 'thParam2' },
        { label: 'TH Param 3', key: 'thParam3' },
        { label: 'MVNO ID', key: 'mvnoid' },
        { label: 'Type', key: 'type' },
        { label: 'QoS Speed', key: 'qosSpeed' },
        { label: 'Upstream Profile UID', key: 'upstreamProfileUID' },
        { label: 'Downstream Profile UID', key: 'downstreamProfileUID' },
        { label: 'Created By', key: 'createByName' },
        { label: 'Updated By', key: 'updateByName' },
        { label: 'Create Date', key: 'createDate' },
        { label: 'Last Modified Date', key: 'lastModifiedDate' }
    ];

    @ViewChild('addEditDialog') addEditDialog!: TemplateRef<any>;
    @ViewChild("detailsDialog") detailsDialog!: TemplateRef<any>;
    @ViewChild('confirmDialog') confirmDialog!: TemplateRef<any>;

    AllDetails(data: any): void {
        this.viewTrcData = data;
        this.dialog.open(this.detailsDialog, {
            width: "80%",
            disableClose: true
        });
    }

    // Dropdown options
    mvnoList = [
        { id: 1, name: 'MVNO 1' },
        { id: 2, name: 'MVNO 2' },
        { id: 3, name: 'MVNO 3' },
        { id: 4, name: 'MVNO 4' },
        { id: 5, name: 'MVNO 5' }
    ];

    typeList = [
        { value: 'BASIC', label: 'Basic' },
        { value: 'PREMIUM', label: 'Premium' },
        { value: 'ENTERPRISE', label: 'Enterprise' },
        { value: 'CUSTOM', label: 'Custom' }
    ];

    qosSpeedList = [
        { value: '1', label: '1 Mbps' },
        { value: '5', label: '5 Mbps' },
        { value: '10', label: '10 Mbps' },
        { value: '25', label: '25 Mbps' },
        { value: '50', label: '50 Mbps' },
        { value: '100', label: '100 Mbps' }
    ];

    // Method to test payload structure (for debugging)
    testPolicyPayloadStructure(): void {
        const expectedPayload = {
            "id": 10,
            "name": "12M/8MTUTU",
            "description": "12M/8MTUTU",
            "thPolicyName": "12M/8MTUTU",
            "baseParam1": "N/A",
            "baseParam2": "N/A",
            "baseParam3": "N/A",
            "thParam1": "N/A",
            "thParam2": "N/A",
            "thParam3": "N/A",
            "isDeleted": 0,
            "createdByStaffid": null,
            "createDate": "2025-06-14T07:38:11.000+00:00",
            "lastModifiedByStaffId": null,
            "lastModifiedDate": "2025-06-14T07:38:11.000+00:00",
            "basePolicyName": "12M/8MTUTU",
            "createByName": null,
            "updateByName": null,
            "mvnoid": 5,
            "type": null,
            "qosSpeed": "10",
            "upstreamProfileUID": null,
            "downstreamProfileUID": null
        };


        if (this.policyForm.valid) {
            const currentPayload = this.prepareFormData();

            // Compare each field
            const fieldComparison: any = {};
            Object.keys(expectedPayload).forEach(key => {
                if (key !== 'id' && key !== 'createDate' && key !== 'lastModifiedDate') { // Skip auto-generated fields
                    fieldComparison[key] = {
                        expected: expectedPayload[key as keyof typeof expectedPayload],
                        current: currentPayload[key],
                        matches: JSON.stringify(expectedPayload[key as keyof typeof expectedPayload]) === JSON.stringify(currentPayload[key])
                    };
                }
            });

        } else {
        }
    }

    // Method to populate form with test data
    populatePolicyTestData(): void {

        // Set form values to match the expected JSON
        this.policyForm.patchValue({
            name: "12M/8MTUTU",
            description: "12M/8MTUTU",
            thPolicyName: "12M/8MTUTU",
            baseParam1: "N/A",
            baseParam2: "N/A",
            baseParam3: "N/A",
            thParam1: "N/A",
            thParam2: "N/A",
            thParam3: "N/A",
            basePolicyName: "12M/8MTUTU",
            mvnoid: 5,
            type: null,
            qosSpeed: "10",
            upstreamProfileUID: null,
            downstreamProfileUID: null
        });

    }
}
