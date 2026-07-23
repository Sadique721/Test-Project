// import { Component, OnInit } from "@angular/core";
// import { FormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
// import { NgxSpinnerService } from "ngx-spinner";
// import { ConfirmationService, MessageService } from "primeng/api";
// import { LeadSourceMasterService } from "src/app/service/lead-source-master-service";
// import { LeadSource } from "../model/leadSource";
// import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
// import { LeadSubSource } from "../model/leadSubSource";
// import { LoginService } from "src/app/service/login.service";
// import { AclClassConstants } from "src/app/constants/aclClassConstants";
// import { AclConstants } from "src/app/constants/aclOperationConstants";
// import { Observable, Observer } from "rxjs";
// import { SALES_CRMS } from "src/app/constants/aclConstants";

// @Component({
//     selector: "app-lead-source-master",
//     templateUrl: "./lead-source-master.component.html",
//     styleUrls: ["./lead-source-master.component.css"],
//     standalone: false
// })
// export class LeadSourceMasterComponent implements OnInit {
//   leadSourceMasterFormGroup: UntypedFormGroup;
//   submitted: boolean = false;
//   searchLeadSourceFormGroup: UntypedFormGroup;
//   statusOptions = RadiusConstants.status;
//   currentLeadSourceListData = 1;
//   leadSourceItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
//   leadSourceDataList: any = [];
//   leadSourceDataListTotalRecords: string;
//   isLeadSourceMasterEdit: boolean;
//   showItemPerPage: any;
//   createLeadSourceMasterData: LeadSource;
//   editLeadSourceMasterData: any;
//   viewTrscData: any;
//   pageLimitOptions = RadiusConstants.pageLimitOptions;
//   leadSubSourcePageLimitOptions = RadiusConstants.pageLimitOptions;
//   leadSubSourceMapping: any;
//   leadSubSourceDeletedIds: any;
//   leadSourceMapping: any;
//   leadSubSourceMappingForm: UntypedFormGroup;
//   leadSubSourceSubmitted: boolean;

//   currentLeadSubSourceListData = 1;
//   leadSubSourceItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
//   leadSubSourceDataListTotalRecords: string;

//   viewLeadSubSourceListData = 1;
//   viewLeadSubSourceItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
//   viewLeadSubSourceDataListTotalRecords: string;

//   mvnoid: number;
//   buid: number;

//   searchkey: string;
//   searchTrscName: any = "";
//   searchData: any;
//   searchSubmitted: boolean = false;

//   AclClassConstants;
//   AclConstants;
//   public loginService: LoginService;
//   createAccess: boolean = false;
//   deleteAccess: boolean = false;
//   editAccess: boolean = false;

//   constructor(
//     private fb: UntypedFormBuilder,
//     private spinner: NgxSpinnerService,
//     private confirmationService: ConfirmationService,
//     private messageService: MessageService,
//     private leadSourceMasterService: LeadSourceMasterService,
//     loginService: LoginService
//   ) {
//     this.loginService = loginService;
//     this.AclClassConstants = AclClassConstants;
//     this.AclConstants = AclConstants;

//     this.createAccess = loginService.hasPermission(SALES_CRMS.CREATE_LEAD_SOURCE);
//     this.deleteAccess = loginService.hasPermission(SALES_CRMS.DELETE_LEAD_SOURCE);
//     this.editAccess = loginService.hasPermission(SALES_CRMS.EDIT_LEAD_SOURCE);
//   }

//   ngOnInit(): void {
//     this.mvnoid = Number.parseInt(localStorage.getItem("mvnoId"));

//     this.leadSourceMasterFormGroup = this.fb.group({
//       leadSourceName: ["", Validators.required],
//       status: ["", Validators.required],
//     });

//     this.searchLeadSourceFormGroup = this.fb.group({
//       searchTrscName: ["", Validators.required],
//     });

//     this.leadSubSourceMappingForm = this.fb.group({
//       id: [""],
//       leadSubSourceName: ["", Validators.required],
//     });

//     this.leadSubSourceMapping = this.fb.array([]);
//     this.leadSubSourceDeletedIds = this.fb.array([]);

//     this.getLeadSourceList("");

//     this.viewTrscData = {
//       buId: null,
//       id: 0,
//       isDelete: false,
//       leadSourceName: "",
//       leadSubSourceDtoList: [],
//       mvnoId: null,
//       status: "",
//     };

