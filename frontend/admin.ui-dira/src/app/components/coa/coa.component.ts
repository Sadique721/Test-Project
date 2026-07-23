import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { CoaService } from "src/app/service/coa.service";
import { UntypedFormBuilder, Validators, UntypedFormGroup, UntypedFormArray } from "@angular/forms";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { MessageService } from "primeng/api";
import { NgxSpinnerService } from "ngx-spinner";
import { ConfirmationService } from "primeng/api";
import { ICoA } from "src/app/components/model/coa-profile";
import { DictionaryService } from "src/app/service/dictionary.service";
import { RadiusUtility } from "src/app/RadiusUtils/RadiusUtility";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { Observable, Observer } from "rxjs";
import { RADIUS_CONSTANTS } from "src/app/constants/aclConstants";
import { MatTableDataSource } from "@angular/material/table";
import { MatSort } from "@angular/material/sort";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { DeleteConfirmationDialogBoxComponent } from "src/app/shared/components/delete-confirmation-dialog-box/delete-confirmation-dialog-box.component";
import { ToastrService } from "ngx-toastr";


@Component({
    selector: "app-coa",
    templateUrl: "./coa.component.html",
    styleUrls: ["./coa.component.css"],
    standalone: false
})
export class CoaComponent implements OnInit {
    coaForm: UntypedFormGroup;
    searchForm: UntypedFormGroup;
    submitted = false;
    searchSubmitted = false;
    editCoAId: number;
    attribute: UntypedFormArray;
    name: string;
    type: string;
    //CoA data
    coaDMData: any = [];
    typeOfProfile = [{ label: "CoA" }, { label: "DM" }];
    timeOutData = [
        { label: "Seconds", value: "SECONDS" },
        { label: "Minute", value: "MIN" },
        { label: "Hour", value: "HOUR" }
    ];
    //Used and required for pagination
    totalRecords: string;
    currentPage = 1;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;

    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    searchkey: string;

    createCoaDMData: ICoA;
    editCoaDMData: ICoA;
    editFormValues: any;
    editAttributeValues: any;
    update: boolean = true;
    editMode: boolean = false;
    dictionaryAttributeData: any = [];
    mvnoData: any;
    loggedInUser: any;
    mvnoId: any;
    filtereDictionaryAttributeList: Array<any> = [];
    accessData: any = JSON.parse(localStorage.getItem("accessData"));

    createCoaFlag = false;
    coaGridFlag = true;

