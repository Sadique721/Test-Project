import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, UntypedFormArray, FormGroup } from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { DiscountManagementService } from "src/app/service/discount-management.service";
import { Regex } from "src/app/constants/regex";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { Observable, Observer } from "rxjs";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { PRODUCTS } from "src/app/constants/aclConstants";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from "@angular/material/sort";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { count } from "console";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { ToastrService } from "ngx-toastr";



@Component({
    selector: "app-discount-management",
    templateUrl: "./discount-management.component.html",
    styleUrls: ["./discount-management.component.css"],
    standalone: false
})
export class DiscountManagementComponent implements OnInit {

    DiscountMappingfromgroup: UntypedFormGroup;

    discountMapping: UntypedFormArray = this.fb.array([]);





    discountDisplayedColumns1: string[] = ['amount', 'discountType', 'validFrom', 'validUpto', 'delete'];

    discountGroupForm: UntypedFormGroup;

    discountPlanMapping: UntypedFormArray;
    submitted: boolean = false;
    planListData: any;
    currentPageDiscountMapping = 1;
    discountMappingitemsPerPage: number = 0;
    discountMappingtotalRecords: number = 0;
    currentPageDiscountPlanMapping = 1;
    discountPlanMappingitemsPerPage: number = 5;
    discountPlanMappingtotalRecords: number = 0;
    currentPageDiscount = 1;
    discountitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    discounttotalRecords: number = 0;
    createDiscountData: any;
    discountListData: any;
    isDiscountEdit: boolean = false;
    viewDiscountListData: any;
    searchData: any;
    searchDiscountName: any = "";

    searchView: boolean = false;
    createView: boolean = false;
    discountDeatilsShow: boolean = true;
    discountMappingDatashow: boolean = false;

    discountPersonalData: any = [];
    discountMappingItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    discountMappingLISTtotalRecords: String;
    currentPagediscountMappingList = 1;
    DiscountMappingSubmitted: boolean = false;
    DiscountPlanMappingSubmitted: boolean = false;
    DiscountPlanMappingfromgroup: UntypedFormGroup;

    AclClassConstants;
    AclConstants;

    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 1;
    searchkey: string;
    totalDataListLength = 0;
    discountType = [
        { label: "Flat", value: "Flat" },
        { label: "Percentage", value: "Percentage" }
    ];
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    statusOptions = RadiusConstants.status;
    public loginService: LoginService;
    dataPlanMappingList: any = [];
    planGroupData: any;
    planAllListData: any;
    selectedValidFromDate: string;
    selectedValidUpToDate: string;
    dialogRef: MatDialogRef<any>;
    @ViewChild('discountDialogTemplate') discountDialogTemplate!: TemplateRef<any>;
    @ViewChild('discountMappingPaginator') discountMappingPaginator!: MatPaginator;
    @ViewChild('discountPlanMappingPaginator') discountPlanMappingPaginator!: MatPaginator;

    @ViewChild('viewDiscountDialogTemplate') viewDiscountDialogTemplate!: TemplateRef<any>;
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    dailogRef!: MatDialogRef<any>;
    discountPlanMappingData: any;
    discountMappingData: any;



    constructor(
        private toastr: ToastrService,
        private dialog: MatDialog,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private discountManagementService: DiscountManagementService,
        loginService: LoginService,
        public commondropdownService: CommondropdownService
    ) {
        this.createAccess = loginService.hasPermission(PRODUCTS.DISCOUNT_CREATE);
        this.deleteAccess = loginService.hasPermission(PRODUCTS.DISCOUNT_DELETE);
        this.editAccess = loginService.hasPermission(PRODUCTS.DISCOUNT_EDIT);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;

        // this.isDiscountEdit = !this.createAccess && this.editAccess ? true : false;
    }

