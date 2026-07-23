import { url } from "inspector";
import { Component, DebugElement, OnInit, AfterViewInit, TemplateRef, ViewChild } from "@angular/core";
import {
    UntypedFormBuilder,
    UntypedFormArray,
    UntypedFormGroup,
    Validators,
    FormControl,
    AbstractControl,
    FormGroup
} from "@angular/forms";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { ToastrService } from 'ngx-toastr'; // Import ToastrService instead of MessageService
import { LoginService } from "src/app/service/login.service";
import * as fs from "fs";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { ITEMS_PER_PAGE, pageLimitOptions } from "src/app/RadiusUtils/RadiusConstants";
import { ProductCategoryManagementService } from "src/app/service/product-category-management.service";
import { Observable, Observer } from "rxjs";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { HttpClient } from "@angular/common/http";
declare var $: any;
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { rest } from "lodash";
import { CheckboxModule } from "primeng/checkbox";
import { ResponseData } from "./../radius-role/base-save-update-response";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { INVENTORYS } from "src/app/constants/aclConstants";
import { MatTable } from '@angular/material/table';

@Component({
    selector: "app-product-category-management",
    templateUrl: "./product-category-management.component.html",
    styleUrls: ["./product-category-management.component.css"],
    standalone: false
})
export class ProductCategoryManagementComponent implements OnInit {
    specificationParametersDataSource =
        new MatTableDataSource<AbstractControl>();

    productDataSource: MatTableDataSource<any> = new MatTableDataSource<any>();
    @ViewChild(MatTable) table!: MatTable<any>;
    displayedColumns = ['id', 'name', 'unit', 'status', 'type', 'action'];
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild('createEditProductCategoryDialog') createEditProductCategoryDialog: TemplateRef<any>;
    @ViewChild('productCategoryDetailDialog') productCategoryDetailDialog: TemplateRef<any>;
    @ViewChild('specificationListDialog') specificationListDialog: TemplateRef<any>;
    dialogRef: MatDialogRef<any>;
    specificationListDialogRef: MatDialogRef<any>;
    @ViewChild('productDetailPaginator') productDetailPaginator: MatPaginator;
    productDetailDataSource: MatTableDataSource<any> = new MatTableDataSource<any>();
    paramName = "";
    isMandatory: boolean = false;
    showTable: boolean = false;
    showSearchBar: boolean = true;
    parameterList: any;
    AclClassConstants;
    AclConstants;
    public loginService: LoginService;
    specificationListDataSource: MatTableDataSource<AbstractControl> = new MatTableDataSource<AbstractControl>();
    productCategoryFormGroup: UntypedFormGroup;
    specificationParametersDTO: UntypedFormGroup;
    specificationParametersDTOList: UntypedFormArray;
    addParamForm: UntypedFormGroup;
    submitted = false;
    countryListData: any;
    currentPageProductListdata = 1;
    duplicateErrorMessage: string = "";
    productListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    productListdatatotalRecords: any;
    productListData: any[] = [];
    searchData: any;
    searchProductCatName: any = "";
    pageLimitOptions = pageLimitOptions;
    showItemPerPage: any = 5;
    viewProductCategoryDetails: any;
    searchkey: string;
    editMode: boolean;
    checked: boolean = true;
    productDeatilItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    productPageChargeDeatilList = 1;
    productDeatiltotalRecords: 0;

    status = [
        { label: "Active", value: "ACTIVE" },
        { label: "Inactive", value: "INACTIVE" }
    ];

    productCatType: any[] = [];
    createView: boolean = false;
    listView: boolean = true;
    detailView: boolean = false;
    uomType: any[] = [];
    dtvCategory: any[] = [];
    timeUnitData = [
        { label: "Day", value: "Day" },
        { label: "Month", value: "Month" }
    ];

    inputTypeList = [
        { label: "Input", value: "Input" },
        { label: "List", value: "List" }
    ];
    ifDTVCateShow = false;
    isEditService: boolean = false;
    mandatoryList: any;
    mvnoId: number;
    myForm: UntypedFormGroup;
    showDelete: boolean = false;
    editedValue: string | null = null;
    isEditMode: boolean;
    searchOptionSelect = this.commondropdownService.productCategorySearchOption;
    searchProductCat: any = "";
    editAccess: boolean = false;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    specificationListForm: UntypedFormGroup;
    showSpecificationListPopup: boolean = false;
    isFromList: boolean = false;
    currentIndexForEditList: any;
    specificationListArray = this.fb.array([]);
    specificationListData: any = [];
    isButtonDisabled: boolean;
    addValueSubmit: boolean = false;
    networkDeviceData: any = [];
    selectedTypes: any[] = [];
    isDeviceTypeVisible: boolean = false;
    // specificationParametersDataSource: MatTableDataSource<any> = new MatTableDataSource<any>();

    constructor(
        private formBuilder: UntypedFormBuilder,
        private http: HttpClient,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private toastr: ToastrService, // Replace MessageService with ToastrService
        private dialog: MatDialog,
        private ProductCategoryManagementService: ProductCategoryManagementService,
        loginService: LoginService,
        public commondropdownService: CommondropdownService
    ) {
        this.createAccess = loginService.hasPermission(INVENTORYS.PRODUCT_CATEGORY_CREATE);
        this.deleteAccess = loginService.hasPermission(INVENTORYS.PRODUCT_CATEGORY_DELETE);
        this.editAccess = loginService.hasPermission(INVENTORYS.PRODUCT_CATEGORY_EDIT);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;

        this.specificationParametersDTOList = this.formBuilder.array([]);
    }

