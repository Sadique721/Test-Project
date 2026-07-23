import { Component, ElementRef, OnInit, TemplateRef, viewChild, ViewChild } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { ActivatedRoute } from "@angular/router";
import { countries } from "../model/country";
import * as uuid from "uuid";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { CustomerdetailsilsService } from "src/app/service/customerdetailsils.service";
import { Subscription, interval } from "rxjs";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ToastrService } from 'ngx-toastr';
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { SystemconfigService } from "src/app/service/systemconfig.service";

@Component({
    selector: "app-customer-pay",
    templateUrl: "./customer-pay.component.html",
    styleUrls: ["./customer-pay.component.css"],
    standalone: false
})
export class CustomerPayComponent implements OnInit {
    customerId: number;
    custType: any;
    bearToken: string;
    paymethd: string;
    userName: string;
    submitted: boolean = false;
    mpinForm: UntypedFormGroup;
    countries: any = countries;
    subscription2: Subscription;
    paymentstatusCount = RadiusConstants.TIMER_COUNT;
    transactionStatus: boolean = false;
    obs1$ = interval(1000);
    customerDetailData: any;
    // paymentConfirmationModal: boolean = false;
    // paymentSucessModel: boolean = false;
    exitBuy: boolean = true;
    hash: string = null;
    //   isRenew: boolean = false;
    errorMessage: string = "";
    paymentLinkToken: string = "";
    // ******************
    @ViewChild('paymentConfirmationModal') paymentConfirmationModal!: TemplateRef<any>;
    @ViewChild('paymentSucessModel') paymentSucessModel!: TemplateRef<any>;
    @ViewChild('mobileNo') mobileNoInput!: ElementRef;
    dialogRef!: MatDialogRef<any>;
    dialogRefS!: MatDialogRef<any>;
    paymentGateWays: any;
    selectedPaymentGateway: string = "";
    enteredMobileNumber: string = "";
    isGatewayFetching: boolean = false;
    countryCode: string = "+91";
    currency: string = "";

    constructor(
        private toastr: ToastrService,
        private dialog: MatDialog,
        private spinner: NgxSpinnerService,
        private route: ActivatedRoute,
        private fb: UntypedFormBuilder,
        public customerdetailsilsService: CustomerdetailsilsService,
        private messageService: MessageService,
        private systemService: SystemconfigService
    ) { }

    ngOnInit(): void {
        this.hash = this.route.snapshot.paramMap.get("hash")!;
        this.getPaymentDetailsByHash();
        // this.isRenew = this.route.snapshot.queryParamMap.get("isRenew") === "true";
        this.mpinForm = this.fb.group({
            countryCode: [this.countryCode],
            amount: ["", [
                Validators.required,
                Validators.min(1),
                Validators.max(5000000),
                Validators.pattern('^[0-9]*$') // only digits allowed
            ]],
            mobileNumber: ["", [Validators.required]]
        });

    }

    paymentData(isFromEnter?) {
        this.submitted = true;
        if (this.mpinForm.invalid) {
            this.mpinForm.markAllAsTouched()
            this.mpinForm.get('mobileNumber')?.reset();
            return;
        }
        if (this.mpinForm.valid) {
            let mobileNo = this.mpinForm.controls["mobileNumber"].value;
            let url = "/gateway/getGatewayFromPrefix?mobileNumber=" + mobileNo;
            this.isGatewayFetching = true;
            this.customerdetailsilsService.intigrationGetMethod(url, this.paymentLinkToken).subscribe(
                (response: any) => {
                    this.isGatewayFetching = false;
                    if (response?.responseCode == 500) {
                        this.toastr.error(`${response.responseMessage}`, 'Failed!');
                    }
                    if (response.data == null || response.data == undefined || response.data == "") {
                        if (this.paymentGateWays.includes("MPESA")) {
                            this.selectedPaymentGateway = "MPESA";
                        }
                        else {
                            this.selectedPaymentGateway = this.paymentGateWays[0];
                        }
                    }
                    else {
                        this.selectedPaymentGateway = response.data;
                    }
                    if (isFromEnter) {
                        this.mobileNoInput.nativeElement.blur();
                    }
                    this.spinner.hide();
                },
                (error: any) => {
                    this.isGatewayFetching = false;
                    this.spinner.show();
                    this.toastr.error(`${error.responseMessage}`, ' "Something went wrong with the payment screen. Please contact your administrator."!');
                    this.spinner.hide();
                }
            );
        }
    }

