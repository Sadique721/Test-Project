import { ThirdPartyMenuService } from "./../../service/third-party-menu.service";
import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup, Validators, FormControl, FormArray, FormGroup } from "@angular/forms";
// Remove MessageService import since we're replacing it with toastr
import { ConfirmationService } from "primeng/api";
import { LoginService } from "src/app/service/login.service";
import { Observable, Observer } from "rxjs";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { INTEGRATION_SYSTEMS } from "src/app/constants/aclConstants";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { PaymentGatewayConfigurationService } from "src/app/service/payment-gateway-configuration.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";

import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog } from "@angular/material/dialog";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
// Add ToastrService import
import { ToastrService } from 'ngx-toastr';

@Component({
    selector: "app-third-party-menu",
    templateUrl: "./third-party-menu.component.html",
    styleUrls: ["./third-party-menu.component.css"],
    standalone: false
})
export class ThirdPartyMenuComponent implements OnInit {

    displayedColumns: string[] = ['id', 'Name', 'EventName', 'ClientName', 'Status', 'Action'];
    dataSource: MatTableDataSource<any> = new MatTableDataSource<any>([]);

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;

    dialogRef: any;
    thirdPartyMenuFormGroup: UntypedFormGroup;
    submitted = false;
    isDropdownClick = false;
    editMode: boolean = false;
    detailView: boolean = false;
    editAccess: boolean = false;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    paymentgatewayList: any[] = [];
    labelMap: any = {};
    parameterForMap: any = {};
    thirdPartyMenuList: any[] = [];
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    currentPage = 1;
    totalRecords: any;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    mvnoId: string;
    pageSizeOptions: number[] = [5, 10, 20, 50, 100];
    //   statusOptions = RadiusConstants.status;
    eventNameList: any[] = [];
    clientNameList: any[] = [];
    status = [
        { label: "Active", value: "Active" },
        { label: "InActive", value: "InActive" }
    ];

    constructor(
        private fb: UntypedFormBuilder,
        private dialog: MatDialog,
        private confirmationService: ConfirmationService,
        // Replace MessageService with ToastrService
        private toastr: ToastrService,
        public loginService: LoginService,
        public commondropdownService: CommondropdownService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        public paymentGatewayConfigService: PaymentGatewayConfigurationService,
        public thirdPartyMenuService: ThirdPartyMenuService
    ) {
        this.createAccess = loginService.hasPermission(INTEGRATION_SYSTEMS.THIRD_PARTY_MENU_CREATE);
        this.deleteAccess = loginService.hasPermission(INTEGRATION_SYSTEMS.THIRD_PARTY_MENU_DELETE);
        this.editAccess = loginService.hasPermission(INTEGRATION_SYSTEMS.THIRD_PARTY_MENU_EDIT);
    }

