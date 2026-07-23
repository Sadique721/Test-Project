import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { FormArray, UntypedFormBuilder, UntypedFormGroup, Validators, FormControl, FormGroup } from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
// Remove MessageService import since we're replacing it with toastr
import { ConfirmationService } from "primeng/api";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { LoginService } from "src/app/service/login.service";
import { ProuctManagementService } from "src/app/service/prouct-management.service";
import { ProductCategoryManagementService } from "src/app/service/product-category-management.service";
import { Observable, Observer } from "rxjs";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { SETTINGS } from "src/app/constants/aclConstants";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { PaymentGatewayConfigurationService } from "src/app/service/payment-gateway-configuration.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";

import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from "@angular/material/sort";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { count } from "console";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
// Add ToastrService import
import { ToastrService } from 'ngx-toastr';

declare var $: any;
@Component({
    selector: "app-payment-gateway-configuration",
    templateUrl: "./payment-gateway-configuration.component.html",
    styleUrls: ["./payment-gateway-configuration.component.scss"],
    standalone: false
})
export class PaymentGatewayConfigurationComponent implements OnInit {
    // Add these properties to your component class (after existing properties)
    displayedColumns: string[] = ['id', 'name', 'status', 'action'];
    dataSource = new MatTableDataSource<any>();

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;

    // Add page size options
    pageSizeOptions = [5,10,20,50,100];
    dialogRef: any;
    paymentgatewayConfigFormGroup: UntypedFormGroup;
    submitted = false;
    isDropdownClick = false;
    AclClassConstants: any;
    AclConstants: any;
    editMode: boolean = false;
    status = [
        { label: "Active", value: "ACTIVE" },
        { label: "Inactive", value: "INACTIVE" },
    ];
    detailView: boolean = false;
    editAccess: boolean = false;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    paymentgatewayList: any[] = [];
    labelMap: any = {};
    parameterForMap: any = {};

    paymentGatewayConfigurationList: any[] = [];
    paymentConfigItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    paymentConfigCurrentPage = 1;
    paymentConfigTotalRecords: any;
    pageLimitOptions = RadiusConstants.pageLimitOptions;

    constructor(
        private fb: UntypedFormBuilder,
        private dialog: MatDialog,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        // Replace MessageService with ToastrService
        private toastr: ToastrService,
        public loginService: LoginService,
        public commondropdownService: CommondropdownService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        public paymentGatewayConfigService: PaymentGatewayConfigurationService
    ) {
        this.createAccess = loginService.hasPermission(SETTINGS.PAYMENT_GATEWAY_CONFIGURATION_CREATE);
        this.deleteAccess = loginService.hasPermission(SETTINGS.PAYMENT_GATEWAY_CONFIGURATION_DELETE);
        this.editAccess = loginService.hasPermission(SETTINGS.PAYMENT_GATEWAY_CONFIGURATION_EDIT);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
    }

    ngOnInit(): void {
        this.paymentgatewayConfigFormGroup = this.fb.group({
            paymentConfigId: [""],
            paymentConfigName: ["", Validators.required],
            paymentGatewayInfo: [""],
        });
        this.dataSource = new MatTableDataSource([]);
        this.getAllPaymentGateway();
        this.getAllPaymentGatewayConfiguration();
    }
    ngAfterViewInit() {
        // this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    getAllPaymentGatewayConfiguration() {
        var pageRequest = {
            page: this.paymentConfigCurrentPage,
            pageSize: this.paymentConfigItemsPerPage,
        };
        this.paymentGatewayConfigService.getAlPaymentGatewayConfiguration(pageRequest).subscribe(
            (response: any) => {
                this.paymentGatewayConfigurationList = response.dataList;
                this.dataSource = new MatTableDataSource(this.paymentGatewayConfigurationList);
                this.dataSource.sort = this.sort;
                this.paymentConfigTotalRecords = response.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                this.dataSource.data = [];
            }
        );
    }

