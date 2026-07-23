import { Component, EventEmitter, OnInit, Output, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup } from "@angular/forms";
import { ConfirmationService, MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { SearchPaymentService } from "src/app/service/search-payment.service";
import { CustomerDetailsService } from "src/app/service/customer-details.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { CustomerDetailsComponent } from "../common/customer-details/customer-details.component";
import { BehaviorSubject } from "rxjs";
import * as FileSaver from "file-saver";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { PaymentAmountModelComponent } from "src/app/components/payment-amount-model/payment-amount-model.component";
import { WorkflowAuditDetailsModalComponent } from "src/app/components/workflow-audit-details-modal/workflow-audit-details-modal.component";
import { SystemconfigService } from "src/app/service/systemconfig.service";
import { CREDIT_NOTES } from "src/app/constants/aclConstants";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { MatSort } from '@angular/material/sort';
import { ToastrService } from 'ngx-toastr';
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatTableDataSource } from '@angular/material/table';
import { CustomerSelectComponent } from "src/app/components/customer-select/customer-select.component";
declare var $: any;

@Component({
    selector: "app-invoice-search-payment",
    templateUrl: "./invoice-search-payment.component.html",
    styleUrls: ["./invoice-search-payment.component.css"],
    standalone: false
})
export class InvoiceSearchPaymentComponent implements OnInit {
    @ViewChild(MatSort) sort!: MatSort;
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    displayedColumns = ['customer', 'amount', 'documentNo', 'referenceNo', 'creditDate', 'creditBy', 'remark', 'status', 'action'];
    @Output() CloseParentCustomerList = new EventEmitter();
    @ViewChild("approverandrejectDialog") approverandrejectDialog: TemplateRef<any>;
    approverandrejectdialogRef!: MatDialogRef<any>;
    @ViewChild(CustomerDetailsComponent)
    customerDetailModal: CustomerDetailsComponent;
    customerDetailsdialogRef!: MatDialogRef<any>;
    @ViewChild(PaymentAmountModelComponent)
    PaymentDetailModal: PaymentAmountModelComponent;
    paymentDetailsdialogRef!: MatDialogRef<any>;
    @ViewChild(CustomerSelectComponent)
    customerSelectdialogRef!: MatDialogRef<any>;
    @ViewChild(WorkflowAuditDetailsModalComponent)
    custauditWorkflowModal: WorkflowAuditDetailsModalComponent;
    dialogId: boolean = false;
    searchPaymentFormGroup: UntypedFormGroup;
    submitted: boolean = false;
    displayInvoiceDetails: boolean = false;
    customerData: any;
    searchPaymentData = new MatTableDataSource<any>([]);;
    currentPagePaymentSlab = 1;
    paymentitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    paymenttotalRecords: number;
    isPaymentSearch: boolean = false;
    customerid: any = "";
    payfromdate = "";
    paytodate = "";
    creditDocumentNumber = "";
    paystatus = "";
    recepit: any;
    custId = new BehaviorSubject({
        custId: ""
    });
    paymentId = new BehaviorSubject({
        paymentId: ""
    });

    auditcustid = new BehaviorSubject({
        auditcustid: "",
        checkHierachy: "",
        planId: ""
    });
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 0;
    searchkey: string;
    totalAreaListLength = 0;
    payStatus = [
        { label: "Adjusted", value: "Fully Adjusted" },
        { label: "Generated", value: "pending" },
        { label: "Partialy Adjusted", value: "Partialy Adjusted" }
    ];

    AclClassConstants;
    AclConstants;
    public loginService: LoginService;
    staffID: number;
    reject = false;
    rejectCAF = [];
    selectStaffReject: any;
    approved = false;
    approveCAF = [];
    selectStaff: any;
    approveId: any;
    mobileNumber = "";
    invoiceNumber = "";
    referenceno = "";
    remarks: any;
    currency: string;
    ifModelIsShow: boolean = false;
    downloadAccess: boolean = false;
    reprintAccess: boolean = false;
    reassignAccess: boolean = false;

