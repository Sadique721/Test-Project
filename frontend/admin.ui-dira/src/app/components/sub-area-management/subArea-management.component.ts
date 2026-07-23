// import { Component, ElementRef, OnInit, ViewChild } from "@angular/core";
// import { FormBuilder, Validators, FormGroup } from "@angular/forms";
// import { MessageService } from "primeng/api";
// import { ConfirmationService } from "primeng/api";
// import { CountryManagement } from "src/app/components/model/country-management";
// import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
// import { LoginService } from "src/app/service/login.service";
// import { AREA, CITY, COUNTRY, STATE, SUBAREA, PINCODE } from "src/app/RadiusUtils/RadiusConstants";
// import { IDeactivateGuard } from "src/app/service/deactivate.service";
// import { Observable, Observer } from "rxjs";
// import { MASTERS } from "src/app/constants/aclConstants";
// import { WhiteeSpaceValidator } from "../shared/custom-validators";
// import { SubAreaManagementService } from "src/app/service/subArea-management.service";
// import { DomSanitizer } from "@angular/platform-browser";
// import { saveAs as importedSaveAs } from "file-saver";

// @Component({
//   selector: "app-subArea-management",
//   templateUrl: "./subArea-management.component.html",
//   styleUrls: ["./subArea-management.component.css"]
// })
// export class SubAreaManagementComponent implements OnInit, IDeactivateGuard {
//   @ViewChild("fileInput") fileInput: ElementRef;
//   title = SUBAREA;
//   countryTitle = COUNTRY;
//   cityTitle = CITY;
//   stateTitle = STATE;
//   areaTitle = AREA;
//   mvnoId: any;
//   inputshowSelsctData: boolean = false;
//   createAccess: boolean = false;
//   deleteAccess: boolean = false;
//   editAccess: boolean = false;
//   subAreaFormGroup: FormGroup;
//   submitted: boolean = false;
//   countryData: CountryManagement;
//   subAreaManagement: any;
//   countryListData: any;
//   isSubAreaEdit: boolean = false;
//   viewCountryListData: any;
//   currentPageSubAreaSlab = 1;
//   subareaitemsPerPage = RadiusConstants.PER_PAGE_ITEMS;
//   subareaTotalRecords: any;
//   searchSubAreaName: any = "";
//   searchData: any;
//   statusOptions = [
//     { label: "Active", value: "Y", val: "ACTIVE" },
//     { label: "Inactive", value: "N", val: "INACTIVE" },
//     { label: "UnderDevelopment", value: "U", val: "UNDERDEVELOPMENT" }
//   ];
//   pageLimitOptions = RadiusConstants.pageLimitOptions;
//   currentPageAreaListdata = 1;
//   areaListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
//   areaListdatatotalRecords: any;
//   showItemPerPage: any;
//   searchkey: string;
//   public loginService: LoginService;
//   subAreaListData: any;
//   cityListData: any;
//   stateListData: any;
//   areaListData: any[];
//   cityDetail: any;
//   selectedCityList: any;
//   selectedStateList: any;
//   selectedCountryList: any;
//   isAreaSelected: boolean;
//   selectedFilePreview: any[];
//   selectedFile: any;
//   downloadDocumentId: boolean = false;
//   previewUrl: any;
//   documentPreview: boolean = false;
//   subAreaIdData: any;
//   subAreaFileData: any;
//   multiFiles: FileList;
//   subAreaDocShowData: any;
//   pincodeListData: any;
//   pincodeTitle = PINCODE;
//   pincodeName: any;
//   selectedFilePreviewd: any[];
//   constructor(
//     private fb: FormBuilder,
//     private confirmationService: ConfirmationService,
//     private messageService: MessageService,
//     private subAreaManagementService: SubAreaManagementService,
//     loginService: LoginService,
//     private sanitizer: DomSanitizer
//   ) {
//     this.loginService = loginService;
//     this.createAccess = loginService.hasPermission(MASTERS.SUBAREA_CREATE);
//     this.deleteAccess = loginService.hasPermission(MASTERS.SUBAREA_DELETE);
//     this.editAccess = loginService.hasPermission(MASTERS.SUBAREA_EDIT);
//   }

//   ngOnInit(): void {
//     this.subAreaFormGroup = this.fb.group({
//       name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
//       status: ["", Validators.required],
//       countryId: ["", Validators.required],
//       cityId: ["", Validators.required],
//       stateId: ["", Validators.required],
//       areaId: ["", Validators.required],
//       file: [""]
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
//     this.getSubAreaList("");
//     this.getCountryList();
//     this.getStateList();
//     this.getCityList();
//     this.getAreaList();
//     this.getPincodeList();
//     this.mvnoId = Number(localStorage.getItem("mvnoId"));
//   }

//   canExit() {
//     if (!this.subAreaFormGroup.dirty) return true;
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