    getAllPaymentGateway() {
        const url = "/commonList/paymentGateway";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.paymentgatewayList = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(SETTINGS.PAYMENT_GATEWAY_CONFIGURATION_DELETE) || this.loginService.hasPermission(SETTINGS.PAYMENT_GATEWAY_CONFIGURATION_EDIT)) {
            return ['id', 'name', 'status', 'action'];
        } else {
            return ['id', 'name', 'status'];
        }
    }
    onPaymentGatewayChange(event: any) {
        this.submitted = false;
        this.isDropdownClick = true;

        let gatewayName = event.value;

        this.paymentGatewayConfigService.getConfigParameterByname(gatewayName).subscribe(
            (response: any) => {
                if (response.paymentConfig.paymentConfigMappingList.length > 0) {
                    this.paymentgatewayConfigFormGroup = this.createForm(
                        response.paymentConfig.paymentConfigMappingList
                    );
                } else {
                    let existingGatewayName = this.paymentgatewayConfigFormGroup.value.paymentConfigName;
                    let existingGatewayInfo = this.paymentgatewayConfigFormGroup.value.paymentGatewayInfo;
                    this.paymentgatewayConfigFormGroup = this.fb.group({
                        paymentConfigId: [""],
                        paymentConfigName: [existingGatewayName, Validators.required],
                        paymentGatewayInfo: [existingGatewayInfo],
                    });
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    public createForm(data: any): UntypedFormGroup {
        let existingGatewayName = this.paymentgatewayConfigFormGroup.value.paymentConfigName;
        let existingGatewayId = this.paymentgatewayConfigFormGroup.value.paymentConfigId;
        let existingGatewayInfo = this.paymentgatewayConfigFormGroup.value.paymentGatewayInfo;
        const formGroupConfig = {};
        this.labelMap = {};
        this.parameterForMap = {};
        formGroupConfig["paymentConfigName"] = [existingGatewayName, Validators.required];
        formGroupConfig["paymentConfigId"] = [existingGatewayId];
        formGroupConfig["paymentGatewayInfo"] = [existingGatewayInfo];

        data.forEach(mapping => {
            formGroupConfig[mapping.paymentParameterName] = [
                mapping.paymentParameterValue,
                Validators.required,
            ];
            this.labelMap[mapping.paymentParameterName] =
                mapping.parameterDisplayName || mapping.paymentParameterName;
            this.parameterForMap[mapping.paymentParameterName] = mapping.paymentParameterFor;
        });

        return this.fb.group(formGroupConfig);
    }

    addUpdatePaymentGatewayConfig() {
        this.submitted = true;
        if (this.paymentgatewayConfigFormGroup.valid) {
            if (this.editMode) {
                var requestData = this.createRequestData();

                this.paymentGatewayConfigService.updatePaymentGatewayConfiguration(requestData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.editMode = false;
                        this.resetConfigForm();
                        this.getAllPaymentGatewayConfiguration();
                        this.toastr.success(`${response.message}`, 'Success!');
                        if (this.dialogRef) {
                            this.dialogRef.close();
                        }
                    },
                    (error: any) => {
                        if (error.status == 400) {
                            this.toastr.info(`${error.error.ERROR}`, 'Info!');
                        } else {
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                        }
                    }
                );
            } else {
                var requestData = this.createRequestData();

                this.paymentGatewayConfigService.addPaymentGatewayConfiguration(requestData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.resetConfigForm();
                        this.getAllPaymentGatewayConfiguration();
                        this.toastr.success(`${response.message}`, 'Success!');
                        this.isDropdownClick = false;
                        if (this.dialogRef) {
                            this.dialogRef.close();
                        }

                        // this.isDropdownClick = false;
                    },
                    (error: any) => {
                        if (error.status == 400) {
                            this.toastr.info(`${error.error.ERROR}`, 'Info!');
                        } else {
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                        }
                        this.isDropdownClick = false;
                    }
                );
            }
        }
    }

    createRequestData() {
        let paymentConfigMappingList: any[] = [];

        for (const key in this.paymentgatewayConfigFormGroup.value) {
            if (
                key !== "paymentConfigName" &&
                key !== "paymentConfigId" &&
                key !== "paymentGatewayInfo"
            ) {
                const transformedItem = {
                    paymentParameterName: key,
                    paymentParameterValue: this.paymentgatewayConfigFormGroup.value[key],
                };
                paymentConfigMappingList.push(transformedItem);
            }
        }

        var request = {
            paymentConfigName: this.paymentgatewayConfigFormGroup.value.paymentConfigName,
            paymentConfigId: this.paymentgatewayConfigFormGroup.value.paymentConfigId,
            paymentGatewayInfo: this.paymentgatewayConfigFormGroup.value.paymentGatewayInfo,
            paymentConfigMappingList: paymentConfigMappingList,
        };

        return request;
    }

    resetConfigForm() {
        this.paymentgatewayConfigFormGroup.reset();
        this.paymentgatewayConfigFormGroup = this.fb.group({
            paymentConfigName: ["", Validators.required],
            paymentGatewayInfo: [""],
            paymentConfigId: [""],
        });
    }

    editConfigById(configId) {
        this.editMode = true;
        this.openpaymentGatewayConfigDetailsDialog(configId);
        this.paymentGatewayConfigService.getPaymentgatewayConfigurationById(configId).subscribe(
            (response: any) => {
                var configuration = response.paymentConfig;
                this.paymentgatewayConfigFormGroup.controls.paymentConfigName.setValue(
                    configuration.paymentConfigName
                );
                this.paymentgatewayConfigFormGroup.controls.paymentConfigId.setValue(
                    configuration.paymentConfigId
                );
                this.paymentgatewayConfigFormGroup.controls.paymentGatewayInfo.setValue(
                    configuration.paymentGatewayInfo
                );
                this.paymentgatewayConfigFormGroup = this.createForm(
                    configuration.paymentConfigMappingList
                );
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    canExit() {
        if (!this.paymentgatewayConfigFormGroup.dirty) return true;
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

    changeConfigStatus(config) {
        var request = {
            paymentConfigId: config.paymentConfigId,
            isActive: config.isActive,
        };

        this.paymentGatewayConfigService.changePaymentGatewatConfigStatus(request).subscribe(
            (response: any) => {
                this.toastr.success(`${response.message}`, 'Success!');
            },
            (error: any) => {
                if (error.status == 400) {
                    this.toastr.info(`${error.error.ERROR}`, 'Info!');
                    config.isActive = false;
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            }
        );
    }

    deletePaymentConfigConfirmation(paymentco) {
        if (paymentco.paymentConfigId) {
            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: "400px",
                disableClose: true,
                data: {
                    title: "Delete Confirmation",
                    description: `Are you sure you want to delete "${paymentco.paymentConfigName}"?`,
                    yesLabel: "Confirm",
                    noLabel: "Cancel"
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.deletePaymentConfig(paymentco.paymentConfigId);

                } else {
                    this.toastr.info(`Delete operation was cancelled`, 'Info!');
                }
            });
            // this.confirmationService.confirm({
            //     message: "Do you want to delete this payment gateway configuration ?",
            //     header: "Delete Confirmation",
            //     icon: "pi pi-info-circle",
            //     accept: () => {
            //         this.deletePaymentConfig(paymentco.paymentConfigId);
            //     },
            //     reject: () => {
            //         this.toastr.info("You have rejected", 'Info!');
            //     },
            // });
        }
    }

    deletePaymentConfig(configId) {
        this.paymentGatewayConfigService.deletePaymentGatewayConfiguration(configId).subscribe(
            (response: any) => {
                this.toastr.success(`${response.message}`, 'Success!');
                this.getAllPaymentGatewayConfiguration();
                this.resetConfigForm();
                this.editMode = false;
                this.isDropdownClick = false;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    onPageChange(event: PageEvent) {
        this.paymentConfigCurrentPage = event.pageIndex + 1;
        this.paymentConfigItemsPerPage = event.pageSize;
        this.getAllPaymentGatewayConfiguration();
    }

    pageChanged(pageNumber) {
        this.paymentConfigCurrentPage = pageNumber;
        this.getAllPaymentGatewayConfiguration();
    }

    TotalItemPerPage(event: any) {
        this.paymentConfigItemsPerPage = Number(event.value);
        this.paymentConfigCurrentPage = 1;
        this.getAllPaymentGatewayConfiguration();
    }
    resetForm() {
        this.paymentgatewayConfigFormGroup.reset();
        this.resetConfigForm();
        this.editMode = false;
        this.isDropdownClick = false;
        this.submitted = false;
        if (this.dialogRef) {
            this.dialogRef.close();
        }
    }

    resetFormConfirm() {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: "400px",
            disableClose: true,
            data: {
                title: "Clear Confirmation",
                description: `Do you want to clear this form ?`,
                yesLabel: "Confirm",
                noLabel: "Cancel"
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.resetForm();
            } else {
                this.toastr.info("You have rejected", 'Info!');
            }
        });
        // this.confirmationService.confirm({
        //     message: "Do you want to clear this form ?",
        //     header: "Clear Confirmation",
        //     icon: "pi pi-info-circle",
        //     accept: () => {
        //         this.resetForm();
        //     },
        //     reject: () => {
        //         this.toastr.info("You have rejected", 'Info!');
        //     },
        // });
    }

    @ViewChild("paymentGatewayConfigDetails") paymentGatewayConfigDetails!: TemplateRef<any>;

    openpaymentGatewayConfigDetailsDialog(configId?: any) {
        if (!configId) {
            this.resetConfigForm();
            this.editMode = false;
            this.isDropdownClick = false;
            this.submitted = false;
        }

        this.dialogRef = this.dialog.open(this.paymentGatewayConfigDetails, {
            width: "900px",
            disableClose: true
        });

        this.dialogRef.afterClosed().subscribe(result => {
            this.resetConfigForm();
            this.editMode = false;
            this.isDropdownClick = false;
            this.submitted = false;
        });
    }
}
