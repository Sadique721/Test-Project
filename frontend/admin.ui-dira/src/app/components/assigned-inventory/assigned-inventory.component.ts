import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import { BehaviorSubject } from "rxjs";
import { ITEMS_PER_PAGE, PER_PAGE_ITEMS, pageLimitOptions } from "../../RadiusUtils/RadiusConstants";
import { CustomerDetailsService } from "../../service/customer-details.service";
import { OutwardService } from "../../service/outward.service";
import { WarhouseManagementService } from "src/app/service/warhouse-management.service";
import { InwardService } from "src/app/service/inward.service";
import { PartnerService } from "src/app/service/partner.service";
import { ServiceAreaService } from "src/app/service/service-area.service";
import { PopManagementsService } from "src/app/service/pop-managements.service";
import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { url } from "inspector";
import * as FileSaver from "file-saver";
import { LoginService } from "src/app/service/login.service";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { INVENTORYS } from "src/app/constants/aclConstants";
import { HttpClient } from "@angular/common/http";
import moment from "moment";
import { ToastrService } from 'ngx-toastr';
import { error } from "console";
import { MatPaginator } from "@angular/material/paginator";

import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog } from "@angular/material/dialog";
import { T } from "node_modules/@fullcalendar/core/internal-common";


declare var $: any;
@Component({
    selector: "app-assigned-inventory",
    templateUrl: "./assigned-inventory.component.html",
    styleUrls: ["./assigned-inventory.component.css"],
    standalone: false
})
export class AssignedInventoryComponent implements OnInit {


    assignedAllInventoryListData = new MatTableDataSource<any>([]);
    @ViewChild('inventoryHistoryDialogTemplate') inventoryHistoryDialogTemplate!: TemplateRef<any>;
    @ViewChild('changeOwnershipStatusDialogTemplate') changeOwnershipStatusDialogTemplate!: TemplateRef<any>;
    @ViewChild('inventoryDetailDialogTemplate') inventoryDetailDialogTemplate!: TemplateRef<any>;
    @ViewChild('viewPortDetailsDialogTemplate') viewPortDetailsDialogTemplate!: TemplateRef<any>;
    @ViewChild('changeTypeDialogTemplate') changeTypeDialogTemplate!: TemplateRef<any>;
    @ViewChild('changeItemStatusDialogTemplate') changeItemStatusDialogTemplate!: TemplateRef<any>;
    @ViewChild('changeWarrantyDialogTemplate') changeWarrantyDialogTemplate!: TemplateRef<any>;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    displayedColumns: string[] = [
        'select',
        'serialNumber',
        'productName',
        'condition',
        'warranty',
        'oemWarrantyStatus',
        'itemStatus',
        'ownershipType',
        'ownerType',
        'ownerName',
        'download'
    ];
    displayedColumnsSerialized: string[] = [
        'serialNumber',
        'macAddress',
        'productName',
        'customerFirstName',
        'customerLastName',
        'serviceAreaName',
        'assigneeName',
        'qty',
        'assignedDateTime',
        'expDate'
    ];

    public loginService: LoginService;
    dialogId: boolean = false;
    assignedAllFormGroup: UntypedFormGroup;
    changeWarrantyFormGroup: UntypedFormGroup;
    changeItemStatusFormGroup: UntypedFormGroup;
    changeItemOwnershipStatusFormGroup: UntypedFormGroup;
    changeTypeFormGroup: UntypedFormGroup;
    changeTypeFormArray: UntypedFormArray;
    changeTypeSubmitted: boolean = false;
    changeOwnershipStatusFormGroup: UntypedFormGroup;
    changeOwnershipStatusFormArray: UntypedFormArray;
    changeOwnershipStatusSubmitted: boolean = false;
    isDetails1: boolean = false;
    isDetails2: boolean = false;
    isDetails3: boolean = false;
    isDetails4: boolean = false;
    isDetails5: boolean = false;
    isDetails6: boolean = false;
    isDetails7: boolean = false;
    isDetails8: boolean = false;
    isDetails9: boolean = false;
    isDetails10: boolean = false;
    isDetails11: boolean = false;
    changeInventoryStatusFormGroup: UntypedFormGroup;
    changeInventoryStatusFormArray: UntypedFormArray;
    changeInventoryStatusSubmitted: boolean = false;
    returnItemFormGroup: UntypedFormGroup;
    returnItemFormArray: UntypedFormArray;
    returnItemSubmitted: boolean = false;
    warrantyStatusChangeFormGroup: UntypedFormGroup;
    warrantyStatusChangeFormArray: UntypedFormArray;
    warrantyStatusChangeSubmitted: boolean = false;
    ChangeTypeModal: boolean = false;
    itemOwnershipStatusModal: boolean = false;
    warrantyTypeModal: boolean = false;
    returnItemModal: boolean = false;
    inventoryDetailModal: boolean = false;
    itemStatusModal: boolean = false;
    searchProductName: any = "";
    searchName: any = "";
    searchNameNonSeriCust: any = "";
    searchNameSeriPop: any = "";
    searchNameNonSeriPop: any = "";
    searchNameSeriServiceArea: any = "";
    searchNameNonSeriServiceArea: any = "";
    outwardNumberFlag: boolean = false;
    InitialOutwardNumberFlag: boolean = true;
    assignedInventoryList = [];
    assignedNonSerializedCustInventoryList = [];
    assignedSerializedPOPInventoryList = [];
    assignedNonSerializePOPInventoryList = [];
    assignedSerializedServiceAreaInventoryList = [];
    assignedNonSerializedServiceAreaInventoryList = [];
    assignedAllInventoryList = [];
    assigntPageProductListdata = 1;
    assignListdataitemsPerPage = PER_PAGE_ITEMS;
    assignListdatatotalRecords: any;
    showInventoryAssignedFlag: boolean = false;
    showInventoryAssignedCustomerFlag: boolean = false;
    showInventoryAssignedPopFlag: boolean = false;
    showInventoryAssignedServiceAreaFlag: boolean = false;
    first = 0;
    searchkey: string;
    searchData: any;
    editedValue: string = "";
    isAddButtonEnabled: boolean = false;
    newValue: string = "";
    inventorySpecificationDetails: any[] = [];
    inventorySpecificationDetailsHistory: any[] = [];
    chakedData: any = [];
    ispaymentChecked: boolean = false;
    rows = 10;
    showStaffsInventory: boolean;
    showCustomerAssignedInventory: boolean;
    showNonSerializedCustomerAssignedInventory: boolean;
    showSerializedPOPAssignedInventory: boolean;
    showNonSerializedPOPAssignedInventory: boolean;
    showSerializedServiceAreaAssignedInventory: boolean;
    showNonSerializedServiceAreaAssignedInventory: boolean;
    showAllAssignedInventrie: boolean;
    userId = localStorage.getItem("userId");
    custId = new BehaviorSubject({
        custId: "",
    });
    showMacMapping: boolean;
    showMac: boolean;
    macMappingList = [];
    pageLimitOptions = pageLimitOptions;
    assignAllListdataitemsPerPage = 5;
    showItemPerPage = 5;
    showAllInventoryItemPerPage = 5;
    showAllCustInventoryItemPerPage = 5;
    showAllNonSerializedCustInventoryItemPerPage = 5;
    showAllSerializedPOPInventoryItemPerPage = 5;
    showAllNonSerializedPOPInventoryItemPerPage = 5;
    showAllSerializedServiceAreaInventoryItemPerPage = 5;
    showAllNonSerializedServiceAreaInventoryItemPerPage = 5;
    hasMac: boolean;
    hasSerial: boolean;
    addReturn: boolean;
    addChangeType: boolean;
    destinations = [];
    staffList: any[] = [];
    warehouses: any[] = [];
    products: any[] = [];
    destinationType = [
        { label: "Warehouse" },
        { label: "Staff" },
        { label: "Partner" },
        { label: "POP" },
        { label: "Service Area" },
    ];
    warrantylabels = [
        { label: "InWarranty" },
        { label: "NoWarranty" },
        { label: "Paused" },
        { label: "Expired" },
        { label: "NotStarted" },
    ];
    itemStatuslabels = [
        // { label: "UnAllocated"},
        // { label: "Allocated"},
        // { label: "Returned"},
        { label: "Maintenance" },
        { label: "Defective" },
    ];
    itemOwnershipStatuslabels = [
        { label: "Sold" },
        { label: "Organization Owned" },
        { label: "Temporary" },
        // { label: "Partner Customer Owned"},
        { label: "Sharing Ownership" },
    ];
    condition = [
        { label: "New" },
        { label: "Refurbished" },
        { label: "DamagedAtStore" },
        { label: "DamagedAtSite" },
        { label: "ScrappedAtStore" },
        { label: "ScrappedAtSite" },
        { label: "Old" },
        // { label: "POP" },
        // { label: "SA" },
    ];
    remarkType = [
        { label: "Lost", value: "Lost" },
        { label: "Theft", value: "Theft" },
        { label: "Other", value: "Other" },
    ];
    filterByCustData = [
        { label: "First Name", value: "name" },
        { label: "Product Name", value: "Product Name" },
    ];
    filterByPOPData = [
        { label: "Pop Name", value: "name" },
        { label: "Product Name", value: "Product Name" },
    ];
    filterByServiceAreaData = [
        { label: "Service Area Name", value: "serviceArea" },
        { label: "Product Name", value: "product" },
    ];
    assignCustomerListdataitemsPerPage = PER_PAGE_ITEMS;
    assignNonSerializedCustomerListdataitemsPerPage = PER_PAGE_ITEMS;
    assignSerializedPopListdataitemsPerPage = PER_PAGE_ITEMS;
    assignNonSerializedPopListdataitemsPerPage = PER_PAGE_ITEMS;
    assignSerializedServiceAreaListdataitemsPerPage = PER_PAGE_ITEMS;
    assignNonSerializedServiceAreaListdataitemsPerPage = PER_PAGE_ITEMS;
    assignCustomertListdatatotalRecords: any;
    assignNonSerializedCustomertListdatatotalRecords: any;
    assignSerializedPopListdatatotalRecords: any;
    assignNonSerializedPopListdatatotalRecords: any;
    assignSerializedServiceAreaListdatatotalRecords: any;
    assignNonSerializedServiceAreaListdatatotalRecords: any;
    currentPageAssigntListdata = 1;
    currentCustomerNonSerializedPageAssigntListdata = 1;
    currentPopSerializedPageAssigntListdata = 1;
    currentPopNonSerializedPageAssigntListdata = 1;
    currentServiceAreaSerializedPageAssigntListdata = 1;
    currentServiceAreaNonSerializedPageAssigntListdata = 1;

    assignAllListdatatotalRecords: any;
    assigntAllPageProductListdata = 1;
    WareHouseType: any[] = [];
    searchByFilterColumn: any = "";
    searchOwnerType: any = "";
    searchOwner: any = "";
    searchProductVal: any = "";
    searchInward: any = "";
    searchOwnership: any = "";
    searchSerialNumber: any = "";
    isEditMode: boolean = false;
    searchStatus: any = "";
    searchItemType: any = "";
    searchWarrantyStatus: any = "";
    updateItemData: any;
    updateItemOwnershipChangeData: any;
    updateItemInventoryChangeData: any;
    returnItemData: any;
    warrantyChangeData: any;
    updateItemFileData: any = [];
    specDetailsShow: boolean = false;
    files: any = [];
    fileName: any = [];
    isSearchInventory: boolean = false;
    changeTypeAccess: boolean = false;
    assignNonSerializedItem: boolean = false;
    assignSerializedItem: boolean = false;
    intAssignedToServiceAreaAccess: boolean = false;
    intAssignedToPOPAccess: boolean = false;
    intAssignedToCustomerAccess: boolean = false;
    assignedIntAccess: boolean = false;
    ownershipStatusAccess: boolean = false;
    statusAccess: boolean = false;
    warrantyAccess: boolean = false;
    inventoryDetailData: any;
    itemId: any;
    values: string[] = [];
    editedValueIndex: number = -1;
    itemID: string;
    ID: string;
    displayDialog: boolean = false;
    value: string;
    portModel: boolean = false;
    selectedInventory: any;
    popListData: any[] = [];
    productListData: any[] = [];
    isProduct: boolean = false;
    isPop: boolean = false;
    isServcieArea: boolean = false;
    isServiceAreaProduct: boolean = false;
    servcieAreaListData: any[] = [];
    dialogRef: any;
    inventoryDetailsDialogRef