//   addEditSubarea(id) {
//     this.submitted = true;
//     if (this.subAreaFormGroup.valid) {
//       const formData = new FormData();
//       let fileArray: FileList;
//       if (
//         this.subAreaFormGroup.controls.file.value != null &&
//         this.subAreaFormGroup.controls.file.value != ""
//       ) {
//         if (
//           this.selectedFile.type != "image/png" &&
//           this.selectedFile.type != "image/jpg" &&
//           this.selectedFile.type != "image/jpeg" &&
//           this.selectedFile.type != "application/pdf"
//         ) {
//           this.subAreaFormGroup.controls.file.reset();
//           this.messageService.add({
//             severity: "info",
//             summary: "Info",
//             detail: "File type must be png, jpg, jpeg or pdf",
//             icon: "far fa-check-circle"
//           });
//         } else {
//           fileArray = this.multiFiles;
//           console.log("fileArray :::", fileArray);
//           Array.from(fileArray).forEach(file => {
//             formData.append("file", file);
//           });
//         }
//       }
//       this.subAreaManagement = this.subAreaFormGroup.value;
//       this.subAreaManagement.isDeleted = false;
//       this.subAreaManagement.mvnoId = this.mvnoId;
//       this.subAreaManagement.buId = null;
//       if (id) {
//         const url = "/subarea/update";
//         this.subAreaManagement.id = Number(id);
//         this.subAreaManagement.filename = this.viewCountryListData.filename;
//         this.subAreaManagement.uniquename = this.viewCountryListData.uniquename;
//         let newFormData = Object.assign({}, this.subAreaManagement);
//         delete newFormData.file;
//         formData.append("entityDTO", JSON.stringify(newFormData));
//         this.subAreaManagementService.postMethod(url, formData).subscribe(
//           (response: any) => {
//             if (response.responseCode == 406) {
//               this.messageService.add({
//                 severity: "error",
//                 summary: "Error",
//                 detail: response.responseMessage,
//                 icon: "far fa-times-circle"
//               });
//             } else {
//               this.submitted = false;
//               this.isSubAreaEdit = false;
//               this.isAreaSelected = false;
//               this.subAreaFormGroup.reset();
//               this.subAreaFormGroup.controls.file.reset();
//               this.selectedFile = "";
//               this.selectedFilePreview = [];
//               this.selectedFilePreviewd = [];
//               this.pincodeName = "";
//               this.subAreaFormGroup.controls.status.setValue("");
//               if (this.fileInput) {
//                 this.fileInput.nativeElement.value = "";
//               }
//               this.subAreaManagementService.clearCache("/subarea/all");
//               this.messageService.add({
//                 severity: "success",
//                 summary: "Successfully",
//                 detail: response.msg,
//                 icon: "far fa-check-circle"
//               });
//               this.submitted = false;
//               if (this.searchkey) {
//                 this.searchSubArea();
//               } else {
//                 this.getSubAreaList("");
//               }
//             }
//           },
//           (error: any) => {
//             if (error.error.status == 417 || error.error.status == 406) {
//               this.messageService.add({
//                 severity: "info",
//                 summary: "Info",
//                 detail: error.error.ERROR,
//                 icon: "far fa-times-circle"
//               });
//             } else {
//               this.messageService.add({
//                 severity: "error",
//                 summary: "Error",
//                 detail: error.error.ERROR,
//                 icon: "far fa-times-circle"
//               });
//             }
//           }
//         );
//       } else {
//         const url = "/subarea/save";
//         let newFormData = Object.assign({}, this.subAreaManagement);
//         delete newFormData.file;
//         formData.append("entityDTO", JSON.stringify(newFormData));
//         this.subAreaManagementService.postMethod(url, formData).subscribe(
//           (response: any) => {
//             if (response.responseCode == 200) {
//               this.submitted = false;
//               this.subAreaFormGroup.reset();
//               this.subAreaFormGroup.controls.file.reset();
//               this.selectedFile = "";
//               this.selectedFilePreview = [];
//               this.selectedFilePreviewd = [];
//               this.isAreaSelected = false;
//               this.pincodeName = "";
//               this.subAreaFormGroup.controls.status.setValue("");
//               if (this.fileInput) {
//                 this.fileInput.nativeElement.value = "";
//               }
//               this.subAreaManagementService.clearCache("/subarea/all");
//               this.messageService.add({
//                 severity: "success",
//                 summary: "Successfully",
//                 detail: response.msg,
//                 icon: "far fa-check-circle"
//               });
//               if (this.searchkey) {
//                 this.searchSubArea();
//               } else {
//                 this.getSubAreaList("");
//               }
//             } else {
//               this.messageService.add({
//                 severity: "info",
//                 summary: "Info",
//                 detail: response.responseMessage,
//                 icon: "far fa-times-circle"
//               });
//             }
//           },
//           (error: any) => {
//             if (error.error.status == 417 || error.error.status == 406) {
//               this.messageService.add({
//                 severity: "info",
//                 summary: "Info",
//                 detail: error.error.ERROR,
//                 icon: "far fa-times-circle"
//               });
//             } else {
//               this.messageService.add({
//                 severity: "error",
//                 summary: "Error",
//                 detail: error.error.ERROR,
//                 icon: "far fa-times-circle"
//               });
//             }
//           }
//         );
//       }
//     }
//   }

//   TotalItemPerPage(event) {
//     this.showItemPerPage = Number(event.value);
//     if (this.currentPageSubAreaSlab > 1) {
//       this.currentPageSubAreaSlab = 1;
//     }
//     if (!this.searchkey) {
//       this.getSubAreaList(this.showItemPerPage);
//     } else {
//       this.searchSubArea();
//     }
//   }

//   editSubarea(id) {
//     if (id) {
//       const url = "/subarea/" + id;
//       this.subAreaManagementService.getMethod(url).subscribe(
//         (response: any) => {
//           this.isSubAreaEdit = true;
//           this.viewCountryListData = response.data;

//           // Patch form values with the response data
//           this.subAreaFormGroup.patchValue(this.viewCountryListData);

//           // Set selectedCityList, selectedStateList, and selectedCountryList based on the response
//           const selectedArea = this.viewCountryListData;

//           // Update city, state, and country lists based on the selected area
//           this.selectedCityList = this.cityListData.filter(city => city.id === selectedArea.cityId);
//           this.selectedStateList = this.stateListData.filter(
//             state => state.id === selectedArea.stateId
//           );
//           this.selectedCountryList = this.countryListData.filter(
//             country => country.id === selectedArea.countryId
//           );
//           let filterPincodeId = this.areaListData?.find(
//             areaId => areaId.id === selectedArea.areaId
//           );
//           let filterPincode = this.pincodeListData?.find(
//             x => x?.pincodeid === filterPincodeId?.pincodeId
//           );
//           this.pincodeName = filterPincode?.pincode;
//           console.log(filterPincode, this.pincodeName);

//           this.selectedFilePreviewd = [];
//           this.selectedFilePreviewd = this.viewCountryListData.filename
//             .split(",")
//             .map(name => ({ name: name.trim(), status: "old" }));

//           // Disable the dropdowns for City, State, and Country after patching the values
//           this.isAreaSelected = true;
//         },
//         (error: any) => {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: error.error.ERROR,
//             icon: "far fa-times-circle"
//           });
//         }
//       );
//     }
//   }

//   clearsearchSubArea() {
//     this.searchSubAreaName = "";
//     this.searchkey = "";
//     this.pincodeName = "";
//     // this.getSubAreaList("");
//     if (this.searchkey) {
//       this.searchSubArea();
//     } else {
//       this.getSubAreaList("");
//     }
//     this.submitted = false;
//     this.isSubAreaEdit = false;
//     this.subAreaFormGroup.reset();
//     this.subAreaFormGroup.controls.status.setValue("");
//     this.selectedFilePreview = [];
//     this.selectedFilePreviewd = [];
//   }

