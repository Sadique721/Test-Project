import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup, FormControl, UntypedFormArray, FormArray, FormGroup } from "@angular/forms";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { LeasedLineCustomerService } from "src/app/service/leased-line-customer.service";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { Regex } from "src/app/constants/regex";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { ThisReceiver } from "@angular/compiler";
import { Observable, Observer } from "rxjs";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { PRE_CUST_CONSTANTS } from "src/app/constants/aclConstants";
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from "@angular/material/sort";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { count } from "console";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { ToastrService } from 'ngx-toastr';
@Component({
    selector: "app-leased-line-customer",
    templateUrl: "./leased-line-customer.component.html",
    styleUrls: ["./leased-line-customer.component.css"],
    standalone: false
})
export class LeasedLineCustomerComponent implements OnInit {
    dataSource = new MatTableDataSource<any>([]);
    displayedColumns: string[] = ['Id', 'name', 'email', 'businessName', 'technicalPerson', 'contactNo', 'action'];
    pageSizeOptions = [5, 10, 25, 50, 100];

    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;



    llcDetailDataSource: MatTableDataSource<any>;
    llcDetailDisplayedColumns: string[] = ['Label', 'Type', 'Plan', 'Static IP', 'Device Type', 'Delete'];

    dialogRef: any;
    AclClassConstants;
    AclConstants;
    createAccess: boolean = false;
    editAccess: boolean = false;
    deleteAccess: boolean = false;
    public loginService: LoginService;
    llcGroupForm: UntypedFormGroup;
    llcDetailArray: UntypedFormArray;
    llcDetailForm: UntypedFormGroup;
    submitted: boolean = false;
    isLlcEdit: boolean = false;
    createView: boolean = false;
    listView: boolean = true;
    detailView: boolean = false;
    llcDetailSubmitted: boolean = false;
    llcDetailArrayitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    llcDetailArraytotalRecords: String;
    currentPagellcDetailArraydata = 1;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    searchkey: string;
    currentPageLlcListdata = 1;
    LlcListdataitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    LlcListdatatotalRecords: any;
    llcListData: any;
    createLlcData: any;
    viewLlcData: any = {
        llcDetailsList: [{}],
    };
    searchData: any;
    searchLlcName: any = "";
    llcDetailItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    llcDetailtotalRecords: String;
    currentPagellcDetailList = 1;
    planData: any;
    devicetype = [
        { label: "Router", value: "Router" },
        { label: "ONU", value: "ONU" },
    ];

    typeSelect = [
        { label: "L2", value: "L2" },
        { label: "L3", value: "L3" },
        { label: "SIP", value: "SIP" },
        { label: "Internet", value: "Internet" },
    ];

    constructor(
        private toastr: ToastrService,
        private dialog: MatDialog,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private leasedLineCustomerService: LeasedLineCustomerService,
        loginService: LoginService,
        private commondropdownService: CommondropdownService
    ) {
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;

        this.createAccess = loginService.hasPermission(PRE_CUST_CONSTANTS.PRE_CUST_LEASED_LINE_CREATE);

        this.editAccess = loginService.hasPermission(PRE_CUST_CONSTANTS.PRE_CUST_LEASED_LINE_EDIT);
        this.deleteAccess = loginService.hasPermission(PRE_CUST_CONSTANTS.PRE_CUST_LEASED_LINE_DELETE);

        // this.isLlcEdit = !createAccess && editAccess ? true : false;
    }

    ngOnInit(): void {
        this.llcGroupForm = this.fb.group({
            name: ["", Validators.required],
            email: ["", [Validators.required, Validators.email]],
            businessName: ["", Validators.required],
            billingAddress: ["", Validators.required],
            technicalPersonName: ["", Validators.required],
            technicalPersonContactNo: ["", [Validators.required, Validators.pattern(Regex.numeric)]],
        });
        this.llcDetailForm = this.fb.group({
            llcIdentifier: [""],
            llcLabel: ["", [Validators.required]],
            llcType: ["", Validators.required],
            packageId: ["", Validators.required],
            llcStaticIP: ["", Validators.required],
            llcDeviceType: ["", Validators.required],
        });
        this.llcDetailArray = this.fb.array([]);
        this.searchData = {
            filters: [
                {
                    filterDataType: "",
                    filterValue: "",
                    filterColumn: "any",
                    filterOperator: "equalto",
                    filterCondition: "and",
                },
            ],
            page: "",
            pageSize: "",
        };
        this.getLlcDataList("");
        this.getPlan();
        this.llcDetailDataSource = new MatTableDataSource(this.llcDetailArray.controls);
    }


