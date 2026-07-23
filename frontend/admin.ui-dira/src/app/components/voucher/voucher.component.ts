import { DatePipe } from "@angular/common";
import { Component, Input, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { FormGroup, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
import { MessageService } from "primeng/api";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { VoucherService } from "src/app/service/voucher.service";
import * as XLSX from "xlsx";
import { countries } from "../model/country";
import { LoginService } from "src/app/service/login.service";
import { PRODUCTS } from "src/app/constants/aclConstants";
import { ToastrService } from 'ngx-toastr';
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatDialog } from '@angular/material/dialog';


@Component({
    selector: "app-voucher",
    templateUrl: "./voucher.component.html",
    styleUrls: ["./voucher.component.css"],
    standalone: false
})
export class VoucherComponent implements OnInit {
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @Input() totalElements: number = 0;
    @Input() searchVoucherForm!: FormGroup;

    displayedColumns = [
        'select',
        'voucherName',
        'serialNumber',
        'batchName',
        'plan',
        'code',
        'status',
        'price',
        'createdOn',
        'action'
    ];


    @ViewChild('SendSMSDialogTemplate') SendSMSDialogTemplate!: TemplateRef<any>;

    dataSource: any;
    sort: any;
    @Input() set batchData2(value: any[]) {
        if (value) {
            this.dataSource = new MatTableDataSource(value);
            if (this.sort) this.dataSource.sort = this.sort;
            // if (this.paginator) this.dataSource.paginator = this.paginator;
        }
    }

    checked: boolean = false;
    allChecked: boolean = false;
    selectedCities: string[] = [];
    public model: any;
    // searchVoucherForm: UntypedFormGroup;
    countries: any = countries;
    customerGroupForm: UntypedFormGroup;
    allIsChecked: boolean = false;
    modalToggle: boolean = false;
    //Used for pagination
    // totalElements: number;
    currentPage = 1;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    accessData: any = JSON.parse(localStorage.getItem("accessData"));
    voucherIdSms = "";
    voucherCodeSms: String = "";
    fileName = "voucher.xlsx";
    submitted: boolean;
    showBatch: boolean;
    batchData: string[] = [];
    status = [
        { label: "GENERATED" },
        { label: "ACTIVE" },
        { label: "BLOCKED" },
        { label: "USED" },
        { label: "SCRAPPED" },
        { label: "EXPIRED" }
    ];
    voucherConfigData: any = [];
    voucherData: any = [];
    errorData: any = [];
    errorMsg = "";
    voucherId: any = [];
    checkVoucher: any = {};
    checkedIDs = [];
    allIDs = [];
    isChecked: boolean = false;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: number;
    searchkey: string;
    voucherIdSet = new Set();
    totalvoucherIdList = [];
    selectedItemsList = [];
    lastkeydown1: number = 0;
    batchNameList;
    batchNameSet = new Set();
    totalBatchNameList = [];
    activeAccess: boolean = false;
    blockAccess: boolean = false;
    unblockAccess: boolean = false;
    scrap: boolean = false;
    downloadAccess: boolean = false;
    voucherBatchAccess: boolean = false;
    voucherManageAccess: boolean = false;
    createVoucherAccess: boolean = false;
    editVoucherAccess: boolean = false;
    deleteVoucherAccess: boolean = false;
    smsAccess: boolean = false;
    batchId = history.state.data ? history.state.data.batchId : null;
    batchName = history.state.data ? history.state.data.batchName : null;
    constructor(
        private toastr: ToastrService,
        private dialog: MatDialog,
        private voucherService: VoucherService,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private messageService: MessageService,
        private datePipe: DatePipe,
        loginService: LoginService
    ) {
        this.createVoucherAccess = loginService.hasPermission(PRODUCTS.VOUCHER_CREATE);
        this.editVoucherAccess = loginService.hasPermission(PRODUCTS.VOUCHER_EDIT);
        this.deleteVoucherAccess = loginService.hasPermission(PRODUCTS.VOUCHER_DELETE);
        this.voucherBatchAccess = loginService.hasPermission(PRODUCTS.SHOW_VOUCHER_BATCH);
        this.voucherManageAccess = loginService.hasPermission(PRODUCTS.SHOW_MANAGE_VOUCHERS);
        this.activeAccess = loginService.hasPermission(PRODUCTS.VOUCHER_ACTIVE);
        this.blockAccess = loginService.hasPermission(PRODUCTS.VOUCHER_BLOCK);
        this.unblockAccess = loginService.hasPermission(PRODUCTS.VOUCHER_UNBLOCK);
        this.scrap = loginService.hasPermission(PRODUCTS.VOUCHER_SCRAP);
        this.smsAccess = loginService.hasPermission(PRODUCTS.SEND_SMS_MANAGE_VOUCHERS);
        this.downloadAccess = loginService.hasPermission(PRODUCTS.DOWNLOAD_VOUCHER);
        this.showBatch = true;
    }

    ngOnInit(): void {
        this.batchId = sessionStorage.getItem("selectedBatchId");
        this.batchName = sessionStorage.getItem("selectedBatchName");
        if (this.batchId == null) {
            this.getAllVouchers("");
        } else {
            this.findByBatchId(this.batchId);
        }
        this.searchVoucherForm = this.fb.group({
            batchName: ["", Validators.required],
            configId: [""],
            status: [""]
        });
        this.customerGroupForm = this.fb.group({
            countryCode: ["+91"],
            mobileNo: ["", [Validators.required, Validators.pattern("[0-9]+")]]
        });
    }
    ngOnChanges() {
        if (this.paginator) {
            this.paginator.length = this.totalElements;
            this.paginator.pageIndex = 0;
        }
    }
    goToFirstPage() {
        this.pageChanged({
            pageIndex: 0,
            pageSize: this.itemsPerPage,
            length: this.totalElements
        } as PageEvent);
    }


    clearcustomerGroupForm() {
        this.customerGroupForm = this.fb.group({
            countryCode: ['+91'],
            mobileNo: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]]
        });

    }
    onlyNumbers(event: KeyboardEvent) {
        const charCode = event.which ? event.which : event.keyCode;
        if (charCode < 48 || charCode > 57) {
            event.preventDefault();
        }
    }

    showSendSms() {

        this.customerGroupForm.reset({
            countryCode: '+91',
            mobileNo: ''
        });

        // Now open dialog
        const dialogRef = this.dialog.open(this.SendSMSDialogTemplate, {
            width: '600px',
            data: { form: this.customerGroupForm.value } // optional: pass initial form data
        });

        dialogRef.afterClosed().subscribe(result => {
        });
    }

    closeModalSendSMS() {
        this.dialog.closeAll();
        this.modalToggle = false;
    }
    sendSms(voucherIdSms) {
        let countryCode = this.customerGroupForm.value.countryCode;
        let mobileNo = this.customerGroupForm.value.mobileNo;
        this.voucherService.sendSms(countryCode, mobileNo, voucherIdSms, this.voucherCodeSms).subscribe(
            (response: any) => {
                this.dialog.closeAll();
                this.toastr.success("Successfuly", 'Success!');

                this.clearcustomerGroupForm();
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


            }
        );
    }
    pageChanged(event: PageEvent) {
        this.currentPage = event.pageIndex + 1;
        this.itemsPerPage = event.pageSize;

        if (this.searchkey) {
            this.searchVoucher1();
        } else if (this.batchId == null) {
            this.getAllVouchers("");
        } else {
            this.findByBatchId(this.batchId);
        }
    }


    addVoucher(id, event: any) {
        if (event.checked) {
            this.allIDs.push(id);
            if (this.allIDs.length === this.voucherData.length) {
                this.isChecked = true;
            }
        } else {
            let voucherDetails = this.voucherData;
            voucherDetails.forEach(element => {
                if (element.eventId == id) {
                    element.isChecked = false;
                }
            });
            if (this.allIsChecked == true) {
                this.allIDs.forEach((value, index) => {
                    if (value == id) {
                        this.allIDs.splice(index, 1);
                    }
                });
            }

            if (this.allIDs.length == 0 || this.allIDs.length !== this.voucherData.length) {
                this.isChecked = false;
            }
        }
    }

    findByBatchId(batchId) {
        this.allIDs = [];
        this.voucherData = [];
        this.itemsPerPage = this.showItemPerPage ? this.showItemPerPage : this.itemsPerPage;
        this.voucherService.findByBatchId(batchId, this.currentPage, this.itemsPerPage).subscribe(
            (response: any) => {
                this.voucherData = response.voucher.content;
                this.totalElements = response.voucher.totalElements;
                if (this.allIsChecked == true) {
                    let voucherDetail = this.voucherData;
                    for (let i = 0; i < voucherDetail.length; i++) {
                        this.allIDs.push(this.voucherData[i].id);
                    }
                    this.allIDs.forEach((value, index) => {
                        voucherDetail.forEach(element => {
                            if (element.id == value) {
                                element.isChecked = true;
                            }
                        });
                        this.allIsChecked = true;
                    });
                }
                this.searchVoucherForm.patchValue({
                    batchName: this.batchName
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );
    }

    changeStatusToActive() {
        if (this.allChecked) {
            this.voucherService.changeStatusToActive(this.allIDs).subscribe(
                (response: any) => {
                    //this.voucherData = response;
                    this.goToFirstPage();

                    this.checkedIDs = [];
                    this.allIDs = [];
                    this.allIsChecked = false;
                    this.isChecked = false;
                    this.toastr.success("Successfully", 'Success!');


                },
                (error: any) => {
                    if (error.error.status == 400) {
                        this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                    } else {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                    }
                }
            );
        } else {
            this.voucherService.changeStatusToActive(this.allIDs).subscribe(
                (response: any) => {
                    // checkedIDs
                    //this.voucherData = response;
                    this.goToFirstPage();

                    this.checkedIDs = [];
                    this.allIDs = [];
                    this.allIsChecked = false;
                    this.isChecked = false;
                    this.toastr.success("Successfully", 'Success!');

                },
                (error: any) => {
                    if (error.error.status == 400) {
                        this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                    } else {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                    }
                }
            );
        }
    }
    changeStatusToBlock() {
        if (this.allChecked) {
            this.voucherService.changeStatusToBlock(this.allIDs).subscribe(
                (response: any) => {
                    //this.voucherData = response;
                    this.goToFirstPage();

                    this.checkedIDs = [];
                    this.allIDs = [];
                    this.allIsChecked = false;
                    this.isChecked = false;
                    this.toastr.success("Successfully", 'Success!');



                },
                (error: any) => {
                    if (error.error.status == 400) {
                        this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                    } else {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                    }
                }
            );
        } else {
            this.voucherService.changeStatusToBlock(this.allIDs).subscribe(
                (response: any) => {
                    // this.voucherData = response;
                    this.goToFirstPage();

                    this.checkedIDs = [];
                    this.allIDs = [];
                    this.allIsChecked = false;
                    this.isChecked = false;
                    this.toastr.success(`${response.message}`, 'Success!');

                },
                (error: any) => {
                    if (error.error.status == 400) {
                        this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                    } else {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                    }
                }
            );
        }
    }
    changeStatusToUnblock() {
        if (this.allChecked) {
            this.voucherService.changeStatusToUnblock(this.allIDs).subscribe(
                (response: any) => {
                    //this.voucherData = response;
                    this.goToFirstPage();

                    this.allIsChecked = false;
                    this.isChecked = false;
                    this.checkedIDs = [];
                    this.allIDs = [];
                    this.toastr.success("Successfully", 'Success!');

                },
                (error: any) => {
                    if (error.error.status == 400) {
                        this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                    } else {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                    }
                }
            );
        } else {
            this.voucherService.changeStatusToUnblock(this.allIDs).subscribe(
                (response: any) => {
                    //this.voucherData = response;
                    this.goToFirstPage();

                    this.checkedIDs = [];
                    this.allIsChecked = false;
                    this.isChecked = false;
                    this.allIDs = [];
                    this.toastr.success("successfully", 'Success!');

                },
                (error: any) => {
                    if (error.error.status == 400) {
                        this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                    } else {


                    }
                }
            );
        }
    }
    changeStatusToScrap() {
        if (this.allChecked) {
            this.voucherService.changeStatusToScrap(this.allIDs).subscribe(
                (response: any) => {
                    //this.voucherData = response;
                    this.goToFirstPage();

                    this.checkedIDs = [];
                    this.allIDs = [];

                    this.allIsChecked = false;
                    this.isChecked = false;
                    this.toastr.success("Successfully", 'Success!');

                },
                (error: any) => {
                    if (error.error.status == 400) {
                        this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                    } else {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                    }
                }
            );
        } else {
            this.voucherService.changeStatusToScrap(this.allIDs).subscribe(
                (response: any) => {
                    //this.voucherData = response;
                    this.goToFirstPage();

                    this.checkedIDs = [];
                    this.allIDs = [];
                    this.allIsChecked = false;
                    this.isChecked = false;
                    this.toastr.success(`${response.message}`, 'Success!');

                },
                (error: any) => {
                    if (error.error.status == 400) {
                        this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                    } else {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                    }
                }
            );
        }
    }
    searchVoucher1() {


        if (!this.searchkey || this.searchkey !== this.searchVoucherForm.value) {
            this.currentPage = 1;
            this.allIsChecked = false;
            this.isChecked = false;
        }
        this.searchkey = this.searchVoucherForm.value;
        if (this.showItemPerPage) {
            this.itemsPerPage = this.showItemPerPage;
        }


        let status = "";
        let batchName = "";
        if (
            this.searchVoucherForm.value.status != null &&
            this.searchVoucherForm.value.status != "null"
        ) {
            status = this.searchVoucherForm.value.status;
        }
        if (
            this.searchVoucherForm.value.batchName != null &&
            this.searchVoucherForm.value.batchName != "null"
        ) {
            batchName = this.searchVoucherForm.value.batchName;
        }
        this.voucherService
            .findVouchers(
                batchName,
                status

            )
            .subscribe(
                (response: any) => {
                    this.voucherData = response.voucher.content;
                    this.totalElements = response.voucher.totalElements;
                    for (let index = 0; index < this.voucherData.length; index++) {
                        const voucher = this.voucherData[index];
                        this.batchNameSet.add(voucher.batchName);
                    }
                    this.totalBatchNameList = Array.from(this.batchNameSet);
                    this.isChecked = false;
                    this.allIDs = [];

                },
                (error: any) => {
                    if (error.error.status == 404) {
                        this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                    } else {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                    }
                    this.voucherData = [];
                    this.totalElements = 0;
                }
            );
    }

    clearSearchForm() {
        this.batchId = null;
        this.currentPage = 1;
        this.searchVoucherForm.reset();
        this.searchVoucherForm.patchValue({
            status: "null"
        });
        this.getAllVouchers("");
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPage > 1) {
            this.currentPage = 1;
        }
        if (!this.searchkey && this.batchId == null) {
            this.getAllVouchers(this.showItemPerPage);
        } else if (this.searchkey && this.batchId == null) {
            this.searchVoucher1();
        } else {
            this.findByBatchId(this.batchId);
        }
    }
    getAllVoucherConfigurations() {
        this.voucherService.getAllVoucherConfgiuration().subscribe(
            response => {
                this.voucherConfigData = response;
            },
            error => {
                this.errorData = error;
                this.errorMsg = this.errorData.errorMessage;
            }
        );
    }

    addAllVoucher(event) {
        if (event.checked == true) {
            this.allIDs = [];
            this.allChecked = true;
            let voucherDetail = this.voucherData;
            for (let i = 0; i < voucherDetail.length; i++) {
                this.allIDs.push(this.voucherData[i].id);
            }
            this.allIDs.forEach((value, index) => {
                voucherDetail.forEach(element => {
                    if (element.id == value) {
                        element.isChecked = true;
                    }
                });
            });
            this.allIsChecked = true;
        }
        if (event.checked == false) {
            this.allChecked = false;
            let voucherDetail = this.voucherData;
            this.allIDs.forEach((value, index) => {
                voucherDetail.forEach(element => {
                    if (element.id == value) {
                        element.isChecked = false;
                    }
                });
            });
            this.allIDs = [];
            this.allIsChecked = false;
            this.isChecked = false;
        }
    }

    getAllVouchers(list) {
        let size;
        this.searchkey = "";
        let page = this.currentPage;
        if (list) {
            size = list;
            this.itemsPerPage = list;
        } else {
            size = this.itemsPerPage;
        }
        this.voucherService.getAllVouchers(page, size).subscribe(
            (response: any) => {
                this.dataSource = response.voucher.content;
                this.totalElements = response.voucher.totalElements;
            },
            error => {
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                } else {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                }
                this.totalElements = 0;
                this.voucherData = [];
            }
        );
    }

    getUserIdsFirstWay($event) {
        let batchName = (<HTMLInputElement>document.getElementById("batchName")).value;
        this.batchNameList = [];

        if (batchName.length > 2) {
            if ($event.timeStamp - this.lastkeydown1 > 200) {
                this.batchNameList = this.searchFromArray(this.totalBatchNameList, batchName);
            }
        }
    }

    searchFromArray(arr, regex) {
        let matches = [],
            i;
        for (i = 0; i < arr.length; i++) {
            if (arr[i].match(regex)) {
                matches.push(arr[i]);
            }
        }
        return matches;
    }

    async exportExcel() {
        let batchName = this.searchVoucherForm.controls.batchName.value
            ? this.searchVoucherForm.controls.batchName.value
            : "";
        let status = this.searchVoucherForm.controls.status.value
            ? this.searchVoucherForm.controls.status.value
            : "";
        this.voucherService.getDataTOExport(batchName, status).subscribe(
            (res: any) => {
                const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(res.dataToExport);
                const wb: XLSX.WorkBook = XLSX.utils.book_new();
                XLSX.utils.book_append_sheet(wb, ws, batchName);
                XLSX.writeFile(wb, batchName ? batchName + ".xlsx" : "Vouchers" + ".xlsx");
            },
            (error: any) => {
                if (error.error.status == 400) {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                }
            }
        );
    }
}
