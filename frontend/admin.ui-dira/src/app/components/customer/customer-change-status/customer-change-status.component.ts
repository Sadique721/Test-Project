import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { NgxSpinnerService } from "ngx-spinner";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { ActivatedRoute, Router } from "@angular/router";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { MessageService } from "primeng/api";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { UntypedFormBuilder, UntypedFormGroup } from "@angular/forms";
import { CustomerInventoryMappingService } from "src/app/service/customer-inventory-mapping.service";
import { StaffService } from "src/app/service/staff.service";
import { BehaviorSubject, Observable, Observer, interval } from "rxjs";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { ToastrService } from 'ngx-toastr';

declare var $: any;

@Component({
    selector: "app-customer-change-status",
    templateUrl: "./customer-change-status.component.html",
    styleUrls: ["./customer-change-status.component.scss"],
    standalone: false
})
export class CustomerChangeStatusComponent implements OnInit {
    @ViewChild('approveRejectDialog') approveRejectDialog: TemplateRef<any>;
    @ViewChild('assignCustomerInventoryDialog') assignCustomerInventoryDialog: TemplateRef<any>;
    @ViewChild('rejectCustomerInventoryDialog') rejectCustomerInventoryDialog: TemplateRef<any>;
    @ViewChild('customerTerminationDialog') customerTerminationDialog: TemplateRef<any>;
    @ViewChild('workflowAuditDialog') workflowAuditDialog: TemplateRef<any>;
    private approveRejectDialogRef: MatDialogRef<any>;
    private assignCustomerDialogRef: MatDialogRef<any>;
    private rejectCustomerDialogRef: MatDialogRef<any>;
    private customerTerminationDialogRef: MatDialogRef<any>;
    private workflowAuditDialogRef: MatDialogRef<any>;
    viewworkflowAuditDataData: any;
    currentPageMasterSlab = 1;
    MasteritemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    MastertotalRecords: any;
    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    workflowAuditData = [];
    discountDetailsData = [];
    CutomerEventID: any;
    check_Hierachy = '';
    ifAuditStatusDetialShow = false;
    ifDiscountStatusDetialShow = false;
    planStatusDetail: any = [];
    nameTitile = ' Partner';
    DiscountPlanID: any;
    custType: any;
    loggedInStaffId = localStorage.getItem("userId");
    partnerId = Number(localStorage.getItem("partnerId"));
    customerId: number;

    custChangeStatusConfigitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    changeStatusShowItemPerPage = 1;
    AllcustApproveList: any = [];
    pageITEMW = RadiusConstants.ITEMS_PER_PAGE;
    AclClassConstants;
    AclConstants;
    ifApproveStatus = false;
    approveRejectRemark = "";
    apprRejectCustID = "";
    remark: string;
    assignTerminationForm: UntypedFormGroup;
    approved = false;
    staffDataList: any = [];
    approveId: any;
    statusListId: any;
    approveInventoryData = [];
    selectStaff: any;
    reject = false;
    rejectInventoryData = [];
    selectStaffReject: any;
    obs$ = interval(1000);
    currentPagecustChangeStatusConfig = 1;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    customerLedgerDetailData: any;
    cafRemainTimeSubscription: any;
    userName: "";
    staffUser: any;
    auditcustid: any = new BehaviorSubject({
        auditcustid: "",
        checkHierachy: "",
        planId: "",
    });
    ifModelIsShow: boolean = false;
    customerTermination: boolean = false;
    searchStaffDeatil = "";
    approveIneventory: any[];


    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        public PaymentamountService: PaymentamountService,
        private customerManagementService: CustomermanagementService,
        private route: ActivatedRoute,
        private router: Router,
        private toastr: ToastrService,