//   deleteConfirmonSubarea(subAreaManagement) {
//     if (subAreaManagement) {
//       this.confirmationService.confirm({
//         message: "Do you want to delete this " + this.title + "?",
//         header: "Delete Confirmation",
//         icon: "pi pi-info-circle",
//         accept: () => {
//           this.deleteSubarea(subAreaManagement);
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

//   deleteSubarea(subAreaManagement) {
//     const url = "/subarea/delete/" + subAreaManagement?.id;
//     this.subAreaManagementService.deleteMethod(url).subscribe(
//       (response: any) => {
//         if (this.currentPageSubAreaSlab != 1 && this.subAreaListData.length == 1) {
//           this.currentPageSubAreaSlab = this.currentPageSubAreaSlab - 1;
//         }
//         if (
//           response.responseCode == 405 ||
//           response.responseCode == 406 ||
//           response.responseCode == 417
//         ) {
//           this.messageService.add({
//             severity: "info",
//             summary: "Info",
//             detail: response.responseMessage,
//             icon: "far fa-times-circle"
//           });
//         } else {
//           this.messageService.add({
//             severity: "success",
//             summary: "Successfully",
//             detail: response.responseMessage,
//             icon: "far fa-check-circle"
//           });
//         }
//         this.clearsearchSubArea();
//       },
//       (error: any) => {
//         console.log(error, "error");
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.responseMessage,
//           icon: "far fa-times-circle"
//         });
//       }
//     );
//   }

//   pageChangedCountryList(pageNumber) {
//     this.currentPageSubAreaSlab = pageNumber;
//     if (this.searchkey) {
//       this.searchSubArea();
//     } else {
//       this.getSubAreaList("");
//     }
//   }

//   getSubAreaList(list) {
//     // const url = "/country/all"
//     // this.subAreaManagementService.getMethod(url).subscribe((response: any) => {
//     const url = "/subarea";
//     let size;
//     this.searchkey = "";
//     let pageList = this.currentPageSubAreaSlab;
//     if (list) {
//       size = list;
//       this.subareaitemsPerPage = list;
//     } else {
//       size = this.subareaitemsPerPage;
//     }
//     let plandata = {
//       page: pageList,
//       pageSize: size
//     };
//     this.subAreaManagementService.postMethod(url, plandata).subscribe(
//       (response: any) => {
//         this.subAreaListData = response.dataList;
//         this.subareaTotalRecords = response.totalRecords;

//         console.log("this.subareaTotalRecords", this.subareaTotalRecords);

//         this.searchkey = "";
//       },
//       (error: any) => {
//         // console.log(error, "error")
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.error.ERROR,
//           icon: "far fa-times-circle"
//         });
//       }
//     );
//   }

//   searchSubArea() {
//     if (!this.searchkey || this.searchkey !== this.searchSubAreaName) {
//       this.currentPageSubAreaSlab = 1;
//     }
//     this.searchkey = this.searchSubAreaName;
//     if (this.showItemPerPage) {
//       this.subareaitemsPerPage = this.showItemPerPage;
//     }
//     this.searchData.filters[0].filterValue = this.searchSubAreaName.trim();
//     this.searchData.page = this.currentPageSubAreaSlab;
//     this.searchData.pageSize = this.subareaitemsPerPage;

//     const url = "/subarea";
//     // console.log("this.searchData", this.searchData)
//     this.subAreaManagementService.postMethod(url, this.searchData).subscribe(
//       (response: any) => {
//         this.subAreaListData = response.dataList;
//         this.subareaTotalRecords = response.dataList.totalRecords;
//       },
//       (error: any) => {
//         this.subareaTotalRecords = 0;
//         if (error.error.status == 404) {
//           this.messageService.add({
//             severity: "info",
//             summary: "Info",
//             detail: error.error.msg,
//             icon: "far fa-times-circle"
//           });
//           this.subAreaListData = [];
//         } else {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: error.response.ERROR,
//             icon: "far fa-times-circle"
//           });
//         }
//       }
//     );
//   }

//   getCountryList() {
//     const url = "/country/all";
//     this.subAreaManagementService.getMethodWithCache(url).subscribe(
//       (response: any) => {
//         this.countryListData = response.countryList;
//       },
//       (error: any) => {
//         console.log(error, "error");
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.error.ERROR,
//           icon: "far fa-times-circle"
//         });
//       }
//     );
//   }

//   getCityList() {
//     const url = "/city/all";
//     this.subAreaManagementService.getMethodWithCache(url).subscribe(
//       (response: any) => {
//         this.cityListData = response.cityList;
//       },
//       (error: any) => {
//         console.log(error, "error");
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.error.ERROR,
//           icon: "far fa-times-circle"
//         });
//       }
//     );
//   }

//   getStateList() {
//     const url = "/state/all";
//     this.subAreaManagementService.getMethodWithCache(url).subscribe(
//       (response: any) => {
//         this.stateListData = response.stateList;
//       },
//       (error: any) => {
//         console.log(error, "error");
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.error.ERROR,
//           icon: "far fa-times-circle"
//         });
//       }
//     );
//   }

//   getAreaList() {
//     const url = "/area/allAreas";
//     this.areaListData = [];
//     this.subAreaManagementService.getMethod(url).subscribe(
//       (response: any) => {
//         if (response.responseCode == 204) {
//           this.messageService.add({
//             severity: "info",
//             summary: "Info",
//             detail: response.responseMessage,
//             icon: "far fa-times-circle"
//           });
//         } else {
//           this.areaListData = response.data;
//         }
//       },
//       (error: any) => {
//         console.log(error, "error");
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.error.ERROR,
//           icon: "far fa-times-circle"
//         });
//       }
//     );
//   }

//   //   getSelCity(event) {
//   //     const selCity = event.value;
//   //     console.log("selCity", selCity);
//   //     this.getAreaList();
//   //     this.getCityDetailbyd(selCity);
//   //     this.getStateList();
//   //     this.getCountryList();
//   //   }

//   getCityDetailbyd(cityId) {
//     const url = "/city/" + cityId;
//     this.subAreaManagementService.getMethod(url).subscribe(
//       (response: any) => {
//         this.cityDetail = response.cityList;
//         // return
//         this.inputshowSelsctData = true;
//         this.subAreaFormGroup.controls.countryId.patchValue(this.cityDetail.countryId);
//         this.subAreaFormGroup.controls.stateId.patchValue(this.cityDetail.statePojo.id);
//       },
//       (error: any) => {
//         console.log(error, "error");
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.error.ERROR,
//           icon: "far fa-times-circle"
//         });
//       }
//     );
//   }

