import { Component, ElementRef, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup } from "@angular/forms";
import { MessageService } from "primeng/api";
import { ConfirmationService } from "primeng/api";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { LoginService } from "src/app/service/login.service";
import { Observable, Observer } from "rxjs";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { ChildManagementService } from "src/app/service/child-management.service";
import { CHILDMANAGEMENT } from "src/app/RadiusUtils/RadiusConstants";
import { ActivatedRoute, Router } from "@angular/router";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { NgxSpinnerService } from "ngx-spinner";
import { CustomerdetailsilsService } from "src/app/service/customerdetailsils.service";
import { ToastrService } from 'ngx-toastr';

import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";

@Component({
    selector: "app-child-management",
    templateUrl: "./child-management.component.html",
    styleUrls: ["./child-management.component.css"],
    standalone: false
})
export class ChildManagementComponent implements OnInit {
    @ViewChild("fileInput") fileInput: ElementRef;
    title = CHILDMANAGEMENT;
    isLoading: boolean = true;
    customerId = 0;
    mvnoId: any;
    inputshowSelsctData: boolean = false;
    //   createAccess: boolean = false;
    //   deleteAccess: boolean = false;
    //   editAccess: boolean = false;
    childManagementFormGroup: UntypedFormGroup;
    submitted: boolean = false;
    childManagement: any;
    isChildEdit: boolean = false;
    currentPageChildSlab = 1;
    childitemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    childTotalRecords: any;
    childManagementName: any = "";
    searchData: any;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    currentPageChildListData = 1;
    showItemPerPage: any;
    searchkey: string;
    public loginService: LoginService;
    childListData: any;
    childManagementData: any;
    viewChildListData: any;
    statusOptions = [
        { label: "Active", value: "Y", val: "ACTIVE" },
        { label: "Inactive", value: "N", val: "INACTIVE" }
    ];
    parentData: any;
    currentPagebillTemplatesListdata = 1;
    billTemplatesListdataitemsPerPage = 20;
    custDetails: any;
    presentFullAddress: any;
    presentAdressDATA: any;
    isChildList: boolean = true;
    isChildCreateOrEdit: boolean = false;
    isMobileNumber: boolean = false;
    childFormData: any = {};
    ischildButton: boolean;
    _passwordType = "password";
    showPassword = false;
    inputMobileNumber: string = "";
    mobileError: boolean = false;
    verifyChildCustData: any;
    custData: any = {};
    custType: string = "";
    // dialog: boolean = false;
    custId: number;
    parentAccountList: { label: string; value: string }[] = [];
    isAccountSelected: boolean = false;
    isviewChildWallet: boolean = false;
    viewChildWalletData: any;
    isViewChildLedger: boolean = false;
    viewChildLedgerData: any;
    currentPageChildLedgerSlab = 1;
    childLedgerItemsPerPage = RadiusConstants.ITEMS_PER_PAGE;
    childLedgerTotalRecords: number = 0;
    pageLimitOptionsChildLedger = RadiusConstants.pageLimitOptions;
    childLedgerShowItemPerpage: any;
    childLedgerId: any;

    //*******************************
    @ViewChild('addChildDialog') addChildDialog!: TemplateRef<any>;
    @ViewChild('childwalletDetails') childwalletDetails!: TemplateRef<any>;
    @ViewChild('viewchildledger') viewchildledger!: TemplateRef<any>;
    @ViewChild('viewchildDialog') viewchildDialog!: TemplateRef<any>;
    dialogRef!: MatDialogRef<any>;
    dialogRef2!: MatDialogRef<any>;
    dialogRef3!: MatDialogRef<any>;
    viewRef!: MatDialogRef<any>;
    currency: string;
    viewChildData: any;


