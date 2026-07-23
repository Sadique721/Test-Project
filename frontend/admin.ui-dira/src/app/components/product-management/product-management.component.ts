import { Component, Inject, OnInit, ViewChild } from "@angular/core";
import { FormGroup, UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { LoginService } from "src/app/service/login.service";
import {
    ITEMS_PER_PAGE,
    PER_PAGE_ITEMS,
    pageLimitOptions
} from "src/app/RadiusUtils/RadiusConstants";
import { ProuctManagementService } from "src/app/service/prouct-management.service";
import { ProductCategoryManagementService } from "src/app/service/product-category-management.service";
import { type } from "os";
import { Regex } from "src/app/constants/regex";
import { Observable, Observer } from "rxjs";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { INVENTORYS } from "src/app/constants/aclConstants";
import { saveAs as importedSaveAs } from "file-saver";
import { MatTable, MatTableDataSource } from "@angular/material/table";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MAT_DIALOG_DATA, MatDialog } from "@angular/material/dialog";
import { MatSnackBar } from "@angular/material/snack-bar";
import { ToastrService } from 'ngx-toastr';
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
declare var $: any;
declare module '@angular/forms' {
    interface FormGroup {
        pick(keys: string[]): FormGroup;
    }
}

FormGroup.prototype.pick = function (keys: string[]): FormGroup {
    const group: { [key: string]: any } = {};

    keys.forEach(k => {
        const control = this.get(k);
        if (control) {
            group[k] = new (control.constructor as any)(control.value, control.validator, control.asyncValidator);
        }
    });

    return new FormGroup(group);
};

