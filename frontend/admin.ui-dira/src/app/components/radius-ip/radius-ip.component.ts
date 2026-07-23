import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, FormArray } from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { IpManagementService } from "src/app/service/ip-management.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { Regex } from "src/app/constants/regex";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { Observable, Observer } from "rxjs";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { NETWORKS } from "src/app/constants/aclConstants";
import { RadiusIpService } from "src/app/service/radius-ip.service";
import { RADIUS_CONSTANTS } from "src/app/constants/aclConstants";
import { MatTableDataSource } from "@angular/material/table";
import { MatSort } from "@angular/material/sort";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { ToastrService } from 'ngx-toastr';
@Component({
    selector: "app-radius-ip",
    templateUrl: "./radius-ip.component.html",
    styleUrls: ["./radius-ip.component.css"],
    standalone: false
})
export class RadiusIpManagementComponent implements OnInit {
    ipForm: UntypedFormGroup;
    searchForm: UntypedFormGroup;
    listView: boolean = true;
    createView: boolean = false;
    detailView: boolean = false;
    submitted: boolean = false;
    isIpEdit: boolean = false;
    charecter150 = "^.{0,150}$";
    createIpData: any;
    ipListData: any;
    currentPageIpListdata = 1;
    ipListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    ipListdatatotalRecords: number;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    viewIpData: any = {
        status: ""
    };

    UsageCategory = [
        { label: "RADIUS", value: "RADIUS" },
        { label: "INVENTORY", value: "INVENTORY" }
    ];

    IpPool = [
        { label: "Yes", value: true },
        { label: "No", value: false }
    ];
    defaultPoolFlagValue = [
        { label: "Yes", value: true },
        { label: "No", value: false }
    ];
    statusOptions = RadiusConstants.status;
    searchSubmitted: boolean = false;
    searchkey: string;
    ipData: any[];
    AclClassConstants;
    AclConstants;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    public loginService: LoginService;

    ipPoolDataList: any;
    ippoollist: any;
    selectedPoolId: any;
    clientData: any[];
    clientDataList: any[];
    ipDataList: any[];
    ipAddress: string;

