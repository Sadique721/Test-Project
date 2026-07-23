import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import {
  FormBuilder,
  Validators,
  FormGroup,
  FormControl,
  FormArray,
  AbstractControl
} from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { CustomerdetailsilsService } from "src/app/service/customerdetailsils.service";
import { MessageService } from "primeng/api";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { Router } from "@angular/router";
import { LoginService } from "src/app/service/login.service";
import * as FileSaver from "file-saver";
import { DatePipe, formatDate } from "@angular/common";
import { COUNTRY, CITY, STATE, PINCODE, AREA } from "src/app/RadiusUtils/RadiusConstants";
import { POST_CUST_CONSTANTS, PRE_CUST_CONSTANTS } from "src/app/constants/aclConstants";
import moment from "moment";
import { StatusCheckService } from "src/app/service/status-check-service.service";
import { CustomerFeedbackService } from "src/app/service/customerfeedback.service";
import { asyncScheduler, BehaviorSubject, interval, Subscription } from "rxjs";
import * as uuid from "uuid";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { countries } from "../model/country";
import { SharedModule } from "src/app/shared/shared.module";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { NgxStarRatingModule } from "ngx-star-rating";
import { CustChangePlanComponent } from "../cust-change_plan/cust-change_plan.component";
import { MatChipsModule } from "@angular/material/chips";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { CustomerInventoryDetailsService } from "../customer-inventory-details/customer-inventory-details.service";

declare var $: any;
@Component({
  selector: "app-customer-details",
  templateUrl: "./customer-details.component.html",
  styleUrls: ["./customer-details.component.css"],
  standalone: true,
  imports: [SharedModule, NgxStarRatingModule, MatChipsModule, CustChangePlanComponent],
  providers: [DatePipe]
})
export class CustomerDetailsComponent implements OnInit {
  today = new Date();
  countryTitle = COUNTRY;
  cityTitle = CITY;
  stateTitle = STATE;
  pincodeTitle = PINCODE;
  areaTitle = AREA;
  @ViewChild("closebutton") closebutton;
  custLedgerForm: FormGroup;
  searchInvoiceMasterFormGroup: FormGroup;
  renewPlanForm: FormGroup;
  ratingForm: FormGroup;
  customerLedgerDetailData: any = [];
  customerAddress: any;
  presentAdressDATA: any = [];
  permentAdressDATA: any = [];
  paymentAdressDATA: any = [];
  chargeDATA = [];
  dataPlan = [];
  permanentAddressData: any = [];
  presentFullAddress: any = [];
  customerLedgerData: any = [];
  customerLedgerListData: any = [];
  customerPaymentData: any = [];
  customerInvoiceData: any = [];
  isCustomerLedgerOpen: boolean = false;
  isCustomerDetailOpen: boolean = true;
  isServiceOpen: boolean = false;
  custServiceData: any = [];
  ifService: boolean = false;
  isPaybuttonDisble: boolean = true;
  isPaymentOpen: boolean = false;
  isInvoiceOpen: boolean = false;
  isInventoryOpen: boolean = false;
  isRenewPlanOpen: boolean = false;
  isUsageHistoryOpen: boolean = false;
  billRunMasterList: any;
  custPlanDeatilItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
  custPlanDeatiltotalRecords: String;
  currentPagecustPlanDeatilList = 1;
  inputMobile: string = "";

  custChargeDeatilItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
  custChargeDeatiltotalRecords: String;
  currentPagecustChargeDeatilList = 1;

  custMacAddItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
  custMacAddtotalRecords: String;
  currentPagecustMacAddList = 1;

  custLedgerItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
  custLedgertotalRecords: any;
  currentPagecustLedgerList = 0;

  customerPaymentdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
  customerPaymentdatatotalRecords: String;
  currentPagecustomerPaymentdata = 0;

  invoiceMasteritemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
  invoiceMastertotalRecords: String;
  currentPageinvoiceMasterSlab = 1;

  custLedgerSubmitted: boolean = false;
  mobileError: boolean = false;
  countries: any = countries;

  postdata: any = {
    CREATE_DATE: "",
    END_DATE: "",
    id: "",
    amount: "",
    balAmount: "",
    custId: "",
    description: "",
    refNo: "",
    transcategory: "",
    transtype: ""
  };
  customerOpeningAmount: any = [];
  customerClosingAmount: any = [];
  customerID: any;

  //............primeNG accordion
  activeChargeState: boolean[] = [true, false, false];
  activeplanState: boolean[] = [true, false, false];
  activeLedgerState: boolean[] = [true, false, false];
  activeQuotaState: boolean[] = [true, false, false];
  activeMyticketState: boolean[] = [true, false, false];

  //.......... Cust Quota List
  custQuotaList: any[];
  custQuotaListItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
  custQuotaListtotalRecords: String;
  currentPagecustQuotaList = 1;

  orderId: any;

  // ticket
  customerTicketView: boolean = false;
  custTicketList: any = [];
  currentPageTicketConfig = 1;
  custTicketConfigitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
  custTicketConfigtotalRecords: String;

  //change Password
  changePasswordForm: FormGroup;
  changePasswordvalue: any;

  pageLimitOptions = RadiusConstants.pageLimitOptions;
  customerLedgerSearchKey: string;
  legershowItemPerPage = 1;
  pageITEM = RadiusConstants.ITEMS_PER_PAGE;
  paymentShowItemPerPage = 1;
  showItemPerPageInvoice = 5;
  filterPlanListCust: any;
  planPurchaseTypeData: any;
  selPlanData: any = {
    quotatype: "",
    quotatime: "",
    quota: "",
    quotaUnit: "",
    quotaunittime: "",
    validity: "",
    offerprice: "",
    taxamount: "",
    activationDate: "",
    expiryDate: "",
    finalAmount: ""
  };
  planListByType: any = [];
  isPlanTypeAddon: boolean = false;
  renewPlansubmitted: boolean = false;
  assignedInventoryList: any = [];
  oldMAC: any = "";
  oldSerial: any = "";
  rowGroupMetadata: any = {};
  inventoryDetailData: any = [];
  inoutwardMacMappingData: any = [];
  renewPlanData: any;
  paymentMode = [{ label: "Cash" }, { label: "Cheque" }, { label: "Online" }];
  PRE_CUST_CONSTANTS = PRE_CUST_CONSTANTS;
  POST_CUST_CONSTANTS = POST_CUST_CONSTANTS;
  isEditing: boolean = false;
  isEditRating: boolean = false;
  isRatingError: boolean = false;
  isRatingPresent: boolean = false;
  customerFeedback: any;

  createcustomerData: any = {};
  activeIndex: number = 0;
  birthDate: any;
  isNameEdit: boolean = false;
  ifWalletMenu = false;
  getWallatData = [];
  isRatingMenu = false;
  timeFrameOption = [
    { label: "Week", value: "Week" },
    { label: "Month", value: "Month" },
    { label: "Last 6 Months", value: "Last 6 Months" }
  ];
  savedConfig: any;
  paymentkeyValuePairs: { [key: string]: any } = {};
  displayDialog: boolean;
  displayInvoicePaymentDialog: boolean;
  selectedPlan: any;
  isPaymentGatewayConfigured: boolean = false;
  invoicePaymentSuccessfully: any;
  paymentGateway: any;
  exitBuy: boolean = true;
  paymentstatusCount = RadiusConstants.TIMER_COUNT;
  paymentConfirmationModal: boolean = false;
  paymentSucessModel: boolean = false;
  subscription2: Subscription;
  obs1$ = interval(1000);
  transactionStatus: boolean = false;
  invoice: any;
  wifiInventory: any;
  editWifi: boolean = false;
  wifiModel: boolean = false;
  wifiForm: FormGroup;
  wifiSubmitted: boolean = false;
  demographicLabel: any;
  payMethod: any;
  mpinModal: boolean = false;
  mpinForm: FormGroup;
  isMpinFormSubmitted: boolean = false;

