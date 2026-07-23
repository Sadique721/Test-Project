import { PopManagementsService } from "src/app/service/pop-managements.service";
import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { NgxSpinnerService } from "ngx-spinner";
import { MessageService } from "primeng/api";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { LeadManagementService } from "./lead-management-service";
import { ProductCategoryManagementService } from "./product-category-management.service";
import { ProuctManagementService } from "./prouct-management.service";
import { HttpResponseCache } from "./http-response-cache";
import { TicketManagementService } from "./ticket-management.service";
import { CountryManagementModule } from "../components/country-management/country-management.module";
import { CountryManagementService } from "./country-management.service";
import { StateManagementService } from "./state-management.service";
import { PincodeManagementService } from "./pincode-management.service";
import { AreaManagementService } from "./area-management.service";
import { CityManagementService } from "./city-management.service";
import { ServiceAreaService } from "./service-area.service";
import { BranchManagementService } from "../components/branch-management/branch-management.service";
import { SystemconfigService } from "./systemconfig.service";
import { SavbillCommonBaseService } from "./savbill-common-base.service";
import { ChargeManagementService } from "./charge-management.service";
import { PartnerService } from "./partner.service";
import { RevenueManagementService } from "./RevenueManagement.service";
import { BehaviorSubject } from "rxjs";
import { ToastrService } from 'ngx-toastr';

@Injectable({
    providedIn: "root"
})
export class CommondropdownService {
    ifPaytmLinkSendBtn = true;
    cityListData: any[] = [];
    countryListData: any[] = [];
    stateListData: any[] = [];
    chargeByTypeData: any;
    ChargeForCustomerData: any = [];
    planserviceData: any = [];
    copyplanserviceData: any = [];
    allpincodeNumber: any = [];
    areaData: any = [];
    commonListTitleData: any = [];
    commonListPaymentData: any = [];
    ippoolData: any = [];
    postpaidplanData: any = [];
    partnerAllNAme: any = [];
    chargeList: any = [];
    taxAllList: any = [];
    priceBookList: any = [];
    activePriceBookList: any = [];
    serviceAreaList: any = [];
    billRunMasterList: any = [];
    planPurchaseTypeData: any = [];
    CustomerStatusValue: any = [];
    CustomerNewStatusValue: any = [];
    customerAllList: any = [];
    PrepaidPlanGroupDetails = [];
    postPlanGroupDetails = [];
    postpaidCustomerList: any = [];
    postpaidParentCustomerList: any = [];
    prepaidParentCustomerList: any = [];
    PlanGroupDetails = [];
    billToData: any = [];
    validityUnitData = [
        { label: "Hours" },
        { label: "Days" },
        { label: "Months" },
        { label: "Years" }
    ];
    specialpostpaidplanData: any = [];
    NomalpostpaidplanData: any = [];
    NormalPlanGroupDetails: any = [];
    NormalPlanGroupDetailsBySpecialPlan: any = [];
    SpecialPlanGroupDetails: any = [];
    teamListData: any;
    activeTeamListData: any;
    tatMatricsData: any = [];
    activeTatMatricsData: any = [];
    activeBranchList: any = [];
    activeStaffList: any = [];
    businessUnitList: any = [];
    activeProductList: any = [];
    activeInwardList: any = [];
    ownershipTypeList: any = [];
    itemStatusList: any = [];
    itemConditionList: any = [];
    warrantyStatusList: any = [];
    popListData: any = [];
    TATForTicketData: any = [];
    productCategoryList: any = [];
    customertypeList: any = [];
    customerSubtypeList: any = [];
    sectortypeList: any = [];
    valleyType: any = [];
    insideValley: any = [];
    outsideValley: any = [];
    branchesByServiceArea: any = [];

    commonCountryCode = "+91";
    commonBefaultCountryCode = "+256";
    commonMoNumberLength = 0;
    commonPanNumberLength = 0;
    regionDataList: any;

    customerChangeStatusValue: any = [];
    isPlanOnDemand: boolean = false;

    customerSearch = [
        { label: "Firstname", value: "name" },
        { label: "Username", value: "username" }
    ];

    customerSearchOption1 = [
        { label: "Product Name", value: "Product Name" },
        { label: "Inward Number", value: "Inward Number" }
    ];
    customerSearchOption2 = [
        { label: "Product Name", value: "Product Name" },
        { label: "Outward Number", value: "Outward Number" }
    ];
    productCategorySearchOption = [
        { label: "Product Category Name", value: "Name" },
        { label: "Product Category Type", value: "Type" }
    ];
    customerSearchOption = [
        { label: "Account No", value: "accountNumber" },
        { label: "Any", value: "any" },
        { label: "Bill to Organization", value: "billTo" },
        { label: "Branch", value: "branchName" },
        { label: "CAf Activation By", value: "activationbyname" },
        { label: "CAF Created By", value: "createbyname" },
        { label: "CAF Created Date", value: "cafCreatedDate" },
        { label: "CAF Number", value: "cafNo" },
        { label: "CAF Status", value: "cafStatus" },
        { label: "CAF to Customer Activation Date", value: "firstactivationdate" },
        { label: "Circuit Name", value: "circuitName" },
        { label: "Current Assigned Staff", value: "currentAssigneeName" },
        { label: "Current Assigned Team", value: "currentAssignedTeam" },
        { label: "Current Assigned Team And Staff", value: "currentAssignedTeamAndStaff" },
        { label: "Customer Type", value: "custtype" },
        { label: "Email", value: "email" },
        { label: "First Name", value: "name" },
        { label: "Framed_Ip_Address", value: "framedIpAddress" },
        { label: "Full Name", value: "fullname" },
        { label: "Inventory Serial Number", value: "inventorySerial" },
        { label: "Mac Address", value: "macaddress" },
        { label: "Param1", value: "param1" },
        { label: "Param2", value: "param2" },
        { label: "Param3", value: "param3" },
        { label: "Param4", value: "param4" },
        { label: "Partner Name", value: "partnerName" },
        { label: "Phone", value: "mobile" },
        { label: "Plan", value: "plan" },
        { label: "Plan Expiry Date", value: "expiryDate" },
        { label: "Plan Group", value: "planGroup" },
        { label: "Service", value: "service" },
        { label: "Service Area", value: "serviceareaName" },
        { label: "Static IP", value: "staticIp" },
        { label: "Status", value: "status" },
        { label: "Subscription Mode", value: "subscriptionMode" },
        { label: "Username", value: "username" }
    ];