//     this.searchData = {
//       filterBy: "",
//       filters: [
//         {
//           filterValue: "",
//           filterColumn: "leadSourceName",
//           filterDataType: "",
//           filterOperator: "",
//           filterCondition: "",
//         },
//       ],
//       page: this.currentLeadSourceListData,
//       pageSize: this.leadSourceItemsPerPage,
//       sortOrder: 0,
//     };
//   }
//   leadSourceDataLength: any = 0;
//   getLeadSourceList(list) {
//     let size;
//     this.searchkey = "";
//     let pageList = this.currentLeadSourceListData;
//     if (list) {
//       size = list;
//       this.leadSourceItemsPerPage = list;
//     } else {
//       size = this.leadSourceItemsPerPage;
//     }

//     const url = "/leadSource/all?page=" + pageList + "&pageSize=" + size;
//     this.searchkey = "";
//     this.leadSourceMasterService.getMethod(url).subscribe((response: any) => {
//       console.log(response.status, " ", response.leadSourceList.content);
//       if (response.status == 200) {
//         console.log(response.leadSourceList.content);
//         this.leadSourceDataList = response.leadSourceList.content;
//         this.leadSourceDataListTotalRecords = response.leadSourceList.totalElements;
//         if (this.showItemPerPage > this.leadSourceItemsPerPage) {
//           this.leadSourceDataLength = this.leadSourceDataList?.length % this.showItemPerPage;
//         } else {
//           this.leadSourceDataLength = this.leadSourceDataList?.length % this.leadSourceItemsPerPage;
//         }
//       } else {
//         this.leadSourceDataList = [];
//       }
//     });
//   }

//   searchLeadSource() {
//     let data: any = [];
//     if (this.searchLeadSourceFormGroup.valid) {
//       this.searchData.filters[0].filterColumn = "leadSourceName";
//       this.searchData.filters[0].filterValue =
//         this.searchLeadSourceFormGroup.controls["searchTrscName"].value.trim();
//       this.searchData.page = this.currentLeadSourceListData;
//       this.searchData.pageSize = this.leadSourceItemsPerPage;
//       this.searchData.filterBy = this.searchData.filters[0].filterColumn;

//       console.log(this.searchData);
//       data = this.searchData;

//       const url = "/leadSource/search";
//       this.leadSourceMasterService.postMethod(url, data).subscribe(
//         (response: any) => {
//           if (response.status == 200) {
//             if (response.errorMessage === "No Records Found!") {
//               this.leadSourceDataList = "";
//               this.leadSourceDataListTotalRecords = "";
//               this.submitted = false;
//               this.leadSubSourceSubmitted = false;
//               this.searchSubmitted = false;
//             } else {
//               this.leadSourceDataList = response.leadSourceList.content;
//               this.leadSourceDataListTotalRecords = response.leadSourceList.totalElements;
//               this.searchSubmitted = false;
//               this.submitted = false;
//               this.leadSubSourceSubmitted = false;
//             }
//           } else {
//             this.messageService.add({
//               severity: "error",
//               summary: "Error",
//               detail: response.errorMessage,
//               icon: "far fa-times-circle",
//             });
//             this.searchSubmitted = false;
//             this.submitted = false;
//             this.leadSubSourceSubmitted = false;
//           }
//         },
//         (error: any) => {
//           this.leadSourceDataListTotalRecords = "";
//           if (error.status == 404) {
//             this.messageService.add({
//               severity: "info",
//               summary: "Info",
//               detail: "Page Not Found!",
//               icon: "far fa-times-circle",
//             });
//             this.leadSourceDataList = [];
//             this.searchSubmitted = false;
//             this.submitted = false;
//             this.leadSubSourceSubmitted = false;
//           } else {
//             this.messageService.add({
//               severity: "error",
//               summary: "Error",
//               detail: "Something went wrong with the request!",
//               icon: "far fa-times-circle",
//             });
//           }
//           this.searchSubmitted = false;
//           this.submitted = false;
//           this.leadSubSourceSubmitted = false;
//         }
//       );
//     } else {
//       this.searchSubmitted = true;
//       this.currentLeadSourceListData = 1;
//       this.getLeadSourceList("");
//     }
//   }

//   clearSearchTrsc() {
//     this.searchTrscName = "";
//     this.searchSubmitted = false;
//     this.getLeadSourceList("");
//     this.searchSubmitted = false;
//     this.submitted = false;
//     this.onRemoveLeadSubSourceMapping(this.leadSubSourceMappingForm.value);
//     this.leadSourceMasterFormGroup.reset();
//     this.leadSubSourceMappingForm.reset();
//   }

//   pageChangedTrscList(pageNumber, idType) {
//     if (idType === "leadSubSourcePageData") {
//       this.viewLeadSubSourceListData = pageNumber;
//     }
//     if (idType === "leadSourceData") {
//       this.currentLeadSourceListData = pageNumber;
//       if (this.searchkey) {
//         this.searchLeadSource();
//       } else {
//         this.getLeadSourceList("");
//       }
//     }
//   }

