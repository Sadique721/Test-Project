import { Component, OnInit, TemplateRef, ViewChild, OnDestroy } from "@angular/core";
import { FormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator } from "@angular/material/paginator";
import { ToastrService } from "ngx-toastr";
import { MatDialog } from "@angular/material/dialog";
import { Subject, takeUntil } from "rxjs";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { VendorService } from "./vendor.service";
import { Vendor } from "./vendor.interface";

@Component({
    selector: "app-vendor",
    templateUrl: "./vendor.component.html",
    styleUrls: ["./vendor.component.css"],
    standalone: false
})
export class VendorComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    vendorForm: UntypedFormGroup;
    isVendorEdit = false;
    createAccess = true;
    editAccess = true;
    deleteAccess = true;

    statusOptions = RadiusConstants.status;
    displayedColumns: string[] = [
        "vendor_id",
        "name",
        "description",
        "status",
        "action"
    ];

    dataSource = new MatTableDataSource<Vendor>([]);

    @ViewChild(MatPaginator) paginator!: MatPaginator;
    totalRecords = 0;
    itemsPerPage = 5;
    currentPageIndex = 0;
    viewTrcData: Vendor | null = null;
    submitted = false;
    editId: number | null = null;
    searchkey: string = '';
    constructor(
        private fb: FormBuilder,
        private service: VendorService,
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
        this.vendorForm = this.fb.group({
            vendor_id: [''],
            name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
            description: ['', Validators.maxLength(500)],
            status: ['', Validators.required],
        });
    }

    loadData(): void {
        this.service.getAll()
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (res: Vendor[]) => {
                    this.dataSource.data = Array.isArray(res) ? res : [];
                    this.totalRecords = this.dataSource.data.length;
                },
                error: (error: any) => {
                    this.toastr.error(error?.error?.ERROR || 'Failed to load vendors', "Failed!");
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

        if (this.vendorForm.invalid) {
            this.markFormGroupTouched(this.vendorForm);
            this.toastr.error('Please fill all required fields correctly', 'Validation Error');
            return;
        }

        const formData = this.prepareFormData();
        const isEdit = this.editId !== null;

        if (isEdit) {
            formData.vendor_id = this.editVendorData.vendor_id
            this.updateVendor(formData, dialogRef);
        } else {
            this.createVendor(formData, dialogRef);
        }
    }

    private prepareFormData(): Vendor {
        const formValue = { ...this.vendorForm.value };

        // Ensure proper field mapping for API
        return {
            vendor_id: formValue.vendor_id || undefined,
            name: formValue.name.trim(),
            description: formValue.description?.trim() || '',
            status: formValue.status
        };
    }

    private createVendor(data: Vendor, dialogRef: any): void {
        this.service.create(data)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (response: any) => {
                    this.handleApiResponse(response, 'Vendor created successfully', dialogRef);
                },
                error: (error: any) => {
                    this.handleApiError(error, 'Failed to create vendor');
                }
            });
    }

    private updateVendor(data: Vendor, dialogRef: any): void {
        this.service.update(this.editId!, data)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (response: any) => {
                    this.handleApiResponse(response, 'Vendor updated successfully', dialogRef);
                },
                error: (error: any) => {
                    this.handleApiError(error, 'Failed to update vendor');
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

    editVendorData: any = {}
    edit(row: any): void {
        this.isVendorEdit = true;
        this.editId = row.id || null;

        this.service.getById(row.id!)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (res: any) => {
                    this.editVendorData = res
                    this.vendorForm.patchValue({
                        vendor_id: res.id,
                        name: res.name,
                        description: res.description,
                        status: res.status
                    });
                    this.submitted = false;
                    this.dialog.open(this.addEditDialog, {
                        width: "80%",
                        disableClose: true
                    });
                },
                error: (error: any) => {
                    this.toastr.error(error?.error?.ERROR || 'Failed to load vendor details', "Failed!");
                }
            });
    }

    delete(row: any): void {
        const dialogRef = this.dialog.open(this.confirmDialog, {
            width: '400px',
            data: {
                title: 'Delete Confirmation',
                description: `Do you want to delete vendor "${row.name}"?`,
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
                                    this.toastr.success('Vendor deleted successfully', 'Success!');
                                    this.refreshData();
                                }
                            },
                            error: (error: any) => {
                                this.toastr.error(error?.error?.ERROR || 'Failed to delete vendor', "Failed!");
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
                next: (res: Vendor[]) => {
                    this.dataSource.data = Array.isArray(res) ? res : [];
                    this.totalRecords = this.dataSource.data.length;
                },
                error: (error: any) => {
                    this.toastr.error(error?.error?.ERROR || 'Search failed', "Failed!");
                }
            });
    }
    createData(): void {
        this.isVendorEdit = false;
        this.editId = null;
        this.vendorForm.reset();
        this.submitted = false;

        // Set default values
        this.vendorForm.patchValue({
            status: 'ACTIVE'
        });

        this.dialog.open(this.addEditDialog, {
            width: "80%",
            disableClose: true
        });
    }

    cancelForm(): void {
        this.vendorForm.reset();
        this.editId = null;
        this.isVendorEdit = false;
        this.submitted = false;
    }

    listViewData(): void {
        this.editId = null;
        this.vendorForm.reset();
        this.searchkey = '';
        this.loadData();
    }

    vendorDetailFields: any[] = [
        { label: 'Vendor ID', key: 'vendor_id' },
        { label: 'Name', key: 'name' },
        { label: 'Description', key: 'description' },
        { label: 'Status', key: 'status' }
    ];

    @ViewChild('addEditDialog') addEditDialog!: TemplateRef<any>;
    @ViewChild("detailsDialog") detailsDialog!: TemplateRef<any>;
    @ViewChild('confirmDialog') confirmDialog!: TemplateRef<any>;

    AllDetails(data: Vendor): void {
        this.viewTrcData = data;
        this.dialog.open(this.detailsDialog, {
            width: "80%",
            disableClose: true
        });
    }

}