    constructor(
        private dialog: MatDialog,
        private toastr: ToastrService,
        private http: HttpClient,
        private fb: UntypedFormBuilder,
        private outwardService: OutwardService,
        private inwardService: InwardService,
        private partnerService: PartnerService,
        private serviceAreaService: ServiceAreaService,
        private popService: PopManagementsService,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private customerDetailsService: CustomerDetailsService,
        private messageService: MessageService,
        private warehouseService: WarhouseManagementService,
        public commondropdownService: CommondropdownService,
        loginService: LoginService
    ) {
        this.changeTypeAccess = loginService.hasPermission(INVENTORYS.INVEN_DETAILS_CHANGE_TYPE);
        this.warrantyAccess = loginService.hasPermission(INVENTORYS.INVEN_DETAILS_WARRANTY);
        this.statusAccess = loginService.hasPermission(INVENTORYS.INVEN_DETAILS_STATUS);
        this.ownershipStatusAccess = loginService.hasPermission(
            INVENTORYS.INVEN_DETAILS_OWNERSHIP_STATUS
        );
        this.assignedIntAccess = loginService.hasPermission(
            INVENTORYS.INVEN_DETAILS_ASSIGNED_INVENTORY
        );
        this.intAssignedToCustomerAccess = loginService.hasPermission(
            INVENTORYS.INVEN_DETAILS_INVEN_ASSIGNED_TO_CUST
        );
        this.intAssignedToPOPAccess = loginService.hasPermission(
            INVENTORYS.INVEN_DETAILS_INVEN_ASSIGNED_TO_POP
        );
        this.intAssignedToServiceAreaAccess = loginService.hasPermission(
            INVENTORYS.INVEN_DETAILS_INVEN_ASSIGNED_TO_SA
        );
        this.assignSerializedItem = loginService.hasPermission(
            INVENTORYS.INVEN_DETAILS_INVEN_ASSIGNED_SERIALIZED
        );
        this.assignNonSerializedItem = loginService.hasPermission(
            INVENTORYS.INVEN_DETAILS_INVEN_ASSIGNED_NONSERIALIZED
        );

        this.loginService = loginService;
        this.outwardService.getAllProducts().subscribe((res: any) => {
            this.products = res.dataList;
        });
    }

