import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { FormBuilder, Validators, FormGroup, FormControl, FormArray } from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from "ngx-toastr";
import { ConfirmationService } from "primeng/api";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { TicketManagementService } from "src/app/service/ticket-management.service";
import { DatePipe } from "@angular/common";
import { BehaviorSubject } from "rxjs";
import { ActivatedRoute } from "@angular/router";
import { DomSanitizer } from "@angular/platform-browser";
import { saveAs as importedSaveAs } from "file-saver";
import { CustomerdetailsilsService } from "src/app/service/customerdetailsils.service";
import { MatDialog } from "@angular/material/dialog";
import { SharedModule } from "src/app/shared/shared.module";
import { CommondropdownService } from "src/app/service/commondropdown.service";

declare var $: any;
@Component({
  selector: "app-ticketmanagement",
  templateUrl: "./ticketmanagement.component.html",
  styleUrls: ["./ticketmanagement.component.scss"],
  standalone: true,
  imports: [SharedModule],
  providers: [DatePipe]
})
export class TicketmanagementComponent implements OnInit {
  ticketGroupForm: FormGroup;
  assignStaffTicketForm: FormGroup;
  ratingTicketForm: FormGroup;
  followupForm: FormGroup;
  ratingSubmmitted: boolean = false;
  submitted: boolean = false;
  caseForData: any;
  caseTypeData: any;
  caseReasonData: any;
  caseOriginData: any;
  priorityData: any;
  hourArray: any = [];
  createTicketData: any = {
    caseForPartner: "",
    caseFor: "",
    caseOrigin: ""
  };
  customerData: any;
  ticketData: any;
  currentPageTicketConfig = 1;
  ticketConfigitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
  ticketConfigtotalRecords: any;
  viewTicketData: any;
  deletedata: any = {
    CaseId: "",
    caseForPartner: "",
    caseOrigin: "",
    caseReasonId: "",
    caseTitle: "",
    caseType: "",
    customersId: "",
    nextFollowupDate: "",
    nextFollowupTime: "",
    oltName: "",
    portName: "",
    priority: "",
    serviceAreaName: "",
    slotName: "",
    userName: "",
    caseStatus: ""
  };
  isTicketEdit: boolean = false;
  statusData: any;
  listTicket: boolean = true;
  createTicket: boolean = false;
  detailTicket: boolean = false;
  searchCustomerId: any = "";
  customerDetailData: any;
  currentDate: any = new Date();
  custId = new BehaviorSubject({
    custId: ""
  });
  allStaffData: any;
  assignStaffData: any;
  staffsubmmitted: boolean = false;
  assignTicketData: any;
  assignticketId: any;
  assignticketStatus: any;
  finalData: string;
  ticketStatusDetail: any = [];
  uploadDocForm: FormGroup;
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

  ticketDeatailData: any = {
    caseTitle: "",
    customerName: "",
    userName: "",
    serviceAreaName: "",
    oltName: "",
    slotName: "",
    portName: "",
    caseType: "",
    caseReasonId: "",
    priority: "",
    nextFollowupDate: "",
    nextFollowupTime: "",
    caseStatus: ""
  };
  teamListData: any;
  currentLoginUserId: any;
  serviceAreaList: any;
  myCustomerId: string;
  previewUrl: any;
  //............primeNG accordion
  activeticketState: boolean[] = [true, false, false];

  pageITEM = RadiusConstants.ITEMS_PER_PAGE;
  pageLimitOptions = RadiusConstants.pageLimitOptions;
  showItemPerPage = 1;
  searchkey: string;
  totalDataListLength = 0;
  ratingTicketId: any;
  folloupTicketId: any = "";
  folloupCustId: any = "";
  folloupTicketassignStaffId: any = "";
  followUpTicketListData: any = [];
  followupSubmmitted: boolean = false;
  viewRating = false;
  ticketAssignToOption = [
    { label: "Team", value: "TEAM" },
    { label: "Staff", value: "STAFF" }
  ];
  resolutionReasonData: any;
  customername: any;
  ticketReasonSubCategoryData: any = [];
  groupReasonData: any;
  rootCauseReasonData: any[];
  customerServiceData: any;
  filteredReasonCategoryList: any;
  TATDetails: any;
  uploadDataTicketId: number;
  selectedFile: any;
  selectedFilePreview: File[] = [];
  selectedFileUploadPreview: File[] = [];
  feedbackDetails: any = [];
  serialNumbers: any;
  constructor(
    private fb: FormBuilder,
    private spinner: NgxSpinnerService,
    private toastr: ToastrService,
    private confirmationService: ConfirmationService,
    // private messageService: MessageService,
    private ticketManagementService: TicketManagementService,
    public datepipe: DatePipe,
    private sanitizer: DomSanitizer,
    private route: ActivatedRoute,
    public commondropdownService: CommondropdownService,
    private dialog: MatDialog,
    private customerdetailsilsService: CustomerdetailsilsService
  ) {}
  ngOnInit(): void {
    this.getCaseStatus();
    this.ticketGroupForm = this.fb.group({
      caseTitle: ["", Validators.required],
      customersId: ["", Validators.required],
      userName: ["", Validators.required],
      serviceAreaName: ["", Validators.required],
      caseType: ["", Validators.required],
      ticketReasonCategoryId: ["", Validators.required],
      serialNumber: [""],
      reasonSubCategoryId: ["", Validators.required],
      //groupReasonId: ['', Validators.required],
      priority: ["", Validators.required],
      nextFollowupDate: [""],
      nextFollowupTime: [""],
      caseStatus: ["Unassigned"],
      currentAssigneeId: [""],

      customerAdditionalMobileNumber: [""],
      customerAdditionalEmail: ["", Validators.email],
      // resolutionId: ['', Validators.required],
      firstRemark: [""],
      //rootCauseReasonId: [''],
      file: [""],
      ticketServicemappingList: ["", Validators.required]
      // problemDomains: [""],
      // subProblemDomain: [""]
    });
    this.assignStaffTicketForm = this.fb.group({
      assignee: ["", Validators.required],
      remark: ["", Validators.required],
      status: ["", Validators.required]
      //fileName: [""],
    });

    this.ratingTicketForm = this.fb.group({
      customerFeedback: ["", Validators.required],
      rating: [, Validators.required]
    });
    this.followupForm = this.fb.group({
      remark: ["", Validators.required]
    });
    this.uploadDocForm = this.fb.group({
      file: [""]
    });
    this.assignStaffTicketForm.controls.assignee.disable();
    this.assignStaffTicketForm.controls.status.disable();
    this.ticketGroupForm.controls.userName.disable();
    this.ticketGroupForm.controls.serviceAreaName.disable();
    this.ticketGroupForm.controls.nextFollowupDate.disable();
    this.ticketGroupForm.controls.nextFollowupTime.disable();
    this.ticketGroupForm.controls.caseStatus.disable();
    this.currentDate = this.datepipe.transform(this.currentDate, "yyyy-MM-dd");
    //this.getGroupReasonBySubCat("");

    // this.getserviceAreaList();
    this.hourSequence();
    this.getCaseFor();
    // this.getCaseType();
    // this.getCaseReason()
    this.getCaseOrigin();
    this.getPriority();

    this.myCustomerId = this.commondropdownService.getUserId();
    // this.myCustomerId = localStorage.getItem("userId");

    this.getservicesByCustomer(this.myCustomerId);
    this.getTicket("");
  }

