import { status } from "./../../RadiusUtils/RadiusConstants";
import { DatePipe } from "@angular/common";
import { Component, Input, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup, NgForm, Validators, FormGroup } from "@angular/forms";
import { Router } from "@angular/router";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import { element } from "protractor";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { RadiusUtility } from "src/app/RadiusUtils/RadiusUtility";
import { LoginService } from "src/app/service/login.service";
import { VoucherConfigurationService } from "src/app/service/voucher-configuration.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { PRODUCTS } from "src/app/constants/aclConstants";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { ToastrService } from 'ngx-toastr';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from "@angular/material/sort";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { MatSlideToggleChange } from "@angular/material/slide-toggle";
import { VoucherBatchComponent } from "../voucher-batch/voucher-batch.component";
import { VoucherBatchService } from "src/app/service/voucher-batch.service";
import { VoucherService } from "src/app/service/voucher.service";
import * as XLSX from "xlsx";

@Component({
    selector: "app-voucher-configuration",
    templateUrl: "./voucher-configuration.component.html",
    styleUrls: ["./voucher-configuration.component.css"],
    standalone: false
})
export class VoucherConfigurationComponent implements OnInit {
    voucherSearchForm!: FormGroup;
    batchNameList = [];
    totalBatchNameList = [];

    allIsChecked: boolean = false;
    batchNameSet = new Set();
    @ViewChild('VoucherDialogTemplate') VoucherDialogTemplate!: TemplateRef<any>;
    @ViewChild('voucherDetailTemplate') voucherDetailTemplate!: TemplateRef<any>;
    @ViewChild('GenerateVoucherDialogTemplate') GenerateVoucherDialogTemplate!: TemplateRef<any>;
    batchName = history.state.data ? history.state.data.batchName : null;
    batchId = history.state.data ? history.state.data.batchId : null;
    dialogRef!: MatDialogRef<any>;
    status1 = [
        { label: "GENERATED" },
        { label: "ACTIVE" },
        { label: "BLOCKED" },
        { label: "USED" },
        { label: "SCRAPPED" },
        { label: "EXPIRED" }
    ];
    totalElements: number;
    activeTabIndex: number = 0;
    displayedColumns = [
        'name',
        'plan',
        'status',
        'createdOn',
        'ispName',
        'action'
    ];
    isChecked: boolean = false;
    @ViewChild(VoucherBatchComponent)
    custauditWorkflowModal: VoucherBatchComponent;
    // @ViewChild("searchVoucherForm") searchVoucherForm: NgForm;
    vourcharConfigForm: UntypedFormGroup;
    voucherGenerateForm: UntypedFormGroup;
    VoucherBatchForm: UntypedFormGroup;
    shownVoucherConfig: boolean = false;
    shownVoucherGenerate: boolean = true;
    submitted = false;
    searchSubmitted = false;
    status = [{ label: "ACTIVE" }, { label: "INACTIVE" }];
    voucherTypeOption = [
        { label: "WALLET", value: "WALLET" },
        { label: "PLAN", value: "PLAN" }
    ];
    batchData: any = [];
    linkType: string;
    mvnoData: any;
    loggedInUser: any;
    mvnoId: any;
    filteredPlanList: Array<any> = [];
    resellerData: any;
    batchPlanId: number;

    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    searchkey: string;
    showProfile: boolean;
    showBatch: boolean;
    showVoucher: boolean;
    searchForm: UntypedFormGroup;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    downloadAccess: boolean = false;
    generateAccess: boolean = false;
    voucherBatchAccess: boolean = false;
    voucherManageAccess: boolean = false;
    AclClassConstants;
    AclConstants;
    userId: String;
    superAdminId: string;
    searchVoucherForm!: FormGroup;
    @Input() batchData2: any[] = [];
    allIDs = [];
    findByBatchId: any;


    constructor(
        private voucherService: VoucherService,
        private toastr: ToastrService,
        private dialog: MatDialog,
        private voucherConfigService: VoucherConfigurationService,
        private radiusUtility: RadiusUtility,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private fb: UntypedFormBuilder,
        private router: Router,
        private VoucherBatchService: VoucherBatchService,
        private datePipe: DatePipe,
        loginService: LoginService
    ) {
        this.createAccess = loginService.hasPermission(PRODUCTS.VOUCHER_CREATE);
        this.deleteAccess = loginService.hasPermission(PRODUCTS.VOUCHER_DELETE);
        this.editAccess = loginService.hasPermission(PRODUCTS.VOUCHER_EDIT);
        this.generateAccess = loginService.hasPermission(PRODUCTS.VOUCHER_GENERATE);
        this.voucherBatchAccess = loginService.hasPermission(PRODUCTS.SHOW_VOUCHER_BATCH);
        this.voucherManageAccess = loginService.hasPermission(PRODUCTS.SHOW_MANAGE_VOUCHERS);
        this.downloadAccess = loginService.hasPermission(PRODUCTS.DOWNLOAD_VOUCHER);
        this.showProfile = true;
        this.getAllVouchers("");
        this.getAllPlans();
        //this.getAllReseller();
    }