    ngAfterViewInit(): void {
        this.productDataSource.paginator = this.paginator;
        this.productDataSource.sort = this.sort;
        this.paginator.page.subscribe((event: PageEvent) => {
            this.pageChangedProductList(event);
        });
    }

    ngOnInit(): void {
        this.specificationParametersDTO = this.formBuilder.group({
            inputType: ['',],
            paramName: ['',],
            // isMandatory: [false]
        });
        this.specificationParametersDTO.valueChanges.subscribe(() => {
            this.updateButtonStates();
        });
        this.specificationListForm = this.fb.group({
            value: ["", [Validators.required, Validators.maxLength(40)]]
        });
        this.specificationListForm.valueChanges.subscribe(() => {
            this.updateButtonStates();
        });
        this.productCategoryFormGroup = this.formBuilder.group({
            id: [""],
            name: ["", Validators.required],
            productId: [""],
            status: ["", Validators.required],
            type: ["", Validators.required],
            unit: ["", Validators.required],
            deviceType: [""],
            specificationParametersDTOList: this.formBuilder.array([]),
            hasMac: [false],
            hasSerial: [false],
            hasTrackable: [false],
            hasPort: [false],
            hasCas: [false],
            expiryTime: [""],
            expiryTimeUnit: [""],
            dtvCategory: [""]
        });
        this.getAllDTVCategory();
        this.getAllUOMType();
        this.getAllProductType();
        this.specificationParametersDTO.get("isMandatory")?.valueChanges.subscribe(value => {
            this.isMandatory = value;
        });
        this.specificationParametersDTOList = this.productCategoryFormGroup.get(
            "specificationParametersDTOList"
        ) as UntypedFormArray;
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
    }
    updateSpecificationListTable(): void {
        // This creates a new reference from the FormArray controls, forcing the MatTable to refresh.
        this.specificationListDataSource.data = this.specificationListArray.controls;
    }

