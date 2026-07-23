import { Component, OnInit, TemplateRef, ViewChild } from "@angular/core";
import { FormBuilder, FormGroup, UntypedFormArray, UntypedFormGroup, Validators } from "@angular/forms";
import { MatTableDataSource } from "@angular/material/table";
import { MatPaginator } from "@angular/material/paginator";
import { ToastrService } from "ngx-toastr";
import { MatDialog } from "@angular/material/dialog";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { AttributeService } from "./attribute.service";
import { DropdownService } from "../dropdown.service";

@Component({
    selector: "app-attribute",
    templateUrl: "./attribute.component.html",
    styleUrls: ["./attribute.component.css"],
    standalone: false
})
export class AttributeComponent implements OnInit {
    attributeForm: UntypedFormGroup;
    isattributeEdit = false;
    createAccess = true;
    editAccess = true;
    deleteAccess = true
    statusOptions = RadiusConstants.status;
    displayedColumns: string[] = [
        "id",
        "name",
        "type",
        "createdBy",
        "status",
        "action"
    ];

    dataSource: any = [] // Use MatTableDataSource for pagination

    @ViewChild(MatPaginator) paginator!: MatPaginator;
    totalRecords = 0;
    itemsPerPage = 5;
    currentPageIndex = 0;
    viewTrcData: any;
    constructor(
        private fb: FormBuilder,
        private service: AttributeService,
        private toastr: ToastrService,
        private dialog: MatDialog,
        public dropdownService: DropdownService
    ) { }

    ngOnInit(): void {
        this.buildForm();
        this.loadData();
        this.dropdownService.getattributData()
        this.dropdownService.getvendorData()
        this.dropdownService.getMasterTypeList()
        this.dropdownService.getDictionaryTypeList()
    }
    ipAddresses: UntypedFormArray;
    buildForm() {
        this.attributeForm = this.fb.group({
            attributeId: [''],
            vendorId: ['', Validators.required],
            name: ['', Validators.required],
            mandatory: ['', Validators.required],
            protectedFlag: ['', Validators.required],
            encryption: ['', Validators.required],
            type: ['', Validators.required],
            status: ['', Validators.required],
            dictionaryType: ['', Validators.required],
            minimum: [''],
            maximum: [''],
            attributeVendorId: [''],
            parentAttributeId: [''],
            createdBy: [''],
            modifiedBy: [''],
            regex: ['']
        });
    }