  getTicket(custId) {
    const url = "/getCasesByCustomer/" + this.myCustomerId;
    const payload = {
      page: this.currentPageTicketConfig,
      pageSize: this.ticketConfigitemsPerPage
    };

    this.ticketManagementService.getCutomerTicketData(url, payload).subscribe(
      (response: any) => {
        this.ticketData = response.dataList;
        this.ticketConfigtotalRecords = response.totalRecords;
        this.currentPageTicketConfig = response.currentPageNumber;
      },
      (error: any) => {
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
      }
    );
  }

  pageChangedTicketConfig(pageNumber) {
    this.currentPageTicketConfig = pageNumber.pageIndex + 1;
    this.ticketConfigitemsPerPage = pageNumber.pageSize;
    this.getTicket(this.myCustomerId);
  }

  getserviceAreaList() {
    this.spinner.show();
    const url = "/serviceArea/all";
    this.customerdetailsilsService.commonGetMethod(url).subscribe(
      (response: any) => {
        this.serviceAreaList = response.dataList;
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  addEditTicket(ticketId, dialogRef) {
    this.submitted = true;
    this.ticketGroupForm.controls.caseType.setValue("Issue");
    if (this.ticketGroupForm.valid) {
      this.spinner.show();
      this.createTicketData = this.ticketGroupForm.value;
      var serialNo = [];
      const formData = new FormData();
      this.ticketGroupForm.value.priority = "Low";
      this.ticketGroupForm.value.customersId = Number(this.myCustomerId);
      this.createTicketData = this.ticketGroupForm.getRawValue();
      this.createTicketData.ticketServicemappingList = [];
      this.ticketGroupForm.controls.ticketServicemappingList.value.forEach(serviceId => {
        this.createTicketData.ticketServicemappingList.push({
          serviceid: serviceId
        });
      });
      this.createTicketData.caseForPartner = "Customer";
      this.createTicketData.caseFor = "Customer";
      this.createTicketData.caseOrigin = "Phone";
      this.createTicketData.department = "Technical";
      this.createTicketData.firstRemark = this.ticketGroupForm.value.firstRemark;
      //this.createTicketData.subCategoryName= this.ticketReasonSubCategoryData[0].subCategoryName;
      // this.createTicketData.reasonSubCategoryId =
      //   this.ticketReasonSubCategoryData[0].id;
      // this.createTicketData.createdFrom = null;
      //console.log(' this.createTicketData', this.createTicketData)

      this.createTicketData.source = null;

      this.createTicketData.finalResolutionId = null;

      this.createTicketData.serialNumber = null;
      this.responseTimetSet(this.createTicketData.reasonSubCategoryId);
      // const files: any = this.ticketGroupForm.controls.file;

      // if (files && files.length > 0) {
      //   for (let i = 0; i < files.length; i++) {
      //     formData.append("file", files[i].value); // append as binary
      //   }
      // }

      let fileArray: FileList;
      if (this.createTicketData.file) {
        //fileArray = this.createTicketData.file;
        fileArray = this.ticketGroupForm.controls.file.value;
        Array.from(fileArray).forEach(file => {
          formData.append("file", file);
        });
      }

      this.createTicketData.file = "";
      this.createTicketData.groupReasonId = null;
      this.createTicketData.helperName = null;
      this.createTicketData.source = null;
      this.createTicketData.subSource = null;
      this.createTicketData.serialNumber = this.ticketGroupForm.value.serialNumber
        ? this.ticketGroupForm.value.serialNumber.toString()
        : "";
      let newFormData = Object.assign({}, this.createTicketData);

      formData.append("entityDTO", JSON.stringify(newFormData));
      console.log("formData", formData);
      const url = "/case/save";
      this.ticketManagementService.postMethod(url, formData).subscribe(
        (response: any) => {
          if (response.responseCode == 406) {
            this.toastr.error(`${response.responseMessage}`, "Failed!");
            this.spinner.hide();
          } else {
            dialogRef.close();
            this.ticketGroupForm.reset();
            this.ticketGroupForm.controls.caseStatus.setValue("Unassigned");
            this.getTicket("");
            this.searchTicketFun();
            this.toastr.success(`Ticket created successfully.`, "Success!");
            this.submitted = false;
            this.spinner.hide();
          }
        },
        (error: any) => {
          console.log(error, "error");

          this.toastr.error(`${error.error.ERROR}`, "Failed!");

          this.spinner.hide();
        }
      );
    }
  }

  getCaseFor() {
    this.spinner.show();
    const url = "/commonList/caseFor";
    this.customerdetailsilsService.getMethod(url).subscribe(
      (response: any) => {
        this.caseForData = response.dataList;
        console.log("this.caseForData", this.caseForData);
        this.spinner.hide();
      },
      (error: any) => {
        console.log(error, "error");
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  selCustomer(custId): void {
    this.getCustomersDetail(custId);
    this.getReasonCategoryByCustomer(custId);
  }

  selReasonCategory(event) {
    console.log("event", event.value);
    this.getSubCategoryByparentCat(event.value);
  }

  selReasonSubCategory(event) {
    console.log("event", event.value);
    this.getGroupReasonBySubCat(event.value);
  }
  getSubProblems(domainName: string) {
    const found = this.problemDomains.find(
      item => this.normalizeText(item.name) === this.normalizeText(domainName)
    );

    return found ? found.sub : [];
  }

  getSubCategoryByparentCat(id) {
    this.spinner.show();
    this.ticketReasonSubCategoryData = [];
    let subProblems: any;
    let problemdomianName: any;
    let problemdomian = this.ticketReasonCategoryData.filter(
      (e: any) => e.id === this.ticketGroupForm.value.ticketReasonCategoryId
    );
    if (problemdomian.length > 0) {
      problemdomianName = problemdomian[0].categoryName;
      subProblems = this.getSubProblems(problemdomianName);
    }
    const url = "/ticketReasonSubCategory/getSubCategoryReasons?parentCategoryId=" + id;
    this.ticketManagementService.getMethod(url).subscribe(
      (response: any) => {
        let data = response.dataList;
        if (response.dataList.length > 0) {
          subProblems.forEach(e1 => {
            data.forEach(e2 => {
              if (this.normalizeText(e1) === this.normalizeText(e2.subCategoryName)) {
                this.ticketReasonSubCategoryData.push(e2);
              }
            });
          });
        }

        // this.ticketReasonSubCategoryData = response.dataList;
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  onFileChange(event: any) {
    
    const files: FileList = event.target.files;
    this.selectedFilePreview = [];

    if (!files || files.length === 0) {
      return;
    }

    // Validate all files
    for (let i = 0; i < files.length; i++) {
      const file = files[i];

      if (
        file.type !== "image/png" &&
        file.type !== "image/jpg" &&
        file.type !== "image/jpeg" &&
        file.type !== "application/pdf"
      ) {
        this.ticketGroupForm.controls.file.reset();
        this.selectedFilePreview = [];
        alert("File type must be PNG, JPG, JPEG or PDF");
        return;
      } else {
        const file = event.target.files;
        this.ticketGroupForm.patchValue({
          file: file
        });
      }

      this.selectedFilePreview.push(file); // Add to preview list
    }
  }

  deletSelectedFile(event: any) {
    var temp: File[] = this.selectedFilePreview?.filter((item: File) => item?.name != event);
    this.selectedFilePreview = temp;
    this.ticketGroupForm.patchValue({
      file: temp
    });
  }
  // onFileChange(event) {
  //   if (event.length > 0) {
  //     const file = event;
  //     this.ticketGroupForm.patchValue({
  //       file: file
  //     });
  //     // this.custmerDoc.filename = "";
  //   }
  // }
  getGroupReasonBySubCat(id): void {
    const selSubCatData = this.ticketReasonSubCategoryData.filter(subCat => subCat.id === id);
    this.groupReasonData = selSubCatData[0].ticketSubCategoryGroupReasonMappingList;
    // console.log('this.groupReasonData', this.groupReasonData);
  }

  getReasonCategoryByCustomer(id) {
    this.spinner.show();
    const url = "/ticketReasonCategory/getReasonCategoryByCustomer?customerId=" + id;
    this.ticketManagementService.getMethod(url).subscribe(
      (response: any) => {
        this.ticketReasonCategoryData = response.dataList;
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }
  getCaseStatus() {
    this.spinner.show();
    const url = "/commonList/caseStatus";
    this.customerdetailsilsService.getMethod(url).subscribe(
      (response: any) => {
        this.statusData = response.dataList;
        console.log("this.statusData", this.statusData);
        this.spinner.hide();
      },
      (error: any) => {
        console.log(error, "error");
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  // getCaseType() {
  //   this.spinner.show();
  //   const url = "/commonList/caseType";
  //   this.customerdetailsilsService.getMethod(url).subscribe(
  //     (response: any) => {
  //       this.caseTypeData = response.dataList;
  //       console.log("this.caseTypeData", this.caseTypeData);
  //       this.spinner.hide();
  //     },
  //     (error: any) => {
  //       console.log(error, "error");
  //       this.messageService.add({
  //         severity: "error",
  //         summary: "Error",
  //         detail: error.error.ERROR,
  //         icon: "far fa-times-circle",
  //       });
  //       this.spinner.hide();
  //     }
  //   );
  // }

  getCaseReason() {
    this.spinner.show();
    const url = "/caseReason/all";
    this.ticketManagementService.getMethod(url).subscribe(
      (response: any) => {
        this.caseReasonData = response.dataList;
        console.log("this.caseReasonData", this.caseReasonData);
        this.spinner.hide();
      },
      (error: any) => {
        console.log(error, "error");
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getCaseOrigin() {
    this.spinner.show();
    const url = "/commonList/origin";
    this.customerdetailsilsService.getMethod(url).subscribe(
      (response: any) => {
        this.caseOriginData = response.dataList;
        console.log("this.caseOriginData", this.caseOriginData);
        this.spinner.hide();
      },
      (error: any) => {
        console.log(error, "error");
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getPriority() {
    this.spinner.show();
    const url = "/commonList/priority";
    this.customerdetailsilsService.getMethod(url).subscribe(
      (response: any) => {
        this.priorityData = response.dataList;
        console.log("this.priorityData", this.priorityData);
        this.spinner.hide();
      },
      (error: any) => {
        console.log(error, "error");
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getTicketById(ticketId) {
    this.spinner.show();
    const url = "/case/" + ticketId;
    this.ticketManagementService.getMethod(url).subscribe(
      (response: any) => {
        this.viewTicketData = response.data;
        this.ticketDeatailData = response.data;
        this.feedbackDetails = this.ticketDeatailData.caseFeedbackRel;
        this.viewRating = this.feedbackDetails.some(item => item.overall_rating !== null);
        this.deletedata = this.viewTicketData;

        this.spinner.hide();
      },
      (error: any) => {
        console.log(error, "error");
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  editTicket(ticketId) {
    if (ticketId) {
      this.createTicketFun();
      this.isTicketEdit = true;
      this.ticketGroupForm.controls.caseStatus.enable();
      this.getTicketById(ticketId);
      setTimeout(() => {
        this.ticketGroupForm.patchValue(this.viewTicketData);
        this.dialog.open(this.addEditDialog, {
          width: "80%",
          disableClose: true
        });
      }, 1000);
    }
  }

  deleteConfirmonTicket(ticketId: number) {
    this.getTicketById(ticketId);
    if (ticketId) {
      this.confirmationService.confirm({
        message: "Do you want to delete this Ticket?",
        header: "Delete Confirmation",
        icon: "pi pi-info-circle",
        accept: () => {
          this.deleteTicket(ticketId);
        },
        reject: () => {
          this.toastr.error(`You have rejected`, "Failed!");
        }
      });
    }
  }

  deleteTicket(ticketId) {
    this.spinner.show();
    const url = "/case/delete";
    //this.deletedata.pincodeId = pincodeId;
    // console.log("this.createQosPolicyData", this.deletedata);
    this.ticketManagementService.postMethod(url, this.deletedata).subscribe(
      (response: any) => {
        if (this.currentPageTicketConfig != 1 && this.totalDataListLength == 1) {
          this.currentPageTicketConfig = this.currentPageTicketConfig - 1;
        }
        this.toastr.success(`${response.responseMessage}`, "Success!");
        this.getTicket("");
        this.spinner.hide();
      },
      (error: any) => {
        console.log(error, "error");

        this.toastr.error(`${error.responseMessage}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  createTicketFun() {
    this.listTicket = false;
    this.createTicket = true;
    this.submitted = false;
    this.isTicketEdit = false;
    this.ticketGroupForm.reset();
    this.detailTicket = false;
    this.selectedFileUploadPreview = [];
    this.ticketGroupForm.controls.caseStatus.setValue("Open");
    this.ticketGroupForm.controls.caseStatus.disable();
    this.ticketGroupForm.controls.customersId.setValue(Number(this.myCustomerId));
    this.ticketGroupForm.controls.priority.setValue("Medium");
    //this.ticketGroupForm.controls.rootCauseReasonId.disable();
    this.ticketGroupForm.controls.priority.disable();
    this.selPriority();
    this.getResolutionReasons("Open");
    this.selCustomer(this.myCustomerId);

    if (this.customerServiceData.length > 0) {
      this.ticketGroupForm.patchValue({
        ticketServicemappingList: [this.customerServiceData[0].id]
      });
      this.onSelectService("");
      // this.getTicketReasonCategory(this.ticketGroupForm.value.ticketServicemappingList);
    }
    this.ticketGroupForm.patchValue({
      priority: "Low"
    });
    this.dialog.open(this.addEditDialog, {
      width: "80%",
      disableClose: true
    });
  }

  searchTicketFun() {
    this.listTicket = true;
    this.createTicket = false;
    this.detailTicket = false;
  }
  getResolutionReasons(value: string): void {
    if (value === "Resolved") {
      this.spinner.show();
      // this.ticketGroupForm.controls.resolutionId.enable();
      const url = "/resolutionReasons/all";
      this.ticketManagementService.getMethod(url).subscribe(
        (response: any) => {
          this.resolutionReasonData = response.dataList;
          // console.log("this.resolutionReasonData", this.resolutionReasonData);
          this.spinner.hide();
        },
        (error: any) => {
          console.log(error, "error");
          this.toastr.error(`${error.error.ERROR}`, "Failed!");
          this.spinner.hide();
        }
      );
    } else {
      // this.ticketGroupForm.controls.resolutionId.disable();
    }
  }

  getCustomersDetail(custId) {
    const url = "/customers/" + custId;
    this.customerdetailsilsService.getMethod(url).subscribe(
      (response: any) => {
        this.customerDetailData = response.customers;
        console.log("this.customerDetailData", this.customerDetailData);

        this.ticketGroupForm.patchValue({
          priority: "Medium",
          caseStatus: "Open",
          customersId: Number(custId),
          userName: this.customerDetailData.username,
          serviceAreaName: this.customerDetailData.serviceareaName
        });
        this.ticketGroupForm.controls.serviceAreaName.setValue(
          this.customerDetailData.serviceareaName
        );
        this.ticketGroupForm.controls.userName.setValue(this.customerDetailData.username);
        console.log("this.customerLedgerDetailData", this.customerDetailData);
        this.spinner.hide();
      },
      (error: any) => {
        console.log(error, "error");
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  selPriority() {
    let priority = "Medium";
    const date = new Date();
    if (priority === "High") {
      date.setDate(date.getDate());
    } else if (priority == "Medium") {
      date.setDate(date.getDate());
    } else if (priority == "Low") {
      date.setDate(date.getDate());
    }
    const follwDate = this.datepipe.transform(date, "yyyy-MM-dd");
    const follwTime = this.datepipe.transform(date, "hh:mm:ss");
    this.ticketGroupForm.controls.nextFollowupDate.setValue(follwDate);
    this.ticketGroupForm.controls.nextFollowupTime.setValue(follwTime);
  }

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

  openModal(id, custId) {
    // this.customerDetailsService.show(id);
    // this.custId.next({
    //   custId: custId,
    // });
  }

  openStaffDetailModal(staffId) {
    $("#staffDetailModal").modal("show");
    this.spinner.show();
    const url = "/staffuser/" + staffId;
    this.customerdetailsilsService.getMethod(url).subscribe(
      (response: any) => {
        this.staffData = response.Staff;
        console.log("this.staffData", this.staffData);
        this.spinner.hide();
      },
      (error: any) => {
        console.log(error, "error");
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  assignTicket(ticketId, serviceAreaId, ticketStatus) {
    this.assignStaffTicketForm.reset();
    this.staffsubmmitted = false;
    this.assignStaffTicketForm.controls.assignee.disable();
    this.getTeamList();
    this.getStaff(serviceAreaId);
    this.assignticketId = ticketId;
    this.assignticketStatus = ticketStatus;
    if (ticketStatus == "Unassigned") {
      this.assignStaffTicketForm.controls.assignee.enable();
    }
    if (ticketStatus == "Assigned") {
      this.assignStaffTicketForm.controls.status.enable();
      this.ticketStatusDetail = [
        {
          name: "In Progress",
          value: "In Progress"
        },
        {
          name: "Closed",
          value: "Closed"
        },
        {
          name: "Reassigned",
          value: "Assigned"
        }
      ];
    } else if (ticketStatus == "In Progress") {
      this.assignStaffTicketForm.controls.status.enable();
      this.ticketStatusDetail = [
        {
          name: "Closed",
          value: "Closed"
        },
        {
          name: "Reassigned",
          value: "Assigned"
        }
      ];
    }
    $("#assignTicketModal").modal("show");
  }

  getTicketStatus(event) {
    const selTicketStatus = event.target.value;
    if (selTicketStatus == "Assigned") {
      this.assignStaffTicketForm.controls.assignee.enable();
    } else {
      this.assignStaffTicketForm.controls.assignee.disable();
    }
  }

  getTeamList() {
    const url = "/teams/all";
    this.ticketManagementService.getMethod(url).subscribe(
      (response: any) => {
        this.teamListData = response.dataList;
        console.log(this.teamListData);
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  getStaff(serviceAreaId) {
    console.log("serviceAreaId", serviceAreaId);
    // this.spinner.show();
    const url = "/staffuser/allActive";
    this.ticketManagementService.getMethod(url).subscribe(
      (response: any) => {
        this.allStaffData = response.staffUserlist;
        this.spinner.hide();
        console.log("this.allStaffData", this.allStaffData);
        this.allStaffData = response.staffUserlist.filter(
          staff => staff.serviceAreaId == serviceAreaId
        );
        this.assignStaffData = this.allStaffData;
        console.log("this.assignStaffData", this.assignStaffData);
      },
      (error: any) => {
        this.toastr.error(`${error.error.errorMessage}`, "Failed!");
        // this.spinner.hide();
      }
    );
  }

  hourSequence() {
    for (let i = 0; i < 24; i++) {
      this.hourArray.push(i + 1);
    }
  }

  assignStaffTicket() {
    this.staffsubmmitted = true;
    if (this.assignStaffTicketForm.valid) {
      this.spinner.show();
      this.assignTicketData = this.assignStaffTicketForm.value;
      if (this.assignticketStatus == "Unassigned") {
        this.assignTicketData.status = "Assigned";
      }
      //this.assignTicketData.status = this.assignticketStatus;
      this.assignTicketData.ticketId = this.assignticketId;
      const formData = new FormData();
      formData.append("caseUpdate", JSON.stringify(this.assignTicketData));
      console.log("this.assignTicketData", formData);
      //return;
      const url = "/case/updateDetails";
      this.ticketManagementService.assignMethod(url, formData).subscribe(
        (response: any) => {
          this.assignStaffTicketForm.reset();
          $("#assignTicketModal").modal("hide");
          this.getTicket("");
          this.toastr.success(`${response.responseMessage}`, "Success!");
          this.staffsubmmitted = false;
          this.spinner.hide();
        },
        (error: any) => {
          console.log(error, "error");
          this.toastr.error(`${error}`, "Failed!");
          this.spinner.hide();
        }
      );
    }
  }

  openTicketDetail(ticketId) {
    this.spinner.show();
    this.viewRating = false;
    this.listTicket = false;
    this.createTicket = false;
    this.detailTicket = true;
    this.getTicketById(ticketId);
    this.getFollowUpDetailById(ticketId);

    if (this.customerServiceData.length > 0) {
      this.ticketGroupForm.patchValue({
        ticketServicemappingList: [this.customerServiceData[0].id]
      });
      this.onSelectService("");
    }
    setTimeout(() => {
      this.dialog.open(this.detailsDialog, {
        width: "80%",
        disableClose: true
      });
    }, 1000);
  }

  minuteSequence(n: number): Array<number> {
    return Array(n);
  }

  //............primeNG accordion
  onTabClose(event) {
    this.toastr.error("Index: " + event.index, "Failed!");
  }
  onTabOpen(event) {
    this.toastr.error("Index: " + event.index, "Failed!");
    console.log(event.originalEvent);
  }
  toggle(index: number) {
    this.activeticketState[index] = !this.activeticketState[index];
  }

  ratingTicketModalOpen(id, rating, customerFeedback) {
    // $("#ratingTicketModal").modal("show");
    this.ratingTicketId = id;
    this.ratingTicketForm.reset();
    if (rating != null) {
      this.ratingTicketForm.controls.rating.patchValue(rating);
    }
    if (customerFeedback != null) {
      this.ratingTicketForm.controls.customerFeedback.patchValue(customerFeedback);
    }
    this.dialog.open(this.ratingTicketDialog, {
      width: "80%",
      disableClose: true
    });
  }

  ratingTicket() {
    this.ratingSubmmitted = true;
    const data = {
      overall_rating: this.ratingTicketForm.controls.rating.value,
      general_remarks: this.ratingTicketForm.controls.customerFeedback.value,
      ticketid: this.ratingTicketId
    };
    if (data.overall_rating != null) {
      if (this.ratingTicketForm.valid) {
        this.spinner.show();
        const url = "/case/rating";
        this.ticketManagementService.postMethod(url, data).subscribe(
          (response: any) => {
            this.ratingSubmmitted = false;
            $("#ratingTicketModal").modal("hide");
            this.getTicket("");
            this.spinner.hide();
            this.toastr.success(`${response.message}`, "Success!");
          },
          (error: any) => {
            console.log(error, "error");
            this.toastr.error(`${error.error.ERROR}`, "Failed!");
            this.spinner.hide();
          }
        );
      }
    } else {
      this.toastr.error(`Rating is required`, "Failed!");
    }
  }

  onSelectService(serviceLists: any) {
    let serviceIdList = this.ticketGroupForm.controls.ticketServicemappingList.value;
    if (serviceIdList != null) {
      this.getTicketReasonCategory(serviceIdList);
      console.log("getSerialNumbers ::: ");

      //  this.getSerialNumbers(serviceIdList);
    }
  }

  getSerialNumbers(serviceIdsList) {
    this.spinner.show();
    let serviceIdList = this.ticketGroupForm.controls.ticketServicemappingList.value;
    console.log("this.ticketGroupForm ::: ", this.ticketGroupForm);
    console.log("Called getSerialNumbers ::: ", serviceIdList);
    const url =
      "/subscriber/getSerialNumber?custId=" +
      this.ticketGroupForm.controls.customersId.value +
      "&serviceIds=" +
      serviceIdList;
    console.log("Called getSerialNumbers ::: ", url);
    this.ticketManagementService.getMethod(url).subscribe(
      (response: any) => {
        this.serialNumbers = response.dataList;
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }
  followupTicketModalOpen(ticketId, custId, staffId) {
    $("#followUpModal").modal("show");
    this.followupForm.reset();
    this.folloupTicketId = ticketId;
    this.folloupCustId = custId;
    this.folloupTicketassignStaffId = staffId;
  }

  getFollowUpDetailById(ticketId) {
    this.spinner.show();
    const url = "/ticketFollowupDetails/getAllByCaseId/" + ticketId;
    this.ticketManagementService.getMethod(url).subscribe(
      (response: any) => {
        this.followUpTicketListData = response.dataList.filter(
          data => data.remarkType === "External Remark"
        );
        // this.spinner.hide();
      },
      (error: any) => {
        console.log(error, "error");
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }

  followupTicket() {
    this.followupSubmmitted = true;
    if (this.followupForm.valid) {
      this.spinner.show();
      const data = {
        remark: this.followupForm.controls.remark.value,
        custId: this.folloupCustId,
        caseId: this.folloupTicketId,
        remarkDate: this.datepipe.transform(new Date(), "YYYY-MM-dd"),
        staffId: this.folloupTicketassignStaffId
      };
      console.log(" this.createTicketFollowupData", data);
      const url = "/ticketFollowupDetails/save";
      this.ticketManagementService.postMethod(url, data).subscribe(
        (response: any) => {
          this.followupSubmmitted = false;
          $("#followUpModal").modal("hide");
          this.spinner.hide();
          this.toastr.success(`${response.responseMessage}`, "Success!");
        },
        (error: any) => {
          console.log(error, "error");
          this.toastr.error(`${error.error.ERROR}`, "Failed!");
          this.spinner.hide();
        }
      );
    }
  }

  getResolutionRootCause(value: string): void {
    this.spinner.show();
    this.rootCauseReasonData = [];
    this.ticketGroupForm.controls.rootCauseReasonId.enable();
    // this.chnageStatusForm.controls.rootCauseReasonId.enable();
    this.resolutionReasonData.forEach(e => {
      if (e.id === value) {
        e.rootCauseResolutionMappingList.forEach(f => this.rootCauseReasonData.push(f));
        // this.rootCauseReasonData.push(e.rootCauseResolutionMappingList);
      }
    });
    this.spinner.hide();
  }

  getservicesByCustomer(id) {
    this.spinner.show();
    const url = "/ticketReasonCategory/getActiveServiceForSubscribers?customerId=" + id;
    this.customerdetailsilsService.getMethod(url).subscribe(
      (response: any) => {
        this.customerServiceData = response.dataList;
        // this.filteredReasonCategoryList = this.ticketReasonCategoryData;
        this.spinner.hide();
      },
      (error: any) => {
        this.toastr.error(`${error.error.ERROR}`, "Failed!");
        this.spinner.hide();
      }
    );
  }
  problemDomain_subprobles: any[] = [
    { name: "Internet" },
    { name: "Appointments" },
    { name: "Billing Issues" },
    { name: "Billing Issue" }
  ];
  ticketReasonCategoryData: any[] = [];
  selectproblemDomain: any[] = [];

  getTicketReasonCategory(serviceLists: any) {
    // Ensure arrays are initialized
    this.ticketReasonCategoryData = this.ticketReasonCategoryData || [];
    this.selectproblemDomain = this.selectproblemDomain || [];

    serviceLists = this.ticketGroupForm.controls.ticketServicemappingList.value;

    if (serviceLists != null) {
      this.spinner.show();
      const url = "/ticketReasonCategory/getReasonCategoryByActiveServices";

      this.ticketManagementService.postMethod(url, serviceLists).subscribe(
        (response: any) => {
          const data = response?.dataList || [];

          // NULL safety and type safety
          if (data.length > 0 && Array.isArray(this.problemDomain_subprobles)) {
            this.problemDomain_subprobles.forEach(e1 => {
              data.forEach(e2 => {
                if (
                  e1?.name &&
                  e2?.categoryName &&
                  this.normalizeText(e1.name) === this.normalizeText(e2.categoryName)
                ) {
                  // Initialize arrays if null just before pushing
                  if (!this.ticketReasonCategoryData) this.ticketReasonCategoryData = [];
                  if (!this.selectproblemDomain) this.selectproblemDomain = [];
                  const exists = this.ticketReasonCategoryData.some(
                    (item: any) => item.categoryName === e2.categoryName
                  );

                  if (!exists) {
                    this.ticketReasonCategoryData.push(e2);

                    this.selectproblemDomain.push(e1.name);
                  }
                }
              });
            });
          }

          this.filteredReasonCategoryList = data;

          this.spinner.hide();
        },
        (error: any) => {
          this.toastr.error(`${error.error.ERROR}`, "Failed!");
          this.spinner.hide();
        }
      );
    }
  }

  // getTicketReasonCategory(serviceLists: any) {
  //   this.ticketReasonCategoryData = [];
  //   this.selectproblemDomain = [];
  //   serviceLists = this.ticketGroupForm.controls.ticketServicemappingList.value;
  //   if (serviceLists != null) {
  //     this.spinner.show();
  //     const url = "/ticketReasonCategory/getReasonCategoryByActiveServices";
  //     this.ticketManagementService.postMethod(url, serviceLists).subscribe(
  //       (response: any) => {
  //         let data = response.dataList;
  //         if (response.dataList.length > 0) {
  //           this.problemDomain_subprobles.forEach(e1 => {
  //             data.forEach(e2 => {
  //               if (e1 == e2.categoryName) {
  //                 this.ticketReasonCategoryData.push({
  //                   categoryName: e2.categoryName,
  //                   id: e2.id
  //                 });
  //               }
  //             });
  //           });
  //         }
  //         // this.ticketReasonCategoryData = response.dataList;

  //         this.filteredReasonCategoryList = response.dataList;

  //         this.spinner.hide();
  //       },
  //       (error: any) => {
  //         this.toastr.error(`${error.error.ERROR}`, "Failed!");
  //         this.spinner.hide();
  //       }
  //     );
  //   }
  // }
  showTATDetails(data) {
    this.spinner.show();
    $("#tatDetails").modal("show");
    this.ticketReasonSubCategoryData.forEach(element => {
      if (element.id == data) {
        this.TATDetails = element.ticketSubCategoryTatMappingList;
      }
    });

    this.spinner.hide();
  }

  responseTimetSet(id: any) {
    const date = new Date();
    console.log(this.ticketReasonSubCategoryData);
    this.TATDetails = this.ticketReasonSubCategoryData.find(
      element => element.id == id
    ).ticketSubCategoryTatMappingList;
    const ticket = this.TATDetails.find(
      element => element.ticketReasonSubCategoryId == id
    )?.ticketTatMatrix;
    const timeUnit = ticket ? ticket.runit : "DAY";
    const time = ticket ? ticket.rtime : 1;
    if (timeUnit == "DAY") {
      date.setDate(date.getDate() + time);
    } else if (timeUnit == "HOUR") {
      date.setHours(date.getHours() + time);
    } else {
      date.setMinutes(date.getMinutes() + time);
    }
    this.createTicketData.nextFollowupDate = this.datepipe.transform(date, "yyyy-MM-dd");
    this.createTicketData.nextFollowupTime = this.datepipe.transform(date, "HH:mm:ss");
  }

  showticketDocData(data: any) {
    console.log("data ", data?.filename.split(".")[data?.filename.split(".")?.length - 1]);
    this.spinner.show();
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
        this.spinner.hide();
        const blob = new Blob([data], { type: "application/pdf" });
        const blobUrl = URL.createObjectURL(blob);
        window.open(blobUrl, "_blank");
      } else {
        this.spinner.hide();
        const blob = new Blob([data], { type });
        const blobUrl = URL.createObjectURL(blob);
        this.previewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(blobUrl);
        // $("#documentPreview").modal("show");
        this.dialog.open(this.documentPreviewDialog, {
          width: "80%",
          disableClose: true
        });
      }
    });
  }

  downloadDoc(filename, docId, ticketId) {
    const url = `/case/document/download/${ticketId}/${docId}`;
    this.ticketManagementService.downloadFile(url).subscribe(blob => {
      importedSaveAs(blob, filename);
    });
  }
  uploadDocument(ticket) {
    this.selectedFileUploadPreview = [];
    console.log(ticket.caseId);
    this.uploadDataTicketId = ticket.caseId;
    this.dialog.open(this.uploadDocumentDialog, {
      width: "550px",
      disableClose: true
    });
    // $("#uploadDocumentId").modal("show");
  }
  deletUploadedFile(event: any) {
    var temp: File[] = this.selectedFileUploadPreview?.filter((item: File) => item?.name != event);
    this.selectedFileUploadPreview = temp;
    this.uploadDocForm.patchValue({
      file: temp
    });
  }
  uploadDocuments(dialogRef) {
    this.submitted = true;
    console.log("form data", this.uploadDocForm);
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
          this.ticketGroupForm.controls.file.reset();
          alert("File type must be png, jpg, jpeg or pdf");
        } else {
          this.spinner.show();
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
            this.toastr.error(`${response.responseMessage}`, "Failed!");

            this.spinner.hide();
          } else if (response.responseCode === 417) {
            this.toastr.error(`${response.responseMessage}`, "Failed!");
            this.spinner.hide();
          } else {
            dialogRef.close();
            this.openTicketDetail(this.uploadDataTicketId);
            this.submitted = false;
            this.toastr.success(`${response.message}`, "Success!");
            $("#uploadDocumentId").modal("hide");
            this.spinner.hide();
          }
        },
        (error: any) => {
          console.log(error, "error");
          this.toastr.error(`${error.error.ERROR}`, "Failed!");
          this.spinner.hide();
        }
      );
    }
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

  // deletSelectedFile(file: File) {
  //   // Remove file from array
  //   this.selectedFileUploadPreview = this.selectedFileUploadPreview.filter(f => f !== file);

  //   // Update form control
  //   this.uploadDocForm.patchValue({
  //     file: this.selectedFileUploadPreview
  //   });
  // }

  onClickServiceArea() {
    this.serviceAreaList = this.staffData.serviceAreasNameList;
    $("#serviceAreaDetail").modal("show");
  }

  ticketConversationModalOpen(ticketId) {
    this.spinner.show();
    this.getFollowUpDetailById(ticketId);
    // $("#conversationModal").modal("show");
    this.dialog.open(this.conversationDialog, {
      width: "80%",
      disableClose: true
    });
    this.spinner.hide();
  }
  convertToDate(dateStr: string): Date | null {
    if (!dateStr) return null;

    const [datePart, timePart] = dateStr.split(" ");
    const [day, month, year] = datePart.split("-").map(Number);

    let [time, modifier] = timePart.split(" ");
    let [hours, minutes] = time.split(":").map(Number);

    if (modifier === "PM" && hours < 12) hours += 12;
    if (modifier === "AM" && hours === 12) hours = 0;

    return new Date(year, month - 1, day, hours, minutes);
  }

  displayedColumns: string[] = [
    "issue",
    "number",
    "type",
    // "serialNumber",
    // "assignee",
    "status",
    // "followup",
    "actions"
  ];

  displatattachment = ["filename", "docStatus", "createdByName", "createdate", "action"];

  @ViewChild("staffDetailsDialog") staffDetailsDialog!: TemplateRef<any>;
  @ViewChild("ratingTicketDialog") ratingTicketDialog!: TemplateRef<any>;
  @ViewChild("documentPreviewDialog") documentPreviewDialog!: TemplateRef<any>;
  @ViewChild("uploadResolveDocumentDialog") uploadResolveDocumentDialog!: TemplateRef<any>;
  @ViewChild("serviceAreaDialog") serviceAreaDialog!: TemplateRef<any>;

  @ViewChild("uploadDocumentDialog") uploadDocumentDialog!: TemplateRef<any>;
  @ViewChild("conversationDialog") conversationDialog!: TemplateRef<any>;
  @ViewChild("addEditDialog") addEditDialog!: TemplateRef<any>;
  @ViewChild("detailsDialog") detailsDialog!: TemplateRef<any>;

  problemDomains = [
    {
      name: "Internet",
      sub: ["Slow Speeds", "Unable to browse via wifi", "SSID-Password Reset"]
    },
    {
      name: "Appointments",
      sub: ["Site visit follow up"]
    },
    {
      name: "Billing Issue",
      sub: ["Payment Follow Up"]
    },
    {
      name: "Billing Issues",
      sub: ["Payment Follow Up"]
    }
  ];
  normalizeText(input: string): string {
    return input
      .toLowerCase() // lowercase
      .replace(/[^a-z0-9]/g, ""); // remove all non-alphanumeric characters (spaces, hyphens, etc.)
  }

  subProblemDomainList: string[] = [];

  onProblemDomainChange(domain: string) {
    this.ticketGroupForm.value.problemDomains = domain;

    const found = this.problemDomains.find(d => d.name === domain);
    this.subProblemDomainList = found ? found.sub : [];

    this.ticketGroupForm.value.subProblemDomain = ""; // reset sub dropdown
  }
}