//   onAreaChange(event: any) {
//     const selectedAreaId = event.value;
//     const selectedArea = this.areaListData.find(area => area.id === selectedAreaId);
//     if (selectedArea) {
//       // Populate City, State, and Country dropdowns
//       this.selectedCityList = this.cityListData.filter(city => city.id === selectedArea.cityId);
//       this.selectedStateList = this.stateListData.filter(
//         state => state.id === selectedArea.stateId
//       );
//       this.selectedCountryList = this.countryListData.filter(
//         country => country.id === selectedArea.countryId
//       );
//       let filterPincode = this.pincodeListData?.find(x => x?.pincodeid === selectedArea?.pincodeId);
//       this.pincodeName = filterPincode?.pincode;
//       console.log(filterPincode, this.pincodeName);

//       // Patch the values
//       this.subAreaFormGroup.patchValue({
//         cityId: selectedArea.cityId,
//         stateId: selectedArea.stateId,
//         countryId: selectedArea.countryId
//       });

//       // Disable other dropdowns after selecting an Area
//       this.isAreaSelected = true;
//     } else {
//       // Clear City, State, and Country dropdowns
//       this.selectedCityList = [];
//       this.selectedStateList = [];
//       this.selectedCountryList = [];
//       this.pincodeName = "";

//       // Reset the form
//       this.subAreaFormGroup.patchValue({
//         cityId: null,
//         stateId: null,
//         countryId: null
//       });

//       // Enable dropdowns before selection
//       this.isAreaSelected = false;
//     }
//   }

//   onFileChange(event: any): void {
//     // if (!this.isSubAreaEdit) {
//     this.selectedFilePreview = [];
//     // }
//     const inputElement = event.target as HTMLInputElement;
//     if (inputElement.files && inputElement.files.length > 0) {
//       const files: FileList = inputElement.files;

//       // Validate all files
//       for (let i = 0; i < files.length; i++) {
//         const file = files.item(i);
//         if (
//           file &&
//           (file.type === "image/png" ||
//             file.type === "image/jpg" ||
//             file.type === "image/jpeg" ||
//             file.type === "application/pdf")
//         ) {
//           this.selectedFilePreview.push(file);
//         } else {
//           this.messageService.add({
//             severity: "info",
//             summary: "Info",
//             detail: `Invalid file type: ${file?.name}. Must be png, jpg, jpeg, or pdf.`,
//             icon: "far fa-check-circle"
//           });
//         }
//       }

//       if (this.selectedFilePreview.length > 0) {
//         // If valid files exist, patch the first file to the form
//         this.multiFiles = this.createFileList(this.selectedFilePreview);
//         console.log(this.multiFiles);
//         this.selectedFile = this.selectedFilePreview[0];
//         this.subAreaFormGroup.patchValue({
//           file: this.multiFiles[0]
//         });
//         this.selectedFilePreviewd.push(
//           ...this.selectedFilePreview.map(file => ({ name: file.name, status: "new" }))
//         );
//       } else {
//         // Reset form control and input if no valid files
//         this.subAreaFormGroup.controls.file.reset();
//         inputElement.value = "";
//       }
//     }
//   }

//   createFileList(files: File[]): FileList {
//     const dataTransfer = new DataTransfer();
//     files.forEach(file => dataTransfer.items.add(file));
//     return dataTransfer.files;
//   }

//   deletSelectedFile(event: any) {
//     var temp: any = this.selectedFilePreviewd?.filter((item: File) => item?.name != event);
//     this.selectedFilePreviewd = temp;
//     this.subAreaFormGroup.patchValue({
//       file: temp
//     });
//   }

//   downloadDocument(subArea) {
//     this.subAreaIdData = subArea.id;
//     const url = "/subarea/" + this.subAreaIdData;
//     this.subAreaManagementService.getMethod(url).subscribe(
//       (response: any) => {
//         if (response.responseCode == 200) {
//           if (response.data != null) {
//             if (response.data.filename != null && response.data.filename != "") {
//               const filenameArray = response.data.filename.split(",");
//               const uniquenameArray = response.data.uniquename.split(",");

//               const fileDetails = filenameArray.map((filename, index) => ({
//                 filename,
//                 uniquename: uniquenameArray[index]
//               }));
//               this.subAreaFileData = {
//                 id: response.data.id,
//                 fileDetails
//               };
//               this.downloadDocumentId = true;
//               this.messageService.add({
//                 severity: "success",
//                 summary: "Successfully",
//                 detail: response.responseMessage,
//                 icon: "far fa-check-circle"
//               });
//             } else {
//               this.downloadDocumentId = false;
//               this.messageService.add({
//                 severity: "info",
//                 summary: "Info",
//                 detail: "No Record Found!",
//                 icon: "far fa-times-circle"
//               });
//             }
//           }
//         } else if (response.responseCode == 404) {
//           this.messageService.add({
//             severity: "info",
//             summary: "Info",
//             detail: response.responseMessage,
//             icon: "far fa-times-circle"
//           });
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
//         console.log(error, "error");
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.error.ERROR,
//           icon: "far fa-times-circle"
//         });
//       }
//     );
//   }

//   closeDownloadDocumentId() {
//     this.downloadDocumentId = false;
//     this.getSubAreaList("");
//   }

//   closeDocumentPreview() {
//     this.documentPreview = false;
//   }

//   downloadDoc(fileName, subAreaId, uniqueName) {
//     this.subAreaManagementService.downloadFile(subAreaId, uniqueName).subscribe(
//       blob => {
//         if (blob.status == 200) {
//           this.messageService.add({
//             severity: "success",
//             summary: "Successfully",
//             detail: "Download Successfully",
//             icon: "far fa-check-circle"
//           });
//           importedSaveAs(blob.body, fileName);
//         } else if (blob.status == 404) {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: "File Not Found",
//             icon: "far fa-times-circle"
//           });
//         } else {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: "Something went wrong!",
//             icon: "far fa-times-circle"
//           });
//         }
//       },
//       (error: any) => {
//         console.log(error, "error");
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.error.ERROR,
//           icon: "far fa-times-circle"
//         });
//       }
//     );
//   }

