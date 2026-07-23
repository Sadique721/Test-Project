// import { Component, OnInit } from "@angular/core";
// import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, FormArray } from "@angular/forms";
// import { MessageService } from "primeng/api";
// import { NgxSpinnerService } from "ngx-spinner";
// import { ConfirmationService } from "primeng/api";
// import { CountryManagementService } from "src/app/service/country-management.service";
// import { Regex } from "src/app/constants/regex";
// import { CountryManagement } from "src/app/components/model/country-management";
// import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
// import { LoginService } from "src/app/service/login.service";
// import { AclClassConstants } from "src/app/constants/aclClassConstants";
// import { AclConstants } from "src/app/constants/aclOperationConstants";
// import { BuildingConfig } from "src/app/RadiusUtils/RadiusConstants";
// import { IDeactivateGuard } from "src/app/service/deactivate.service";
// import { Observable, Observer } from "rxjs";
// import { resolve } from "dns";
// import { ObserversModule } from "@angular/cdk/observers";
// import { CommondropdownService } from "src/app/service/commondropdown.service";
// import { MASTERS } from "src/app/constants/aclConstants";
// import { WhiteeSpaceValidator } from "../shared/custom-validators";
// import { BuildingConfigManagementService } from "src/app/service/building-config-management.service";

// import { MatTableDataSource } from '@angular/material/table';
// import { MatPaginator } from '@angular/material/paginator';
// import { MatSort } from '@angular/material/sort';
// import { ViewChild, AfterViewInit } from '@angular/core';

// @Component({
//     selector: "app-building-config-management",
//     templateUrl: "./building-config-management.component.html",
//     styleUrls: ["./building-config-management.component.css"],
//     standalone: false
// })
// export class BuidingConfigManagement implements OnInit,AfterViewInit, IDeactivateGuard {
//   title = BuildingConfig;
//     displayedColumns: string[] = ['id', 'Name', 'BuildingConfigMapping'];
//   dataSource: MatTableDataSource<any> = new MatTableDataSource();

//   @ViewChild(MatPaginator) paginator: MatPaginator;
//   @ViewChild(MatSort) sort: MatSort;
//   countryListData: any[] = [];

//   createAccess: boolean = false;
//   deleteAccess: boolean = false;
//   editAccess: boolean = false;
//   branchData: any = [];
//   buildingconfFormGroup: UntypedFormGroup;
//   submitted: boolean = false;
//   countryData: CountryManagement;
//   isCountryEdit: boolean = false;
//   viewCountryListData: any;
//   currentPageCountrySlab = 1;
//   countryitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
//   countrytotalRecords: any;
//   searchCountryName: any = "";
//   searchData: any;
//   statusOptions = RadiusConstants.status;
//   pageLimitOptions = RadiusConstants.pageLimitOptions;
//   showItemPerPage: any;
//   searchkey: string;
//   public loginService: LoginService;
//   bankTypeData: any;
//   dunningData: any;
//   constructor(
//     private fb: UntypedFormBuilder,
//     private spinner: NgxSpinnerService,
//     private confirmationService: ConfirmationService,
//     private messageService: MessageService,
//     private countryManagementService: CountryManagementService,
//     private commondropdownService: CommondropdownService,
//     loginService: LoginService,
//     private buidingConfigManagement: BuildingConfigManagementService
//   ) {
//     this.loginService = loginService;
//     this.createAccess = loginService.hasPermission(MASTERS.BUILDING_CONFIG_CREATE);
//   }

//   ngOnInit(): void {
//     this.buildingconfFormGroup = this.fb.group({
//       name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
//       mappingFrom: ["", Validators.required]
//     });

//     this.searchData = {
//       filters: [
//         {
//           filterDataType: "",
//           filterValue: "",
//           filterColumn: "any",
//           filterOperator: "equalto",
//           filterCondition: "and"
//         }
//       ],
//       page: "",
//       pageSize: ""
//     };
//      this.getbuildingRefrenceListData();
//     this.getmappingFrom();
//   }

//   ngAfterViewInit() {
//     this.dataSource.paginator = this.paginator;
//     this.dataSource.sort = this.sort;
//   }

