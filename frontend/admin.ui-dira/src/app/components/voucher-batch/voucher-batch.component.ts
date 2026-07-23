import { Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { MessageService } from "primeng/api";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { VoucherBatchService } from "src/app/service/voucher-batch.service";
import { VoucherBatch } from "../model/voucher-batch";

import { DatePipe } from "@angular/common";
import { LoginService } from "src/app/service/login.service";
import { PRODUCTS } from "src/app/constants/aclConstants";
import { ToastrService } from 'ngx-toastr';
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";

declare var $: any;
@Component({
    selector: "app-voucher-batch",
    templateUrl: "./voucher-batch.component.html",
    styleUrls: ["./voucher-batch.component.css"],
    standalone: false
})
export class VoucherBatchComponent implements OnInit {

    activeTabIndex = 0;
    extendExpiryRef!: MatDialogRef<any>;
    @Output() goToTab3 = new EventEmitter<any>();
    @Input() set batchData2(value: any[]) {
        if (value) {
            this.dataSource = new MatTableDataSource(value);
            this.dataSource.sort = this.sort;
            // this.dataSource.paginator = this.paginator;
        }
    }
    @ViewChild('voucherDetailTemplate') voucherDetailTemplate: TemplateRef<any>;

    dataSource = new MatTableDataSource<any>();
    @ViewChild('ExtendExpiryDialog') ExtendExpiryDialog: TemplateRef<any>;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    batchData: any = [];
    displayedColumns = [
        "batchName",
        "voucherName",
        "plan",
        "quantity",
        "price",
        "createdOn",
        "expiry",
        "action"
    ];
    UpdatedExpiryDate: any = null;
    UpdatedExpiryTime: any = null;
    UpdatedExpiry: any = null;
    submitted: boolean = false;
    currentPage: number = 1;
    itemsPerPage: number = RadiusConstants.ITEMS_PER_PAGE;
    totalRecords: number;


    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    searchkey: string;

    searchForm: UntypedFormGroup;
    assignForm: UntypedFormGroup;
    searchSubmitted = false;
    assignSubmitted = false;
    expiryAccess = false;
    liveUserDetail: any = [];
    fileName = "Live-User.xlsx";
    batchWithoutAssign: any;
    resellerData: any;
    voucherShowBatch: VoucherBatch;
    loggedInUser: string;
    showBatch: boolean;
    currentExpiry: any;

    voucherBatchId: number;
    prevMonth: any;
    prevYear: any;
    createdDate: any;
    voucherBatchAccess: boolean = false;
    voucherManageAccess: boolean = false;
    // @Input() batchData2: any[] = [];
    constructor(
        private toastr: ToastrService,
        private dialog: MatDialog,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private messageService: MessageService,
        private VoucherBatchService: VoucherBatchService,
        private router: Router,
        private datePipe: DatePipe,
        loginService: LoginService
    ) {
        this.expiryAccess = loginService.hasPermission(PRODUCTS.EXTEND_EXPIRY_VOUCHER_BATCH);
        this.voucherBatchAccess = loginService.hasPermission(PRODUCTS.SHOW_VOUCHER_BATCH);
        this.voucherManageAccess = loginService.hasPermission(PRODUCTS.SHOW_MANAGE_VOUCHERS);
        this.showBatch = true;
    }

    ngOnInit(): void {


        let today = new Date();
        let month = today.getMonth();
        let year = today.getFullYear();
        this.prevMonth = month === 0 ? 11 : month - 1;
        this.prevYear = this.prevMonth === 11 ? year - 1 : year;

        this.loggedInUser = localStorage.getItem("loggedInUser");
        this.searchForm = this.fb.group({
            batchName: [""],
        });
        this.assignForm = this.fb.group({
            resellerId: ["", Validators.required],
            voucherBatchId: ["", Validators.required],
            lastModifiedBy: [this.loggedInUser],
            overwiteExpiry: [false],
        });
        this.getAll("");
        // this.getAllBatchWithoutReseller();
        // this.getAllReseller();
    }

    getAll(list) {
        let size;
        this.searchkey = "";
        let page = this.currentPage;
        if (list) {
            size = list;
            this.itemsPerPage = list;
        } else {
            size = this.itemsPerPage;
        }
        this.VoucherBatchService.getAll(page, size).subscribe(
            (response: any) => {

                // this.batchData = response.voucherBatchList;
                // this.totalRecords = response.voucherBatchList.length;
                this.batchData = response.voucherbatch.data;
                this.batchData2 = this.batchData


                this.totalRecords = response.voucherbatch.totalRecords;
            },
            (error: any) => {
                this.batchData = [];
                this.totalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                } else {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


                }
            }
        );
    }

    currentExpiryUpdate(voucherBatchId: number, date: any, create: any) {


        this.UpdatedExpiry = null;
        this.UpdatedExpiryDate = null;
        this.UpdatedExpiryTime = null;

        this.currentExpiry = new Date(date);
        this.createdDate = new Date(create);
        this.createdDate.setFullYear(this.prevYear);
        this.voucherBatchId = voucherBatchId;

        this.extendExpiryRef = this.dialog.open(this.ExtendExpiryDialog, {
            width: '600px',
            disableClose: true,
            autoFocus: false
        });
    }



    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPage > 1) {
            this.currentPage = 1;
        }
        if (!this.searchkey) {
            this.getAll(this.showItemPerPage);
        } else {
            this.search();
        }
    }
    getAllBatchWithoutReseller() {
        this.VoucherBatchService.getAllBatchWithoutReseller().subscribe(
            (response: any) => {

                this.batchWithoutAssign = response.voucherBatch;
            },
            (error: any) => {
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                } else {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


                }
            }
        );
    }
    filteredResellerData: any;
    getAllReseller() {
        this.VoucherBatchService.findAllReseller().subscribe(
            (response: any) => {
                this.resellerData = response.resellers.data;
                this.filteredResellerData = this.resellerData.filter(element => element.status == "Active");
            },
            (error: any) => {
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                } else {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


                }
            }
        );
    }

    async search() {
        // this.currentPage = 1;
        if (!this.searchkey || this.searchkey !== this.searchForm.value.batchName) {
            this.currentPage = 1;
        }
        this.searchkey = this.searchForm.value.batchName;
        if (this.showItemPerPage) {
            this.itemsPerPage = this.showItemPerPage;
        }
        this.searchSubmitted = true;
        let name = this.searchForm.value.batchName ? this.searchForm.value.batchName : "";
        if (this.searchForm.valid) {
            this.VoucherBatchService.getByUserName(name, this.currentPage, this.itemsPerPage).subscribe(
                (response: any) => {
                    this.batchData = response.voucherbatch.data;

                    this.totalRecords = response.voucherbatch.totalRecords;
                },
                error => {
                    this.totalRecords = 0;
                    if (error.error.status == 404) {
                        this.batchData = [];
                        this.totalRecords = 0;
                        this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                    } else {
                        this.batchData = [];
                        this.totalRecords = 0;
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


                    }
                }
            );
        }
    }



    viewVoucher(batchId: any, batchName: any) {
        this.goToTab3.emit({ batchId, batchName });
    }

    clearSearchForm() {
        this.currentPage = 1;
        this.searchSubmitted = false;
        this.getAll("");
        this.searchForm.reset();
    }

    assignReseller() {
        this.assignSubmitted = true;
        if (this.assignForm.valid) {
            this.assignForm.value.lastModifiedBy = this.loggedInUser;
            this.VoucherBatchService.assignReseller(this.assignForm.value).subscribe(
                (response: any) => {
                    this.assignForm.reset();
                    this.assignSubmitted = false;
                    this.getAll("");
                    this.getAllBatchWithoutReseller();
                    this.toastr.success(`${response.message}`, 'Success!');


                },
                (error: any) => {
                    this.assignForm.reset();
                    this.assignSubmitted = false;
                    if (error.error.status == 402) {
                        this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                    } else {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


                    }
                }
            );
        }
    }
    resellerDropDown: any;
    searchReseller() {
        let selectedBatch;
        this.VoucherBatchService.getAllVoucherBatchData().subscribe(
            (response: any) => {
                // this.batchData = response.voucherBatchList;
                // this.totalRecords = response.voucherBatchList.length;
                this.batchData = response.voucherbatch.data;

                this.totalRecords = response.voucherbatch.totalRecords;
            },
            (error: any) => {
                this.batchData = [];
                this.totalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                } else {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


                }
            }
        );
        selectedBatch = this.batchData.filter(
            element => element.voucherBatchId == this.assignForm.value.voucherBatchId
        );
        this.resellerDropDown = this.filteredResellerData;
        let resellerFilterDataNew: any = [];
        let isMappedLocationFound: boolean = false;
        selectedBatch.forEach(item => {
            if (item.plan.planLocationsMapping != null) {
                item.plan.planLocationsMapping.forEach(locationData => {
                    this.resellerDropDown = [];
                    this.filteredResellerData.filter(element => {
                        if (element.locationMaster.locationMasterId == locationData.locationId) {
                            isMappedLocationFound = true;
                            resellerFilterDataNew.push(element);
                        }
                    });
                });
            }
        });
        if (isMappedLocationFound) {
            this.resellerDropDown = resellerFilterDataNew;
        }
    }
    updateExpiry() {
        this.submitted = true;

        if (!this.UpdatedExpiryDate || !this.UpdatedExpiryTime) {
            return;
        }
        const date = new Date(this.UpdatedExpiryDate);
        const time = new Date(this.UpdatedExpiryTime);
        date.setHours(time.getHours());
        date.setMinutes(time.getMinutes());
        date.setSeconds(0);
        const year = date.getFullYear();
        const month = ("0" + (date.getMonth() + 1)).slice(-2);
        const day = ("0" + date.getDate()).slice(-2);
        const hour = ("0" + date.getHours()).slice(-2);
        const minute = ("0" + date.getMinutes()).slice(-2);

        const finalExpiry = `${year}-${month}-${day} ${hour}:${minute}`;
        this.VoucherBatchService.updateExpiryDate(finalExpiry, this.voucherBatchId)
            .subscribe(
                (response: any) => {
                    this.UpdatedExpiryDate = null;
                    this.UpdatedExpiryTime = null;

                    this.getAll("");

                    if (this.extendExpiryRef) this.extendExpiryRef.close();

                    this.toastr.success("Successfully", "Success!");
                },
                (error) => {
                    this.toastr.error(`${error.error.errorMessage}`, "Failed!");
                }
            );
    }

    pageChanged(event: any) {
        this.currentPage = event.pageIndex + 1;
        this.itemsPerPage = event.pageSize;

        if (!this.searchkey) {
            this.getAll("");
        } else {
            this.search();
        }
    }

    getVoucherBatchDetails(batchId: any) {

        this.voucherShowBatch = this.batchData.find(
            (batch) => batch.voucherBatchId == batchId
        );


        if (this.voucherShowBatch) {
            this.dialog.open(this.voucherDetailTemplate, {
                width: "600px",
                disableClose: true
            });
        }
    }

    onTabChange(event: any) {
        this.activeTabIndex = event.index;


    }


    onCancle() {
        if (this.extendExpiryRef) {
            this.extendExpiryRef.close();
        }
    }
}