    constructor(
        private toastr: ToastrService,
        private fb: UntypedFormBuilder,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        public commonDropdownService: CommondropdownService,
        public childManagementService: ChildManagementService,
        private route: ActivatedRoute,
        private customerManagementService: CustomermanagementService,
        private spinner: NgxSpinnerService,
        private router: Router,
        public customerdetailsilsService: CustomerdetailsilsService,
        private dialog: MatDialog,
    ) {
        // this.loginService = loginService;
        // this.createAccess = loginService.hasPermission(CHILD_MANAGEMENT.CHILD_MANAGEMENT_CREATE);
        // this.deleteAccess = loginService.hasPermission(CHILD_MANAGEMENT.CHILD_MANAGEMENT_DELETE);
        // this.editAccess = loginService.hasPermission(CHILD_MANAGEMENT.CHILD_MANAGEMENT_EDIT);
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.custType = this.route.snapshot.parent.paramMap.get("custType")!;
    }

    ngOnInit(): void {
        this.customerId = Number(this.route.snapshot.paramMap.get("customerId")!);
        this.childManagementFormGroup = this.fb.group({
            firstName: ["", Validators.required],
            lastName: ["", Validators.required],
            userName: ["", Validators.required],
            password: ["", Validators.required],
            email: ["", [Validators.required, Validators.email]],
            wallet: ["0"],
            status: ["", Validators.required],
            mobileNumber: ["", Validators.required],
            parentAccountNumber: [""]
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
            ],
            page: "",
            pageSize: "",
            fromDate: null,
            toDate: null,
            status: "Active"
        };
        this.getChildListData("");
        this.getCustomersDetail(this.customerId);
        this.mvnoId = Number(localStorage.getItem("mvnoId"));
        this.commonDropdownService.mobileNumberLengthSubject$.subscribe(len => {
            if (len) {
                this.childManagementFormGroup
                    .get("mobileNumber")
                    ?.setValidators([
                        Validators.required,
                        Validators.minLength(len.min),
                        Validators.maxLength(len.max)
                    ]);
                this.childManagementFormGroup.get("mobileNumber")?.updateValueAndValidity();
            }
        });
    }

