import { Component, OnInit, ElementRef, ViewChild, AfterViewInit } from "@angular/core";
import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { RadiusUtility } from "src/app/RadiusUtils/RadiusUtility";
import { BankService } from "src/app/service/bank.service";
import { ToastrService } from 'ngx-toastr';
import { bankManagement } from "src/app/components/model/bankManagement";
import { LoginService } from "../../service/login.service";
import { AclClassConstants } from "../../constants/aclClassConstants";
import { AclConstants } from "../../constants/aclOperationConstants";
import { Regex } from "src/app/constants/regex";
import { Observable, Observer } from "rxjs";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { CountryManagementService } from "src/app/service/country-management.service";
import { MASTERS } from "src/app/constants/aclConstants";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from "@angular/material/sort";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { MatDialog } from "@angular/material/dialog";
import { MatDialogRef } from '@angular/material/dialog';


@Component({
    selector: "app-bankmanagement",
    templateUrl: "./bankmanagement.component.html",
    styleUrls: ["./bankmanagement.component.css"],
    standalone: false
})
export class BankmanagementComponent implements OnInit {
    dataSource = new MatTableDataSource<any>([]);
    displayedColumns = ['id', 'bankname', 'banktype', 'accountnum', 'status', 'action'];
    @ViewChild('createEditBankDialog') createEditBankDialog: any;
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;
    BankFormGroup: UntypedFormGroup;
    dialogRef!: MatDialogRef<any>;
    submitted: boolean = false;
    bankData: bankManagement;
    BankListData: any;
    BankFormArray: UntypedFormArray;
    isBankEdit: boolean = false;
    viewBankListData: any;
    bankTypeData: any;
    currentpage = 1;
    itemPerpageBank = RadiusConstants.ITEMS_PER_PAGE;
    BanktotalRecords: number;
    searchBankName: any = "";
    searchData: any;
    account = "Account Number";
    AclClassConstants;
    AclConstants;