    displayedColumns: string[] = ['poolName', 'ipRange', 'usageCategory', 'status', 'action'];
    dataSource = new MatTableDataSource([]);
    dataSource1 = new MatTableDataSource([]);
    @ViewChild(MatSort) sort: MatSort = Object.create(null);
    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);
    @ViewChild('ipDialog') ipDialog!: TemplateRef<any>;
    @ViewChild('ipDetailDialog') ipDetailDialog!: TemplateRef<any>;
    dialogRef!: MatDialogRef<any>;
    dialogRefs!: MatDialogRef<any>;
    // displayedColumns: string[] = ['ipAddress', 'status', 'userName', 'blockBySessionId', 'nasIpAddress'];



    constructor(
        private toastr: ToastrService,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private ipManagementService: IpManagementService,
        private radiusipManagementService: RadiusIpService,

        private commondropdownService: CommondropdownService,
        loginService: LoginService,
        private dialog: MatDialog
    ) {
        this.createAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_IP_MANAGEMENT_CREATE);
        this.editAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_IP_MANAGEMENT_EDIT);
        this.deleteAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_IP_MANAGEMENT_DELETE);
        this.loginService = loginService;
        this.isIpEdit = !this.createAccess && this.editAccess ? true : false;
    }

    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_IP_MANAGEMENT_EDIT) || this.loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_IP_MANAGEMENT_DELETE)) {
            return ['poolName', 'ipRange', 'usageCategory', 'status', 'action'];
        } else {
            return ['poolName', 'ipRange', 'usageCategory', 'status'];
        }
    }

    ngOnInit(): void {
        this.ipForm = this.fb.group({
            broadcastIp: [""],
            remark: ["", [Validators.required, Validators.pattern(this.charecter150)]],
            firstHost: [""],
            ipRange: ["", Validators.required],
            lastHost: [""],
            netMask: [""],
            networkIp: [""],
            poolName: ["", Validators.required],
            usageCategory: ["", Validators.required],
            status: ["", Validators.required],
            totalHost: [""],
            poolId: [""]
        });
        this.searchForm = this.fb.group({
            ipAddress: ["", Validators.required]
        });
        this.getIpDataList(this.ipListdataitemsPerPage);
    }

    createIpView() {
        this.listView = false;
        this.createView = true;
        this.dialogRef = this.dialog.open(this.ipDialog, {
            width: '800px',
            disableClose: true
        });
        this.detailView = false;
        this.submitted = false;
        this.isIpEdit = false;
        this.ipForm.reset();
    }
    onCancel() {
        this.dialogRef.close();
        this.getIpDataList("");
    }
    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    listIpView() {
        this.listView = true;
        this.createView = false;
        this.detailView = false;
        this.currentPageIpListdata = 1;
        this.getIpDataList(this.ipListdataitemsPerPage);
    }

    getIpDataList(list) {
        let size: number;
        let page = this.currentPageIpListdata;

        if (list) {
            size = list;
            this.ipListdataitemsPerPage = list;
        } else {
            size = this.ipListdataitemsPerPage;
        }

        const mvnoId = localStorage.getItem("mvnoId");
        const url = `/ippool/search?mvnoId=${mvnoId}&page=${page}&size=${size}`;

        this.radiusipManagementService.getMethod(url, this.ipData).subscribe(
            (response: any) => {
                this.ipListData = response.ippoollist.data;
                this.dataSource = new MatTableDataSource<any>(this.ipListData);

                this.ipListdatatotalRecords = response.ippoollist.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    getIpDataListForSearch(id, list) {
        let size;
        this.searchkey = "";
        let page = this.currentPageIpListdata;
        if (list) {
            size = list;
            this.ipListdataitemsPerPage = list;
        } else {
            size = this.ipListdataitemsPerPage;
        }
        const mvnoId = localStorage.getItem("mvnoId");
        const url = `/ippool/allocation/search?mvnoId=${mvnoId}&page=${page}&size=${size}&poolId=${id}`;
        this.radiusipManagementService.getMethod(url, this.ipData).subscribe(
            (response: any) => {
                this.ipPoolDataList = response.ippoollist.data;
                this.dataSource1 = new MatTableDataSource<any>(this.ipListData);


                this.ipListdatatotalRecords = response.ippoollist.totalRecords;
                this.selectedPoolId = response.poolId;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }
    // pageChanged(pageNumber) {
    //     const id = this.viewIpData.poolId;
    //     this.currentPageIpListdata = pageNumber;
    //     this.getIpDataListForSearch(id, this.showItemPerPage);

    //     if (!this.searchkey) {
    //     } else {
    //         this.searchIPByIpAddress();
    //     }
    // }
    pageChanged(event: PageEvent): void {
        const id = this.viewIpData.poolId;
        this.ipListdataitemsPerPage = event.pageSize;
        this.currentPageIpListdata = event.pageIndex;
        this.getIpDataListForSearch(id, this.showItemPerPage);
        if (!this.searchkey) {
        } else {
            this.searchIPByIpAddress();
        }
    }

    TotalItemPerPageIp(event) {
        const id = this.viewIpData.poolId;
        this.showItemPerPage = Number(event.value);
        if (this.currentPageIpListdata > 1) {
            this.currentPageIpListdata = 1;
        }
        if (this.searchkey == null || !this.searchkey) {
            this.getIpDataListForSearch(id, this.showItemPerPage);
        } else {
            this.searchIPByIpAddress();
        }
    }
    clearSearchForm() {
        const id = this.viewIpData.poolId;
        this.searchSubmitted = false;
        this.ipForm.reset();
        this.currentPageIpListdata = 1;
        this.getIpDataListForSearch(id, "");
        this.searchkey = null;
        this.searchForm.reset();
    }
    async IpAllDetails(id) {
        this.ipListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
        await this.getIpbyId(id);
        this.currentPageIpListdata = 1;
        this.getIpDataListForSearch(id, "");
        this.detailView = true;
        this.dialogRef = this.dialog.open(this.ipDetailDialog, {
            width: '1000px',
            disableClose: true
        });
        this.listView = false;
        this.createView = false;
    }

    async editIp(id) {
        this.listView = false;
        this.createView = true;
        this.detailView = false;
        this.ipForm.reset();
        this.isIpEdit = true;
        this.dialogRef = this.dialog.open(this.ipDialog, {
            width: '800px'
        });
        await this.getIpbyId(id);
    }

    async getIpbyId(id) {
        const url = `/ippool/findIpPoolById?ipPoolId=${id}&mvnoId=${localStorage.getItem("mvnoId")}`;
        await this.radiusipManagementService.findIpPoolById(url).subscribe(
            (response: any) => {
                this.viewIpData = response.ippool;
                this.ipForm.patchValue(this.viewIpData);
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    // deleteConfirmonIp(id) {
    //     if (id) {
    //         this.confirmationService.confirm({
    //             message: "Do you want to delete this IPPoolConfiguration?",
    //             header: "Delete Confirmation",
    //             icon: "pi pi-info-circle",
    //             accept: () => {
    //                 this.getIpbyId(id);
    //                 this.deleteIp(id);
    //             },
    //             reject: () => {
    //                 this.messageService.add({
    //                     severity: "info",
    //                     summary: "Rejected",
    //                     detail: "You have rejected"
    //                 });
    //             }
    //         });
    //     }
    // }
    deleteConfirmonIp(item: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete Confirmation ',
                description: `Are you sure you want to delete "${item.poolName}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.getIpbyId(item.poolId);
                this.deleteIp(item.poolId);
            } else {
            }
        });
    }

    deleteIp(id: string) {
        const url = `/ippool/delete?mvnoId=${localStorage.getItem("mvnoId")}`;
        this.viewIpData.poolId = id;

        this.radiusipManagementService.postMethod(url, this.viewIpData).subscribe(
            (response: any) => {
                if (response.status === 400) {
                    this.toastr.error(`${response.errorMessage}`, 'Failed!');

                } else {
                    this.commondropdownService.clearCacheCMS("/ippool/all");
                    this.getIpDataList(this.ipListdataitemsPerPage);
                    this.toastr.success(`${response.message}`, 'Successfully!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageIpListdata > 1) {
            this.currentPageIpListdata = 1;
        }
        this.getIpDataList(this.showItemPerPage);
    }

    pageChangedIpList(event: PageEvent) {
        this.currentPageIpListdata = event.pageIndex + 1;
        this.ipListdataitemsPerPage = event.pageSize;
        this.getIpDataList(this.ipListdataitemsPerPage);
    }

    addEditIp(id: number) {
        this.submitted = true;
        if (this.ipForm.valid) {
            this.createIpData = this.ipForm.getRawValue();
            this.createIpData.poolId = id;

            const url = id
                ? "/ippool/updateIPPool?mvnoId=" + localStorage.getItem("mvnoId")
                : "/ippool/saveIPPool?mvnoId=" + localStorage.getItem("mvnoId");

            if (!id) {
                this.createIpData.isDelete = true;
            } else {
                this.createIpData.isDelete = true;
            }

            this.radiusipManagementService.postMethod(url, this.createIpData).subscribe(
                (response: any) => {
                    if (response.status === 200) {
                        this.ipForm.reset();
                        this.toastr.success(`${response.message}`, 'Successfully!');


                        this.submitted = false;
                        this.listView = true;
                        this.createView = false;
                        this.isIpEdit = false;
                        this.dialogRef.close();
                        this.commondropdownService.clearCacheCMS("/ippool/all");
                        this.getIpDataList(this.ipListdataitemsPerPage);
                    } else {
                        this.toastr.success(`${response.errorMessage}`, 'Failed!');

                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                }
            );
        }
    }

    canExit() {
        if (!this.ipForm.dirty) return true;
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

    async searchIPByIpAddress() {
        this.searchSubmitted = true;

        if (this.searchForm.value.ipAddress == null) {
            this.searchForm.value.ipAddress = "";
        }

        if (this.searchForm.valid) {
            if (!this.searchkey || this.searchkey !== this.searchForm.value.ipAddress.trim()) {
                this.currentPageIpListdata = 1;
            }

            if (this.showItemPerPage) {
                this.ipListdataitemsPerPage = this.showItemPerPage;
            }

            const serachIpAddress = this.searchForm.value.ipAddress.trim()
                ? this.searchForm.value.ipAddress.trim()
                : "";

            this.searchkey = serachIpAddress;
            this.ipPoolDataList = [];

            this.radiusipManagementService
                .searchByIp(
                    this.viewIpData.poolId,
                    serachIpAddress,
                    this.ipListdataitemsPerPage,
                    this.currentPageIpListdata
                )
                .subscribe(
                    (response: any) => {
                        this.ipPoolDataList.push(response.ippoollist);
                        this.ipPoolDataList = new MatTableDataSource<any>(this.ipPoolDataList);
                    },
                    (error: any) => {
                        if (error.error.status == 404) {
                            this.ipListdatatotalRecords = 0;
                            this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                        } else {
                            this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                        }
                    }
                );
        }
    }
}