    ngOnInit(): void {
        this.mvnoId = localStorage.getItem("mvnoId");
        this.thirdPartyMenuFormGroup = this.fb.group({
            id: [""],
            name: ["", Validators.required],
            eventName: ["", Validators.required],
            clientName: ["", Validators.required],
            status: ["", Validators.required],
            mvnoId: [this.mvnoId]
        });

        this.getAllEventNames();
        this.getAllClientNames();
        this.getAllThirdPartyMenu();
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(INTEGRATION_SYSTEMS.THIRD_PARTY_MENU_DELETE) || this.loginService.hasPermission(INTEGRATION_SYSTEMS.THIRD_PARTY_MENU_EDIT)) {
            return ['id', 'Name', 'EventName', 'ClientName', 'Status', 'Action'];
        } else {
            return ['id', 'Name', 'EventName', 'ClientName', 'Status'];
        }
    }
    ngAfterViewInit() {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
        this.getAllThirdPartyMenu();
    }

    onPageChange(event: any) {
        this.currentPage = event.pageIndex + 1;
        this.itemsPerPage = event.pageSize;
    }

    updateTableData() {
        this.dataSource.data = this.thirdPartyMenuList;
        this.totalRecords = this.thirdPartyMenuList.length;
        if (this.paginator) {
            this.paginator.firstPage();
        }
    }

    getAllThirdPartyMenu() {
        var pageRequest = {
            page: this.currentPage,
            pageSize: this.itemsPerPage
        };
        this.thirdPartyMenuService.getAllThirdPartyMenus(pageRequest).subscribe(
            (response: any) => {
                this.thirdPartyMenuList = response.thirdPartyIntegrationMenusList;
                this.totalRecords = response.pageDetails.totalRecords;
                this.dataSource.data = this.thirdPartyMenuList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    getAllEventNames() {
        const url = "/commonList/integrationEventType";
        this.commondropdownService.getMethodForEventAndClientNames(url).subscribe(
            (response: any) => {
                this.eventNameList = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    getAllClientNames() {
        const url = "/commonList/thirdPartyIntegration";
        this.commondropdownService.getMethodForEventAndClientNames(url).subscribe(
            (response: any) => {
                this.clientNameList = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    onEventAndClientChange() {
        this.submitted = false;
        const clientName = this.thirdPartyMenuFormGroup.get("clientName")?.value;
        const eventName = this.thirdPartyMenuFormGroup.get("eventName")?.value;
        if (clientName && eventName) {
            this.isDropdownClick = true;
            this.thirdPartyMenuService.getParamsByEventAndClientName(clientName, eventName).subscribe(
                (response: any) => {
                    if (response.thirdPartyIntegrationMenuData.thirdPartyIntegrationMenuMappings.length > 0) {
                        this.thirdPartyMenuFormGroup = this.createForm(
                            response.thirdPartyIntegrationMenuData.thirdPartyIntegrationMenuMappings
                        );
                    } else {
                        this.thirdPartyMenuFormGroup = this.fb.group({
                            id: [this.thirdPartyMenuFormGroup.value.id],
                            name: [this.thirdPartyMenuFormGroup.value.name],
                            status: [this.thirdPartyMenuFormGroup.value.status],
                            clientName: [this.thirdPartyMenuFormGroup.value.clientName],
                            eventName: [this.thirdPartyMenuFormGroup.value.eventName],
                            mvnoId: [this.thirdPartyMenuFormGroup.value.mvnoId]
                        });
                    }
                },
                (error: any) => {
                    this.clearThirdPartyFields();
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    clearThirdPartyFields() {
        const formControls = this.thirdPartyMenuFormGroup.controls;
        Object.keys(formControls).forEach(key => {
            if (this.labelMap[key]) {
                this.thirdPartyMenuFormGroup.removeControl(key);
                delete this.labelMap[key];
                delete this.parameterForMap[key];
            }
        });
    }

    public createForm(data: any): UntypedFormGroup {
        const formGroupConfig = {};
        this.labelMap = {};
        this.parameterForMap = {};
        const thirdPartyFormValue = this.thirdPartyMenuFormGroup.value || {};
        formGroupConfig["name"] = [thirdPartyFormValue.name || "", Validators.required];
        formGroupConfig["id"] = [thirdPartyFormValue.id || ""];
        formGroupConfig["status"] = [thirdPartyFormValue.status || "", Validators.required];
        formGroupConfig["clientName"] = [thirdPartyFormValue.clientName || "", Validators.required];
        formGroupConfig["eventName"] = [thirdPartyFormValue.eventName || "", Validators.required];
        formGroupConfig["mvnoId"] = [thirdPartyFormValue.mvnoId || ""];
        data.forEach(mapping => {
            if (mapping.thirdPartyParameterName && mapping.thirdPartyParameterValue !== undefined) {
                formGroupConfig[mapping.thirdPartyParameterName] = [
                    mapping.thirdPartyParameterValue || "",
                    Validators.required
                ];
                this.labelMap[mapping.thirdPartyParameterName] =
                    mapping.parameterDisplayName || mapping.thirdPartyParameterName;
                this.parameterForMap[mapping.thirdPartyParameterName] = mapping.paymentParameterFor;
            }
        });

        return this.fb.group(formGroupConfig);
    }

    addUpdateThirdPartyMenu() {
        this.submitted = true;
        if (this.thirdPartyMenuFormGroup.valid) {
            if (this.editMode) {
                var requestData = this.createRequestData();
                let url = "update/" + requestData.id;
                this.thirdPartyMenuService.updateThirdPartyMenu(url, requestData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.editMode = false;
                        this.resetMenuForm();
                        this.getAllThirdPartyMenu();
                        if (this.dialogRef) {
                            this.dialogRef.close();
                        }
                        this.toastr.success(`Successfully updated!`, 'Success!');

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
                this.thirdPartyMenuService.addThirdPartyMenu(requestData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.resetMenuForm();
                        this.getAllThirdPartyMenu();
                        if (this.dialogRef) {
                            this.dialogRef.close();
                        }

                        this.toastr.success(`Successfully Created!`, 'Success!');
                        this.isDropdownClick = false;

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
        const formValues = this.thirdPartyMenuFormGroup.value;
        let thirdPartyIntegrationMenuMappingList: any[] = [];

        for (const key in formValues) {
            if (
                key !== "id" &&
                key !== "name" &&
                key !== "status" &&
                key !== "eventName" &&
                key !== "clientName" &&
                key !== "mvnoId"
            ) {
                const transformedItem = {
                    thirdPartyParameterName: key,
                    thirdPartyParameterValue: formValues[key]
                };
                thirdPartyIntegrationMenuMappingList.push(transformedItem);
            }
        }

        // Now handle the 'thirdPartyIntegrationMenuMappings' field properly
        if (
            formValues.thirdPartyIntegrationMenuMappings &&
            formValues.thirdPartyIntegrationMenuMappings.length > 0
        ) {
            formValues.thirdPartyIntegrationMenuMappings.forEach(mapping => {
                thirdPartyIntegrationMenuMappingList.push({
                    thirdPartyParameterName: mapping.thirdPartyParameterName,
                    thirdPartyParameterValue: mapping.thirdPartyParameterValue
                });
            });
        }

        const request = {
            id: formValues.id,
            clientName: formValues.clientName,
            eventName: formValues.eventName,
            mvnoId: formValues.mvnoId,
            name: formValues.name,
            status: formValues.status,
            thirdPartyIntegrationMenuMappings: thirdPartyIntegrationMenuMappingList
        };

        return request;
    }

    resetMenuForm() {
        this.thirdPartyMenuFormGroup.reset();
        this.submitted = false;
        this.isDropdownClick = false;
        this.thirdPartyMenuFormGroup = this.fb.group({
            id: [""],
            name: ["", Validators.required],
            eventName: ["", Validators.required],
            clientName: ["", Validators.required],
            status: ["", Validators.required],
            mvnoId: [this.mvnoId]
        });
    }

    editThirdPartyMenuById(id) {
        this.editMode = true;
        // this.openThirdPartyDialog(id);9

        this.thirdPartyMenuService.getThirdPartyMenuById(id).subscribe(
            (response: any) => {
                var configuration = response.thirdPartyIntegrationMenusList;
                this.thirdPartyMenuFormGroup.controls.id.setValue(configuration.id);
                this.thirdPartyMenuFormGroup.controls.name.setValue(configuration.name);
                this.thirdPartyMenuFormGroup.controls.status.setValue(configuration.status);
                this.thirdPartyMenuFormGroup.controls.eventName.setValue(configuration.eventName);
                this.thirdPartyMenuFormGroup.controls.clientName.setValue(configuration.clientName);
                this.thirdPartyMenuFormGroup.controls.mvnoId.setValue(configuration.mvnoId);
                this.thirdPartyMenuFormGroup = this.createForm(
                    configuration.thirdPartyIntegrationMenuMappings
                );

                if (this.editMode) {
                    this.thirdPartyMenuFormGroup.get('eventName')?.disable();
                    this.thirdPartyMenuFormGroup.get('clientName')?.disable();
                } else {
                    this.thirdPartyMenuFormGroup.get('eventName')?.enable();
                    this.thirdPartyMenuFormGroup.get('clientName')?.enable();
                }

            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    canExit() {
        if (!this.thirdPartyMenuFormGroup.dirty) return true;
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

    deleteMenuConfirmation(configId) {
        if (configId) {

            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: "400px",
                disableClose: true,
                data: {
                    title: "Delete Confirmation",
                    description: "Do you want to delete this branch?",
                    yesLabel: "Confirm",
                    noLabel: "Cancel"
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.deleteThirdPartyMenu(configId);
                } else {
                    this.toastr.info(`Delete operation was cancelled`, 'Info!');
                }
            });
            // this.confirmationService.confirm({
            //     message: "Do you want to delete this payment gateway configuration ?",
            //     header: "Clear  Confirmation",
            //     icon: "pi pi-info-circle",
            //     accept: () => {
            //         this.deleteThirdPartyMenu(configId);
            //     },
            //     reject: () => {
            //         this.toastr.info("You have rejected", 'Info!');
            //     }
            // });
        }
    }

    deleteThirdPartyMenu(id) {
        this.thirdPartyMenuService.deleteThirdPartyMenu(id).subscribe(
            (response: any) => {
                this.toastr.success(`${response.msg}`, 'Success!');
                this.getAllThirdPartyMenu();
                this.resetMenuForm();
                this.editMode = false;
                this.isDropdownClick = false;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    pageChanged(pageNumber) {
        this.currentPage = pageNumber;
        this.getAllThirdPartyMenu();
    }

    TotalItemPerPage(event: any) {
        this.itemsPerPage = Number(event.value);
        this.getAllThirdPartyMenu();
    }
    resetForm() {
        this.thirdPartyMenuFormGroup.reset();
        this.resetMenuForm();
        this.editMode = false;
        this.isDropdownClick = false;
        if (this.dialogRef) {
            this.dialogRef.close();
        }
    }

    resetFormConfirm() {
        const dialogRef2 = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: "400px",
            disableClose: true,
            data: {
                title: "Clear Confirmation",
                description: "Do you want to clear the data?",
                yesLabel: "Confirm",
                noLabel: "Cancel"
            }
        });
        dialogRef2.afterClosed().subscribe(result => {
            if (result) {
                // Only reset form but don't close the main dialog
                this.thirdPartyMenuFormGroup.reset();
                this.resetMenuForm();
                this.editMode = false;
                this.isDropdownClick = false;
                this.submitted = false;
            } else {
                this.toastr.info(`Clear operation was cancelled`, 'Info!');
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
        //     }
        // });
    }

    @ViewChild("thirdPartyDialog") thirdPartyDialog!: TemplateRef<any>;

    openThirdPartyDialog(thirdpartyId?: any) {
        this.editMode = false;
        this.submitted = false;
        this.isDropdownClick = false;

        if (thirdpartyId) {
            this.editMode = true;
            this.editThirdPartyMenuById(thirdpartyId);
        } else {
            this.resetMenuForm();
        }

        this.dialogRef = this.dialog.open(this.thirdPartyDialog, {
            width: "900px",
            disableClose: true
        });

        this.dialogRef.afterClosed().subscribe(result => {
            this.editMode = false;
            this.submitted = false;
            this.isDropdownClick = false;
            this.resetMenuForm();
        });
    }
}
