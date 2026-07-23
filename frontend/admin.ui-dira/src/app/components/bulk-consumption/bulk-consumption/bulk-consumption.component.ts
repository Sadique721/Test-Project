import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { BehaviorSubject, Observable, Observer } from "rxjs";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import { InwardService } from "src/app/service/inward.service";
import { ProuctManagementService } from "src/app/service/prouct-management.service";
import { CustomerInventoryManagementService } from "src/app/service/customer-inventory-management.service";
import { PartnerService } from "src/app/service/partner.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { RadiusUtility } from "src/app/RadiusUtils/RadiusUtility";

import { ITEMS_PER_PAGE } from "src/app/RadiusUtils/RadiusConstants";
import { Regex } from "src/app/constants/regex";
import { OutwardService } from "src/app/service/outward.service";
import { CustomerInventoryMappingService } from "src/app/service/customer-inventory-mapping.service";
import { BulkConsumptionService } from "src/app/service/bulk-consumption.service";
import { LoginService } from "src/app/service/login.service";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { Table } from "primeng/table";
import { serialize } from "v8";
import { type } from "os";
import { INVENTORYS } from "src/app/constants/aclConstants";
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { AfterViewInit } from '@angular/core';
import { forkJoin } from 'rxjs';
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { ToastrService } from "ngx-toastr";
declare var $: any;
@Component({
    selector: "app-bulk-consumption",
    templateUrl: "./bulk-consumption.component.html",
    styleUrls: ["./bulk-consumption.component.css"],
    standalone: false
})
export class BulkConsumptionComponent implements OnInit, AfterViewInit {

    dataSource: MatTableDataSource<any> = new MatTableDataSource<any>();
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild('detailDialog') detailDialog: TemplateRef<any>;
    @ViewChild('createEditDialog') createEditDialog: TemplateRef<any>;
    @ViewChild('approveChangeStatusDialog') approveChangeStatusDialog: TemplateRef<any>;
    @ViewChild('rejectChangeStatusDialog') rejectChangeStatusDialog: TemplateRef<any>;
    @ViewChild('macMappingDialog') macMappingDialog: TemplateRef<any>;

