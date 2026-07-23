import { Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild } from "@angular/core";
import { NgxSpinnerService } from "ngx-spinner";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { ActivatedRoute, Router } from "@angular/router";
import { PaymentamountService } from "src/app/service/paymentamount.service";
import { UntypedFormBuilder, FormBuilder, FormGroup, UntypedFormGroup, Validators } from "@angular/forms";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { MessageService } from "primeng/api";
import { DatePipe } from "@angular/common";
import * as FileSaver from "file-saver";
import { TicketManagementService } from "../../../service/ticket-management.service";
import { CustomerService } from "src/app/service/customer.service";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { LoginService } from "src/app/service/login.service";
import { POST_CUST_CONSTANTS, PRE_CUST_CONSTANTS } from "src/app/constants/aclConstants";
import { TicketManagementComponent } from "../../ticket-management/ticket-management.component";
import { ToastrService } from 'ngx-toastr';
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { AnyNaptrRecord } from "dns";
import { DomSanitizer } from "@angular/platform-browser";
import { TICKETING_SYSTEMS } from 'src/app/constants/aclConstants';
import { HttpClient } from '@angular/common/http';
import { saveAs as importedSaveAs } from "file-saver";
import { BehaviorSubject, interval, Observable, Observer, Subscription, timer } from "rxjs";
import moment from "moment";
import * as XLSX from "xlsx";

declare var $: any;
@Component({
    selector: "app-cust-tickets",
    templateUrl: "./cust-tickets.component.html",
    styleUrls: ["./cust-tickets.component.scss"],
    standalone: false
})
export class CustTicketsComponent implements OnInit {
    @ViewChild('detailsDialog') detailsDialog!: TemplateRef<any>;
    tatDetailsShowModel: boolean = false;
    @ViewChild('assignTicketDialog') assignTicketDialog!: TemplateRef<any>;
    @ViewChild('ticketApproveRejectDialog') ticketApproveRejectDialog!: TemplateRef<any>;
    @ViewChild('assignStaffDialog') assignStaffDialog!: TemplateRef<any>;
    @ViewChild('rejectStaffDialog') rejectStaffDialog!: TemplateRef<any>;
    @ViewChild('changeStatusDialog') changeStatusDialog!: TemplateRef<any>;
    @ViewChild('changePriorityDialog') changePriorityDialog!: TemplateRef<any>;
    @ViewChild('pickTicketDialog') pickTicketDialog!: TemplateRef<any>;
    @ViewChild('slaCounterDialog') slaCounterDialog!: TemplateRef<any>;
    @ViewChild('viewDocumentDialog') viewDocumentDialog!: TemplateRef<any>;
    @ViewChild('uploadDocumentRootDialog') uploadDocumentRootDialog!: TemplateRef<any>;
    @ViewChild('uploadResolveDocumentDialog') uploadResolveDocumentDialog!: TemplateRef<any>;
    @ViewChild('documentResolvePreviewDialog') documentResolvePreviewDialog!: TemplateRef<any>;

    @ViewChild('confirmDialog') confirmDialog!: TemplateRef<any>;

    @ViewChild('feedbackFormDialog') feedbackFormDialog!: TemplateRef<any>;

    @ViewChild('selectTicketDialog') selectTicketDialog!: TemplateRef<any>;

    @ViewChild('uploadDocumentDialog') uploadDocumentDialog!: TemplateRef<any>;

    @ViewChild('changeProblemDomainDialog') changeProblemDomainDialog!: TemplateRef<any>;

    @ViewChild('ticketETRDialog') ticketETRDialog!: TemplateRef<any>;
    @ViewChild('followupTicketDialog') followupTicketDialog!: TemplateRef<any>;
    @ViewChild('conversationDialog') conversationDialog!: TemplateRef<any>;
    @ViewChild('scheduleFollowupDialog') scheduleFollowupDialog!: TemplateRef<any>;
    @ViewChild('reScheduleFollowupDialog') reScheduleFollowupDialog!: TemplateRef<any>;

    @ViewChild('tatMessageDialog') tatMessageDialog!: TemplateRef<any>;
    @ViewChild('documentPreviewDialog') documentPreviewDialog!: TemplateRef<any>;
    @ViewChild('teamDetailsDialog') teamDetailsDialog!: TemplateRef<any>;
    @ViewChild('parentTicketDialog') parentTicketDialog!: TemplateRef<any>;
    @ViewChild('childTicketDialog') childTicketDialog!: TemplateRef<any>;
    @Input() cafCustomerID: any
    @Input() cafCustomerType: any
    @Input() isFromCaf: boolean = false;
    @Output() backToList = new EventEmitter<void>();
    closeFollowupForm: UntypedFormGroup;
    remarkFollowupForm: UntypedFormGroup;
    assignStaffDisplay = ['select', 'fullName', 'username', 'caseAssignCount'];
    custType: any;
    // loggedInStaffId = localStorage.getItem("userId");
    // partnerId = Number(localStorage.getItem("partnerId"));
    customerId: number;
    custData: any = {};
    ticketComponentModel: TicketManagementComponent;
    custTicketList: any = [];

    isDisable: boolean = false;
    createTicketAccess: boolean = false;

    pageLimitOptions = RadiusConstants.pageLimitOptions;
    currentPage = 1;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    totalRecords: String;
    ticketShowItemPerPage = 1;

    mvnoid: any;
    staffid: any;

    staffData: any = {
        fullName: "",
        email: "",
        phone: "",
        username: "",
        roleName: [],
        servicearea: {
            name: ""
        }
    };
    displaySelectTicket: string[] = [
        'select',
        'ticketId',
        'ticketName',
        'customerName',
        'createdDate',
        'status'
    ];

    selectStaffDisplay: string[] = [
        'select',
        'fullName',
        'username'
    ];
    displayhistory = ['createdByName', 'createdate', 'updatedate', 'lastModifiedByName', 'actions']

    ticketprogressDisplay = ['entitytype', 'operation', 'oldvalue', 'newvalue']

    remarkDisplay = ['remark', 'createdOn']
    selectticketConfigDisplay = ['select', 'name', 'number', 'type', 'customer', 'assignee', 'status', 'followup']
    displaytatDetails = ['orderNo', 'level', 'mtime1', 'mtime2', 'mtime3', 'munit', 'action']
    displayFile = ['fileName', 'latitude', 'longitude', 'actions']
    chilTicketDisplay = ['name', 'number', 'type', 'customer', 'assignee', 'status', 'followup']


    serviceAreaList: any;
    addTicketModal: boolean = false;

    assignAccess = false;
    followUpAccess = false;
    changeStatusAccess = false;
    changePriorityAccess = false;
    linkTicketAccess = false;
    uploadDocAccess = false;
    changePBDomainAccess = false;
    etrAccess = false;
    remarksAccess = false;
    conversationAccess = false;
    attachementAccess = false;
    attachementDownloadAccess = false;
    etrExcelDownloadAccess = false;

    currentLoginUserId: any = null;
    staffUserId: any = null;
    assignStaffParentId: any = null;

    uploadDataTicketId: number;
    tabs = [
        "FAT Optical Power Picture",
        "FAT Inside Picture",
        "FAT Outside Picture",
        "ONU Optical Power Picture",
        "Optical Power Range",
        "Installation Picture",
        "Speedtest Picture",
        "Smart Gadget"
    ];
    constructor(
        private toastr: ToastrService,
        private dialog: MatDialog,
        private spinner: NgxSpinnerService,
        public PaymentamountService: PaymentamountService,
        private customerManagementService: CustomermanagementService,
        private ticketManagementService: TicketManagementService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        private route: ActivatedRoute,
        public customerService: CustomerService,
        private router: Router,
        private fb: UntypedFormBuilder,
        private datePipe: DatePipe,
        private messageService: MessageService,
        loginService: LoginService,
        private sanitizer: DomSanitizer,
        private http: HttpClient,
        public commondropdownService: CommondropdownService,
    ) {
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;
        this.createTicketAccess = loginService.hasPermission(
            this.custType == "Prepaid"
                ? PRE_CUST_CONSTANTS.PRE_CUST_TICKETS_CREATE_TICKET
                : POST_CUST_CONSTANTS.POST_CUST_TICKETS_CREATE_TICKETS
        );
        this.mvnoid = Number(localStorage.getItem("mvnoId"));
        this.staffid = Number(localStorage.getItem("userId"));
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;
        this.createTicketAccess = loginService.hasPermission(TICKETING_SYSTEMS.TICKET_CREATE);
        this.assignAccess = loginService.hasPermission(TICKETING_SYSTEMS.TICKET_ASSIGN);
        this.followUpAccess = loginService.hasPermission(TICKETING_SYSTEMS.TICKET_FOLLOW_UP);
        this.changeStatusAccess = loginService.hasPermission(TICKETING_SYSTEMS.TICKET_CHANGE_STATUS);
        this.changePriorityAccess = loginService.hasPermission(TICKETING_SYSTEMS.TICKET_CHANGE_PRIORITY);
        this.linkTicketAccess = loginService.hasPermission(TICKETING_SYSTEMS.TICKET_LINE_TICKET);
        this.uploadDocAccess = loginService.hasPermission(TICKETING_SYSTEMS.TICKET_UPLOAD_DOC);
        this.changePBDomainAccess = loginService.hasPermission(TICKETING_SYSTEMS.TICKET_CHANGE_PB_DOMAIN);
        this.etrAccess = loginService.hasPermission(TICKETING_SYSTEMS.TICKET_ETR);
        this.remarksAccess = loginService.hasPermission(TICKETING_SYSTEMS.TICKET_REMARKS);
        this.conversationAccess = loginService.hasPermission(TICKETING_SYSTEMS.TICKET_CONVERSATION);
        this.attachementAccess = loginService.hasPermission(TICKETING_SYSTEMS.TICKET_ATTACHMENT);
        this.attachementDownloadAccess = loginService.hasPermission(TICKETING_SYSTEMS.TICKET_ATTACHMENT_DOWNLOAD);
        this.etrExcelDownloadAccess = loginService.hasPermission(TICKETING_SYSTEMS.TICKET_ETR_EXCEL_DOWNLOAD);
    }
    assignStaffTicketForm !: UntypedFormGroup;
    reassignStaffTicketForm !: UntypedFormGroup;
    ratingTicketForm !: UntypedFormGroup;
    followupForm !: UntypedFormGroup;
    followupScheduleForm: UntypedFormGroup;
    reFollowupScheduleForm: UntypedFormGroup;
    chnageStatusForm: UntypedFormGroup;
    uploadResolveDocForm: UntypedFormGroup[] = [];
    feedbackForm: UntypedFormGroup;
    uploadDocForm: UntypedFormGroup;
    ticketETRForm: UntypedFormGroup;
    uploadRootForm: UntypedFormGroup = this.createUploadRootForm();
    inventoryDocType: any;

    async ngOnInit() {
        if (this.cafCustomerID) {
            this.customerId = this.cafCustomerID
            this.custType = this.cafCustomerType;
        }
        let custData1 = history.state.data;

        this.getCustomersDetail(this.customerId);
        const storedUserId = localStorage.getItem('userId') || localStorage.getItem('userID') || localStorage.getItem('loginUserId');
        this.currentLoginUserId = storedUserId ? Number(storedUserId) : null;
        this.staffUserId = this.currentLoginUserId;
        const parentId = localStorage.getItem('assignStaffParentId');
        this.assignStaffParentId = parentId ? Number(parentId) : null;
        this.assignStaffTicketForm = this.fb.group({
            remark: ["", Validators.required],
            staffId: ["", Validators.required]
        });
        this.reassignStaffTicketForm = this.fb.group({
            remark: ["", Validators.required],
            staffId: ["", Validators.required]
        });
        this.ratingTicketForm = this.fb.group({
            customerFeedback: ["", Validators.required],
            rating: [, Validators.required]
        });
        this.followupForm = this.fb.group({
            remarkType: ["", Validators.required],
            remark: ["", Validators.required]
        });
        this.followupScheduleForm = this.fb.group({
            id: [""],
            followUpName: ["", Validators.required],
            followUpDatetime: ["", Validators.required],
            followUpTime: ["", Validators.required],
            remarks: ["", Validators.required],
            isMissed: [false],
            caseId: []
        });
        this.reFollowupScheduleForm = this.fb.group({
            id: [""],
            followUpName: ["", Validators.required],
            followUpDatetime: ["", Validators.required],
            remarks: [""],
            isMissed: [false],
            caseId: [],
            remarksTemp: ["", Validators.required]
        });
        this.uploadDocForm = this.fb.group({
            file: ["", Validators.required]
        });
        this.chnageStatusForm = this.fb.group({
            ticketId: [""],
            oldStatus: [""],
            newStatus: ["", Validators.required],
            remark: [""],
            customerId: [""],
            finalResolutionId: [""],
            rootCauseReasonId: [""],
            helperName: [""],
            nextFollowupDate: [""],
            nextFollowupTime: [""],
            serviceAreaValue: [""],
            call_status: [""],
            is_closed: [""],
            reason: [""],
            resolutionFiles: [[]],
            latitude: [""],
            longitude: [""],
            uploadremark: [""]
        });
        this.feedbackForm = this.fb.group({
            support_type: [""],
            staff_behavior: [""],
            payment_mode: [""],
            infoOfPaymentMode: [""],
            current_bandwidth_feedback: [""],
            current_price_feedback: [""],
            referal_information: [""],
            technicial_support_feedback: [""],
            problem_type: [""],
            service_experience: [""],
            behaviour_professionalism: [""],
            reason: [""],
            overall_rating: [""],
            general_remarks: [""]
        });
        this.ticketETRForm = this.fb.group({
            isTemplateDynamic: ["", Validators.required],
            notificationDate: ["", Validators.required],
            notificationTime: ["", Validators.required],
            remark: [""],
            sms: [""],
            email: [""]
        });
        this.closeFollowupForm = this.fb.group({
            followUpId: [""],
            remarks: ["", Validators.required]
        });
        this.remarkFollowupForm = this.fb.group({
            cafFollowUpId: [""],
            remark: ["", Validators.required]
        });
        this.commondropdownService
            .getMethodFromCommon(`/commonList/generic/inventoryDocType`)
            .subscribe((response: any) => {
                this.inventoryDocType = response.dataList;
                this.inventoryDocType.sort((a, b) => this.tabs.indexOf(a.text) - this.tabs.indexOf(b.text));

                // Create mapping: tab name -> mandatory flag
                this.tabs = this.inventoryDocType.map(item => item.text);
                this.tabsMandatory = this.inventoryDocType.map(item => item.hasMandatory);
            });
        this.tabs.forEach(() => {
            this.uploadResolveDocForm.push(this.createForm());
        });

        this.getTicketPriority();
    }
    createForm(): UntypedFormGroup {
        return this.fb.group({
            sectionName: [""],
            latitude: [""],
            longitude: [""],
            opticalRange: [null],
            file: ["", Validators.required]
        });
    }

