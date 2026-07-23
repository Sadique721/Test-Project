import { Component, OnInit, TemplateRef, ViewChild, OnDestroy } from "@angular/core";
import { FormBuilder, FormGroup, UntypedFormArray, UntypedFormGroup, Validators } from "@angular/forms";
import { PeerConfigService } from "./peer-config.service";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator } from "@angular/material/paginator";
import { ToastrService } from "ngx-toastr";
import { MatDialog } from "@angular/material/dialog";
import { Subject, takeUntil } from "rxjs";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { DropdownService } from "../dropdown.service";

declare module '@angular/forms' {
    interface FormGroup {
        pick(keys: string[]): FormGroup;
    }
}

FormGroup.prototype.pick = function (keys: string[]): FormGroup {
    const group: { [key: string]: any } = {};

    keys.forEach(k => {
        const control = this.get(k);
        if (control && control.constructor.name !== 'FormArray') {
            group[k] = new (control.constructor as any)(control.value, control.validator, control.asyncValidator);
        }
    });

    return new FormGroup(group);
};
@Component({
    selector: "app-peerConfig",
    templateUrl: "./peerConfig.component.html",
    styleUrls: ["./peerConfig.component.css"],
    standalone: false
})
export class PeerConfigComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();
    peerForm: UntypedFormGroup;
    isPeerConfigEdit = false;
    createAccess = true;
    editAccess = true;
    deleteAccess = true;
    statusOptions = RadiusConstants.status;
    displayedColumns: string[] = [
        "nodeName",
        "realm",
        "fqdn",
        "tcpListenPort",
        "ipAddresses",
        "remoteIpAddress",
        "status",
        "action"
    ];

    dataSource: any = new MatTableDataSource<any>([]); // Use MatTableDataSource for pagination

    @ViewChild(MatPaginator) paginator!: MatPaginator;
    totalRecords = 0;
    itemsPerPage = 5;
    currentPageIndex = 0;
    viewTrcData: any;
    constructor(
        private fb: FormBuilder,
        private service: PeerConfigService,
        private toastr: ToastrService,
        private dialog: MatDialog,
        public dropdownService: DropdownService
    ) { }

    ngOnInit(): void {
        this.buildForm();
        this.loadData();
        this.dropdownService.getVerificationModeList()
        this.dropdownService.getCertificateTypeList()
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }
    ipAddresses: UntypedFormArray;
    buildForm() {
        this.peerForm = this.fb.group({
            nodeName: ["", Validators.required],
            realm: ["", Validators.required],
            fqdn: ["", [Validators.required, Validators.pattern('^([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$')]],
            // location: [""],

            // Ports
            sctpListenPort: ["", this.portValidator],
            tcpListenPort: ["", [Validators.required, this.portValidator]],
            dtlsSctpListenPort: ["", this.portValidator],
            tlsTcpListenPort: ["", this.portValidator],
            radiusUdpServerPort: ["", this.portValidator],

            // Radius Config
            enableRadiusUdpClientPorts: [true],
            radiusClientUdpPortRangeStart: ["", this.portValidator],
            radiusClientUdpPortRangeEnd: ["", this.portValidator],

            // Certificate
            verificationMode: [""],
            certificateType: [""],
            certificateName: [""],

            // IP Address Array
            ipAddresses: this.fb.array([this.fb.control("", [Validators.required, this.singleIpValidator])]),
            remoteIpAddress: ["", [Validators.required, Validators.pattern('^([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$')]],
            watchdogInterval: ["", [Validators.required, Validators.min(1)]],
            remotePort: ["", [Validators.required, this.portValidator]],

            status: ["", Validators.required]
        });

        this.ipAddresses = this.peerForm.get("ipAddresses") as UntypedFormArray;
    }
    ipListValidator(control: any) {
        if (!control.value || control.value.trim() === '') return null;

        const items = control.value.split(",").map((x: string) => x.trim()).filter((x: string) => x !== '');

        if (items.length === 0) return null;

        // Strict IP regex (0-255 per octet)
        const ipRegex = /^(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}$/;

        // Strict hostname regex (RFC compliant simplified)
        const hostnameRegex = /^(?=.{1,253}$)(?!-)([a-zA-Z0-9-]{1,63}(?<!-)\.)+[a-zA-Z]{2,63}$/;

        for (let item of items) {
            if (item && !ipRegex.test(item) && !hostnameRegex.test(item)) {
                return { invalidIpList: true, invalidItem: item };
            }
        }

        return null;
    }

    // Custom validator for port numbers
    portValidator(control: any) {
        if (!control.value) return null;
        const port = parseInt(control.value, 10);
        if (isNaN(port) || port < 1 || port > 65535) {
            return { invalidPort: true };
        }
        return null;
    }

    // Custom validator for remote IP/hostname
    remoteIpValidator(control: any) {
        if (!control.value) return null;

        const value = control.value.trim();
        const ipRegex = /^(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}$/;
        const hostnameRegex = /^(?=.{1,253}$)(?!-)([a-zA-Z0-9-]{1,63}(?<!-)\.)+[a-zA-Z]{2,63}$/;

        if (!ipRegex.test(value) && !hostnameRegex.test(value)) {
            return { invalidRemoteIp: true };
        }
        return null;
    }

    // Single IP validator for FormArray items
    singleIpValidator(control: any) {
        if (!control.value || control.value.trim() === '') return null;

        const value = control.value.trim();
        const ipRegex = /^(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}$/;
        const hostnameRegex = /^(?=.{1,253}$)(?!-)([a-zA-Z0-9-]{1,63}(?<!-)\.)+[a-zA-Z]{2,63}$/;

        if (!ipRegex.test(value) && !hostnameRegex.test(value)) {
            return { invalidIp: true };
        }
        return null;
    }
    get step1Group(): FormGroup {
        return this.peerForm.pick(['nodeName', 'realm', 'fqdn', 'status']);
    }

    get step2Group(): FormGroup {
        return this.peerForm.pick(['sctpListenPort', 'tcpListenPort', 'dtlsSctpListenPort', 'tlsTcpListenPort', 'radiusUdpServerPort', 'enableRadiusUdpClientPorts', 'radiusClientUdpPortRangeStart', 'radiusClientUdpPortRangeEnd']);
    }
    get step3Group(): FormGroup {
        return this.peerForm.pick(['verificationMode', 'certificateType', 'certificateName']);
    }

    get step4Group(): FormGroup {
        return this.peerForm.pick(['remoteIpAddress', 'remotePort', 'watchdogInterval']);
    }

    get step4Valid(): boolean {
        const basicFieldsValid = this.step4Group.valid;
        const ipAddressesValid = this.ipAddresses.valid && this.ipAddresses.length > 0;
        return basicFieldsValid && ipAddressesValid;
    }

    formatIpAddresses(ipAddresses: any): string[] {
        if (!ipAddresses) return [];

        // Handle string input
        if (typeof ipAddresses === 'string') {
            return ipAddresses.split(',').map(ip => ip.trim()).filter(ip => ip !== '');
        }

        // Handle array input
        if (Array.isArray(ipAddresses)) {
            return ipAddresses.filter(ip => ip && ip.trim() !== '');
        }

        return [];
    }
    get ipAddressControls() {
        return this.ipAddresses.controls;
    }

    addIpAddress(): void {
        this.ipAddresses.push(this.fb.control("", [this.singleIpValidator]));
    }

    removeIpAddress(index: number): void {
        if (this.ipAddresses.length > 1) {
            this.ipAddresses.removeAt(index);
        }
    }

    get ipAddressFormArray(): UntypedFormArray {
        return this.peerForm.get('ipAddresses') as UntypedFormArray;
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
                    this.toastr.error(error?.error?.ERROR || 'Failed to load data', "Failed!");
                }
            });
    }
    // Handle page change
    pageChangedTrcList(event: any) {
        this.currentPageIndex = event.pageIndex;
        this.itemsPerPage = event.pageSize;
        this.updatePagedData();
    }

    // Update current page data
    pagedData: any[] = [];
    tableData: any = [];
    updatePagedData() {
        const startIndex = this.currentPageIndex * this.itemsPerPage;
        const endIndex = startIndex + this.itemsPerPage;
        this.pagedData = this.tableData.slice(startIndex, endIndex);
    }
    onSubmit(): void {
        this.submitted = true;



        if (this.peerForm.invalid) {
            this.markFormGroupTouched(this.peerForm);
            this.toastr.error('Please fill all required fields correctly', 'Validation Error');

            return;
        }

        const formData = this.prepareFormData();
        const isEdit = this.editId != null;


        // Validate against expected structure for create
        if (!isEdit) {
            this.validateCreatePayload(formData);
        }

        if (isEdit) {
            this.updatePeerConfig(formData);
        } else {
            this.createPeerConfig(formData);
        }
    }

    private validateCreatePayload(data: any): void {
        const requiredFields = ['nodeName', 'realm', 'fqdn', 'tcpListenPort', 'ipAddresses', 'remoteIpAddress', 'watchdogInterval', 'remotePort', 'status'];
        const missingFields: string[] = [];

        requiredFields.forEach(field => {
            if (!data[field] || (Array.isArray(data[field]) && data[field].length === 0)) {
                missingFields.push(field);
            }
        });

        if (missingFields.length > 0) {
            console.error('Missing required fields:', missingFields);
        } else {
        }

        // Check IP addresses format
        if (data.ipAddresses && Array.isArray(data.ipAddresses)) {
        } else {
            console.error('IP addresses format: INCORRECT (should be array)');
        }
    }

    private getFormValidationErrors(): any {
        const formErrors: any = {};
        Object.keys(this.peerForm.controls).forEach(key => {
            const controlErrors = this.peerForm.get(key)?.errors;
            if (controlErrors) {
                formErrors[key] = controlErrors;
            }
        });

        // Check IP addresses array
        if (this.ipAddresses.errors) {
            formErrors['ipAddresses'] = this.ipAddresses.errors;
        }

        this.ipAddresses.controls.forEach((control, index) => {
            if (control.errors) {
                formErrors[`ipAddress_${index}`] = control.errors;
            }
        });

        return formErrors;
    }

    private prepareFormData(): any {
        const formValue = { ...this.peerForm.value };

        // Convert string numbers to actual numbers for ports
        const numberFields = [
            'sctpListenPort', 'tcpListenPort', 'dtlsSctpListenPort',
            'tlsTcpListenPort', 'radiusUdpServerPort', 'remotePort',
            'watchdogInterval', 'radiusClientUdpPortRangeStart',
            'radiusClientUdpPortRangeEnd'
        ];

        numberFields.forEach(field => {
            if (formValue[field] && formValue[field] !== '') {
                formValue[field] = parseInt(formValue[field], 10);
            } else {
                // Set to null or remove field if empty
                if (field === 'sctpListenPort' || field === 'dtlsSctpListenPort' ||
                    field === 'tlsTcpListenPort' || field === 'radiusUdpServerPort' ||
                    field === 'radiusClientUdpPortRangeStart' || field === 'radiusClientUdpPortRangeEnd') {
                    delete formValue[field]; // Optional fields can be removed
                } else {
                    formValue[field] = null; // Required fields set to null
                }
            }
        });

        // Keep IP addresses as array (matching JSON structure)
        if (formValue.ipAddresses && Array.isArray(formValue.ipAddresses)) {
            const validIps = formValue.ipAddresses.filter((ip: string) => ip && ip.trim() !== '');
            formValue.ipAddresses = validIps; // Keep as array, not string
        }

        // Ensure boolean fields are properly set
        if (typeof formValue.enableRadiusUdpClientPorts !== 'boolean') {
            formValue.enableRadiusUdpClientPorts = formValue.enableRadiusUdpClientPorts === true || formValue.enableRadiusUdpClientPorts === 'true';
        }

        // Remove empty string fields
        Object.keys(formValue).forEach(key => {
            if (formValue[key] === '' || formValue[key] === null || formValue[key] === undefined) {
                if (!['enableRadiusUdpClientPorts'].includes(key)) { // Don't delete boolean fields
                    delete formValue[key];
                }
            }
        });

        return formValue;
    }

    private createPeerConfig(data: any): void {

        this.service.create(data)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (response: any) => {
                    this.handleApiResponse(response, 'Peer Configuration created successfully');
                },
                error: (error: any) => {
                    console.error('CREATE ERROR - Full error object:', error);
                    console.error('CREATE ERROR - Error status:', error.status);
                    console.error('CREATE ERROR - Error message:', error.message);
                    console.error('CREATE ERROR - Error body:', error.error);
                    this.handleApiError(error, 'Failed to create peer configuration');
                }
            });
    }

    private updatePeerConfig(data: any): void {
        this.service.update(this.editId, data)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (response: any) => {
                    this.handleApiResponse(response, 'Peer Configuration updated successfully');
                },
                error: (error: any) => {
                    this.handleApiError(error, 'Failed to update peer configuration');
                }
            });
    }

    private handleApiResponse(response: any, successMessage: string): void {

        if (response?.responseCode === 406) {
            this.toastr.error(response.responseMessage || 'Operation failed', "Failed!");
        } else if (response?.responseCode === 417 || response?.responseCode === 500) {
            this.toastr.error(response.responseMessage || 'Server error occurred', "Failed!");
        } else {
            this.toastr.success(successMessage, "Success!");
            this.refreshData();
            this.cancelForm();
        }
    }

    private handleApiError(error: any, defaultMessage: string): void {
        console.error('API Error:', error);
        const errorMessage = error?.error?.ERROR || error?.error?.message || error?.message || defaultMessage;
        this.toastr.error(errorMessage, "Failed!");
    }

    private refreshData(): void {
        if (this.searchkey) {
            this.getsearch();
        } else {
            this.loadData();
        }
    }

    private markFormGroupTouched(formGroup: FormGroup): void {
        Object.keys(formGroup.controls).forEach(key => {
            const control = formGroup.get(key);
            control?.markAsTouched();

            if (control instanceof FormGroup) {
                this.markFormGroupTouched(control);
            }
        });
    }

    submitted = false;
    editId: any;
    edit(row: any): void {
        this.isPeerConfigEdit = true;
        this.editId = row.id;

        this.service.getById(row.id)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (res: any) => {
                    this.createView = true;
                    this.listView = false;

                    // Handle IP addresses conversion (both string and array formats)
                    if (res.ipAddresses) {
                        let ipArray: string[] = [];

                        if (typeof res.ipAddresses === 'string') {
                            ipArray = res.ipAddresses.split(',').map((ip: string) => ip.trim()).filter((ip: string) => ip !== '');
                        } else if (Array.isArray(res.ipAddresses)) {
                            ipArray = res.ipAddresses.filter((ip: string) => ip && ip.trim() !== '');
                        }

                        // Clear existing FormArray
                        while (this.ipAddresses.length !== 0) {
                            this.ipAddresses.removeAt(0);
                        }

                        // Add IP addresses to FormArray
                        if (ipArray.length > 0) {
                            ipArray.forEach((ip: string) => {
                                this.ipAddresses.push(this.fb.control(ip, [this.singleIpValidator]));
                            });
                        } else {
                            // Add at least one empty control
                            this.ipAddresses.push(this.fb.control('', [Validators.required, this.singleIpValidator]));
                        }

                        // Remove ipAddresses from res to avoid overwriting FormArray
                        delete res.ipAddresses;
                    }

                    this.peerForm.patchValue(res);
                    this.submitted = false;
                },
                error: (error: any) => {
                    this.toastr.error(error?.error?.ERROR || 'Failed to load peer configuration', "Failed!");
                }
            });
    }

    delete(row: any): void {
        const dialogRef = this.dialog.open(this.confirmDialog, {
            width: '400px',
            data: {
                title: 'Delete Confirmation',
                description: `Do you want to delete this Peer Configuration?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed()
            .pipe(takeUntil(this.destroy$))
            .subscribe((result) => {
                if (result === true) {
                    this.service.delete(row.id)
                        .pipe(takeUntil(this.destroy$))
                        .subscribe({
                            next: (response: any) => {
                                if (response.responseCode === 406) {
                                    this.toastr.error(response.responseMessage || 'Delete failed', 'Failed!');
                                } else if (response.responseCode === 417) {
                                    this.toastr.info(response.responseMessage || 'Delete not allowed', 'Info!');
                                } else {
                                    this.toastr.success('Deleted Successfully', 'Success!');
                                    this.refreshData();
                                }
                            },
                            error: (error: any) => {
                                this.toastr.error(error?.error?.ERROR || 'Failed to delete peer configuration', "Failed!");
                            }
                        });
                }
            });
    }
    searchkey: any = null
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
                    this.toastr.error(error?.error?.ERROR || 'Search failed', "Failed!");
                }
            });
    }
    createData(): void {
        this.listView = false;
        this.createView = true;
        this.editId = null;
        this.isPeerConfigEdit = false;
        this.submitted = false;

        // Rebuild form to ensure clean state
        this.buildForm();

        // Set default values
        this.peerForm.patchValue({
            enableRadiusUdpClientPorts: true,
            status: 'ACTIVE'
        });
    }
    cancelForm(): void {
        this.peerForm.reset();
        this.editId = null;
        this.isPeerConfigEdit = false;
        this.listView = true;
        this.createView = false;
        this.submitted = false;
    }
    listViewData() {
        this.listView = true;
        this.createView = false;
        this.searchkey = ''
        this.editId = null;
        this.peerForm.reset();

        this.loadData();
    }

    listView = true;
    createView = false;
    @ViewChild("detailsDialog") detailsDialog!: TemplateRef<any>;
    @ViewChild('confirmDialog') confirmDialog!: TemplateRef<any>;
    AllDetails(data: any): void {
        this.service.getById(data.id)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (res: any) => {
                    this.viewTrcData = res;
                    this.dialog.open(this.detailsDialog, {
                        width: "80%",
                        disableClose: true
                    });
                },
                error: (error: any) => {
                    this.toastr.error(error?.error?.ERROR || 'Failed to load details', "Failed!");
                }
            });
    }

    verificationModes = ['NO_VERIFY', 'VERIFY_PEER'];
    certificateTypes = ['X.509', 'PEM'];
    enableOptions = [
        { label: 'Enable', value: true },
        { label: 'Disable', value: false }
    ];

    // Method to test payload structure (for debugging)
    testPayloadStructure(): void {
        const expectedPayload = {
            "nodeName": "iameter.com",
            "realm": "local",
            "fqdn": "pravin.diameter.com",
            "sctpListenPort": 3868,
            "tcpListenPort": 3868,
            "dtlsSctpListenPort": 5658,
            "tlsTcpListenPort": 5658,
            "radiusUdpServerPort": 1812,
            "enableRadiusUdpClientPorts": true,
            "radiusClientUdpPortRangeStart": 2000,
            "radiusClientUdpPortRangeEnd": 2499,
            "verificationMode": "VERIFY_PEER",
            "certificateType": "X.509",
            "certificateName": "alphaCert001",
            "ipAddresses": ["pravin.diameter.com", "192.168.1.11"],
            "remoteIpAddress": "pravin.diameter.com",
            "watchdogInterval": 1000,
            "remotePort": 3868,
            "status": "ACTIVE"
        };


        if (this.peerForm.valid) {
            const currentPayload = this.prepareFormData();

            // Compare each field
            const fieldComparison: any = {};
            Object.keys(expectedPayload).forEach(key => {
                fieldComparison[key] = {
                    expected: expectedPayload[key as keyof typeof expectedPayload],
                    current: currentPayload[key],
                    matches: JSON.stringify(expectedPayload[key as keyof typeof expectedPayload]) === JSON.stringify(currentPayload[key])
                };
            });

        } else {
        }
    }

    // Method to populate form with test data
    populateTestData(): void {

        // Clear existing IP addresses
        while (this.ipAddresses.length !== 0) {
            this.ipAddresses.removeAt(0);
        }

        // Add test IP addresses
        this.ipAddresses.push(this.fb.control("pravin.diameter.com", [this.singleIpValidator]));
        this.ipAddresses.push(this.fb.control("192.168.1.11", [this.singleIpValidator]));

        // Set form values
        this.peerForm.patchValue({
            nodeName: "iameter.com",
            realm: "local",
            fqdn: "pravin.diameter.com",
            sctpListenPort: 3868,
            tcpListenPort: 3868,
            dtlsSctpListenPort: 5658,
            tlsTcpListenPort: 5658,
            radiusUdpServerPort: 1812,
            enableRadiusUdpClientPorts: true,
            radiusClientUdpPortRangeStart: 2000,
            radiusClientUdpPortRangeEnd: 2499,
            verificationMode: "VERIFY_PEER",
            certificateType: "X.509",
            certificateName: "alphaCert001",
            remoteIpAddress: "pravin.diameter.com",
            watchdogInterval: 1000,
            remotePort: 3868,
            status: "ACTIVE"
        });

    }
}