    ngOnInit(): void {
        this.getNonSerializedItemPopByInventoryMappingByStaffId("");
        this.getNonSerializedItemServiceAreaByInventoryMappingByStaffId("");
        this.showInventoryAssignedToCustomer();
        this.showInventoryAssignedToPOP();
        this.showInventoryAssignedToServiceArea();
        // this.assignedAllFormGroup = this.fb.group({
        //   id: [""],
        //   destinationType: ["", Validators.required],
        //   destination: ["", Validators.required],
        //   product: ["", Validators.required],
        //   inward: ["", Validators.required],
        //   ownership: ["", Validators.required],
        //   status: ["", Validators.required],
        //   itemType: ["", Validators.required],
        //    warrantyStatus:["", Validators.required]
        // });

        this.changeWarrantyFormGroup = this.fb.group({
            warrantylabels: [""],
        });
        this.changeItemStatusFormGroup = this.fb.group({
            itemStatuslabels: [""],
        });
        this.changeItemOwnershipStatusFormGroup = this.fb.group({
            itemOwnershipStatuslabels: [""],
        });

        this.changeTypeFormGroup = this.fb.group({});
        this.changeTypeFormArray = this.fb.array([]);
        this.changeOwnershipStatusFormGroup = this.fb.group({});
        this.changeOwnershipStatusFormArray = this.fb.array([]);
        this.changeInventoryStatusFormGroup = this.fb.group({});
        this.changeInventoryStatusFormArray = this.fb.array([]);
        this.returnItemFormGroup = this.fb.group({});
        this.returnItemFormArray = this.fb.array([]);
        this.warrantyStatusChangeFormGroup = this.fb.group({});
        this.warrantyStatusChangeFormArray = this.fb.array([]);
        this.searchData = {
            filter: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "any",
                    filterOperator: "equalto",
                    filterCondition: "and",
                },
            ],
        };
        this.commondropdownService.getAllActiveProduct();
        this.commondropdownService.getAllActiveInward();
        this.commondropdownService.getOwnershipType();
        this.commondropdownService.getItemStatusList();
        this.commondropdownService.getItemConditionList();
        this.commondropdownService.getWarrantyStatusList();
        this.showAllAssignedInventries();
        this.getAllPopName();
        this.getAllServcieArea();
    }

    searchProduct() {
        this.outwardNumberFlag = true;
        this.InitialOutwardNumberFlag = false;

        if (!this.searchkey || this.searchkey !== this.searchProductName) {
            this.assigntPageProductListdata = 1;
        }
        this.searchkey = this.searchProductName;
        if (this.searchkey == "") {
            this.getAllAssignInventories("");
        } else {
            if (this.showAllInventoryItemPerPage) {
                this.assignListdataitemsPerPage = this.showAllInventoryItemPerPage;
            }
            this.searchData.filter[0].filterValue = this.searchProductName.trim();
            let page = {
                page: this.assigntPageProductListdata,
                pageSize: this.showAllInventoryItemPerPage,
            };
            //const url = '/state/search'
            this.outwardService.searchAssignInventories(page, this.userId, this.searchData).subscribe(
                (response: any) => {
                    if (response.responseCode == 404) {
                        this.toastr.info(response.responseMessage, 'Info!');

                        this.assignedInventoryList = [];
                        this.assignListdatatotalRecords = 0;
                    } else {
                        this.assignedInventoryList = response.dataList;
                        this.assignListdatatotalRecords = response.totalRecords;
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    addValue() {
        if (this.newValue.trim() !== "") {
            this.inventorySpecificationDetails.push({
                paramName: "New Param",
                isMandatory: false,
                paramValue: this.newValue.trim(),
            });
            this.newValue = "";
        }
    }

    addOrEditValuee(newValue: string) {
        if (
            this.isEditMode &&
            this.editedValueIndex >= 0 &&
            this.editedValueIndex < this.values.length
        ) {
            this.values[this.editedValueIndex] = newValue;
        } else {
            this.values.push(newValue);
        }
        this.isEditMode = false;
        this.editedValueIndex = -1;
    }

    onValueInput(newValue: string) {
        const inputElement = event.target as HTMLInputElement;
        // const newValue = inputElement.value;

        this.isAddButtonEnabled = this.isValueNew(newValue);
    }

    toggleEditMode() {
        this.isEditMode = !this.isEditMode;
        // this.editedValueIndex = index;
    }

    onValueChange(newValue: string) {
        this.isAddButtonEnabled = this.isValueNew(newValue);
        this.editedValue = newValue; // Update the edited value
    }

    clearSearchProduct() {
        this.outwardNumberFlag = false;
        this.InitialOutwardNumberFlag = true;
        this.searchProductName = "";
        this.showStaffInventory();
    }

    isValueNew(newValue: string) {
        return newValue !== this.editedValue;
    }

    editedRowIndex: number = -1;

    editValue(rowIndex: number) {
        this.editedRowIndex = rowIndex;
    }

    viewHistory(paramId) {

        this.displayDialog = true;

        this.inwardService.getAllParameterHistory(this.itemId, paramId).subscribe(
            (response: any) => {
                this.inventorySpecificationDetailsHistory = response.data;
                if (this.inventorySpecificationDetailsHistory) {
                    this.inventorySpecificationDetailsHistory.map(history => {
                        history.createdate = moment(history.createdate, "DD/MM/YYYY HH:mm").toDate();
                    });
                }
                this.dialogRef = this.dialog.open(this.inventoryHistoryDialogTemplate, {
                    width: '60%',
                    disableClose: true,
                    autoFocus: false,

                });
            },

            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    isEditing(rowIndex: number): boolean {
        return rowIndex === this.editedRowIndex;
    }
    addOrEditValue(rowIndex: number, id: any, newValue: string, param: any) {
        if (this.editedRowIndex !== -1) {
            this.editedRowIndex = -1;
        } else {
            this.inventorySpecificationDetails.push({
                paramName: "",
                isMandatory: false,
                paramValue: newValue,
                isMultiValueParam: param.isMultiValueParam,
                multiValue: param.multiValue,
            });
        }
        // const postData = {
        //   inventorySpecificationDetails: this.inventorySpecificationDetails,
        // };
        const data = {
            invenId: id,
            itemId: this.inventoryDetailData.id,
            newParamValue: newValue,
            isMultiValueParam: param.isMultiValueParam,
        };

        const url = "/inventorySpecification/updateSpecificationValue";
        this.inwardService.postMethod(url, data).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.InventoryDetails(this.inventoryDetailData);
                } else {
                    this.toastr.info(response.responseMessage, 'Info!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }
    searchInventory(pageNumber) {
        if (this.searchOwnerType != "" && this.searchOwner == "") {
            this.toastr.info('Please select Owner!', '');
            return;
        } else {

            if (this.assignAllListdataitemsPerPage) {
                this.showItemPerPage = this.assignAllListdataitemsPerPage;
            }

            // 🔹 Page number fix
            if (pageNumber) {
                this.assigntAllPageProductListdata = pageNumber.pageIndex
                    ? pageNumber.pageIndex + 1
                    : pageNumber;
                this.showItemPerPage = pageNumber.pageSize || this.showItemPerPage;
            } else {
                this.assigntAllPageProductListdata = 1;
            }

            let page = {
                page: this.assigntAllPageProductListdata,
                pageSize: this.showItemPerPage || 5,
            };

            const url =
                "/item/searchItems?inwardId=" +
                this.searchInward +
                "&itemStatus=" +
                this.searchStatus +
                "&itemType=" +
                this.searchItemType +
                "&ownerId=" +
                this.searchOwner +
                "&ownerType=" +
                this.searchOwnerType +
                "&ownership=" +
                this.searchOwnership +
                "&productId=" +
                this.searchProductVal +
                "&warrantyStatus=" +
                this.searchWarrantyStatus +
                "&serialNumber=" +
                this.searchSerialNumber;

            this.inwardService.postMethod(url, page).subscribe(
                (response: any) => {
                    if (response.responseCode == 200) {
                        this.assignedAllInventoryList = response.dataList;
                        this.assignAllListdatatotalRecords = response.totalRecords;
                        this.chakedData = [];
                    } else if (response.responseCode == 404) {
                        this.toastr.info(response.responseMessage, 'Info!');
                        this.assignedAllInventoryList = [];
                        this.chakedData = [];
                        this.assignAllListdatatotalRecords = 0;
                    } else {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');
                        this.assignedAllInventoryList = [];
                        this.chakedData = [];
                        this.assignAllListdatatotalRecords = 0;
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.responseMessage}`, 'Failed!');
                }
            );
        }
    }


    openModal(id, custId) {
        this.dialogId = true;
        this.custId.next({
            custId: custId,
        });
    }

    closeSelectStaff() {
        this.dialogId = false;
    }

    clearSearchInventory() {
        this.searchOwnerType = "";
        this.searchOwner = "";
        this.searchProductVal = "";
        this.searchInward = "";
        this.searchOwnership = "";
        this.searchStatus = "";
        this.searchItemType = "";
        this.searchWarrantyStatus = "";
        this.searchSerialNumber = "";
        this.searchInventory("");
        this.assignAllListdataitemsPerPage = ITEMS_PER_PAGE;
        this.assigntAllPageProductListdata = 1;
    }
    pageAllInventriesList(pageEvent) {

        this.assigntAllPageProductListdata = pageEvent.pageIndex + 1;
        this.assignAllListdataitemsPerPage = pageEvent.pageSize;
        this.searchInventory(this.assigntAllPageProductListdata);
    }
    // pageAllInventriesList(pageNumber) {
    //     //this.assigntAllPageProductListdata = pageNumber;
    //     this.searchInventory(pageNumber);
    //     // if (!this.searchkey) {
    //     //   //this.getOutwardByStaffId();
    //     //   this.searchInventory();
    //     // } else {
    //     //   this.searchInventory();
    //     // }
    // }

    TotalAllInventriesItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        this.searchInventory("");
    }

    // searchAllProduct() {
    //
    //   if (!this.searchkey || this.searchkey !== this.searchProductName) {
    //     this.assigntAllPageProductListdata = 1;
    //   }
    //   this.searchkey = this.searchProductName;
    //   if (this.showItemPerPage) {
    //     this.assignAllListdataitemsPerPage = this.showItemPerPage;
    //   }
    //   this.searchData.filter[0].filterValue = this.searchProductName.trim();
    //   if (this.assignedAllFormGroup.valid) {
    //     this.assigntPageProductListdata = 1;
    //     let type = this.assignedAllFormGroup.value.destinationType;
    //     let ownerId = this.assignedAllFormGroup.value.destination;
    //     this.searchData.page = this.assigntPageProductListdata;
    //     let page = {
    //       page: this.assigntPageProductListdata,
    //       pageSize: this.showItemPerPage,
    //       sortBy: "id",
    //     };
    //     //const url = '/state/search'
    //     this.inwardService.getsearchAssignInventories(page, ownerId, type).subscribe(
    //       (response: any) => {
    //         if (response.responseCode == 404) {
    //           this.messageService.add({
    //             severity: "info",
    //             summary: "Info",
    //             detail: response.responseMessage,
    //             icon: "far fa-times-circle",
    //           });
    //           this.assignedAllInventoryList = [];
    //           this.assignAllListdatatotalRecords = 0;
    //         } else {
    //           this.assignedAllInventoryList = response.dataList;
    //           this.assignAllListdatatotalRecords = response.totalRecords;
    //         }
    //
    //       },
    //       (error: any) => {
    //         this.messageService.add({
    //           severity: "error",
    //           summary: "Error",
    //           detail: error.error.ERROR,
    //           icon: "far fa-times-circle",
    //         });
    //
    //       }
    //     );
    //   }
    // }

    // clearSearchAllProduct() {
    //   this.searchProductName = "";
    //   this.showAllAssignedInventries();
    // }
    next() {
        this.first = this.first + this.rows;
    }

    prev() {
        this.first = this.first - this.rows;
    }

    reset() {
        this.first = 0;
    }

    isLastPage(): boolean {
        return this.assignedInventoryList
            ? this.first === this.assignedInventoryList.length - this.rows
            : true;
    }

    isFirstPage(): boolean {
        return this.assignedInventoryList ? this.first === 0 : true;
    }

    getOutwardByStaffId() {
        this.assignedInventoryList = [];
        this.outwardService.getByStaffId(this.userId).subscribe(
            (res: any) => {
                this.assignedInventoryList = res.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    warehouseIdsList = [];
    warehouseNamesList = [];
    getAllAssignInventories(pageNumber) {
        this.assignedInventoryList = [];
        if (this.showAllInventoryItemPerPage) {
            this.assignListdataitemsPerPage = this.showAllInventoryItemPerPage;
        }
        if (pageNumber != "") {
            this.assigntPageProductListdata = pageNumber;
        } else {
            this.assigntPageProductListdata = 1;
        }
        let assignInventoriesData = {
            page: this.assigntPageProductListdata,
            pageSize: this.showAllInventoryItemPerPage,
        };
        //let size: number;
        this.searchkey = "";
        //let List = this.assigntPageProductListdata;
        // let assignInventoriesData = {
        //   page: List,
        //   pageSize: this.showAllInventoryItemPerPage,
        // };
        this.inwardService.getAllAssignInventories(this.userId, assignInventoriesData).subscribe(
            (response: any) => {
                this.assignedInventoryList = response.dataList;
                this.assignListdatatotalRecords = response.totalRecords;
                this.searchkey = "";

                for (let i = 0; i < this.assignedInventoryList.length; i++) {
                    this.warehouseIdsList.push(this.assignedInventoryList[i].sourceId);
                }
                const url = "/getAllByWarehouseIds";
                this.warehouseService.getNamesByIds(this.warehouseIdsList).subscribe(
                    (res: any) => {
                        this.warehouseNamesList = res.dataList;
                        this.warehouseIdsList = [];
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                    }
                );
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    isSinglepaymentChecked = false;
    getshowAllAssignedInventries() {
        this.assignedAllInventoryList = [];
        let List = this.assigntAllPageProductListdata;
        let assignAllInventoriesData = {
            page: List,
            pageSize: this.showItemPerPage,
        };
        this.inwardService
            .getAllAssignInventoryMappingByStaffId(this.userId, "Staff", assignAllInventoriesData)
            .subscribe(
                (response: any) => {
                    this.assignedAllInventoryList = response.dataList;
                    // this.assignAllListdatatotalRecords = response.dataList;

                    this.chakedData = [];
                    this.ispaymentChecked = false;
                    this.isSinglepaymentChecked = false;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        //     const url = "/getAllByWarehouseIds";
        //     this.warehouseService.getNamesByIds(this.warehouseIdsList).subscribe(
        //       (res: any) => {
        //         this.warehouseNamesList = res.dataList;
        //         this.warehouseIdsList = [];
        //       },
        //       (error: any) => {
        //         this.messageService.add({
        //           severity: "error",
        //           summary: "Error",
        //           detail: error.error.ERROR,
        //           icon: "far fa-times-circle",
        //         });
        //
        //       }
        //     );
        //   },
        //   (error: any) => {
        //     this.messageService.add({
        //       severity: "error",
        //       summary: "Error",
        //       detail: error.error.ERROR,
        //       icon: "far fa-times-circle",
        //     });
        //
        //   }
        // );
    }

    showStaffInventory() {
        this.showStaffsInventory = true;
        this.showCustomerAssignedInventory = false;
        this.showNonSerializedCustomerAssignedInventory = false;
        this.showSerializedPOPAssignedInventory = false;
        this.showNonSerializedPOPAssignedInventory = false;
        this.showSerializedServiceAreaAssignedInventory = false;
        this.showNonSerializedServiceAreaAssignedInventory = false;
        this.showAllAssignedInventrie = false;
        //this.getOutwardByStaffId();
        this.getAllAssignInventories("");
    }
    showInventoryAssignedToCustomer() {
        this.isDetails2 = true;
        this.isDetails1 = false;
        this.isDetails3 = true;
        this.isDetails4 = false;
        this.isDetails5 = false;
        this.isDetails6 = true;
        this.isDetails7 = false;
        this.isDetails8 = false;
        this.isDetails9 = false;
        this.isDetails10 = false;
        this.isDetails11 = false;
        this.showCustomerAssignedInventory = true;
        this.showNonSerializedCustomerAssignedInventory = true;
        this.showSerializedPOPAssignedInventory = false;
        this.showNonSerializedPOPAssignedInventory = false;
        this.showSerializedServiceAreaAssignedInventory = false;
        this.showNonSerializedServiceAreaAssignedInventory = false;
        this.showStaffsInventory = false;
        this.showAllAssignedInventrie = false;
        this.showInventoryAssignedFlag = true;
        this.showInventoryAssignedCustomerFlag = true;
        this.showInventoryAssignedPopFlag = false;
        this.showInventoryAssignedServiceAreaFlag = false;
        this.getCustomerInventoryMapping("");
    }

    showInventoryAssignedToPOP() {
        this.isDetails8 = true;
        this.isDetails1 = false;
        this.isDetails2 = true;
        this.isDetails3 = false;
        this.isDetails4 = true;
        this.isDetails5 = false;
        this.isDetails6 = true;
        this.isDetails9 = false;
        this.isDetails7 = false;
        this.isDetails10 = false;
        this.isDetails11 = false;
        this.isPop = false;
        this.isProduct = false;
        this.showCustomerAssignedInventory = false;
        this.showNonSerializedCustomerAssignedInventory = false;
        this.showSerializedPOPAssignedInventory = true;
        this.showNonSerializedPOPAssignedInventory = false;
        this.showSerializedServiceAreaAssignedInventory = false;
        this.showNonSerializedServiceAreaAssignedInventory = false;
        this.showStaffsInventory = false;
        this.showAllAssignedInventrie = false;
        this.showInventoryAssignedFlag = true;
        this.showInventoryAssignedCustomerFlag = false;
        this.showInventoryAssignedPopFlag = true;
        this.showInventoryAssignedServiceAreaFlag = false;
        this.getSerializedItemPopByInventoryMappingByStaffId("");
    }

    showInventoryAssignedToServiceArea() {
        this.isDetails5 = true;
        this.isDetails10 = true;
        this.isDetails2 = true;
        this.isDetails11 = false;
        this.isDetails1 = false;
        this.isDetails3 = false;
        this.isDetails4 = false;
        this.isDetails6 = false;
        this.isDetails7 = false;
        this.isDetails8 = false;
        this.isDetails9 = false;
        this.isServcieArea = false;
        this.isServiceAreaProduct = false;
        this.showCustomerAssignedInventory = false;
        this.showNonSerializedCustomerAssignedInventory = false;
        this.showSerializedPOPAssignedInventory = false;
        this.showNonSerializedPOPAssignedInventory = false;
        this.showSerializedServiceAreaAssignedInventory = true;
        this.showNonSerializedServiceAreaAssignedInventory = false;
        this.showStaffsInventory = false;
        this.showAllAssignedInventrie = false;
        this.showInventoryAssignedFlag = true;
        this.showInventoryAssignedCustomerFlag = false;
        this.showInventoryAssignedPopFlag = false;
        this.showInventoryAssignedServiceAreaFlag = true;
        this.getSerializedItemServiceAreaByInventoryMappingByStaffId("");
    }

    showAllAssignedInventries() {
        this.isDetails1 = true;
        this.isDetails2 = false;
        this.isDetails3 = false;
        this.isDetails4 = false;
        this.isDetails5 = false;
        this.isDetails6 = false;
        this.isDetails7 = false;
        this.isDetails8 = false;
        this.isDetails9 = false;
        this.isDetails10 = false;
        this.isDetails11 = false;
        this.showStaffsInventory = false;
        this.showCustomerAssignedInventory = false;
        this.showNonSerializedCustomerAssignedInventory = false;
        this.showSerializedPOPAssignedInventory = false;
        this.showNonSerializedPOPAssignedInventory = false;
        this.showSerializedServiceAreaAssignedInventory = false;
        this.showNonSerializedServiceAreaAssignedInventory = false;
        this.showAllAssignedInventrie = true;
        this.showInventoryAssignedFlag = false;
        this.showInventoryAssignedPopFlag = false;
        this.showInventoryAssignedCustomerFlag = false;
        this.showInventoryAssignedServiceAreaFlag = false;
        this.searchInventory("");
    }

    getCustomerInventoryMapping(pageNumber) {
        this.searchByFilterColumn = "";
        this.searchName = "";
        this.searchNameNonSeriCust = "";
        this.searchNameNonSeriPop = "";
        this.searchNameNonSeriServiceArea = "";
        this.searchNameSeriPop = "";
        this.searchNameSeriServiceArea = "";
        this.assignedInventoryList = [];
        if (this.showAllCustInventoryItemPerPage) {
            this.assignCustomerListdataitemsPerPage = this.showAllCustInventoryItemPerPage;
        }
        if (pageNumber != "") {
            this.currentPageAssigntListdata = pageNumber;
        } else {
            this.currentPageAssigntListdata = 1;
        }
        let page = {
            page: this.currentPageAssigntListdata,
            pageSize: this.showAllCustInventoryItemPerPage,
        };
        this.inwardService
            .getSerializedItemCustomerInventoryMappingByStaffId(this.userId, page)
            .subscribe(
                (response: any) => {
                    this.assignedInventoryList = response.dataList;
                    this.assignCustomertListdatatotalRecords = response.totalRecords;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
    }
    getNonSerializedItemCustomerInventoryMappingByStaffId(pageNumber) {
        this.isDetails8 = false;
        this.isDetails1 = false;
        this.isDetails2 = true;
        this.isDetails3 = true;
        this.isDetails4 = false;
        this.isDetails5 = false;
        this.isDetails6 = false;
        this.isDetails9 = false;
        this.isDetails7 = true;
        this.isDetails10 = false;
        this.isDetails11 = false;
        this.showStaffsInventory = false;
        this.showCustomerAssignedInventory = false;
        this.showNonSerializedCustomerAssignedInventory = true;
        this.showSerializedPOPAssignedInventory = false;
        this.showNonSerializedPOPAssignedInventory = false;
        this.showSerializedServiceAreaAssignedInventory = false;
        this.showNonSerializedServiceAreaAssignedInventory = false;
        this.showInventoryAssignedFlag = true;
        this.showInventoryAssignedCustomerFlag = true;
        this.showInventoryAssignedPopFlag = false;
        this.showInventoryAssignedServiceAreaFlag = false;
        this.showAllAssignedInventrie = false;
        this.assignedNonSerializedCustInventoryList = [];
        this.searchByFilterColumn = "";
        this.searchName = "";
        this.searchNameNonSeriCust = "";
        this.searchNameNonSeriPop = "";
        this.searchNameNonSeriServiceArea = "";
        this.searchNameSeriPop = "";
        this.searchNameSeriServiceArea = "";
        if (this.showAllNonSerializedCustInventoryItemPerPage) {
            this.assignNonSerializedCustomerListdataitemsPerPage =
                this.showAllNonSerializedCustInventoryItemPerPage;
        }
        if (pageNumber != "") {
            this.currentCustomerNonSerializedPageAssigntListdata = pageNumber;
        } else {
            this.currentCustomerNonSerializedPageAssigntListdata = 1;
        }
        let page = {
            page: this.currentCustomerNonSerializedPageAssigntListdata,
            pageSize: this.showAllNonSerializedCustInventoryItemPerPage,
        };
        this.inwardService
            .getNonSerializedItemCustomerInventoryMappingByStaffId(this.userId, page)
            .subscribe(
                (response: any) => {
                    this.assignedNonSerializedCustInventoryList = response.dataList;
                    this.assignNonSerializedCustomertListdatatotalRecords = response.totalRecords;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
    }
    getSerializedItemPopByInventoryMappingByStaffId(pageNumber) {
        this.searchByFilterColumn = "";
        this.searchName = "";
        this.searchNameNonSeriCust = "";
        this.searchNameNonSeriPop = "";
        this.searchNameNonSeriServiceArea = "";
        this.searchNameSeriPop = "";
        this.searchNameSeriServiceArea = "";
        this.assignedSerializedPOPInventoryList = [];
        if (this.showAllSerializedPOPInventoryItemPerPage) {
            this.assignSerializedPopListdataitemsPerPage = this.showAllSerializedPOPInventoryItemPerPage;
        }
        if (pageNumber != "") {
            this.currentPopSerializedPageAssigntListdata = pageNumber;
        } else {
            this.currentPopSerializedPageAssigntListdata = 1;
        }
        let page = {
            page: this.currentPopSerializedPageAssigntListdata,
            pageSize: this.showAllSerializedPOPInventoryItemPerPage,
        };
        this.inwardService.getSerializedItemPopByInventoryMappingByStaffId(this.userId, page).subscribe(
            (response: any) => {
                this.assignedSerializedPOPInventoryList = response.dataList;
                this.assignSerializedPopListdatatotalRecords = response.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }
    getNonSerializedItemPopByInventoryMappingByStaffId(pageNumber) {
        this.isDetails9 = true;
        this.isDetails1 = false;
        this.isDetails6 = true;
        this.isDetails8 = false;
        this.isDetails7 = false;
        this.isDetails10 = false;
        this.isDetails11 = false;
        this.showStaffsInventory = false;
        this.showCustomerAssignedInventory = false;
        this.showNonSerializedCustomerAssignedInventory = false;
        this.showSerializedPOPAssignedInventory = false;
        this.showNonSerializedPOPAssignedInventory = true;
        this.showSerializedServiceAreaAssignedInventory = false;
        this.showNonSerializedServiceAreaAssignedInventory = false;
        this.showAllAssignedInventrie = false;
        this.showInventoryAssignedFlag = true;
        this.showInventoryAssignedCustomerFlag = false;
        this.showInventoryAssignedPopFlag = true;
        this.showInventoryAssignedServiceAreaFlag = false;
        this.assignedNonSerializePOPInventoryList = [];
        this.searchByFilterColumn = "";
        this.searchName = "";
        this.searchNameNonSeriCust = "";
        this.searchNameNonSeriPop = "";
        this.searchNameNonSeriServiceArea = "";
        this.searchNameSeriPop = "";
        this.searchNameSeriServiceArea = "";
        if (this.showAllNonSerializedPOPInventoryItemPerPage) {
            this.assignNonSerializedPopListdataitemsPerPage =
                this.showAllNonSerializedPOPInventoryItemPerPage;
        }
        if (pageNumber != "") {
            this.currentPopNonSerializedPageAssigntListdata = pageNumber;
        } else {
            this.currentPopNonSerializedPageAssigntListdata = 1;
        }
        let page = {
            page: this.currentPopNonSerializedPageAssigntListdata,
            pageSize: this.showAllNonSerializedPOPInventoryItemPerPage,
        };
        this.inwardService
            .getNonSerializedItemPopByInventoryMappingByStaffId(this.userId, page)
            .subscribe(
                (response: any) => {
                    this.assignedNonSerializePOPInventoryList = response.dataList;
                    this.assignNonSerializedPopListdatatotalRecords = response.totalRecords;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
    }
    getSerializedItemServiceAreaByInventoryMappingByStaffId(pageNumber) {
        this.searchByFilterColumn = "";
        this.searchName = "";
        this.searchNameNonSeriCust = "";
        this.searchNameNonSeriPop = "";
        this.searchNameNonSeriServiceArea = "";
        this.searchNameSeriPop = "";
        this.searchNameSeriServiceArea = "";
        this.assignedSerializedServiceAreaInventoryList = [];
        if (this.showAllSerializedServiceAreaInventoryItemPerPage) {
            this.assignSerializedServiceAreaListdataitemsPerPage =
                this.showAllSerializedServiceAreaInventoryItemPerPage;
        }
        if (pageNumber != "") {
            this.currentServiceAreaSerializedPageAssigntListdata = pageNumber;
        } else {
            this.currentServiceAreaSerializedPageAssigntListdata = 1;
        }
        let page = {
            page: this.currentServiceAreaSerializedPageAssigntListdata,
            pageSize: this.showAllSerializedServiceAreaInventoryItemPerPage,
        };
        this.inwardService
            .getSerializedItemServiceAreaByInventoryMappingByStaffId(this.userId, page)
            .subscribe(
                (response: any) => {
                    this.assignedSerializedServiceAreaInventoryList = response.dataList;
                    this.assignSerializedServiceAreaListdatatotalRecords = response.totalRecords;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
    }
    getNonSerializedItemServiceAreaByInventoryMappingByStaffId(pageNumber) {
        this.isDetails11 = true;
        this.isDetails8 = true;
        this.isDetails2 = true;
        this.isDetails10 = false;
        this.isDetails1 = false;
        this.isDetails3 = false;
        this.isDetails4 = false;
        this.isDetails5 = true;
        this.isDetails6 = false;
        this.isDetails9 = false;
        this.isDetails7 = false;
        this.searchByFilterColumn = "";
        this.searchName = "";
        this.searchNameNonSeriCust = "";
        this.searchNameNonSeriPop = "";
        this.searchNameNonSeriServiceArea = "";
        this.searchNameSeriPop = "";
        this.searchNameSeriServiceArea = "";
        this.showStaffsInventory = false;
        this.showCustomerAssignedInventory = false;
        this.showNonSerializedCustomerAssignedInventory = false;
        this.showSerializedPOPAssignedInventory = false;
        this.showNonSerializedPOPAssignedInventory = false;
        this.showSerializedServiceAreaAssignedInventory = false;
        this.showNonSerializedServiceAreaAssignedInventory = true;
        this.showAllAssignedInventrie = false;
        this.showInventoryAssignedFlag = true;
        this.showInventoryAssignedCustomerFlag = false;
        this.showInventoryAssignedPopFlag = false;
        this.showInventoryAssignedServiceAreaFlag = true;
        this.assignedNonSerializedServiceAreaInventoryList = [];
        if (this.showAllNonSerializedServiceAreaInventoryItemPerPage) {
            this.assignNonSerializedServiceAreaListdataitemsPerPage =
                this.showAllNonSerializedServiceAreaInventoryItemPerPage;
        }
        if (pageNumber != "") {
            this.currentServiceAreaNonSerializedPageAssigntListdata = pageNumber;
        } else {
            this.currentServiceAreaNonSerializedPageAssigntListdata = 1;
        }
        let page = {
            page: this.currentServiceAreaNonSerializedPageAssigntListdata,
            pageSize: this.showAllNonSerializedServiceAreaInventoryItemPerPage,
        };
        this.inwardService
            .getNonSerializedItemServiceAreaByInventoryMappingByStaffId(this.userId, page)
            .subscribe(
                (response: any) => {
                    this.assignedNonSerializedServiceAreaInventoryList = response.dataList;
                    this.assignNonSerializedServiceAreaListdatatotalRecords = response.totalRecords;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
    }
    //   openModal(id, custId) {
    //     this.customerDetailsService.show(id);
    //     this.custId.next({
    //       custId: custId,
    //     });
    //   }

    openAssignedQtyMACMapping(customerId, outwardId, mappingId, hasMac, hasSerial) {
        // if (hasMac || hasSerial) {
        //   this.macMappingList = [];
        //   this.outwardService
        //     .getMacMappingByCustomerIdAndOutwardId(customerId, outwardId, mappingId)
        //     .subscribe((res: any) => {
        //       if (res.dataList.length > 0) {
        //         this.macMappingList = res.dataList;
        //         this.showMac = true;
        //         this.hasMac = hasMac;
        //         this.hasSerial = hasSerial;
        //       }
        //     });
        // }
    }
    closeMacMapping() {
        this.showMac = false;
        this.macMappingList = [];
    }

    TotalItemPerPage(event) {
        this.showAllInventoryItemPerPage = Number(event.value);
        // if (this.assigntPageProductListdata > 1) {
        //   this.assigntPageProductListdata = 1;
        // }
        if (!this.searchkey) {
            //this.getOutwardByStaffId();
            this.getAllAssignInventories("");
        } else {
            this.searchProduct();
        }
    }

    pageChangedProductList(pageNumber) {
        //this.assigntPageProductListdata = pageNumber;
        if (!this.searchkey) {
            //this.getOutwardByStaffId();
            this.getAllAssignInventories(pageNumber);
        } else {
            this.searchProduct();
        }
    }
    pageAllInventriesProductList(pageNumber) {
        this.assigntAllPageProductListdata = pageNumber;
        if (!this.searchkey) {
            //this.getOutwardByStaffId();
            this.getshowAllAssignedInventries();
        } else {
            this.searchProduct();
        }
    }
    // Serialized Item to Customer
    pageChangedassignCustomerList(event) {
        this.currentPageAssigntListdata = event.pageIndex + 1;
        this.showAllCustInventoryItemPerPage = event.pageSize;

        if (!this.searchkey) {
            this.getCustomerInventoryMapping(this.currentPageAssigntListdata);
        } else {
            this.searchSerializedCustomer();
        }
    }
    // Serialized Item to Customer
    TotalItemPerPageAssignCustomer(event) {
        this.showAllCustInventoryItemPerPage = Number(event.value);
        if (this.currentPageAssigntListdata > 1) {
            this.currentPageAssigntListdata = 1;
        }
        if (!this.searchkey) {
            this.getCustomerInventoryMapping("");
        } else {
            this.searchSerializedCustomer();
        }
    }
    // NON Serialized Item to Customer
    pageChangedassignNonSerializedCustomerList(pageNumber) {
        this.currentPageAssigntListdata = pageNumber;
        if (!this.searchkey) {
            this.getNonSerializedItemCustomerInventoryMappingByStaffId(pageNumber);
        } else {
            this.searchNonSerializedCustomer();
        }
    }
    // NON Serialized Item to Customer
    TotalItemPerPageAssignNonSerializedCustomer(event) {
        this.showAllNonSerializedCustInventoryItemPerPage = Number(event.value);
        if (this.currentPageAssigntListdata > 1) {
            this.currentPageAssigntListdata = 1;
        }
        if (!this.searchkey) {
            this.getNonSerializedItemCustomerInventoryMappingByStaffId("");
        } else {
            this.searchNonSerializedCustomer();
        }
    }
    // Serialized Item to POP
    pageChangedassignPOPList(pageNumber) {
        this.currentPageAssigntListdata = pageNumber;
        if (!this.searchkey) {
            this.getSerializedItemPopByInventoryMappingByStaffId(pageNumber);
        } else {
            this.searchSerializedPop();
        }
    }
    // Serialized Item to POP
    TotalItemPerPageAssignPOP(event) {
        this.showAllSerializedPOPInventoryItemPerPage = Number(event.value);
        if (this.currentPageAssigntListdata > 1) {
            this.currentPageAssigntListdata = 1;
        }
        if (!this.searchkey) {
            this.getSerializedItemPopByInventoryMappingByStaffId("");
        } else {
            this.searchSerializedPop();
        }
    }
    // NON Serialized Item to POP
    pageChangedassignNonSerializedPOPList(pageNumber) {
        this.currentPageAssigntListdata = pageNumber;
        if (!this.searchkey) {
            this.getNonSerializedItemPopByInventoryMappingByStaffId(pageNumber);
        } else {
            this.searchNonSerializedPop();
        }
    }
    // NON Serialized Item to POP
    TotalItemPerPageAssignNonSerializedPOP(event) {
        this.showAllNonSerializedPOPInventoryItemPerPage = Number(event.value);
        if (this.currentPageAssigntListdata > 1) {
            this.currentPageAssigntListdata = 1;
        }
        if (!this.searchkey) {
            this.getNonSerializedItemPopByInventoryMappingByStaffId("");
        } else {
            this.searchNonSerializedPop();
        }
    }
    // Serialized Item to Service Area
    pageChangedassignServiceAreaList(pageNumber) {
        this.currentPageAssigntListdata = pageNumber;
        if (!this.searchkey) {
            this.getSerializedItemServiceAreaByInventoryMappingByStaffId(pageNumber);
        } else {
            this.searchSerializedServiceArea();
        }
    }
    // Serialized Item to Service Area
    TotalItemPerPageAssignServiceArea(event) {
        this.showAllSerializedServiceAreaInventoryItemPerPage = Number(event.value);
        if (this.currentPageAssigntListdata > 1) {
            this.currentPageAssigntListdata = 1;
        }
        if (!this.searchkey) {
            this.getSerializedItemServiceAreaByInventoryMappingByStaffId("");
        } else {
            this.searchSerializedServiceArea();
        }
    }
    // NON Serialized Item to Service Area
    pageChangedassignNonSerializedServiceAreaList(pageNumber) {
        this.currentPageAssigntListdata = pageNumber;
        if (!this.searchkey) {
            this.getNonSerializedItemServiceAreaByInventoryMappingByStaffId(pageNumber);
        } else {
            this.searchNonSerializedServiceArea();
        }
    }
    // NON Serialized Item to Service Area
    TotalItemPerPageAssignNonSerializedServiceArea(event) {
        this.showAllNonSerializedServiceAreaInventoryItemPerPage = Number(event.value);
        if (this.currentPageAssigntListdata > 1) {
            this.currentPageAssigntListdata = 1;
        }
        if (!this.searchkey) {
            this.getNonSerializedItemServiceAreaByInventoryMappingByStaffId("");
        } else {
            this.searchNonSerializedServiceArea();
        }
    }

    isDestAStaffOrCustomer = false;
    getDestinations(sourceType): void {
        this.searchOwner = "";

        if (sourceType.value == "Warehouse") {
            this.inwardService.getAllWareHouse().subscribe((res: any) => {
                this.warehouses = res.dataList;
                this.destinations = res.dataList;
            });
            this.isDestAStaffOrCustomer = false;
        } else if (sourceType.value == "Staff") {
            this.isDestAStaffOrCustomer = true;
            this.outwardService.getAllStaff().subscribe((res: any) => {
                this.staffList = res.dataList;
                this.destinations = res.dataList;
            });
        } else if (sourceType.value == "Partner") {
            const url = "/partner/allActive";
            this.isDestAStaffOrCustomer = false;
            this.outwardService.getMethod(url).subscribe(
                (res: any) => {
                    this.destinations = res.dataList;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        } else if (sourceType.value == "Service Area") {
            const url = "/serviceArea/getAllServiceAreaByStaff";
            this.isDestAStaffOrCustomer = false;
            this.serviceAreaService.getMethod(url).subscribe(
                (res: any) => {
                    this.destinations = res.dataList;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        } else if (sourceType.value == "POP") {
            const url = "/popmanagement/all";
            this.isDestAStaffOrCustomer = false;
            this.popService.getMethodWithCache(url).subscribe(
                (res: any) => {
                    this.destinations = res.dataList;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        } else if (sourceType.value == "Customer") {
            const url = `/customers/getActiveCustomersList`;
            this.isDestAStaffOrCustomer = true;
            this.popService.getMethod(url).subscribe(
                (res: any) => {
                    this.destinations = res.dataList;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }
    allSelectBatch(event) {
        if (event.checked == true) {
            this.chakedData = [];
            let checkedData = this.assignedAllInventoryList;
            // let itemType = this.changeType;
            for (let i = 0; i < checkedData.length; i++) {
                {
                    this.chakedData.push({
                        id: this.assignedAllInventoryList[i].id,
                        serialNumber: this.assignedAllInventoryList[i].serialNumber,
                        remark: "",
                        inwardNumber: this.assignedAllInventoryList[i].currentInwardNumber,
                        type: "",
                        warranty: this.assignedAllInventoryList[i].warranty,
                        itemStatus: this.assignedAllInventoryList[i].itemStatus,
                        ownershipStatus: "",
                        condition: this.assignedAllInventoryList[i].condition,

                        //changeType: this.changeType[i].label
                    });
                }
            }
            this.chakedData.forEach((value, index) => {
                checkedData.forEach(element => {
                    if (element.id == value.id) {
                        element.isSinglepaymentChecked = true;
                    }
                });
            });

            this.ispaymentChecked = true;
            // console.log(this.chakedPaymentData);
        }
        if (event.checked == false) {
            let checkedData = this.assignedAllInventoryList;
            this.chakedData.forEach((value, index) => {
                checkedData.forEach(element => {
                    if (element.id == value.id) {
                        element.isSinglepaymentChecked = false;
                    }
                });
            });
            this.chakedData = [];
            // console.log(this.chakedPaymentData);
            this.ispaymentChecked = false;
            // this.allIsChecked = false;
        }
    }

    addbatchChecked(id, event) {
        if (event.checked) {
            this.assignedAllInventoryList.forEach((value, i) => {
                if (value.id == id) {
                    this.chakedData.push({
                        id: value.id,
                        serialNumber: value.serialNumber,
                        remark: "",
                        inwardNumber: value.currentInwardNumber,
                        type: "",
                        warranty: this.assignedAllInventoryList[i].warranty,
                        itemStatus: this.assignedAllInventoryList[i].itemStatus,
                        ownershipStatus: "",
                        condition: this.assignedAllInventoryList[i].condition,
                    });
                }
            });

            if (this.assignedAllInventoryList.length === this.chakedData.length) {
                this.ispaymentChecked = true;
                // this.allIsChecked = true;
            }
            // console.log(this.chakedPaymentData);
        } else {
            let checkedData = this.assignedAllInventoryList;
            checkedData.forEach(element => {
                if (element.id == id) {
                    element.isSinglepaymentChecked = false;
                }
            });
            this.chakedData.forEach((value, index) => {
                if (value.id == id) {
                    this.chakedData.splice(index, 1);
                    // console.log(this.chakedPaymentData);
                }
            });

            if (
                this.chakedData.length == 0 ||
                this.chakedData.length !== this.assignedAllInventoryList.length
            ) {
                this.ispaymentChecked = false;
            }
        }
    }

    inwardNotEqualsFlag: boolean = false;
    submitData(): void {
        let itemIds: any = [];
        let idsList: any = [];
        let map = new Map<string, string[]>();
        let remark: any = [];
        let inwardNum: string = this.chakedData[0].inwardNumber;
        this.inwardNotEqualsFlag = false;
        this.chakedData.forEach(e => {
            let idnum = e.id;
            if (inwardNum != e.inwardNumber) this.inwardNotEqualsFlag = true;
            itemIds.push({ key: idnum.toString(), value: e.remark });
        });
        if (!this.inwardNotEqualsFlag) {
            let object = itemIds.reduce(
                (obj, item) => Object.assign(obj, { [item.key]: item.value }),
                {}
            );
            this.outwardService.returnMethod(object).subscribe(
                (res: any) => {
                    if (res.responseCode == 406) {
                        this.toastr.error(`${res.responseMessage}`, 'Failed!');

                    } else {
                        this.toastr.success(`${res.responseMessage}`, 'Success!');


                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        } else {
            error: (error) => {
                this.toastr.error(`${error.error.ERROR}`, 'External Item Should not be Allowed to be return!');
            }

        }
    }
    changeNewType(): void {
        let id;
        let itemIds: any = [];
        this.chakedData.forEach(e => {
            let idnum = e.id;
            itemIds.push({ key: idnum.toString(), value: e.type });
        });
        let object = itemIds.reduce((obj, item) => Object.assign(obj, { [item.key]: item.value }), {});

        this.outwardService.changeNewTypeMethod(object).subscribe(
            (res: any) => {
                if (res.responseCode == 406) {
                    this.toastr.error(`${res.responseMessage}`, 'Failed!');

                } else {
                    this.toastr.success("Successfully", 'Success!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }
    changeWarranty(): void {
        let id;
        let type = this.changeWarrantyFormGroup.value.warrantylabels;
        if (this.chakedData.length > 0) {
            id = this.assignedAllInventoryList[0].id;
        }
        let itemIds: any = [];
        this.chakedData.forEach(e => {
            let idnum = e.id;
            itemIds.push({ key: idnum.toString(), value: e.warranty });
            // map.set(e.id, e.remark);
        });
        let object = itemIds.reduce((obj, item) => Object.assign(obj, { [item.key]: item.value }), {});

        this.outwardService.changeWarrantyMethod(object).subscribe(
            (res: any) => {
                if (res.responseCode == 406) {
                    this.toastr.error(`${res.responseMessage}`, 'Failed!');

                } else {
                    this.toastr.success("Successfully", 'Success!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    // changeItemStatus(): void {
    //   let id;
    //   let statusList = this.changeItemStatusFormGroup.value.itemStatuslabels;
    //   if (this.chakedData.length > 0) {
    //     id = this.assignedAllInventoryList[0].id;
    //   }
    //   let itemIds: any = [];
    //   this.chakedData.forEach(e => {
    //     let idnum = e.id;
    //     itemIds.push({ key: idnum.toString(), value: e.itemStatus });
    //     // map.set(e.id, e.remark);
    //   });
    //   let object = itemIds.reduce((obj, item) => Object.assign(obj, { [item.key]: item.value }), {});
    //
    //   this.outwardService.changeStatusMethod(object).subscribe(
    //     (res: any) => {
    //       if (res.responseCode == 406) {
    //
    //         this.messageService.add({
    //           severity: "error",
    //           summary: "Error",
    //           detail: res.responseMessage,
    //           icon: "far fa-times-circle",
    //         });
    //       } else {
    //         this.messageService.add({
    //           severity: "success",
    //           summary: "Successfully",
    //           detail: res.message,
    //           icon: "far fa-check-circle",
    //         });
    //
    //       }
    //     },
    //     (error: any) => {
    //
    //       this.messageService.add({
    //         severity: "error",
    //         summary: "Error",
    //         detail: error.error.msg,
    //         icon: "far fa-times-circle",
    //       });
    //     }
    //   );
    // }

    changeItemOwnershipStatus(): void {
        let id;
        let statusList = this.changeItemOwnershipStatusFormGroup.value.itemOwnershipStatuslabels;
        if (this.chakedData.length > 0) {
            id = this.assignedAllInventoryList[0].id;
        }
        let itemIds: any = [];
        this.chakedData.forEach(e => {
            let idnum = e.id;
            itemIds.push({ key: idnum.toString(), value: e.ownershipStatus });
            // map.set(e.id, e.remark);
        });
        let object = itemIds.reduce((obj, item) => Object.assign(obj, { [item.key]: item.value }), {});

        this.outwardService.changeItemOwnershipStatusMethod(object).subscribe(
            (res: any) => {
                if (res.responseCode == 406) {
                    this.toastr.error(`${res.responseMessage}`, 'Failed!');

                } else {
                    this.toastr.success("Successfully", 'Success!');


                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    returnItems() {
        this.addReturn = true;
        // this.newBatchName = "";
    }
    editItemsName() {
        this.addChangeType = true;
        this.condition;
    }
    editItemsWarranty() {
        this.addChangeType = true;
    }
    editItemStatus() {
        this.addChangeType = true;
    }

    filetrConditions(status: string, chakedDataObj: any) {
        if (
            status == "Refurbished" &&
            chakedDataObj?.condition == "New" &&
            chakedDataObj?.warranty == "Expired" &&
            chakedDataObj?.itemStatus == "Defective"
        ) {
            return true;
        } else if (status == "Refurbished") {
            return false;
        }

        if (status == "DamagedAtStore" && chakedDataObj?.itemStatus == "Defective") {
            return true;
        } else if (status == "DamagedAtStore") {
            return false;
        }

        if (status == "DamagedAtSite" && chakedDataObj?.itemStatus == "Defective") {
            return true;
        } else if (status == "DamagedAtSite") {
            return false;
        }

        if (
            status == "ScrappedAtStore" &&
            chakedDataObj?.condition == "DamagedAtStore" &&
            chakedDataObj?.itemStatus == "Defective"
        ) {
            return true;
        } else if (status == "ScrappedAtStore") {
            return false;
        }

        if (
            status == "ScrappedAtSite" &&
            chakedDataObj?.condition == "DamagedAtSite" &&
            chakedDataObj?.itemStatus == "Defective"
        ) {
            return true;
        } else if (status == "ScrappedAtSite") {
            return false;
        }
    }

    ChangeTypeFormGroup(id): UntypedFormGroup {
        return this.fb.group({
            condition: ["", Validators.required],
            remarks: ["", Validators.required],
            otherreason: ["", Validators.required],
            file: [""],
            itemId: [id],
        });
    }

    changeOwnershipStatusFormGroupFun(id): UntypedFormGroup {
        return this.fb.group({
            ownershipType: ["", Validators.required],
            remarks: [""],
            id: [id],
        });
    }

    changeInventoryStatusFormGroupFun(id): UntypedFormGroup {
        return this.fb.group({
            itemStatus: ["", Validators.required],
            id: [id],
        });
    }

    returnItemFormGroupFun(id): UntypedFormGroup {
        return this.fb.group({
            remarks: ["", Validators.required],
            id: [id],
        });
    }

    warrantyChangeStatusFormGroupFun(id): UntypedFormGroup {
        return this.fb.group({
            warranty: ["", Validators.required],
            id: [id],
        });
    }
    openChangeTypeModal() {
        let dialogModal = false;


        if (this.chakedData.length == 0) {
            this.toastr.info("Please select at least one inventory!", "Info");
            dialogModal = true;
        }


        this.chakedData.forEach(value => {
            if (this.chakedData.length == 1 && value.itemStatus == "Allocated") {
                this.toastr.info("You can't perform this operation on an Allocated item!", "Info");
                dialogModal = true;
            }
        });


        this.chakedData.forEach(value => {
            if (this.chakedData.length > 1 && value.itemStatus == "Allocated") {
                this.toastr.info("You can't perform this operation on Allocated items!", "Info");
                dialogModal = true;
            }
        });


        if (!dialogModal) {
            this.chakedData.forEach(element => {
                this.changeTypeFormArray.push(this.ChangeTypeFormGroup(element.id));
            });

            this.fileName = [];
            this.files = [];
            this.updateItemFileData = [];

            for (let i = 0; i < this.changeTypeFormArray.controls.length; i++) {
                this.changeTypeFormArray.controls[i].get("otherreason").disable();
                this.fileName.push("");
                this.files.push("");
                this.updateItemFileData.push("");
            }



            this.dialogRef = this.dialog.open(this.changeTypeDialogTemplate, {
                width: '80%',
                disableClose: true,
                panelClass: 'custom-mat-dialog'
            });
        }
    }
    onCancel() {
        // if (this.dialogRef) {
        this.dialogRef.close();
        // }
    }

    // openChangeTypeModal() {
    //     var dialogModal = false;
    //     console.log("this.ceckdata", this.chakedData);
    //     if (this.chakedData.length == 0) {
    //         error: (error) => {
    //             this.toastr.info(error.ERROR || error.responseMessage, 'Please Select Atleast One Inventory!');
    //         }


    //         dialogModal = true;
    //     }
    //     this.chakedData.forEach(value => {
    //         if (this.chakedData.length == 1 && value.itemStatus == "Allocated") {
    //             error: (error) => {
    //                 this.toastr.info(error.ERROR || error.responseMessage, "You can't perform this operation on Allocated item!");
    //             }

    //             dialogModal = true;
    //         }
    //     });
    //     this.chakedData.forEach(value => {
    //         if (this.chakedData.length > 1 && value.itemStatus == "Allocated") {
    //             error: (error) => {
    //                 this.toastr.info(error.ERROR || error.responseMessage, "You can't perform this operation on Allocated item!");
    //             }

    //             dialogModal = true;
    //         }
    //     });
    //     if (!dialogModal) {
    //         this.chakedData.forEach(element => {
    //             this.changeTypeFormArray.push(this.ChangeTypeFormGroup(element.id));
    //         });
    //         this.fileName = [];
    //         this.files = [];
    //         this.updateItemFileData = [];
    //         for (let i = 0; i < this.changeTypeFormArray.controls.length; i++) {
    //             this.changeTypeFormArray.controls[i].get("otherreason").disable();
    //             this.fileName.push("");
    //             this.files.push("");
    //             this.updateItemFileData.push("");
    //         }
    //         console.log("this.changeTypeFormArray", this.changeTypeFormArray.controls);
    //         this.addChangeType = true;
    //         this.ChangeTypeModal = true;

    //     }
    // }

    changeNewInventoryType() {
        this.changeTypeSubmitted = true;
        this.changeTypeFormGroup.markAllAsTouched(); this.changeTypeFormArray.markAllAsTouched();

        if (this.changeTypeFormArray.valid) {
            const url = "/item/updateItemTypeByList";
            this.updateItemData = this.changeTypeFormArray.value;
            //this.updateItemFileData = this.files;
            this.updateItemData.forEach((data, index) => {
                if (this.fileName[index]) {
                    data.filename = this.fileName[index];
                    this.updateItemFileData.splice(index, 1, this.files[index]);
                } else {
                    data.filename = "";
                    this.updateItemFileData.splice(index, 1, null);
                }
                delete data.file;
            });
            const formData = new FormData();
            if (this.updateItemFileData.length > 0) {
                var fileArray = this.updateItemFileData.file;
                this.updateItemFileData.forEach(file => {
                    formData.append("file", file);
                });
            }

            formData.append("entityDTOs", JSON.stringify(this.updateItemData));
            this.inwardService.postMethod(url, formData).subscribe(
                (response: any) => {
                    if (response.responseCode == 200) {
                        this.closeChangeTypeModal();
                        this.searchInventory("");
                        this.dialogRef.close();
                        this.toastr.success("Successfully", 'Success!');


                    } else {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');

                    }
                },
                (error: any) => {
                    this.toastr.info(error.ERROR || error.responseMessage, 'Info!');



                }
            );
        }
    }

    getSelRemark(event, index) {
        if (event.value == "Other") {
            this.changeTypeFormArray.controls[index].get("otherreason").enable();
        } else {
            this.changeTypeFormArray.controls[index].get("otherreason").disable();
        }
    }

    onFileChange(event, index) {
        if (event.target.files.length > 0) {
            const fileName = event.target.files[0].name;
            this.fileName.splice(index, 1, fileName);
            const file = event.target.files[0];
            this.files.splice(index, 1, file);
            const data = [];
            data.push(file);
        }
    }

    closeChangeTypeModal() {
        this.changeTypeFormArray.controls = [];
        this.changeTypeSubmitted = false;
        this.ChangeTypeModal = false;
        this.dialogRef.close();
    }

    downloadInvoice(itemId, conditionId, fileName) {

        const url = "/item/documentForItemComplain/download/" + conditionId + "/" + itemId;
        this.inwardService.downloadInvoice(url).subscribe(
            (response: any) => {
                var fileType = "";
                var file = new Blob([response]);
                var fileURL = URL.createObjectURL(file);
                FileSaver.saveAs(file, fileName);
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    InventoryDetails(InventoryData) {
        this.inventoryDetailData = InventoryData;
        this.itemId = InventoryData.id;
        // const url = "/getAllInventorySpecByItemId?itemId=" + InventoryData.id;
        this.inwardService.getByItemId(InventoryData.id).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.specDetailsShow = true;
                    this.inventorySpecificationDetails = response.dataList;

                    this.inventorySpecificationDetails.map(item => {
                        if (item.isMultiValueParam) {
                            item.multiValue = item.paramMultiValues.map(value => ({
                                label: value,
                                value: value,
                            }));
                        }
                        return item;
                    });

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
        this.inventoryDetailModal = true;
        this.inventoryDetailsDialogRef = this.dialog.open(this.inventoryDetailDialogTemplate, {
            width: '75%',
            disableClose: false,
            panelClass: 'custom-dialog-container',
        });
    }

    closeInventoryDetailModal() {
        this.inventoryDetailModal = false;
        this.specDetailsShow = false;
        this.inventoryDetailsDialogRef.close();
    }
    openItemOwnershipStatus() {
        let dialogModal = false;



        if (this.chakedData.length === 0) {
            this.toastr.info("Please select at least one inventory!", "Info");
            dialogModal = true;
        }


        this.chakedData.forEach((value) => {
            if (this.chakedData.length === 1 && value.itemStatus === "Allocated") {
                this.toastr.info("You can't perform this operation on an Allocated item!", "Info");
                dialogModal = true;
            }
        });


        this.chakedData.forEach((value) => {
            if (this.chakedData.length > 1 && value.itemStatus === "Allocated") {
                this.toastr.info("You can't perform this operation on Allocated items!", "Info");
                dialogModal = true;
            }
        });


        if (!dialogModal) {
            this.changeOwnershipStatusFormArray.clear();
            this.chakedData.forEach((element) => {
                this.changeOwnershipStatusFormArray.push(
                    this.changeOwnershipStatusFormGroupFun(element.id)
                );
            });


            this.dialogRef = this.dialog.open(this.changeOwnershipStatusDialogTemplate, {
                width: "1000px",
                disableClose: true,
            });


            this.dialogRef.afterClosed().subscribe(() => {
                this.searchInventory("");
            });
        }
    }


    // openItemOwnershipStatus() {
    //     var dialogModal = false;
    //     console.log("this.ceckdata", this.chakedData);
    //     if (this.chakedData.length == 0) {
    //         error: (error) => {
    //             this.toastr.info(`${error.responseMessage}`, "Please Select Atleast One Inventory!");
    //         }


    //         dialogModal = true;
    //     }
    //     this.chakedData.forEach(value => {
    //         if (this.chakedData.length == 1 && value.itemStatus == "Allocated") {
    //             error: (error) => {
    //                 this.toastr.info(`${error.responseMessage}`, "You can't perform this operation on Allocated item!");
    //             }

    //             dialogModal = true;
    //         }
    //     });
    //     this.chakedData.forEach(value => {
    //         if (this.chakedData.length > 1 && value.itemStatus == "Allocated") {
    //             error: (error) => {
    //                 this.toastr.info(`${error.responseMessage}`, "You can't perform this operation on Allocated item!");
    //             }
    //             dialogModal = true;
    //         }
    //     });
    //     if (!dialogModal) {
    //         this.chakedData.forEach(element => {
    //             this.changeOwnershipStatusFormArray.push(
    //                 this.changeOwnershipStatusFormGroupFun(element.id)
    //             );
    //         });
    //         this.itemOwnershipStatusModal = true;
    //     }
    // }

    changeOwnershipStatus() {
        this.changeOwnershipStatusSubmitted = true;
        this.changeOwnershipStatusFormArray.markAllAsTouched();
        if (this.changeOwnershipStatusFormArray.valid) {
            const url = "/item/updateItemOwnerShipStatusByList";
            this.updateItemOwnershipChangeData = this.changeOwnershipStatusFormArray.value;
            this.inwardService.postMethod(url, this.updateItemOwnershipChangeData).subscribe(
                (response: any) => {
                    if (response.responseCode == 200) {
                        this.closeOwnershipStatusChangeTypeModal();
                        this.searchInventory("");
                        this.dialogRef.close();
                        this.toastr.success("Successfully", 'Success!');

                    } else {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');

                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    closeOwnershipStatusChangeTypeModal() {
        this.changeOwnershipStatusFormArray.controls = [];
        this.changeOwnershipStatusSubmitted = false;
        this.itemOwnershipStatusModal = false;
        this.dialogRef.close();
    }
    openInventoryStatusModal() {
        let dialogModal = false;


        if (this.chakedData.length === 0) {
            this.toastr.info('Please select at least one inventory!', 'Info');
            dialogModal = true;
        }


        this.chakedData.forEach((value) => {
            if (this.chakedData.length === 1 && value.itemStatus === "Allocated") {
                this.toastr.info("You can't perform this operation on an Allocated item!", 'Info');
                dialogModal = true;
            }
        });


        this.chakedData.forEach((value) => {
            if (this.chakedData.length > 1 && value.itemStatus === "Allocated") {
                this.toastr.info("You can't perform this operation on Allocated items!", 'Info');
                dialogModal = true;
            }
        });


        if (!dialogModal) {
            this.changeInventoryStatusFormArray.clear(); // reset
            this.chakedData.forEach((element) => {
                this.changeInventoryStatusFormArray.push(
                    this.changeInventoryStatusFormGroupFun(element.id)
                );
            });


            this.dialogRef = this.dialog.open(this.changeItemStatusDialogTemplate, {
                width: '1000px',
                disableClose: true,
            });


            this.dialogRef.afterClosed().subscribe(() => {
                this.searchInventory("");
            });
        }
    }

    // openInventoryStatusModal() {
    //     var dialogModal = false;
    //     console.log("this.ceckdata", this.chakedData);
    //     if (this.chakedData.length == 0) {
    //         error: (error) => {
    //             this.toastr.info(error.ERROR || error.responseMessage, 'Please Select Atleast One Inventory!');
    //         }


    //         dialogModal = true;
    //     }
    //     this.chakedData.forEach(value => {
    //         if (this.chakedData.length == 1 && value.itemStatus == "Allocated") {
    //             error: (error) => {
    //                 this.toastr.info(error.ERROR || error.responseMessage, "You can't perform this operation on Allocated item!");
    //             }

    //             dialogModal = true;
    //         }
    //     });
    //     this.chakedData.forEach(value => {
    //         if (this.chakedData.length > 1 && value.itemStatus == "Allocated") {

    //             error: (error) => {
    //                 this.toastr.info(error.ERROR || error.responseMessage, "You can't perform this operation on Allocated item!");
    //             }

    //             dialogModal = true;
    //         }
    //     });
    //     if (!dialogModal) {
    //         this.chakedData.forEach(element => {
    //             this.changeInventoryStatusFormArray.push(
    //                 this.changeInventoryStatusFormGroupFun(element.id)
    //             );
    //         });
    //         this.itemStatusModal = true;
    //     }
    // }

    closeInventoryStatusChangeTypeModal() {
        this.changeInventoryStatusFormArray.controls = [];
        this.changeInventoryStatusSubmitted = false;
        this.itemStatusModal = false;
        this.dialogRef.close();
    }

    filetrItemStatusConditions(status: string, chakedDataObj: any) {
        if (status == "Maintenance" && chakedDataObj?.itemStatus !== "Maintenance") {
            return true;
        } else if (status == "Maintenance") {
            return false;
        }

        if (status == "Defective" && chakedDataObj?.itemStatus != "Maintenance") {
            return true;
        } else if (status == "Defective") {
            return false;
        }

        if (status == "UnAllocated" && chakedDataObj?.itemStatus == "Maintenance") {
            return true;
        } else if (status == "UnAllocated") {
            return false;
        }
    }

    changeItemStatus() {
        this.changeInventoryStatusSubmitted = true;
        this.changeInventoryStatusFormArray.markAllAsTouched();
        if (this.changeInventoryStatusFormArray.valid) {
            const url = "/item/updateItemStatusByList";
            this.updateItemInventoryChangeData = this.changeInventoryStatusFormArray.value;
            this.inwardService.postMethod(url, this.updateItemInventoryChangeData).subscribe(
                (response: any) => {
                    if (response.responseCode == 200) {
                        this.closeInventoryStatusChangeTypeModal();
                        this.searchInventory("");
                        this.dialogRef.close();
                        this.toastr.success("Successfully", 'Success!');


                    } else {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');

                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    openReturnItemModal() {
        if (this.chakedData.length > 0) {
            this.chakedData.forEach(element => {
                this.returnItemFormArray.push(this.returnItemFormGroupFun(element.id));
            });
            this.returnItemModal = true;
        } else {
            (error: any) => {
                this.toastr.info(`${error.error.ERROR}`, 'Please select atleast one inventory!');
            }
        }
    }

    closeReturnItemModal() {
        this.returnItemFormArray.controls = [];
        this.returnItemSubmitted = false;
        this.returnItemModal = false;
    }

    returnItemStatus() {
        this.returnItemSubmitted = true;
        if (this.returnItemFormArray.valid) {
            const url = "/item/return";
            this.returnItemData = this.returnItemFormArray.value;
            this.inwardService.postMethod(url, this.returnItemData).subscribe(
                (response: any) => {
                    if (response.responseCode == 200) {
                        this.closeReturnItemModal();
                        this.searchInventory("");
                        this.toastr.success("Successfully", 'Success!');

                    } else {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');

                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }
    openWarrantyStatusChangeModal() {
        let dialogModal = false;


        if (this.chakedData.length === 0) {
            this.toastr.info("Please select at least one inventory!", "Info");
            dialogModal = true;
        }


        this.chakedData.forEach((value) => {
            if (this.chakedData.length === 1 && value.itemStatus === "Allocated") {
                this.toastr.info("You can't perform this operation on an Allocated item!", "Info");
                dialogModal = true;
            }
        });


        this.chakedData.forEach((value) => {
            if (this.chakedData.length > 1 && value.itemStatus === "Allocated") {
                this.toastr.info("You can't perform this operation on Allocated items!", "Info");
                dialogModal = true;
            }
        });


        if (!dialogModal) {

            this.warrantyStatusChangeFormArray.clear();
            this.chakedData.forEach((element) => {
                this.warrantyStatusChangeFormArray.push(
                    this.warrantyChangeStatusFormGroupFun(element.id)
                );
            });


            this.dialogRef = this.dialog.open(this.changeWarrantyDialogTemplate, {

                width: '1000px',
                disableClose: true,
            });
        }
    }


    // openWarrantyStatusChangeModal() {
    //     var dialogModal = false;
    //     console.log("this.chakedData", this.chakedData);
    //     if (this.chakedData.length == 0) {
    //         (error: any) => {
    //             this.toastr.info(`${error.error.ERROR}`, 'Please select atleast one inventory!');
    //         }

    //         dialogModal = true;
    //     }
    //     this.chakedData.forEach(value => {
    //         if (this.chakedData.length == 1 && value.itemStatus == "Allocated") {
    //             (error: any) => {
    //                 this.toastr.info(`${error.error.ERROR}`, "You can't perform this operation on Allocated item!");
    //             }

    //             dialogModal = true;
    //         }
    //     });
    //     this.chakedData.forEach(value => {
    //         if (this.chakedData.length > 1 && value.itemStatus == "Allocated") {
    //             (error: any) => {
    //                 this.toastr.info(`${error.error.ERROR}`, "You can't perform this operation on Allocated item!");
    //             }


    //             dialogModal = true;
    //         }
    //     });
    //     if (!dialogModal) {
    //         this.chakedData.forEach(element => {
    //             this.warrantyStatusChangeFormArray.push(this.warrantyChangeStatusFormGroupFun(element.id));
    //         });
    //         this.warrantyTypeModal = true;
    //     }
    // }
    closeWarrantyChangeModal() {
        this.warrantyStatusChangeFormArray.controls = [];
        this.warrantyStatusChangeSubmitted = false;
        this.warrantyTypeModal = false;
        this.dialogRef.close();
    }

    warrantyChangeStatus() {
        this.warrantyStatusChangeSubmitted = true;
        this.warrantyStatusChangeFormArray.markAllAsTouched();
        if (this.warrantyStatusChangeFormArray.valid) {
            const url = "/item/updateItemWarrantyByList";
            this.warrantyChangeData = this.warrantyStatusChangeFormArray.value;
            this.inwardService.postMethod(url, this.warrantyChangeData).subscribe(
                (response: any) => {
                    if (response.responseCode == 200) {
                        this.closeWarrantyChangeModal();
                        this.searchInventory("");
                        this.dialogRef.close();

                        this.toastr.success("Successfully", 'Success!');

                    } else {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');

                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }
    // Search Customer Name and Product Name for Serialized Item
    searchSerializedCustomer() {
        const data = {
            filters: [
                {
                    filterValue: "",
                    filterColumn: "",
                },
            ],
            page: 1,
            pageSize: 5,
        };
        data.filters[0].filterColumn = this.searchByFilterColumn;
        this.searchkey = this.searchName;
        if (this.searchkey == "") {
            this.showInventoryAssignedToCustomer();
        } else {
            data.filters[0].filterValue = this.searchName;
            data.page = this.currentPageAssigntListdata;
            data.pageSize = this.showAllCustInventoryItemPerPage;
            if (this.showAllCustInventoryItemPerPage) {
                this.assignCustomerListdataitemsPerPage = this.showAllCustInventoryItemPerPage;
            }
            const staffId = localStorage.getItem("userId");
            const url =
                "/inwards/searchByCustomerAndPopAndServiceAreaName?filtername=Customer&isSerelized=true&staffId=" +
                staffId;
            this.inwardService.postMethod(url, data).subscribe(
                (response: any) => {
                    if (response.responseCode == 404) {
                        this.toastr.info(response.responseMessage, 'Info!');

                        this.assignedInventoryList = [];
                        this.assignCustomertListdatatotalRecords = 0;
                    } else {
                        this.assignedInventoryList = response.dataList;
                        this.assignCustomertListdatatotalRecords = response.totalRecords;
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    clearSearchSerializedCustomer() {
        this.searchByFilterColumn = "";
        this.searchName = "";
        this.showInventoryAssignedToCustomer();
    }

    // Search Customer Name and Product Name for Non Serialized Item
    searchNonSerializedCustomer() {
        const data = {
            filters: [
                {
                    filterValue: "",
                    filterColumn: "",
                },
            ],
            page: 1,
            pageSize: 5,
        };
        data.filters[0].filterColumn = this.searchByFilterColumn;
        this.searchkey = this.searchNameNonSeriCust;
        if (this.searchkey == "") {
            this.getNonSerializedItemCustomerInventoryMappingByStaffId("");
        } else {
            data.filters[0].filterValue = this.searchNameNonSeriCust;
            data.page = this.currentCustomerNonSerializedPageAssigntListdata;
            data.pageSize = this.showAllNonSerializedCustInventoryItemPerPage;
            if (this.showAllNonSerializedCustInventoryItemPerPage) {
                this.assignNonSerializedCustomerListdataitemsPerPage =
                    this.showAllNonSerializedCustInventoryItemPerPage;
            }
            const staffId = localStorage.getItem("userId");
            const url =
                "/inwards/searchByCustomerAndPopAndServiceAreaName?filtername=Customer&isSerelized=false&staffId=" +
                staffId;
            this.inwardService.postMethod(url, data).subscribe(
                (response: any) => {
                    if (response.responseCode == 404) {
                        this.toastr.info(response.responseMessage, 'Info!');

                        this.assignedNonSerializedCustInventoryList = [];
                        this.assignNonSerializedCustomertListdatatotalRecords = 0;
                    } else {
                        this.assignedNonSerializedCustInventoryList = response.dataList;
                        this.assignNonSerializedCustomertListdatatotalRecords = response.totalRecords;
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    clearSearchNonSerializedCustomer() {
        this.searchByFilterColumn = "";
        this.searchNameNonSeriCust = "";
        this.getNonSerializedItemCustomerInventoryMappingByStaffId("");
    }

    // Search Pop Name and Product Name for Serialized Item
    searchSerializedPop() {
        const data = {
            filters: [
                {
                    filterValue: "",
                    filterColumn: "",
                },
            ],
            page: 1,
            pageSize: 5,
        };
        data.filters[0].filterColumn = this.searchByFilterColumn;
        this.searchkey = this.searchNameSeriPop;
        if (this.searchkey == "") {
            this.showInventoryAssignedToPOP();
        } else {
            data.filters[0].filterValue = this.searchNameSeriPop;
            data.page = this.currentPopSerializedPageAssigntListdata;
            data.pageSize = this.showAllSerializedPOPInventoryItemPerPage;
            if (this.showAllSerializedPOPInventoryItemPerPage) {
                this.assignSerializedPopListdataitemsPerPage =
                    this.showAllSerializedPOPInventoryItemPerPage;
            }
            const staffId = localStorage.getItem("userId");
            const url =
                "/inwards/searchByCustomerAndPopAndServiceAreaName?filtername=Pop&isSerelized=true&staffId=" +
                staffId;
            this.inwardService.postMethod(url, data).subscribe(
                (response: any) => {
                    if (response.responseCode == 404) {
                        this.toastr.info(response.responseMessage, 'Info!');

                        this.assignedSerializedPOPInventoryList = [];
                        this.assignSerializedPopListdatatotalRecords = 0;
                    } else {
                        this.assignedSerializedPOPInventoryList = response.dataList;
                        this.assignSerializedPopListdatatotalRecords = response.totalRecords;
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    clearSearchSerializedPop() {
        this.searchByFilterColumn = "";
        this.searchNameSeriPop = "";
        this.isPop = false;
        this.isProduct = false;
        this.showInventoryAssignedToPOP();
    }

    // Search Pop Name and Product Name for Non Serialized Item
    searchNonSerializedPop() {
        const data = {
            filters: [
                {
                    filterValue: "",
                    filterColumn: "",
                },
            ],
            page: 1,
            pageSize: 5,
        };
        data.filters[0].filterColumn = this.searchByFilterColumn;
        this.searchkey = this.searchNameNonSeriPop;
        if (this.searchkey == "") {
            this.getNonSerializedItemPopByInventoryMappingByStaffId("");
        } else {
            data.filters[0].filterValue = this.searchNameNonSeriPop;
            data.page = this.currentPopNonSerializedPageAssigntListdata;
            data.pageSize = this.showAllNonSerializedPOPInventoryItemPerPage;
            if (this.showAllNonSerializedPOPInventoryItemPerPage) {
                this.assignNonSerializedPopListdataitemsPerPage =
                    this.showAllNonSerializedPOPInventoryItemPerPage;
            }
            const staffId = localStorage.getItem("userId");
            const url =
                "/inwards/searchByCustomerAndPopAndServiceAreaName?filtername=Pop&isSerelized=false&staffId=" +
                staffId;
            this.inwardService.postMethod(url, data).subscribe(
                (response: any) => {
                    if (response.responseCode == 404) {
                        this.toastr.info(response.responseMessage, 'Info!');

                        this.assignedNonSerializePOPInventoryList = [];
                        this.assignNonSerializedPopListdatatotalRecords = 0;
                    } else {
                        this.assignedNonSerializePOPInventoryList = response.dataList;
                        this.assignNonSerializedPopListdatatotalRecords = response.totalRecords;
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
        }
    }

    clearSearchNonSerializedPop() {
        this.searchByFilterColumn = "";
        this.searchNameNonSeriPop = "";
        this.getNonSerializedItemPopByInventoryMappingByStaffId("");
    }

    // Search Service Area Name and Product Name for Serialized Item
    searchSerializedServiceArea() {
        const data = {
            filters: [
                {
                    filterValue: "",
                    filterColumn: "",
                },
            ],
            page: 1,
            pageSize: 5,
        };
        data.filters[0].filterColumn = this.searchByFilterColumn;
        this.searchkey = this.searchNameSeriServiceArea;
        if (this.searchkey == "") {
            this.showInventoryAssignedToServiceArea();
        } else {
            data.filters[0].filterValue = this.searchNameSeriServiceArea;
            data.page = this.currentServiceAreaSerializedPageAssigntListdata;
            data.pageSize = this.showAllSerializedServiceAreaInventoryItemPerPage;
            if (this.showAllSerializedServiceAreaInventoryItemPerPage) {
                this.assignSerializedServiceAreaListdataitemsPerPage =
                    this.showAllSerializedServiceAreaInventoryItemPerPage;
            }
            const staffId = localStorage.getItem("userId");
            const url =
                "/inwards/searchByCustomerAndPopAndServiceAreaName?filtername=Service Area&isSerelized=true&staffId=" +
                staffId;
            this.inwardService.postMethod(url, data).subscribe(
                (response: any) => {
                    if (response.responseCode == 404) {
                        this.toastr.info(`${response.responseMessage}`, 'Info!');

                        this.assignedSerializedServiceAreaInventoryList = [];
                        this.assignSerializedServiceAreaListdatatotalRecords = 0;
                    } else {
                        this.assignedSerializedServiceAreaInventoryList = response.dataList;
                        this.assignSerializedServiceAreaListdatatotalRecords = response.totalRecords;
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    clearSearchSerializedServiceArea() {
        this.searchByFilterColumn = "";
        this.searchNameSeriServiceArea = "";
        this.isServcieArea = false;
        this.isServiceAreaProduct = false;
        this.showInventoryAssignedToServiceArea();
    }

    // Search Pop Name and Product Name for Non Serialized Item
    searchNonSerializedServiceArea() {
        const data = {
            filters: [
                {
                    filterValue: "",
                    filterColumn: "",
                },
            ],
            page: 1,
            pageSize: 5,
        };
        data.filters[0].filterColumn = this.searchByFilterColumn;
        this.searchkey = this.searchNameNonSeriServiceArea;
        if (this.searchkey == "") {
            this.getNonSerializedItemServiceAreaByInventoryMappingByStaffId("");
        } else {
            data.filters[0].filterValue = this.searchNameNonSeriServiceArea;
            data.page = this.currentServiceAreaNonSerializedPageAssigntListdata;
            data.pageSize = this.showAllNonSerializedServiceAreaInventoryItemPerPage;
            if (this.showAllNonSerializedServiceAreaInventoryItemPerPage) {
                this.assignNonSerializedServiceAreaListdataitemsPerPage =
                    this.showAllNonSerializedServiceAreaInventoryItemPerPage;
            }
            const staffId = localStorage.getItem("userId");
            const url =
                "/inwards/searchByCustomerAndPopAndServiceAreaName?filtername=Service Area&isSerelized=false&staffId=" +
                staffId;
            this.inwardService.postMethod(url, data).subscribe(
                (response: any) => {
                    if (response.responseCode == 404) {
                        this.toastr.info(`${response.responseMessage}`, 'Info!');

                        this.assignedNonSerializedServiceAreaInventoryList = [];
                        this.assignNonSerializedServiceAreaListdatatotalRecords = 0;
                    } else {
                        this.assignedNonSerializedServiceAreaInventoryList = response.dataList;
                        this.assignNonSerializedServiceAreaListdatatotalRecords = response.totalRecords;
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    clearSearchNonSerializedServiceArea() {
        this.searchByFilterColumn = "";
        this.searchNameNonSeriServiceArea = "";
        this.getNonSerializedItemServiceAreaByInventoryMappingByStaffId("");
    }

    viewInventory(inventory: any) {
        this.selectedInventory = inventory;

        this.dialogRef = this.dialog.open(this.viewPortDetailsDialogTemplate, {
            width: '900px',
            disableClose: true,
            panelClass: 'custom-dialog-container'
        });
    }


    getAllPopName() {
        const url = "/popmanagement/all";
        this.isDestAStaffOrCustomer = false;
        this.popService.getMethodWithCache(url).subscribe(
            (res: any) => {
                this.popListData = res.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    getAllServcieArea() {
        const url = "/serviceArea/getAllServiceAreaByStaff";
        this.isDestAStaffOrCustomer = false;
        this.serviceAreaService.getMethod(url).subscribe(
            (res: any) => {
                this.servcieAreaListData = res.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    popSearchaByChange(event: any, dd: any) {
        const selectedValue = event.value;

        if (selectedValue === "name") {
            this.isPop = true;
            this.isProduct = false;
        } else {
            this.isPop = false;
            this.isProduct = true;
        }
    }


    serviceAreaSearchaByChange(selectedValue: any) {

        if (selectedValue.value === 'serviceArea') {
            this.isServcieArea = true;
            this.isServiceAreaProduct = false;
        } else if (selectedValue.value === 'product') {
            this.isServcieArea = false;
            this.isServiceAreaProduct = true;
        } else {
            this.isServcieArea = false;
            this.isServiceAreaProduct = false;
        }
    }

}
