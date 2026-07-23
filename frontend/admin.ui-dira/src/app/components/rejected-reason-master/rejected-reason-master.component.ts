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
// import { SALES_CRMS } from "src/app/constants/aclConstants";

// @Component({
//     selector: "app-rejected-reason-master",
//     templateUrl: "./rejected-reason-master.component.html",
//     styleUrls: ["./rejected-reason-master.component.css"],
//     standalone: false
// })
// export class RejectedReasonMasterComponent implements OnInit {
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
//   createAccess: boolean = false;
//   deleteAccess: boolean = false;
//   editAccess: boolean = false;

//   constructor(
//     private fb: UntypedFormBuilder,
//     private spinner: NgxSpinnerService,
//     private confirmationService: ConfirmationService,
//     private messageService: MessageService,
//     private rejectedReasonService: RejectedReasonService,
//     loginService: LoginService
//   ) {
//     this.loginService = loginService;
//     this.AclClassConstants = AclClassConstants;
//     this.AclConstants = AclConstants;
//     this.createAccess = loginService.hasPermission(SALES_CRMS.CREATE_REJECTED_REASON);
//     this.deleteAccess = loginService.hasPermission(SALES_CRMS.DELETE_REJECTED_REASON);
//     this.editAccess = loginService.hasPermission(SALES_CRMS.EDIT_REJECTED_REASON);
//   }

//   ngOnInit(): void {
//     // this.mvnoid = Number.parseInt(localStorage.getItem('mvnoId'));

//     this.rejectedReasonMasterFormGroup = this.fb.group({
//       name: ["", Validators.required],
//       status: ["", Validators.required]
//     });

//     this.rejecteSubReasonMappingForm = this.fb.group({
//       name: ["", Validators.required]
//     });

//     this.searchRejectedReasonFormGroup = this.fb.group({
//       searchTrscName: ["", Validators.required]
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
//           filterCondition: ""
//         }
//       ],
//       filterBy: ""
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
//       status: ""
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
//       let data: any = [];
//       this.searchData.filters[0].filterColumn = "name";
//       this.searchData.filters[0].filterValue =
//         this.searchRejectedReasonFormGroup.controls["searchTrscName"].value.trim();
//       this.searchData.page = this.currentRejectedReasonListData;
//       this.searchData.pageSize = this.rejectedReasonItemsPerPage;
//       this.searchData.filterBy = this.searchData.filters[0].filterColumn;

//       console.log(this.searchData);

//       data = this.searchData;

//       const url = "/rejectReason/search";
//       this.rejectedReasonService.postMethod(url, data).subscribe(
//         (response: any) => {
//           if (response.status == 200) {
//             if (response.message) {
//               this.messageService.add({
//                 severity: "info",
//                 summary: "Info",
//                 detail: response.message,
//                 icon: "far fa-times-circle"
//               });
//             } else {
//               this.messageService.add({
//                 severity: "success",
//                 summary: "Successfully",
//                 detail: "Record fetched successfully",
//                 icon: "far fa-times-circle"
//               });
//             }
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
//         icon: "far fa-times-circle"
//       });
//     }
//   }

//   clearSearchTrsc() {
//     this.searchTrscName = "";
//     this.getRejectedReasonList("");
//     this.submitted = false;
//     this.rejectedSubReasonSubmitted = false;
//     this.searchSubmitted = false;
//     this.onRemoveRejectedSubReasonMapping(this.rejecteSubReasonMappingForm.value);
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
//       name: [this.rejecteSubReasonMappingForm.value.name]
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
//             detail: "You have rejected the request!"
//           });
//         }
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
//             icon: "far fa-check-circle"
//           });
//         } else {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: response.errorMessage,
//             icon: "far fa-times-circle"
//           });
//         }
//         if (this.currentRejectedReasonListData != 1 && this.rejectedReasonDataList?.length == 1) {
//           this.currentRejectedReasonListData = this.rejectedReasonDataList - 1;
//         }
//         this.clearRejectedReasonMasterData();
//         this.getRejectedReasonList("");
//       },
//       (error: any) => {
//         if (error.status === 417) {
//           this.messageService.add({
//             severity: "info",
//             summary: "Info",
//             detail: error.error.errorMessage
//           });
//         } else {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: error.error.errorMessage,
//             icon: "far fa-times-circle"
//           });
//         }
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
//             detail: "You have rejected"
//           });
//         }
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
//           rejectSubReasonDeletedIds: []
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
//               rejectReasonId: dataObj.id
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
//                 icon: "far fa-check-circle"
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
//                 icon: "far fa-times-circle"
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
//               icon: "far fa-times-circle"
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
//               rejectReasonId: null
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
//                 icon: "far fa-check-circle"
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
//                 icon: "far fa-times-circle"
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
//               icon: "far fa-times-circle"
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
//             status: this.editRejectedReasonMasterData.status
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
//             icon: "far fa-times-circle"
//           });
//         }
//       },
//       (error: any) => {
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.error.ERROR,
//           icon: "far fa-times-circle"
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
//           }
//         });
//         return false;
//       });
//     }
//   }
// }

