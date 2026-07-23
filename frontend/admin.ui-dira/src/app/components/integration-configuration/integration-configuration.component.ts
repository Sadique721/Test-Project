// import { Component, OnInit } from "@angular/core";
// import { FormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
// import { NgxSpinnerService } from "ngx-spinner";
// import { ConfirmationService, MessageService } from "primeng/api";
// import { AclClassConstants } from "src/app/constants/aclClassConstants";
// import { AclConstants } from "src/app/constants/aclOperationConstants";
// import { LoginService } from "src/app/service/login.service";
// import { Observable, Observer } from "rxjs";
// import { CommondropdownService } from "src/app/service/commondropdown.service";
// import { INTEGRATION_SYSTEMS, SETTINGS } from "src/app/constants/aclConstants";
// import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
// import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
// import { IntegrationConfigurationService } from "src/app/service/integration-configuration.service";

// declare var $: any;
// @Component({
//     selector: "app-integration-configuration",
//     templateUrl: "./integration-configuration.component.html",
//     styleUrls: ["./integration-configuration.component.scss"],
//     standalone: false
// })
// export class IntegrationConfigurationComponent implements OnInit {
//   integrationConfigFormGroup: UntypedFormGroup;
//   submitted = false;
//   isDropdownClick = false;
//   AclClassConstants: any;
//   AclConstants: any;
//   editMode: boolean = false;
//   status = [
//     { label: "Active", value: "ACTIVE" },
//     { label: "Inactive", value: "INACTIVE" },
//   ];
//   detailView: boolean = false;
//   editAccess: boolean = false;
//   createAccess: boolean = false;
//   deleteAccess: boolean = false;
//   paymentgatewayList: any[] = [];
//   labelMap: any = {};
//   parameterForMap: any = {};

//   integrationConfigurationList: any[] = [];
//   integrationConfigItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
//   integrationConfigCurrentPage = 1;
//   integrationConfigTotalRecords: any;
//   pageLimitOptions = RadiusConstants.pageLimitOptions;

//   constructor(
//     private fb: UntypedFormBuilder,
//     private spinner: NgxSpinnerService,
//     private confirmationService: ConfirmationService,
//     private messageService: MessageService,
//     public loginService: LoginService,
//     public commondropdownService: CommondropdownService,
//     public savbillCommonBaseService: SavbillCommonBaseService,
//     public integrationConfigService: IntegrationConfigurationService
//   ) {
//     this.createAccess = loginService.hasPermission(INTEGRATION_SYSTEMS.INTEGRATION_CONFIG_CREATE);
//     this.deleteAccess = loginService.hasPermission(INTEGRATION_SYSTEMS.INTEGRATION_CONFIG_DELETE);
//     this.editAccess = loginService.hasPermission(INTEGRATION_SYSTEMS.INTEGRATION_CONFIG_EDIT);
//     this.loginService = loginService;
//     this.AclClassConstants = AclClassConstants;
//     this.AclConstants = AclConstants;
//   }

//   ngOnInit(): void {
//     this.integrationConfigFormGroup = this.fb.group({
//       name: ["", Validators.required],
//       baseurl: ["", Validators.required],
//       port: ["", Validators.required],
//       username: [""],
//       password: [""],
//       id: [""],
//     });
//     this.getAllIntegrationConfiguration();
//   }

//   getAllIntegrationConfiguration() {
//     var pageRequest = {
//       page: this.integrationConfigCurrentPage,
//       pageSize: this.integrationConfigItemsPerPage,
//     };

//     this.integrationConfigService.getAllIntegrationConfiguration(pageRequest).subscribe(
//       (response: any) => {
//         if (response.configlist) {
//           this.integrationConfigurationList = response.configlist.content;
//         }
//         this.integrationConfigTotalRecords = response.pageDetails.totalRecords;
//       },
//       (error: any) => {
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.error.ERROR,
//           icon: "far fa-times-circle",
//         });
//       }
//     );
//   }

