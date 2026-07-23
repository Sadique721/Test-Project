// import { Component, OnInit } from "@angular/core";
// import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
// import { NgxSpinnerService } from "ngx-spinner";
// import { ConfirmationService, MessageService } from "primeng/api";
// import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
// import { RejectedReason } from "../model/rejectedReason";
// import { RejectedReasonService } from "src/app/service/rejected-reason.service";
// import { RejectedSubReason } from "../model/rejectedSubReason";
// import { PaginationDto } from "../model/paginationDto";
// import { GenericSearchModel } from "../model/GenericSearchModel";
// import { LoginService } from "src/app/service/login.service";
// import { AclClassConstants } from "src/app/constants/aclClassConstants";
// import { AclConstants } from "src/app/constants/aclOperationConstants";
// import { Observable, Observer } from "rxjs";
// import { PrepaidRejectedReasonService } from "src/app/service/prepaid-rejected-reason.service";
// import { PRE_CUST_CONSTANTS } from "src/app/constants/aclConstants";

// @Component({
//     selector: "app-prepaid-rejected-reason-master",
//     templateUrl: "./prepaid-rejected-reason-master.component.html",
//     styleUrls: ["./prepaid-rejected-reason-master.component.css"],
//     standalone: false
// })
// export class PrepaidRejectedReasonMasterComponent implements OnInit {
//   rejectedReasonMasterFormGroup: UntypedFormGroup;
//   submitted: boolean = false;
//   searchSubmitted: boolean = false;
//   statusOptions = RadiusConstants.status;
//   currentRejectedReasonListData = 1;
//   rejectedReasonItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
//   rejectedReasonDataList: any = [];
//   rejectedReasonDataListTotalRecords: string;
//   isRejectedReasonMasterEdit: boolean = false;
//   showItemPerPage: any;
//   createRejectedReasonMasterData: RejectedReason;
//   editRejectedReasonMasterData: any;
//   viewTrscData: any;
//   pageLimitOptions = RadiusConstants.pageLimitOptions;
//   leadSubSourcePageLimitOptions = RadiusConstants.pageLimitOptions;
//   rejectedSubReasonMapping: any;
//   rejectedReasonMapping: any;
//   rejecteSubReasonMappingForm: UntypedFormGroup;
//   rejectedSubReasonSubmitted: boolean = false;
//   currentRejectedSubReasonListData = 1;
//   rejectedSubReasonItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
//   rejectedSubReasonDataListTotalRecords: string;
//   searchRejectedReasonFormGroup: UntypedFormGroup;
//   rejectedSubReasonDeletedIds: any;
//   viewrejectedSubReasonListData = 1;
//   viewrejectedSubReasonItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
//   viewrejectedSubReasonDataListTotalRecords: string;

//   searchkey: any;
//   searchTrscName: any = "";
//   searchData: any;

//   AclClassConstants;
//   AclConstants;
//   public loginService: LoginService;
//   viewAccess: any;
//   createAccess: any;
//   editAccess: any;
//   deleteAccess: any;

//   constructor(
//     private fb: UntypedFormBuilder,
//     private spinner: NgxSpinnerService,
//     private confirmationService: ConfirmationService,
//     private messageService: MessageService,
//     private rejectedReasonService: PrepaidRejectedReasonService,
//     loginService: LoginService
//   ) {
//     this.loginService = loginService;
//     this.AclClassConstants = AclClassConstants;
//     this.AclConstants = AclConstants;

//     this.createAccess = loginService.hasPermission(
//       PRE_CUST_CONSTANTS.CREATE_PRE_CUST_REJECTED_REASON
//     );
//     this.deleteAccess = loginService.hasPermission(
//       PRE_CUST_CONSTANTS.DELETE_PRE_CUST_REJECTED_REASON
//     );
//     this.editAccess = loginService.hasPermission(PRE_CUST_CONSTANTS.EDIT_PRE_CUST_REJECTED_REASON);
//   }

//   ngOnInit(): void {
//     // this.mvnoid = Number.parseInt(localStorage.getItem('mvnoId'));

//     this.rejectedReasonMasterFormGroup = this.fb.group({
//       name: ["", Validators.required],
//       status: ["", Validators.required],
//     });

//     this.rejecteSubReasonMappingForm = this.fb.group({
//       name: ["", Validators.required],
//     });

//     this.searchRejectedReasonFormGroup = this.fb.group({
//       searchTrscName: ["", Validators.required],
//     });

