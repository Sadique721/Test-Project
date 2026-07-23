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
import { MatTableDataSource } from "@angular/material/table";
import { MatSort } from "@angular/material/sort";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { ToastrService } from "ngx-toastr";

@Component({
    selector: "app-ip-management",
    templateUrl: "./ip-management.component.html",
    styleUrls: ["./ip-management.component.css"],
    standalone: false
})
export class IpManagementComponent implements OnInit {
    ipForm: UntypedFormGroup;
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
    ipListdatatotalRecords: String;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    viewIpData: any = {
        status: ""
    };
    PoolType = [
        { label: "Public", value: "Public" },
        { label: "Private", value: "Private" }
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

    AclClassConstants;
    AclConstants;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    public loginService: LoginService;
    //************************** */
    title = "IP Management";
    dataSource = new MatTableDataSource<any>([]);
    @ViewChild(MatSort) sort: MatSort = Object.create(null);
    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);
    @ViewChild('addEditIPDialog') addEditIPDialog!: TemplateRef<any>;
    @ViewChild('viewIpDetailDialog') viewIpDetailDialog!: TemplateRef<any>;
    dialogRef!: MatDialogRef<any>;
    dialogRef2!: MatDialogRef<any>;
    createUpdateName: string;

    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private ipManagementService: IpManagementService,
        private commondropdownService: CommondropdownService,
        loginService: LoginService,
        // 
        private dialog: MatDialog,
        private toastr: ToastrService
    ) {
        this.createAccess = loginService.hasPermission(NETWORKS.IP_CREATE);
        this.deleteAccess = loginService.hasPermission(NETWORKS.IP_DELETE);
        this.editAccess = loginService.hasPermission(NETWORKS.IP_EDIT);
        this.loginService = loginService;
        this.isIpEdit = !this.createAccess && this.editAccess ? true : false;
    }

    ngOnInit(): void {
        this.ipForm = this.fb.group({
            broadcastIp: ["", Validators.required],
            defaultPoolFlag: ["", Validators.required],
            displayName: ["", Validators.required],
            remark: ["", [Validators.required, Validators.pattern(this.charecter150)]],
            firstHost: ["", Validators.required],
            ipRange: ["", [
                Validators.required,
                Validators.pattern(/^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\/(3[0-2]|[12]?[0-9])$/)
            ]],
            isStaticIpPool: ["", Validators.required],
            lastHost: ["", Validators.required],
            netMask: ["", Validators.required],
            networkIp: ["", Validators.required],
            poolCategory: ["", Validators.required],
            poolName: ["", Validators.required],
            poolType: ["", Validators.required],
            status: ["", Validators.required],
            totalHost: ["", Validators.required]
        });

        this.getIpDataList("");
    }
    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    createIpView() {
        this.listView = false;
        this.createView = true;
        this.detailView = false;
        this.submitted = false;
        this.isIpEdit = false;
        this.ipForm.controls.poolName.enable();
        this.ipForm.reset();
    }

    listIpView() {
        this.listView = true;
        this.createView = false;
        this.detailView = false;
    }

    getIpDataList(list) {
        let size;
        let page = this.currentPageIpListdata;
        if (list) {
            size = list;
            this.ipListdataitemsPerPage = list;
        } else {
            size = this.ipListdataitemsPerPage;
        }

        let ipdata = {
            page: page,
            pageSize: size
        };
        const url = "/ippool";
        this.ipManagementService.postMethod(url, ipdata).subscribe(
            (response: any) => {
                this.ipListData = response.dataList;
                this.dataSource = new MatTableDataSource<any>(this.ipListData);
                this.ipListdatatotalRecords = response.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    async IpAllDetails(id) {
        await this.getIpbyId(id);
        this.detailView = true;
        this.listView = false;
        this.createView = false;
        this.dialogRef2 = this.dialog.open(this.viewIpDetailDialog, {
            width: '1200px',
            data: {
                width: '1500px',
                maxWidth: '90vw',
                height: 'auto',
                autoFocus: false,
                disableClose: true
            }
        });
        this.dialogRef2.afterClosed().subscribe(result => {
            this.dialogRef2.close();
        });
    }

    async editIp(id) {
        this.listView = false;
        this.createView = true;
        this.detailView = false;
        this.ipForm.reset();
        this.isIpEdit = true;
        this.createUpdateName = 'Update';
        await this.getIpbyId(id);
        this.ipForm.controls.poolName.disable();
        this.ipForm.patchValue(this.viewIpData);

        this.dialogRef = this.dialog.open(this.addEditIPDialog, {
            width: '1200px',
            data: {
                width: '1500px',
                maxWidth: '90vw',
                height: 'auto',
                autoFocus: false,
                disableClose: true
            }
        });
        this.dialogRef.afterClosed().subscribe(result => {
            this.dialogRef.close();
        });


    }
    async getIpbyId(id) {
        const url = "/ippool/" + id;
        await this.ipManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.viewIpData = response.data;
                this.ipForm.patchValue(this.viewIpData);
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
            }
        );
    }

    deleteConfirmonIp(ip: any) {
        if (ip.poolId) {
            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: '400px',
                data: {
                    title: `Delete ${this.title}`,
                    description: `Are you sure you want to delete "${ip.networkIp}"?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.getIpbyId(ip.poolId);
                    this.deleteIp(ip.poolId);
                } else {
                }
            });

            // this.confirmationService.confirm({
            //     message: "Do you want to delete this Ip?",
            //     header: "Delete Confirmation",
            //     icon: "pi pi-info-circle",
            //     accept: () => {
            //         this.getIpbyId(id);
            //         this.deleteIp(id);
            //     },
            //     reject: () => {
            //         this.messageService.add({
            //             severity: "info",
            //             summary: "Rejected",
            //             detail: "You have rejected"
            //         });
            //     }
            // });
        }
    }

    deleteIp(id) {
        const url = "/ippool/delete";
        this.viewIpData.poolId = id;
        this.ipManagementService.postMethod(url, this.viewIpData).subscribe(
            (response: any) => {
                if (response.responseCode == 406) {
                    this.toastr.error(`${response.responseMessage}`, 'Error!');
                } else {
                    this.commondropdownService.clearCacheCMS("/ippool/all");
                    this.getIpDataList("");
                    this.toastr.success(`${response.responseMessage}`, 'Success!');
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Error!');
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

    pageChangedIpList(pageNumber: any) {
        this.currentPageIpListdata = pageNumber;
        this.getIpDataList("");
    }

    addEditIp(id) {
        this.submitted = true;
        if (this.ipForm.valid) {
            if (id) {
                this.createIpData = this.ipForm.getRawValue();
                this.createIpData.poolId = id;
                const url = "/ippool/updateIPPool";
                this.ipManagementService.postMethod(url, this.createIpData).subscribe(
                    (response: any) => {
                        this.ipForm.reset();
                        this.dialogRef.close();
                        if (response.responseCode == 200) {
                            this.ipForm.reset();
                            this.toastr.success(`${response.responseMessage}`, 'Success!');
                            this.submitted = false;
                            this.ipForm.reset();
                            this.listView = true;
                            this.createView = false;
                            this.isIpEdit = false;
                            this.commondropdownService.clearCacheCMS("/ippool/all");
                            this.getIpDataList("");
                        } else {
                            this.toastr.error(`${response.responseMessage}`, 'Error!');
                        }
                    },
                    (error: any) => {
                        // console.log(error, "error")
                        this.toastr.error(`${error.error.ERROR}`, 'Error!');
                    }
                );
            } else {
                this.createIpData = this.ipForm.value;
                const url = "/ippool/saveIPPool";
                this.ipManagementService.postMethod(url, this.createIpData).subscribe(
                    (response: any) => {
                        this.ipForm.reset();
                        this.dialogRef.close();
                        if (response.responseCode == 200) {
                            this.ipForm.reset();
                            this.toastr.success(`${response.responseMessage}`, 'Success!');
                            this.submitted = false;
                            this.listView = true;
                            this.createView = false;
                            this.commondropdownService.clearCacheCMS("/ippool/all");
                            this.getIpDataList("");
                        } else {
                            this.toastr.error(`${response.responseMessage}`, 'Error!');
                        }
                    },
                    (error: any) => {
                        // console.log(error, "error")
                        this.toastr.error(`${error.error.ERROR}`, 'Error!');
                    }
                );
            }
        } else {
            this.ipForm.markAllAsTouched();
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

    addEditIpDialogOpen() {
        this.isIpEdit = false;
        this.submitted = false;
        this.createView = true;
        this.createUpdateName = "Create";
        this.ipForm.reset();
        this.dialogRef = this.dialog.open(this.addEditIPDialog, {
            width: '1200px',
            data: {
                width: '1500px',
                maxWidth: '90vw',
                height: 'auto',
                autoFocus: false,
                disableClose: true
            }
        });
        this.dialogRef.afterClosed().subscribe(result => {
            this.dialogRef.close();
        });
    }

    closeDialog() {
        if (this.dialogRef) {
            this.dialogRef.close();
        }
        if (this.dialogRef2) {
            this.dialogRef2.close();
        }
    }
}
