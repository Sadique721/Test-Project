import { Component, Inject, TemplateRef, ViewChild } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { WhiteeSpaceValidator } from '../../shared/custom-validators';
import { StateManagementService } from 'src/app/service/state-management.service';
import { MessageService } from 'primeng/api';
import { MatCardContent } from "@angular/material/card";
import { CityManagementService } from 'src/app/service/city-management.service';
import { PincodeManagementService } from 'src/app/service/pincode-management.service';
import { ServiceAreaService } from 'src/app/service/service-area.service';
import { LocationService } from 'src/app/service/location.service';
import { ToastrService } from 'ngx-toastr';

// declare const google: any;
export interface createDialogData {
    showPolygonNameDialog: boolean;
    isClearShow: boolean;
    mArea: any;
    polygonCreateAccess: any;
    currentPagesearchLocationList: any;
    ifsearchLocationModal: any;
    assignInventoryWithSerial: any;
    customerrMyInventoryView: any;
    fomrGrp: any;
    status: string;
    mvnoIds: any[];
    serviceAreaType: string;
    blockNo: string;
    radius: string;
    longitude: string;
    latitude: string;
    siteName: string;
    name: string;
    cityListDatas: any;
    locationDataByPlan: any;
    siteNameListData: any;
    pincodeListDatas: any;
    pincodeData: any;
    isServiceAreaEdit: boolean;
    createAcS: boolean;
    editAcs: boolean;
    createView: boolean;
    isViewService: boolean;
    isSiteName: boolean;
    viewAcs: boolean;
    polygonViewAcs: boolean;
    loggedinUsermvnoid: number;
    title?: string;
    description: string;
    yesLabel?: string;
    noLabel?: string;
    polygonDeleteAccess: boolean;
}

interface marker {
    lat: number;
    lng: number;
    label?: string;
    draggable?: boolean;
    visible?: boolean;
    opacity?: number;
}

@Component({
    selector: 'app-add-edit-service-area-managment',
    standalone: false,
    templateUrl: './add-edit-service-area-managment.component.html',
    styleUrl: './add-edit-service-area-managment.component.css'
})
export class AddEditServiceAreaManagmentComponent {
    serviceAreaGroupForm: UntypedFormGroup;
    statusOptions = [
        { label: "Active", value: "Y", val: "ACTIVE" },
        { label: "Inactive", value: "N", val: "INACTIVE" },
        { label: "UnderDevelopment", value: "U", val: "UNDERDEVELOPMENT" }
    ];
    submitted: boolean = false;
    createAccess: boolean = true;
    deleteAccess: boolean = false;
    editAccess: boolean = false;
    isServiceAreaEdit: boolean = false;
    countryListData: any;
    stateListData: any;
    cityListData: any;
    cityDetail: any;
    siteNameListData: any;
    pincodeDetail: any;
    pincodeOptions: any[] = [];
    areaInputview: boolean = false;
    pincodeListData: any;
    locationDataByPlan: any = [];
    iflocationFill = false;
    isMapModelEnable: boolean = false;
    polygonList: any[] = [];
    isUploadView: boolean = false;
    viewserviceAreaListData: any = [];
    inputshowSelsctData: boolean = false;
    ispListData: any[] = [];
    isSiteNameAvailable: boolean = false;
    editableData: any;
    searchLocationtotalRecords: String;
    searchLocationItemPerPage = RadiusConstants.ITEMS_PER_PAGE;
    location: string = "";
    // center: google.maps.LatLngLiteral = { lat: 21, lng: 78 };
    lat = 23.16774596751141;
    lng = 72.39140613721185;
    zoom = 7;
    markers: marker[] = [];
    map: any;
    drawingManager: any;
    drawnPolygonLatLongList: any[] = [];
    currentPolygonCoordinates: any[] = [];
    // mapTypeId: google.maps.MapTypeId = google.maps.MapTypeId.HYBRID;
    serviceAreaType = [
        { label: "Public", value: "public" },
        { label: "Private", value: "private" }
    ];
    searchLocationForm: UntypedFormGroup;
    searchLocationData: any;
    closebutton: any;
    currentPagesearchLocationList: any;
    items: any;
    isViewService: boolean;
    createView: boolean;