//     this.rejectedSubReasonMapping = this.fb.array([]);
//     this.rejectedSubReasonDeletedIds = this.fb.array([]);

//     this.searchData = {
//       page: this.currentRejectedReasonListData,
//       pageSize: this.rejectedReasonItemsPerPage,
//       sortOrder: 0,
//       filters: [
//         {
//           filterColumn: "name",
//           filterValue: "",
//           filterDataType: "",
//           filterOperator: "",
//           filterCondition: "",
//         },
//       ],
//       filterBy: "",
//     };
//     console.log("this.searchData", this.searchData);

//     this.getRejectedReasonList("");

//     this.viewTrscData = {
//       buId: null,
//       id: 0,
//       isDelete: false,
//       name: "",
//       rejectSubReasonDtoList: [],
//       mvnoId: null,
//       status: "",
//     };
//   }

//   getRejectedReasonList(list: any) {
//     let size;
//     this.searchkey = "";
//     let pageList = this.currentRejectedReasonListData;
//     if (list) {
//       size = list;
//       this.rejectedReasonItemsPerPage = list;
//     } else {
//       size = this.rejectedReasonItemsPerPage;
//     }

//     const url = "/rejectReason/all?page=" + pageList + "&pageSize=" + size;
//     this.searchkey = "";

//     this.rejectedReasonService.getMethod(url).subscribe((response: any) => {
//       if (response.status == 200) {
//         console.log(response.rejectReasonList.content);

//         this.rejectedReasonDataList = response.rejectReasonList.content;
//         this.rejectedReasonDataListTotalRecords = response.rejectReasonList.totalElements;
//       } else {
//         this.rejectedReasonDataList = [];
//       }
//     });
//   }

//   searchRejectedReason() {
//     this.searchSubmitted = true;
//     if (this.searchRejectedReasonFormGroup.valid) {
//       if (
//         this.searchkey !==
//         this.searchRejectedReasonFormGroup.controls["searchTrscName"].value.trim()
//       ) {
//         this.currentRejectedReasonListData = 1;
//       }
//       this.searchkey = this.searchRejectedReasonFormGroup.controls["searchTrscName"].value.trim();
//       let data: any = [];
//       this.searchData.filters[0].filterColumn = "name";
//       this.searchData.filters[0].filterValue =
//         this.searchRejectedReasonFormGroup.controls["searchTrscName"].value.trim();
//       this.searchData.page = this.currentRejectedReasonListData;
//       this.searchData.pageSize = this.rejectedReasonItemsPerPage;
//       this.searchData.filterBy = this.searchData.filters[0].filterColumn;

//       data = this.searchData;

//       const url = "/rejectReason/search";
//       this.rejectedReasonService.postMethod(url, data).subscribe(
//         (response: any) => {
//           if (response.status == 200) {
//             this.rejectedReasonDataList = response.rejectReasonList.content;
//             this.rejectedReasonDataListTotalRecords = response.rejectReasonList.totalElements;
//             this.submitted = false;
//             this.rejectedSubReasonSubmitted = false;
//             this.searchSubmitted = false;
//           } else {
//             this.getRejectedReasonList("");
//             this.submitted = false;
//             this.rejectedSubReasonSubmitted = false;
//             this.searchSubmitted = false;
//           }
//         },
//         (error: any) => {
//           this.rejectedReasonDataListTotalRecords = "";
//           if (error.status == 404) {
//             this.getRejectedReasonList("");

//             this.submitted = false;
//             this.rejectedSubReasonSubmitted = false;
//             this.searchSubmitted = false;
//           } else {
//             this.getRejectedReasonList("");

//             this.submitted = false;
//             this.rejectedSubReasonSubmitted = false;
//             this.searchSubmitted = false;
//           }
//         }
//       );
//     } else {
//       this.searchRejectedReasonFormGroup.reset();
//       this.getRejectedReasonList("");
//       this.messageService.add({
//         severity: "error",
//         summary: "Error",
//         detail: "Keyword is required!",
//         icon: "far fa-times-circle",
//       });
//     }
//   }

//   clearSearchTrsc() {
//     this.searchTrscName = "";
//     this.getRejectedReasonList("");
//     this.submitted = false;
//     this.rejectedSubReasonSubmitted = false;
//     this.searchSubmitted = false;
//     // this.onRemoveRejectedSubReasonMapping(this.rejecteSubReasonMappingForm.value);
//     this.rejectedSubReasonMapping = this.fb.array([]);
//     this.rejectedReasonMasterFormGroup.reset();
//     this.rejecteSubReasonMappingForm.reset();
//   }