    AclClassConstants;
    AclConstants;
    public loginService: LoginService;
    createAccess: any;
    editAccess: any;
    deleteAccess: any;
    userId: string;
    superAdminId = RadiusConstants.SUPERADMINID;
    //   ***************
    title = 'CoA/DM Profile';
    dataSource = new MatTableDataSource<any>([]);
    @ViewChild(MatSort) sort: MatSort = Object.create(null);
    @ViewChild(MatPaginator) paginator: MatPaginator = Object.create(null);
    @ViewChild('addEditcoadmDialog') addEditcoadmDialog!: TemplateRef<any>;
    @ViewChild('viewCoaDialog') viewCoaDialog!: TemplateRef<any>;
    dialogRef!: MatDialogRef<any>;
    dialogRef2!: MatDialogRef<any>;
    dataSourcess = new MatTableDataSource<any>();
    constructor(
        private coAService: CoaService,
        private dictionaryService: DictionaryService,
        private fb: UntypedFormBuilder,
        private spinner: NgxSpinnerService,
        private confirmationService: ConfirmationService,
        private messageService: MessageService,
        private radiusUtility: RadiusUtility,
        loginService: LoginService,
        private dialog: MatDialog,
        private toastr: ToastrService
    ) {
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;

        this.createAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_COA_DM_CREATE);
        this.deleteAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_COA_DM_DELETE);
        this.editAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_COA_DM_EDIT);
        this.findAllCoA("");
    }
    getDisplayedPlanDetailsColumns(): Array<string> {
        if (this.loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_COA_DM_EDIT) || this.loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_COA_DM_DELETE)) {
            return ['id', 'profile', 'type', 'port', 'c-on', 'action'];
        } else {
            return ['id', 'profile', 'type', 'port', 'c-on'];
        }
    }
    ngOnInit(): void {
        this.userId = localStorage.getItem("userId");
        this.superAdminId = RadiusConstants.SUPERADMINID;
        this.attribute = this.fb.array([]);
        this.onAddAttribute();
        this.coaForm = this.fb.group({
            name: ["", Validators.required],
            gateway: [""],
            sharedkey: ["", Validators.required],
            port: ["", Validators.required],
            type: ["", Validators.required],
            timevar: ["", Validators.required],
            // unitsOftime:['', Validators.required],
            mvnoName: [""],
            createdBy: [""],
            lastModifiedBy: [""]
        });
        this.attribute = this.fb.array([]);
        this.onAddAttribute();
        this.searchForm = this.fb.group({
            name: [null],
            type: [null]
        });
        this.getAllDictionaryAttributes();

        this.mvnoData = JSON.parse(localStorage.getItem("mvnoData"));
        this.loggedInUser = localStorage.getItem("loggedInUser");
        this.mvnoId = localStorage.getItem("mvnoId");

        this.createCoaFlag = false;
        this.coaGridFlag = true;

    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }


    async searchByName() {
        if (!this.searchkey || this.searchkey !== this.searchForm.value) {
            this.currentPage = 1;
        }
        this.searchkey = this.searchForm.value;
        if (this.showItemPerPage) {
            this.itemsPerPage = this.showItemPerPage;
        }
        this.searchSubmitted = true;

        this.searchSubmitted = true;
        this.createCoaFlag = false;
        if (this.searchForm.value.name != null || this.searchForm.value.type != null) {
            // this.currentPage = 1;

            let name = this.searchForm.controls.name.value
                ? this.searchForm.controls.name.value.trim()
                : "";
            let type = this.searchForm.controls.type.value ? this.searchForm.controls.type.value : "";
            this.coaDMData = [];
            this.coAService.searchProfiles(name, type).subscribe(
                (response: any) => {
                    this.coaDMData = response.coaDMProfileList;
                    this.dataSource = new MatTableDataSource<any>(this.coaDMData);
                    if (this.paginator) {
                        this.dataSource.paginator = this.paginator;
                    }
                    //this.totalRecords=response.coaDMProfileList.totalRecords;
                },
                (error: any) => {
                    if (error.error.status == 404) {
                        this.toastr.info(`${error.error.errorMessage}`, 'Info!');
                    } else {
                        this.toastr.error(`${error.error.errorMessage}`, 'Error!');
                    }
                }
            );
        }
    }
    clearSearchForm() {
        this.clearFormData();
        this.searchSubmitted = false;
        this.searchForm.reset();
        this.currentPage = 1;
        this.findAllCoA("");
        this.createCoaFlag = false;
        this.coaGridFlag = true;
    }
    createCoa() {
        this.editMode = false;
        if (this.accessData.coadm.createUpdateAccess) {
            this.coaForm.controls['type'].enable();
            this.clearFormData();
            this.createCoaFlag = true;
            this.coaGridFlag = false;
            this.editMode = false;
            // this.coaForm.controls.unitsOftime.setValue("SECONDS");

            this.dialogRef = this.dialog.open(this.addEditcoadmDialog, {
                width: '1200px',
                maxWidth: '90vw',
                height: 'auto',
                autoFocus: false,
                disableClose: true
            });

            this.dialogRef.afterClosed().subscribe(() => {
                this.coaForm.reset();
            });
        }
    }

    clearFormData() {
        this.editMode = false;
        this.submitted = false;
        this.coaForm.reset();
        this.attribute.reset();
    }

    TotalItemPerPage(event) {
        this.showItemPerPage = Number(event.value);
        if (this.currentPage > 1) {
            this.currentPage = 1;
        }
        if (!this.searchkey) {
            this.findAllCoA(this.showItemPerPage);
        } else {
            this.searchByName();
        }
    }

    async findAllCoA(list) {
        let size;
        this.searchkey = "";
        let page = this.currentPage;
        if (list) {
            size = list;
            this.itemsPerPage = list;
        } else {
            size = this.itemsPerPage;
        }
        this.coAService.findAllCoA(page, size, (this.name = ""), (this.type = "")).subscribe(
            (response: any) => {
                this.coaDMData = response.coaDMProfileList;
                this.dataSource = new MatTableDataSource<any>(this.coaDMData);
                if (this.paginator) {
                    this.dataSource.paginator = this.paginator;
                    this.paginator.pageIndex = this.currentPage - 1;
                }
                if (this.sort) {
                    this.dataSource.sort = this.sort;
                }
                this.totalRecords = response.coaDMProfileList.length;
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Error!');
            }
        );
    }

    coaProfileIndex: any;

    // async editCoaById(coaId, index, selectedMvnoId) {
    //     if (!this.validateUserToPerformOperations(selectedMvnoId)) return;

    //     this.dialogRef = this.dialog.open(this.addEditcoadmDialog, {
    //         width: '1200px',
    //         maxWidth: '90vw',
    //         height: 'auto',
    //         autoFocus: false,
    //         disableClose: true
    //     });

    //     this.dialogRef.afterClosed().subscribe(() => {
    //         this.coaForm.reset();
    //     });
    //     this.editMode = true;

    //     this.editCoAId = coaId;
    //     this.editMode = true;
    //     this.createCoaFlag = true;
    //     this.coaGridFlag = false;
    //     this.coaProfileIndex = this.coaDMData[index];
    //     // index = this.radiusUtility.getIndexOfSelectedRecord(
    //     //   index,
    //     //   this.currentPage,
    //     //   this.itemsPerPage
    //     // );
    //     // this.coaForm.patchValue({
    //     //   name: this.coaProfileIndex.name,
    //     //   gateway: this.coaProfileIndex.gateway,
    //     //   sharedkey: this.coaProfileIndex.sharedkey,
    //     //   port: this.coaProfileIndex.port,
    //     //   type: this.coaProfileIndex.type,
    //     //   mvnoName: this.coaProfileIndex.mvnoId,
    //     // })
    //     this.editFormValues = this.coaForm.value;
    //     this.editCoaDMData = this.coaProfileIndex;


    //     this.coAService.getCoAById(coaId).subscribe(
    //         (response: any) => {
    //             this.editCoaDMData = response.coaDMProfile;
    //             this.editCoAId = this.editCoaDMData.coaDMProfileId;
    //             this.coaForm.patchValue({
    //                 name: this.editCoaDMData.name,
    //                 gateway: this.editCoaDMData.gateway,
    //                 sharedkey: this.editCoaDMData.sharedkey,
    //                 port: this.editCoaDMData.port,
    //                 type: this.editCoaDMData.type,
    //                 mvnoName: this.editCoaDMData.mvnoId,
    //                 timevar: this.editCoaDMData.timevar
    //                 // unitsOftime:this.editCoaDMData.unitsOftime,
    //             });
    //             this.editFormValues = this.coaForm.value;
    //             this.attribute = this.fb.array([]);
    //             this.coAService.getCoAAttributeById(coaId).subscribe(
    //                 (response: any) => {
    //                     let attributeList = response.CoaDMProfileAttributeAttributeList;
    //                     attributeList.forEach(element => {
    //                         this.attribute.push(this.fb.group(element));
    //                         this.attribute.controls = this.attribute.controls;
    //                         this.dataSourcess.data = [...this.attribute.controls] as any[];
    //                     });
    //                     this.editAttributeValues = response.CoaDMProfileAttributeAttributeList;
    //                 },
    //                 (error: any) => {
    //                     this.messageService.add({
    //                         severity: "error",
    //                         summary: "Error",
    //                         detail: error.error.errorMessage,
    //                         icon: "far fa-times-circle"
    //                     });
    //                 }
    //             );


    //         },
    //         (error: any) => {
    //             this.messageService.add({
    //                 severity: "error",
    //                 summary: "Error",
    //                 detail: error.error.errorMessage,
    //                 icon: "far fa-times-circle"
    //             });
    //         }
    //     );
    // }

    async editCoaById(coaId, index, selectedMvnoId) {
        this.editMode = true;
        if (!this.validateUserToPerformOperations(selectedMvnoId)) return;

        // open dialog
        this.dialogRef = this.dialog.open(this.addEditcoadmDialog, {
            width: '1200px',
            maxWidth: '90vw',
            height: 'auto',
            autoFocus: false,
            disableClose: true
        });
        this.dialogRef.afterClosed().subscribe(() => {
            this.coaForm.reset();
        });
        this.createCoaFlag = true;
        this.coaGridFlag = false;
        this.editCoAId = coaId;

        if (this.editMode) {
            this.coaForm.controls['type'].disable();
        }

        // clear attribute formarray before filling
        this.attribute = this.fb.array([]);
        this.dataSourcess.data = [];

        // fetch profile
        this.coAService.getCoAById(coaId).subscribe(
            (response: any) => {
                this.editCoaDMData = response.coaDMProfile;
                this.editCoAId = this.editCoaDMData.coaDMProfileId;

                // patch the form with values from backend
                this.coaForm.patchValue({
                    name: this.editCoaDMData.name,
                    gateway: this.editCoaDMData.gateway,
                    sharedkey: this.editCoaDMData.sharedkey,
                    port: this.editCoaDMData.port,
                    type: this.editCoaDMData.type,
                    mvnoName: this.editCoaDMData.mvnoId,
                    timevar: this.editCoaDMData.timevar
                });

                this.editFormValues = JSON.parse(JSON.stringify(this.coaForm.value));



                this.coAService.getCoAAttributeById(coaId).subscribe(
                    (respAttr: any) => {
                        const attributeList = respAttr.CoaDMProfileAttributeAttributeList || [];

                        attributeList.forEach(element => {
                            const grp = this.fb.group({
                                checkitem: [element.checkitem || ''],
                                profileAtt: [element.profileAtt || '', Validators.required],
                                radiusAtt: [element.radiusAtt || '', Validators.required],
                                coaDMProfileId: [element.coaDMProfileId || this.editCoAId],
                                coaDMProfileAttributeMappingId: [element.coaDMProfileAttributeMappingId || '']
                            });
                            this.attribute.push(grp);
                        });

                        this.dataSourcess.data = this.attribute.controls.map(ctrl => ctrl) as any[];

                        this.editAttributeValues = JSON.parse(JSON.stringify(this.attribute.value));
                    },
                    error => {
                        this.toastr.error(`${error.error?.errorMessage} || 'Error fetching profile`, 'Error!');
                    }
                );
            },
            error => {
                this.toastr.error(`${error.error?.errorMessage} || 'Error fetching profile`, 'Error!');
            }
        );
    }

    async updateCoADMProfile() {
        if (this.validateUserToPerformOperations(this.coaForm.value.mvnoName)) {
            if (this.editCoaDMData) this.editCoaDMData = this.coaForm.value;
            this.editCoaDMData.coaDMProfileId = this.editCoAId;



            // Assign form values
            this.editCoaDMData = this.coaForm.getRawValue();

            // Add profile ID
            this.editCoaDMData.coaDMProfileId = this.editCoAId;

            // Add attributes if required
            this.editCoaDMData.coaDMProfileAttributeDtoList = this.attribute.value || [];

            // Clean null/undefined
            Object.keys(this.editCoaDMData).forEach(key => {
                if (this.editCoaDMData[key] === null || this.editCoaDMData[key] === undefined) {
                    this.editCoaDMData[key] = "";
                }
            });

            // Ensure mvnoName is string
            this.editCoaDMData.mvnoName = String(this.editCoaDMData.mvnoName);


            this.coAService.updateCoA(this.editCoaDMData).subscribe(
                (response: any) => {
                    this.closeDialog();
                    this.editMode = false;
                    this.submitted = false;
                    this.createCoaFlag = false;
                    this.coaGridFlag = true;
                    if (!this.searchkey) {
                        this.findAllCoA("");
                    } else {
                        this.searchByName();
                    }
                    this.coaForm.reset();
                    this.attribute = this.fb.array([]);
                    this.onAddAttribute();
                    if (this.update) {
                        this.toastr.success(`${response.message}`, 'Success!');
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.errorMessage}`, 'Error!');
                }
            );
        }
    }
    async updateCoAAttribute() {
        if (this.validateUserToPerformOperations(this.coaForm.value.mvnoName)) {
            this.attribute.value.forEach(element => {
                element.coaDMProfileId = this.editCoAId;
            });
            this.dataSourcess.data = [...this.attribute.controls] as any[];

            this.coAService
                .updateCoAAttribute(this.attribute.value, this.coaForm.value.mvnoName, this.editCoAId)
                .subscribe(
                    (response: any) => {
                        this.closeDialog();
                        this.editMode = false;
                        this.submitted = false;
                        this.createCoaFlag = false;
                        this.coaGridFlag = true;
                        this.coaForm.reset();
                        this.attribute = this.fb.array([]);
                        this.onAddAttribute();
                        if (this.update) {
                            this.toastr.success(`${response.message}`, 'Success!');
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.errorMessage}`, 'Error!');
                    }
                );
        }
    }

    async addCoA() {
        this.submitted = true;
        // if (
        //   this.coaForm.value.unitsOftime == "" ||
        //   this.coaForm.value.unitsOftime == null
        // ) {
        //   this.coaForm.value.unitsOftime = "SECONDS";
        // }

        if (this.coaForm.valid && this.attribute.valid) {
            // if (this.editMode) {
            //     this.coaForm.get('type')?.disable();
            //     this.createCoaDMData = this.coaForm.value;
            //     this.createCoaDMData.coaDMProfileAttributeDtoList = this.attribute.value;
            //     if (
            //         this.coaForm.value == this.editFormValues &&
            //         JSON.stringify(this.attribute.value) === JSON.stringify(this.editAttributeValues)
            //     ) {
            //         this.editMode = false;
            //         this.submitted = false;
            //         this.createCoaFlag = false;
            //         this.coaGridFlag = true;
            //         this.coaForm.reset();
            //         this.attribute = this.fb.array([]);
            //         this.onAddAttribute();
            //         this.messageService.add({
            //             severity: "success",
            //             summary: "Successfully",
            //             detail: "Profile data is same",
            //             icon: "far fa-check-circle"
            //         });
            //     } else if (
            //         this.coaForm.value != this.editFormValues &&
            //         JSON.stringify(this.attribute.value) === JSON.stringify(this.editAttributeValues)
            //     ) {
            //         this.updateCoADMProfile();
            //     } else if (
            //         this.coaForm.value == this.editFormValues &&
            //         JSON.stringify(this.attribute.value) !== JSON.stringify(this.editAttributeValues)
            //     ) {
            //         this.updateCoAAttribute();
            //     } else {
            //         this.update = false;
            //         this.updateCoADMProfile();
            //         this.updateCoAAttribute();
            //         this.editMode = false;
            //         this.messageService.add({
            //             severity: "success",
            //             summary: "Successfully",
            //             detail: "CoA/DM profile and attribute has been updated successfully.",
            //             icon: "far fa-check-circle"
            //         });
            //     }
            // }
            if (this.editMode) {
                this.createCoaDMData = this.coaForm.getRawValue();
                this.createCoaDMData.coaDMProfileAttributeDtoList
                    = this.attribute.value;

                const formUnchanged = JSON.stringify(this.coaForm.getRawValue()) === JSON.stringify(this.editFormValues);
                const attrsUnchanged = JSON.stringify(this.attribute.value) === JSON.stringify(this.editAttributeValues);
                // const formUnchanged = isEqual(this.coaForm.getRawValue(), this.editFormValues);
                // const attrsUnchanged = isEqual(this.attribute.value, this.editAttributeValues);


                if (formUnchanged && attrsUnchanged) {
                    this.editMode = false;
                    this.submitted = false;
                    this.createCoaFlag = false;
                    this.coaGridFlag = true;
                    this.coaForm.reset();
                    this.attribute = this.fb.array([]);
                    this.onAddAttribute();
                    this.toastr.success(`Profile data is same`, 'Success!');
                } else if (!formUnchanged && attrsUnchanged) {
                    await this.updateCoADMProfile();
                } else if (formUnchanged && !attrsUnchanged) {
                    await this.updateCoAAttribute();
                } else {
                    // both changed
                    this.update = false;
                    await this.updateCoADMProfile();
                    await this.updateCoAAttribute();
                    this.editMode = false;
                    this.toastr.success(`CoA/DM profile and attribute has been updated successfully.`, 'Success!');
                }
            }
            else {
                this.createCoaDMData = this.coaForm.value;
                this.createCoaDMData.coaDMProfileAttributeDtoList = this.attribute.value;
                this.coAService.addNewCoA(this.createCoaDMData).subscribe(
                    (response: any) => {
                        this.closeDialog();
                        this.submitted = false;
                        this.createCoaFlag = false;
                        this.coaGridFlag = true;
                        this.findAllCoA("");
                        this.coaForm.reset();
                        this.attribute = this.fb.array([]);
                        this.onAddAttribute();
                        this.toastr.success(`${response.message}`, 'Success!');
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.errorMessage}`, 'Error!');
                    }
                );
            }
        }
        else {
            this.coaForm.markAllAsTouched();
            this.attribute.markAllAsTouched();
        }
    }
    deleteConfirm(coa, selectedMvnoId, index) {
        if (this.validateUserToPerformOperations(selectedMvnoId)) {
            // this.confirmationService.confirm({
            //     message: "Do you want to delete this CoA/DM Profile?",
            //     header: "Delete Confirmation",
            //     icon: "pi pi-info-circle",
            //     accept: () => {
            //         this.deleteCoAById(coaId, selectedMvnoId, index);
            //     },
            //     reject: () => {
            //         this.messageService.add({
            //             severity: "info",
            //             summary: "Rejected",
            //             detail: "You have rejected"
            //         });
            //     }
            // });

            const dialogRef = this.dialog.open(DeleteConfirmationDialogBoxComponent, {
                width: '400px',
                data: {
                    title: `Delete Confirmation`,
                    description: `Are you sure you want to delete "${coa.name}"?`,
                    yesLabel: 'Delete',
                    noLabel: 'Cancel'
                }
            });

            dialogRef.afterClosed().subscribe(result => {
                if (result) {
                    this.deleteCoAById(coa.coaDMProfileId, selectedMvnoId, index);
                } else {
                }
            });
        }
    }
    async deleteCoAById(coaId, selectedMvnoId, index) {
        this.coAService.deleteCoAById(coaId, selectedMvnoId).subscribe(
            (response: any) => {
                if (this.currentPage != 1 && index == 0 && this.coaDMData.length == 1) {
                    this.currentPage = this.currentPage - 1;
                }
                if (!this.searchkey) {
                    this.findAllCoA("");
                } else {
                    this.searchByName();
                }
                this.coaForm.reset();
                this.attribute = this.fb.array([]);
                this.onAddAttribute();
                this.toastr.success(`${response.message}`, 'Success!');
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Error!');
            }
        );
    }

    onAddAttribute() {
        // this.attribute.push(this.createAttributeFormGroup());
        // this.attribute.controls = this.attribute.controls;
        // this.dataSourcess.data = [...this.attribute.controls] as any[];
        const group = this.createAttributeFormGroup();
        this.attribute.push(group);

        this.dataSourcess.data = this.attribute.controls.map(ctrl => ctrl) as any[];
    }
    deleteConfirmAttribute(attributeIndex: number, attributeId: number, selectedMvnoId) {
        // this.attribute.removeAt(attributeIndex);
        // this.dataSourcess.data = [...this.attribute.controls] as any[];

        this.attribute.removeAt(attributeIndex);
        this.dataSourcess.data = this.attribute.controls.map(ctrl => ctrl) as any[];
    }

    createAttributeFormGroup(): UntypedFormGroup {
        return this.fb.group({
            checkitem: [""],
            profileAtt: ["", Validators.required],
            radiusAtt: ["", Validators.required],
            coaDMProfileId: [""],
        })
    }

    async getAllDictionaryAttributes() {
        this.dictionaryService.findAllAttributes().subscribe(
            (response: any) => {
                this.dictionaryAttributeData = response;
                this.getDetailsByMVNO(JSON.parse(localStorage.getItem("mvnoId")));
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Error!');
            }
        );
    }

    pageChanged(pageNumber) {
        this.currentPage = pageNumber;
        if (!this.searchkey) {
            this.findAllCoA("");
        } else {
            this.searchByName();
        }
    }
    validateUserToPerformOperations(selectedMvnoId) {
        let loggedInUserMvnoId = localStorage.getItem("mvnoId");
        this.userId = localStorage.getItem("userId");
        if (this.userId != RadiusConstants.SUPERADMINID && selectedMvnoId != loggedInUserMvnoId) {
            //  this.reset();
            this.toastr.info(`You are not authorized to do this operation. Please contact to the administrato`, 'Info!');
            //  this.modalToggle = false;
            return false;
        }
        return true;
    }

    getDetailsByMVNO(mvnoId) {
        let alldictionaryAttributeList = this.dictionaryAttributeData.dictionaryAttributeList;
        if (mvnoId == 1) {
            this.filtereDictionaryAttributeList = alldictionaryAttributeList;
        } else {
            this.filtereDictionaryAttributeList = alldictionaryAttributeList.filter(
                element => element.mvnoId == mvnoId || element.mvnoId == 1
            );
        }
    }
    coa = {
        coaDMProfileId: "",
        name: "",
        gateway: "",
        sharedkey: "",
        port: "",
        type: "",
        mvnoId: ""
    };
    modalToggle: boolean = true;
    OneCoaData: any = [];
    showCoaDetail(coaDMProfileId) {
        this.modalToggle = true;

        this.coAService.getCoAById(coaDMProfileId).subscribe(
            (response: any) => {
                this.OneCoaData = response;
                this.coa = this.OneCoaData.coaDMProfile;
                this.dialogRef2 = this.dialog.open(this.viewCoaDialog, {
                    width: '800px',
                    maxWidth: '90vw',
                    height: 'auto',
                    autoFocus: false,
                    disableClose: true
                });

                this.dialogRef2.afterClosed().subscribe(() => {
                    this.coaForm.reset();
                });
            },
            (error: any) => {
                this.toastr.error(`${error.error.errorMessage}`, 'Error!');
            }
        );
    }
    canExit() {
        if (!this.coaForm.dirty && !this.searchForm.dirty && !this.attribute.dirty) return true;
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

    closeDialog() {
        this.editMode = false;
        this.submitted = false;
        if (this.dialogRef) this.dialogRef.close();
        if (this.dialogRef2) this.dialogRef2.close();
    }
}