    constructor(
        public dialogRef: MatDialogRef<AddEditServiceAreaManagmentComponent>,
        public dialogRef2: MatDialogRef<AddEditServiceAreaManagmentComponent>,
        public dialogRef3: MatDialogRef<AddEditServiceAreaManagmentComponent>,
        @Inject(MAT_DIALOG_DATA) public data: createDialogData,
        private fb: UntypedFormBuilder,
        private stateManagementService: StateManagementService,
        private cityManagementService: CityManagementService,
        private messageService: MessageService,
        private pincodeManagementService: PincodeManagementService,
        private serviceAreaSErvice: ServiceAreaService,
        private toastr: ToastrService,
        private locationService: LocationService,
        private dialog: MatDialog

    ) { }

    ngOnInit(): void {
        this.serviceAreaGroupForm = this.fb.group({
            name: ["", [Validators.required, WhiteeSpaceValidator.cannotContainSpace]],
            siteName: [""],
            latitude: [""],
            longitude: [""],
            radius: [""],
            blockNo: [""],
            cityid: ["", Validators.required],
            pincodes: ["", Validators.required],
            serviceAreaType: ["", Validators.required],
            locationIds: [[]],
            mvnoIds: [[]],
            status: ["", Validators.required],
            areaid: [""],
            id: [""],
        });

        this.searchLocationForm = this.fb.group({
            searchLocationname: ["", Validators.required]
        });

        // // Agar view mode hai to form ko disable karo
        // if (this.data.isViewService) {
        //     this.serviceAreaGroupForm.disable();
        // }


        this.editableData = this.data.fomrGrp?.value || this.data.fomrGrp || {};
        this.isServiceAreaEdit = this.data.isServiceAreaEdit;
        this.createView = this.data.createView;
        this.isViewService = this.data.isViewService;

        this.locationDataByPlan = this.data.locationDataByPlan;
        this.siteNameListData = this.data.siteNameListData;

        this.cityListData = this.data.cityListDatas || [];
        this.pincodeListData = this.data.pincodeListDatas || [];

        if (this.isServiceAreaEdit && this.data.editAcs) {
            this.data.title = "Update Service Area";
            this.tryToAddFormPathValue();
            this.getPincodeDetailbyId(this.editableData.cityid);

        } else if (this.createAccess && this.createView) {
            this.data.title = "Create Service Area";
        }

    }

    getISPList() {
        let url = `/mvno/getMvnoNameAndIds`;
        this.serviceAreaSErvice.getMethod(url).subscribe(
            (response: any) => {
                let superAdminId = Number(RadiusConstants.SUPERADMINID);
                this.ispListData = response.dataList.filter(isp => isp.id != superAdminId);
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }
    @ViewChild('mapDialog') mapDialog!: TemplateRef<any>;

    DrawPolygon() {
        this.isMapModelEnable = true;
        if (this.polygonList.length > 0) {
            this.isUploadView = false;
        } else {
            this.isUploadView = true;
        }
        this.dialogRef3 = this.dialog.open(this.mapDialog, {
            width: '1000px',
            maxWidth: '100vw',
            height: 'auto',
            panelClass: 'custom-dialog-container',
            autoFocus: false,
            disableClose: true
        });

        this.dialogRef3.afterClosed().subscribe(result => {
            this.dialogRef3 = null;
        });

    }

    handleKeyDown(event: KeyboardEvent) {
        if (
            event.keyCode === 8 ||
            (event.key >= "0" && event.key <= "9")
        ) {
            return true;
        } else {
            return false;
        }
    }

    mylocation() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(position => {
                if (position) {
                    this.serviceAreaGroupForm.patchValue({
                        latitude: position.coords.latitude,
                        longitude: position.coords.longitude
                    });
                    this.iflocationFill = true;
                }
            });
        } else {
            this.toastr.error(`Geolocation is not supported by this browser.`, 'Failed!')
            // this.messageService.add({
            //     severity: "error",
            //     summary: "Error",
            //     detail: "Geolocation is not supported by this browser.",
            //     icon: "far fa-times-circle"
            // });
        }
    }