//   canExit() {
//     if (!this.buildingconfFormGroup.dirty) return true;
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
//   addEditCountry() {
//     this.submitted = true;
//     if (this.buildingconfFormGroup.valid) {
//       {
//         const url = "/buildingRefrence/save";
//         this.branchData = this.buildingconfFormGroup.value;
//         this.buidingConfigManagement.postMethod(url, this.branchData).subscribe(
//           (response: any) => {
//             if (
//               response.responseCode == 406 ||
//               response.responseCode == 405 ||
//               response.responseCode == 417
//             ) {
//               this.messageService.add({
//                 severity: "info",
//                 summary: "Info",
//                 detail: response.responseMessage,
//                 icon: "far fa-times-circle"
//               });
//             } else {
//               this.submitted = false;
//               //   this.buildingconfFormGroup.controls.status.setValue("");
//               this.messageService.add({
//                 severity: "success",
//                 summary: "Successfully",
//                 detail: response.responseMessage,
//                 icon: "far fa-check-circle"
//               });
//             }
//             this.clearSearchCountry();
//             this.buildingconfFormGroup.reset();
//             this.commondropdownService.clearCache("/buildingRefrence/all");
//           },
//           (error: any) => {
//             this.messageService.add({
//               severity: "error",
//               summary: "Error",
//               detail: error.error.ERROR,
//               icon: "far fa-times-circle"
//             });
//           }
//         );
//       }
//     }
//   }

//   getbuildingRefrenceListData() {
//     const url = "/buildingRefrence/all";
//     this.searchkey = "";
//     this.buidingConfigManagement.getMethod(url).subscribe(
//       (response: any) => {
//         this.countryListData = response.dataList || [];
//         this.dataSource.data = this.countryListData; // Update dataSource for table
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

//   pageChangedCountryList(event: any) {
//     // Perform API call or update dataSource here as required for server-side paging,
//     // else just display the page - adjust as per your scenario
//     this.countryitemsPerPage = event.pageSize;
//     this.currentPageCountrySlab = event.pageIndex + 1;
//     this.getbuildingRefrenceListData(); // if server-side paging, supply page params in API
//   }


//   clearSearchCountry() {
//     this.searchCountryName = "";
//     this.searchkey = "";
//     this.getbuildingRefrenceListData();
//     this.submitted = false;
//     this.isCountryEdit = false;
//     this.buildingconfFormGroup.reset();
//     // this.buildingconfFormGroup.controls.name.setValue("");
//   }

//   getmappingFrom() {
//     const url = "/commonList/buildingRefrence";
//     this.buidingConfigManagement.getMethodWithCache(url).subscribe(
//       (response: any) => {
//         this.dunningData = response.dataList;

//         this.searchkey = "";
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
// }
import { Component, OnInit, AfterViewInit, ViewChild } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup } from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { CountryManagementService } from "src/app/service/country-management.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { LoginService } from "src/app/service/login.service";
import { IDeactivateGuard } from "src/app/service/deactivate.service";
import { Observable, Observer } from "rxjs";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { MASTERS } from "src/app/constants/aclConstants";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from "@angular/material/sort";
import { MatDialog } from "@angular/material/dialog";
import { ToastrService } from 'ngx-toastr';
import { AddEditBuildingConfigManagementComponent } from "./add-edit-building-config-management/add-edit-building-config-management.component";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";

@Component({
  selector: "app-building-config-management",
  templateUrl: "./building-config-management.component.html",
  styleUrls: ["./building-config-management.component.css"],
  standalone: false
})
export class BuidingConfigManagement implements OnInit, AfterViewInit, IDeactivateGuard {
  title = RadiusConstants.BuildingConfig;