//   deleteConfirm(subAreaId, uniqueName, fileName) {
//     this.confirmationService.confirm({
//       message: "Do you want to delete this file?",
//       header: "Delete Confirmation",
//       icon: "pi pi-info-circle",
//       accept: () => {
//         this.deleteDoc(subAreaId, uniqueName, fileName);
//       },
//       reject: () => {
//         this.messageService.add({
//           severity: "info",
//           summary: "Rejected",
//           detail: "You have rejected"
//         });
//       }
//     });
//   }

//   deleteDoc(subAreaId, uniqueName, fileName) {
//     let urldoc = "/subarea/document/delete/" + subAreaId + "/" + uniqueName + "/" + fileName + "/";
//     this.subAreaManagementService.deleteMethod(urldoc).subscribe(
//       (response: any) => {
//         if (response.responseCode == 200) {
//           this.messageService.add({
//             severity: "success",
//             summary: "Successfully",
//             detail: response.responseMessage,
//             icon: "far fa-check-circle"
//           });
//           this.closeDownloadDocumentId();
//         } else if (response.responseCode == 404) {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: response.responseMessage,
//             icon: "far fa-times-circle"
//           });
//         } else {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: response.responseMessage,
//             icon: "far fa-times-circle"
//           });
//         }
//         this.getSubAreaList("");
//       },
//       (error: any) => {
//         console.log(error, "error");
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.error.ERROR,
//           icon: "far fa-times-circle"
//         });
//       }
//     );
//   }

//   showticketDocData(fileName, subAreaId, uniqueName) {
//     const fileType = fileName.split(".");
//     this.subAreaManagementService.downloadFile(subAreaId, uniqueName).subscribe(
//       data => {
//         if (data.status == 200) {
//           let type = "application/octet-stream";
//           const uint = new Uint8Array(data.body);
//           const magic = uint.subarray(0, 4);
//           if (magic.every(b => b === 0xff)) {
//             type = "image/jpeg";
//           } else if (
//             magic[0] === 0x89 &&
//             magic[1] === 0x50 &&
//             magic[2] === 0x4e &&
//             magic[3] === 0x47
//           ) {
//             type = "image/png";
//           } else if (
//             magic[0] === 0x47 &&
//             magic[1] === 0x49 &&
//             magic[2] === 0x46 &&
//             magic[3] === 0x38
//           ) {
//             type = "image/gif";
//           } else if (
//             magic[0] === 0xd0 &&
//             magic[1] === 0xcf &&
//             magic[2] === 0x11 &&
//             magic[3] === 0xe0
//           ) {
//             type = "application/vnd.ms-excel";
//           } else if (
//             magic[0] === 0x25 &&
//             magic[1] === 0x50 &&
//             magic[2] === 0x44 &&
//             magic[3] === 0x46
//           ) {
//             type = "application/pdf";
//           } else if (
//             magic[0] === 0xd0 &&
//             magic[1] === 0xcf &&
//             magic[2] === 0x11 &&
//             magic[3] === 0xe0
//           ) {
//             type = "application/msword";
//           }

//           if (fileType[fileType?.length - 1] === "pdf") {
//             const blob = new Blob([data.body], { type: "application/pdf" });
//             const blobUrl = URL.createObjectURL(blob);
//             window.open(blobUrl, "_blank");
//           } else if (fileType[fileType?.length - 1] === "png") {
//             const blob = new Blob([data.body], { type: "image/png" });
//             const blobUrl = URL.createObjectURL(blob);
//             this.previewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(blobUrl);
//             this.documentPreview = true;
//           } else {
//             const blob = new Blob([data.body], { type });
//             const blobUrl = URL.createObjectURL(blob);
//             this.previewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(blobUrl);
//             this.documentPreview = true;
//           }
//         } else if (data.status == 404) {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: "File Not Found",
//             icon: "far fa-times-circle"
//           });
//         } else {
//           this.messageService.add({
//             severity: "error",
//             summary: "Error",
//             detail: "Something went wrong!",
//             icon: "far fa-times-circle"
//           });
//         }
//       },
//       (error: any) => {
//         console.log(error, "error");
//         this.messageService.add({
//           severity: "error",
//           summary: "Error",
//           detail: error.error.ERROR,
//           icon: "far fa-times-circle"
//         });
//       }
//     );
//   }

//   getPincodeList() {
//     // const url = "/pincode/all";
//     const url = "/pincode/getAll";
//     this.subAreaManagementService.getMethod(url).subscribe(
//       (response: any) => {
//         this.pincodeListData = response.dataList.filter(pincode => pincode.status == "Active");
//       },
//       (error: any) => {
//         console.log(error, "error");
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
import { Component, OnInit, ViewChild } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup } from "@angular/forms";
import { MessageService } from "primeng/api";
import { ConfirmationService } from "primeng/api";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { LoginService } from "src/app/service/login.service";
import { MASTERS } from "src/app/constants/aclConstants";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { SubAreaManagementService } from "src/app/service/subArea-management.service";
import { DomSanitizer } from "@angular/platform-browser";
import { saveAs as importedSaveAs } from "file-saver";
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from "@angular/material/sort";
import { MatDialog } from "@angular/material/dialog";
import { Observable, Observer } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { AddEditSubareaManagementComponent } from "./add-edit-subarea-management/add-edit-subarea-management.component";

@Component({
    selector: "app-subarea-management",
    templateUrl: "./subArea-management.component.html",
    styleUrls: ["./subArea-management.component.css"],
    standalone: false
})

export class SubAreaManagementComponent implements OnInit {

    title = RadiusConstants.SUBAREA;
    createAccess = false;
    deleteAccess = false;
    editAccess = false;
    statusOptions = [
        { label: "Active", value: "Y", val: "ACTIVE" },
        { label: "Inactive", value: "N", val: "INACTIVE" },
        { label: "UnderDevelopment", value: "U", val: "UNDERDEVELOPMENT" }
    ];
    subAreaFormGroup: UntypedFormGroup;
    submitted = false;
    isSubAreaEdit = false;
    selectedCityList: any[] = [];
    selectedStateList: any[] = [];
    selectedCountryList: any[] = [];