    getSelCity(event) {
        const selCityId = event.value;
        this.getPincodeDetailbyId(selCityId);
    }

    serviceAreaTypeChange(event: any) {
        let selectedType = event.value;
        if (selectedType === "private") {
            this.serviceAreaGroupForm.get("blockNo").setValidators([Validators.required]);
            this.serviceAreaGroupForm.get("blockNo").updateValueAndValidity();
        } else {
            this.serviceAreaGroupForm.get("blockNo").clearValidators();
            this.serviceAreaGroupForm.get("blockNo").updateValueAndValidity();
        }
    }

    getPincodeDetailbyId(selCityId) {
        this.pincodeDetail = "";

        const url = "/serviceArea/getPincodefromCity/withSpecificParameter?id=" + selCityId;
        this.serviceAreaSErvice.getMethod(url).subscribe(
            (response: any) => {
                this.pincodeDetail = response.dataList;
                this.pincodeListData = this.pincodeDetail;
                if (this.pincodeDetail.length > 0) {
                    const selectedPincodes = this.serviceAreaGroupForm.value.pincodes || [];
                    this.pincodeOptions = this.pincodeDetail.map(pincode => ({
                        pincode: pincode.pincode,
                        id: pincode.id,
                        selected: selectedPincodes.includes(pincode.id)
                    }));
                    this.areaInputview = true;
                } else {
                    this.pincodeOptions = [];
                    this.toastr.error("No " + this.data.title + " found.", 'Failed!')
                    // this.messageService.add({
                    //     severity: "info",
                    //     summary: "Info",
                    //     detail: "No " + this.data.title + " found.",
                    //     icon: "far fa-times-circle"
                    // });
                }
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }



    onKeyName(event) {
        if (!this.data.isServiceAreaEdit) {
            this.serviceAreaGroupForm.patchValue({
                siteName: this.serviceAreaGroupForm.value.name
            });
            this.checkSiteNameExistOrNot(this.serviceAreaGroupForm.value.siteName);
        }
    }

    checkSiteNameExistOrNot(siteName) {
        if (siteName) {
            let url = `/serviceArea/isSiteNameExists/${siteName}`;
            this.serviceAreaSErvice.getMethod(url).subscribe(
                (response: any) => {
                    if (response.data) {
                        this.toastr.info(`Site Name is not available`, 'Info!')
                        // this.messageService.add({
                        //     severity: "info",
                        //     summary: "Info",
                        //     detail: "Site Name is not available",
                        //     icon: "far fa-times-circle"
                        // });
                        this.isSiteNameAvailable = false;
                    } else {
                        this.isSiteNameAvailable = true;
                    }
                },
                (error: any) => {
                    this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                    // this.messageService.add({
                    //     severity: "error",
                    //     summary: "Error",
                    //     detail: error.error.ERROR,
                    //     icon: "far fa-times-circle"
                    // });
                }
            );
        }
    }


    onSiteNameChange(event) {
        let siteName = this.serviceAreaGroupForm.value.siteName;
        if (!this.data.isServiceAreaEdit) {
            this.checkSiteNameExistOrNot(siteName);
        }
    }



    onCancel(): void {
        this.dialogRef.close(null);
    }

    onSubmit() {
        this.submitted = true;
        if (!this.serviceAreaGroupForm.valid) {
            this.serviceAreaGroupForm.markAllAsTouched();

        } else {
            this.dialogRef.close(this.serviceAreaGroupForm);
        }
    }




    selectCityChange(event) {
        const selCity = event.value;
        this.getCityDetailbyd(selCity);
    }

    getCityDetailbyd(cityId) {
        const url = "/city/" + cityId;
        this.pincodeManagementService.getMethod(url).subscribe(
            (response: any) => {
                this.cityDetail = response.cityList;
                // return
                this.inputshowSelsctData = true;
                this.serviceAreaGroupForm.controls.countryId.patchValue(this.cityDetail.countryId);
                this.serviceAreaGroupForm.controls.stateId.patchValue(this.cityDetail.statePojo.id);
            },
            (error: any) => {
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }


    tryToAddFormPathValue() {
        // if (
        //     this.data.isServiceAreaEdit &&
        //     this.data.cityListDatas &&
        //     this.data.locationDataByPlan &&
        //     this.data.pincodeListDatas &&
        //     this.editableData
        // ) {
        this.isServiceAreaEdit = true;
        this.editAccess = this.data.editAcs;

        const cityIdToSelect = this.editableData.cityid || this.editableData.cityId;
        const city = this.data.cityListDatas.find(c => c.id == cityIdToSelect);

        // let pincodeIds: number[] = [];
        // if (Array.isArray(this.editableData.pincodes)) {
        //     pincodeIds = this.editableData.pincodes.map((p: any) => {
        //         if (typeof p === "object") return p.id;
        //         if (typeof p === "string") return parseInt(p, 10);
        //         return p;
        //     });
        // }
        // const matchedPincodes = this.editableData.pincodes
        //     .filter((pin: any) => pincodeIds.includes(pin.id))
        //     .map((pin: any) => pin.id);

        const locationIds = this.editableData.locationIds ?? [];
        const mvnoIds = this.editableData.mvnoIds ?? [];

        const latitude = this.editableData.latitude
            ? parseFloat(this.editableData.latitude)
            : "";
        const longitude = this.editableData.longitude
            ? parseFloat(this.editableData.longitude)
            : "";

        const siteNameValue = (() => {
            if (!this.editableData.siteName) return "";
            const nameStr = Array.isArray(this.editableData.siteName)
                ? this.editableData.siteName[0]
                : this.editableData.siteName;

            if (this.data.loggedinUsermvnoid === 1) {
                return nameStr;
            } else {
                const selectedSite = this.siteNameListData?.find(
                    (site) => site.name === nameStr
                );
                return selectedSite ? selectedSite.id : null;
            }
        })();

        this.serviceAreaGroupForm.patchValue({
            name: this.editableData.name ?? "",
            siteName: siteNameValue,
            latitude: latitude,
            longitude: longitude,
            radius: this.editableData.radius ?? "",
            blockNo: this.editableData.blockNo ?? "",
            cityid: city ? city.id : "",
            pincodes: this.editableData.pincodes ?? [],
            serviceAreaType: this.editableData.serviceAreaType ?? "",
            locationIds: locationIds,
            mvnoIds: mvnoIds,
            status: this.editableData.status ?? "",
            id: this.editableData.id
        });
        // } else {
        //     console.log("Condition FAILED. Form will not be patched.");
        // }
    }

    @ViewChild('searchLocationDialog') searchLocationDialog!: TemplateRef<any>;


    openSearchModel() {
        this.data.customerrMyInventoryView;
        this.data.assignInventoryWithSerial;
        this.data.ifsearchLocationModal;
        this.data.currentPagesearchLocationList;

        if (!this.searchLocationDialog) {
            console.error('undefine');
            return;
        }
        if (this.data.ifsearchLocationModal) {
            this.dialogRef2 = this.dialog.open(this.searchLocationDialog, {
                width: '50%',
                height: 'auto',
                autoFocus: false,
                disableClose: true
            });
            this.dialogRef2.afterClosed().subscribe(result => {
                this.dialogRef2 = null;
            });
        }
    }
    onCancelSEarch() {
        this.dialogRef2.close(null);
    }

    searchLocation() {
        if (this.searchLocationForm.valid) {
            const url =
                "/serviceArea/getPlaceId?query=" + this.searchLocationForm.value.searchLocationname;
            this.serviceAreaSErvice.getMethod(url).subscribe(
                (response: any) => {
                    this.searchLocationData = response.locations;
                },
                (error: any) => {
                    if (error.error.code == 422) {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                        // this.messageService.add({
                        //     severity: "error",
                        //     summary: "Error",
                        //     detail: error.error.error,
                        //     icon: "far fa-times-circle"
                        // });
                    } else {
                        this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                        // this.messageService.add({
                        //     severity: "error",
                        //     summary: "Error",
                        //     detail: error.error.ERROR,
                        //     icon: "far fa-times-circle"
                        // });
                    }
                }
            );
        }
    }
    clearLocationForm() {
        this.submitted = false;
        this.searchLocationData = [];

        this.searchLocationForm.reset({
            searchLocationname: ""
        });

        this.searchLocationForm.markAsPristine();
        this.searchLocationForm.markAsUntouched();
        this.searchLocationForm.updateValueAndValidity();
    }
    filedLocation(placeId) {
        const url = "/serviceArea/getLatitudeAndLongitude?placeId=" + placeId;
        this.serviceAreaSErvice.getMethod(url).subscribe(
            (response: any) => {
                this.data.ifsearchLocationModal = false;
                this.serviceAreaGroupForm.patchValue({
                    latitude: response.location.latitude,
                    longitude: response.location.longitude
                });

                this.iflocationFill = true;
                this.closebutton.nativeElement.click();
                this.searchLocationData = [];
                this.searchLocationForm.reset();
            },
            (error: any) => {
                // console.log(error, 'error')
                this.toastr.error(`${error.error.ERROR}`, 'Failed!')
                // this.messageService.add({
                //     severity: "error",
                //     summary: "Error",
                //     detail: error.error.ERROR,
                //     icon: "far fa-times-circle"
                // });
            }
        );
    }
    pageChangedSearchLocationList(currentPage) {
        this.currentPagesearchLocationList = currentPage;
    }

    handleAddressChange(address: any) {
        this.lat = address.geometry.location.lat();
        this.lng = address.geometry.location.lng();
        this.zoom = 20;
        this.markers = [];
        this.markers.push({
            lat: address.geometry.location.lat(),
            lng: address.geometry.location.lng(),
            draggable: false
        });
    }

    onMapReady(map) {
        this.map = map;
        this.initDrawingManager(map);
    }

    initDrawingManager(map: any) {
        // this.drawingManager = "";
        // let drawingControl: boolean = false;
        // if (this.data.polygonCreateAccess) {
        //     drawingControl = true;
        // } else {
        //     drawingControl = false;
        // }

        // const options = {
        //     drawingMode: google.maps.drawing.OverlayType.MARKER,

        //     drawingControl: true,
        //     drawingControlOptions: {
        //         position: google.maps.ControlPosition.TOP_CENTER,
        //         drawingModes: [google.maps.drawing.OverlayType.MARKER,
        //         google.maps.drawing.OverlayType.CIRCLE,
        //         google.maps.drawing.OverlayType.POLYGON,
        //         google.maps.drawing.OverlayType.POLYLINE,
        //         google.maps.drawing.OverlayType.RECTANGLE,]
        //     },
        //     markerOptions: {
        //         icon: "https://developers.google.com/maps/documentation/javascript/examples/full/images/beachflag.png",
        //     },
        //     polygonOptions: {
        //         draggable: false,
        //         editable: false,
        //         fillColor: "#FF0000", // Set fill color to red
        //         strokeColor: "#FF0000", // Set stroke color to red
        //         strokeOpacity: 0.8,
        //         strokeWeight: 2
        //     },
        //     circleOptions: {
        //         fillColor: "#ffff00",
        //         fillOpacity: 1,
        //         strokeWeight: 5,
        //         clickable: false,
        //         editable: true,
        //         zIndex: 1,
        //     },

        // };

        // this.drawingManager = new google.maps.drawing.DrawingManager(options);
        // this.drawingManager.setMap(map);
        // // if (!this.polygonCreateAccess) {
        // //     this.drawingManager.setDrawingMode(null);
        // // }

        // google.maps.event.addListener(this.drawingManager, "overlaycomplete", event => {
        //     if (event.type === google.maps.drawing.OverlayType.POLYGON) {
        //         this.drawnPolygonLatLongList = [];

        //         const len = event.overlay.getPath().getLength();
        //         this.currentPolygonCoordinates = [];

        //         for (let i = 0; i < len; i++) {
        //             const vertex = event.overlay.getPath().getAt(i);
        //             const vertexLatLng = { lat: vertex.lat(), lng: vertex.lng(), polyOrder: i + 1 };
        //             this.currentPolygonCoordinates.push(vertexLatLng);
        //         }

        //         this.drawingManager.setDrawingMode(null);
        //         // this.data.mArea = event.overlay;
        //         // this.drawingManager.setOptions({
        //         //     drawingControl: false
        //         // });
        //         this.data.isClearShow = true;

        //         // Show dialog for polygon name
        //         this.data.showPolygonNameDialog = true;
        //         // if (this.showPolygonNameDialog = true) {
        //         //     this.openPolygonNameDialog();
        //         // }
        //     }
        // });
    }

    savePolygon() { }
    hideMapModel() {
        this.dialogRef3.close(null);
    }
    onCancelMap() {
        this.dialogRef3.close(null);
    }
    clearDrawnData() { }
    uploadPolygonDocument() { }

    sieNameChange(event) {
        let siteName = this.serviceAreaGroupForm.value.siteName;
        if (!this.isServiceAreaEdit) {
            this.getPolygonListBySiteName(siteName);
        }
    }

    getPolygonListBySiteName(siteName) {
        if (siteName) {
            let url = `/serviceArea/getPolygonFromServiceArea/${siteName}`;
            this.serviceAreaSErvice.getMethod(url).subscribe(
                (response: any) => {
                    if (response.dataList && response.dataList.length > 0) {
                        this.drawnPolygonLatLongList = response.dataList.map(poly => ({
                            lat: poly.lat,
                            lng: poly.lng,
                            polyOrder: poly.polyOrder,
                            polygoneName: poly.polygoneName
                        }));
                        // this.drawPolygon(this.map, response.dataList);
                    }
                },
                (error: any) => { }
            );
        }
    }


    toggleSelectAll() {
        const pincodeControl = this.serviceAreaGroupForm.get('pincodes');
        if (this.isAllSelected()) {
            // Deselect all
            pincodeControl?.setValue([]);
        } else {
            // Select all
            const allPincodeIds = this.pincodeOptions.map(pincode => pincode.id);
            pincodeControl?.setValue(allPincodeIds);
        }
    }
    onSelectionChange(event: any) {
        // Filter out undefined values if Select All option gets selected
        const pincodeControl = this.serviceAreaGroupForm.get('pincodes');
        const currentValue = pincodeControl?.value || [];
        // Remove any undefined or null values
        const filteredValue = currentValue.filter((val: any) => val !== undefined && val !== null);
        if (filteredValue.length !== currentValue.length) {
            pincodeControl?.setValue(filteredValue, { emitEvent: false });
        }
    }
    isAllSelected(): boolean {
        const pincodeControl = this.serviceAreaGroupForm.get('pincodes');
        const selectedCount = pincodeControl?.value?.length || 0;
        const totalCount = this.pincodeOptions?.length || 0;
        return selectedCount === totalCount && totalCount > 0;
    }
    isIndeterminate(): boolean {
        const pincodeControl = this.serviceAreaGroupForm.get('pincodes');
        const selectedCount = pincodeControl?.value?.length || 0;
        const totalCount = this.pincodeOptions?.length || 0;
        return selectedCount > 0 && selectedCount < totalCount;
    }
}