    ngOnInit(): void {

        this.voucherSearchForm = this.fb.group({
            batchName: ["", Validators.required],
            configId: [""],
            status1: [""]
        });
        this.searchVoucherForm = this.fb.group({
            voucherName: [''],
            fromDate: [''],
            toDate: ['']
        });
        this.searchForm = this.fb.group({
            batchName: [""],
        });
        this.loggedInUser = localStorage.getItem("loggedInUser");
        this.mvnoData = JSON.parse(localStorage.getItem("mvnoData"));
        this.userId = localStorage.getItem("userId");
        this.superAdminId = RadiusConstants.SUPERADMINID;
        if (this.userId == this.superAdminId) {
            this.vourcharConfigForm = this.fb.group({
                voucherName: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
                voucherCodeFormat: ["", Validators.required],
                noOfVoucher: ["", [Validators.required, Validators.min(1), Validators.max(10000)]],
                voucherCodeLength: ["", [Validators.required, Validators.min(1), Validators.max(16)]],
                linkType: ["", Validators.required],
                planName: ["", Validators.required],
                validity: ["", [Validators.required, Validators.min(-1)]],
                prefix: [""],
                suffix: [""],
                status: ["", Validators.required],
                mvnoName: ["", Validators.required],
                createdBy: "",
                lastModifiedBy: "",
                voucherAmount: ["", Validators.required]
            });
        } else {
            this.vourcharConfigForm = this.fb.group({
                voucherName: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
                voucherCodeFormat: ["", Validators.required],
                noOfVoucher: ["", [Validators.required, Validators.min(1), Validators.max(10000)]],
                voucherCodeLength: ["", [Validators.required, Validators.min(1), Validators.max(16)]],
                planId: [""],
                linkType: ["", Validators.required],
                planName: [""],
                validity: ["", [Validators.required, Validators.min(-1)]],
                prefix: [""],
                suffix: [""],
                status: ["", Validators.required],
                mvnoName: [""],
                createdBy: "",
                lastModifiedBy: "",
                voucherAmount: [""]
            });

            this.vourcharConfigForm.get("linkType")?.valueChanges.subscribe(value => {
                const planControl = this.vourcharConfigForm.get("planId");
                const voucherAmountControl = this.vourcharConfigForm.get("voucherAmount");

                if (value === "PLAN") {
                    planControl?.setValidators([Validators.required]);
                    voucherAmountControl?.clearValidators();
                    voucherAmountControl?.setValue(null);
                } else {
                    voucherAmountControl?.setValidators([Validators.required]);
                    planControl?.clearValidators();
                    planControl?.setValue(null);
                }

                planControl?.updateValueAndValidity();
                voucherAmountControl?.updateValueAndValidity();
            });
        }
        this.vourcharConfigForm.patchValue({
            status: "Active"
        });

        this.hideGeneateVoucherForm();

        this.voucherGenerateForm = this.fb.group({
            batchName: ["", Validators.required],
            voucherProfileId: [""],
            configName: ["", Validators.required],
            configId: [""],
            reseller: [""],
            lastModifiedBy: [""],
            linkType: [""],
            voucherAmount: [""]
        });
        this.VoucherBatchForm = this.fb.group({
            batchName: ["", Validators.required],
            voucherProfileId: [""],
            linkType: ["", Validators.required],
            planId: [""],
            resellerId: [""],
            voucherQuantity: ["", Validators.required],
            voucherAmount: [""]
            //createdBy: [''],
        });
        this.getAllVouchers('')
    }

    //Used for pagination
    totalRecords: number;
    currentPage: number = 1;
    itemsPerPage: number = RadiusConstants.ITEMS_PER_PAGE;

    pageChanged(event: any) {
        this.currentPage = event.pageIndex + 1;
        this.itemsPerPage = event.pageSize;

        if (!this.searchkey) {
            this.getAllVouchers("");
        } else {
            this.searchVoucher("");
        }
    }


    voucherTypeValue: String[] = ["UPPER_CASE", "NUMBER", "LOWER_CASE"];
    //   voucherTypeValue: String[] = ["UPPER_CASE", "NUMBER", "LOWER_CASE", "SYMBOL"];
    voucherData: any = [];