    subAreaListData: any[] = [];
    subareaTotalRecords = 0;
    currentPageSubAreaSlab = 1;
    subareaitemsPerPage = RadiusConstants.PER_PAGE_ITEMS;

    searchSubAreaName = "";
    searchkey = "";
    showItemPerPage: number | undefined;

    dataSource = new MatTableDataSource<any>([]);
    displayedColumns = ['id', 'name', 'status', 'action'];
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;

    selectedFilePreviewd: any[] = [];
    selectedFilePreview: any[] = [];
    selectedFile: File | null = null;
    multiFiles!: FileList;

    pincodeName: any = "";
    isAreaSelected = false;

    areaListData: any[] = [];
    cityListData: any[] = [];
    stateListData: any[] = [];
    countryListData: any[] = [];
    pincodeListData: any[] = [];

    downloadDocumentId = false;
    subAreaFileData: any;
    previewUrl: any;
    documentPreview = false;
    viewCountryListData: any;

    constructor(
        private fb: UntypedFormBuilder,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private subAreaManagementService: SubAreaManagementService,
        public loginService: LoginService,
        private sanitizer: DomSanitizer,
        private toastr: ToastrService,
        private dialog: MatDialog
    ) {
        this.createAccess = loginService.hasPermission(MASTERS.SUBAREA_CREATE);
        this.deleteAccess = loginService.hasPermission(MASTERS.SUBAREA_DELETE);
        this.editAccess = loginService.hasPermission(MASTERS.SUBAREA_EDIT);
    }

    ngOnInit(): void {
        this.subAreaFormGroup = this.fb.group({
            name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            status: ["", Validators.required],
            countryId: ["", Validators.required],
            cityId: ["", Validators.required],
            stateId: ["", Validators.required],
            areaId: ["", Validators.required],
            file: [""]
        });

        this.loadInitialLists();

        this.loadSubAreaList();
    }
    loadInitialLists(): void {
        this.getCountryList();
        this.getStateList();
        this.getCityList();
        this.getAreaList();
        this.getPincodeList();
    }