    showParentCustomerModel = false;
    custType = "both";
    searchStaffDeatil: any;
    approveCafData: any[];


    constructor(
        private matdialog: MatDialog,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private searchPaymentService: SearchPaymentService,
        private customerManagementService: CustomermanagementService,
        public PaymentamountService: PaymentamountService,
        public commondropdownService: CommondropdownService,
        loginService: LoginService,
        private systemService: SystemconfigService,
        private toastr: ToastrService
    ) {
        this.downloadAccess = loginService.hasPermission(CREDIT_NOTES.CREDIT_NOTE_DOWNLOAD);
        this.reprintAccess = loginService.hasPermission(CREDIT_NOTES.CREDIT_NOTE_REPRINT);
        this.reassignAccess = loginService.hasPermission(CREDIT_NOTES.CREDIT_NOTE_REASSIGN);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
    }
    assignPLANForm: UntypedFormGroup;
    ngOnInit(): void {
        let staffID = localStorage.getItem("userId");
        this.staffID = Number(staffID);
        this.commondropdownService.getCustomerStatus();
        this.commondropdownService.getPostpaidplanData();
        const serviceArea = localStorage.getItem("serviceArea");

        let serviceAreaArray = JSON.parse(serviceArea);
        if (serviceAreaArray.length !== 0) {
            this.commondropdownService.filterserviceAreaList();
        } else {
            this.commondropdownService.getserviceAreaList();
        }
        this.assignPLANForm = this.fb.group({
            remark: [""]
        });
        this.systemService.getConfigurationByName("CURRENCY_FOR_PAYMENT").subscribe((res: any) => {
            this.currency = res.data.value;
        });
    }

    ngAfterViewInit() {
        this.searchPaymentData.sort = this.sort;
        this.searchPaymentData.paginator = this.paginator;
    }