    initializeProductDetailDataSource(): void {
        if (this.viewProductCategoryDetails?.specificationParametersDTOList) {
            this.productDeatiltotalRecords = this.viewProductCategoryDetails.specificationParametersDTOList.length;
            this.updateProductDetailPagination();
        }
    }

    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(INVENTORYS.PRODUCT_CATEGORY_DELETE) || this.loginService.hasPermission(INVENTORYS.PRODUCT_CATEGORY_EDIT)) {
            return ['id', 'name', 'unit', 'status', 'type', 'action'];
        } else {
            return ['id', 'name', 'unit', 'status', 'type'];
        }
    }
    updateButtonStates(): void {
        setTimeout(() => {
            this.isButtonDisabled = this.shouldDisableListButton();
        });
    }



    openDetailDialog(productId: any) {
        this.ProductCategoryManagementService.getMethod(`/productCategory/${productId}`).subscribe(
            (res: any) => {
                this.viewProductCategoryDetails = res.data;

                this.initializeProductDetailDataSource();

                this.dialogRef = this.dialog.open(this.productCategoryDetailDialog, {
                    width: '1100px',
                    disableClose: true
                });

                this.dialogRef.afterOpened().subscribe(() => {
                    setTimeout(() => {
                        this.updateProductDetailPagination();
                    });
                });
            },
            (error) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    onCancel(): void {
        if (this.dialogRef) {
            this.dialogRef.close();
        }
    }

    deleteConfirmProductDialog(product: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete Product Category',
                description: `Are you sure you want to delete "${product.name}"?`,
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

    openCreateEditDialog(editMode: boolean, productId?: number): void {
        this.editMode = editMode;

        if (editMode && productId) {
            const productEditData = this.productListData.find(element => element.id === productId);
            if (productEditData) {
                this.patchProductCategoryForm(productEditData, editMode);
                this.setConditionControlsState(true);
            }
        } else {
            this.setConditionControlsState(false);
            this.productCategoryFormGroup.reset();
            this.specificationParametersDTOList.clear();
            this.showTable = false;
            this.isDeviceTypeVisible = false;
            this.specificationParametersDTO.reset({
                inputType: '',
                paramName: '',
                isMandatory: false
            });
            this.createProductCategory()
            this.listView = true;
        }

        this.dialogRef = this.dialog.open(this.createEditProductCategoryDialog, {
            width: '1100px',
            disableClose: true,
        });

        this.dialogRef.afterClosed().subscribe(result => {
            if (result === 'saved') {
                this.clearSearchProduct();
                this.listView = true;
                this.createView = false;
                this.detailView = false;
            }
        });
    }

    patchProductCategoryForm(productEditData: any, flag): void {
        this.specificationParametersDTOList.clear();
        this.isEditMode = flag;

        if (productEditData.specificationParametersDTOList && productEditData.specificationParametersDTOList.length > 0) {
            productEditData.specificationParametersDTOList.forEach((param: any) => {
                let listData = this.fb.array([]);
                let isMultiValueParam = false;

                if (param.isMultiValueParam && param.paramMultiValues && Array.isArray(param.paramMultiValues)) {
                    isMultiValueParam = true;
                    param.paramMultiValues.forEach((data: any) => {
                        const value = typeof data === 'string' ? data : data.value;
                        if (value) {
                            listData.push(this.formBuilder.group({
                                value: [value,]
                            }));
                        }
                    });
                }

                const newParameter = this.formBuilder.group({
                    id: [param.id],
                    paramName: [param.paramName,],
                    isMandatory: [param.isMandatory || false],
                    isEditing: [false],
                    isNew: [false],
                    isMultiValueParam: [isMultiValueParam],
                    paramMultiValues: listData,
                    inputType: [isMultiValueParam ? 'List' : 'Input']
                });

                if (this.editMode) {
                    newParameter.get('paramName')?.disable();
                    newParameter.get('isMandatory')?.disable();
                }

                this.specificationParametersDTOList.push(newParameter);
                this.specificationParametersDataSource.data = this.specificationParametersDTOList.controls;
            });

            this.showTable = true;
        } else {
            this.showTable = false;
        }

        this.isDeviceTypeVisible = productEditData.type && productEditData.type.includes("NetworkBind");

        this.productCategoryFormGroup.patchValue({
            id: productEditData.id,
            name: productEditData.name,
            productId: productEditData.productId,
            unit: productEditData.unit || '',
            type: productEditData.type,
            status: productEditData.status,
            hasMac: productEditData.hasMac || false,
            hasSerial: productEditData.hasSerial || false,
            hasTrackable: productEditData.hasTrackable || false,
            hasPort: productEditData.hasPort || false,
            hasCas: productEditData.hasCas || false,
            dtvCategory: productEditData.dtvCategory || '',
            deviceType: productEditData.deviceType || ''
        });

        this.specificationParametersDTO.reset({
            inputType: '',
            paramName: '',
            isMandatory: false
        });

        if (this.editMode) {
            this.productCategoryFormGroup.get('unit')?.disable();
            this.productCategoryFormGroup.get('type')?.disable();
            this.productCategoryFormGroup.get('deviceType')?.disable();
            this.productCategoryFormGroup.get('hasMac')?.disable();
            this.productCategoryFormGroup.get('hasSerial')?.disable();
            this.productCategoryFormGroup.get('hasTrackable')?.disable();
            this.productCategoryFormGroup.get('hasPort')?.disable();
            this.productCategoryFormGroup.get('hasCas')?.disable();

            this.specificationParametersDTO.enable();
        } else {
            this.productCategoryFormGroup.get('unit')?.enable();
            this.productCategoryFormGroup.get('type')?.enable();
            this.productCategoryFormGroup.get('deviceType')?.enable();
            this.productCategoryFormGroup.get('hasMac')?.enable();
            this.productCategoryFormGroup.get('hasSerial')?.enable();
            this.productCategoryFormGroup.get('hasTrackable')?.enable();
            this.productCategoryFormGroup.get('hasPort')?.enable();
            this.productCategoryFormGroup.get('hasCas')?.enable();

            this.specificationParametersDTO.enable();
        }

        this.productCategoryFormGroup.updateValueAndValidity();
    }

    setConditionControlsState(isEditMode: boolean) {
        if (isEditMode) {
            this.productCategoryFormGroup.get('unit').disable();
            this.productCategoryFormGroup.get('type').disable();
            this.productCategoryFormGroup.get('deviceType').disable();
            this.productCategoryFormGroup.get('hasMac').disable();
            this.productCategoryFormGroup.get('hasSerial').disable();
            this.productCategoryFormGroup.get('hasTrackable').disable();
            this.productCategoryFormGroup.get('hasPort').disable();
            this.productCategoryFormGroup.get('hasCas').disable();
        } else {
            this.productCategoryFormGroup.get('unit').enable();
            this.productCategoryFormGroup.get('type').enable();
            this.productCategoryFormGroup.get('deviceType').enable();
            this.productCategoryFormGroup.get('hasMac').enable();
            this.productCategoryFormGroup.get('hasSerial').enable();
            this.productCategoryFormGroup.get('hasTrackable').enable();
            this.productCategoryFormGroup.get('hasPort').enable();
            this.productCategoryFormGroup.get('hasCas').enable();
        }
    }

    createProductCategory() {
        this.specificationParametersDTOList.clear();
        this.productCategoryFormGroup.value.specificationParametersDTOList = [];
        this.productCategoryFormGroup.value.specificationParametersDTOList = "";
        this.showTable = this.productCategoryFormGroup.value.specificationParametersDTOList.length > 0;
        this.showSearchBar = false;
        this.editMode = false;
        this.listView = false;
        this.detailView = false;
        this.createView = true;
        this.submitted = false;
        this.productCategoryFormGroup.reset();
        this.productCategoryFormGroup.get("specificationParametersDTOList").reset([]);
        this.productCategoryFormGroup.reset();
        this.showDelete = true;
        this.isDeviceTypeVisible = false;
        this.productCategoryFormGroup.get("deviceType").clearValidators;
        this.productCategoryFormGroup.get("deviceType").updateValueAndValidity();
        this.specificationListArray = this.fb.array([]);
        this.getAllDTVCategory();
        this.commonGenericData();
        this.getAllProductType();
        this.getAllUOMType();
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

    TotalItemPerPageParameter(event: any): void {
        this.productDeatilItemPerPage = Number(event.value);
        if (this.productDetailPaginator) {
            this.productDetailPaginator.pageSize = this.productDeatilItemPerPage;
            this.productDetailPaginator.firstPage();
        }
    }

    getProductListt(pageSize: number): void {
        this.productListData = [];
        this.searchkey = "";
        const page = this.currentPageProductListdata;
        const plandata = {
            page,
            pageSize
        };
        this.ProductCategoryManagementService.getAll(plandata).subscribe(
            (response: any) => {
                this.productListData = response.dataList;
                this.productListdatatotalRecords = response.totalRecords;
                this.productDataSource.data = response.dataList;
                if (this.paginator) {
                    this.paginator.pageSize = pageSize;
                    this.paginator.length = this.productListdatatotalRecords;
                    this.paginator.pageIndex = page - 1;
                }
                this.showItemPerPage = pageSize;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
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
        };
        this.ProductCategoryManagementService.getAll(plandata).subscribe(
            (response: any) => {
                this.productListData = response.dataList;
                this.productListdatatotalRecords = response.totalRecords;
                this.productDataSource.data = response.dataList;
                if (this.paginator) {
                    setTimeout(() => {
                        this.paginator.length = this.productListdatatotalRecords;
                        this.paginator.pageSize = this.productListdataitemsPerPage;
                        this.paginator.pageIndex = this.currentPageProductListdata - 1;
                    });
                }
                this.searchkey = "";
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    deleteRaw(index: number) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete Parameter',
                description: 'Are you sure you want to delete this row?',
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.showDelete = true;

                const parametersArray = this.productCategoryFormGroup.get(
                    'specificationParametersDTOList'
                ) as UntypedFormArray;

                if (index >= 0 && index < parametersArray.length) {
                    parametersArray.removeAt(index);
                }

                this.showTable = parametersArray.length > 0;
            }
            this.updateParameterListValues();
        });
    }


    editRaw(item: FormGroup): void {


        this.specificationParametersDataSource.data.forEach((row: FormGroup) => {
            row.patchValue({ isEditing: false });
            row.get('paramName')?.disable();
            row.get('isMandatory')?.disable();
        });


        item.get('paramName')?.enable();
        item.get('isMandatory')?.enable();


        item.patchValue({
            isEditing: true
        });
    }

    saveChanges(item: any) {
        if (item.invalid) {
            return;
        }
        item.patchValue({
            isEditing: false
        });
    }

    checkForDuplicates(paramName: string): void {
        if (!paramName || paramName.trim() === "") {
            this.duplicateErrorMessage = "";
            return;
        }

        const trimmedParamName = paramName.trim();
        const paramArray = this.productCategoryFormGroup.get('specificationParametersDTOList') as UntypedFormArray;

        const isDuplicate = paramArray.controls.some(control => {
            const existingName = control.get('paramName')?.value?.trim();
            return existingName === trimmedParamName;
        });

        if (isDuplicate) {
            this.duplicateErrorMessage = "Duplicate Parameter found";
        } else {
            this.duplicateErrorMessage = "";
        }
    }

    saveParameterChanges(item: FormGroup): void {

        item.patchValue({
            isEditing: false
        });

        if (this.editMode && !item.get('isNew')?.value) {
            item.get('paramName')?.disable();
            item.get('isMandatory')?.disable();
        }
    }


    get specificationParameters() {
        return (this.productCategoryFormGroup.get('specificationParametersDTOList') as UntypedFormArray).controls;
    }

    saveParameter(): void {

        this.submitted = true;



        if (this.specificationParametersDTO.invalid) {
            Object.values(this.specificationParametersDTO.controls).forEach(control => {
                control.markAsTouched();
                control.markAsDirty();
                control.updateValueAndValidity();
            });

            this.toastr.error('Please fill all required fields correctly', 'Failed!');
            return;
        }

        const paramName = this.specificationParametersDTO.get('paramName').value.trim();
        const inputType = this.specificationParametersDTO.get('inputType').value;
        // const isMandatory = this.specificationParametersDTO.get('isMandatory').value || false;

        if (this.isDuplicate(paramName)) {
            this.duplicateErrorMessage = "Parameter name already exists";
            this.toastr.error('This parameter name already exists in the list', 'Failed!');
            return;
        }

        const paramArray = this.productCategoryFormGroup.get('specificationParametersDTOList') as UntypedFormArray;

        try {
            if (inputType === 'Input') {
                paramArray.push(
                    this.formBuilder.group({
                        id: [null],
                        paramName: [paramName,],
                        isMandatory: this.specificationParametersDTO.value.isMandatory || false,
                        isEditing: [false],
                        isNew: [true],
                        isMultiValueParam: [false],
                        paramMultiValues: [null],
                        inputType: ['Input']
                    })
                );
            } else if (inputType === 'List') {
                if (this.specificationListData.length === 0) {
                    this.toastr.error('Please add at least one value to the list', 'Failed!');
                    return;
                }

                const listData = this.fb.array([]);
                this.specificationListData.forEach(data => {
                    listData.push(this.formBuilder.group({
                        value: [data.value, [Validators.required, Validators.maxLength(40)]]
                    }));
                });

                paramArray.push(
                    this.formBuilder.group({
                        id: [null],
                        paramName: [paramName,],
                        isMandatory: this.specificationParametersDTO.value.isMandatory || false,
                        isEditing: [false],
                        isNew: [true],
                        paramMultiValues: listData,
                        isMultiValueParam: [true],
                        inputType: ['List']
                    })
                );
                this.toastr.success('Parameter added successfully', 'Success!');

                this.specificationListData = [];
                this.specificationListArray.clear();
            }
            this.specificationParametersDataSource.data = paramArray.controls;
            this.showTable = paramArray.length > 0;

            this.specificationParametersDTO.reset({
                inputType: '',
                paramName: '',
                isMandatory: false
            });

            this.duplicateErrorMessage = '';
            this.submitted = false;


        } catch (error) {
            this.toastr.error('Failed to add parameter', 'Failed!');
        }
    }

    getParamMultiValuesLength(item: any): number {
        const paramMultiValues = item.get('paramMultiValues');
        if (paramMultiValues && paramMultiValues instanceof UntypedFormArray) {
            return paramMultiValues.length;
        }
        return 0;
    }

    isDuplicate(paramName: string): boolean {
        const trimmedParamName = paramName.trim();
        return this.specificationParametersDTOList.controls.some(
            control => control.get("paramName").value.trim() === trimmedParamName
        );
    }

    debugFormStructure(): void {
        const parametersArray = this.productCategoryFormGroup.get('specificationParametersDTOList') as UntypedFormArray;

        parametersArray.controls.forEach((control: AbstractControl, index: number) => {
            const paramData = control.getRawValue();
        });
    }

    submit(): void {
        this.submitted = true;
        this.debugFormStructure();

        if (this.productCategoryFormGroup.valid) {
            const saveOrUpdate$ = this.editMode
                ? this.ProductCategoryManagementService.update(this.mapObject())
                : this.ProductCategoryManagementService.save(this.mapObject());

            saveOrUpdate$.subscribe(
                (res: any) => {
                    if (res.responseCode === 200) {
                        this.toastr.success(`${res.responseMessage}`, 'Success!');
                        if (this.dialogRef) {
                            this.dialogRef.close('saved');
                        }
                        this.submitted = false;
                        this.productCategoryFormGroup.reset();
                        this.checked = true;
                        this.clearSearchProduct();
                        this.editMode = false;
                    } else if (res.responseCode === 406 || res.responseCode === 417 ||
                        (res.data && (res.data.responseCode === 406 || res.data.responseCode == 417))) {
                        this.toastr.info(`${res.responseMessage}`, 'Info!');
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
        }
    }

    mapObject = () => {
        const productValues = this.productCategoryFormGroup.getRawValue();
        const product = {
            id: null,
            name: "",
            productId: "",
            unit: "",
            type: "",
            status: "",
            specificationParametersDTOList: [],
            hasMac: false,
            hasSerial: false,
            hasTrackable: false,
            hasPort: false,
            hasCas: false,
            expiryTime: "",
            expiryTimeUnit: "",
            dtvCategory: "",
            deviceType: ""
        };

        const processedSpecificationParameters = this.specificationParametersDTOList.controls.map((control: AbstractControl) => {
            const paramData = control.getRawValue();

            if (paramData.isMultiValueParam && paramData.paramMultiValues && Array.isArray(paramData.paramMultiValues)) {
                const listValues = paramData.paramMultiValues
                    .map((item: any) => item.value)
                    .filter((value: any) => value !== null && value !== undefined && value.trim() !== '');

                return {
                    id: paramData.id,
                    paramName: paramData.paramName,
                    isMandatory: paramData.isMandatory,
                    isMultiValueParam: true,
                    paramMultiValues: listValues
                };
            } else {
                return {
                    id: paramData.id,
                    paramName: paramData.paramName,
                    isMandatory: paramData.isMandatory,
                    isMultiValueParam: false,
                    paramMultiValues: null
                };
            }
        });

        product.id = productValues.id ? productValues.id : null;
        product.productId = productValues.productId;
        product.unit = productValues.unit;
        product.status = productValues.status;
        product.specificationParametersDTOList = processedSpecificationParameters;
        product.name = productValues.name;
        product.hasMac = productValues.hasMac;
        product.hasSerial = productValues.hasSerial;
        product.hasTrackable = productValues.hasTrackable;
        product.hasPort = productValues.hasPort;
        product.hasCas = productValues.hasCas;
        product.expiryTime = productValues.expiryTime;
        product.expiryTimeUnit = productValues.expiryTimeUnit;
        product.dtvCategory = productValues.dtvCategory;
        product.deviceType = productValues.deviceType;

        if (!this.editMode) {
            if (productValues.type.length == 1) {
                if (productValues.type[0] == "CustomerBind") {
                    productValues.type = "CustomerBind";
                    product.type = productValues.type;
                    return product;
                } else if (productValues.type[0] == "NetworkBind") {
                    productValues.type = "NetworkBind";
                    product.type = productValues.type;
                    return product;
                } else if (productValues.type[0] == "NA") {
                    productValues.type = "NA";
                    product.type = productValues.type;
                    return product;
                }
            } else if (productValues.type.length == 2) {
                if (
                    (productValues.type[0] == "CustomerBind" && productValues.type[1] == "NetworkBind") ||
                    (productValues.type[0] == "NetworkBind" && productValues.type[1] == "CustomerBind")
                ) {
                    productValues.type = "CustomerBind, NetworkBind";
                    product.type = productValues.type;
                    return product;
                } else if (
                    (productValues.type[0] == "NA" && productValues.type[1] == "NetworkBind") ||
                    (productValues.type[0] == "NetworkBind" && productValues.type[1] == "NA")
                ) {
                    productValues.type = "NA, NetworkBind";
                    product.type = productValues.type;
                    return product;
                } else if (
                    (productValues.type[0] == "NA" && productValues.type[1] == "CustomerBind") ||
                    (productValues.type[0] == "CustomerBind" && productValues.type[1] == "NA")
                ) {
                    productValues.type = "CustomerBind, NA";
                    product.type = productValues.type;
                    return product;
                }
            } else if (productValues.type.length == 3) {
                productValues.type = "CustomerBind, NA, NetworkBind";
                product.type = productValues.type;
                return product;
            }
        } else {
            if (this.editMode) {
                product.type = productValues.type;
                return product;
            }
        }

        return product;
    };

    onCheckboxChange(event: any) {
        this.productCategoryFormGroup.get("isMandatory")?.setValue(!!event);
    }

    editProduct(id): void {
        this.isEditMode = true;

        this.showDelete = false;
        this.showTable = true;
        this.editMode = true;
        this.createView = true;
        this.listView = false;
        this.detailView = false;
        this.getAllDTVCategory();
        this.commonGenericData();
        this.getAllProductType();
        this.getAllUOMType();
        const productEditData = this.productListData.find(element => element.id === id);
        if (productEditData.hasCas == true) {
            this.ifDTVCateShow = true;
        } else {
            this.ifDTVCateShow = false;
        }
        this.specificationParametersDTOList.clear();
        productEditData.specificationParametersDTOList.forEach((param: any) => {
            let listData = this.fb.array([]);
            if (param.isMultiValueParam) {
                param.paramMultiValues.forEach(data => {
                    listData.push(
                        this.formBuilder.group({
                            value: data
                        })
                    );
                });
            } else {
                listData = null;
            }
            const newParameter = this.formBuilder.group({
                id: [param.id],
                paramName: [param.paramName,],
                isMandatory: [param.isMandatory],
                isEditing: false,
                isNew: true,
                isMultiValueParam: [param.isMultiValueParam],
                paramMultiValues: listData
            });
            this.specificationParametersDTOList.push(newParameter);
        });
        let isPresent = productEditData.type.includes("NetworkBind");
        if (isPresent) {
            this.isDeviceTypeVisible = true;
        } else {
            this.isDeviceTypeVisible = false;
        }
        this.productCategoryFormGroup.patchValue({
            id: productEditData.id,
            name: productEditData.name,
            productId: productEditData.productId,
            unit: productEditData.unit,
            type: productEditData.type,
            status: productEditData.status,
            hasMac: productEditData.hasMac,
            hasSerial: productEditData.hasSerial,
            hasTrackable: productEditData.hasTrackable,
            hasPort: productEditData.hasPort,
            hasCas: productEditData.hasCas,
            dtvCategory: productEditData.dtvCategory,
            deviceType: productEditData.deviceType
        });
        this.productCategoryFormGroup.updateValueAndValidity();
    }

    searchProduct(): void {
        if (!this.searchkey || this.searchkey !== this.searchProductCatName) {
            this.currentPageProductListdata = 1;
        }
        this.searchkey = this.searchProductCatName;
        if (this.showItemPerPage) {
            this.productListdataitemsPerPage = this.showItemPerPage;
        }
        this.searchData.filter[0].filterValue = this.searchProductCatName;
        this.searchData.filter[0].filterColumn = this.searchProductCat.trim();
        const page = {
            page: this.currentPageProductListdata,
            pageSize: this.productListdataitemsPerPage
        };
        this.ProductCategoryManagementService.searchProduct(page, this.searchData).subscribe(
            (response: any) => {
                if (response.responseCode === 404) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                    this.productListData = [];
                    this.productListdatatotalRecords = 0;
                } else {
                    this.productListData = response.dataList;
                    this.productListdatatotalRecords = response.totalRecords;
                    this.productDataSource.data = response.dataList;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    clearSearchProduct(): void {
        this.showSearchBar = true;
        this.searchProductCatName = "";
        this.searchProductCat = "";
        this.editMode = false;
        this.submitted = false;
        this.createView = false;
        this.listView = true;
        this.detailView = false;
        this.searchkey = "";
        this.getProductList("");
        this.productCategoryFormGroup.reset();
        this.specificationParametersDTO.reset();
        this.ifDTVCateShow = false;
    }

    deleteConfirmProduct(pcid: number): void {
        if (pcid) {
            this.confirmationService.confirm({
                message: "Do you want to delete this Product Category?",
                header: "Delete Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.deleteProduct(pcid);
                },
                reject: () => {
                    this.toastr.info('Delete operation was cancelled', 'Info!');
                }
            });
        }
        this.productCategoryFormGroup.reset();
    }

    getProductDetails(id) {
        const url = "/productCategory/" + id;
        this.ProductCategoryManagementService.getMethod(url).subscribe(
            (res: any) => {
                this.viewProductCategoryDetails = res.data;
                this.listView = false;
                this.createView = false;
                this.detailView = true;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    pageChangedProductPlanMappingDetailList(event: PageEvent): void {
        this.productDeatilItemPerPage = event.pageSize;
        this.productPageChargeDeatilList = event.pageIndex + 1;
        this.updateProductDetailPagination();
    }

    updateProductDetailPagination(): void {
        if (!this.viewProductCategoryDetails?.specificationParametersDTOList) {
            this.productDetailDataSource.data = [];
            return;
        }

        const startIndex = (this.productPageChargeDeatilList - 1) * this.productDeatilItemPerPage;
        const endIndex = startIndex + this.productDeatilItemPerPage;

        const paginatedData = this.viewProductCategoryDetails.specificationParametersDTOList.slice(startIndex, endIndex);

        this.productDetailDataSource.data = paginatedData;
    }

    ProductCategoryList() {
        this.listView = true;
        this.createView = false;
        this.detailView = false;
    }

    deleteProduct(pcid): void {
        const url = "/productCategory/delete/" + pcid;
        this.ProductCategoryManagementService.deleteMethod(url).subscribe(
            (response: any) => {
                if (response.responseCode === 200) {
                    this.toastr.success(`Successfully Deleted`, 'Success!');
                    this.getProductList("");
                } else {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    pageChangedProductList(event: PageEvent): void {
        this.currentPageProductListdata = event.pageIndex + 1;
        this.productListdataitemsPerPage = event.pageSize;

        if (!this.searchkey) {
            this.getProductList(this.productListdataitemsPerPage);
        } else {
            this.searchProduct();
        }
    }


    macAction(): void {
        this.productCategoryFormGroup.controls.hasSerial.setValue(
            this.productCategoryFormGroup.controls.hasMac.value
        );
        this.productCategoryFormGroup.controls.hasTrackable.setValue(
            this.productCategoryFormGroup.controls.hasMac.value
        );
    }

    serialAction(): void {
        this.productCategoryFormGroup.controls.hasTrackable.setValue(
            this.productCategoryFormGroup.controls.hasSerial.value
        );
    }

    getAllProductType(): void {
        const url = "/commonList/generic/PRODUCT_TYPE";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.productCatType = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    getAllDTVCategory(): void {
        const url = "/commonList/generic/dtvCategory";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.dtvCategory = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    getAllUOMType(): void {
        const url = "/commonList/generic/UOM_TYPE";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.uomType = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    checkExit(type: 'create' | 'list' = 'list'): void {
        if (this.productCategoryFormGroup && !this.productCategoryFormGroup.dirty) {
            this.productCategoryFormGroup.markAsPristine();
            this.executeTargetNavigation(type);
        } else {
            const dialogRef2 = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: '400px',
                disableClose: true, 
                data: {
                    title: 'Alert',
                    description: 'The filled data will be lost. Do you want to continue? (Yes/No)',
                    yesLabel: 'Yes',
                    noLabel: 'No'
                }
            });

            dialogRef2.afterClosed().subscribe(result => {
                if (result) {
                    this.productCategoryFormGroup.markAsPristine();
                    this.executeTargetNavigation(type);
                }
            });
        }
    }

    private executeTargetNavigation(type: string): void {
        if (this.dialogRef) {
            this.dialogRef.close();
        }
        if (type === "create") {
            this.createProductCategory();
        } else {
            this.ProductCategoryList();
        }
    }

    
    checkHasCas(event) {
        if (event.checked == true) {
            this.ifDTVCateShow = true;
        }
        if (event.checked == false) {
            this.ifDTVCateShow = false;
            this.productCategoryFormGroup.controls["dtvCategory"].reset();
        }
    }

    onInputTypeChange(event: any) {
        this.specificationListData = [];
        this.specificationListArray.clear();
    }

    openDialogOnClick(): void {
        // this.specificationListForm.reset();
        // this.specificationListArray.clear();
        this.specificationListData = [];
        this.addValueSubmit = false;
        this.isFromList = false;
        this.currentIndexForEditList = undefined;
        this.updateSpecificationListTable();
        this.specificationListDialogRef = this.dialog.open(this.specificationListDialog, {
            width: '600px',
            disableClose: true,
            autoFocus: false
        });

        this.specificationListDialogRef.afterClosed().subscribe(result => {
            if (result === 'saved') {
                this.toastr.info('List values are ready. Now click "Add Parameter" to save the parameter with list values.', 'Info!');
            }
        });
    }

    openDialog() {
        this.showSpecificationListPopup = true;
    }

    addValue(): void {
        this.addValueSubmit = true;
        const formValueControl = this.specificationListForm.get('value');

        if (this.specificationListForm.valid) {
            const newValue = formValueControl.value ? formValueControl.value.trim() : '';
            // ... (omitted: validation checks) ...
            const existingValues = this.specificationListArray.controls.map(control =>
                control.get('value')?.value?.trim().toLowerCase()
            );
            if (existingValues.includes(newValue.toLowerCase())) {
                this.toastr.error('This value already exists in the list', 'Failed!');
                return;
            }

            this.specificationListArray.push(
                this.fb.group({
                    value: [newValue, [Validators.required, Validators.maxLength(40)]],
                    isEdit: false
                })
            );
            this.specificationListData.push({
                value: newValue,
                isEdit: false
            });


            this.updateSpecificationListTable();

            formValueControl.setValue(null);
            formValueControl.markAsUntouched();
            formValueControl.markAsPristine();
            formValueControl.setErrors(null);
            this.specificationListForm.updateValueAndValidity();
            this.addValueSubmit = false;

        } else {
            this.specificationListForm.markAllAsTouched();
        }
    }

    deleteValueFromArray(index: number): void {
        if (index >= 0 && index < this.specificationListArray.length) {
            this.specificationListArray.removeAt(index);
            if (index < this.specificationListData.length) {
                this.specificationListData.splice(index, 1);
            }
            this.updateSpecificationListTable();
        }
    }

    specificationListModelClose() {
        this.showSpecificationListPopup = false;
        this.specificationListForm.reset();
    }

    specificationModelClose(): void {
        if (this.specificationListDialogRef) {
            this.specificationListDialogRef.close('cancelled');
        }
        this.specificationListForm.reset();
        this.specificationListArray.clear();
    }

    saveSpecificationListData(): void {
        let hasErrors = false;

        this.specificationListArray.controls.forEach(control => {
            control.markAsTouched();
            if (control.invalid) {
                hasErrors = true;
            }
        });

        if (hasErrors) {
            this.toastr.error('Please fix all validation errors before saving', 'Failed!');
            return;
        }

        if (this.specificationListArray.length === 0) {
            this.toastr.error('Please add at least one value to the list', 'Failed!');
            return;
        }

        const values = this.specificationListArray.controls.map(control =>
            control.get('value')?.value?.trim().toLowerCase()
        );
        const uniqueValues = new Set(values);


        if (values.length !== uniqueValues.size) {
            this.toastr.error('Please remove duplicate values from the list', 'Failed!');
            return;
        }

        this.specificationListData = this.specificationListArray.value.map(item => ({
            value: item.value,
            isEdit: false
        }));

        // this.specificationListData.forEach(item => {
        //     this.specificationListArray.push(
        //         this.fb.group({
        //             value: [item.value, [Validators.required, Validators.maxLength(40)]],
        //             isEdit: false
        //         })
        //     )
        ;
        // });




        if (this.isFromList && this.currentIndexForEditList !== undefined && this.currentIndexForEditList >= 0) {
            this.updateParameterListValues();
        }

        if (this.specificationListDialogRef) {
            this.specificationListDialogRef.close('saved');
        }

        this.toastr.success('List values saved successfully', 'Success!');
    }

    updateParameterListValues(): void {
        const parametersArray = this.productCategoryFormGroup.get(
            'specificationParametersDTOList'
        ) as UntypedFormArray;


        if (
            this.currentIndexForEditList >= 0 &&
            this.currentIndexForEditList < parametersArray.length
        ) {
            const targetParameter = parametersArray.at(this.currentIndexForEditList);

            if (targetParameter && targetParameter.get('isMultiValueParam')?.value) {
                const paramMultiValues = targetParameter.get(
                    'paramMultiValues'
                ) as UntypedFormArray;

                while (paramMultiValues.length > 0) {
                    paramMultiValues.removeAt(0);
                }

                this.specificationListData.forEach(data => {
                    if (data.value && data.value.trim() !== '') {
                        paramMultiValues.push(
                            this.formBuilder.group({
                                value: [
                                    data.value.trim(),
                                    [Validators.required, Validators.maxLength(40)]
                                ]
                            })
                        );
                    }
                });

                targetParameter.markAsDirty();
            }
        }


        this.specificationParametersDataSource.data =
            parametersArray.controls;

        this.table.renderRows();

        this.productCategoryFormGroup.markAsDirty();
        this.productCategoryFormGroup.updateValueAndValidity();
    }


    updateExistingParameterWithNewList(): void {
        const parametersArray = this.productCategoryFormGroup.get('specificationParametersDTOList') as UntypedFormArray;

        if (this.currentIndexForEditList >= 0 && this.currentIndexForEditList < parametersArray.length) {
            const targetParameter = parametersArray.at(this.currentIndexForEditList);

            if (targetParameter) {
                const paramMultiValues = targetParameter.get('paramMultiValues') as UntypedFormArray;

                while (paramMultiValues.length !== 0) {
                    paramMultiValues.removeAt(0);
                }

                this.specificationListData.forEach(data => {
                    paramMultiValues.push(this.formBuilder.group({
                        value: [data.value, [Validators.required, Validators.maxLength(40)]]
                    }));
                });
            }
        }
    }

    showSpecificationListData() {
        this.showSpecificationListPopup = true;
    }

    particularSpecificationListView(item: any, index: any) {
        this.specificationListArray = this.fb.array([]);
        this.isFromList = true;
        this.currentIndexForEditList = index;
        this.showSpecificationListPopup = true;
        this.specificationParametersDTOList.value[index].paramMultiValues.forEach(data => {
            this.specificationListArray.push(
                this.fb.group({
                    value: data.value,
                    isEdit: true
                })
            );
        });
    }

    saveSpecificationListDataFromList() {
        this.isFromList = false;
        if (this.currentIndexForEditList || this.currentIndexForEditList == 0) {
            let listData = this.fb.array([]);
            this.specificationListArray.value.forEach(data => {
                listData.push(
                    this.formBuilder.group({
                        value: data.value,
                        isEdit: false
                    })
                );
            });

            this.specificationParametersDTOList.value[this.currentIndexForEditList].paramMultiValues =
                listData.value;
            this.specificationListArray = this.fb.array([]);
        }
        this.showSpecificationListPopup = false;
    }

    commonGenericData() {
        const url = "/commonList/generic/networkDeviceType";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.networkDeviceData = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    typeChange(event: any) {
        this.selectedTypes = event.value;
        let isPresent = this.selectedTypes.some(type => type === "NetworkBind");
        if (isPresent) {
            this.isDeviceTypeVisible = true;
            this.productCategoryFormGroup.get("deviceType").setValidators(Validators.required);
            this.productCategoryFormGroup.get("deviceType").updateValueAndValidity();
        } else {
            this.isDeviceTypeVisible = false;
            this.productCategoryFormGroup.get("deviceType").clearAsyncValidators();
            this.productCategoryFormGroup.get("deviceType").clearValidators;
            this.productCategoryFormGroup.get("deviceType").setValidators(null);
            this.productCategoryFormGroup.get("deviceType").updateValueAndValidity();
        }
    }

    canAddParameter(): boolean {
        const paramName = this.specificationParametersDTO.get('paramName')?.value;
        const inputType = this.specificationParametersDTO.get('inputType')?.value;

        if (!this.specificationParametersDTO.valid) {
            return false;
        }

        if (this.duplicateErrorMessage && this.duplicateErrorMessage !== '') {
            return false;
        }

        if (inputType === 'List' && (!this.specificationListData || this.specificationListData.length === 0)) {
            return false;
        }

        return true;
    }

    shouldDisableListButton(): boolean {
        const inputType = this.specificationParametersDTO.get('inputType')?.value;
        return inputType !== 'List';
    }

    isSpecificationListFormInvalid(): boolean {
        if (this.specificationListArray.length === 0) {
            return true;
        }

        for (let control of this.specificationListArray.controls) {
            control.markAsTouched();
            if (control.invalid) {
                return true;
            }
        }

        return false;
    }

    viewListValues(item: any, index: number): void {
        this.currentIndexForEditList = index;
        this.isFromList = true;

        this.specificationListArray.clear();
        this.specificationListData = [];

        const paramMultiValues = item.get('paramMultiValues') as UntypedFormArray;

        if (paramMultiValues && paramMultiValues.length > 0) {
            paramMultiValues.controls.forEach((control: AbstractControl) => {
                const valueControl = control.get('value');
                if (valueControl) {
                    const value = valueControl.value;
                    if (value) {
                        this.specificationListArray.push(this.fb.group({
                            value: [value, [Validators.required, Validators.maxLength(40)]]
                        }));

                        this.specificationListData.push({
                            value: value,
                            isEdit: false
                        });
                    }
                }
            });
        }
        this.updateSpecificationListTable();

        this.specificationListDialogRef = this.dialog.open(this.specificationListDialog, {
            width: '600px',
            disableClose: true,
            autoFocus: false
        });

        this.specificationListDialogRef.afterClosed().subscribe(result => {
            if (result === 'saved') {
                // Success message already shown in saveSpecificationListData
            }
            this.isFromList = false;
            this.currentIndexForEditList = undefined;
        });
    }
}