    canExit() {
        if (!this.childManagementFormGroup.dirty) return true;
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

    addEditChildManagement(id) {
        this.submitted = true;
        if (this.childManagementFormGroup.valid) {
            if (id) {
                const url = "/parentchildmapping/updateChild";
                this.childManagementData = this.childManagementFormGroup.value;
                this.childManagementData.parentCustId = this.customerId;
                this.childManagementData.childId = id;
                this.childManagementService.postMethod(url, this.childManagementData).subscribe(
                    (response: any) => {
                        this.dialogRef.close();
                        this.childManagementFormGroup.reset();
                        this.submitted = false;
                        this.isChildEdit = false;
                        this.isMobileNumber = false;
                        this.ischildButton = false;
                        this.isChildCreateOrEdit = false;
                        this.isChildList = true;
                        this.childManagementFormGroup.reset();
                        this.isAccountSelected = false;
                        this.toastr.success(`${response.responseMessage}`, 'Success!');

                        this.submitted = false;
                        if (this.searchkey) {
                            this.searchChild();
                        } else {
                            this.clearsearchChild();
                        }
                    },
                    (error: any) => {
                        if (error.error.status == 417 || error.error.status == 406) {
                            this.toastr.info(`${error.error.ERROR}`, 'Info!');

                        } else {
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                        }
                    }
                );
            } else {
                const url = "/childCustomer/save";
                this.childManagementData = this.childManagementFormGroup.value;
                this.childManagementData.parentCustId = this.customerId;
                this.childManagementService.postMethod(url, this.childManagementData).subscribe(
                    (response: any) => {
                        this.dialogRef.close();
                        this.childManagementFormGroup.reset();
                        this.submitted = false;
                        this.isChildEdit = false;
                        this.isMobileNumber = false;
                        this.ischildButton = false;
                        this.isChildCreateOrEdit = false;
                        this.isChildList = true;
                        this.childManagementFormGroup.reset();
                        this.isAccountSelected = false;

                        this.toastr.success(`${response.responseMessage}`, 'Success!');

                        if (this.searchkey) {
                            this.searchChild();
                        } else {
                            this.clearsearchChild();
                        }
                    },
                    (error: any) => {
                        if (
                            error.error.status == 417 ||
                            error.error.status == 406 ||
                            error.error.status == 409
                        ) {
                            this.toastr.info(`${error.error.responseMessage}`, 'Info!');
                        } else {
                            this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                            icon: "far fa-times-circle"
                        }
                    }
                );
            }
        }
    }
    openModal(childId: number) {
        this.custId = childId;
        const url = `/parentchildmapping/getChildById?childId=${childId}`;
        this.childManagementService.getMethod(url).subscribe(
            (response: any) => {
                // this.childListData = response.data;
                this.viewChildData = response.data;
                this.viewRef = this.dialog.open(this.viewchildDialog, {
                    width: '1000px',
                    maxWidth: '90vw',
                    height: 'auto',
                    autoFocus: false,
                    disableClose: true
                });

                this.viewRef.afterClosed().subscribe(() => {
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error?.ERROR || "Failed to fetch child details"}`, 'Failed!');
            }
        );
    }
    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPageChildSlab > 1) {
            this.currentPageChildSlab = 1;
        }
        if (!this.searchkey) {
            this.getChildListData(this.showItemPerPage);
        } else {
            this.searchChild();
        }
    }

    editChild(id) {
        if (id) {
            // this.isChildList = false;
            this.isChildCreateOrEdit = true;
            this.isChildEdit = true;
            const url = `/parentchildmapping/getChildById?childId=${id}`;
            this.childManagementService.getMethod(url).subscribe(
                (response: any) => {
                    this.viewChildListData = response.data;
                    this.parentAccountList = [];
                    if (this.viewChildListData.parentAccountNumber) {
                        this.parentAccountList.push({
                            label: this.viewChildListData.parentAccountNumber,
                            value: this.viewChildListData.parentAccountNumber
                        });
                    }
                    this.childManagementFormGroup.patchValue({
                        firstName: this.viewChildListData.childFirstName,
                        lastName: this.viewChildListData.childLastName,
                        userName: this.viewChildListData.childUsername,
                        password: this.viewChildListData.childPassword,
                        email: this.viewChildListData.childEmail,
                        mobileNumber: this.viewChildListData.childMobile,
                        status: this.viewChildListData.status,
                        parentAccountNumber: this.viewChildListData.parentAccountNumber,
                        wallet: "0"
                    });
                    this.childManagementFormGroup?.get("mobileNumber")?.disable();
                    this.childManagementFormGroup?.get("userName")?.disable();
                    this.childManagementFormGroup?.get("password")?.disable();

                    this.dialogRef = this.dialog.open(this.addChildDialog, {
                        width: '1000px',
                        maxWidth: '90vw',
                        height: 'auto',
                        autoFocus: false,
                        disableClose: true
                    });

                    this.dialogRef.afterClosed().subscribe(() => {
                        this.childManagementFormGroup.reset();
                    });
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }
    clearsearchChild() {
        this.childManagementName = "";
        this.searchkey = "";
        this.getChildListData("");
        this.submitted = false;
        this.isChildEdit = false;
        this.isMobileNumber = false;
        this.ischildButton = false;
        this.childFormData = "";
        this.childManagementFormGroup.reset();
    }

    closedialog() {
        this.dialogRef.close();
    }

    getCustomersDetail(custId) {
        const url = "/customers/" + custId;
        this.customerManagementService.getMethod(url).subscribe((response: any) => {
            this.custData = response.customers;
        });
    }
    customerDetailOpen() {
        this.router.navigate(["/home/customer/details/" + this.custType + "/x/" + this.customerId]);
    }
    deleteChildConfirmation(item: any) {
        // if (id) {
        //     this.confirmationService.confirm({
        //         message: "Do you want to delete this Child ?",
        //         header: "Delete Confirmation",
        //         icon: "pi pi-info-circle",
        //         accept: () => {
        //             this.deleteChild(id);
        //         },
        //         reject: () => {
        //                   //                 error: (error) => {
        //                         //                 this.toastr.info(`${error.responseMessage}`, 'You have rejected!');
        //             }
        //         }
        //     });
        // }
        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete ' + this.title,
                description: `Are you sure you want to delete "${item.childUsername}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.deleteChild(item.id)
            } else {
            }
        });
    }

    deleteChild(id) {
        const url = "/parentchildmapping/deleteChild?childId=" + id;
        this.childManagementService.deleteMethod(url).subscribe(
            (response: any) => {
                if (this.currentPageChildSlab != 1 && this.childListData.length == 1) {
                    this.currentPageChildSlab = this.currentPageChildSlab - 1;
                }
                if (
                    response.responseCode == 405 ||
                    response.responseCode == 406 ||
                    response.responseCode == 417
                ) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                } else {
                    this.toastr.success(`${response.responseMessage}`, 'success!');

                }
                if (this.searchkey) {
                    this.searchChild();
                } else {
                    this.clearsearchChild();
                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.responseMessage}`, 'Failed!');

            }
        );
    }

    pageChangedChildList(pageNumber) {
        this.currentPageChildSlab = pageNumber.pageIndex + 1;
        this.childitemsPerPage = pageNumber.pageSize;

        if (this.searchkey) {
            this.searchChild();
        } else {
            this.getChildListData("");
        }
    }

    getChildListData(list: any) {
        let page = this.currentPageChildSlab;

        if (list) {
            this.childitemsPerPage = list;
        }

        let size = this.childitemsPerPage;
        this.searchkey = "";
        let childObj = {
            page: page,
            pageSize: size,
            id: this.customerId
        };

        const url = `/childCustomer/getAllChildByParent`;

        this.childManagementService.postMethod(url, childObj).subscribe(
            (response: any) => {
                this.childListData = response.childCustomer.content;
                this.childTotalRecords = response.childCustomer.totalElements;
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error?.ERROR || "Something went wrong."}`, 'Failed!');

            }
        );
    }

    searchChild() {
        if (!this.searchkey || this.searchkey !== this.childManagementName) {
            this.currentPageChildSlab = 1;
        }
        this.searchkey = this.childManagementName;
        if (this.showItemPerPage) {
            this.childitemsPerPage = this.showItemPerPage;
        }
        this.searchData.filters[0].filterValue = this.childManagementName.trim();
        this.searchData.page = this.currentPageChildSlab;
        this.searchData.pageSize = this.childitemsPerPage;

        const url = "/parentchildmapping/search?parentId=" + this.customerId;
        this.childManagementService.postMethod(url, this.searchData).subscribe(
            (response: any) => {
                this.childListData = response.childCustomer.content;
                this.childTotalRecords = response.childCustomer.totalElements;
            },
            (error: any) => {
                this.childTotalRecords = 0;
                if (error.error.status == 404) {
                    this.toastr.info(`${error.error.msg}`, 'Info!');

                    this.childListData = [];
                } else {
                    this.toastr.error(`${error.response.ERROR}`, 'Failed!');

                }
            }
        );
    }

    openChildListMenu() {
        this.submitted = false;
        this.isChildCreateOrEdit = false;
        this.isChildList = true;
    }

    openChildCustomerCreateMenu() {
        this.submitted = false;
        this.isChildEdit = false;
        // this.isChildList = false;
        this.isChildCreateOrEdit = true;

        this.childManagementFormGroup?.get("mobileNumber")?.enable();
        this.childManagementFormGroup?.get("userName")?.enable();
        this.childManagementFormGroup?.get("password")?.enable();

        this.dialogRef = this.dialog.open(this.addChildDialog, {
            width: '1000px',
            maxWidth: '90vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });

        this.dialogRef.afterClosed().subscribe(() => {
            this.childManagementFormGroup.reset();
        });
    }

    getRefresh() {
        this.getChildListData("");
    }

    listChild() {
        this.isChildList = true;
        this.isChildCreateOrEdit = false;
        this.getChildListData("");
    }

    onMobileBlur() {
        this.spinner.show();
        if (this.isChildEdit) {
            return;
        }
        const mobile = this.childManagementFormGroup.controls.mobileNumber.value;

        if (mobile && mobile.toString().length === 10) {
            const url = `/childCustomer/getChildByMobileNumber?mobileNumber=${mobile}&parentId=${this.customerId}`;

            this.childManagementService.getMethod(url).subscribe(
                (response: any) => {
                    if (response.responseCode === 204) {
                        this.spinner.hide();
                        this.ischildButton = false;
                        this.isMobileNumber = false;

                        const userName = this.childManagementFormGroup.get("userName")?.value;
                        if (userName && userName.toString().trim()) {
                            this.verifyChildCustomer();
                        }
                    } else if (response.responseCode === 417) {
                        this.ischildButton = true;
                        this.isMobileNumber = false;

                        this.toastr.info(`${response.responseMessage}`, 'Info!');

                        this.spinner.hide();
                    } else if (response.responseCode === 200 && response.data) {
                        this.ischildButton = false;
                        this.isMobileNumber = false;

                        // Clear form data and dropdowns
                        this.childFormData = [];
                        this.parentAccountList = [];

                        if (Array.isArray(response.data)) {
                            this.childFormData = response.data; // Assign array data first

                            for (let item of this.childFormData) {
                                if (item.parentAccountNumber) {
                                    this.parentAccountList.push({
                                        label: item.parentAccountNumber,
                                        value: item.parentAccountNumber
                                    });
                                }
                            }
                        } else {
                            this.childFormData.push(response.data); // Push single object
                            if (response.data.parentAccountNumber) {
                                this.parentAccountList.push({
                                    label: response.data.parentAccountNumber,
                                    value: response.data.parentAccountNumber
                                });
                            }
                        }

                        if (this.parentAccountList.length === 1) {
                            this.childManagementFormGroup.patchValue(this.childFormData[0]);
                            this.isAccountSelected = true;
                        } else {
                            // If more than one or none, clear specific fields
                            this.childManagementFormGroup.patchValue({
                                parentAccountNumber: null
                                // Add other fields to clear if needed, like:
                                // childName: null,
                                // age: null,
                                // etc.
                            });
                            this.isAccountSelected = false;
                        }
                        this.spinner.hide();
                    }
                },
                (error: any) => {
                    this.spinner.hide();
                    console.error("Error fetching child by mobile:", error);
                    this.toastr.error(`${error?.error?.ERROR || "Unexpected error occurred"}`, 'Failed!');

                }
            );
        }
        this.spinner.hide();
    }

    onParentAccountSelect(selectedAccountNumber: string) {
        const selectedData = this.childFormData.find(
            (item: any) => item.parentAccountNumber === selectedAccountNumber
        );

        if (selectedData) {
            this.childManagementFormGroup.patchValue(selectedData);
            this.isAccountSelected = true;
        }
    }

    onInputMobile(event: any) {
        const inputElement = event.target as HTMLInputElement;
        const inputValue = inputElement.value;
        if (inputValue.startsWith("0")) {
            this.mobileError = true;
        } else {
            this.mobileError = false;
        }
        this.clearFormOnMobileChange(inputValue);
    }

    onKeymobileNumberlength(event) {
        const str = this.childManagementFormGroup.value.mobileNumber.toString();
        const withoutCommas = str.replace(/,/g, "");
        const strrr = withoutCommas.trim();
        let mobilenumberlength = this.commonDropdownService.commonMoNumberLength;
        if (mobilenumberlength === 0 || mobilenumberlength === null) {
            mobilenumberlength = 10;
        }
        if (strrr.length > Number(mobilenumberlength)) {
            this.inputMobileNumber = `${mobilenumberlength} character required.`;
        } else if (strrr.length == Number(mobilenumberlength)) {
            this.inputMobileNumber = "";
        } else {
            this.inputMobileNumber = `${mobilenumberlength} character required.`;
        }
    }

    verifyChildCustomer() {
        const url = "/parentchildmapping/verifyChildCustomer";
        const customerId = Number(this.route.snapshot.paramMap.get("customerId"));
        const data = {
            userName: this.childManagementFormGroup.get("userName")?.value,
            mobileNumber: this.childManagementFormGroup.get("mobileNumber")?.value,
            parentId: customerId
        };
        this.childManagementService.postMethod(url, data).subscribe(
            (response: any) => {
                if (response.responseCode == 204) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                } else {
                    this.verifyChildCustData = response;
                }
            },
            (error: any) => {
                console.log(error, "error");
                this.toastr.error(`${error.error.responseMessage}`, 'Failed!');

            }
        );
    }
    keypressSession(event: any) {
        const pattern = /[0-9]/;
        let inputChar = String.fromCharCode(event.charCode);
        if (event.keyCode != 8 && event.keyCode != 9 && !pattern.test(inputChar)) {
            event.preventDefault();
        }
    }

    clearFormOnMobileChange(mobileValue: string) {
        this.parentAccountList = [];
        this.childFormData = [];
        this.ischildButton = false;
        this.isMobileNumber = false;
        this.isAccountSelected = false;
        this.childManagementFormGroup.reset();
        this.childManagementFormGroup.patchValue({ mobileNumber: mobileValue });
    }

    closewalletDialog() {
        this.dialogRef2.close();
    }

    viewChildWallet(id) {
        const url = `/parentchild/getwallet?childId=${id}`;
        this.customerdetailsilsService.revenueGetMethod(url).subscribe(
            (response: any) => {
                this.viewChildWalletData = response.data;
                this.isviewChildWallet = true;

                this.dialogRef2 = this.dialog.open(this.childwalletDetails, {
                    width: '1000px',
                    maxWidth: '90vw',
                    height: 'auto',
                    autoFocus: false,
                    disableClose: true
                });

                this.dialogRef2.afterClosed().subscribe(() => {
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    closeChildWallet() {
        this.isviewChildWallet = false;
    }

    viewChildLedger(id) {
        // this.isViewChildLedger = true;
        this.childLedgerId = id;
        this.getChildLedgerData("");
        this.dialogRef3 = this.dialog.open(this.viewchildledger, {
            width: '1000px',
            maxWidth: '90vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });

        this.dialogRef3.afterClosed().subscribe(() => {
        });
    }

    getChildLedgerData(list: any) {
        let page = this.currentPageChildLedgerSlab;
        if (list) {
            this.childLedgerItemsPerPage = list;
        }
        let size = this.childLedgerItemsPerPage;
        const url = `/parentchild/getchildledger?childId=${this.childLedgerId}`;
        let obj = {
            page: page,
            pageSize: size
        };
        this.customerdetailsilsService.postRevenueMethod(url, obj).subscribe(
            (response: any) => {
                this.viewChildLedgerData = response?.dataList || [];
                this.childLedgerTotalRecords = response.totalRecords;
            },
            (error: any) => {
                this.toastr.error(`No record Found`, 'Failed!');

            }
        );
    }

    closeViewChildLedger() {
        this.isViewChildLedger = false;
        this.currentPageChildLedgerSlab = 0;
        this.childLedgerShowItemPerpage = 0;
        this.childLedgerTotalRecords = 0;
        this.childLedgerId = "";
        this.dialogRef3.close();
    }

    pageChangedChildLedgerList(pageNumber) {
        this.currentPageChildLedgerSlab = pageNumber;
        this.getChildLedgerData(this.childLedgerShowItemPerpage);
    }

    TotalItemPerPageChildLedger(event) {
        this.childLedgerShowItemPerpage = Number(event.value);

        if (this.currentPageChildLedgerSlab > 1) {
            this.currentPageChildLedgerSlab = 1;
        }
        this.getChildLedgerData(this.childLedgerShowItemPerpage);
    }

    closeViewChildDialog() {
        this.viewRef.close();
    }
}