//   pageChangedTrscList(pageNumber) {
//     this.currentRejectedReasonListData = pageNumber;
//     if (this.searchkey) {
//       this.searchRejectedReason();
//     } else {
//       this.getRejectedReasonList("");
//     }
//   }

//   pageChangedleadSubSourceOnView(pageNumber) {
//     this.viewrejectedSubReasonListData = pageNumber;
//     // this.trscAllDetails('');
//   }

//   TotalItemPerPage(event) {
//     this.currentRejectedReasonListData = 1;
//     this.rejectedReasonItemsPerPage = Number(event.value);

//     if (!this.searchkey) {
//       this.getRejectedReasonList(this.rejectedReasonItemsPerPage);
//     } else {
//       this.searchRejectedReason();
//     }
//   }

//   trscAllDetails(data) {
//     this.viewTrscData = data;

//     console.log(this.viewTrscData);
//   }

//   setRejectedSubReasonMappingForm(): UntypedFormGroup {
//     return this.fb.group({
//       name: [this.rejecteSubReasonMappingForm.value.name],
//     });
//   }

//   onAddRejectedSubReasonMappingField() {
//     this.rejectedSubReasonSubmitted = true;
//     if (this.rejecteSubReasonMappingForm.valid) {
//       this.rejectedSubReasonMapping.push(this.setRejectedSubReasonMappingForm());
//       this.rejecteSubReasonMappingForm.reset();
//       this.rejectedSubReasonSubmitted = false;
//     }
//   }

//   async onRemoveRejectedSubReasonMapping(reasonMappingFieldIndex: number) {
//     this.rejectedSubReasonMapping.removeAt(reasonMappingFieldIndex);
//     console.log(this.rejectedSubReasonMapping);
//   }

//   deleteConfirmonRejectedSubReasonMappingField(rejectedReasonMappingFieldIndex: number, id: any) {
//     if (rejectedReasonMappingFieldIndex !== null) {
//       this.confirmationService.confirm({
//         message: "Do you want to delete this rejected Sub Reason?",
//         header: "Delete Confirmation",
//         icon: "pi pi-info-circle",
//         accept: () => {
//           this.onRemoveRejectedSubReasonMapping(rejectedReasonMappingFieldIndex);
//           if (id) this.rejectedSubReasonDeletedIds.push(id);
//           console.log(this.rejectedSubReasonMapping);
//           if (this.rejectedSubReasonMapping.length <= 5) this.currentRejectedSubReasonListData = 1;
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
//     const url = "/rejectReason/delete?rejectReasonId=" + id;
//     this.rejectedReasonService.deleteMethod(url).subscribe(
//       (response: any) => {
//         if (response.status === 200) {
//           this.messageService.add({
//             severity: "success",
//             summary: "Successfully",
//             detail: response.message,
//             icon: "far fa-check-circle",
//           });
//         } else {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: response.errorMessage,
//             icon: "far fa-times-circle",
//           });
//         }
//         if (this.currentRejectedReasonListData != 1 && this.rejectedReasonDataList?.length == 1) {
//           this.currentRejectedReasonListData = this.rejectedReasonDataList - 1;
//         }
//         this.clearRejectedReasonMasterData();
//         this.getRejectedReasonList("");
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

//   deleteConfirmonRejectedReasonData(id) {
//     if (id) {
//       this.confirmationService.confirm({
//         message: "Do you want to delete this rejected reason?",
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

//   addEditRejectedReasonMaster(id) {
//     this.submitted = true;
//     if (this.rejectedReasonMasterFormGroup.valid) {
//       if (id) {
//         const url = "/rejectReason/update/" + id;
//         let dataObj: any;
//         dataObj = {
//           id: this.editRejectedReasonMasterData.id,
//           name: this.editRejectedReasonMasterData.name,
//           status: this.editRejectedReasonMasterData.status,
//           rejectSubReasonDtoList: this.editRejectedReasonMasterData.rejectSubReasonDtoList,
//           rejectSubReasonDeletedIds: [],
//         };
//         dataObj.id = id;
//         console.log("dataObj => ", dataObj);
//         dataObj.name = this.rejectedReasonMasterFormGroup.value.name;
//         dataObj.status = this.rejectedReasonMasterFormGroup.value.status;
//         let subSourceArray: any = [];
//         let deletedSubsource: any = [];