        private messageService: MessageService,
        public loginService: LoginService,
        private customerInventoryMappingService: CustomerInventoryMappingService,
        private staffService: StaffService,
        private dialog: MatDialog
    ) {
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        this.cafremaingTime();
    }

    async ngOnInit() {
        this.getCustomersDetail(this.customerId);
        this.getapproveStatusList("");
        this.getLoggedinUserData();
        this.assignTerminationForm = this.fb.group({
            remark: [""],
        });
    }

    customerDetailOpen() {
        this.router.navigate(["/home/customer/details/" + this.custType + "/x/" + this.customerId]);
    }

    getCustomersDetail(custId) {
        const url = "/customers/" + custId;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            this.customerLedgerDetailData = response.customers;
            this.cafremaingTime();
        });
    }

    // closeModal() {
    //     this.ifModelIsShow = false;
    // }

    getLoggedinUserData() {
        const staffId = localStorage.getItem("userId");
        this.staffService.getById(staffId).subscribe(
            (response: any) => {
                this.staffUser = response?.Staff;
                this.userName = this.staffUser?.username;
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.errorMessage,
                //     icon: "far fa-times-circle",
                // });
            }
        );
    }

    getapproveStatusList(size) {
        let page_list;
        if (size) {
            page_list = size;
            this.custChangeStatusConfigitemsPerPage = size;
        } else {
            if (this.changeStatusShowItemPerPage == 1) {
                this.custChangeStatusConfigitemsPerPage = this.pageITEM;
            } else {
                this.custChangeStatusConfigitemsPerPage = this.changeStatusShowItemPerPage;
            }
        }
        this.AllcustApproveList = [];
        const url = `/allCustApprove/${this.customerId}`;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 200 || response.status == 200) {
                    const list = response.customer;
                    for (let i = list.length; i > 0; i--) {
                        this.AllcustApproveList.push(list[i - 1]);
                    }
                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed')
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle",
                    // });
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle",
                // });
            }
        );
    }

    custTerminationWorkflowAuditopen(id, auditcustid) {
        this.ifModelIsShow = true;
        this.PaymentamountService.show(id);
        this.auditcustid.next({
            auditcustid: auditcustid,
            checkHierachy: "TERMINATION",
            planId: "",
        });
    }
    getAllEvent(check_Hierachy) {
        let hierachyValue = check_Hierachy;
        if (this.check_Hierachy == 'TerminationCAF') {
            hierachyValue = 'TERMINATION';
        }

        const url = '/commonList/hierarchy_event';
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                let eventData = response.dataList;
                eventData = response.dataList.filter(element => element.value == hierachyValue);
                if (eventData[0]) {
                    this.CutomerEventID = eventData[0].id;
                }

                switch (check_Hierachy) {
                    case 'TerminationCAF':
                    case 'TERMINATION':
                        this.ifAuditStatusDetialShow = false;
                        this.nameTitile = 'Customer Name';
                        this.getHierarchyStatus();
                        break;
                    // Add other cases as needed
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: 'error',
                //     summary: 'Error',
                //     detail: error.error.ERROR,
                //     icon: 'far fa-times-circle'
                // });
            }
        );
    }

    show_AuditData() {
        this.ifAuditStatusDetialShow = true;
        this.getworkflowAuditDetails('');
    }

    show_DiscountData() {
        this.ifDiscountStatusDetialShow = true;
        this.getDiscountDetails('');
    }
    ststusData() {
        this.ifAuditStatusDetialShow = false;
        this.ifDiscountStatusDetialShow = false;
    }
    getworkflowAuditDetails(size) {
        let page_list;
        let page = this.currentPageMasterSlab;
        if (size) {
            page_list = size;
            this.MasteritemsPerPage = size;
        } else {
            this.MasteritemsPerPage = this.pageITEM;
        }

        this.workflowAuditData = [];
        let data = {
            page: page,
            pageSize: this.MasteritemsPerPage
        };

        let url = `/workflowaudit/list?entityId=${this.customerLedgerDetailData.id}&eventName=${this.check_Hierachy}`;

        this.customerManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                this.workflowAuditData = response.dataList;
                this.MastertotalRecords = response.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: 'error',
                //     summary: 'Error',
                //     detail: error.error.ERROR,
                //     icon: 'far fa-times-circle'
                // });
            }
        );
    }

    getDiscountDetails(size) {
        this.discountDetailsData = [];
        let url = `/subscriber/changediscountaudit/${this.customerId}`;

        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.discountDetailsData = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: 'error',
                //     summary: 'Error',
                //     detail: error.error.ERROR
                // });
            }
        );
    }

    pageChangedMasterList(event: any) {
        this.currentPageMasterSlab = event.pageIndex + 1;
        this.MasteritemsPerPage = event.pageSize;
        this.getworkflowAuditDetails('');
    }

    getHierarchyStatus() {

        let url = `/teamHierarchy/getApprovalProgress?entityId=${this.customerLedgerDetailData.id}&eventName=${this.check_Hierachy}`;

        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.planStatusDetail = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR || error.error.msg}`, 'Failed!')
                // this.messageService.add({
                //     severity: 'error',
                //     summary: 'Error',
                //     detail: error.error.ERROR || error.error.msg
                // });
            }
        );
    }

    closeWorkflowAuditDialog() {
        if (this.workflowAuditDialogRef) {
            this.workflowAuditDialogRef.close();
        }
        this.ifAuditStatusDetialShow = false;
        this.ifDiscountStatusDetialShow = false;
    }

    // Update existing closeModal to also close workflow audit
    closeModal() {
        this.ifModelIsShow = false;
        this.closeWorkflowAuditDialog();
    }
    pickModalOpen(data) {
        let name;
        let entityID;
        name = "TERMINATION";
        entityID = data.customerID;
        let url = "/workflow/pickupworkflow?eventName=" + name + "&entityId=" + entityID;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.getapproveStatusList("");
                this.getCustomersDetail(this.customerId);

                if (response.responseCode == 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle",
                    // });
                } else {
                    this.toastr.success(`${response.responseMessage}`, 'Success!')
                    // this.messageService.add({
                    //     severity: "success",
                    //     summary: "Success",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle",
                    // });
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle",
                // });
            }
        );
    }

    approvestatusModalOpen(custId: any, id: any) {
        this.statusListId = id;
        this.ifApproveStatus = true;
        this.apprRejectCustID = custId;
        this.approveRejectRemark = '';

        this.approveRejectDialogRef = this.dialog.open(this.approveRejectDialog, {
            width: '600px',
            disableClose: false
        });

    }

    rejectstatusModalOpen(custId: any, id: any) {
        this.statusListId = id;
        this.ifApproveStatus = false;
        this.apprRejectCustID = custId;
        this.approveRejectRemark = '';

        this.approveRejectDialogRef = this.dialog.open(this.approveRejectDialog, {
            width: '600px',
            disableClose: false
        });
    }

    closeApproveRejectDialog() {
        if (this.approveRejectDialogRef) {
            this.approveRejectDialogRef.close();
        }
    }

    closeAssignCustomerInventoryDialog() {
        if (this.assignCustomerDialogRef) {
            this.assignCustomerDialogRef.close();
        }
    }

    closeRejectCustomerInventoryDialog() {
        if (this.rejectCustomerDialogRef) {
            this.rejectCustomerDialogRef.close();
        }
    }

    closeCustomerTerminationDialog() {
        if (this.customerTerminationDialogRef) {
            this.customerTerminationDialogRef.close();
        }
        this.customerTermination = false;
    }

    closeStaffReasignListForTermination() {
        this.closeCustomerTerminationDialog();
    }

    StaffReasignListForTermination(id) {
        this.remark = this.assignTerminationForm.value.remark;
        let url = `/teamHierarchy/reassignWorkflowGetStaffList?entityId=${id}&eventName=TERMINATION&remark=${this.remark}`;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 417) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed')
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle",
                    // });
                }
                if (response.dataList != null) {
                    this.staffDataList = response.dataList;
                    this.approved = true;
                    this.customerTermination = true;
                    this.customerTerminationDialogRef = this.dialog.open(this.customerTerminationDialog, {
                        width: '700px',
                        disableClose: false
                    });
                } else {
                    this.customerTermination = false;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle",
                // });
            }
        );
    }
    assignPlansubmitted = false
    //   closeStaffReasignListForTermination() {
    //     this.customerTermination = false;
    //   }
    statusApporevedRejected() {
        this.approveId = this.apprRejectCustID;

        let custstatus = "";
        if (this.ifApproveStatus == true) {
            this.approved = false;
            this.approveInventoryData = [];
            this.selectStaff = null;
            custstatus = "Approved";
        } else {
            this.reject = false;
            this.selectStaffReject = null;
            this.rejectInventoryData = [];
            custstatus = "Rejected";
        }
        const data = {
            id: this.statusListId,
            status: custstatus,
            remarks: this.approveRejectRemark,
        };

        const url =
            "/changeStatusCustomerApprove/" +
            this.apprRejectCustID +
            "?status=" +
            custstatus +
            "&remarks=" +
            this.approveRejectRemark;
        this.customerManagementService.updateMethod(url, data).subscribe(
            (response: any) => {
                $("#ApproveRejectModal").modal("hide");
                if (this.ifApproveStatus == true) {
                    if (response.result.dataList) {
                        this.approved = true;

                        this.approveInventoryData = response.result.dataList;
                        this.approveIneventory = this.approveInventoryData;
                        if (this.approveRejectDialogRef) {
                            this.approveRejectDialogRef.close();
                        }
                        this.assignCustomerDialogRef = this.dialog.open(this.assignCustomerInventoryDialog, {
                            width: '700px',
                            disableClose: false
                        });
                        // $("#assignCustomerInventoryModal").modal("show");
                    } else {
                        this.toastr.success(`${response.message}`, 'Success!')
                        // this.messageService.add({
                        //     severity: "success",
                        //     summary: "Successfully",
                        //     detail: response.message,
                        //     icon: "far fa-times-circle",
                        // });
                        this.getapproveStatusList("");
                        this.getCustomersDetail(this.customerId);
                    }
                } else {
                    if (response.result.dataList) {
                        this.reject = true;
                        this.rejectInventoryData = response.result.dataList;
                        this.rejectCustomerDialogRef = this.dialog.open(this.rejectCustomerInventoryDialog, {
                            width: '700px',
                            disableClose: false
                        });
                        if (this.approveRejectDialogRef) {
                            this.approveRejectDialogRef.close();
                        }
                        // $("#rejectCustomerInventoryModal").modal("show");
                    } else {
                        this.getapproveStatusList("");
                        this.getCustomersDetail(this.customerId);
                    }
                }
                if (this.dialog) {
                    this.dialog.closeAll();
                }
                if (this.approveRejectDialogRef) {
                    this.approveRejectDialogRef.close();
                }
            },
            (error: any) => {
                this.toastr.info(`${error.error.ERROR}`, 'Info!')
                // this.messageService.add({
                //     severity: "info",
                //     summary: "Info",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle",
                // });
            }
        );
    }

    assignToStaff(flag) {
        let url: any;
        let name: string;
        name = "TERMINATION";
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
                        this.toastr.error(`${response.responseMessage}`, 'Failed')
                        // this.messageService.add({
                        //     severity: "error",
                        //     summary: "Error",
                        //     detail: response.responseMessage,
                        //     icon: "far fa-times-circle",
                        // });
                    } else {
                        this.toastr.success(`Approved Successfully`, 'Success!')
                        // this.messageService.add({
                        //     severity: "success",
                        //     summary: "Success",
                        //     detail: "Approved Successfully.",
                        //     icon: "far fa-times-circle",
                        // });
                    }
                } else {
                    this.toastr.success(`Rejected Successfully`, 'Success!')
                    // this.messageService.add({
                    //     severity: "success",
                    //     summary: "Success",
                    //     detail: "Rejected Successfully.",
                    //     icon: "far fa-times-circle",
                    // });
                }
                $("#assignCustomerInventoryModal").modal("hide");
                $("#rejectCustomerInventoryModal").modal("hide");
                this.getapproveStatusList("");
                this.getCustomersDetail(this.customerId);
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle",
                // });
            }
        );
    }

    reassignTerminationWorkflow() {
        this.remark = this.assignTerminationForm.value.remark;
        let url: any;
        url = `/teamHierarchy/reassignWorkflow?entityId=${this.customerLedgerDetailData.id}&eventName=TERMINATION&assignToStaffId=${this.selectStaff}&remark=${this.remark}`;

        if (this.selectStaff == undefined) {
            this.toastr.info(`Please select staff for reassign termination workflow`, 'Info!')
            // this.messageService.add({
            //     severity: "info",
            //     summary: "info",
            //     detail: "Please select staff for reassign termination workflow",
            //     icon: "far fa-times-circle",
            // });
        } else {
            this.customerManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.customerTermination = false;
                    this.getapproveStatusList("");
                    this.getCustomersDetail(this.customerId);
                    if (response.responseCode == 417) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed')
                        // this.messageService.add({
                        //     severity: "error",
                        //     summary: "Error",
                        //     detail: response.responseMessage,
                        //     icon: "far fa-times-circle",
                        // });
                    } else {
                        // this.getCustomer;
                        this.toastr.success(`Assigned to the next staff successfully`, 'Success!')
                        // this.messageService.add({
                        //     severity: "success",
                        //     summary: "Successfully",
                        //     detail: "Assigned to the next staff successfully.",
                        //     icon: "far fa-times-circle",
                        // });
                    }
                },
                error => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: error.error.ERROR,
                    //     icon: "far fa-times-circle",
                    // });
                }
            );
        }
    }

    pageChangeStatusConfig(event: any) {
        // Material Paginator returns event with pageIndex (0-based) and pageSize
        this.currentPagecustChangeStatusConfig = event.pageIndex + 1; // Convert to 1-based
        this.custChangeStatusConfigitemsPerPage = event.pageSize;
        this.getapproveStatusList(''); // Your existing method to fetch data
    }

    TotalChangeStatusItemPerPage(event) {
        this.changeStatusShowItemPerPage = Number(event.value);
        if (this.currentPagecustChangeStatusConfig > 1) {
            this.currentPagecustChangeStatusConfig = 1;
        }
        this.getapproveStatusList(this.changeStatusShowItemPerPage);
    }

    cafremaingTime() {
        this.cafRemainTimeSubscription = this.obs$.subscribe(e => {
            if (this.customerLedgerDetailData) {
                if (
                    this.customerLedgerDetailData.status != "Rejected" &&
                    this.customerLedgerDetailData.status != "Terminate" &&
                    this.customerLedgerDetailData.status != "Approved"
                ) {
                    if (
                        this.customerLedgerDetailData.currentAssigneeId == null ||
                        this.customerLedgerDetailData.currentAssigneeId !== null
                    ) {
                        const newYearsDate: any = new Date(
                            this.customerLedgerDetailData.nextfollowupdate +
                            " " +
                            this.customerLedgerDetailData.nextfollowuptime
                        );
                        const currentDate: any = new Date();
                        if (newYearsDate > currentDate) {
                            const totalSeconds = (newYearsDate - currentDate) / 1000;
                            const minutes = Math.floor(totalSeconds / 60) % 60;
                            const hours = Math.floor(totalSeconds / 3600) % 24;
                            const days = Math.floor(totalSeconds / 3600 / 24);
                            const seconds = Math.floor(totalSeconds) % 60;
                            const remainTime =
                                ("0" + days).slice(-2) +
                                ":" +
                                ("0" + hours).slice(-2) +
                                ":" +
                                ("0" + minutes).slice(-2) +
                                ":" +
                                ("0" + seconds).slice(-2);

                            this.customerLedgerDetailData.remainTime = remainTime;
                        } else {
                            this.customerLedgerDetailData.remainTime = "00:00:00:00";
                        }
                    }
                }
            }
        });
    }

    searchStaffByName() {
        if (this.searchStaffDeatil) {
            this.approveInventoryData = this.approveIneventory.filter(
                staff =>
                    staff.fullName.toLowerCase().includes(this.searchStaffDeatil.toLowerCase()) ||
                    staff.username.toLowerCase().includes(this.searchStaffDeatil.toLowerCase())
            );
        } else {
            this.approveInventoryData = this.approveIneventory;
        }
        // this.approvestaffListdatatotalRecords = this.approveInventoryData?.length;
    }

    clearSearchForm() {
        this.searchStaffDeatil = "";
        this.approveInventoryData = this.approveIneventory;
    }
}