import { Component, OnInit, TemplateRef, ViewChild, ViewEncapsulation, AfterViewInit } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormArray } from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatDialog } from "@angular/material/dialog";
import { Observable, Observer } from "rxjs";

import { RejectedReasonService } from "src/app/service/rejected-reason.service";
import { LoginService } from "src/app/service/login.service";
import { SALES_CRMS } from "src/app/constants/aclConstants";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { AddEditRejectedReasonMasterComponent } from "./add-edit-rejected-reason-master/add-edit-rejected-reason-master.component";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { IDeactivateGuard } from "src/app/service/deactivate.service";
import { ToastrService } from "ngx-toastr";

@Component({
    selector: "app-rejected-reason-master",
    templateUrl: "./rejected-reason-master.component.html",
    styleUrls: ["./rejected-reason-master.component.css"],
    standalone: false,
    encapsulation: ViewEncapsulation.None
})
export class RejectedReasonMasterComponent implements OnInit, AfterViewInit, IDeactivateGuard {
    title = "Rejected Reason Master";

    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    viewAccess: boolean = false;
    isRejectedReasonMasterEdit: boolean = false;

    rejectedReasonFormGroup!: UntypedFormGroup;
    searchForm!: UntypedFormGroup;

    submitted: boolean = false;
    searchSubmitted: boolean = false;

    rejectedReasonListData: any[] = [];
    dataSource = new MatTableDataSource<any>([]);
    rejectedReasonTotalRecords: number = 0;
    rejectedReasonItemsPerPage: number = RadiusConstants.ITEMS_PER_PAGE;
    currentPage: number = 1;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    statusOptions = RadiusConstants.status;

    searchKey: string = "";
    searchRejectedReasonName: string = "";

    viewTrscData: any;
    paginatedSubReasons: any[] = [];
    rejectedSubReasonItemsPerPage: number = RadiusConstants.ITEMS_PER_PAGE;
    currentRejectedSubReasonListData: number = 1;
    subReasonDisplayedColumns: string[] = ['subSource'];

    @ViewChild('rejectedReasonDetailsDialog') rejectedReasonDetailsDialog!: TemplateRef<any>;
    @ViewChild(MatSort) sort!: MatSort;
    @ViewChild(MatPaginator) paginator!: MatPaginator;

    displayedColumns = ["id", "name", "status", "action"];