//         for (let j = 0; j < this.rejectedSubReasonDeletedIds.value?.length; j++) {
//           if (dataObj.rejectSubReasonDtoList?.length > 0) {
//             dataObj.rejectSubReasonDtoList.forEach((entity: any) =>
//               entity.id === this.rejectedSubReasonDeletedIds.value[j]
//                 ? deletedSubsource.push(entity.id)
//                 : ""
//             );
//             console.log(deletedSubsource);
//           }
//         }
//         if (this.rejectedSubReasonMapping.value?.length > 0) {
//           for (let i = 0; i < this.rejectedSubReasonMapping.value?.length; i++) {
//             let myList: RejectedSubReason = {
//               id: this.rejectedSubReasonMapping.value[i].id,
//               name: this.rejectedSubReasonMapping.value[i].name,
//               rejectReasonId: dataObj.id,
//             };
//             subSourceArray.push(myList);
//             console.log(subSourceArray);
//           }
//         }

//         // this.createLeadSourceMasterData.leadSubSourceDtoList = this.leadSubSourceMapping.value;
//         dataObj.rejectSubReasonDtoList = subSourceArray;
//         dataObj.rejectSubReasonDeletedIds = deletedSubsource;

//         console.log("Rejected Reason Prepared Data Obj => ", dataObj);

//         this.rejectedReasonService.updateMethod(url, dataObj).subscribe(
//           (response: any) => {
//             console.log(response);
//             if (response.status === 200) {
//               this.messageService.add({
//                 severity: "success",
//                 summary: "Successfully",
//                 // detail: "Update Successfull",
//                 icon: "far fa-check-circle",
//               });
//               this.getRejectedReasonList("");
//               this.rejectedReasonMasterFormGroup.reset();
//               this.rejecteSubReasonMappingForm.reset();
//               this.rejectedSubReasonMapping.controls = [];
//               this.isRejectedReasonMasterEdit = false;
//               this.submitted = false;
//               this.rejectedSubReasonSubmitted = false;
//               this.searchSubmitted = false;
//             }
//             if (response.status === 406) {
//               this.messageService.add({
//                 severity: "error",
//                 summary: "Error",
//                 detail: response.errorMessage,
//                 icon: "far fa-times-circle",
//               });
//               this.getRejectedReasonList("");
//               this.rejectedReasonMasterFormGroup.reset();
//               this.rejecteSubReasonMappingForm.reset();
//               this.rejectedSubReasonMapping.controls = [];
//               this.isRejectedReasonMasterEdit = false;
//               this.submitted = false;
//               this.rejectedSubReasonSubmitted = false;
//               this.searchSubmitted = false;
//             }
//           },
//           error => {
//             console.log(error);
//             this.messageService.add({
//               severity: "error",
//               summary: "Error",
//               detail: error.error.errorMessage,
//               icon: "far fa-times-circle",
//             });
//             this.getRejectedReasonList("");
//             this.rejectedReasonMasterFormGroup.reset();
//             this.rejecteSubReasonMappingForm.reset();
//             this.rejectedSubReasonMapping.controls = [];
//             this.isRejectedReasonMasterEdit = false;
//             this.submitted = false;
//             this.rejectedSubReasonSubmitted = false;
//             this.searchSubmitted = false;
//           }
//         );
//         // }
//       } else {
//         const url = "/rejectReason/save";

//         this.createRejectedReasonMasterData = this.rejectedReasonMasterFormGroup.value;
//         this.createRejectedReasonMasterData.rejectSubReasonDtoList = [];
//         console.log(
//           "this.rejectedSubReasonMappingList size => ",
//           this.rejectedSubReasonMapping?.length
//         );

//         if (this.rejectedSubReasonMapping?.length > 0) {
//           for (let i = 0; i < this.rejectedSubReasonMapping.controls?.length; i++) {
//             let myList: RejectedSubReason = {
//               id: null,
//               name: this.rejectedSubReasonMapping.controls[i].value.name,
//               rejectReasonId: null,
//             };
//             console.log(this.createRejectedReasonMasterData.rejectSubReasonDtoList);

//             this.createRejectedReasonMasterData.rejectSubReasonDtoList.push(myList);
//           }
//         }
//         console.log(" this.createRejectedReasonMasterData", this.createRejectedReasonMasterData);

//         this.rejectedReasonService.postMethod(url, this.createRejectedReasonMasterData).subscribe(
//           (response: any) => {
//             console.log("My Response from backend", response.rejectReason);

//             if (response.status === 200) {
//               this.messageService.add({
//                 severity: "success",
//                 summary: "Successfully",
//                 //detail: response.message,
//                 icon: "far fa-check-circle",
//               });