    statusOptions = RadiusConstants.status;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 0;
    searchkey: string;
    editMode = false;
    hideSearchBar = true;
    listView = true;
    createView = false;
    detailView = false;
    products: any[] = [];
    warehouses: any[] = [];
    staffList: any[] = [];
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    public loginService: LoginService;
    title: string;
    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private dialog: MatDialog,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private bankService: BankService,
        loginService: LoginService,
        private toastr: ToastrService,
        public commondropdownService: CommondropdownService,
        public countrymgmtSerivce: CountryManagementService
    ) {
        this.createAccess = loginService.hasPermission(MASTERS.BANK_CREATE);
        this.deleteAccess = loginService.hasPermission(MASTERS.BANK_DELETE);
        this.editAccess = loginService.hasPermission(MASTERS.BANK_EDIT);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
    }

    ngOnInit(): void {
        this.BankFormGroup = this.fb.group({
            accountnum: ["", WhiteeSpaceValidator.cannotContainSpace],
            bankholdername: [""],
            bankname: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            ifsccode: [""],
            mvnoId: [""],
            status: ["", Validators.required],
            bankcode: [""],
            banktype: ["", Validators.required],
        });

        this.searchData = {
            filter: [
                {
                    filterValue: "",
                },
            ],
        };

        this.getBankListData();
        this.BankFormGroup.controls.accountnum.enable();
        this.getBankTypeList();
    }
    selectAllbankTypeEvent(event) {
        if (event.value == "operator") {
            this.account = "Account Number*";
            this.BankFormGroup.get("accountnum").setValidators([
                Validators.required,
                Validators.pattern(Regex.alphaNUmeric),
            ]);
            this.BankFormGroup.get("accountnum").updateValueAndValidity();
        } else {
            this.account = "Account Number";
            this.BankFormGroup.get("accountnum").clearValidators();
            this.BankFormGroup.get("accountnum").updateValueAndValidity();
        }
    }

    onCancel(): void {
        if (this.dialogRef) {
            this.dialogRef.close();
        }
    }
    createBankDialog(): void {
        this.editMode = false;
        this.submitted = false;
        this.BankFormGroup.reset();
        this.hideSearchBar = false;

        if (!this.bankTypeData || this.bankTypeData.length === 0) {
            this.getBankTypeList();
        }
        this.BankFormGroup.get('banktype').enable();
        this.dialogRef = this.dialog.open(this.createEditBankDialog, {
            width: '800px',
            data: { editMode: this.editMode }
        });

        this.dialogRef.afterClosed().subscribe((result) => {
            if (result) {
                this.clearSearchBank();
                this.listView = true;
                this.createView = false;
                this.detailView = false;
            }
        });
    }

    deleteConfirmonCountryDialog(item: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete ' + this.title,
                description: `Are you sure you want to delete "${item.bankname}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteBank(item);
            }
        });
    }

    addEditBank(bankId) {
        this.submitted = true;
        this.markFormGroupTouched(this.BankFormGroup);
        if (this.BankFormGroup.valid) {
            if (bankId) {
                const url = "/bankManagement/update";
                this.bankData = this.BankFormGroup.value;
                this.bankData.id = bankId;
                this.bankData.isDeleted = false;
                this.bankService.postMethod(url, this.bankData).subscribe(
                    (response: any) => {
                        if (response.responseCode == 200) {
                            this.cancelEditBank();
                            this.toastr.success(`Successfully updated`, 'Success!');
                            this.dialog?.closeAll();
                            this.submitted = false;
                            if (this.dialogRef) {
                                this.dialogRef.close(true);
                            }
                            if (this.searchkey) {
                                this.searchBank();
                            } else {
                                this.getBankListData();
                            }
                        } else if (response.responseCode == 406) {
                            this.toastr.info(response.responseMessage, 'Info!');
                        }
                    },
                    (error: any) => {

                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    }
                );
            } else {
                const url = "/bankManagement/save";
                this.bankData = this.BankFormGroup.value;

                this.bankData.isDeleted = false;
                this.bankService.postMethod(url, this.bankData).subscribe(
                    (response: any) => {
                        if (response.responseCode == 200) {
                            this.submitted = false;
                            this.BankFormGroup.reset();
                            this.BankFormGroup.controls.status.setValue("");
                            this.toastr.success(`${response.responseMessage}`, 'Success!');
                            this.dialog?.closeAll();
                            if (this.dialogRef) {
                                this.dialogRef.close(true);
                            }
                            if (this.searchkey) {
                                this.searchBank();
                            } else {
                                this.getBankListData();
                            }
                        } else if (response.responseCode == 406) {
                            this.toastr.info(response.responseMessage, 'Info!');

                        } else {
                            (error: any) => {

                                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                            }
                        }
                    },
                    (error: any) => {

                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    }
                );
            }
        }
    }

    cancelEditBank() {
        this.submitted = false;
        this.isBankEdit = false;
        this.BankFormGroup.reset();
        this.BankFormGroup.controls.status.setValue("");
        this.BankFormGroup.controls.accountnum.enable();
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentpage > 1) {
            this.currentpage = 1;
        }
        if (!this.searchkey) {
            this.getBankListData();
        } else {
            this.searchBank();
        }
    }

    getBankData(list) {
        let size;
        this.searchkey = "";

        let pageList = this.currentpage;
        if (list) {
            size = list;
            this.itemPerpageBank = list;
        } else {
            if (this.showItemPerPage == 0) {
                this.itemPerpageBank = this.pageITEM;
            } else {
                this.itemPerpageBank = this.showItemPerPage;
            }
        }

        const url = "/bankManagement/all";
        this.bankService.getMethod(url).subscribe(
            (response: any) => {

                this.BankListData = response.dataList;
                this.BanktotalRecords = response.totalRecords;

                this.searchkey = "";
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    getBankListData(): void {
        const data = {
            page: this.currentpage,
            pageSize: this.itemPerpageBank,
            sortBy: "bankid",
            sortOrder: 0,
        };
        this.bankService.postMethod("/bankManagement", data).subscribe({
            next: (response: any) => {
                this.BankListData = response.dataList ?? [];
                this.BanktotalRecords = response.totalRecords ?? 0;
                this.dataSource.data = this.BankListData;
                if (this.paginator) {
                    setTimeout(() => {
                        this.paginator.pageIndex = this.currentpage - 1;
                        this.paginator.length = this.BanktotalRecords;
                    });
                }
            },
            error: (error) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        });
    }


    private markFormGroupTouched(formGroup: UntypedFormGroup) {
        Object.values(formGroup.controls).forEach(control => {
            control.markAsTouched();
            if ((control as any).controls) {
                this.markFormGroupTouched(control as UntypedFormGroup);
            }
        });
    }

    editBank(bankId: string): void {
        if (!bankId) return;
        const url = "/bankManagement/" + bankId;
        this.bankService.getMethod(url).subscribe({
            next: (response: any) => {
                this.isBankEdit = true;
                this.viewBankListData = response.data;
                this.BankFormGroup.patchValue(this.viewBankListData);

                Object.keys(this.BankFormGroup.controls).forEach(controlName => {
                    this.BankFormGroup.controls[controlName].enable();
                });
                this.BankFormGroup.get('banktype').disable();
                this.BankFormGroup.get('accountnum').disable();
                this.dialogRef = this.dialog.open(this.createEditBankDialog, {
                    width: '800px',
                    data: { editMode: this.isBankEdit }
                });

                this.dialogRef.afterClosed().subscribe(result => {
                    if (result) {
                        this.clearSearchBank();
                        this.isBankEdit = false;
                        this.listView = true;
                        this.createView = false;
                        this.detailView = false;
                    }
                });
            },
            error: (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        });
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    searchBank(): void {
        if (!this.searchBankName?.trim()) {
            (response: any) => {
                this.toastr.info(response.responseMessage, "Please add bank name for search");
            }
            return;
        }
        if (!this.searchkey || this.searchkey !== this.searchBankName) {
            this.currentpage = 1;
        }
        this.searchkey = this.searchBankName;
        if (this.showItemPerPage) {
            this.itemPerpageBank = this.showItemPerPage;
        }
        this.searchData.filter[0].filterValue = this.searchBankName.trim();

        const url =
            `/bankManagement/search?page=${this.currentpage}&pageSize=${this.itemPerpageBank}&sortBy=id&sortOrder=0`;

        this.bankService.postMethod(url, this.searchData).subscribe({
            next: (response: any) => {
                if (response.responseCode === 200) {
                    this.BankListData = response.dataList ?? [];
                    this.BanktotalRecords = response.totalRecords ?? 0;
                    this.dataSource.data = this.BankListData;
                } else if (response.responseCode === 404) {
                    this.toastr.info(response.responseMessage, 'Info!');

                    this.BankListData = [];
                    this.BanktotalRecords = 0;
                    this.dataSource.data = this.BankListData;
                }
            },
            error: (error) => {
                this.BanktotalRecords = 0;
                this.BankListData = [];
                this.dataSource.data = [];

                if (error.error.status === 404) {
                    (response: any) => {
                        this.toastr.info(response.responseMessage, "Info!");
                    }
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            }
        });
    }

    clearSearchBank() {
        this.searchBankName = "";
        this.searchkey = "";
        this.getBankListData();
        this.submitted = false;
        this.isBankEdit = false;
        this.BankFormGroup.reset();
        this.BankFormGroup.controls.status.setValue("");
        this.BankFormGroup.controls.accountnum.enable();
    }

    canExit() {
        if (!this.BankFormGroup.dirty) return true;
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
                    },
                });
                return false;
            });
        }
    }

    deleteBank(bankData: any) {
        const url = "/bankManagement/delete";
        bankData.isDeleted = true;
        this.bankService.postMethod(url, bankData).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    if (this.currentpage != 1 && this.BankListData.length == 1) {
                        this.currentpage = this.currentpage - 1;
                    }
                    this.toastr.success(`${response.responseMessage}`, 'Success!');
                    this.dialog?.closeAll();
                    this.clearSearchBank();
                    if (this.searchkey) {
                        this.searchBank();
                    } else {
                        this.getBankListData();
                    }
                } else if (response.responseCode == 406) {
                    this.toastr.info(response.responseMessage, 'Info!');
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    refreshBankList(): void {
        if (this.searchkey) {
            this.searchBank();
        } else {
            this.getBankListData();
        }
    }

    pageChangedBankList(event: PageEvent): void {
        this.currentpage = event.pageIndex + 1;
        this.itemPerpageBank = event.pageSize;
        if (this.searchkey) {
            this.searchBank();
        } else {
            this.getBankListData();
        }
    }


    getBankTypeList() {
        const url = "/commonList/banktype";
        this.countrymgmtSerivce.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.bankTypeData = response.dataList;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
}
