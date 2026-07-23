import { Component, OnInit, Input, Output, EventEmitter, Inject } from "@angular/core";
import { error } from "console";
import { MessageService } from "primeng/api";
import { Observable } from "rxjs";
import { InwardService } from "src/app/service/inward.service";
import { ToastrService } from 'ngx-toastr';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from "@angular/material/dialog";

@Component({
    selector: "app-customer-inventory-specification-params",
    templateUrl: "./customer-inventory-specification-params.component.html",
    styleUrls: ["./customer-inventory-specification-params.component.css"],
    standalone: false
})
export class CustomerInventorySpecificationParamsComponent implements OnInit {
    displayedColumns: string[] = ['paramName', 'isMandatory', 'paramValue', 'actions'];
    // dialogRef!: MatDialogRef<any>;
    @Input() productData: any;
    @Output() closeInventorySpecModel = new EventEmitter();
    inventorySpecificationDetails: any;
    inventorySpecificationParamModal: boolean = false;
    editedRowIndex: number = -1;
    constructor(
        private toastr: ToastrService,
        private dialog: MatDialog,
        private inwardService: InwardService,
        private messageService: MessageService,
        public dialogRef: MatDialogRef<CustomerInventorySpecificationParamsComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any
    ) {
        this.productData = data.productData; // ✅ get data from parent
    }

    ngOnInit(): void {
        // this.inventorySpecificationParamModal = true;
        this.addSpecificationParamDetails();
    }

    addSpecificationParamDetails() {
        this.inwardService.getInventoryParamsByMappingID(this.productData.id).subscribe(
            (response: any) => {
                if (response.responseCode == 200) {
                    //   this.specDetailsShow = true;
                    this.inventorySpecificationDetails = response.dataList;

                    this.inventorySpecificationDetails.map(item => {
                        if (item.isMultiValueParam) {
                            item.multiValue = item.paramMultiValues.map(value => ({
                                label: value,
                                value: value
                            }));
                        }

                        return item;
                    });
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');

            }
        );
    }

    isEditing(rowIndex: number): boolean {
        return rowIndex === this.editedRowIndex;
    }

    addOrEditValue(rowIndex: number, id: any, newValue: string, param: any) {
        if (this.editedRowIndex !== -1) {
            this.editedRowIndex = -1;
        } else {
            this.inventorySpecificationDetails.push({
                paramName: "",
                isMandatory: false,
                paramValue: newValue,
                isMultiValueParam: param.isMultiValueParam,
                multiValue: param.multiValue
            });
        }
    }

    closeInventorySpecificationDetailModal() {
        this.inventorySpecificationParamModal = false;
        this.closeInventorySpecModel.emit();
    }

    saveInventorySpecificationParams() {
        let custInvParams = this.inventorySpecificationDetails.map(item => ({
            paramName: item.paramName,
            paramValue: item.paramValue,
            custSerMapId: item.custSerMapId
        }));

        let data = {
            custSerMapId: custInvParams[0].custSerMapId,
            custInvParams: custInvParams,
            custInvId: this.productData.id
        };
        this.inwardService.updateCustomerInventoryParams(this.productData.customerId, data).subscribe(
            (response: any) => {
                this.toastr.success(`${response.responseMessage}`, 'Success!');

                this.closeInventorySpecificationDetailModal();
                this.dialogRef.close();
            },
            (error: any) => {
                this.toastr.error(`${error.error.msg}`, 'Failed!');


            }
        );
    }

    editValue(rowIndex: number) {
        this.editedRowIndex = rowIndex;
    }

    onCloseDialog() {
        if (this.dialogRef) {
            this.dialogRef.close();
        }
    }
}
