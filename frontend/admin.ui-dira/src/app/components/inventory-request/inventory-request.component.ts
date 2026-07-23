import { ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild, TemplateRef } from "@angular/core";
import { Router } from "@angular/router";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, UntypedFormArray, FormGroup } from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import { InventoryRequestService } from "src/app/service/inventory-request.service";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { log } from "console";
import { AbstractControl, ValidatorFn } from "@angular/forms";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { InwardService } from "src/app/service/inward.service";
import { OutwardService } from "src/app/service/outward.service";
import { PartnerService } from "src/app/service/partner.service";
import { ServiceAreaService } from "src/app/service/service-area.service";
import { PopManagementsService } from "src/app/service/pop-managements.service";
import { INVENTORYS } from "src/app/constants/aclConstants";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { ToastrService } from "ngx-toastr";
declare var $: any;
@Component({
    selector: "app-inventory-request",
    templateUrl: "./inventory-request.component.html",
    styleUrls: ["./inventory-request.component.css"],
    standalone: false
})
export class InventoryRequestComponent implements OnInit {
    @ViewChild('inventoryRequestPaginator') inventoryRequestPaginator!: MatPaginator;
    @ViewChild('myReqPaginator') myReqPaginator!: MatPaginator;


    ngAfterViewInit() {
        if (this.dataSource) {
            this.dataSource.paginator = this.inventoryRequestPaginator;
        }
    }

    @ViewChild('inventoryRequestDialog') inventoryRequestDialog!: TemplateRef<any>;
    dataSource = new MatTableDataSource<FormGroup>();
    @ViewChild('inventoryDialogTemplate') inventoryDialogTemplate!: TemplateRef<any>;
    dialogRef!: MatDialogRef<any>;
    displayedColumns: string[] = [
        'requestId',
        'onBehalfOf',
        'requester',
        'requestTo',
        'reason',
        'status',
        'inventoryStatus',
        'action'
    ];



    displayedColumns1: string[] = [
        'requestId',
        'onBehalfOf',
        'requester',
        'requestTo',
        'reason',
        'status',
        'action'
    ];
    @ViewChild("remarks") remarks: ElementRef;
    //rId : any = undefined;
    @ViewChild('approveDialog') approveDialog!: TemplateRef<any>;
    approveDialogRef!: MatDialogRef<any>;
    @ViewChild('rejectDialog') rejectDialog!: TemplateRef<any>;
    rejectDialogRef!: MatDialogRef<any>;
    inventoryRequestFrom: UntypedFormGroup;
    inventoryRequestMappingFrom: UntypedFormGroup;
    approveRequestRemarkForm: UntypedFormGroup;
    rejectRequestRemarkForm: UntypedFormGroup;
    requestId: any;
    inventoryReturntFrom: UntypedFormGroup;
    fileterGlobal: string;
    inventoryRequestFromArray: UntypedFormArray;
    isMyInventoryShow: boolean = true;
    isAssignedInventoryShow: boolean = false;
    submitted: boolean = false;
    inventoryProductMappingSubmitted: boolean = false;
    currentPageReqInventoryProMapping = 1;
    reqInventoryProMappingItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;

    reqInventoryProMappingTotalRecords: number = 0;
    currentPageAllRequestInventoryListdata = 1;
    allRequestInventoryListdataitemsPerPage = RadiusConstants.PER_PAGE_ITEMS;
    allRequestInventoryListdatatotalRecords: any;
    currentPageMyRequestInventoryListdata = 1;
    myRequestInventoryListdataitemsPerPage: number = 5;
    myRequestFulfilInventorydataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    myRequestInventoryListdatatotalRecords: any;
    currentPageViewReqInventoryProMapping = 1;
    currentPagefulfilReqInventoryProMapping = 1;
    totalItemsInventoryReqFulfilment: any;
    viewReqInventoryProMappingItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    viewReqInventoryProMappingTotalRecords: string;
    createReqInventoryData: any;
    createRefundData: any;
    viewReqInventoryData: any;
    onbehalfof: any;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    productList: any = [];
    requesterList: any = [];
    wareHouseData: any = [];
    groupReturnValue: any;
    ifForwardCase: boolean = false;
    filterWareHouseData: any = [];
    allActiveProduct: any = [];
    allRequestInventory: any = [];
    myRequestInventory: any = [];
    productCategoryList: any = [];
    filterProductCategory: any = [];
    approveRequestSubmitted: boolean = false;
    rejectRequestSubmitted: boolean = false;
    viewInventoryRequestModal: boolean = false;
    inventoryRequestModal: boolean = false;
    approveChangeStatusModal: boolean = false;
    rejectChangeStatusModal: boolean = false;
    viewInventoryFulfillmentModal: boolean = false;
    inventoryReturnModal: boolean = false;
    assignInwardForm: UntypedFormGroup;
    editMode: boolean;
    pageSize = 5;
    showItemPerPage: any = 5;
    showAllRequestItemPerPage: any = 5;
    requesterFlag: boolean = false;
    requestToFlag: boolean = false;
    types = [
        { label: "New", value: "New" },
        { label: "Refurbished", value: "Refurbished" },
    ];
    behalfListType = [
        { label: "Warehouse", value: "WareHouse" },
        { label: "Pop", value: "Pop" },
        { label: "Service Area", value: "ServiceArea" },
        { label: "Staff User", value: "StaffUser" },
    ];
    qtyErroMsg: string;
    AclClassConstants;
    AclConstants;
    showQtyError: boolean;
    public loginService: LoginService;
    deleteAccess: boolean = false;
    reisedIntReqAccess: boolean = false;
    fullfillmentAccess: boolean = false;
    forwardToWarehouseAccess: boolean = false;
    assignIntReqAccess: boolean = false;
    outwardFormGroup: UntypedFormGroup;

