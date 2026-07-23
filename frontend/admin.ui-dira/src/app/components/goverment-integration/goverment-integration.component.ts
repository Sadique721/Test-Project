import { Component, OnInit } from "@angular/core";
import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { ConfirmationService, MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { GovernmentIntegrationService } from "src/app/service/government-integration.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { LoginService } from "src/app/service/login.service";
import { Observable, Observer } from "rxjs";
import { GovernmentIntegration } from "../model/government-integration";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { INTEGRATION_SYSTEMS } from "src/app/constants/aclConstants";
import { ToastrService } from 'ngx-toastr';

@Component({
    selector: "app-goverment-integration",
    templateUrl: "./goverment-integration.component.html",
    styleUrls: ["./goverment-integration.component.css"],
    standalone: false
})
export class GovermentIntegrationComponent implements OnInit {
    title = "Government Integration";
    govIntragationFormGroup: UntypedFormGroup;
    submitted: boolean = false;
    investigationData: GovernmentIntegration;
    investigationListData: any;
    isInvestmentEdit: boolean = false;
    viewInvestmentListData: any;
    currentPageInvestmentSlab = 1;
    investmentItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    investmentTotalRecords: any;
    searchInvestmentName: any = "";
    searchData: any;
    AclClassConstants;
    AclConstants;

    statusOptions = RadiusConstants.status;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    searchkey: string;
    governmentAPIMappingsFormArray: UntypedFormArray;
    governmentAPIMappingsFormGroup: UntypedFormGroup;

    parameterItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    parametertotalRecords: String;
    currentPageparameter = 1;
    public loginService: LoginService;
    apiMethodList: any;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    createAccess: boolean = false;
    constructor(
        private toastr: ToastrService,

        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private GovernmentIntegrationService: GovernmentIntegrationService,
        loginService: LoginService,
        public commondropdownService: CommondropdownService
    ) {
        this.createAccess = loginService.hasPermission(INTEGRATION_SYSTEMS.GOV_INTEGRATION_CREATE);
        this.deleteAccess = loginService.hasPermission(INTEGRATION_SYSTEMS.GOV_INTEGRATION_DELETE);
        this.editAccess = loginService.hasPermission(INTEGRATION_SYSTEMS.GOV_INTEGRATION_EDIT);
        this.governmentAPIMappingsFormGroup = this.fb.group({
            apiName: ["", Validators.required],
            endpoint: ["", Validators.required],
        });
        this.getMethodType();
    }

    ngOnInit(): void {
        this.govIntragationFormGroup = this.fb.group({
            username: ["", Validators.required],
            password: ["", Validators.required],
            governmentAPIMappings: (this.governmentAPIMappingsFormArray = this.fb.array([])),
            status: ["", Validators.required],
        });

        this.searchData = {
            filters: [
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
        this.getInvestmentListData("");
    }

    canExit() {
        if (!this.govIntragationFormGroup.dirty) {
            return true;
        }
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

    addEditGovIntegration(govIntegId) {
        this.submitted = true;
        if (this.govIntragationFormGroup.valid) {
            if (govIntegId) {
                const url = "/governmentintegrationmaster/update";
                this.govIntragationFormGroup.value.governmentAPIMappings =
                    this.governmentAPIMappingsFormArray.value;
                this.investigationData = this.govIntragationFormGroup.value;
                this.investigationData.id = govIntegId;
                this.GovernmentIntegrationService.postMethod(url, this.investigationData).subscribe(
                    (response: any) => {
                        this.governmentAPIMappingsFormArray = this.fb.array([]);
                        this.governmentAPIMappingsFormGroup.reset();
                        this.submitted = false;
                        this.isInvestmentEdit = false;
                        this.govIntragationFormGroup.reset();
                        this.toastr.success(`${response.msg}`, 'Success!');

                        this.submitted = false;
                        if (this.searchkey) {
                            this.searchInvestment();
                        } else {
                            this.getInvestmentListData("");
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                    }
                );
            } else {
                const url = "/governmentintegrationmaster/save";
                this.isInvestmentEdit = false;
                this.govIntragationFormGroup.value.governmentAPIMappings =
                    this.governmentAPIMappingsFormArray.value;
                this.investigationData = this.govIntragationFormGroup.value;
                this.GovernmentIntegrationService.postMethod(url, this.investigationData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.govIntragationFormGroup.reset();
                        this.governmentAPIMappingsFormArray = this.fb.array([]);
                        this.governmentAPIMappingsFormGroup.reset();
                        this.toastr.success(`${response.msg}`, 'Success!');

                        if (this.searchkey) {
                            this.searchInvestment();
                        } else {
                            this.getInvestmentListData("");
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                    }
                );
            }
        }
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageInvestmentSlab > 1) {
            this.currentPageInvestmentSlab = 1;
        }
        if (!this.searchkey) {
            this.getInvestmentListData(this.showItemPerPage);
        } else {
            this.searchInvestment();
        }
    }

    getInvestmentListData(list) {
        const url = "/governmentintegrationmaster";
        let size;
        this.searchkey = "";
        let pageList = this.currentPageInvestmentSlab;
        if (list) {
            size = list;
            this.investmentItemsPerPage = list;
        } else {
            size = this.investmentItemsPerPage;
        }
        let plandata = {
            page: pageList,
            pageSize: size,
        };
        this.GovernmentIntegrationService.postMethod(url, plandata).subscribe(
            (response: any) => {
                this.investigationListData = response.dataList;
                this.investigationListData = this.investigationListData.filter(s => s.isdelete != 1);
                this.investmentTotalRecords = response.totalRecords;

                this.searchkey = "";
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    editCountry(govIntegId) {
        if (govIntegId) {
            this.governmentAPIMappingsFormArray = this.fb.array([]);
            this.governmentAPIMappingsFormGroup.reset();
            const url = "/governmentintegrationmaster/" + govIntegId;
            this.GovernmentIntegrationService.getMethod(url).subscribe(
                (response: any) => {
                    this.isInvestmentEdit = true;
                    this.viewInvestmentListData = response.data;
                    this.govIntragationFormGroup.patchValue(this.viewInvestmentListData);
                    if (this.viewInvestmentListData.governmentAPIMappings.length > 0) {
                        this.viewInvestmentListData.governmentAPIMappings.forEach(element => {
                            this.governmentAPIMappingsFormArray.push(
                                this.fb.group({
                                    apiName: [element.apiName],
                                    endpoint: [element.endpoint],
                                })
                            );
                        });
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    searchInvestment() {
        if (!this.searchkey || this.searchkey !== this.searchInvestmentName) {
            this.currentPageInvestmentSlab = 1;
        }
        this.searchkey = this.searchInvestmentName;
        if (this.showItemPerPage) {
            this.investmentItemsPerPage = this.showItemPerPage;
        }
        this.searchData.filters[0].filterValue = this.searchInvestmentName.trim();
        this.searchData.page = this.currentPageInvestmentSlab;
        this.searchData.pageSize = this.investmentItemsPerPage;

        const url = "/governmentintegrationmaster/search";
        this.GovernmentIntegrationService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                this.investigationListData = response.countryList;
                this.investmentTotalRecords = response.pageDetails.totalRecords;
            },
            (error: any) => {
                this.investmentTotalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');

                    this.investigationListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            }
        );
    }

    clearSearchInvestment() {
        this.searchInvestmentName = "";
        this.searchkey = "";
        this.getInvestmentListData("");
        this.submitted = false;
        this.isInvestmentEdit = false;
        this.govIntragationFormGroup.reset();
        this.governmentAPIMappingsFormArray = this.fb.array([]);
        this.governmentAPIMappingsFormGroup.reset();
    }

    deleteConfirmonInvestigation(govIntegId: number) {
        if (govIntegId) {
            this.confirmationService.confirm({
                message: "Do you want to delete this " + this.title + "?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.deleteInvestigation(govIntegId);
                },
                reject: () => {
                    error: (error) => { this.toastr.info(`${error.error.msg}`, 'You have rejected!'); }

                },
            });
        }
    }

    deleteInvestigation(govInteg) {
        const url = "/governmentintegrationmaster/delete";
        let data = govInteg;
        data.isDeleted = true;
        this.GovernmentIntegrationService.postMethod(url, data).subscribe(
            (response: any) => {
                if (this.currentPageInvestmentSlab != 1 && this.investigationListData.length == 1) {
                    this.currentPageInvestmentSlab = this.currentPageInvestmentSlab - 1;
                }
                this.clearSearchInvestment();
                this.toastr.success(`${response.msg}`, 'Success!');


                if (this.searchkey) {
                    this.searchInvestment();
                } else {
                    this.getInvestmentListData("");
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    pageChangedCountryList(pageNumber) {
        this.currentPageInvestmentSlab = pageNumber;
        if (this.searchkey) {
            this.searchInvestment();
        } else {
            this.getInvestmentListData("");
        }
    }

    onAddParameterMappingList() {
        this.submitted = true;
        if (
            this.governmentAPIMappingsFormGroup.controls.apiName.value != null &&
            this.governmentAPIMappingsFormGroup.controls.endpoint.value != null
        ) {
            this.governmentAPIMappingsFormArray.push(this.createParameterMappingFormGroup());
            this.governmentAPIMappingsFormGroup.reset();
            this.submitted = false;
        } else {
            this.governmentAPIMappingsFormGroup.reset();
        }
    }

    createParameterMappingFormGroup(): UntypedFormGroup {
        return this.fb.group({
            apiName: [
                this.governmentAPIMappingsFormGroup.value.apiName
                    ? this.governmentAPIMappingsFormGroup.value.apiName
                    : "",
            ],
            endpoint: [this.governmentAPIMappingsFormGroup.value.endpoint],
        });
    }

    async onRemoveparameter(chargeFieldIndex: number) {
        this.governmentAPIMappingsFormArray.removeAt(chargeFieldIndex);
    }

    deleteConfirmonParameterMappingList(index) {
        this.confirmationService.confirm({
            message: "Do you want to delete this Paramater?",
            header: "Delete Confirmation",
            icon: "pi pi-info-circle",
            accept: () => {
                this.onRemoveparameter(index);
            },
            reject: () => {
                error: (error) => { this.toastr.info(`${error.error.msg}`, 'You have rejected!'); }


            },
        });
    }

    pageChangedParameterMappingList(pageNumber) {
        this.currentPageparameter = pageNumber;
    }

    getMethodType() {
        let url = `/commonList/generic/SEND_OPTIONS_GOVERNMENT`;
        this.commondropdownService.getMethodWithCache(url).subscribe((response: any) => {
            this.apiMethodList = response.dataList;
        });
    }
}