    customerSearchOptionForCAF = [{ label: "Account No", value: "accountNumber" },
    { label: "Any", value: "any" },
    // { label: "Bill to Organization", value: "billTo" },
    { label: "Branch", value: "branchName" },
    { label: "CAf Activation By", value: "activationbyname" },
    // { label: "CAF Created By", value: "createbyname" },
    { label: "CAF Created Date", value: "cafCreatedDate" },
    { label: "CAF Number", value: "cafNo" },
    { label: "CAF Status", value: "cafStatus" },
    { label: "CAF to Customer Activation Date", value: "firstactivationdate" },
    { label: "Circuit Name", value: "circuitName" },
    // { label: "Current Assigned Staff", value: "currentAssigneeName" },
    // { label: "Current Assigned Team", value: "currentAssignedTeam" },
    // { label: "Current Assigned Team And Staff", value: "currentAssignedTeamAndStaff" },
    { label: "Customer Type", value: "custtype" },
    { label: "Email", value: "email" },
    { label: "First Name", value: "name" },
    { label: "Framed_Ip_Address", value: "framedIpAddress" },
    { label: "Full Name", value: "fullname" },
    { label: "Inventory Serial Number", value: "inventorySerial" },
    { label: "Mac Address", value: "macaddress" },
    { label: "Param1", value: "param1" },
    { label: "Param2", value: "param2" },
    { label: "Param3", value: "param3" },
    { label: "Param4", value: "param4" },
    { label: "Partner Name", value: "partnerName" },
    { label: "Phone", value: "mobile" },
    // { label: "Plan", value: "plan" },
    { label: "Plan Expiry Date", value: "expiryDate" },
    { label: "Plan Group", value: "planGroup" },
    { label: "Service", value: "service" },
    // { label: "Service Area", value: "serviceareaName" },
    { label: "Static IP", value: "staticIp" },
    { label: "Status", value: "status" },
    { label: "Subscription Mode", value: "subscriptionMode" },
        // { label: "Username", value: "username" }
    ]

    customerSearchOptionBill = [
        { label: "Firstname", value: "name" },
        { label: "Username", value: "username" },
        { label: "Email", value: "email" },
        { label: "Phone", value: "mobile" },
        { label: "Service", value: "service" },
        { label: "Branch", value: "branchName" }
    ];

    radiusSearchOptionBill = [
        { label: "Username", value: "userName" },
        { label: "Email", value: "emailAddress" },
        { label: "Phone", value: "mobileNo" },
        { label: "Status", value: "customerStatus" },
        { label: "Plan Name", value: "planName" }
    ];

    planSearchOption = [
        { label: "Plan Name", value: "planname" },
        { label: "Plan Type", value: "plantype" },
        { label: "Validity", value: "planvalidity" },
        { label: "Price", value: "planprice" },
        { label: "Service Area", value: "servicearea" },
        { label: "Status", value: "planstatus" },
        { label: "Branch", value: "planbranch" },
        { label: "Start Date", value: "planstartdate" },
        { label: "End Date", value: "planenddate" },
        { label: "Created By", value: "plancreatedby" },
        { label: "Created Date", value: "plancreateddate" }
    ];

    planGroupSearchOption = [
        { label: "Plan Group Name", value: "planname" },
        { label: "Plan Group Type", value: "plantype" },
        { label: "Price", value: "planprice" },
        { label: "Status", value: "planstatus" },
        { label: "Created By", value: "plancreatedby" },
        { label: "Created Date", value: "plancreateddate" }
    ];

    locationDetailsData = [
        { label: "Router", value: "Router" },
        { label: "LT", value: "LT" },
        { label: "Switch", value: "Switch" },
        { label: "OLT", value: "OLT" },
        { label: "AP", value: "AP" },
        { label: "CPE", value: "CPE" },
        { label: "ONU", value: "ONU" },
        { label: "DB", value: "DB" },
        { label: "MDB", value: "MDB" },
        { label: "VLAN", value: "VLAN" }
    ];

    macSearchOption = [
        { label: "Item Id", value: "itemId" },
        { label: "Mac", value: "mac" },
        { label: "Serial Number", value: "serialNumber" },
        { label: "Asset Id", value: "assetId" },
        { label: "Item Type", value: "itemType" }
    ];
    searchInventoryOption = [
        { label: "Item Id", value: "itemId" },
        { label: "Mac", value: "mac" },
        { label: "Serial Number", value: "serialNumber" },
        { label: "Item Type", value: "itemType" }
    ];