//   TotalItemPerPage(event) {
//     this.currentLeadSourceListData = 1;
//     this.leadSourceItemsPerPage = Number(event.value);

//     if (!this.searchkey) {
//       this.getLeadSourceList(this.leadSourceItemsPerPage);
//     } else {
//       this.searchLeadSource();
//     }
//   }

//   leadSourceDetailsmodal: boolean = false;

//   trscAllDetails(data) {
//     this.viewTrscData = data;
//     this.leadSourceDetailsmodal = true;
//   }

//   closeModalOfdetails() {
//     this.leadSourceDetailsmodal = false;
//   }

//   setLeadSubSourceMappingForm(): UntypedFormGroup {
//     return this.fb.group({
//       leadSubSourceName: [this.leadSubSourceMappingForm.value.leadSubSourceName],
//     });
//   }

//   onAddSubSourceMappingField() {
//     this.leadSubSourceSubmitted = true;
//     if (this.leadSubSourceMappingForm.valid) {
//       this.leadSubSourceMapping.push(this.setLeadSubSourceMappingForm());
//       this.leadSubSourceMappingForm.reset();
//       this.leadSubSourceSubmitted = false;
//     }
//   }

//   async onRemoveLeadSubSourceMapping(reasonMappingFieldIndex: number) {
//     console.log(this.leadSubSourceMapping);
//     this.leadSubSourceMapping.removeAt(reasonMappingFieldIndex);
//     console.log(this.leadSubSourceMapping);
//   }

//   canExit() {
//     if (!this.leadSourceMasterFormGroup.dirty) return true;
//     {
//       return Observable.create((observer: Observer<boolean>) => {
//         this.confirmationService.confirm({
//           header: "Alert",
//           message: "The filled data will be lost. Do you want to continue? (Yes/No)",
//           icon: "pi pi-info-circle",
//           accept: () => {
//             observer.next(true);
//             observer.complete();
//           },
//           reject: () => {
//             observer.next(false);
//             observer.complete();
//           },
//         });
//         return false;
//       });
//     }
//   }
//   deleteConfirmonLeadSubSourceMappingField(leadSourceMappingFieldIndex: number, id: any) {
//     if (leadSourceMappingFieldIndex !== null) {
//       this.confirmationService.confirm({
//         message: "Do you want to delete this lead sub source?",
//         header: "Delete Confirmation",
//         icon: "pi pi-info-circle",
//         accept: () => {
//           this.onRemoveLeadSubSourceMapping(leadSourceMappingFieldIndex);
//           if (id) this.leadSubSourceDeletedIds.push(id);
//           console.log(this.leadSubSourceMapping);
//         },
//         reject: () => {
//           this.messageService.add({
//             severity: "info",
//             summary: "Rejected",
//             detail: "You have rejected the request!",
//           });
//         },
//       });
//     }
//   }

//   deleteTrsc(id) {
//     const url = "/leadSource/delete?leadSourceId=" + id;
//     this.leadSourceMasterService.deleteMethod(url).subscribe(
//       (response: any) => {
//         if (response.status == 406) {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: response.errorMessage,
//             icon: "far fa-times-circle",
//           });
//         } else {
//           if (this.currentLeadSourceListData != 1 && this.leadSourceDataList.length == 1) {
//             this.currentLeadSourceListData = this.leadSourceDataList - 1;
//           }
//           if (!this.searchkey) {
//             this.getLeadSourceList("");
//           } else {
//             this.searchLeadSource();
//           }
//           this.clearLeadSourceMasterData();
//           this.messageService.add({
//             severity: "success",
//             summary: "Successfully",
//             detail: response.message,
//             icon: "far fa-check-circle",
//           });
//         }
//       },
//       (error: any) => {
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.error.errorMessage,
//           icon: "far fa-times-circle",
//         });
//       }
//     );
//   }

//   deleteConfirmonLeadSourceData(id) {
//     if (id) {
//       this.confirmationService.confirm({
//         message: "Do you want to delete this lead source?",
//         header: "Delete Confirmation",
//         icon: "pi pi-info-circle",
//         accept: () => {
//           this.deleteTrsc(id);
//         },
//         reject: () => {
//           this.messageService.add({
//             severity: "info",
//             summary: "Rejected",
//             detail: "You have rejected",
//           });
//         },
//       });
//     }
//   }

