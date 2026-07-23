import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { FormBuilder, Validators, FormGroup, FormControl, FormArray } from "@angular/forms";
import { ToastrService } from "ngx-toastr";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { PlanBundleService } from "src/app/service/plan-bundle.service";
import { Regex } from "src/app/constants/regex";
import { PlanBundle } from "src/app/components/model/planBundle";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import * as _ from "lodash";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { element } from "protractor";
import { Observable, Observer, of } from "rxjs";
import { PARTNERS, PRODUCTS } from "src/app/constants/aclConstants";
import { PartnerService } from "src/app/service/partner.service";
import { MatDialog } from "@angular/material/dialog";
import { MatTable } from "@angular/material/table";
import { PageEvent } from "@angular/material/paginator";

@Component({
    selector: "app-plan-bundle",
    templateUrl: "./plan-bundle.component.html",
    styleUrls: ["./plan-bundle.component.css"], standalone: false
})
export class PlanBundleComponent implements OnInit {
    planBundleGroupForm: FormGroup;
    planBundleCategoryList: any;
    submitted: boolean = false;
    createplanBundleData: PlanBundle;
    currentpage = 1;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    totalRecords: number = 0;
    planBundleListData: any = [];
    viewplanBundleListData: any = [];
    isplanBundleEdit: boolean = false;
    planBundletype = "";
    planBundlecategory = "";
    searchplanBundleUrl: any;
    title = "Plan Bundle";
    chargeCategoryList: any;
    viewPlanListData: any;
    parenetpartnerList: any = [];

    serviceCommonFromGroup: FormGroup;
    serviceCommonListFromArray: FormArray;
    paricebookListFromArray: FormArray;
    paricebookSlabListFromArray: FormArray;
    chargeitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    chargetotalRecords: any;
    currentPageCharge = 1;
    selectvalue = "";
    searchName: any = "";
    searchData: any;
    planTypeDataFromgroup: FormGroup;

    temp = [];
    planBundleListData1: any;
    priceBooklistlength = 0;

    PlanDetailListfromgroup: FormGroup;
    planBundleSubmitted: boolean = false;

    slabListListfromgroup: FormGroup;
    slabSubmitted: boolean = false;

    planBundleRuleItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    planBundleRuletotalRecords: String;
    currentPageplanBundleRuleList = 1;

    serviceitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    servicetotalRecords: String;
    currentPageservice = 1;

    listView: boolean = true;
    createView: boolean = false;
    planBundleDeatilsShow: boolean = true;

    planBundleActinDatashow: boolean = false;
    planBundlelistDeatils: any;

    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 0;
    searchkey: string;
    totalPlanBundleListLength = 0;
    showItemPlanMappingPerPage = 0;

    registration: any;
    renewal: any;
    revsharen: any;
    planAmountshow: boolean = false;
    statusOptions = RadiusConstants.status;
    PlanBundleType = [
        { label: "Revenue Share", value: "RevenueShare" },
        { label: "Fixed", value: "Fixed" },
        { label: "Token Amount", value: "TokenAmount" },
    ];
    serviceCommisionSubmitted: boolean = false;
    plantypeName: any;
    AclClassConstants;
    AclConstants;
    public loginService: LoginService;
    planGroupData: any;
    planListData: any;
    planListMasterData: any;
    planGroupdata: any;
    planGroupMasterData: any;
    revenueType: any = ["Percentage", "Slab"];
    slabFlag: boolean = false;

    currentPageTaxSlab = 1;
    TaxSlabitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    TaxSlabtotalRecords: String;

    commisionData = [{ label: "Plan level" }, { label: "Service level" }];
    planServiceList: any = [];
    planGroupFieldFromGroup: FormGroup;
    planGrouptype: any = "";
    planSelection: boolean = true;
    allSelection: any = [];
    planDetailsCategory = [
        { label: "Individual", value: "individual" },
        { label: "Plan Group", value: "groupPlan" },
    ];
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    partnerListData: any[];
    selectedPartner: any;
    parentpartnerDetails: any;
    priceBookSlabDetailsList: any;
    servicelevelDetails: any;
    isParentPartnerSelected: boolean = false;
    selectedParentPartner: any;
    selectedServiceData: any;

