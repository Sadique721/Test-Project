import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { RadiusUtility } from "src/app/RadiusUtils/RadiusUtility";
import { IProxy } from "src/app/components/model/proxy-server";
import { ProxyServerService } from "src/app/service/proxy-server.service";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { Observable, Observer } from "rxjs";
import { RADIUS_CONSTANTS } from "src/app/constants/aclConstants";
import { MatTableDataSource } from "@angular/material/table";
import { MatSort } from "@angular/material/sort";
import { MatPaginator } from "@angular/material/paginator";
import { MatSlideToggleChange } from "@angular/material/slide-toggle";
import { _closeDialogVia, MatDialog, MatDialogRef } from "@angular/material/dialog";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { ToastrService } from "ngx-toastr";
@Component({
    selector: "app-proxy-server",
    templateUrl: "./proxy-server.component.html",
    styleUrls: ["./proxy-server.component.css"],
    standalone: false
})
export class ProxyServerComponent implements OnInit {
    proxyServers: any[] = [];
    proxyDetail: IProxy;
    //Used and required for pagination
    totalRecords: number;
    currentPage: number = 1;
    itemsPerPage: number = RadiusConstants.ITEMS_PER_PAGE;
    name: String;
    detailGroupForm: UntypedFormGroup;
    searchForm: UntypedFormGroup;
    editId: any;
    submitted: boolean = false;
    searchSubmitted = false;
    status = [{ label: "Active" }, { label: "Inactive" }];
    override = [
        { label: "True", value: true },
        { label: "False", value: false }
    ];
    mvnoData: any;
    loggedInUser: any;
    myModal: boolean = false;
    mvnoId: any;
    modalToggle: boolean = true;
    editMode: boolean = false;
    accessData: any = JSON.parse(localStorage.getItem("accessData"));

    pageLimitOptions = RadiusConstants.pageLimitOptions;
    searchkey: string;
    showItemPerPage: any;

    createProxyFlag = false;
    proxyGridFlag = true;
    _passwordNewType = "password";

    showNewPassword = false;

