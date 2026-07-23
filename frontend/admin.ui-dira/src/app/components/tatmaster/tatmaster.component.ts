import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { FormGroup, UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatTable } from "@angular/material/table";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { ToastrService } from "ngx-toastr"; // Added ToastrService import
import { Observable, Observer } from "rxjs";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { TICKETING_SYSTEMS } from "src/app/constants/aclConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { Regex } from "src/app/constants/regex";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { LoginService } from "src/app/service/login.service";
import { TicketReasonCategoryService } from "src/app/service/ticket-reason-category.service";

declare module '@angular/forms' {
    interface FormGroup {
        pick(keys: string[]): FormGroup;
    }
}

FormGroup.prototype.pick = function (keys: string[]): FormGroup {
    const group: { [key: string]: any } = {};

    keys.forEach(k => {
        const control = this.get(k);
        if (control) {
            group[k] = new (control.constructor as any)(control.value, control.validator, control.asyncValidator);
        }
    });

    return new FormGroup(group);
};

@Component({
    selector: "app-tatmaster",
    templateUrl: "./tatmaster.component.html",
    styleUrls: ["./tatmaster.component.css"],
    standalone: false
})
export class TATmasterComponent implements OnInit {
    isEditable = false;
    mainForm: FormGroup;

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
    TATMatrixListDatatotalRecords: any;
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
    pageItem = RadiusConstants.PER_PAGE_ITEMS;

