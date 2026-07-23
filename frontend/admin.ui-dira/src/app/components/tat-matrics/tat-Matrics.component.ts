import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import { Regex } from "src/app/constants/regex";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { TicketReasonCategoryService } from "src/app/service/ticket-reason-category.service";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { Observable, Observer } from "rxjs";
import { WORKFLOWS } from "src/app/constants/aclConstants";
import { CustomerService } from "src/app/service/customer.service";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { ToastrService } from "ngx-toastr";

@Component({
    selector: "app-tat-Matrics",
    templateUrl: "./tat-Matrics.component.html",
    styleUrls: ["./tat-Matrics.component.css"],
    standalone: false
})
export class TATMatricsComponent implements OnInit {

    @ViewChild(MatPaginator) tatPaginator!: MatPaginator;
    @ViewChild('viewTATDialogTemplate') viewTATDialogTemplate!: TemplateRef<any>;
    @ViewChild('tatMetricDialogTemplate') tatMetricDialogTemplate!: TemplateRef<any>;
    tatMatricsDataSource = new MatTableDataSource<any>();
    dialogRef: MatDialogRef<any>;
    displayedColumns: string[] = ['ID', 'Name', 'SLA Time', 'SLA Unit', 'Status', 'Action'];

    public loginService: LoginService;
    AclClassConstants;
    AclConstants;

    MatricsFormGroup: UntypedFormGroup;
    tatMatricsTAT: UntypedFormArray;
    tatMatricsTATForm: UntypedFormGroup;
    submitted = false;
    statusOptions = RadiusConstants.status;
    serviceData: any;
    teamListData: any;
    tatMatricsTATSubmitted = false;
    currentPagetatMatricsTAT = 1;
    tatMatricsTATitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    tatMatricsTATtotalRecords: string;
    createtatMatricsData: any = [];
    currentPagetatMatricsListdata = 1;
    tatMatricsListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    tatMatricsListDatatotalRecords: any;
    tatMatricsListData: any;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    istatMatricsEdit = false;
    edittatMatricsData: any = [];
    searchkey: string;
    searchTATName: any = "";
    searchService: any = "";
    searchData: any;
    searchAllData: any;
    listView = true;
    createView = false;
    detailView = false;
    viewTrcData: any = [];
    currentPageViewTATListdata = 1;
    viewTATListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    viewTATListDatatotalRecords: any;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    actionData = [
        { label: "Notification", value: "Notification" },
        { label: "Reassign", value: "Reassign" },
        { label: "Both", value: "Both" },
    ];
    timeUnitData = [
        { label: "Day", value: "Day" },
        { label: "Hour", value: "Hour" },
        { label: "Min", value: "Min" },
    ];
    levelData: any = [];
    ifOrder4greaterthan = false;
    pageSize;

    constructor(
        private toastr: ToastrService,
        private dialog: MatDialog,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private tatMatricsService: TicketReasonCategoryService,
        private commondropdownService: CommondropdownService,
        loginService: LoginService,
        private customermgmtservice: CustomerService
    ) {
        this.createAccess = loginService.hasPermission(WORKFLOWS.TAT_METRICS__CREATE);
        this.deleteAccess = loginService.hasPermission(WORKFLOWS.TAT_METRICS__DELETE);
        this.editAccess = loginService.hasPermission(WORKFLOWS.TAT_METRICS__EDIT);
        this.loginService = loginService;
    }

