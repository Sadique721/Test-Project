import { AfterViewInit, Component, OnInit, ViewChild } from "@angular/core";
import {
    AbstractControl,
    FormGroup,
    UntypedFormArray,
    UntypedFormBuilder,
    UntypedFormGroup,
    Validators
} from "@angular/forms";
import { ConfirmationService, MessageService } from "primeng/api";
import { ServiceManagement } from "src/app/components/model/service-management";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { ServiceManagementService } from "src/app/service/service-management.service";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { Observable, Observer } from "rxjs";
import { FieldmappingService } from "src/app/service/fieldmapping.service";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { CountryManagementService } from "src/app/service/country-management.service";
import { ProductCategoryManagementService } from "src/app/service/product-category-management.service";
import { PRODUCTS } from "src/app/constants/aclConstants";
import { StatusCheckService } from "src/app/service/status-check-service.service";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialog } from "@angular/material/dialog";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { AddEditServiceDialogComponent } from "./add-edit-service-dialog/add-edit-service-dialog.component";
import { ToastrService } from "ngx-toastr";
import { error } from "console";
declare var $: any;
@Component({
    selector: "app-service-management",
    templateUrl: "./service-management.component.html",
    styleUrls: ["./service-management.component.css"],
    standalone: false
})
export class ServiceManagementComponent implements OnInit, AfterViewInit {
    serviceGroupForm: FormGroup;
    serviceSelectExpire: UntypedFormGroup;
    createServiceData: ServiceManagement;
    submitted: boolean = false;
    viewServiceListData: any = [];
    currentPageServiceListdata = 1;
    serviceListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    serviceListdatatotalRecords: String;
    isServiceEdit: boolean;
    AclClassConstants;
    AclConstants;
    isDisabled: boolean = true;
    serviceParamArray: UntypedFormArray;
    expiryFlag: boolean = false;
    parameterList: any = [];
    parameterOptions: any = [];
    addServiceParamForm: UntypedFormGroup;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 0;
    searchkey: string;
    totalAreaListLength = 0;
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    counterServiceParam: number = 0;
    tableServiceParameter: {} = [];
    serviceModelFlag: boolean = false;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    reqInventoryList: any = [];
    planInventoryList: any = [];
    serviceParams: any = [];
    finalServiceParamList: any = [];
    parameterOptionOriginalList: any;
    serviceParamMappingList: any;
    serviceListData = new MatTableDataSource<any>([]);
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    constructor(
        private fb: UntypedFormBuilder,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private serviceManagementService: ServiceManagementService,
        public productCategoryManagementService: ProductCategoryManagementService,
        private countrymanagemntservice: CountryManagementService,
        public loginService: LoginService,
        private tempservice: FieldmappingService,
        public commondropdownService: CommondropdownService,
        public statusCheckService: StatusCheckService,
        private dialog: MatDialog,
        private toastr: ToastrService,
    ) {
        this.createAccess = loginService.hasPermission(PRODUCTS.SERVICE_CREATE);
        this.deleteAccess = loginService.hasPermission(PRODUCTS.SERVICE_DELETE);
        this.editAccess = loginService.hasPermission(PRODUCTS.SERVICE_EDIT);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
    }
    ngAfterViewInit() {
        this.serviceListData.paginator = this.paginator;
    }
    isEditService: boolean = false;
    ngOnInit(): void {
        this.serviceGroupForm = this.fb.group({
            name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            displayName: [""],
            icname: [""],
            iccode: [""],
            investmentid: [""],
            installation: [""],
            feasibility: [""],
            poc: [""],
            isServiceThroughLead: [""],
            isPriceEditable: [""],
            provisioning: [""],
            ledgerId: [""],
            expiry: [""],
            pcategoryId: [],
            serviceParamMappingList: [],
            is_dtv: [false]
        });
        this.serviceParamArray = this.fb.array([]);
        this.addServiceParamForm = this.fb.group({
            serviceParamId: ["", Validators.required],
            isMandatory: [],
            value: []
        });
        this.serviceSelectExpire = this.fb.group({
            expireDropdownValue: [""]
        });
        this.getSelIcName("");
        this.getServiceDataList("");
        this.getReqInventory();
        this.getServiceParams();
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(PRODUCTS.SERVICE_DELETE) || this.loginService.hasPermission(PRODUCTS.SERVICE_EDIT)) {
            return ["id",
                "name",
                "display_name",
                "ic_name",
                "ic_code",
                "isp_name",
                "action"];
        } else {
            return ["id",
                "name",
                "display_name",
                "ic_name",
                "ic_code",
                "isp_name"];
        }
    }

    getServiceParameter() {
        if (this.parameterOptions.length == 0) {
            this.serviceManagementService.getMethod("/service_parameters/all").subscribe((res: any) => {
                this.parameterOptionOriginalList = res.dataList;
                this.parameterList = res["dataList"].map((el: any) => {
                    el["isSelected"] = false;
                    return el;
                });
                if (!this.isEditService) {
                    this.parameterOptions = res["dataList"];
                } else if (this.isEditService) {
                    this.parameterList = this.parameterList.map((el: any) => {
                        this.serviceParamArray.value.forEach((val: any) => {
                            if (val.serviceParamId == el.id) {
                                el.isSelected = true;
                            }
                        });
                        return el;
                    });
                    this.parameterOptions = this.parameterList.filter((el: any) => !el.isSelected);
                }

            });
        }
    }

    closeModal() {
        this.serviceModelFlag = false;
    }
    addServiceParam() {
        if (this.addServiceParamForm.valid) {
            this.parameterList = this.parameterList.map((el: any) => {
                if (this.addServiceParamForm.value.serviceParamId == el.id) el.isSelected = true;
                return el;
            });
            // console.log(this.parameterList)
            var selectedParamName = this.parameterOptions.filter(
                item => item.id == this.addServiceParamForm.value.serviceParamId
            );
            this.parameterOptions = this.parameterList.filter((el: any) => !el.isSelected);

            this.serviceParamArray.push(this.createServiceParamFormGroup(selectedParamName));
            this.addServiceParamForm.reset();
        }
    }

    createServiceParamFormGroup(selectedParamName): UntypedFormGroup {
        return this.fb.group({
            // isBounded: [this.addServiceParamForm.value.isBounded],
            serviceParamName: selectedParamName != null ? selectedParamName[0].name : "",
            serviceParamId: [this.addServiceParamForm.value.serviceParamId],
            isMandatory: [this.addServiceParamForm.value.isMandatory],
            value: [this.addServiceParamForm.value.value]
        });
    }
    searchCountry() { }
    clearSearchCountry() { }
    saveChanges() {
        this.finalServiceParamList = this.serviceParamArray.value;
        this.serviceModelFlag = false;
    }

    addEditService(serviceId, result) {
        this.submitted = true;
        this.expiryFlag = false;
        // if (this.serviceGroupForm.valid) {
        if (serviceId) {
            const url = "/planservice/" + serviceId;
            this.createServiceData = JSON.parse(JSON.stringify(result));
            this.serviceManagementService.updateMethod(url, this.createServiceData).subscribe(
                (response: any) => {
                    if (response.responseCode == 406) {
                        // this.messageService.add({
                        //   severity: "error",
                        //   summary: "Error",
                        //   detail: response.responseMessage,
                        //   icon: "far fa-times-circle"
                        // });
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');

                    } else {
                        this.reserServiceGroupForm();
                        // this.messageService.add({
                        //   severity: "success",
                        //   summary: "Successfully Updated",
                        //   detail: response.msg,
                        //   icon: "far fa-check-circle"
                        // });
                        this.toastr.success(`Successfuly Updated`, 'Success!');
                    }
                },
                (error: any) => {
                    console.log(error, "error");
                    if (error.error.status == 417 || error.error.status == 406) {
                        // this.messageService.add({
                        //     severity: "info",
                        //     summary: "Info",
                        //     detail: error.error.ERROR,
                        //     icon: "far fa-times-circle"
                        // });
                        this.toastr.info(`${error.error.ERROR}`);
                    } else {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    }
                }
            );
        } else {
            const url = "/planservice";
            // this.createServiceData = this.serviceGroupForm.value;
            // this.createServiceData = result;
            this.createServiceData = JSON.parse(JSON.stringify(result));

            this.serviceManagementService.postMethod(url, this.createServiceData).subscribe(
                (response: any) => {
                    this.reserServiceGroupForm();
                    //   this.messageService.add({
                    //     severity: "success",
                    //     summary: "Successfully Created",
                    //     icon: "far fa-check-circle"
                    //   });
                    this.toastr.success(`${response.msg}`, 'Success!');
                },
                (error: any) => {
                    if (error.error.status == 406) {
                        //   this.messageService.add  ({
                        //         severity: "info",
                        //         summary: "Info",
                        //         detail: error.error.ERROR,
                        //         icon: "far fa-times-circle"
                        //     });
                        this.toastr.info(`${error.error.ERROR}`);
                    } else {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    }
                }
            );
            // }
        }
        // }
    }
    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageServiceListdata > 1) {
            this.currentPageServiceListdata = 1;
        }
        if (!this.searchkey) {
            this.getServiceDataList(this.showItemPerPage);
        }
    }

    displayedColumns: string[] = [
        "id",
        "name",
        "display_name",
        "ic_name",
        "ic_code",
        "isp_name",
        "action"
    ];
    getServiceDataList(size) {
        let page_list;
        if (size) {
            page_list = size;
            this.serviceListdataitemsPerPage = size;
        } else {
            if (this.showItemPerPage == 0) {
                this.serviceListdataitemsPerPage = this.pageITEM;
            } else {
                this.serviceListdataitemsPerPage = this.showItemPerPage;
            }
        }

        const url = "/planservice/all";
        this.serviceManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.serviceListData = response;
                //  this.dataSource = new MatTableDataSource(response.serviceList || []);
                // this.dataSource.paginator = this.paginator;
                this.serviceListData = new MatTableDataSource(response.serviceList || []);
                this.serviceListData.paginator = this.paginator;
                this.serviceGroupForm.patchValue({
                    isQoSV: false,
                    installation: false,
                    feasibility: false,
                    poc: false,
                    isPriceEditable: false,
                    provisioning: false,
                    isServiceThroughLead: false
                });
            },
            (error: any) => {
                console.log(error, "error");
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    addEditDialogService() {
        const dialogRef = this.dialog.open(AddEditServiceDialogComponent, {
            width: "80%",
            data: {
                createAccess: this.createAccess,
                editAcs: this.editAccess
            }
        });
        dialogRef.componentInstance.serviceList.subscribe(() => {
            this.getServiceDataList("");
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.addEditService("", result);
            } else {
            }
        });
    }

    canExit() {
        if (!this.serviceGroupForm.dirty) return true;
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
    deleteConfirmationServiceDialog(item: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: "400px",
            data: {
                title: "Delete Service",
                description: `Are you sure you want to delete "${item.name}"?`,
                yesLabel: "Delete",
                noLabel: "Cancel"
            }



        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteCharge(item.id);
            } else {
                this.toastr.info(`You have rejected`, 'Rejected!');


            }
        });

    }

    deleteCharge(serviceId) {
        const url = "/planservice/" + serviceId;
        this.serviceManagementService.deleteMethod(url).subscribe(
            (response: any) => {
                if (this.currentPageServiceListdata != 1 && this.totalAreaListLength == 1) {
                    this.currentPageServiceListdata = this.currentPageServiceListdata - 1;
                }
                if (response.responseCode == 417) {
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Error",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle"
                    // });
                    this.toastr.info(`${response.responseMessage}`);
                } else if (response.responseCode == 406) {
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle"
                    // });
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');
                } else {
                    // this.messageService.add({
                    //     severity: "success",
                    //     summary: "Successfully",
                    //     detail: response.message,
                    //     icon: "far fa-check-circle"
                    // });
                    this.toastr.success(`${response.message}`, 'Success!');
                }
                this.reserServiceGroupForm();
                this.getServiceDataList("");
                this.serviceSelectExpire.reset();
            },
            (error: any) => {
                if (error.error.responseCode == 417) {
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: error.error.ERROR,
                    //     icon: "far fa-times-circle"
                    // });
                    this.toastr.info(`${error.error.ERROR}`);
                } else {
                    console.log(error, "error");
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: error.error.ERROR,
                    //     icon: "far fa-times-circle"
                    // });
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            }
        );
        this.serviceGroupForm.reset();
    }

    pageChangedServiceList(pageNumber) {
        this.currentPageServiceListdata = pageNumber;
        this.getServiceDataList("");
    }
    eventExpireData: any = [];
    getExpireDataFunction() {
        let data = {
            value: "at_midnight"
        };
        let data1 = {
            value: "actual_time"
        };
    }

    getReqInventory() {
        if (this.statusCheckService.isActiveInventoryService) {
            let url = "/productCategory/getAllActiveProductCategoriesByCB";
            this.productCategoryManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.reqInventoryList = response.dataList;
                },
                (error: any) => {
                    console.log(error, "error");
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: error.error.ERROR,
                    //     icon: "far fa-times-circle"
                    // });
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    getServiceParams() {
        let url = "/service_parameters/all";
        this.serviceManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.serviceParams = response.dataList;
            },
            (error: any) => {
                console.log(error, "error");
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    onchangeEventForDTV(value: any) {
        if (value) {
            this.serviceGroupForm.controls.expiry.setValue("at_midnight");
            this.expiryFlag = true;
            //this.serviceGroupForm.controls.expiry.disable();
        } else {
            this.serviceGroupForm.controls.expiry.setValue("");
            this.expiryFlag = false;
            //this.serviceGroupForm.controls.expiry.enable();
        }
    }
    ICListdata: any = [];
    iccodedata: any = [];
    Data: any;
    getSelIcName(event) {
        const elist = event.value;
        let icData = this.ICListdata.find(item => item.icname == elist);
        if (icData) {
            this.Data = icData.iccode;
            this.serviceGroupForm.controls.investmentid.setValue(icData.id);
            this.serviceGroupForm.controls.iccode.patchValue(this.Data);
        }
        const url = "/investmentCode/getIcNames/";
        this.countrymanagemntservice.getMethod(url).subscribe((response: any) => {
            this.ICListdata = response;
        });
    }

    defaultParamValues = [];
    isMultipleFields: boolean = false;
    withEndpoint: Boolean = false;
    defultUnitName = "";
    onParamSelect(e) {
        this.defaultParamValues = [];
        this.addServiceParamForm.get("value").setValue("");
        let filterdata = this.parameterOptionOriginalList.filter((el: any) => el.id == e.value);
        if (filterdata.length > 0) {
            let filterName = filterdata[0].name;
            if (
                filterName === "RAM" ||
                filterName === "Storage" ||
                filterName === "No of Additional Storage"
            ) {
                this.defultUnitName = "GB";
            } else if (filterName === "CPU") {
                this.defultUnitName = "Core";
            } else if (filterName === "Event per second") {
                this.defultUnitName = "EPS";
            } else if (filterName === "Distance") {
                this.defultUnitName = "Km";
            } else {
                this.defultUnitName = "";
            }
        }

        const url = "/fieldMapping/fieldDetailsByParam?paramId=" + e.value;
        this.serviceManagementService.getMethod(url).subscribe((response: any) => {
            this.defaultParamValues = response.dataList;
            if (this.defaultParamValues.length == 1) {
                this.isMultipleFields = false;
                if (
                    this.defaultParamValues[0].endpoint != null &&
                    this.defaultParamValues[0].endpoint.size != 0 &&
                    this.defaultParamValues[0].endpoint != ""
                ) {
                    this.withEndpoint = true;
                    this.tempservice
                        .getMethod2(this.defaultParamValues[0].endpoint)
                        .subscribe((response: any) => {
                            this.defaultParamValues = response.dataList;
                        });
                } else this.withEndpoint = false;
            } else this.isMultipleFields = true;
        });
    }

    deleteConfirmonServiceParameter(index: number, serviceParamId) {
        if (index || index == 0) {
            this.confirmationService.confirm({
                message: "Do you want to delete this action?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.onRemoveServiceParameter(index, serviceParamId);
                },
                reject: () => {
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Rejected",
                    //     detail: "You have rejected"
                    // });
                    this.toastr.error('You have rejected');
                }
            });
        }
    }

    async onRemoveServiceParameter(index: number, serviceParamId) {
        this.serviceParamArray.removeAt(index);
        let data = this.parameterOptionOriginalList.filter((el: any) => el.id == serviceParamId);
        this.parameterOptions = this.parameterOptions.concat(data);
    }

    reserServiceGroupForm() {
        this.submitted = false;
        this.serviceGroupForm.reset();
        this.serviceGroupForm.markAsPristine();
        this.serviceGroupForm.markAsUntouched();
        this.serviceGroupForm.updateValueAndValidity();
        this.parameterOptions = [];
        this.addServiceParamForm.reset();
        this.finalServiceParamList = [];
        Object.keys(this.serviceGroupForm.controls).forEach(key => {
            this.serviceGroupForm.get(key)?.setErrors(null);
        });

        this.serviceParamArray.clear();
        this.serviceSelectExpire.reset();
        this.serviceGroupForm.controls.is_dtv.setValue(false);
        // this.serviceGroupForm.controls.name.setValue("a");
        this.isServiceEdit = false;
        this.isEditService = false;
        this.getServiceDataList("");
        this.getSelIcName("");
    }

    getPlanServiceInventoryMapping(serviceId) {
        if (this.statusCheckService.isActiveInventoryService) {
            let prductCateId = [];
            let url = `/planserviceinventory/getPlanServiceInventoryByServiceId?serviceId=${serviceId}`;
            this.productCategoryManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.planInventoryList = response.dataList;
                    if (this.planInventoryList.length > 0) {
                        this.planInventoryList.forEach(element => {
                            prductCateId.push(element.id);
                        });
                        this.serviceGroupForm.patchValue({
                            pcategoryId: prductCateId
                        });
                    } else {
                        this.reqInventoryList;
                    }
                },
                (error: any) => {
                    console.log(error, "error");
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: error.error.ERROR,
                    //     icon: "far fa-times-circle"
                    // });
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }
    //   -------------------------

    displayedColumnsforModal: string[] = ["serviceParameter", "mandatory", "value", "delete"];
    inputDisplayedColumns: string[] = ["parameter", "mandatory", "defaultValue", "action"];
    inputDataSource = [{}]; // Single row for input

    getFormControl(index: number, controlName: string): any {
        return this.serviceParamArray.at(index).get(controlName);
    }

    editService(serviceId: number) {
        if (serviceId) {
            const url = "/planservice/" + serviceId;
            this.serviceManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.isServiceEdit = true;
                    this.viewServiceListData = response.servicebyId;

                    const dialogRef = this.dialog.open(AddEditServiceDialogComponent, {
                        width: "80%",
                        data: {
                            isEdit: true,
                            editAcs: this.editAccess,
                            title: "Update Service",
                            serviceData: this.viewServiceListData
                        }
                    });

                    dialogRef.afterClosed().subscribe(result => {

                        if (result) {
                            // Patch form with returned data
                            this.serviceGroupForm.patchValue(this.viewServiceListData);

                            // Reset and repopulate FormArray
                            this.serviceParamArray.clear();
                            result.serviceParamMappingList.forEach((el: any) => {
                                this.serviceParamArray.push(
                                    this.fb.group({
                                        serviceParamId: [el.serviceParamId],
                                        isMandatory: [el.isMandatory],
                                        value: [el.value]
                                    })
                                );
                            });
                            this.getServiceParameter();

                            // Set additional logic
                            this.onchangeEventForDTV(this.viewServiceListData.is_dtv);
                            if (this.viewServiceListData.icname) {
                                this.ICListdata = [{ icname: this.viewServiceListData.icname }];
                            } else {
                                this.getSelIcName("");
                            }
                            this.getPlanServiceInventoryMapping(serviceId);

                            let expireType = this.eventExpireData.filter(
                                data => data.value === this.viewServiceListData.expiry
                            );

                            if (expireType.length > 0) {
                                if (expireType[0].type === "At_Midnight") {
                                    this.serviceSelectExpire.patchValue({ expireDropdownValue: "at_midnight" });
                                } else if (expireType[1]?.type === "At_Midnight") {
                                    this.serviceSelectExpire.patchValue({ expireDropdownValue: "actual_time" });
                                }
                            }
                            this.addEditService(serviceId, result);
                        } else {
                        }
                    });
                },
                (error: any) => {
                    console.log(error, "error");
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: error.error.ERROR,
                    //     icon: "far fa-times-circle"
                    // });
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }
}
