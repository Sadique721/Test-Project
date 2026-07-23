import { Component, OnInit, ViewChild } from "@angular/core";
import { SmsConfigService } from "src/app/service/sms-config.service";
import { UntypedFormBuilder, Validators, UntypedFormGroup, NgForm, UntypedFormArray, FormGroup } from "@angular/forms";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { RadiusUtility } from "src/app/RadiusUtils/RadiusUtility";
import { ConfirmationService, MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { SavbillCommonBaseService } from "src/app/service/savbill-common-base.service";
import { NOTIFICATIONS } from "src/app/constants/aclConstants";
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { AbstractControl } from '@angular/forms';
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { ToastrService } from "ngx-toastr";
@Component({
    selector: "app-sms-config",
    templateUrl: "./sms-config.component.html",
    styleUrls: ["./sms-config.component.css"],
    standalone: false
})
export class SmsConfigComponent implements OnInit {

    attributeDataSource = new MatTableDataSource<AbstractControl>();
    displayedColumnsAttr: string[] = ['parameter', 'value', 'delete'];
    @ViewChild('smsDialogTemplate') smsDialogTemplate: any;
    dialogRef!: MatDialogRef<any>;
    displayedColumns: string[] = ['smsUrl', 'status', 'mvnoName', 'action'];
    smsConfigData: any[] = [];
    // itemsPerPage = 10;
    totalRecords = 0;
    public loginService: LoginService;
    AclClassConstants;
    AclConstants;
    editGroupData = {
        smsConfigId: "",
        smsUrl: "",
        configStatus: ""
    };
    editAttributeValues: any;
    attribute: UntypedFormArray;
    changeStatusData: any = [];
    groupData: any = [];
    editForm: UntypedFormGroup;
    smsConfigId: number;
    submitted = false;
    //Used and required for pagination
    // totalRecords: number;
    currentPage = 1;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;

    //Used to store error data and error message
    errorData: any = [];
    errorMsg = "";
    configStatus: boolean;
    editMode: boolean = false;
    editFormValues: any;
    update: boolean = true;
    isSmsConfigEdit = true;

    pageITEM = RadiusConstants.ITEMS_PER_PAGE;
    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage = 1;
    createAccess: boolean = false;
    editAccess: boolean = false;
    constructor(
        private toastr: ToastrService,
        private dialog: MatDialog,
        private messageService: MessageService,
        private smsConfigService: SmsConfigService,
        private radiusUtility: RadiusUtility,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        public savbillCommonBaseService: SavbillCommonBaseService,
        loginService: LoginService
    ) {
        this.loginService = loginService;
        this.createAccess = loginService.hasPermission(NOTIFICATIONS.SMS_CONFIG_CREATE);
        this.editAccess = loginService.hasPermission(NOTIFICATIONS.SMS_CONFIG_EDIT);
    }

    isActive: boolean = true;
    activeValue: any = true;
    inactiveValue: any = false;
    loginmvnoid: any = JSON.parse(localStorage.getItem("mvnoId"));
    dropdownOptions: any[] = [
        { label: "Active", value: true },
        { label: "Inactive", value: false }
        // Add other options as needed
    ];
    ngOnInit(): void {
        this.editForm = this.fb.group({
            smsUrl: ["", Validators.required],
            configStatus: ["", Validators.required]
        });
        this.attribute = this.fb.array([]);
        this.getCurrentStaffBUId();
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(NOTIFICATIONS.SMS_CONFIG_EDIT)) {
            return ['smsUrl', 'status', 'mvnoName', 'action'];
        } else {
            return ['smsUrl', 'status', 'mvnoName'];
        }
    }
    //Properties of Confirmation Popup
    popoverTitle: string = RadiusConstants.CONFIRM_DIALOG_TITLE;
    popoverMessage: string = RadiusConstants.DELETE_GROUP_CONFIRM_MESSAGE;
    confirmedClicked: boolean = false;
    cancelClicked: boolean = false;
    closeOnOutsideClick: boolean = true;
    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPage > 1) {
            this.currentPage = 1;
        }
        this.findAll(this.showItemPerPage);
    }

    async findAll(size) {
        let page_list;
        if (size) {
            page_list = size;
            this.itemsPerPage = size;
        } else {
            if (this.showItemPerPage == 1) {
                this.itemsPerPage = this.pageITEM;
            } else {
                this.itemsPerPage = this.showItemPerPage;
            }
        }
        this.smsConfigService.findAll(this.currentStaffBuid).subscribe(
            (response: any) => {
                this.groupData = response;
                const startIndex = (this.currentPage - 1) * this.itemsPerPage;
                const endIndex = startIndex + this.itemsPerPage;

                this.smsConfigData = (this.groupData?.smsConfigList || []).slice(startIndex, endIndex);

                this.totalRecords = this.groupData?.smsConfigList?.length || 0;

                // if (this.groupData.smsConfigList !== null && this.groupData.smsConfigList.length > 1) {
                //   console.log("enter in groupdata find all sms config");
                //   this.editMode = true;
                // }
                const filteredLength = this.groupData.smsConfigList.filter(
                    item => item.mvnoId === this.loginmvnoid
                ).length;
                if (filteredLength >= 2) {
                    this.editMode = true;
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );
    }

    async editSmsConfig() {
        this.submitted = true;
        if (this.editMode) {
            if (this.editForm.valid) {
                if (
                    this.editForm.value == this.editFormValues &&
                    JSON.stringify(this.attribute.value) === JSON.stringify(this.editAttributeValues)
                ) {
                    this.editMode = true;
                    this.submitted = false;
                    this.isSmsConfigEdit = true;

                    this.editForm.reset();
                    this.attribute = this.fb.array([]);
                    this.attribute.controls = [];
                    // this.onAddAttribute();
                    this.toastr.success("Profile data is same", 'Success!');

                } else if (
                    this.editForm.value != this.editFormValues &&
                    JSON.stringify(this.attribute.value) === JSON.stringify(this.editAttributeValues)
                ) {
                    this.updateSmsConfig();
                } else if (
                    this.editForm.value == this.editFormValues &&
                    JSON.stringify(this.attribute.value) !== JSON.stringify(this.editAttributeValues)
                ) {
                    this.updateSmsConfigMapping(this.selectedSmsConfigId);
                } else {
                    this.update = false;
                    this.updateSmsConfig();
                    this.updateSmsConfigMapping(this.selectedSmsConfigId);
                    this.editMode = true;
                    this.toastr.success("CoA/DM profile and attribute has been updated successfully.", 'Success!');

                }
            }
        } else {
            if (this.editForm.valid) {
                if (
                    this.editForm.value == this.editFormValues &&
                    JSON.stringify(this.attribute.value) === JSON.stringify(this.editAttributeValues)
                ) {
                    this.editMode = false;
                    this.submitted = false;
                    this.editForm.reset();
                    this.attribute = this.fb.array([]);
                    this.attribute.controls = [];
                    // this.onAddAttribute();
                    this.toastr.success("Profile data is same", 'Success!');

                } else if (
                    this.editForm.value != this.editFormValues &&
                    JSON.stringify(this.attribute.value) === JSON.stringify(this.editAttributeValues)
                ) {
                    this.addSmsConfig();
                } else if (
                    this.editForm.value == this.editFormValues &&
                    JSON.stringify(this.attribute.value) !== JSON.stringify(this.editAttributeValues)
                ) {
                    this.addSmsConfigMapping(this.selectedSmsConfigId);
                } else {
                    this.update = false;
                    this.addSmsConfig();
                    this.editMode = true;
                    this.toastr.success("SMS config add successfully.", 'Success!');

                }
            }
        }
    }

    async updateSmsConfigMapping(selectedSmsConfigId) {
        this.smsConfigService
            .updateSmsConfigMapping(this.attribute.value, selectedSmsConfigId)
            .subscribe(
                (response: any) => {
                    this.editMode = false;
                    this.submitted = false;
                    this.editForm.reset();
                    this.attribute = this.fb.array([]);
                    this.attribute.controls = [];
                    // this.onAddAttribute();
                    if (this.update) {
                        this.toastr.success(`${response.message}`, 'Success!');

                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

                }
            );
    }
    async updateSmsConfig() {
        // if (this.editCoaDMData) this.editCoaDMData = this.editForm.value;
        // this.editCoaDMData.coaDMProfileId = this.editCoAId;
        const updatedGroupData = {
            smsConfigId: this.editGroupData.smsConfigId,
            smsUrl: this.editForm.value.smsUrl,
            configStatus: this.editForm.value.configStatus
        };
        this.smsConfigService.updateSmsConfig(updatedGroupData).subscribe(
            (response: any) => {
                this.editMode = false;
                this.submitted = false;
                this.findAll("");
                this.editForm.reset();
                this.dialogRef.close();
                if (this.update) {
                    this.toastr.success(`${response.message}`, 'Success!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );
    }
    async addSmsConfig() {
        // if (this.editCoaDMData) this
        // .editCoaDMData = this.editForm.value;
        // this.editCoaDMData.coaDMProfileId = this.editCoAId;
        this.submitted = true;

        const updatedGroupData = {
            smsUrl: this.editForm.value.smsUrl,
            configStatus: this.editForm.value.configStatus
        };
        this.smsConfigService.addSmsConfig(updatedGroupData, this.currentStaffBuid).subscribe(
            (response: any) => {
                this.selectedSmsConfigId = response.smsConfig.smsConfigId;
                this.submitted = false;
                this.addSmsConfigMapping(this.selectedSmsConfigId);
                this.findAll("");
                this.editForm.reset();
                this.editMode = true;
                location.reload();
                this.dialogRef.close();
                if (this.update) {
                    this.toastr.success(`${response.message}`, 'Success!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );
    }

    async addSmsConfigMapping(selectedSmsConfigId) {

        this.smsConfigService.addSmsConfigMapping(this.attribute.value, selectedSmsConfigId).subscribe(
            (response: any) => {
                this.editMode = false;
                this.submitted = false;
                this.editForm.reset();
                this.attribute = this.fb.array([]);
                this.attribute.controls = [];
                // this.onAddAttribute();
                this.dialogRef.close();
                if (this.update) {
                    this.toastr.success(`${response.message}`, 'Success!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');
            }
        );
    }

    // async editSmsConfig() {
    //   this.submitted = true;
    //   if (this.editForm.valid) {
    //
    //     const updatedGroupData = {
    //       smsConfigId: this.editGroupData.smsConfigId,
    //       smsUrl: this.editGroupData.smsUrl,
    //     };
    //     this.smsConfigService.updateSmsConfig(updatedGroupData).subscribe(
    //       (response: any) => {
    //         this.submitted = false;
    //         this.editForm.reset();
    //         this.findAll('');
    //         this.messageService.add({
    //           severity: 'success',
    //           summary: 'Successfully',
    //           detail: 'Updated Successfuly',
    //           icon: 'far fa-check-circle',
    //         });
    //         this.editMode = false;
    //
    //       },
    //       (error: any) => {
    //         this.messageService.add({
    //           severity: 'error',
    //           summary: error.error.errorMessage,
    //           detail: error.error.errorMessage,
    //           icon: 'far fa-times-circle',
    //         });
    //
    //       }
    //     );
    //   }
    // }
    selectedSmsConfigId: any = "";

    async editConfigById(smsConfigId, index) {
        this.dropdownOptions;
        this.editMode = true;
        this.isSmsConfigEdit = false;
        this.selectedSmsConfigId = smsConfigId;
        index = this.radiusUtility.getIndexOfSelectedRecord(index, this.currentPage, this.itemsPerPage);
        this.editForm.patchValue({
            smsUrl: this.groupData.smsConfigList[index].smsUrl,
            configStatus: this.groupData.smsConfigList[index].configStatus
        });

        this.editGroupData = {
            smsConfigId: this.groupData.smsConfigList[index].smsConfigId,
            smsUrl: this.groupData.smsConfigList[index].smsUrl,
            configStatus: this.groupData.smsConfigList[index].configStatus
        };

        this.attribute = this.fb.array([]);

        this.smsConfigService.getSmsConfigMappings(smsConfigId).subscribe(
            (response: any) => {
                let attributeList = response.smsConfigMappingList;
                attributeList.forEach(element => {
                    this.attribute.push(this.fb.group(element));
                });

                this.editAttributeValues = response.smsConfigMappingList;
                this.editFormValues = this.editForm.value;
                this.attributeDataSource.data = this.attribute.controls as FormGroup[];
                this.dialogRef = this.dialog.open(this.smsDialogTemplate, {
                    width: '900px',
                    maxHeight: '90vh',
                    disableClose: true
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');

            }
        );
    }

    async clearSearchForm() {
        this.currentPage = 1;
        this.findAll("");
    }
    deleteConfirmAttribute(attributeIndex: number, smsConfigMappingId: number) {
        const item = this.attribute.at(attributeIndex).value;

        const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
            width: '400px',
            data: {
                title: 'Delete SMS Parameter',
                description: `Are you sure you want to delete "${item.parameter}"?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.onRemoveAttribute(attributeIndex, smsConfigMappingId);
            } else {
                this.toastr.info('You have rejected', 'Info!');

            }
        });
    }


    async onRemoveAttribute(attributeIndex: number, smsConfigMappingId: number) {

        this.attribute.removeAt(attributeIndex);
        this.attributeDataSource.data = this.attribute.controls;


        this.smsConfigService.deleteSmsConfigByAttributeId(smsConfigMappingId).subscribe(
            (response: any) => {
                this.toastr.success(`${response.message}`, 'Success!');

            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Failed!');


                // If backend failed, optionally re-add the row
                // this.attribute.insert(attributeIndex, this.fb.group(previousData));
                // this.attributeDataSource.data = this.attribute.controls;
            }
        );
    }

    onAddAttribute() {
        const newAttr = this.createAttributeFormGroup();
        this.attribute.push(newAttr);

        this.attributeDataSource.data = this.attribute.controls;

    }

    createAttributeFormGroup(): UntypedFormGroup {
        return this.fb.group({
            parameter: ['', Validators.required],
            value: ['', Validators.required],
            smsConfigId: [''],
            smsConfigMappingId: ['']
        });
    }
    pageChanged(event: any) {
        this.currentPage = event.pageIndex + 1;
        this.itemsPerPage = event.pageSize;

        const startIndex = (this.currentPage - 1) * this.itemsPerPage;
        const endIndex = startIndex + this.itemsPerPage;

        this.smsConfigData = (this.groupData?.smsConfigList || []).slice(startIndex, endIndex);
    }


    currentLoginStaffData: any;
    currentStaffBuid: any;
    async getCurrentStaffBUId() {

        const url = "/staffuser/" + localStorage.getItem("userId");
        this.savbillCommonBaseService.get(url).subscribe(
            async (response: any) => {
                if (response.status == 200) {
                    this.currentLoginStaffData = await response.Staff;
                    this.currentStaffBuid = this.currentLoginStaffData?.businessUnitIdsList || [];
                    if (!Array.isArray(this.currentStaffBuid)) {
                        this.currentStaffBuid = [];
                    }
                    if (this.currentStaffBuid.length === 1) {
                        this.currentStaffBuid = this.currentStaffBuid[0];
                        this.findAll("");
                    } else if (this.currentStaffBuid.length === 0) {
                        this.currentStaffBuid = 0;
                        this.findAll("");
                    } else if (this.currentStaffBuid.length > 1) {
                        this.currentStaffBuid = 0;
                        this.findAll("");
                        this.toastr.info("Multiple BU found in given staff", 'Info!');

                    }
                } else {
                    this.toastr.error(`${response.errorMessage}`, 'Failed!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error?.ERROR || "An unexpected error occurred"}`, 'Failed!');

            }
        );
    }
    opensmsDialog(edit: boolean = false, smsData: any = null) {
        this.editMode = edit;


        this.editForm.reset();
        this.attribute = this.fb.array([]);
        this.attributeDataSource.data = []

        if (edit && smsData) {

            this.editForm.patchValue({
                smsUrl: smsData.smsUrl,
                configStatus: smsData.configStatus
            });

            this.attribute = this.fb.array([]);
            if (smsData.smsConfigId) {
                this.selectedSmsConfigId = smsData.smsConfigId;
                this.smsConfigService.getSmsConfigMappings(smsData.smsConfigId).subscribe(
                    (response: any) => {
                        response.smsConfigMappingList.forEach(element => {
                            this.attribute.push(this.fb.group(element));
                        });
                        this.editAttributeValues = response.smsConfigMappingList;
                        this.editFormValues = this.editForm.value;
                    }
                );
            }
        } else {

            this.editForm.reset();
            this.attribute = this.fb.array([]);
            this.selectedSmsConfigId = null;
        }


        this.dialogRef = this.dialog.open(this.smsDialogTemplate, {
            width: '800px'
        });
    }

    onCancel(): void {
        if (this.dialogRef) {
            this.dialogRef.close();
        }
    }


}

