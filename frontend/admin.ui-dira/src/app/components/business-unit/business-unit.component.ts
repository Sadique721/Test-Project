import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, FormArray } from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { CountryManagementService } from "../../service/country-management.service";
import * as RadiusConstants from "../../RadiusUtils/RadiusConstants";
import { LoginService } from "../../service/login.service";
import { AclClassConstants } from "../../constants/aclClassConstants";
import { AclConstants } from "../../constants/aclOperationConstants";
import { Observable, Observer } from "rxjs";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { MASTERS } from "src/app/constants/aclConstants";
import { WhiteeSpaceValidator } from "../shared/custom-validators";
import { MatTableDataSource } from "@angular/material/table";
import { MatSort } from "@angular/material/sort";
import { MatPaginator, PageEvent } from "@angular/material/paginator";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { ToastrService } from 'ngx-toastr';

@Component({
    selector: "app-business-unit",
    templateUrl: "./business-unit.component.html",
    styleUrls: ["./business-unit.component.css"],
    standalone: false
})
export class BusinessUnitComponent implements OnInit {
    businessUnitFormGroup: UntypedFormGroup;
    submitted: boolean = false;
    businessUnitData: any;
    businessUnitListData: any;
    isEdit: boolean = false;
    viewListData: any;

    currentPageSlab = 1;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    totalRecords: any;

    searchName: any = "";
    searchData: any;
    AclClassConstants;
    AclConstants;

    statusOptions = RadiusConstants.status;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    searchkey: string;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    IcListData: any = [];
    // businessUnitTypeData :any= [
    //   { label: "Predefined", value: "Predefined", val: "Predefined" },
    //   { label: "On-Demand", value: "On-Demand", val: "On-Demand" },
    // ];
    businessUnitTypeData: any = [];
    defaultPlanCreation = { label: "Predefined", value: "Predefined" };
    public loginService: LoginService;

    dataSource = new MatTableDataSource<any>();
    displayedColumns: string[] = ['Name', 'Code', 'Status', 'action'];
    // Paginator + Sort refs
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;
    @ViewChild('businessUnitDialog') businessUnitDialog!: TemplateRef<any>;
    dialogRef!: MatDialogRef<any>;
    @ViewChild('buDetailsDialog') buDetailsDialog!: TemplateRef<any>;
    constructor(
        private toastr: ToastrService,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private countryManagementService: CountryManagementService,
        loginService: LoginService,
        public commondropdownService: CommondropdownService,
        public countrymgmtService: CountryManagementService,
        private dialog: MatDialog
    ) {
        this.createAccess = loginService.hasPermission(MASTERS.BUSINESS_UNIT_CREATE);
        this.deleteAccess = loginService.hasPermission(MASTERS.BUSINESS_UNIT_DELETE);
        this.editAccess = loginService.hasPermission(MASTERS.BUSINESS_UNIT_EDIT);
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;
        // this.isEdit = !this.createAccess && this.editAccess ? true : false;
        this.getAllBusinessUnitType();
    }

    ngOnInit(): void {
        this.businessUnitFormGroup = this.fb.group({
            buname: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            bucode: ["", Validators.required],
            status: ["", Validators.required],
            investmentCodeid: [],
            planBindingType: ["", Validators.required],
        });

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

        this.getListData("");
        this.getInvestmentList("");
    }