    rejectedSubReasonMapping!: FormArray;
    rejectedSubReasonDeletedIds!: FormArray;

    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private rejectedReasonService: RejectedReasonService,
        private dialog: MatDialog, private toastr: ToastrService,
        public loginService: LoginService
    ) {
        this.createAccess = this.loginService.hasPermission(SALES_CRMS.CREATE_REJECTED_REASON);
        this.deleteAccess = this.loginService.hasPermission(SALES_CRMS.DELETE_REJECTED_REASON);
        this.editAccess = this.loginService.hasPermission(SALES_CRMS.EDIT_REJECTED_REASON);
        this.viewAccess = this.loginService.hasPermission(SALES_CRMS.EDIT_REJECTED_REASON);
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(SALES_CRMS.EDIT_REJECTED_REASON) || this.loginService.hasPermission(SALES_CRMS.DELETE_REJECTED_REASON)) {
            return ["id", "name", "status", "action"];
        } else {
            return ["id", "name", "status"];
        }
    }
    ngOnInit(): void {
        this.rejectedReasonFormGroup = this.fb.group({
            name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            status: ["", Validators.required],
            rejectSubReasonList: this.fb.array([]),
            rejectedSubReasonDeletedIds: this.fb.array([])
        });

        this.searchForm = this.fb.group({
            searchText: ['']
        });

        this.rejectedSubReasonMapping = this.fb.array([]);
        this.rejectedSubReasonDeletedIds = this.fb.array([]);


        this.getRejectedReasonList();
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    trackByRejectedReasonId(index: number, item: any): any {
        return item.id ?? index;
    }

    trackBySubReasonId(index: number, item: any): any {
        return item.id ?? index;
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
                    this.rejectedReasonListData = [];
                    this.rejectedReasonTotalRecords = 0;
                    this.dataSource.data = [];
                    this.updatePaginator();
                }
            },
            error: (error) => {
                this.spinner.hide();
                this.rejectedReasonListData = [];
                this.rejectedReasonTotalRecords = 0;
                this.dataSource.data = [];
                this.updatePaginator();
                this.handleError(error);
            }
        });
    }

    private updatePaginator(): void {
        if (this.paginator) {
            this.dataSource = new MatTableDataSource<any>(this.rejectedReasonListData);
            this.dataSource.paginator = this.paginator;
            this.dataSource.sort = this.sort;
            this.paginator.length = this.rejectedReasonTotalRecords;
            this.paginator.pageIndex = this.currentPage - 1;
        }
    }

    searchRejectedReason(): void {
        const keyword = this.searchRejectedReasonName.trim();
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
                    this.toastr.info(res.responseMessage, "No records found.");
                    this.clearSearchTrsc();
                }
            },
            error: (error) => {
                this.toastr.error(`${error.error.ERROR}`, 'Search failed.');
            }
        });
    }

    clearSearchTrsc(): void {
        this.searchKey = "";
        this.searchRejectedReasonName = "";
        this.searchForm.reset();
        this.currentPage = 1;
        this.getRejectedReasonList();
    }

    pageChangedTrscList(event: PageEvent): void {
        this.currentPage = event.pageIndex + 1;
        this.rejectedReasonItemsPerPage = event.pageSize;

        if (this.searchKey) {
            this.searchRejectedReason();
        } else {
            this.getRejectedReasonList();
        }
    }

    TotalItemPerPage(event: any): void {
        this.currentPage = 1;
        this.rejectedReasonItemsPerPage = Number(event.value);

        if (this.searchKey) {
            this.searchRejectedReason();
        } else {
            this.getRejectedReasonList();
        }
    }

    addEditRejectedReasonDialog(): void {
        const dialogRef = this.dialog.open(AddEditRejectedReasonMasterComponent, {
            width: '900px',
            autoFocus: false,
            data: {
                isEdit: false,
                title: 'Create ' + this.title,
                addLabel: true,
                yesLabel: 'Create',
                noLabel: 'Cancel',
                createAcS: this.createAccess,
                editAcs: this.editAccess
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.editRejectedReasonMasterDataFunction("", result);
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

                        const dialogRef = this.dialog.open(AddEditRejectedReasonMasterComponent, {
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

                this.toastr.success(`${response.message}`, "Successfully " + (rejectedReasonId ? "Updated" : "Created"));

                if (this.searchKey) {
                    this.searchRejectedReason();
                } else {
                    this.getRejectedReasonList();
                }
            },
            error: (error: any) => {
                this.toastr.error(error.error?.ERROR || error.error.errorMessage || "Failed to save rejected reason.", 'Failed!');

            }
        });
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
                    this.toastr.success(`${res.message}`, 'Deleted successfully!');

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

    private handleError(error: any): void {
        if (error.error) {
            if (error.error.status === 417 || error.error.status === 406) {
                this.toastr.info(error.ERROR || error.responseMessage, 'Info!');
            } else {
                this.toastr.error(error.error?.ERROR || error.error.errorMessage);

            }
        } else {
            this.toastr.error(`${error.error.ERROR}`, "An unexpected error occurred");
        }
    }

    resetForm(): void {
        this.submitted = false;
        this.rejectedReasonFormGroup.reset();
        this.rejectedReasonFormGroup.markAsPristine();
        this.rejectedReasonFormGroup.markAsUntouched();
        Object.keys(this.rejectedReasonFormGroup.controls).forEach(key => {
            this.rejectedReasonFormGroup.get(key)?.setErrors(null);
        });
    }

    canExit(): Observable<boolean> {
        if (!this.rejectedReasonFormGroup.dirty && !this.searchForm.dirty) {
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
                },
            });
        });
    }
}
