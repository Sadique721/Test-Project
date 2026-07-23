import { Component, OnInit, AfterViewInit, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, UntypedFormArray } from "@angular/forms";
import { MessageService } from "primeng/api";
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { DunningrulesService } from "src/app/service/dunningrules.service";
import { Regex } from "src/app/constants/regex";
import { DunningManagement } from "src/app/components/model/dunning-managements";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import * as _ from "lodash";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { Observable, Observer, forkJoin } from "rxjs";
import { error } from "console";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { DUNNINGS } from "src/app/constants/aclConstants";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { ToastrService } from "ngx-toastr";

@Component({
    selector: "app-dunning-rules",
    templateUrl: "./dunning-rules.component.html",
    styleUrls: ["./dunning-rules.component.css"],
    standalone: false
})
export class DunningRulesComponent implements OnInit {
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild('createEditDialog') createEditDialog: TemplateRef<any>;
    @ViewChild('detailDialog') detailDialog: TemplateRef<any>;
    dialogRef: MatDialogRef<any>;

    displayedColumns: string[] = [
        'id',
        'name',
        'ccemail',
        'mobile',
        'creditclass',
        'status',
        'mvnoName',
        'action'
    ];
    dunningRuleFormGroup: UntypedFormGroup;

    dataSource: MatTableDataSource<any> = new MatTableDataSource<any>();
    dunningGroupForm: UntypedFormGroup;
    dunningCategoryList: any;
    submitted: boolean = false;
    isBranchAvailable = false;
    taxListData: any;
    createdunningData: DunningManagement;
    currentPagedunningListdata = 1;
    dunningListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    dunningListdatatotalRecords: number;
    dunningListData: any = [];
    viewdunningListData: any = [];
    searchDunningRule: any = "";
    searchData: any;
    isdunningEdit: boolean = false;
    dunningtype = "";
    dunningcategory = "";
    searchdunningUrl: any;
    editMode: boolean = false;

    serviceData: any;
    qosPolicyData: any;
    quotaData: any;
    quotaTypeData: any;
    chargeCategoryList: any;
    isPlanEdit: boolean = false;
    viewPlanListData: any;

    chargeFromArray: UntypedFormArray;
    chargeitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    chargetotalRecords: number;
    currentPageCharge = 1;
    selectvalue = "";
    CreditclassData = [{ value: "Gold" }, { value: "Silver" }, { value: "Platinum" }];

    temp = [];
    dunningListData1: any;
    dunningListDataselector: any;
    dunningRulelength = 0;

    dunningRulefromgroup: UntypedFormGroup;
    dunningSubmitted: boolean = false;

    dunningRuleItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    dunningRuletotalRecords: number;
    currentPagedunningRuleList = 1;
    createView: boolean = false;
    listView: boolean = true;

    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 1;
    searchkey: any = [];
    totalDataListLength = 0;

    dunningRoleAction = [
        { label: "Email", value: "Email" },
        { label: "SMS", value: "SMS" },
        { label: "DeActivation", value: "DeActivation" },
    ];
    customerTypes = [
        { label: "Postpaid", value: "Postpaid" },
        { label: "Prepaid", value: "Prepaid" },
    ];
    dunningApplyTypes = [
        { label: "Partner", value: "Partner" },
        { label: "Customer", value: "Customer" },
    ];

    selectSMSvalue = [
        { label: "Yes", value: "Y" },
        { label: "No", value: "N" },
    ];
    selectemail = [
        { label: "Yes", value: "Y" },
        { label: "No", value: "N" },
    ];

    statusOptions = RadiusConstants.status;

    detailView: boolean = false;
    DunningruleActionlistData: any = [];
    AclClassConstants;
    AclConstants;
    public loginService: LoginService;
    dunningTypeList: any;
    isCustSubTypeCon: boolean = false;