    addEdit(id) {
        this.submitted = true;
        if (this.businessUnitFormGroup.valid) {
            if (id) {
                const url = "/businessUnit/update";
                this.businessUnitData = this.businessUnitFormGroup.value;
                this.businessUnitData.id = id;
                this.countryManagementService.postMethod(url, this.businessUnitData).subscribe(
                    (response: any) => {
                        if (response.responseCode == 406) {
                            this.toastr.info(`${response.responseMessage}`, 'Info!');

                        } else {
                            this.submitted = false;
                            this.isEdit = false;
                            this.businessUnitFormGroup.reset();
                            this.businessUnitFormGroup.controls.status.setValue("");
                            this.commondropdownService.clearCache("/businessUnit/all");
                            this.toastr.success(`Succesfully Updated`, 'Success!');

                            this.submitted = false;
                            this.dialog.closeAll();
                            if (this.searchkey) {
                                this.search();
                            } else {
                                this.getListData("");
                            }
                        }
                    },
                    (error: any) => {
                        console.log(error, "error")
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                    }
                );
            } else {
                const url = "/businessUnit/save";
                this.businessUnitData = this.businessUnitFormGroup.value;
                this.countryManagementService.postMethod(url, this.businessUnitData).subscribe(
                    (response: any) => {
                        if (response.responseCode == 406) {

                            this.toastr.info(`${response.responseMessage}`, 'Info!');

                        } else {
                            this.submitted = false;
                            this.businessUnitFormGroup.reset();
                            this.businessUnitFormGroup.controls.status.setValue("");
                            this.commondropdownService.clearCache("/businessUnit/all");
                            this.toastr.success(`${response.responseMessage}`, 'Success!');

                            this.dialog.closeAll();
                            if (this.searchkey) {
                                this.search();
                            } else {
                                this.getListData("");
                            }
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                    }
                );
            }
        }
    }

    // Cancel / Close dialog
    onCancel(): void {
        this.dialog.closeAll();
        this.businessUnitFormGroup.reset();
    }
    addNewDialog() {
        this.isEdit = false;
        this.dialogRef = this.dialog.open(this.businessUnitDialog, {
            width: '800px',
            disableClose: true
        });
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageSlab > 1) {
            this.currentPageSlab = 1;
        }
        if (!this.searchkey) {
            this.getListData(this.showItemPerPage);
        } else {
            this.search();
        }
    }