    getPaymentDetailsByHash() {
        this.spinner.show();
        if (this.hash == null || this.hash == undefined || this.hash == "") {
            this.errorMessage = "This link is expired! Please regenerate a new link.";
            this.spinner.hide();
            return;
        } else {
            let url = "/open/getPaymentDetailsByHash?hash=" + this.hash;
            this.customerdetailsilsService.getMethodForPay(url).subscribe(
                (response: any) => {
                    if (response?.status == 226) {
                        this.errorMessage = response.message;
                    } else if (response?.status == 204) {
                        this.errorMessage = response.message;
                    } else {
                        this.customerDetailData = response.paymentDetails;
                        this.mpinForm.controls["amount"].setValue(this.customerDetailData.amount);
                        let token = this.customerDetailData?.token;
                        this.paymentLinkToken = token;
                        localStorage.setItem("payLinkToken", token);
                        this.systemService.getConfigurationByNamePayLink("PAYMENT_GATEWAY_FOR_ADMIN").subscribe((res: any) => {
                            if (!(res?.data?.value == null || res?.data?.value == undefined || res?.data?.value == "")) {
                                this.paymentGateWays = res.data.value.split(",").map((gateway: string) => gateway.trim());
                            }
                        });
                        this.systemService.getConfigurationByNamePayLink("COUNTRY_CODE").subscribe((res: any) => {
                            this.countryCode = res.data.value;
                            this.mpinForm.controls["countryCode"].setValue(this.countryCode);
                        });
                        this.systemService.getConfigurationByNamePayLink("CURRENCY_FOR_PAYMENT").subscribe((res: any) => {
                            this.currency = res.data.value;
                        });
                        this.errorMessage = "";
                    }
                    this.spinner.hide();
                },
                (error: any) => {
                    this.spinner.show();
                    this.errorMessage =
                        "Something went wrong with the payment screen. Please contact your administrator.";
                    this.spinner.hide();
                }
            );
        }
    }

    buyMomoInvoicePayment() {
        this.exitBuy = true;
        this.paymentstatusCount = RadiusConstants.TIMER_COUNT;
        let data = {
            customerId: this.customerDetailData.custId,
            amount: this.mpinForm.value.amount.toString(),
            isFromCaptive: false,
            merchantName: "MoMo Pay",
            customerUserName: this.customerDetailData.customerUsername,
            customerUUID: uuid.v4(),
            mvnoId: this.customerDetailData.mvnoId,
            mobileNumber:
                this.mpinForm.value.countryCode.replace("+", "") + (this.mpinForm.value.mobileNumber ?? ""),
            partnerId: this.customerDetailData?.partnerid,
            hash: this.hash,
            accountNumber: this.customerDetailData?.accountNumber ?? ""
        };
        this.customerdetailsilsService.buyPlanUsingMomo(data).subscribe(
            (response: any) => {
                this.spinner.hide();
                //localStorage.setItem("transactionId"),
                localStorage.setItem("transactionId", response.data.data.orderId),
                    this.mpinForm.reset();
                this.submitted = false;
                // this.paymentConfirmationModal = true;
                this.paymentConfDialogOpen();
                this.exitBuy = false;

                // this.subscription2 = this.obs1$.subscribe(d => {
                //   if (this.paymentstatusCount > 0) {
                //     this.paymentstatusCount = this.paymentstatusCount - 1;
                //     this.getStatusSuccessByMomo("SUCCESSFUL");
                //     if (this.transactionStatus === true) {
                //       this.subscription2.unsubscribe();
                //     }
                //   }
                //   if (this.paymentstatusCount == 0) {
                //     this.subscription2.unsubscribe();
                //   }
                // });
                this.spinner.hide();
            },
            (error: any) => {
                this.spinner.hide();
                this.toastr.error(`${error.error.ERROR}`, 'Something went wrong"!');


            }
        );
    }