    ngOnInit(): void {
        if (this.discountListData) {
            this.discountListData.paginator = this.paginator;

        }
        this.discountPlanMapping = this.fb.array([]);
        this.discountPlanMappingData = this.discountPlanMapping.controls;
        ;

        this.discountGroupForm = this.fb.group({
            name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            status: ["", Validators.required],
            desc: ["", [Validators.required, Validators.pattern(Regex.characterlength255)]]
        });
        this.discountMapping = this.fb.array([]);
        this.discountPlanMapping = this.fb.array([]);

        this.DiscountMappingfromgroup = this.fb.group({
            amount: ["", [Validators.required, Validators.pattern(Regex.decimalNumber)]],
            discountType: ["", Validators.required],
            validFrom: ["", Validators.required],
            validUpto: ["", Validators.required],
            id: [""]
        });

        this.DiscountPlanMappingfromgroup = this.fb.group({
            planId: ["", Validators.required],
            id: [""]
        });

        this.getPlanListData();
        // this.onAddDiscountMappingField();


        // this.onAddDiscountPlanMappingField();
        this.getDiscountListData("");
        this.getPlanGroup();
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
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(PRODUCTS.DISCOUNT_DELETE) || this.loginService.hasPermission(PRODUCTS.DISCOUNT_EDIT)) {
            return ['Id', 'Name', 'Status', 'ISP', 'Action'];
        } else {
            return ['Id', 'Name', 'Status', 'ISP'];
        }
    }
    createDiscount() {
        this.searchView = false;
        this.createView = true;
        this.discountDeatilsShow = false;
        this.discountMappingDatashow = false;

        this.submitted = false;
        this.isDiscountEdit = false;
        this.discountGroupForm.reset();
        this.discountMapping.reset();
        this.discountPlanMapping.reset();
        this.DiscountPlanMappingfromgroup.reset();
        this.DiscountMappingfromgroup.reset();
        this.discountPlanMapping.controls = [];
        this.discountMapping.controls = [];
        this.discountGroupForm.controls.status.setValue("");
        this.DiscountMappingfromgroup.controls.discountType.setValue("");
        this.DiscountPlanMappingfromgroup.controls.planId.setValue("");
    }

    onChangeValidFromDate = event => {
        this.selectedValidFromDate = event.target.value;
    };

    onChangeValidUpToDate = event => {
        this.selectedValidUpToDate = event.target.value;
    };

    listDiscount() {
        this.searchView = true;
        this.createView = false;
        this.discountDeatilsShow = true;
        this.discountMappingDatashow = false;
    }

    createDiscountMappingFormGroup(): UntypedFormGroup {
        return this.fb.group({
            amount: [
                this.DiscountMappingfromgroup.value.amount,
                [Validators.pattern(Regex.decimalNumber)]
            ],
            discountType: [this.DiscountMappingfromgroup.value.discountType],
            validFrom: [this.DiscountMappingfromgroup.value.validFrom],
            validUpto: [this.DiscountMappingfromgroup.value.validUpto],
            id: ['']
        });
    }

    createDiscountPlanMappingFormGroup(): UntypedFormGroup {
        return this.fb.group({
            planId: [this.DiscountPlanMappingfromgroup.value.planId],
            id: [""]
        });
    }

    onAddDiscountMappingField() {
        this.DiscountMappingSubmitted = true;
        if (this.DiscountMappingfromgroup.valid) {

            this.discountMapping.push(this.createDiscountMappingFormGroup());


            this.discountMappingData = [...this.discountMapping.controls];



            this.discountMappingtotalRecords = this.discountMapping.controls.length;
            this.DiscountMappingfromgroup.reset();
            Object.keys(this.DiscountMappingfromgroup.controls).forEach(key => {
                this.DiscountMappingfromgroup.get(key)?.setErrors(null);
                this.DiscountMappingfromgroup.get(key)?.markAsPristine();
                this.DiscountMappingfromgroup.get(key)?.markAsUntouched();
            });

            this.DiscountMappingSubmitted = false;
        } else {
            this.toastr.error('Fill all fields', 'Failed!');
        }
    }

    onAddDiscountPlanMappingField() {
        if (this.DiscountPlanMappingfromgroup.valid) {
            this.discountPlanMapping.push(this.createDiscountPlanMappingFormGroup());


            this.discountPlanMappingData = [...this.discountPlanMapping.controls];


            this.discountPlanMappingtotalRecords = this.discountPlanMapping.controls.length;

            this.DiscountPlanMappingfromgroup.reset();
            Object.keys(this.DiscountPlanMappingfromgroup.controls).forEach(key => {
                this.DiscountPlanMappingfromgroup.get(key)?.setErrors(null);
                this.DiscountPlanMappingfromgroup.get(key)?.markAsPristine();
                this.DiscountPlanMappingfromgroup.get(key)?.markAsUntouched();
            });

        }
    }



    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageDiscount > 1) {
            this.currentPageDiscount = 1;
        }
        if (!this.searchkey) {
            this.getDiscountListData(this.showItemPerPage);
        } else {
            this.searchDiscount();
        }
    }
    discountDisplayedColumns: string[] = ['Id', 'Name', 'Status', 'ISP', 'Action'];

    getDiscountListData(size) {
        let page_list;
        this.searchkey = "";
        let pageIndex = this.currentPageDiscount ? this.currentPageDiscount - 1 : 0;
        let pageSize = size ? size : (this.showItemPerPage === 1 ? this.pageITEM : this.showItemPerPage);
        const url = "/discounts/all";
        this.commondropdownService.getMethod(url).subscribe(
            (response: any) => {

                this.discounttotalRecords = response?.discountList?.length || 0;

                const startIndex = pageIndex * pageSize;
                const endIndex = startIndex + pageSize;


                this.discountListData = (response?.discountList || []).slice(startIndex, endIndex);

            },
            (error: any) => {
                // console.log(error, 'error')
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    getPlanListData() {
        const url = "/postpaidplan/all";
        this.commondropdownService.getMethod(url).subscribe(
            (response: any) => {
                this.planAllListData = response.postpaidplanList;
                // console.log('this.planListData', this.planListData)
            },
            (error: any) => {
                // console.log(error, 'error')
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }

    addEditDiscount(discountId) {
        this.submitted = true;

        if (this.discountGroupForm.valid) {
            if (discountId) {
                this.createDiscountData = this.discountGroupForm.value;
                this.createDiscountData.discoundMappingList = this.discountMapping.value;
                this.createDiscountData.discoundPlanMappingList = this.discountPlanMapping.value;
                // console.log('this.createDiscountData', this.createDiscountData)
                const url = "/discounts/" + discountId;
                if (this.createDiscountData.discoundMappingList.length > 0) {
                    this.discountManagementService.updateMethod(url, this.createDiscountData).subscribe(
                        (response: any) => {

                            this.discountGroupForm.reset();
                            this.discountMapping.reset();
                            this.discountPlanMapping.reset();
                            this.DiscountPlanMappingfromgroup.reset();
                            this.DiscountMappingfromgroup.reset();
                            this.discountPlanMapping.controls = [];
                            this.discountMapping.controls = [];
                            if (!this.searchkey) {
                                this.getDiscountListData("");
                            } else {
                                this.searchDiscount();
                            }
                            this.dialogRef.close();
                            this.isDiscountEdit = false;
                            this.discountDeatilsShow = true;
                            this.searchView = false;
                            this.createView = false;
                            this.discountMappingDatashow = false;
                            this.toastr.success("Successful Updated", 'Success!');

                            this.submitted = false;
                        },
                        (error: any) => {
                            // console.log(error, 'error')
                            if (error.error.status == 417) {
                                this.toastr.info(`${error.error.ERROR}`, 'Info!');

                            } else {
                                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                            }
                        }
                    );
                } else {
                    this.toastr.error("Minimum one  Discount Mapping Details need to add", 'Failed!');

                }
            } else {
                this.createDiscountData = this.discountGroupForm.value;
                this.createDiscountData.discoundMappingList = this.discountMapping.value;
                this.createDiscountData.discoundPlanMappingList = this.discountPlanMapping.value;
                // console.log('this.createDiscountData', this.createDiscountData)
                const url = "/discounts";
                if (this.createDiscountData.discoundMappingList.length > 0) {
                    this.discountManagementService.postMethod(url, this.createDiscountData).subscribe(
                        (response: any) => {
                            this.discountGroupForm.reset();
                            this.discountMapping.reset();
                            this.discountPlanMapping.reset();
                            this.DiscountPlanMappingfromgroup.reset();
                            this.DiscountMappingfromgroup.reset();
                            if (!this.searchkey) {
                                this.getDiscountListData("");
                            } else {
                                this.searchDiscount();
                            }
                            this.dialogRef.close();
                            this.discountPlanMapping.controls = [];
                            this.discountMapping.controls = [];

                            this.discountDeatilsShow = true;
                            this.searchView = false;
                            this.createView = false;
                            this.discountMappingDatashow = false;
                            this.isDiscountEdit = false;
                            this.toastr.success("Successful Created", 'Success!');

                            this.submitted = false;
                        },
                        (error: any) => {
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                        }
                    );
                } else {
                    this.toastr.error("Minimum one  Discount Mapping Details need to add", 'Failed!');

                }
            }
        }

    }

    editDiscount(discountId) {
        this.discountDeatilsShow = false;
        this.searchView = false;
        this.createView = true;
        this.discountMappingDatashow = false;

        if (discountId) {
            const url = "/discounts/" + discountId;
            this.discountManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.isDiscountEdit = true;
                    this.viewDiscountListData = response.discountList;


                    this.discountGroupForm.patchValue(this.viewDiscountListData);


                    this.discountMapping.clear();
                    this.discountPlanMapping.clear();


                    if (this.viewDiscountListData.discoundMappingList?.length) {
                        this.viewDiscountListData.discoundMappingList.forEach((element: any) => {
                            this.discountMapping.push(this.fb.group({
                                amount: [element.amount],
                                discountType: [element.discountType],
                                validFrom: [element.validFrom],
                                validUpto: [element.validUpto],
                                id: [element.id]
                            }));
                        });


                        this.discountMappingData = [...this.discountMapping.controls];
                        this.discountMappingtotalRecords = this.discountMapping.length;
                    }


                    if (this.viewDiscountListData.discoundPlanMappingList?.length) {
                        this.viewDiscountListData.discoundPlanMappingList.forEach((element: any) => {
                            this.discountPlanMapping.push(this.fb.group({
                                planId: [element.planId],
                                id: [element.id]
                            }));
                        });


                        this.discountPlanMappingData = [...this.discountPlanMapping.controls];
                        this.discountPlanMappingtotalRecords = this.discountPlanMapping.length;
                    } else {

                        this.discountPlanMappingData = [];
                        this.discountPlanMappingtotalRecords = 0;
                    }


                    this.dialogRef = this.dialog.open(this.discountDialogTemplate, {
                        width: '2100px',
                        maxWidth: '80vw',
                        height: 'auto',
                        autoFocus: false,
                        disableClose: true
                    });

                    this.dialogRef.afterClosed().subscribe(() => {
                        this.dialogRef = null!;
                        this.discountGroupForm.reset();
                        this.discountMapping.clear();
                        this.discountPlanMapping.clear();
                        this.discountMappingData = [];
                        this.discountPlanMappingData = [];
                        this.isDiscountEdit = false;
                    });
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    searchDiscount() {
        if (!this.searchkey || this.searchkey !== this.searchDiscountName) {
            this.currentPageDiscount = 1;
        }

        this.searchkey = this.searchDiscountName;
        this.discountitemsPerPage = (this.showItemPerPage == 1) ? this.pageITEM : this.showItemPerPage;

        this.searchData.filters[0].filterValue = this.searchDiscountName.trim();
        const url = "/discounts/search";

        this.discountManagementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {

                const allDiscounts: any[] = response.discountList || [];


                this.discounttotalRecords = response.totalCount || allDiscounts.length;


                const pageIndex = this.currentPageDiscount ? this.currentPageDiscount - 1 : 0;
                const startIndex = pageIndex * this.discountitemsPerPage;
                const endIndex = startIndex + this.discountitemsPerPage;

                this.discountListData = allDiscounts.slice(startIndex, endIndex);
            },
            (error: any) => {
                this.discounttotalRecords = 0;
                this.discountListData = [];
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');

                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            }
        );
    }

    clearSearchDiscount() {
        this.searchDiscountName = "";
        this.getDiscountListData("");
    }

    canExit() {
        if (
            !this.discountGroupForm.dirty &&
            !this.DiscountMappingfromgroup.dirty &&
            !this.DiscountPlanMappingfromgroup.dirty
        )
            return true;
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
    // deleteConfirmonDiscountMappingField(index: number) {
    //     console.log("index", index)
    //     this.confirmationService.confirm({
    //         message: 'Do you want to delete this Discount Mapping?',
    //         header: 'Delete Confirmation',
    //         icon: 'pi pi-info-circle',
    //         accept: () => {

    //             this.discountMapping.removeAt(index);
    //             this.discountMappingData.data = this.discountMapping.controls.map(ctrl => ctrl as UntypedFormGroup);
    //             this.discountMappingData.paginator = this.discountMappingPaginator;


    //             this.discountMappingtotalRecords = this.discountMapping.length;

    //             this.messageService.add({
    //                 severity: 'success',
    //                 summary: 'Deleted',
    //                 detail: 'Discount Mapping removed successfully'
    //             });
    //         }
    //     });
    // }

    deleteConfirmonDiscountMappingField(index: number, discountMappingFieldId?: number) {
        if (index !== null && index >= 0) {
            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: '400px',
                data: {
                    title: 'Delete Discount Mapping',
                    description: `Are you sure you want to delete this Discount Mapping Attribute?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {

                    this.onRemovediscountMapping(index, discountMappingFieldId);
                    this.toastr.success("Discount Mapping Attribute has been deleted", 'Success!');

                } else {
                    this.toastr.info('You have cancelled', 'Info!');

                }
            });
        }
    }

    async onRemovediscountMapping(index: number, id?: number) {
        try {

            this.discountMapping.removeAt(index);


            this.discountMappingData = [...this.discountMapping.controls];


            this.discountMappingtotalRecords = this.discountMapping.length;


        } catch (error) {
            this.toastr.error("Failed to delete mapping from server", 'Failed!');

        }
    }


    deleteConfirmonDiscountPlanMappingField(index: number, discountPlanMappingFieldId?: number) {
        if (index !== null && index >= 0) {
            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: '400px',
                data: {
                    title: 'Delete Discount Plan Mapping',
                    description: `Are you sure you want to delete this Discount Plan Mapping Attribute?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.onRemovediscountPlanMapping(index, discountPlanMappingFieldId);
                    this.toastr.success('Discount Plan Mapping Attribute has been deleted', 'Success!');

                } else {
                    this.toastr.info(" 'You have cancelled'", 'Info!');

                }
            });
        }
    }

    async onRemovediscountPlanMapping(index: number, id?: number) {
        try {

            this.discountPlanMapping.removeAt(index);


            this.discountPlanMappingData = [...this.discountPlanMapping.controls];


            this.discountPlanMappingtotalRecords = this.discountPlanMapping.length;

        } catch (error) {
            this.toastr.error("Failed to delete Discount Plan Mapping", 'Failed!');

        }
    }


    deleteDiscount(discountId) {
        const url = "/discounts/" + discountId;
        this.discountManagementService.deleteMethod(url).subscribe(
            (response: any) => {
                if (this.currentPageDiscount != 1 && this.totalDataListLength == 1) {
                    this.currentPageDiscount = this.currentPageDiscount - 1;
                }
                this.toastr.success(`${response.message}`, 'Success!');

                if (!this.searchkey) {
                    this.getDiscountListData("");
                } else {
                    this.searchDiscount();
                }
            },
            (error: any) => {
                // console.log(error, 'error')
                if (error.error.status == 417) {
                    this.toastr.info(`${error.error.ERROR}`, 'Info!');

                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            }
        );
    }

    pageChangedDiscountMapping(event: PageEvent) {
        this.currentPageDiscountMapping = event.pageIndex;
        this.discountMappingitemsPerPage = event.pageSize;

        const startIndex = this.currentPageDiscountMapping * this.discountMappingitemsPerPage;
        const endIndex = startIndex + this.discountMappingitemsPerPage;


        const pagedData = this.discountMapping.controls
            .slice(startIndex, endIndex)
            .map(ctrl => ctrl as UntypedFormGroup);

        this.discountMappingData = new MatTableDataSource(pagedData);
        this.discountMappingData.paginator = this.discountMappingPaginator;
    }



    pageChangedDiscountPlanMapping(event: PageEvent) {
        this.currentPageDiscountPlanMapping = event.pageIndex;
        this.discountPlanMappingitemsPerPage = event.pageSize;

        const startIndex = this.currentPageDiscountPlanMapping * this.discountPlanMappingitemsPerPage;
        const endIndex = startIndex + this.discountPlanMappingitemsPerPage;


        const pagedData = this.discountPlanMapping.controls
            .slice(startIndex, endIndex)
            .map(ctrl => ctrl as UntypedFormGroup);

        this.discountPlanMappingData = new MatTableDataSource(pagedData);
        this.discountPlanMappingData.paginator = this.discountPlanMappingPaginator;
    }


    pageChangedDiscount(event: any) {
        this.currentPageDiscount = event.pageIndex + 1;
        this.discountitemsPerPage = event.pageSize;


        if (!this.searchkey) {
            this.getDiscountListData(this.discountitemsPerPage);
        } else {
            this.searchDiscount();
        }
    }



    discountPersonaDetails(data: any) {
        this.discountPersonalData = data;
        this.dataPlanMappingList = [];

        const planMappingList = data.discoundPlanMappingList || [];
        if (planMappingList.length === 0) {
            this.openDiscountViewDialog();
        }

        let loaded = 0;
        planMappingList.forEach((item: any) => {
            const planUrl = `/postpaidplan/${item.planId}`;
            this.discountManagementService.getMethod(planUrl).subscribe((res: any) => {
                this.dataPlanMappingList.push(res.postPaidPlan.name);
                loaded++;
                if (loaded === planMappingList.length) {
                    this.openDiscountViewDialog();
                }
            });
        });
    }

    openDiscountViewDialog() {
        this.dialogRef = this.dialog.open(this.viewDiscountDialogTemplate, {
            width: '2100px',
            maxWidth: '80vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });

        this.dialogRef.afterClosed().subscribe(() => {
            this.dialogRef = null!;
        });
    }


    pageChangedDiscountPersonaList(pageNumber) {
        this.currentPagediscountMappingList = pageNumber;
    }

    getPlanGroup() {
        const url = "/commonList/planGroup";
        this.commondropdownService.getMethodWithCache(url).subscribe(
            (response: any) => {
                this.planGroupData = response.dataList;
                let data = {
                    text: "All",
                    value: "All"
                };
                this.planGroupData.unshift(data);
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    selPlanGroup(event) {
        this.getPlanListbyGroup(event.value);
    }

    getPlanListbyGroup(group: any) {
        const url = "/postpaidplan/all?planGroup=" + group;
        this.discountManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.planListData = response.postpaidplanList;
                // console.log('this.planListData', this.planListData)
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // console.log(error, 'error')

            }
        );
    }

    amountValidation(event) {
        var num = String.fromCharCode(event.which);
        if (!/[0-9]/.test(num)) {
            event.preventDefault();
        }
    }
    opendiscountDailog() {
        this.createView = true;
        this.discountDeatilsShow = false;
        this.searchView = false;
        this.createView = true;
        this.discountMappingDatashow = false;

        this.discountGroupForm.reset();
        this.discountMapping.clear();
        this.discountPlanMapping.clear();


        this.discountMappingData = [];
        this.discountPlanMappingData = [];
        this.discountMappingtotalRecords = 0;
        this.discountPlanMappingtotalRecords = 0;

        this.isDiscountEdit = false;
        this.dialogRef = this.dialog.open(this.discountDialogTemplate, {
            width: '2100px',
            maxWidth: '80vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });

        this.dialogRef.afterClosed().subscribe(() => {
            this.dialogRef = null!;
            this.discountGroupForm.reset();
        });
    }

    onCancel() {
        this.dialogRef.close(); this.discountGroupForm.reset();
        this.DiscountMappingfromgroup.reset();
        this.DiscountPlanMappingfromgroup.reset();


        this.discountMapping.clear();
        this.discountPlanMapping.clear();

        this.discountMappingData = [];
        this.discountPlanMappingData = [];
        this.discountMappingtotalRecords = 0;
        this.discountPlanMappingtotalRecords = 0;


        this.isDiscountEdit = false;

    }
    deleteConfirmonCountryDialog(item: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete Discount',
                description: `Are you sure you want to delete "${item.name}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteDiscount(item.id);
            } else {
            }
        });
    }
}