//   addUpdateIntegrationConfig() {
//     this.submitted = true;
//     if (this.integrationConfigFormGroup.valid) {
//       if (this.editMode) {
//         this.integrationConfigService
//           .updateIntegrationConfiguration(this.integrationConfigFormGroup.value)
//           .subscribe(
//             (response: any) => {
//               this.submitted = false;
//               this.editMode = false;
//               this.resetConfigForm();
//               this.getAllIntegrationConfiguration();
//               this.messageService.add({
//                 severity: "success",
//                 summary: "Successfully",
//                 detail: response.responseMessage,
//                 icon: "far fa-check-circle",
//               });
//             },
//             (error: any) => {
//               if (error.status == 400) {
//                 this.messageService.add({
//                   severity: "info",
//                   summary: "Info",
//                   detail: error.error.ERROR,
//                   icon: "far fa-times-circle",
//                 });
//               } else {
//                 this.messageService.add({
//                   severity: "error",
//                   summary: "Error",
//                   detail: error.error.ERROR,
//                   icon: "far fa-times-circle",
//                 });
//               }
//             }
//           );
//       } else {
//         this.integrationConfigService
//           .addIntegrationConfiguration(this.integrationConfigFormGroup.value)
//           .subscribe(
//             (response: any) => {
//               this.submitted = false;
//               this.resetConfigForm();
//               this.getAllIntegrationConfiguration();
//               this.messageService.add({
//                 severity: "success",
//                 summary: "Successfully",
//                 detail: response.message,
//                 icon: "far fa-check-circle",
//               });

//               this.isDropdownClick = false;
//             },
//             (error: any) => {
//               if (error.status == 400) {
//                 this.messageService.add({
//                   severity: "info",
//                   summary: "Info",
//                   detail: error.error.ERROR,
//                   icon: "far fa-times-circle",
//                 });
//               } else {
//                 this.messageService.add({
//                   severity: "error",
//                   summary: "Error",
//                   detail: error.error.ERROR,
//                   icon: "far fa-times-circle",
//                 });
//               }
//               this.isDropdownClick = false;
//             }
//           );
//       }
//     }
//   }

//   resetConfigForm() {
//     this.integrationConfigFormGroup.reset();
//   }

//   editConfigById(configId) {
//     this.editMode = true;
//     this.integrationConfigService.getIntegrationConfigurationById(configId).subscribe(
//       (response: any) => {
//         this.integrationConfigFormGroup.patchValue(response.data);
//       },
//       (error: any) => {
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.error.ERROR,
//           icon: "far fa-times-circle",
//         });
//       }
//     );
//   }

//   canExit() {
//     if (!this.integrationConfigFormGroup.dirty) return true;
//     {
//       return Observable.create((observer: Observer<boolean>) => {
//         this.confirmationService.confirm({
//           header: "Alert",
//           message: "The filled data will be lost. Do you want to continue? (Yes/No)",
//           icon: "pi pi-info-circle",
//           accept: () => {
//             observer.next(true);
//             observer.complete();
//           },
//           reject: () => {
//             observer.next(false);
//             observer.complete();
//           },
//         });
//         return false;
//       });
//     }
//   }

//   deleteIntegrationConfirmation(configId) {
//     if (configId) {
//       this.confirmationService.confirm({
//         message: "Do you want to delete this integration configuration ?",
//         header: "Delete Confirmation",
//         icon: "pi pi-info-circle",
//         accept: () => {
//           this.deleteIntegrationConfig(configId);
//         },
//         reject: () => {
//           this.messageService.add({
//             severity: "info",
//             summary: "Rejected",
//             detail: "You have rejected",
//           });
//         },
//       });
//     }
//   }

//   deleteIntegrationConfig(configId) {
//     this.integrationConfigService.deleteIntegrationConfiguration(configId).subscribe(
//       (response: any) => {
//         this.messageService.add({
//           severity: "success",
//           summary: "Successfully",
//           detail: response.responseMessage,
//           icon: "far fa-check-circle",
//         });
//         this.getAllIntegrationConfiguration();
//         this.resetConfigForm();
//         this.editMode = false;
//         this.isDropdownClick = false;
//       },
//       (error: any) => {
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.error.ERROR,
//           icon: "far fa-times-circle",
//         });
//       }
//     );
//   }

//   pageChanged(pageNumber) {
//     this.integrationConfigCurrentPage = pageNumber;
//     this.getAllIntegrationConfiguration();
//   }

//   TotalItemPerPage(event: any) {
//     this.integrationConfigItemsPerPage = Number(event.value);
//     this.getAllIntegrationConfiguration();
//   }
//   resetForm() {
//     this.integrationConfigFormGroup.reset();
//     this.resetConfigForm();
//     this.editMode = false;
//     this.isDropdownClick = false;
//   }

//   keypressId(event: any) {
//     const pattern = /^[0-9]+$/;
//     let inputChar = String.fromCharCode(event.charCode);
//     if (event.keyCode != 8 && event.keyCode != 9 && !pattern.test(inputChar)) {
//       event.preventDefault();
//     }
//   }
// }
import { Component, OnInit, ViewChild, AfterViewInit } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import { LoginService } from "src/app/service/login.service";
import { Observable, Observer } from "rxjs";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { IntegrationConfigurationService } from "src/app/service/integration-configuration.service";
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatDialog } from '@angular/material/dialog';
import { AddEditIntegrationConfigurationComponent } from './add-edit-integration-configuration/add-edit-integration-configuration.component';
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { INTEGRATION_SYSTEMS } from "src/app/constants/aclConstants";
import { ToastrService } from "ngx-toastr";