    getStatusSuccessByMomo(status) {
        this.spinner.hide();
        let data = {
            orderId: localStorage.getItem("transactionId"),
            status: status
        };
        this.customerdetailsilsService.getIntigrationTransactionstatus(data).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    if (response.data.istransactionsuccess === "true") {
                        this.transactionStatus = response.istransactionsuccess;
                        // this.getDevice(data);
                        // this.paymentConfirmationModal = false;
                        this.paymentConfDialogOpen();
                        this.subscription2.unsubscribe();
                        // this.paymentSucessModel = true;
                        this.paymentConfDialogOpen();
                    }
                }
            },
            (error: any) => {
                this.spinner.hide();
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }

    airtelPayPlan() {
        this.exitBuy = true;
        this.paymentstatusCount = RadiusConstants.TIMER_COUNT;
        let data = {
            customerId: this.customerDetailData.custId,
            amount: this.mpinForm.value.amount.toString(),
            isFromCaptive: false,
            merchantName: "AIRTEL",
            customerUserName: this.customerDetailData.customerUsername,
            mvnoId: this.customerDetailData.mvnoId,
            mobileNumber: this.mpinForm.value.mobileNumber ?? "",
            partnerId: this.customerDetailData.partnerid,
            hash: this.hash,
            accountNumber: this.customerDetailData?.accountNumber ?? ""
        };
        this.customerdetailsilsService.buyPlanUsingAirtel(data).subscribe(
            (response: any) => {
                this.spinner.hide();
                // this.mpinForm.reset();
                this.submitted = false;
                // this.paymentConfirmationModal = true;
                this.paymentConfDialogOpen();
                //localStorage.setItem("transactionId"),
                if (response.responseCode === 417) {
                    this.toastr.info(`${response.responseMessage}`, 'Info!');

                    return;
                }
                if (response.data?.data?.transaction?.id) {

                    localStorage.setItem("transactionId", response.data.data.transaction.id)
                    this.exitBuy = false;
                }


                // this.subscription2 = this.obs1$.subscribe(d => {
                //   if (this.paymentstatusCount > 0) {
                //     this.paymentstatusCount = this.paymentstatusCount - 1;
                //     this.getStatusSuccessByMomo("SUCCESSFUL");
                //     if (this.transactionStatus === true) {
                //       this.subscription2.unsubscribe();
                //     }
                //   }
                //   if (this.paymentstatusCount == 0) {
                //     this.subscription2.unsubscribe();
                //   }
                // });
                this.spinner.hide();
            },
            (error: any) => {
                this.spinner.hide();
                this.toastr.error(`${error.error.ERROR}`, 'Something went wrong!');


            }
        );
    }

    hidepaymentConfirmDialog() {
        this.dialogRef.close();
        // this.paymentConfirmationModal = false;
    }

    hidepaymentSucessDialog() {
        // this.paymentSucessModel = false;
        this.dialogRefS.close();
    }

    paymentConfDialogOpen() {
        this.dialogRef = this.dialog.open(this.paymentConfirmationModal, {
            width: '1000px',
            maxWidth: '70vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });

        this.dialogRef.afterClosed().subscribe(result => {
            this.dialogRef = null;
        });
    }
    paymentsussDialogOpen() {
        this.dialogRef = this.dialog.open(this.paymentSucessModel, {
            width: '1000px',
            maxWidth: '70vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });

        this.dialogRef.afterClosed().subscribe(result => {
            this.dialogRefS = null;
        });
    }

    onMobileNumberFocusOut(isFromEnter?) {
        if (this.enteredMobileNumber !== this.mpinForm.controls["mobileNumber"].value) {
            if (isFromEnter) {
                this.paymentData(true);
            }
            else {
                this.paymentData();
            }
            this.enteredMobileNumber = this.mpinForm.controls["mobileNumber"].value;
        }
    }

    payNow() {
        this.submitted = true;
        if (this.mpinForm.invalid) {
            this.mpinForm.markAllAsTouched();
            return;
        }
        if (!this.selectedPaymentGateway) {
            // if (this.isGatewayFetching) {
            //     this.toastr.info('Please wait, fetching payment gateway...', 'Please wait!');
            // } else {
            //     this.toastr.warning('Please enter a mobile number to detect the payment gateway.', 'Gateway not selected!');
            // }
            return;
        }
        this.proceedWithPayment();
    }

    proceedWithPayment() {
        if (this.selectedPaymentGateway === "MoMo Pay") {
            this.buyMomoInvoicePayment();
        } else if (this.selectedPaymentGateway === "AIRTEL") {
            this.airtelPayPlan();
        } else if (this.selectedPaymentGateway === "MPESA") {
            this.buyMpesaInvoicePayment();
        } else {
            this.toastr.error('Selected payment gateway is not supported!', 'Error!');
        }
    }

    buyMpesaInvoicePayment() {
        this.customerdetailsilsService.buyPlanUisngMpesaExpressPayLink({
            customerId: this.customerDetailData.custId,
            amount: this.mpinForm.value.amount.toString(),
            customerUserName: this.customerDetailData.customerUsername,
            mvnoId: this.customerDetailData.mvnoId,
            payerMobileNumber: (this.mpinForm.value.countryCode.replace('+', '') ?? '91') + (this.mpinForm.value.mobileNumber ?? ""),
            accountNumber: this.customerDetailData?.accountNumber ?? "",
        }).subscribe({
            next: () => {
                this.submitted = false;
                this.paymentConfDialogOpen();
            },
            error: (error: any) => {
                this.submitted = false;
                this.toastr.error(`${error.error.error}`, 'Something went wrong with MPESA payment!');
            }

        })
    }

    checkPaymentDetails(event: KeyboardEvent) {
        this.onMobileNumberFocusOut(true);
    }

}