    onlineSourceData: any = [];
    bankDataList: any = [];
    bankDestination: any = [];
    partnerData: any = [];
    popData: any = [];
    serviceAreaTypeData: any = [];
    mvnoId: string;
    resellerData: any = [];
    BUFromStaffList: any = [];
    qosPolicyData: any = [];
    trialPLanMaxLength: any = "";
    staffDataList: any = [];
    dunningRules: any;
    subareaData: any = [];
    buildingData: any = [];
    knowledgeCategoryList: any;
    knowledgeTypeList: any;
    knowledgeEventList: any;
    currency: any;
    generalCurrency: any;
    installmentTypeData: any;

    constructor(
        private toastr: ToastrService,

        private http: HttpClient,
        private spinner: NgxSpinnerService,
        private messageService: MessageService,
        private leadManagementService: LeadManagementService,
        private ticketManagementService: TicketManagementService,
        private productCategoryManagementService: ProductCategoryManagementService,
        private productService: ProuctManagementService,
        private cache: HttpResponseCache,
        private PopManagementsService: PopManagementsService,
        private coutnryManagementService: CountryManagementService,
        private stateManagementService: StateManagementService,
        private pincodeManagementService: PincodeManagementService,
        private areaManagementService: AreaManagementService,
        private cityManagementService: CityManagementService,
        private serviceAreaService: ServiceAreaService,
        private branchManagementService: BranchManagementService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        private chargeManagementService: ChargeManagementService,
        private systemconfigService: SystemconfigService,
        public partnerService: PartnerService,
        public revenueManagementService: RevenueManagementService
    ) {
        this.mvnoId = localStorage.getItem("mvnoId");
    }

    getMethod(url) {
        return this.http.get(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url);
    }

    getMethodFromCommon(url) {
        return this.http.get(RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url);
    }

    getMethodWithCache(url) {
        return this.http.get(RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url, {
            params: { from_cache: "true" } // Return the cached response if available.
        });
    }
    getForInventoryAndNature(url) {
        return this.http.get(RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url);
    }
    getMethodWithCacheInventory(url) {
        return this.http.get(RadiusConstants.SAVBILL_INVENTORY_MANAGEMENT_BASE_URL + url, {
            params: { from_cache: "true" } // Return the cached response if available.
        });
    }
    getMethodWithCacheCMS(url) {
        return this.http.get(RadiusConstants.SAVBILL_COMMON_BASE_URL + url, {
            params: { from_cache: "true" } // Return the cached response if available.
        });
    }

    getMethodForEventAndClientNames(url) {
        return this.http.get(RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url);
    }

    clearCache(url) {
        if (
            this.cache.hasStored(
                RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url + "?from_cache=true"
            )
        ) {
            this.cache.remove(
                RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url + "?from_cache=true"
            );
        }
    }

    clearCacheCMS(url) {
        if (this.cache.hasStored(RadiusConstants.SAVBILL_COMMON_BASE_URL + url + "?from_cache=true")) {
            this.cache.remove(RadiusConstants.SAVBILL_COMMON_BASE_URL + url + "?from_cache=true");
        }
    }

    getMethodWithCacheFromSales(url) {
        return this.http.get(RadiusConstants.SAVBILL_LEAD_BASE_URL + url, {
            params: { from_cache: "true" } // Return the cached response if available.
        });
    }

    postMethod(url, data) {
        return this.http.post(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url, data);
    }