    sourceType = [
        { label: "Warehouse" },
        { label: "Staff" },
        // { label: "Partner" }
    ];
    destinationType = [
        { label: "Warehouse" },
        { label: "Staff" },
        { label: "Partner" },
        // { label: "POP" },
        // { label: "SA" },
    ];
    warehouses = [];
    destinationStaffList = [];
    productDetailForm: UntypedFormGroup;
    // products:FormArray
    productGroup: UntypedFormGroup;
    isSinglepaymentChecked = false;
    rejectAccess = false;
    approveAccess = false;
    inventoryRequestFromArrayData: FormGroup<any>[];
    reqInventoryProductItemsPerPage: any;

    constructor(
        private toastr: ToastrService,
        private dialog: MatDialog,
        private router: Router,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        public commondropdownService: CommondropdownService,
        private messageService: MessageService,
        private inventoryRequestService: InventoryRequestService,
        loginService: LoginService,
        private inwardService: InwardService,
        private outwardService: OutwardService,
        private partnerService: PartnerService,
        private serviceAreaService: ServiceAreaService,
        private popService: PopManagementsService,
        private cd: ChangeDetectorRef
    ) {
        this.reisedIntReqAccess = loginService.hasPermission(INVENTORYS.RAISED_INVEN_REQUEST);
        this.deleteAccess = loginService.hasPermission(INVENTORYS.INVEN_REQUEST_DELETE);
        this.assignIntReqAccess = loginService.hasPermission(INVENTORYS.ASSIGNED_INVEN_REQUEST);
        this.approveAccess = loginService.hasPermission(INVENTORYS.ASSIGNED_INVEN_REQUEST_APPROVE);
        this.rejectAccess = loginService.hasPermission(INVENTORYS.ASSIGNED_INVEN_REQUEST_REJECT);
        this.forwardToWarehouseAccess = loginService.hasPermission(
            INVENTORYS.ASSIGNED_INVEN_REQUEST_FORWARD
        );
        this.fullfillmentAccess = loginService.hasPermission(
            INVENTORYS.ASSIGNED_INVEN_REQUEST_FULLFILLMENT
        );
        this.loginService = loginService;
        this.inwardService.getAllWareHouse().subscribe((res: any) => {
            this.warehouses = res.dataList;
        });

    }