    public loginService: LoginService;
    AclClassConstants;
    AclConstants;
    bulkConsumptionFormGroup: UntypedFormGroup;
    inventoryAssignSumitted: boolean = false;
    loggedInStaffId = localStorage.getItem("userId");
    ItemSelectionType = [
        { label: "Serialized Item", value: "Serialized Item" },
        { label: "Non Serialized Item", value: "Non Serialized Item" }
    ];
    productSelectionType = [
        { label: "Single Item", value: false },
        { label: "Pair Item", value: true }

    ];
    itemConditionData = [
        { label: "New", value: "New" },
        { label: "Refurbished", value: "Refurbished" }
    ];
    @ViewChild("dt") table: Table;
    sourceType = [{ label: "Warehouse" }, { label: "Staff" }, { label: "Partner" }];
    macAddressList: any = [];
    products = [];
    displayedColumns1: string[] = ['select', 'itemId', 'condition', 'macAddress', 'serialNumber'];
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    editMode: boolean = false;
    hideSearchBar: boolean = true;
    productHasMac: boolean;
    productHasSerial: boolean;
    macList: any = [];
    searchkey: string;
    searchData: any;
    searchProductCatName: any = "";
    productListData: any[] = [];
    productListdataitemsPerPage = RadiusConstants.PER_PAGE_ITEMS;
    currentPageProductListdata = 1;
    showItemPerPage: any = 5;
    productListdatatotalRecords: number = 0;
    inwardMacdataitemsPerPage = ITEMS_PER_PAGE;
    currentPageinwardMacdata = 1;
    inwardMacdatatotalRecords: any;
    selItemCondition: any = "";
    warehouses: any[] = [];
    staffList = [];
    allActiveProducts: any = [];
    hasMac: boolean;
    hasSerial: boolean;
    enterMacSerial: any = "";
    sources = [];
    sourceTypeAsStaffFlag: boolean = false;
    availableQty: any;
    inwardList = [];
    selectedMACAddress: any[] = [];
    customerId: any;
    submitted: boolean = false;
    approved = false;
    reject = false;
    assignInwardID: any;
    rejectInventoryData = [];
    fullInwardMacList: any[] = [];
    approveInventoryData = [];
    selectStaff: any;
    assignInwardForm: UntypedFormGroup;
    rejectInwardForm: UntypedFormGroup;
    assignInwardSubmitted: boolean = false;
    rejectInwardSubmitted: boolean = false;
    approveChangeStatusModal: boolean = false;
    rejectChangeStatusModal: boolean = false;
    MACShowModal: boolean = false;
    selectStaffReject: any;
    inwardMacList: any[] = [];
    inwardIdForMac: number;
    unit: any;
    userId: number = +localStorage.getItem("userId");
    parentItemList: any = [];
    dialogRef: MatDialogRef<any>;
    createBulkData: any;
    mapping: any;
    chekedData: any;
    allActiveNonTrackableProducts: any = [];
    getNonTrackableProductQtyList: any = [];
    getSerializedProductFlag: boolean = false;
    getAllAssemblyNameFlag: boolean = false;
    itemConditionSingleFlag: boolean = false;
    getAllNonSerializedProductFlag: boolean = false;
    sourceTypeFlag: boolean = false;
    sourceFlag: boolean = false;
    availableQtyFlag: boolean = false;
    getAllSingleItemMacFlag: boolean = false;
    getAllNonSerializedItemFlag: boolean = false;
    viewBulkConsumptionDetails: any;
    UOM: any = "";
    showQtyError: boolean;
    negativeAssignQtyError: boolean;
    getInwardFlag: boolean = false;
    getItemListFlag: boolean = false;
    createView = false;
    listView = true;
    detailView: boolean = false;
    inOutWardMACMappings: any;
    itemId: any;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    createAccess: boolean = false;
    approveAccess: boolean = false;
    rejectAccess: boolean = false;
    showMacAddressAccess: boolean = false;
    isSelected(product: any): boolean {
        return this.selectedMACAddress.some(selected => selected.id === product.id);
    }
    disabledProductIds = new Set<number>();
    constructor(
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private inwardService: InwardService,
        private outwardService: OutwardService,
        private bulkConsumptionService: BulkConsumptionService,
        private productService: ProuctManagementService,
        private customerInventoryManagementService: CustomerInventoryManagementService,
        private partnerService: PartnerService,
        public dialog: MatDialog, private toastr: ToastrService,
        public customerInventoryMappingService: CustomerInventoryMappingService,
        loginService: LoginService
    ) {
        this.loginService = loginService;
        this.createAccess = loginService.hasPermission(INVENTORYS.CREATE_BULK_CONSUMPTION);
        this.deleteAccess = loginService.hasPermission(INVENTORYS.DELETE_BULK_CONSUMPTION);
        this.editAccess = loginService.hasPermission(INVENTORYS.EDIT_BULK_CONSUMPTION);
        this.approveAccess = loginService.hasPermission(INVENTORYS.BULK_CONSUMPTION_APPROVE);
        this.rejectAccess = loginService.hasPermission(INVENTORYS.BULK_CONSUMPTION_REJECT);
        this.showMacAddressAccess = loginService.hasPermission(INVENTORYS.VIEW_INWARD_MAC_MAPPING);
        this.editMode = !this.createAccess && this.editAccess ? true : false;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        this.displayedColumns = ['id',
            'bulkConsumptionName',
            'productName',
            'qty',
            'itemType',
            'approvalStatus',
        ];
        if (this.deleteAccess || this.createAccess || this.showMacAddressAccess) {
            this.displayedColumns.push('action');
        }
    }

    displayedColumns: string[] = [];