    staffsubmmitted = false;
    assignticketId: any;
    assignTicketStatus: any = "";
    assignableStaffList: any;
    assignTicketModal = false;
    changeStausSubmitted = false;
    ifApproveTicket = false;
    approveRejectRemark = "";
    ticketApprRejectData: any = [];
    ticketApproveRejectModal: boolean = false;
    createUploadRootForm(): UntypedFormGroup {
        return this.fb.group({
            resolutionFiles: [[]],
            latitude: [""],
            longitude: [""],
            uploadremark: [""],
            addRemarkChecked: [false]
        });
    }

    assignTicket(ticketId, serviceAreaId, ticketStatus) {
        this.assignStaffTicketForm.reset();
        this.staffsubmmitted = false;
        this.assignticketId = ticketId;
        this.assignTicketStatus = ticketStatus;
        if (ticketStatus != "Closed") {
            this.getStaff(ticketId);
        } else {
            this.toastr.info(`Can not assign close tickets.`, 'Info!')
            return;
        }
    }
    getStaff(ticketId) {
        const url = `/case/reassignTicket?caseId=${ticketId}`;
        this.ticketManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.assignableStaffList = response.dataList;
                if (response.dataList == null) {
                    this.toastr.error(`No staff available to assign..`, 'Failed!')

                } else {
                    this.assignTicketModal = true;
                    this.assignTicketDialogRef = this.dialog.open(this.assignTicketDialog, {
                        width: '80%',
                        disableClose: true
                    });

                    this.assignTicketDialogRef?.afterClosed()?.subscribe(res => {
                        this.refreshTicketDetails(this.viewTicketId);
                    })
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!')

            }
        );
    }
    hasFilesForTab(tab: string): boolean {
        return this.ticketFileDocData?.some(section => section?.sectionName === tab) ?? false;
    }
    assignTicketSubmit(dialogRef): void {
        this.staffsubmmitted = true;
        if (this.assignStaffTicketForm.valid) {
            const updateDetails: any = {};
            updateDetails.ticketId = this.assignticketId;
            updateDetails.status = this.assignTicketStatus;
            updateDetails.remark = this.assignStaffTicketForm.controls.remark.value;
            updateDetails.remarkType = "Change Assignee";
            updateDetails.assignee = this.assignStaffTicketForm.controls.staffId.value;
            const formData = new FormData();
            formData.append("caseUpdate", JSON.stringify(updateDetails));
            const url = "/case/updateDetails";
            this.ticketManagementService.assignMethod(url, formData).subscribe(
                (response: any) => {
                    if (response.responseCode === 406) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed')

                    } else {
                        dialogRef.close()
                        this.getcustTicket(this.customerId, "");
                        this.dialogRef.close();
                        if (this.dialogRef && this.dialogRef.componentInstance) {
                            this.refreshTicketDetails(this.viewTicketId);
                        } else {
                            this.openTicketDetailView(this.viewTicketId);
                        }
                        this.assignTicketModal = false;
                        this.toastr.success(`${response.message}`, 'Success!')

                        this.changeStausSubmitted = false;
                    }
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')

                }
            );
        }
    }
    approveTicket(data) {
        this.approveRejectRemark = "";
        this.ifApproveTicket = true;
        this.ticketApprRejectData = data;
        this.ticketApproveRejectModal = true;
        this.dialog.open(this.ticketApproveRejectDialog, {
            width: '80%',
            disableClose: true
        });
    }

    rejectTicket(data) {
        this.approveRejectRemark = "";
        this.ifApproveTicket = false;
        this.ticketApprRejectData = data;
        this.ticketApproveRejectModal = true;
        this.dialog.open(this.ticketApproveRejectDialog, {
            width: '80%',
            disableClose: true
        });
    }
    approved = false;
    approveCAF = [];
    selectStaff: any;
    approveId: any;
    workflowAuditData1: any = [];
    currentPageMasterSlab1 = 1;
    SLAremainTime: any;
    approveCAFData: any[];
    assignCustomerCAFModal = false;
    reject = false;
    rejectCAF = [];
    rejectCAFData: any[];
    rejectCustomerCAFModal = false;
    selectStaffReject: any;

    statusApporeved(dialogRef) {
        this.approveId = this.ticketApprRejectData.caseId;
        this.approved = false;
        this.approveCAF = [];
        this.selectStaff = null;
        const url =
            "/case/approveTicket?caseId=" +
            this.approveId +
            "&isApproveRequest=true&remarks=" +
            this.approveRejectRemark;
        this.ticketManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == "200") {
                    // this.recepit = response.data;
                    dialogRef.close()
                    this.ticketApproveRejectModal = false;
                    if (response.dataList) {
                        this.approved = true;
                        this.approveCAF = response.dataList;
                        this.approveCAFData = this.approveCAF;
                        this.dialog.open(this.assignStaffDialog, {
                            width: '80%',
                            disableClose: true
                        });
                        this.assignCustomerCAFModal = true;
                    } else {
                        this.getcustTicket(this.customerId, "");
                        this.dialogRef.close();
                        if (this.dialogRef && this.dialogRef.componentInstance) {
                            this.refreshTicketDetails(this.viewTicketId);
                        } else {
                            this.openTicketDetailView(this.viewTicketId);
                        }
                    }
                    this.ifApproveTicket = false;
                    this.ticketApprRejectData = [];
                } else {
                    this.toastr.info(`${response.responseMessage}`, 'Info!')
                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
            }
        );
    }
    searchStaffDeatil
    newStaffFirst
    approveStaffListdataitemsPerPageForStaff
    searchStaffByName(searchText: string) {
        const trimmedSearchText = searchText.trim().replace(/\s+/g, " ");
        this.searchStaffDeatil = searchText;
        this.newStaffFirst = 0;
        this.approveStaffListdataitemsPerPageForStaff = 5;

        const normalizedSearchText = trimmedSearchText.toLowerCase();

        if (trimmedSearchText) {
            this.approveCAF = this.approveCAFData.filter(
                staff =>
                    staff.fullName.toLowerCase().includes(normalizedSearchText) ||
                    staff.username.toLowerCase().includes(normalizedSearchText)
            );
        } else {
            this.approveCAF = this.approveCAFData;
        }
    }

    clearSearchForm() {
        this.searchStaffDeatil = "";
        this.approveCAF = this.approveCAFData;
        this.newStaffFirst = 0;
        this.approveStaffListdataitemsPerPageForStaff = 5;
    }
    statusRejected(dialogRef) {
        this.approveId = this.ticketApprRejectData.caseId;
        this.reject = false;
        this.selectStaffReject = null;
        this.rejectCAF = [];
        const url =
            "/case/approveTicket?caseId=" +
            this.approveId +
            "&isApproveRequest=false&remarks=" +
            this.approveRejectRemark;
        this.ticketManagementService.getMethod(url).subscribe(
            (response: any) => {
                // this.recepit = response.data;
                dialogRef.close()
                this.ticketApproveRejectModal = false;
                if (response.dataList) {
                    this.reject = true;
                    this.rejectCAF = response.dataList;
                    this.rejectCAFData = this.rejectCAF;
                    this.rejectCustomerCAFModal = true;
                    this.dialog.open(this.rejectStaffDialog, {
                        width: '80%',
                        disableClose: true
                    });
                } else {
                    this.getcustTicket(this.customerId, "");
                    this.dialogRef.close();
                    if (this.dialogRef && this.dialogRef.componentInstance) {
                        this.refreshTicketDetails(this.viewTicketId);
                    } else {
                        this.openTicketDetailView(this.viewTicketId);
                    }
                }

                this.ifApproveTicket = false;
                this.ticketApprRejectData = [];
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
            }
        );
    }
    assignToAllStaffTicket(dialogRef) {
        let remark = "assign to everyone from list.";
        const url = `/case/assignEveryStaffFromList?caseId=${this.approveId}&remark=${remark}&isApproveRequest=${this.approved}`;
        this.ticketManagementService.getMethod(url).subscribe(
            (response: any) => {
                dialogRef.close()
                this.assignCustomerCAFModal = false;
                this.rejectCustomerCAFModal = false;
                this.getcustTicket(this.customerId, "");
                this.dialogRef.close();
                if (this.dialogRef && this.dialogRef.componentInstance) {
                    this.refreshTicketDetails(this.viewTicketId);
                } else {
                    this.openTicketDetailView(this.viewTicketId);
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')

            }
        );
    }
    assignToStaffTicket(flag, dialogRef) {
        let url: any;
        if (flag == true) {
            url = `/teamHierarchy/assignFromStaffList?entityId=${this.approveId}&eventName=${"CASE"}&nextAssignStaff=${this.selectStaff}&isApproveRequest=${flag}`;
        } else {
            url = `/teamHierarchy/assignFromStaffList?entityId=${this.approveId}&eventName=${"CASE"}&nextAssignStaff=${this.selectStaffReject}&isApproveRequest=${flag}`;
        }

        this.ticketManagementService.getMethod(url).subscribe(
            response => {
                dialogRef.close()
                this.assignCustomerCAFModal = false;
                this.rejectCustomerCAFModal = false;
                this.getcustTicket(this.customerId, "");
                this.dialogRef.close();
                if (this.dialogRef && this.dialogRef.componentInstance) {
                    this.refreshTicketDetails(this.viewTicketId);
                } else {
                    this.openTicketDetailView(this.viewTicketId);
                }
            },
            error => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')

            }
        );
    }
    folloupTicketId: any = "";
    folloupCustId: any = "";
    folloupTicketassignStaffId: any = "";
    followupPopupOpen = false;
    generateNameOfFollowUp: any;

    followupTicketModalOpen(ticketId, custId, staffId) {
        this.followupForm.reset();
        this.folloupTicketId = ticketId;
        this.folloupCustId = custId;
        this.folloupTicketassignStaffId = staffId;
        this.followupPopupOpen = true;
        this.generatedNameOfTheFollowUp(this.folloupTicketId);
        this.scheduleFollowupPopupOpen();
    }
    generatedNameOfTheFollowUp(ticketId) {
        const url = "/ticketFollowUp/generateNameOfTheTicketFollowUp/" + ticketId;

        this.ticketManagementService.getMethod(url).subscribe(
            async (response: any) => {
                this.generateNameOfFollowUp = await response.data;
                this.generateNameOfFollowUp
                    ? this.followupScheduleForm.controls["followUpName"].setValue(this.generateNameOfFollowUp)
                    : "";
            },
            async (error: any) => {
                console.log("Generated followup name error => ", await error);
                this.toastr.error(`Something went wrong with 'followup name.' Generation`, 'Failed!')
            }
        );
    }
    scheduleFollowup = false
    scheduleFollowupPopupOpen() {
        this.followupPopupOpen = true;
        this.dialog.open(this.scheduleFollowupDialog, {
            width: '80%',
            disableClose: true
        });
        this.generatedNameOfTheFollowUp(this.folloupTicketId);
        this.scheduleFollowup = true;

    }
    changeStatusSingleMultiple = "";
    changeStatusModal = false;
    ticketIdData: any;
    isCall = false;
    isticket = false
    changeStatusModalOpen(data, tickT): void {
        if (tickT == "pTicket") {
            this.changeStatusSingleMultiple = tickT;
            if (data.caseStatus === "Closed") {
                this.toastr.info(`Can not change status as ticket is closed`, 'Info!')

                return;
            } else {
                this.chnageStatusForm = this.fb.group({
                    ticketId: [data.caseId],
                    oldStatus: [data.caseStatus],
                    newStatus: ["", Validators.required],
                    remark: [""],
                    customerId: [data.customersId],
                    finalResolutionId: [""],
                    rootCauseReasonId: [""],
                    helperName: [""],
                    nextFollowupDate: [""],
                    nextFollowupTime: [""],
                    serviceAreaValue: [""],
                    call_status: [""],
                    is_closed: [""],
                    deacivate_reason: [""]
                });
                this.getCaseStatusForChange(data.currentAssigneeId);
                this.changeStatusModal = true;
                this.dialog.open(this.changeStatusDialog, {
                    width: '80%',
                    disableClose: true
                });
                this.ticketIdData = data.caseId;
            }
        } else if (tickT == "mTicket") {
            this.isCall = false;
            this.changeStatusSingleMultiple = tickT;
            this.confirmChangeStatus();
        }
    }
    resolutionReasonData: any;
    isCallDisconnected = false
    getResolutionReasonsChangeStatus(value: string): void {
        // this.getAllStaff();
        this.changeStatusSelection(value);
        if (value === "Resolved") {
            this.uploadResolveDocument();
            this.chnageStatusForm.controls.finalResolutionId.enable();
            const url = `/resolutionReasons/searchBySubCategory/${this.viewTicketData.reasonSubCategoryId}`;
            this.ticketManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.resolutionReasonData = response.dataList;

                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')

                }
            );
        } else {
            this.chnageStatusForm.controls.finalResolutionId.setValue("");
            this.chnageStatusForm.controls.rootCauseReasonId.setValue("");
        }

        if (value === "Follow Up") {
            this.chnageStatusForm.controls.nextFollowupDate.setValidators(Validators.required);
            this.chnageStatusForm.controls.nextFollowupDate.updateValueAndValidity();
            this.chnageStatusForm.controls.nextFollowupTime.setValidators(Validators.required);
            this.chnageStatusForm.controls.nextFollowupTime.updateValueAndValidity();
        } else {
            this.chnageStatusForm.controls.nextFollowupDate.clearValidators();
            this.chnageStatusForm.controls.nextFollowupDate.updateValueAndValidity();
            this.chnageStatusForm.controls.nextFollowupTime.clearValidators();
            this.chnageStatusForm.controls.nextFollowupTime.updateValueAndValidity();
        }
        if (value === "Closed") {
            this.isCall = true;
            this.isticket = true;
            const url =
                "/resolutionReasons/searchBySubCategory/" + this.viewTicketData.reasonSubCategoryId;
            this.ticketManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.resolutionReasonData = response.dataList;
                    // console.log("this.resolutionReasonData", this.resolutionReasonData);
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')

                }
            );
        } else {
            this.isCall = false;
            this.isticket = false;
            this.isCallDisconnected = false;
            this.chnageStatusForm.get("is_closed").clearValidators();
            this.chnageStatusForm.get("is_closed").updateValueAndValidity();
        }
    }
    rootCauseReasonData: any;
    uploadDocumentRoot = false
    getResolutionRootCause(value: string): void {
        this.rootCauseReasonData = [];
        this.chnageStatusForm.controls.rootCauseReasonId.enable();
        this.resolutionReasonData.forEach(e => {
            if (e.id === value) {
                e.rootCauseResolutionMappingList.forEach(f => this.rootCauseReasonData.push(f));

            }
        });
        this.uploadDocumentRoot = true;
        this.dialog.open(this.uploadDocumentRootDialog, {
            width: '80%',
            disableClose: true
        });
    }
    createTicketData: any
    helperdata: any
    helperStringData: any
    uploadformData: any;
    updatefeedbackDetails: any = [];

    formatTime(fromTime) {
        if (typeof fromTime != "string") {
            let hour = new Date(fromTime).getHours();
            let min = new Date(fromTime).getMinutes();
            if (hour < 10) {
                if (min < 10) {
                    fromTime = `0${hour}:0${min}`;
                } else {
                    fromTime = `0${hour}:${min}`;
                }
            } else {
                if (min < 10) {
                    fromTime = `${hour}:0${min}`;
                } else {
                    fromTime = `${hour}:${min}`;
                }
            }
            return fromTime;
        } else {
            return fromTime;
        }
    }
    changeStatusTicket(dialogRef): void {
        this.changeStausSubmitted = true;

        const formData = new FormData();

        if (this.chnageStatusForm.valid) {
            this.createTicketData = this.chnageStatusForm.value;
            this.helperdata = this.createTicketData.helperName;

            if (this.helperdata) {
                this.helperStringData = this.helperdata.map(el => `${el}`).join(",");
            } else {
                this.helperStringData = "";
            }

            const updateDetails: any = {
                ticketId: this.createTicketData.ticketId,
                status: this.createTicketData.newStatus,
                remark: this.createTicketData.remark,
                remarkType: "Change status",
                helperName: this.helperStringData,
                nextFollowupDate: this.createTicketData.nextFollowupDate,
                nextFollowupTime: this.formatTime(this.createTicketData.nextFollowupTime)
            };

            if (this.createTicketData.newStatus === "Resolved") {
                updateDetails.finalResolutionId = this.createTicketData.finalResolutionId;
                updateDetails.rootCauseReasonId = this.createTicketData.rootCauseReasonId;

                const resolutionDto = {
                    resolutionId: this.createTicketData.finalResolutionId,
                    caseId: this.createTicketData.ticketId,
                    remarks: this.uploadRootForm.get("uploadremark")?.value || "",
                    latitude: this.uploadRootForm.get("latitude")?.value,
                    longitude: this.uploadRootForm.get("longitude")?.value
                };

                formData.append("resoultionFileMappingDTO", JSON.stringify(resolutionDto));

                const files: File[] = this.uploadRootForm.get("resolutionFiles")?.value || [];
                for (let i = 0; i < files.length; i++) {
                    formData.append("resolutionFiles", files[i]);
                }

                if (this.uploadformData?.length > 0) {
                    this.uploadformData.forEach((section, i) => {
                        formData.append(`sections[${i}].name`, section.name);
                        formData.append(`sections[${i}].latitude`, section.latitude);
                        formData.append(`sections[${i}].longitude`, section.longitude);
                        formData.append(`sections[${i}].opticalRange`, section.opticalRange);
                        section.files.forEach((file: File) => {
                            formData.append(`sections[${i}].files`, file);
                        });
                    });
                }
            }

            if (this.createTicketData.newStatus === "Closed") {
                if (this.updatefeedbackDetails.length == 0) {
                    updateDetails.call_status = this.createTicketData.call_status;
                    updateDetails.is_closed = this.createTicketData.is_closed;
                    updateDetails.caseFeedbackRel = null;
                    updateDetails.deacivate_reason = this.createTicketData.deacivate_reason;
                } else {
                    updateDetails.call_status = this.createTicketData.call_status;
                    updateDetails.is_closed = this.createTicketData.is_closed;
                    updateDetails.caseFeedbackRel = [this.updatefeedbackDetails];
                    updateDetails.deacivate_reason = this.createTicketData.deacivate_reason;
                }
            }

            formData.append("caseUpdate", JSON.stringify(updateDetails));

            const url = "/case/updateDetails";
            this.ticketManagementService.assignMethod(url, formData).subscribe(
                (response: any) => {
                    if (response.responseCode == 417) {
                        this.toastr.info(`${response.responseMessage}`, 'Info!')

                    } else if (response.responseCode == 404) {
                        this.toastr.info(`${response.responseMessage}`, 'Info!')

                    } else if (response.responseCode === 406) {
                        this.toastr.info(`${response.responseMessage}`, 'Info!')

                    } else {
                        dialogRef.close()
                        this.getcustTicket(this.customerId, "");
                        this.dialogRef.close();
                        if (this.dialogRef && this.dialogRef.componentInstance) {
                            this.refreshTicketDetails(this.viewTicketId);
                        } else {
                            this.openTicketDetailView(this.viewTicketId);
                        }
                        this.toastr.success(`Status Changed Successfully`, 'Success!')

                        this.uploadformData = [];
                        this.chnageStatusForm.reset();
                        this.uploadRootForm.reset();
                        this.selectedFileUploadPreview = [];
                        const fileInput = document.getElementById("txtSelectDocument") as HTMLInputElement;
                        if (fileInput) {
                            fileInput.value = "";
                        }

                        this.changeStausSubmitted = false;
                        this.changeStatusModal = false;
                    }
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')

                }
            );
        }
    }
    chakedTicketData = [];

    changeSelectStatus(dialogRef) {
        let data = [];

        this.chakedTicketData.forEach(element => {
            data.push({
                ticketId: element.caseId,
                status: this.chnageStatusForm.value.newStatus,
                remark: this.chnageStatusForm.value.remark,
                remarkType: "Change status",
                finalResolutionId: this.chnageStatusForm.value.finalResolutionId,
                rootCauseReasonId: this.chnageStatusForm.value.rootCauseReasonId,
                helperName: this.chnageStatusForm.value.helperName?.toString(),
                nextFollowupDate: this.chnageStatusForm.value.nextFollowupDate,
                nextFollowupTime: this.formatTime(this.chnageStatusForm.value.nextFollowupTime),
                call_status: this.chnageStatusForm.value.call_status,
                is_closed: this.chnageStatusForm.value.is_closed,
                deacivate_reason: this.chnageStatusForm.value.deacivate_reason,
                caseFeedbackRel: [this.caseFeedbackRel]
            });
        });
        // const url = `/case/bulkUpdateDetails?Status=${this.changeCaseStatus}&remark=${this.changeCaseRemark}`;
        const url = `/case/bulkUpdateDetails`;
        this.ticketManagementService.updateMethod(url, data).subscribe(
            (response: any) => {
                if (response.responseCode == 406) {
                    this.toastr.success(`${response.responseMessage}`, 'Success!')

                } else if (response.responseCode == 200) {
                    this.toastr.success(`${response.responseMessage}`, 'Success!')


                    this.changeStatusModal = false;
                }
                dialogRef.close()
                this.changeStatusModal = false;
                this.changeStatusDataObj = [];
                this.changeCaseStatus = "";
                this.changeCaseRemark = "";
                this.getcustTicket(this.customerId, "");
                this.dialogRef.close();
                if (this.dialogRef && this.dialogRef.componentInstance) {
                    this.refreshTicketDetails(this.viewTicketId);
                } else {
                    this.openTicketDetailView(this.viewTicketId);
                }
                this.changeStausSubmitted = false;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')

            }
        );
    }
    caseFeedbackRel: any
    getResolutionReasonsChangeStatusbulk(value: string): void {
        // this.getAllStaff();
        this.caseFeedbackRel = null;
        this.feedbackForm.reset();
        this.isCall = false;
        this.isticket = false;
        this.isCallDisconnected = false;
        this.changeStatusSelection(value);
        if (value === "Resolved") {
            this.chnageStatusForm.controls.finalResolutionId.enable();
            this.chnageStatusForm.get("finalResolutionId").clearValidators();
            this.chnageStatusForm.get("finalResolutionId").updateValueAndValidity();
            this.chnageStatusForm.get("rootCauseReasonId").clearValidators();
            this.chnageStatusForm.get("rootCauseReasonId").updateValueAndValidity();

            const url = `/resolutionReasons/all`;
            this.ticketManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.resolutionReasonData = response.dataList;
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')

                }
            );
        } else {
            this.chnageStatusForm.controls.finalResolutionId.setValue("");
            this.chnageStatusForm.controls.rootCauseReasonId.setValue("");
        }

        if (value === "Follow Up") {
            this.chnageStatusForm.controls.nextFollowupDate.setValidators(Validators.required);
            this.chnageStatusForm.controls.nextFollowupDate.updateValueAndValidity();
            this.chnageStatusForm.controls.nextFollowupTime.setValidators(Validators.required);
            this.chnageStatusForm.controls.nextFollowupTime.updateValueAndValidity();
        } else {
            this.chnageStatusForm.controls.nextFollowupDate.clearValidators();
            this.chnageStatusForm.controls.nextFollowupDate.updateValueAndValidity();
            this.chnageStatusForm.controls.nextFollowupTime.clearValidators();
            this.chnageStatusForm.controls.nextFollowupTime.updateValueAndValidity();
        }
    }
    getResolutionReasons(event) {
        const subCatId = this.viewTicketData.reasonSubCategoryId;
        var value = event.value;
        if (value === "Resolved") {
            if (this.viewTicketData.currentAssigneeId == this.currentLoginUserId) {

                const url = "/resolutionReasons/searchBySubCategory/" + subCatId;

                this.ticketManagementService.getMethod(url).subscribe(
                    (response: any) => {
                        this.resolutionReasonData = response.dataList;

                    },
                    (error: any) => {
                        console.log(error, "error");
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!')

                    }
                );
            }
        }
        if (value === "Raise and Close") {
            const url = "/resolutionReasons/searchBySubCategory/" + subCatId;
            this.ticketManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.resolutionReasonData = response.dataList;
                },
                (error: any) => { }
            );
        }
    }
    changeStatusSelection(status) {
        this.isCall = false;
        if (this.changeStatusSingleMultiple == "pTicket") {
            let oldStatus = this.chnageStatusForm.controls.oldStatus.value;
            if (oldStatus != "Resolved") {
                if (status == "Closed") {
                    this.chnageStatusForm.controls.newStatus.setValue("");
                    this.toastr.info(`Ticket can be marked closed only after the resolved status.`, 'Info!')

                }
            }
        }
    }
    activeTabIndex: number = 0;
    selectedResolveFileUploadPreview: any[] = [];
    uploadResolvedocumentId = false
    uploadResolveDocument() {
        this.activeTabIndex = 0;
        this.uploadResolveDocForm.forEach((formGroup, tabIndex) => {
            formGroup.patchValue({
                sectionName: this.tabs[tabIndex]
            });
        });
        this.selectedResolveFileUploadPreview = [];
        this.uploadResolvedocumentId = true;
        this.dialog.open(this.uploadResolveDocumentDialog, {
            width: '80%',
            disableClose: true
        });
    }
    ChangestatusList = [];
    statusData: any
    getCaseStatusForChange(currentAssigneeId) {
        // this.createStatusList = [];
        this.ChangestatusList = [];

        const url = "/commonList/caseStatus";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.statusData = response.dataList;

                this.statusData.forEach(element => {
                    if (
                        currentAssigneeId == null &&
                        this.chnageStatusForm.value.oldStatus === "Resolved" &&
                        element.value === "Closed"
                    ) {
                        this.ChangestatusList.push(element);
                    } else if (currentAssigneeId != null && element.value !== "Raise and Close") {
                        this.ChangestatusList.push(element);
                    }
                });
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')

            }
        );
    }
    changeStatusDataObj: any = [];
    changeCaseStatus = "";
    changeCaseRemark = "";
    dialogRef2: MatDialogRef<any>;

    confirmChangeStatus() {
        this.changeCaseStatus = "";
        this.changeCaseRemark = "";
        this.chnageStatusForm.reset();
        this.dialogRef2 = this.dialog.open(this.confirmDialog, {
            width: '400px',
            disableClose: true
        });

        this.dialogRef2.afterClosed().subscribe((result) => {
            if (result === true) {
                this.dialog.open(this.changeStatusDialog, {
                    width: '80%',
                    disableClose: true
                });
            } else {
                this.toastr.info(`You have Rejected`, 'Info!')

            }
        });
    }
    reasonForCallDisconnect: any = [];
    feedbackFormModal = false
    onCallDisconnected(event) {
        if (event == "false") {
            this.isCallDisconnected = true;
            this.isticket = true;
            const url = "/case/findAll/ContactFailed";
            this.ticketManagementService.getMethod(url).subscribe(
                (response: any) => {
                    if (response.ContactFailed && response.ContactFailed?.length > 0) {
                        this.reasonForCallDisconnect = response.ContactFailed[0].split(",");
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')

                }
            );
        } else {
            this.chnageStatusForm.controls.deacivate_reason.reset();
            this.chnageStatusForm.controls.is_closed.reset();
            this.isCallDisconnected = false;
            //this.isCall = false;
            this.isticket = false;
            this.feedbackFormModal = true;
            this.dialog.open(this.feedbackFormDialog, {
                width: '80%',
                disableClose: true
            });
            this.getStaffBehaviourFeedback();
        }
    }
    priorityTicketData = [];

    getTicketPriority() {
        const url = "/commonList/ticket_priority";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.priorityTicketData = response.dataList;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
            }
        );
    }
    selectPriorityValue = "";
    selcetTicketData: any = [];
    idChangePriority: boolean = false;

    opechangePriorityMadel(data) {
        this.selectPriorityValue = "";
        this.selcetTicketData = data;
        this.idChangePriority = true;
        this.dialog.open(this.changePriorityDialog, {
            width: '550px',
            disableClose: true
        });
    }
    SavechangePriority(dialogRef) {
        let ticketdata: any = {};
        ticketdata.ticketId = this.selcetTicketData.caseId;
        ticketdata.status = this.selcetTicketData.caseStatus;
        ticketdata.caseType = this.selcetTicketData.caseType;
        ticketdata.assignee = this.selcetTicketData.currentAssigneeId;
        ticketdata.priority = this.selectPriorityValue;
        ticketdata.attachment = "";
        ticketdata.filename = "";
        ticketdata.remarkType = "";
        ticketdata.groupReasonId = this.selcetTicketData.groupReasonId;
        ticketdata.reasonSubCategoryId = this.selcetTicketData.reasonSubCategoryId;
        ticketdata.ticketReasonCategoryId = this.selcetTicketData.ticketReasonCategoryId;
        ticketdata.caseTitle = this.selcetTicketData.caseTitle;
        // ticketdata.caseStatus = this.selcetTicketData.caseStatus;
        ticketdata.source = this.selcetTicketData.source;
        ticketdata.subSource = this.selcetTicketData.subSource;
        ticketdata.customerAdditionalMobileNumber =
            this.selcetTicketData.customerAdditionalMobileNumber;
        ticketdata.customerAdditionalEmail = this.selcetTicketData.customerAdditionalEmail;
        ticketdata.helperName = this.selcetTicketData.helperName;
        ticketdata.nextFollowupDate = this.selcetTicketData.nextFollowupDate;
        ticketdata.nextFollowupTime = this.selcetTicketData.nextFollowupTime;
        // ticketdata.finalResolutionId = '';
        // ticketdata.remark = '';
        const formData = new FormData();
        formData.append("caseUpdate", JSON.stringify(ticketdata));
        const url = "/case/updateDetails";
        this.ticketManagementService.assignMethod(url, formData).subscribe(
            (response: any) => {
                if (response.responseCode === 406) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!')

                } else {
                    dialogRef.close()
                    this.selectPriorityValue = "";
                    this.idChangePriority = false;
                    this.getcustTicket(this.customerId, "");
                    this.dialogRef.close();
                    if (this.dialogRef && this.dialogRef.componentInstance) {
                        this.refreshTicketDetails(this.viewTicketId);
                    } else {
                        this.openTicketDetailView(this.viewTicketId);
                    }
                    this.toastr.success(`Successfully Updated`, 'Success!')
                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')

            }
        );
    }
    ticketDataForLink = [];
    showLinktickets: boolean = false;
    linkedTicketId: number;
    ticketIdToLink: number;
    searchData: any;
    ticketData: any
    ticketConfigtotalRecords: any;
    selectLinkTicket = false
    openLinkTicketDialog(ticket) {
        this.showLinktickets = true;
        this.getLinkableTickets(ticket);
    }
    getLinkableTickets(ticket) {
        this.ticketIdToLink = ticket.caseId;

        let data: any = [];
        if (ticket.customersId) {
            let data2 = {
                filterValue: ticket.customersId,
                filterColumn: "customerId"
            };
            data.push(data2);
        }
        if (ticket.ticketReasonCategoryId) {
            let data2 = {
                filterValue: ticket.ticketReasonCategoryId,
                filterColumn: "ticketReasonCategoryId"
            };
            data.push(data2);
        }
        let data3 = {
            filterValue: this.ticketIdToLink,
            filterColumn: "ticketIdToLink"
        };
        data.push(data3);
        this.searchData = {
            filters: data,
            page: this.currentPage,
            pageSize: this.itemsPerPage,
            sortBy: "createdate",
            sortOrder: 0
        };
        // this.showLinktickets = true;
        const url = "/case/case/search";
        this.ticketManagementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                if (response.responseCode == 404) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!')

                    this.ticketData = [];
                } else {
                    this.showLinktickets = true;
                    let filteredCases = response.dataList.filter(
                        caseItem => caseItem.caseStatus !== "Closed"
                    );
                    this.ticketDataForLink = filteredCases;
                    this.ticketConfigtotalRecords = this.ticketDataForLink.length;
                    this.linkedTicketId = null;
                    this.selectLinkTicket = true;
                    this.dialog.open(this.selectTicketDialog, {
                        width: '80%',
                        disableClose: true
                    });
                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')

            }
        );
    }
    uploadDocumentId = false
    selectedFileUploadPreview: any;
    ticketDataForDomain: any;

    uploadDocument(ticket) {
        this.uploadDataTicketId = ticket.caseId;
        this.uploadDocForm.patchValue({
            file: ""
        });
        this.selectedFileUploadPreview = [];
        this.uploadDocumentId = true;
        this.dialog.open(this.uploadDocumentDialog, {
            width: '80%',
            disableClose: true
        });
    }
    showChangeProblemDomain = false
    checkChangeProblemDomain(ticket) {
        this.ticketReasonSubCategoryData = "";
        this.ticketDataForDomain = ticket;
        this.assignTicketStatus = ticket.caseStatus;
        const url = `/case/reassignTicket?caseId=${ticket.caseId}`;
        this.ticketManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (response.data == "TRUE") {
                    this.getservicesByCustomer(ticket.customersId);
                    this.showChangeProblemDomain = true;
                    this.dialog.open(this.changeProblemDomainDialog, {
                        width: '600px',
                        disableClose: true
                    });
                } else {
                    this.toastr.info(`Not eligible to change problem domain...`, 'Info!')

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!')

            }
        );
    }
    changeProblemDomain(dialogRef) {
        this.ticketReasonSubCategoryData = "";
        const updateDetails: any = {};
        updateDetails.ticketId = this.ticketDataForDomain.caseId;
        updateDetails.status = this.ticketDataForDomain.caseStatus;
        updateDetails.remark = this.pickRemark;
        updateDetails.remarkType = "Change Problem Domain";
        updateDetails.ticketReasonCategoryId = this.problemDomain;
        updateDetails.reasonSubCategoryId = this.subProblemDomain;
        updateDetails.groupReasonId = this.reasonGroup;
        const formData = new FormData();
        formData.append("caseUpdate", JSON.stringify(updateDetails));
        const url = "/case/updateDetails";
        this.ticketManagementService.assignMethod(url, formData).subscribe(
            (response: any) => {
                if (response.responseCode === 406) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed')

                } else {
                    dialogRef.close()
                    this.getcustTicket(this.customerId, '')
                    this.dialogRef.close();
                    if (this.dialogRef && this.dialogRef.componentInstance) {
                        this.refreshTicketDetails(this.viewTicketId);
                    } else {
                        this.openTicketDetailView(this.viewTicketId);
                    }
                    this.showChangeProblemDomain = false;
                    this.pickRemark = null;
                    this.problemDomain = null;
                    this.ticketDataForDomain = null;
                    this.subProblemDomain = null;
                    this.reasonGroup = null;
                    this.toastr.success(`${response.message}`, 'Success!')

                }
            },
            (error: any) => {

                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')

            }
        );
    }
    customerServiceData
    getservicesByCustomer(id) {
        const url = "/ticketReasonCategory/getActiveServiceForSubscribers?customerId=" + id;
        this.customerService.getMethod(url).subscribe(
            (response: any) => {
                this.customerServiceData = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')

            }
        );
    }
    ticketETRData: any
    ticketETRModal = false
    openETRModal(data) {
        this.ticketETRForm.reset();
        this.ticketETRData = data;
        this.ticketETRModal = true;
        this.dialog.open(this.ticketETRDialog, {
            width: '80%',
            disableClose: true
        });
    }
    remarkTypeOption = []
    ticketRemarkModalOpen(ticket, ticketId, custId, staffId) {
        this.remarkTypeOption = [];
        let data1 = { label: "Internal Remark", value: "Internal Remark" };
        let data2 = { label: "External Remark", value: "External Remark" };
        this.remarkTypeOption.push(data1);
        if (ticket.caseOrigin === "Email") {
            this.remarkTypeOption.push(data2);
        }
        this.dialog.open(this.followupTicketDialog, {
            width: '80%',
            disableClose: true
        });
        this.followUpModal = true;
        this.followupForm.reset();
        this.folloupTicketId = ticketId;
        this.folloupCustId = custId;
        this.folloupTicketassignStaffId = staffId;
    }
    ticketConversationModalOpen(ticketId) {
        this.getFollowUpDetailById(ticketId);
        this.conversationModal = true;
        this.dialog.open(this.conversationDialog, {
            width: '80%',
            disableClose: true
        });
    }
    conversationModal = false
    followupSubmmitted = false
    followUpModal = false
    followupTicket(dialogRef) {
        this.followupSubmmitted = true;
        if (this.followupForm.valid) {
            const data = {
                remarkType: this.followupForm.controls.remarkType.value,
                isFromCustomer: false,
                remark: this.followupForm.controls.remark.value,
                custId: this.folloupCustId,
                caseId: this.folloupTicketId,
                remarkDate: this.datePipe.transform(new Date(), "yyyy-MM-dd HH:mm:ss"),
                staffId: this.folloupTicketassignStaffId
            };
            // console.log(' this.createTicketFollowupData', data);
            const url = "/ticketFollowupDetails/save";
            this.ticketManagementService.postMethod(url, data).subscribe(
                (response: any) => {
                    dialogRef.close()
                    this.followupSubmmitted = false;
                    this.followUpModal = false;
                    this.getFollowUpDetailById(this.folloupTicketId);
                    this.getFollowUpDetailById(this.folloupTicketId);
                    this.toastr.success(`Saved Successfully`, 'Success!')

                },
                (error: any) => {
                    // console.log(error, 'error');
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')

                }
            );
        }
    }
    closeFollowupTicket() {
        this.followupSubmmitted = false;
        this.followupForm.reset();
        this.followupForm.clearValidators();
        this.followupForm.updateValueAndValidity();
        this.followUpModal = false;
    }
    downloadResolveDocumentId = false
    activeTabViewIndex: number = 0;
    downloadResolveDocument(ticketDeatailData) {
        this.ticketIdData = ticketDeatailData.caseId;
        let url = "/case/documentList/" + this.ticketIdData;
        this.ticketManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.ticketFileDocData = response.dataList;
                if (response.responseCode == 200) {
                    this.toastr.success(`${response.responseMessage}`, 'Success!')

                    this.downloadResolveDocumentId = true;

                    this.dialog.open(this.viewDocumentDialog, {
                        width: '80%',
                        disableClose: true
                    });
                    this.activeTabViewIndex = 0;
                } else if (response.responseCode == 404) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!')

                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!')

                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')

            }
        );
    }

    closeDownloadResolveDocumentId() {
        this.downloadResolveDocumentId = false;
        this.activeTabViewIndex = 0;
    }

    downloadResolveDoc(fileName, section, sectionName) {
        let ticketId = section.ticketId;
        let uniqueName = section.uniqueName;
        this.ticketManagementService.downloadResolveFile(ticketId, uniqueName, sectionName).subscribe(
            blob => {
                if (blob.status == 200) {
                    this.toastr.success(`Download Successfully`, 'Success!')

                    importedSaveAs(blob.body, fileName);
                } else if (blob.status == 404) {
                    this.toastr.error(`File Not Found`, 'Failed!')

                } else {
                    this.toastr.error(`Something went wrong!`, 'Failed!')

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                console.log(error, "error");

            }
        );
    }
    resolvePreviewUrl: any;
    resolvedocumentPreview = false;
    showticketResolveDocData(fileName, section, sectionName) {
        // console.log("data ", data?.filename.split(".")[data?.filename.split(".")?.length - 1]);
        // const url = `/case/document/download/${data.ticketId}/${data.docId}`;
        const fileType = fileName.split(".");
        let ticketId = section.ticketId;
        let uniqueName = section.uniqueName;
        this.ticketManagementService.downloadResolveFile(ticketId, uniqueName, sectionName).subscribe(
            data => {
                if (data.status == 200) {
                    let type = "application/octet-stream"; // Default type
                    const uint = new Uint8Array(data.body);

                    const magic = uint.subarray(0, 4); // Check the magic bytes to identify the file type

                    if (magic.every(b => b === 0xff)) {
                        type = "image/jpeg";
                    } else if (
                        magic[0] === 0x89 &&
                        magic[1] === 0x50 &&
                        magic[2] === 0x4e &&
                        magic[3] === 0x47
                    ) {
                        type = "image/png";
                    } else if (
                        magic[0] === 0x47 &&
                        magic[1] === 0x49 &&
                        magic[2] === 0x46 &&
                        magic[3] === 0x38
                    ) {
                        type = "image/gif";
                    } else if (
                        magic[0] === 0xd0 &&
                        magic[1] === 0xcf &&
                        magic[2] === 0x11 &&
                        magic[3] === 0xe0
                    ) {
                        type = "application/vnd.ms-excel";
                    } else if (
                        magic[0] === 0x25 &&
                        magic[1] === 0x50 &&
                        magic[2] === 0x44 &&
                        magic[3] === 0x46
                    ) {
                        type = "application/pdf";
                    } else if (
                        magic[0] === 0xd0 &&
                        magic[1] === 0xcf &&
                        magic[2] === 0x11 &&
                        magic[3] === 0xe0
                    ) {
                        type = "application/msword";
                    }

                    if (fileType[fileType?.length - 1] === "pdf") {
                        // If it's a PDF file, create a blob and open it in a new tab
                        const blob = new Blob([data.body], { type: "application/pdf" });
                        const blobUrl = URL.createObjectURL(blob);
                        window.open(blobUrl, "_blank"); // Open PDF in a new tab
                    } else if (fileType[fileType?.length - 1] === "png") {
                        // If it's a PNG image, create a blob URL and display it in an <img> tag
                        const blob = new Blob([data.body], { type: "image/png" });
                        const blobUrl = URL.createObjectURL(blob);
                        this.resolvePreviewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(blobUrl); // Trust the blob URL
                        this.resolvedocumentPreview = true; // Set flag to show the image preview
                        this.dialog.open(this.documentResolvePreviewDialog, {
                            width: '80%',
                            disableClose: true
                        });
                    } else {
                        // For other types (e.g., JPEG, GIF), display as image preview
                        const blob = new Blob([data.body], { type });
                        const blobUrl = URL.createObjectURL(blob);
                        this.resolvePreviewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(blobUrl); // Trust the blob URL
                        this.resolvedocumentPreview = true; // Set flag to show the image preview
                        this.dialog.open(this.documentResolvePreviewDialog, {
                            width: '80%',
                            disableClose: true
                        });
                    }
                } else if (data.status == 404) {
                    this.toastr.error(`File Not Found`, 'Failed!')

                } else {
                    this.toastr.error(`Something went wrong!`, 'Failed!')

                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')

            }
        );
    }
    deleteResolveConfirm(file, section, sectionName) {
        const dialogRef = this.dialog.open(this.confirmDialog, {
            width: '400px',
            data: {
                title: 'Delete Confirmation',
                description: `Do you want to delete this File?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe((result) => {
            if (result === true) {
                this.deleteResolveDoc(file, section, sectionName);
            } else {
                this.toastr.info(`You have rejected`, 'Info!')

            }
        });
    }
    deleteResolveDoc(fileName, section, sectionName) {
        let ticketId = section.ticketId;
        let uniqueName = section.uniqueName;
        let urldoc =
            "/case/document/delete/" +
            ticketId +
            "/" +
            fileName +
            "/" +
            uniqueName +
            "/" +
            sectionName +
            "/";
        this.ticketManagementService.deleteMethod(urldoc).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.toastr.success(`${response.responseMessage}`, 'Success!')

                    this.closeDownloadResolveDocumentId();
                } else if (response.responseCode == 404) {
                    this.toastr.success(`${response.responseMessage}`, 'Success!')

                } else {
                    this.toastr.success(`${response.responseMessage}`, 'Success!')

                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')

            }
        );
    }
    onFileChangeUploadRoot(event: any): void {
        const validFiles: File[] = [];
        const files: FileList = event.target.files;

        for (let i = 0; i < files.length; i++) {
            const file = files.item(i);
            if (
                file &&
                (file.type === "image/png" ||
                    file.type === "image/jpg" ||
                    file.type === "image/jpeg" ||
                    file.type === "application/pdf")
            ) {
                validFiles.push(file);
            } else {
                this.toastr.error(`Only PNG, JPG, JPEG, or PDF allowed`, 'Failed!')

            }
        }

        this.selectedFileUploadPreview = validFiles;

        this.uploadRootForm.patchValue({
            resolutionFiles: validFiles
        });
    }
    onFileChangeUpload(event: any) {
        this.selectedFileUploadPreview = [];
        if (event.target.files.length > 0) {
            this.selectedFile = event.target.files[0];
            const files: FileList = event.target.files;
            for (let i = 0; i < files.length; i++) {
                this.selectedFileUploadPreview.push(files.item(i));
            }
            if (
                this.selectedFile.type != "image/png" &&
                this.selectedFile.type != "image/jpg" &&
                this.selectedFile.type != "image/jpeg" &&
                this.selectedFile.type != "application/pdf"
            ) {
                this.uploadDocForm.controls.file.reset();
                alert("File type must be png, jpg, jpeg or pdf");
            } else {
                const file = event.target.files;
                this.uploadDocForm.patchValue({
                    file: file
                });
            }
        }
    }
    deletUploadedFile(event: any) {
        var temp: File[] = this.selectedFileUploadPreview?.filter((item: File) => item?.name != event);
        this.selectedFileUploadPreview = temp;
        this.uploadDocForm.patchValue({
            file: temp
        });
    }
    mylocationRoot(): void {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(position => {
                this.uploadRootForm.patchValue({
                    latitude: position.coords.latitude,
                    longitude: position.coords.longitude
                });
            });
        } else {
            this.toastr.error(`Geolocation is not supported by this browser.`, 'Failed!')
        }
    }
    mylocation() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(position => {
                if (position) {
                    // this.iflocationFill = true;
                    this.uploadResolveDocForm.forEach((formGroup, tabIndex) => {
                        if (this.activeTabIndex === tabIndex) {
                            formGroup.patchValue({
                                latitude: position.coords.latitude,
                                longitude: position.coords.longitude
                            });
                        }
                    });
                }
            });
        } else {
            this.toastr.error(`Geolocation is not supported by this browser.`, 'Failed!')

        }
    }
    deletUploadedFileRoot(fileName: string): void {
        const remainingFiles = this.selectedFileUploadPreview.filter(
            (file: File) => file.name !== fileName
        );
        this.selectedFileUploadPreview = remainingFiles;
        this.uploadRootForm.patchValue({
            resolutionFiles: remainingFiles
        });
    }
    onFileResolveChangeUpload(event: any, tabIndex: number): void {
        this.selectedResolveFileUploadPreview[tabIndex] = [];
        const inputElement = event.target as HTMLInputElement;
        if (inputElement.files && inputElement.files.length > 0) {
            const files: FileList = inputElement.files;

            // Validate all files
            for (let i = 0; i < files.length; i++) {
                const file = files.item(i);
                if (
                    file &&
                    (file.type === "image/png" ||
                        file.type === "image/jpg" ||
                        file.type === "image/jpeg" ||
                        file.type === "application/pdf")
                ) {
                    this.selectedResolveFileUploadPreview[tabIndex].push(file);
                } else {
                    this.toastr.info(`Invalid file type: ${file?.name}.Must be png, jpg, jpeg, or pdf.`, 'Info!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: `Invalid file type: ${file?.name}. Must be png, jpg, jpeg, or pdf.`,
                    //     icon: "far fa-check-circle"
                    // });
                }
            }

            if (this.selectedResolveFileUploadPreview[tabIndex].length > 0) {
                this.resolveMultiFiles = this.createResolveFileList(
                    this.selectedResolveFileUploadPreview[tabIndex]
                );
                this.selectedFile = this.selectedResolveFileUploadPreview[tabIndex][0];
                this.uploadResolveDocForm[tabIndex].patchValue({
                    file: this.resolveMultiFiles[0]
                });
            } else {
                this.uploadResolveDocForm[tabIndex].controls.file.reset();
                inputElement.value = "";
            }
        }
    }
    createResolveFileList(files: File[]): FileList {
        const dataTransfer = new DataTransfer();
        files.forEach(file => dataTransfer.items.add(file));
        return dataTransfer.files;
    }

    deleteResolveUploadedFile(fileName: string, tabIndex: number): void {
        const temp: File[] = this.selectedResolveFileUploadPreview[tabIndex]?.filter(
            (item: File) => item?.name !== fileName
        );
        this.selectedResolveFileUploadPreview[tabIndex] = temp;
        this.uploadResolveDocForm[tabIndex].patchValue({
            file: temp
        });
    }
    resolvesubmitted = false;
    tabsMandatory: any[];
    resolveMultiFiles: any;
    selectedFile: any;

    uploadResolveAllDocuments(dialogRef): void {
        this.resolvesubmitted = true;
        let allSectionsData: any[] = [];
        let allFiles: File[] = [];
        let invalidMandatoryTabs: string[] = [];
        dialogRef.close()
        this.uploadResolveDocForm.forEach((formGroup, tabIndex) => {
            formGroup.patchValue({
                sectionName: this.tabs[tabIndex]
            });
            const isOpticalPowerRange = this.tabs[tabIndex] === "Optical Power Range";
            const sectionData = this.collectResolveSectionData(formGroup, tabIndex);
            const hasFiles = sectionData && sectionData.files.length > 0;
            const isMandatory = this.tabsMandatory[tabIndex];
            let isValid = true;
            if (isMandatory) {
                isValid =
                    formGroup.valid ||
                    (isOpticalPowerRange && (hasFiles || formGroup.get("opticalRange")?.value != null));
            }
            if (isValid) {
                if (sectionData) {
                    allSectionsData.push(sectionData.section);
                    if (sectionData.files.length > 0) {
                        allFiles = [...allFiles, ...sectionData.files];
                    }
                }
            } else if (isMandatory) {
                invalidMandatoryTabs.push(this.tabs[tabIndex]);
            }
        });
        if (invalidMandatoryTabs.length > 0) {
            this.toastr.info(`Fields are mandatory in these tabs: ${invalidMandatoryTabs.join(", ")}`, 'Info!')

            return;
        }
        if (allSectionsData.length > 0) {
            this.uploadResolveDocuments(allSectionsData, allFiles);
        }
        const fileInput = document.getElementById("txtSelectDocument") as HTMLInputElement;
        if (fileInput) {
            fileInput.value = "";
        }
    }
    collectResolveSectionData(
        formGroup: UntypedFormGroup,
        tabIndex: number
    ): { section: any; files: File[] } | null {
        const section = {
            name: formGroup.value.sectionName,
            latitude: formGroup.value.latitude,
            longitude: formGroup.value.longitude,
            opticalRange: formGroup.value.opticalRange,
            files: [] as File[]
        };

        if (this.selectedResolveFileUploadPreview[tabIndex]) {
            this.selectedResolveFileUploadPreview[tabIndex].forEach((file: File) => {
                section.files.push(file);
            });
        }

        return { section, files: section.files };
    }
    uploadResolveDocuments(sectionsData: any[], allFiles: File[]): void {
        this.uploadformData = sectionsData;

        const url = `/inwards/inventory/document/upload/`;

        this.closeUploadResolveDocumentId();
        this.uploadResolvedocumentId = false;
    }
    closeUploadResolveDocumentId(): void {
        this.uploadResolvedocumentId = false;
        this.resolvesubmitted = false;

        // Reset the main form

        // Reset all form groups inside uploadDocForm array
        this.uploadResolveDocForm.forEach(formGroup => {
            formGroup.reset();
        });

        // Clear file previews
        this.selectedResolveFileUploadPreview = [];

        // Clear the file input
        const fileInput = document.getElementById("txtSelectDocument") as HTMLInputElement;
        if (fileInput) {
            fileInput.value = ""; // This clears the file input
        }

        // Optional: If your component supports multiple tabIndexes and each has its own preview
        this.tabs.forEach((_, index) => {
            this.selectedResolveFileUploadPreview[index] = [];
            const dynamicFileInput = document.getElementById(
                `txtSelectDocument_${index}`
            ) as HTMLInputElement;
            if (dynamicFileInput) {
                dynamicFileInput.value = "";
            }
        });
    }
    staffBehaviourData: any;
    isProblemType = false
    getStaffBehaviourFeedback() {
        const url = "/case/findAll/Feedback";
        this.ticketManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.staffBehaviourData = response.UnsatisfiedList[0].split(",");
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')

            }
        );
    }
    getServiceExperience(event) {
        this.feedbackForm.controls.problem_type.reset();
        if (event == "Not Satisfied") {
            this.isProblemType = true;
        } else {
            this.isProblemType = false;
        }
    }
    BehaviourReasonData: any
    BehaviourData: any
    isEnable = false
    getbehaviourPro(event) {
        this.feedbackForm.controls.reason.reset();
        const url = "/case/findAll/Feedback";
        this.ticketManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.BehaviourData = response.UnsatisfiedList[0].split(",");
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
        if (event == "Satisfied") {
            this.isEnable = true;
            const url = "/case/findAll/Satisfied";
            this.ticketManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.BehaviourReasonData = response.SatisfiedList[0].split(",");
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')

                }
            );
        } else if (event == "Not Satisfied") {
            this.BehaviourReasonData = [];
            this.isEnable = true;
            const url = "/case/findAll/Unsatisfied";
            this.ticketManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.BehaviourReasonData = response.UnsatisfiedList[0].split(",");
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')

                }
            );
        } else {
            this.isEnable = false;
        }
    }
    reasonStringdata: any
    reasondata
    infodata
    infoStringdata
    paymentTypedata
    paymentTypeStringdata
    feedbackSubmitted = false
    saveFeedback(ticketId, dialogRef) {
        if (this.feedbackForm.valid) {
            this.feedbackSubmitted = true;
            this.caseFeedbackRel = this.feedbackForm.value;
            this.reasondata = this.caseFeedbackRel.reason;
            if (this.reasondata != null && this.reasondata != undefined && this.reasondata != "") {
                this.reasonStringdata = this.reasondata.map((element, index) => `${element}`).join(",");
                //console.log("this.reasonStringdata",this.reasonStringdata);
            } else {
                this.reasonStringdata;
            }

            this.infodata = this.caseFeedbackRel.infoOfPaymentMode;
            if (this.infodata != null && this.infodata != undefined && this.infodata != "") {
                this.infoStringdata = this.infodata.map((element, index) => `${element}`).join(",");
                //console.log("this.infoStringdata",this.infoStringdata);
            } else {
                this.infoStringdata = "";
            }
            this.paymentTypedata = this.caseFeedbackRel.problem_type;
            if (
                this.paymentTypedata != null &&
                this.paymentTypedata != undefined &&
                this.paymentTypedata != ""
            ) {
                this.paymentTypeStringdata = this.paymentTypedata
                    .map((element, index) => `${element}`)
                    .join(",");
            } else {
                this.paymentTypeStringdata = "";
            }
            this.updatefeedbackDetails = {};
            this.updatefeedbackDetails.support_type = this.caseFeedbackRel.support_type;
            this.updatefeedbackDetails.staff_behavior = this.caseFeedbackRel.staff_behavior;
            this.updatefeedbackDetails.payment_mode = this.caseFeedbackRel.payment_mode;
            this.updatefeedbackDetails.infoOfPaymentMode = this.infoStringdata;
            this.updatefeedbackDetails.current_bandwidth_feedback =
                this.caseFeedbackRel.current_bandwidth_feedback;
            this.updatefeedbackDetails.current_price_feedback =
                this.caseFeedbackRel.current_price_feedback;
            this.updatefeedbackDetails.referal_information = this.caseFeedbackRel.referal_information;
            this.updatefeedbackDetails.technicial_support_feedback =
                this.caseFeedbackRel.technicial_support_feedback;
            this.updatefeedbackDetails.problem_type = this.paymentTypeStringdata;
            this.updatefeedbackDetails.service_experience = this.caseFeedbackRel.service_experience;
            this.updatefeedbackDetails.behaviour_professionalism =
                this.caseFeedbackRel.behaviour_professionalism;
            this.updatefeedbackDetails.reason = this.reasonStringdata;
            this.updatefeedbackDetails.overall_rating = this.caseFeedbackRel.overall_rating;
            this.updatefeedbackDetails.general_remarks = this.caseFeedbackRel.general_remarks;
            this.updatefeedbackDetails.ticketid = ticketId;
            this.feedbackForm.reset();
            this.feedbackFormModal = false;
            dialogRef.close()
        }
    }
    chakedTktData = []
    isTicketChecked = false
    isTicketCheckedAssigntome = false
    allIsChecked = false
    modalCloseTicket() {
        this.selectLinkTicket = false;
        this.chakedTktData = [];
    }
    addTicketCheckedData(id, event) {
        if (event.checked) {
            this.ticketDataForLink.forEach((value, i) => {
                if (value.caseId == id) {
                    this.chakedTktData.push(value.caseId);
                }
            });

            if (this.ticketDataForLink.length === this.chakedTktData.length) {
                this.isTicketChecked = true;
                this.isTicketCheckedAssigntome = true;
                this.allIsChecked = true;
            }
        } else {
            let checkedData = this.ticketDataForLink;
            checkedData.forEach(element => {
                if (element.caseId == id) {
                    element.isSingleTktChecked = false;
                }
            });
            this.chakedTktData.forEach((value, index) => {
                if (value.caseId == id) {
                    this.chakedTktData.splice(index, 1);
                    // console.log(this.chakedTicketData);
                }
            });

            if (this.chakedTktData.length == 0) {
                this.isTicketChecked = false;
                this.isTicketCheckedAssigntome = false;
            }
        }
    }
    ticketReasonCategoryData
    filteredReasonCategoryList
    ticketServiceList
    groupReasonData
    getAllTicketReasonCategory(serviceLists: any) {
        serviceLists = this.ticketServiceList;
        if (serviceLists != null) {
            const url = "/ticketReasonCategory/getReasonCategoryByActiveServices";
            this.ticketManagementService.postMethod(url, serviceLists).subscribe(
                (response: any) => {
                    this.ticketReasonCategoryData = response.dataList;
                    this.filteredReasonCategoryList = this.ticketReasonCategoryData;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                }
            );
        }
    }
    selReasonCategory(event) {
        this.getSubCategoryByparentCat(event.value);
    }

    selReasonSubCategory(event) {
        this.getGroupReasonBySubCat(event.value);
    }
    getGroupReasonBySubCat(id): void {
        const selSubCatData = this.ticketReasonSubCategoryData.filter(subCat => subCat.id === id);
        this.groupReasonData = selSubCatData[0].ticketSubCategoryGroupReasonMappingList;
    }
    pickRemark
    problemDomain
    subProblemDomain
    reasonGroup
    hangeProblemDomain(dialogRef) {
        this.ticketReasonSubCategoryData = "";
        const updateDetails: any = {};
        updateDetails.ticketId = this.ticketDataForDomain.caseId;
        updateDetails.status = this.ticketDataForDomain.caseStatus;
        updateDetails.remark = this.pickRemark;
        updateDetails.remarkType = "Change Problem Domain";
        updateDetails.ticketReasonCategoryId = this.problemDomain;
        updateDetails.reasonSubCategoryId = this.subProblemDomain;
        updateDetails.groupReasonId = this.reasonGroup;
        const formData = new FormData();
        formData.append("caseUpdate", JSON.stringify(updateDetails));
        const url = "/case/updateDetails";
        this.ticketManagementService.assignMethod(url, formData).subscribe(
            (response: any) => {
                if (response.responseCode === 406) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed')

                } else {
                    dialogRef.close()

                    this.getcustTicket(this.customerId, '')
                    this.dialogRef.close();
                    if (this.dialogRef && this.dialogRef.componentInstance) {
                        this.refreshTicketDetails(this.viewTicketId);
                    } else {
                        this.openTicketDetailView(this.viewTicketId);
                    }
                    this.showChangeProblemDomain = false;
                    this.pickRemark = null;
                    this.problemDomain = null;
                    this.ticketDataForDomain = null;
                    this.subProblemDomain = null;
                    this.reasonGroup = null;
                    this.toastr.success(`${response.message}`, 'Success!')

                }
            },
            (error: any) => {

                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')

            }
        );
    }
    submittedETR = false
    parentCustomerListdatatotalRecords
    ETRSaveData(dialogRef) {
        let customerBill = "";
        let custEmail = "";
        this.submittedETR = true;
        if (this.ticketETRForm.valid) {
            const url = "/customers/" + this.ticketETRData.customersId;
            this.customerService.getMethod(url).subscribe((response: any) => {
                if (response.customers.planMappingList.length > 0) {
                    customerBill = response.customers.planMappingList[0].billTo;
                    custEmail = response.customers.email;
                }

                this.ticketETRForm.value.notificationTime = this.formatTime(
                    this.ticketETRForm.value.notificationTime
                );

                let data = {
                    custId: this.ticketETRData.customersId,
                    customerEmailId: custEmail,
                    customerMobileNo: this.ticketETRData.mobile,
                    mvnoId: this.ticketETRData.mvnoId,
                    notificationDate: this.ticketETRForm.value.notificationDate
                        ? this.ticketETRForm.value.notificationDate
                        : "",
                    notificationTime: this.ticketETRForm.value.notificationTime
                        ? this.ticketETRForm.value.notificationTime
                        : "",
                    remark: this.ticketETRForm.value.remark ? this.ticketETRForm.value.remark : " ",
                    selectedNotificationType: {
                        sms: this.ticketETRForm.value.sms ? this.ticketETRForm.value.sms : false,
                        email: this.ticketETRForm.value.email ? this.ticketETRForm.value.email : false
                    },
                    staffId: this.ticketETRData.currentAssigneeId,
                    templateContent: "",
                    ticketId: this.ticketETRData.caseId,
                    ticketNumber: this.ticketETRData.caseNumber,
                    isTemplateDynamic: this.ticketETRForm.value.isTemplateDynamic,
                    status: this.ticketETRData.caseStatus,
                    sender: "Organization" //customerBill,
                };

                const url = "/case/sendETRtoCustomer";
                this.ticketManagementService.postMethod(url, data).subscribe(
                    (response: any) => {
                        dialogRef.close()
                        this.closeETRModel();
                        this.ticketETRModal = false;
                        this.dialogRef.close();
                        if (this.dialogRef && this.dialogRef.componentInstance) {
                            this.refreshTicketDetails(this.viewTicketId);
                        } else {
                            this.openTicketDetailView(this.viewTicketId);
                        }
                        this.toastr.success(`Saved Successfully`, 'Success!')

                    },
                    (error: any) => {
                        this.parentCustomerListdatatotalRecords = 0;
                        if (error.error.status == 400 || error.error.status == 404) {
                            this.toastr.info(`${error.error.msg}`, 'Info!')

                        } else {
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!')

                        }
                    }
                );
            });
        }
    }
    fileNameCDR = "CDR.xlsx";

    ticketETRXMLDownload() {
        if (this.ticketETRDetailData.length > 0) {
            const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(this.ticketETRDetailData);
            const wb: XLSX.WorkBook = XLSX.utils.book_new();
            XLSX.utils.book_append_sheet(wb, ws, "Sheet1");
            XLSX.writeFile(wb, this.fileNameCDR);
        }
    }
    selETRmessageMode(event) {
        let mode = event.value;
        if (mode == false) {
            this.ticketETRForm.get("remark").clearValidators();
            this.ticketETRForm.get("remark").updateValueAndValidity();
            this.ticketETRForm.patchValue({
                remark: ""
            });
        } else {
            this.ticketETRForm.get("remark").setValidators(Validators.required);
            this.ticketETRForm.get("remark").updateValueAndValidity();
        }
    }
    closeETRModel() {
        this.submittedETR = false;
        this.ticketETRForm.reset();
    }
    callTicketRef = () => {
        // this.ticketManagementComponentRef.addEditTicket("", '');
    };

    returnTicketArray(value) {
        this.addTicketModal = false;
        this.getcustTicket(this.custData.id, "");
    }

    addTicket() {
        // if (this.ticketManagementComponentRef) {
        // this.addTicketModal = true;
        // this.dialog.open(this.createTicketDialog, { width: '80vw' });
        // this.ticketManagementComponentRef.createTicketFun();
        // this.ticketManagementComponentRef?.getTicketClassification();
        // this.ticketManagementComponentRef?.getCustomersDetail(this.custData.id);
        // const state = "{ data: this.custData }";
        // } else {
        // console.error('Ticket component is not initialized');

        const dialogRef = this.dialog.open(TicketManagementComponent, {
            width: '80%',
            data: { customerDetailsData: this.custData }
        });
        dialogRef.afterClosed().subscribe(result => {
            this.getcustTicket(this.custData.id, this.ticketShowItemPerPage);
        });
        // }
    }

    closeDialog() {
        // this.ticketManagementComponentRef.ticketGroupForm.reset();
        this.addTicketModal = false;
    }

    getCustomersDetail(custId) {
        const url = "/customers/" + custId;
        this.customerManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.custData = response.customers;
                this.getcustTicket(custId, "");
            },
            (error: any) => {
                // console.log(error, "error")
            }
        );
    }

    customerDetailOpen() {
        if (this.isFromCaf) {
            this.backToList.emit();
            return;
        }
        this.router.navigate(["/home/customer/details/" + this.custType + "/x/" + this.customerId]);
    }

    getcustTicket(custId, size, page?) {
        // let pageSize: number;
        // if (size) {
        // let pageSize = size;
        // this.itemsPerPage = size;
        // } else if (this.ticketShowItemPerPage === 1) {
        //     pageSize = RadiusConstants.ITEMS_PER_PAGE;
        //     this.itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
        // } else {
        //     pageSize = this.ticketShowItemPerPage;
        //     this.itemsPerPage = this.ticketShowItemPerPage;
        // }
        this.tiketTimer();
        const url = "/getCasesByCustomer/" + custId;
        const payload = {
            page: this.currentPage,
            pageSize: this.itemsPerPage
        };

        this.ticketManagementService.getCutomerTicketData(url, payload).subscribe(
            (response: any) => {
                this.custTicketList = response.dataList;
                this.totalRecords = response.totalRecords;
                this.currentPage = response.currentPageNumber;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    pageChangedTicketConfig(pageNumber) {
        this.currentPage = pageNumber.pageIndex + 1;
        this.itemsPerPage = pageNumber.pageSize;
        this.getcustTicket(this.custData.id, "", pageNumber);
    }


    openStaffDetailModal(id) {
        // $("#staffDetailModal").modal("show");
        this.dialog.open(this.staffDetailDialog, { width: '70vw' });
        const url = "/staffuser/" + id;
        this.savbillCommonBaseService.get(url).subscribe(
            (response: any) => {
                this.staffData = response.Staff;
                //console.log("this.staffData", this.staffData);
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                console.log(error, "error");

            }
        );
    }
    currentPageTicketConfig
    linkTicketItemsPerPage
    pageChangedLinkTicket(pageNumber): void {
        this.currentPageTicketConfig = pageNumber.pageIndex + 1;
        this.linkTicketItemsPerPage = pageNumber.pageSize
        this.getLinkableTickets(this.ticketDeatailData);
    }
    data
    linkTicket(dialogRef) {
        this.data = this.chakedTktData;
        const url = `/case/linkBulkTicket?linkTicketId=${this.ticketIdToLink}`;
        this.ticketManagementService.postMethod(url, this.data).subscribe(
            (response: any) => {
                dialogRef.close()
                this.chakedTktData = [];
                this.selectLinkTicket = false;
                this.toastr.success(`Linked this ticket successfully..`, 'Success!')

            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')

            }
        );
    }
    // openTicketDetail(ticketId): void {
    //     this.viewTicket = false;
    //     this.createTicket = false;
    //     this.detailTicket = true;
    //     this.viewTicketId = ticketId;
    //     this.getTicketById(ticketId);
    //     this.getFollowUpDetailById(ticketId);
    //     this.getCafFollowupList("");
    //     this.ticketETRListShow(ticketId);
    //     this.getTicketTatListShow(ticketId);
    //     this.getworkflowAuditDetails("", ticketId, "CASE");
    //     this.showTATDetailsData();
    // }

    // View flags
    viewTicket: boolean = true;
    createTicket: boolean = false;
    detailTicket: boolean = false;

    // Ticket detail data
    ticketDeatailData: any;
    viewTicketData: any;
    viewTicketId: number;

    // ETR and TAT data
    ticketETRDetailData: any;
    ticketTATDetailData: any;

    // Followup data
    cafFollowupList: any;
    followUpTicketListData: any;

    // Workflow audit data
    workflowAuditDataSource = new MatTableDataSource<any>();


    // Service area
    serviceAreaId: any;
    nameOfService: any;

    // Staff and team data
    staffList: any;
    teamListData: any;


    // TAT Details
    TATDetails: any = [];
    caseInfo: any;

    // Feedback
    feedbackDetails: any;
    rating: any = 0;

    // Parent and child tickets
    parentTicketDetails: any;
    childTicketDetails: any;

    slaCounterAccess: boolean = false;

    MasteritemsPerPage1 = RadiusConstants.ITEMS_PER_PAGE;
    MastertotalRecords1: number;
    dialogRef: MatDialogRef<any>;
    assignTicketDialogRef: MatDialogRef<any>;
    pickTicketDialogRef: MatDialogRef<any>;

    refreshTicketDetails(ticketId: number): void {
        // central place to call all fetchers so re-use is easy and consistent
        this.viewTicketId = ticketId;

        // call all the functions that populate the modal data
        this.getTicketById(ticketId);
        this.getFollowUpDetailById(ticketId);
        this.getCafFollowupList("");
        this.ticketETRListShow(ticketId);
        this.getTicketTatListShow(ticketId);
        this.getworkflowAuditDetails("", ticketId, "CASE");
        this.showTATDetailsData();
    }
    openTicketDetailView(ticketId: number): void {
        this.viewTicket = false;
        this.createTicket = false;
        this.detailTicket = true; this.dialogRef = this.dialog.open(this.detailsDialog, {
            width: '95%',
            maxWidth: '95vw',
            height: '90vh',
            disableClose: true
        }); this.dialogRef.afterOpened().subscribe(() => {
            this.refreshTicketDetails(ticketId);
        }); this.dialogRef.afterClosed().subscribe(() => {
        });
    }
    messageModeETR = [
        { label: "Dynamic", value: true },
        { label: "Static", value: false }
    ];
    submitted = false
    uploadDocuments(dialogRef) {
        this.submitted = true;
        if (this.uploadDocForm.valid) {
            const formData = new FormData();
            let fileArray: FileList;
            if (this.uploadDocForm.controls.file) {
                if (
                    this.selectedFile.type != "image/png" &&
                    this.selectedFile.type != "image/jpg" &&
                    this.selectedFile.type != "image/jpeg" &&
                    this.selectedFile.type != "application/pdf"
                ) {

                    alert("File type must be png, jpg, jpeg or pdf");
                } else {
                    fileArray = this.uploadDocForm.controls.file.value;
                    Array.from(fileArray).forEach(file => {
                        formData.append("file", file);
                    });
                }
            }
            const url = `/case/updateDocumentDetails?caseId=${this.uploadDataTicketId}`;
            this.ticketManagementService.postMethod(url, formData).subscribe(
                (response: any) => {
                    if (response.responseCode === 406) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed')

                    } else if (response.responseCode === 417) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed')


                    } else {
                        dialogRef.close()
                        this.submitted = false;
                        this.toastr.success(`${response.message}`, 'Success!')
                        this.getTicketById(this.uploadDataTicketId)
                        this.uploadDocumentId = false;
                    }
                },
                (error: any) => {
                    console.log(error, "error");
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')

                }
            );
        }
    }

    downloadDoc(filename, docId, ticketId) {
        const url = `/case/document/download/${ticketId}/${docId}`;
        this.ticketManagementService.downloadFile(url).subscribe(blob => {
            importedSaveAs(blob, filename);
        });
    }

    caseUpdateDetails: any;
    @ViewChild('ticketProgressDialog') ticketProgressDialog!: TemplateRef<any>;


    viewProgressDetails(caseUpdate): void {
        this.caseUpdateDetails = caseUpdate.updateDetails;
        // this.caseUpdateDetailsModel = true;
        this.dialog.open(this.ticketProgressDialog, {
            width: '80%',
            disableClose: true
        });
    }

    getTicketById(ticketId: number): void {
        const url = "/case/" + ticketId;
        this.ticketManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.viewTicketData = response.data;
                this.ticketDeatailData = response.data;

                this.feedbackDetails = this.ticketDeatailData?.caseFeedbackRel || [];
                this.nameOfService = this.ticketDeatailData.serviceAreaName;
                this.serviceAreaId = this.ticketDeatailData.serviceAreaId;

                if (this.ticketDeatailData.currentAssigneeId) {
                    this.getTicketCurrentAssigneeData(this.ticketDeatailData.currentAssigneeId);
                }

                this.getStaffbyServiceArea();

                if (this.viewTicketData.ticketReasonCategoryId) {
                    this.getSubCategoryByparentCat(this.viewTicketData.ticketReasonCategoryId);
                }

                if (this.viewTicketData.nextFollowupTime) {
                    const timeRegexResult = /\d+:\d+/gi.exec(this.viewTicketData.nextFollowupTime);
                    if (timeRegexResult && timeRegexResult.length) {
                        const time = timeRegexResult.shift();
                        this.viewTicketData.nextFollowupTime = time;
                    }
                }
            },
            (error: any) => {
                console.log(error);
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    getStaffbyServiceArea() {
        let url = "/case/getAllStaffUserByServiceArea/" + this.serviceAreaId;
        this.ticketManagementService.getMethod(url).subscribe((response: any) => {
            this.staffList = response.dataList;
            //console.log("caseInfo",  this.staffList)
        });
    }
    ticketReasonSubCategoryData: any;

    getSubCategoryByparentCat(id) {
        const url = "/ticketReasonSubCategory/getSubCategoryReasons?parentCategoryId=" + id;
        this.ticketManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.ticketReasonSubCategoryData = response.dataList;
                this.ticketReasonSubCategoryData?.forEach(element => {
    this.TATDetails = [...this.TATDetails, ...element.ticketSubCategoryTatMappingList];
});
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

    assignStaffData: any;
    getTicketCurrentAssigneeData(staffId) {
        const url = "/staffuser/" + staffId;
        this.savbillCommonBaseService.get(url).subscribe(
            (response: any) => {
                this.assignStaffData = response.Staff;
                this.assignStaffParentId = this.assignStaffData.parentStaffId;
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


    ticketETRListShow(caseId: number): void {
        let data = {};
        const url = "/case/getTicketETRReport/" + caseId;
        this.ticketManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                this.ticketETRDetailData = response.dataList;
            }
        );
    }

    getTicketTatListShow(caseId: number): void {
        const url = "/case/getTatAuditDetails?caseId=" + caseId;
        this.ticketManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.ticketTATDetailData = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    getworkflowAuditDetails(workflowId: any, entityId: any, entityType: string): void {
        let url = "/workflowaudit/list?entityId=" + this.viewTicketId + "&eventName=" + entityType;


        const payload = {
            workflowId: workflowId || '',
            entityId,
            entityType,
            page: this.currentPageMasterSlab1,
            pageSize: this.MasteritemsPerPage1
        };

        this.ticketManagementService.postMethod(url, payload).subscribe(
            (response: any) => {
                this.workflowAuditData1 = response.dataList || [];
                this.workflowAuditDataSource = new MatTableDataSource(response.data);
                this.MastertotalRecords1 = response.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    pageChangedMasterList(pageNumber: any): void {
        this.currentPageMasterSlab1 = pageNumber.pageIndex + 1;
        this.MasteritemsPerPage1 = pageNumber.pageSize;
        this.getworkflowAuditDetails("", this.viewTicketId, "CASE");
    }
    TATcaseData: any = [];

    showTATDetailsData(): void {
        const url = "/case/getTatDetials?caseId=" + this.viewTicketId;
        this.ticketManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.caseInfo = response.data;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    openTATModel(): void {
        this.TATcaseData = this.caseInfo;
        this.tatDetailsShowModel = true;
        this.dialog.open(this.tatDetailDialog, {
            width: '80%',
            disableClose: true
        });
    }

    closeTATModel(): void {
        this.tatDetailsShowModel = false;

    }
    rescheduleFollowupRemarks = [
        "Confirm Later",
        "Do Not Call",
        "Expensive Package",
        "Call rejected by Client"
    ];
    cafFollowupDatalength = 0;
    cafFollowupPage = 1;
    cafFollowupItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    followupListTotalRecordsForUserAndTeam: any;
    followupListForUserAndTeam: any;
    showItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    getCafFollowupList(list: any): void {
        let size;
        const page = this.cafFollowupPage;

        if (list) {
            size = list;
            this.cafFollowupItemsPerPage = list;
        } else {
            size = this.cafFollowupItemsPerPage;
        }

        const url = "/ticketFollowUp/findAll?caseId=" +
            this.viewTicketId +
            "&page=" + page +
            "&pageSize=" + size;

        this.ticketManagementService.getMethod(url).subscribe(
            async (response: any) => {
                this.cafFollowupList = await response?.dataList;
                this.followupListTotalRecordsForUserAndTeam = await response?.totalRecords;

                if (this.showItemPerPage > this.cafFollowupItemsPerPage) {
                    this.cafFollowupDatalength = this.cafFollowupList?.length % this.showItemPerPage;
                } else {
                    this.cafFollowupDatalength = this.cafFollowupList?.length % this.cafFollowupItemsPerPage;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    pageChangedCafFollowup(pageNumber: any): void {
        this.cafFollowupPage = pageNumber.pageIndex + 1;
        this.cafFollowupItemsPerPage = pageNumber.pageSize;
        this.getCafFollowupList("");
    }

    totalCafFollowupItems(event: any): void {
        this.showItemPerPage = Number(event.value);
        if (this.cafFollowupPage > 1) {
            this.cafFollowupPage = 1;
        }
        this.getCafFollowupList(this.showItemPerPage);
    }
    previewUrl: any;
    documentPreview: boolean = false;
    ticketFileDocData: any;
    showticketDocData(data: any) {
        const url = `/case/document/download/${data.ticketId}/${data.docId}`;
        const fileType = data?.filename.split(".");
        this.ticketManagementService.downloadFile(url).subscribe(data => {
            let type = "application/octet-stream"; // default type
            const uint = new Uint8Array(data);
            const magic = uint.subarray(0, 4);
            if (magic.every(b => b === 0xff)) {
                type = "image/jpeg";
            } else if (magic[0] === 0x89 && magic[1] === 0x50 && magic[2] === 0x4e && magic[3] === 0x47) {
                type = "image/png";
            } else if (magic[0] === 0x47 && magic[1] === 0x49 && magic[2] === 0x46 && magic[3] === 0x38) {
                type = "image/gif";
            } else if (magic[0] === 0xd0 && magic[1] === 0xcf && magic[2] === 0x11 && magic[3] === 0xe0) {
                type = "application/vnd.ms-excel";
            } else if (magic[0] === 0x25 && magic[1] === 0x50 && magic[2] === 0x44 && magic[3] === 0x46) {
                type = "application/pdf";
            } else if (magic[0] === 0xd0 && magic[1] === 0xcf && magic[2] === 0x11 && magic[3] === 0xe0) {
                type = "application/msword";
            }

            if (fileType[fileType?.length - 1] == "pdf") {
                const blob = new Blob([data], { type: "application/pdf" });
                const blobUrl = URL.createObjectURL(blob);
                window.open(blobUrl, "_blank");
            } else {
                const blob = new Blob([data], { type });
                const blobUrl = URL.createObjectURL(blob);
                this.previewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(blobUrl);
                this.documentPreview = true;
                this.dialog.open(this.documentPreviewDialog, {
                    width: '80%',
                    disableClose: true
                });
            }
        });
    }

    ticketStaffTeamdetails: boolean = false;
    serviceAreaDetail: boolean = false;
    parentDetailsShowModel: boolean = false;
    childDetailsShowModel: boolean = false;
    customerETRDetailData: any[] = [];

    openTeamDetailModel(data) {
        this.ticketETRData = data;
        let staffId;
        if (data.actionByStaffId != null) {
            staffId = data.actionByStaffId;
        } else if (data.staffId != null) {
            staffId = data.staffId;
        }
        this.ticketStaffTeamdetails = true;

        const url = `/ticketFollowupDetails/getAllTeamNameByStaffId/${staffId}`;
        this.ticketManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.customerETRDetailData = response;
                this.dialog.open(this.teamDetailsDialog, {
                    width: '60%',
                    disableClose: true
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
            }
        );
    }

    openParentTicketModel(ticketId: number): void {
        this.dialog.open(this.parentTicketDialog, {
            width: '80%',
            disableClose: true
        });

        this.parentDetailsShowModel = true;

        const url = "/case/" + ticketId;
        this.ticketManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.parentTicketDetails = response.data;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    closeParentTicketModel(): void {
        this.parentDetailsShowModel = false;
        this.childDetailsShowModel = false;
    }

    openChildTicketModel(ticketId: number): void {
        this.childDetailsShowModel = true;
        this.dialog.open(this.childTicketDialog, {
            width: '80%',
            disableClose: true
        });

        const url = "/case/getChildTickets?caseId=" + ticketId;
        this.ticketManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.childTicketDetails = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    closeChildTicketModel(): void {
        this.childDetailsShowModel = false;
    }
    displayedETRColumns: string[] = [
        'custUserName',
        'staffPersonName',
        'caseNumber',
        'messageMode',
        'notificationMode',
        'notificationSentTime',
        'notificationSentDate',
        'notificationStatus'
    ];

    displatattachment: string[] = [
        'filename',
        'docStatus',
        'createdByName',
        'createdate',
        'action'
    ];

    displayedTatColumns: string[] = [
        'tatAction',
        'tatTime',
        'slaTime',
        'tatMessage',
        'caseLevel',
        'notificationFor',
        'isTatBreached',
        'messageMode',
        'messageStatus'
    ];

    displayedworkflowColumns: string[] = [
        'ticketNumber',
        'action',
        'staffName',
        'remark',
        'actionDateTime'
    ];



    ticketRemarkListData: any;
    conversationListData: any;
    getFollowUpDetailById(ticketId): void {
        const url = "/ticketFollowupDetails/getAllByCaseId/" + ticketId;
        this.ticketManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.followUpTicketListData = response.dataList;
                this.ticketRemarkListData = response.dataList.filter(
                    data => data.remarkType === "Internal Remark"
                );
                this.conversationListData = response.dataList.filter(
                    data => data.remarkType === "External Remark"
                );
            },
            (error: any) => {
                // console.log(error, 'error');
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

    onClickServiceArea() {
        this.serviceAreaList = this.staffData.serviceAreasNameList;

        this.dialog.open(this.serviceAreaDialog, { width: '50vw' });
    }
    displayedColumns: string[] = [
        'caseTitle',
        'caseNumber',
        'caseType',
        'assignee',
        'caseStatus',
        'createdate',
        'caseReasonCategory',
        'caseReasonSubCategory',
        'action'
    ];
    @ViewChild('staffDetailDialog') staffDetailDialog!: TemplateRef<any>;
    @ViewChild('serviceAreaDialog') serviceAreaDialog!: TemplateRef<any>;
    @ViewChild('createTicketDialog') createTicketDialog!: TemplateRef<any>;
    @ViewChild('TATDetailsDialog') TATDetailsDialog!: TemplateRef<any>;
    @ViewChild('tatDetailsDialog') tatDetailsDialog!: TemplateRef<any>;
    @ViewChild('tatDetailDialog') tatDetailDialog!: TemplateRef<any>;

    checklog(ticketDeatailData) {
    }
    displayTATDetails = false;
    tatDetailsShow = false;
    viewTrcData
    showTATDetails(data) {
        this.displayTATDetails = true;
        this.dialog.open(this.TATDetailsDialog, {
            width: '80%',
            disableClose: true
        });
        this.ticketReasonSubCategoryData.forEach(element => {
            if (element.id == data) {
                this.TATDetails = element.ticketSubCategoryTatMappingList;
            }
        });
    }
    openRawDataModal(finalData: any) {
        this.viewTrcData = finalData.ticketTatMatrix;
        // this.getRawData(finalData, "");
        this.tatDetailsShow = true;
        this.dialog.open(this.tatDetailsDialog, {
            width: '80%',
            disableClose: true
        });
    }
    closeDocumentPreview() {
        this.documentPreview = false;
    }
    reFollowupFormsubmitted = false
    ifCafFollowupSubmited = false
    followupData
    saveCafFollowup(dialogRef) {
        this.ifCafFollowupSubmited = true;
        if (this.followupScheduleForm.valid) {
            const url = "/ticketFollowUp/save";
            this.followupData = this.followupScheduleForm.value;
            this.followupData.caseId = this.folloupTicketId;
            this.followupData.staffUserId = this.staffid;
            this.followupData.mvnoId = this.mvnoid;
            this.followupData.isSend = false;
            this.followupData.status = "Pending";
            const myFormattedDate = this.datePipe.transform(
                this.followupData.followUpDatetime,
                "yyyy-MM-dd HH:mm:ss"
            );
            this.followupData.followUpDatetime = myFormattedDate;

            const myDate = new Date(this.followupData.followUpTime);
            const finalFormat = this.formatDateTime(myDate);
            const onlyTime = finalFormat.split(" ")[1];
            const onlyDate = this.followupData.followUpDatetime.split(" ")[0];
            const finalDateTime2 = `${onlyDate} ${onlyTime}`;
            this.followupData.followUpDatetime = finalDateTime2;
            this.ticketManagementService.postMethod(url, this.followupData).subscribe(
                (response: any) => {
                    dialogRef.close()
                    this.ifCafFollowupSubmited = false;
                    this.followupScheduleForm.reset();
                    this.getcustTicket(this.customerId, '');
                    this.dialogRef.close();
                    if (this.dialogRef && this.dialogRef.componentInstance) {
                        this.refreshTicketDetails(this.viewTicketId);
                    } else {
                        this.openTicketDetailView(this.viewTicketId);
                    }
                    this.toastr.success(`${response.message}`, 'Success!')
                    this.followupPopupOpen = false;
                    this.scheduleFollowup = false;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')

                }
            );
            this.ifCafFollowupSubmited = false;
        }
    }
    formatDateTime(dateObj: Date) {
        const year = dateObj.getFullYear();
        const month = String(dateObj.getMonth() + 1).padStart(2, '0');
        const day = String(dateObj.getDate()).padStart(2, '0');

        const hours = String(dateObj.getHours()).padStart(2, '0');
        const minutes = String(dateObj.getMinutes()).padStart(2, '0');
        const seconds = String(dateObj.getSeconds()).padStart(2, '0');

        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    }
    closeFolloupPopup() {
        this.scheduleFollowup = false;
        this.followupPopupOpen = false;
    }

    rescheduleFollowUp(followUpDetails) {
        this.followUpId = followUpDetails.id;
        this.followUpCaseNumber = followUpDetails.caseNumber;
        this.generatedNameOfTheReFollowUp(followUpDetails.caseId);
        this.reFollowupFormsubmitted = false;
        this.reScheduleFollowup = true;
        this.dialog.open(this.reScheduleFollowupDialog, {
            width: '80%',
            disableClose: true
        });
    }
    followUpCaseNumber: any;
    followUpId: any;
    generateNameOfReFollowUp: any;

    generatedNameOfTheReFollowUp(customersId) {
        const url = "/ticketFollowUp/generateNameOfTheTicketFollowUp/" + customersId;

        this.ticketManagementService.getMethod(url).subscribe(
            async (response: any) => {
                this.generateNameOfReFollowUp = await response.data;
                this.generateNameOfReFollowUp
                    ? this.reFollowupScheduleForm.controls["followUpName"].setValue(
                        this.generateNameOfReFollowUp
                    )
                    : "";
            },
            async (error: any) => {
                this.toastr.error(`Something went wrong with 'followup name.' Generation`, 'Failed!')

            }
        );
    }
    saveReFollowup(dialogRef) {
        this.followupData = {};
        this.reFollowupFormsubmitted = true;
        if (this.reFollowupScheduleForm.valid) {
            this.followupData = this.reFollowupScheduleForm.value;
            this.followupData.caseId = this.viewTicketId;
            this.followupData.caseNumber = this.followUpCaseNumber;
            this.followupData.staffUserId = this.staffid;
            this.followupData.mvnoId = this.mvnoid;
            this.followupData.isSend = false;
            this.followupData.status = "Pending";
            const myFormattedDate = this.datePipe.transform(
                this.followupData.followUpDatetime,
                "yyyy-MM-dd HH:mm:ss"
            );
            this.followupData.followUpDatetime = myFormattedDate;
            const url =
                "/ticketFollowUp/reScheduleTicketfollowup?followUpId=" +
                this.followUpId +
                "&remarks=" +
                this.followupData.remarksTemp;
            this.ticketManagementService.postMethod(url, this.followupData).subscribe(
                (response: any) => {
                    dialogRef.close()
                    this.reFollowupFormsubmitted = false;
                    this.reFollowupScheduleForm.reset();
                    this.toastr.success(`${response.message}`, 'Success!')

                    this.reScheduleFollowup = false;
                    this.reFollowupFormsubmitted = false;
                    this.getCafFollowupList("");
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')

                }
            );
            this.reFollowupFormsubmitted = false;
        }
    }
    reScheduleFollowup = false
    closeReFolloupPopup() {
        this.reFollowupFormsubmitted = false;
        this.reScheduleFollowup = false;
        this.reFollowupScheduleForm.reset();
    }
    ticketPickModal = false
    closeTicketPickModal() {
        this.ticketPickModal = false;
    }
    pickId: any
    pickModalOpen(data) {
        this.pickId = this.viewTicketId;
        this.pickRemark = "";
        if (data.ticketAssignStaffMappings.length > 0) {
            let show: boolean = false;
            data.ticketAssignStaffMappings.forEach(element => {
                if (element.staffId == this.currentLoginUserId) {
                    show = true;
                    this.ticketPickModal = true;
                    this.pickTicketDialogRef = this.dialog.open(this.pickTicketDialog, {
                        width: '80%',
                        disableClose: true
                    });

                    this.pickTicketDialogRef?.afterClosed()?.subscribe(res => {
                        this.refreshTicketDetails(this.viewTicketId);
                    })
                }
            });
            if (!show) {
                this.toastr.info(`You are not eligible to pick this ticket..`, 'Info!')
            }
        } else {
            this.toastr.info(`You are not eligible to pick this ticket..`, 'Info!')
        }
    }


    pickstaff(dialogRef) {
        const url = `/case/assignPickedTicket?caseId=${this.pickId}&remark=${this.pickRemark}&staffId=${this.currentLoginUserId}`;
        this.ticketManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.getcustTicket(this.customerId, '');
                this.dialogRef.close();
                if (this.dialogRef && this.dialogRef.componentInstance) {
                    this.refreshTicketDetails(this.viewTicketId);
                } else {
                    this.openTicketDetailView(this.viewTicketId);
                }
                this.toastr.success(`Ticket Picked Sucessfully`, 'Success!')

                dialogRef.close()
                this.ticketPickModal = false;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
            }
        );
    }
    counterDetailModel = false
    SlaCounterModelOpen(ticketdata) {
        let url = "/case/" + ticketdata.caseId;
        this.SLATimer(ticketdata);
        this.counterDetailModel = true;

        this.dialog.open(this.slaCounterDialog, {
            width: '80%',
            disableClose: true
        });
        // });
    }
    finaltotalSeconds: any;
    slaTime: any;
    newDate: any;
    slaInSeconds: any;
    SLAData: any;
    ticketRemainTimeSubscription: Subscription;

    SLATimer(ticketdata) {
        this.SLAData = ticketdata.caseSlaTime;
        this.slaTime = moment(ticketdata.createdate, "DD-MM-YYYY hh:mm A").toDate();
        this.newDate = new Date();

        if (ticketdata.caseSlaUnit === "Min") {
            this.slaTime.setMinutes(this.slaTime.getMinutes() + this.SLAData);
        } else if (ticketdata.caseSlaUnit === "Hour") {
            this.slaTime.setHours(this.slaTime.getHours() + this.SLAData);
        } else if (ticketdata.caseSlaUnit === "Day") {
            this.slaTime.setDate(this.slaTime.getDate() + this.SLAData);
        }
        this.slaInSeconds = Math.floor((this.slaTime - this.newDate) / 1000);
        this.timerStamp(this.slaInSeconds);
        this.ticketRemainTimeSubscription = timer(0, 1000).subscribe(e => {
            this.getTimeDiffrence(this.slaInSeconds);
        });
    }

    timerStamp(totalSeconds) {
        if (this.newDate < this.slaTime) {
            // const totalSeconds = (this.slaTime - this.newDate) / 1000;
            const minutes = Math.floor(totalSeconds / 60) % 60;
            const hours = Math.floor(totalSeconds / 3600) % 24;
            const days = Math.floor(totalSeconds / 3600 / 24);
            const seconds = Math.floor(totalSeconds) % 60;
            this.SLAremainTime =
                ("0" + days).slice(-2) +
                ":" +
                ("0" + hours).slice(-2) +
                ":" +
                ("0" + minutes).slice(-2) +
                ":" +
                ("0" + seconds).slice(-2);
        } else {
            this.SLAremainTime = "00:00:00:00";
        }
    }
    getTimeDiffrence(totalSeconds) {
        const newSlaInSeconds = Math.floor((this.slaTime - Date.now()) / 1000);

        if (newSlaInSeconds > 0) {
            this.slaInSeconds = newSlaInSeconds;
        } else {
            this.slaInSeconds = 0;
            this.ticketRemainTimeSubscription.unsubscribe();
        }

        this.timerStamp(this.slaInSeconds);
    }
    obs$ = interval(1000);
    obs2$ = interval(1940);
    tiketTimer() {
        this.ticketRemainTimeSubscription = this.obs$.subscribe(e => {
            if (!this.custTicketList || !Array.isArray(this.custTicketList)) {
            return;
        }
            this.custTicketList.forEach(element => {
                if (!element) return;
                if (element.caseStatus != "Raise and Close") {
                    if (
                        element.currentAssigneeId == null ||
                        (element.currentAssigneeId !== null &&
                            element.caseStatus != "Closed" &&
                            element.caseStatus != "rejected" &&
                            element.caseStatus != "Raise and Close" &&
                            element.caseStatus != "Pending")
                    ) {
                        const newYearsDate: any = new Date(
                            element.nextFollowupDate + " " + element.nextFollowupTime
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

                            element.remainTime = remainTime;
                        } else {
                            element.remainTime = "00:00:00:00";
                        }
                    }
                }
            });
        });
    }
    closeFollowup = false;
    closeFollowupFormsubmitted: boolean = false;
    @ViewChild('closeFollowupDialog') closeFollowupDialog!: TemplateRef<any>;
    closeFollowUp(followUpDetails) {
        this.closeFollowupFormsubmitted = false;
        this.followUpId = followUpDetails.id;
        this.closeFollowup = true;
        this.dialog.open(this.closeFollowupDialog, {
            width: '80%',
            disableClose: true
        });
    }
    saveCloseFollowUp(dialogRef) {
        this.closeFollowupFormsubmitted = true;

        if (this.closeFollowupForm.valid) {
            const url =
                "/ticketFollowUp/closefollowup?followUpId=" +
                this.followUpId +
                "&remarks=" +
                this.closeFollowupForm.get("remarks").value;
            this.ticketManagementService.getMethod(url).subscribe(
                async (response: any) => {
                    this.closeFollowup = false;
                    this.closeFollowupForm.reset();
                    dialogRef.close()
                    this.toastr.success(`${response.responseMessage}`, 'Success!')
                    await this.openTicketDetail(this.viewTicketData.caseId);
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                }
            );
            this.closeFollowupFormsubmitted = false;
        }
    }
    onCloseDialog(dialogRef) {
        this.closeFollowupForm.reset();
        this.remarkFollowupForm.reset();
        dialogRef.close();

    }
    openTicketDetail(ticketId): void {
        this.viewTicket = false;
        this.createTicket = false;
        this.detailTicket = true;
        this.viewTicketId = ticketId;
        this.getTicketById(ticketId);
        this.getFollowUpDetailById(ticketId);
        this.getCafFollowupList("");
        this.ticketETRListShow(ticketId);
        this.getTicketTatListShow(ticketId);
        this.getworkflowAuditDetails("", ticketId, "CASE");
        this.showTATDetailsData();

    }
    followUpDetailsObj: any;
    remarkFollowupFormsubmitted: boolean = false;
    remarkScheduleFollowup: boolean = false;
    @ViewChild('remarkFollowupDialog') remarkFollowupDialog!: TemplateRef<any>;
    remarkFollowUp(followUpDetails) {
        this.followUpDetailsObj = followUpDetails;
        this.remarkFollowupFormsubmitted = false;
        this.followUpId = followUpDetails.id;
        this.getfollowUpRemarkList(this.followUpId);
        this.remarkScheduleFollowup = true;
        this.dialog.open(this.remarkFollowupDialog, {
            width: '50%',
            disableClose: true
        });
    }

    followUpRemarkList: any = [];
    tableWrapperRemarks: any = "";
    scrollIdRemarks: any = "";
    getfollowUpRemarkList(id) {
        this.tableWrapperRemarks = "";
        this.scrollIdRemarks = "";

        const url = "/ticketFollowUp/findAll/ticketFollowUpRemark/" + id;
        this.ticketManagementService.getMethod(url).subscribe(
            async (response: any) => {
                this.followUpRemarkList = await response.dataList;
                if (this.followUpRemarkList && this.followUpRemarkList?.length > 3) {
                    this.tableWrapperRemarks = "table-wrapper";
                    this.scrollIdRemarks = "table-scroll-remark";
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
            }
        );
    }

    saveRemarkFollowUp(dialogRef) {
        this.remarkFollowupFormsubmitted = true;
        this.remarkFollowupForm.get("cafFollowUpId").setValue(this.followUpId);
        if (this.remarkFollowupForm.valid) {
            var data = this.remarkFollowupForm.value;
            data.ticketFollowUpId = this.followUpId;
            data.mvnoId = this.mvnoid;

            const url = "/ticketFollowUp/ticketFollowUp/remark";
            this.ticketManagementService.postMethod(url, data).subscribe(
                async (response: any) => {
                    this.remarkScheduleFollowup = false;
                    this.remarkFollowupForm.reset();
                    await this.openTicketDetail(this.viewTicketData.caseId);
                    dialogRef.close()
                    this.toastr.success(`${response.responseMessage}`, 'Success!')

                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                }
            );
            this.remarkFollowupFormsubmitted = false;
        }
    }
    makeACall() {
        this.toastr.info(`Sorry! Please configure call client first..`, 'Info!')
    }
    unsbuscribe() {
        this.ticketRemainTimeSubscription.unsubscribe();
    }
    ngOnDestroy() {
        if (this.ticketRemainTimeSubscription) {
            this.ticketRemainTimeSubscription.unsubscribe();
        }
    }
    checkFollowUpDatetimeOutDate(obj) {
        if (obj != null && obj != undefined) {
            if (obj.status && obj.status === "Pending") {
                if (obj.followUpDatetime && new Date(obj.followUpDatetime) < new Date()) {
                    return true;
                }
            }
        } else {
            return false;
        }
    }
}
