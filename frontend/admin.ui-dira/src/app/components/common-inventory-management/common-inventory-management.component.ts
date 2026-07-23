import { Component, Inject, Input, OnInit, Optional, Output, SimpleChange, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { ConfirmationService, MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { CustomerInventoryManagementService } from "src/app/service/customer-inventory-management.service";
import { BehaviorSubject, Observable, Observer } from "rxjs";
import { CustomerInventoryDetailsService } from "src/app/service/customer-inventory-details.service";
import { element } from "protractor";
import { Table } from "primeng/table";
import { LoginService } from "src/app/service/login.service";
import { INVENTORYS, MASTERS } from "src/app/constants/aclConstants";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { InwardService } from "src/app/service/inward.service";
import { EventEmitter } from "stream";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from "@angular/material/dialog";
import { ToastrService } from 'ngx-toastr';
import { error } from "console";

declare var $: any;
@Component({
    selector: "app-common-inventory-management",
    templateUrl: "./common-inventory-management.component.html",
    styleUrls: ["./common-inventory-management.component.css"],
    standalone: false
})
export class CommonInventoryManagementComponent implements OnInit {

    @Input() data: any;
    @Input() type: any;
    @Input() openFrom: string = "";
    @Input() assignIntevortServiceArea = false
    inventoryListDataCurrentPage = 1;
    inventoryListItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    inventoryListDataTotalRecords: number;
    assignedInventoryList = [];
    loggedInStaffId = localStorage.getItem("userId");
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    private assignInventoryCustomerId: any;
    assignedInventoryListWithSerial = [];
    viewAssignInventoryWithSerial: boolean = false;
    customerInventoryMappingId: any;
    assignProduct: any;
    assignInwardID: any;
    assignInwardForm: UntypedFormGroup;
    rejectInwardForm: UntypedFormGroup;
    inventoryAssignForm: UntypedFormGroup;
    inventoryReplaceForm: UntypedFormGroup;
    assignInwardSubmitted: boolean = false;
    approveChangeStatusModal: boolean = false;
    rejectChangeStatusModal: boolean = false;
    assignInventory: boolean = false;
    rejectInwardSubmitted: boolean = false;
    inventoryAssignSubmitted: boolean = false;
    inventoryReplaceSubmitted: boolean = false;
    allActiveProducts: any = [];
    macAddressList: any = [];
    selectedMACAddress: any = [];
    staffUserId: any;
    approved: boolean = false;
    reject: boolean = false;
    approveInventoryData: any = [];
    rejectInventoryData: any = [];
    selectStaffReject: any;
    selectStaff: any;
    getAllSerializedProductFlag: boolean = false;
    getItemSelctionFlag: boolean = false;
    getAllNonSerializedProductFlag: boolean = false;
    serializedItemAssignFlag: boolean = false;
    nonSerializedItemAssignFlag: boolean = false;
    allActiveNonTrackableProducts: any = [];
    getNonTrackableProductQtyList: any = [];
    availableQtyFlag: boolean = false;
    assignInventoryAccess: boolean = false;
    editAccess: boolean = false;
    approveProgressAccess: boolean = false;
    deleteAccess: boolean = false;
    showQtyError: boolean;
    negativeAssignQtyError: boolean;
    availableQty = 0;
    hasMac: boolean;
    hasSerial: boolean;
    enterMacSerial: any = "";
    editMacSerialBtn: any = "";
    fileterGlobal: any = "";
    replaceInventory: boolean = false;
    isEditEnable: boolean = false;
    @ViewChild("dt") table: Table;
    inventoryStatus = [
        { label: "Active", value: "ACTIVE" },
        { label: "Inactive", value: "INACTIVE" }
    ];
    ItemSelectionType = [
        { label: "Serialized Item", value: "Serialized Item" },
        { label: "Non Serialized Item", value: "Non Serialized Item" }
    ];
    replacementreasonList = [
        { label: "Defective", value: "Defective" },
        { label: "Upgrade", value: "Upgrade" }
    ];
    productTotalInPorts: number = 0;
    productAvailableInPorts: number = 0;
    productTotalOutPorts: number = 0;
    productavailableOutPorts: number = 0;
    deviceTotalInPorts: number = 0;
    deviceAvailableInPorts: number = 0;
    deviceTotalOutPorts: number = 0;
    deviceAvailableOutPorts: number = 0;
    isProductSelected: boolean = false;
    deviceName: string = "";
    currentDate: Date = new Date();
    currentMacAddressListdata = 1;
    macAddressListdataitemsPerPage = RadiusConstants.PER_PAGE_ITEMS;
    macAddressListtotalRecords: any;
    newFirstMacAddress: number = 0;
    productMacAddressId: any;
    optionValue: any;
    searchMacData: any;
    macOptionSelect = this.commondropdownService.searchInventoryOption;
    searchOption: any;
    searchMacDeatil: string;
    staffId: string;
    searchForm: UntypedFormGroup;

    // after change 
    @Input() totalRecords!: number;
    @Input() itemsPerPage!: number;


    constructor(
        private toastr: ToastrService,

        private messageService: MessageService,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private customerInventoryManagementService: CustomerInventoryManagementService,
        public CustomerInventoryDetailsService: CustomerInventoryDetailsService,
        public loginService: LoginService,
        public commondropdownService: CommondropdownService,
        private inwardService: InwardService,
        private dialog: MatDialog,
        @Optional() @Inject(MAT_DIALOG_DATA) public datas: any
    ) { }

    ngOnInit(): void {

        this.assignInventoryAccess = this.loginService.hasPermission(
            this.openFrom == "service_area"
                ? MASTERS.SA_INVENTORY_ASSIGN
                : INVENTORYS.POP_INVEN_LIST_ASSIGN_INVENTORY
        );
        this.editAccess = this.loginService.hasPermission(
            this.openFrom == "service_area" ? MASTERS.SA_INVENTORY_EDIT : INVENTORYS.INVEN_LIST_EDIT
        );
        this.deleteAccess = this.loginService.hasPermission(
            this.openFrom == "service_area" ? MASTERS.SA_INVENTORY_DELETE : INVENTORYS.INVEN_LIST_DELETE
        );
        this.approveProgressAccess = this.loginService.hasPermission(
            this.openFrom == "service_area"
                ? MASTERS.SA_INVENTORY_APPROVE
                : INVENTORYS.INVEN_LIST_PROGRESS
        );
        this.staffUserId = localStorage.getItem("userId");
        this.getAssignedInventoryList();

        this.assignInwardForm = this.fb.group({
            remark: ['', Validators.required]
        });
        this.rejectInwardForm = this.fb.group({
            remark: ["", Validators.required]
        });

        // this.initInventoryAssignForm();
        this.inventoryAssignForm = this.fb.group({
            id: [""],
            qty: ["1"],
            productId: ["", Validators.required],
            //customerId: [this.customerId],
            staffId: [""],
            inwardId: [""],
            assignedDateTime: [this.currentDate, Validators.required],
            status: [""],
            mvnoId: [""],
            ownerId: [this.data.id],
            ownerType: [this.type],
            itemTypeFlag: ["", Validators.required],
            nonSerializedQty: [""],
            latitude: [""],
            longitude: [""]
        });

        this.inventoryReplaceForm = this.fb.group({
            macMappingId: [""],
            id: [""],
            qty: ["1"],
            productId: ["", Validators.required],
            staffId: [""],
            inwardId: [""],
            assignedDateTime: [this.currentDate, Validators.required],
            status: [""],
            mvnoId: [""],
            ownerId: [this.data.id],
            ownerType: [this.type],
            itemTypeFlag: ["", Validators.required],
            nonSerializedQty: [""],
            replacementReason: ["", Validators.required]
        });

        this.inventoryReplaceForm.get("nonSerializedQty").valueChanges.subscribe(val => {
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

        this.inventoryAssignForm.get("nonSerializedQty").valueChanges.subscribe(val => {
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
        this.searchForm = this.fb.group({
            searchOption: [""],
            searchMacDeatil: [""]
        });
        this.searchMacData = {
            filterBy: "",
            paginationRequestDTO: {
                page: "",
                pageSize: "",
                filters: [
                    {
                        filterDataType: "",
                        filterValue: "",
                        filterColumn: "any",
                        filterOperator: "equalto",
                        filterCondition: "and"
                    }
                ]
            },
            productId: "",
            ownerId: "",
            ownerType: ""
        };
    }
    selMacSearchOption(event) {
        this.searchForm.value.searchMacDeatil = "";
        this.optionValue = event;
    }
    searchMac() {
        const staffId = localStorage.getItem("userId");
        this.searchMacData.paginationRequestDTO.filters[0].filterValue =
            this.searchForm.value.searchMacDeatil;
        this.searchMacData.paginationRequestDTO.filters[0].filterColumn =
            this.searchForm.value.searchOption;
        this.searchMacData.productId = this.productMacAddressId;
        this.searchMacData.ownerId = Number(staffId);
        this.searchMacData.ownerType = "staff";
        this.searchMacData.paginationRequestDTO.page = this.currentMacAddressListdata;
        this.searchMacData.paginationRequestDTO.pageSize = this.macAddressListdataitemsPerPage;
        const url = "/outwards/searchItemHistoryByProduct";
        this.inwardService.postMethod(url, this.searchMacData).subscribe(
            (response: any) => {
                this.macAddressList = response.dataList;
                this.macAddressListtotalRecords = response.totalRecords;
            },
            (error: any) => {
                console.log(error, "error");

                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }
    clearMac() {
        this.searchForm.reset();
        this.newFirstMacAddress = 0;
        this.currentMacAddressListdata = 1;
        this.macAddressListdataitemsPerPage = 20;
        let obj = {
            value: this.productMacAddressId
        };
        this.getMacAddressList(obj);
    }

    getAssignedInventoryList(): void {
        const data = {
            filters: [
                {
                    filterValue: this.data.id,
                    filterColumn: this.type
                }
            ],
            page: this.inventoryListDataCurrentPage,
            pageSize: this.inventoryListItemsPerPage,
            sortBy: "createdate",
            sortOrder: 0
        };
        const url = "/inwards/getByOwnerIdAndType";
        this.customerInventoryManagementService.postMethod(url, data).subscribe(
            (res: any) => {
                this.assignedInventoryList = res.dataList;
                this.inventoryListDataTotalRecords = res.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');

            }
        );
    }

    pageChangedEventAssignInventory(pageNumber): void {
        this.inventoryListDataCurrentPage = pageNumber;
        this.getAssignedInventoryList();
    }
    pageChangedEventCustomerAssignInventoryDetails(pageNumber): void {
    }

    itemPerPageChangedEventAssignInventory(event): void {
        this.inventoryListItemsPerPage = Number(event.value);
        if (this.inventoryListDataCurrentPage > 1) {
            this.inventoryListDataCurrentPage = 1;
        }
        this.getAssignedInventoryList();
    }

    @ViewChild('viewAssignInventoryWithSerialDailog') viewAssignInventoryWithSerialDailog!: TemplateRef<any>;
    editCustomerInventory(mappingId, assignInventory): void {
        this.assignProduct = assignInventory;
        this.customerInventoryMappingId = mappingId;
        const invenoryDetailsMapping: any[] = this.assignedInventoryList.find(
            inventory => inventory.id === mappingId
        ).inOutWardMACMapping;
        if (invenoryDetailsMapping.length === 0) {
            error: (error) => {
                this.toastr.info(`${error.responseMessage}`, 'Assigned product is not eligible for replace!');
            }

        } else {
            this.dialog.open(this.viewAssignInventoryWithSerialDailog, {
                width: '80%',
                disableClose: true // same as data-backdrop="static" data-keyboard="false"
            });

            // this.viewAssignInventoryWithSerial = true;
            this.assignedInventoryListWithSerial = invenoryDetailsMapping;
        }
    }

    removeConfirmationInventory(assignedInventoryId: number) {
        if (assignedInventoryId) {
            this.confirmationService.confirm({
                message: "Do you want to remove inventory " + "?",
                header: "Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.removeInventory(assignedInventoryId, "true");
                },
                reject: () => {
                    error: (error) => {
                        this.toastr.info(`${error.responseMessage}`, 'You have rejected!');
                    }

                }
            });
        }
    }

    removeInventory(id, type): void {
        // const url = `/inoutWardMacMapping/removeInventory?macMappingId=${id}&customerInventoryId=${this.customerInventoryMappingId}`;
        const url = `/inoutWardMacMapping/removeInventoryfromowner?macMappingId=${id}&isflag=` + type;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (res: any) => {
                if (res.responseCode == 406) {
                    this.toastr.info(`${res.responseMessage}`, 'Info!');

                } else {
                    this.viewAssignInventoryWithSerial = false;
                    this.getAssignedInventoryList();
                    this.toastr.success(`${res.message}`, 'Removed Successfully!');



                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');


            }
        );
    }

    @ViewChild('assignInventoryModelDialog') assignInventoryModelDialog!: TemplateRef<any>;
    assignInventoryModalOpen() {
        this.dialogRef = this.dialog.open(this.assignInventoryModelDialog, {
            width: '80%',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });


        this.dialogRef.afterClosed().subscribe(result => {
            this.dialogRef = null;
        });
    }
    @ViewChild('approveChangeStatusDialog') approveChangeStatusDialog!: TemplateRef<any>;
    @ViewChild('rejectChangeStatusModalDialog') rejectChangeStatusModalDialog!: TemplateRef<any>;
    approveChangeStatus(id) {
        this.approveChangeStatusModal = true;
        this.assignInwardID = id;

        if (this.approveChangeStatusDialog) {
            this.dialogRef = this.dialog.open(this.approveChangeStatusDialog, {
                width: '50%',
                height: 'auto',
                autoFocus: false,
                disableClose: true
            });


            this.dialogRef.afterClosed().subscribe(result => {
                this.dialogRef = null;
            });
        }
    }
    rejectChangeStatus(id) {
        this.rejectChangeStatusModal = true;
        this.assignInwardID = id;

        if (this.rejectChangeStatusModalDialog) {
            this.dialogRef = this.dialog.open(this.rejectChangeStatusModalDialog, {
                width: '50%',
                height: 'auto',
                autoFocus: false,
                disableClose: true
            });


            this.dialogRef.afterClosed().subscribe(result => {
                this.dialogRef = null;
            });
        }
    }

    closeApproveInventoryModal() {
        if (this.dialogRef) {
            this.dialogRef.close();
        }
        this.assignInwardSubmitted = false;
        this.assignInwardForm.reset();
        this.assignInwardID = "";
        this.approveChangeStatusModal = false;
    }

    closeRejectInventoryModal() {
        if (this.dialogRef) {
            this.dialogRef.close();
        }
        this.rejectInwardSubmitted = false;
        this.assignInwardID = "";
        this.rejectInwardForm.reset();
        this.rejectChangeStatusModal = false;
    }

    getAllProducts() {
        const url = "/product/getAllNetworkandNaBindProduct";
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (res: any) => {
                this.allActiveProducts = res.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');


            }
        );
    }

    getMacAddressList(event) {
        if (event && event.value !== this.productMacAddressId) {
            this.currentMacAddressListdata = 1;
            this.newFirstMacAddress = 0;
        }
        const staffId = localStorage.getItem("userId");
        this.productMacAddressId = event.value;
        let product = this.allActiveProducts.find(element => element.id == this.productMacAddressId);
        this.hasMac = product.productCategory.hasMac;
        this.hasSerial = product.productCategory.hasSerial;
        let currentPage = this.currentMacAddressListdata;
        const requestData = {
            productId: this.productMacAddressId,
            ownerId: staffId,
            ownerType: "staff",
            paginationRequestDTO: {
                page: currentPage,
                pageSize: this.macAddressListdataitemsPerPage
            }
        };

        const url = "/outwards/getItemHistoryByProduct";

        this.customerInventoryManagementService.postMethod(url, requestData).subscribe(
            (res: any) => {
                if (res?.dataList?.length > 0) {
                    this.macAddressList = res.dataList;
                    this.macAddressListtotalRecords = res.totalRecords;
                    this.getItemSelctionFlag = true;
                    // this.newFirstMacAddress = 1;
                } else {
                    this.macAddressList = [];
                    this.newFirstMacAddress = 0;
                    this.macAddressListtotalRecords = 0;
                    this.getItemSelctionFlag = false;
                    this.toastr.info(`${res.responseMessage}`, 'Product MAC address not available!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');


            }
        );
    }
    paginateMacAddress(event) {
        this.newFirstMacAddress = event.first;
        this.macAddressListdataitemsPerPage = event.rows;
        this.currentMacAddressListdata = event.page + 1;
        let obj = {
            value: this.productMacAddressId
        };
        this.getMacAddressList(obj);
    }
    assigneInventory(dialogRef): void {
        Object.keys(this.inventoryAssignForm.controls).forEach(field => {
            const control = this.inventoryAssignForm.get(field);
            control?.markAsTouched({ onlySelf: true });

            if (control?.invalid) {
                console.error(` Invalid field: ${field}`, control.errors);
            } else {
            }
        });

        this.inventoryAssignSubmitted = true;
        let assigneInventoryData: any = "";
        assigneInventoryData = this.inventoryAssignForm.value;
        assigneInventoryData.qty = "1";
        assigneInventoryData.itemId = this.selectedMACAddress?.itemId;
        assigneInventoryData.staffId = this.staffUserId;
        assigneInventoryData.inOutWardMACMapping = [];
        if (this.selectedMACAddress != "") {
            assigneInventoryData.inOutWardMACMapping.push(this.selectedMACAddress);
        }

        if (this.inventoryAssignForm.valid) {
            if (assigneInventoryData.inOutWardMACMapping.length > 0) {
                const url = "/inwards/assignToEndOwner";
                this.customerInventoryManagementService.postMethod(url, assigneInventoryData).subscribe(
                    (res: any) => {
                        if (res.responseCode == 200) {
                            dialogRef.close()
                            this.assignInventoryModalClose();
                            this.getAssignedInventoryList();
                            this.toastr.success(`${res.message}`, 'Assigned inventory successfully!');


                        } else if (res.responseCode == 406) {
                            this.toastr.info(`${res.responseMessage}`, 'Info!');

                        } else {
                            this.toastr.error(`${res.responseMessage}`, 'Failed!');


                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.msg}`, 'Failed!');

                    }
                );
            } else {
                this.toastr.info(`Please Select One Mac Address!`, 'Info');

            }
        }
    }

    assignInventoryModalClose() {
        if (this.dialogRef) {
            this.dialogRef.close();
        }
        this.inventoryAssignSubmitted = false;
        //this.inventoryAssignForm.reset();
        this.inventoryAssignForm.get("id").reset();
        this.inventoryAssignForm.get("qty").reset();
        this.inventoryAssignForm.get("productId").reset();
        this.inventoryAssignForm.get("staffId").reset();
        this.inventoryAssignForm.get("inwardId").reset();
        this.inventoryAssignForm.get("assignedDateTime").reset();

        this.inventoryAssignForm.get("status").reset();
        this.inventoryAssignForm.get("mvnoId").reset();
        this.inventoryAssignForm.get("itemTypeFlag").reset();
        this.inventoryAssignForm.get("nonSerializedQty").reset();
        this.selectedMACAddress = "";
        this.fileterGlobal = "";
        this.macAddressList = [];
        // this.initInventoryAssignForm();
        this.getAllNonSerializedProductFlag = false;
        this.getAllSerializedProductFlag = false;
        this.serializedItemAssignFlag = false;
        this.nonSerializedItemAssignFlag = false;
        this.availableQtyFlag = false;
        this.getItemSelctionFlag = false;
        this.showQtyError = false;
        this.negativeAssignQtyError = false;
        this.assignInventory = false;
        this.currentMacAddressListdata = 1;
        this.newFirstMacAddress = 0;
    }

    approveInventory(dialogRef): void {
        this.assignInwardSubmitted = true;
        if (this.assignInwardForm.invalid) {
            this.assignInwardForm.markAllAsTouched();
            Object.values(this.assignInwardForm.controls).forEach(c => {
                c.markAsDirty();
                c.updateValueAndValidity({ onlySelf: true });
            });
            return;
        }
        if (this.assignInwardForm.valid) {
            this.approved = false;
            this.approveInventoryData = [];
            this.selectStaff = null;
            let approvalInwardData = {
                id: this.assignInwardID,
                approvalStatus: "Approve",
                approvalRemark: this.assignInwardForm.controls.remark.value
            };
            const url = `/inwards/approveInventoryFromOwner?inventoryApprovalRemark=${approvalInwardData.approvalRemark}&inventoryMappingId=${approvalInwardData.id}&isApproveRequest=true`;
            this.customerInventoryManagementService.getMethod(url).subscribe(
                (response: any) => {
                    if (response.responseCode == 200) {
                        dialogRef.close()
                        this.closeApproveInventoryModal();
                        this.toastr.success(`${response.message}`, 'Success!');

                        if (response.dataList) {
                            this.approved = true;
                            this.approveInventoryData = response.dataList;
                        } else {
                            this.getAssignedInventoryList();
                        }
                    } else {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');


                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.msg}`, 'Failed!');

                }
            );
        }
    }
    rejectInventory(dialogRef): void {
        this.rejectInwardSubmitted = true;
        if (this.assignInwardForm.invalid) {
            this.assignInwardForm.markAllAsTouched();
            Object.values(this.assignInwardForm.controls).forEach(c => {
                c.markAsDirty();
                c.updateValueAndValidity({ onlySelf: true });
            });
            return;
        }
        if (this.rejectInwardForm.valid) {
            this.reject = false;
            this.selectStaffReject = null;
            this.rejectInventoryData = [];
            let approvalInwardData = {
                id: this.assignInwardID,
                approvalStatus: "Rejected",
                approvalRemark: this.rejectInwardForm.controls.remark.value
            };
            const url = `/inwards/approveInventoryFromOwner?inventoryApprovalRemark=${approvalInwardData.approvalRemark}&inventoryMappingId=${approvalInwardData.id}&isApproveRequest=false`;

            this.customerInventoryManagementService.getMethod(url).subscribe(
                (response: any) => {
                    if (response.responseCode == 200) {
                        dialogRef.close()
                        this.closeRejectInventoryModal();
                        if (response.dataList) {
                            this.reject = true;
                            this.rejectInventoryData = response.dataList;
                        } else {
                            this.getAssignedInventoryList();
                        }

                        this.toastr.success(`${response.message}`, 'Success!');


                    } else {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');

                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.msg}`, 'Failed!');


                }
            );
        }
    }
    getSelItemType(event) {
        // console.log("event", event.value);
        this.inventoryAssignForm.get("productId").reset();
        if (event.value == "Non Serialized Item") {
            this.getAllNonSerializedProductFlag = true;
            this.getAllSerializedProductFlag = false;
            this.getItemSelctionFlag = false;
            this.serializedItemAssignFlag = false;
            this.nonSerializedItemAssignFlag = true;
            this.availableQtyFlag = false;
            this.getProductSelection();
        } else {
            this.getAllNonSerializedProductFlag = false;
            this.getAllSerializedProductFlag = true;
            this.getItemSelctionFlag = false;
            this.serializedItemAssignFlag = true;
            this.nonSerializedItemAssignFlag = false;
            this.availableQtyFlag = false;
            this.getAllProducts();
        }
    }

    getProductSelection(): void {
        const url = "/product/getAllNetworkAndNABindNonSerializedProduct";
        this.customerInventoryManagementService.getMethod(url).subscribe((response: any) => {
            this.allActiveNonTrackableProducts = response.dataList;
        });
    }

    getNonTrackableProductQty(event) {
        this.showQtyError = false;
        this.negativeAssignQtyError = false;
        this.inventoryAssignForm.get("nonSerializedQty").reset();
        const staffId = localStorage.getItem("userId");
        const productId = event.value;
        const url =
            "/outwards/getNonTrackableProductQty?productId=" +
            productId +
            "&ownerId=" +
            staffId +
            "&ownerType=Staff";
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (res: any) => {
                this.availableQtyFlag = true;
                this.getNonTrackableProductQtyList = res.dataList;
                if (res.dataList.length == 0) {
                    this.availableQty = 0;
                } else {
                    this.availableQty = res.dataList.find(element => element).unusedQty;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');


            }
        );
    }
    assigneOtherInventoryForNonSerializedItem(dialogRef): void {
        this.inventoryAssignSubmitted = true;
        let data: any = "";
        data = this.inventoryAssignForm.value;
        data.itemId = data.productId;
        // data.customerId = this.custData.id;
        data.staffId = this.staffUserId;
        data.itemAssemblyStatus = "Pending";
        if (this.inventoryAssignForm.valid && !this.showQtyError && !this.negativeAssignQtyError) {
            data.qty = data.nonSerializedQty;
            if (data.qty == null || data.qty == "") {
                error: (error) => {
                    this.toastr.info(`${error.responseMessage}`, 'Please Enter Assign Quantity!');
                }

            } else {
                const url = "/inwards/assignNonSerializedItemToEndOwner";
                this.customerInventoryManagementService.postMethod(url, data).subscribe(
                    (res: any) => {
                        if (res.responseCode == 200) {
                            dialogRef.close()
                            this.assignInventoryModalClose();
                            this.getAssignedInventoryList();
                            this.toastr.success(`${res.message}`, 'Assigned inventory successfully!');


                        } else {

                            this.toastr.error(`${res.responseMessage}`, 'Failed!');

                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.msg}`, 'Failed!');

                    }
                );
            }
        }
    }
    saveMacidMapping(id, data) {
        let url = `/item/updateItemMacAndSerial?itemId=${id}&macAddress=${data.macAddress}&serialNumber=${data.serialNumber}`;
        this.customerInventoryManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.enterMacSerial = "";
                    this.isEditEnable = true;
                    this.toastr.success(`${response.responseMessage}`, 'Success!');

                } else {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');



                }

                // this.workflowAuditData1 = response.dataList;
                // this.MastertotalRecords1 = response.totalRecords;
            },
            (error: any) => {
                if (error.status == 200) {
                    this.toastr.error(`${error.ERROR}`, 'Failed!');


                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
                console.log(error, "error");
            }
        );
    }
    editMacMapping(id) {
        this.editMacSerialBtn = id;
        this.isEditEnable = true;
    }
    editMac(id) {
        this.enterMacSerial = id;
        this.isEditEnable = false;
    }
    assignQuantityValidation(event) {
        var num = String.fromCharCode(event.which);
        if (!/[0-9]/.test(num)) {
            event.preventDefault();
        }
    }
    clearFilterGlobal(table: Table) {
        this.fileterGlobal = "";
        table.clear();
    }


    @ViewChild('replaceInventoryDialog') replaceInventoryDialog!: TemplateRef<any>;

    InventoryReplace(assignInventory) {
        this.dialog.open(this.replaceInventoryDialog, {
            width: '80%',
            disableClose: true // same as data-backdrop="static" data-keyboard="false"
        });
        this.replaceInventory = true;

        let url = `/NetworkDevice/getNetworkDeviceByInventoryMappingId?id=${assignInventory.inventoryMappingId}`;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.inventoryReplaceForm.controls.macMappingId.setValue(assignInventory.id);
                this.deviceName = response.data.name;
                this.inventoryReplaceForm.controls.id.setValue(response.data.id);
                this.setPortValue(response.data);
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    setPortValue(product) {
        let availableInPorts = 0;
        let availableOutPorts = 0;
        let totalInPorts = 0;
        let totalOutPorts = 0;

        if (product.availableInPorts > 0) {
            availableInPorts = product.availableInPorts;
        }

        if (product.availableOutPorts > 0) {
            availableOutPorts = product.availableOutPorts;
        }

        if (product.totalInPorts > 0) {
            totalInPorts = product.totalInPorts;
        }

        if (product.totalOutPorts > 0) {
            totalOutPorts = product.totalOutPorts;
        }

        this.deviceAvailableInPorts = availableInPorts;
        this.deviceAvailableOutPorts = availableOutPorts;
        this.deviceTotalInPorts = totalInPorts;
        this.deviceTotalOutPorts = totalOutPorts;
    }

    getAllProductByDeviceId() {
        let deviceId = this.inventoryReplaceForm.value.id;
        let url = "";
        if (deviceId) {
            url = `/product/getAllNetworkandNaBindProductBasedOnDeviceId/${deviceId}`;
        } else {
            url = `/product/getAllNetworkandNaBindProductBasedOnDeviceId/-1/${this.assignProduct.productId}`;
        }
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.allActiveProducts = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    onItemChange(event) {
        this.inventoryAssignForm.get("productId").reset();
        if (event.value == "Non Serialized Item") {
            this.getAllNonSerializedProductFlag = true;
            this.getAllSerializedProductFlag = false;
            this.getItemSelctionFlag = false;
            this.serializedItemAssignFlag = false;
            this.nonSerializedItemAssignFlag = true;
            this.availableQtyFlag = false;
            this.getNonSerializedProductByDeviceId();
        } else {
            this.getAllNonSerializedProductFlag = false;
            this.getAllSerializedProductFlag = true;
            this.getItemSelctionFlag = false;
            this.serializedItemAssignFlag = true;
            this.nonSerializedItemAssignFlag = false;
            this.availableQtyFlag = false;
            this.getAllProductByDeviceId();
        }
    }

    onReplaceInventory(dialogRef): void {
        this.inventoryReplaceSubmitted = true;
        let assigneInventoryData: any = "";
        assigneInventoryData = this.inventoryReplaceForm.value;
        assigneInventoryData.id = "";
        assigneInventoryData.qty = "1";
        assigneInventoryData.itemId = this.selectedMACAddress?.itemId;
        assigneInventoryData.staffId = this.staffUserId;
        assigneInventoryData.inOutWardMACMapping = [];
        if (this.selectedMACAddress != "") {
            assigneInventoryData.inOutWardMACMapping.push(this.selectedMACAddress);
        }

        if (this.inventoryReplaceForm.valid) {
            if (assigneInventoryData.inOutWardMACMapping.length > 0) {
                const url = "/inwards/assignToEndOwner";
                this.customerInventoryManagementService.postMethod(url, assigneInventoryData).subscribe(
                    (res: any) => {
                        if (res.responseCode == 200) {
                            dialogRef.close()
                            this.replaceInventoryModalClose();
                            this.getAssignedInventoryList();
                            setTimeout(() => {
                                this.editCustomerInventory(this.customerInventoryMappingId, this.assignProduct);
                            }, 1000);

                            this.toastr.success(`${res.message}`, 'Assigned inventory successfully!');

                        } else if (res.responseCode == 406) {
                            this.toastr.info(`${res.responseMessage}`, 'Info!');

                        } else {
                            this.toastr.error(`${res.responseMessage}`, 'Failed!');

                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.msg}`, 'Failed!');

                    }
                );
            } else {
                error: (error) => {
                    this.toastr.info(`${error.responseMessage}`, 'Please Select One Mac Address!');
                }

            }
        }
    }

    replaceInventoryModalClose() {
        this.inventoryReplaceSubmitted = false;
        this.replaceInventory = false;
        this.inventoryReplaceForm.reset();
        this.inventoryReplaceForm.updateValueAndValidity();
        this.selectedMACAddress = "";
        this.fileterGlobal = "";
        this.macAddressList = [];
        this.getAllNonSerializedProductFlag = false;
        this.getAllSerializedProductFlag = false;
        this.serializedItemAssignFlag = false;
        this.nonSerializedItemAssignFlag = false;
        this.availableQtyFlag = false;
        this.getItemSelctionFlag = false;
        this.showQtyError = false;
        this.negativeAssignQtyError = false;
        this.assignInventory = false;
        this.isProductSelected = false;
        this.currentMacAddressListdata = 1;
        this.newFirstMacAddress = 0;
    }

    onProductChange(event: any, dd: any) {
        this.isProductSelected = true;
        this.productTotalInPorts = dd.selectedOption.totalInPorts;
        this.productAvailableInPorts = dd.selectedOption.availableInPorts;
        this.productTotalOutPorts = dd.selectedOption.totalOutPorts;
        this.productavailableOutPorts = dd.selectedOption.availableOutPorts;
        this.getMacAddressList(event);
        this.newFirstMacAddress = 1;
    }

    getNonSerializedProductByDeviceId(): void {
        let deviceId = this.inventoryReplaceForm.value.id;
        const url = `/product/getAllNetworkAndNABindNonSerializedProduct?deviceId=${deviceId}`;
        this.customerInventoryManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.allActiveNonTrackableProducts = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    mylocation() {
        // this.spinner.show()
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(position => {
                if (position) {
                    this.inventoryAssignForm.patchValue({
                        latitude: position.coords.latitude,
                        longitude: position.coords.longitude
                    });
                }
            });
        } else {
            error: (error) => {
                this.toastr.error(`${error.error.ERROR}`, 'Geolocation is not supported by this browser!');
            }

        }
    }


    displayedInventoryListColumns = ['productName', 'macAddress', 'serialNumber', 'qty',
        'approvalStatus', 'assigneeName', 'assignedDateTime', 'expiryDateTime', 'actions'];

    @ViewChild(MatPaginator) paginator: MatPaginator;




    onPaginatorChange(event: any) {
        this.inventoryListDataCurrentPage = event.pageIndex + 1;
        this.inventoryListItemsPerPage = event.pageSize;
        this.getAssignedInventoryList();
    }

    @ViewChild('assignInventoryDialog') assignInventoryDialog!: TemplateRef<any>;
    private dialogRef: MatDialogRef<any> | null = null;

    openDialog(datas?: any) {
        if (!datas || !datas.type) {
            console.error('No data or type provided to openDialog');
            return;
        }
        if (datas.type === 'assignInventory') {
            this.dialogRef = this.dialog.open(this.assignInventoryDialog, {
                width: '1000px',
                maxWidth: '100vw',
                height: 'auto',
                panelClass: 'custom-dialog-container',
                autoFocus: false
            });
            this.dialogRef.afterClosed().subscribe(result => {
                this.dialogRef = null;

                // Reset the form here
                if (this.inventoryAssignForm) {
                    this.inventoryAssignForm.reset();
                }
                this.inventoryAssignForm.patchValue({
                    assignedDateTime: new Date()
                });
                this.getAllNonSerializedProductFlag = false;
                this.getAllSerializedProductFlag = false;
                this.nonSerializedItemAssignFlag = false;
                this.serializedItemAssignFlag = false;
            });
        }
    }


    closeModel() {
        if (this.dialogRef) {
            this.dialogRef.close();
        }
    }


    displayedColumns: string[] = ['select', 'itemId', 'itemType', 'macAddress', 'serialNumber', 'actions'];



}
