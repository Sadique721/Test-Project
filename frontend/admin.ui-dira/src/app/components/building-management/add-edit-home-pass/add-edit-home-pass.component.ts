import { Component, Inject } from '@angular/core';
import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { WhiteeSpaceValidator } from '../../shared/custom-validators';
import { CommondropdownService } from 'src/app/service/commondropdown.service';
import { MessageService } from 'primeng/api';
import { BuildingManagementService } from 'src/app/service/building-management.service';
import { BUILDING } from 'src/app/RadiusUtils/RadiusConstants';
import { ToastrService } from 'ngx-toastr';

@Component({
    selector: 'app-add-edit-home-pass',
    standalone: false,
    templateUrl: './add-edit-home-pass.component.html',
    styleUrl: './add-edit-home-pass.component.css'
})
export class AddEditHomePassComponent {

    areaTitle: string = "";
    buildingFormGroup: UntypedFormGroup;
    pincodeListData: any;
    selectedMappingFrom: string = "";
    dunningData: any;
    areaListData: any[];
    subAreaListData: any;
    methodOptions = [
        { label: "Range", value: "Range" },
        { label: "CSV", value: "CSV" },
        { label: "Manual", value: "Manual" }
    ];
    buildingMappings: any;
    selectedFile: any;
    selectedFilePreview: File[] = [];
    buildingTypeData: any;
    submitted: boolean = false;
    createAccess: boolean = false;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    isReadonly: boolean = false;

    isBuildingEdit: false;
    constructor(
        private toastr: ToastrService,
        public dialogRef: MatDialogRef<AddEditHomePassComponent>,
        @Inject(MAT_DIALOG_DATA) public data,
        private fb: UntypedFormBuilder,
        private commondropdownService: CommondropdownService,
        private messageService: MessageService,
        private buildingMangementService: BuildingManagementService,
    ) { }