//               this.getRejectedReasonList("");
//               this.rejectedReasonMasterFormGroup.reset();
//               this.rejecteSubReasonMappingForm.reset();
//               this.rejectedSubReasonMapping.controls = [];
//               this.isRejectedReasonMasterEdit = false;
//               this.submitted = false;
//               this.rejectedSubReasonSubmitted = false;
//               this.searchSubmitted = false;
//             }
//             if (response.status === 406) {
//               this.messageService.add({
//                 severity: "error",
//                 summary: "Error",
//                 detail: response.errorMessage,
//                 icon: "far fa-times-circle",
//               });
//               this.getRejectedReasonList("");
//               this.rejectedReasonMasterFormGroup.reset();
//               this.rejecteSubReasonMappingForm.reset();
//               this.rejectedSubReasonMapping.controls = [];
//               this.isRejectedReasonMasterEdit = false;
//               this.submitted = false;
//               this.rejectedSubReasonSubmitted = false;
//               this.searchSubmitted = false;
//             }
//           },
//           (error: any) => {
//             this.messageService.add({
//               severity: "error",
//               summary: "Error",
//               detail: error.error.errorMessage,
//               icon: "far fa-times-circle",
//             });
//             this.getRejectedReasonList("");
//             this.rejectedReasonMasterFormGroup.reset();
//             this.rejecteSubReasonMappingForm.reset();
//             this.rejectedSubReasonMapping.controls = [];
//             this.isRejectedReasonMasterEdit = false;
//             this.submitted = false;
//             this.rejectedSubReasonSubmitted = false;
//             this.searchSubmitted = false;
//           }
//         );
//       }
//     }
//   }

//   clearRejectedReasonMasterData() {
//     this.rejectedReasonMasterFormGroup.reset();
//     this.submitted = false;
//     this.rejectedSubReasonSubmitted = false;
//     this.searchSubmitted = false;
//     this.rejectedSubReasonMapping.controls = [];
//     this.getRejectedReasonList("");
//   }

//   editRejectedReasonMasterDataFunction(id) {
//     this.rejectedReasonMasterFormGroup.reset();
//     this.rejectedSubReasonMapping.reset();
//     this.isRejectedReasonMasterEdit = true;

//     if (this.rejectedSubReasonMapping.controls) {
//       this.rejectedSubReasonMapping.controls = [];
//     }
//     const url = "/rejectReason/findById?rejectReasonId=" + id;
//     this.rejectedReasonService.getMethod(url).subscribe(
//       (response: any) => {
//         if (response.status == 200) {
//           this.editRejectedReasonMasterData = response.rejectReason;
//           this.rejectedReasonMasterFormGroup.patchValue({
//             name: this.editRejectedReasonMasterData.name,
//             status: this.editRejectedReasonMasterData.status,
//           });

//           this.rejectedReasonMapping = this.fb.array([]);
//           if (this.editRejectedReasonMasterData.rejectSubReasonDtoList) {
//             this.editRejectedReasonMasterData.rejectSubReasonDtoList.forEach(element => {
//               this.rejectedSubReasonMapping.push(this.fb.group(element));
//             });
//             this.rejecteSubReasonMappingForm.patchValue(
//               this.editRejectedReasonMasterData.rejectSubReasonDtoList
//             );
//           }
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
//     this.currentRejectedSubReasonListData = 1;
//     this.rejectedSubReasonItemsPerPage = Number(event.value);
//   }

//   pageChangedRejectedSubReason(pageNumber) {
//     this.currentRejectedSubReasonListData = pageNumber;
//   }

//   canExit() {
//     if (!this.rejectedReasonMasterFormGroup.dirty) return true;
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
import { PrepaidRejectedReasonService } from "src/app/service/prepaid-rejected-reason.service";
import { LoginService } from "src/app/service/login.service";
import { PRE_CUST_CONSTANTS } from "src/app/constants/aclConstants";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { AddEditPrepaidRejectedReasonMasterComponent } from "./add-edit-prepaid-rejected-reason-master/add-edit-prepaid-rejected-reason-master.component";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { ToastrService } from "ngx-toastr";

@Component({
    selector: "app-prepaid-rejected-reason-master",
    templateUrl: "./prepaid-rejected-reason-master.component.html",
    styleUrls: ["./prepaid-rejected-reason-master.component.css"],
    standalone: false,
    encapsulation: ViewEncapsulation.None
})
export class PrepaidRejectedReasonMasterComponent implements OnInit, AfterViewInit {
    title = "Rejected Reason Master";