    async findAllplanGroups() {
        let url = '/planGroupMappings?mode=""';
        this.getMethod(url).subscribe(
            (response: any) => {
                this.PlanGroupDetails = response.planGroupList;
                this.postPlanGroupDetails = response.planGroupList.filter(
                    data => data.plantype === "Postpaid"
                );

                this.PrepaidPlanGroupDetails = response.planGroupList.filter(
                    data => data.plantype === "Prepaid"
                );
            },
            error => { }
        );
    }
    async getBillToData() {
        let url = "/commonList/billTo";
        this.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.billToData = response.dataList;
            },
            error => { }
        );
    }
    async findAllNormalplanGroupsBySpecialPlan(specialPlanId: number) {
        let url = "/planGroupMappings?mode=NORMAL&specialPlanId=" + specialPlanId;
        this.getMethod(url).subscribe(
            (response: any) => {
                this.NormalPlanGroupDetailsBySpecialPlan = response.planGroupList;

            },
            error => { }
        );
    }

    async findAllNormalplanGroups() {
        let url = "/planGroupMappings?mode=NORMAL";
        this.getMethod(url).subscribe(
            (response: any) => {
                this.NormalPlanGroupDetails = response.planGroupList;
            },
            error => { }
        );
    }
    async findAllSepicalplanGroups() {
        let url = "/planGroupMappings?mode=SPECIAL";
        this.getMethod(url).subscribe(
            (response: any) => {
                this.SpecialPlanGroupDetails = response.planGroupList;
            },
            error => { }
        );
    }
    getValleyTypee() {
        let url = "/commonList/valleyType";
        this.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.valleyType = response.dataList;
            },
            error => { }
        );
    }
    getInsideValley() {
        let url = "/commonList/insideValley";
        this.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.insideValley = response.dataList;
            },
            error => { }
        );
    }
    getOutsideValley() {
        let url = "/commonList/outsideValley";
        this.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.outsideValley = response.dataList;
            },
            error => { }
        );
    }
    gracePeriod: any;

    private panNumberLengthSubject = new BehaviorSubject<number | null>(null);
    panNumberLength$ = this.panNumberLengthSubject.asObservable();
    private mobileNumberLengthSubject = new BehaviorSubject<{ min: number; max: number } | null>(
        null
    );
    mobileNumberLengthSubject$ = this.mobileNumberLengthSubject.asObservable();
    private commonCountryCodeSubject = new BehaviorSubject<string | null>(null);
    commonCountryCodeSubject$ = this.commonCountryCodeSubject.asObservable();
    public mobileNumberRegex: RegExp = /^\d{8,12}$/;
    public minMobileLength: number = 8;
    public maxMobileLength: number = 12;
    public AllowChangePlanWhenEnoughBalance: string;

    getsystemconfigList() {
        const url = "/system/configuration/";
        this.systemconfigService.getMethod(url).subscribe(
            (response: any) => {
                let paytmlinksms_Data = response.clientlist.filter(
                    data => data.name === "paytmlinksms_enable"
                );
                if (paytmlinksms_Data[0]?.value === "false" || paytmlinksms_Data[0]?.value === false) {
                    this.ifPaytmLinkSendBtn = false;
                } else {
                    this.ifPaytmLinkSendBtn = true;
                }
                let MOBILE_NUMBER_Data = response.clientlist.find(data => data.name === "MOBILE_NUMBER");
                this.commonMoNumberLength = parseInt(MOBILE_NUMBER_Data?.value) || 0;
                // this.mobileNumberLengthSubject.next(this.commonMoNumberLength);

                const mobileNumberRegexStr = MOBILE_NUMBER_Data?.value || "^\\d{8,12}$";

                this.mobileNumberRegex = new RegExp(mobileNumberRegexStr);
                let fixedMatch = /\\d\{(\d+)\}/.exec(mobileNumberRegexStr); // matches \d{10}
                let rangeMatch = /\\d\{(\d+),(\d+)\}/.exec(mobileNumberRegexStr); // matches \d{8,12}

                if (rangeMatch) {
                    this.minMobileLength = parseInt(rangeMatch[1], 10);
                    this.maxMobileLength = parseInt(rangeMatch[2], 10);
                } else if (fixedMatch) {
                    this.minMobileLength = parseInt(fixedMatch[1], 10);
                    this.maxMobileLength = this.minMobileLength;
                } else {
                    // fallback or invalid pattern
                    this.minMobileLength = 8;
                    this.maxMobileLength = 12;
                }

                this.mobileNumberLengthSubject.next({
                    min: this.minMobileLength,
                    max: this.maxMobileLength
                });

                const PAN_NUMBER = response.clientlist.find(
                    (data: any) => data.name === "PAN_NUMBER_LENGTH"
                );
                this.commonPanNumberLength = PAN_NUMBER ? parseInt(PAN_NUMBER.value, 10) : 0;
                this.panNumberLengthSubject.next(this.commonPanNumberLength);
                const COUNTRY_CODE_Data = response.clientlist.filter(data => data.name === "COUNTRY_CODE");
                this.commonCountryCode = COUNTRY_CODE_Data[0]?.value ? COUNTRY_CODE_Data[0].value : "+91";
                this.commonCountryCodeSubject.next(this.commonCountryCode);

                // let COUNTRY_CODE_Data = response.clientlist.filter(data => data.name === "COUNTRY_CODE");
                // this.commonCountryCode = COUNTRY_CODE_Data[0]?.value ? COUNTRY_CODE_Data[0]?.value : "+91";
                this.gracePeriod = response.clientlist.filter(data => data.name === "graceperiod");

                this.generalCurrency = response.clientlist.find(
                    data => data.name === "CURRENCY_FOR_PAYMENT"
                );

                let trialPlanPeriodThreshold = response.clientlist.filter(
                    data => data.name === "trialPlanPeriodThreshold"
                );
                this.trialPLanMaxLength = trialPlanPeriodThreshold[0]?.value;
                this.AllowChangePlanWhenEnoughBalance = response.clientlist.find(
                    data => data.name === "AllowChangePlanWhenEnoughBalance"
                )?.value;

                // let ONLINE_SOURCE_OPTION = response.clientlist.filter(
                //   data => data.name === "paymentonlinesource"
                // );
                // let onlineList = ONLINE_SOURCE_OPTION[0].value;
                // const split_string = onlineList.split(",");
                // split_string.forEach(element => {
                //   this.onlineSourceData.push({ label: element, value: element });
                // });
            },
            (error: any) => { }
        );
    }

    getCustomer() {
        const url = "/customers/list";
        let custerlist = {};
        this.postMethod(url, custerlist).subscribe(
            (response: any) => {
                this.customerAllList = response.customerList;
            },
            (error: any) => {
                console.log(error, "error");
            }
        );
    }
    async getOnlineSourceData(payMode) {
        this.onlineSourceData = [];
        const url = "/commonList/generic/" + payMode;
        this.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.onlineSourceData = response.dataList;
            },
            (error: any) => {
                this.onlineSourceData = [];
                console.log(error, "error");
            }
        );
    }

    getCustomerStatus() {
        const url = "/commonList/generic/custStatus";
        this.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.CustomerStatusValue = response.dataList.filter(
                    status => status.value !== "NewActivation" && status.value !== "Reject" && status.value !== "Staff" && status.value !== "Relocated" && status.value !== "Not service ready" && status.value !== "Suspend" && status.value !== "Office Accounts" && status.value !== "Complimentary"
                );
                this.customerChangeStatusValue = response.dataList.filter(
                    status => status.value !== "NewActivation" && status.value !== "Reject"
                );
                this.CustomerNewStatusValue = response.dataList.filter(
                    status => status.value !== "NewActivation" && status.value !== "Reject"
                );
                response.dataList = response.dataList.filter(item => item.text !== "Rejected");
            },
            (error: any) => { }
        );
    }

    async getserviceAreaList() {
        const url = "/serviceArea/dropdown/all";
        this.serviceAreaService.getMethod(url).subscribe(
            (response: any) => {
                this.serviceAreaList = response.dataList;
            },
            (error: any) => { }
        );
    }

    async getserviceAreaListForCafCustomer() {
        const url = "/serviceArea/dropdown/all/caf/customer";
        this.serviceAreaService.getMethod(url).subscribe(
            (response: any) => {
                if (response.dataList) {
                    // Map the response to add '(UnderDeveloped)' for relevant items
                    this.serviceAreaList = response.dataList.map((item: any) => ({
                        id: item.id,
                        name: item.name,
                        isUnderDevelopment: item.serviceAreaStatus === "UnderDevelopment"
                    }));
                }
            },
            (error: any) => {
                console.error("Error fetching service area list:", error);
            }
        );
    }
    filterserviceAreaList() {
        let serviceAreaData = [];
        let serviceArea: any = [];

        serviceArea = localStorage.getItem("serviceArea");
        let userID = localStorage.getItem("userId");

        let serviceAreaArray = JSON.parse(serviceArea);
        const url = "/serviceArea/dropdown/all";
        this.serviceAreaService.getMethod(url).subscribe(
            async (response: any) => {
                await response.dataList.forEach(element => {
                    if (userID == element.createdById || element.createdById == "1") {
                        serviceAreaData.push(element);
                    } else {
                        serviceAreaArray.forEach(serID => {
                            if (element.id == serID) {
                                serviceAreaData.push(element);
                            }
                        });
                    }
                });

                this.serviceAreaList = Array.from(
                    new Map(serviceAreaData.map(item => [item.id, item])).values()
                );
                this.branchByServiceAreaID(this.serviceAreaList.map(item => item.id));
                // this.serviceAreaList = response.dataList;
            },
            (error: any) => { }
        );
    }

    getPriceBookListAll() {
        const url = "/priceBook/all";
        this.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.priceBookList = response.dataList;
                // console.log("priceBookList", this.priceBookList);
            },
            (error: any) => { }
        );
    }

    getActivePriceBookListAll() {
        const url = "/priceBook/active";
        this.getMethod(url).subscribe(
            (response: any) => {
                this.activePriceBookList = response.dataList;
                //console.log("priceBookList", this.activePriceBookList);
            },
            (error: any) => { }
        );
    }

    getTaxAllListAll() {
        const url = "/taxes/all";
        this.getMethod(url).subscribe(
            (response: any) => {
                this.taxAllList = response.taxlist;
                // console.log("taxAllList", this.taxAllList);
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: 'error',
                //   summary: 'Error',
                //   detail: error.error.ERROR,
                //   icon: 'far fa-times-circle',
                // })
            }
        );
    }

    getchargeAll() {
        const url = "/charge/all";
        this.getMethodWithCacheCMS(url).subscribe(
            (response: any) => {
                this.chargeList = response.chargelist;
                // console.log("chargeList", this.chargeList);
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: 'error',
                //   summary: 'Error',
                //   detail: error.error.ERROR,
                //   icon: 'far fa-times-circle',
                // })
            }
        );
    }

    getpartnerAll() {
        const url = "/partner/all";
        this.coutnryManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.partnerAllNAme = response.partnerlist;
                // this.partnerAllNAme = response.partnerlist
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: 'error',
                //   summary: 'Error',
                //   detail: error.error.ERROR,
                //   icon: 'far fa-times-circle',
                // })
            }
        );
    }

    filterPartnerAll() {
        const url = "/partner/all";

        // let serviceAreaData =[]
        // let serviceArea :any =[]
        // serviceArea = localStorage.getItem("serviceArea")
        // let userID = localStorage.getItem("userId")
        // let serviceAreaArray =JSON.parse(serviceArea);

        this.coutnryManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.partnerAllNAme = response.partnerlist;
                // for (let j = 0; j < response.partnerlist.length; j++) {
                //   if (
                //     response.partnerlist[j].serviceAreaIds.includes(
                //       Number(localStorage.getItem("serviceArea"))
                //     ) == true
                //   ) {
                //     this.partnerAllNAme.push(response.partnerlist[j]);
                //   }
                // }
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: 'error',
                //   summary: 'Error',
                //   detail: error.error.ERROR,
                //   icon: 'far fa-times-circle',
                // })
            }
        );
    }

    getChargeForCustomer() {
        const url = "/charge/getChargeForCustomer";
        this.getMethod(url).subscribe(
            (response: any) => {
                this.ChargeForCustomerData = response.chargelist;
                // console.log("ChargeForCustomerData", this.ChargeForCustomerData);
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: 'error',
                //   summary: 'Error',
                //   detail: error.error.ERROR,
                //   icon: 'far fa-times-circle',
                // })
            }
        );
    }

    getplanservice() {
        const url = "/planservice/all";
        this.getMethod(url).subscribe(
            (response: any) => {
                this.copyplanserviceData = response.serviceList;
                this.planserviceData = response.serviceList;
                // console.log("planserviceData", this.planserviceData);
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: 'error',
                //   summary: 'Error',
                //   detail: error.error.ERROR,
                //   icon: 'far fa-times-circle',
                // })
            }
        );
    }

    getAllPinCodeNumber() {
        const url = "/pincode/getAll";
        this.pincodeManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.allpincodeNumber = response.dataList;
                // console.log("allpincodeNumber", this.allpincodeNumber);
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: 'error',
                //   summary: 'Error',
                //   detail: error.error.ERROR,
                //   icon: 'far fa-times-circle',
                // })
            }
        );
    }

    getAllPinCodeData() {
        const url = "/pincode/getAll";
        this.pincodeManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.allpincodeNumber = response.dataList;
                // console.log("allpincodeNumber", this.allpincodeNumber);
            },
            (error: any) => { }
        );
    }

    getALLAreaData() {
        const url = "/area/all";
        this.areaManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.areaData = response.dataList;
                // console.log("areaData", this.areaData);
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: 'error',
                //   summary: 'Error',
                //   detail: error.error.ERROR,
                //   icon: 'far fa-times-circle',
                // })
            }
        );
    }

    getALLArea() {
        const url = "/area/all";
        this.areaManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.areaData = response.dataList;
                // console.log("areaData", this.areaData);
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: 'error',
                //   summary: 'Error',
                //   detail: error.error.ERROR,
                //   icon: 'far fa-times-circle',
                // })
            }
        );
    }

    getAllSubAreaData() {
        const url = "/subarea/all";
        this.areaManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.subareaData = response.dataList;
                // console.log("areaData", this.areaData);
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: 'error',
                //   summary: 'Error',
                //   detail: error.error.ERROR,
                //   icon: 'far fa-times-circle',
                // })
            }
        );
    }

    getAllBuildingData() {
        const url = "/buildingmgmt/all";
        this.areaManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.buildingData = response.dataList;
                // console.log("areaData", this.areaData);
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: 'error',
                //   summary: 'Error',
                //   detail: error.error.ERROR,
                //   icon: 'far fa-times-circle',
                // })
            }
        );
    }

    getCommonListTitleData() {
        const url = "/commonList/title";
        this.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.commonListTitleData = response.dataList;
                // console.log("commonListTitleData", this.commonListTitleData);
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: 'error',
                //   summary: 'Error',
                //   detail: error.error.ERROR,
                //   icon: 'far fa-times-circle',
                // })
            }
        );
    }

    getCommonListPaymentData() {
        const url = "/commonList/paymentMode";
        this.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.commonListPaymentData = response.dataList;
                // console.log("commonListPaymentData", this.commonListPaymentData);
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: 'error',
                //   summary: 'Error',
                //   detail: error.error.ERROR,
                //   icon: 'far fa-times-circle',
                // })
            }
        );
    }

    getIppoolData() {
        const url = "/ippool/all";
        this.getMethodWithCacheCMS(url).subscribe(
            (response: any) => {
                this.ippoolData = response.dataList;
                // console.log("ippoolData", this.ippoolData);
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: 'error',
                //   summary: 'Error',
                //   detail: error.error.ERROR,
                //   icon: 'far fa-times-circle',
                // })
            }
        );
    }

    getPostpaidplanData() {
        const url = "/postpaidplan/all";
        this.getMethod(url).subscribe(
            (response: any) => {
                this.postpaidplanData = response.postpaidplanList;
            },
            (error: any) => { }
        );
    }

    getPOSTpaidNormalPlan() {
        const url = "/postpaidplan/all?type=NORMAL";
        this.getMethod(url).subscribe(
            (response: any) => {
                this.NomalpostpaidplanData = response.postpaidplanList;
            },
            (error: any) => { }
        );
    }

    getPOSTpaidSpecialPlan() {
        const url = "/postpaidplan/all?type=SPECIAL";
        this.getMethod(url).subscribe(
            (response: any) => {
                this.specialpostpaidplanData = response.postpaidplanList;
            },
            (error: any) => { }
        );
    }

    getCountryList() {
        const url = "/country/all";
        this.coutnryManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.countryListData = response.countryList;
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: 'error',
                //   summary: 'Error',
                //   detail: error.error.ERROR,
                //   icon: 'far fa-times-circle',
                // })
            }
        );
    }

    getStateList() {
        const url = "/state/all";
        this.stateManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.stateListData = response.stateList;
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: 'error',
                //   summary: 'Error',
                //   detail: error.error.ERROR,
                //   icon: 'far fa-times-circle',
                // })
            }
        );
    }

    getCityList() {
        const url = "/city/all";
        this.cityManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.cityListData = response.cityList;
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: 'error',
                //   summary: 'Error',
                //   detail: error.error.ERROR,
                //   icon: 'far fa-times-circle',
                // })
            }
        );
    }

    getChargeTypeByList(serviceId = "", currency?) {
        let queryParam = "";
        if (serviceId !== "") {
            queryParam = `?serviceId=${serviceId}`;
        }
        const url = "/charge/ByType/CUSTOMER_DIRECT" + queryParam;
        this.getMethod(url).subscribe(
            (response: any) => {
                this.chargeByTypeData = response.chargelist;
                if (currency) {
                    this.chargeByTypeData = this.chargeByTypeData.filter(
                        charge => charge.currency == currency
                    );
                }
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: 'error',
                //   summary: 'Error',
                //   detail: error.error.ERROR,
                //   icon: 'far fa-times-circle',
                // })
            }
        );
    }

    getBillRunMasterList() {
        const url = "/billrun/All";
        this.revenueManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.billRunMasterList = response.billRunlist;
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: 'error',
                //   summary: 'Error',
                //   detail: error.error.ERROR,
                //   icon: 'far fa-times-circle',
                // })
            }
        );
    }

    getPlanPurchaseType() {
        const url = "/commonList/generic/planPurchaseType";
        this.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.planPurchaseTypeData = response.dataList.filter(
                    type => type.text !== "New" && type.text !== "Upgrade"
                );
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: 'error',
                //   summary: 'Error',
                //   detail: error.error.ERROR,
                //   icon: 'far fa-times-circle',
                // })
            }
        );
        return this.planPurchaseTypeData;
    }

    getPostpaidCustomer() {
        const url = "/customers/list/" + RadiusConstants.CUSTOMER_TYPE.POSTPAID;
        let custerlist = {
            page: 1,
            pageSize: 10000
        };
        this.postMethod(url, custerlist).subscribe(
            (response: any) => {
                this.postpaidCustomerList = response.customerList;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getParentPostpaidCustomer() {
        const url = "/parentcustomer/list/" + RadiusConstants.CUSTOMER_TYPE.POSTPAID;
        this.getMethod(url).subscribe(
            (response: any) => {
                this.postpaidParentCustomerList = response.parentCustomers;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');



            }
        );
    }

    getParentPrepaidCustomer() {
        const url = "/parentcustomer/list/" + RadiusConstants.CUSTOMER_TYPE.PREPAID;
        this.getMethod(url).subscribe(
            (response: any) => {
                this.prepaidParentCustomerList = response.parentCustomers;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');



            }
        );
    }

    getTeamList() {
        const url = "/teams/getAllTeamsWithoutPagination";
        this.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.teamListData = response.dataList;
                this.activeTeamListData = response.dataList.filter(item => item.status == "active");
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');



            }
        );
    }

    getMatrixList() {
        const url = "/matrix/all";
        this.getMethod(url).subscribe(
            (response: any) => {
                this.tatMatricsData = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getActiveMatrixList() {
        const url = "/matrix/status";
        this.getMethod(url).subscribe(
            (response: any) => {
                this.activeTatMatricsData = response;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    partnersFromSalesCRMS: any;
    getPartnersFromSalesCRMS() {
        const url = "/leadMaster/findAll/Partner";
        this.leadManagementService.getMethod(url).subscribe((res: any) => {
            this.partnersFromSalesCRMS = res.partnerList;
        });
    }

    branchesFromSalesCRMS: any;
    getBranchesFromSalesCRMS() {
        const url = "/leadMaster/findAll/Branch";
        this.leadManagementService.getMethod(url).subscribe((res: any) => {
            this.branchesFromSalesCRMS = res.branchList;
        });
    }

    serviceAreasFromSalesCRMS: any;
    getServiceAreasFromSalesCRMS() {
        const url = "/leadMaster/findAll/ServiceArea";
        this.leadManagementService.getMethod(url).subscribe((res: any) => {
            this.serviceAreasFromSalesCRMS = res.serviceAreaList;
        });
    }

    customersFromSalesCRMS: any;
    getCustomersFromSalesCRMS() {
        const url = "/leadMaster/findAll/Customers";
        this.leadManagementService.getMethod(url).subscribe((res: any) => {
            this.customersFromSalesCRMS = res.customersList;
        });
    }

    staffsFromSalesCRMS: any;
    getStaffsFromSalesCRMS() {
        const url = "/leadMaster/findAll/StaffUser";
        this.leadManagementService.getMethod(url).subscribe((res: any) => {
            this.staffsFromSalesCRMS = res.staffUserList;
        });
    }

    getRegionData() {
        const url = "/region/all";
        this.stateManagementService.getMethodWithCache(url).subscribe((res: any) => {
            this.regionDataList = res.dataList;
        });
    }

    priorityTicketData = [];
    getTicketPriority() {
        const url = "/commonList/ticket_priority";
        this.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.priorityTicketData = response.dataList;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getAllActiveBranch() {
        const url = "/branchManagement/findAll";
        this.branchManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.activeBranchList = response.dataList.filter(branch => branch.status == "Active");
            },
            (error: any) => {

                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


            }
        );
    }
    getAllActiveStaff() {
        const url = "/staffList/dropdown/all";
        this.savbillCommonBaseService.get(url).subscribe(
            (response: any) => {
                this.activeStaffList = response.dataList; //.filter(staff => staff.status == "Active");
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');



            }
        );
    }

    getBusinessUnitList() {
        const url = "/businessUnit/all";
        this.coutnryManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.businessUnitList = response.dataList.filter(staff => staff.status == "Active");
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');



            }
        );
    }

    getAllActiveProduct() {
        const url = "/product/getAllActiveProduct";
        this.productService.getMethod(url).subscribe(
            (response: any) => {
                this.activeProductList = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');



            }
        );
    }

    getAllActiveInward() {
        const url = "/inwards/all";
        this.getMethodWithCacheInventory(url).subscribe(
            (response: any) => {
                this.activeInwardList = response.dataList.filter(inward => inward.status == "ACTIVE");
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');



            }
        );
    }

    getOwnershipType() {
        const url = "/commonList/generic/OWNERSHIP_TYPE";
        this.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.ownershipTypeList = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


            }
        );
    }

    getItemStatusList() {
        const url = "/commonList/generic/ITEM_STATUS_MANAGEMENT";
        this.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.itemStatusList = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');



            }
        );
    }

    getItemConditionList() {
        const url = "/commonList/generic/ITEM_CONDITION_MANAGEMENT";
        this.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.itemConditionList = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');



            }
        );
    }

    getWarrantyStatusList() {
        const url = "/commonList/generic/ITEM_WARRANTY_MANAGEMENT";
        this.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.warrantyStatusList = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');



            }
        );
    }

    getPOPList() {
        const url = "/popmanagement/all";
        this.PopManagementsService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.popListData = response.dataList;
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');



            }
        );
    }

    popListFromSalesCrms: any;
    getPopDataFromSalesCrms() {
        const url = "/leadMaster/findAll/popManagement";
        this.leadManagementService.getMethod(url).subscribe(
            async (response: any) => {
                this.popListFromSalesCrms = await response.popManagementList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');



            }
        );
    }

    getTATForTicketList() {
        const url = "/tickettatmatrix/searchByStatus";
        this.ticketManagementService.getMethod(url).subscribe(
            (response: any) => {

                this.TATForTicketData = response.dataList;
            },
            (error: any) => { }
        );
    }

    getCustomerType() {
        const url = "/commonList/Customer_Type";
        const custerlist = {};
        this.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.customertypeList = response.dataList;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');



            }
        );
    }

    getCustomerSubType(data) {
        const url = "/commonList/" + data;
        const custerlist = {};
        this.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.customerSubtypeList = response.dataList;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');



            }
        );
    }

    getSectorType() {
        const url = "/commonList/Customer_Sector";
        const custerlist = {};
        this.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.sectortypeList = response.dataList;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');



            }
        );
    }

    getActiveProductCategoryList() {
        const url = "/productCategory/getAllActiveProductCategoriesByCB";
        this.productCategoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.productCategoryList = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');



            }
        );
    }

    getBankDetail() {
        const url = "/bankManagement/searchByStatus?banktype=other";
        this.savbillCommonBaseService.get(url).subscribe(
            (response: any) => {
                this.bankDataList = response.dataList;
                // this.bankDestination = response.dataList.banktype
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');



            }
        );
    }

    getBankDestinationDetail() {
        const url = "/bankManagement/searchByStatus?banktype=operator";
        this.savbillCommonBaseService.get(url).subscribe(
            (response: any) => {
                // this.bankDataList = response.dataList.banktype;
                this.bankDestination = response.dataList;
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    getPartner() {
        const url = "/partner/all";
        this.coutnryManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.partnerData = response.partnerlist;
            },
            (error: any) => { }
        );
    }

    getServiceAreaType() {
        const url = "/commonList/generic/service_Area_Type";
        this.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.serviceAreaTypeData = response.dataList;
            },
            (error: any) => { }
        );
    }

    getPop() {
        const url = "/popmanagement/all";
        this.PopManagementsService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.popData = response.dataList;
            },
            (error: any) => { }
        );
    }
    customerTypeSearchOption = [
        { label: "Parent", value: "custparent" },
        { label: "Child", value: "custchild" },
        { label: "Individual", value: "custindividual" }
    ];

    subScriptionMode = [
        { label: "Free", value: "free" },
        { label: "Paid", value: "paid" },
        { label: "Funded", value: "funded" },
        { label: "Barter", value: "barter" }
    ];

    planCreationType() {
        const planBindingType = localStorage.getItem("planBindingType");
        this.isPlanOnDemand = planBindingType === "On-Demand";
    }

    getAllReseller() {
        this.mvnoId = localStorage.getItem("mvnoId");
        this.getMethod(`/Reseller/getAllResellers?mvnoId=${this.mvnoId}`).subscribe((response: any) => {
            this.resellerData = response.resellers.data;
        });
    }

    getBUFromStaff() {
        this.savbillCommonBaseService
            .post(`/staffuser/list?product=BSS`, {})
            .subscribe((response: any) => {
                this.BUFromStaffList = response.staffUserlist;
            });
    }

    //need to use this common api for get all branches by service area ids replace this method from whole project when get time
    branchByServiceAreaID(ids) {
        let url = "/branchManagement/getAllBranchesByServiceAreaId";
        this.savbillCommonBaseService.post(url, ids).subscribe((response: any) => {
            this.branchesByServiceArea = response.dataList;
        });
    }

    getQosPolicy() {
        const url = "/qosPolicy/all";
        this.getMethodWithCache(url).subscribe((response: any) => {
            this.qosPolicyData = response.dataList;
        });
    }
    data = [];
    getStaffDetailById() {
        let currentPageForStaff;
        const data = {};
        let staffData: any = [];
        const url = "/staffuser/list?product=BSS";
        this.savbillCommonBaseService.post(url, data).subscribe((response: any) => {
            staffData = response.staffUserlist;
            this.staffDataList.forEach((element, i) => {
                element.displayLabel = element.fullName + " (Ph: " + element.phone + ")";
                this.data.push(element.id);
            });
        });
    }
    customerInventorySearchOption = [
        { label: "Name", value: "name" },
        { label: "Username", value: "username" }
    ];

    getCustomerCategory() {
        let url = "/commonList/CustomerCategory";
        this.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.dunningRules = response.dataList;

            },
            error => { }
        );
    }

    getAllKnowledgeCategory() {
        let url = "/commonList/byType/docCategory";
        this.getMethodFromCommon(url).subscribe(
            (response: any) => {
                this.knowledgeCategoryList = response.dataList;
            },
            error => { }
        );
    }

    getAllKnowledgeType() {
        let url = "/commonList/byType/docFor";
        this.getMethodFromCommon(url).subscribe(
            (response: any) => {
                this.knowledgeTypeList = response.dataList;
            },
            error => { }
        );
    }

    getAllKnowledgeEvent() {
        let url = "/commonList/byType/knowledgeBase";
        this.getMethodFromCommon(url).subscribe(
            (response: any) => {
                this.knowledgeEventList = response.dataList;
            },
            error => { }
        );
    }

    getAllCurrencyData() {
        let url = "/commonList/multipleCurrency";
        this.getMethodFromCommon(url).subscribe(
            (response: any) => {
                this.currency = response.dataList;
            },
            error => { }
        );
    }

    getInstallmentTypeData() {
        let url = "/commonList/installmentFrequency";
        this.getMethodFromCommon(url).subscribe(
            (response: any) => {
                this.installmentTypeData = response.dataList;
            },
            error => { }
        );
    }
}