    createLlc() {
        this.listView = false;
        this.createView = true;
        this.submitted = false;
        this.isLlcEdit = false;
        this.detailView = false;
        this.llcGroupForm.reset();
        this.llcDetailArray.clear(); // Changed from controls = []
        this.llcDetailDataSource.data = this.llcDetailArray.controls; // Add this line
    }


    listLlc() {
        this.listView = true;
        this.createView = false;
        this.detailView = false;
        this.getLlcDataList("");
    }

    getLlcDataList(list) {
        let size;
        this.searchkey = "";
        let page = this.currentPageLlcListdata;
        if (list) {
            size = list;
            this.LlcListdataitemsPerPage = list;
        } else {
            size = this.LlcListdataitemsPerPage;
        }

        let data = {
            page: page,
            pageSize: size,
        };
        const url = "/leasedlinecustomers/list";
        this.leasedLineCustomerService.postMethod(url, data).subscribe(
            (response: any) => {
                this.llcListData = response.leasedlinecustomersList;
                this.LlcListdatatotalRecords = response.pageDetails.totalRecords;
                this.dataSource.data = this.llcListData;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                this.dataSource.data = [];
                // this.messageService.add({
                //   severity: "error",
                //   summary: "Error",
                //   detail: error.error.ERROR,
                //   icon: "far fa-times-circle",
                // });
            }
        );
    }

