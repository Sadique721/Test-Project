import { Component, Inject, Input, OnInit } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { ConfirmationService, MessageService } from "primeng/api";
import { Observable, Observer } from "rxjs";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { LoginService } from "src/app/service/login.service";
import { RoleService } from "src/app/service/role.service";
import { ActivatedRoute, Router } from "@angular/router";
import { PasswordPolicyService } from "src/app/service/password-policy/password-policy.service";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { ToastrService } from "ngx-toastr";

@Component({
    selector: "app-password-create",
    templateUrl: "./password-create.component.html",
    styleUrls: ["./password-create.component.css"],
    standalone: false
})
export class PasswordCreateComponent implements OnInit {

    @Input() editPasswordId = ''
    passwordPolicyForm: UntypedFormGroup;
    isEdit: boolean = false;
    submitted: boolean = false;
    passwordData: any;
    viewpasswordData: any;
    searchData: any;
    roleList: any[] = [{ id: "", rolename: "" }];
    statusOptions = RadiusConstants.status;
    //   editPasswordId: any;
    public loginService: LoginService;


    constructor(
        private dialogRef: MatDialogRef<PasswordCreateComponent>,
        private fb: UntypedFormBuilder,
        private roleService: RoleService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private toastr: ToastrService,
        private PasswordPolicyService: PasswordPolicyService,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private route: ActivatedRoute,
        loginService: LoginService,
        private router: Router
    ) {
        this.loginService = loginService;
        // this.editPasswordId = this.route.snapshot.paramMap.get("mvnoId")!;
    }

    async ngOnInit() {
        if (this.editPasswordId != null) {
            this.isEdit = false;
            this.getPasswordById(this.editPasswordId);
        }
        this.passwordPolicyForm = this.fb.group({
            name: ["", Validators.required],
            status: ["", Validators.required],
            min_length: ["", Validators.required],
            max_length: ["", Validators.required],
            expiration_days: ["", Validators.required],
            disable_recycling_prevention: ["", Validators.required],
            disable_account_lockout: ["", Validators.required],
            pattern: ["", Validators.required],
            pattern_description: ["", Validators.required],
            mvnoId: [""],
            isDelete: [false],
            isNotificationRequired: [false]
        });
        if (this.data?.edit && this.data?.passwordId) {
            this.isEdit = true;
            this.getPasswordById(this.data.passwordId);
        }
        else if (this.editPasswordId && this.editPasswordId !== '') {
            this.isEdit = true;
            this.getPasswordById(this.editPasswordId);
        }

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
        this.getAllRole();
    }
    onlyNumberKey(event: KeyboardEvent) {
        let specialKeys: Array<string> = [
            "Backspace",
            "Tab",
            "End",
            "Home",
            "ArrowLeft",
            "ArrowRight",
            "ArrowUp",
            "ArrowDown"
        ];

        if (specialKeys.indexOf(event.key) !== -1) {
            return;
        }

        let current: string = event.currentTarget["value"];
        let next: string = current.concat(event.key);

        // Allow only non-zero positive integers
        if (next && !String(next).match(/^[1-9]\d*$/)) {
            event.preventDefault();
        }
    }

    getAllRole() {
        this.roleService.getAll().subscribe(
            (response: any) => {
                // this.roleList = response.dataList.filter(
                //   (role) => role.product === "BSS"
                // );
                this.roleList = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
            }
        );
    }

    addEditPassword(id) {
        this.submitted = true;
        this.passwordPolicyForm.markAllAsTouched();

        if (this.passwordPolicyForm.valid) {
            if (id) {
                const url = "/passwordPolicy/update/" + id;
                this.passwordData = this.passwordPolicyForm.value;
                this.passwordData.id = id;
                this.PasswordPolicyService.updateMethod(url, this.passwordData).subscribe(
                    (response: any) => {
                        if (response.status === 200) {
                            this.submitted = false;
                            this.isEdit = false;
                            this.passwordPolicyForm.reset();
                            this.dialogRef.close('refresh');
                            this.toastr.success("Successfull Updated", 'Success!');


                            this.submitted = false;
                            this.router.navigate(["/home/password-policy/list"]);
                        } else {
                            this.toastr.info(`${response.responseMessage}`, 'Info!');

                        }
                    },
                    (error: any) => {
                        // console.log(error, "error")
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                    }
                );
            } else {
                const url = "/passwordPolicy/create";
                this.passwordData = this.passwordPolicyForm.value;
                // console.log("this.createChargeData", this.countryData);
                this.PasswordPolicyService.postMethod(url, this.passwordData).subscribe(
                    (response: any) => {
                        if (response.status === 200) {
                            this.submitted = false;
                            this.passwordPolicyForm.reset();
                            this.dialogRef.close('refresh');
                            this.toastr.success("Successfull Created", 'Success!');

                            this.router.navigate(["/home/password-policy/list"]);
                        } else {
                            this.toastr.info("", 'Info!');

                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                    }
                );
            }
        }
    }

    getPasswordById(id) {
        if (id) {
            const url = "/passwordPolicy/get/" + id;
            this.PasswordPolicyService.getMethod(url).subscribe(
                (response: any) => {
                    this.isEdit = true;
                    this.viewpasswordData = response.passwordList;
                    this.passwordPolicyForm.patchValue(this.viewpasswordData);
                },
                (error: any) => {
                    // console.log(error, "error")
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!');

                }
            );
        }
    }

    canExit() {
        if (!this.passwordPolicyForm.dirty) return true;
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
    onCancel(): void {
        this.dialogRef.close();
    }

}