@Component({
    selector: "app-integration-configuration",
    templateUrl: "./integration-configuration.component.html",
    styleUrls: ["./integration-configuration.component.css"],
    standalone: false
})
export class IntegrationConfigurationComponent implements OnInit, AfterViewInit {
    integrationConfigFormGroup: UntypedFormGroup;
    submitted = false;
    editMode = false;
    createAccess = false;
    editAccess = false;
    deleteAccess = false;
    dataSource = new MatTableDataSource<any>([]);
    integrationConfigurationList: any[] = [];

    integrationConfigCurrentPage = 1;
    integrationConfigItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    integrationConfigTotalRecords = 0;
    pageLimitOptions = RadiusConstants.pageLimitOptions;

    displayedColumns: string[] = ['id', 'name', 'baseurl', 'action'];

    AclClassConstants = AclClassConstants;
    AclConstants = AclConstants;

    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;

    searchIntegrationConfigName = '';
    searchkey = '';

    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService, private toastr: ToastrService,
        public loginService: LoginService,
        public commondropdownService: CommondropdownService,
        public integrationConfigService: IntegrationConfigurationService,
        private dialog: MatDialog
    ) {
        this.createAccess = loginService.hasPermission(INTEGRATION_SYSTEMS.INTEGRATION_CONFIG_CREATE);
        this.deleteAccess = loginService.hasPermission(INTEGRATION_SYSTEMS.INTEGRATION_CONFIG_DELETE);
        this.editAccess = loginService.hasPermission(INTEGRATION_SYSTEMS.INTEGRATION_CONFIG_EDIT);
    }

    ngOnInit(): void {
        this.integrationConfigFormGroup = this.fb.group({
            name: ['', Validators.required],
            baseurl: ['', Validators.required],
            port: ['', Validators.required],
            username: [''],
            password: [''],
            id: ['']
        });
        this.getAllIntegrationConfiguration();
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(INTEGRATION_SYSTEMS.INTEGRATION_CONFIG_DELETE) || this.loginService.hasPermission(INTEGRATION_SYSTEMS.INTEGRATION_CONFIG_EDIT)) {
            return ['id', 'name', 'baseurl', 'action'];
        } else {
            return ['id', 'name', 'baseurl'];
        }
    }


    getAllIntegrationConfiguration(): void {
        const pageRequest = {
            page: this.integrationConfigCurrentPage,
            pageSize: this.integrationConfigItemsPerPage
        };

        this.integrationConfigService.getAllIntegrationConfiguration(pageRequest).subscribe(
            (response: any) => {
                this.integrationConfigurationList = response.configlist?.content || [];
                this.integrationConfigTotalRecords = response.pageDetails?.totalRecords || 0;
                this.dataSource.data = [...this.integrationConfigurationList];
                setTimeout(() => {
                    if (this.paginator) {
                        this.paginator.length = this.integrationConfigTotalRecords;
                        this.paginator.pageIndex = this.integrationConfigCurrentPage - 1;
                    }
                });
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    pageChanged(event: PageEvent): void {
        this.integrationConfigCurrentPage = event.pageIndex + 1;
        this.integrationConfigItemsPerPage = event.pageSize;

        if (this.searchkey) {
            this.searchIntegrationConfig();
        } else {
            this.getAllIntegrationConfiguration();
        }
    }

    addUpdateIntegrationConfig(): void {
        this.submitted = true;

        if (this.integrationConfigFormGroup.invalid) {
            return;
        }

        if (this.editMode) {
            this.integrationConfigService.updateIntegrationConfiguration(this.integrationConfigFormGroup.value).subscribe(
                (response: any) => {
                    this.toastr.success(`${response.responseMessage}`, "Successfully Updated");
                    this.afterSaveSuccess();
                },
                error => this.handleErrorResponse(error)
            );
        } else {
            this.integrationConfigService.addIntegrationConfiguration(this.integrationConfigFormGroup.value).subscribe(
                (response: any) => {
                    this.toastr.success(`${response.responseMessage}`, "Successfully Added");
                    this.afterSaveSuccess();
                },
                error => this.handleErrorResponse(error)
            );
        }
    }

    private afterSaveSuccess(): void {
        this.submitted = false;
        this.editMode = false;
        this.resetForm();
        this.getAllIntegrationConfiguration();
    }

    editConfigById(configId: any): void {
        if (configId) {
            this.integrationConfigService.getIntegrationConfigurationById(configId).subscribe(
                (response: any) => {
                    this.editMode = true;
                    const configData = response.data;
                    const dialogRef = this.dialog.open(AddEditIntegrationConfigurationComponent, {
                        width: '800px',
                        data: {
                            isEdit: true,
                            title: 'Update Integration Config',
                            yesLabel: 'Update',
                            noLabel: 'Cancel',
                            createAcS: this.createAccess,
                            editAcs: this.editAccess,
                            configData: configData,
                            inputName: 'Enter Integration Config Name',
                            inputStatus: 'Select Status'
                        }
                    });

                    dialogRef.afterClosed().subscribe(result => {
                        if (result) {
                            this.integrationConfigFormGroup.patchValue(result);
                            this.addUpdateIntegrationConfig();
                        }
                    });
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    deleteIntegrationConfirmation(configId: any): void {
        if (!configId) return;

        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete Integration Config',
                description: `Are you sure you want to delete this integration configuration?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteIntegrationConfig(configId);
            }
        });
    }

    deleteIntegrationConfig(configId: any): void {
        this.integrationConfigService.deleteIntegrationConfiguration(configId).subscribe(
            (response: any) => {
                this.toastr.success(`${response.responseMessage}`, "Successfully Deleted");
                this.getAllIntegrationConfiguration();
                this.resetForm();
                this.editMode = false;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    resetForm(): void {
        this.integrationConfigFormGroup.reset();
        this.submitted = false;
    }

    canExit(): Observable<boolean> {
        if (!this.integrationConfigFormGroup.dirty) return new Observable<boolean>(observer => observer.next(true));
        return new Observable((observer: Observer<boolean>) => {
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
        });
    }


    searchIntegrationConfig(): void {
        if (!this.searchkey || this.searchkey !== this.searchIntegrationConfigName) {
            this.integrationConfigCurrentPage = 1;
        }
        this.searchkey = this.searchIntegrationConfigName.trim();

        const searchPayload = {
            filters: [{
                filterDataType: "",
                filterValue: this.searchkey,
                filterColumn: "any",
                filterOperator: "equalto",
                filterCondition: "and"
            }],
            page: this.integrationConfigCurrentPage,
            pageSize: this.integrationConfigItemsPerPage
        };

        this.integrationConfigService.addIntegrationConfiguration(searchPayload).subscribe(
            (response: any) => {
                this.integrationConfigurationList = response.configlist?.content || [];
                this.integrationConfigTotalRecords = response.pageDetails?.totalRecords || 0;

                this.dataSource.data = [...this.integrationConfigurationList];
                setTimeout(() => {
                    if (this.paginator) {
                        this.paginator.length = this.integrationConfigTotalRecords;
                        this.paginator.pageIndex = this.integrationConfigCurrentPage - 1;
                    }
                });
            },
            error => {
                this.integrationConfigTotalRecords = 0;
                this.dataSource.data = [];

                if (error.error.status === 404) {
                    this.toastr.info(error.error.msg || error.error.ERROR, 'Info!');
                } else {
                    this.toastr.error(error.error.msg || error.error.ERROR, 'Error!');
                }

            }
        );
    }

    clearSearchIntegrationConfig(): void {
        this.searchIntegrationConfigName = "";
        this.searchkey = "";
        this.integrationConfigCurrentPage = 1;
        this.getAllIntegrationConfiguration();
    }

    TotalItemPerPage(event: any): void {
        this.integrationConfigItemsPerPage = Number(event.value);
        this.integrationConfigCurrentPage = 1;
        if (this.searchkey) {
            this.searchIntegrationConfig();
        } else {
            this.getAllIntegrationConfiguration();
        }
    }


    addEditIntegrationConfigDialog(): void {
        const dialogRef = this.dialog.open(AddEditIntegrationConfigurationComponent, {
            width: '800px',
            data: {
                isEdit: this.editMode,
                createAcS: this.createAccess,
                editAcs: this.editAccess,
                title: this.editMode ? 'Update Integration Config' : 'Create Integration Config',
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.integrationConfigFormGroup.patchValue(result);
                this.addUpdateIntegrationConfig();
            }
        });
    }

    private handleErrorResponse(error: any): void {
        if (error.status === 400) {
            this.toastr.info(error.error.msg || error.error.ERROR, 'Info!');
        } else {
            this.toastr.error(`${error.error.ERROR}`, 'Failed!');
        }
    }
}