    AclClassConstants;
    AclConstants;
    public loginService: LoginService;
    createAccess: any;
    editAccess: any;
    deleteAccess: any;
    userId: string;
    superAdminId: string = RadiusConstants.SUPERADMINID;
    //   ************************************
    title = 'Proxy Configuration';
    dataSource = new MatTableDataSource<any>([]);
    @ViewChild(MatSort) sort: MatSort = Object.create(null);
    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);
    @ViewChild('addEditProxyConfigDialog') addEditProxyConfigDialog!: TemplateRef<any>;
    @ViewChild('viewProxyServerDialog') viewProxyServerDialog!: TemplateRef<any>;
    dialogRef!: MatDialogRef<any>;


    constructor(
        private proxyServerService: ProxyServerService,
        private radiusUtility: RadiusUtility,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        loginService: LoginService,
        // 
        private dialog: MatDialog,
        private toastr: ToastrService
    ) {
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;

        this.createAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_PROXY_CONFIG_CREATE);
        this.deleteAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_PROXY_CONFIG_DELETE);
        this.editAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_PROXY_CONFIG_EDIT);
    }

    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_PROXY_CONFIG_EDIT) || this.loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_PROXY_CONFIG_DELETE)) {
            return ['id', 'name', 'ip', 'acc-p', 'auth-p', 'status', 'c-on', 'action'];
        } else {
            return ['id', 'name', 'ip', 'acc-p', 'auth-p', 'status', 'c-on'];
        }
    }
    ngOnInit(): void {
        this.getAll("");
        this.detailGroupForm = this.fb.group({
            name: ["", Validators.required],
            ip: ["", Validators.required],
            // ip: [
            //     "",
            //     [
            //         Validators.required,
            //         Validators.pattern(
            //             "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)"
            //         )
            //     ]
            // ],
            secretkey: ["", Validators.required],
            acctport: ["", [Validators.required, Validators.pattern("^[0-9]*$")]],
            authport: ["", [Validators.required, Validators.pattern("^[0-9]*$")]],
            status: ["", Validators.required],
            mvnoName: [""],
            createdBy: [""],
            lastModifiedBy: [""],
            dynaAuthPort: ["", [Validators.pattern("^[0-9]*$")]],
            overrideNAS: [false],
            nasip: [""],
            timeout: ["", Validators.required]
        });
        this.searchForm = this.fb.group({
            name: ["", Validators.required]
        });

        this.mvnoData = JSON.parse(localStorage.getItem("mvnoData"));
        this.loggedInUser = localStorage.getItem("loggedInUser");
        this.mvnoId = localStorage.getItem("mvnoId");

        this.createProxyFlag = false;
        this.proxyGridFlag = true;
        this.userId = localStorage.getItem("userId");
        this.superAdminId = RadiusConstants.SUPERADMINID;


        this.detailGroupForm.get('overrideNAS').valueChanges.subscribe(value => {
            if (value === false) {
                this.detailGroupForm.get('nasip').disable();
            } else {
                this.detailGroupForm.get('nasip').enable();
            }
        });

        if (this.detailGroupForm.get('overrideNAS').value === false) {
            this.detailGroupForm.get('nasip').disable();
        }
    }

    createnewProxy() {
        if (this.accessData.proxy.createUpdateAccess) {
            // this.createProxyFlag = true;
            this.proxyGridFlag = false;
            this.editMode = false;
            this.searchName = "";

            // Reset form to blank values explicitly
            this.detailGroupForm.reset({
                name: '',
                ip: '',
                secretkey: '',
                acctport: '',
                authport: '',
                status: '',
                mvnoName: '',
                createdBy: '',
                lastModifiedBy: '',
                dynaAuthPort: '',
                overrideNAS: false,
                nasip: '',
                timeout: ''
            });
            this.dialogRef = this.dialog.open(this.addEditProxyConfigDialog, {
                width: '1200px',
                maxWidth: '90vw',
                height: 'auto',
                autoFocus: false,
                disableClose: true
            });

            this.dialogRef.afterClosed().subscribe(() => {
                this.dialogRef.close();
                this.detailGroupForm.reset();
            });

            this.clearFormData();

        }


    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    ProxyListData() {
        this.createProxyFlag = false;
        this.proxyGridFlag = true;
        this.editMode = false;
        this.currentPage = 1;
        this.getAll("");
        this.searchName = "";
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

    async getAll(list) {
        let size;
        this.searchkey = "";
        let page = this.currentPage;
        if (list) {
            size = list;
            this.itemsPerPage = list;
        } else {
            size = this.itemsPerPage;
        }
        this.proxyServerService.getAll(page, size, "").subscribe(
            (response: any) => {
                this.proxyServers = response.proxyServerList;
                this.dataSource = new MatTableDataSource<any>(this.proxyServers);
                if (this.paginator) {
                    this.dataSource.paginator = this.paginator;
                }
                if (this.sort) {
                    this.dataSource.sort = this.sort;
                }
                this.totalRecords = this.proxyServers.length;
            },
            (error: any) => {
                if (error.error.status == 404) {
                    this.totalRecords = 0;
                    this.toastr.info(`${error.error.errorMessage}`, 'Info!');
                } else {
                    this.toastr.error(`${error.error.errorMessage}`, 'Error!');
                }
                this.totalRecords = 0;
                this.proxyServers = [];
            }
        );
    }

    // async search() {
    //   this.currentPage = 1;
    //   this.searchSubmitted = true;
    //   if (this.searchForm.valid) {
    //
    //     this.proxyServerService
    //       .getByName(this.searchForm.value.name, this.mvnoId)
    //       .subscribe(
    //         (response: any) => {
    //           this.proxyServers = response;
    //           this.totalRecords = this.proxyServers.proxyServerList.length;
    //
    //         },
    //         (error: any) => {
    //           this.messageService.add({
    //             severity: 'error',
    //             summary: 'Error',
    //             detail: error.error.errorMessage,
    //             icon: 'far fa-times-circle',
    //           });
    //           this.proxyServers = [];
    //           this.totalRecords = 0;
    //
    //         }
    //       );
    //   }
    // }
    searchName = "";
    search() {
        if (!this.searchkey || this.searchkey !== this.searchName.trim()) {
            this.currentPage = 1;
        }
        this.searchSubmitted = true;
        this.createProxyFlag = false;
        this.proxyGridFlag = true;
        this.searchForm.controls.name.setValue(this.searchName);
        if (this.searchForm.valid) {
            this.proxyServers = [];
            let name = this.searchName.trim() ? this.searchName.trim() : "";

            this.searchkey = name;
            if (this.showItemPerPage) {
                this.itemsPerPage = this.showItemPerPage;
            }

            this.proxyServerService.getAll(this.currentPage, this.itemsPerPage, name).subscribe(
                (response: any) => {
                    this.proxyServers = response.proxyServerList;
                    this.dataSource = new MatTableDataSource<any>(this.proxyServers);
                    if (this.paginator) {
                        this.dataSource.paginator = this.paginator;
                    }
                    this.totalRecords = this.proxyServers.length;
                },
                (error: any) => {
                    if (error.error.status == 404) {
                        this.totalRecords = 0;
                        this.toastr.info(`${error.error.errorMessage}`, 'Info!');
                    } else {
                        this.toastr.error(`${error.error.errorMessage}`, 'Error!');
                    }
                    this.totalRecords = 0;
                    this.proxyServers = [];
                }
            );
        }
    }
    clearSearchForm() {
        this.clearFormData();
        this.searchSubmitted = false;
        this.searchForm.reset();
        this.currentPage = 1;
        this.searchName = "";
        this.getAll("");
        this.createProxyFlag = false;
        this.proxyGridFlag = true;
    }

    deleteConfirm(serverId, selectedMvnoId, index, name) {
        if (this.validateUserToPerformOperations(selectedMvnoId)) {
            // this.confirmationService.confirm({
            //     message: "Do you want to delete this record?",
            //     header: "Delete Confirmation",
            //     icon: "pi pi-info-circle",
            //     accept: () => {
            //         this.delete(serverId, selectedMvnoId, index);
            //     },
            //     reject: () => {
            //         this.messageService.add({
            //             severity: "info",
            //             summary: "Rejected",
            //             detail: "You have rejected"
            //         });
            //     }
            // });

            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: '400px',
                data: {
                    title: `Delete ${this.title}`,
                    description: `Are you sure you want to delete "${name}"?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.delete(serverId, selectedMvnoId, index);
                } else {
                }
            });
        }
    }

    async delete(serverId, selectedMvnoId, index) {
        this.proxyServerService.delete(serverId, selectedMvnoId).subscribe(
            (response: any) => {
                if (this.currentPage != 1 && index == 0 && this.proxyServers.length == 1) {
                    this.currentPage = this.currentPage - 1;
                }
                if (!this.searchkey) {
                    this.getAll("");
                } else {
                    this.search();
                }
                this.toastr.success(`${response.message}`, 'Success!');
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Error!');
            }
        );
    }

    edit(configId, index) {
        // index = this.radiusUtility.getIndexOfSelectedRecord(
        //   index,
        //   this.currentPage,
        //   this.itemsPerPage
        // );
        if (this.validateUserToPerformOperations(this.proxyServers[index].mvnoId)) {
            this.editId = configId;

            this.overrideNASEvent(this.proxyServers[index].nasip);
            this.createProxyFlag = true;
            this.proxyGridFlag = false;

            this.dialogRef = this.dialog.open(this.addEditProxyConfigDialog, {
                width: '1200px',
                maxWidth: '90vw',
                height: 'auto',
                autoFocus: false,
                disableClose: true
            });

            this.dialogRef.afterClosed().subscribe(() => {
                this.dialogRef.close();
                this.detailGroupForm.reset();
            });

            this.detailGroupForm.patchValue({
                name: this.proxyServers[index].name,
                ip: this.proxyServers[index].ip,
                secretkey: this.proxyServers[index].secretkey,
                acctport: this.proxyServers[index].acctport,
                authport: this.proxyServers[index].authport,
                status: this.proxyServers[index].status,
                mvnoName: this.proxyServers[index].mvnoId,
                createdBy: this.proxyServers[index].createdBy,
                lastModifiedBy: this.proxyServers[index].lastModifiedBy,
                dynaAuthPort: this.proxyServers[index].dynaAuthPort,
                overrideNAS: this.proxyServers[index].overrideNAS,
                nasip: this.proxyServers[index].nasip,
                timeout: this.proxyServers[index].timeout
            });
        }
    }

    addOrUpdate() {
        this.submitted = true;
        this.userId = localStorage.getItem("userId");
        // if (this.userId == RadiusConstants.SUPERADMINID) {
        //     this.detailGroupForm.get("mvnoName").setValidators([Validators.required]);
        //     this.detailGroupForm.get("mvnoName").updateValueAndValidity();
        // }
        if (this.detailGroupForm.invalid) {
            this.detailGroupForm.markAllAsTouched();
            return;
        }
        const data = {
            name: this.detailGroupForm.value.name,
            ip: this.detailGroupForm.value.ip,
            secretkey: this.detailGroupForm.value.secretkey,
            acctport: this.detailGroupForm.value.acctport,
            authport: this.detailGroupForm.value.authport,
            status: this.detailGroupForm.value.status,
            mvnoId: this.detailGroupForm.value.mvnoName,
            createdBy: "",
            lastModifiedBy: "",
            dynaAuthPort: this.detailGroupForm.value.dynaAuthPort,
            overrideNAS: this.detailGroupForm.value.overrideNAS,
            nasip: this.detailGroupForm.value.nasip,
            timeout: this.detailGroupForm.value.timeout
        };

        if (this.editId) {
            data.createdBy = this.detailGroupForm.value.createdBy;
            data.lastModifiedBy = this.loggedInUser;
            this.update(data);
        } else {
            data.createdBy = this.loggedInUser;
            data.lastModifiedBy = "";
            this.add(data);
        }

        this.detailGroupForm.get("mvnoName").clearValidators();
        this.detailGroupForm.get("mvnoName").updateValueAndValidity();
    }

    get f() {
        return this.detailGroupForm.controls;
    }

    private async add(data) {
        this.proxyServerService.add(data).subscribe(
            (response: any) => {
                this.closeDialog();
                this.getAll("");
                this.createProxyFlag = false;
                this.proxyGridFlag = true;
                this.toastr.success(`${response.message}`, 'Success!');
                this.clearFormData();
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Error!');
            }
        );
    }

    private async update(data: any) {
        this.proxyServerService.update(this.editId, data).subscribe(
            (response: any) => {
                this.closeDialog();
                if (!this.searchkey) {
                    this.getAll("");
                } else {
                    this.search();
                }

                this.createProxyFlag = false;
                this.proxyGridFlag = true;
                this.toastr.success(`${response.message}`, 'Success!');
                this.clearFormData();
            },
            (error: any) => {
                this.toastr.success(`${error.error.errorMessage}`, 'Success!');
            }
        );
    }

    clearFormData() {
        this.editId = null;
        this.submitted = false;

        this.detailGroupForm.reset({
            name: '',
            ip: '',
            secretkey: '',
            acctport: '',
            authport: '',
            status: '',
            mvnoName: '',
            createdBy: '',
            lastModifiedBy: '',
            dynaAuthPort: '',
            overrideNAS: false,
            nasip: '',
            timeout: ''
        });

        this._passwordNewType = 'password';
    }

    // async changeStatus(configId, status, selectedMvnoId, event) {
    //     console.log("hello", configId, status, selectedMvnoId, event)
    //     event.preventDefault();
    //     this.modalToggle = true;
    //     if (this.validateUserToPerformOperations(selectedMvnoId)) {
    //         this.proxyServerService
    //             .changeSatus(
    //                 configId,
    //                 status == "Active" ? RadiusConstants.IN_ACTIVE : RadiusConstants.ACTIVE,
    //                 selectedMvnoId
    //             )
    //             .subscribe(
    //                 (response: any) => {
    //                     this.messageService.add({
    //                         severity: "success",
    //                         summary: "Successfully",
    //                         detail: response.message,
    //                         icon: "far fa-check-circle"
    //                     });
    //                     if (!this.searchkey) {
    //                         this.getAll("");
    //                     } else {
    //                         this.search();
    //                     }
    //                 },
    //                 (error: any) => {
    //                     this.messageService.add({
    //                         severity: "error",
    //                         summary: "Error",
    //                         detail: error.error.errorMessage,
    //                         icon: "far fa-times-circle"
    //                     });
    //                 }
    //             );
    //     }
    // }
    async changeStatus(configId: number, status: string, selectedMvnoId: number, event: MatSlideToggleChange) {

        this.modalToggle = true;

        if (this.validateUserToPerformOperations(selectedMvnoId)) {
            const newStatus = event.checked ? RadiusConstants.ACTIVE : RadiusConstants.IN_ACTIVE;

            this.proxyServerService.changeSatus(configId, newStatus, selectedMvnoId)
                .subscribe(
                    (response: any) => {
                        this.toastr.success(`${response.message}`, 'Success!');
                        if (!this.searchkey) {
                            this.getAll("");
                        } else {
                            this.search();
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.errorMessage}`, 'Error!');
                    }
                );
        }
    }
    pageChanged(pageNumber) {
        this.clearFormData();
        this.currentPage = pageNumber;
        if (!this.searchkey) {
            this.getAll("");
        } else {
            this.search();
        }
    }
    OneClientData: any = [];
    config = {
        name: "",
        ip: "",
        authport: "",
        acctport: "",
        secretKey: "",
        status: "",
        dynaAuthPort: "",
        overrideNAS: "",
        nasip: "",
        timeout: ""
    };
    showConfigDetail(clientId, mvnoId) {
        this.myModal = true;
        this.modalToggle = true;

        this.proxyServerService.getById(clientId, mvnoId).subscribe(
            (response: any) => {
                this.OneClientData = response;
                this.config = this.OneClientData.proxyServer;

                this.dialogRef = this.dialog.open(this.viewProxyServerDialog, {
                    width: '1200px',
                    maxWidth: '90vw',
                    height: 'auto',
                    autoFocus: false,
                    disableClose: true
                });

                this.dialogRef.afterClosed().subscribe(() => {
                    this.dialogRef.close();
                    this.detailGroupForm.reset();
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Error!');
            }
        );
    }



    validateUserToPerformOperations(selectedMvnoId) {
        let loggedInUserMvnoId = localStorage.getItem("mvnoId");
        this.userId = localStorage.getItem("userId");
        if (this.userId != RadiusConstants.SUPERADMINID && selectedMvnoId != loggedInUserMvnoId) {
            //  this.reset();
            this.toastr.info(`You are not authorized to do this operation. Please contact to the administrator`, 'Info!');
            //   this.modalToggle = false;
            return false;
        }
        return true;
    }

    overrideNASEvent(ip) {
        if (ip) {
            this.detailGroupForm.controls.nasip.enable();
            this.detailGroupForm.controls.nasip.setValue("");
            this.detailGroupForm.controls.nasip.setValidators(Validators.required);
            this.detailGroupForm.controls.nasip.updateValueAndValidity();
        } else {
            this.detailGroupForm.controls.nasip.disable();
            this.detailGroupForm.controls.nasip.setValue("");
            this.detailGroupForm.controls.nasip.clearValidators();
            this.detailGroupForm.controls.nasip.updateValueAndValidity();
        }
    }

    canExit() {
        if (!this.detailGroupForm.dirty && !this.searchForm.dirty) return true;
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

    closeDialog() {
        this.dialogRef.close();
    }
    toggleNewPassword() {
        this._passwordNewType = this._passwordNewType === 'password' ? 'text' : 'password';
    }
}