    loadData() {
        this.service.getAll().subscribe(
            (res: any) => {
                this.dataSource = res;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }
    // Handle page change
    pageChangedTrcList(event: any) {
        this.currentPageIndex = event.pageIndex;
        this.itemsPerPage = event.pageSize;
        this.updatePagedData();
    }

    // Update current page data
    pagedData: any[] = [];
    tableData: any = [];
    updatePagedData() {
        const startIndex = this.currentPageIndex * this.itemsPerPage;
        const endIndex = startIndex + this.itemsPerPage;
        this.pagedData = this.tableData.slice(startIndex, endIndex);
    }
    onSubmit(dialogRef) {
        this.submitted = true;


        if (this.attributeForm.invalid) return;

        if (this.editId == null) {
            // CREATE
            this.service.create(this.attributeForm.value).subscribe(
                response => {
                    if (response.responseCode == 406) {
                        this.toastr.error(`${response.responseMessage}`, "Failed!");
                    } else if (response.responseCode == 417 || response.responseCode == 500) {
                        this.toastr.error(`${response.responseMessage}`, "Failed!");
                    } else {

                        if (this.searchkey) {
                            this.getsearch();
                        } else {
                            this.loadData();
                        }
                        this.toastr.success(`Successfully Updated`, "Success!");
                        dialogRef.close()
                        this.cancelForm();
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, "Failed!");
                }
            );
        } else {
            // UPDATE
            this.service.update(this.editId, this.attributeForm.value).subscribe(
                response => {
                    if (response.responseCode == 406) {
                        this.toastr.error(`${response.responseMessage}`, "Failed!");
                    } else if (response.responseCode == 417 || response.responseCode == 500) {
                        this.toastr.error(`${response.responseMessage}`, "Failed!");
                    } else {

                        if (this.searchkey) {
                            this.getsearch();
                        } else {
                            this.loadData();
                        }
                        this.toastr.success(`Successfully Updated`, "Success!");
                        dialogRef.close()
                        this.cancelForm();
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, "Failed!");
                }
            );
        }
    }

    submitted = false;
    editId: any;
    isAttrinuteEdit: any
    edit(row: any) {
        this.editId = row.id;
        this.isAttrinuteEdit = row.id
        this.service.getById(row.id).subscribe(
            (res: any) => {
                if (res.length > 0) {
                    this.attributeForm.patchValue(res[0]);
                    this.dialog.open(this.addEditDialog, {
                        width: "80%",
                        disableClose: true
                    });
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }

    delete(row: any) {
        const dialogRef = this.dialog.open(this.confirmDialog, {
            width: '400px',
            data: {
                title: 'Delete Confirmation',
                description: `Do you want to delete this Attribute?`,
                yesLabel: 'Delete',
                noLabel: 'Cancel'
            }
        });

        dialogRef.afterClosed().subscribe((result) => {
            if (result === true) {
                this.service.delete(row.id).subscribe(
                    (response) => {
                        if (response.responseCode == 406) {
                            this.toastr.error(`${response.responseMessage}`, 'Failed!');
                        } else if (response.responseCode == 417) {
                            this.toastr.info(`${response.responseMessage}`, 'Info!');
                        } else {
                            if (this.searchkey) {
                                this.getsearch();
                            } else {
                                this.loadData();
                            }
                            this.toastr.success(`Deleted Successfully`, 'Success!');
                        }
                    },
                    (error: any) => {
                        this.toastr.error(`${error.error.ERROR}`, "Failed!");
                    }
                );
            } else {
            }
        });


    }
    searchkey: any = null
    getsearch() {
        this.service.getSearch(this.searchkey).subscribe(
            (res: any) => {
                this.dataSource = res
            },
            (error: any) => {
                this.dataSource = []
                this.toastr.error(`${error.error.ERROR}`, "Failed!");
            }
        );
    }
    createData() {
        this.editId = null;
        this.attributeForm.reset();
        this.submitted = false;
        this.dialog.open(this.addEditDialog, {
            width: "80%",
            disableClose: true
        });
    }
    cancelForm() {
        this.attributeForm.reset();
        this.editId = null;
        this.submitted = false;
    }
    listViewData() {

        this.editId = null;
        this.submitted = false;
        this.editId = null;
        this.isAttrinuteEdit = ''
        this.attributeForm.reset();
        this.searchkey = ''
        this.loadData();
    }

    attributeDetailFields: any = []
    @ViewChild('addEditDialog') addEditDialog!: TemplateRef<any>;
    @ViewChild("detailsDialog") detailsDialog!: TemplateRef<any>;
    @ViewChild('confirmDialog') confirmDialog!: TemplateRef<any>;
    AllDetails(data) {
        this.viewTrcData = data;
        // attribute-details.component.ts

        this.attributeDetailFields = [
            { label: 'Attribute ID', key: data.attributeId },
            { label: 'Name', key: data.name },
            { label: 'Vendor ID(s)', key: data.vendorId },
            // { label: 'Vendor Name', key: data.},
            { label: 'Mandatory', key: data.mandatory },
            { label: 'Protected Flag', key: data.protectedFlag },
            { label: 'Encryption', key: data.encryption },
            { label: 'Type', key: data.type },
            { label: 'Status', key: data.status },
            { label: 'Dictionary Type', key: data.dictionaryType },
            { label: 'Minimum', key: data.minimum },
            { label: 'Maximum', key: data.maximum },
            { label: 'Attribute Vendor ID', key: data.attributeVendorId },
            { label: 'Parent Attribute ID', key: data.parentAttributeId },
            { label: 'Created By', key: data.createdBy },
            { label: 'Modified By', key: data.modifiedBy },
            { label: 'Regex', key: data.regex }
        ];


        this.dialog.open(this.detailsDialog, {
            width: "80%",
            disableClose: true
        });
    }

    attributeVendorList = [
        { id: 1001, name: 'Vendor A' },
        { id: 1002, name: 'Vendor B' },
        { id: 1003, name: 'Vendor C' }
    ];

    parentAttributeList = [
        { id: 2001, name: 'Parent 1' },
        { id: 2002, name: 'Parent 2' },
        { id: 2003, name: 'Parent 3' }
    ];
    typeList = [
        { value: 'Unsigned32/OctetString', label: 'Unsigned32/OctetString' },
        { value: 'Grouped', label: 'Grouped' },
        { value: 'UTF8String', label: 'UTF8String' }
    ];

    dictionaryTypeList = [
        { value: 'DIAMETER', label: 'DIAMETER' },
        { value: 'RADIUS', label: 'RADIUS' }
    ];
}