    getCountryList(): void {
        const url = "/country/all";
        this.subAreaManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.countryListData = response.countryList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    getStateList(): void {
        const url = "/state/all";
        this.subAreaManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.stateListData = response.stateList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    getCityList(): void {
        const url = "/city/all";
        this.subAreaManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.cityListData = response.cityList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    getAreaList(): void {
        const url = "/area/allAreas";
        this.areaListData = [];
        this.subAreaManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 204) {
                    this.toastr.info(response.responseMessage, 'Info!');
                } else {
                    this.areaListData = response.data;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    getPincodeList(): void {
        const url = "/pincode/getAll";
        this.subAreaManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.pincodeListData = response.dataList.filter((pincode: any) => pincode.status == "Active");
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }


    ngAfterViewInit() {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }


    canExit(): Observable<boolean> | boolean {
        if (!this.subAreaFormGroup.dirty) return true;

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

    loadSubAreaList(listSize?: number): void {
        if (listSize) {
            this.subareaitemsPerPage = listSize;
        }
        const plandata = {
            page: this.currentPageSubAreaSlab,
            pageSize: this.subareaitemsPerPage
        };
        const url = `/subarea/allWithPagination?page=${this.currentPageSubAreaSlab}&pageSize=${this.subareaitemsPerPage}`;
        this.subAreaManagementService.getMethod(url).subscribe({
            next: (response: any) => {
                this.subAreaListData = response.dataList || [];
                this.subareaTotalRecords = response.totalRecords || 0;
                this.dataSource = new MatTableDataSource<any>([...this.subAreaListData]);
                setTimeout(() => {
                    if (this.paginator) {
                        this.paginator.length = this.subareaTotalRecords;
                        this.paginator.pageIndex = this.currentPageSubAreaSlab - 1;
                    }
                });
            },
            error: (error: any) => {
                this.toastr.error(error.error?.ERROR || "Error loading sub-area list", 'Failed!');

            }
        });
    }

    pageChangedCountryList(event: PageEvent): void {
        this.currentPageSubAreaSlab = event.pageIndex + 1;
        this.subareaitemsPerPage = event.pageSize;

        if (this.searchkey) {
            this.searchSubArea();
        } else {
            this.loadSubAreaList();
        }
    }

    addEditSubarea(id?: any): void {
        this.submitted = true;
        if (this.subAreaFormGroup.invalid) {
            return;
        }

        const formData = new FormData();

        if (this.subAreaFormGroup.controls.file.value) {
            if (this.selectedFile &&
                !["image/png", "image/jpg", "image/jpeg", "application/pdf"].includes(this.selectedFile.type)) {
                this.subAreaFormGroup.controls.file.reset();
                (response: any) => {
                    this.toastr.info(response.responseMessage, "File type must be png, jpg, jpeg or pdf");
                }
                return;
            } else {
                Array.from(this.multiFiles).forEach(file => formData.append("file", file));
            }
        }

        const subAreaData = {
            ...this.subAreaFormGroup.value,
            isDeleted: false,
            mvnoId: Number(localStorage.getItem("mvnoId")),
            buId: null
        };

        if (id) {
            subAreaData.id = Number(id);
            subAreaData.filename = this.viewCountryListData.filename;
            subAreaData.uniquename = this.viewCountryListData.uniquename;

            const copyData = { ...subAreaData };
            delete copyData.file;
            formData.append("entityDTO", JSON.stringify(copyData));

            this.subAreaManagementService.postMethod("/subarea/update", formData).subscribe({
                next: (response: any) => this.handleSaveResponse(response),
                error: (error: any) => this.handleError(error)
            });
        } else {
            const copyData = { ...subAreaData };
            delete copyData.file;
            formData.append("entityDTO", JSON.stringify(copyData));

            this.subAreaManagementService.postMethod("/subarea/save", formData).subscribe({
                next: (response: any) => this.handleSaveResponse(response),
                error: (error: any) => this.handleError(error)
            });
        }
    }

    private handleSaveResponse(response: any): void {
        if (response.responseCode === 406) {
            this.toastr.error(`${response.error.ERROR}`, 'Failed!');
            return;
        }

        this.submitted = false;
        this.isSubAreaEdit = false;
        this.isAreaSelected = false;
        this.subAreaFormGroup.reset();
        this.subAreaFormGroup.controls.file.reset();
        this.selectedFile = null;
        this.selectedFilePreview = [];
        this.selectedFilePreviewd = [];
        this.pincodeName = "";

        this.subAreaFormGroup.controls.status.setValue("");
        this.subAreaManagementService.clearCache("/subarea/all");

        this.toastr.success(`Successfully Updated`, 'Success!');

        if (this.searchkey) {
            this.searchSubArea();
        } else {
            this.loadSubAreaList();
        }
    }

    private handleError(error: any): void {
        if ([417, 406].includes(error.error.status)) {
            this.toastr.error(`${error.error.ERROR}`, 'Failed!');
        } else {
            this.toastr.error(`${error.error.ERROR}`, 'Failed!');
        }
    }

    onAreaChange(event: any): void {
        const selectedAreaId = event.value;
        const selectedArea = this.areaListData.find(area => area.id === selectedAreaId);
        if (selectedArea) {
            this.selectedCityList = this.cityListData.filter(city => city.id === selectedArea.cityId);
            this.selectedStateList = this.stateListData.filter(state => state.id === selectedArea.stateId);
            this.selectedCountryList = this.countryListData.filter(country => country.id === selectedArea.countryId);
            let filterPincode = this.pincodeListData?.find(x => x?.pincodeid === selectedArea?.pincodeId);
            this.pincodeName = filterPincode?.pincode ?? '';

            this.subAreaFormGroup.patchValue({
                cityId: selectedArea.cityId,
                stateId: selectedArea.stateId,
                countryId: selectedArea.countryId
            });

            this.isAreaSelected = true;
        } else {
            this.selectedCityList = [];
            this.selectedStateList = [];
            this.selectedCountryList = [];
            this.pincodeName = '';

            this.subAreaFormGroup.patchValue({
                cityId: null,
                stateId: null,
                countryId: null
            });

            this.isAreaSelected = false;
        }
    }

    searchSubArea(): void {
        if (!this.searchkey || this.searchkey !== this.searchSubAreaName) {
            this.currentPageSubAreaSlab = 1;
        }
        this.searchkey = this.searchSubAreaName;
        if (this.showItemPerPage) {
            this.subareaitemsPerPage = this.showItemPerPage;
        }

        const searchData = {
            filters: [{
                filterDataType: "",
                filterValue: this.searchSubAreaName.trim(),
                filterColumn: "any",
                filterOperator: "equalto",
                filterCondition: "and"
            }],
            page: this.currentPageSubAreaSlab,
            pageSize: this.subareaitemsPerPage
        };

        this.subAreaManagementService.postMethod("/subarea", searchData).subscribe({
            next: (response: any) => {
                this.subAreaListData = response.dataList || [];
                this.subareaTotalRecords = response.totalRecords || 0;
                this.dataSource.data = this.subAreaListData;
            },
            error: (error: any) => {
                this.subareaTotalRecords = 0;
                if (error.error.status === 404) {
                    this.toastr.info(error.responseMessage, 'Info!');

                    this.subAreaListData = [];
                    this.dataSource.data = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            }
        });
    }

    addEditSubareaDialog(): void {
        const dialogRef = this.dialog.open(AddEditSubareaManagementComponent, {
            width: '900px',
            data: {
                isEdit: false,
                title: 'Create ' + this.title,
                createAcS: this.createAccess,
                editAcs: this.editAccess,
                areaListData: this.areaListData,
                cityListData: this.cityListData,
                stateListData: this.stateListData,
                countryListData: this.countryListData,
                pincodeListData: this.pincodeListData,
                areaTitle: RadiusConstants.AREA,
                countryTitle: RadiusConstants.COUNTRY,
                cityTitle: RadiusConstants.CITY,
                stateTitle: RadiusConstants.STATE,
                pincodeTitle: RadiusConstants.PINCODE,
                subAreaTitle: this.title
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.subAreaFormGroup = this.fb.group({
                    name: result.name,
                    status: result.status,
                    countryId: result.countryId || '',
                    cityId: result.cityId || '',
                    stateId: result.stateId || '',
                    areaId: result.areaId || '',
                    file: result.file || ''
                });
                this.addEditSubarea("");
            }
        });
    }

    editSubarea(id: number): void {
        if (!id) return;

        this.subAreaManagementService.getMethod(`/subarea/${id}`).subscribe({
            next: (response: any) => {
                if (response.data) {
                    this.viewCountryListData = response.data;

                    const dialogRef = this.dialog.open(AddEditSubareaManagementComponent, {
                        width: '900px',
                        data: {
                            isEdit: true,
                            title: 'Update ' + this.title,
                            yesLabel: 'Update',
                            noLabel: 'Cancel',
                            createAcS: this.createAccess,
                            editAcs: this.editAccess,
                            subAreaData: this.viewCountryListData,
                            areaListData: this.areaListData,
                            cityListData: this.cityListData,
                            stateListData: this.stateListData,
                            countryListData: this.countryListData,
                            pincodeListData: this.pincodeListData,
                            areaTitle: RadiusConstants.AREA,
                            countryTitle: RadiusConstants.COUNTRY,
                            cityTitle: RadiusConstants.CITY,
                            subAreaTitle: this.title,
                            stateTitle: RadiusConstants.STATE,
                            pincodeTitle: RadiusConstants.PINCODE,
                            roadListData: response.data.roads ?? []
                        }
                    });

                    dialogRef.afterClosed().subscribe(result => {
                        if (result) {
                            this.subAreaFormGroup.patchValue({
                                name: result.name,
                                status: result.status,
                                countryId: result.countryId,
                                cityId: result.cityId,
                                stateId: result.stateId,
                                areaId: result.areaId,
                                file: result.file
                            });

                            this.isSubAreaEdit = true;
                            this.addEditSubarea(id);
                        }
                    });
                }
            },
            error: (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        });
    }

    clearsearchSubArea(): void {
        this.searchSubAreaName = "";
        this.searchkey = "";
        this.pincodeName = "";
        this.submitted = false;
        this.isSubAreaEdit = false;
        this.subAreaFormGroup.reset();
        this.subAreaFormGroup.controls.status.setValue("");
        this.selectedFilePreview = [];
        this.selectedFilePreviewd = [];
        this.loadSubAreaList();
    }


    deleteConfirmonSubarea(subAreaManagement: any): void {
        if (!subAreaManagement) return;

        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete ' + this.title,
                description: `Are you sure you want to delete "${subAreaManagement.name}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteSubarea(subAreaManagement);
            }
        });
    }

    deleteSubarea(subAreaManagement: any): void {
        if (!subAreaManagement?.id) return;

        this.subAreaManagementService.deleteMethod(`/subarea/delete/${subAreaManagement.id}`).subscribe({
            next: (response: any) => {
                if (this.currentPageSubAreaSlab !== 1 && this.subAreaListData.length === 1) {
                    this.currentPageSubAreaSlab--;
                }
                this.toastr.success(`${response.responseMessage}`, 'Success!');
                this.clearsearchSubArea();
            },
            error: (error: any) => {

                this.toastr.error(error.error?.ERROR || "Error deleting sub-area", 'Failed!');

            }
        });
    }

    onFileChange(event: any): void {
        this.selectedFilePreview = [];
        const inputElement = event.target as HTMLInputElement;

        if (inputElement.files && inputElement.files.length) {
            const files = Array.from(inputElement.files);
            const validFiles = files.filter(file =>
                ["image/png", "image/jpg", "image/jpeg", "application/pdf"].includes(file.type)
            );

            if (validFiles.length !== files.length) {
                (error: any) => {
                    this.toastr.info(error.responseMessage, "Some files have invalid format (png, jpg, jpeg, pdf allowed)");
                }

            }

            this.selectedFilePreview = validFiles;
            this.multiFiles = this.createFileList(validFiles);
            this.selectedFile = validFiles[0] || null;
            if (this.selectedFile) {
                this.subAreaFormGroup.patchValue({ file: this.selectedFile });
            }
            this.selectedFilePreviewd.push(...validFiles.map(f => ({ name: f.name, status: "new" })));
        } else {
            this.subAreaFormGroup.controls.file.reset();
            inputElement.value = "";
        }
    }

    createFileList(files: File[]): FileList {
        const dataTransfer = new DataTransfer();
        files.forEach(file => dataTransfer.items.add(file));
        return dataTransfer.files;
    }

    deletSelectedFile(name: string): void {
        this.selectedFilePreviewd = this.selectedFilePreviewd.filter(f => f.name !== name);
    }

    downloadDocument(subArea) {
        this.subAreaListData = subArea.id;
        const url = "/subarea/" + this.subAreaListData;
        this.subAreaManagementService.getMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    if (response.data != null) {
                        if (response.data.filename != null && response.data.filename != "") {
                            const filenameArray = response.data.filename.split(",");
                            const uniquenameArray = response.data.uniquename.split(",");

                            const fileDetails = filenameArray.map((filename, index) => ({
                                filename,
                                uniquename: uniquenameArray[index]
                            }));
                            this.subAreaFileData = {
                                id: response.data.id,
                                fileDetails
                            };
                            this.downloadDocumentId = true;
                            this.toastr.success(`${response.responseMessage}`, 'Success!');
                        } else {
                            this.downloadDocumentId = false;
                            this.toastr.info(response.responseMessage, "No Record Found!");

                        }
                    }
                } else if (response.responseCode == 404) {
                    this.toastr.info(response.responseMessage, 'Info!');
                } else {
                    this.toastr.error(`${response.error.ERROR}`, 'Failed!');
                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    closeDownloadDocumentId(): void {
        this.downloadDocumentId = false;
        this.loadSubAreaList();
    }

    closeDocumentPreview(): void {
        this.documentPreview = false;
    }

    downloadDoc(fileName: string, subAreaId: any, uniqueName: any): void {
        this.subAreaManagementService.downloadFile(subAreaId, uniqueName).subscribe({
            next: blob => {
                if (blob.status === 200) {
                    importedSaveAs(blob.body, fileName);
                    this.toastr.success(`${blob.message}`, 'Success!');
                } else if (blob.status === 404) {
                    this.toastr.error(`${blob.error.ERROR}`, 'File Not Found!');
                } else {
                    this.toastr.error(`${blob.error.ERROR}`, 'Something went wrong!');
                }
            },
            error: (error: any) => {

                this.toastr.error(error.error?.ERROR || "Error downloading file", 'Failed!');

            }
        });
    }

    deleteConfirm(subAreaId: any, uniqueName: any, fileName: string): void {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete File',
                description: "Do you want to delete this file?",
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteDoc(subAreaId, uniqueName, fileName);
            }
        });
    }

    deleteDoc(subAreaId: any, uniqueName: any, fileName: string): void {
        this.subAreaManagementService.deleteMethod(`/subarea/document/delete/${subAreaId}/${uniqueName}/${fileName}/`).subscribe({
            next: (response: any) => {
                if (response.responseCode === 200) {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');
                    this.closeDownloadDocumentId();
                } else {
                    this.toastr.error(`${response.error.ERROR}`, 'Failed!');
                }
                this.loadSubAreaList();
            },
            error: (error: any) => {

                this.toastr.error(error.error?.ERROR || "Error deleting file", 'Failed!');


            }
        });
    }

    showticketDocData(fileName: string, subAreaId: any, uniqueName: any): void {
        const fileType = fileName.split(".").pop()?.toLowerCase();
        this.subAreaManagementService.downloadFile(subAreaId, uniqueName).subscribe({
            next: (data: any) => {
                if (data.status === 200) {
                    let type = "application/octet-stream";

                    const uint = new Uint8Array(data.body);
                    const magic = uint.subarray(0, 4);

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

                    if (fileType === "pdf") {
                        const blob = new Blob([data.body], { type: "application/pdf" });
                        const blobUrl = URL.createObjectURL(blob);
                        window.open(blobUrl, "_blank");
                    } else if (fileType === "png") {
                        const blob = new Blob([data.body], { type: "image/png" });
                        const blobUrl = URL.createObjectURL(blob);
                        this.previewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(blobUrl);
                        this.documentPreview = true;
                    } else {
                        const blob = new Blob([data.body], { type });
                        const blobUrl = URL.createObjectURL(blob);
                        this.previewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(blobUrl);
                        this.documentPreview = true;
                    }
                } else if (data.status === 404) {

                    this.toastr.error(`${data.error.ERROR}`, 'File Not Found');
                } else {

                    this.toastr.error(`${data.error.ERROR}`, 'Something went wrong!');
                }
            },
            error: (error: any) => {

                this.toastr.error(error.error?.ERROR || "Error previewing file", 'Failed!');

            }
        });
    }
}

