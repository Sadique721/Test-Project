import { AfterViewInit, Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, FormArray, FormGroup } from "@angular/forms";
// Remove MessageService import since we're replacing it with toastr
// import { MessageService } from "primeng/api";
import { ConfirmationService } from "primeng/api";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { IDeactivateGuard } from "src/app/service/deactivate.service";
import { Observable, Observer } from "rxjs";
import { FultyMacManagementService } from "src/app/service/fulty-mac.service";
import { RADIUS_CONSTANTS } from "src/app/constants/aclConstants";
import { LoginService } from "src/app/service/login.service";

import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from "@angular/material/sort";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { count } from "console";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
// Add ToastrService import
import { ToastrService } from 'ngx-toastr';

@Component({
    selector: "app-fulty-mac-management",
    templateUrl: "./fulty-mac-management.component.html",
    styleUrls: ["./fulty-mac-management.component.css"],
    standalone: false
})
export class FultyMacManagementComponent implements OnInit, AfterViewInit {

    fultyMacDisplayedColumns: string[] = ['id', 'mackId', 'lastConnected', 'Action'];
    fultyMacDataSource: MatTableDataSource<any> = new MatTableDataSource();
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;

    pageSizeOptions = [5, 10, 20, 50, 100];
    dialogRef: any;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    fultyMacFormGroup: UntypedFormGroup;
    submitted: boolean = false;
    isFullyMacEdit: boolean = false;
    viewFultyMacListData: any;
    currentPageFultyMacSlab = 0;
    fultyMacitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    fultyMactotalRecords: any;
    searchFultyMacName: any = "";
    searchData: any;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    searchkey: string;
    fultyMacData: any;
    fultyMacListData: any;
    selectedFile: any;
    fileName: any;
    isFIleNameDialog: boolean = false;
    selectOptionData = [
        { label: "Single Data", value: "single" },
        { label: "Bulk Data", value: "bulk" }
    ];
    public loginService: LoginService;
    selectAction: string;