    getPlan() {
        const url = "/postpaidplan/all";
        this.commondropdownService.getMethod(url).subscribe(
            (response: any) => {
                // this.planData = response.postpaidplanList;
                if (response.postpaidplanList) {
                    this.planData = response.postpaidplanList.filter(
                        plan =>
                        (plan.serviceId =
                            plan.planGroup === "Registration" || plan.planGroup === "Registration and Renewal")
                    );
                } else {
                    this.planData = [];
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // this.messageService.add({
                //   severity: "error",
                //   summary: "Error",
                //   detail: error.error.ERROR,
                //   icon: "far fa-times-circle",
                // });
            }
        );
    }

    searchLlc() {
        if (!this.searchkey || this.searchkey !== this.searchData) {
            this.currentPageLlcListdata = 1;
        }
        this.searchkey = this.searchData;
        if (this.showItemPerPage) {
            this.LlcListdataitemsPerPage = this.showItemPerPage;
        }
        this.searchData.filters[0].filterValue = this.searchLlcName.trim();
        this.searchData.page = this.currentPageLlcListdata;
        this.searchData.pageSize = this.LlcListdataitemsPerPage;
        const url = "/leasedlinecustomers/search";
        // console.log("this.searchData", this.searchData)
        this.leasedLineCustomerService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                this.llcListData = response.leasedlinecustomersList;
                this.LlcListdatatotalRecords = response.pageDetails.totalRecords;
                this.dataSource.data = this.llcListData;
            },
            (error: any) => {
                this.LlcListdatatotalRecords = 0;
                this.dataSource.data = [];
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'info!');
                    // this.messageService.add({
                    //   severity: "info",
                    //   summary: "Info",
                    //   detail: error.error.msg,
                    //   icon: "far fa-times-circle",
                    // });
                    this.llcListData = [];
                } else {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                    // this.messageService.add({
                    //   severity: "error",
                    //   summary: "Error",
                    //   detail: error.error.ERROR,
                    //   icon: "far fa-times-circle",
                    // });
                }
            }
        );
    }

    clearSearchLlc() {
        this.searchLlcName = "";
        this.getLlcDataList("");
    }

    detailLlc(id) {
        this.listView = false;
        this.createView = false;
        this.detailView = true;
        this.getLlcById(id);
        this.openViewDetailsDialog(id);
    }

    addEditLlc(id) {
        this.submitted = true;
        this.llcDetailArray.controls.forEach(control => {
            control.markAllAsTouched();
        });
        if (this.llcGroupForm.valid) {
            if (id) {
                this.createLlcData = this.llcGroupForm.value;
                this.createLlcData.llcDetailsList = this.llcDetailArray.value;
                //console.log(" this.createLlcData", this.createLlcData);
                const url = "/leasedlinecustomers/" + id;
                this.leasedLineCustomerService.updateMethod(url, this.createLlcData).subscribe(
                    (response: any) => {
                        this.llcGroupForm.reset();
                        this.toastr.success(`Successfully Updated`, 'Success!');
                        // this.messageService.add({
                        //   severity: "success",
                        //   summary: "Successfully",
                        //   detail: response.message,
                        //   icon: "far fa-check-circle",
                        // });
                        this.submitted = false;
                        if (!this.searchkey) {
                            this.getLlcDataList("");
                        } else {
                            this.searchLlc();
                        }
                        this.llcDetailArray.clear();
                        this.llcDetailDataSource.data = this.llcDetailArray.controls;
                        this.listView = true;
                        this.createView = false;
                        this.detailView = false;
                        this.isLlcEdit = false;
                        this.llcDetailArray.controls = [];
                        if (this.dialogRef) this.dialogRef.close();
                    },
                    (error: any) => {
                        // console.log(error, "error")
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                        // this.messageService.add({
                        //   severity: "error",
                        //   summary: "Error",
                        //   detail: error.error.ERROR,
                        //   icon: "far fa-times-circle",
                        // });
                    }
                );
            } else {
                this.createLlcData = this.llcGroupForm.value;
                this.createLlcData.llcDetailsList = this.llcDetailArray.value;
                //console.log(" this.createLlcData", this.createLlcData);
                const url = "/leasedlinecustomers";
                this.leasedLineCustomerService.postMethod(url, this.createLlcData).subscribe(
                    (response: any) => {
                        this.llcGroupForm.reset();
                        this.toastr.success(`Successfully Created`, 'Success!');
                        // this.messageService.add({
                        //   severity: "success",
                        //   summary: "Successfully",
                        //   detail: response.message,
                        //   icon: "far fa-check-circle",
                        // });
                        this.submitted = false;
                        if (!this.searchkey) {
                            this.getLlcDataList("");
                        } else {
                            this.searchLlc();
                        }
                        this.llcDetailArray.clear();
                        this.llcDetailDataSource.data = this.llcDetailArray.controls;
                        this.listView = true;
                        this.createView = false;
                        this.detailView = false;
                        this.llcDetailArray.controls = [];
                        if (this.dialogRef) this.dialogRef.close();
                    },
                    (error: any) => {
                        // console.log(error, "error")
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                        // this.messageService.add({
                        //   severity: "error",
                        //   summary: "Error",
                        //   detail: error.error.ERROR,
                        //   icon: "far fa-times-circle",
                        // });
                    }
                );
            }
        }
    }

    createllcDetailFormGroup(): UntypedFormGroup {
        return this.fb.group({
            llcIdentifier: [this.llcDetailForm.value.llcIdentifier],
            llcLabel: [this.llcDetailForm.value.llcLabel, Validators.required], // Add validator
            llcType: [this.llcDetailForm.value.llcType, Validators.required], // Add validator
            packageId: [this.llcDetailForm.value.packageId, Validators.required], // Add validator
            llcStaticIP: [this.llcDetailForm.value.llcStaticIP, Validators.required], // Add validator
            llcDeviceType: [this.llcDetailForm.value.llcDeviceType, Validators.required], // Add validator
            id: [""],
        });
    }


    onAddllcDetailField() {
        this.llcDetailSubmitted = true;
        if (this.llcDetailForm.valid) {
            this.llcDetailArray.push(this.createllcDetailFormGroup());
            this.llcDetailForm.reset();
            this.llcDetailSubmitted = false;
            this.llcDetailDataSource.data = this.llcDetailArray.controls;
        } else {
            // console.log("I am not valid");
            Object.keys(this.llcDetailForm.controls).forEach(key => {
                this.llcDetailForm.get(key)?.markAsTouched();
                this.llcDetailForm.get(key)?.updateValueAndValidity(); // Add this
            });
        }
    }

    editLlc(id) {
        this.getLlcById(id);
        this.listView = false;
        this.createView = true;
        this.detailView = false;
        this.llcGroupForm.reset();
        this.llcDetailForm.reset();
        this.isLlcEdit = true;
        this.openLeasedLineDialogDialog(id);
    }

    getLlcById(id) {
        const url = "/leasedlinecustomers/" + id;
        this.leasedLineCustomerService.getMethod(url).subscribe(
            (response: any) => {
                this.viewLlcData = response.leasedLineCustomersData;
                this.llcGroupForm.patchValue({
                    name: this.viewLlcData.name,
                    email: this.viewLlcData.email,
                    businessName: this.viewLlcData.businessName,
                    billingAddress: this.viewLlcData.billingAddress,
                    technicalPersonName: this.viewLlcData.technicalPersonName,
                    technicalPersonContactNo: this.viewLlcData.technicalPersonContactNo,
                });
                this.llcDetailArray = this.fb.array([]);
                this.viewLlcData.llcDetailsList.forEach(element => {
                    this.llcDetailArray.push(this.fb.group(element));
                });
                this.llcDetailDataSource.data = this.llcDetailArray.controls;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // this.messageService.add({
                //   severity: "error",
                //   summary: "Error",
                //   detail: error.error.ERROR,
                //   icon: "far fa-times-circle",
                // });
            }
        );
    }

    deleteConfirmonLlcDetailField(llcDetailFieldIndex: number, llcDetailFieldId: number) {
        if (llcDetailFieldIndex || llcDetailFieldIndex == 0) {
            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: "400px",
                disableClose: true,
                data: {
                    title: "Delete Confirmation",
                    description: `Are you sure you want to delete this Leased Line Circuit Detail?`,
                    yesLabel: "Confirm",
                    noLabel: "Cancel"
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.onRemoveTaxTypeTiered(llcDetailFieldIndex, llcDetailFieldId);
                } else {
                    this.toastr.info(`Delete operation was cancelled`, 'Info!');
                }
            });
        }
    }

    async onRemoveTaxTypeTiered(llcDetailFieldIndex: number, llcDetailFieldId: number) {
        this.llcDetailArray.removeAt(llcDetailFieldIndex);
        this.llcDetailDataSource.data = this.llcDetailArray.controls;
    }
    deleteConfirmonLlc(llcData: any) {
        if (llcData) {
            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: "400px",
                disableClose: true,
                data: {
                    title: "Delete Confirmation",
                    description: `Are you sure you want to delete "${llcData.name}"?`,
                    yesLabel: "Confirm",
                    noLabel: "Cancel"
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.deleteLlc(llcData.id);
                } else {
                    this.toastr.info(`Delete operation was cancelled`, 'Info!');
                }
            });
        }
    }


    deleteLlc(id) {
        const url = "/leasedlinecustomers/" + id;
        this.leasedLineCustomerService.deleteMethod(url).subscribe(
            (response: any) => {
                if (this.currentPageLlcListdata != 1 && this.llcListData.length == 1) {
                    this.currentPageLlcListdata = this.currentPageLlcListdata - 1;
                }
                if (!this.searchkey) {
                    this.getLlcDataList("");
                } else {
                    this.searchLlc();
                }
                this.toastr.success(`Successfully Deleted`, 'Success!');
                // this.messageService.add({
                //   severity: "success",
                //   summary: "Successfully",
                //   detail: response.message,
                //   icon: "far fa-check-circle",
                // });
            },
            (error: any) => {
                // console.log(error, "error")
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                // this.messageService.add({
                //   severity: "error",
                //   summary: "Error",
                //   detail: error.error.ERROR,
                //   icon: "far fa-times-circle",
                // });
            }
        );
    }

    pageChangedTaxTiered(pageNumber) {
        this.currentPagellcDetailArraydata = pageNumber;
    }

    pageChangedllcDetailbList(pageNumber) {
        this.currentPagellcDetailList = pageNumber;
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageLlcListdata > 1) {
            this.currentPageLlcListdata = 1;
        }
        if (!this.searchkey) {
            this.getLlcDataList(this.showItemPerPage);
        } else {
            this.searchLlc();
        }
    }
    onLlcPageChange(event: PageEvent) {
        this.LlcListdataitemsPerPage = event.pageSize;
        this.currentPageLlcListdata = event.pageIndex + 1;

        if (!this.searchkey) {
            this.getLlcDataList("");
        } else {
            this.searchLlc();
        }
    }

    pageChangedTaxList(pageNumber) {
        this.currentPageLlcListdata = pageNumber;
        if (!this.searchkey) {
            this.getLlcDataList("");
        } else {
            this.searchLlc();
        }
    }
    canExit() {
        if (!this.llcGroupForm.dirty && !this.llcDetailForm.dirty) return true;
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

    @ViewChild("LeasedLineDialog") LeasedLineDialog!: TemplateRef<any>;
    openLeasedLineDialogDialog(leasedLine: any) {
        // Reset forms before opening
        this.llcGroupForm.reset();
        this.llcDetailForm.reset();
        this.llcDetailArray.clear();
        this.llcDetailDataSource.data = this.llcDetailArray.controls;
        this.submitted = false;
        this.llcDetailSubmitted = false;

        this.dialogRef = this.dialog.open(this.LeasedLineDialog, {
            width: "900px",
            disableClose: true
        });

        this.dialogRef.afterClosed().subscribe(result => {
            // Clear all data when dialog closes
            this.llcGroupForm.reset();
            this.llcDetailForm.reset();
            this.llcDetailArray.clear();
            this.llcDetailDataSource.data = this.llcDetailArray.controls;
            this.submitted = false;
            this.llcDetailSubmitted = false;
            this.isLlcEdit = false;
            this.clearSearchLlc();
        });
    }


    @ViewChild("viewDetails") viewDetails!: TemplateRef<any>;

    openViewDetailsDialog(branchManagement: any) {
        this.dialogRef = this.dialog.open(this.viewDetails, {
            width: "900px",
            disableClose: true
        });
        this.dialogRef.afterClosed().subscribe(result => {
            this.clearSearchLlc();
        });
    }

}