    openPaymentInvoiceModal(id, paymentId) {
        this.displayInvoiceDetails = true;
        this.PaymentamountService.show(id);
        this.paymentId.next({
            paymentId: paymentId
        });
        this.paymentDetailsdialogRef = this.matdialog.open(PaymentAmountModelComponent, {
            width: '50%',
            maxWidth: '90vw',
            height: 'auto',
            autoFocus: false,
            data: { paymentId: paymentId },
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
    }
    getCustomer() {
        // const url = "/customers/list";
        // let custerlist = {
        //     page: 1,
        //     pageSize: 10000
        // };
        // this.searchPaymentService.postMethod(url, custerlist).subscribe(
        //     (response: any) => {
        //         let serviceArea: any = [];
        //         serviceArea = JSON.parse(localStorage.getItem("serviceArea"));
        //         if (serviceArea.length > 0) {
        //             let customerListData = [];
        //             for (var idx = 0; idx < response.customerList.length; idx++) {
        //                 var custobj = response.customerList[idx];
        //                 if (serviceArea.includes(custobj.networkDetails.serviceareaid)) {
        //                     customerListData.push(custobj);
        //                 }
        //             }
        //             this.customerData = customerListData;
        //         } else {
        //             this.customerData = response.customerList;
        //         }
        //     },
        //     (error: any) => {
        //         this.toastr.error(`${error.error.ERROR}`, 'Failed!');
        //     }
        // );
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPagePaymentSlab > 1) {
            this.currentPagePaymentSlab = 1;
        }
        if (!this.searchkey) {
            this.searchPayment(this.showItemPerPage);
        }
    }

    openModal(id, custId) {
        this.dialogId = true;
        this.custId.next({
            custId: custId
        });
        this.customerDetailsdialogRef = this.matdialog.open(CustomerDetailsComponent, {
            width: '70%',
            maxWidth: '90vw',
            height: 'auto',
            autoFocus: false,
            data: { custId: custId },
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        })
    }

    closeSelectStaff() {
        this.dialogId = false;
    }

    searchPayment(size) {
        let page_list;
        if (size) {
            page_list = size;
            this.paymentitemsPerPage = size;
        } else {
            if (this.showItemPerPage == 0) {
                this.paymentitemsPerPage = this.pageITEM;
            } else {
                this.paymentitemsPerPage = this.showItemPerPage;
            }
        }

        let url: any = "";
        url =
            "/paymentGateway/payment/search?type=CreditNote&page=" +
            this.currentPagePaymentSlab +
            "&pageSize=" +
            this.paymentitemsPerPage;
        if (this.customerid) {
            url = url + "&customerid=" + this.customerid;
        }
        if (this.paystatus) {
            url = url + "&paystatus=" + this.paystatus;
        }
        if (this.paytodate) {
            url = url + "&paytodate=" + this.paytodate;
        }
        if (this.invoiceNumber) {
            url = url + "&invoiceNumber=" + this.invoiceNumber;
        }
        if (this.referenceno) {
            url = url + "&referenceno=" + this.referenceno;
        }
        if (this.mobileNumber) {
            url = url + "&mobileNumber=" + this.mobileNumber;
        }
        if (this.payfromdate) {
            url = url + "&payfromdate=" + this.payfromdate;
        }
        if (this.creditDocumentNumber) {
            url = url + "&creditDocumentNumber=" + this.creditDocumentNumber;
        }

        this.searchPaymentService.getMethod(url).subscribe(
            (response: any) => {
                if (response.creditDocumentPojoList != null) {
                    this.searchPaymentData.data = response.creditDocumentPojoList;
                    if (this.showItemPerPage > this.paymentitemsPerPage) {
                        this.totalAreaListLength = this.searchPaymentData.data.length % this.showItemPerPage;
                    } else {
                        this.totalAreaListLength = this.searchPaymentData.data.length % this.paymentitemsPerPage;
                    }
                    this.isPaymentSearch = true;
                    this.paymenttotalRecords = response.pageDetails?.totalRecords;
                    this.toastr.success(`Record fetched successfully`, 'Success!');
                } else {
                    this.toastr.info(`${response.message}`, 'Info!');
                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    clearPayment() {
        this.isPaymentSearch = false;
        this.customerid = "";
        this.payfromdate = "";
        this.paytodate = "";
        this.paystatus = "";
        this.mobileNumber = "";
        this.invoiceNumber = "";
        this.referenceno = "";
        this.creditDocumentNumber = "";
        this.searchPaymentData.data = [];
    }

    pageChangedPaymentList(event: PageEvent) {
        this.paymentitemsPerPage = event.pageSize;
        this.currentPagePaymentSlab = event.pageIndex + 1;
        this.searchPayment(this.paymentitemsPerPage);
    }
    // pageChangedPaymentList(pageNumber) {
    //     this.currentPagePaymentSlab = pageNumber;
    //     this.searchPayment("");
    // }

    downloadreceipt(id: any) {
        const url = "/payment/generatereceipt/" + id;
        this.searchPaymentService.downloadPDF(url).subscribe(
            (response: any) => {
                var file = new Blob([response], { type: "application/pdf" });
                var fileURL = URL.createObjectURL(file);
                FileSaver.saveAs(file, "bill.pdf");
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    ifApproveStatus = false;
    approveRejectRemark = "";
    ticketApprRejectData: any = [];

    approveModalOpen(data) {
        this.approverandrejectdialogRef = this.matdialog.open(this.approverandrejectDialog, {
            width: '50%',
            maxWidth: '90vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
        this.approveRejectRemark = "";
        this.ifApproveStatus = true;
        this.ticketApprRejectData = data;
        $("#ApproveRejectModal").modal("show");
    }

    rejectModalOpen(data) {
        this.approverandrejectdialogRef = this.matdialog.open(this.approverandrejectDialog, {
            width: '50%',
            maxWidth: '90vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
        this.approveRejectRemark = "";
        this.ifApproveStatus = false;
        this.ticketApprRejectData = data;
        $("#ApproveRejectModal").modal("show");
    }

    statusRejected() {
        this.approveId = this.ticketApprRejectData.id;
        this.reject = false;
        this.selectStaffReject = null;
        this.rejectCAF = [];
        let rejectdata = {
            customerid: this.ticketApprRejectData.custId,
            idlist: Number(this.ticketApprRejectData.id),
            paymode: this.ticketApprRejectData.paymode,
            paystatus: this.ticketApprRejectData.status,
            paytodate: this.ticketApprRejectData.paymentdate,
            referenceno: this.ticketApprRejectData.referenceno,
            remarks: this.approveRejectRemark
        };
        const url = "/payment/reject";
        this.searchPaymentService.postMethod(url, rejectdata).subscribe(
            (response: any) => {
                $("#ApproveRejectModal").modal("hide");
                this.approverandrejectdialogRef.close();
                if (response.payment.dataList) {
                    this.reject = true;
                    this.rejectCAF = response.payment.dataList;
                    $("#rejectCustomerCAFModal").modal("show");
                } else {
                    this.searchPayment("");
                }
                this.ifApproveStatus = false;
                this.ticketApprRejectData = [];
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    closeParentCustt() {
        this.matdialog.closeAll();
        this.ifModelIsShow = false;
    }

    closeParentCust() {
        this.displayInvoiceDetails = false;
    }

    statusApporeved() {
        this.approveId = this.ticketApprRejectData.id;
        this.approved = false;
        this.approveCAF = [];
        this.selectStaff = null;
        let approvedData = {
            customerid: this.ticketApprRejectData.custId,
            idlist: Number(this.ticketApprRejectData.id),
            paymode: this.ticketApprRejectData.paymode,
            paystatus: this.ticketApprRejectData.status,
            paytodate: this.ticketApprRejectData.paymentdate,
            referenceno: this.ticketApprRejectData.referenceno,
            remarks: this.approveRejectRemark
        };

        const url = "/payment/approve";
        this.searchPaymentService.postMethod(url, approvedData).subscribe(
            (response: any) => {
                $("#ApproveRejectModal").modal("hide");
                this.approverandrejectdialogRef.close();
                if (response.payment.dataList) {
                    this.approved = true;
                    this.approveCAF = response.payment.dataList;
                    this.approveCafData = this.approveCAF;
                    $("#assignCustomerCAFModal").modal("show");
                } else {
                    this.searchPayment("");
                }
                this.ifApproveStatus = false;
                this.ticketApprRejectData = [];
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    assignToStaff(flag) {
        let url: any;
        if (flag == true) {
            if (this.selectStaff) {
                url = `/teamHierarchy/assignFromStaffList?entityId=${this.approveId
                    }&eventName=${"PAYMENT"}&nextAssignStaff=${this.selectStaff}&isApproveRequest=${flag}`;
            } else {
                url = `/teamHierarchy/assignEveryStaff?entityId=${this.approveId
                    }&eventName=${"PAYMENT"}&isApproveRequest=${flag}`;
            }
        } else {
            if (this.selectStaffReject) {
                url = `/teamHierarchy/assignFromStaffList?entityId=${this.approveId
                    }&eventName=${"PAYMENT"}&nextAssignStaff=${this.selectStaffReject
                    }&isApproveRequest=${flag}`;
            } else {
                url = `/teamHierarchy/assignEveryStaff?entityId=${this.approveId
                    }&eventName=${"PAYMENT"}&isApproveRequest=${flag}`;
            }
        }

        this.searchPaymentService.getMethod(url).subscribe(
            (response: any) => {
                $("#assignCustomerCAFModal").modal("hide");
                $("#rejectCustomerCAFModal").modal("hide");
                this.approverandrejectdialogRef.close();
                this.searchPayment("");
                if (response.status == 200) {
                }
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    assignCreditnoteToStaff(flag) {
        let url: any;
        if (flag == true) {
            if (this.selectStaff) {
                url = `/teamHierarchy/assignFromStaffList?entityId=${this.approveId
                    }&eventName=${"CREDIT_NOTE"}&nextAssignStaff=${this.selectStaff}&isApproveRequest=${flag}`;
            } else {
                url = `/teamHierarchy/assignEveryStaff?entityId=${this.approveId
                    }&eventName=${"CREDIT_NOTE"}&isApproveRequest=${flag}`;
            }
        } else {
            if (this.selectStaffReject) {
                url = `/teamHierarchy/assignFromStaffList?entityId=${this.approveId
                    }&eventName=${"CREDIT_NOTE"}&nextAssignStaff=${this.selectStaffReject
                    }&isApproveRequest=${flag}`;
            } else {
                url = `/teamHierarchy/assignEveryStaff?entityId=${this.approveId
                    }&eventName=${"CREDIT_NOTE"}&isApproveRequest=${flag}`;
            }
        }

        this.searchPaymentService.getMethod(url).subscribe(
            (response: any) => {
                $("#assignCustomerCAFModal").modal("hide");
                $("#rejectCustomerCAFModal").modal("hide");
                this.searchPayment("");
                if (response.status == 200) {
                }
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    openPaymentWorkFlow(id, auditcustid) {
        this.PaymentamountService.show(id);
        this.auditcustid.next({
            auditcustid: auditcustid,
            checkHierachy: "PAYMENT",
            planId: ""
        });
    }
    openCreditNoteWorkFlow(id, auditcustid) {
        this.ifModelIsShow = true;
        this.PaymentamountService.show(id);
        this.auditcustid.next({
            auditcustid: auditcustid,
            checkHierachy: "CREDIT_NOTE",
            planId: ""
        });
    }
    creditNoteReprint(id) {
        const url = "/payment/generatereceipt/" + id;
        this.searchPaymentService.downloadPDF(url).subscribe(
            (response: any) => {
                const file = new Blob([response], { type: "application/pdf" });
                const fileURL = URL.createObjectURL(file);
                FileSaver.saveAs(file, id);
                this.toastr.success(`File Downloaded`, 'Success!');
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    currentPageParentCustomerListdata = 1;
    parentCustomerListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    parentCustomerListdatatotalRecords: any;
    selectedParentCust: any = [];
    selectedParentCustId: any;
    parentCustList: any;
    newFirst = 0;
    searchParentCustOption = "";
    searchParentCustValue = "";
    parentFieldEnable = false;
    customerList = [];
    searchOptionSelect = this.commondropdownService.customerSearchOption;

    // customer dropdown
    getParentCustomerData() {
        let currentPage;
        currentPage = this.currentPageParentCustomerListdata;
        const data = {
            page: currentPage,
            pageSize: this.parentCustomerListdataitemsPerPage
        };
        const url = "/customers/list";
        this.searchPaymentService.postMethod(url, data).subscribe(
            (response: any) => {
                this.customerList = response.customerList;
                this.parentCustomerListdatatotalRecords = response.pageDetails.totalRecords;
                this.newFirst = 1;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    async modalOpenParentCustomer() {
        // this.matdialog.open(this.ParentCustomerList);
        this.showParentCustomerModel = true;
        this.selectedParentCust = [];
        this.customerSelectdialogRef = this.matdialog.open(CustomerSelectComponent, {
            width: '1500px',
            maxWidth: '70vw',
            height: 'auto',
            autoFocus: false,
            data: {
                type: 'both', // or 'postpaid'
                custId: this.customerid
            },
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });

        this.customerSelectdialogRef.afterClosed().subscribe(result => {
            if (result) {
                // reuse existing method
                this.selectedCustChange(result);
            } else {
                this.showParentCustomerModel = false;
            }
        });
    }

    async selectedCustChange(event) {
        this.showParentCustomerModel = false;
        this.selectedParentCust = event;

        this.parentCustList = [
            {
                id: Number(this.selectedParentCust.id),
                name: this.selectedParentCust.name
            }
        ];
        this.customerid = Number(this.selectedParentCust.id);
    }
    closeCust() {
        this.matdialog.closeAll();
        this.CloseParentCustomerList.emit();
        this.showParentCustomerModel = false;
    }

    paginate(event) {
        this.currentPageParentCustomerListdata = event.page + 1;
        if (this.searchParentCustValue) {
            this.searchParentCustomer();
        } else {
            this.getParentCustomerData();
        }
    }

    clearSearchParentCustomer() {
        this.currentPageParentCustomerListdata = 1;
        this.getParentCustomerData();
        this.searchParentCustValue = "";
        this.searchParentCustOption = "";
        this.parentFieldEnable = false;
    }

    searchParentCustomer() {
        this.currentPageParentCustomerListdata = 1;
        const searchParentData = {
            filters: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "any",
                    filterOperator: "equalto",
                    filterCondition: "and"
                }
            ],
            page: this.currentPageParentCustomerListdata,
            pageSize: this.parentCustomerListdataitemsPerPage
        };

        searchParentData.filters[0].filterValue = this.searchParentCustValue;
        searchParentData.filters[0].filterColumn = this.searchParentCustOption.trim();

        const url = "/parentCustomers/search/" + RadiusConstants.CUSTOMER_TYPE.PREPAID;
        this.searchPaymentService.postMethod(url, searchParentData).subscribe(
            (response: any) => {
                if (response.status == 204) {
                    this.toastr.info(`${response.msg}`, 'Info!');
                    this.parentCustomerListdatatotalRecords = 0;
                } else {
                    this.customerList = response.customerList;
                    this.parentCustomerListdatatotalRecords = response.pageDetails.totalRecords;
                }
            },
            (error: any) => {
                this.parentCustomerListdatatotalRecords = 0;
                if (error.error.status == 400 || error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            }
        );
    }

    selParentSearchOption(event) {
        if (event.value) {
            this.parentFieldEnable = true;
        } else {
            this.parentFieldEnable = false;
        }
    }

    pickModalOpen(data) {
        let url = "/workflow/pickupworkflow?eventName=CREDIT_NOTE&entityId=" + data.id;
        this.searchPaymentService.getMethod(url).subscribe(
            (response: any) => {
                this.searchPayment("");
                if (response.responseCode == 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                } else {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    approvableStaff: any = [];
    reAssignPLANModalApprove: boolean = false;
    assignedCreditNoteid: any;
    StaffReasignList1(data) {
        let url = `/teamHierarchy/reassignWorkflowGetStaffList?entityId=${data.id}&eventName=CREDIT_NOTE`;
        this.searchPaymentService.getMethod(url).subscribe(
            (response: any) => {
                this.assignedCreditNoteid = data.id;
                this.approvableStaff = [];
                if (response.responseCode == 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                } else {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');
                }
                if (response.dataList != null) {
                    this.approvableStaff = response.dataList;
                    this.approved = true;
                    this.reAssignPLANModalApprove = true;
                } else {
                    this.reAssignPLANModalApprove = false;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    closeStaffReasignListForTermination() {
        this.reAssignPLANModalApprove = false;
    }
    reassignWorkflow() {
        let url: any;
        if (this.assignedCreditNoteid == null) {
            this.toastr.error(`Please Approve Before reasign`, 'Failed!');
        } else {
            this.remarks = this.assignPLANForm.value.remark;
            url = `/teamHierarchy/reassignWorkflow?entityId=${this.assignedCreditNoteid}&eventName=CREDIT_NOTE&assignToStaffId=${this.selectStaff}&remark=${this.remarks}`;

            this.searchPaymentService.getMethod(url).subscribe(
                (response: any) => {
                    this.reAssignPLANModalApprove = false;
                    this.searchPayment("");
                    this.getCustomer();
                    if (response.responseCode == 417) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');
                    } else {
                        this.toastr.success(`Assigned to the next staff successfully`, 'Success!');
                    }
                },
                error => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    searchStaffByName() {
        if (this.searchStaffDeatil) {
            this.approveCAF = this.approveCafData.filter(
                staff =>
                    staff.fullName.toLowerCase().includes(this.searchStaffDeatil.toLowerCase()) ||
                    staff.username.toLowerCase().includes(this.searchStaffDeatil.toLowerCase())
            );
        } else {
            this.approveCAF = this.approveCafData;
        }
    }

    clearSearchForm() {
        this.searchStaffDeatil = "";
        this.approveCAF = this.approveCafData;
    }
}