  // editWifiPassword: boolean = false;
  constructor(
    private fb: FormBuilder,
    // private messageService: MessageService,
    private spinner: NgxSpinnerService,
    private toastr: ToastrService,
    public customerdetailsilsService: CustomerdetailsilsService,
    private _router: Router,
    public loginService: LoginService,
    public datepipe: DatePipe,
    public statusCheckService: StatusCheckService,
    public customerFeedbackService: CustomerFeedbackService,
    public commondropdownService: CommondropdownService,
    public CustomerInventoryDetailsService: CustomerInventoryDetailsService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.searchInvoiceMasterFormGroup = this.fb.group({
      billfromdate: [""],
      billrunid: [""],
      billtodate: [""],
      custMobile: [""],
      custname: [""],
      docnumber: [""],
      customerid: [""],
      searchByTimeFrame: [""]
    });
    this.customerID = this.commondropdownService.getUserId();
    this.getCustomersDetail();
    this.getCustQuotaList(this.customerID);
    this.getBillRunMasterList();
    this.getPlanPurchaseType();
    this.demographicLabel = RadiusConstants.DEMOGRAPHICDATA || [];

    this.custLedgerForm = this.fb.group({
      startDateCustLedger: ["", Validators.required],
      endDateCustLedger: ["", Validators.required]
    });

    this.changePasswordForm = this.fb.group({
      id: [""],
      newPassword: ["", Validators.required],
      confirmPassword: ["", Validators.required],
      oldPassword: ["", Validators.required],
      remarks: [""],
      selfcarepwd: [""]
    });

    this.renewPlanForm = this.fb.group({
      purchaseType: ["", Validators.required],
      planId: ["", Validators.required],
      isPaymentReceived: ["false"],
      remarks: ["", Validators.required],
      recordPaymentDTO: this.fb.group({
        paymentAmount: ["", Validators.required],
        paymentDate: ["", Validators.required],
        paymentMode: ["", Validators.required],
        referenceNo: ["", Validators.required],
        chequeNo: ["", Validators.required],
        bankName: ["", Validators.required],
        chequeDate: ["", Validators.required],
        branch: ["", Validators.required],
        remarks: ["", Validators.required]
        // tdsDeducted: ['', Validators.required],
        // tdsAmount: ['', Validators.required],
      }),
      addonStartDate: [""]
    });
    this.renewPlanForm.get("recordPaymentDTO").disable();

    this.ratingForm = this.fb.group({
      rating: [, Validators.required],
      feedback: [""],
      custId: [""],
      id: [""]
    });
    this.wifiForm = this.fb.group({
      username: ["", Validators.required],
      password: ["", Validators.required],
      workingFrequency: [""]
    });
    this.mpinForm = this.fb.group({
      countryCode: [""],
      mobileNumber: ["", [Validators.required]]
    });
    this.checkPaymentGatewayConfiguration();
    this.commondropdownService.getsystemconfigList();

    this.commondropdownService.mobileNumberLengthSubject$.subscribe(lengthObj => {
      if (lengthObj) {
        this.mpinForm
          .get("mobileNumber")
          ?.setValidators([
            Validators.required,
            Validators.minLength(lengthObj.min),
            Validators.maxLength(lengthObj.max)
          ]);
        this.mpinForm.get("mobileNumber")?.updateValueAndValidity();
      }
    });
  }