//   addEditLeadSourceMaster(id) {
//     this.submitted = true;
//     if (this.leadSourceMasterFormGroup.valid) {
//       if (id) {
//         const url = "/leadSource/update/" + id;
//         let dataObj: any;
//         dataObj = {
//           id: this.editLeadSourceMasterData.id,
//           leadSourceName: this.editLeadSourceMasterData.leadSourceName,
//           status: this.editLeadSourceMasterData.status,
//           leadSubSourceDtoList: this.editLeadSourceMasterData.leadSubSourceList,
//           leadSubSourceDeletedIds: [],
//           mvnoId: this.editLeadSourceMasterData.mvnoId,
//           buId: this.editLeadSourceMasterData.buId,
//         };
//         dataObj.id = id;
//         console.log("dataObj => ", dataObj);
//         dataObj.leadSourceName = this.leadSourceMasterFormGroup.value.leadSourceName;
//         dataObj.status = this.leadSourceMasterFormGroup.value.status;
//         let subSourceArray: any = [];
//         let deletedSubsource: any = [];

//         for (let j = 0; j < this.leadSubSourceDeletedIds.value.length; j++) {
//           if (dataObj.leadSubSourceDtoList.length > 0) {
//             dataObj.leadSubSourceDtoList.forEach((entity: any) =>
//               entity.id === this.leadSubSourceDeletedIds.value[j]
//                 ? deletedSubsource.push(entity.id)
//                 : ""
//             );
//             console.log(deletedSubsource);
//           }
//         }
//         if (this.leadSubSourceMapping.value.length > 0) {
//           for (let i = 0; i < this.leadSubSourceMapping.value.length; i++) {
//             let myList: LeadSubSource = {
//               id: this.leadSubSourceMapping.value[i].id,
//               name: this.leadSubSourceMapping.value[i].leadSubSourceName,
//               leadSourceId: dataObj.id,
//             };
//             subSourceArray.push(myList);
//             console.log(subSourceArray);
//           }
//         }

//         dataObj.leadSubSourceDtoList = subSourceArray;
//         dataObj.leadSubSourceDeletedIds = deletedSubsource;
//         console.log(" this.createLeadSourceMasterData", dataObj);

//         this.leadSourceMasterService.updateMethod(url, dataObj).subscribe(
//           response => {
//             console.log(response);
//             this.messageService.add({
//               severity: "success",
//               summary: "Successfully",
//               //detail: "Update successfully",
//               icon: "far fa-check-circle",
//             });
//             this.leadSubSourceDeletedIds.value = [];
//             this.leadSourceMasterFormGroup.reset();
//             this.leadSubSourceMappingForm.reset();
//             this.leadSubSourceMapping.controls = [];
//             this.isLeadSourceMasterEdit = false;
//             this.leadSubSourceSubmitted = false;
//             this.searchSubmitted = false;
//             this.submitted = false;
//             this.getLeadSourceList("");
//           },
//           error => {
//             console.log(error);
//             this.messageService.add({
//               severity: "error",
//               summary: "Error",
//               detail: error.error.errorMessage,
//               icon: "far fa-times-circle",
//             });
//             this.leadSubSourceDeletedIds.value = [];
//             this.leadSourceMasterFormGroup.reset();
//             this.leadSubSourceMappingForm.reset();
//             this.leadSubSourceMapping.controls = [];
//             this.isLeadSourceMasterEdit = false;
//             this.leadSubSourceSubmitted = false;
//             this.searchSubmitted = false;
//             this.submitted = false;
//             this.getLeadSourceList("");
//           }
//         );
//       } else {
//         const url = "/leadSource/save";

//         this.createLeadSourceMasterData = this.leadSourceMasterFormGroup.value;
//         this.createLeadSourceMasterData.leadSubSourceDtoList = [];
//         console.log("this.leadsubsourcemappinglist size => ", this.leadSubSourceMapping.length);

//         if (this.leadSubSourceMapping.length > 0) {
//           for (let i = 0; i < this.leadSubSourceMapping.controls.length; i++) {
//             let myList: LeadSubSource = {
//               id: null,
//               name: this.leadSubSourceMapping.controls[i].value.leadSubSourceName,
//               leadSourceId: null,
//             };
//             console.log(this.createLeadSourceMasterData.leadSubSourceDtoList);

//             this.createLeadSourceMasterData.leadSubSourceDtoList.push(myList);
//           }
//         }
//         console.log(" this.createLeadSourceMasterData", this.createLeadSourceMasterData);

//         this.leadSourceMasterService.postMethod(url, this.createLeadSourceMasterData).subscribe(
//           (response: any) => {
//             console.log("My Response from backend", response.leadSource);