    ngOnInit(): void {
        this.tatMatricsDataSource.paginator = this.tatPaginator;

        this.MatricsFormGroup = this.fb.group({
            name: ["", Validators.required],
            status: ["", Validators.required],
            slaTime: ["", Validators.required],
            slaUnit: ["", Validators.required],
            rtime: ["", [Validators.pattern(Regex.numeric), Validators.required]],
            runit: ["", [Validators.required]],
        });


        this.tatMatricsTATForm = this.fb.group({
            orderNo: [1, Validators.required],
            level: ['Level 1', Validators.required],
            mtime: ["", [Validators.pattern(Regex.numeric), Validators.required]],
            munit: ["", [Validators.required]],
            action: ["Notification", [Validators.required]],

            tatManagementId: [""],
            id: [""],
        });
        this.tatMatricsTAT = this.fb.array([]);
        this.levelAllData();
        this.gettatMatricsDataList("");
        this.searchData = {
            filter: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "any",
                    filterOperator: "equalto",
                    filterCondition: "and",
                },
            ],
            page: "",
            pageSize: "",
        };
    }

    levelAllData() {
        for (let i = 1; i < 100; i++) {
            this.levelData.push({ label: `Level ${i}` });
        }
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(WORKFLOWS.TAT_METRICS__DELETE) || this.loginService.hasPermission(WORKFLOWS.TAT_METRICS__EDIT)) {
            return ['ID', 'Name', 'SLA Time', 'SLA Unit', 'Status', 'Action'];
        } else {
            return ['ID', 'Name', 'SLA Time', 'SLA Unit', 'Status'];
        }
    }
    gettatMatricsDataList(list?: any) {
        this.searchkey = "";
        const page = this.currentPagetatMatricsListdata;
        let size;

        if (list !== null && list !== undefined && list !== "" && list !== " ") {
            size = Number(list);
            this.tatMatricsListdataitemsPerPage = size;
        } else {
            size = this.tatMatricsListdataitemsPerPage;
        }

        const pagedata = {
            page,
            pageSize: size,
        };

        const url = "/matrix";
        this.customermgmtservice.postMethod(url, pagedata).subscribe(
            (response: any) => {
                this.tatMatricsListData = response.dataList;
                this.tatMatricsListDatatotalRecords = response.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }


    searchViewTrc() {
        this.listView = true;
        this.createView = false;
        this.detailView = false;
        this.currentPagetatMatricsListdata = 1;
        this.tatMatricsListdataitemsPerPage = 5;
        this.pageSize = 5;
        this.gettatMatricsDataList("");
        this.searchTATName = "";
        this.searchService = "";
    }

    createTrc() {
        this.listView = false;
        this.createView = true;
        this.detailView = false;
        this.submitted = false;
        this.istatMatricsEdit = false;
        this.MatricsFormGroup.reset();
        this.tatMatricsTATForm.reset();
        this.tatMatricsTAT.controls = [];

        this.tatMatricsTATForm.patchValue({
            action: "Notification",
            orderNo: 1,
            level: "Level 1",
        });
    }

    TotalItemPerPage(event) {
        this.tatMatricsListdataitemsPerPage = Number(event.value);
        this.currentPagetatMatricsListdata = 1;
        if (this.tatMatricsListData > 1) {
            this.currentPagetatMatricsListdata = 1;
        }
        if (!this.searchkey) {
            this.gettatMatricsDataList("");
        } else {
            this.searchTAT();
        }
    }
    pageChangedTrcList(event: any) {
        this.currentPagetatMatricsListdata = event.pageIndex + 1;
        this.tatMatricsListdataitemsPerPage = event.pageSize;


        this.gettatMatricsDataList();
    }


    //   pageChangedTrcList(pageNumber) {
    //     this.currentPagetatMatricsListdata = pageNumber;
    //     this.gettatMatricsDataList("");
    //   }

    deleteConfirmonTATField(TATFieldIndex: number) {
        if (TATFieldIndex || TATFieldIndex == 0) {
            this.confirmationService.confirm({
                message: "Do you want to delete this TAT ?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {

                    this.onRemoveTAT(TATFieldIndex);
                },
                reject: () => {
                    this.toastr.info(`You have rejected the request`, 'Rejected!');

                },
            });
        }
    }

    onRemoveTAT(TATFieldIndex: number) {

        const item = this.tatMatricsTAT.at(TATFieldIndex).value;
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete Discount',
                description: `Are you sure you want to delete "${item.action}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.tatMatricsTAT.removeAt(TATFieldIndex);

                this.tatMatricsTAT.patchValue(this.tatMatricsTAT.value);

                this.tatMatricsTAT.controls.forEach((ctrl, i) => {
                    ctrl.patchValue({
                        orderNo: i + 1,
                        level: `Level ${i + 1}`
                    });
                });


                this.tatMatricsDataSource.data = this.tatMatricsTAT.controls.map(ctrl => ctrl.value);


                const nextOrder = this.tatMatricsTAT.length + 1;
                this.tatMatricsTATForm.reset({

                    orderNo: nextOrder,
                    level: `Level ${nextOrder}`,
                    action: 'Notification',
                    mtime: '',
                    munit: ''
                });

                Object.keys(this.tatMatricsTATForm.controls).forEach(key => {
                    const control = this.tatMatricsTATForm.get(key);
                    control?.setErrors(null);
                    control?.markAsPristine();
                    control?.markAsUntouched();
                });

            } else {
            }
        });
    }


    //   if (this.tatMatricsTAT.length === 0) {
    //     this.tatMatricsTATForm.reset({
    //       orderNo: 1,
    //       level: 'Level 1',
    //       action: 'Notification',
    //       mtime: '',
    //       munit: ''
    //     });



    addEdittatMatrics(id) {
        this.submitted = true;

        if (this.MatricsFormGroup.valid) {
            if (id) {
                const url = "/matrix/update";

                this.createtatMatricsData = this.MatricsFormGroup.value;
                this.createtatMatricsData.id = id;
                this.createtatMatricsData.matrixDetailsList = this.tatMatricsTAT.value;

                if (this.tatMatricsTAT.value.length < 1) {
                    this.toastr.error("Please add TAT details", 'Failed!');

                } else {
                    this.customermgmtservice.postMethod(url, this.createtatMatricsData).subscribe(
                        (response: any) => {

                            this.dialogRef.close();

                            if (response.responseCode == 200) {
                                this.toastr.success("Successful Updated", 'Success!');

                                this.dialogRef.close();
                                this.cleartatMatrics();
                                this.istatMatricsEdit = false;

                            } else {
                                this.toastr.info(`${response.responseMessage}`, 'Info!');

                            }
                        },
                        (error: any) => {
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                        }
                    );
                }
            } else {
                const url = "/matrix/save";

                this.createtatMatricsData = this.MatricsFormGroup.value;
                this.createtatMatricsData.matrixDetailsList = this.tatMatricsTAT.value;
                if (this.tatMatricsTAT.value.length < 1) {
                    this.toastr.error("Please add TAT details", 'Failed!');

                } else {
                    this.customermgmtservice.postMethod(url, this.createtatMatricsData).subscribe(
                        (response: any) => {

                            if (response.responseCode == 406) {
                                this.toastr.error(`${response.responseMessage}`, 'Failed!');

                            } else {

                                this.toastr.success("Successful Created", 'Success!');

                                this.cleartatMatrics();
                                this.dialogRef.close();
                            }
                        },
                        (error: any) => {
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                        }
                    );
                }
            }
        }
    }

    cleartatMatrics() {
        this.MatricsFormGroup.reset();
        this.submitted = false;
        this.tatMatricsTAT.controls = [];
        this.tatMatricsTATForm.reset();
        this.tatMatricsTAT = this.fb.array([]);
        this.listView = true;
        this.createView = false;
        if (!this.searchkey) {
            this.gettatMatricsDataList("");
        } else {
            this.searchTAT();
        }
    }

    tatMatricsTATFormGroup(): UntypedFormGroup {
        // console.log("this.tatMatricsTATForm.value.orderNo", this.tatMatricsTATForm.value.orderNo);
        return this.fb.group({
            orderNo: [this.tatMatricsTATForm.value.orderNo, [Validators.required]],
            level: [this.tatMatricsTATForm.value.level, [Validators.required]],
            mtime: [
                String(this.tatMatricsTATForm.value.mtime),
                [Validators.pattern(Regex.numeric), Validators.required],
            ],
            munit: [this.tatMatricsTATForm.value.munit, [Validators.required]],
            action: [this.tatMatricsTATForm.value.action, [Validators.required]],
            tatManagementId: [this.tatMatricsTATForm.value.tatManagementId],
            id: [this.tatMatricsTATForm.value.id],
        });
    }
    onAddtatMatricsTATField() {
        this.tatMatricsTATSubmitted = true;

        if (this.tatMatricsTATForm.valid) {
            const newRow = this.tatMatricsTATFormGroup();
            this.tatMatricsTAT.push(newRow);


            this.tatMatricsDataSource.data = this.tatMatricsTAT.controls.map(ctrl => ctrl.value);
            this.tatMatricsListDatatotalRecords = this.tatMatricsTAT.controls.length;


            const nextOrder = this.tatMatricsTAT.length + 1;
            this.tatMatricsTATForm.reset({
                action: "Notification",
                orderNo: nextOrder,
                level: `Level ${nextOrder}`,
                mtime: '',
                munit: ''
            });


            Object.keys(this.tatMatricsTATForm.controls).forEach(key => {
                const control = this.tatMatricsTATForm.get(key);
                control?.setErrors(null);
                control?.markAsPristine();
                control?.markAsUntouched();
            });

            this.tatMatricsTAT.controls.forEach((row: UntypedFormGroup) => {
                Object.keys(row.controls).forEach(key => {
                    const control = row.get(key);
                    control?.markAsPristine();
                    control?.markAsUntouched();
                });
            });

            this.tatMatricsTATSubmitted = false;
        }
    }


    pageChangedtatMatricsTATData(event: PageEvent) {
        this.currentPagetatMatricsTAT = event.pageIndex;
        this.tatMatricsTATitemsPerPage = event.pageSize;

        const startIndex = this.currentPagetatMatricsTAT * this.tatMatricsTATitemsPerPage;
        const endIndex = startIndex + this.tatMatricsTATitemsPerPage;

        this.tatMatricsDataSource.data = this.tatMatricsTAT.controls
            .slice(startIndex, endIndex)
            .map(ctrl => ctrl.value);
    }


    edittatMatrics(id: number) {
        this.MatricsFormGroup.reset();
        this.tatMatricsTATForm.reset();
        this.istatMatricsEdit = true;
        this.listView = false;
        this.createView = true;
        this.detailView = false;

        this.tatMatricsTAT.clear();
        this.tatMatricsDataSource.data = [];

        const url = `/matrix/${id}`;
        this.customermgmtservice.getMethod(url).subscribe(
            (response: any) => {
                this.edittatMatricsData = response.data;

                this.MatricsFormGroup.patchValue({
                    name: this.edittatMatricsData.name,
                    status: this.edittatMatricsData.status,
                    slaTime: this.edittatMatricsData.slaTime,
                    slaUnit: this.edittatMatricsData.slaUnit,
                    rtime: this.edittatMatricsData.rtime,
                    runit: this.edittatMatricsData.runit,
                });

                this.edittatMatricsData.matrixDetailsList.forEach(element => {
                    this.tatMatricsTAT.push(this.fb.group(element));
                });


                this.tatMatricsDataSource.data = this.tatMatricsTAT.controls.map(ctrl => ctrl.value);
                //   this.tatMatricsDataSource.paginator = this.tatPaginator;
                //   this.tatMatricsDataSource._updateChangeSubscription();


                const orderN = this.tatMatricsTAT.length + 1;
                this.tatMatricsTATForm.patchValue({
                    action: "Notification",
                    orderNo: orderN,
                    level: `Level ${orderN}`,
                    mtime: "",
                    munit: "",
                    escalatedTime: "",
                });


                this.dialogRef = this.dialog.open(this.tatMetricDialogTemplate, {
                    width: '1500px',
                    maxWidth: '80vw',
                    height: 'auto',
                    autoFocus: false,
                    disableClose: true
                });


                this.dialogRef.afterClosed().subscribe(() => {
                    this.dialogRef = null!;
                    this.MatricsFormGroup.reset();
                    this.tatMatricsTATForm.reset({
                        action: "Notification",
                        orderNo: 1,
                        level: "Level 1",
                        mtime: "",
                        munit: "",
                    });
                    this.tatMatricsTAT.clear();
                    this.tatMatricsDataSource.data = [];
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    openTATViewDialog() {
        this.dialogRef = this.dialog.open(this.viewTATDialogTemplate, {
            width: '1500px',
            maxWidth: '70vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });

        this.dialogRef.afterClosed().subscribe(() => {
            this.dialogRef = null!;
        });
    }

    tatAllDetails(id: number) {
        const url = "/matrix/" + id;
        this.customermgmtservice.getMethod(url).subscribe(
            (response: any) => {
                this.viewTrcData = response.data;
                this.openTATViewDialog();
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }


    deleteConfirmontatMatrics(TrcData) {
        if (TrcData) {
            this.confirmationService.confirm({
                message: "Do you want to delete this TAT Metrics?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.deleteTAT(TrcData);
                },
                reject: () => {
                    this.toastr.info(`You have rejected the request`, 'Rejected!');

                },
            });
        }
    }

    searchTAT() {
        if (!this.searchkey || this.searchkey !== this.searchData) {
            this.currentPagetatMatricsListdata = 1;
            this.tatMatricsListdataitemsPerPage = 5;
            this.pageSize = 5;
        }
        this.searchkey = this.searchData;
        // if (this.showItemPerPage) {
        //   this.tatMatricsListdataitemsPerPage = this.showItemPerPage;
        // }
        let data: any = [];
        this.searchData.filter[0].filterValue = this.searchTATName.trim();
        (this.searchData.filter[0].page = this.currentPagetatMatricsListdata),
            (this.searchData.filter[0].pageSize = this.tatMatricsListdataitemsPerPage);
        data = this.searchData;

        // console.log("this.searchData", this.searchData)
        const url =
            "/matrix/search?page=" +
            this.currentPagetatMatricsListdata +
            "&pageSize=" +
            this.tatMatricsListdataitemsPerPage +
            "&sortBy=Id&sortOrder=0";
        this.customermgmtservice.postMethod(url, data).subscribe(
            (response: any) => {
                if (response.responseCode == 404) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                    this.tatMatricsListData = [];
                } else {
                    this.tatMatricsListData = response.dataList;
                    this.tatMatricsListDatatotalRecords = response.totalRecords;
                }
            },
            (error: any) => {
                this.tatMatricsListDatatotalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');

                    this.tatMatricsListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            }
        );
    }

    clearSearchTAT() {
        this.searchTATName = "";
        this.searchService = "";
        this.gettatMatricsDataList("");
    }

    deleteTAT(data) {
        const url = "/matrix/delete";
        this.customermgmtservice.postMethod(url, data).subscribe(
            (response: any) => {
                if (
                    response.responseCode == 406 ||
                    response.responseCode == 417 ||
                    response.responseCode == 500
                ) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                } else {
                    if (this.currentPagetatMatricsListdata != 1 && this.tatMatricsListData.length == 1) {
                        this.currentPagetatMatricsListdata = this.currentPagetatMatricsListdata - 1;
                    }
                    if (!this.searchkey) {
                        this.gettatMatricsDataList("");
                    } else {
                        this.searchTAT();
                    }
                    this.toastr.success("Successful Deleted", 'Success!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // console.log(error, "error")

            }
        );
    }

    pageChangedViewTAT(pageNumber) {
        this.currentPageViewTATListdata = pageNumber;
    }

    canExit() {
        if (!this.MatricsFormGroup.dirty && !this.tatMatricsTAT.dirty && !this.tatMatricsTATForm.dirty)
            return true;
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

    keypressId(event: any) {
        const pattern = /^[0-9]+$/;
        let inputChar = String.fromCharCode(event.charCode);
        if (event.keyCode != 8 && event.keyCode != 9 && !pattern.test(inputChar)) {
            event.preventDefault();
        }
    }

    onInput(event: any) {
        const pattern = /^[0-9]+$/;
        let inputValue = event.target.value;

        // Remove non-numeric characters
        inputValue = inputValue.replace(/[^0-9]/g, "");


        inputValue = inputValue.slice(0, 5);


        if (event.target.value.length <= 5) {
            event.target.value = inputValue;
        }


        const mobileNumber = inputValue;
    }
    openTatMetricDialog(value?: any) {

        this.MatricsFormGroup.reset();


        this.tatMatricsTATForm.reset({
            action: "Notification",
            orderNo: 1,
            level: "Level 1",
            mtime: "",
            munit: "",
        });
        this.tatMatricsTAT.clear();

        this.tatMatricsDataSource.data = [];


        this.dialogRef = this.dialog.open(this.tatMetricDialogTemplate, {
            width: '1500px',
            maxWidth: '80vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });


        this.dialogRef.afterClosed().subscribe(() => {
            this.dialogRef = null!;

            this.MatricsFormGroup.reset();
            this.tatMatricsTATForm.reset({
                action: "Notification",
                orderNo: 1,
                level: "Level 1",
                mtime: "",
                munit: "",
            });


            this.tatMatricsTAT.clear();
            this.tatMatricsDataSource.data = [];


            this.listView = true;
            this.createView = false;
            this.detailView = false;
            this.submitted = false;
            this.istatMatricsEdit = false;
        });
    }

    deleteConfirmonTATDialog(item: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete Discount',
                description: `Are you sure you want to delete "${item.name}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteTAT(item);
            } else {
            }
        });
    }



    onCancel() {
        this.dialogRef.close();
        this.MatricsFormGroup.reset();
        this.tatMatricsTAT.controls = [];
        this.MatricsFormGroup.reset();
        this.tatMatricsTATForm.reset();
        this.tatMatricsTAT.clear();


        this.tatMatricsTATForm.patchValue({
            action: "Notification",
            orderNo: 1,
            level: "Level 1",
            mtime: "",
            munit: "",
        });

        // Reset views
        this.listView = true;
        this.createView = false;
        this.detailView = false;
        this.submitted = false;
        this.istatMatricsEdit = false;
    }

}