    ngOnInit(): void {
        this.areaTitle = BUILDING;
        this.buildingFormGroup = this.fb.group({
            buildingName: ["", Validators.required],
            methodType: [""],
            pincode: [""],
            areaId: [""],
            subAreaId: [""],
            selectedMethod: ["", Validators.required],
            buildingType: [""],
            rangeStart: [""],
            rangeEnd: [""],
            manualControls: this.fb.array([]),
            file: [""]
        });
        this.getMappingFrom();
        this.getBuildingType();
        this.createAccess = this.data.createAcS;
        if (this.data.isEdit === true) {
            this.isBuildingEdit = this.data.isEdit;
            this.isReadonly = true;
            this.editAccess = this.data.editAcs;
            if (this.data.selectedMappingFrom === "Pin Code") {
                const selectedPincode = this.data.pincodeListData.find(
                    pincode => pincode.pincodeid === this.data.buildingData.pincodeId
                );
                if (selectedPincode) {
                    this.buildingFormGroup.patchValue({
                        pincode: selectedPincode.pincodeid
                    });
                }
            } else if (this.data.selectedMappingFrom === "Area") {
                const selectedArea = this.data.areaListData.find(
                    area => area.id === this.data.this.buildingData.areaId
                );
                if (selectedArea) {
                    this.buildingFormGroup.patchValue({
                        areaId: selectedArea.id
                    });
                }
            } else if (this.data.selectedMappingFrom === "Sub Area") {
                const selectedSubArea = this.data.subAreaListData.find(
                    subArea => subArea.id === this.data.buildingData.subAreaId
                );
                if (selectedSubArea) {
                    this.buildingFormGroup.patchValue({
                        subAreaId: selectedSubArea.id
                    });
                }
            }

            this.buildingFormGroup.patchValue(this.data.buildingData);


            let mappingMethod = this.data.buildingData.mappingMethod || "Manual";
            this.buildingFormGroup.patchValue({ selectedMethod: mappingMethod });

            if (mappingMethod === "Manual") {
                this.manualControls.clear();
                if (
                    this.data.buildingData.buildingMappings &&
                    this.data.buildingData.buildingMappings.length > 0
                ) {
                    this.data.buildingData.buildingMappings.forEach(mapping => {
                        this.manualControls.push(this.fb.control(mapping.buildingNumber));
                    });
                }
            } else if (mappingMethod === "Range") {
                if (
                    this.data.buildingData.buildingMappings &&
                    this.data.buildingData.buildingMappings.length > 0
                ) {
                    const numbers = this.data.buildingData.buildingMappings.map(mapping => {
                        const parts = mapping.buildingNumber.split("-");
                        return parts.length > 1 ? parseInt(parts[1], 10) : 0;
                    });
                    const min = Math.min(...numbers);
                    const max = Math.max(...numbers);
                    this.buildingFormGroup.patchValue({
                        rangeStart: min,
                        rangeEnd: max
                    });
                }
            } else if (mappingMethod === "CSV") {
                this.buildingMappings = this.data.buildingData.buildingMappings;
            }
        }
    }
    getBuildingType() {
        const url = "/commonList/buildingType";
        this.buildingMangementService.getMethod(url).subscribe(
            (response: any) => {
                this.buildingTypeData = response.dataList;
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');
            }
        );
    }
    onChnage(event) {
        this.selectedMappingFrom = this.dunningData[0].mappingFrom;
        if (this.selectedMappingFrom === "Pin Code") {
            let data = this.pincodeListData.find(x => x.pincodeid == event.value);
            this.buildingFormGroup.controls.buildingName.setValue(data.pincode);
        } else if (this.selectedMappingFrom === "Area") {
            let data = this.areaListData.find(x => x.id == event.value);
            this.buildingFormGroup.controls.buildingName.setValue(data.name);
        } else if (this.selectedMappingFrom === "Sub Area") {
            let data = this.subAreaListData.find(x => x.id == event.value);
            this.buildingFormGroup.controls.buildingName.setValue(data.name);
        }
    }
    getMappingFrom() {
        const url = "/buildingRefrence/all";
        this.buildingMangementService.getMethod(url).subscribe(
            (response: any) => {
                this.dunningData = response.dataList;
                if (this.dunningData?.length > 0) {
                    this.selectedMappingFrom = this.dunningData[0].mappingFrom;
                    if (this.selectedMappingFrom === "Pin Code") {
                        this.getAllPinCodeData();
                    } else if (this.selectedMappingFrom === "Area") {
                        this.getALLAreaData();
                    } else if (this.selectedMappingFrom === "Sub Area") {
                        this.getAllSubAreaData();
                    }
                } else {
                    this.toastr.info(`${response.responseMessage}`, 'Please Select First Building Reference Management!');

                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!');


            }
        );
    }
    get manualControls(): UntypedFormArray {
        return this.buildingFormGroup.get("manualControls") as UntypedFormArray;
    }
    addManualInput(): void {
        this.manualControls.push(this.fb.control("", Validators.required)); // Add new FormControl
    }
    removeManualInput(index: number): void {
        if (this.manualControls.length > 1) {
            this.manualControls.removeAt(index);
        }
    }
    onMethodChange(event: any) {
        const selectedValue = event.value;
        this.buildingFormGroup.patchValue({ selectedMethod: selectedValue });

        this.buildingMappings = [];

        if (selectedValue === "Manual") {
            this.selectedFile = null;
            this.manualControls.clear();
            this.buildingFormGroup.patchValue({ file: null });
            this.addManualInput();
            this.buildingFormGroup.controls.file.clearValidators();
            this.buildingFormGroup.controls.file.updateValueAndValidity();
            this.buildingFormGroup.controls.rangeStart.clearValidators();
            this.buildingFormGroup.controls.rangeStart.updateValueAndValidity();
            this.buildingFormGroup.controls.rangeEnd.clearValidators();
            this.buildingFormGroup.controls.rangeEnd.updateValueAndValidity();
        } else if (selectedValue === "CSV") {
            this.selectedFile = null;
            this.manualControls.clear();
            this.buildingFormGroup.controls.file.setValidators([Validators.required]);
            this.buildingFormGroup.controls.file.updateValueAndValidity();
            this.buildingFormGroup.controls.rangeStart.clearValidators();
            this.buildingFormGroup.controls.rangeStart.updateValueAndValidity();
            this.buildingFormGroup.controls.rangeEnd.clearValidators();
            this.buildingFormGroup.controls.rangeEnd.updateValueAndValidity();
        } else if (selectedValue === "Range") {
            this.selectedFile = null;
            this.manualControls.clear();
            this.buildingFormGroup.patchValue({ file: null });
            this.buildingFormGroup.controls.file.clearValidators();
            this.buildingFormGroup.controls.file.updateValueAndValidity();
            this.buildingFormGroup.controls.rangeStart.setValidators([Validators.required]);
            this.buildingFormGroup.controls.rangeStart.updateValueAndValidity();
            this.buildingFormGroup.controls.rangeEnd.setValidators([Validators.required]);
            this.buildingFormGroup.controls.rangeEnd.updateValueAndValidity();
        }
    }
    onFileChange(event: any) {
        this.selectedFilePreview = [];
        if (event.target.files.length > 0) {
            this.selectedFile = event.target.files[0];
            const files: FileList = event.target.files;
            for (let i = 0; i < files.length; i++) {
                this.selectedFilePreview.push(files.item(i));
            }
            if (this.selectedFile.type != "text/csv") {
                this.buildingFormGroup.controls.file.reset();
                alert("File type must be csv");
            } else {
                const file = event.target.files;
                this.buildingFormGroup.patchValue({
                    file: file
                });
            }
        }
    }
    onCancel(): void {
        this.dialogRef.close(null);
    }

    addOrUpdateBuilding() {
        this.submitted = true;
        if (this.buildingFormGroup.valid) {
            this.dialogRef.close(this.buildingFormGroup.value);
        } else {
            this.buildingFormGroup.markAllAsTouched();
        }
    }


    onChange(event) {
        this.selectedMappingFrom = this.dunningData[0].mappingFrom;
        if (this.selectedMappingFrom === "Pin Code") {
            let data = this.pincodeListData.find(x => x.pincodeid == event.value);
            this.buildingFormGroup.controls.buildingName.setValue(data.pincode);
        } else if (this.selectedMappingFrom === "Area") {
            let data = this.areaListData.find(x => x.id == event.value);
            this.buildingFormGroup.controls.buildingName.setValue(data.name);
        } else if (this.selectedMappingFrom === "Sub Area") {
            let data = this.subAreaListData.find(x => x.id == event.value);
            this.buildingFormGroup.controls.buildingName.setValue(data.name);
        }
    }
    getALLAreaData() {
        this.areaListData = [];
        const url = "/area/all";
        this.commondropdownService.getMethodFromCommon(url).subscribe(
            (response: any) => {
                this.areaListData = response.dataList;
                // console.log("areaData", this.areaData);
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: 'error',
                //   summary: 'Error',
                //   detail: error.error.ERROR,
                //   icon: 'far fa-times-circle',
                // })
            }
        );
    }
    getAllPinCodeData() {
        this.pincodeListData = [];
        const url = "/pincode/getAll";
        this.commondropdownService.getMethodFromCommon(url).subscribe(
            (response: any) => {
                this.pincodeListData = response.dataList;
                // console.log("allpincodeNumber", this.allpincodeNumber);
            },
            (error: any) => { }
        );
    }
    getAllSubAreaData() {
        this.subAreaListData = [];
        const url = "/subarea/all";
        this.commondropdownService.getMethodFromCommon(url).subscribe(
            (response: any) => {
                this.subAreaListData = response.dataList;
            },
            (error: any) => {
                // this.messageService.add({
                //   severity: 'error',
                //   summary: 'Error',
                //   detail: error.error.ERROR,
                //   icon: 'far fa-times-circle',
                // })
            }
        );
    }
}