    dataSourceData = [{}];

    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private toastr: ToastrService, // Replaced messageService with toastr
        private dialog: MatDialog,
        private TATMatrixService: TicketReasonCategoryService,
        loginService: LoginService
    ) {
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;

        this.createAccess = loginService.hasPermission(TICKETING_SYSTEMS.TAT_TICKET_CREATE);
        this.deleteAccess = loginService.hasPermission(TICKETING_SYSTEMS.TAT_TICKET_DELETE);
        this.editAccess = loginService.hasPermission(TICKETING_SYSTEMS.TAT_TICKET_EDIT);
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(TICKETING_SYSTEMS.TAT_TICKET_EDIT) || this.loginService.hasPermission(TICKETING_SYSTEMS.TAT_TICKET_DELETE)) {
            return ['id', 'name', 'status', 'action'];
        } else {
            return ['id', 'name', 'status'];
        }
    }
    ngOnInit(): void {
        this.ticketReasonCatFormGroup = this.fb.group({
            name: ["", Validators.required],
            status: ["", Validators.required],
            slaTimep1: ["", [Validators.pattern(Regex.numeric), Validators.required, Validators.min(0)]],
            slaTimep2: ["", [Validators.pattern(Regex.numeric), Validators.required, Validators.min(0)]],
            slaTime3: ["", [Validators.pattern(Regex.numeric), Validators.required, Validators.min(0)]],
            sunitp1: ["", Validators.required],
            sunitp2: ["", Validators.required],
            sunitp3: ["", Validators.required],
            rtime: ["", [Validators.pattern(Regex.numeric), Validators.required, Validators.min(0)]],
            runit: ["", Validators.required],
        });
        this.TATMatrixTATForm = this.fb.group({
            orderNo: [""],
            mtime2: ["", [Validators.pattern(Regex.numeric), Validators.required, Validators.min(0)]],
            munit: ["", [Validators.required, Validators.min(0)]],
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
        this.mainForm = this.fb.group({
            firstName: ['', Validators.required],   // Step 1
            lastName: ['', Validators.required],    // Step 1
            email: ['', [Validators.required, Validators.email]], // Step 1

            address: ['', Validators.required],     // Step 2
            city: ['', Validators.required],        // Step 2
            zipCode: ['', Validators.required]      // Step 2
        });
    }

    // ✅ Yeh sahi hai: ek hi FormControl reference rehta hai
    get step1Group(): FormGroup {
        return this.ticketReasonCatFormGroup.pick(['name', 'status', 'rtime', 'runit']);
    }

    get step2Group(): FormGroup {
        return this.ticketReasonCatFormGroup.pick(['slaTimep1', 'slaTimep2', 'slaTimep3', 'sunitp1', 'sunitp2', 'sunitp3']);
    }

    onSubmit() {
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
        const url = "/tickettatmatrix";
        this.TATMatrixService.postMethod(url, pagedata).subscribe(
            (response: any) => {
                this.TATMatrixListData = response.dataList;
                this.TATMatrixListDatatotalRecords = response.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
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
        this.listView = false;
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

    pageChangedTrcList(pageNumber) {
        this.currentPageTATMatrixListdata = pageNumber.pageIndex + 1;
        this.TATMatrixListdataitemsPerPage = pageNumber.pageSize;
        this.getTATMatrixDataList("");
    }

    deleteConfirmonTATField(TATFieldIndex: number) {
        const dialogRef = this.dialog.open(this.confirmDialog, {
            width: '400px',
            data: {
                title: 'Delete Confirmation',
                description: `Do you want to delete this TAT?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe((result) => {
            if (result === true) {
                this.onRemoveTAT(TATFieldIndex);
            } else {
                this.toastr.info(`Delete operation was cancelled`, 'Info!');
            }
        });
    }

    async onRemoveTAT(TATFieldIndex: number) {
        this.TATMatrixTAT.removeAt(TATFieldIndex);
        this.TATMatrixTAT.value.forEach((element, i) => {
            let n = i + 1;
            element.orderNo = n;
            element.level = `Level ${n}`;

            if (this.TATMatrixTAT.value.length == n) {
                this.TATMatrixTAT.patchValue(this.TATMatrixTAT.value);

                this.TATMatrixTATForm.patchValue({
                    orderNo: n + 1,
                    level: `Level ${n + 1}`,
                });
            }
        });
        if (this.TATMatrixTAT.value.length == 0) {
            this.TATMatrixTATForm.patchValue({
                orderNo: 1,
                level: `Level ${1}`,
            });
        }

        this.TATMatrixTable.renderRows();
    }

    addEditTicketReasonCat(id) {
        this.submitted = true;

        let createTATMatrixData: any = [];
        if (this.ticketReasonCatFormGroup.valid) {
            if (id) {
                const url = "/tickettatmatrix/update";
                createTATMatrixData = this.ticketReasonCatFormGroup.value;
                createTATMatrixData.id = id;
                createTATMatrixData.tatMatrixMappings = this.TATMatrixTAT.value;

                if (createTATMatrixData.tatMatrixMappings.length < 1) {
                    this.toastr.error('Please add TAT details', 'Failed!');
                } else {
                    this.TATMatrixService.postMethod(url, createTATMatrixData).subscribe(
                        (response: any) => {
                            if (response.responseCode == 406) {
                                this.toastr.error(`${response.responseMessage}`, 'Failed!');
                            } else if (response.responseCode == 417 || response.responseCode == 500) {
                                this.toastr.error(`${response.responseMessage}`, 'Failed!');
                            } else {
                                this.toastr.success(`Successfully Updated`, 'Success!');
                                this.clearTATMatrix();
                                this.isTATMatrixEdit = false;
                            }
                        },
                        (error: any) => {
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                        }
                    );
                }
            } else {
                const url = "/tickettatmatrix/save";

                createTATMatrixData = this.ticketReasonCatFormGroup.value;
                createTATMatrixData.tatMatrixMappings = this.TATMatrixTAT.value;

                if (createTATMatrixData.tatMatrixMappings.length < 1) {
                    this.toastr.error('Please add TAT details', 'Failed!');
                } else {
                    this.TATMatrixService.postMethod(url, createTATMatrixData).subscribe(
                        (response: any) => {
                            if (response.responseCode == 406 || response.responseCode == 417) {
                                this.toastr.error(`${response.responseMessage}`, 'Failed!');
                            } else {
                                this.toastr.success(`Successfully Added`, 'Success!');
                                this.clearTATMatrix();
                            }
                        },
                        (error: any) => {
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                        }
                    );
                }
            }
        } else {
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
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

    TATMatrixTATFormGroup(): UntypedFormGroup {
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

    onAddTATMatrixTATField() {
        this.TATMatrixTATSubmitted = true;
        if (this.TATMatrixTATForm.valid) {
            this.TATMatrixTAT.push(this.TATMatrixTATFormGroup());
            this.TATMatrixTATForm.reset();
            this.TATMatrixTATSubmitted = false;
            let orderN = this.TATMatrixTAT.length + 1;
            let level = `Level ${orderN}`;

            this.TATMatrixTATForm.patchValue({
                orderNo: orderN,
                level: level,
            });
            this.TATMatrixTable.renderRows();
            this.TATMatrixListDatatotalRecords = this.TATMatrixTAT.length
        }
    }

    pageChangedTATMatrixTATData(pageNumber) {
        this.currentPageTATMatrixTAT = pageNumber.pageIndex + 1;
        this.TATMatrixTATitemsPerPage = pageNumber.pageSize
    }

    editcustomerID = "";

    editTATMatrix(id) {
        let editTATMatrixData: any = [];
        this.ticketReasonCatFormGroup.reset();
        this.TATMatrixTATForm.reset();
        this.isTATMatrixEdit = true;
        this.listView = false;
        this.createView = true;
        this.detailView = false;

        if (this.TATMatrixTAT.controls) {
            this.TATMatrixTAT.controls = [];
        }
        const url = "/tickettatmatrix/" + id;
        this.TATMatrixService.getMethod(url).subscribe(
            (response: any) => {
                editTATMatrixData = response.data;
                this.editcustomerID = editTATMatrixData.id;
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

                this.TATMatrixTAT = this.fb.array([]);
                editTATMatrixData.tatMatrixMappings.forEach(element => {
                    this.TATMatrixTAT.push(this.fb.group(element));
                });
                this.TATMatrixTAT.patchValue(editTATMatrixData.tatMatrixMappings);

                let orderN = this.TATMatrixTAT.length + 1;
                let level = `Level ${orderN}`;

                this.TATMatrixTATForm.patchValue({
                    orderNo: orderN,
                    level: level,
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    trcAllDetails(data) {
        this.listView = true;
        this.createView = false;
        this.detailView = false;
        this.viewTrcData = data;
        this.dialog.open(this.detailsDialog, {
            width: '80%',
            disableClose: true
        });
    }

    deleteConfirmonTicketReasonCat(TrcData) {
        if (TrcData) {
            const dialogRef = this.dialog.open(this.confirmDialog, {
                width: '400px',
                data: {
                    title: 'Delete Confirmation',
                    description: `Do you want to delete this TAT?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe((result) => {
                if (result === true) {
                    this.deleteTrc(TrcData);
                } else {
                    this.toastr.info(`Delete operation was cancelled`, 'Info!');
                }
            });
        }
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

        const url = `/tickettatmatrix/searchAll`;
        this.TATMatrixService.postMethod(url, data).subscribe(
            (response: any) => {
                if (response?.dataList?.length <= 0) {
                    this.toastr.info('No Record Found!', 'Info!');
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
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
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
        const url = "/tickettatmatrix/delete";
        this.TATMatrixService.postMethod(url, data).subscribe(
            (response: any) => {
                if (response.responseCode == 406) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');
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
                    this.toastr.success(`Deleted Successfully`, 'Success!');
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
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
                const dialogRef = this.dialog.open(this.confirmDialog, {
                    width: '400px',
                    data: {
                        title: 'Alert',
                        description: `The filled data will be lost. Do you want to continue? (Yes/No)`,
                        yesLabel: 'Yes',
                        noLabel: 'No'
                    }
                });

                dialogRef.afterClosed().subscribe((result) => {
                    if (result === true) {
                        observer.next(true);
                        observer.complete();
                    }
                    else {
                        observer.next(false);
                        observer.complete();
                    }
                });

                return false;
            });
        }
    }

    @ViewChild('detailsDialog') detailsDialog!: TemplateRef<any>;
    @ViewChild('confirmDialog') confirmDialog!: TemplateRef<any>;
    @ViewChild('TATMatrixTable') TATMatrixTable!: MatTable<any>;

    displayedShowTATColumns: string[] = [
        'orderNo',
        'level',
        'mtime1',
        'mtime2',
        'mtime3',
        'munit',
        'action'
    ];
    displayedColumns: string[] = ['id', 'name', 'status', 'action'];
    displayedTATMatrixColumns: string[] = ['orderNo', 'level', 'mtime1', 'mtime2', 'mtime3', 'munit', 'action', 'delete'];
}
