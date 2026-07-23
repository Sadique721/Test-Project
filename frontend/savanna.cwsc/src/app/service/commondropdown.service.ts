import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { MessageService } from "primeng/api";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import * as CryptoJS from "crypto-js";
import { BehaviorSubject } from "rxjs";

@Injectable({
  providedIn: "root"
})
export class CommondropdownService {
  ifPaytmLinkSendBtn = true;
  cityListData: any[] = [];
  counttryListData: any[] = [];
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
  SpecialPlanGroupDetails: any = [];
  teamListData: any;
  generalCurrency: any;
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

  commonCountryCode = "";
  commonMoNumberLength = 0;
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

  customerSearchOption = [
    { label: "Firstname", value: "name" },
    { label: "Username", value: "username" },
    { label: "Fullname", value: "fullname" },
    { label: "Email", value: "email" },
    { label: "Phone", value: "mobile" },
    { label: "Service", value: "service" },
    { label: "Plan", value: "plan" },
    { label: "Plan Group", value: "planGroup" },
    { label: "Service Area", value: "serviceareaName" },
    { label: "MAC-based", value: "macaddress" },
    { label: "Status", value: "status" },
    { label: "CAF Status", value: "cafStatus" },
    { label: "Any", value: "any" },
    { label: "PartnerName", value: "partnerName" },
    { label: "Branch", value: "branchName" },
    { label: "Customer Type", value: "custtype" },
    { label: "Circuit Name", value: "circuitName" },
    { label: "Current Assigned Staff", value: "currentAssigneeName" },
    { label: "Current Assigned Team", value: "currentAssignedTeam" },
    { label: "CAF Created Date", value: "cafCreatedDate" },
    { label: "CAF Number", value: "cafNo" },
    { label: "Static IP", value: "staticIp" },
    { label: "Inventory Serial Number", value: "inventorySerial" },
    { label: "Plan Expiry Date", value: "expiryDate" }
    // { label: "Bill to Subisu", value: "billTo" },
  ];