//             if (response.status === 200) {
//               this.messageService.add({
//                 severity: "success",
//                 summary: "Successfully",
//                 //detail: response.message,
//                 icon: "far fa-check-circle",
//               });
//               this.leadSubSourceDeletedIds.value = [];
//               this.leadSourceMasterFormGroup.reset();
//               this.leadSubSourceMappingForm.reset();
//               this.leadSubSourceMapping.controls = [];
//               this.leadSubSourceSubmitted = false;
//               this.submitted = false;
//               this.isLeadSourceMasterEdit = false;
//               this.searchSubmitted = false;
//               this.getLeadSourceList("");
//             } else {
//               this.messageService.add({
//                 severity: "error",
//                 summary: "Error",
//                 detail: response.errorMessage,
//                 icon: "far fa-times-circle",
//               });
//               this.leadSubSourceDeletedIds.value = [];
//               this.leadSourceMasterFormGroup.reset();
//               this.leadSubSourceMappingForm.reset();
//               this.leadSubSourceMapping.controls = [];
//               this.leadSubSourceSubmitted = false;
//               this.submitted = false;
//               this.isLeadSourceMasterEdit = false;
//               this.searchSubmitted = false;
//               this.getLeadSourceList("");
//             }
//           },
//           (error: any) => {
//             this.messageService.add({
//               severity: "error",
//               summary: "Error",
//               detail: error.error.errorMessage,
//               icon: "far fa-times-circle",
//             });
//             this.leadSubSourceDeletedIds.value = [];
//             this.leadSourceMasterFormGroup.reset();
//             this.leadSubSourceMappingForm.reset();
//             this.leadSubSourceMapping.controls = [];
//             this.leadSubSourceSubmitted = false;
//             this.submitted = false;
//             this.isLeadSourceMasterEdit = false;
//             this.searchSubmitted = false;
//             this.getLeadSourceList("");
//           }
//         );
//       }
//     }
//   }

//   clearLeadSourceMasterData() {
//     this.leadSourceMasterFormGroup.reset();
//     this.submitted = false;
//     this.searchSubmitted = false;
//     this.leadSubSourceSubmitted = false;
//     this.isLeadSourceMasterEdit = false;
//     this.leadSubSourceMapping.controls = [];
//     this.searchSubmitted = false;
//     this.getLeadSourceList("");
//   }

//   editLeadSourceMasterDataFunction(id) {
//     this.leadSourceMasterFormGroup.reset();
//     this.leadSubSourceMappingForm.reset();
//     this.isLeadSourceMasterEdit = true;

//     if (this.leadSubSourceMapping.controls) {
//       this.leadSubSourceMapping.controls = [];
//     }
//     const url = "/leadSource/findById?leadSourceid=" + id;
//     this.leadSourceMasterService.getMethod(url).subscribe(
//       (response: any) => {
//         if (response.status == 200) {
//           this.editLeadSourceMasterData = response.leadSource;
//           this.leadSourceMasterFormGroup.patchValue({
//             leadSourceName: this.editLeadSourceMasterData.leadSourceName,
//             status: this.editLeadSourceMasterData.status,
//           });

//           this.leadSourceMapping = this.fb.array([]);
//           this.editLeadSourceMasterData.leadSubSourceList.forEach(element => {
//             this.leadSubSourceMapping.push(this.fb.group(element));
//           });
//           this.leadSubSourceMappingForm.patchValue(this.editLeadSourceMasterData.leadSubSourceList);
//         } else {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: response.responseMessage,
//             icon: "far fa-times-circle",
//           });
//         }
//       },
//       (error: any) => {
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.error.ERROR,
//           icon: "far fa-times-circle",
//         });
//       }
//     );
//   }

//   LeadSubTotalItemPerPage(event) {
//     this.currentLeadSubSourceListData = 1;
//     this.leadSubSourceItemsPerPage = Number(event.value);
//   }

//   leadSubsourcePageChanged(pageNumber: any) {
//     this.currentLeadSubSourceListData = pageNumber;
//   }
// }

import { Component, OnInit, AfterViewInit, ViewChild, TemplateRef, ViewEncapsulation } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup, Validators, FormArray } from "@angular/forms";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatDialog } from "@angular/material/dialog";
import { Observable, Observer } from "rxjs";

