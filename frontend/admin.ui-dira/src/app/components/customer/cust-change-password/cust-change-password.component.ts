import { DatePipe } from "@angular/common";
import { Component, OnInit, Input, Output, EventEmitter, ViewChild, TemplateRef, AfterViewInit } from "@angular/core";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService, MessageService } from "primeng/api";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { InvoicePaymentListService } from "src/app/service/invoice-payment-list.service";
import { LiveUserService } from "src/app/service/live-user.service";
import { LoginService } from "src/app/service/login.service";
import { ActivatedRoute, Router } from "@angular/router";
import { UntypedFormBuilder, UntypedFormGroup, Validators, AbstractControl, ValidationErrors } from "@angular/forms";
import { ToastrService } from 'ngx-toastr';
import { MatDialog } from "@angular/material/dialog";

export function MustMatch(controlName: string, matchingControlName: string) {
    return (formGroup: UntypedFormGroup) => {
        const control = formGroup.controls[controlName];
        const matchingControl = formGroup.controls[matchingControlName];

        if (matchingControl.errors && !matchingControl.errors.mustMatch) {
            return;
        }

        if (control.value !== matchingControl.value) {
            matchingControl.setErrors({ mustMatch: true });
        } else {
            matchingControl.setErrors(null);
        }
    };
}

declare var $: any;
@Component({
    selector: "app-cust-change-password",
    templateUrl: "./cust-change-password.component.html",
    styleUrls: ["./cust-change-password.component.scss"],
    standalone: false
})
export class CustChangePasswordComponent implements OnInit {
    @Input() custId: any;
    @Output() closePassChange = new EventEmitter();
    @ViewChild('changePasswordModal') changePasswordModal: TemplateRef<any>;
    changePasswordForm: UntypedFormGroup;
    _passwordOLDType = "password";
    _passwordNewType = "password";
    _passwordConfirmType = "password";
    showNewPassword = false;
    showOLDPassword = false;
    showConfirmPassword = false;
    displayChangePassword: boolean = false;
    constructor(
        private toastr: ToastrService,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService, private dialog: MatDialog,
        private customerManagementService: CustomermanagementService,
        public commondropdownService: CommondropdownService,
        public datepipe: DatePipe,
        public loginService: LoginService,
        public invoicePaymentListService: InvoicePaymentListService,
        private datePipe: DatePipe,
        private route: ActivatedRoute,
        private router: Router,
        private liveUserService: LiveUserService
    ) { }
    async ngOnInit() {
        this.changePasswordForm = this.fb.group({
            custId: [""],
            newpassword: ["", Validators.required],
            confirmNewPassword: ["", Validators.required],
            password: [""],
            remarks: [""],
            selfcarepwd: [""],
        },
            {
                validator: MustMatch("newpassword", "confirmNewPassword")
            }
        );

    }

    ngAfterViewInit(): void {
        this.dialog.open(this.changePasswordModal, {
            width: '400px',
            disableClose: true
        });
    }

    closeDialog() {
        this.dialog.closeAll();
        this.closePassChange.emit();
    }

    toggleNewPasswordVisibility() {
        this.showNewPassword = !this.showNewPassword;
        this._passwordNewType = this.showNewPassword ? "text" : "password";
    }

    toggleConfirmPasswordVisibility() {
        this.showConfirmPassword = !this.showConfirmPassword;
        this._passwordConfirmType = this.showConfirmPassword ? "text" : "password";
    }

    changePasswordWithpopup() {
        if (this.changePasswordForm.valid) {
            this.confirmationService.confirm({
                message: "Do you want to change password for this customer?",
                header: "Change Password Confirmation",
                icon: "pi pi-info-circle",
                accept: () => {
                    this.changePassword();
                },
                reject: () => {
                    error: (error) => {
                        this.toastr.info(`${error.responseMessage}`, 'You have rejected!');
                    }
                },
            });
        }
    }
    // change Password
    changePassword() {
        const url = "/updatePassword";
        if (this.changePasswordForm.valid) {
            this.changePasswordForm.value.custId = this.custId;
            this.changePasswordForm.value.remarks = "";
            this.changePasswordForm.value.selfcarepwd = this.changePasswordForm.value.newpassword;
            let changePasswordvalue = this.changePasswordForm.value;
            this.customerManagementService.PostSubMethod(url, changePasswordvalue).subscribe(
                (response: any) => {
                    if (response.responseCode == 417) {
                        this.toastr.info(`${response.responseMessage}`, 'Info!');
                    } else {
                        this.clearChangePasswordForm();
                        this.toastr.success(`${response.responseMessage}`, 'Password Update Successfully!');
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');
                }
            );
            // }
        }
    }
    clearChangePasswordForm() {
        this.changePasswordForm.reset();
    }
    close() {
        this.closePassChange.emit();
    }
}