@Component({
    selector: "app-product-management",
    templateUrl: "./product-management.component.html",
    styleUrls: ["./product-management.component.css"],
    standalone: false
})
export class ProductManagementComponent implements OnInit {
    @ViewChild(MatSort) sort: MatSort = Object.create(null);
    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);

    displayedColumns = [
        "name",
        "warrantyTime",
        "warrantyTimeUnit",
        "type",
        "status",
        "action"
    ];
    displayedColumnsParam = ["paramName", "paramValue"]
    dataSource = new MatTableDataSource([]);

    productFormGroup: UntypedFormGroup;
    submitted = false;
    allVendor: any = [];
    countryListData: any;
    currentPageProductListdata = 1;
    productListdataitemsPerPage = PER_PAGE_ITEMS;
    productListdatatotalRecords: any;
    productListData: any[] = [];
    viewproductDeviceListData: any = [];
    searchData: any;
    searchProductName: any = "";
    // searchKey: string;
    AclClassConstants: any;
    AclConstants: any;
    showSpecification: boolean = false;
    pageLimitOptions: any = pageLimitOptions;
    showItemPerPage: any = 5;
    viewProductManagementDetails: any;
    searchkey: string;
    isproductDeviceEdit: boolean = false;
    ifSpliterInputShow = false;
    ifSpliterCASDropdownShow = false;
    taxFlag: boolean = false;
    removeFileFlag: boolean = false;
    editMode: boolean;
    status = [
        { label: "Active", value: "ACTIVE" },
        { label: "Inactive", value: "INACTIVE" }
    ];
    taxs: any[] = [];
    charges: any[] = [];
    productCatagorys: any = [];
    vendorId: any = [];
    casePacakeges: any = [];
    // timeUnitData = [
    //   { label: "Day", value: "Day" },
    //   { label: "Month", value: "Month" },
    // ];
    timeUnitData: any[] = [];
    public loginService: LoginService;
    listview: boolean = true;
    createView: boolean = false;
    warrentyError: boolean;
    // thresholdQtyError: boolean;
    // UOM: "";
    newProductRefAmountInWarrantyError: boolean;
    newProductRefAmountPostWarrantyError: boolean;
    refurburshiedProductRefAmountInWarrantyError: boolean;
    refurburshiedProductRefAmountPostWarrantyError: boolean;
    detailView: boolean = false;
    editAccess: boolean = false;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    specificationParametersDTOList: UntypedFormArray;
    specificationParametersDTO: UntypedFormGroup;
    selectedFileUploadPreview: File[] = [];
    selectedFile: any;
    uploadDocForm: UntypedFormGroup;
    isValidFile: boolean = true;
    searchOption: any = "";
    // step1Group: UntypedFormGroup;
    // step2Group: UntypedFormGroup;
    // step3Group: UntypedFormGroup;

    constructor(
        private fb: UntypedFormBuilder,
        private toastr: ToastrService,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private productManagementService: ProuctManagementService,
        loginService: LoginService,
        private productCatagoryService: ProductCategoryManagementService,
        public commondropdownService: CommondropdownService,
        private dialog: MatDialog,
        private snackBar: MatSnackBar
    ) {
        this.createAccess = loginService.hasPermission(INVENTORYS.INVEN_PRODUCT_CREATE);
        this.deleteAccess = loginService.hasPermission(INVENTORYS.INVEN_PRODUCT_DELETE);
        this.editAccess = loginService.hasPermission(INVENTORYS.INVEN_PRODUCT_EDIT);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        // this.editMode = !createAccess && editAccess ? true : false;
        // this.getAllCustomerDirectTypeCharge();
        this.specificationParametersDTOList = this.fb.array([]);
    }

    ngOnInit(): void {
        this.productFormGroup = this.fb.group({
            id: [""],
            name: ["", Validators.required],
            specificationParametersDTOList: this.specificationParametersDTOList,
            productId: [""],
            navLedgerId: [""],
            status: ["", Validators.required],
            productCategory: ["", Validators.required],
            availableInPorts: [""],
            availableOutPorts: [""],
            totalInPorts: [""],
            totalOutPorts: [""],
            vendorId: ["", Validators.required],
            licenseDate: [""],
            filename: [""],
            fileSource: [""],
            description: ["", [Validators.required, Validators.pattern(Regex.characterlength255)]],
            // thresholdQty: [""],
            actualpricenewProduct: [],
            newPrice: [""],
            newProductCharge: [""],
            newProductTax: [""],
            newProductRefAmountInWarranty: [
                "",
                [Validators.required, Validators.pattern(Regex.decimalNumber)]
            ],
            newProductRefAmountPostWarranty: [
                "",
                [Validators.required, Validators.pattern(Regex.decimalNumber)]
            ],
            refurburshiedPrice: [""],
            actualpricerefurbishedProduct: [""],
            refurburshiedProductCharge: [""],
            refurburshiedProductTax: [""],
            refurburshiedProductRefAmountInWarranty: [
                "",
                [Validators.required, Validators.pattern(Regex.decimalNumber)]
            ],
            refurburshiedProductRefAmountPostWarranty: [
                "",
                [Validators.required, Validators.pattern(Regex.decimalNumber)]
            ],
            expiryTime: ["", Validators.required],
            hasOEMConsider: [false],
            hasAssetConsider: [false],
            expiryTimeUnit: ["", Validators.required],
            caseId: [""]

            // refundAmount: [""],
        });
        // this.step1Group = this.fb.group({
        //     name: this.productFormGroup.get('name'),
        //     productCategory: this.productFormGroup.get('productCategory'),
        //     status: this.productFormGroup.get('status'),
        //     vendorId: this.productFormGroup.get('vendorId'),
        //     description: this.productFormGroup.get('description'),
        //     expiryTime: this.productFormGroup.get('expiryTime'),
        //     expiryTimeUnit: this.productFormGroup.get('expiryTimeUnit')
        // });

        // this.step2Group = this.fb.group({
        //     newProductRefAmountInWarranty: this.productFormGroup.get('newProductRefAmountInWarranty'),
        //     newProductRefAmountPostWarranty: this.productFormGroup.get('newProductRefAmountPostWarranty')
        // });

        // this.step3Group = this.fb.group({
        //     refurburshiedProductRefAmountInWarranty: this.productFormGroup.get('refurburshiedProductRefAmountInWarranty'),
        //     refurburshiedProductRefAmountPostWarranty: this.productFormGroup.get('refurburshiedProductRefAmountPostWarranty')
        // });

        this.specificationParametersDTO = this.fb.group({
            defaultValue: [""],
            paramValue: [""],
            id: [""],
            isMandatory: [false],
            mvnoId: [""],
            paramName: [""],
            pcid: [""]
        });

        this.searchData = {
            filter: [
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

        this.productFormGroup.get("expiryTime").valueChanges.subscribe(val => {
            if (val < 0) {
                this.warrentyError = true;
            } else {
                this.warrentyError = false;
            }
        });

        // this.productFormGroup.get("thresholdQty").valueChanges.subscribe(val => {
        //   if (val <= 0 && val != null) {
        //     this.thresholdQtyError = true;
        //   } else {
        //     this.thresholdQtyError = false;
        //   }
        // });

        this.productFormGroup.get("newProductRefAmountInWarranty").valueChanges.subscribe(val => {
            if (val < 0) {
                this.newProductRefAmountInWarrantyError = true;
            } else {
                this.newProductRefAmountInWarrantyError = false;
            }
        });

        this.productFormGroup.get("newProductRefAmountPostWarranty").valueChanges.subscribe(val => {
            if (val < 0) {
                this.newProductRefAmountPostWarrantyError = true;
            } else {
                this.newProductRefAmountPostWarrantyError = false;
            }
        });

        this.productFormGroup
            .get("refurburshiedProductRefAmountInWarranty")
            .valueChanges.subscribe(val => {
                if (val < 0) {
                    this.refurburshiedProductRefAmountInWarrantyError = true;
                } else {
                    this.refurburshiedProductRefAmountInWarrantyError = false;
                }
            });

        this.productFormGroup
            .get("refurburshiedProductRefAmountPostWarranty")
            .valueChanges.subscribe(val => {
                if (val < 0) {
                    this.refurburshiedProductRefAmountPostWarrantyError = true;
                } else {
                    this.refurburshiedProductRefAmountPostWarrantyError = false;
                }
            });

        this.productFormGroup.get("filename")?.valueChanges.subscribe(filename => {
            if (filename) {
                this.productFormGroup.get("licenseDate")?.enable();
            } else {
                this.productFormGroup.get("licenseDate")?.disable();
            }
        });
    }

    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(INVENTORYS.INVEN_PRODUCT_DELETE) || this.loginService.hasPermission(INVENTORYS.INVEN_PRODUCT_EDIT)) {
            return [
                "name",
                "warrantyTime",
                "warrantyTimeUnit",
                "type",
                "status",
                "action"
            ];
        } else {
            return [
                "name",
                "warrantyTime",
                "warrantyTimeUnit",
                "type",
                "status"];
        }
    }
    get step1Group(): FormGroup {
        return this.productFormGroup.pick(['name', 'productCategory', 'status', 'vendorId', 'description', 'expiryTime', 'expiryTimeUnit']);
    }

    get step2Group(): FormGroup {
        return this.productFormGroup.pick(['newProductRefAmountInWarranty', 'newProductRefAmountPostWarranty']);
    }


    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }


    createProduct() {
        this.specificationParametersDTOList = this.fb.array([]);
        this.editMode = false;
        this.listview = false;
        this.createView = true;
        this.submitted = false;
        // this.UOM = "";
        this.productFormGroup.reset();
        this.taxFlag = false;
        this.ifSpliterCASDropdownShow = false;
        this.ifSpliterInputShow = false;
        this.detailView = false;
        this.showSpecification = false;
        this.getTaxDataList();
        this.getAllProductCategory();
        this.getAllCASPackage();
        this.getAllWarrantyTimeUnit();
        this.getAllVendor();
    }

    getProductManagementDetails(id) {
        const url = "/product/" + id;
        this.productManagementService.getMethod(url).subscribe(
            (res: any) => {
                this.viewProductManagementDetails = res.data;
                this.listview = false;
                this.createView = false;
                this.detailView = true;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    optionProductCategoryParameter() {

        const productCategoryId = this.step1Group.value.productCategory;
        const url =
            "/specificationParameters/getSpecificParametersByProductCategoryId?product_category_id=" +
            productCategoryId;
        this.productCatagoryService.getMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode == 406) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "info",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle"
                    // });
                } else {
                    this.specificationParametersDTOList = this.fb.array([]);
                    response.dataList.forEach(element => {
                        let newArray;
                        let listData = this.fb.array([]);
                        if (element.paramMultiValues && element.paramMultiValues.length > 0) {
                            //   newArray = element.paramValues
                            //     .split(",")
                            //     .map(value => ({ label: +value, value: +value }));

                            element.paramMultiValues.forEach(data => {
                                listData.push(
                                    this.fb.group({
                                        value: data,
                                        label: data
                                    })
                                );
                            });
                        }

                        let defaultParamValue = this.fb.control(element.defaultValue);

                        if (element.isMandatory) {
                            defaultParamValue.setValidators([Validators.required]);
                        } else {
                            defaultParamValue.clearValidators();
                        }
                        defaultParamValue.setValidators([Validators.minLength(1), Validators.maxLength(40)]);
                        defaultParamValue.updateValueAndValidity();

                        this.specificationParametersDTOList.push(
                            this.fb.group({
                                defaultValue: defaultParamValue,
                                paramValue: null,
                                // paramValue: [element.paramValue, element.isMandatory ? Validators.required : null],
                                id: [element.id],
                                // identityKey: [element.identityKey],
                                isMandatory: [element.isMandatory],
                                mvnoId: [element.mvnoId],
                                paramName: [element.paramName],
                                pcid: [element.pcid],
                                isMultiValueParam: [element.isMultiValueParam],
                                paramValues: listData
                            })
                        );
                    });
                    this.showSpecification = this.specificationParametersDTOList.value.length > 0;
                }

                // this.macForm.reset();
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    onKeyTotalInPort(e: any) {
        if (this.productFormGroup.value.totalInPorts >= 0) {
            this.productFormGroup.patchValue({
                availableInPorts: this.productFormGroup.value.totalInPorts
            });
        }
    }

    onKeyTotalOutPort(e: any) {
        if (this.productFormGroup.value.totalOutPorts >= 0) {
            this.productFormGroup.patchValue({
                availableOutPorts: this.productFormGroup.value.totalOutPorts
            });
        }
    }

    ProductManagementList() {
        this.listview = true;
        this.createView = false;
        this.detailView = false;
    }

    productListView() {
        this.specificationParametersDTOList = this.fb.array([]);
        this.listview = true;
        this.detailView = false;
        this.createView = false;
        this.searchProductName = "";
        this.searchkey = "";
        this.taxFlag = false;
        this.ifSpliterCASDropdownShow = false;
        this.ifSpliterInputShow = false;
        this.getProductList("");
        this.productFormGroup.reset();
    }

    TotalItemPerPage(event): void {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageProductListdata > 1) {
            this.currentPageProductListdata = 1;
        }
        if (!this.searchkey) {
            this.getProductList(this.showItemPerPage);
        } else {
            this.searchProduct();
        }
    }

    onPageChange(event: PageEvent): void {
        this.currentPageProductListdata = event.pageIndex + 1;
        this.productListdataitemsPerPage = event.pageSize;

        if (!this.searchkey) {
            this.getProductList(this.productListdataitemsPerPage);
        } else {
            this.searchProduct();
        }
    }

    getProductList(list): void {
        this.productListData = [];
        let size: number;
        this.searchkey = "";
        const page = this.currentPageProductListdata;
        if (list) {
            size = list;
            this.productListdataitemsPerPage = list;
        } else {
            size = this.productListdataitemsPerPage;
        }

        const plandata = {
            page,
            pageSize: this.productListdataitemsPerPage
            // filters:filter
        };

        this.productManagementService.getAll(plandata).subscribe(
            (response: any) => {
                this.productListData = response.dataList;
                this.dataSource = new MatTableDataSource(this.productListData);
                this.productListdatatotalRecords = response.totalRecords;
                this.searchkey = "";
            },
            (error: any) => {

                this.toastr.error(`${error.error.ERROR || "Internal Server Error"}`, 'Failed!');


                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    submit(): void {
        Object.keys(this.productFormGroup.controls).forEach(field => {
            const control = this.productFormGroup.get(field);
            control?.markAsTouched({ onlySelf: true });

            if (control?.invalid) {
            } else {
            }
        });
        this.submitted = true;

        if (this.productFormGroup.valid) {
            if (this.editMode) {
                const productValues = this.mapObject();
                let formData = new FormData();
                let newFormData = Object.assign({}, this.productFormGroup.value);
                formData.append("filename", newFormData.fileSource);
                formData.append("productDetailList", JSON.stringify(productValues));

                this.productManagementService.update(formData).subscribe(
                    (res: any) => {
                        if (res.responseCode === 406 || res.responseCode === 417) {
                            this.toastr.info(`${res.responseMessage}`, 'Info!');
                            // this.messageService.add({
                            //     severity: "info",
                            //     summary: "info",
                            //     detail: res.responseMessage,
                            //     icon: "far fa-times-circle"
                            // });
                        } else {
                            this.toastr.success(`${res.responseMessage}`, 'Success!')
                            // this.snackBar.open(res.responseMessage, "Close", {
                            //     duration: 3000,
                            //     horizontalPosition: "end",
                            //     verticalPosition: "top",
                            //     panelClass: ["success-snackbar"]
                            // });
                            this.clearSearchProduct();
                            this.productListView();
                            this.editMode = false;
                            this.submitted = false;
                        }
                    },
                    (error: any) => {

                        this.toastr.error(`${error.error.ERROR || "Internal Server Error"}`, 'Failed!');

                        // this.snackBar.open(error.error.ERROR, "Close", {
                        //     duration: 3000,
                        //     horizontalPosition: "end",
                        //     verticalPosition: "top",
                        //     panelClass: ["error-snackbar"]
                        // });
                    }
                );
            } else {
                const productValues = this.mapObject();
                let formData = new FormData();
                let newFormData = Object.assign({}, this.productFormGroup.value);
                formData.append("filename", newFormData.fileSource);
                formData.append("productDetailList", JSON.stringify(productValues));

                this.productManagementService.save(formData).subscribe(
                    (res: any) => {
                        if (res.responseCode === 406 || res.responseCode === 417) {
                            this.toastr.info(`${res.responseMessage}`, 'Info!')
                            // this.snackBar.open(res.responseMessage, "Close", {
                            //     duration: 3000,
                            //     horizontalPosition: "end",
                            //     verticalPosition: "top",
                            //     panelClass: ["info-snackbar"]
                            // });
                        } else {
                            this.toastr.success(`Successfully`, 'Success!')
                            // this.snackBar.open(res.message, "Close", {
                            //     duration: 3000,
                            //     horizontalPosition: "end",
                            //     verticalPosition: "top",
                            //     panelClass: ["success-snackbar"]
                            // });
                            this.submitted = false;
                            this.clearSearchProduct();
                            this.productListView();
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                        // this.snackBar.open(error.error.ERROR, "Close", {
                        //     duration: 3000,
                        //     horizontalPosition: "end",
                        //     verticalPosition: "top",
                        //     panelClass: ["error-snackbar"]
                        // });
                    }
                );
            }
        }
    }

    mapObject = () => {
        const productValues = this.productFormGroup.getRawValue();
        const product = {
            id: null,
            name: "",
            productId: "",
            navLedgerId: "",
            unit: "",
            description: "",
            // thresholdQty: "",
            productCategory: "",
            status: "",
            // hasMac: false,
            // hasSerial: false,
            newPrice: "",
            actualpricenewProduct: "",
            actualpricerefurbishedProduct: "",
            newProductCharge: "",
            newProductTax: "",
            newProductRefAmountInWarranty: "",
            newProductRefAmountPostWarranty: "",
            refurburshiedPrice: "",
            refurburshiedProductCharge: "",
            refurburshiedProductTax: "",
            refurburshiedProductRefAmountInWarranty: "",
            refurburshiedProductRefAmountPostWarranty: "",
            expiryTime: "",
            hasOEMConsider: this.productFormGroup.value.hasOEMConsider || false,
            hasAssetConsider: this.productFormGroup.value.hasAssetConsider || false,
            expiryTimeUnit: "",
            //refundAmount: "",
            totalInPorts: "",
            totalOutPorts: "",
            availableInPorts: "",
            availableOutPorts: "",
            caseId: "",
            vendorId: "",
            filename: "",
            licenseDate: "",
            specificationParametersDTOList: ""
            // isDeleted:''
        };

        product.id = productValues.id ? productValues.id : null;
        product.productId = productValues.productId;
        product.navLedgerId = productValues.navLedgerId;
        product.unit = productValues.unit;
        product.description = productValues.description;
        // product.thresholdQty = productValues.thresholdQty;
        product.status = productValues.status;
        product.name = productValues.name;
        product.productCategory = productValues.productCategory;
        product.vendorId = productValues.vendorId;
        // product.hasMac = productValues.hasMac;
        // product.hasSerial = productValues.hasSerial;
        product.expiryTime = productValues.expiryTime;
        product.hasOEMConsider = this.productFormGroup.value.hasOEMConsider || false;
        product.expiryTimeUnit = productValues.expiryTimeUnit;
        product.newPrice = productValues.newPrice;
        product.actualpricenewProduct = productValues.actualpricenewProduct;
        product.actualpricerefurbishedProduct = productValues.actualpricerefurbishedProduct;
        product.newProductCharge = productValues.newProductCharge;
        product.newProductTax = productValues.newProductTax;
        product.newProductRefAmountInWarranty = productValues.newProductRefAmountInWarranty;
        product.newProductRefAmountPostWarranty = productValues.newProductRefAmountPostWarranty;
        product.refurburshiedPrice = productValues.refurburshiedPrice;
        product.refurburshiedProductCharge = productValues.refurburshiedProductCharge;
        product.refurburshiedProductTax = productValues.refurburshiedProductTax;
        product.refurburshiedProductRefAmountInWarranty =
            productValues.refurburshiedProductRefAmountInWarranty;
        product.refurburshiedProductRefAmountPostWarranty =
            productValues.refurburshiedProductRefAmountPostWarranty;
        product.totalInPorts = productValues.totalInPorts;
        product.totalOutPorts = productValues.totalOutPorts;
        product.availableInPorts = productValues.availableInPorts;
        product.availableOutPorts = productValues.availableOutPorts;
        product.caseId = productValues.caseId;
        product.filename = productValues.filename?.split("\\").pop();
        product.licenseDate = productValues.licenseDate;
        product.specificationParametersDTOList = this.specificationParametersDTOList.value.map(
            ({ paramValues, ...rest }) => rest
        );
        // product.productCategory.name= productValues.productCategory.name
        return product;
    };

    editProduct(id): void {
        this.showSpecification = true;
        this.createProduct();
        this.editMode = true;
        const productEditData = this.productListData.find(element => element.id === id);
        // this.UOM = productEditData.productCategory.unit;
        this.ifSpliterInputShow = productEditData.productCategory.type == "NA" ? false : true;
        this.ifSpliterCASDropdownShow = productEditData.productCategory.type == "NA" ? false : true;
        if (productEditData?.filename) {
            this.removeFileFlag = true;
        }

        this.productFormGroup.patchValue({
            id: productEditData.id,
            name: productEditData.name,
            productId: productEditData.productId,
            navLedgerId: productEditData.navLedgerId,
            // unit: productEditData.unit,
            productCategory: productEditData.productCategory.id,
            description: productEditData.description,
            // thresholdQty: productEditData.thresholdQty,
            status: productEditData.status,
            vendorId: productEditData.vendorId,
            // hasMac: productEditData.hasMac,
            // hasSerial: productEditData.hasSerial,
            expiryTime: productEditData.expiryTime,
            hasOEMConsider: productEditData.hasOEMConsider,
            hasAssetConsider: productEditData.hasAssetConsider,
            expiryTimeUnit: productEditData.expiryTimeUnit,
            newPrice: productEditData.newPrice,
            actualpricenewProduct: productEditData.actualpricenewProduct,
            newProductCharge: productEditData.newProductCharge,
            newProductTax: productEditData.newProductTax,
            newProductRefAmountInWarranty: Number(productEditData.newProductRefAmountInWarranty),
            newProductRefAmountPostWarranty: Number(productEditData.newProductRefAmountPostWarranty),
            refurburshiedPrice: productEditData.refurburshiedPrice,
            actualpricerefurbishedProduct: productEditData.actualpricerefurbishedProduct,
            refurburshiedProductTax: productEditData.refurburshiedProductTax,
            refurburshiedProductCharge: productEditData.refurburshiedProductCharge,
            refurburshiedProductRefAmountInWarranty: Number(
                productEditData.refurburshiedProductRefAmountInWarranty
            ),
            refurburshiedProductRefAmountPostWarranty: Number(
                productEditData.refurburshiedProductRefAmountPostWarranty
            ),
            totalInPorts: productEditData.totalInPorts,
            totalOutPorts: productEditData.totalOutPorts,
            availableInPorts: productEditData.availableInPorts,
            availableOutPorts: productEditData.availableOutPorts,
            caseId: productEditData.caseId,
            filename: productEditData?.filename,
            file: productEditData?.file,
            licenseDate: productEditData.licenseDate
        });

        if (productEditData.totalInPorts < 0) {
            this.ifSpliterInputShow = false;
        }
        if (productEditData.totalOutPorts < 0) {
            this.ifSpliterInputShow = false;
        }
        if (productEditData.availableInPorts < 0) {
            this.ifSpliterInputShow = false;
        }
        if (productEditData.availableInPorts < 0) {
            this.ifSpliterInputShow = false;
        }
        if (productEditData.caseId == null) {
            this.ifSpliterCASDropdownShow = false;
        }

        this.specificationParametersDTOList = this.fb.array([]);
        productEditData.specificationParametersDTOList?.forEach(element => {
            let newArray;
            let listData = this.fb.array([]);
            if (element.paramValues) {
                // newArray = element.paramValues
                //   .split(",")
                //   .map(value => ({ label: +value, value: +value }));
                element.paramMultiValues.forEach(data => {
                    listData.push(
                        this.fb.group({
                            value: data,
                            label: data
                        })
                    );
                });
            }

            this.specificationParametersDTOList.push(
                this.fb.group({
                    defaultValue: [element.paramValue ? element.paramValue : element.defaultValue],
                    paramValue: [element.paramValue],
                    // paramValue: [element.paramValue, element.isMandatory ? Validators.required : null],
                    id: [element.id],
                    // identityKey: [element.identityKey],
                    isMandatory: [element.isMandatory],
                    mvnoId: [element.mvnoId],
                    paramName: [element.paramName],
                    pcid: [element.pcid],
                    isMultiValueParam: [element.isMultiValueParam],
                    paramValues: listData
                })
            );
            this.showSpecification = this.specificationParametersDTOList.value.length > 0;
        });
    }

    searchProduct(): void {
        if (!this.searchkey || this.searchkey !== this.searchProductName) {
            this.currentPageProductListdata = 1;
        }
        this.searchkey = this.searchProductName;
        if (this.showItemPerPage) {
            this.productListdataitemsPerPage = this.showItemPerPage;
        }
        this.searchData.filter[0].filterValue = this.searchProductName.trim();
        const page = {
            page: this.currentPageProductListdata,
            pageSize: this.productListdataitemsPerPage
        };

        this.productManagementService.searchProduct(page, this.searchData).subscribe(
            (response: any) => {
                if (response.responseCode === 404) {
                    this.toastr.success(`${response.responseMessage}`, 'Success!')
                    // this.snackBar.open(response.responseMessage, "Close", {
                    //     duration: 3000,
                    //     horizontalPosition: "end",
                    //     verticalPosition: "top",
                    //     panelClass: ["info-snackbar"]
                    // });
                    this.productListData = [];
                    this.dataSource = new MatTableDataSource(this.productListData);
                    this.productListdatatotalRecords = 0;
                } else {
                    this.productListData = response.dataList;
                    this.dataSource = new MatTableDataSource(this.productListData);
                    this.productListdatatotalRecords = response.totalRecords;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR || "Internal Server Error"} `, 'Failed!')
                // this.snackBar.open(error.error.ERROR, "Close", {
                //     duration: 3000,
                //     horizontalPosition: "end",
                //     verticalPosition: "top",
                //     panelClass: ["error-snackbar"]
                // });
            }
        );
    }

    clearSearchProduct() {
        this.searchProductName = "";
        this.searchkey = "";
        this.searchOption = "";
        this.getProductList("");
    }

    deleteConfirmProduct(pid: number): void {
        if (pid) {
            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: '400px',
                data: {
                    title: 'Delete Product',
                    description: 'Are you sure you want to delete this Product?',
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.deleteProduct(pid);
                } else {
                    this.toastr.error(`You have rejected`, 'Rejected!');
                    // this.messageService.add({
                    //     severity: 'info',
                    //     summary: 'Rejected',
                    //     detail: 'You have rejected'
                    // });
                }
            });
        }

    }
    deleteProduct(pid): void {
        const url = "/product/delete/" + pid;
        this.productManagementService.deleteMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode === 200) {
                    this.toastr.success(
                        response.message, "success",
                    );
                    this.getProductList("");
                } else {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "info",
                    //     detail: response.responseMessage,
                    //     icon: "far fa-times-circle"
                    // });
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }


    // deleteProduct(id: any): void {
    //     const dialogRef = this.dialog.open(ConfirmDeleteDialogComponent, {
    //         width: "400px",
    //         data: {
    //             title: "Confirm Delete",
    //             message: "Are you sure you want to delete this product?"
    //         }
    //     });

    //     dialogRef.afterClosed().subscribe(result => {
    //         if (result) {
    //             this.productManagementService.delete(id).subscribe(
    //                 (res: any) => {
    //                     this.toastr.success(`${res.responseMessage}`, 'Success!')
    //                     // this.snackBar.open(res.responseMessage, "Close", {
    //                     //     duration: 3000,
    //                     //     horizontalPosition: "end",
    //                     //     verticalPosition: "top",
    //                     //     panelClass: ["success-snackbar"]
    //                     // });
    //                     this.getProductList("");
    //                 },
    //                 (error: any) => {
    //                     this.toastr.error(`Failed to Delete`, 'Failed!')
    //                     // this.snackBar.open(error.error.ERROR, "Close", {
    //                     //     duration: 3000,
    //                     //     horizontalPosition: "end",
    //                     //     verticalPosition: "top",
    //                     //     panelClass: ["error-snackbar"]
    //                     // });
    //                 }
    //             );
    //         }
    //     });
    // }









    getTaxDataList() {
        const url = "/taxes/all";
        this.commondropdownService.getMethod(url).subscribe(
            (response: any) => {
                this.taxs = response.taxlist;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR || "Internal Server Error"}`, 'Failed!');
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    getAllProductCategory(): void {
        const url = "/productCategory/getAllActiveProductCategories";
        this.productCatagoryService.getMethod(url).subscribe(
            (response: any) => {
                this.productCatagorys = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR || "Internal Server Error"}`, 'Failed!');

                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }
    getAllCASPackage(): void {
        const url = "/casepackage/all";
        this.commondropdownService.getMethod(url).subscribe(
            (response: any) => {
                this.casePacakeges = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR || "Internal Server Error"}`, 'Failed!');
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }

    getAllWarrantyTimeUnit(): void {
        const url = "/commonList/generic/warrantyTimeUnit";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.timeUnitData = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR || "Internal Server Error"}`, 'Failed!');
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }


    getAllVendor(): void {
        const url = "/vendor/findAll";
        this.productCatagoryService.getMethod(url).subscribe(
            (response: any) => {
                this.allVendor = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR || "Internal Server Error"}`, 'Failed!');
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }
    removeFile() {
        this.productFormGroup.controls.filename.setValue('');
        this.removeFileFlag = false;
    }

    onFileChange(event: any) {
        if (event.target.files.length > 0) {
            const file = event.target.files[0];

            this.productFormGroup.patchValue({
                fileSource: file,
                filename: event.target.value
            });
            this.removeFileFlag = true;
        }
    }

    canExit() {
        if (!this.productFormGroup.dirty) {
            return true;
        }
        return Observable.create((observer: Observer<boolean>) => {

            const dialogRef = this.dialog.open(ConfirmExitDialogComponent, {
                width: "400px",
                data: {
                    title: "Alert",
                    description: "The filled data will be lost. Do you want to continue?"
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                observer.next(result);
                observer.complete();
            });
        });
    }

    applyFilter(event: Event) {
        const filterValue = (event.target as HTMLInputElement).value;
        this.dataSource.filter = filterValue.trim().toLowerCase();

        if (this.dataSource.paginator) {
            this.dataSource.paginator.firstPage();
        }
    }
    onFirstNext() {

    }
}

// Confirm Delete Dialog Component
@Component({
    selector: "app-confirm-delete-dialog",
    template: `
    <h2 mat-dialog-title>{{ data.title }}</h2>
    <mat-dialog-content>
      <p>{{ data.message }}</p>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button [mat-dialog-close]="false">No</button>
      <button mat-raised-button color="warn" [mat-dialog-close]="true">Yes</button>
    </mat-dialog-actions>
  `,
    standalone: false
})
export class ConfirmDeleteDialogComponent {
    constructor(@Inject(MAT_DIALOG_DATA) public data: any) { }
}

// Confirm Exit Dialog Component
@Component({
    selector: "app-confirm-exit-dialog",
    template: `
   <div class="dialog-container">
    <div class="dialog-header dialog-header-titleFont">
        {{ data.title }}
        <span class="w-h-18">
            <a mat-icon-button href="javascript:void(0)" [mat-dialog-close]="false">
                 <img src="assets/img/DashboardIcons/Reject.svg">
            </a>
        </span>
    </div>
    <div mat-dialog-content>
        <p class="dialog-description">{{ data.description }}</p>
        <div mat-dialog-actions class="p-0">
            <button mat-flat-button type="button" class="primary" [mat-dialog-close]="true">
               Yes
            </button>
            <button mat-stroked-button matButton="outlined" color="primary" [mat-dialog-close]="false">
                No
            </button>
        </div>
    </div>
</div>
  `,
    standalone: false
})
export class ConfirmExitDialogComponent {
    constructor(@Inject(MAT_DIALOG_DATA) public data: any) { }
}

