import { Component, OnInit } from "@angular/core";
import { UntypedFormBuilder, Validators, UntypedFormGroup } from "@angular/forms";
import { MessageService } from "primeng/api";
import { Title } from "@angular/platform-browser";
import { TITLE } from "../../RadiusUtils/RadiusConstants";
import { ActivatedRoute, Router } from "@angular/router";
import { LoginService } from "src/app/service/login.service";
import { ToastrService } from 'ngx-toastr';

@Component({
    selector: "app-reset-password",
    templateUrl: "./reset-password.component.html",
    styleUrls: ["./reset-password.component.css"],
    standalone: false
})
export class ResetPasswordComponent implements OnInit {
    generatePasswordForm: UntypedFormGroup;
    submitted = false;
    showPassword: boolean = false;
    uuId: any;
    staffId: string;

    constructor(
        private toastr: ToastrService,
        private fb: UntypedFormBuilder,
        private titleService: Title,
        private route: ActivatedRoute,
        private loginService: LoginService,
        private messageService: MessageService,
        private router: Router
    ) {
        this.uuId = this.route.snapshot.paramMap.get("uuId")!;
        this.staffId = this.route.snapshot.paramMap.get("staffId")!;
    }

    ngOnInit(): void {
        localStorage.setItem("hostName", window.location.hostname);
        this.titleService.setTitle(TITLE);
        this.generatePasswordForm = this.fb.group(
            {
                userName: ["", Validators.required],
                oldPassword: ["", Validators.required],
                newPassword: ["", Validators.required],
                confirmpassword: ["", Validators.required],
            },
            {
                validator: this.passwordMatchValidator,
            }
        );
    }

    passwordMatchValidator(form: UntypedFormGroup) {
        return form.controls["newPassword"].value ===
            form.controls["confirmpassword"].value
            ? null
            : { mismatch: true };
    }

    savePassword() {
        this.submitted = true;
        if (this.generatePasswordForm.invalid) {
            return;
        }

        this.loginService.resetPassword(this.generatePasswordForm.value).subscribe(
            (response: any) => {
                if (response.status == 200) {

                    this.toastr.success(`${response.msg}`, 'Success!');


                    setTimeout(() => {
                        this.router.navigate(["/login"]);
                    }, 1000);
                }
            },
            (error) => {
                if (error.error.status === 409) {

                    this.toastr.info(`${error.error.msg}`, 'Info!');

                } else if (error.error.status === 417) {
                    this.toastr.info(`${error.error.responseMessage}`, 'Info!');

                } else {
                    this.toastr.error(`${error.ERROR}`, 'Failed!');

                }
            }
        );
    }
}