    editMode: boolean = false;
    changeStatusData: any[] = [];
    accessData: any = JSON.parse(localStorage.getItem("accessData"));

    getAllVouchers(list) {
        let size;
        this.searchkey = "";
        let page = this.currentPage;
        if (list) {
            size = list;
            this.itemsPerPage = list;
        } else {
            size = this.itemsPerPage;
        }
        this.voucherConfigService.getAllVouchers(page, size).subscribe(
            (response: any) => {
                if (response.status == 204) {
                    this.toastr.info(`${response.message}`, 'Info!');

                } else {
                    this.voucherData = response.voucherConfigurationList.data;
                    this.totalRecords = response.voucherConfigurationList.totalRecords;
                }
            },
            error => {
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                } else {

                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                }
                this.totalRecords = 0;
                this.voucherData = [];
            }
        );
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPage > 1) {
            this.currentPage = 1;
        }
        if (!this.searchkey) {
            this.getAllVouchers(this.showItemPerPage);
        }
        if (this.searchkey && this.currentPage == 1) {
            this.currentPage = 1;
            this.itemsPerPage = this.showItemPerPage;
        } else {
            this.searchVoucher("");
        }
    }

    getAllReseller() {
        this.voucherConfigService.getAllReseller().subscribe(
            (response: any) => {
                this.resellerData = response.resellers.data;
            },
            (error: any) => {
                if (error.status == 500) {
                    this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                } else {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                }
            }
        );
    }
    selectedVoucherTypeValueForSearch = "";
    searchVoucher(searchData) {
        var f = "";
        var t = "";
        if (this.searchVoucherForm.value.fromDate) {
            f = this.datePipe.transform(this.searchVoucherForm.controls.fromDate.value, "yyyy-MM-dd");
        }
        if (this.searchVoucherForm.value.toDate) {
            t = this.datePipe.transform(this.searchVoucherForm.controls.toDate.value, "yyyy-MM-dd");
        }
        // this.currentPage = 1;

        this.searchSubmitted = true;
        this.hideGeneateVoucherForm();

        if (this.selectedVouchersForSearch != null) {
            this.selectedVoucherTypeValueForSearch = this.selectedVouchersForSearch.map(
                ({ name }) => name
            );
        }

        if (!this.searchkey || this.searchkey !== this.searchVoucherForm.value.voucherName) {
            this.currentPage = 1;
        }

        if (this.searchVoucherForm.valid) {
            this.voucherData = [];
            let userNameForSearch = this.searchVoucherForm.value.voucherName
                ? this.searchVoucherForm.value.voucherName
                : "";

            this.searchkey = userNameForSearch;
            this.voucherConfigService
                .searchVoucher(
                    userNameForSearch,
                    this.selectedVoucherTypeValueForSearch,
                    f,
                    t,
                    this.currentPage,
                    this.itemsPerPage
                )
                .subscribe(
                    (response: any) => {
                        //this.reset();
                        this.voucherData = response.voucherConfigurationList.data;
                        this.totalRecords = response.voucherConfigurationList.totalRecords;
                        this.selectedVouchersForSearch = [];
                    },
                    (error: any) => {
                        this.reset();
                        this.totalRecords = 0;
                        this.voucherData = [];
                        if (error.error.status == 404) {
                            this.totalRecords = 0;
                            this.voucherData = [];
                            this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                        } else {
                            this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                        }
                        this.selectedVouchersForSearch = [];
                    }
                );
        }
    }

    selectedVoucherData: any = [];
    voucherConfig = {
        name: "",
        noOfVoucher: "",
        planName: "",
        validity: "",
        voucherCodeLength: "",
        prefix: "",
        status: "",
        suffix: "",
        voucherCodeFormat: [],
        createdBy: "",
        lastModifiedBy: "",
        voucherAmount: "",
        selectedVoucherType: "",
        linkType: ""
    };
    planDetail = {
        planName: ""
    };

    showVoucherDetail(voucherId, index, mvnoId) {
        this.modalToggle = true;
        this.hideGeneateVoucherForm();

        this.voucherConfigService.viewVoucherConfigDetail(voucherId, mvnoId).subscribe(
            (response: any) => {

                this.selectedVoucherData = response;
                this.voucherConfig = this.selectedVoucherData.voucherConfiguration;
                this.planDetail = this.selectedVoucherData.voucherConfiguration.plan;


                const dialogRef = this.dialog.open(this.voucherDetailTemplate, {
                    width: '650px',
                    height: 'auto',
                    disableClose: false,
                    data: {
                        voucher: this.voucherConfig,
                        plan: this.planDetail
                    }
                });


                dialogRef.afterClosed().subscribe(result => {
                });

            },
            error => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
            }
        );
    }


    voucherConfigIdForUpdate = "";
    editVoucherById(voucherId, index) {

        this.voucherConfigIdForUpdate = voucherId;
        this.hideGeneateVoucherForm();

        this.editMode = true;
        index = this.radiusUtility.getIndexOfSelectedRecord(
            index,
            this.currentPage,
            this.itemsPerPage
        );

        this.voucherConfigService.viewVoucherConfigDetail(voucherId, '').subscribe(
            response => {
                let data: any = response;

            },
            error => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );


        if (this.validateUserToPerformOperations(this.voucherData[index].mvnoId)) {
            let selectedValueArray = [];
            let type = this.voucherData[index].voucherCodeFormat;
            type.forEach(voucherCodeFormat => {
                let voucherType = this.voucherTypes.filter(x => x.name == voucherCodeFormat);
                selectedValueArray.push({
                    id: voucherType[0].id,
                    name: voucherType[0].name
                });
            });

            const plan = {
                //id: this.voucherData[index].plan.id,
                // name: this.voucherData[index].plan.name
            };

            this.vourcharConfigForm.patchValue({
                voucherName: this.voucherData[index].name,
                voucherCodeFormat: selectedValueArray,
                noOfVoucher: this.voucherData[index].noOfVoucher,
                voucherCodeLength: this.voucherData[index].voucherCodeLength,
                planId: this.voucherData[index].plan?.id,
                validity: this.voucherData[index].validity,
                prefix: this.voucherData[index].prefix,
                suffix: this.voucherData[index].suffix,
                status: this.voucherData[index].status,
                linkType: this.voucherData[index].linkType,
                voucherAmount: this.voucherData[index].voucherAmount
                // mvnoName: this.voucherData[index].mvnoId,
            }

            );

        }
        this.dialogRef = this.dialog.open(this.VoucherDialogTemplate, {
            width: '1200px',

        });



    }

    modalToggle: boolean = true;
    validateUserToPerformOperations(selectedMvnoId) {
        let loggedInUserMvnoId = localStorage.getItem("mvnoId");
        let userId = localStorage.getItem("userId");
        if (userId != RadiusConstants.SUPERADMINID && selectedMvnoId != loggedInUserMvnoId) {
            this.reset();
            error: (error) => {
                this.toastr.info(`${error.responseMessage}`, 'You are not authorized to do this operation. Please contact to the administrator!');
            }

            this.modalToggle = false;
            return false;
        }
        return true;
    }

    deleteConfirm(voucherId, index) {
        this.confirmationService.confirm({
            message: "Do you want to delete this Voucher?",
            header: "Delete Confirmation",
            icon: "pi pi-info-circle",
            accept: () => {
                this.deleteVoucherById(voucherId, index);
                this.confirmationService.close();
            },
            reject: () => {
                error: (error) => {
                    this.toastr.info(`${error.responseMessage}`, 'You have rejected');
                }
                this.modalToggle = true;
                this.confirmationService.close();
            }
        });
    }
    deleteVoucherById(voucherId: number, index: number) {

        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete Voucher',
                description: 'Are you sure you want to delete this Voucher?',
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {

            if (result) {


                this.hideGeneateVoucherForm();

                this.voucherConfigService.deleteById(voucherId).subscribe(
                    (response: any) => {

                        if (this.currentPage != 1 && index == 0 && this.voucherData.length == 1) {
                            this.currentPage = this.currentPage - 1;
                        }

                        if (!this.searchkey) {
                            this.getAllVouchers("");
                        }

                        this.reset();
                        this.voucherData = response;
                        this.toastr.success(`Successfully delete`, 'Success!');
                    },

                    error => {
                        if (error.error.status == 417) {
                            this.toastr.info(`${error.error.msg}`, 'Info!');
                        } else {
                            this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
                        }
                        this.clearFormData();
                    }
                );

            } else {

                this.toastr.info("You have cancelled", "Info!");
            }

        });
    }


    // clearSearchForm() {
    //     this.clearFormData();
    //     this.hideGeneateVoucherForm();
    //     this.reset();
    //     this.currentPage = 1;
    //     this.searchVoucherForm.reset();
    //     this.getAllVouchers("");
    // }

    clearFormData() {
        this.submitted = false;
        this.editMode = false;
        this.vourcharConfigForm.reset();
        this.vourcharConfigForm.patchValue({
            status: "Active"
        });
    }

    changeStatusToInActive(id, mvnoId, event) {
        this.modalToggle = true;

        if (this.validateUserToPerformOperations(mvnoId)) {

            this.hideGeneateVoucherForm();

            this.voucherConfigService.changeVoucherConfigStatus(id, "Inactive", mvnoId)
                .subscribe(
                    (response: any) => {
                        this.getAllVouchers("");
                        this.reset();
                        this.toastr.success("Successfully", 'Success!');
                        this.changeStatusData = response;
                    },
                    error => {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
                        this.reset();
                    }
                );
        }
    }

    changeStatusToActive(id, mvnoId, event) {
        this.modalToggle = true;

        if (this.validateUserToPerformOperations(mvnoId)) {

            this.hideGeneateVoucherForm();

            this.voucherConfigService.changeVoucherConfigStatus(id, "Active", mvnoId)
                .subscribe(
                    (response: any) => {
                        this.getAllVouchers("");
                        this.reset();
                        this.toastr.success("Successfully", 'Success!');
                        this.changeStatusData = response;
                    },
                    error => {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
                        this.reset();
                    }
                );
        }
    }


    reset() {
        this.clearFormData();
    }

    selectedVouchersForSearch: any = [];
    selectedVouchers: any = [];
    voucherTypes = [
        { id: 1, name: "UPPER_CASE" },
        { id: 2, name: "NUMBER" },
        { id: 3, name: "LOWER_CASE" }
        // { id: 4, name: "SYMBOL" }
    ];

    newVoucherData = {
        name: "",
        noOfVoucher: "",
        planId: "",
        validity: "",
        voucherCodeLength: "",
        prefix: "",
        status: "",
        suffix: "",
        voucherCodeFormat: [],
        mvnoId: "",
        createdBy: "",
        lastModifiedBy: "",
        linkType: "",
        voucherAmount: ""
    };

    newVoucherDataForUpdate = {
        id: "",
        name: "",
        noOfVoucher: "",
        planId: "",
        validity: "",
        voucherCodeLength: "",
        prefix: "",
        status: "",
        suffix: "",
        voucherCodeFormat: [],
        mvnoId: "",
        createdBy: "",
        lastModifiedBy: "",
        linkType: "",
        voucherAmount: ""
    };

    mapFormDataWithObject() {
        if (this.editMode) {
            this.newVoucherDataForUpdate.id = this.voucherConfigIdForUpdate;
            this.newVoucherDataForUpdate.name = this.vourcharConfigForm.value.voucherName;
            this.newVoucherDataForUpdate.noOfVoucher = this.vourcharConfigForm.value.noOfVoucher;
            this.newVoucherDataForUpdate.voucherCodeLength =
                this.vourcharConfigForm.value.voucherCodeLength;
            this.newVoucherDataForUpdate.planId = this.vourcharConfigForm.value.planId;
            this.newVoucherDataForUpdate.validity = this.vourcharConfigForm.value.validity;
            this.newVoucherDataForUpdate.suffix = this.vourcharConfigForm.value.suffix;
            this.newVoucherDataForUpdate.prefix = this.vourcharConfigForm.value.prefix;
            this.newVoucherDataForUpdate.status = this.vourcharConfigForm.value.status;
            this.newVoucherDataForUpdate.linkType = this.vourcharConfigForm.value.linkType;
            this.newVoucherDataForUpdate.voucherAmount = this.vourcharConfigForm.value.voucherAmount;
            if (this.vourcharConfigForm.value.voucherCodeFormat != null) {
                const selectedVoucherType = this.vourcharConfigForm.value.voucherCodeFormat.map(
                    ({ name }) => name
                );
                this.newVoucherDataForUpdate.voucherCodeFormat = selectedVoucherType;
            }
            this.newVoucherDataForUpdate.mvnoId = this.vourcharConfigForm.value.mvnoName;
            this.newVoucherDataForUpdate.createdBy = this.vourcharConfigForm.value.loggedInUser;
        } else {

            this.newVoucherData.name = this.vourcharConfigForm.value.voucherName;
            this.newVoucherData.noOfVoucher = this.vourcharConfigForm.value.noOfVoucher;
            this.newVoucherData.voucherCodeLength = this.vourcharConfigForm.value.voucherCodeLength;
            this.newVoucherData.planId = this.vourcharConfigForm.value.planId;
            this.newVoucherData.validity = this.vourcharConfigForm.value.validity;
            this.newVoucherData.suffix = this.vourcharConfigForm.value.suffix;
            this.newVoucherData.prefix = this.vourcharConfigForm.value.prefix;
            this.newVoucherData.status = this.vourcharConfigForm.value.status;
            this.newVoucherData.linkType = this.vourcharConfigForm.value.linkType;
            this.newVoucherData.voucherAmount = this.vourcharConfigForm.value.voucherAmount;
            let code = this.vourcharConfigForm.value.voucherCodeFormat;
            if (this.vourcharConfigForm.value.voucherCodeFormat) {
                const selectedVoucherType = code.map(({ name }) => name);
                this.newVoucherData.voucherCodeFormat = selectedVoucherType;
            }
            this.newVoucherData.mvnoId = this.vourcharConfigForm.value.mvnoName;
        }
    }

    saveVoucherConfig() {
        this.submitted = true;
        this.vourcharConfigForm.markAllAsTouched();
        if (this.vourcharConfigForm.valid) {
            this.mapFormDataWithObject();
            if (!this.editMode) {
                this.addVoucher();
            } else {
                this.updateVoucher();
            }
        }
    }

    addVoucher() {

        let userId = localStorage.getItem("userId");

        if (userId == RadiusConstants.SUPERADMINID) {
            this.vourcharConfigForm.get("mvnoName").setValidators([Validators.required]);
            this.vourcharConfigForm.get("mvnoName").updateValueAndValidity();
        }

        this.newVoucherData.createdBy = this.loggedInUser;
        this.newVoucherData.lastModifiedBy = "";

        this.voucherConfigService.saveVoucherConfig(this.newVoucherData).subscribe(
            (response: any) => {
                this.reset();
                this.getAllVouchers("");
                this.voucherData = response;
                this.vourcharConfigForm.get("mvnoName").clearValidators();
                this.vourcharConfigForm.get("mvnoName").updateValueAndValidity();
                this.dialogRef.close();
                this.toastr.success(`Successfully`, 'Success!');


            },
            error => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );
    }

    updateVoucher() {

        this.newVoucherDataForUpdate.lastModifiedBy = this.loggedInUser;
        this.voucherConfigService.updateVoucherConfig(this.newVoucherDataForUpdate).subscribe(
            (response: any) => {
                this.reset();
                this.getAllVouchers("");
                this.dialogRef.close();
                this.toastr.success(`Successfully Update`, 'Success!');


                this.voucherData = response;
            },
            error => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );
    }

    planData: any;

    getAllPlans() {
        this.voucherConfigService.getValidPlans().subscribe(
            (response: any) => {
                this.planData = response.postpaidplanList;

            },
            (error: any) => {
                if (error.status == 500) {
                    this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                } else {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                }
            }
        );
    }

    hideGeneateVoucherForm() {
        this.shownVoucherGenerate = true;
        this.shownVoucherConfig = false;
    }

    voucherConfigurationId = "";
    mvnoIdToGenerateVoucher: any;
    batchNoVoucher: number;
    voucherConfigId: number;
    resellerDropDown: any;
    generateVoucher(voucherConfigId, voucherConfigName, planId, noVoucher) {
        this.voucherGenerateForm.reset();
        this.voucherGenerateForm.patchValue({
            configName: voucherConfigName
        });
        this.voucherConfigId = voucherConfigId;
        this.shownVoucherConfig = true;
        this.shownVoucherGenerate = false;

        this.resellerDropDown = [];
        let selectedVoucherPlan;
        this.resellerDropDown = this.resellerData;
        this.batchPlanId = planId;
        this.batchNoVoucher = noVoucher;
        if (planId != null) {
            selectedVoucherPlan = this.planData.postpaidplanList.filter(
                element => element.planId == planId
            );

            let resellerFilterDataNew: any = [];
            let isMappedLocationFound: boolean = false;
            selectedVoucherPlan.forEach(item => {
                this.resellerData = this.resellerData.filter(element => element.status == "Active");
                if (item.planLocationsMapping != null) {
                    item.planLocationsMapping.forEach(locationData => {
                        this.resellerDropDown = [];
                        this.resellerData.filter(element => {
                            if (element.locationMaster.locationMasterId == locationData.locationId) {
                                isMappedLocationFound = true;
                                resellerFilterDataNew.push(element);
                            }
                        });
                    });
                }
            });
            if (isMappedLocationFound) {
                this.resellerDropDown = resellerFilterDataNew;
            }
        }

    }
    openGenerateDialog(voucher) {

        this.dialogRef = this.dialog.open(this.GenerateVoucherDialogTemplate, {
            width: '800px',
        });


        this.generateVoucher(
            voucher.id,
            voucher.name,
            voucher.plan?.id,
            voucher.noOfVoucher
        );
    }

    generateVoucherData: any = [];
    generateSubmitted: boolean = false;
    submitToGenerateVoucher() {
        this.generateSubmitted = true;
        this.voucherGenerateForm.markAllAsTouched();
        let batchName = this.voucherGenerateForm.value.batchName;

        if (this.voucherGenerateForm.valid) {
            // this.voucherConfigService
            //   .generateVoucher(
            //     this.voucherConfigurationId,
            //     batchName,
            //     this.mvnoIdToGenerateVoucher
            //   )
            //   .subscribe(
            //     (response: any) => {
            //       this.generateVoucherData = response;
            //       this.messageService.add({
            //         severity: 'success',
            //         summary: 'Successfully',
            //         detail: response.message,
            //         icon: 'far fa-check-circle',
            //       });
            //       this.generateSubmitted = false;
            //       this.VoucherBatchForm.reset();
            //       this.VoucherBatchForm.patchValue({
            //         batchName: this.voucherGenerateForm.value.batchName,
            //         planId: this.batchPlanId,
            //         voucherQuantity: this.batchNoVoucher,
            //         resellerId: this.voucherGenerateForm.value.reseller,
            //       });
            //       this.addVoucherBatch(this.mvnoIdToGenerateVoucher);
            //       this.currentPage = 1;
            //       this.voucherGenerateForm.reset();
            //
            // setTimeout(() => {
            //   this.currentPage = 1;
            //   this.hideGeneateVoucherForm();
            //   this.voucherGenerateForm.reset();
            // }, 1000);
            //   },
            //   (error) => {
            //     this.messageService.add({
            //       severity: 'error',
            //       summary: 'Error',
            //       detail: error.error.errorMessage,
            //       icon: 'far fa-times-circle',
            //     });
            //
            //   }
            // );
            // console.log('check this : ', this.voucherConfigId);
            this.VoucherBatchForm.reset();
            this.VoucherBatchForm.patchValue({
                batchName: this.voucherGenerateForm.value.batchName,
                voucherProfileId: this.voucherConfigId,
                planId: this.batchPlanId,
                voucherQuantity: this.batchNoVoucher,
                resellerId: this.voucherGenerateForm.value.reseller
            });
            this.addVoucherBatch();
        }
    }

    clearSearchForm() {
        this.clearFormData();
        this.hideGeneateVoucherForm();
        this.reset();
        this.currentPage = 1;
        this.searchVoucherForm.reset();
        this.getAllVouchers("");
    }
    clearSearchForm2() {
        this.currentPage = 1;
        this.searchSubmitted = false;
        this.getAll("");
        this.searchForm.reset();
    }
    async search() {

        if (!this.searchkey || this.searchkey !== this.searchForm.value.batchName) {
            this.currentPage = 1;
        }
        this.searchkey = this.searchForm.value.batchName;
        if (this.showItemPerPage) {
            this.itemsPerPage = this.showItemPerPage;
        }
        this.searchSubmitted = true;
        let name = this.searchForm.value.batchName ? this.searchForm.value.batchName : "";
        if (this.searchForm.valid) {
            this.VoucherBatchService.getByUserName(name, this.currentPage, this.itemsPerPage).subscribe(
                (response: any) => {
                    this.batchData = response.voucherbatch.data;
                    this.totalRecords = response.voucherbatch.totalRecords;
                },
                error => {
                    this.totalRecords = 0;
                    if (error.error.status == 404) {
                        this.batchData = [];
                        this.totalRecords = 0;
                        this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                    } else {
                        this.batchData = [];
                        this.totalRecords = 0;
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


                    }
                }
            );
        }
    }
    getAll(list) {
        let size;
        this.searchkey = "";
        let page = this.currentPage;
        if (list) {
            size = list;
            this.itemsPerPage = list;
        } else {
            size = this.itemsPerPage;
        }
        this.VoucherBatchService.getAll(page, size).subscribe(
            (response: any) => {


                this.batchData = response.voucherbatch.data;
                this.batchData2 = this.batchData


                this.totalRecords = response.voucherbatch.totalRecords;
            },
            (error: any) => {
                this.batchData = [];
                this.totalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                } else {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


                }
            }
        );
    }

    addVoucherBatch() {
        this.VoucherBatchForm.value.createdBy = this.loggedInUser;

        this.voucherConfigService.generateVoucherBatch(this.VoucherBatchForm.value).subscribe(
            response => {

                this.currentPage = 1;


                this.voucherGenerateForm.reset();


                this.dialogRef.close();


                this.activeTabIndex = 1;


                this.getAll("");

            },
            error => {
                if (error.error.status == 402) {
                    this.toastr.info(`${error.error.errorMessage}`, 'Info!');
                } else {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
                }
                this.currentPage = 1;
            }
        );
    }


    getDetailsByMVNO(mvnoId) {
        let allPlanList: Array<any> = this.planData.planList;
        this.filteredPlanList = [];
        if (mvnoId == RadiusConstants.SUPER_ADMIN_MVNO) {
            this.filteredPlanList = allPlanList;
        } else {
            this.filteredPlanList = allPlanList.filter(
                element => element.mvnoId == mvnoId || element.mvnoId == 1
            );
        }
    }
    changeToProfile() {
        this.showProfile = true;
    }
    onTabChange(event: any) {
        this.activeTabIndex = event.index;
        if (event.index === 0) {
            this.getAllVouchers("");
        }
        if (event.index === 1) {
            this.getAll("");
        }
        if (event.index === 2) {

            this.getAllVouchers1("");
        }


    }
    openAddVoucherDialog(edit: boolean = false, voucherData: any = null) {
        this.vourcharConfigForm.reset();
        this.dialogRef = this.dialog.open(this.VoucherDialogTemplate, {
            width: '1200px',
            data: { edit: edit, voucher: voucherData }
        });

        this.dialogRef.afterClosed().subscribe(result => {
            if (result === 'refresh') {
                this.getAllVouchers("");
            }
        });
    }
    switchToTab3(data: any) {

        this.activeTabIndex = 2;
        this.onTabChange({ index: 2 });
    }


    onCancel(): void {
        this.dialogRef.close();
    }


    searchVoucher1() {


        if (!this.searchkey || this.searchkey !== this.voucherSearchForm.value) {
            this.currentPage = 1;
            this.allIsChecked = false;
            this.isChecked = false;
        }
        this.searchkey = this.voucherSearchForm.value;
        if (this.showItemPerPage) {
            this.itemsPerPage = this.showItemPerPage;
        }


        let status = "";
        let batchName = "";
        if (
            this.voucherSearchForm.value.status1 != null &&
            this.voucherSearchForm.value.status1 != "null"
        ) {
            status = this.voucherSearchForm.value.status1;

        }
        if (
            this.voucherSearchForm.value.batchName != null &&
            this.voucherSearchForm.value.batchName != "null"
        ) {
            batchName = this.voucherSearchForm.value.batchName;
        }
        this.voucherService
            .findVouchers(
                batchName,
                status
            )
            .subscribe(
                (response: any) => {
                    this.voucherData = response.voucher.content;
                    this.totalElements = response.voucher.totalElements;
                    for (let index = 0; index < this.voucherData.length; index++) {
                        const voucher = this.voucherData[index];
                        this.batchNameSet.add(voucher.batchName);
                    }
                    this.totalBatchNameList = Array.from(this.batchNameSet);
                    this.isChecked = false;
                    this.allIDs = [];

                },
                (error: any) => {
                    if (error.error.status1 == 404) {
                        this.toastr.info(`${error.error.errorMessage}`, 'Info!');

                    } else {
                        this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                    }
                    this.voucherData = [];
                    this.totalElements = 0;
                }
            );
    }

    clearSearchForm3() {
        this.batchId = null;
        this.currentPage = 1;
        this.voucherSearchForm.reset();
        this.voucherSearchForm.patchValue({
            status1: "null"
        });
        this.getAllVouchers1("");
        this.voucherSearchForm.reset();
    }
    getAllVouchers1(list) {
        this.searchkey = "";
        let page = this.currentPage;
        let size = list ? list : this.itemsPerPage;

        this.voucherService.getAllVouchers(page, size).subscribe(
            (response: any) => {
                this.voucherData = response.voucher.content;
                this.totalElements = response.voucher.totalElements;

            },
            (error) => {
                if (error.error.status1 === 404) {
                    this.toastr.info(`${error.error.errorMessage}`, 'Info!');
                } else {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
                }
                this.totalElements = 0;
                this.voucherData = [];
            }
        );
    }
    async exportExcel() {
        let batchName = this.voucherSearchForm.controls.batchName.value
            ? this.voucherSearchForm.controls.batchName.value
            : "";
        let status = this.voucherSearchForm.controls.status1.value
            ? this.voucherSearchForm.controls.status1.value
            : "";
        this.voucherService.getDataTOExport(batchName, status).subscribe(
            (res: any) => {
                const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(res.dataToExport);
                const wb: XLSX.WorkBook = XLSX.utils.book_new();
                XLSX.utils.book_append_sheet(wb, ws, batchName);
                XLSX.writeFile(wb, batchName ? batchName + ".xlsx" : "Vouchers" + ".xlsx");
            },
            (error: any) => {
                if (error.error.status == 400) {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                }
            }
        );
    }

}