    constructor(
        private fb: FormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private toastr: ToastrService,
        private planBundleService: PlanBundleService,
        private partnerService: PartnerService,
        public commondropdownService: CommondropdownService, private dialog: MatDialog,
        loginService: LoginService
    ) {
        this.createAccess = loginService.hasPermission(PARTNERS.PARTNER_BUNDLE_CREATE);
        this.deleteAccess = loginService.hasPermission(PARTNERS.PARTNER_BUNDLE_DELETE);
        this.editAccess = loginService.hasPermission(PARTNERS.PARTNER_BUNDLE_EDIT);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(PARTNERS.PARTNER_BUNDLE_CREATE) || this.loginService.hasPermission(PARTNERS.PARTNER_BUNDLE_EDIT) || this.loginService.hasPermission(PARTNERS.PARTNER_BUNDLE_DELETE)) {
            return ['planid', 'name', 'createdate', 'status', 'action'];
        } else {
            return ['planid', 'name', 'createdate', 'status'];
        }
    }
    ngOnInit(): void {
        this.planBundleGroupForm = this.fb.group({
            bookname: ["", Validators.required],
            agrPercentage: ["", [Validators.required, Validators.min(0), Validators.max(100)]],
            tdsPercentage: ["", [Validators.required, Validators.min(0), Validators.max(100)]],
            revenueType: ["", Validators.required],
            description: ["", [Validators.required, Validators.pattern(Regex.character200)]],
            id: [""],
            isDeleted: [""],
            status: ["", Validators.required],
            validFromString: [""],
            partnerId: [""],
            validToString: [""],
            validfrom: [""],
            validto: [""],
            allSelection: [[]],
            isAllPlanSelected: [false],
            isAllPlanGroupSelected: [false],
            revenueSharePercentage: ["", [Validators.min(-100), Validators.max(100)]],
            commission_on: ["Plan level", Validators.required],
            priceBookPlanDetailList: (this.paricebookListFromArray = this.fb.array([])),
            priceBookSlabDetailsList: (this.paricebookSlabListFromArray = this.fb.array([])),
            planCategory: ["individual"],
            serviceCommissionList: (this.serviceCommonListFromArray = this.fb.array([])),
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

        this.planGroupFieldFromGroup = this.fb.group({
            planGroup: [""],
        });

        this.serviceCommonFromGroup = this.fb.group({
            serviceId: ['', Validators.required],
            revenue_share_percentage: ["", [Validators.required, Validators.min(0), Validators.max(100)]],
            royaltyPercentage: ["", [Validators.min(0), Validators.max(100)]],
        });

        this.slabListListfromgroup = this.fb.group({
            commissionAmount: ["", Validators.required],
            fromRange: ["", Validators.required],
            id: [""],
            toRange: ["", Validators.required],
        });

        this.PlanDetailListfromgroup = this.fb.group({
            id: [""],
            offerprice: ["", Validators.required],
            isTaxIncluded: [""],
            revenueSharePercentage: ["", [Validators.min(-100), Validators.max(100)]],
            postpaidPlan: this.fb.group({
                id: ["", Validators.required],
            }),
        });

        this.planTypeDataFromgroup = this.fb.group({
            type: ["RevenueShare", Validators.required],
        });

        this.getplanBundleList("");
        this.commondropdownService.getPostpaidplanData();
        this.commondropdownService.getplanservice();
        this.findAllplanGroups();
        this.getPlanGroup();
        window.scroll(0, 0);
        this.getParenetPartnerList();
        this.setPricebookColumns();
    }

    findAllplanGroups() {
        let url = '/planGroupMappings?mode=""';
        this.commondropdownService.getMethod(url).subscribe(
            (response: any) => {
                this.planGroupdata = response.planGroupList.filter(data => data.plantype === "Prepaid");
                this.planGroupMasterData = response.planGroupList.filter(
                    data => data.plantype === "Prepaid"
                );
            },
            error => { }
        );
    }

    getParenetPartnerList() {
        const url = "/partner/all";
        this.partnerListData = [];
        this.partnerService.getMethodNew(url).subscribe((response: any) => {
            this.parenetpartnerList = response.partnerlist.filter(
                partner => partner.status === "ACTIVE" && partner.partnerType === "Franchise"
            );
        });
    }

    onChangePartner(_event: any, dd: any) {
        this.paricebookListFromArray = this.fb.array([]);
        this.isParentPartnerSelected = true;
        this.getParentPartnerData(_event.value, dd);
    }

    getParentPartnerData(id: any, dropDowndata: any) {
        let priceBookId = dropDowndata.selectedOption.pricebookId;
        var parentPartner = dropDowndata.selectedOption;
        const url = "/priceBook/" + priceBookId;

        this.planBundleService.getMethod(url).subscribe(
            (response: any) => {
                this.parentpartnerDetails = response.data;

                if (this.parentpartnerDetails.commission_on === "Plan level") {
                    if (this.planBundleGroupForm.value.planCategory === "individual") {
                        if (this.parentpartnerDetails.isAllPlanSelected) {
                            this.planListData = this.planListMasterData.filter(plan => {
                                return parentPartner.serviceAreaNameList.some(serviceName =>
                                    plan.serviceAreaNameList.some(service => service.name === serviceName)
                                );
                            });
                        } else {
                            this.planListData = [];
                            this.parentpartnerDetails.priceBookPlanDetailList
                                .filter(planDetails => planDetails.planGroup == null)
                                .map(plan => this.planListData.push(plan.postpaidPlan));
                        }
                    } else {
                        if (this.parentpartnerDetails.isAllPlanGroupSelected) {
                            this.planGroupdata = this.planGroupMasterData.filter(plan => {
                                return parentPartner.serviceAreaIds.some(serviceId =>
                                    plan.servicearea.includes(serviceId)
                                );
                            });
                        } else {
                            this.planGroupdata = [];
                            this.parentpartnerDetails.priceBookPlanDetailList
                                .filter(planDetails => planDetails.planGroup != null)
                                .map(plan => this.planGroupdata.push(plan.planGroup));
                        }
                    }
                } else {
                    this.planserviceData = this.planserviceCopyData.filter(item =>
                        this.parentpartnerDetails.serviceCommissionList.some(
                            commission => commission.serviceId === item.id
                        )
                    );

                }

                this.planBundleGroupForm.patchValue({
                    agrPercentage: Number(this.parentpartnerDetails.agrPercentage),
                    tdsPercentage: Number(this.parentpartnerDetails.tdsPercentage),
                    revenueType: this.parentpartnerDetails.revenueType,
                    commission_on: this.parentpartnerDetails.commission_on,
                });
            },
            (error: any) => { }
        );
    }

    createPlan() {
        this.dialog.open(this.AddEditDailog, {
            width: "80%",
            disableClose: true,
        });
        this.listView = false;
        this.createView = true;
        this.planBundleDeatilsShow = false;
        this.planBundleActinDatashow = false;
        this.submitted = false;
        this.serviceCommisionSubmitted = false;
        this.planBundleSubmitted = false;
        this.slabSubmitted = false;
        this.planBundleGroupForm.reset();
        this.planTypeDataFromgroup.reset();
        this.slabListListfromgroup.reset();
        this.serviceCommonFromGroup.reset();
        this.serviceCommonListFromArray.controls = [];
        this.paricebookListFromArray.controls = [];
        this.paricebookSlabListFromArray.controls = [];
        this.planAmountshow = false;
        this.isplanBundleEdit = false;
        this.serviceCommisionSubmitted = false;
        this.planTypeDataFromgroup.controls.type.setValue("RevenueShare");
        this.planBundleGroupForm.patchValue({
            commission_on: "Plan level",
            planCategory: "individual",
            isAllPlanSelected: false,
            isAllPlanGroupSelected: false,
        });
        this.planGroupFieldFromGroup.patchValue({
            planGroup: "All",
        });
        this.planListData = [];
        this.getplanservice();
        this.getPlanListbyGroup("All");
    }

    listPlan() {
        this.listView = true;
        this.createView = false;
        this.planBundleDeatilsShow = true;
        this.planBundleActinDatashow = false;
    }

    planBundleDeatils() {
        this.planBundleDeatilsShow = true;
        this.listView = true;
        this.createView = false;
        this.planBundleActinDatashow = false;
    }

    createServiceCommissionFormGroup(): FormGroup {
        return this.fb.group({
            id: [""],
            serviceId: [this.serviceCommonFromGroup.value.serviceId],
            serviceName: [this.selectedServiceData?.name],
            revenue_share_percentage: [this.serviceCommonFromGroup.value.revenue_share_percentage],
            royaltyPercentage: [this.serviceCommonFromGroup.value.royaltyPercentage],
        });
    }

    onAddServiceCommissionField() {
        this.serviceCommisionSubmitted = true;

        if (this.serviceCommonFromGroup.valid) {

            this.serviceSelectList.push(this.serviceSelectID);


            this.serviceCommonListFromArray.push(
                this.createServiceCommissionFormGroup()
            );

            this.serviceCommonFromGroup.reset();
            this.serviceCommisionSubmitted = false;


            this.planserviceData = this.planserviceData.filter(
                s => s.id !== this.serviceSelectID
            );

            this.serviceTable.renderRows();
        }
    }


    createChargeFormGroup(): FormGroup {
        if (this.PlanDetailListfromgroup.value.isTaxIncluded == null) {
            this.PlanDetailListfromgroup.value.isTaxIncluded = true;
        }
        if (this.planBundleGroupForm.value.planCategory == "groupPlan") {
        }
        if (this.planBundleGroupForm.value.planCategory == "individual") {
            return this.fb.group({
                id: [""],
                isDeleted: [false],
                offerprice: [this.PlanDetailListfromgroup.value.offerprice],
                partnerofficeprice: [0],
                revenueSharePercentage: [this.PlanDetailListfromgroup.value.revenueSharePercentage],
                isTaxIncluded: [true],
                registration: [this.registration],
                renewal: [this.renewal],
                revsharen: [this.revsharen],
                postpaidPlan: this.fb.group({
                    id: [this.PlanDetailListfromgroup.value.postpaidPlan.id],
                }),
            });
        } else {
            return this.fb.group({
                id: [""],
                isDeleted: [false],
                offerprice: [this.PlanDetailListfromgroup.value.offerprice],
                partnerofficeprice: [0],
                revenueSharePercentage: [this.PlanDetailListfromgroup.value.revenueSharePercentage],
                isTaxIncluded: [true],
                registration: [this.registration],
                renewal: [this.renewal],
                revsharen: [this.revsharen],
                planGroup: this.fb.group({
                    planGroupId: [this.PlanDetailListfromgroup.value.postpaidPlan.id],
                }),
            });
        }
    }

    onAddChargeField() {
        this.planBundleSubmitted = true;
        if (this.PlanDetailListfromgroup.valid) {
            this.paricebookListFromArray.push(this.createChargeFormGroup());
            this.PlanDetailListfromgroup.reset();
            this.planBundleSubmitted = false;
            this.planAmountshow = false;
            this.planTable.renderRows()
        } else {
        }
    }

    setPricebookColumns() {
        const columns: string[] = ['plan', 'offerprice'];

        // condition for Revenue Share
        if (!this.slabFlag && this.planBundleGroupForm.value.commission_on !== 'Service level') {
            columns.push('revenueShare');
        }

        // delete hamesha last me
        columns.push('delete');

        this.pricebookListDisplay = columns;
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageplanBundleRuleList > 1) {
            this.currentPageplanBundleRuleList = 1;
        }
        if (!this.searchkey) {
            this.getplanBundleList(this.showItemPerPage);
        }
    }

    getplanBundleList(list) {
        let size;
        this.searchkey = "";
        let page_list = this.currentpage;

        if (list) {
            size = list;
            this.itemsPerPage = list;
        } else {
            size = this.itemsPerPage;
        }

        const url = "/priceBook/list";
        let pricebookdata = {
            page: page_list,
            pageSize: size,
        };
        this.planBundleListData = [];
        this.planBundleService.postMethod(url, pricebookdata).subscribe(
            (response: any) => {
                if (response.responseCode == 204) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                } else {
                    this.planBundleListData = response.dataList;
                    this.totalRecords = response.totalRecords;
                    if (this.planBundleListData) {
                        if (this.showItemPerPage > this.itemsPerPage) {
                            this.totalPlanBundleListLength =
                                this.planBundleListData.length % this.showItemPerPage;
                        } else {
                            this.totalPlanBundleListLength = this.planBundleListData.length % this.itemsPerPage;
                        }
                    }
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    selectplanType(event) {
        let type = event.value;

        if ("RevenueShare" === type) {
            this.registration = "No";
            this.renewal = "No";
            this.revsharen = "Yes";
        }
        if ("Fixed" === type) {
            this.registration = "No";
            this.renewal = "Yes";
            this.revsharen = "No";
        }
        if ("TokenAmount" === type) {
            this.registration = "Yes";
            this.renewal = "No";
            this.revsharen = "No";
        }
    }

    selectplandetails(event) {
        let plandetails;

        const url = "/postpaidplan/" + event.value;
        this.planBundleService.getMethod(url).subscribe((response: any) => {
            plandetails = response.postPaidPlan;
            this.planAmountshow = true;
            this.PlanDetailListfromgroup.patchValue({
                offerprice: Number(plandetails.offerprice),
            });
        });
    }

    addEditplanBundle(planBundleId, dialogRef) {
        this.submitted = true;

        if (
            this.planBundleGroupForm.value.revenueType == "Slab" &&
            this.paricebookSlabListFromArray.value.length === 0
        ) {
            this.toastr.error('Atleast one slab should be added', 'Failed!');
        } else {
            if (this.planBundleGroupForm.valid && this.paricebookSlabListFromArray.valid) {
                if (planBundleId) {
                    let plandetalislength = 1;
                    const url = "/priceBook/update";

                    if (this.paricebookSlabListFromArray.value.length > 0) {
                        this.planBundleGroupForm.value.priceBookSlabDetailsList =
                            this.paricebookSlabListFromArray.value;
                    }
                    if (this.paricebookListFromArray.value.length > 0) {
                        this.planBundleGroupForm.value.priceBookPlanDetailList =
                            this.paricebookListFromArray.value;
                    }

                    if (this.serviceCommonListFromArray.value.length > 0) {
                        this.planBundleGroupForm.value.serviceCommissionList =
                            this.serviceCommonListFromArray.value;
                    }

                    this.createplanBundleData = this.planBundleGroupForm.value;

                    if (this.createplanBundleData.priceBookPlanDetailList.length > 0) {
                        this.createplanBundleData.priceBookPlanDetailList[0].registration = "No";
                        this.createplanBundleData.priceBookPlanDetailList[0].renewal = "No";
                        this.createplanBundleData.priceBookPlanDetailList[0].revsharen = "YES";
                        while (plandetalislength < this.createplanBundleData.priceBookPlanDetailList.length) {
                            this.createplanBundleData.priceBookPlanDetailList[plandetalislength].registration =
                                this.createplanBundleData.priceBookPlanDetailList[0].registration;
                            this.createplanBundleData.priceBookPlanDetailList[plandetalislength].renewal =
                                this.createplanBundleData.priceBookPlanDetailList[0].renewal;
                            this.createplanBundleData.priceBookPlanDetailList[plandetalislength].revsharen =
                                this.createplanBundleData.priceBookPlanDetailList[0].revsharen;
                            plandetalislength++;
                        }
                    }
                    while (plandetalislength < this.createplanBundleData.priceBookPlanDetailList.length) {
                        this.createplanBundleData.priceBookPlanDetailList[plandetalislength].registration =
                            this.createplanBundleData.priceBookPlanDetailList[0].registration;
                        this.createplanBundleData.priceBookPlanDetailList[plandetalislength].renewal =
                            this.createplanBundleData.priceBookPlanDetailList[0].renewal;
                        this.createplanBundleData.priceBookPlanDetailList[plandetalislength].revsharen =
                            this.createplanBundleData.priceBookPlanDetailList[0].revsharen;
                        plandetalislength++;
                    }
                    if (
                        this.createplanBundleData.isAllPlanSelected !== true ||
                        this.createplanBundleData.isAllPlanSelected == null
                    ) {
                        this.createplanBundleData.isAllPlanSelected = false;
                    }

                    this.createplanBundleData.isDeleted = false;
                    this.createplanBundleData.validFromString = "";
                    this.createplanBundleData.validToString = "";
                    this.createplanBundleData.validfrom = "";
                    this.createplanBundleData.validto = "";
                    this.createplanBundleData.noPartnerAssociate = 0;

                    if (
                        this.planBundleGroupForm.value.priceBookPlanDetailList.length > 0 ||
                        this.planBundleGroupForm.value.isAllPlanSelected == true ||
                        this.planBundleGroupForm.value.isAllPlanGroupSelected == true
                    ) {
                        this.planBundleService.postMethod(url, this.createplanBundleData).subscribe(
                            (response: any) => {
                                if (
                                    response.responseCode == 406 ||
                                    response.responseCode == 400 ||
                                    response.responseCode == 417
                                ) {
                                    this.toastr.error(`${response.responseMessage}`, 'Failed!');
                                } else {
                                    dialogRef.close()
                                    this.submitted = false;
                                    this.planBundleGroupForm.reset();
                                    this.planTypeDataFromgroup.reset();
                                    this.slabListListfromgroup.reset();
                                    this.serviceCommonFromGroup.reset();
                                    this.serviceCommonListFromArray.controls = [];
                                    this.isplanBundleEdit = false;
                                    this.planAmountshow = false;
                                    this.serviceCommisionSubmitted = false;
                                    this.planBundleSubmitted = false;
                                    this.slabSubmitted = false;
                                    this.viewplanBundleListData = [];
                                    this.paricebookListFromArray.controls = [];
                                    this.paricebookSlabListFromArray.controls = [];
                                    this.planListData = [];
                                    this.toastr.success(`${response.responseMessage}`, 'Success!');

                                    this.listView = true;
                                    this.createView = false;
                                    this.planBundleActinDatashow = false;
                                    this.planBundleDeatilsShow = true;
                                    this.getplanBundleList("");
                                }
                            },
                            (error: any) => {
                                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                            }
                        );
                    } else {
                        this.toastr.error('Minimum one Plan Details need to add', 'Failed!');
                    }
                } else {
                    const url = "/priceBook/save";

                    this.planBundleGroupForm.value.id = "";
                    this.planBundleGroupForm.value.updatedate = "";
                    this.planBundleGroupForm.value.validFromString = "";
                    this.planBundleGroupForm.value.validToString = "";
                    this.planBundleGroupForm.value.lastModifiedById = "";
                    this.planBundleGroupForm.value.lastModifiedByName = "";
                    this.planBundleGroupForm.value.createdById = "";
                    this.planBundleGroupForm.value.createdByName = "";
                    this.planBundleGroupForm.value.createdate = "";

                    if (this.paricebookSlabListFromArray.value.length > 0) {
                        this.planBundleGroupForm.value.priceBookSlabDetailsList =
                            this.paricebookSlabListFromArray.value;
                    }
                    if (this.paricebookListFromArray.value.length > 0) {
                        this.planBundleGroupForm.value.priceBookPlanDetailList =
                            this.paricebookListFromArray.value;
                    }

                    if (this.serviceCommonListFromArray.value.length > 0) {
                        this.planBundleGroupForm.value.serviceCommissionList =
                            this.serviceCommonListFromArray.value;
                    }

                    this.createplanBundleData = this.planBundleGroupForm.value;
                    this.createplanBundleData.planGroup = this.planGroupFieldFromGroup.value.planGroup;
                    this.createplanBundleData.isDeleted = false;
                    let plandetalislength = 1;
                    if (this.createplanBundleData.priceBookPlanDetailList.length > 0) {
                        this.createplanBundleData.priceBookPlanDetailList[0].registration = "No";
                        this.createplanBundleData.priceBookPlanDetailList[0].renewal = "No";
                        this.createplanBundleData.priceBookPlanDetailList[0].revsharen = "YES";
                        while (plandetalislength < this.createplanBundleData.priceBookPlanDetailList.length) {
                            this.createplanBundleData.priceBookPlanDetailList[plandetalislength].registration =
                                this.createplanBundleData.priceBookPlanDetailList[0].registration;
                            this.createplanBundleData.priceBookPlanDetailList[plandetalislength].renewal =
                                this.createplanBundleData.priceBookPlanDetailList[0].renewal;
                            this.createplanBundleData.priceBookPlanDetailList[plandetalislength].revsharen =
                                this.createplanBundleData.priceBookPlanDetailList[0].revsharen;
                            plandetalislength++;
                        }
                    }

                    if (
                        this.createplanBundleData.isAllPlanSelected !== true ||
                        this.createplanBundleData.isAllPlanSelected == null
                    ) {
                        this.createplanBundleData.isAllPlanSelected = false;
                    }
                    this.createplanBundleData.noPartnerAssociate = 0;

                    if (
                        this.planBundleGroupForm.value.priceBookPlanDetailList.length > 0 ||
                        this.planBundleGroupForm.value.isAllPlanSelected == true ||
                        this.planBundleGroupForm.value.isAllPlanGroupSelected == true
                    ) {
                        this.planBundleService.postMethod(url, this.createplanBundleData).subscribe(
                            (response: any) => {
                                if (response.responseCode == 406 || response.responseCode == 417) {
                                    this.toastr.error(`${response.responseMessage}`, 'Failed!');
                                } else {
                                    dialogRef.close()
                                    this.submitted = false;
                                    this.planBundleGroupForm.reset();
                                    this.planTypeDataFromgroup.reset();
                                    this.slabListListfromgroup.reset();
                                    this.serviceCommonFromGroup.reset();

                                    this.serviceCommonListFromArray.controls = [];
                                    this.paricebookListFromArray.controls = [];
                                    this.paricebookSlabListFromArray.controls = [];
                                    this.planListData = [];
                                    this.planAmountshow = false;
                                    this.toastr.success(`${response.responseMessage}`, 'Success!');

                                    this.listView = false;
                                    this.createView = false;
                                    this.planBundleActinDatashow = false;
                                    this.planBundleDeatilsShow = true;
                                    this.serviceCommisionSubmitted = false;
                                    this.planBundleSubmitted = false;
                                    this.slabSubmitted = false;
                                    this.getplanBundleList("");
                                }
                            },
                            (error: any) => {
                                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                            }
                        );
                    } else {
                        this.toastr.error('Minimum one Plan Details need to add', 'Failed!');
                    }
                }
            }
        }
    }

    editplanBundle(id: any) {
        this.dialog.open(this.AddEditDailog, {
            width: "80%",
            disableClose: true,
        });
        this.listView = false;
        this.createView = true;
        this.planBundleActinDatashow = false;
        this.planBundleDeatilsShow = false;
        this.priceBooklistlength = 0;
        this.serviceCommisionSubmitted = false;
        this.planBundleSubmitted = false;
        this.slabSubmitted = false;
        this.planBundleGroupForm.reset();
        this.planTypeDataFromgroup.reset();
        this.slabListListfromgroup.reset();
        this.serviceCommonFromGroup.reset();
        this.paricebookSlabListFromArray = this.fb.array([]);
        this.paricebookListFromArray = this.fb.array([]);
        this.serviceCommonListFromArray = this.fb.array([]);

        this.serviceSelectList = [];
        if (this.paricebookListFromArray.controls) {
            this.paricebookListFromArray.controls = [];
        }
        const url = "/priceBook/" + id;
        this.planBundleService.getMethod(url).subscribe(
            (response: any) => {
                this.isplanBundleEdit = true;
                this.viewplanBundleListData = response.data;
                this.planBundleGroupForm.patchValue(this.viewplanBundleListData);

                this.planTypeDataFromgroup.patchValue({ type: "RevenueShare" });

                this.getplanservice();

                if (this.planBundleGroupForm.value.revenueType == "Slab") {
                    this.slabFlag = true;
                } else {
                    this.slabFlag = false;
                }

                if (this.viewplanBundleListData.serviceCommissionList.length > 0) {
                    this.viewplanBundleListData.serviceCommissionList.forEach(element => {
                        this.serviceCommonListFromArray.push(
                            this.fb.group({
                                serviceId: [element.serviceId],
                                revenue_share_percentage: [element.revenue_share_percentage],
                                id: [element.id],
                                royaltyPercentage: [element.royaltyPercentage],
                            })
                        );

                        this.serviceSelectList.push(element.serviceId);

                        this.planserviceData.forEach((ele, index) => {
                            if (ele.id == element.serviceId) {
                                this.planserviceData.splice(index, 1);
                            }
                        });
                    });
                }

                if (
                    this.planBundleGroupForm.value.isAllPlanSelected ||
                    this.planBundleGroupForm.value.isAllPlanGroupSelected
                ) {
                    this.planSelection = false;
                    this.allSelection = [];
                    if (this.planBundleGroupForm.value.isAllPlanSelected) {
                        this.allSelection.push("individual");
                    }
                    if (this.planBundleGroupForm.value.isAllPlanGroupSelected) {
                        this.allSelection.push("groupPlan");
                    }
                }

                if (this.viewplanBundleListData.priceBookSlabDetailsList.length > 0) {
                    this.viewplanBundleListData.priceBookSlabDetailsList.forEach(element => {
                        this.paricebookSlabListFromArray.push(
                            this.fb.group({
                                fromRange: [
                                    element.fromRange,
                                    ,
                                ],
                                toRange: [
                                    element.toRange,
                                    ,
                                ],
                                commissionAmount: [
                                    element.commissionAmount, ,
                                ],
                                id: [element.id],
                            })
                        );
                    });
                }

                if (this.viewplanBundleListData.priceBookPlanDetailList.length > 0) {
                    this.planGroupFieldFromGroup.patchValue({
                        planGroup: this.viewplanBundleListData.planGroup
                    });

                    if (this.viewplanBundleListData.serviceCommissionList.length == 0) {
                        let planGroupValue = {
                            value: this.viewplanBundleListData.priceBookPlanDetailList[0].postpaidPlan
                                ? this.viewplanBundleListData.priceBookPlanDetailList[0].postpaidPlan.planGroup
                                : this.viewplanBundleListData.priceBookPlanDetailList[0].planGroup.planGroupType,
                        };
                        this.selPlanGroup(planGroupValue);
                    } else {
                        const url =
                            "/postpaidplan/filter?planGroup=" +
                            this.planGroupFieldFromGroup.value.planGroup +
                            "&type=NORMAL";

                        this.planBundleService.postMethod(url, this.serviceSelectList).subscribe(
                            (response: any) => {
                                this.planListData = response.postpaidplanList;
                                this.planListMasterData = response.postpaidplanList;
                            },
                            (error: any) => { }
                        );
                    }

                    this.viewplanBundleListData.priceBookPlanDetailList.forEach(element => {
                        if (element.postpaidPlan) {
                            this.paricebookListFromArray.push(
                                this.fb.group({
                                    id: [element.id],
                                    isDeleted: [false],
                                    offerprice: [element.offerprice],
                                    partnerofficeprice: [0],
                                    revenueSharePercentage: [element.revenueSharePercentage],
                                    isTaxIncluded: [element.isTaxIncluded],
                                    registration: [this.registration],
                                    renewal: [this.renewal],
                                    revsharen: [this.revsharen],
                                    postpaidPlan: this.fb.group({
                                        id: [element.postpaidPlan.id],
                                    }),
                                })
                            );
                        } else {
                            this.paricebookListFromArray.push(
                                this.fb.group({
                                    id: [element.id],
                                    isDeleted: [false],
                                    offerprice: [element.offerprice],
                                    partnerofficeprice: [0],
                                    revenueSharePercentage: [element.revenueSharePercentage],
                                    isTaxIncluded: [element.isTaxIncluded],
                                    registration: [this.registration],
                                    renewal: [this.renewal],
                                    revsharen: [this.revsharen],
                                    planGroup: this.fb.group({
                                        planGroupId: [element.planGroup.planGroupId],
                                    }),
                                })
                            );
                        }
                    });
                }
                this.setPlanAndPlanGroupAtEdit();
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    checkRevenueType(e) {

        this.PlanDetailListfromgroup.reset();
        this.serviceCommonFromGroup.reset();
        this.slabListListfromgroup.reset();
        this.paricebookListFromArray = this.fb.array([]);
        this.serviceCommonListFromArray = this.fb.array([]);
        this.paricebookSlabListFromArray = this.fb.array([]);
        this.enableRevenueSharePercentage();
        if (e.value == "Slab") {
            this.commisionData = [{ label: "Plan level" }];
            this.slabFlag = true;
        } else {
            this.commisionData = [{ label: "Plan level" }, { label: "Service level" }];
            this.slabFlag = false;
        }
    }

    enableRevenueSharePercentage() {
        this.planBundleGroupForm.get("isAllPlanSelected").setValue(false);
        this.planBundleGroupForm.get("isAllPlanGroupSelected").setValue(false);
        if (
            this.planSelection !== true &&
            this.planBundleGroupForm.value.commission_on == "Plan level" &&
            this.planBundleGroupForm.value.revenueType == "Percentage"
        ) {
            this.planBundleGroupForm
                .get("revenueSharePercentage")
                .setValidators([Validators.required, Validators.min(-100), Validators.max(100)]);
            this.planBundleGroupForm.get("revenueSharePercentage").updateValueAndValidity();
        } else {
            this.planBundleGroupForm.get("revenueSharePercentage").clearValidators();
            this.planBundleGroupForm.get("revenueSharePercentage").updateValueAndValidity();
        }
    }

    checkcommisionOn(e) {
        this.PlanDetailListfromgroup.reset();
        this.serviceCommonFromGroup.reset();
        this.slabListListfromgroup.reset();
        this.paricebookListFromArray = this.fb.array([]);
        this.serviceCommonListFromArray = this.fb.array([]);
        this.paricebookSlabListFromArray = this.fb.array([]);
        this.enableRevenueSharePercentage();
    }

    onAddTaxTypeSlabField() {
        this.slabSubmitted = true;
        if (this.slabListListfromgroup.valid) {
            this.paricebookSlabListFromArray.push(this.createTaxTypeSlabFormGroup());
            this.slabTable.renderRows()
            this.slabListListfromgroup.reset();
            this.slabSubmitted = false;
        } else {
        }
    }

    createTaxTypeSlabFormGroup(): FormGroup {
        return this.fb.group({
            fromRange: [
                this.slabListListfromgroup.value.fromRange,
                [Validators.required, Validators.pattern(Regex.numeric)],
            ],
            toRange: [
                this.slabListListfromgroup.value.toRange,
                [Validators.required, Validators.pattern(Regex.numeric)],
            ],
            commissionAmount: [
                this.slabListListfromgroup.value.commissionAmount,
                [Validators.required, Validators.pattern(Regex.decimalNumber)],
            ],
            id: [""],
        });
    }

    canExit() {
        if (!this.planBundleGroupForm.dirty) return true;
        {
            return Observable.create((observer: Observer<boolean>) => {
                const dialogRef = this.dialog.open(this.confirmDialog, {
                    width: '400px',
                    data: {
                        title: 'Alert',
                        description: `The filled data will be lost. Do you want to continue? (Yes/No)`,
                        yesLabel: 'Yes',
                        noLabel: 'No'
                    }
                });

                dialogRef.afterClosed().subscribe((result) => {
                    if (result === true) {
                        observer.next(true);
                        observer.complete();
                    }
                    else {
                        observer.next(false);
                        observer.complete();
                    }
                });
                return false;
            });
        }
    }

    deleteConfirmonTaxTypeSlabField(TaxTypeSlabFieldIndex: number, TaxTypeSlabFieldId: number) {
        if (TaxTypeSlabFieldIndex || TaxTypeSlabFieldIndex == 0) {
            const dialogRef = this.dialog.open(this.confirmDialog, {
                width: '400px',
                data: {
                    title: 'Delete Confirmation',
                    description: `Do you want to delete this Slab?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe((result) => {
                if (result === true) {
                    this.onRemoveTaxTypeSlab(TaxTypeSlabFieldIndex, TaxTypeSlabFieldId);
                } else {
                    this.toastr.info('Delete operation was cancelled', 'Info!');
                }
            });
        }
    }

    deleteConfirmonServiceCommisiionField(index: number, idService: any) {
        this.serviceCommonListFromArray.removeAt(index);
        this.serviceSelectList.splice(index, 1);
        this.serviceTable.renderRows()
        this.commondropdownService.planserviceData.forEach((element, index) => {
            if (element.id == idService.value.serviceId) {
                this.planserviceData.push(element);
            }
        });
    }

    async onRemoveTaxTypeSlab(TaxTypeSlabFieldIndex: number, TaxTypeSlabFieldId: number) {
        this.paricebookSlabListFromArray.removeAt(TaxTypeSlabFieldIndex);
        this.slabTable.renderRows()
    }

    deleteConfirmonplanBundle(planBundleId: number) {
        if (planBundleId) {
            const dialogRef = this.dialog.open(this.confirmDialog, {
                width: '400px',
                data: {
                    title: 'Delete Confirmation',
                    description: `Do you want to delete this Plan Bundle?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe((result) => {
                if (result === true) {
                    this.deleteplanBundle(planBundleId);
                } else {
                    this.toastr.info('Delete operation was cancelled', 'Info!');
                }
            });
        }
    }

    deleteplanBundle(data) {
        let planBundleData = [];

        const url1 = "/priceBook/" + data.id;

        this.planBundleService.getMethod(url1).subscribe(
            (response: any) => {
                planBundleData = response.data;
                if (this.currentpage != 1 && this.totalPlanBundleListLength == 1) {
                    this.currentpage = this.currentpage - 1;
                }
                this.dataDelegte(planBundleData);
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    dataDelegte(planBundleData) {
        const url = "/priceBook/delete";

        this.planBundleService.postMethod(url, planBundleData).subscribe(
            (response: any) => {
                if (response.responseCode == 406 || response.responseCode == 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                } else {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');
                    this.getplanBundleList("");
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    pageChangedplanBundleList(event: PageEvent) {
        this.currentpage = event.pageIndex + 1;
        this.itemsPerPage = event.pageSize;
        if (!this.searchkey) {
            this.getplanBundleList("");
        } else {
            this.search();
        }
    }

    pageChangedCharge(pageNumber) {
        this.currentPageCharge = pageNumber;
    }

    pageChangedServiceCommission(pageNumber) {
        this.currentPageservice = pageNumber;
    }

    deleteConfirmonChargeField(chargeFieldIndex: number, chargeFieldId: number) {
        if (chargeFieldIndex || chargeFieldIndex == 0) {
            const dialogRef = this.dialog.open(this.confirmDialog, {
                width: '400px',
                data: {
                    title: 'Delete Confirmation',
                    description: `Do you want to delete this Plan?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe((result) => {
                if (result === true) {
                    this.onRemoveCharge(chargeFieldIndex, chargeFieldId);
                } else {
                    this.toastr.info('Delete operation was cancelled', 'Info!');
                }
            });
        }
    }

    async onRemoveCharge(chargeFieldIndex: number, chargeFieldId: number) {
        this.paricebookListFromArray.removeAt(chargeFieldIndex);
        this.planTable.renderRows()
    }

    searchplanBundle() {
        const url = "/planBundlerule";
        this.planBundleService.getMethod(url).subscribe((response: any) => {
            this.planBundleListData1 = response.planBundlerulelist;
        });

        this.planBundleGroupForm = this.planBundleListData1;
        this.temp = [...this.planBundleListData1];
        let valueobj = {};

        if (this.planBundlecategory) {
            valueobj["name"] = this.planBundlecategory;
        }
        if (this.planBundletype) {
            valueobj["creditclass"] = this.planBundletype;
        }

        let filterdata = _.filter(this.planBundleGroupForm, valueobj);
        this.planBundleListData = filterdata;
        this.temp = filterdata;
    }

    clearSearchplanBundle() {
        this.getplanBundleList("");
        this.planBundlecategory = "";
        this.planBundletype = "";
        this.listView = true;
    }

    serviListName: any = [];
    planDetailDisplayedColumns = [];
    basicDetails: any = {}

    PlanDetailListDetails(data) {
        this.listView = false;

        const url = "/priceBook/" + data.id;
        this.planBundleService.getMethod(url).subscribe(
            (response: any) => {
                this.planBundlelistDeatils = response.data;
                this.basicDetails = response.data;

                // handle plan group safely
                if (this.planBundlelistDeatils.priceBookPlanDetailList?.length > 0) {
                    const firstPlan = this.planBundlelistDeatils.priceBookPlanDetailList[0];
                    this.planGrouptype = firstPlan?.postpaidPlan?.planGroup || firstPlan?.planGroup?.planGroupType || '-';
                }

                this.plantypeName = "Revenue Share";
                this.planBundleActinDatashow = true;
                this.listView = false;
                this.createView = false;
                this.planBundleDeatilsShow = false;

                // columns
                this.planDetailDisplayedColumns =
                    this.planBundlelistDeatils.commission_on === 'Plan level'
                        ? ['plan', 'offerPrice', 'revenueShare']
                        : ['plan', 'offerPrice'];

                // FIXED: length + safety
                if (this.planBundlelistDeatils.serviceCommissionList?.length > 0) {
                    this.planBundlelistDeatils.serviceCommissionList.forEach((element) => {
                        this.commondropdownService?.planserviceData?.forEach((data) => {
                            if (element?.serviceId === data?.id) {
                                if (data?.name) {
                                    this.serviListName.push(data.name);
                                }
                            }
                        });
                    });
                }

                this.dialog.open(this.detailsDialog, {
                    width: '80%',
                    disableClose: true
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    pageChangedplanBundleRuleList(pageNumber) {
        this.currentPageplanBundleRuleList = pageNumber;
    }

    getPlanGroup() {
        const url = "/commonList/planGroup";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.planGroupData = response.dataList;
                let data = {
                    text: "All",
                    value: "All",
                };
                let isExist = this.planGroupData.some(plan => plan.text === data.text);
                if (!isExist) {
                    this.planGroupData.unshift(data);
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    selPlanGroup(event) {
        this.getPlanListbyGroup(event.value);
        this.paricebookListFromArray = this.fb.array([]);
        this.PlanDetailListfromgroup.reset();
        this.planGroupdata = this.planGroupMasterData;
        this.planBundleGroupForm.patchValue({
            planCategory: "individual",
            isAllPlanSelected: false,
            isAllPlanGroupSelected: false,
        });
    }

    getPlanListbyGroup(group: any) {
        const url = "/postpaidplan/all?planGroup=" + group;
        this.planBundleService.getMethod(url).subscribe(
            (response: any) => {
                this.planListData = response.postpaidplanList;
                this.planListMasterData = response.postpaidplanList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    pageChangedTaxSlab(pageNumber) {
        this.currentPageTaxSlab = pageNumber;
    }

    planserviceData: any = [];
    planserviceCopyData: any = [];

    getplanservice() {
        const url = "/planservice/all";
        this.planBundleService.getMethod(url).subscribe(
            (response: any) => {
                this.planserviceData = response.serviceList;
                this.planserviceCopyData = response.serviceList;
            },
            (error: any) => {
                // Silent error handling for this service call
            }
        );
    }

    serviceSelectList: any = [];
    serviceSelectID: any = "";

    selectServiceId(event: any) {
        const id = Number(event.value);
        this.serviceSelectID = id;


        const matchedService = this.planserviceData.find(
            (service: any) => service.id === id
        );

        if (matchedService) {
            this.selectedServiceData = matchedService;
        }

        const serviceData = [this.serviceSelectID, ...this.serviceSelectList];

        const url =
            "/postpaidplan/filter?planGroup=" +
            this.planGroupFieldFromGroup.value.planGroup +
            "&type=NORMAL";

        this.planBundleService.postMethod(url, serviceData).subscribe(
            (response: any) => {
                this.planListData = response.postpaidplanList;
                this.planListMasterData = response.postpaidplanList;
            },
            (error: any) => { }
        );
    }

    selectAllPlanselctEvent(event) {
        this.planSelection = !event.checked;
        this.paricebookListFromArray = this.fb.array([]);
        this.allSelection = [];
        this.enableRevenueSharePercentage();
    }

    TotalItemPlanMappingPerPage(event) {
        let size;
        this.showItemPlanMappingPerPage = Number(event.value);
        if (this.currentPageCharge > 1) {
            this.currentPageCharge = 1;
        }

        if (this.showItemPlanMappingPerPage) {
            size = this.showItemPlanMappingPerPage;
            this.chargeitemsPerPage = this.showItemPlanMappingPerPage;
        } else {
            size = this.chargeitemsPerPage;
        }

        this.chargetotalRecords = this.paricebookListFromArray.value.length;
    }

    planGroupSelect(e) {
        if (e.value) {
            let url = "/findPlanGroupById?planGroupId=" + e.value;
            this.planBundleService.getMethod(url).subscribe(
                (response: any) => {
                    const planDetailData = response.planGroup;

                    let newAmount = 0;
                    let totalAmount = 0;
                    planDetailData.planMappingList.forEach((element, i) => {
                        let n = i + 1;
                        newAmount = element.plan.newOfferPrice
                            ? element.plan.newOfferPrice
                            : element.plan.offerprice;
                        totalAmount = Number(totalAmount) + Number(newAmount);
                        if (planDetailData.planMappingList.length == n) {
                            this.PlanDetailListfromgroup.patchValue({
                                offerprice: totalAmount,
                            });
                            this.planAmountshow = true;
                        }
                    });
                },
                (error: any) => { }
            );
        }
    }

    search() {
        if (!this.searchkey || this.searchkey !== this.searchName) {
            this.currentpage = 1;
            if (this.paginator) {
                this.paginator.pageIndex = 0;
            }
        }
        this.searchkey = this.searchName;
        if (this.showItemPerPage) {
            this.itemsPerPage = this.showItemPerPage;
        }
        this.searchData.filter[0].filterValue = this.searchName.trim();

        const url = `/priceBook/search?page=${this.currentpage}&pageSize=${this.itemsPerPage}&sortBy=id&sortOrder=0`;
        this.planBundleService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                this.planBundleListData = response.dataList;
                this.totalRecords = response.totalRecords;
            },
            (error: any) => {
                this.chargetotalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');
                    this.planBundleListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            }
        );
    }

    clearSearch() {
        this.searchName = "";
        this.searchkey = "";
        this.currentpage = 1;
        if (this.paginator) {
            this.paginator.pageIndex = 0;
        }
        this.getplanBundleList("");
        this.submitted = false;
        this.isplanBundleEdit = false;
        this.planBundleGroupForm.reset();
    }

    allPlanSection(event) {
        this.PlanDetailListfromgroup.reset();
        this.planBundleGroupForm.patchValue({
            isAllPlanSelected: false,
            isAllPlanGroupSelected: false,
        });
        if (event.value.includes("individual")) {
            this.planBundleGroupForm.patchValue({
                isAllPlanSelected: true,
            });
        }
        if (event.value.includes("groupPlan")) {
            this.planBundleGroupForm.patchValue({
                isAllPlanGroupSelected: true,
            });
        }
    }

    setPlanAndPlanGroupAtEdit() {
        if (this.planBundleGroupForm.value.partnerId) {
            this.selectedParentPartner = this.parenetpartnerList.find(
                parent => parent.id == this.planBundleGroupForm.value.partnerId
            );

            const url = "/priceBook/" + this.selectedParentPartner.pricebookId;

            this.planBundleService.getMethod(url).subscribe(
                (response: any) => {
                    this.parentpartnerDetails = response.data;
                },
                (error: any) => { }
            );
        }
    }

    planCategoryChange(event: any, dd: any) {
        this.PlanDetailListfromgroup.reset();
        let selectedcategory = dd.selectedOption.value;
        if (this.parentpartnerDetails && this.parentpartnerDetails.commission_on === "Plan level") {
            if (this.planBundleGroupForm.value.planCategory === "individual") {
                if (this.parentpartnerDetails.isAllPlanSelected) {
                    this.planListData = this.planListMasterData.filter(plan => {
                        return this.selectedParentPartner.serviceAreaNameList.some(serviceName =>
                            plan.serviceAreaNameList.some(service => service.name === serviceName)
                        );
                    });
                } else {
                    this.planListData = [];
                    this.parentpartnerDetails.priceBookPlanDetailList
                        .filter(planDetails => planDetails.planGroup == null)
                        .map(plan => this.planListData.push(plan.postpaidPlan));
                }
            } else {
                if (this.parentpartnerDetails.isAllPlanGroupSelected) {
                    this.planGroupdata = this.planGroupMasterData.filter(plan => {
                        return this.selectedParentPartner.serviceAreaIds.some(serviceId =>
                            plan.servicearea.includes(serviceId)
                        );
                    });
                } else {
                    this.planGroupdata = [];
                    this.parentpartnerDetails.priceBookPlanDetailList
                        .filter(planDetails => planDetails.planGroup != null)
                        .map(plan => this.planGroupdata.push(plan.planGroup));
                }
            }
        } else {
            if (this.parentpartnerDetails && this.parentpartnerDetails.serviceCommissionList) {
                this.planserviceData = this.planserviceCopyData.filter(item =>
                    this.parentpartnerDetails.serviceCommissionList.some(
                        commission => commission.serviceId === item.id
                    )
                );
            } else {
                if (
                    selectedcategory === "groupPlan" &&
                    this.planGroupFieldFromGroup.value.planGroup !== "All"
                ) {
                    this.planGroupdata = this.planGroupdata.filter(
                        plan => plan.planGroupType === this.planGroupFieldFromGroup.value.planGroup
                    );
                }
            }
        }
    }

    handleKeyDown(event: KeyboardEvent) {
        let maxValue: number = Number(100);

        const inputElement = event.target as HTMLInputElement;

        if (event.keyCode === 8 || (event.key >= "0" && event.key <= "9")) {
            if (parseFloat(inputElement.value + event.key) <= maxValue) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    dataSourceData = [{}]
    displayedColumnsmainTable: string[] = ['planid', 'name', 'createdate', 'status', 'action'];

    servicecommissionDisply = ['service', 'revenueShare', 'royalty', 'delete']
    pricebookListDisplay = ['plan', 'offerprice', 'revenueShare', 'delete']
    pariceSalbListDisplay = ['fromRange', 'toRange', 'commissionAmount', 'delete']

    serviceCommissionDisplayedColumns = ['service', 'revenueShare', 'royalty'];
    slabDisplayedColumns = ['fromRange', 'toRange', 'commissionAmount'];

    @ViewChild('confirmDialog') confirmDialog!: TemplateRef<any>;
    @ViewChild('detailsDialog') detailsDialog!: TemplateRef<any>;
    @ViewChild('AddEditDailog') AddEditDailog!: TemplateRef<any>;

    @ViewChild('serviceTable') serviceTable!: MatTable<any>;
    @ViewChild('planTable') planTable!: MatTable<any>;
    @ViewChild('slabTable') slabTable!: MatTable<any>;

    @ViewChild('paginator') paginator!: any; // Use MatPaginator if imported
}