    ngOnInit(): void {

        this.inventoryRequestFrom = this.fb.group({
            onBehalfOf: ["", Validators.required],
            requestNameId: ["", Validators.required],
            requestToWarehouseId: ["", Validators.required],
            reason: ["", Validators.required],
        });
        this.inventoryReturntFrom = this.fb.group({
            requestInventoryName: ["", Validators.required],
            onBehalfOf: ["", Validators.required],
            requestNameId: ["", Validators.required],
            requestToWarehouseId: ["", Validators.required],
            reason: ["", Validators.required],
            remarks: ["", Validators.required],
            reqId: [""],
        });
        this.productDetailForm = this.fb.group({
            products: this.fb.array([]),
        });
        // this.products = this.fb.array([])
        this.outwardFormGroup = this.fb.group({
            product: [""],
            sourceType: ["", Validators.required],
            source: ["", Validators.required],
            sourceId: [""],
            destinationType: ["", Validators.required],
            destination: ["", Validators.required],
            outwardDateTime: [new Date()],
            mvnoId: [""],
            inwardId: [""],
            usedQty: [0],
            unusedQty: [""],
            ispaymentChecked: [],
            inTransitQty: [""],
            outTransitQty: [""],
            rejectedQty: [""],
            requestInventoryId: [""],
            requestInventoryName: [""],
            requestInventoryProductId: [""],
            selectedItems: [0],
        });

        this.inventoryRequestMappingFrom = this.fb.group({
            productCategoryId: ["", Validators.required],
            productId: ["", Validators.required],
            itemType: ["", Validators.required],
            quantity: ["", [Validators.required, Validators.min(1)]],
            id: [""],
        });

        this.outwardService.getAllStaff().subscribe((res: any) => {
            const staffId = localStorage.getItem("userId");
            // this.staffList = res.staffUserlist;
            this.staffList = res.dataList.filter(element => element.id == staffId);
            this.destinationStaffList = res.dataList;
        });

        this.approveRequestRemarkForm = this.fb.group({
            requestRemark: ["", Validators.required],
        });
        this.rejectRequestRemarkForm = this.fb.group({
            requestRemark: ["", Validators.required],
        });
        this.inventoryRequestFromArray = this.fb.array([]);

        // this.commondropdownService.getActiveProductCategoryList();
        this.getAllActiveProductCategory();
        this.getAllRequestInventoryData("");
        this.getMyRequestInventoryData("");
        this.geetAllWarehouseData();
        this.getAllProduct();
        this.inventoryRequestMappingFrom.get("quantity").valueChanges.subscribe(val => {
            const qty: number = val;
            var letters = /^[A-Za-z]+$/;
            this.qtyErroMsg = "";
            this.showQtyError = false;
            if (val != null) {
                if (String(val).match(letters)) {
                    this.showQtyError = true;
                    this.qtyErroMsg = "Only Numeric value are allowed.";
                } else if (qty < 0) {
                    this.showQtyError = true;
                    this.qtyErroMsg = "Quantity must be greater than 0.";
                } else {
                    this.showQtyError = false;
                }
            }
        });
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(INVENTORYS.INVEN_REQUEST_DELETE) || this.loginService.hasPermission(INVENTORYS.ASSIGNED_INVEN_REQUEST)) {
            return [
                'requestId',
                'onBehalfOf',
                'requester',
                'requestTo',
                'reason',
                'status',
                'inventoryStatus',
                'action'
            ];
        } else {
            return [
                'requestId',
                'onBehalfOf',
                'requester',
                'requestTo',
                'reason',
                'status',
                'inventoryStatus'
            ];
        }
    }

    getAllRequestInventoryData(list) {
        let size: number;
        if (list) {
            size = list;
            this.allRequestInventoryListdataitemsPerPage = list;
        } else {
            size = this.allRequestInventoryListdataitemsPerPage;
        }
        const pageData = {
            page: this.currentPageAllRequestInventoryListdata,
            pageSize: this.allRequestInventoryListdataitemsPerPage,
            sortBy: "id",
            sortOrder: 0,
        };
        const url = "/requestinventory/getAllAssignedRequestInventory";
        this.inventoryRequestService.postMethod(url, pageData).subscribe(
            (response: any) => {
                this.allRequestInventory = response.dataList;
                this.allRequestInventoryListdatatotalRecords = response.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    getMyRequestInventoryData(list) {
        let size: number;
        let page_list = this.currentPageMyRequestInventoryListdata || 1;

        if (list) {
            size = list;
            this.myRequestInventoryListdataitemsPerPage = list;
        } else {
            size = this.myRequestInventoryListdataitemsPerPage;
        }

        const pageData = {
            page: page_list,
            pageSize: size,
            sortBy: "id",
            sortOrder: 0,
        };

        const url = "/requestinventory/getAllByCurrentStaff";
        this.inventoryRequestService.postMethod(url, pageData).subscribe(
            (response: any) => {
                this.myRequestInventory = response.dataList;
                this.myRequestInventoryListdatatotalRecords = response.totalRecords;
            },
            (error: any) => {
                this.myRequestInventory = [];
                this.myRequestInventoryListdatatotalRecords = 0;
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }
    chakedData: any = [];
    // ispaymentChecked=false
    allSelectBatch(event) {
        if (event.checked == true) {
            this.products.controls.forEach((el: any) => {
                el.patchValue({
                    isSinglepaymentChecked: true,
                });
            });
            //  this.ispaymentChecked=true
        }
        if (event.checked == false) {
            this.products.controls.forEach((el: any) => {
                el.patchValue({
                    isSinglepaymentChecked: false,
                });
            });
            //  this.ispaymentChecked=false
        }
    }
    openReqPresent = false;
    submit(dialogRef): void {
        let data: any = [];
        this.submitted = true;
        if (this.outwardFormGroup.valid && !this.showQtyError) {
            data = this.products.value;

            data.forEach((obj: any) => {
                if (obj.isSinglepaymentChecked) {
                    this.openReqPresent = true;
                }
            });
            if (this.openReqPresent) {
                let outwardSaveData = [];
                data.forEach((obj: any, index) => {
                    if (obj.isSinglepaymentChecked && obj.quantity !== null && obj.quantity !== 0) {
                        const outward = {
                            id: "",
                            productId: "",
                            qty: "",
                            outwardDateTime: new Date(),
                            source: "",
                            sourceId: "",
                            sourceType: "",
                            status: "",
                            outwardNumber: "",
                            destinationId: null,
                            destinationType: "",
                            mvnoId: "",
                            usedQty: 0,
                            unusedQty: "",
                            inTransitQty: "",
                            outTransitQty: "",
                            rejectedQty: 0,
                            requestInventoryId: "",
                            requestInventoryProductId: "",
                            selectedItems: 0,
                        };
                        outward.id = "";
                        outward.productId = obj.productId;
                        outward.qty = "";
                        outward.outwardDateTime = this.outwardFormGroup.get("outwardDateTime").value
                            ? this.outwardFormGroup.get("outwardDateTime").value
                            : outward.outwardDateTime;
                        outward.sourceId = this.outwardFormGroup.get("sourceId").value;
                        outward.sourceType = this.outwardFormGroup.get("sourceType").value;
                        outward.status = "ACTIVE";
                        outward.outwardNumber = "";
                        outward.destinationId = this.outwardFormGroup.get("destination").value;
                        outward.destinationType = this.outwardFormGroup.get("destinationType").value;
                        outward.mvnoId = null;
                        outward.usedQty = 0;
                        outward.selectedItems = 0;
                        outward.unusedQty = obj.availableQty;
                        outward.inTransitQty = obj.quantity;
                        outward.outTransitQty = "";
                        outward.rejectedQty = 0;
                        outward.requestInventoryId = this.reqInventoryList[index]?.inventoryRequestId || "";
                        outward.requestInventoryProductId = this.reqInventoryList[index]?.id || "";
                        outwardSaveData.push(outward);
                    }
                }),
                    this.outwardService.saveAllInventoryRequest(outwardSaveData).subscribe(
                        (res: any) => {
                            if (res.responseCode == 406 || res.responseCode == 417) {
                                // this.closefulfillInventoryModal();
                                this.toastr.info(`${res.responseMessage}`, 'Info!');

                            } else if (res.responseCode == 200) {
                                this.submitted = false;
                                this.outwardFormGroup.patchValue({
                                    outwardDateTime: new Date(),
                                });
                                setTimeout(() => {
                                    dialogRef.close()
                                    this.closefulfillInventoryModal();

                                    this.toastr.success(`${res.responseMessage}`, 'Success!');

                                }, 100);
                                this.getAllRequestInventoryData("");

                            }
                        },
                        (error: any) => {
                            this.closefulfillInventoryModal();
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                        }
                    );
            } else {
                this.submitted = false;
                this.closefulfillInventoryModal();
                this.toastr.info("Please select at least one item to assign/ No Open Request present", 'Info!');
                dialogRef.close();

            }
        }
    }
    mapObject(): {} {
        const outwardValues = this.outwardFormGroup.getRawValue();
        const outward = {
            id: "",
            productId: "",
            qty: 0,
            outwardDateTime: "",
            source: "",
            sourceId: "",
            sourceType: "",
            status: "",
            outwardNumber: "",
            destinationId: null,
            destinationType: "",
            mvnoId: "",
            //inwardId: "",
            // isQtyChanged: false,
            usedQty: "",
            unusedQty: "",
            inTransitQty: "",
            outTransitQty: "",
            rejectedQty: "",
            requestInventoryId: "",
            requestInventoryProductId: "",
            selectedItems: "",
        };
        outward.id = outwardValues.id ? outwardValues.id : null;
        // outward.productId = this.;
        outward.qty = outwardValues.qty;
        // outward.isQtyChanged = (outwardValues.qty != this.initialValue);
        outward.status = outwardValues.status;
        outward.outwardDateTime = outwardValues.outwardDateTime;
        outward.sourceId = outwardValues.sourceId;
        outward.sourceType = outwardValues.sourceType;
        outward.outwardNumber = outwardValues.outwardNumber ? outwardValues.outwardNumber : "";
        outward.destinationId = outwardValues.destination;
        outward.destinationType = outwardValues.destinationType;
        outward.mvnoId = null;
        //outward.inwardId = outwardValues.inwardId;
        outward.usedQty = outwardValues.usedQty;
        outward.unusedQty = outwardValues.unusedQty;
        outward.selectedItems = outwardValues.selectedItems;
        outward.inTransitQty = outwardValues.inTransitQty;
        outward.outTransitQty = outwardValues.outTransitQty;
        outward.rejectedQty = outwardValues.rejectedQty;
        outward.requestInventoryId = outwardValues.requestInventoryId;
        outward.requestInventoryProductId = outwardValues.requestInventoryProductId;
        return outward;
    }
    addbatchChecked(ind: number, event) {
        if (!event.value) {
            this.outwardFormGroup.patchValue({
                ispaymentChecked: false,
            });
            // this.cd.markForCheck();
        }

    }
    reqInventoryList = [];
    openRequestFlag = false;
    @ViewChild('viewInventoryFulfillmentModalDialog') viewInventoryFulfillmentModalDialog!: TemplateRef<any>;

    dataforwardToOutwardScreen(dialogRef) {
        dialogRef.close()
        this.viewInventoryRequestModal = false;
        this.viewInventoryFulfillmentModal = true;
        // this.router.navigate(["/home/outwards"], {
        //   queryParams: { id: reqId, mapId: mapId },
        // });
        this.currentPagefulfilReqInventoryProMapping = 1;
        this.products.clear();
        if (this.viewReqInventoryData) {
            this.reqInventoryList = this.viewReqInventoryData.requestInvenotryProductMappings.map(
                (obj: any) => {

                    this.outwardService
                        .getProductAvailableQTY(
                            obj.productId,
                            this.viewReqInventoryData.requestToWarehouseId,
                            "Warehouse"
                        )
                        .subscribe((res: any) => {
                            if (res.dataList[0]?.unusedQty) {
                                obj.availableQty = res.dataList[0].unusedQty;
                                const productGroup = this.fb.group({
                                    isSinglepaymentChecked: [obj.isSinglepaymentChecked],
                                    quantity: [obj.quantity ? Number(obj.quantity) : 0],
                                    productCategoryName: [obj.productCategoryName],
                                    productName: [obj.productName],
                                    availableQty: [obj.availableQty],

                                    requestStatus: [obj.requestStatus],
                                    productId: [obj.productId],
                                });

                                this.products.push(productGroup);
                                this.totalItemsInventoryReqFulfilment = this.products.length;
                                this.dialog.open(this.viewInventoryFulfillmentModalDialog, {
                                    width: '80%',
                                    disableClose: true
                                });
                            } else {
                                obj.availableQty = 0;
                                const productGroup = this.fb.group({
                                    isSinglepaymentChecked: [obj.isSinglepaymentChecked],
                                    quantity: [obj.quantity ? Number(obj.quantity) : 0],
                                    productCategoryName: [obj.productCategoryName],
                                    productName: [obj.productName],
                                    availableQty: [obj.availableQty],

                                    requestStatus: [obj.requestStatus],
                                    productId: [obj.productId],
                                });

                                this.products.push(productGroup);
                                this.totalItemsInventoryReqFulfilment = this.products.length;
                                this.dialog.open(this.viewInventoryFulfillmentModalDialog, {
                                    width: '80%',
                                    disableClose: true
                                });
                            }
                        });

                    return obj;
                }
            );

            // this.reqInventoryList.forEach((reqInventory) => {
            //   const productGroup = this.fb.group({
            //     isSinglepaymentChecked: [reqInventory.isSinglepaymentChecked],
            //     quantity: [reqInventory.quantity],
            //     productCategoryName:[reqInventory.productCategoryName],
            //     productName:[reqInventory.productName],
            //     availableQty:[reqInventory.availableQty],

            //     requestStatus:[reqInventory.requestStatus]

            //   });

            //   this.products.push(productGroup);
            //   console.log('products',this.products)
            // });
            setTimeout(() => {
                let data = this.products.value;
                for (let datus of data) {
                    if (datus.quantity > datus.availableQty || datus.requestStatus !== "Open") {
                        this.openRequestFlag = true;
                        this.cd.markForCheck();
                        break;
                    }
                }
            }, 500);
            this.sources = this.warehouses;
            this.outwardFormGroup.patchValue({
                requestInventoryName: this.viewReqInventoryData.requestInventoryName,
                sourceType: "Warehouse",
                sourceId: this.viewReqInventoryData.requestToWarehouseId,
                source: this.viewReqInventoryData.requestToName,
            });
        }
        if (this.viewReqInventoryData.onBehalfOf === "WareHouse") {
            this.destinationType = [{ label: "Warehouse" }];
        }
        // else if (this.viewReqInventoryData.onBehalfOf === "StaffUser") {
        //   this.destinationType = [{ label: "Staff" }];
        // }
        else {
            this.destinationType = [{ label: "Staff" }];
        }
    }

    get products(): UntypedFormArray {
        return this.productDetailForm.get("products") as UntypedFormArray;
    }
    isDestAStaffOrCustomer = false;
    destinations = [];
    getDestinations(destinationType): void {
        this.outwardFormGroup.controls.destination.setValue("");
        // this.outwardFormGroup.controls.inTransitQty.setValue("");
        const destinationTypeVal = destinationType;
        const sourceTypeVal = this.outwardFormGroup.controls.sourceType.value;
        const sourceVal = this.outwardFormGroup.controls.source.value;
        if (destinationType == "Warehouse") {
            this.isDestAStaffOrCustomer = false;
            this.destinations = this.warehouses;
            const destinationData = this.destinations;
            if (this.viewReqInventoryData.onBehalfOf === "WareHouse") {
                this.destinations = destinationData.filter(
                    item => item.id == this.viewReqInventoryData.requestNameId
                );
            } else if (sourceTypeVal != "" && destinationTypeVal != "") {
                if (sourceTypeVal == destinationTypeVal) {
                    if (sourceVal != "") {
                        this.destinations = destinationData.filter(item => item.name != sourceVal);
                    }
                }
            }
        } else if (destinationType == "Staff") {
            this.isDestAStaffOrCustomer = true;
            this.destinations = this.destinationStaffList.filter(element => element.partnerid == 1);
            const destinationData = this.destinations;
            if (this.viewReqInventoryData.onBehalfOf === "StaffUser") {
                this.destinations = destinationData.filter(
                    item => item.id == this.viewReqInventoryData.requestNameId
                );
            } else if (sourceTypeVal != "" && destinationTypeVal != "") {
                if (sourceTypeVal == destinationTypeVal) {
                    if (sourceVal != "") {
                        this.destinations = destinationData.filter(item => item.id != sourceVal);
                    }
                }
            }
        } else if (destinationType == "Partner") {
            const url = "/partner/getAllTypePartner";
            this.isDestAStaffOrCustomer = false;
            this.partnerService.getMethod(url).subscribe(
                (res: any) => {
                    this.destinations = res.partnerlist;

                    const destinationData = this.destinations;
                    if (sourceTypeVal != "" && destinationTypeVal != "") {
                        if (sourceTypeVal == destinationTypeVal) {
                            if (sourceVal != "") {
                                this.destinations = destinationData.filter(item => item.id != sourceVal);
                            }
                        }
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }
    sources = [];
    isSourceAStaffOrCustomer = false;
    sourceTypeAsStaffFlag = false;
    staffList = [];
    getSources(sourceType): void {
        this.outwardFormGroup.controls.destinationType.setValue("");
        this.outwardFormGroup.controls.destination.setValue("");
        // this.outwardFormGroup.controls.inTransitQty.setValue("");
        // this.availableQty = 0;
        if (sourceType == "Warehouse") {
            this.isSourceAStaffOrCustomer = false;
            this.sourceTypeAsStaffFlag = false;
            this.sources = this.warehouses;
            this.destinationType = [{ label: "Warehouse" }, { label: "Staff" }, { label: "Partner" }];
        } else if (sourceType == "Staff") {
            this.isSourceAStaffOrCustomer = true;
            this.sourceTypeAsStaffFlag = true;
            this.sources = this.staffList;
            this.destinationType = [{ label: "Warehouse" }];
        } else if (sourceType == "Partner") {
            const url = "/partner/allActive";
            this.isSourceAStaffOrCustomer = false;
            this.destinationType = [{ label: "POP" }, { label: "Service Area" }];
            this.outwardService.getMethod(url).subscribe(
                (res: any) => {
                    this.sources = res.dataList;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        } else if (sourceType == "Service Area") {
            const url = "/serviceArea/all";
            this.isSourceAStaffOrCustomer = false;
            this.commondropdownService.getMethodWithCache(url).subscribe(
                (res: any) => {
                    this.sources = res.dataList;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        } else if (sourceType == "POP") {
            const url = "/popmanagement/all";
            this.isSourceAStaffOrCustomer = false;
            this.popService.getMethodWithCache(url).subscribe(
                (res: any) => {
                    this.sources = res.dataList;
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    openInventoryReqModal(id) {
        this.ifForwardCase = true;
        this.getInventoryRequestDetails(id);
    }
    openInventoryRequestDetails(id) {
        this.ifForwardCase = false;
        this.getInventoryRequestDetails(id);
    }

    getInventoryRequestDetails(id) {
        const url = "/requestinventory/getById?id=" + id;
        this.inventoryRequestService.getMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.viewInventoryRequestModal = true;
                    this.viewReqInventoryData = response.data;
                    this.dialog.open(this.inventoryRequestDialog, {
                        width: '1200px'
                    });
                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    myInventoryOpen() {
        this.isMyInventoryShow = true;
        this.isAssignedInventoryShow = false;
        this.getMyRequestInventoryData("");
    }

    assignedInventoryOpen() {
        this.isMyInventoryShow = false;
        this.isAssignedInventoryShow = true;
        this.getAllRequestInventoryData("");
    }

    openRequestInventoryModal() {
        this.inventoryRequestModal = true;



    } @ViewChild('inventoryReturnModalDialog') inventoryReturnModalDialog!: TemplateRef<any>;

    openReturnInventoryModal(data) {

        this.inventoryReturntFrom.patchValue({
            requestInventoryName: data.requestInventoryName,
            onBehalfOf: data.onBehalfOf,
            requestNameId: data.requesterName,
            requestToWarehouseId: data.requestToWarehouseId,
            reason: data.reason,
            reqId: data.id,
        });
        this.dialog.open(this.inventoryReturnModalDialog, {
            width: '80%',
            disableClose: true
        });
        this.inventoryReturnModal = true;
    }
    onCloseDialog() {
        this.dialog.closeAll();
    }
    saveForwardRequest() {

        this.submitted = true;
        if (this.inventoryReturntFrom.valid) {
            let forwardToReqId = this.inventoryReturntFrom.value.requestToWarehouseId;
            let remarks = this.inventoryReturntFrom.value.remarks;
            let reqId = this.inventoryReturntFrom.value.reqId;
            this.createRefundData = [];
            let data = this.createRefundData;
            this.inventoryRequestService
                .forwardToWarehouse(forwardToReqId, remarks, reqId, data)
                .subscribe(
                    (response: any) => {
                        if (response.responseCode == 200) {

                            this.toastr.success(`${response.responseMessage}`, 'Success!');

                            this.closeForwarInventoryModal();
                            this.getAllRequestInventoryData("");
                            this.onCloseDialog();
                            // dialogRef.close()
                            // this.getAllRequestInventoryData();
                            // this.getMyRequestInventoryData();
                        } else {
                            this.toastr.error(`${response.responseMessage}`, 'Failed!');

                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                    }
                );
        }
        // else {
        //   this.messageService.add({
        //     severity: "info",
        //     summary: "info",
        //     detail: "Minimum one Product Details need to add",
        //     icon: "far fa-times-circle",
        //   });
        // }
    }

    closefulfillInventoryModal() {
        this.viewInventoryFulfillmentModal = false;
        while (this.products.controls.length != 0) {
            this.products.removeAt(0);
        }
        // this.products.value.splice(0,this.products.length)
        this.outwardFormGroup.reset();
        this.openReqPresent = false;
        this.openRequestFlag = false;
    }

    closeRequestInventoryModal() {
        this.viewInventoryRequestModal = false;
        this.submitted = false;
        this.inventoryRequestFrom.reset();
        this.inventoryRequestFromArray = this.fb.array([]);
        this.inventoryRequestFromArray.controls = [];
        this.requesterList = [];
        this.inventoryRequestMappingFrom.reset();
        this.inventoryRequestModal = false;
        this.inventoryReturnModal = false;
        this.requestToFlag = false;
        this.requesterFlag = false;
        this.getAllActiveProductCategory();
    }

    closeForwarInventoryModal() {
        this.submitted = false;
        this.inventoryReturntFrom.reset();
        this.inventoryReturnModal = false;
    }

    getProductbyCategory(event) {
        let prodCateId = event.value;
        this.inventoryRequestMappingFrom.controls["productId"].reset();
        const url = "/product/getAllActiveProductsByProductCategoryId?pc_id=" + prodCateId;
        this.inventoryRequestService.getMethod(url).subscribe(
            (response: any) => {
                this.productList = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }
    updateTableData() {
        const startIndex = this.currentPageReqInventoryProMapping * this.reqInventoryProductItemsPerPage;
        const endIndex = startIndex + this.reqInventoryProductItemsPerPage;

        const pagedData = this.inventoryRequestFromArray.controls
            .slice(startIndex, endIndex)
            .map(ctrl => ctrl as FormGroup);

        this.dataSource.data = pagedData;
        if (this.inventoryRequestPaginator) {
            this.dataSource.paginator = this.inventoryRequestPaginator;
        }

        this.cd.detectChanges();
    }
    onAddRequestInventoryProductField() {

        if (this.inventoryRequestMappingFrom.valid) {
            const formGroup = this.fb.group({
                productCategoryId: [this.inventoryRequestMappingFrom.value.productCategoryId],
                productId: [this.inventoryRequestMappingFrom.value.productId],
                itemType: [this.inventoryRequestMappingFrom.value.itemType],
                quantity: [this.inventoryRequestMappingFrom.value.quantity]
            });

            this.inventoryRequestFromArray.push(formGroup);
            this.reqInventoryProMappingTotalRecords = this.inventoryRequestFromArray.length;
            this.inventoryRequestFromArray.controls.forEach((ctrl: FormGroup) => {
                ctrl.get('productCategoryId')?.disable();
                ctrl.get('productId')?.disable();
                ctrl.get('itemType')?.disable();
                ctrl.get('quantity')?.disable();
            });

            this.updateTableData();



            this.inventoryRequestMappingFrom.reset();
            this.dataSource.data = this.inventoryRequestFromArray.controls as FormGroup[];

        }
    }
    // onAddRequestInventoryProductField() {
    //     this.inventoryProductMappingSubmitted = true;
    //     if (this.inventoryRequestMappingFrom.valid) {
    //         this.inventoryRequestFromArray.push(this.reqInventoryProductFormGroup());
    //         console.log(this.reqInventoryProductFormGroup());
    //         this.inventoryRequestMappingFrom.reset();
    //         this.inventoryProductMappingSubmitted = false;
    //     }
    // }

    reqInventoryProductFormGroup(): UntypedFormGroup {
        return this.fb.group({
            productCategoryId: [
                this.inventoryRequestMappingFrom.value.productCategoryId,
                Validators.required,
            ],
            productId: [this.inventoryRequestMappingFrom.value.productId],
            itemType: [this.inventoryRequestMappingFrom.value.itemType],
            quantity: [this.inventoryRequestMappingFrom.value.quantity, Validators.required],
            id: [""],
        });
    }

    saveInventoryRequest() {
        this.submitted = true;
        if (this.inventoryRequestFrom.valid) {
            if (this.inventoryRequestFromArray.controls.length > 0) {
                const url = "/requestinventory/save";
                this.createReqInventoryData = "";
                this.createReqInventoryData = this.inventoryRequestFrom.value;
                this.createReqInventoryData.status = "Pending";
                this.createReqInventoryData.requestInvenotryProductMappings =
                    this.inventoryRequestFromArray.value;
                this.inventoryRequestService.postMethod(url, this.createReqInventoryData).subscribe(
                    (response: any) => {
                        if (response.responseCode == 406) {
                            this.toastr.info(`${response.responseMessage}`, 'Info!');
                        } else if (response.responseCode == 200) {

                            this.toastr.success(`${response.responseMessage}`, 'Success!');

                            this.dialogRef.close();
                            this.closeRequestInventoryModal();
                            this.getAllRequestInventoryData("");
                            this.getMyRequestInventoryData("");
                            this.dataSource.data = [];
                        } else {
                            this.toastr.error(`${response.responseMessage}`, 'Failed!');

                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                    }
                );
            } else {
                this.toastr.info("Minimum one Product Details need to add", 'Info!');

            }
        }
    }



    getRequesterData(event) {
        const data = event.value;
        const url = "/requestinventory/onbehalfoff?onBehalfOf=" + data;
        this.inventoryRequestService.getMethod(url).subscribe(
            (response: any) => {
                this.inventoryRequestFrom.get("requestNameId").reset();
                // this.inventoryRequestFrom.get("requestNameId")     
                // this.inventoryRequestMappingFrom.get('productCategoryId').disable
                this.inventoryRequestFrom.get("requestToWarehouseId").reset();
                this.inventoryRequestMappingFrom.get("productCategoryId").reset();
                this.inventoryRequestMappingFrom.controls["productId"].reset();
                this.inventoryRequestFromArray = this.fb.array([]);
                this.inventoryRequestFromArray.controls = [];
                this.requesterFlag = true;
                this.requestToFlag = false;
                if (event.value == "Pop" || event.value == "ServiceArea") {
                    this.productCategoryList = this.filterProductCategory.filter(
                        item => item.type != "CustomerBind"
                    );
                } else {
                    this.productCategoryList = this.filterProductCategory;
                }
                this.requesterList = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    geetAllWarehouseData() {
        const url = "/requestinventory/getAllWareHouses";
        this.inventoryRequestService.getMethod(url).subscribe(
            (response: any) => {
                this.wareHouseData = response.dataList;
                this.filterWareHouseData = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    reqInventoryProductpageChangedData(event: PageEvent) {
        this.currentPageReqInventoryProMapping = event.pageIndex;
        this.reqInventoryProductItemsPerPage = event.pageSize;
        this.updateTableData();
    }



    // deleteConfirmonReqInventoryProdMapping(
    //     ReqInventoryProductMappingFieldIndex,
    //     ReqInventoryProductMappingFieldId
    // ): void {
    //     this.confirmationService.confirm({
    //         message: "Do you want to delete this Product Mapping?",
    //         header: "Delete Confirmation",
    //         icon: "pi pi-info-circle",
    //         accept: () => {
    //             this.onRemoveReqInventoryProductMapping(
    //                 ReqInventoryProductMappingFieldIndex,
    //                 ReqInventoryProductMappingFieldId
    //             );
    //         },
    //         reject: () => {
    //             this.messageService.add({
    //                 severity: "info",
    //                 summary: "Rejected",
    //                 detail: "You have rejected",
    //             });
    //         },
    //     });
    // }

    // async onRemoveReqInventoryProductMapping(
    //     ReqInventoryProductMappingFieldIndex: number,
    //     ReqInventoryProductMappingFieldId: number
    // ): Promise<void> {
    //     this.inventoryRequestFromArray.removeAt(ReqInventoryProductMappingFieldIndex);
    // }


    deleteConfirmonReqInventoryProdMapping(index: number): void {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete Product Mapping',
                description: 'Are you sure you want to delete this Product Mapping?',
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.inventoryRequestFromArray.removeAt(index);
                this.inventoryRequestFromArrayData = [...(this.inventoryRequestFromArray.controls as FormGroup[])];
                if (this.dataSource) {
                    this.dataSource.data = this.inventoryRequestFromArray.controls as FormGroup[];
                }

                this.dataSource.paginator = this.inventoryRequestPaginator;
                this.reqInventoryProMappingTotalRecords = this.inventoryRequestFromArray.length;

                this.cd.detectChanges();
                // this.updateTableData()
                this.toastr.success("Product Mapping removed successfully.", 'Success!');

            }
        });
    }


    onRemoveReqInventoryProductMapping(
        ReqInventoryProductMappingFieldIndex: number,
        ReqInventoryProductMappingFieldId: number
    ): void {
        this.inventoryRequestFromArray.removeAt(ReqInventoryProductMappingFieldIndex);
        this.toastr.success('Product Mapping has been removed successfully.', 'Success!');

    }


    getAllProduct() {
        const url = "/product/getAllActiveProduct";
        this.inventoryRequestService.getMethod(url).subscribe(
            (response: any) => {
                this.allActiveProduct = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }
    pageChangedAllReqInvList(event: any): void {
        this.currentPageAllRequestInventoryListdata = event;
        // this.allRequestInventoryListdataitemsPerPage = event.pageSize;
        this.getAllRequestInventoryData("");
    }



    TotalItemPerPageAllReqInv(event): void {
        this.showAllRequestItemPerPage = Number(event.value);
        if (this.currentPageAllRequestInventoryListdata > 1) {
            this.currentPageAllRequestInventoryListdata = 1;
        }
        this.getAllRequestInventoryData(this.showAllRequestItemPerPage);
    }

    pageChangedMyReqInvList(event: any): void {
        this.myRequestInventoryListdataitemsPerPage = event.pageSize;
        this.currentPageMyRequestInventoryListdata = event.pageIndex + 1;


        this.getMyRequestInventoryData(this.myRequestInventoryListdataitemsPerPage);
    }

    TotalItemPerPageMyReqInv(event): void {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageMyRequestInventoryListdata > 1) {
            this.currentPageMyRequestInventoryListdata = 1;
        }
        //this.getAllRequestInventoryData();
        this.getMyRequestInventoryData(this.showItemPerPage);
    }
    showItemReqInventory: any;
    TotalItemPerPageMyReqInvFulfil(event) {
        this.showItemReqInventory = Number(event.value);
        if (this.currentPagefulfilReqInventoryProMapping > 1) {
            this.currentPagefulfilReqInventoryProMapping = 1;
        }
        this.getMyRequestFulfilInventoryData(this.showItemReqInventory);
    }

    getMyRequestFulfilInventoryData(list) {
        //
        let size: number;

        this.myRequestFulfilInventorydataitemsPerPage = list
            ? list
            : this.myRequestFulfilInventorydataitemsPerPage;

        // if (list) {

        //    = list;
        // } else {
        //   this.myRequestFulfilInventorydataitemsPerPage=this.myRequestFulfilInventorydataitemsPerPage
        // }
    }
    deleteConfirmonReqInventory(item: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete Inventory Request',
                description: `Are you sure you want to delete this inventory request?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteInventoryRequest(item.id);
            } else {
                this.toastr.info("You cancelled the delete action.", 'Info!');

            }
        });
    }


    deleteInventoryRequest(id) {
        const url = "/requestinventory/delete?id=" + id;
        this.inventoryRequestService.deleteMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');

                    this.getMyRequestInventoryData("");
                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    getSelRequester(event) {
        this.requestToFlag = true;
        if (this.inventoryRequestFrom.controls.onBehalfOf.value == "WareHouse") {
            this.wareHouseData = this.filterWareHouseData.filter(item => item.id != event.value);
        } else {
            this.inventoryRequestFrom.get("requestToWarehouseId").reset();
            this.wareHouseData = this.filterWareHouseData;
        }
    }
    closeApproveInventoryModal() {
        this.approveRequestSubmitted = false;
        this.approveRequestRemarkForm.reset();
        this.approveChangeStatusModal = false;

    }
    closeRejectInventoryModal() {
        this.rejectRequestSubmitted = false;
        this.rejectRequestRemarkForm.reset();
        this.rejectChangeStatusModal = false;

    }

    @ViewChild('approveChangeStatusModalDialog') approveChangeStatusModalDialog!: TemplateRef<any>;
    approveChangeStatus(id) {
        this.requestId = id;
        this.approveChangeStatusModal = true;
        this.approveDialogRef = this.dialog.open(this.approveDialog, {
            width: '40%',
            disableClose: true
        });

    }

    @ViewChild('rejectChangeStatusModalDialog') rejectChangeStatusModalDialog!: TemplateRef<any>;
    rejectChangeStatus(id) {
        this.requestId = id;
        this.rejectChangeStatusModal = true;
        this.rejectDialogRef = this.dialog.open(this.rejectDialog, {
            width: '40%',
            disableClose: true,
        });

    }

    approveRequest() {
        this.approveRequestSubmitted = true;
        if (this.approveRequestRemarkForm.invalid) {
            this.approveRequestRemarkForm.markAllAsTouched();
            return;
        }
        let id = this.requestId;
        const status = "Approve";
        const remarks = this.approveRequestRemarkForm.controls.requestRemark.value;

        const url =
            "/requestinventory/approveStatus?id=" + id + "&status=" + status + "&remarks=" + remarks;
        this.inventoryRequestService.getMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');

                    this.getMyRequestInventoryData("");
                    this.getAllRequestInventoryData("");
                    this.approveDialogRef.close();
                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                }
                this.approveRequestRemarkForm.reset();
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
        // dialogRef.close()
        this.approveDialogRef.close();
        this.approveChangeStatusModal = false;
    }

    rejectRequest() {
        this.rejectRequestSubmitted = true;
        if (this.rejectRequestRemarkForm.invalid) {
            this.rejectRequestRemarkForm.markAllAsTouched();
            return;
        }
        let id = this.requestId;
        const status = "Rejected";
        const remarks = this.rejectRequestRemarkForm.controls.requestRemark.value;

        const url =
            "/requestinventory/approveStatus?id=" + id + "&status=" + status + "&remarks=" + remarks;
        this.inventoryRequestService.getMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.toastr.success(`${response.responseMessage}`, 'Success!');

                    this.getMyRequestInventoryData("");
                    this.getAllRequestInventoryData("");
                    // dialogRef.close()
                    this.rejectDialogRef.close();
                } else {
                    this.toastr.error(`${response.responseMessage}`, 'Failed!');

                }
                this.rejectRequestRemarkForm.reset();
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
        this.rejectChangeStatusModal = false;
    }

    viewfulfilReqInventoryProductpageChangedData(pageNumber) {
        this.currentPagefulfilReqInventoryProMapping = pageNumber;
    }
    viewReqInventoryProductpageChangedData(pageNumber) {
        this.currentPageViewReqInventoryProMapping = pageNumber;
    }
    quantityInValidation(event) {
        var num = String.fromCharCode(event.which);
        if (!/[0-9]/.test(num)) {
            event.preventDefault();
        }
    }
    getAllActiveProductCategory() {
        const url = "/productCategory/getAllActiveProductCategories";
        this.inventoryRequestService.getMethod(url).subscribe(
            (response: any) => {
                this.productCategoryList = response.dataList;
                this.filterProductCategory = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );
    }
    openinventoryDailog() {

        this.dialogRef = this.dialog.open(this.inventoryDialogTemplate, {
            width: '1200px',
            maxWidth: '80vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });

        this.dialogRef.afterClosed().subscribe(() => {
            this.dialogRef = null!;
            this.inventoryRequestFrom.reset();
            this.inventoryRequestMappingFrom.reset();
        });
    }

    onCancel(): void {
        this.dataSource.data = [];
        if (this.dialogRef) {
            this.dialogRef.close();
        }
    }

    activeTabIndex = 0
    onTabChange(event: any) {
        switch (event.index) {
            case 0:
                this.activeTabIndex = 0
                this.myInventoryOpen()
                break;
            case 1:
                this.activeTabIndex = 1
                this.assignedInventoryOpen()
                break;
        }
    }
}