  selectTitile = [
    { label: "Mr" },
    { label: "Ms" },
    { label: "Mrs" },
    { label: "Miss" },
    { label: "M/S" }
  ];
  // ..........customer deatils...........
  getCustomersDetail() {
    this.spinner.show();
    this.presentAdressDATA = [];
    this.permentAdressDATA = [];
    this.paymentAdressDATA = [];
    this.chargeDATA = [];
    const url = "/customers/" + this.customerID;
    this.customerdetailsilsService.getMethod(url).subscribe(
      (response: any) => {
        this.createcustomerData = { ...response.customers };
        this.customerLedgerDetailData = { ...response.customers };
        this.currency = this.createcustomerData.currency
          ? this.createcustomerData.currency
          : this.currencySymbol;
        this.birthDate = moment(this.createcustomerData.birthDate).format("YYYY-MM-DD").toString();
        this.customerAddress = this.customerLedgerDetailData.addressList.find(
          address => address.version === "NEW"
        );

        //Address
        if (this.customerLedgerDetailData.addressList.length > 0) {
          if (this.customerLedgerDetailData.addressList[0].addressType) {
            this.presentFullAddress = this.customerLedgerDetailData.addressList[0].fullAddress;
            let areaurl = "/area/" + this.customerLedgerDetailData.addressList[0].areaId;

            this.customerdetailsilsService.commonGetMethod(areaurl).subscribe((response: any) => {
              this.presentAdressDATA = response.data;
              this.presentAdressDATA.buildingNumber =
                this.customerLedgerDetailData.addressList[0]?.buildingNumber;
              this.serviceAreaAndBuildingNameFromCustomerId();
            });
          }
        }

        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  pageChangedcustChargeDetailList(pageNumber) {
    this.currentPagecustChargeDeatilList = pageNumber;
  }

  pageChangedcustPlanDetailList(pageNumber) {
    this.currentPagecustPlanDeatilList = pageNumber;
  }

  pageChangedcustMacAddDetailList(pageNumber) {
    this.currentPagecustMacAddList = pageNumber;
  }

  getCustomerData() {
    this.isCustomerLedgerOpen = false;
    this.isCustomerDetailOpen = true;
    this.customerTicketView = false;
    this.isPaymentOpen = false;
    this.isInvoiceOpen = false;
    this.isRenewPlanOpen = false;
    this.ifWalletMenu = false;
    this.isInventoryOpen = false;
    this.ifService = false;
    this.isUsageHistoryOpen = false;
    this.isRatingMenu = false;
  }
  private formatDateToString(date: Date): string {
    if (!date) return "";

    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, "0");
    const day = date.getDate().toString().padStart(2, "0");

    return `${year}-${month}-${day}`;
  }
  private convertToDateString(dateInput: any): string {
    if (!dateInput) return "";

    let date: Date;

    if (dateInput instanceof Date) {
      date = dateInput;
    } else if (typeof dateInput === "string") {
      date = new Date(dateInput);
    } else {
      return "";
    }

    // Check if date is valid
    if (isNaN(date.getTime())) {
      return "";
    }

    return this.formatDateToString(date);
  }
  //........ pay details
  searchCustomerLedger() {
    this.custLedgerSubmitted = true;
    if (
      !this.customerLedgerSearchKey ||
      this.customerLedgerSearchKey !== this.custLedgerForm.value
    ) {
      this.currentPagecustLedgerList = 1;
    }
    this.customerLedgerSearchKey = this.custLedgerForm.value;

    this.custLedgerItemPerPage = this.custLedgerItemPerPage;

    this.custLedgerSubmitted = true;
    if (this.custLedgerForm.valid) {
      // Convert date values to yyyy-MM-dd format before sending to API
      const startDate = this.custLedgerForm.controls.startDateCustLedger.value;
      const endDate = this.custLedgerForm.controls.endDateCustLedger.value;

      this.postdata.CREATE_DATE = this.convertToDateString(startDate);
      this.postdata.END_DATE = this.convertToDateString(endDate);
    }
    this.getCustomersLedger(this.customerID, "");
  }
  clearSearchCustomerLedger() {
    this.postdata.CREATE_DATE = "";
    this.postdata.END_DATE = "";
    this.custLedgerForm.controls.startDateCustLedger.setValue("");
    this.custLedgerForm.controls.endDateCustLedger.setValue("");
    this.custLedgerSubmitted = false;
    this.getCustomersLedger(this.customerID, "");
  }
  TotalLedgerItemPerPage(event) {
    this.legershowItemPerPage = Number(event.value);
    if (this.currentPagecustLedgerList > 1) {
      this.currentPagecustLedgerList = 1;
    }
    if (!this.customerLedgerSearchKey) {
      this.getCustomersLedger(this.customerLedgerDetailData.id, this.legershowItemPerPage);
    } else {
      this.searchCustomerLedger();
    }
  }
  onLedgerPageChange(event: PageEvent) {
    this.custLedgerItemPerPage = event.pageSize;
    this.currentPagecustLedgerList = event.pageIndex + 1;
    this.getCustomersLedger(this.customerID, this.custLedgerItemPerPage);
  }
  ledgerDataSource = new MatTableDataSource<any>([]);

  getCustomersLedger(custId: number, size: any) {
    let page_list;
    this.customerLedgerSearchKey = "";

    if (size) {
      page_list = size;
      this.custLedgerItemPerPage = size;
    } else {
      this.custLedgerItemPerPage = this.custLedgerItemPerPage;
    }

    const url = "/customerLedgers";
    this.postdata.custId = custId;

    this.customerdetailsilsService.getCustomerLedger(this.postdata).subscribe(
      (response: any) => {
        this.customerLedgerData = response.customerLedgerDtls;
        this.customerLedgerListData =
          response.customerLedgerDtls.customerLedgerInfoPojo.debitCreditDetail.map((item: any) => {
            // Format the date to yyyy-MM-dd format for display
            let formattedDate = "";
            if (item.create_DATE) {
              const date = new Date(item.create_DATE);
              if (!isNaN(date.getTime())) {
                formattedDate = this.formatDateToString(date);
              } else {
                formattedDate = item.create_DATE; // fallback to original if conversion fails
              }
            }

            return { ...item, create_DATE: formattedDate };
          });

        this.ledgerDataSource.data = this.customerLedgerListData;
        this.custLedgertotalRecords = this.customerLedgerListData.length;

        this.initializeMatTable();
      },
      (error: any) => {
        this.toastr.error(`${error.error.error}`, "Failed!");

        this.ledgerDataSource.data = [];
        this.custLedgertotalRecords = 0;
      }
    );
  }

  @ViewChild(MatPaginator, { static: false }) paginator!: MatPaginator;
  @ViewChild(MatSort, { static: false }) sort!: MatSort;
  private initializeMatTable() {
    setTimeout(() => {
      if (this.paginator && this.sort) {
        this.ledgerDataSource.paginator = this.paginator;
        this.ledgerDataSource.sort = this.sort;

        this.sort.active = "createDate";
        this.sort.direction = "desc";
        this.ledgerDataSource.sort = this.sort;
      }
    });
  }

  // getCustomersLedger(custId, size) {
  //   let page_list;
  //   this.customerLedgerSearchKey = "";
  //   // if (size) {
  //   //   page_list = size;
  //   //   this.custLedgerItemPerPage = size;
  //   // } else {
  //   //   if (this.legershowItemPerPage == 1) {
  //   //     this.custLedgerItemPerPage = this.pageITEM;
  //   //   } else {
  //   //     this.custLedgerItemPerPage = this.legershowItemPerPage;
  //   //   }
  //   // }
  //   this.spinner.show();
  //   // this.isCustomerLedgerOpen = true;
  //   // this.isCustomerDetailOpen = false;
  //   // this.isPaymentOpen = false;
  //   // this.isInvoiceOpen = false;
  //   // this.isInventoryOpen = false;
  //   // this.isRenewPlanOpen = false;
  //   // this.ifWalletMenu = false;
  //   // this.ifService = false;
  //   // this.isUsageHistoryOpen = false;
  //   // this.isRatingMenu = false;

  //   this.postdata.custId = custId;
  //   this.customerdetailsilsService.getCustomerLedger(this.postdata).subscribe(
  //     (response: any) => {
  //       this.customerLedgerData = response.customerLedgerDtls;
  //       this.customerLedgerListData =
  //         response.customerLedgerDtls.customerLedgerInfoPojo.debitCreditDetail.map((item: any) => {
  //           // Format the date to yyyy-MM-dd format for display
  //           let formattedDate = "";
  //           if (item.create_DATE) {
  //             const date = new Date(item.create_DATE);
  //             if (!isNaN(date.getTime())) {
  //               formattedDate = this.formatDateToString(date);
  //             } else {
  //               formattedDate = item.create_DATE; // fallback to original if conversion fails
  //             }
  //           }

  //           return { ...item, create_DATE: formattedDate };
  //         });

  //       this.ledgerDataSource.data = this.customerLedgerListData;
  //       this.custLedgertotalRecords = this.customerLedgerListData.length;
  //       this.spinner.hide();
  //     },
  //     (error: any) => {
  //       this.toastr.error(`${error.error.ERROR}`, "Failed!");
  //       this.spinner.hide();
  //     }
  //   );
  // }
  // pageChangedcustledgerList(pageNumber) {
  //   this.currentPagecustLedgerList = pageNumber;
  //   this.getCustomersLedger(this.customerID, "");
  // }

  //............primeNG accordion
  onTabClose(event) {
    this.toastr.error("Index: " + event.index, "Failed!");
  }
  onTabOpen(event) {
    this.toastr.error("Index: " + event.index, "Failed!");
    console.log(event.originalEvent);
  }
  toggle(index: number) {
    this.activeChargeState[index] = !this.activeChargeState[index];
    this.activeplanState[index] = !this.activeplanState[index];
    this.activeLedgerState[index] = !this.activeLedgerState[index];
    this.activeQuotaState[index] = !this.activeQuotaState[index];
    this.activeMyticketState[index] = !this.activeMyticketState[index];
  }

  //.......... Cust Quota List
  getCustQuotaList(custId) {
    let url = "/customer/custQuota/" + custId;
    this.customerdetailsilsService.getMethod(url).subscribe(
      (response: any) => {
        this.custQuotaList = response.custQuotaList;
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }
  pageChangedCustQuotaList(pageNumber) {
    this.currentPagecustQuotaList = pageNumber;
  }

  pageChange() {
    this._router.navigate(["paymentResponse"]);
  }

  addPayment() {
    let data;
    let amount = this.customerClosingAmount;
    if (amount < 0) {
      // Multiply number with -1
      // to make it positive
      amount = amount * -1;
    }
    data = {
      custId: this.customerLedgerData.custId,
      payment: amount,
      status: "Initiate"
    };
    this.customerdetailsilsService.addPayment(data).subscribe(
      (response: any) => {
        this.orderId = response.CustomerPayment.orderId;
        this.paymentGateWay(this.orderId);
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  paymentGateWay(orderId) {
    let data: any;
    let amount = this.customerClosingAmount;
    if (amount < 0) {
      // Multiply number with -1
      // to make it positive
      amount = amount * -1;
    }
    data = {
      custId: this.customerLedgerData.custId,
      customerName: this.customerLedgerData.username,
      mobileNo: this.customerLedgerDetailData.mobile,
      customerEmail: this.customerLedgerDetailData.email,
      txnAmount: amount,
      orderId: this.orderId
    };

    this.customerdetailsilsService.paymentGateway(data).subscribe(
      (response: any) => {
        window.location.href = response.paytmRedirectUrl;
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  // ticket
  openCustomersTicket(id) {
    this.isCustomerDetailOpen = false;
    this.isCustomerLedgerOpen = false;
    this.customerTicketView = true;
    this.ifWalletMenu = false;
    this.isRatingMenu = false;
    this.getcustTicket(id);
  }

  getcustTicket(custId) {
    this.spinner.show();
    const url = "/getCasesByCustomer/" + custId;
    this.customerdetailsilsService.getCutomerTicketData(url).subscribe(
      (response: any) => {
        this.custTicketList = response.dataList;
        this.spinner.hide();
      },
      (error: any) => {
        // console.log(error, "error")
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getCustInvoice(id) {
    this.isCustomerLedgerOpen = false;
    this.isCustomerDetailOpen = false;
    this.isPaymentOpen = false;
    this.isInvoiceOpen = true;
    this.isRenewPlanOpen = false;
    this.ifWalletMenu = false;
    this.isRatingMenu = false;
    this.isInventoryOpen = false;
    this.ifService = false;
    this.isUsageHistoryOpen = false;
    this.spinner.show();

    const dtoData = {
      page: this.currentPageinvoiceMasterSlab,
      pageSize: this.showItemPerPageInvoice
    };
    let url = "";
    if (
      this.customerLedgerDetailData.status === "NewActivation" ||
      this.customerLedgerDetailData.status === "ActivationPending"
    ) {
      url =
        "/trial/invoice/search?billrunid=&docnumber=&customerid=" +
        id +
        "&billfromdate=&billtodate=&custmobile=";
    } else {
      url =
        "/invoice/search?billrunid=&docnumber=&customerid=" +
        id +
        "&billfromdate=&billtodate=&custmobile=&isInvoiceVoid=true";
    }
    this.customerdetailsilsService.postRevenueMethod(url, dtoData).subscribe(
      (response: any) => {
        console.log("invoice", response);
        this.customerInvoiceData = response.invoicesearchlist;
        this.invoiceMastertotalRecords = response.pageDetails.totalRecords;
        this.spinner.hide();
      },
      (error: any) => {
        // console.log(error, "error")
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getCustPayment(id) {
    this.isCustomerLedgerOpen = false;
    this.isCustomerDetailOpen = false;
    this.isPaymentOpen = true;
    this.isInvoiceOpen = false;
    this.isRenewPlanOpen = false;
    this.ifWalletMenu = false;
    this.isRatingMenu = false;
    this.isInventoryOpen = false;
    this.ifService = false;
    this.isUsageHistoryOpen = false;
    this.spinner.show();
    this.customerdetailsilsService.getSubscriberPaymentHistory(id).subscribe(
      (response: any) => {
        console.log("payment", response);
        this.customerPaymentData = response.dataList;
        this.spinner.hide();
      },
      (error: any) => {
        // console.log(error, "error")
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  pageChangedTicketConfig(pageNumber) {
    this.currentPageTicketConfig = pageNumber;
  }

  //change Password
  changePassword() {
    let url = "/changePassword";
    //
    // this.changeSubmitted = false;
    this.spinner.show();

    if (this.changePasswordForm.valid) {
      // if (this.changePasswordForm.value.newpassword === this.changePasswordForm.value.selfcarepwd) {
      const newPassword = this.changePasswordForm.value.newPassword;
      const confirmPassword = this.changePasswordForm.value.confirmPassword;
      const oldPassword = this.changePasswordForm.value.oldPassword;
      if (newPassword !== oldPassword) {
        if (newPassword === confirmPassword) {
          this.changePasswordForm.value.id = this.customerLedgerDetailData.id;
          this.changePasswordForm.value.remarks = "";
          this.changePasswordForm.value.selfcarepwd = this.changePasswordForm.value.newPassword;
          this.changePasswordvalue = this.changePasswordForm.value;

          this.customerdetailsilsService
            .postSubscriberMethod(url, this.changePasswordvalue)
            .subscribe(
              (response: any) => {
                // this.changeSubmitted = true;
                this.spinner.hide();
                if (response.responseCode == 406) {
                  this.toastr.error(`Old password is wrong password`, "Failed!");
                } else {
                  this.toastr.success(`Password Update Successfully`, "Success!");
                  this.clearChangePasswordForm();
                  this.closebutton.nativeElement.click();
                }
              },
              (error: any) => {
                this.spinner.hide();
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
              }
            );
          // }
        } else {
          this.toastr.error(`New Password and Confirm Password should be the same`, "Failed!");

          this.spinner.hide();
        }
      } else {
        this.toastr.error(`New Password should be different from the Old Password`, "Failed!");

        this.spinner.hide();
      }
    }
  }

  clearChangePasswordForm() {
    this.changePasswordForm.reset();
  }

  searchinvoiceMaster(id, size) {
    let page_list;
    // if (size) {
    //   page_list = size;
    //   this.invoiceMasteritemsPerPage = size;
    // } else {
    //   if (this.showItemPerPageInvoice == 1) {
    //     this.invoiceMasteritemsPerPage = this.pageITEM;
    //   } else {
    //     this.invoiceMasteritemsPerPage = this.showItemPerPageInvoice;
    //   }
    // }

    let url;
    const dtoData = {
      page: this.currentPageinvoiceMasterSlab,
      pageSize: this.invoiceMasteritemsPerPage
    };

    this.searchInvoiceMasterFormGroup.value.custMobile = "";
    this.searchInvoiceMasterFormGroup.value.customerid = this.customerLedgerDetailData.id;

    let fromdate = this.searchInvoiceMasterFormGroup.value.billfromdate
      ? this.convertToDateString(this.searchInvoiceMasterFormGroup.value.billfromdate)
      : "";
    let toDate = this.searchInvoiceMasterFormGroup.value.billtodate
      ? this.convertToDateString(this.searchInvoiceMasterFormGroup.value.billtodate)
      : "";
    this.spinner.show();
    if (this.customerLedgerDetailData.status === "NewActivation") {
      url =
        "/trial/invoice/search?billrunid=" +
        this.searchInvoiceMasterFormGroup.value.billrunid +
        "&docnumber=" +
        this.searchInvoiceMasterFormGroup.value.docnumber.trim() +
        "&customerid=" +
        this.searchInvoiceMasterFormGroup.value.customerid +
        "&billfromdate=" +
        fromdate +
        "&billtodate=" +
        toDate +
        "&custmobile=" +
        this.searchInvoiceMasterFormGroup.value.custMobile.trim() +
        "&searchByTimeFrame=" +
        this.searchInvoiceMasterFormGroup.value.searchByTimeFrame;
    } else {
      url =
        "/invoice/search?billrunid=" +
        this.searchInvoiceMasterFormGroup.value.billrunid +
        "&docnumber=" +
        this.searchInvoiceMasterFormGroup.value.docnumber.trim() +
        "&customerid=" +
        this.searchInvoiceMasterFormGroup.value.customerid +
        "&billfromdate=" +
        fromdate +
        "&billtodate=" +
        toDate +
        "&custmobile=" +
        this.searchInvoiceMasterFormGroup.value.custMobile.trim() +
        "&searchByTimeFrame=" +
        this.searchInvoiceMasterFormGroup.value.searchByTimeFrame;
    }
    this.customerdetailsilsService.postRevenueMethod(url, dtoData).subscribe(
      (response: any) => {
        this.spinner.hide();
        this.customerInvoiceData = response.invoicesearchlist;
      },
      (error: any) => {
        console.log(error, "error");
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  clearSearchinvoiceMaster() {
    this.searchInvoiceMasterFormGroup.reset();
    this.searchInvoiceMasterFormGroup.controls.billrunid.setValue("");
    this.searchInvoiceMasterFormGroup.controls.docnumber.setValue("");
    this.searchInvoiceMasterFormGroup.controls.custname.setValue("");
    this.searchInvoiceMasterFormGroup.controls.billfromdate.setValue("");
    this.searchInvoiceMasterFormGroup.controls.billtodate.setValue("");
    this.searchInvoiceMasterFormGroup.controls.customerid.setValue("");
    this.searchInvoiceMasterFormGroup.controls.searchByTimeFrame.setValue("");
    this.customerInvoiceData = [];
    this.getCustInvoice(this.customerLedgerDetailData.id);
  }

  getBillRunMasterList() {
    const url = "/billrun/All";
    this.customerdetailsilsService.getMethod(url).subscribe(
      (response: any) => {
        this.billRunMasterList = response.billRunlist;
        // console.log("this.billRunMasterList", this.billRunMasterList);
        this.spinner.hide();
      },
      (error: any) => {
        // this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  downloadPDFINvoice(docNo, customerName) {
    if (docNo) {
      let downloadurl = "";
      if (this.customerLedgerDetailData.status === "NewActivation") {
        downloadurl = "/trialinvoicePdf/download/" + docNo;
      } else {
        downloadurl = "/invoicePdf/download/" + docNo;
      }
      this.customerdetailsilsService.downloadPDFInvoice(downloadurl).subscribe(
        (response: any) => {
          var file = new Blob([response], { type: "application/pdf" });
          var fileURL = URL.createObjectURL(file);
          FileSaver.saveAs(file, customerName + docNo);
          this.spinner.hide();
        },
        (error: any) => {
          this.spinner.hide();
          console.log(error, "error");
          this.toastr.error(`${error.error.ERROR}`, "Failed!");
        }
      );
    }
  }

  pageChangedcustomerPaymentList(pageNumber) {
    this.currentPagecustomerPaymentdata = pageNumber;
    this.getCustPayment(this.customerLedgerDetailData.id);
  }

  TotalPaymentItemPerPage(event) {
    this.paymentShowItemPerPage = Number(event.value);
    if (this.currentPagecustomerPaymentdata > 1) {
      this.currentPagecustomerPaymentdata = 1;
    }
    this.getCustPayment(this.customerLedgerDetailData.id);
  }

  pageChangedinvoiceMasterList(pageNumber) {
    this.currentPageinvoiceMasterSlab = pageNumber.pageIndex + 1;
    this.invoiceMasteritemsPerPage = pageNumber.pageSize;
    this.searchinvoiceMaster("", "");
  }

  TotalItemPerPageInvoice(event) {
    this.showItemPerPageInvoice = Number(event.value);
    if (this.currentPageinvoiceMasterSlab > 1) {
      this.currentPageinvoiceMasterSlab = 1;
    }
    this.searchinvoiceMaster("", "");
  }

  async renewPlan(data) {
    this.isCustomerLedgerOpen = false;
    this.isCustomerDetailOpen = false;
    this.customerTicketView = false;
    this.isPaymentOpen = false;
    this.isInvoiceOpen = false;
    this.isRenewPlanOpen = true;
    this.isPlanTypeAddon = false;
    this.renewPlansubmitted = false;
    this.ifWalletMenu = false;
    this.isRatingMenu = false;
    this.isInventoryOpen = false;
    this.isUsageHistoryOpen = false;
    this.ifService = false;
    this.renewPlanForm.reset();
    this.spinner.show();
    this.renewPlanForm.get("isPaymentReceived").setValue("false");
    this.selPlanData = {
      quotatype: "",
      quotatime: "",
      quota: "",
      quotaUnit: "",
      quotaunittime: "",
      validity: "",
      offerprice: "",
      taxamount: "",
      activationDate: "",
      expiryDate: "",
      finalAmount: ""
    };
    console.log("data", data);
    let checkCustTypeurl = "/isCustomerPrimeOrNot?custId=" + data.id;
    await this.customerdetailsilsService
      .getMethod(checkCustTypeurl)
      .subscribe(async (response: any) => {
        let planurl;
        if (response.isCustomerPrime) {
          planurl =
            "/premierePlan/all?custId=" +
            data.id +
            "&isPremiere=true&serviceAreaId=" +
            this.customerLedgerDetailData.serviceareaid;
        } else {
          planurl =
            "/plans/serviceArea?serviceAreaId=" + this.customerLedgerDetailData.serviceareaid;
        }
        await this.customerdetailsilsService.getMethod(planurl).subscribe(async (response: any) => {
          this.filterPlanListCust = await response.postpaidplanList.filter(
            plan => plan.plantype === this.customerLedgerDetailData.custtype
          );
          this.filterPlanListCust.forEach(element => {
            if (element.mode == "SPECIAL") {
              element.name = element.name + " - (SP)";
            }
          });
        });
      });
    console.log("this.filterPlanListCust1", this.filterPlanListCust);
    this.spinner.hide();
  }

  getPlanPurchaseType() {
    const url = "/commonList/generic/planPurchaseType";
    this.customerdetailsilsService.getMethod(url).subscribe(
      (response: any) => {
        this.planPurchaseTypeData = response.dataList.filter(
          type => type.text !== "New" && type.text !== "Upgrade"
        );
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  onPaymentTypeChange(data) {
    if (data === "YES") {
      this.renewPlanForm.controls.recordPaymentDTO.enable();
      this.renewPlanForm.get("recordPaymentDTO").get("chequeDate").disable();
      this.renewPlanForm.get("recordPaymentDTO").get("bankName").disable();
      this.renewPlanForm.get("recordPaymentDTO").get("branch").disable();
      this.renewPlanForm.get("recordPaymentDTO").get("referenceNo").disable();
      this.renewPlanForm.get("recordPaymentDTO").get("chequeNo").disable();
    } else {
      this.renewPlanForm.controls.recordPaymentDTO.disable();
    }
  }

  selPayMode(event) {
    // console.log('event', event.value)
    const payMode = event.value;
    if (payMode == "Cheque") {
      this.renewPlanForm.get("recordPaymentDTO").get("chequeDate").enable();
      this.renewPlanForm.get("recordPaymentDTO").get("bankName").enable();
      this.renewPlanForm.get("recordPaymentDTO").get("branch").disable();
      this.renewPlanForm.get("recordPaymentDTO").get("referenceNo").disable();
      this.renewPlanForm.get("recordPaymentDTO").get("chequeNo").enable();
    } else if (payMode == "Online") {
      this.renewPlanForm.get("recordPaymentDTO").get("chequeDate").disable();
      this.renewPlanForm.get("recordPaymentDTO").get("bankName").enable();
      this.renewPlanForm.get("recordPaymentDTO").get("branch").enable();
      this.renewPlanForm.get("recordPaymentDTO").get("referenceNo").enable();
      this.renewPlanForm.get("recordPaymentDTO").get("chequeNo").disable();
    } else if (payMode == "Cash") {
      this.renewPlanForm.get("recordPaymentDTO").get("chequeDate").disable();
      this.renewPlanForm.get("recordPaymentDTO").get("bankName").disable();
      this.renewPlanForm.get("recordPaymentDTO").get("branch").disable();
      this.renewPlanForm.get("recordPaymentDTO").get("referenceNo").disable();
      this.renewPlanForm.get("recordPaymentDTO").get("chequeNo").disable();
    }
  }

  async changePlanType($event) {
    this.spinner.show();
    this.isPlanTypeAddon = false;
    this.planListByType = [];
    setTimeout(() => {
      console.log("this.filterPlanListCust2", this.filterPlanListCust);
      if ($event.value != null && $event.value != undefined) {
        if (this.filterPlanListCust && this.filterPlanListCust.length > 0) {
          if ($event.value === "Addon") {
            this.isPlanTypeAddon = true;
            this.planListByType = this.filterPlanListCust.filter(
              plan => plan.planGroup === "Volume Booster"
            );
          } else if ($event.value === "Renew") {
            this.isPlanTypeAddon = false;
            this.planListByType = this.filterPlanListCust.filter(
              plan => plan.planGroup === "Renew"
            );
          }
        } else {
          this.spinner.hide();
        }
      }
      this.spinner.hide();
    }, 3000);
  }

  getPlanDetailById($event) {
    const id = $event.value;
    const url = "/postpaidplan/" + id;
    this.customerdetailsilsService.getMethod(url).subscribe(
      (response: any) => {
        this.selPlanData = response.postPaidPlan;
        // console.log('this.selPlanData', this.selPlanData)
        const date = new Date();
        this.selPlanData.activationDate = this.datepipe.transform(date, "dd-MM-yyyy");
        this.selPlanData.expiryDate = date.setDate(date.getDate() + this.selPlanData.validity);
        this.selPlanData.expiryDate = this.datepipe.transform(
          this.selPlanData.expiryDate,
          "dd-MM-yyyy"
        );
        this.selPlanData.finalAmount = this.selPlanData.offerprice + this.selPlanData.taxamount;
        this.spinner.hide();
      },
      (error: any) => {
        // console.log(error, "error")
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  changePlan() {
    this.renewPlansubmitted = true;
    if (this.renewPlanForm.valid) {
      this.renewPlanData = this.renewPlanForm.value;
      this.renewPlanData.isAdvRenewal = false;
      this.renewPlanData.custId = this.customerLedgerDetailData.id;
      if (!this.renewPlanData.recordPaymentDTO) {
        this.renewPlanData.recordPaymentDTO = {};
      } else {
        this.renewPlanData.recordPaymentDTO.isTdsDeducted = false;
        this.renewPlanData.recordPaymentDTO.custId = this.customerLedgerDetailData.id;
      }
      this.renewPlanData.isRefund = false;
      const url = "/subscriber/changePlan";
      // return;
      // console.log("this.renewPlanData", this.renewPlanData);
      this.customerdetailsilsService.postMethod(url, this.renewPlanData).subscribe(
        (response: any) => {
          if (response.responseCode == 200) {
            this.renewPlansubmitted = false;
            this.renewPlanForm.reset();
            this.renewPlanForm.get("isPaymentReceived").setValue("false");
            this.selPlanData = {
              quotatype: "",
              quotatime: "",
              quota: "",
              quotaUnit: "",
              quotaunittime: "",
              validity: "",
              offerprice: "",
              taxamount: "",
              activationDate: "",
              expiryDate: "",
              finalAmount: ""
            };
          } else {
            this.toastr.error(`${response.responseMessage}`, "Failed!");
            this.renewPlanForm.get("isPaymentReceived").setValue("false");
          }
        },
        (error: any) => {
          // console.log(error, "error")
          this.toastr.error(`${error.error.ERROR}`, "Failed!");
          this.renewPlanForm.get("isPaymentReceived").setValue("false");
          this.spinner.hide();
        }
      );
    }
  }

  addWalletIncustomer(custID) {
    this.isCustomerLedgerOpen = false;
    this.isCustomerDetailOpen = false;
    this.customerTicketView = false;
    this.isPaymentOpen = false;
    this.isInvoiceOpen = false;
    this.isRenewPlanOpen = false;
    this.isPlanTypeAddon = false;
    this.renewPlansubmitted = false;
    this.ifWalletMenu = true;
    this.isRatingMenu = false;
    this.isInventoryOpen = false;
    this.ifService = false;
    this.isUsageHistoryOpen = false;
    // custID=65
    let data = {
      CREATE_DATE: "",
      END_DATE: "",
      amount: "",
      balAmount: "",
      create_DATE: "",
      custId: custID,
      description: "",
      end_DATE: "",
      id: "",
      refNo: "",
      transcategory: "",
      transtype: ""
    };
    const url = "/wallet";
    this.customerdetailsilsService.postRevenueMethod(url, data).subscribe((response: any) => {
      this.getWallatData = response;
    });
  }

  //Get Inventory Details
  getInventoryDetails(id) {
    this.isCustomerLedgerOpen = false;
    this.isCustomerDetailOpen = false;
    this.isPaymentOpen = false;
    this.isInvoiceOpen = false;
    this.isRenewPlanOpen = false;
    this.ifWalletMenu = false;
    this.isRatingMenu = false;
    this.isInventoryOpen = true;
    this.ifService = false;
    this.isUsageHistoryOpen = false;
    this.spinner.show();
    const url = "/inwards/getAllCustomerInventoryList?custId=" + id;
    this.customerdetailsilsService.inventoryGetMethod(url).subscribe(
      (res: any) => {
        this.assignedInventoryList = res.dataList;
        this.spinner.hide();
        setTimeout(() => {
          this.updateRowGroupMetaData();
        }, 1000);
      },
      (error: any) => {
        this.toastr.error(`${error.error.msg}`, "Failed!");
        this.spinner.hide();
      }
    );
  }
  updateRowGroupMetaData() {
    this.rowGroupMetadata = {};

    if (this.assignedInventoryList) {
      for (let i = 0; i < this.assignedInventoryList.length; i++) {
        let rowData = this.assignedInventoryList[i];
        let representativeName = rowData.itemAssemblyName;

        if (i == 0) {
          this.rowGroupMetadata[representativeName] = { index: 0, size: 1 };
        } else {
          let previousRowData = this.assignedInventoryList[i - 1];
          let previousRowGroup = previousRowData.itemAssemblyName;
          if (representativeName === previousRowGroup)
            this.rowGroupMetadata[representativeName].size++;
          else this.rowGroupMetadata[representativeName] = { index: i, size: 1 };
        }
      }
    }
  }

  // openInventoryDetailModal(modalId, data) {
  //   this.inventoryDetailData = this.assignedInventoryList.find(element => element.id == data.id);
  //   this.inoutwardMacMappingData = this.assignedInventoryList.find(
  //     element => element.id == data.id
  //   ).inOutWardMACMapping;
  //   if (this.inoutwardMacMappingData.length != 0) {
  //     this.oldMAC = this.inoutwardMacMappingData[0].macAddress;
  //     this.oldSerial = this.inoutwardMacMappingData[0].serialNumber;
  //   }
  //   $("#openInventoryDetailModal").modal("show");
  // }

  getserviceData(groupId) {
    this.spinner.show();
    let services = [];
    this.isCustomerLedgerOpen = false;
    this.isCustomerDetailOpen = false;
    this.isPaymentOpen = false;
    this.isInvoiceOpen = false;
    this.isRenewPlanOpen = false;
    this.ifWalletMenu = false;
    this.isRatingMenu = false;
    this.isInventoryOpen = false;
    this.ifService = true;
    this.isUsageHistoryOpen = false;
    const url =
      "/subscriber/getPlanByCustService/" +
      this.customerLedgerDetailData.id +
      "?isAllRequired=true";

    this.customerdetailsilsService.getMethod(url).subscribe((response: any) => {
      this.custServiceData = [];
      this.custServiceData = response.dataList;
      this.spinner.hide();
    });
  }

  getUsageDetails(groupId) {
    // this.spinner.show();
    // let services = [];
    this.isCustomerLedgerOpen = false;
    this.isCustomerDetailOpen = false;
    this.isPaymentOpen = false;
    this.isInvoiceOpen = false;
    this.isRenewPlanOpen = false;
    this.ifWalletMenu = false;
    this.isRatingMenu = false;
    this.isInventoryOpen = false;
    this.ifService = false;
    this.isUsageHistoryOpen = true;
  }

  saveEditNickName(serviceMappingID, nickName) {
    this.spinner.show();
    let data = {};
    const url = `/subscriber/nickName?custServiceMappingId=${serviceMappingID}&name=${nickName}`;
    this.customerdetailsilsService.postMethod(url, data).subscribe(
      (response: any) => {
        if (response.responseCode == 406) {
          this.spinner.hide();

          this.toastr.error(`${response.responseMessage}`, "Failed!");
        } else {
          this.spinner.hide();
          //this.getserviceData("");
          this.toastr.success(`Successfully`, "Success!");
        }
      },
      (error: any) => {
        console.log(error, "error");
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
    this.spinner.hide();
  }

  editCustomer() {
    this.isEditing = true;
  }

  stopEditing() {
    this.createcustomerData.firstname = this.customerLedgerDetailData.firstname;
    this.createcustomerData.lastname = this.customerLedgerDetailData.lastname;
    this.createcustomerData.lastname = this.customerLedgerDetailData.altemail;
    this.createcustomerData.mobile = this.customerLedgerDetailData.mobile;
    this.createcustomerData.altmobile = this.customerLedgerDetailData.altmobile;
    this.createcustomerData.email = this.customerLedgerDetailData.email;
    this.createcustomerData.altemail = this.customerLedgerDetailData.altemail;
    this.createcustomerData.contactperson = this.customerLedgerDetailData.contactperson;
    this.birthDate = moment(this.customerLedgerDetailData.birthDate)
      .format("YYYY-MM-DD")
      .toString();
    this.isEditing = false;
  }
  convertToUTC(date: any) {
    if (date && !isNaN(new Date(date).getTime())) {
    const d = new Date(date);
    // Local date → remove timezone offset → pure UTC date
    const utcDate = new Date(Date.UTC(d.getFullYear(), d.getMonth(), d.getDate()));

    return utcDate.toISOString(); // Example: 2025-11-30T00:00:00.000Z
    }
  }
  saveCustomer() {
    const email = this.createcustomerData.email;

    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

    if (!emailPattern.test(email)) {
      return; // ❌ Stop save
    }

    this.isEditing = false;

    this.spinner.show();
    const url = "/customers/" + this.customerID;
    this.createcustomerData.birthDate = this.birthDate ? this.convertToUTC(this.birthDate) : null;
    this.createcustomerData.custname =
      this.createcustomerData.firstname + "  " + this.createcustomerData.lastname;
    this.customerdetailsilsService.updateMethod(url, this.createcustomerData).subscribe(
      (response: any) => {
        this.toastr.success(`Customer details updated successfully.`, "Success!");

        this.getCustomersDetail();
        this.spinner.hide();
      },
      (error: any) => {
        this.spinner.hide();
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
      }
    );
  }

  nameModelOpen() {
    this.isNameEdit = true;
  }

  nameModelClose() {
    this.createcustomerData.firstname = this.customerLedgerDetailData.firstname;
    this.createcustomerData.lastname = this.customerLedgerDetailData.lastname;
    this.isNameEdit = false;
  }

  save() {
    this.isNameEdit = false;
    // this.createcustomerData.firstname = this.customerLedgerDetailData.firstname;
    // this.createcustomerData.lastname = this.customerLedgerDetailData.lastname;
  }

  sendemailinvoice(docNo) {
    if (docNo) {
      const downloadUrl = "/invoice/send/" + docNo;
      this.customerdetailsilsService.getmethodforrevenue(downloadUrl).subscribe(
        (response: any) => {
          console.log("response ::", response);
          this.spinner.hide();

          this.toastr.success(`${response.msg}`, "Success!");
        },
        (error: any) => {
          this.spinner.hide();
          console.log(error, "error");
          this.toastr.error(`${error.error.ERROR}`, "Failed!");
        }
      );
    }
  }

  generatePDFInvoice(docId, custId) {
    if (docId) {
      this.spinner.show();
      let url = "";
      if (this.customerLedgerDetailData.status === "NewActivation") {
        url = "/generateTrialPdfByInvoiceId/" + docId;
      } else {
        url = "/generatePdfByInvoiceId/" + docId;
      }

      this.customerdetailsilsService.getmethodforrevenue(url).subscribe(
        (response: any) => {
          this.spinner.hide();
          this.toastr.success(`${response.responseMessage}`, "Success!");
          this.getCustInvoice(custId);
        },
        (error: any) => {
          console.log(error, "error");
          this.toastr.error(`${error.error.ERROR}`, "Failed!");
          this.spinner.hide();
        }
      );
    }
  }

  getRatingDetails(customerId) {
    this.isCustomerLedgerOpen = false;
    this.isCustomerDetailOpen = false;
    this.isPaymentOpen = false;
    this.isInvoiceOpen = false;
    this.isRenewPlanOpen = false;
    this.ifWalletMenu = false;
    this.isInventoryOpen = false;
    this.ifService = false;
    this.isRatingMenu = true;
    this.getFeedbackBuCustomer();
    this.ratingForm.reset();
    this.isRatingError = false;
    this.isEditRating = false;
    this.isRatingPresent = false;
    this.ratingForm.updateValueAndValidity();
  }

  saveRating() {
    this.isRatingError = true;
    if (this.ratingForm.valid) {
      var request = this.ratingForm.value;
      request.custId = this.customerLedgerDetailData.id;
      this.customerFeedbackService.saveCustomerFeedback(request).subscribe(
        (response: any) => {
          this.toastr.success(`${response.message}`, "Success!");

          this.isRatingPresent = true;
          this.spinner.hide();
          this.customerFeedback = response.customerFeedback;
          this.ratingForm.patchValue(response.customerFeedback);
        },
        (error: any) => {
          this.toastr.error(`${error.error.ERROR}`, "Failed!");
          this.spinner.hide();
        }
      );
    }
  }

  editRating() {
    this.isEditRating = true;
  }

  cancelEditRating() {
    this.isEditRating = false;
    if (this.customerFeedback) this.ratingForm.patchValue(this.customerFeedback);
  }

  getFeedbackBuCustomer() {
    this.spinner.show();
    this.customerFeedbackService.getCustomerFeedback(this.customerLedgerDetailData.id).subscribe(
      (response: any) => {
        if (response.status == 200) {
          this.isRatingPresent = true;
          this.customerFeedback = response.customerFeedback;
          this.ratingForm.patchValue(response.customerFeedback);
        }
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  updateRating() {
    this.isRatingError = true;
    if (this.ratingForm.valid) {
      var request = this.ratingForm.value;
      request.custId = this.customerLedgerDetailData.id;
      this.customerFeedbackService.updateCustomerFeedback(request).subscribe(
        (response: any) => {
          this.toastr.success(`${response.message}`, "Success!");

          this.isRatingPresent = true;
          this.isEditRating = false;
          this.customerFeedback = response.customerFeedback;
          this.ratingForm.patchValue(response.customerFeedback);
          this.spinner.hide();
        },
        (error: any) => {
          this.toastr.error(`${error.error.ERROR}`, "Failed!");
          this.spinner.hide();
        }
      );
    }
  }
  checkPaymentGatewayConfiguration() {
    this.spinner.show();
    this.customerdetailsilsService.getActivePaymentConfiguration().subscribe(
      (response: any) => {
        this.savedConfig = [];
        if (response.status == 204) {
          this.isPaymentGatewayConfigured = false;
        } else {
          var activeConfig = response.activePaymentConfig;
          var config = activeConfig.some(config => config.paymentConfigName == this.paymentGateway);
          this.savedConfig = activeConfig;

          const keyValuePairs: { [key: string]: any } = {};
          for (const config of this.savedConfig) {
            for (const mappingItem of config.paymentConfigMappingList) {
              keyValuePairs[mappingItem.paymentParameterName] = mappingItem.paymentParameterValue;
            }
          }
          this.paymentkeyValuePairs = keyValuePairs;
          this.isPaymentGatewayConfigured = config;
        }
      },
      (error: any) => {
        this.toastr.error(`${error.error.errorMessage}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  dialogRef: MatDialogRef<any> | null = null;
  openPaymentGatewaysforInvoicePayment(invoice: any) {
    console.log("invoice :::", invoice);
    this.displayInvoicePaymentDialog = false;
    console.log("this.savedConfig :::", this.savedConfig);
    this.dialogRef?.close();
    if (this.savedConfig.length === 0) {
      this.toastr.error(`Payment Gateway Configuration Not Found!`, "Failed!");
    } else if (this.savedConfig.length === 1) {
      if (this.savedConfig[0].paymentConfigName === "MoMo Pay") {
        this.spinner.show();
        this.buyMomoInvoicePayment(invoice);
      } else if (this.savedConfig[0].paymentConfigName === "AIRTEL") {
        this.spinner.show();
        this.airtelPayPlan(invoice);
      } else if (this.savedConfig[0].paymentConfigName === "SELCOM") {
        this.spinner.show();
        this.selcomPayPlan(invoice);
      } else {
        this.toastr.error(`Invoice payment is not available for this gateway.`, "Failed!");
      }
    } else if (this.savedConfig.length >= 1) {
      this.invoice = invoice;
      this.displayInvoicePaymentDialog = true;
      this.dialog.open(this.payamentGetwayMethode, {
        width: "600px",
        disableClose: false
      });
    }
  }

  @ViewChild("payamentGetwayMethode") payamentGetwayMethode!: TemplateRef<any>;
  invoicePayment(savedConfig: any) {
    console.log("saveConfigInDialog ::::", savedConfig);
    this.invoicePaymentpaymentGateway(savedConfig, "");
  }

  invoicePaymentpaymentGateway(selectedConfig: any, dialogRef: any) {
    this.payMethod = selectedConfig.paymentConfigName;
    console.log("selectedConfig.paymentConfigName ::::", selectedConfig.paymentConfigName);
    this.showMpinModal();
    dialogRef.close();
  }

  invoicePaymentGateway(dialogRef) {
    if (this.payMethod === "MoMo Pay") {
      this.spinner.show();
      this.buyMomoInvoicePayment(this.invoice);
    } else if (this.payMethod === "AIRTEL") {
      this.spinner.show();
      this.airtelPayPlan(this.invoice);
    } else if (this.payMethod === "SELCOM") {
      this.spinner.show();
      this.selcomPayPlan(this.invoice);
    } else if (this.payMethod === "MPESA") {
     this.buyMpesaExpressPlan(this.invoice);
    }
     else {
      this.toastr.error(`Invoice payment is not available for this gateway.`, "Failed!");
    }
    dialogRef.close();
  }

  @ViewChild("mpinModalDailog") mpinModalDailog!: TemplateRef<any>;
  showMpinModal() {
    this.displayInvoicePaymentDialog = false;
    this.mpinModal = true;
    // this.momoPayinvoice = invoice;
    this.mpinForm.controls.countryCode.setValue(this.customerLedgerDetailData.countryCode);
    this.mpinForm.controls.mobileNumber.setValue(this.customerLedgerDetailData.mobile);

    this.dialog.open(this.mpinModalDailog, {
      width: "550px",
      disableClose: false
    });

    // this.mpinForm.controls.mobileNumber.reset();
  }
  buyMomoInvoicePayment(invoice) {
    this.exitBuy = true;
    this.isMpinFormSubmitted = true;
    this.mpinModal = false;
    this.paymentstatusCount = RadiusConstants.TIMER_COUNT;
    let rawAmount = invoice.totalamount - invoice.adjustedAmount;
    let formattedAmount = rawAmount.toFixed(2);
    let data = {
      customerId: this.createcustomerData.id,
      amount: formattedAmount,
      isFromCaptive: false,
      merchantName: "MoMo Pay",
      customerUserName: this.createcustomerData.username,
      customerUUID: uuid.v4(),
      mvnoId: this.createcustomerData.mvnoId,
      mobileNumber:
        this.mpinForm.value.countryCode.replace("+", "") + (this.mpinForm.value.mobileNumber ?? ""),
      invoiceId: invoice.id,
      partnerId: this.createcustomerData.partnerid,
      accountNumber: this.customerLedgerDetailData?.acctno ?? ""
    };
    console.log(data);
    this.customerdetailsilsService.buyPlanUsingMomo(data).subscribe(
      (response: any) => {
        this.spinner.hide();
        //localStorage.setItem("transactionId"),
        (localStorage.setItem("transactionId", response.data.data.orderId),
          console.log("in response of momo buy"));
        this.paymentConfirmationModal = true;
        this.isMpinFormSubmitted = false;
        this.mobileError = false;
        this.inputMobile = "";
        this.mpinForm.reset();
        this.mpinForm.controls.countryCode.setValue("");
        this.mpinForm.controls.mobileNumber.setValue("");
        this.exitBuy = false;
        // this.subscription2 = this.obs1$.subscribe((d) => {
        //   if (this.paymentstatusCount > 0) {
        //     this.paymentstatusCount = this.paymentstatusCount - 1;
        //     this.getStatusSuccessByMomo("SUCCESSFUL");
        //     if (this.transactionStatus === true) {
        //       this.subscription2.unsubscribe();
        //     }
        //   }
        //   if (this.paymentstatusCount == 0) {
        //
        //     this.subscription2.unsubscribe();
        //   }
        // });
        this.spinner.hide();
      },
      (error: any) => {
        this.spinner.hide();

        this.toastr.error(`Something went wrong`, "Failed!");
      }
    );
  }

  getStatusSuccessByMomo(status) {
    this.spinner.hide();
    let data = {
      orderId: localStorage.getItem("transactionId"),
      status: status
    };
    this.customerdetailsilsService.getIntigrationTransactionstatus(data).subscribe(
      (response: any) => {
        console.log("transactionresponse:::", response);
        if (response.responseCode == 200) {
          if (response.data.istransactionsuccess === "true") {
            this.transactionStatus = response.istransactionsuccess;
            let data = {
              userName: this.customerLedgerData.username,
              password: this.customerLedgerData.password
            };
            // this.getDevice(data);
            this.paymentConfirmationModal = false;
            this.subscription2.unsubscribe();
            this.paymentSucessModel = true;
          }
        }
      },
      (error: any) => {
        this.spinner.hide();
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
      }
    );
  }
  hidepaymentConfirmDialog() {
    this.paymentConfirmationModal = false;
    this.displayInvoicePaymentDialog = false;
  }

  hidepaymentSucessDialog() {
    this.paymentSucessModel = false;
  }
  airtelPayPlan(invoice) {
    this.exitBuy = true;
    this.isMpinFormSubmitted = true;
    this.mpinModal = false;
    this.paymentstatusCount = RadiusConstants.TIMER_COUNT;
    let rawAmount = invoice.totalamount - invoice.adjustedAmount;
    let formattedAmount = rawAmount.toFixed(2);
    let data = {
      customerId: this.createcustomerData.id,
      amount: formattedAmount,
      isFromCaptive: false,
      merchantName: "AIRTEL",
      customerUserName: this.createcustomerData.username,
      mvnoId: this.createcustomerData.mvnoId,
      mobileNumber: this.mpinForm.value.mobileNumber ?? "",
      invoiceId: invoice.id,
      partnerId: this.createcustomerData.partnerid,
      accountNumber: this.customerLedgerDetailData?.acctno ?? ""
    };
    console.log(data);
    this.customerdetailsilsService.buyPlanUsingAirtel(data).subscribe(
      (response: any) => {
        this.spinner.hide();
        this.isMpinFormSubmitted = false;
        this.mpinForm.reset();
        this.mpinForm.controls.countryCode.setValue("");
        this.mpinForm.controls.mobileNumber.setValue("");
        //localStorage.setItem("transactionId"),
        if (response.responseCode === 417) {
          this.toastr.error(`${response.responseMessage}`, "Failed!");
          return;
        }
        (localStorage.setItem("transactionId", response.data.data.transaction.id),
          console.log("in response of AIrtel buy"));
        this.paymentConfirmationModal = true;
        this.mobileError = false;
        this.inputMobile = "";
        this.exitBuy = false;
        // this.subscription2 = this.obs1$.subscribe((d) => {
        //   if (this.paymentstatusCount > 0) {
        //     this.paymentstatusCount = this.paymentstatusCount - 1;
        //     this.getStatusSuccessByMomo("SUCCESSFUL");
        //     if (this.transactionStatus === true) {
        //       this.subscription2.unsubscribe();
        //     }
        //   }
        //   if (this.paymentstatusCount == 0) {
        //     this.subscription2.unsubscribe();
        //   }
        // });
        this.spinner.hide();
      },
      (error: any) => {
        this.spinner.hide();

        this.toastr.error(`Something went wrong`, "Failed!");
      }
    );
  }

  selcomPayPlan(invoice) {
    this.exitBuy = true;
    this.isMpinFormSubmitted = true;
    this.mpinModal = false;
    this.paymentstatusCount = RadiusConstants.TIMER_COUNT;

    let customerPaymentDTO = {
      amount: (invoice.totalamount - invoice.adjustedAmount).toString(),
      buid: this.createcustomerData.buId,
      custServiceMappingId: this.createcustomerData.planMappingList[0].custServiceMappingId,
      customerId: this.createcustomerData.id,
      customerUUID: uuid.v4(),
      customerUserName: this.createcustomerData.username,
      invoiceId: invoice.id,
      isBuyPlan: true,
      isFromCaptive: true,
      merchantName: "SELCOM",
      mobileNumber:
        this.mpinForm.value.countryCode.replace("+", "") + (this.mpinForm.value.mobileNumber ?? ""),
      mvnoId: this.createcustomerData.mvnoId,
      orderId: null,
      partnerId: this.createcustomerData.partnerid,
      partnerPaymentId: this.createcustomerData.partnerPaymentId ?? null,
      requestFor: this.createcustomerData.requestFor ?? null,
      status: this.createcustomerData.status
    };

    let selcomPayPayment = {
      vendor: "",
      order_id: null,
      buyer_email: this.createcustomerData.email,
      buyer_name: this.createcustomerData.username,
      buyer_phone:
        this.mpinForm.value.countryCode.replace("+", "") + (this.mpinForm.value.mobileNumber ?? ""),
      gateway_buyer_uuid: "",
      amount: (invoice.totalamount - invoice.adjustedAmount).toString(),
      currency: "",
      payment_methods: "",
      "billing.firstname": this.createcustomerData.firstname ?? "",
      "billing.lastname": this.createcustomerData.lastname ?? "",
      "billing.address_1": this.createcustomerData?.addressList[0]?.landmark ?? "",
      "billing.city": this.presentAdressDATA.cityName ?? "",
      "billing.state_or_region": this.presentAdressDATA.stateName ?? "",
      "billing.country": this.presentAdressDATA.countryName ?? "",
      "billing.phone":
        this.mpinForm.value.countryCode.replace("+", "") + (this.mpinForm.value.mobileNumber ?? ""),
      no_of_items: 1,
      webhook: ""
    };

    let data = {
      customerPaymentDTO: customerPaymentDTO,
      selcomPayPayment: selcomPayPayment
    };
    console.log(data);
    this.customerdetailsilsService.buyPlanUsingSelcom(data).subscribe(
      (response: any) => {
        this.spinner.hide();
        this.mobileError = false;
        this.inputMobile = "";
        this.isMpinFormSubmitted = false;
        this.mpinForm.reset();
        //localStorage.setItem("transactionId"),
        if (response.responseCode === 417) {
          this.toastr.error(`${response.responseMessage}`, "Failed!");
          return;
        } else if (response.responseCode === 200 && response.data && response.data.data) {
          const paymentLink = response.data.data;
          window.open(paymentLink, "_blank");
        } else {
          this.toastr.error(
            `${response.responseMessage}|| "Unexpected response received.`,
            "Failed!"
          );
        }
        this.spinner.hide();
      },
      (error: any) => {
        this.spinner.hide();

        this.toastr.error(`Something went wrong`, "Failed!");
      }
    );
  }
  wifiModalOpen(inventory) {
    this.wifiInventory = inventory;
    let obj = {
      custInvenId: this.wifiInventory.id,
      customerId: this.customerLedgerDetailData.id,
      itemId: this.wifiInventory.itemId,
      serialNumber: this.wifiInventory.inOutWardMACMapping[0].serialNumber
    };
    let urldoc = "/nmsIntegration/getWifiConfig";
    this.customerdetailsilsService.postIntegrationMethod(urldoc, obj).subscribe(
      (response: any) => {
        if (response.responseCode == 200) {
          this.wifiModel = true;
          if (
            response.data.ssidUsername !== null &&
            response.data.ssidPassword !== null &&
            response.data.workingFrequency !== null
          ) {
            this.wifiForm.patchValue({
              username: response.data.ssidUsername,
              password: response.data.ssidPassword,
              workingFrequency: response.data.workingFrequency
            });
            this.editWifi = true;
            // this.editWifiPassword = true;
          } else {
            this.editWifi = false;
          }
          console.log(this.wifiForm.value);
        } else {
          this.toastr.error(`${response.responseMessage}`, "Failed!");
        }
      },
      (error: any) => {
        console.log(error, "error");
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
      }
    );
  }

  saveWifi() {
    this.wifiSubmitted = true;
    if (this.wifiForm.valid) {
      let obj = {
        custInvenId: this.wifiInventory.id,
        customerId: this.customerLedgerDetailData.id,
        itemId: this.wifiInventory.itemId,
        serialNumber: this.wifiInventory.inOutWardMACMapping[0].serialNumber,
        ssidPassword: this.wifiForm.value.password,
        ssidUsername: this.wifiForm.value.username,
        workingFrequency: this.wifiForm.value.workingFrequency
      };
      let data = obj;
      let urldoc = "/nmsIntegration/NMSWifiConfig";
      this.customerdetailsilsService.postIntegrationMethod(urldoc, data).subscribe(
        (response: any) => {
          if (response.responseCode == 200) {
            this.toastr.error(`${response.responseMessage}`, "Failed!");
            this.wifiSubmitted = false;
            this.wifiModel = false;
            // this.editWifiPassword = true;
            this.wifiForm.reset();
          } else {
            this.toastr.error(`${response.responseMessage}`, "Failed!");
          }
        },
        (error: any) => {
          console.log(error, "error");
          this.toastr.error(`${error.error.ERROR}`, "Failed!");
        }
      );
    }
  }

  editWifiModel() {
    this.editWifi = !this.editWifi;
  }

  closeWifiModal() {
    this.wifiModel = false;
    this.wifiForm.reset();
  }

  getDemographicLabel(currentName: string): string {
    if (!this.demographicLabel || this.demographicLabel.length === 0) {
      return currentName;
    }
    const label = this.demographicLabel.find(item => item.currentName === currentName);
    return label ? label.newName : currentName;
  }

  onInputMobile(event: any) {
    const inputElement = event.target as HTMLInputElement;
    const inputValue = inputElement.value;

    // Check if the input starts with 0
    if (inputValue.startsWith("0")) {
      this.mobileError = true;
    } else {
      this.mobileError = false;
    }
  }
  onKeymobilelength(event) {
    const str = this.mpinForm.value.mobileNumber.toString();
    const withoutCommas = str.replace(/,/g, "");
    const strrr = withoutCommas.trim();
    let mobilenumberlength = this.commondropdownService.commonMoNumberLength;
    if (mobilenumberlength === 0 || mobilenumberlength === null) {
      mobilenumberlength = 10;
    }
    if (strrr.length > Number(mobilenumberlength)) {
      this.inputMobile = `${mobilenumberlength} character required.`;
    } else if (strrr.length == Number(mobilenumberlength)) {
      this.inputMobile = "";
    } else {
      this.inputMobile = `${mobilenumberlength} character required.`;
    }
  }

  hideMpinModal() {
    this.isMpinFormSubmitted = false;
    this.mpinForm.reset();
    // this.mpinForm.updateValueAndValidity();
    this.mpinModal = false;
    this.mobileError = false;
    this.inputMobile = "";
  }

  serviceAreaAndBuildingNameFromCustomerId() {
    const url = "/BuildingAndSubareaNames/" + this.customerID;
    this.customerdetailsilsService.getMethod(url).subscribe(
      (response: any) => {
        this.presentAdressDATA.subarea = response?.data?.name;
        this.presentAdressDATA.buildingName = response?.data?.building_name;
      },
      (error: any) => {
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
      }
    );
  }

  searchInvoices() {
    this.currentPageinvoiceMasterSlab = 1;
    this.searchinvoiceMaster("", "");
  }
  inventoryData = new BehaviorSubject({
    inventoryData: ""
  });
  openInventoryDetailModal(modalId, data) {
    this.CustomerInventoryDetailsService.show(data);
    this.inventoryData.next({
      inventoryData: data
    });
  }
  currencySymbol = localStorage.getItem("CURRENCY_SYMBOL");
  currency: string;
  ledgerDisplayedColumns: string[] = [
    "createDate",
    "receiptNo",
    "invoiceNo",
    "category",
    "debit",
    "credit",
    "balAmount",
    "remarks"
  ];
  paymentDisplayedColumns: string[] = [
    "paymentBy",
    "referenceNo",
    "paymode",
    "paymentreferenceno",
    "amount",
    "paymentdate",
    "status"
  ];
  buyplan = false;
  activeTabIndex = 0;
  onTabChange(event: any) {
    this.buyplan = false;
    switch (event.index) {
      case 0:
        this.getCustomerData();
        break;
      case 1:
        if (this.customerLedgerDetailData.status == "Active") {
          this.getCustomersLedger(this.customerLedgerDetailData.id, "");
        } else {
          // event.stopPropagation();
        }
        break;
      case 2:
        if (
          this.customerLedgerDetailData.parentCustomerId == null ||
          this.customerLedgerDetailData.invoiceType == "Independent"
        ) {
          this.getCustPayment(this.customerLedgerDetailData.id);
        } else {
          // event.stopPropagation();
        }
        break;
      case 3:
        this.getCustInvoice(this.customerLedgerDetailData.id);
        break;
      case 4:
        this.buyplan = true;
        // if (this.customerLedgerDetailData.status == "Active") {
        //   this.renewPlan(this.customerLedgerDetailData);
        // } else {
        //   // event.stopPropagation();
        // }
        break;
      case 5:
        this.getInventoryDetails(this.customerLedgerDetailData.id);
        break;
      // case 5:
      //   if (this.customerLedgerDetailData.status == "Active") {
      //     this.addWalletIncustomer(this.customerLedgerDetailData.id);
      //   } else {
      //     // event.stopPropagation();
      //   }
      //   break;
      // case 7:
      //   this.getRatingDetails(this.customerLedgerDetailData.id);
      //   break;

      case 8:
        break;
    }
  }
  emailPattern: string = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

  numberOnly(event: KeyboardEvent): boolean {
    const pattern = /[0-9]/;
    const inputChar = String.fromCharCode(event.keyCode);

    if (!pattern.test(inputChar)) {
      event.preventDefault();
      return false;
    }
    return true;
  }

    buyMpesaExpressPlan(invoice) {
        this.exitBuy = true;
        this.isMpinFormSubmitted = true;
        this.mpinModal = false;
        this.paymentstatusCount = RadiusConstants.TIMER_COUNT;
        let data = {
            customerId: this.createcustomerData.id,
            amount: (invoice?.totalamount - invoice?.adjustedAmount).toString(),
            // isFromCaptive: true,
            customerUserName: this.createcustomerData.username,
            // customerUUID: uuid.v4(),
            mvnoId: this.createcustomerData.mvnoId,
            // mobileNumber:
            //     this.createcustomerData.countryCode.replace("+", "") +
            //     (this.createcustomerData.mobile ?? ""),
            payerMobileNumber:
                this.mpinForm.value.countryCode.replace("+", "") +
                (this.mpinForm.value.mobileNumber ?? ""),
            // merchantName: null,
            // invoiceId: invoice.id,
            // partnerId: this.createcustomerData.partnerid,
            accountNumber: this.createcustomerData?.acctno ?? "",
            // custServiceMappingId: this.createcustomerData.planMappingList[0].custServiceMappingId,
            // buid: this.createcustomerData?.buId,
            // orderId: "",
            // planId: this.createcustomerData.planMappingList[0].planId,
            // hash: null,
            // isAdvancePayment: false,
            // isBuyPlan: true,
            // partnerPaymentId: this.createcustomerData.partnerid,
            // status: "PENDING"
        };
        this.customerdetailsilsService.buyPlanUsingMpesaExpress(data).subscribe(
            (response: any) => {
                this.spinner.hide();
                if (response.responseCode == 200) {
                    this.paymentConfirmationModal = true;
                    this.toastr.success(`${response.data.ResponseDescription}`, 'Success!');


                } else {
                    this.toastr.info(`${response?.data?.errorMessage}`, 'Info!');

                }
                this.spinner.hide();
            },
            (error: any) => {
                this.spinner.hide();
                this.toastr.error(`${error.error.ERROR}`, 'Something went wrong!');


            }
        );
    }
}
