import { Component, EventEmitter, Inject, Input, OnInit, Output, ViewChild, TemplateRef, Optional } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { BillRunMasterService } from "src/app/service/bill-run-master.service";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from "@angular/material/dialog";
import { ToastrService } from 'ngx-toastr';


export interface fetchData {
    auditcustid: any,
    checkHierachy: any,
    planId: any
}

@Component({
    selector: "app-workflow-audit-details-modal",
    templateUrl: "./workflow-audit-details-modal.component.html",
    styleUrls: ["./workflow-audit-details-modal.component.css"],
    standalone: false,
})
export class WorkflowAuditDetailsModalComponent implements OnInit {

    @Input() dialogId: string;
    @Input() auditcustid: Observable<any>;
    @Output() closeParentCustt = new EventEmitter();
    @Input() custName = "";
    viewworkflowAuditDataData: any;
    @ViewChild("auditDetail") auditDetail;
    displayedColumns = ['dynamicHeader', 'action', 'staffName', 'remark', 'actionDate'];
    displayedColumns1 = ['staffname', 'oldDiscount', 'newDiscount', 'updateddate', 'remarks'];
    // auditcustid = new BehaviorSubject<any>(null); // ✅ initialize here
    pageSize = RadiusConstants.ITEMS_PER_PAGE;
    pageIndex = 0
    currentPageMasterSlab = 1;
    MasteritemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    MastertotalRecords: String;

    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 0;
    workflowAuditData = [];
    discountDetailsData = [];
    ID: any = "";
    CutomerEventID: any;
    check_Hierachy = "";
    ifAuditStatusDetialShow = false;
    ifDiscountStatusDetialShow = false;
    planStatusDetail: any = [];
    ifStatusDetialhide: boolean;
    modalId: string;
    ifModelIsShow: boolean = false;
    nameTitile = " Partner";
    DiscountPlanID: any;

    @ViewChild('detailsDialog') detailsDialog!: TemplateRef<any>;
    constructor(
        private toastr: ToastrService,

        private matdialog: MatDialog,
        private messageService: MessageService,
        private spinner: NgxSpinnerService,
        private billRunMasterService: BillRunMasterService,
        private paymentamountService: PaymentamountService,
        public commondropdownService: CommondropdownService, private dialog: MatDialog,
        @Optional() public dialogRef: MatDialogRef<WorkflowAuditDetailsModalComponent>,
        @Optional() @Inject(MAT_DIALOG_DATA) public data: fetchData,
    ) { }

    ngOnInit(): void {
        this.ifModelIsShow = true;
        // if (this.data) {
        //     this.ID = this.data.auditcustid;
        //     this.check_Hierachy = this.data.checkHierachy;
        //     this.DiscountPlanID = this.data.planId;

        //     this.getAllEvent(this.check_Hierachy);
        // }
        this.auditcustid.subscribe(value => {

            if (value.auditcustid) {
                this.ID = value.auditcustid;
                this.check_Hierachy = value.checkHierachy;
                this.DiscountPlanID = value.planId;
                this.getAllEvent(this.check_Hierachy);
            }
        });
        this.getworkflowAuditDetails("");
        this.getAllEvent("");
    }

    ngAfterViewInit() {
        this.matdialog.open(this.auditDetail, {
            width: '1200px',
        });
    }

