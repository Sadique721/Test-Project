import { Component, OnInit, TemplateRef, ViewChild, OnDestroy } from "@angular/core";
import { FormBuilder, FormGroup, FormArray, Validators, FormControl } from "@angular/forms";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator } from "@angular/material/paginator";
import { ToastrService } from "ngx-toastr";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { Subject } from "rxjs";
import { takeUntil } from "rxjs/operators";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { PacketMappingService } from "./packetMapping.service";
import { PacketMapping, PacketMappingDetail } from "./packetMapping.interface";
import { DropdownService } from "../dropdown.service";

@Component({
    selector: "app-packetMapping",
    templateUrl: "./packetMapping.component.html",
    styleUrls: ["./packetMapping.component.css"],
    standalone: false
})
export class PacketMappingComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    packetForm: FormGroup;
    isPacketEdit = false;
    createAccess = true;
    editAccess = true;
    deleteAccess = true;

    displayedColumns: string[] = [
        "requestType",
        "responseType",
        "application",
        "vendorId",
        "enabled",
        "action"
    ];

    dataSource = new MatTableDataSource<PacketMapping>([]);
    tableData: PacketMapping[] = [];
    pagedData: PacketMapping[] = [];

    @ViewChild(MatPaginator) paginator!: MatPaginator;
    totalRecords = 0;
    itemsPerPage = 5;
    currentPageIndex = 0;
    viewTrcData: PacketMapping | null = null;

    // Form options
    applicationOptions = ['GX', 'RX', 'SY', 'S6A', 'S6B'];
    valueTypeOptions = ['STRING', 'INTEGER', 'UNSIGNED32', 'FLOAT', 'GROUPED'];

    // Table configuration for details
    detailsDisplayedColumns: string[] = ['sequence', 'requestAvp', 'responseAvp', 'valueExpression', 'valueType', 'mandatory', 'actions'];
    detailsViewDisplayedColumns: string[] = ['sequence', 'requestAvp', 'responseAvp', 'valueExpression', 'valueType', 'mandatory'];
    detailsTableDataSource = new MatTableDataSource<any>([]);

    // Detail fields for display
    packetDetailFields = [
        { label: 'Request Type', key: 'requestType' },
        { label: 'Response Type', key: 'responseType' },
        { label: 'Application', key: 'application' },
        { label: 'Vendor ID', key: 'vendorId' },
        { label: 'Description', key: 'description' },
        { label: 'Enabled', key: 'enabled' },
        { label: 'Created By', key: 'createdBy' },
        { label: 'Modified By', key: 'modifiedBy' }
    ];

    constructor(
        private fb: FormBuilder,
        private service: PacketMappingService,
        private toastr: ToastrService,
        private dialog: MatDialog,
        public dropdownService: DropdownService
    ) { }

    ngOnInit(): void {
        this.buildForm();
        this.loadData();
        this.dropdownService.getvendorData()
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    buildForm(): void {
        this.packetForm = this.fb.group({
            requestType: ['', Validators.required],
            responseType: ['', Validators.required],
            application: ['', Validators.required],
            vendorId: [0, [Validators.required, Validators.min(0)]],
            description: ['', Validators.required],
            enabled: [true],
            details: this.fb.array([])
        });
    }

    get detailsArray(): FormArray {
        return this.packetForm.get('details') as FormArray;
    }

    createDetailFormGroup(detail?: PacketMappingDetail): FormGroup {
        return this.fb.group({
            requestAvp: [detail?.requestAvp || ''],
            responseAvp: [detail?.responseAvp || ''],
            valueExpression: [detail?.valueExpression || '', Validators.required],
            valueType: [detail?.valueType || 'STRING', Validators.required],
            sequence: [detail?.sequence || 1, [Validators.required, Validators.min(1)]],
            mandatory: [detail?.mandatory || false]
        });
    }

    addDetail(): void {
        const detailGroup = this.createDetailFormGroup();
        this.detailsArray.push(detailGroup);
        this.updateDetailsTableDataSource();
    }

    removeDetail(index: number): void {
        if (this.detailsArray.length > 1) {
            this.detailsArray.removeAt(index);
            this.updateDetailsTableDataSource();
        } else {
            this.toastr.warning('At least one detail is required', 'Warning');
        }
    }

    getDetailFormGroup(index: number): FormGroup {
        return this.detailsArray.at(index) as FormGroup;
    }

    updateDetailsTableDataSource(): void {
        // Create dummy data for table rows (the actual form controls are bound in the template)
        const tableData = this.detailsArray.controls.map((_, index) => ({ index }));
        this.detailsTableDataSource.data = tableData;
    }

    loadData(): void {
        this.service.getAll()
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (response: any) => {
                    if (response && Array.isArray(response)) {
                        this.tableData = response;
                        this.totalRecords = response.length;
                        this.updatePagedData();
                    } else if (response?.data && Array.isArray(response.data)) {
                        this.tableData = response.data;
                        this.totalRecords = response.data.length;
                        this.updatePagedData();
                    } else {
                        this.tableData = [];
                        this.totalRecords = 0;
                        this.updatePagedData();
                    }
                },
                error: (error) => {
                    console.error('Load data error:', error);
                    this.toastr.error(error?.error?.message || 'Failed to load data', 'Error');
                    this.tableData = [];
                    this.totalRecords = 0;
                    this.updatePagedData();
                }
            });
    }
    pageChangedTrcList(event: any): void {
        this.currentPageIndex = event.pageIndex;
        this.itemsPerPage = event.pageSize;
        this.updatePagedData();
    }

    updatePagedData(): void {
        const startIndex = this.currentPageIndex * this.itemsPerPage;
        const endIndex = startIndex + this.itemsPerPage;
        this.pagedData = this.tableData.slice(startIndex, endIndex);
        this.dataSource.data = this.pagedData;
    }
    onSubmit(dialogRef: MatDialogRef<any>): void {
        this.submitted = true;



        if (this.packetForm.invalid) {
            this.markFormGroupTouched(this.packetForm);
            this.toastr.error('Please fill all required fields correctly', 'Validation Error');
            return;
        }

        const formData = this.prepareFormData();
        const isEdit = this.editId !== null;


        // Validate against expected structure
        if (!isEdit) {
            this.validateCreatePayload(formData);
        }

        if (isEdit) {
            this.updatePacketMapping(formData, dialogRef);
        } else {
            this.createPacketMapping(formData, dialogRef);
        }
    }

    private prepareFormData(): any {
        const formValue = { ...this.packetForm.value };

        // Prepare the payload to match the expected JSON structure
        const payload: any = {
            requestType: formValue.requestType?.trim() || '',
            responseType: formValue.responseType?.trim() || '',
            application: formValue.application || '',
            vendorId: formValue.vendorId || 0,
            description: formValue.description?.trim() || '',
            enabled: formValue.enabled !== undefined ? formValue.enabled : true,
            details: []
        };

        // Process details array
        if (formValue.details && Array.isArray(formValue.details)) {
            payload.details = formValue.details.map((detail: any, index: number) => {
                const processedDetail: any = {
                    valueExpression: detail.valueExpression?.trim() || '',
                    valueType: detail.valueType || 'STRING',
                    sequence: detail.sequence ? parseInt(detail.sequence, 10) : index + 1,
                    mandatory: detail.mandatory !== undefined ? detail.mandatory : false
                };

                // Only include AVP fields if they have values
                if (detail.requestAvp && detail.requestAvp.trim()) {
                    processedDetail.requestAvp = detail.requestAvp.trim();
                }
                if (detail.responseAvp && detail.responseAvp.trim()) {
                    processedDetail.responseAvp = detail.responseAvp.trim();
                }

                return processedDetail;
            });
        }

        // Only include ID for update operations
        if (this.editId !== null) {
            payload.id = this.editId;
        }

        return payload;
    }

    private validateCreatePayload(data: any): void {
        const requiredFields = ['requestType', 'responseType', 'application', 'description'];
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

        // Validate details array
        if (!data.details || !Array.isArray(data.details) || data.details.length === 0) {
            console.error('Details array is missing or empty');
        } else {
            data.details.forEach((detail: any, index: number) => {
                if (!detail.valueExpression || !detail.valueType) {
                    console.error(`Detail ${index + 1} missing required fields:`, detail);
                }
            });
        }
    }

    private getFormValidationErrors(): any {
        const formErrors: any = {};
        Object.keys(this.packetForm.controls).forEach(key => {
            const controlErrors = this.packetForm.get(key)?.errors;
            if (controlErrors) {
                formErrors[key] = controlErrors;
            }
        });

        // Check details array
        if (this.detailsArray.errors) {
            formErrors['details'] = this.detailsArray.errors;
        }

        this.detailsArray.controls.forEach((control, index) => {
            if (control.errors) {
                formErrors[`detail_${index}`] = control.errors;
            }
        });

        return formErrors;
    }

    private createPacketMapping(data: any, dialogRef: MatDialogRef<any>): void {

        this.service.create(data)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (response) => {
                    this.handleApiResponse(response, 'Packet mapping created successfully', dialogRef);
                },
                error: (error) => {
                    console.error('PACKET MAPPING CREATE ERROR - Full error object:', error);
                    console.error('PACKET MAPPING CREATE ERROR - Error status:', error.status);
                    console.error('PACKET MAPPING CREATE ERROR - Error message:', error.message);
                    console.error('PACKET MAPPING CREATE ERROR - Error body:', error.error);
                    this.handleApiError(error, 'Failed to create packet mapping');
                }
            });
    }

    private updatePacketMapping(data: any, dialogRef: MatDialogRef<any>): void {

        this.service.update(this.editId!, data)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (response) => {
                    this.handleApiResponse(response, 'Packet mapping updated successfully', dialogRef);
                },
                error: (error) => {
                    console.error('PACKET MAPPING UPDATE ERROR - Full error object:', error);
                    this.handleApiError(error, 'Failed to update packet mapping');
                }
            });
    }

    private handleApiResponse(response: any, successMessage: string, dialogRef: MatDialogRef<any>): void {

        if (response?.responseCode === 406) {
            this.toastr.error(response.responseMessage || 'Operation failed', 'Error');
        } else if (response?.responseCode === 417 || response?.responseCode === 500) {
            this.toastr.error(response.responseMessage || 'Server error occurred', 'Error');
        } else {
            this.toastr.success(successMessage, 'Success');
            dialogRef.close();
            this.loadData();
            this.cancelForm();
        }
    }

    private handleApiError(error: any, defaultMessage: string): void {
        console.error('API Error:', error);
        const errorMessage = error?.error?.ERROR || error?.error?.message || error?.message || defaultMessage;
        this.toastr.error(errorMessage, 'Error');
    }

    private markFormGroupTouched(formGroup: FormGroup): void {
        Object.keys(formGroup.controls).forEach(key => {
            const control = formGroup.get(key);
            if (control instanceof FormControl) {
                control.markAsTouched();
            } else if (control instanceof FormGroup) {
                this.markFormGroupTouched(control);
            } else if (control instanceof FormArray) {
                control.controls.forEach(arrayControl => {
                    if (arrayControl instanceof FormGroup) {
                        this.markFormGroupTouched(arrayControl);
                    }
                });
            }
        });
    }

    submitted = false;
    editId: number | null = null;
    searchkey = '';

    edit(row: PacketMapping): void {
        this.editId = row.id!;
        this.isPacketEdit = true;

        // this.service.getById(row.id!)
        //     .pipe(takeUntil(this.destroy$))
        //     .subscribe({
        //         next: (response: any) => {
        const data = row;

        // Clear existing details
        while (this.detailsArray.length !== 0) {
            this.detailsArray.removeAt(0);
        }

        // Populate form
        this.packetForm.patchValue({
            requestType: data.requestType,
            responseType: data.responseType,
            application: data.application,
            vendorId: data.vendorId,
            description: data.description,
            enabled: data.enabled
        });

        // Add details
        if (data.details && Array.isArray(data.details)) {
            data.details.forEach((detail: PacketMappingDetail) => {
                this.detailsArray.push(this.createDetailFormGroup(detail));
            });
        }

        // Update table data source after loading details
        this.updateDetailsTableDataSource();

        this.dialog.open(this.addEditDialog, {
            width: "95%",
            maxHeight: "95vh",
            disableClose: true
        });
        //     },
        //     error: (error) => {
        //         console.error('Edit error:', error);
        //         this.toastr.error(error?.error?.message || 'Failed to load packet mapping', 'Error');
        //     }
        // });
    }

    delete(row: PacketMapping): void {
        const dialogRef = this.dialog.open(this.confirmDialog, {
            width: '400px',
            data: {
                title: 'Delete Confirmation',
                description: `Do you want to delete packet mapping "${row.requestType}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe((result) => {
            if (result === true) {
                this.service.delete(row.id!)
                    .pipe(takeUntil(this.destroy$))
                    .subscribe({
                        next: (response) => {
                            if (response?.responseCode === 406 || response?.responseCode === 417) {
                                this.toastr.error(response.responseMessage || 'Delete failed', 'Error');
                            } else {
                                this.toastr.success('Packet mapping deleted successfully', 'Success');
                                this.loadData();
                            }
                        },
                        error: (error) => {
                            console.error('Delete error:', error);
                            this.toastr.error(error?.error?.message || 'Failed to delete packet mapping', 'Error');
                        }
                    });
            }
        });
    }
    searchkey1: any
    getsearch(): void {
        // if (!this.searchkey?.trim()) {
        //     this.loadData();
        //     return;
        // }

        this.service.getSearch(this.searchkey.trim(), this.searchkey1.trim())
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (response: any) => {
                    if (response && Array.isArray(response)) {
                        this.tableData = response;
                        this.totalRecords = response.length;
                        this.updatePagedData();
                    } else if (response?.data && Array.isArray(response.data)) {
                        this.tableData = response.data;
                        this.totalRecords = response.data.length;
                        this.updatePagedData();
                    } else {
                        this.tableData = [];
                        this.totalRecords = 0;
                        this.updatePagedData();
                    }
                },
                error: (error) => {
                    console.error('Search error:', error);
                    this.toastr.error(error?.error?.message || 'Search failed', 'Error');
                }
            });
    }

    createData(): void {
        this.editId = null;
        this.isPacketEdit = false;
        this.buildForm();
        this.submitted = false;

        // Add one default detail
        this.addDetail();

        this.dialog.open(this.addEditDialog, {
            width: "95%",
            maxHeight: "95vh",
            disableClose: true
        });
    }

    cancelForm(): void {
        this.buildForm();
        this.editId = null;
        this.isPacketEdit = false;
        this.submitted = false;
    }

    listViewData(): void {
        this.searchkey = '';
        this.cancelForm();
        this.loadData();
    }

    @ViewChild('addEditDialog') addEditDialog!: TemplateRef<any>;
    @ViewChild("detailsDialog") detailsDialog!: TemplateRef<any>;
    @ViewChild('confirmDialog') confirmDialog!: TemplateRef<any>;

    AllDetails(data: PacketMapping): void {
        this.viewTrcData = data;
        this.dialog.open(this.detailsDialog, {
            width: "90%",
            maxHeight: "90vh",
            disableClose: true
        });
    }

    formatDetailsForDisplay(details: PacketMappingDetail[]): string {
        if (!details || details.length === 0) return 'No details';
        return `${details.length} detail(s)`;
    }

    getBadgeClass(enabled: boolean): string {
        return enabled ? 'badge-success' : 'badge-danger';
    }

    getBadgeText(enabled: boolean): string {
        return enabled ? 'Enabled' : 'Disabled';
    }

    // Method to test payload structure (for debugging)
    testPacketMappingPayloadStructure(): void {
        const expectedPayload = {
            "requestType": "Credit-Control-Request",
            "responseType": "Credit-Control-Answer",
            "application": "GX",
            "vendorId": 0,
            "description": "Vendor Cisco",
            "enabled": true,
            "details": [
                {
                    "requestAvp": "Session-Id",
                    "responseAvp": "Session-Id",
                    "valueExpression": "${request.Session-Id}",
                    "valueType": "STRING",
                    "sequence": 1,
                    "mandatory": true
                },
                {
                    "requestAvp": "CC-Request-Number",
                    "responseAvp": "CC-Request-Number",
                    "valueExpression": "${request.CC-Request-Number}",
                    "valueType": "INTEGER",
                    "sequence": 2,
                    "mandatory": true
                },
                {
                    "responseAvp": "Origin-Host",
                    "valueExpression": "${system.originHost}",
                    "valueType": "STRING",
                    "sequence": 3,
                    "mandatory": false
                }
            ]
        };


        if (this.packetForm.valid) {
            const currentPayload = this.prepareFormData();

            // Compare each field
            const fieldComparison: any = {};
            Object.keys(expectedPayload).forEach(key => {
                if (key !== 'details') {
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
    populatePacketMappingTestData(): void {

        // Clear existing details
        while (this.detailsArray.length !== 0) {
            this.detailsArray.removeAt(0);
        }

        // Set basic form values
        this.packetForm.patchValue({
            requestType: "Credit-Control-Request",
            responseType: "Credit-Control-Answer",
            application: "GX",
            vendorId: 0,
            description: "Vendor Cisco",
            enabled: true
        });

        // Add test details
        const testDetails: PacketMappingDetail[] = [
            {
                requestAvp: "Session-Id",
                responseAvp: "Session-Id",
                valueExpression: "${request.Session-Id}",
                valueType: "STRING",
                sequence: 1,
                mandatory: true
            },
            {
                requestAvp: "CC-Request-Number",
                responseAvp: "CC-Request-Number",
                valueExpression: "${request.CC-Request-Number}",
                valueType: "INTEGER",
                sequence: 2,
                mandatory: true
            },
            {
                requestAvp: "",
                responseAvp: "Origin-Host",
                valueExpression: "${system.originHost}",
                valueType: "STRING",
                sequence: 3,
                mandatory: false
            }
        ];

        testDetails.forEach(detail => {
            this.detailsArray.push(this.createDetailFormGroup(detail));
        });

        // Update table data source
        this.updateDetailsTableDataSource();

    }
}
