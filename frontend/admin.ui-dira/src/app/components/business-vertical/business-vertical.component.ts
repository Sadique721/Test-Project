import { Component, OnInit } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, FormArray } from "@angular/forms";
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { StateManagementService } from "src/app/service/state-management.service";
import { Regex } from "src/app/constants/regex";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { MASTERS } from "src/app/constants/aclConstants";

@Component({
    selector: "app-business-vertical",
    templateUrl: "./business-vertical.component.html",
    styleUrls: ["./business-vertical.component.css"],
    standalone: false
})
export class BusinessVerticalComponent implements OnInit {
    busVerticalForm: UntypedFormGroup;
    submitted: boolean = false;
    busVerticalListData: any = [];
    viewbusVerticalListData: any = [];
    currentPage = 1;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    totalRecords: any;
    isEditData: boolean = false;
    searchData: any;
    searchName: any = "";
    AclClassConstants;
    AclConstants;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    searchkey: string;
    statusOptions = RadiusConstants.status;
    public loginService: LoginService;

    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private toastr: ToastrService,
        private stateManagementService: StateManagementService,
        loginService: LoginService,
        public commondropdownService: CommondropdownService
    ) {
        this.createAccess = loginService.hasPermission(MASTERS.BUSINESS_VERTICALS_CREATE);
        this.deleteAccess = loginService.hasPermission(MASTERS.BUSINESS_VERTICALS_DELETE);
        this.editAccess = loginService.hasPermission(MASTERS.BUSINESS_VERTICALS_DELETE);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
    }

    ngOnInit(): void {
        this.busVerticalForm = this.fb.group({
            vname: ["", Validators.required],
            status: ["", Validators.required],
            region_id: [""],
            id: [""],
        });

        this.searchData = {
            filter: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "any",
                    filterOperator: "equalto",
                    filterCondition: "and",
                },
            ],
        };

        this.getbusVerticalList("");
        this.commondropdownService.getRegionData();
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPage > 1) {
            this.currentPage = 1;
        }
        if (!this.searchkey) {
            this.getbusVerticalList(this.showItemPerPage);
        } else {
            this.searchbusVertical();
        }
    }

    getbusVerticalList(list) {
        let size;
        this.searchkey = "";
        let List = this.currentPage;
        if (list) {
            size = list;
            this.itemsPerPage = list;
        } else {
            size = this.itemsPerPage;
        }
        const url = "/businessverticals";
        let plandata = {
            page: List,
            pageSize: size,
        };
        this.stateManagementService.postMethod(url, plandata).subscribe(
            (response: any) => {
                this.busVerticalListData = response.dataList;
                this.totalRecords = response.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    addEditBVertical(id) {
        this.submitted = true;
        if (this.busVerticalForm.valid) {
            if (id) {
                setTimeout(() => {
                    const url = "/businessverticals/update";

                    if (this.busVerticalForm.value.region_id.length == 0) {
                        this.busVerticalForm.value.region_id = null;
                    }
                    let busVerticalListData = this.busVerticalForm.value;

                    this.stateManagementService.postMethod(url, busVerticalListData).subscribe(
                        (response: any) => {
                            this.submitted = false;
                            this.isEditData = false;
                            this.busVerticalForm.reset();
                            this.busVerticalForm.controls.region_id.setValue("");
                            this.stateManagementService.clearCache("/businessverticals/all");
                            if (!this.searchkey) {
                                this.getbusVerticalList("");
                            } else {
                                this.searchbusVertical();
                            }
                            this.toastr.success(`${response.message}`, 'Success!');
                        },
                        (error: any) => {
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                        }
                    );
                }, 3000);
            } else {
                setTimeout(() => {
                    const url = "/businessverticals/save";

                    if (this.busVerticalForm.value.region_id.length == 0) {
                        this.busVerticalForm.value.region_id = null;
                    }
                    let busVerticalListData = this.busVerticalForm.value;

                    this.stateManagementService.postMethod(url, busVerticalListData).subscribe(
                        (response: any) => {
                            this.submitted = false;
                            this.busVerticalForm.reset();
                            this.commondropdownService.clearCache("/businessverticals/all");
                            if (!this.searchkey) {
                                this.getbusVerticalList("");
                            } else {
                                this.searchbusVertical();
                            }
                            if (response.responseCode !== 200) {
                                this.toastr.error(`${response.responseMessage}`, 'Failed!');
                            } else {
                                this.toastr.success(`${response.message}`, 'Success!');
                            }
                            this.busVerticalForm.controls.region_id.setValue("");
                        },
                        (error: any) => {
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                        }
                    );
                }, 3000);
            }
        }
    }

    editBVertical(id) {
        if (id) {
            const url = "/businessverticals/" + id;
            this.stateManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.isEditData = true;
                    this.viewbusVerticalListData = response.data;
                    this.busVerticalForm.patchValue(this.viewbusVerticalListData);
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    searchbusVertical() {
        if (!this.searchkey || this.searchkey !== this.searchName) {
            this.currentPage = 1;
        }
        this.searchkey = this.searchName;
        if (this.showItemPerPage) {
            this.itemsPerPage = this.showItemPerPage;
        }

        this.searchData.filter[0].filterValue = this.searchName.trim();

        const url =
            "/businessverticals/search?page=" +
            this.currentPage +
            "&pageSize=" +
            this.itemsPerPage +
            "&sortBy=id&sortOrder=0";

        this.stateManagementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                if (response.responseCode == 404) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                    this.busVerticalListData = response.dataList;
                    this.totalRecords = response.totalRecords;
                } else {
                    this.busVerticalListData = response.dataList;
                    this.totalRecords = response.totalRecords;
                }
            },
            (error: any) => {
                this.totalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');
                    this.busVerticalListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            }
        );
    }

    clearData() {
        this.searchName = "";
        this.getbusVerticalList("");
        this.busVerticalForm.reset();
        this.submitted = false;
        this.isEditData = false;
        this.busVerticalForm.controls.region_id.setValue("");
    }

    deleteConfirmon(rdata: any) {
        if (rdata) {
            this.confirmationService.confirm({
                message: "Do you want to delete this Business Vertical?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.deleteBusVertical(rdata);
                },
                reject: () => {
                    this.toastr.info(`Delete operation was cancelled`, 'Info!');
                },
            });
        }
    }

    deleteBusVertical(rdata) {
        let data = rdata;

        const url = "/businessverticals/delete";
        this.stateManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                if (
                    response.responseCode == 405 ||
                    response.responseCode == 406 ||
                    response.responseCode == 417
                ) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                } else {
                    if (this.currentPage != 1 && this.busVerticalListData.length == 1) {
                        this.currentPage = this.currentPage - 1;
                    }
                    this.toastr.success(`${response.message}`, 'Success!');
                    if (!this.searchkey) {
                        this.getbusVerticalList("");
                    } else {
                        this.searchbusVertical();
                    }
                    this.searchName = "";
                    this.busVerticalForm.reset();
                    this.submitted = false;
                    this.isEditData = false;
                    this.busVerticalForm.controls.region_id.setValue("");
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    pageChangedList(pageNumber) {
        this.currentPage = pageNumber;
        if (!this.searchkey) {
            this.getbusVerticalList("");
        } else {
            this.searchbusVertical();
        }
    }
}
