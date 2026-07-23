import { Component, OnInit } from "@angular/core";
import { NgxSpinnerService } from "ngx-spinner";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { ActivatedRoute, Router } from "@angular/router";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { MessageService } from "primeng/api";
import moment from "moment";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { LoginService } from "src/app/service/login.service";
import { BehaviorSubject, Observable, Observer } from "rxjs";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { CustomerInventoryMappingService } from "src/app/service/customer-inventory-mapping.service";
import { POST_CUST_CONSTANTS, PRE_CUST_CONSTANTS } from "src/app/constants/aclConstants";
import { ToastrService } from 'ngx-toastr';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ViewChild, TemplateRef } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { BillRunMasterService } from "src/app/service/bill-run-master.service";

declare var $: any;

@Component({
    selector: "app-customer-change-discount",
    templateUrl: "./customer-change-discount.component.html",
    styleUrls: ["./customer-change-discount.component.scss"],
    standalone: false
})
export class CustomerChangeDiscountComponent implements OnInit {
    @ViewChild('approveRejectDialog') approveRejectDialog: TemplateRef<any>;
    @ViewChild('assignStaffDialog') assignStaffDialog: TemplateRef<any>;
    @ViewChild('rejectStaffDialog') rejectStaffDialog: TemplateRef<any>;
    @ViewChild('workflowAuditDialog') workflowAuditDialog: TemplateRef<any>;
    @ViewChild('pickDialog') pickDialog: TemplateRef<any>;
    private currentDialogRef: MatDialogRef<any>;
    custType: any;
    loggedInStaffId = localStorage.getItem("userId");
    partnerId = Number(localStorage.getItem("partnerId"));
    userId = Number(localStorage.getItem("userId"));
    customerId: number;

    customerCustDiscountListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    OlddiscountData = [];
    CustDiscountShowItemPerPage = 1;
    custCustDiscountList: any = [];
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    customerLedgerDetailData: any;
    AclClassConstants;
    AclConstants;
    maxDiscountValue = 99;
    chargeType = [{ label: "One-time" }, { label: "Recurring" }];
    isnewDiscount: boolean = true;
    approveId: any;
    assignDiscountData: any = [];
    discountFlageType = "";
    AppRjecHeader = "";
    assignAppRejectDiscountForm: UntypedFormGroup;
    assignDiscounsubmitted = false;
    approved = false;
    reject = false;
    rejectInventoryData = [];
    approveInventoryData = [];
    selectStaff: any;
    discountId: any;
    selectStaffReject: any;
    newCustomerAddressDataForCustometr: any;
    oldDiscValue: number;
    newDiscValue: number;
    auditcustid = new BehaviorSubject({
        auditcustid: "",
        checkHierachy: "",
        planId: ""
    });
    ifModelIsShow: boolean = false;
    rejectApproveDiscountModal: boolean = false;
    assignCustomerInventoryModal: boolean = false;
    rejectCustomerInventoryModal: boolean = false;
    auditAccess: boolean = false;
    updateAccess: boolean = false;
    searchStaffDeatil: any;
    approveData: any[];
    displayedColumns: string[] = [
        'connectionNo',
        'service',
        'currentDiscountType',
        'currentDiscount',
        'currentExpiryDate',
        'newDiscountType',
        'newDiscount',
        'newExpiryDate',
        'remarks',
        'action'
    ];
    auditColumns: string[] = ['entityName', 'auditAction', 'staffName', 'remark', 'actionDate'];
    discountColumns: string[] = ['discountStaff', 'oldDiscount', 'newDiscount', 'discountActionDate', 'discountRemarks'];
    workflowAuditData: any[] = [];
    discountDetailsData: any[] = [];
    planStatusDetail: any[] = [];
    ifAuditStatusDetialShow = false;
    ifDiscountStatusDetialShow = false;
    nameTitile = "Customer";
    check_Hierachy = "";
    ID: any;
    CutomerEventID: any;
    DiscountPlanID: any;
    dateTime: any
    constructor(
        private toastr: ToastrService,

        private spinner: NgxSpinnerService,
        public PaymentamountService: PaymentamountService,
        private customerManagementService: CustomermanagementService,
        private route: ActivatedRoute,
        private router: Router,
        private messageService: MessageService,
        public loginService: LoginService,
        private fb: UntypedFormBuilder,
        private customerInventoryMappingService: CustomerInventoryMappingService,
        private dialog: MatDialog,
        private commondropdownService: CommondropdownService,
        private billRunMasterService: BillRunMasterService
    ) {
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;
        this.auditAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_CHANGE_DISCOUNT_AUDIT
                : POST_CUST_CONSTANTS.POST_CUST_CHANGE_DISCOUNT
        );
        this.updateAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_CHANGE_UPDATE_DISCOUNT
                : POST_CUST_CONSTANTS.POST_CUST_CHANGE_DISCOUNT
        );
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;

        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
    }

    async ngOnInit() {
        this.assignAppRejectDiscountForm = this.fb.group({
            remark: ["", Validators.required]
        });
        this.getCustomersDetail(this.customerId);
        this.getcustDiscountDetails(this.customerId, "", "changeDiscount");
    }
    custDiscountWorkflowAuditopen(workflowId, id, planId) {
        this.ID = id;
        this.check_Hierachy = "CUSTOMER_DISCOUNT";
        this.DiscountPlanID = planId;
        this.getAllEvent(this.check_Hierachy);

        this.currentDialogRef = this.dialog.open(this.workflowAuditDialog, {
            width: '80%',
            maxHeight: '90vh',
            disableClose: false,
            autoFocus: false
        });
    }
    closeWorkflowAuditModal() {
        if (this.currentDialogRef) {
            this.currentDialogRef.close();
        }
        this.ifAuditStatusDetialShow = false;
        this.ifDiscountStatusDetialShow = false;
    }

    getAllEvent(check_Hierachy) {
        let hierachyValue = check_Hierachy;
        const url = "/commonList/hierarchy_event";

        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                let eventData = response.dataList;
                eventData = response.dataList.filter(element => element.value == hierachyValue);

                if (eventData[0]) {
                    this.CutomerEventID = eventData[0].id;
                    if (check_Hierachy === 'CUSTOMER_DISCOUNT') {
                        this.ifAuditStatusDetialShow = false;
                        this.ifDiscountStatusDetialShow = false;
                        this.nameTitile = "Customer Name";
                        this.getHierarchyStatus();
                    }
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }
    getHierarchyStatus() {
        let url = `/teamHierarchy/getApprovalProgress?entityId=${this.ID}&eventName=${this.check_Hierachy}`;

        this.billRunMasterService.getMethod(url).subscribe(
            (response: any) => {
                this.planStatusDetail = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR || error.error.msg}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR || error.error.msg,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }
    show_AuditData() {
        this.ifAuditStatusDetialShow = true;
        this.getworkflowAuditDetails("");
    }

    show_DiscountData() {
        this.ifDiscountStatusDetialShow = true;
        this.getDiscountDetails("");
    }

    BackshowAuditData() {
        this.ifAuditStatusDetialShow = false;
    }

    ststusData() {
        this.ifAuditStatusDetialShow = false;
        this.ifDiscountStatusDetialShow = false;
    }
    getworkflowAuditDetails(size) {
        let url = `/workflowaudit/list?entityId=${this.ID}&eventName=${this.check_Hierachy}`;
        let data = { page: 1, pageSize: 10 };

        this.billRunMasterService.postMethod(url, data).subscribe(
            (response: any) => {
                this.workflowAuditData = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    getDiscountDetails(size) {
        let url = `/subscriber/changediscountaudit/${this.ID}`;

        this.billRunMasterService.getMethod(url).subscribe(
            (response: any) => {
                this.discountDetailsData = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }
    closePickModal() {
        if (this.currentDialogRef) {
            this.currentDialogRef.close();
        }
    }

    confirmPick() {
        // Your existing pick logic here
        const url = "/pickWorkflowTask";
        const pickData = {
            entityId: this.assignDiscountData.id,
            eventName: "CUSTOMER_DISCOUNT",
            staffId: localStorage.getItem("userId")
        };

        this.customerManagementService.updateMethod(url, pickData).subscribe(
            (response: any) => {
                this.toastr.success(`Successfully picked discount request`, 'Success!')
                // this.messageService.add({
                //     severity: "success",
                //     summary: "Success",
                //     detail: "Successfully picked discount request",
                //     icon: "far fa-check-circle"
                // });
                this.closePickModal();
                this.getcustDiscountDetails(this.customerId, "", "changeDiscount");
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //         severity: "error",
                //         summary: "Error",
                //         detail: error.error.ERROR,
                //         icon: "far fa-times-circle"
                //     });
            }
        );
    }

    customerDetailOpen() {
        this.router.navigate(["/home/customer/details/" + this.custType + "/x/" + this.customerId]);
    }

    getCustomersDetail(custId) {
        const url = "/customers/" + custId;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            this.customerLedgerDetailData = response.customers;
        });
    }

    getcustDiscountDetails(custId, size, cust360Type = "") {
        let page_list;
        this.OlddiscountData = [];
        if (size) {
            page_list = size;
            this.customerCustDiscountListdataitemsPerPage = size;
        } else {
            if (this.CustDiscountShowItemPerPage == 1) {
                this.customerCustDiscountListdataitemsPerPage = this.pageITEM;
            } else {
                this.customerCustDiscountListdataitemsPerPage = this.CustDiscountShowItemPerPage;
            }
        }

        let custDiscountdatalength = 0;
        let url =
            "/subscriber/fetchCustomerDiscountDetailServiceLevel/" +
            custId +
            "?isExpiredRequired=" +
            (cust360Type === "changeDiscount");
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.custCustDiscountList = response.discountDetails;

                while (custDiscountdatalength < this.custCustDiscountList.length) {
                    if (
                        this.custCustDiscountList[custDiscountdatalength].discount === null ||
                        this.custCustDiscountList[custDiscountdatalength].discount === ""
                    ) {
                        this.custCustDiscountList[custDiscountdatalength].discount = 0;
                    }
                    this.custCustDiscountList[custDiscountdatalength].discount = parseFloat(
                        this.custCustDiscountList[custDiscountdatalength].discount
                    ).toFixed(2);

                    if (
                        this.custCustDiscountList[custDiscountdatalength].newDiscount === null ||
                        this.custCustDiscountList[custDiscountdatalength].newDiscount === ""
                    ) {
                        this.custCustDiscountList[custDiscountdatalength].newDiscount = 0;
                    }
                    this.custCustDiscountList[custDiscountdatalength].newDiscount = parseFloat(
                        this.custCustDiscountList[custDiscountdatalength].newDiscount
                    ).toFixed(2);

                    if (
                        this.custCustDiscountList[custDiscountdatalength].discountType === null ||
                        this.custCustDiscountList[custDiscountdatalength].discountType === ""
                    ) {
                        this.custCustDiscountList[custDiscountdatalength].discountType = "One-time";
                    }
                    if (
                        this.custCustDiscountList[custDiscountdatalength].newDiscountType === null ||
                        this.custCustDiscountList[custDiscountdatalength].newDiscountType === ""
                    ) {
                        this.custCustDiscountList[custDiscountdatalength].newDiscountType = "One-time";
                    }

                    if (
                        this.custCustDiscountList[custDiscountdatalength].discountExpiryDate !== null &&
                        this.custCustDiscountList[custDiscountdatalength].discountExpiryDate !== ""
                    ) {
                        this.custCustDiscountList[custDiscountdatalength].discountExpiryDate = moment(
                            this.custCustDiscountList[custDiscountdatalength].discountExpiryDate
                        )
                            .utc(true)
                            .toDate();
                    }

                    if (
                        this.custCustDiscountList[custDiscountdatalength].newDiscountExpiryDate !== null &&
                        this.custCustDiscountList[custDiscountdatalength].newDiscountExpiryDate !== ""
                    ) {
                        this.custCustDiscountList[custDiscountdatalength].newDiscountExpiryDate = moment(
                            this.custCustDiscountList[custDiscountdatalength].newDiscountExpiryDate
                        )
                            .utc(true)
                            .toDate();
                    }
                    custDiscountdatalength++;
                }
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    changeValue(value) {
        if (!value.dirty) {
            this.isnewDiscount = false;
            let msg = "value required";
        }
    }

    // custDiscountWorkflowAuditopen(id, auditcustid, planId) {
    //     this.ifModelIsShow = true;
    //     this.PaymentamountService.show(id);
    //     this.auditcustid.next({
    //         auditcustid,
    //         checkHierachy: "CUSTOMER_DISCOUNT",
    //         planId
    //     });
    // }

    closeParentCustt() {
        this.ifModelIsShow = false;
    }

    assignDiscountApprove() {
        this.assignDiscounsubmitted = true;
        if (this.assignAppRejectDiscountForm.valid) {
            let url = "/approveChangeDiscountServiceLevel";
            let assignCAFData = {
                custPackageId: this.assignDiscountData.id,
                flag: this.discountFlageType,
                nextStaffId: 0,
                planId: this.assignDiscountData.planId,
                remark: this.assignAppRejectDiscountForm.controls.remark.value,
                staffId: localStorage.getItem("userId")
            };

            this.customerManagementService.updateMethod(url, assignCAFData).subscribe(
                (response: any) => {
                    // Close current dialog
                    if (this.currentDialogRef) {
                        this.currentDialogRef.close();
                    }

                    if (response.dataList) {
                        if (this.discountFlageType == "approved") {
                            this.approved = true;
                            this.approveInventoryData = response.dataList;
                            this.approveData = this.approveInventoryData;

                            // Open assign staff dialog
                            this.currentDialogRef = this.dialog.open(this.assignStaffDialog, {
                                width: '70%',
                                maxHeight: '90vh',
                                disableClose: false,
                                autoFocus: false
                            });
                        } else {
                            this.reject = true;
                            this.rejectInventoryData = response.dataList;

                            // Open reject staff dialog
                            this.currentDialogRef = this.dialog.open(this.rejectStaffDialog, {
                                width: '70%',
                                maxHeight: '90vh',
                                disableClose: false,
                                autoFocus: false
                            });
                        }
                    } else {
                        this.getcustDiscountDetails(this.customerId, "", "changeDiscount");
                    }

                    this.assignAppRejectDiscountForm.reset();
                    this.assignDiscounsubmitted = false;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    closeRejectCustomerInventoryModal() {
        if (this.currentDialogRef) {
            this.currentDialogRef.close();
        }
        this.rejectCustomerInventoryModal = false;
    }
    closeRejectApproveDiscountModal() {
        if (this.currentDialogRef) {
            this.currentDialogRef.close();
        }
    }

    closeAssignDiscountModal() {
        if (this.currentDialogRef) {
            this.currentDialogRef.close();
        }
        this.assignCustomerInventoryModal = false;
    }

    assignToStaff(flag) {
        let url: any;
        let name = "CUSTOMER_DISCOUNT";
        if (!this.selectStaff && !this.selectStaffReject) {
            url = `/teamHierarchy/assignEveryStaff?entityId=${this.approveId}&eventName=${name}&isApproveRequest=${flag}`;
        } else {
            if (flag) {
                url = `/teamHierarchy/assignFromStaffList?entityId=${this.approveId}&eventName=${name}&nextAssignStaff=${this.selectStaff}&isApproveRequest=${flag}`;
            } else {
                url = `/teamHierarchy/assignFromStaffList?entityId=${this.approveId}&eventName=${name}&nextAssignStaff=${this.selectStaffReject}&isApproveRequest=${flag}`;
            }
        }

        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (flag) {
                    if (response.responseCode == 417) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');

                    } else {
                        this.toastr.success(`${response.responseMessage}`, 'Approved Successfully !');

                    }
                } else {
                    this.toastr.success(`Rejected Successfully`, 'Success!')
                    // this.messageService.add({
                    //     severity: "success",
                    //     summary: "Success",
                    //     detail: "Rejected Successfully.",
                    //     icon: "far fa-times-circle"
                    // });
                }
                this.assignCustomerInventoryModal = false;
                this.rejectCustomerInventoryModal = false;

                this.getcustDiscountDetails(this.customerId, "", "changeDiscount");
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    pickModalOpen(data) {
        let name;
        let entityID;

        name = "CUSTOMER_DISCOUNT";
        entityID = data.id;

        let url = "/workflow/pickupworkflow?eventName=" + name + "&entityId=" + entityID;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.getcustDiscountDetails(this.customerId, "", "changeDiscount");

                if (response.responseCode == 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info !');

                } else {
                    this.toastr.success(`${response.responseMessage}`, 'Success!')
                    // this.messageService.add({
                    //     severity: "success",
                    //     summary: "Success",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle"
                    // });
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    discountApporeved(data) {
        this.approveId = data.id;
        this.assignDiscountData = data;
        this.discountFlageType = "approved";
        this.AppRjecHeader = "Approve ";
        this.assignAppRejectDiscountForm.reset();

        // Open Material Dialog
        this.currentDialogRef = this.dialog.open(this.approveRejectDialog, {
            width: '500px',
            disableClose: false,
            autoFocus: false
        });
    }
    discountRejected(data) {
        this.approveId = data.id;
        this.assignDiscountData = data;
        this.discountFlageType = "Rejected";
        this.AppRjecHeader = "Reject";
        this.assignAppRejectDiscountForm.reset();

        // Open Material Dialog
        this.currentDialogRef = this.dialog.open(this.approveRejectDialog, {
            width: '500px',
            disableClose: false,
            autoFocus: false
        });
    }
    async updateDiscount() {
        const data = [];

        for (let index = 0; index < this.custCustDiscountList.length; index++) {
            if (
                this.custCustDiscountList[index].discount !==
                this.custCustDiscountList[index].newDiscount ||
                this.custCustDiscountList[index].discountType !==
                this.custCustDiscountList[index].newDiscountType ||
                this.custCustDiscountList[index].discountExpiryDate !==
                this.custCustDiscountList[index].newDiscountExpiryDate
            ) {
                data.push({
                    id: this.custCustDiscountList[index].id,
                    custId: this.custCustDiscountList[index].custId,
                    connectionNo: this.custCustDiscountList[index].connectionNo,
                    serviceName: this.custCustDiscountList[index].serviceName,
                    serviceId: this.custCustDiscountList[index].serviceId,
                    invoiceType: this.custCustDiscountList[index].invoiceType,
                    discount: this.custCustDiscountList[index].discount,
                    newDiscount: this.custCustDiscountList[index].newDiscount,
                    remarks: this.custCustDiscountList[index].remarks,
                    status: this.custCustDiscountList[index].status,
                    discountType:
                        this.custCustDiscountList[index].discountType === null
                            ? "One-time"
                            : this.custCustDiscountList[index].discountType,
                    newDiscountType:
                        this.custCustDiscountList[index].newDiscountType === null
                            ? "One-time"
                            : this.custCustDiscountList[index].newDiscountType,
                    discountExpiryDate: moment(
                        this.custCustDiscountList[index].discountExpiryDate,
                        "DD/MM/YYYY"
                    )
                        .utc(true)
                        .toDate(),
                    newDiscountExpiryDate:
                        this.custCustDiscountList[index].newDiscountType === null ||
                            this.custCustDiscountList[index].newDiscountType === "One-time"
                            ? null
                            : moment(this.custCustDiscountList[index].newDiscountExpiryDate, "DD/MM/YYYY")
                                .utc(true)
                                .toDate()
                });
            }
        }

        this.oldDiscValue = 0;
        this.newDiscValue = 0;

        const url =
            "/subscriber/changeCustomerDiscountServiceLevel/" + this.customerLedgerDetailData.id;
        if (data.length != 0) {
            this.customerManagementService.postMethod(url, data).subscribe(
                (response: any) => {
                    this.getcustDiscountDetails(this.customerLedgerDetailData.id, "", "changeDiscount");
                    this.toastr.success(`Successfully Updated`, 'Success!');

                },
                (error: any) => {
                    this.toastr.error(`Something is wrong`, 'Failed!');

                }
            );
        }
    }

    discountReasignListShiftLocation(data) {
        this.approveId = data.id;
        let url = `/teamHierarchy/reassignWorkflowGetStaffList?entityId=${data.id}&eventName=CUSTOMER_DISCOUNT`;

        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.discountId = data.id;
                this.approveInventoryData = [];

                if (response.responseCode == 417) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');
                } else {
                    this.toastr.success(`${response.responseMessage}`, 'Success !');
                }

                if (response.dataList != null) {
                    this.approveInventoryData = response.dataList;
                    this.approved = true;

                    // Open assign staff dialog
                    this.currentDialogRef = this.dialog.open(this.assignStaffDialog, {
                        width: '70%',
                        maxHeight: '90vh',
                        disableClose: false,
                        autoFocus: false
                    });
                }

            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    reassignWorkflowShiftLocation() {
        let url: any;
        url = `/teamHierarchy/reassignWorkflow?entityId=${this.discountId}&eventName=CUSTOMER_DISCOUNT&assignToStaffId=${this.selectStaff}&remark=${this.assignAppRejectDiscountForm.controls.remark.value}`;

        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.rejectApproveDiscountModal = true;
                if (response.responseCode == 417) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                } else {
                    this.toastr.success(`${response.message}`, 'Assigned to the next staff successfully!');

                }
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    searchStaffByName() {
        if (this.searchStaffDeatil) {
            this.approveInventoryData = this.approveData.filter(
                staff =>
                    staff.fullName.toLowerCase().includes(this.searchStaffDeatil.toLowerCase()) ||
                    staff.username.toLowerCase().includes(this.searchStaffDeatil.toLowerCase())
            );
        } else {
            this.approveInventoryData = this.approveData;
        }
    }

    clearSearchForm() {
        this.searchStaffDeatil = "";
        this.approveInventoryData = this.approveData;
    }
}