    getListData(list) {
        const url = "/businessUnit";
        let size;
        this.searchkey = "";
        let pageList = this.currentPageSlab;
        if (list) {
            size = list;
            this.itemsPerPage = list;
        } else {
            size = this.itemsPerPage;
        }
        let plandata = {
            page: pageList,
            pageSize: size,
        };
        this.countryManagementService.postMethod(url, plandata).subscribe(
            (response: any) => {
                this.businessUnitListData = response.dataList;
                this.dataSource = new MatTableDataSource<any>(this.businessUnitListData);
                this.totalRecords = response.totalRecords;

                this.searchkey = "";
            },
            (error: any) => {
                console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    edit(id) {
        if (id) {
            const url = "/businessUnit/" + id;
            this.countryManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.isEdit = true;
                    this.dialogRef = this.dialog.open(this.businessUnitDialog, {
                        width: '800px',
                        disableClose: true
                    });
                    this.viewListData = response.data;
                    this.businessUnitFormGroup.patchValue(this.viewListData);
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            );
        }
    }

    search() {
        if (!this.searchkey || this.searchkey !== this.searchName) {
            this.currentPageSlab = 1;
        }
        this.searchkey = this.searchName;
        if (this.showItemPerPage) {
            this.itemsPerPage = this.showItemPerPage;
        }
        this.searchData.filter[0].filterValue = this.searchName.trim();

        const url = `/businessUnit/search?page=${this.currentPageSlab}&pageSize=${this.itemsPerPage}&sortBy=id&sortOrder=0`;
        this.countryManagementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    this.businessUnitListData = response.dataList;
                    this.totalRecords = response.totalRecords;
                    this.dataSource = new MatTableDataSource<any>(this.businessUnitListData);
                    if (this.paginator) {
                        this.dataSource.paginator = this.paginator;
                    }
                }
                if (response.responseCode == 404) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                    this.businessUnitListData = [];
                }
            },
            (error: any) => {
                this.totalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');

                    this.businessUnitListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                }
            }
        );
    }

    clearSearch() {
        this.searchName = "";
        this.searchkey = "";
        this.getListData("");
        this.submitted = false;
        this.isEdit = false;
        this.businessUnitFormGroup.reset();
        this.businessUnitFormGroup.controls.status.setValue("");
    }

    canExit() {
        if (!this.businessUnitFormGroup.dirty) return true;
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
                    },
                });
                return false;
            });
        }
    }

    // deleteConfirmon(id: number) {
    //     if (id) {
    //         this.confirmationService.confirm({
    //             message: "Do you want to delete this Business Unit?",
    //             header: "Delete Confirmation",
    //             icon: "pi pi-info-circle",
    //             accept: () => {
    //                 let data: any;

    //                 const url1 = "/businessUnit/" + id;
    //                 this.countryManagementService.getMethod(url1).subscribe(
    //                     (response: any) => {
    //                         data = response.data;
    //                         this.delete(data);
    //                     },
    //                     (error: any) => {
    //                         this.messageService.add({
    //                             severity: "error",
    //                             summary: "Error",
    //                             detail: error.error.ERROR,
    //                             icon: "far fa-times-circle",
    //                         });
    //                     }
    //                 );
    //             },
    //             reject: () => {
    //                 this.messageService.add({
    //                     severity: "info",
    //                     summary: "Rejected",
    //                     detail: "You have rejected",
    //                 });
    //             },
    //         });
    //     }
    // }
    deleteConfirmon(item: any) {
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete Confirmation ',
                description: `Are you sure you want to delete "${item.buname}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                // this.deleteProduct(item.id);
                let data: any;

                const url1 = "/businessUnit/" + item.id;
                this.countryManagementService.getMethod(url1).subscribe(
                    (response: any) => {
                        data = response.data;
                        this.delete(data);
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');


                    }
                );

            } else {
            }
        });
    }

    delete(data) {
        const url = "/businessUnit/delete";
        this.countryManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                if (this.currentPageSlab != 1 && this.businessUnitListData.length == 1) {
                    this.currentPageSlab = this.currentPageSlab - 1;
                }
                // this.messageService.add({
                //   severity: "success",
                //   summary: "Successfully",
                //   detail: response.message,
                //   icon: "far fa-check-circle",
                // });
                if (response.responseCode == 405 || response.responseCode == 406) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                } else {
                    this.toastr.success(`Successfully Deleted`, 'Success!');


                }
                this.clearSearch();
                if (this.searchkey) {
                    this.search();
                } else {
                    this.getListData("");
                }
            },
            (error: any) => {
                console.log(error, "error")

                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    // pageChangedList(pageNumber) {
    //     this.currentPageSlab = pageNumber;
    //     if (this.searchkey) {
    //         this.search();
    //     } else {
    //         this.getListData("");
    //     }
    // }
    pageChangedList(event: PageEvent) {
        this.currentPageSlab = event.pageIndex + 1;
        this.itemsPerPage = event.pageSize;
        if (this.searchkey) {
            this.search();
        } else {
            this.getListData("");
        }
    }
    getInvestmentList(list) {
        const url = "/investmentCode/all";
        this.countryManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.IcListData = response.dataList;
            },
            (error: any) => {
                console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');



            }
        );
    }
    buList: any = [];
    buNameDetailsModal: boolean = false;
    closeModal() {
        this.buNameDetailsModal = false;
    }
    IcCodeOpenModel(id) {
        this.buNameDetailsModal = true;
        this.dialog.open(this.buDetailsDialog, {
            width: '800px',
            disableClose: false
        });
        const url = "/businessUnit/BusinessUnit/" + id;
        this.countryManagementService.getMethod(url).subscribe((response: any) => {
            this.buList = response.BuById;
        });
    }
    getAllBusinessUnitType(): void {
        // let url = "";
        const url = "/commonList/generic/PLAN_BINDING_TYPE";
        this.countryManagementService.getMethodWithCache(url).subscribe(
            (response: any) => {
                // this.productListData = response.dataList;ad
                this.businessUnitTypeData = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');




            }
        );
    }
}