    ngOnInit(): void {
        this.bulkConsumptionFormGroup = this.fb.group({
            id: [""],
            bulkConsumptionName: ["", Validators.required],
            ownerType: ["", Validators.required],
            ownerId: ["", Validators.required],
            productId: ["", Validators.required],
            itemType: ["", Validators.required],
            itemListLongId: [""],
            qty: [""],
            nonSerializedQty: [""],
            isDeleted: [""],
            mvnoId: [""],
            inOutWardMACMappings: [""]
        });
        if (this.hasMac) {
            this.displayedColumns.push('macAddress');
        }
        if (this.hasSerial) {
            this.displayedColumns.push('serialNumber');
        }
        this.assignInwardForm = this.fb.group({
            remark: ["", Validators.required]
        });
        this.rejectInwardForm = this.fb.group({
            remark: ["", Validators.required]
        });
        this.searchData = {
            filters: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "any",
                    filterOperator: "equalto",
                    filterCondition: "and"
                }
            ]
        };

        this.getProductList("");
        this.bulkConsumptionFormGroup.get("nonSerializedQty").valueChanges.subscribe(val => {
            const total = val;
            if (total > this.availableQty) {
                this.showQtyError = true;
            } else {
                this.showQtyError = false;
            }
            if (total < 0 || total == 0) {
                this.negativeAssignQtyError = true;
            } else {
                this.negativeAssignQtyError = false;
            }
        });
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    onCancel(): void {
        this.bulkConsumptionFormGroup.reset();
        this.macAddressList = [];
        this.fullInwardMacList = [];
        this.getAllSingleItemMacFlag = false;
        this.getSerializedProductFlag = false;
        this.getAllNonSerializedProductFlag = false;
        if (this.dialogRef) {
            this.dialogRef.close();
        }
    }

    deleteConfirmProductDialog(product: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete Bulk Consumption',
                description: `Are you sure you want to delete "${product.bulkConsumptionName}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteProduct(product.id);
            }
        });
    }

    openCreateEditDialog(editMode = false) {
        this.editMode = editMode;
        this.dialogRef = this.dialog.open(this.createEditDialog, {
            width: '800px',
            data: { editMode }
        });

        this.dialogRef.afterClosed().subscribe((result) => {
            this.submitted = false;
            this.bulkConsumptionFormGroup.reset();
            if (result) {
                this.clearSearchProduct();
                this.listView = true;
                this.createView = false;
                this.detailView = false;
            }
        });
    }

    onApproveClick(id: number) {
        this.assignInwardID = id;
        this.dialogRef = this.dialog.open(this.approveChangeStatusDialog, {
            width: '600px'
        });

        this.dialogRef.afterClosed().subscribe(() => {
            this.closeApproveInventoryModal();
            this.getProductList('');
        });
    }

    onRejectClick(id: number) {
        this.assignInwardID = id;
        this.dialogRef = this.dialog.open(this.rejectChangeStatusDialog, {
            width: '600px'
        });

        this.dialogRef.afterClosed().subscribe(() => {
            this.closeRejectInventoryModal();
            this.getProductList('');
        });
    }

    openMacMappingDialog(inwardId: number) {
        this.inwardIdForMac = inwardId;
        this.getInwardMACMapping(inwardId);
        // this.dialogRef = this.dialog.open(this.macMappingDialog, {
        //     width: '600px'
        // });

        this.dialogRef.afterClosed().subscribe(() => {
            this.closeMACandSerialModal();
        });
    }

    createBulkConsumption() {
        this.editMode = false;
        this.submitted = false;
        this.bulkConsumptionFormGroup.reset();
        this.hideSearchBar = false;

        forkJoin({
            productsRes: this.productService.getAllActiveProduct() as Observable<any>,
            warehousesRes: this.inwardService.getAllWareHouse() as Observable<any>,
            staffRes: this.outwardService.getAllStaff() as Observable<any>
        }).subscribe({
            next: ({ productsRes, warehousesRes, staffRes }) => {
                this.products = productsRes.dataList;
                this.warehouses = warehousesRes.dataList;
                const staffId = localStorage.getItem("userId");
                this.staffList = staffRes.dataList.filter(element => element.id == staffId);

                this.dialogRef = this.dialog.open(this.createEditDialog, {
                    width: '1100px',
                    data: { editMode: this.editMode }
                });

                this.dialogRef.afterClosed().subscribe(result => {
                    if (result) {
                        this.clearSearchProduct();
                        this.listView = true;
                        this.createView = false;
                        this.detailView = false;
                    }
                });
            },
            error: (error) => {
                this.toastr.error(`${error.error.ERROR}`, 'Unable to fetch necessary data. Please try again.');
            }
        });
    }


    mapData(type): {} {
        this.itemIds = [];
        if (type == "Serialized Item") {
            this.selectedMACAddress.forEach(e => {
                this.itemIds.push(e.id);
            });
        }
        const customerInventoryMapping = this.bulkConsumptionFormGroup.getRawValue();
        const mapping = {
            productId: 0,
            itemListLongId: this.itemIds,
            inOutWardMACMappings: [""],
            bulkConsumptionName: "",
            inwardId: 0,
            qty: 0,
            isDeleted: false,
            ownerId: "",
            ownerType: "",
            nonSerializedQty: 0,
            itemType: ""
        };
        if (type == "Non Serialized Item") {
            mapping.qty = customerInventoryMapping.nonSerializedQty;
            mapping.nonSerializedQty = customerInventoryMapping.nonSerializedQty;
            mapping.itemType = customerInventoryMapping.itemType;
            mapping.inOutWardMACMappings = [];
        }
        if (type == "Serialized Item") {
            mapping.qty = this.selectedMACAddress.length;
            mapping.inOutWardMACMappings = this.selectedMACAddress;
            mapping.itemType = customerInventoryMapping.itemType;
        }
        mapping.ownerId = customerInventoryMapping.ownerId;
        mapping.ownerType = customerInventoryMapping.ownerType;
        mapping.bulkConsumptionName = customerInventoryMapping.bulkConsumptionName;
        mapping.productId = customerInventoryMapping.productId;
        mapping.inwardId = customerInventoryMapping.inwardId;
        mapping.isDeleted = false;
        return mapping;
    }

    getAllProductbasedOnItemType(value) {
        this.macAddressList = [];

        const staffId = localStorage.getItem("userId");
        const itemType = value;
        const url = "/product/getAllProductbasedOnItemType?itemtype=" + itemType;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.allActiveProducts = response.dataList;
                this.allActiveNonTrackableProducts = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    // oneSelect() {
    //     if (this.chekedData.length == 0) {
    //         (response: any) => {
    //             this.toastr.info(response.responseMessage, "Please Select Atleast One Inventory");
    //         }
    //     }
    // }

    onSelect(product: any, event: any) {
        if (event.checked) {
            this.selectedMACAddress.push(product);
        } else {
            this.selectedMACAddress = this.selectedMACAddress.filter(
                item => item.id !== product.id
            );
        }
        if (this.selectedMACAddress.length === 0) {
            this.toastr.info("Please Select Atleast One Inventory");
        }
    }
    getProductSelection(): void {
        const url = "/product/getAllProductForNonTrackableProductCategory";
        this.customerInventoryManagementService.getMethod(url).subscribe((response: any) => {
            this.allActiveNonTrackableProducts = response.dataList;
            this.allActiveProducts = response.dataList;
        });
    }

    getAllSerializedProductItem(productId, ownerId, ownerType): void {
        this.macAddressList = [];
        const staffId = localStorage.getItem("userId");
        let product = this.allActiveProducts.find(element => element.id == productId);
        this.hasMac = product.productCategory.hasMac;
        this.hasSerial = product.productCategory.hasSerial;
        const url =
            "/product/getAllSerializedItemBaseOnProduct?productId=" +
            productId +
            "&ownerId=" +
            ownerId +
            "&ownerType=" +
            ownerType;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.macAddressList = response.dataList;

                if (this.macAddressList.length == 0 || this.macAddressList == null) {
                    this.toastr.info(response.responseMessage, "Product MAC address not available");
                } else {
                    this.getAllSingleItemMacFlag = true;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    getBulkConsumptionDetails(id: number) {
        const url = "/bulk_consumption/getById?id=" + id;
        this.bulkConsumptionService.getMethod(url).subscribe(
            (res: any) => {
                if (res && res.data) {
                    this.viewBulkConsumptionDetails = res.data;


                    this.dialogRef = this.dialog.open(this.detailDialog, {
                        width: '800px',
                        data: { details: this.viewBulkConsumptionDetails }
                    });

                    this.dialogRef.afterClosed().subscribe(() => {
                        this.WarehouseList();
                    });

                } else {
                    (response: any) => {
                        this.toastr.info(response.responseMessage, "No details found for this item.");
                    }
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, "Failed to get details.");
            }
        );
    }

    WarehouseList() {
        this.listView = true;
        this.createView = false;
        this.detailView = false;
    }

    getAvailableQtyByProductAndSource(productId, sourceId, sourceType): void {
        if (productId && sourceId) {
            this.inwardList = [];
            this.outwardService.getProductAvailableQTY(productId, sourceId, sourceType).subscribe(
                (res: any) => {
                    this.inwardList = res.dataList;
                    this.availableQtyFlag = true;
                    if (res.dataList.length == 0) {
                        this.availableQty = 0;
                    } else {
                        this.availableQty = res.dataList.find(element => element).unusedQty;
                    }
                    this.bulkConsumptionFormGroup.get("nonSerializedQty").reset();
                    this.getAllNonSerializedItemFlag = true;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        } else {
        }
    }
    getSourceType() {
        this.sourceTypeFlag = true;
        this.sourceFlag = false;
        this.getAllNonSerializedItemFlag = false;
        this.getAllSingleItemMacFlag = false;
        this.bulkConsumptionFormGroup.get("ownerType").reset();
        this.bulkConsumptionFormGroup.get("ownerId").reset();
        this.bulkConsumptionFormGroup.get("nonSerializedQty").reset();
    }
    getUnit(event) {
        this.getInwardFlag = true;
        this.bulkConsumptionFormGroup.controls["inwardId"].reset();
        this.getItemListFlag = false;
        this.selectedMACAddress = [];
        this.unit = this.products.find(element => element.id == event.value).unit;
        this.getOutWardList(event.value);
    }

    getOutWardList(productID) {
        const staffId = localStorage.getItem("userId");
        this.inwardService
            .getAllInwardByProductAndStaffforPopandSeriveareaandCustomer(productID, staffId)
            .subscribe(
                (res: any) => {
                    this.productHasMac = this.products.find(element => element.id == productID).hasMac;
                    this.productHasSerial = this.products.find(element => element.id == productID).hasSerial;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
    }

    getMacMappingsByInwardId(id): void {
        this.macList = [];
        this.inwardService.getAllMACMappingByInwardId(id).subscribe((res: any) => {
            this.macList = res.dataList;
            if (this.macList.length === 0) {
                (response: any) => {
                    this.toastr.info(response.responseMessage, "No product available for this outward.");
                }
            }
        });
    }

    getSelItemType(event) {
        this.bulkConsumptionFormGroup.get("productId").reset();
        this.bulkConsumptionFormGroup.get("nonSerializedQty").reset();
        if (event.value == "Non Serialized Item") {
            this.getAllProductbasedOnItemType(event.value);
            this.getSerializedProductFlag = false;
            this.getAllAssemblyNameFlag = false;
            this.getAllNonSerializedProductFlag = true;
            this.sourceTypeFlag = false;
            this.sourceFlag = false;
            this.getAllSingleItemMacFlag = false;
        } else {
            this.getSerializedProductFlag = true;
            this.getAllAssemblyNameFlag = false;
            this.getAllNonSerializedProductFlag = false;
            this.sourceTypeFlag = false;
            this.sourceFlag = false;
            this.getAllNonSerializedItemFlag = false;
            this.getAllProductbasedOnItemType(event.value);
        }
    }
    getSources(sourceType): void {
        this.getAllNonSerializedItemFlag = false;
        this.getAllSingleItemMacFlag = false;
        this.sourceFlag = true;
        this.bulkConsumptionFormGroup.get("ownerId").reset();
        this.bulkConsumptionFormGroup.get("nonSerializedQty").reset();
        if (sourceType == "Warehouse") {
            this.sources = this.warehouses;
            this.sourceTypeAsStaffFlag = false;
        } else if (sourceType == "Staff") {
            this.sources = this.staffList;
            this.sourceTypeAsStaffFlag = true;
        } else if (sourceType == "Partner") {
            this.sourceTypeAsStaffFlag = false;
            const url = "/partner/all";
            this.partnerService.getMethodNew(url).subscribe(
                (res: any) => {
                    this.sources = res.partnerlist;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }
    async getAvailableQty(id) {
        this.selectedMACAddress = [];
        this.getItemListFlag = true;
        this.inwardService.getById(id).subscribe((res: any) => {
            let productId = res.data.productId.id;
            const url = "/product/" + productId;
            this.productService.getMethod(url).subscribe((response: any) => {
                let product = response.data;
                this.productHasMac = product.productCategory.hasMac;
                this.productHasSerial = product.productCategory.hasSerial;
                if (this.productHasMac || this.productHasSerial) {
                    this.getMacMappingsByInwardId(id);
                }
            });
        });
    }

    deleteMacMappInCustomer(macMaddress) {
    }

    searchProduct(): void {
        const url = "/bulk_consumption/searchByNamebybulkconsumption";
        if (!this.searchkey || this.searchkey !== this.searchProductCatName) {
            this.currentPageProductListdata = 1;
        }
        this.searchkey = this.searchProductCatName;
        if (this.showItemPerPage) {
            this.productListdataitemsPerPage = this.showItemPerPage;
        }
        this.searchData.filters[0].filterValue = this.searchProductCatName.trim();
        this.searchData.page = this.currentPageProductListdata;
        this.searchData.pageSize = this.productListdataitemsPerPage;
        this.bulkConsumptionService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                if (response.responseCode === 200) {
                    this.productListData = response.dataList;
                    this.productListdatatotalRecords = response.totalRecords;
                    this.dataSource.data = [...this.productListData];
                    if (this.paginator) {
                        this.paginator.length = this.productListdatatotalRecords;
                        this.paginator.pageIndex = 0;
                    };

                } else {
                    this.toastr.info(response.responseMessage, 'Info!');

                    this.productListData = [];
                    this.productListdatatotalRecords = 0;
                    this.dataSource.data = [];
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    clearSearchProduct(): void {
        this.searchProductCatName = "";
        this.getSerializedProductFlag = false;
        this.getItemListFlag = false;
        this.hideSearchBar = true;
        this.editMode = false;
        this.macList = [];
        this.submitted = false;
        this.searchkey = "";
        this.getProductList("");
        this.availableQtyFlag = false;
        this.sourceTypeFlag = false;
        this.sourceFlag = false;
        this.getAllNonSerializedProductFlag = false;
        this.bulkConsumptionFormGroup.reset();
        this.listView = true;
        this.createView = false;
        this.detailView = false;
        this.getSerializedProductFlag = false;
        this.sourceFlag = false;
        this.getInwardFlag = false;
        this.availableQtyFlag = false;
        this.sourceTypeAsStaffFlag = false;
        this.getAllAssemblyNameFlag = false;
        this.getAllSingleItemMacFlag = false;
        this.getAllNonSerializedItemFlag = false;
        this.itemConditionSingleFlag = false;
        this.selectedMACAddress = [];
        this.itemIds = [];
    }

    getProductList(list): void {
        this.productListData = [];
        this.searchkey = "";

        if (list) {
            this.productListdataitemsPerPage = list;
        }

        const plandata = {
            page: this.currentPageProductListdata,
            pageSize: this.productListdataitemsPerPage
        };

        const url = "/bulk_consumption";
        this.bulkConsumptionService.postMethod(url, plandata).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.productListData = response.dataList;
                    this.productListdatatotalRecords = response.totalRecords;
                    this.dataSource.data = [...this.productListData];

                    setTimeout(() => {
                        if (this.paginator) {
                            this.paginator.length = this.productListdatatotalRecords;
                            this.paginator.pageIndex = this.currentPageProductListdata - 1;
                        }
                    });
                } else if (response.responseCode == 406) {
                    this.toastr.info(response.responseMessage, 'Info!');
                    this.productListData = [];
                    this.productListdatatotalRecords = 0;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }


    itemIds: any = [];

    saveMapping: any;
    updateMapping: any;
    submitSerialized() {
        this.submitted = true;
        const type = "Serialized Item";
        this.mapping = this.mapData(type);
        if (this.mapping.itemListLongId.length === 0) {
            (response: any) => {
                this.toastr.info(response.responseMessage, `Please select atleast one item.`);
            }
            this.selectedMACAddress = [];
            this.itemIds = [];
            return;
        }

        const url = "/bulk_consumption/save";
        this.bulkConsumptionService.postMethod(url, this.mapping).subscribe(
            (response: any) => {
                if (response.responseCode === 200) {
                    this.macList = [];
                    this.toastr.success(`Success`, "Successfully ");
                    this.submitted = false;
                    this.clearSearchProduct();
                    this.onCancel();
                } else {
                    this.toastr.info(response.responseMessage, 'Info!');
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.error}`, 'Failed!');
            }
        );
    }
    submitNonSerialized() {
        this.submitted = true;
        const type = "Non Serialized Item";
        this.mapping = this.mapData(type);
        if (this.mapping.nonSerializedQty == 0) {
            (response: any) => {
                this.toastr.info(response.responseMessage, `Please enter quantity more than 0.`);
            }
            this.selectedMACAddress = [];
            return;
        }

        const url = "/bulk_consumption/save";
        this.bulkConsumptionService.postMethod(url, this.mapping).subscribe(
            (response: any) => {
                if (response.responseCode == 406) {
                    this.toastr.info(response.responseMessage, 'Info!');
                } else {
                    this.macList = [];
                    this.toastr.success(`Success`, "Successfully ");
                    this.submitted = false;
                    this.clearSearchProduct();
                    this.onCancel();
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }


    deleteProduct(id): void {
        const url = "/bulk_consumption/delete";
        const productEditData = this.productListData.find(element => element.id == id);
        this.bulkConsumptionService.deleteMethod(url, productEditData).subscribe(
            (response: any) => {
                if (response.responseCode === 406) {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                } else {
                    this.toastr.success(`Success`, "Successfully ");
                }
                this.getProductList("");
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    pageChangedProductList(event: PageEvent): void {
        this.currentPageProductListdata = event.pageIndex + 1;
        this.productListdataitemsPerPage = event.pageSize;
        if (this.searchkey) {
            this.searchProduct();
        } else {
            this.getProductList("");
        }
    }


    TotalItemPerPage(event: any): void {
        this.showItemPerPage = Number(event.value);
        this.productListdataitemsPerPage = this.showItemPerPage;
        this.currentPageProductListdata = 1;

        if (this.paginator) {
            this.paginator.pageIndex = 0;  // Reset paginator UI page index
        }

        if (!this.searchkey) {
            this.getProductList(this.showItemPerPage);
        } else {
            this.searchProduct();
        }
    }

    approveChangeStatus(id) {
        this.approveChangeStatusModal = true;
        this.assignInwardID = id;
        this.productService.getAllActiveProduct().subscribe((res: any) => {
            this.products = res.dataList;
        });
        this.inwardService.getAllWareHouse().subscribe((res: any) => {
            this.warehouses = res.dataList;
        });
        this.outwardService.getAllStaff().subscribe((res: any) => {
            const staffId = localStorage.getItem("userId");
            this.staffList = res.dataList.filter(element => element.id == staffId);
        });
    }
    rejectChangeStatus(id) {
        this.rejectChangeStatusModal = true;
        this.assignInwardID = id;
    }

    approveInventory(): void {
        this.assignInwardSubmitted = true;
        if (this.assignInwardForm.valid) {
            this.approved = false;
            this.approveInventoryData = [];
            this.selectStaff = null;
            let approvalInwardData = {
                id: this.assignInwardID,
                approvalStatus: "Approve",
                approvalRemark: this.assignInwardForm.controls.remark.value
            };
            const url = `/bulk_consumption/approveStatus`;
            this.customerInventoryMappingService.postMethod(url, approvalInwardData).subscribe(
                (response: any) => {
                    if (response.responseCode == 200) {
                        this.closeApproveInventoryModal();
                        this.toastr.success(`Success`, "Successfully ");
                        if (this.dialogRef) {
                            this.dialogRef.close();
                        }
                        if (response.dataList) {
                            this.approved = true;
                            this.approveInventoryData = response.dataList;
                        } else {
                            this.getProductList("");
                        }
                    } else {
                        this.toastr.error(`${response.error.ERROR}`, 'Failed!');
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }
    rejectInventory(): void {
        this.rejectInwardSubmitted = true;
        if (this.rejectInwardForm.valid) {
            this.reject = false;
            this.selectStaffReject = null;
            this.rejectInventoryData = [];
            let approvalInwardData = {
                id: this.assignInwardID,
                approvalStatus: "Rejected",
                approvalRemark: this.rejectInwardForm.controls.remark.value
            };
            const url = `/bulk_consumption/approveStatus`;

            this.customerInventoryMappingService.postMethod(url, approvalInwardData).subscribe(
                (response: any) => {
                    if (response.responseCode == 200) {
                        this.closeRejectInventoryModal();
                        if (this.dialogRef) {
                            this.dialogRef.close();
                        }

                        if (response.dataList) {
                            this.reject = true;
                            this.rejectInventoryData = response.dataList;
                        } else {
                            this.getProductList("");
                        }
                    } else {
                        this.toastr.error(`${response.error.ERROR}`, 'Failed!');
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    getInwardMACMapping(inwardId) {
        this.inwardMacList = [];
        this.inwardService.getInwardMacMapping(inwardId).subscribe(
            (res: any) => {
                if (res.dataList.length > 0) {
                    this.inwardMacList = res.dataList;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    ShowMACandSerial(id) {
        this.inwardMacList = [];
        this.fullInwardMacList = []; // Reset full list
        this.dialogRef = this.dialog.open(this.macMappingDialog, {
            width: '50%',
            disableClose: true
        });
        const url = "/bulk_consumption/getBulkConsumptionMapping?bulkconsumptionId=" + id;
        this.bulkConsumptionService.getMethod(url).subscribe(
            (res: any) => {
                if (res.responseCode == 200) {
                    if (res.dataList.length > 0) {
                        this.fullInwardMacList = res.dataList;
                        this.inwardMacdatatotalRecords = res.dataList.length;
                        this.currentPageinwardMacdata = 0;
                        this.inwardMacList = this.fullInwardMacList.slice(0, this.inwardMacdataitemsPerPage);
                        this.MACShowModal = true;
                    }
                } else {
                    this.toastr.error(`${res.error.ERROR}`, 'Failed!');
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    closeMACandSerialModal() {
        this.MACShowModal = false;
        this.inwardMacList = [];
        this.fullInwardMacList = [];
        this.currentPageinwardMacdata = 1;
        this.getItemListFlag = false;
        this.getInwardFlag = false;
    }

    pageChangedMacSerialList(event: PageEvent) {
        this.currentPageinwardMacdata = event.pageIndex;
        this.inwardMacdataitemsPerPage = event.pageSize;
        const startIndex = event.pageIndex * event.pageSize;
        const endIndex = startIndex + event.pageSize;
        this.inwardMacList = this.fullInwardMacList.slice(startIndex, endIndex);
    }

    closeApproveInventoryModal() {
        this.assignInwardSubmitted = false;
        this.assignInwardForm.reset();
        this.approveChangeStatusModal = false;
    }

    closeRejectInventoryModal() {
        this.rejectInwardSubmitted = false;
        this.rejectInwardForm.reset();
        this.rejectChangeStatusModal = false;
    }

    canExit() {
        if (!this.bulkConsumptionFormGroup.dirty) return true;
        {
            return Observable.create((observer: Observer<boolean>) => {
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
                return false;
            });
        }
    }
    assignQuantityValidation(event) {
        var num = String.fromCharCode(event.which);
        if (!/[0-9]/.test(num)) {
            event.preventDefault();
        }
    }
}