    branchData = [];
    partnerListByServiceArea = [];
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    isDunningForMvno: boolean = false;
    loggedInUserMvnoId: any;
    changeFromArrayDataSource: MatTableDataSource<any> = new MatTableDataSource<any>()


    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private dialog: MatDialog, private toastr: ToastrService,
        private dunningManagementService: DunningrulesService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        loginService: LoginService,
        public commondropdownService: CommondropdownService
    ) {
        this.createAccess = loginService.hasPermission(DUNNINGS.DUNNING_RULES_CREATE);
        this.deleteAccess = loginService.hasPermission(DUNNINGS.DUNNING_RULES_DELETE);
        this.editAccess = loginService.hasPermission(DUNNINGS.DUNNING_RULES_EDIT);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;

        this.loggedInUserMvnoId = localStorage.getItem("mvnoId");

        if (this.loggedInUserMvnoId == 1) {
            this.dunningApplyTypes.push({ label: "Mvno", value: "Mvno" });
        }
    }

    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(DUNNINGS.DUNNING_RULES_EDIT) || this.loginService.hasPermission(DUNNINGS.DUNNING_RULES_DELETE)) {
            return [
                'id',
                'name',
                'ccemail',
                'mobile',
                'creditclass',
                'status',
                'mvnoName',
                'action'
            ];
        } else {
            return [
                'id',
                'name',
                'ccemail',
                'mobile',
                'creditclass',
                'status',
                'mvnoName'
            ];
        }
    }

    ngOnInit(): void {
        this.dunningGroupForm = this.fb.group({
            ccemail: ["", Validators.email],
            mobile: [""],
            creditclass: ["", Validators.required],
            delete: [""],
            id: [""],
            name: ["", Validators.required],
            status: ["", Validators.required],
            customerType: [""],
            dunningRuleActionPojoList: (this.chargeFromArray = this.fb.array([])),
            dunningType: ["", Validators.required],
            dunningFor: ["", Validators.required],
            dunningSubType: [""],
            dunningSector: [""],
            dunningSubSector: [""],
            customerPayType: ["", Validators.required],
            serviceAreaIds: ["", Validators.required],
            partnerIds: [""],
            branchIds: [""],
            isGeneratepaymentLink: [false],
        });
        this.dunningGroupForm.controls.dunningSubType.disable();
        this.dunningGroupForm.controls.dunningSubSector.disable();
        this.dunningRulefromgroup = this.fb.group({
            action: ["", Validators.required],
            days: ["", [Validators.required, Validators.pattern(Regex.numericWithNegative)]],
        });
        this.getdunningList();
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
        window.scroll(0, 0);
        this.getDunningType();
        this.commondropdownService.getCustomerType();
        this.commondropdownService.getSectorType();
        const serviceArea = localStorage.getItem("serviceArea");
        let serviceAreaArray = JSON.parse(serviceArea);
        if (serviceAreaArray.length !== 0) {
            this.commondropdownService.filterserviceAreaList();
        } else {
            this.commondropdownService.getserviceAreaList();
        }
        this.loggedInUserMvnoId = localStorage.getItem("mvnoId");
    }

    createDunning() {
        this.editMode = false;
        this.submitted = false;
        this.isDunningForMvno = false;
        this.dunningGroupForm.reset();
        this.chargeFromArray.clear();
        this.changeFromArrayDataSource.data = this.chargeFromArray.controls;

        this.dunningGroupForm.controls.dunningSubType.disable();
        this.dunningGroupForm.controls.dunningSubSector.disable();

        this.dialogRef = this.dialog.open(this.createEditDialog, {
            width: '1100px',
            data: { isEdit: false }
        });

        this.dialogRef.afterClosed().subscribe(result => {
            if (result === 'saved' || result === 'cancelled') {
                this.getdunningList();
            }
        });
    }


    selServiceArea(serAreaId) {
        if (serAreaId != null && serAreaId.length > 0) {
            this.getBranchByServiceAreaID(serAreaId);
            this.getPartnerAllByServiceArea(serAreaId);
        } else {
            this.branchData = [];
            this.partnerListByServiceArea = [];
            this.dunningGroupForm.controls.branchIds.reset();
            this.dunningGroupForm.controls.partnerIds.reset();
        }
    }
    getBranchByServiceAreaID(ids) {
        let data = [];
        let url = "/branchManagement/getAllBranchesByServiceAreaId";
        this.savbillCommonBaseService.post(url, ids).subscribe(
            (response: any) => {
                this.branchData = response.dataList;
                if (this.isBranchAvailable && this.branchData != null && this.branchData.length > 0) {

                    this.dunningGroupForm.controls.branchIds.setValidators(Validators.required);
                } else {

                    this.dunningGroupForm.controls.branchIds.clearValidators();

                }
                this.dunningGroupForm.controls.branchIds.updateValueAndValidity();
            },
            error => { }
        );
    }

    getPartnerAllByServiceArea(serviceAreaId) {
        const url = "/getPartnerByServiceAreaIds/" + serviceAreaId;
        this.commondropdownService.getMethod(url).subscribe(
            (response: any) => {
                this.partnerListByServiceArea = response.partnerList.filter(item => item.id != 1);
                if (
                    !this.isBranchAvailable &&
                    this.partnerListByServiceArea != null &&
                    this.partnerListByServiceArea.length > 0
                ) {
                    this.dunningGroupForm.controls.partnerIds.setValidators(Validators.required);
                } else {
                    this.dunningGroupForm.controls.partnerIds.clearValidators();
                }
                this.dunningGroupForm.controls.partnerIds.updateValueAndValidity();
            },
            (error: any) => { }
        );
    }

    listDunning() {
        this.createView = false;
        this.listView = true;
        this.detailView = false;
    }

    dunningDeatils() {
        this.listView = false;
        this.createView = false;
        this.detailView = false;
    }

    selectActionChange(_event: any) {
        this.selectvalue = _event.value;
    }

    createChargeFormGroup(): UntypedFormGroup {
        return this.fb.group({
            action: [this.dunningRulefromgroup.value.action],
            days: [this.dunningRulefromgroup.value.days],
            dunningRuleId: [""],
            id: [""],
        });
    }

    onAddChargeField() {
        this.dunningSubmitted = true;
        Object.keys(this.dunningRulefromgroup.controls).forEach(key => {
            const control = this.dunningRulefromgroup.get(key);
            control?.markAsTouched();
            control?.markAsDirty();
        });
        if (this.dunningRulefromgroup.valid) {
            this.chargeFromArray.push(this.createChargeFormGroup());
            this.changeFromArrayDataSource.data = this.chargeFromArray.controls;

            this.dunningRulefromgroup.patchValue({
                action: '',
                days: ''
            });
            Object.keys(this.dunningRulefromgroup.controls).forEach(key => {
                const control = this.dunningRulefromgroup.get(key);
                control?.setErrors(null);
                control?.markAsPristine();
                control?.markAsUntouched();
            });

            this.dunningSubmitted = false;
        } else {
        }
    }


    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPagedunningListdata > 1) {
            this.currentPagedunningListdata = 1;
        }
        if (!this.searchkey) {
            this.getdunningList(this.showItemPerPage);
        } else {
            this.searchdunning();
        }
    }

    getdunningList(pageSize?: number) {
        if (pageSize) {
            this.dunningListdataitemsPerPage = pageSize;
        }
        const data = {
            page: this.currentPagedunningListdata,
            pageSize: this.dunningListdataitemsPerPage,
        };

        this.dunningManagementService.getDunningRuleList(data).subscribe(
            (response: any) => {
                this.dunningListData = response.dunningRuleList.content;
                this.dunningListdatatotalRecords = response.pageDetails.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }


    addEditdunning(dunningId) {
        this.submitted = true;

        this.dunningSubmitted = true;
        this.dunningGroupForm.markAllAsTouched();

        if (this.dunningGroupForm.valid) {
            if (dunningId) {
                const url = "/dunningrule/" + dunningId;
                this.createdunningData = this.dunningGroupForm.value;
                this.createdunningData.delete = false;
                this.dunningManagementService.updateMethod(url, this.createdunningData).subscribe(
                    (response: any) => {
                        if (this.dialogRef) {
                            this.dialogRef.close('saved');
                        }
                        this.submitted = false;
                        this.isDunningForMvno = false;
                        this.customerFormReset();
                        this.dunningGroupForm.controls.dunningSubType.disable();
                        this.dunningGroupForm.controls.dunningSubSector.disable();
                        this.branchData = [];
                        this.partnerListByServiceArea = [];
                        this.isdunningEdit = false;
                        this.listView = true;
                        this.viewdunningListData = [];
                        this.chargeFromArray.controls = [];
                        this.toastr.success(`Successfully Updated`, "Successfully ");
                        this.createView = false;
                        this.detailView = false;
                        if (!this.searchkey) {
                            this.getdunningList();
                        } else {
                            this.searchdunning();
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    }
                );
            } else {
                const url = "/dunningrule";
                this.createdunningData = this.dunningGroupForm.value;
                this.createdunningData.delete = false;

                this.dunningManagementService.postMethod(url, this.createdunningData).subscribe(
                    (response: any) => {
                        this.submitted = false;
                        this.chargeFromArray.controls = [];
                        this.customerFormReset();
                        this.isDunningForMvno = false;
                        this.dunningGroupForm.controls.dunningSubType.disable();
                        this.dunningGroupForm.controls.dunningSubSector.disable();
                        this.toastr.success(`Successfully Created!`, "Successfull");
                        if (this.dialogRef) {
                            this.dialogRef.close('saved');
                        }
                        this.listView = true;
                        this.createView = false;
                        this.detailView = false;
                        if (!this.searchkey) {
                            this.getdunningList();
                        } else {
                            this.searchdunning();
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    }
                );
            }
        }
    }


    editdunning(id: any) {
        this.editMode = true;
        this.submitted = false;
        this.isdunningEdit = true;
        this.viewdunningListData = [];
        this.dunningRulelength = 0;

        const url = "/dunningrule/" + id;
        this.dunningManagementService.getMethod(url).subscribe(
            async (response: any) => {
                const data = response.dunningRuleListById;
                this.dunningGroupForm.patchValue(data);
                this.chargeFromArray = this.dunningGroupForm.get('dunningRuleActionPojoList') as UntypedFormArray;

                // Clear existing controls before adding new ones
                this.chargeFromArray.clear();
                if (data.dunningRuleActionPojoList && data.dunningRuleActionPojoList.length) {
                    data.dunningRuleActionPojoList.forEach(item => {
                        this.chargeFromArray.push(this.fb.group({
                            action: [item.action, Validators.required],
                            days: [item.days, [Validators.required, Validators.pattern(Regex.numericWithNegative)]],
                            id: [item.id || ''],
                            dunningRuleId: [item.dunningRuleId || '']
                        }));
                    });
                    this.chargeFromArray.clear();

                    // this.clearChargeFromArrayValidators();
                }
                this.changeFromArrayDataSource.data = this.chargeFromArray.controls;
                const dunningForEvent = { value: data.dunningFor };
                this.getDunningTypeFor(dunningForEvent);
                await this.getBranchByServiceAreaID(data.serviceAreaIds);
                this.viewdunningListData = response.dunningRuleListById;
                this.dunningGroupForm.patchValue(this.viewdunningListData);
                while (this.dunningRulelength < this.viewdunningListData.dunningRuleActionPojoList.length) {
                    // while () {
                    this.dunningRulefromgroup.patchValue(
                        this.viewdunningListData.dunningRuleActionPojoList[this.dunningRulelength]
                    );
                    this.onAddChargeField();
                    this.chargeFromArray.patchValue(this.viewdunningListData.dunningRuleActionPojoList);
                    // }
                    this.dunningRulelength++;
                }
                if (this.viewdunningListData.customerType != null) {
                    const data = {
                        value: this.viewdunningListData.customerType,
                    };
                    this.dunningGroupForm.controls.dunningSubType.enable();
                    this.getCustSubType(data);
                } else {
                    this.dunningGroupForm.controls.dunningSubType.disable();
                }
                if (this.viewdunningListData.dunningSector != null) {
                    this.dunningGroupForm.controls.dunningSubSector.enable();
                } else {
                    this.dunningGroupForm.controls.dunningSubSector.disable();
                }

                this.dialogRef = this.dialog.open(this.createEditDialog, {
                    width: '1100px',
                    data: { isEdit: true, dunningId: id }
                });

                this.dialogRef.afterClosed().subscribe(result => {
                    this.getdunningList();
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    deletedunning(dunningId) {
        const url = "/dunningrule/" + dunningId;
        this.dunningManagementService.deleteMethod(url).subscribe(
            (response: any) => {
                if (this.currentPagedunningListdata != 1 && this.totalDataListLength == 1) {
                    this.currentPagedunningListdata = this.currentPagedunningListdata - 1;
                }
                this.toastr.success(`Successfully Deleted!`, "Successfull");
                if (!this.searchkey) {
                    this.getdunningList();
                } else {
                    this.searchdunning();
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    pageChangeddunningList(pageEvent: PageEvent) {
        this.currentPagedunningListdata = pageEvent.pageIndex + 1;
        this.dunningListdataitemsPerPage = pageEvent.pageSize;
        if (!this.searchkey) {
            this.getdunningList(this.dunningListdataitemsPerPage);
        } else {
            this.searchdunning();
        }
    }


    pageChangedCharge(pageNumber) {
        this.currentPageCharge = pageNumber;
    }

    deleteConfirmonChargeField(chargeFieldIndex: number, chargeFieldId: number) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete Action',
                description: `Are you sure you want to delete this action?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.onRemoveCharge(chargeFieldIndex, chargeFieldId);
            }
        });
    }

    async onRemoveCharge(chargeFieldIndex: number, chargeFieldId: number) {
        this.chargeFromArray.removeAt(chargeFieldIndex);
        this.changeFromArrayDataSource.data = this.chargeFromArray.controls; // Update datasource to reflect UI
    }



    searchdunning() {
        if (!this.searchkey || this.searchkey != this.searchDunningRule) {
            this.currentPagedunningListdata = 1;
        }
        this.searchkey = this.searchDunningRule;
        if (this.showItemPerPage) {
            this.dunningListdataitemsPerPage;
        }
        this.searchData.filters[0].filterValue = this.searchDunningRule;
        this.searchData.page = this.currentPagedunningListdata;
        this.searchData.pageSize = this.dunningListdataitemsPerPage;

        this.dunningListData = [];
        this.dunningManagementService.searchDunningRule(this.searchData).subscribe(
            (response: any) => {
                this.dunningListData = response.dunningRuleList.content;
                this.dunningListdatatotalRecords = response.pageDetails.totalRecords;
            },
            error => {
                this.dunningListdatatotalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(error.responseMessage, 'Info!');
                } else {
                    this.toastr.error(`No Record Found`, 'Failed!');
                }
            }
        );
    }

    clearSearchDunning() {
        this.getdunningList();

        this.searchDunningRule = "";
    }

    dunningRuleDetails(data: any) {
        const url = "/dunningrule/" + data.id;
        this.DunningruleActionlistData = [];
        this.dunningManagementService.getMethod(url).subscribe(async (response: any) => {
            this.DunningruleActionlistData = response.dunningRuleListById;
            await this.getBranchByServiceAreaID(response.dunningRuleListById.serviceAreaIds);
            await this.getPartnerAllByServiceArea(response.dunningRuleListById.serviceAreaIds);

            this.dialogRef = this.dialog.open(this.detailDialog, {
                width: '1000px',
                data: { details: this.DunningruleActionlistData }
            });

            this.dialogRef.afterClosed().subscribe(() => {
                this.getdunningList();
            });
        });
    }

    deleteConfirmDunningDialog(dunning: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete Dunning Rule',
                description: `Are you sure you want to delete "${dunning.name}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deletedunning(dunning.id);
            }
        });
    }


    onCancel(): void {

        if (this.dialogRef) {
            this.dialogRef.close();
        }
    }

    pageChangedDunningRuleList(pageNumber) {
        this.currentPagedunningRuleList = pageNumber;
    }

    customerFormReset() {
        this.dunningGroupForm.reset();
        this.chargeFromArray = this.dunningGroupForm.get('dunningRuleActionPojoList') as UntypedFormArray;
        this.chargeFromArray.clear();
        this.changeFromArrayDataSource.data = this.chargeFromArray.controls;
        this.dunningGroupForm.controls.ccemail.setValue("");
        this.dunningGroupForm.controls.mobile.setValue("");
        // this.dunningRulefromgroup.reset();
        // this.dunningRulefromgroup.controls.action.setValue("");
        // this.dunningRulefromgroup.controls.days.setValue("");
    }

    getDunningType() {
        const url = "/commonList/dunningType";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.dunningTypeList = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    getCustSubType(event) {
        this.dunningGroupForm.controls.dunningSubType.enable();
        let value = event.value;
        if (event.value == "Barter") {
            this.isCustSubTypeCon = false;
        } else {
            this.isCustSubTypeCon = true;
            this.commondropdownService.getCustomerSubType(value);
        }
    }

    getSectSubType(event) {
        const value = event.value;
        if (value) {
            this.dunningGroupForm.controls.dunningSubSector.enable();
        } else {
            this.dunningGroupForm.controls.dunningSubSector.disable();
        }
    }
    canExit() {
        if (!this.dunningGroupForm.dirty && !this.dunningRulefromgroup.dirty) return true;
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
    dunningTypeListByDropdown: any = [];
    getDunningTypeFor(event) {
        const url = "/commonList/dunningType";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.dunningTypeList = response.dataList;
                if (event.value === "Customer") {
                    this.isDunningForMvno = false;
                    this.dunningTypeListByDropdown = [];
                    this.dunningTypeListByDropdown = this.dunningTypeList.filter(
                        m =>
                            m.value !== "PartnerDocument" &&
                            m.value !== "MvnoDocument" &&
                            m.value !== "MvnoPayment" &&
                            m.value !== "MVNOAdvanceNotification"
                    );
                    this.isBranchAvailable = true;
                    this.dunningGroupForm.controls.branchIds.setValidators(Validators.required);
                    this.dunningGroupForm.controls.partnerIds.clearValidators();
                    this.dunningGroupForm.controls.branchIds.updateValueAndValidity();

                    this.dunningGroupForm.controls.serviceAreaIds.setValidators(Validators.required);
                    this.dunningGroupForm.controls.serviceAreaIds.updateValueAndValidity();
                    this.dunningGroupForm.controls.customerPayType.setValidators(Validators.required);
                    this.dunningGroupForm.controls.customerPayType.updateValueAndValidity();
                    this.dunningGroupForm.controls.creditclass.setValidators(Validators.required);
                    this.dunningGroupForm.controls.creditclass.updateValueAndValidity();
                }
                if (event.value === "Partner") {
                    this.isDunningForMvno = false;
                    this.dunningTypeListByDropdown = [];
                    this.dunningTypeListByDropdown = this.dunningTypeList.filter(
                        m => m.value === "PartnerDocument"
                    );
                    this.isBranchAvailable = false;
                    this.dunningGroupForm.controls.partnerIds.setValidators(Validators.required);
                    this.dunningGroupForm.controls.branchIds.clearValidators();
                    this.dunningGroupForm.controls.branchIds.updateValueAndValidity();

                    this.dunningGroupForm.controls.serviceAreaIds.setValidators(Validators.required);
                    this.dunningGroupForm.controls.serviceAreaIds.updateValueAndValidity();
                    this.dunningGroupForm.controls.customerPayType.setValidators(Validators.required);
                    this.dunningGroupForm.controls.customerPayType.updateValueAndValidity();
                    this.dunningGroupForm.controls.creditclass.setValidators(Validators.required);
                    this.dunningGroupForm.controls.creditclass.updateValueAndValidity();
                }
                if (event.value === "Mvno") {
                    this.isDunningForMvno = true;
                    this.isBranchAvailable = false;
                    this.dunningTypeListByDropdown = [];
                    this.dunningTypeListByDropdown = this.dunningTypeList.filter(
                        m =>
                            m.value === "MvnoDocument" ||
                            m.value === "MvnoPayment" ||
                            m.value === "MVNOAdvanceNotification"
                    );
                    this.dunningGroupForm.controls.partnerIds.clearValidators();
                    this.dunningGroupForm.controls.partnerIds.updateValueAndValidity();
                    this.dunningGroupForm.controls.branchIds.clearValidators();
                    this.dunningGroupForm.controls.branchIds.updateValueAndValidity();
                    this.dunningGroupForm.controls.serviceAreaIds.clearValidators();
                    this.dunningGroupForm.controls.serviceAreaIds.updateValueAndValidity();
                    this.dunningGroupForm.controls.customerPayType.clearValidators();
                    this.dunningGroupForm.controls.customerPayType.updateValueAndValidity();
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    dunningEventList: any = [];
    getDunningEvent(event) {
        if (event.value == "AdvanceNotification") {
            this.dunningEventList = [];
            this.dunningRoleAction = this.dunningRoleAction;
            this.dunningEventList = this.dunningRoleAction.filter(m => m.label != "DeActivation");
        } else if (event.value == "MVNOAdvanceNotification") {
            this.dunningEventList = [];
            this.dunningRoleAction = this.dunningRoleAction;
            this.dunningEventList = this.dunningRoleAction.filter(m => m.label != "DeActivation");
        } else {
            this.dunningEventList = this.dunningRoleAction;
        }
    }
}