    constructor(
        private fb: UntypedFormBuilder,
        private dialog: MatDialog,
        private confirmationService: ConfirmationService,
        // Replace MessageService with ToastrService
        private toastr: ToastrService,
        private fultyMacManagementService: FultyMacManagementService,
        loginService: LoginService
    ) {
        this.loginService = loginService;
        this.createAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_FAULTY_MAC_CREATE);
        this.deleteAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_FAULTY_MAC_DELETE);
        this.editAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_FAULTY_MAC_EDIT);
    }

    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_FAULTY_MAC_EDIT) || this.loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_FAULTY_MAC_DELETE)) {
            return ['id', 'mackId', 'lastConnected', 'Action'];
        } else {
            return ['id', 'mackId', 'lastConnected'];
        }
    }

    ngOnInit(): void {
        this.fultyMacFormGroup = this.fb.group({
            id: [""],
            mackId: ["", Validators.required],
            file: ["", Validators.required]
        });

        this.searchData = {
            filters: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "any",
                    filterOperator: "equalto",
                    filterCondition: "and"
                }
            ],
            page: "",
            pageSize: ""
        };
        this.getFultyMacListData("");
        this.selectAction = "single";
        this.fultyMacDisplayedColumns = ['mackId', 'lastConnected'];
        if (this.editAccess || this.deleteAccess) {
            this.fultyMacDisplayedColumns.push('Action');
        }
    }

    canExit() {
        if (!this.fultyMacFormGroup.dirty) return true;
        {
            return Observable.create((observer: Observer<boolean>) => {
                this.confirmationService.confirm({
                    header: "Alert",
                    message: "The filled data will be lost. Do you want to continue? (Yes/No)",
                    icon: "pi pi-info-circle",
                    accept: () => {
                        observer.next(true);
                        observer.complete();
                    },
                    reject: () => {
                        observer.next(false);
                        observer.complete();
                    }
                });
                return false;
            });
        }
    }

    onFileChangeUpload(event) {
        const formData = new FormData();
        let fileArray: FileList;
        this.fultyMacFormGroup.controls.file;
        fileArray = this.fultyMacFormGroup.controls.file.value;
        if (fileArray.length > 0) {
            this.selectedFile = event.target.files[0];
            if (this.fultyMacFormGroup.controls.file) {
                if (!this.isValidXLSFile(this.selectedFile)) {
                    this.fultyMacFormGroup.controls.file.reset();
                    alert("Please upload valid .XLSX file");
                } else {
                    this.submitted = true;
                }
            }
        } else {
            alert("Please upload .XLSX file");
        }
    }

    isValidXLSFile(file: any) {
        return file.name.endsWith(".xlsx");
    }

    isValidCSVFile(file: any) {
        return file.name.endsWith(".csv");
    }

    addEditFultyMac(fullyMacId) {
        this.submitted = true;
        // if (this.fultyMacFormGroup.valid) {
        if (fullyMacId) {
            const url = "/faultyMack/updateMack";
            let mvnoId = localStorage.getItem("mvnoId");
            let fultyMacData = {
                mackId: this.fultyMacFormGroup.value.mackId,
                mvnoId: mvnoId,
                id: fullyMacId
            };
            this.fultyMacManagementService.updateMethod(url, fultyMacData).subscribe(
                (response: any) => {
                    this.submitted = false;
                    this.isFullyMacEdit = false;
                    this.fultyMacFormGroup.reset();
                    this.fultyMacManagementService.clearCache("/faultyMack/list");
                    this.toastr.success(`${response.message}`, 'Success!');

                    this.submitted = false;
                    if (this.searchkey) {
                        this.searchFultyMac();
                    } else {
                        this.getFultyMacListData("");
                    }
                    this.closeDialog();
                },
                (error: any) => {
                    if (error.error.status == 417 || error.error.status == 406) {
                        this.toastr.info(`${error.error.errorMessage}`, 'Info!');
                    } else {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
                    }
                }
            );
        } else {
            const url = "/faultyMack/save";
            let mvnoId = localStorage.getItem("mvnoId");
            // this.fultyMacData = this.fultyMacFormGroup.value;
            let fultyMacData = {
                mackId: this.fultyMacFormGroup.value.mackId,
                mvnoId: mvnoId
            };
            this.fultyMacManagementService.postMethod(url, fultyMacData).subscribe(
                (response: any) => {
                    this.submitted = false;
                    this.fultyMacFormGroup.reset();
                    this.fultyMacManagementService.clearCache("/faultyMack/list");
                    this.toastr.success(`${response.message}`, 'Success!');
                    if (this.searchkey) {
                        this.searchFultyMac();
                    } else {
                        this.getFultyMacListData("");
                    }
                    this.closeDialog();
                },
                (error: any) => {
                    if (error.error.status == 417 || error.error.status == 406) {
                        this.toastr.info(`${error.error.errorMessage}`, 'Info!');
                    } else {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
                    }
                }
            );
        }
        // }
    }

    uploadDocument() {
        this.submitted = true;
        if (this.fultyMacFormGroup.valid) {
            const formData = new FormData();
            if (this.fultyMacFormGroup.controls.file) {
                if (!this.isValidXLSFile(this.selectedFile)) {
                    this.fultyMacFormGroup.controls.file.reset();
                    alert("Please upload valid .XLSX file");
                } else {
                    formData.append("file", this.selectedFile);
                }
            }
            const url = "/faultyMack/uploadXL";
            let mvnoId = localStorage.getItem("mvnoId");
            formData.append("mvnoId", mvnoId);
            this.fultyMacManagementService.postMethodWithBulkRecord(url, formData).subscribe(
                (response: any) => {
                    this.submitted = false;
                    this.fultyMacFormGroup.reset();
                    this.fultyMacManagementService.clearCache("/faultyMack/list");
                    this.toastr.success(`${response.message}`, 'Success!');
                    if (this.searchkey) {
                        this.searchFultyMac();
                    } else {
                        this.getFultyMacListData("");
                    }
                },
                error => {
                    this.fultyMacFormGroup.controls.file.reset();
                    if (error.error.status == 400) {
                        this.toastr.info(`${error.error.errorMessage}`, 'Info!');
                    } else {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
                    }
                }
            );
        }
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageFultyMacSlab > 1) {
            this.currentPageFultyMacSlab = 1;
        }
        if (!this.searchkey) {
            this.getFultyMacListData(this.showItemPerPage);
        } else {
            this.searchFultyMac();
        }
    }

    getFultyMacListData(list) {
        let size;
        this.searchkey = "";
        let pageList = this.currentPageFultyMacSlab + 1;
        if (list) {
            size = list;
            this.fultyMacitemsPerPage = list;
        } else {
            size = this.fultyMacitemsPerPage;
        }
        let mvnoId = localStorage.getItem("mvnoId");
        const url = "/faultyMack/list?mvnoId=" + mvnoId + "&page=" + pageList + "&size=" + size;
        this.fultyMacManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.fultyMacListData = response.response.content;
                this.fultyMactotalRecords = response.response.totalElements;
                this.fultyMacDataSource.data = this.fultyMacListData || [];
                this.searchkey = "";
            },
            (error: any) => {
                this.fultyMacDataSource.data = [];
                this.fultyMactotalRecords = 0;
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    onFultyMacPageChange(event: PageEvent): void {
        this.currentPageFultyMacSlab = event.pageIndex; // API expects 1-based page numbers
        this.fultyMacitemsPerPage = event.pageSize;

        if (this.searchkey) {
            this.searchFultyMac();
        } else {
            this.getFultyMacListData("");
        }
    }

    editFullyMac(fullyMacId) {
        this.selectAction = "single";
        this.openFultyMacDialog(fullyMacId);
        if (fullyMacId) {
            const url = "/faultyMack/findById?macId=" + fullyMacId;
            this.fultyMacManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.isFullyMacEdit = true;
                    this.viewFultyMacListData = response.response;
                    this.fultyMacFormGroup.patchValue(this.viewFultyMacListData);
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    ngAfterViewInit(): void {
        this.fultyMacDataSource.sort = this.sort;;
    }

    searchFultyMac() {
        if (!this.searchkey || this.searchkey !== this.searchFultyMacName) {
            this.currentPageFultyMacSlab = 0;
        }
        this.searchkey = this.searchFultyMacName;
        if (this.showItemPerPage) {
            this.fultyMacitemsPerPage = this.showItemPerPage;
        }
        this.searchData.filters[0].filterValue = this.searchFultyMacName.trim();
        this.searchData.page = this.currentPageFultyMacSlab + 1;
        this.searchData.pageSize = this.fultyMacitemsPerPage;

        const url = "/country/search";
        this.fultyMacManagementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                this.fultyMacListData = response.countryList;
                this.fultyMactotalRecords = response.pageDetails.totalRecords;
                this.fultyMacDataSource.data = this.fultyMacListData;
            },
            (error: any) => {
                this.fultyMactotalRecords = 0;
                this.fultyMacDataSource.data = [];
                if (error.error.status == 404) {
                    this.toastr.info(`No Record Found`, 'Info!');
                    this.fultyMacListData = [];
                } else {
                    this.toastr.error(`${error.response.ERROR}`, 'Failed!');
                }
            }
        );
    }

    clearSearchFultyMac() {
        this.searchFultyMacName = "";
        this.searchkey = "";
        this.currentPageFultyMacSlab = 0;
        this.submitted = false;
        this.isFullyMacEdit = false;
        this.fultyMacFormGroup.reset();
        if (this.paginator) {
            this.paginator.pageIndex = 0;
            this.paginator.firstPage();
        }

        this.getFultyMacListData("");
    }

    deleteConfirmonFultyMac(fullyMac: any) {
        if (fullyMac.id) {
            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: "400px",
                disableClose: true,
                data: {
                    title: "Delete Confirmation",
                    description: `Are you sure you want to delete "${fullyMac.mackId}"?`,
                    yesLabel: "Confirm",
                    noLabel: "Cancel"
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.deleteFultyMac(fullyMac.id);
                } else {
                    this.toastr.info(`Delete operation was cancelled`, 'Info!');
                }
            });

            // this.confirmationService.confirm({
            //     message: "Do you want to delete this Fulty Mac Ip?",
            //     header: "Delete Confirmation",
            //     icon: "pi pi-info-circle",
            //     accept: () => {
            //         this.deleteFultyMac(fullyMacId);
            //     },
            //     reject: () => {
            //         this.toastr.info("You have rejected", 'Info!');
            //     }
            // });
        }
    }

    deleteFultyMac(fullyMacId) {
        let mvnoId = localStorage.getItem("mvnoId");
        const url = "/faultyMack/deleteMac?mackId=" + fullyMacId + "&mvnoId=" + mvnoId;

        this.fultyMacManagementService.deleteMethod(url).subscribe(
            (response: any) => {
                if (this.currentPageFultyMacSlab != 1 && this.fultyMacListData.length == 1) {
                    this.currentPageFultyMacSlab = this.currentPageFultyMacSlab - 1;
                    if (this.paginator) {
                        this.paginator.pageIndex = this.currentPageFultyMacSlab;
                    }
                }
                this.clearSearchFultyMac();
                this.toastr.success(`Successfully Deleted`, 'Success!');
                if (this.searchkey) {
                    this.searchFultyMac();
                } else {
                    this.getFultyMacListData("");
                }
            },
            (error: any) => {
                if (error.error.status == 417 || error.error.status == 405 || error.error.status == 406) {
                    this.toastr.info(`${error.error.ERROR}`, 'Info!');
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            }
        );
    }

    closeDialog() {
        this.resetForm();
        if (this.dialogRef) {
            this.dialogRef.close();
        }
    }

    resetForm() {
        this.submitted = false;
        this.isFullyMacEdit = false;
        this.selectAction = "single";
        this.fultyMacFormGroup.reset();
        this.selectedFile = null;
    }

    pageChangedFultyMacList(pageNumber) {
        this.currentPageFultyMacSlab = pageNumber - 1;
        if (this.searchkey && this.searchkey.trim()) {
            this.searchFultyMac();
        } else {
            this.getFultyMacListData("");
        }
    }

    selectActionData(event) {
        this.selectAction = event.value;
    }

    @ViewChild("fultyMacDialog") fultyMacDialog!: TemplateRef<any>;
    openFultyMacDialog(fultyMac?: any) {
        this.resetForm(); // Reset form when opening dialog

        this.dialogRef = this.dialog.open(this.fultyMacDialog, {
            width: "900px",
            disableClose: true
        });

        this.dialogRef.afterClosed().subscribe(result => {
            this.resetForm(); // Reset form when dialog is closed
        });
    }
}