import { MessageService, ConfirmationService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { LeadSourceMasterService } from "src/app/service/lead-source-master-service";
import { LoginService } from "src/app/service/login.service";
import { SALES_CRMS } from "src/app/constants/aclConstants";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { AddEditLeadSourceMasterComponent } from "./add-edit-lead-source-master/add-edit-lead-source-master.component";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { ToastrService } from "ngx-toastr";

@Component({
    selector: "app-lead-source-master",
    templateUrl: "./lead-source-master.component.html",
    styleUrls: ["./lead-source-master.component.css"],
    standalone: false,
    encapsulation: ViewEncapsulation.None
})
export class LeadSourceMasterComponent implements OnInit, AfterViewInit {
    title = "Lead Source Master";

    createAccess = false;
    deleteAccess = false;
    editAccess = false;
    viewAccess = false;
    isLeadSourceMasterEdit = false;

    leadSourceMasterFormGroup!: UntypedFormGroup;
    searchForm!: UntypedFormGroup;

    submitted = false;
    searchSubmitted = false;

    leadSourceDataList: any[] = [];
    dataSource = new MatTableDataSource<any>([]);
    leadSourceDataListTotalRecords = 0;
    leadSourceItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    currentPage = 1;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    statusOptions = RadiusConstants.status;

    searchKey = "";
    searchLeadSourceName = "";

    displayedColumns: string[] = ["id", "leadSourceName", "status", "action"];

    viewTrscData: any;
    paginatedSubSources: any[] = [];
    leadSubSourceItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    currentLeadSubSourceListData = 1;
    subSourceDisplayedColumns: string[] = ["subSourceName"];

    leadSubSourceMapping!: FormArray;
    leadSubSourceDeletedIds!: FormArray;
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;
    @ViewChild('leadSourceDetailsDialog') leadSourceDetailsDialog!: TemplateRef<any>;
    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private leadSourceMasterService: LeadSourceMasterService,
        private dialog: MatDialog,
        private toastr: ToastrService,
        public loginService: LoginService
    ) {
        this.createAccess = loginService.hasPermission(SALES_CRMS.CREATE_LEAD_SOURCE);
        this.deleteAccess = loginService.hasPermission(SALES_CRMS.DELETE_LEAD_SOURCE);
        this.editAccess = loginService.hasPermission(SALES_CRMS.EDIT_LEAD_SOURCE);
        this.viewAccess = loginService.hasPermission(SALES_CRMS.EDIT_LEAD_SOURCE);
    }

    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(SALES_CRMS.EDIT_LEAD_SOURCE) || this.loginService.hasPermission(SALES_CRMS.DELETE_LEAD_SOURCE)) {
            return ["id", "leadSourceName", "status", "action"];
        } else {
            return ["id", "leadSourceName", "status"];
        }
    }

    trackByLeadSourceId(index: number, item: any): any {
        return item.id ?? index;
    }

    trackBySubSourceId(index: number, item: any): any {
        return item.id ?? index;
    }

    ngOnInit(): void {
        this.leadSourceMasterFormGroup = this.fb.group({
            leadSourceName: ['', Validators.required],
            status: ['', Validators.required],
            leadSubSourceList: this.fb.array([]),
            leadSubSourceDeletedIds: this.fb.array([])
        });

        this.searchForm = this.fb.group({
            searchLeadSourceName: ['']
        });

        this.leadSubSourceMapping = this.fb.array([]);
        this.leadSubSourceDeletedIds = this.fb.array([]);

        this.getLeadSourceList();
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    getLeadSourceList(): void {
        const url = `/leadSource/all?page=${this.currentPage}&pageSize=${this.leadSourceItemsPerPage}`;
        this.spinner.show();

        this.leadSourceMasterService.getMethod(url).subscribe({
            next: (res: any) => {
                this.spinner.hide();
                if (res.status === 200 && Array.isArray(res.leadSourceList?.content)) {
                    this.leadSourceDataList = res.leadSourceList.content;
                    this.leadSourceDataListTotalRecords = res.leadSourceList.totalElements;
                    this.dataSource.data = [...this.leadSourceDataList];
                    setTimeout(() => {
                        if (this.paginator) {
                            this.paginator.length = this.leadSourceDataListTotalRecords;
                            this.paginator.pageIndex = this.currentPage - 1;
                        }
                    });
                } else {
                    this.leadSourceDataList = [];
                    this.leadSourceDataListTotalRecords = 0;
                    this.dataSource.data = [];
                    this.updatePaginator();
                }
            },
            error: () => {
                this.spinner.hide();
                this.leadSourceDataList = [];
                this.leadSourceDataListTotalRecords = 0;
                this.dataSource.data = [];
                this.updatePaginator();
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, "Failed to load Lead Source data.");
                }
            }
        });
    }

    private updatePaginator(): void {
        if (this.paginator) {
            this.dataSource = new MatTableDataSource<any>(this.leadSourceDataList);
            this.dataSource.paginator = this.paginator;
            this.dataSource.sort = this.sort;
            this.paginator.length = this.leadSourceDataListTotalRecords;
            this.paginator.pageIndex = this.currentPage - 1;
        }
    }

    searchLeadSource(): void {
        const keyword = this.searchLeadSourceName.trim();
        if (!keyword) {
            this.clearSearchTrsc();
            return;
        }

        if (this.searchKey !== keyword) {
            this.currentPage = 1;
        }

        this.searchKey = keyword;

        const searchData = {
            page: this.currentPage,
            pageSize: this.leadSourceItemsPerPage,
            sortOrder: 0,
            filters: [
                {
                    filterColumn: "leadSourceName",
                    filterValue: keyword,
                    filterDataType: "",
                    filterOperator: "",
                    filterCondition: ""
                }
            ],
            filterBy: "leadSourceName"
        };

        this.spinner.show();
        this.leadSourceMasterService.postMethod('/leadSource/search', searchData).subscribe({
            next: (res: any) => {
                this.spinner.hide();
                if (res.status === 200 && res.leadSourceList?.content.length) {
                    this.leadSourceDataList = res.leadSourceList.content;
                    this.leadSourceDataListTotalRecords = res.leadSourceList.totalElements;
                    this.dataSource.data = [...this.leadSourceDataList];
                    setTimeout(() => {
                        if (this.paginator) {
                            this.paginator.length = this.leadSourceDataListTotalRecords;
                            this.paginator.pageIndex = 0;
                        }
                    });
                } else {
                    this.toastr.info(res.responseMessage, "No records found.");

                    this.clearSearchTrsc();
                }
            },
            error: () => {
                (error: any) => this.toastr.error(`${error.error.ERROR}`, "Search failed.")
            }
        });
    }

    clearSearchTrsc(): void {
        this.searchKey = "";
        this.searchLeadSourceName = "";
        this.searchForm.reset();
        this.currentPage = 1;
        this.getLeadSourceList();
    }

    pageChangedTrscList(event: PageEvent): void {
        this.currentPage = event.pageIndex + 1;
        this.leadSourceItemsPerPage = event.pageSize;

        if (this.searchKey) {
            this.searchLeadSource();
        } else {
            this.getLeadSourceList();
        }
    }

    addEditLeadSourceDialog(): void {
        const dialogRef = this.dialog.open(AddEditLeadSourceMasterComponent, {
            width: '900px',
            autoFocus: false,
            data: {
                isEdit: false,
                title: ('Create ') + this.title,
                addLabel: true,
                yesLabel: 'Create',
                noLabel: 'Cancel',
                createAcS: this.createAccess,
                editAcs: this.editAccess
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.editLeadSourceMasterDataFunction('', result);
            }
        });
    }

    editLeadSourceMasterDataFunction(id: string, resultData?: any): void {
        if (id) {
            const url = "/leadSource/findById?leadSourceid=" + id;
            this.leadSourceMasterService.getMethod(url).subscribe({
                next: (response: any) => {
                    if (response.status === 200) {
                        this.isLeadSourceMasterEdit = true;
                        const leadSourceData = response.leadSource;

                        const subSourceListFromApi = leadSourceData?.leadSubSourceList || [];
                        const normalizedSubSourceList = subSourceListFromApi.map((subSource: any) => ({
                            id: subSource.id,
                            leadSubSourceName: subSource.leadSubSourceName,
                        }));

                        const dialogRef = this.dialog.open(AddEditLeadSourceMasterComponent, {
                            width: '900px',
                            data: {
                                isEdit: true,
                                title: 'Update ' + this.title,
                                yesLabel: 'Update',
                                noLabel: 'Cancel',
                                createAcS: this.createAccess,
                                editAcs: this.editAccess,
                                leadSourceData: leadSourceData,
                                leadSubSourceList: normalizedSubSourceList
                            }
                        });

                        dialogRef.afterClosed().subscribe(result => {
                            if (result) {
                                this.saveLeadSourceMaster(id, result);
                            }
                        });

                    } else {
                        this.toastr.error(`${response.error.ERROR}`, 'Failed!')
                    }
                },
                error: (error: any) => {
                    this.toastr.error(error.error?.ERROR || 'Failed to fetch lead source data', 'Failed!');

                }
            });
        } else if (resultData) {
            this.saveLeadSourceMaster("", resultData);
        }
    }

    saveLeadSourceMaster(id: string, resultData: any): void {
        this.submitted = true;

        const leadSourceData: any = {
            id: id || null,
            leadSourceName: resultData.leadSourceName,
            status: resultData.status,
            leadSubSourceDtoList: Array.isArray(resultData.leadSubSourceList)
                ? resultData.leadSubSourceList.map((subSource: any) => ({
                    id: subSource.id ? subSource.id : undefined,
                    name: subSource.leadSubSourceName || subSource.name,
                    leadSourceid: id || null
                }))
                : [],
            leadSubSourceDeletedIds: resultData.leadSubSourceDeletedIds || [],
            delete: false,
            isDelete: false
        };

        const url = id ? `/leadSource/update/${id}` : "/leadSource/save";

        this.spinner.show();

        const request$ = id
            ? this.leadSourceMasterService.updateMethod(url, leadSourceData)
            : this.leadSourceMasterService.postMethod(url, leadSourceData);

        request$.subscribe({
            next: (response: any) => {
                this.spinner.hide();
                this.submitted = false;
                this.isLeadSourceMasterEdit = false;
                this.resetForm();
                this.toastr.success(`${response.message}`, "Successfully " + (id ? "Updated" : "Created"));

                if (this.searchKey) {
                    this.searchLeadSource();
                } else {
                    this.getLeadSourceList();
                }
            },
            error: (error: any) => {
                this.spinner.hide();
                if (error.error.status === 417 || error.error.status === 406) {
                    this.toastr.info(error.ERROR || error.responseMessage, 'Info!');

                } else {

                    this.toastr.error(error.error?.ERROR || error.error.errorMessage || "Failed to save lead source.", 'Failed!');

                }
            }
        });
    }

    deleteConfirmonLeadSourceData(source: any): void {
        if (!source?.id) return;

        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: "400px",
            data: {
                title: `Delete ${this.title}`,
                description: `Are you sure you want to delete "${source.leadSourceName}"?`,
                yesLabel: "Delete",
                noLabel: "Cancel"
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.confirmDelete(source.id);
            }
        });
    }

    confirmDelete(id: any): void {
        this.spinner.show();
        this.leadSourceMasterService.deleteMethod(`/leadSource/delete?leadSourceId=${id}`).subscribe({
            next: (res: any) => {
                this.spinner.hide();
                if (res.status === 200) {
                    this.toastr.success(`${res.message}`, 'Deleted successfully!');

                    if (this.currentPage > 1 && this.leadSourceDataList.length === 1) {
                        this.currentPage--;
                    }

                    if (this.searchKey) {
                        this.searchLeadSource();
                    } else {
                        this.getLeadSourceList();
                    }
                } else {
                    this.toastr.error(`${res.error.ERROR}`, 'Delete error')
                }
            },
            error: (error: any) => {
                this.toastr.error(`${error.error.error}`, 'Failed to delete record')
            }
        });
    }

    openLeadSourceDetailsDialog(data: any): void {
        this.viewTrscData = data;
        this.currentLeadSubSourceListData = 1;

        if (data.leadSubSourceDtoList?.length) {
            this.updatePaginatedSubSources();
        } else {
            this.paginatedSubSources = [];
        }

        this.dialog.open(this.leadSourceDetailsDialog, {
            width: '900px',
            panelClass: 'custom-dialog-container'
        });
    }

    updatePaginatedSubSources(): void {
        const startIndex = (this.currentLeadSubSourceListData - 1) * this.leadSubSourceItemsPerPage;
        const endIndex = startIndex + this.leadSubSourceItemsPerPage;
        this.paginatedSubSources = this.viewTrscData.leadSubSourceDtoList.slice(startIndex, endIndex);
    }

    pageChangedleadSubSourceOnView(event: PageEvent): void {
        this.currentLeadSubSourceListData = event.pageIndex + 1;
        this.leadSubSourceItemsPerPage = event.pageSize;
        this.updatePaginatedSubSources();
    }

    resetForm(): void {
        this.submitted = false;
        this.leadSourceMasterFormGroup.reset();
        this.leadSourceMasterFormGroup.markAsPristine();
        this.leadSourceMasterFormGroup.markAsUntouched();
        Object.keys(this.leadSourceMasterFormGroup.controls).forEach(key => {
            this.leadSourceMasterFormGroup.get(key)?.setErrors(null);
        });
    }

    canExit(): Observable<boolean> {
        if (!this.leadSourceMasterFormGroup.dirty && !this.searchForm.dirty) {
            return new Observable(observer => {
                observer.next(true);
                observer.complete();
            });
        }

        return new Observable((observer: Observer<boolean>) => {
            this.confirmationService.confirm({
                header: "Alert",
                message: "The filled data will be lost. Do you want to continue? (Yes/No)",
                icon: "pi pi-info-circle",
                accept: () => {
                    observer.next(true);
                    observer.complete();
                },
                reject: () => {
                    observer.next(false);
                    observer.complete();
                }
            });
        });
    }
}