    createAccess = false;
    deleteAccess = false;
    editAccess = false;
    viewAccess = false;
    isRejectedReasonMasterEdit = false;

    rejectedReasonMasterFormGroup!: UntypedFormGroup;
    searchForm!: UntypedFormGroup;

    submitted = false;
    searchSubmitted = false;

    rejectedReasonListData: any[] = [];
    dataSource = new MatTableDataSource<any>([]);
    rejectedReasonTotalRecords = 0;
    rejectedReasonItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    currentPage = 1;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    statusOptions = RadiusConstants.status;

    searchKey = "";
    searchRejectedReasonName = "";

    displayedColumns: string[] = ["id", "name", "status", "action"];
    viewTrscData: any;
    paginatedSubReasons: any[] = [];
    rejectedSubReasonItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    currentRejectedSubReasonListData = 1;
    subReasonDisplayedColumns: string[] = ["subSource"];

    rejectedSubReasonMapping!: FormArray;
    rejectedSubReasonDeletedIds!: FormArray;

    trackByRejectedReasonId(index: number, item: any): any {
        return item.id ?? index;
    }

    trackBySubReasonId(index: number, item: any): any {
        return item.id ?? index;
    }

    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;
    @ViewChild('rejectedReasonDetailsDialog') rejectedReasonDetailsDialog!: TemplateRef<any>;
    @ViewChild('rejectedSubReasonPaginator') rejectedSubReasonPaginator!: MatPaginator;
    fullSubReasonList: any[] = [];

    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService, private toastr: ToastrService,
        private rejectedReasonService: PrepaidRejectedReasonService,
        private dialog: MatDialog,
        public loginService: LoginService
    ) {
        this.createAccess = this.loginService.hasPermission(PRE_CUST_CONSTANTS.CREATE_PRE_CUST_REJECTED_REASON);
        this.deleteAccess = this.loginService.hasPermission(PRE_CUST_CONSTANTS.DELETE_PRE_CUST_REJECTED_REASON);
        this.editAccess = this.loginService.hasPermission(PRE_CUST_CONSTANTS.EDIT_PRE_CUST_REJECTED_REASON);
        this.viewAccess = this.loginService.hasPermission(PRE_CUST_CONSTANTS.EDIT_PRE_CUST_REJECTED_REASON);
    }

    trackById(index: number, item: any): any {
        return item.id ?? index;
    }

    ngOnInit(): void {
        this.rejectedReasonMasterFormGroup = this.fb.group({
            name: ['', [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            status: ['', Validators.required],
            rejectedSubReasonDtoList: this.fb.array([]),
            rejectedSubReasonDeletedIds: this.fb.array([])
        });

        this.rejectedSubReasonMapping = this.rejectedReasonMasterFormGroup.get('rejectedSubReasonDtoList') as FormArray;
        this.rejectedSubReasonDeletedIds = this.rejectedReasonMasterFormGroup.get('rejectedSubReasonDeletedIds') as FormArray;

        this.searchForm = this.fb.group({
            searchRejectedReasonName: ['']
        });

        this.getRejectedReasonList();
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }



    getRejectedReasonList(): void {
        const url = `/rejectReason/all?page=${this.currentPage}&pageSize=${this.rejectedReasonItemsPerPage}`;
        this.spinner.show();
        this.rejectedReasonService.getMethod(url).subscribe({
            next: (res: any) => {
                this.spinner.hide();
                if (res.status === 200 && Array.isArray(res.rejectReasonList?.content)) {
                    this.rejectedReasonListData = res.rejectReasonList.content;
                    this.rejectedReasonTotalRecords = res.rejectReasonList.totalElements;
                    this.dataSource.data = [...this.rejectedReasonListData];
                    setTimeout(() => {
                        if (this.paginator) {
                            this.paginator.length = this.rejectedReasonTotalRecords;
                            this.paginator.pageIndex = this.currentPage - 1;
                        }
                    });
                } else {
                    this.clearData();
                }
            },
            error: (error) => {
                this.toastr.error(`${error.error.ERROR}`, "Failed to load Rejected Reason data.");
            }
        });
    }

    clearData(): void {
        this.rejectedReasonListData = [];
        this.rejectedReasonTotalRecords = 0;
        this.dataSource.data = [];
        if (this.paginator) {
            this.paginator.length = 0;
        }
    }

    searchRejectedReason(): void {
        const keyword = this.searchRejectedReasonName.trim();
        if (!keyword) {
            this.clearSearchRejectedReason();
            return;
        }

        if (this.searchKey !== keyword) {
            this.currentPage = 1;
        }
        this.searchKey = keyword;

        const searchData = {
            page: this.currentPage,
            pageSize: this.rejectedReasonItemsPerPage,
            sortOrder: 0,
            filters: [
                {
                    filterColumn: "name",
                    filterValue: keyword,
                    filterDataType: "",
                    filterOperator: "",
                    filterCondition: ""
                }
            ],
            filterBy: "name"
        };

        this.spinner.show();
        this.rejectedReasonService.postMethod('/rejectReason/search', searchData).subscribe({
            next: (res: any) => {
                this.spinner.hide();
                if (res.status === 200 && res.rejectReasonList?.content.length) {
                    this.rejectedReasonListData = res.rejectReasonList.content;
                    this.rejectedReasonTotalRecords = res.rejectReasonList.totalElements;
                    this.dataSource.data = [...this.rejectedReasonListData];
                    setTimeout(() => {
                        if (this.paginator) {
                            this.paginator.length = this.rejectedReasonTotalRecords;
                            this.paginator.pageIndex = 0;
                        }
                    });
                } else {
                    this.toastr.info(res.msg, "No records found.");
                    this.clearSearchRejectedReason();
                }
            },
            error: (error) => {
                this.toastr.error(`${error.error.ERROR}`, 'Search failed.');
            }
        });
    }

    clearSearchRejectedReason(): void {
        this.searchKey = "";
        this.searchRejectedReasonName = "";
        this.searchForm.reset();
        this.currentPage = 1;
        this.getRejectedReasonList();
    }

    pageChangedRejectedReasonList(event: PageEvent): void {
        if (this.rejectedReasonItemsPerPage !== event.pageSize) {
            this.rejectedReasonItemsPerPage = event.pageSize;
            this.currentPage = 1; // Reset to first page on page size change
        } else {
            this.currentPage = event.pageIndex + 1;
        }

        if (this.searchKey) {
            this.searchRejectedReason();
        } else {
            this.getRejectedReasonList();
        }
    }

    addEditRejectedReasonDialog(id?: number): void {
        const dialogRef = this.dialog.open(AddEditPrepaidRejectedReasonMasterComponent, {
            width: '900px',
            autoFocus: false,
            data: {
                isEdit: !!id,
                title: (id ? 'Update ' : 'Create ') + this.title,
                yesLabel: id ? 'Update' : 'Create',
                noLabel: 'Cancel',
                createAcS: this.createAccess,
                editAcs: this.editAccess,
                rejectReasonId: id
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.saveRejectedReason('', result);
            }
        });
    }

    saveRejectedReason(rejectedReasonId: string, resultData: any): void {
        this.submitted = true;

        const rejectedReasonData: any = {
            id: rejectedReasonId || null,
            name: resultData.name,
            status: resultData.status,
            rejectSubReasonDtoList: Array.isArray(resultData.rejectSubReasonList)
                ? resultData.rejectSubReasonList.map((subReason: any) => ({
                    id: subReason.id ? subReason.id : undefined, // null or undefined for new
                    name: subReason.name,
                    rejectReasonId: rejectedReasonId || null
                }))
                : [],
            rejectedSubReasonDeletedIds: resultData.rejectedSubReasonDeletedIds || [],
            delete: false,
            isDelete: false
        };

        const url = rejectedReasonId ? `/rejectReason/update/${rejectedReasonId}` : "/rejectReason/save";

        this.spinner.show();

        const request$ = rejectedReasonId
            ? this.rejectedReasonService.updateMethod(url, rejectedReasonData)
            : this.rejectedReasonService.postMethod(url, rejectedReasonData);

        request$.subscribe({
            next: (response: any) => {
                this.spinner.hide();
                this.submitted = false;
                this.isRejectedReasonMasterEdit = false;
                this.resetForm();

                this.toastr.success(`${response.msg}`, "Successfully " + (rejectedReasonId ? "Updated" : "Created"));

                if (this.searchKey) {
                    this.searchRejectedReason();
                } else {
                    this.getRejectedReasonList();
                }
            },
            error: (error: any) => {
                this.toastr.error(error.error?.ERROR || error.error.msg || "Failed to save rejected reason.", 'Failed!');
            }
        });
    }



    editRejectedReasonMasterDataFunction(id: string, resultData?: any): void {
        if (id) {
            const url = "/rejectReason/findById?rejectReasonId=" + id;
            this.rejectedReasonService.getMethod(url).subscribe({
                next: (response: any) => {
                    if (response.status === 200) {
                        this.isRejectedReasonMasterEdit = true;
                        const rejectedReasonData = response.rejectReason;

                        const dialogRef = this.dialog.open(AddEditPrepaidRejectedReasonMasterComponent, {
                            width: '900px',
                            data: {
                                isEdit: true,
                                title: 'Update ' + this.title,
                                yesLabel: 'Update',
                                noLabel: 'Cancel',
                                createAcS: this.createAccess,
                                editAcs: this.editAccess,
                                rejectedReasonData: rejectedReasonData,
                                rejectedSubReasonList: rejectedReasonData.rejectSubReasonDtoList || []
                            }
                        });

                        dialogRef.afterClosed().subscribe(result => {
                            if (result) {
                                // Use 'result' directly as payload data
                                this.saveRejectedReason(id, result);
                            }
                        });
                    } else {
                        this.toastr.error(`${response.error.ERROR}`, "Failed!");
                    }
                },
                error: (error: any) => {
                    this.toastr.error(error.error?.ERROR || 'Failed to fetch rejected reason data', 'Failed!');
                }
            });
        } else if (resultData) {
            this.saveRejectedReason("", resultData);
        }
    }

    pageChangedTrscList(event: PageEvent): void {
        if (this.rejectedReasonItemsPerPage !== event.pageSize) {
            this.rejectedReasonItemsPerPage = event.pageSize;
            this.currentPage = 1;
        } else {
            this.currentPage = event.pageIndex + 1;
        }

        if (this.searchKey) {
            this.searchRejectedReason();
        } else {
            this.getRejectedReasonList();
        }
    }

    deleteConfirmonSubReasonDialog(source: any): void {
        if (!source?.id) return;

        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: "400px",
            data: {
                title: `Delete ${this.title}`,
                description: `Are you sure you want to delete "${source.name}"?`,
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
        this.rejectedReasonService.deleteMethod(`/rejectReason/delete?rejectReasonId=${id}`).subscribe({
            next: (res: any) => {
                this.spinner.hide();
                if (res.status === 200) {
                    this.toastr.success(`${res.msg}`, 'Deleted successfully!');

                    if (this.currentPage > 1 && this.rejectedReasonListData.length === 1) {
                        this.currentPage--;
                    }

                    if (this.searchKey) {
                        this.searchRejectedReason();
                    } else {
                        this.getRejectedReasonList();
                    }
                } else {
                    this.toastr.error(`${res.error.ERROR}`, "Delete error");
                }
            },
            error: (error) => {
                this.toastr.error(`${error.error.ERROR}`, "Failed to delete record");
            }
        });
    }

    openRejectedReasonDetailsDialog(data: any): void {
        this.viewTrscData = data;
        this.currentRejectedSubReasonListData = 1;

        if (data.rejectSubReasonDtoList?.length) {
            this.updatePaginatedSubReasons();
        } else {
            this.paginatedSubReasons = [];
        }

        this.dialog.open(this.rejectedReasonDetailsDialog, {
            width: '900px',
            height: 'auto',
            panelClass: 'custom-dialog-container'
        });
    }

    updatePaginatedSubReasons(): void {
        const startIndex = (this.currentRejectedSubReasonListData - 1) * this.rejectedSubReasonItemsPerPage;
        const endIndex = startIndex + this.rejectedSubReasonItemsPerPage;
        this.paginatedSubReasons = this.viewTrscData.rejectSubReasonDtoList.slice(startIndex, endIndex);
    }

    pageChangedleadSubSourceOnView(event: PageEvent): void {
        this.currentRejectedSubReasonListData = event.pageIndex + 1;
        this.rejectedSubReasonItemsPerPage = event.pageSize;
        this.updatePaginatedSubReasons();
    }


    resetForm(): void {
        this.submitted = false;
        this.rejectedReasonMasterFormGroup.reset();

        this.rejectedSubReasonMapping.clear();
        this.rejectedSubReasonDeletedIds.clear();

        this.rejectedReasonMasterFormGroup.markAsPristine();
        this.rejectedReasonMasterFormGroup.markAsUntouched();
        Object.keys(this.rejectedReasonMasterFormGroup.controls).forEach(key => {
            this.rejectedReasonMasterFormGroup.get(key)?.setErrors(null);
        });
    }

    canExit(): Observable<boolean> {
        if (!this.rejectedReasonMasterFormGroup.dirty && !this.searchForm.dirty) {
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
