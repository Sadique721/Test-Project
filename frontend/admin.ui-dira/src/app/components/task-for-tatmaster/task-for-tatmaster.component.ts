import { Component, OnInit, TemplateRef, ViewChild, } from "@angular/core";
import { FormArray, FormGroup, FormGroupDirective, UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { ToastrService } from "ngx-toastr";
import { ConfirmationService, MessageService } from "primeng/api";
import { Observable, Observer } from "rxjs";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { TASK_SYSTEMS, TICKETING_SYSTEMS } from "src/app/constants/aclConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { Regex } from "src/app/constants/regex";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { LoginService } from "src/app/service/login.service";
import { TaskForTATCategoryService } from "src/app/service/task-for-tat.service";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";

@Component({
    selector: "app-task-for-tatmaster",
    templateUrl: "./task-for-tatmaster.component.html",
    styleUrls: ["./task-for-tatmaster.component.css"],
    standalone: false
})
export class TaskTATmasterComponent implements OnInit {
    public loginService: LoginService;
    AclClassConstants;
    AclConstants;
    ticketReasonCatFormGroup: UntypedFormGroup;
    TATMatrixTAT: UntypedFormArray;
    TATMatrixTATForm: UntypedFormGroup;
    submitted = false;
    statusOptions = RadiusConstants.status;
    serviceData: any;
    teamListData: any;
    TATMatrixTATSubmitted = false;
    currentPageTATMatrixTAT = 1;
    TATMatrixTATitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    TATMatrixTATtotalRecords: string;
    currentPageTATMatrixListdata = 1;
    TATMatrixListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    TATMatrixListDatatotalRecords: number = 0;
    TATMatrixListData: any;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    isTATMatrixEdit = false;
    searchkey: string;
    searchTrcName: any = "";
    searchService: any = "";
    searchData: any;
    searchAllData: any;
    listView = true;
    createView = false;
    detailView = false;
    isViewMode = false;
    viewTrcData: any;
    currentPageViewTATListdata = 1;
    viewTATListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    viewTATListDatatotalRecords: any;
    levelData = [];
    departmentTypeData: any;

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
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    pageItem;
    // **************************
    title = "TAT";
    dataSource = new MatTableDataSource<any>([]);
    @ViewChild(MatSort) sort: MatSort = Object.create(null);
    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);
    @ViewChild('addEditTatForTaskDialog') addEditTatForTaskDialog!: TemplateRef<any>;
    @ViewChild('viewTatForTaskDialog') viewTatForTaskDialog!: TemplateRef<any>;
    dialogRef!: MatDialogRef<any>;
    TATMatrixTATFormArray: FormArray;
    dataSources = new MatTableDataSource<FormGroup>();
    dataSourceArr = new MatTableDataSource;
    dataSourceData = [{}];
    rowArray: any[] = [];
    editId: number | null = null;


    constructor(
        private fb: UntypedFormBuilder,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private TATMatrixService: TaskForTATCategoryService,
        loginService: LoginService,
        // *******************
        private dialog: MatDialog,
        private toastr: ToastrService
    ) {
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;

        this.createAccess = loginService.hasPermission(TASK_SYSTEMS.TAT_TASK_CREATE);
        this.deleteAccess = loginService.hasPermission(TASK_SYSTEMS.TAT_TASK_DELETE);
        this.editAccess = loginService.hasPermission(TASK_SYSTEMS.TAT_TASK_EDIT);
    }

    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(TASK_SYSTEMS.TAT_TASK_EDIT) || this.loginService.hasPermission(TASK_SYSTEMS.TAT_TASK_DELETE)) {
            return ['id', 'Name', 'Status', 'Action'];
        } else {
            return ['id', 'Name', 'Status'];
        }
    }

    ngOnInit(): void {
        this.ticketReasonCatFormGroup = this.fb.group({
            name: ["", Validators.required],
            status: ["", Validators.required],
            slaTimep1: ["", [Validators.pattern(Regex.numeric), Validators.required, Validators.min(0)]],
            slaTimep2: ["", [Validators.pattern(Regex.numeric), Validators.required, Validators.min(0)]],
            slaTime3: ["", [Validators.pattern(Regex.numeric), Validators.required, Validators.min(0)]],
            sunitp1: ["Day", Validators.required],
            sunitp2: ["Day", Validators.required],
            sunitp3: ["Day", Validators.required],
            rtime: ["", [Validators.pattern(Regex.numeric), Validators.required, Validators.min(0)]],
            runit: ["Day", Validators.required],

        });

        this.TATMatrixTATForm = this.fb.group({
            orderNo: [""],
            mtime2: ["", [Validators.pattern(Regex.numeric), Validators.required, Validators.min(0)]],
            munit: ["Day", [Validators.required]],
            action: ["", [Validators.required, Validators.min(0)]],
            mtime1: ["", [Validators.required, Validators.min(0)]],
            mtime3: ["", [Validators.pattern(Regex.numeric), Validators.required, Validators.min(0)]],
            level: [""],
            tatMappingtId: [""],
            id: [""],
        });
        this.TATMatrixTAT = this.fb.array([]);

        this.getTATMatrixDataList("");
        this.levelAllData();

        this.searchData = {
            filters: [
                {
                    filterColumn: "any",
                    filterOperator: "equalto",
                    filterCondition: "and",
                    filterDataType: "",
                    filterValue: "",
                    port: "",
                    salesRepresentative: "",
                    serviceArea: "",
                    serviceNetwork: "",
                    slot: "",
                },
            ],
            page: "",
            pageSize: "",
            sortBy: "createdate",
            sortOrder: 0,
        };
        this.TATMatrixTATFormArray = this.fb.array([]);
        this.onAddTATMatrixTATField();
    }

    createTATMatrixTATFormGroup(): FormGroup {
        return this.fb.group({
            orderNo: ['', Validators.required],
            level: ['', Validators.required],
            mtime1: ['', [Validators.required, Validators.pattern(/^[0-9]+$/)]],
            mtime2: ['', [Validators.required, Validators.pattern(/^[0-9]+$/)]],
            mtime3: ['', [Validators.required, Validators.pattern(/^[0-9]+$/)]],
            munit: ['', Validators.required],
            action: ['', Validators.required],
            tatMappingtId: [''],
            id: ['']
        });
    }


    refreshDataSource() {
        this.dataSource.data = this.TATMatrixTATFormArray.controls as FormGroup[];
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }
    levelAllData() {
        for (let i = 1; i < 100; i++) {
            this.levelData.push({ label: `Level ${i}` });
        }
    }

    getTATMatrixDataList(list) {
        let size;
        this.searchkey = "";
        const page = this.currentPageTATMatrixListdata;
        if (list) {
            size = list;
            this.currentPageTATMatrixListdata = list;
        } else {
            size = this.TATMatrixListdataitemsPerPage;
        }

        const pagedata = {
            page,
            pageSize: size,
        };
        const url = "/tasktatmatrix";
        this.TATMatrixService.postMethod(url, pagedata).subscribe(
            (response: any) => {
                this.TATMatrixListData = response.dataList;
                this.dataSource = new MatTableDataSource<any>(this.TATMatrixListData);

                this.TATMatrixListDatatotalRecords = response.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    async searchViewTrc() {
        this.listView = true;
        this.createView = false;
        this.detailView = false;
        this.pageItem = this.TATMatrixListdataitemsPerPage;
        this.getTATMatrixDataList("");
        this.searchTrcName = "";
        this.searchService = "";
    }

    async createTicketMaster() {
        this.createView = true;
        this.detailView = false;
        this.submitted = false;
        this.isTATMatrixEdit = false;
        this.ticketReasonCatFormGroup.reset();
        this.TATMatrixTATForm.reset();
        this.TATMatrixTAT.controls = [];
        this.TATMatrixTATForm.patchValue({
            orderNo: 1,
            level: "Level 1",
        });
    }

    TotalItemPerPage(event) {
        this.TATMatrixListdataitemsPerPage = Number(event.value);
        this.currentPageTATMatrixListdata = 1;
        if (this.TATMatrixListData > 1) {
            this.currentPageTATMatrixListdata = 1;
        }
        this.getTATMatrixDataList(this.showItemPerPage);
    }

    pageChangedTrcList(event: PageEvent): void {
        this.currentPageTATMatrixListdata = event.pageIndex + 1;
        this.TATMatrixListdataitemsPerPage = event.pageSize;

        this.getTATMatrixDataList("");
    }

    async onRemoveTAT(index: number) {
        this.rowArray.splice(index, 1);
        this.rowArray = this.TATMatrixTAT.value.map((el, i) => ({
            ...el,
            orderNo: i + 1,
            level: `Level ${i + 1}`
        }));

        const lastIndex = this.TATMatrixTAT.value.length;
        this.TATMatrixTATForm.patchValue({
            orderNo: lastIndex + 1,
            level: `Level ${lastIndex + 1}`
        });

        if (this.rowArray.length === 0) {
            this.TATMatrixTATForm.patchValue({
                orderNo: 1,
                level: `Level 1`
            });
        }
    }

    addEditTicketReasonCat(id) {
        this.submitted = true;

        let createTATMatrixData: any = [];
        if (this.ticketReasonCatFormGroup.valid) {
            if (id) {
                const url = "/tasktatmatrix/update";
                createTATMatrixData = this.ticketReasonCatFormGroup.value;
                createTATMatrixData.id = id;
                // createTATMatrixData.tatMatrixMappings = this.TATMatrixTAT.value;
                createTATMatrixData.tatMatrixMappings = this.rowArray;
                if (createTATMatrixData.tatMatrixMappings.length < 1) {
                    this.toastr.error(`Please add TAT details`, 'Error!');
                } else {
                    this.TATMatrixService.postMethod(url, createTATMatrixData).subscribe(
                        (response: any) => {
                            this.editId = null;
                            this.dialogRef.close();
                            this.ticketReasonCatFormGroup.reset();
                            this.TATMatrixTATForm.reset();

                            if (response.responseCode == 406) {
                                this.toastr.error(`${response.responseMessage}`, 'Error!');
                            } else if (response.responseCode == 417 || response.responseCode == 500) {
                                this.toastr.error(`${response.responseMessage}`, 'Error!');
                            } else {
                                this.toastr.success(`Successfully Updated`, 'Success!');
                                this.clearTATMatrix();
                                this.isTATMatrixEdit = false;
                                this.closeDialog();

                            }
                        },
                        (error: any) => {
                            this.toastr.error(`${error.error.ERROR}`, 'Error!');
                        }
                    );
                }
            } else {
                const url = "/tasktatmatrix/save";
                createTATMatrixData = this.ticketReasonCatFormGroup.value;
                // createTATMatrixData.tatMatrixMappings = this.TATMatrixTAT.value;
                createTATMatrixData.tatMatrixMappings = this.rowArray;


                if (createTATMatrixData.tatMatrixMappings.length < 1) {
                    this.toastr.error(`Please add TAT details`, 'Error!');
                } else {
                    this.TATMatrixService.postMethod(url, createTATMatrixData).subscribe(
                        (response: any) => {
                            this.dialogRef.close();
                            this.ticketReasonCatFormGroup.reset();
                            this.TATMatrixTATForm.reset();
                            if (response.responseCode == 406 || response.responseCode == 417) {
                                this.toastr.error(`${response.responseMessage}`, 'Error!');
                            } else {
                                this.toastr.success(`Successfully Added`, 'Success!');
                                this.clearTATMatrix();
                            }
                        },
                        (error: any) => {
                            this.toastr.error(`${error.error.ERROR}`, 'Error!');
                        }
                    );
                }
            }
        } else {
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            };
        }
    }

    clearTATMatrix() {
        this.ticketReasonCatFormGroup.reset();
        this.submitted = false;
        this.TATMatrixTAT.controls = [];
        this.listView = true;
        this.createView = false;
        this.getTATMatrixDataList("");
    }
    orderno: number = 1;
    levelno: number = 1;
    TATMatrixTATFormGroup(): UntypedFormGroup {
        this.orderno = this.orderno + 1;
        this.levelno = this.levelno + 1;
        const newIndex = this.rowArray.length + 1;
        const newRow = {
            orderNo: newIndex,
            level: 'Level ' + newIndex,
            mtime1: this.TATMatrixTATForm.value.mtime1 || 0,
            mtime2: this.TATMatrixTATForm.value.mtime2 || 0,
            mtime3: this.TATMatrixTATForm.value.mtime3 || 0,
            munit: this.TATMatrixTATForm.value.munit || null,
            action: this.TATMatrixTATForm.value.action || null
        };
        this.rowArray.push(newRow);
        this.rowArray = [...this.rowArray];
        return this.fb.group({
            orderNo: [this.TATMatrixTATForm.value.orderNo],
            mtime2: [
                this.TATMatrixTATForm.value.mtime2,
                [Validators.pattern(Regex.numeric), Validators.required],
            ],
            munit: [this.TATMatrixTATForm.value.munit, [Validators.required]],
            action: [this.TATMatrixTATForm.value.action, [Validators.required]],
            mtime3: [
                this.TATMatrixTATForm.value.mtime3,
                [Validators.pattern(Regex.numeric), Validators.required],
            ],
            mtime1: [this.TATMatrixTATForm.value.mtime1, Validators.required],
            level: [this.TATMatrixTATForm.value.level],
            tatMappingtId: [this.TATMatrixTATForm.value.tatMappingtId],
            id: [this.TATMatrixTATForm.value.id],
        });
    }
    get tatRows() {
        return this.TATMatrixTAT.controls;
    }

    onAddTATMatrixTATField() {
        this.TATMatrixTATSubmitted = true;

        Object.keys(this.TATMatrixTATForm.controls).forEach(key => {
            this.TATMatrixTATForm.controls[key].markAsTouched();
            this.TATMatrixTATForm.controls[key].updateValueAndValidity();
        });
        if (this.TATMatrixTATForm.invalid) {
            return;
        }
        if (this.TATMatrixTATForm.valid) {
            let orderN = this.TATMatrixTAT.length + 1;
            let level = `Level ${orderN}`;

            this.TATMatrixTATForm.patchValue({
                orderNo: orderN,
                level: level,
            });
            this.TATMatrixTAT.push(this.TATMatrixTATFormGroup());

            this.TATMatrixTATForm.reset();
            Object.keys(this.TATMatrixTATForm.controls).forEach(key => {
                this.TATMatrixTATForm.controls[key].markAsPristine();
                this.TATMatrixTATForm.controls[key].markAsUntouched();
                this.TATMatrixTATForm.controls[key].setErrors(null);
            });
            this.TATMatrixTATSubmitted = false;
        }
    }

    onPageChange(event: PageEvent) {

        const startIndex = event.pageIndex * event.pageSize;
        const endIndex = startIndex + event.pageSize;
        this.dataSource.data = this.rowArray.slice(startIndex, endIndex);
    }

    pageChangedTATMatrixTATData(pageNumber) {
        this.currentPageTATMatrixTAT = pageNumber;
    }
    editcustomerID = "";
    editTATMatrix(id) {
        this.editId = id;
        this.ticketReasonCatFormGroup.reset();
        this.TATMatrixTATForm.reset();
        this.TATMatrixTATSubmitted = false;
        let editTATMatrixData: any = [];
        this.isTATMatrixEdit = true;
        this.createView = true;
        this.detailView = false;

        if (this.TATMatrixTAT.controls) {
            this.TATMatrixTAT.controls = [];
        }
        const url = "/tasktatmatrix/" + id;
        this.TATMatrixService.getMethod(url).subscribe(
            (response: any) => {
                editTATMatrixData = response.data;

                this.ticketReasonCatFormGroup.patchValue({
                    name: editTATMatrixData.name,
                    status: editTATMatrixData.status,
                    slaTimep1: editTATMatrixData.slaTimep1,
                    slaTimep2: editTATMatrixData.slaTimep2,
                    slaTime3: editTATMatrixData.slaTime3,
                    sunitp1: editTATMatrixData.sunitp1,
                    sunitp2: editTATMatrixData.sunitp2,
                    sunitp3: editTATMatrixData.sunitp3,
                    rtime: editTATMatrixData.rtime,
                    runit: editTATMatrixData.runit,
                });

                // let orderN = editTATMatrixData.tatMatrixMappings.length + 1;
                // let level = `Level ${orderN}`;

                // this.TATMatrixTATForm.patchValue({
                //     orderNo: orderN,
                //     level: level,
                // });

                // map existing rows from API
                this.rowArray = editTATMatrixData.tatMatrixMappings.map((element: any, index: number) => {
                    const group = this.fb.group({
                        ...element,
                        orderNo: index + 1,
                        level: `Level ${index + 1}`
                    });
                    this.TATMatrixTAT.push(group);
                    return group.value;
                });

                // set next orderNo / level for new row add
                const lastIndex = this.rowArray.length;
                this.orderno = lastIndex + 1;
                this.levelno = lastIndex + 1;
                this.TATMatrixTAT = this.fb.array([]);

                this.rowArray = editTATMatrixData.tatMatrixMappings.map((element: any) => {
                    this.TATMatrixTAT.push(this.fb.group(element));
                    return element;
                });
                this.rowArray = [...this.rowArray];
                // open dialog
                this.dialogRef = this.dialog.open(this.addEditTatForTaskDialog, {
                    width: '1500px',
                    maxWidth: '90vw',
                    height: 'auto',
                    autoFocus: false,
                    disableClose: true,
                    id: id
                });

                this.dialogRef.afterClosed().subscribe(() => {
                    this.ticketReasonCatFormGroup.reset();
                    this.TATMatrixTATForm.reset();
                    this.dialogRef = null;
                    this.rowArray = []
                });
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    trcAllDetails(data) {
        this.createView = false;
        this.detailView = true;
        this.isViewMode = true;
        this.isTATMatrixEdit = false;


        this.viewTrcData = data;
        this.ticketReasonCatFormGroup.patchValue(data);

        this.rowArray = data.tatMatrixMappings ? data.tatMatrixMappings.map((el: any) => el) : [];

        this.TATMatrixTAT = this.fb.array([]);
        if (data.tatMatrixMappings) {
            data.tatMatrixMappings.forEach((el: any) => {
                this.TATMatrixTAT.push(this.fb.group(el));
            });
        }
        this.rowArray = this.TATMatrixTAT.value;

        this.dialogRef = this.dialog.open(this.viewTatForTaskDialog, {
            width: '1200px',
            maxWidth: '90vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });

        this.dialogRef.afterClosed().subscribe(() => {
            this.dialogRef = null;
            this.isViewMode = false;
        });

    }

    deleteConfirmonTicketReasonCat(TrcData) {
        if (TrcData) {
            this.confirmationService.confirm({
                message: "Do you want to delete this TAT ?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.deleteRow(TrcData);
                },
                reject: () => {
                    this.toastr.info(`You have rejected`, 'Info!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Rejected",
                    //     detail: "You have rejected",
                    // });
                },
            });
        }
    }

    deleteRow(index: number) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete TAT',
                description: `Are you sure you want to delete ?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.rowArray = this.rowArray.filter((_, i) => i !== index);
                if (this.rowArray.length === 0) {
                    this.orderno = 1;
                    this.levelno = 1;
                }
                this.rowArray.forEach((item, i) => {
                    item.orderNo = i + 1;
                    this.orderno = item.orderNo + 1;
                    item.level = `Level ${i + 1}`;
                    this.levelno = item.orderNo + 1;
                });
                this.rowArray = [...this.rowArray];
            } else {
            }
        });
    }


    deleteConfirmonTicketReasonCatDialog(item: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete TAT',
                description: `Are you sure you want to delete "${item.name}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteTrc(item);
            } else {
            }
        });
    }

    searchTrc() {
        if (!this.searchkey || this.searchkey !== this.searchData) {
            this.currentPageTATMatrixListdata = 1;
        }
        this.searchkey = this.searchData;
        if (this.showItemPerPage) {
            this.TATMatrixListdataitemsPerPage = this.showItemPerPage;
        }
        let data: any = [];
        this.searchData.filters[0].filterColumn = "any";
        this.searchData.filters[0].filterValue = this.searchTrcName ? this.searchTrcName.trim() : "";

        this.searchData.page = this.currentPageTATMatrixListdata;
        this.searchData.pageSize = this.TATMatrixListdataitemsPerPage;
        data = this.searchData;

        // console.log("this.searchData", this.searchData)
        const url = `/tasktatmatrix/searchAll`;
        this.TATMatrixService.postMethod(url, data).subscribe(
            (response: any) => {
                const listData = response.dataList;
                this.dataSource = new MatTableDataSource<any>(listData);
                if (this.paginator) {
                    this.dataSource.paginator = this.paginator;
                }
                if (response?.dataList?.length <= 0) {
                    this.toastr.info(`No Record Found!`, 'Info!');
                    this.TATMatrixListData = [];
                } else {
                    this.TATMatrixListData = response.dataList;
                    this.TATMatrixListDatatotalRecords = response.totalRecords;
                }

            },
            (error: any) => {
                this.TATMatrixListDatatotalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');
                    this.TATMatrixListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Error!');
                }
            }
        );
    }

    clearSearchTrc() {
        this.searchTrcName = "";
        this.searchService = "";
        this.getTATMatrixDataList("");
    }

    deleteTrc(data) {
        const url = "/tasktatmatrix/delete";
        this.TATMatrixService.postMethod(url, data).subscribe(
            (response: any) => {
                if (response.responseCode == 406) {
                    this.toastr.error(`${response.responseMessage}`, 'Error!');
                } else if (response.responseCode == 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                } else {
                    if (this.currentPageTATMatrixListdata != 1 && this.TATMatrixListData.length == 1) {
                        this.currentPageTATMatrixListdata = this.currentPageTATMatrixListdata - 1;
                    }
                    if (!this.searchkey) {
                        this.getTATMatrixDataList("");
                    } else {
                        this.searchTrc();
                    }
                    this.toastr.success(`Successfully Deleted`, 'Success!');
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    pageChangedViewTAT(pageNumber) {
        this.currentPageViewTATListdata = pageNumber;
    }
    canExit() {
        if (!this.ticketReasonCatFormGroup.dirty && !this.TATMatrixTATForm.dirty) return true;
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

    addEditTaskForMasterDialog() {
        this.ticketReasonCatFormGroup.reset();
        // add default value
        this.ticketReasonCatFormGroup.patchValue({
            sunitp1: 'Day',
            sunitp2: 'Day',
            sunitp3: 'Day',
            runit: 'Day'
        });
        this.TATMatrixTATForm.reset();
        this.TATMatrixTATSubmitted = false;
        this.createView = true;

        this.isTATMatrixEdit = false;
        this.createView = true;
        this.detailView = false;
        this.editcustomerID = null;

        this.dialogRef = this.dialog.open(this.addEditTatForTaskDialog, {
            width: '1500px',
            maxWidth: '90vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });

        this.dialogRef.afterClosed().subscribe(result => {
            this.ticketReasonCatFormGroup.reset();
            this.TATMatrixTATForm.reset();
            this.dialogRef = null;
        });

    }
    closeDialog() {
        this.submitted = false;
        this.addEditTatForTaskDialog = null;
        this.ticketReasonCatFormGroup.reset();
        this.TATMatrixTATForm.reset();
        this.rowArray = [];
        this.orderno = 1;
        this.levelno = 1;
        this.dialogRef.close();
    }
}