    getAllEvent(check_Hierachy) {
        let hierachyValue = check_Hierachy;
        if (this.check_Hierachy == "TerminationCAF") {
            hierachyValue = "TERMINATION";
        }

        const url = "/commonList/hierarchy_event";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                let eventData = response.dataList;
                eventData = response.dataList.filter(element => element.value == hierachyValue);
                if (eventData[0]) {
                    this.CutomerEventID = eventData[0].id;
                }
                switch (check_Hierachy) {
                    case "TerminationCAF":
                        this.ifAuditStatusDetialShow = true;
                        this.nameTitile = "Customer Name";
                        if (this.CutomerEventID) {
                            this.getworkflowAuditDetails("");
                        }
                        break;
                    case "CUSTOMER_SERVICE_ADD":
                        this.ifAuditStatusDetialShow = false;
                        this.nameTitile = "Customer Service Add";
                        if (this.CutomerEventID) {
                            this.getHierarchyStatus();
                        }
                        break;
                    case "CUSTOMER_SERVICE_TERMINATION":
                        this.ifAuditStatusDetialShow = false;
                        this.nameTitile = "Customer Service";
                        if (this.CutomerEventID) {
                            this.getHierarchyStatus();
                        }
                        break;
                    case "TERMINATION":
                        this.ifAuditStatusDetialShow = false;
                        this.nameTitile = "Customer Name";
                        this.getHierarchyStatus();
                        break;
                    case "PAYMENT":
                        this.ifAuditStatusDetialShow = false;
                        this.nameTitile = "Customer Name";
                        this.getHierarchyStatus();
                        break;
                    case "PLAN":
                        this.ifAuditStatusDetialShow = false;
                        this.nameTitile = "Plan Name";
                        this.getHierarchyStatus();
                        break;
                    case "PLAN_GROUP":
                        this.ifAuditStatusDetialShow = false;
                        this.nameTitile = "Plan Name";
                        this.getHierarchyStatus();
                        break;
                    case "CUSTOMER_DISCOUNT":
                        this.ifAuditStatusDetialShow = false;
                        this.ifDiscountStatusDetialShow = false;
                        this.nameTitile = "Customer Name";
                        this.getHierarchyStatus();
                        break;
                    case "BILL_TO_ORGANIZATION":
                        this.ifAuditStatusDetialShow = false;
                        this.nameTitile = "Workflow Audit";
                        this.getHierarchyStatus();
                        break;
                    case "DOCUMENT_VERIFICATION":
                        this.ifAuditStatusDetialShow = false;
                        this.nameTitile = "Customer Name";
                        this.getHierarchyStatus();
                        break;
                    case "SHIFT_LOCATION":
                        this.ifAuditStatusDetialShow = false;
                        this.nameTitile = "Customer Name";
                        this.getHierarchyStatus();
                        break;
                    case "SPECIAL_PLAN_MAPPING":
                        this.ifAuditStatusDetialShow = false;
                        this.nameTitile = "Customer Name";
                        this.getHierarchyStatus();
                        break;
                    case "CREDIT_NOTE":
                        this.ifAuditStatusDetialShow = false;
                        this.nameTitile = "Credit Document No";
                        this.getHierarchyStatus();
                        break;
                    case "CUSTOMER_SERVICE_TERMINATION":
                        this.ifAuditStatusDetialShow = false;
                        this.nameTitile = "Customer Service Terminate";
                        if (this.CutomerEventID) {
                            this.getworkflowAuditDetails("");
                        }
                        break;
                    case "LEAD":
                        this.ifAuditStatusDetialShow = false;
                        this.nameTitile = "Lead Name";
                        this.getHierarchyStatus();
                        break;
                    case "PARTNER_BALANCE":
                        this.ifAuditStatusDetialShow = false;
                        this.nameTitile = "Partner Name";
                        this.getHierarchyStatus();
                        break;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    BackshowAuditData(data) {
        this.ifAuditStatusDetialShow = false;
    }

    show_AuditData() {
        this.ifAuditStatusDetialShow = true;
        // if(this.check_Hierachy == 'TerminationCAF'){
        //   this.check_Hierachy = 'CAF'
        //   this.getAllEvent(this.check_Hierachy)
        // }
        // else{
        this.getworkflowAuditDetails("");
        // }
    }

    show_DiscountData() {
        this.ifDiscountStatusDetialShow = true;
        this.getDiscountDetails("");
    }

    ststusData() {
        this.ifAuditStatusDetialShow = false;
        this.ifDiscountStatusDetialShow = false;
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageMasterSlab > 1) {
            this.currentPageMasterSlab = 1;
        }
        this.getworkflowAuditDetails(this.showItemPerPage);
    }

    getworkflowAuditDetails(size) {
        let page_list;
        let page = this.currentPageMasterSlab;
        if (size) {
            page_list = size;
            this.MasteritemsPerPage = size;
        } else {
            size = this.MasteritemsPerPage;
            // if (this.showItemPerPage == 0) {
            //     this.MasteritemsPerPage = this.pageITEM;
            // } else {
            //     this.MasteritemsPerPage = this.showItemPerPage;
            // }
        }
        this.workflowAuditData = [];

        let data = {
            page: page,
            pageSize: size,
        };
        let url = "/workflowaudit/list?entityId=" + this.ID + "&eventName=" + this.check_Hierachy;
        // if (
        //   this.check_Hierachy === "CAF" ||
        //   this.check_Hierachy === "TerminationCAF" ||
        //   this.check_Hierachy === "Termination"
        // ) {
        //   url = "/workflowaudit/list?entityId=" + this.ID + "&eventName=" + this.check_Hierachy;
        // } else if (this.check_Hierachy === "PAYMENT") {
        //   url = "/workflowaudit/list?entityId=" + this.ID + "&eventName=" + this.check_Hierachy;
        // } else if (this.check_Hierachy === "PLAN") {
        //   url = "/workflowaudit/list?entityId=" + this.ID + "&eventName=" + this.check_Hierachy;
        // } else if (this.check_Hierachy === "CUSTOMER_DISCOUNT") {
        //   url = "/workflowaudit/list?entityId=" + this.ID + "&eventName=" + this.check_Hierachy;
        // } else if (this.check_Hierachy === "BILL_TO_ORGANIZATION") {
        //   url = "/workflowaudit/list?entityId=" + this.ID + "&eventName=" + this.check_Hierachy;
        // } else {
        //   url = "/workflowaudit/list?entityId=" + this.ID + "&eventName=" + this.check_Hierachy;
        // }

        this.billRunMasterService.postMethod(url, data).subscribe(
            (response: any) => {
                this.workflowAuditData = response.dataList;
                this.MastertotalRecords = response.totalRecords;
            },
            (error: any) => {
                if (error.status == 200) {
                    this.toastr.error(`${error.ERROR}`, 'Failed!');


                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
                console.log(error, "error");
            }
        );
    }

    getDiscountDetails(size) {
        // let page_list;
        // if (size) {
        //     page_list = size;
        //     this.MasteritemsPerPage = size;
        // } else {
        //     if (this.showItemPerPage == 0) {
        //         this.MasteritemsPerPage = this.pageITEM;
        //     } else {
        //         this.MasteritemsPerPage = this.showItemPerPage;
        //     }
        // }
        this.discountDetailsData = [];

        let url = "/subscriber/changediscountaudit/" + this.ID;
        this.billRunMasterService.getMethod(url).subscribe(
            (response: any) => {
                this.discountDetailsData = response.dataList;
                // this.MastertotalRecords = response.totalRecords;
            },
            (error: any) => {
                if (error.status == 200) {
                    this.toastr.error(`${error.ERROR}`, 'Failed!');

                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
                console.log(error, "error");
            }
        );
    }

    pageChangedMasterList(pageNumber) {
        this.currentPageMasterSlab = pageNumber.pageIndex + 1;
        this.MasteritemsPerPage = pageNumber.pageSize;
        this.getworkflowAuditDetails("");
    }

    getHierarchyStatus() {
        let url = `/teamHierarchy/getApprovalProgress?entityId=${this.ID}&eventName=${this.check_Hierachy}`;
        // if (this.check_Hierachy == "Termination" || this.check_Hierachy == "TerminationCAF") {
        //   url = `/teamHierarchy/getApprovalProgress?entityId=${this.ID}&eventName=TERMINATION`;
        //   this.nameTitile = "Customer";
        // } else if (this.check_Hierachy == "PAYMENT") {
        //   url = `/teamHierarchy/getApprovalProgress?entityId=${this.ID}&eventName=PAYMENT`;
        //   this.nameTitile = "Customer";
        // } else if (this.check_Hierachy == "PLAN") {
        //   url = `/teamHierarchy/getApprovalProgress?entityId=${this.ID}&eventName=PLAN`;
        //   this.nameTitile = "Plan";
        // } else if (this.check_Hierachy == "CUSTOMER_DISCOUNT") {
        //   url = `/teamHierarchy/getApprovalProgress?entityId=${this.ID}&eventName=CUSTOMER_DISCOUNT`;
        //   this.nameTitile = "Discount";
        // } else if (this.check_Hierachy == "BILL_TO_ORGANIZATION") {
        //   url = `/teamHierarchy/getApprovalProgress?entityId=${this.ID}&eventName=BILL_TO_ORGANIZATION`;
        //   this.nameTitile = "Workflow Audit";
        // }

        this.billRunMasterService.getMethod(url).subscribe(
            (response: any) => {
                this.planStatusDetail = response.dataList;
            },
            (error: any) => {
                if (error.error.status == 417) {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                } else {
                    this.toastr.error(`${error.error.msg}`, 'Failed!');


                }
            }
        );
    }

    closeModal() {
        this.dialog.closeAll();
        this.closeParentCustt.emit();
        this.ifAuditStatusDetialShow = false;
        this.ifDiscountStatusDetialShow = false;
        this.paymentamountService.hide(this.dialogId);
        this.ifModelIsShow = false;
    }

    // saveSelstaff() {
    //   this.ifModelIsShow=false;
    // }

    // modalCloseStaff() {
    //   this.ifModelIsShow=false;
    // }

    closeSelectStaff() {
        // this.closeParentCustt.emit();
        this.ifModelIsShow = false;
    }


    auditDisplayedColumns = ['entityName', 'action', 'staff', 'remark', 'date'];
    discountDisplayedColumns = ['staff', 'oldDiscount', 'newDiscount', 'date', 'remarks'];
}