  planSearchOption = [
    { label: "Plan Name", value: "planname" },
    { label: "Plan Type", value: "plantype" },
    { label: "Validity", value: "planvalidity" },
    { label: "Price", value: "planprice" },
    { label: "Service Area", value: "servicearea" },
    { label: "Status", value: "planstatus" },
    { label: "Branch", value: "planbranch" },
    { label: "Franchise", value: "planfranchise" },
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
  secretKey = RadiusConstants.SECRET_KEY;

  constructor(
    private http: HttpClient,
    private spinner: NgxSpinnerService,
    private toastr: ToastrService
    // private commonDropdownService:CommondropdownService,
  ) {
    this.mvnoId = localStorage.getItem("mvnoId");
  }

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

  getMethod(url) {
    return this.http.get(RadiusConstants.SAVBILL_COMMON_BASE_URL + url);
  }

  commonGetMethod(url) {
    return this.http.get(RadiusConstants.SAVBILL_COMMON_BASE_URL + url);
  }

  getMethodWithCache(url) {
    return this.http.get(RadiusConstants.SAVBILL_API_GATEWAY_COMMON_MANAGEMENT + url, {
      params: { from_cache: "true" } // Return the cached response if available.
    });
  }

  getMethodForCommonList(url) {
    return this.http.get(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url, {
      params: { from_cache: "true" } // Return the cached response if available.
    });
  }

  postMethod(url, data) {
    return this.http.post(RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL + url, data);
  }

  commonPostMethod(url, data) {
    return this.http.post(RadiusConstants.SAVBILL_COMMON_BASE_URL + url, data);
  }

  async findAllplanGroups() {
    this.spinner.show();
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

        this.spinner.hide();
      },
      error => {
        this.spinner.hide();
      }
    );
  }
  async getBillToData() {
    this.spinner.show();
    let url = "/commonList/billTo";
    this.getMethodForCommonList(url).subscribe(
      (response: any) => {
        this.billToData = response.dataList;
        this.spinner.hide();
      },
      error => {
        this.spinner.hide();
      }
    );
  }
  async findAllNormalplanGroups() {
    this.spinner.show();
    let url = "/planGroupMappings?mode=NORMAL";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.NormalPlanGroupDetails = response.planGroupList;
        this.spinner.hide();
      },
      error => {
        this.spinner.hide();
      }
    );
  }
  async findAllSepicalplanGroups() {
    this.spinner.show();
    let url = "/planGroupMappings?mode=SPECIAL";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.SpecialPlanGroupDetails = response.planGroupList;
        this.spinner.hide();
      },
      error => {
        this.spinner.hide();
      }
    );
  }
  getValleyTypee() {
    this.spinner.show();
    let url = "/commonList/valleyType";
    this.getMethodForCommonList(url).subscribe(
      (response: any) => {
        this.valleyType = response.dataList;
        this.spinner.hide();
      },
      error => {
        this.spinner.hide();
      }
    );
  }
  getInsideValley() {
    this.spinner.show();
    let url = "/commonList/insideValley";
    this.getMethodForCommonList(url).subscribe(
      (response: any) => {
        this.insideValley = response.dataList;
        this.spinner.hide();
      },
      error => {
        this.spinner.hide();
      }
    );
  }
  getOutsideValley() {
    this.spinner.show();
    let url = "/commonList/outsideValley";
    this.getMethodForCommonList(url).subscribe(
      (response: any) => {
        this.outsideValley = response.dataList;
        this.spinner.hide();
      },
      error => {
        this.spinner.hide();
      }
    );
  }
  gracePeriod: any;

  getsystemconfigList() {
    this.spinner.show();

    const url = "/system/configuration/";
    this.getMethod(url).subscribe(
      (response: any) => {
        let paytmlinksms_Data = response.clientlist.filter(
          data => data.name === "paytmlinksms_enable"
        );
        if (paytmlinksms_Data[0]?.value === "false" || paytmlinksms_Data[0]?.value === false) {
          this.ifPaytmLinkSendBtn = false;
        } else {
          this.ifPaytmLinkSendBtn = true;
        }

        let MOBILE_NUMBER_Data = response.clientlist?.filter(data => data.name === "MOBILE_NUMBER");

        const pattern = MOBILE_NUMBER_Data[0]?.value; // e.g. "\d{10}"

        const match = pattern.match(/\{(\d+)\}/); // extract number inside { }

        this.commonMoNumberLength = match ? parseInt(match[1]) : 10;

        let COUNTRY_CODE_Data = response.clientlist?.filter(data => data.name === "COUNTRY_CODE");
        this.commonCountryCode = COUNTRY_CODE_Data[0]?.value;
        this.gracePeriod = response.clientlist.filter(data => data.name === "graceperiod");
        this.generalCurrency = response.clientlist.find(
          data => data.name === "CURRENCY_FOR_PAYMENT"
        );
        localStorage.setItem("currency", this.generalCurrency);
        console.log("generalCurrency", this.generalCurrency);
        let trialPlanPeriodThreshold = response.clientlist?.filter(
          data => data.name === "trialPlanPeriodThreshold"
        );
        this.trialPLanMaxLength = trialPlanPeriodThreshold[0]?.value;

        // let ONLINE_SOURCE_OPTION = response.clientlist.filter(
        //   data => data.name === "paymentonlinesource"
        // );
        // let onlineList = ONLINE_SOURCE_OPTION[0].value;
        // const split_string = onlineList.split(",");
        // split_string.forEach(element => {
        //   this.onlineSourceData.push({ label: element, value: element });
        // });
        this.AllowChangePlanWhenEnoughBalance = response.clientlist.find(
          data => data.name === "AllowChangePlanWhenEnoughBalance"
        )?.value;
        this.spinner.hide();
      },
      (error: any) => {
        this.spinner.hide();
      }
    );
  }

  getCustomer() {
    this.spinner.show();
    const url = "/customers/list";
    let custerlist = {};
    this.postMethod(url, custerlist).subscribe(
      (response: any) => {
        this.customerAllList = response.customerList;
        this.spinner.hide();
      },
      (error: any) => {
        console.log(error, "error");
        this.spinner.hide();
      }
    );
  }
  getOnlineSourceData(payMode) {
    this.spinner.show();
    this.onlineSourceData = [];
    const url = "/commonList/generic/" + payMode;
    this.getMethodForCommonList(url).subscribe(
      (response: any) => {
        this.onlineSourceData = response.dataList;
        this.spinner.hide();
      },
      (error: any) => {
        this.onlineSourceData = [];
        console.log(error, "error");
        this.spinner.hide();
      }
    );
  }

  getCustomerStatus() {
    this.spinner.show();
    const url = "/commonList/generic/custStatus";
    this.getMethodForCommonList(url).subscribe(
      (response: any) => {
        this.CustomerStatusValue = response.dataList.filter(
          status =>
            status.value !== "NewActivation" &&
            status.value !== "Reject" &&
            status.value !== "Terminate"
        );
        this.customerChangeStatusValue = response.dataList.filter(
          status => status.value !== "NewActivation" && status.value !== "Reject"
        );
        this.spinner.hide();
      },
      (error: any) => {
        this.spinner.hide();
      }
    );
  }

  getserviceAreaList() {
    this.spinner.show();
    const url = "/serviceArea/all";
    this.commonGetMethod(url).subscribe(
      (response: any) => {
        this.serviceAreaList = response.dataList;
        this.spinner.hide();
      },
      (error: any) => {
        this.spinner.hide();
      }
    );
  }

  filterserviceAreaList() {
    let serviceAreaData = [];
    let serviceArea: any = [];
    this.spinner.show();
    serviceArea = localStorage.getItem("serviceArea");
    let userID = localStorage.getItem("userId");
    let serviceAreaArray = JSON.parse(serviceArea);
    const url = "/serviceArea/all";
    this.commonGetMethod(url).subscribe(
      async (response: any) => {
        await response.dataList.forEach(element => {
          if (userID == element.createdById) {
            serviceAreaData.push(element);
          } else {
            serviceAreaArray.forEach(serID => {
              if (element.id == serID) {
                serviceAreaData.push(element);
              }
            });
          }
        });
        this.serviceAreaList = serviceAreaData;
        this.branchByServiceAreaID(this.serviceAreaList.map(item => item.id));
        // this.serviceAreaList = response.dataList;
        this.spinner.hide();
      },
      (error: any) => {
        this.spinner.hide();
      }
    );
  }

  getPriceBookListAll() {
    this.spinner.show();
    const url = "/priceBook/all";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.priceBookList = response.dataList;
        // console.log("priceBookList", this.priceBookList);
        this.spinner.hide();
      },
      (error: any) => {
        this.spinner.hide();
      }
    );
  }

  getActivePriceBookListAll() {
    this.spinner.show();
    const url = "/priceBook/active";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.activePriceBookList = response.dataList;
        //console.log("priceBookList", this.activePriceBookList);
        this.spinner.hide();
      },
      (error: any) => {
        this.spinner.hide();
      }
    );
  }

  getTaxAllListAll() {
    this.spinner.show();
    const url = "/taxes/all";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.taxAllList = response.taxlist;
        // console.log("taxAllList", this.taxAllList);
        this.spinner.hide();
      },
      (error: any) => {
        // this.messageService.add({
        //   severity: 'error',
        //   summary: 'Error',
        //   detail: error.error.ERROR,
        //   icon: 'far fa-times-circle',
        // })
        this.spinner.hide();
      }
    );
  }

  getchargeAll() {
    this.spinner.show();
    const url = "/charge/all";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.chargeList = response.chargelist;
        // console.log("chargeList", this.chargeList);
        this.spinner.hide();
      },
      (error: any) => {
        // this.messageService.add({
        //   severity: 'error',
        //   summary: 'Error',
        //   detail: error.error.ERROR,
        //   icon: 'far fa-times-circle',
        // })
        this.spinner.hide();
      }
    );
  }

  getpartnerAll() {
    this.spinner.show();
    const url = "/partner/all";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.partnerAllNAme = response.partnerlist;
        // this.partnerAllNAme = response.partnerlist
        console.log("partnerAllNAme", response);
        this.spinner.hide();
      },
      (error: any) => {
        // this.messageService.add({
        //   severity: 'error',
        //   summary: 'Error',
        //   detail: error.error.ERROR,
        //   icon: 'far fa-times-circle',
        // })
        this.spinner.hide();
      }
    );
  }

  filterPartnerAll() {
    this.spinner.show();
    const url = "/partner/all";

    // let serviceAreaData =[]
    // let serviceArea :any =[]
    // serviceArea = localStorage.getItem("serviceArea")
    // let userID = localStorage.getItem("userId")
    // let serviceAreaArray =JSON.parse(serviceArea);

    this.getMethod(url).subscribe(
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
        this.spinner.hide();
      },
      (error: any) => {
        // this.messageService.add({
        //   severity: 'error',
        //   summary: 'Error',
        //   detail: error.error.ERROR,
        //   icon: 'far fa-times-circle',
        // })
        this.spinner.hide();
      }
    );
  }

  getChargeForCustomer() {
    this.spinner.show();
    const url = "/charge/getChargeForCustomer";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.ChargeForCustomerData = response.chargelist;
        // console.log("ChargeForCustomerData", this.ChargeForCustomerData);
        this.spinner.hide();
      },
      (error: any) => {
        // this.messageService.add({
        //   severity: 'error',
        //   summary: 'Error',
        //   detail: error.error.ERROR,
        //   icon: 'far fa-times-circle',
        // })
        this.spinner.hide();
      }
    );
  }

  getplanservice() {
    this.spinner.show();
    const url = "/planservice/all";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.copyplanserviceData = response.serviceList;
        this.planserviceData = response.serviceList;
        // console.log("planserviceData", this.planserviceData);

        this.spinner.hide();
      },
      (error: any) => {
        // this.messageService.add({
        //   severity: 'error',
        //   summary: 'Error',
        //   detail: error.error.ERROR,
        //   icon: 'far fa-times-circle',
        // })
        this.spinner.hide();
      }
    );
  }

  getAllPinCodeNumber() {
    this.spinner.show();
    const url = "/pincode/all";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.allpincodeNumber = response.dataList;
        // console.log("allpincodeNumber", this.allpincodeNumber);
        this.spinner.hide();
      },
      (error: any) => {
        // this.messageService.add({
        //   severity: 'error',
        //   summary: 'Error',
        //   detail: error.error.ERROR,
        //   icon: 'far fa-times-circle',
        // })
        this.spinner.hide();
      }
    );
  }

  getALLArea() {
    this.spinner.show();
    const url = "/area/all";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.areaData = response.dataList;
        // console.log("areaData", this.areaData);
        this.spinner.hide();
      },
      (error: any) => {
        // this.messageService.add({
        //   severity: 'error',
        //   summary: 'Error',
        //   detail: error.error.ERROR,
        //   icon: 'far fa-times-circle',
        // })
        this.spinner.hide();
      }
    );
  }

  getCommonListTitleData() {
    this.spinner.show();
    const url = "/commonList/title";
    this.getMethodForCommonList(url).subscribe(
      (response: any) => {
        this.commonListTitleData = response.dataList;
        // console.log("commonListTitleData", this.commonListTitleData);
        this.spinner.hide();
      },
      (error: any) => {
        // this.messageService.add({
        //   severity: 'error',
        //   summary: 'Error',
        //   detail: error.error.ERROR,
        //   icon: 'far fa-times-circle',
        // })
        this.spinner.hide();
      }
    );
  }

  getCommonListPaymentData() {
    this.spinner.show();
    const url = "/commonList/paymentMode";
    this.getMethodForCommonList(url).subscribe(
      (response: any) => {
        this.commonListPaymentData = response.dataList;
        // console.log("commonListPaymentData", this.commonListPaymentData);
        this.spinner.hide();
      },
      (error: any) => {
        // this.messageService.add({
        //   severity: 'error',
        //   summary: 'Error',
        //   detail: error.error.ERROR,
        //   icon: 'far fa-times-circle',
        // })
        this.spinner.hide();
      }
    );
  }

  getIppoolData() {
    this.spinner.show();
    const url = "/ippool/all";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.ippoolData = response.dataList;
        // console.log("ippoolData", this.ippoolData);
        this.spinner.hide();
      },
      (error: any) => {
        // this.messageService.add({
        //   severity: 'error',
        //   summary: 'Error',
        //   detail: error.error.ERROR,
        //   icon: 'far fa-times-circle',
        // })
        this.spinner.hide();
      }
    );
  }

  getPostpaidplanData() {
    this.spinner.show();
    const url = "/postpaidplan/all";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.postpaidplanData = response.postpaidplanList;
        this.spinner.hide();
      },
      (error: any) => {
        this.spinner.hide();
      }
    );
  }

  getPOSTpaidNormalPlan() {
    this.spinner.show();
    const url = "/postpaidplan/all?type=NORMAL";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.NomalpostpaidplanData = response.postpaidplanList;
        this.spinner.hide();
      },
      (error: any) => {
        this.spinner.hide();
      }
    );
  }

  getPOSTpaidSpecialPlan() {
    this.spinner.show();
    const url = "/postpaidplan/all?type=SPECIAL";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.specialpostpaidplanData = response.postpaidplanList;
        this.spinner.hide();
      },
      (error: any) => {
        this.spinner.hide();
      }
    );
  }

  getCountryList() {
    const url = "/country/all";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.counttryListData = response.countryList;
        this.spinner.hide();
      },
      (error: any) => {
        // this.messageService.add({
        //   severity: 'error',
        //   summary: 'Error',
        //   detail: error.error.ERROR,
        //   icon: 'far fa-times-circle',
        // })
        this.spinner.hide();
      }
    );
  }

  getStateList() {
    const url = "/state/all";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.stateListData = response.stateList;
        this.spinner.hide();
      },
      (error: any) => {
        // this.messageService.add({
        //   severity: 'error',
        //   summary: 'Error',
        //   detail: error.error.ERROR,
        //   icon: 'far fa-times-circle',
        // })
        this.spinner.hide();
      }
    );
  }

  getCityList() {
    const url = "/city/all";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.cityListData = response.cityList;
        this.spinner.hide();
      },
      (error: any) => {
        // this.messageService.add({
        //   severity: 'error',
        //   summary: 'Error',
        //   detail: error.error.ERROR,
        //   icon: 'far fa-times-circle',
        // })
        this.spinner.hide();
      }
    );
  }

  getChargeTypeByList(serviceId = "") {
    let queryParam = "";
    if (serviceId !== "") {
      queryParam = `?serviceId=${serviceId}`;
    }
    const url = "/charge/ByType/CUSTOMER_DIRECT" + queryParam;
    this.getMethod(url).subscribe(
      (response: any) => {
        this.chargeByTypeData = response.chargelist;
        this.spinner.hide();
      },
      (error: any) => {
        // this.messageService.add({
        //   severity: 'error',
        //   summary: 'Error',
        //   detail: error.error.ERROR,
        //   icon: 'far fa-times-circle',
        // })
        this.spinner.hide();
      }
    );
  }

  getBillRunMasterList() {
    const url = "/billrun/All";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.billRunMasterList = response.billRunlist;
        // console.log("this.billRunMasterList", this.billRunMasterList);
        this.spinner.hide();
      },
      (error: any) => {
        // this.messageService.add({
        //   severity: 'error',
        //   summary: 'Error',
        //   detail: error.error.ERROR,
        //   icon: 'far fa-times-circle',
        // })
        this.spinner.hide();
      }
    );
  }

  getPlanPurchaseType() {
    const url = "/commonList/generic/planPurchaseType";
    this.getMethodForCommonList(url).subscribe(
      (response: any) => {
        console.log("  this.planPurchaseTypeData", response);
        this.planPurchaseTypeData = response.dataList.filter(
          type => type.text !== "New" && type.text !== "Upgrade"
        );
        this.spinner.hide();
      },
      (error: any) => {
        // this.messageService.add({
        //   severity: 'error',
        //   summary: 'Error',
        //   detail: error.error.ERROR,
        //   icon: 'far fa-times-circle',
        // })
        this.spinner.hide();
      }
    );
    return this.planPurchaseTypeData;
  }

  getPostpaidCustomer() {
    this.spinner.show();
    const url = "/customers/list/" + RadiusConstants.CUSTOMER_TYPE.POSTPAID;
    let custerlist = {
      page: 1,
      pageSize: 10000
    };
    this.postMethod(url, custerlist).subscribe(
      (response: any) => {
        this.postpaidCustomerList = response.customerList;
        this.spinner.hide();
      },
      (error: any) => {
        console.log(error, "error");
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getParentPostpaidCustomer() {
    this.spinner.show();
    const url = "/parentcustomer/list/" + RadiusConstants.CUSTOMER_TYPE.POSTPAID;
    this.getMethod(url).subscribe(
      (response: any) => {
        this.postpaidParentCustomerList = response.parentCustomers;
        this.spinner.hide();
      },
      (error: any) => {
        console.log(error, "error");
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getParentPrepaidCustomer() {
    this.spinner.show();
    const url = "/parentcustomer/list/" + RadiusConstants.CUSTOMER_TYPE.PREPAID;
    this.getMethod(url).subscribe(
      (response: any) => {
        this.prepaidParentCustomerList = response.parentCustomers;
        this.spinner.hide();
      },
      (error: any) => {
        console.log(error, "error");
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getTeamList() {
    const url = "/teams/all";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.teamListData = response.dataList;
        this.activeTeamListData = response.dataList.filter(item => item.status == "active");
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getMatrixList() {
    const url = "/matrix/all";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.tatMatricsData = response.dataList;
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getActiveMatrixList() {
    const url = "/matrix/status";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.activeTatMatricsData = response;
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  // partnersFromSalesCRMS: any;
  // getPartnersFromSalesCRMS() {
  //   const url = "/leadMaster/findAll/Partner";
  //   this.leadManagementService.getMethod(url).subscribe((res: any) => {
  //     this.partnersFromSalesCRMS = res.partnerList;
  //   });
  // }

  // branchesFromSalesCRMS: any;
  // getBranchesFromSalesCRMS() {
  //   const url = "/leadMaster/findAll/Branch";
  //   this.leadManagementService.getMethod(url).subscribe((res: any) => {
  //     this.branchesFromSalesCRMS = res.branchList;
  //   });
  // }

  // serviceAreasFromSalesCRMS: any;
  // getServiceAreasFromSalesCRMS() {
  //   const url = "/leadMaster/findAll/ServiceArea";
  //   this.leadManagementService.getMethod(url).subscribe((res: any) => {
  //     this.serviceAreasFromSalesCRMS = res.serviceAreaList;
  //   });
  // }

  // customersFromSalesCRMS: any;
  // getCustomersFromSalesCRMS() {
  //   const url = "/leadMaster/findAll/Customers";
  //   this.leadManagementService.getMethod(url).subscribe((res: any) => {
  //     this.customersFromSalesCRMS = res.customersList;
  //   });
  // }

  // staffsFromSalesCRMS: any;
  // getStaffsFromSalesCRMS() {
  //   const url = "/leadMaster/findAll/StaffUser";
  //   this.leadManagementService.getMethod(url).subscribe((res: any) => {
  //     this.staffsFromSalesCRMS = res.staffUserList;
  //   });
  // }

  getRegionData() {
    const url = "/region/all";
    this.getMethod(url).subscribe((res: any) => {
      this.regionDataList = res.dataList;
    });
  }

  priorityTicketData = [];
  getTicketPriority() {
    this.spinner.show();
    const url = "/commonList/ticket_priority";
    this.getMethodForCommonList(url).subscribe(
      (response: any) => {
        this.priorityTicketData = response.dataList;
        console.log("this.priorityTicketData", this.priorityTicketData);

        this.spinner.hide();
      },
      (error: any) => {
        console.log(error, "error");
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getAllActiveBranch() {
    this.spinner.show();
    const url = "/branchManagement/all";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.activeBranchList = response.dataList.filter(branch => branch.status == "Active");
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.errorMessage}`, "Failed!");
        this.spinner.hide();
      }
    );
  }
  getAllActiveStaff() {
    this.spinner.show();
    const url = "/staffList/all";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.activeStaffList = response.dataList; //.filter(staff => staff.status == "Active");
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.errorMessage}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getBusinessUnitList() {
    this.spinner.show();
    const url = "/businessUnit/all";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.businessUnitList = response.dataList.filter(staff => staff.status == "Active");
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.errorMessage}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getAllActiveProduct() {
    this.spinner.show();
    const url = "/product/getAllActiveProduct";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.activeProductList = response.dataList;
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.errorMessage}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getAllActiveInward() {
    this.spinner.show();
    const url = "/inwards/all";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.activeInwardList = response.dataList.filter(inward => inward.status == "ACTIVE");
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.errorMessage}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getOwnershipType() {
    this.spinner.show();
    const url = "/commonList/generic/OWNERSHIP_TYPE";
    this.getMethodForCommonList(url).subscribe(
      (response: any) => {
        this.ownershipTypeList = response.dataList;
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.errorMessage}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getItemStatusList() {
    this.spinner.show();
    const url = "/commonList/generic/ITEM_STATUS_MANAGEMENT";
    this.getMethodForCommonList(url).subscribe(
      (response: any) => {
        this.itemStatusList = response.dataList;
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.errorMessage}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getItemConditionList() {
    this.spinner.show();
    const url = "/commonList/generic/ITEM_CONDITION_MANAGEMENT";
    this.getMethodForCommonList(url).subscribe(
      (response: any) => {
        this.itemConditionList = response.dataList;
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.errorMessage}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getWarrantyStatusList() {
    this.spinner.show();
    const url = "/commonList/generic/ITEM_WARRANTY_MANAGEMENT";
    this.getMethodForCommonList(url).subscribe(
      (response: any) => {
        this.warrantyStatusList = response.dataList;
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.errorMessage}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getPOPList() {
    const url = "/popmanagement/all";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.popListData = response.dataList;
        this.spinner.hide();
      },
      (error: any) => {
        // console.log(error, "error")
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  // popListFromSalesCrms: any;
  // getPopDataFromSalesCrms() {
  //   const url = "/leadMaster/findAll/popManagement";
  //   this.leadManagementService.getMethod(url).subscribe(
  //     async (response: any) => {
  //       this.popListFromSalesCrms = await response.popManagementList;
  //     },
  //     (error: any) => {
  //       this.messageService.add({
  //         severity: "error",
  //         summary: "Error",
  //         detail: "Something went wrong while fetching pop list",
  //         icon: "far fa-times-circle",
  //       });
  //       this.spinner.hide();
  //     }
  //   );
  // }

  getTATForTicketList() {
    const url = "/tickettatmatrix/searchByStatus";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.TATForTicketData = response.dataList;
        this.spinner.hide();
      },
      (error: any) => {
        this.spinner.hide();
      }
    );
  }

  getCustomerType() {
    this.spinner.show();
    const url = "/commonList/Customer_Type";
    const custerlist = {};
    this.getMethodForCommonList(url).subscribe(
      (response: any) => {
        this.customertypeList = response.dataList;
        this.spinner.hide();
      },
      (error: any) => {
        console.log(error, "error");
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getCustomerSubType(data) {
    this.spinner.show();
    const url = "/commonList/" + data;
    const custerlist = {};
    this.getMethodForCommonList(url).subscribe(
      (response: any) => {
        this.customerSubtypeList = response.dataList;
        this.spinner.hide();
      },
      (error: any) => {
        console.log(error, "error");
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getSectorType() {
    this.spinner.show();
    const url = "/commonList/Customer_Sector";
    const custerlist = {};
    this.getMethodForCommonList(url).subscribe(
      (response: any) => {
        this.sectortypeList = response.dataList;
        this.spinner.hide();
      },
      (error: any) => {
        console.log(error, "error");
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getActiveProductCategoryList() {
    this.spinner.show();
    const url = "/productCategory/getAllActiveProductCategoriesByCB";
    this.getMethod(url).subscribe(
      (response: any) => {
        console.log("response", response);
        this.productCategoryList = response.dataList;
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.errorMessage}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getBankDetail() {
    this.spinner.show();
    const url = "/bankManagement/searchByStatus?banktype=other";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.bankDataList = response.dataList;
        // this.bankDestination = response.dataList.banktype
        this.spinner.hide();
      },
      (error: any) => {
        // console.log(error, "error")
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getBankDestinationDetail() {
    this.spinner.show();
    const url = "/bankManagement/searchByStatus?banktype=operator";
    this.getMethod(url).subscribe(
      (response: any) => {
        // this.bankDataList = response.dataList.banktype;
        this.bankDestination = response.dataList;
        this.spinner.hide();
      },
      (error: any) => {
        // console.log(error, "error")
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getPartner() {
    this.spinner.show();
    const url = "/partner/all";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.partnerData = response.partnerlist;
        this.spinner.hide();
      },
      (error: any) => {
        this.spinner.hide();
      }
    );
  }

  getServiceAreaType() {
    this.spinner.show();
    const url = "/commonList/generic/service_Area_Type";
    this.getMethodForCommonList(url).subscribe(
      (response: any) => {
        this.serviceAreaTypeData = response.dataList;
        this.spinner.hide();
      },
      (error: any) => {
        this.spinner.hide();
      }
    );
  }

  getPop() {
    this.spinner.show();
    const url = "/popmanagement/all";
    this.getMethod(url).subscribe(
      (response: any) => {
        this.popData = response.dataList;
        this.spinner.hide();
      },
      (error: any) => {
        this.spinner.hide();
      }
    );
  }
  customerTypeSearchOption = [
    { label: "Parent", value: "custparent" },
    { label: "Child", value: "custchild" },
    { label: "Individual", value: "custindividual" }
  ];

  planCreationType() {
    this.getMethod("/businessUnit/getBUFromCurrentStaff").subscribe((res: any) => {
      if (res.dataList?.length === 1) {
        if (res.dataList[0].planBindingType == "On-Demand") {
          this.isPlanOnDemand = true;
        } else {
          this.isPlanOnDemand = false;
        }
      } else if (res.dataList?.length == 0 || res.dataList == null) {
        this.isPlanOnDemand = false;
      } else this.isPlanOnDemand = false;
    });
  }

  getAllReseller() {
    this.getMethod(`/Reseller/getAllResellers?mvnoId=${this.mvnoId}`).subscribe((response: any) => {
      this.resellerData = response.resellers.data;
    });
  }

  getBUFromStaff() {
    this.commonPostMethod(`/staffuser/list`, {}).subscribe((response: any) => {
      this.BUFromStaffList = response.staffUserlist;
      this.spinner.hide();
    });
  }

  //need to use this common api for get all branches by service area ids replace this method from whole project when get time
  branchByServiceAreaID(ids) {
    let url = "/branchManagement/getAllBranchesByServiceAreaId";
    this.postMethod(url, ids).subscribe((response: any) => {
      this.spinner.hide();
      this.branchesByServiceArea = response.dataList;
    });
  }

  getQosPolicy() {
    const url = "/qosPolicy/all";
    this.getMethod(url).subscribe((response: any) => {
      this.qosPolicyData = response.dataList;
      this.spinner.hide();
    });
  }

  getUserId() {
    // const calculetedHash = localStorage.getItem("userIdH");

    // let item = localStorage.getItem("userId");
    // const calculateHash = CryptoJS.enc.Base64.stringify(
    //   CryptoJS.HmacSHA256(item, CryptoJS.enc.Utf8.parse(this.secretKey))
    // );
    // let userId;
    // if (calculetedHash === calculateHash) {
    //   userId = item.split("|")[0];
    // }
    // return userId;
    const userIdKey = this.createHash("userId");
    const userIdHashKey = this.createHash("userIdH");

    const storedValue = localStorage.getItem(userIdKey);
    const storedHash = localStorage.getItem(userIdHashKey);

    if (!storedValue || !storedHash) return null;

    const calculatedHash = this.createHash(storedValue);

    if (calculatedHash === storedHash) {
      return storedValue.split("|")[0];
    }
    return null;
  }

  getMvnoId() {
    // const calculetedHash = localStorage.getItem("mvnoIdH");

    // let item = localStorage.getItem("mvnoId");
    // const calculateHash = CryptoJS.enc.Base64.stringify(
    //   CryptoJS.HmacSHA256(item, CryptoJS.enc.Utf8.parse(this.secretKey))
    // );
    // let mvnoId;
    // if (calculetedHash === calculateHash) {
    //   mvnoId = item.split("|")[0];
    // }
    // return mvnoId;
    const mvnoIdKey = this.createHash("mvnoId");
    const mvnoIdHashKey = this.createHash("mvnoIdH");

    const storedValue = localStorage.getItem(mvnoIdKey);
    const storedHash = localStorage.getItem(mvnoIdHashKey);

    if (!storedValue || !storedHash) return null;

    const calculatedHash = this.createHash(storedValue);

    if (calculatedHash === storedHash) {
      return storedValue.split("|")[0];
    }
    return null;
  }

  createHash(data) {
    const calculateHash = CryptoJS.enc.Base64.stringify(
      CryptoJS.HmacSHA256(data, CryptoJS.enc.Utf8.parse(this.secretKey))
    );
    return calculateHash;
  }

  data = [];
  getStaffDetailById() {
    let currentPageForStaff;
    const data = {};
    let staffData: any = [];
    const url = "/staffuser/list";
    this.commonPostMethod(url, data).subscribe((response: any) => {
      staffData = response.staffUserlist;
      this.staffDataList.forEach((element, i) => {
        element.displayLabel = element.fullName + " (Ph: " + element.phone + ")";
        this.data.push(element.id);
      });
      this.spinner.hide();
    });
  }
}