  displayedColumns: string[] = ['id', 'Name', 'BuildingConfigMapping'];
  dataSource = new MatTableDataSource<any>([]);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  buildingconfFormGroup!: UntypedFormGroup;
  submitted = false;
  countryListData: any[] = [];
  currentPage = 1;
  itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
  totalRecords!: number;
  searchkey = '';
  createAccess = false;
  editAccess = false;
 isCountryEdit: boolean = false;
  constructor(
    private fb: UntypedFormBuilder,
    private spinner: NgxSpinnerService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private countryManagementService: CountryManagementService,
    private commondropdownService: CommondropdownService,
    public loginService: LoginService,
     private toastr: ToastrService,
    private buildingConfigManagementService: CountryManagementService,
    private dialog: MatDialog
  ) {
    this.createAccess = loginService.hasPermission(MASTERS.BUILDING_CONFIG_CREATE);
    this.editAccess = loginService.hasPermission(MASTERS.BUILDING_CONFIG_CREATE); // adjust if needed
  }

  ngOnInit(): void {
    this.buildingconfFormGroup = this.fb.group({
      name: ['', [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
      mappingFrom: ['', Validators.required]
    });
    this.getBuildingReferenceListData();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  canExit(): Observable<boolean> | boolean {
    if (!this.buildingconfFormGroup.dirty) return true;
    return new Observable((observer: Observer<boolean>) => {
      this.confirmationService.confirm({
        header: 'Alert',
        message: 'The filled data will be lost. Do you want to continue? (Yes/No)',
        icon: 'pi pi-info-circle',
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

  getBuildingReferenceListData(pageSize: number = this.itemsPerPage, page: number = this.currentPage): void {
    const url = '/buildingRefrence/all'; // Adjust API if needed for paging

    this.buildingConfigManagementService.getMethod(url).subscribe(
      (res: any) => {
        this.countryListData = res.dataList || [];
        this.dataSource.data = this.countryListData;
        this.totalRecords = res.totalRecords > 0 ? res.totalRecords : this.countryListData.length;
      },
      (error: any) =>  this.toastr.error(`${error.error.ERROR}`, 'Failed!')
    );
  }


  pageChangedCountryList(event: PageEvent): void {
    this.itemsPerPage = event.pageSize;
    this.currentPage = event.pageIndex + 1;
    this.getBuildingReferenceListData(this.itemsPerPage, this.currentPage);
  }

  clearSearchCountry(): void {
    this.searchkey = '';
    this.getBuildingReferenceListData();
    this.submitted = false;
    this.buildingconfFormGroup.reset();
  }

  addEditCountryDialog(data?: any): void {
  const dialogRef = this.dialog.open(AddEditBuildingConfigManagementComponent, {
    width: '600px',
    data: {
      isEdit: !!data,
      title: data ? `Update ${this.title}` : `Create ${this.title}`,
      createAcS: this.createAccess,
      editAcs: this.editAccess,
      countryData: data || null,
      countryListData: this.countryListData
    }
  });

  dialogRef.afterClosed().subscribe(result => {
    if (result) {
      // Call API to save building config with the result from dialog
      const url = '/buildingRefrence/save';
      this.buildingConfigManagementService.postMethod(url, result).subscribe(
        (response: any) => {
          if ([406, 405, 417].includes(response.responseCode)) {
            this.toastr.info(response.responseMessage, 'Info!');
          } else {
            this.toastr.success(`${response.responseMessage}`, 'Success!');
            this.clearSearchCountry();
            this.getBuildingReferenceListData();
          }
        },
        (error: any) => {
          this.toastr.error(`${error.error.ERROR}`, 'Failed!');
        }
      );
    }
  });
}



  deleteConfirmDialog(item: any): void {
    const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
      width: '400px',
      data: {
        title: `Delete ${this.title}`,
        description: `Are you sure you want to delete "${item.name}"?`,
        yesLabel: 'Delete',
        noLabel: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.deleteBuildingConfig(item.id);
      }
    });
  }

  deleteBuildingConfig(id: any): void {
    const url = `/buildingRefrence/${id}`;
    this.buildingConfigManagementService.deleteMethod(url).subscribe(
      (response: any) => {
       this.toastr.success(`${response.responseMessage}`, 'Success!');
        this.getBuildingReferenceListData();
      },
      (error: any) =>  this.toastr.error(`${error.error.ERROR}`, 'Failed!')
    );
  }
}
