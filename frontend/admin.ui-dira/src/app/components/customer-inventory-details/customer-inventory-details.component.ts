import { Component, OnInit, Input, Inject } from "@angular/core";
import { MAT_DIALOG_DATA } from "@angular/material/dialog";
import { Observable } from "rxjs";
import { Customer } from "../customer-inventory-management/customer-inventory-management.component";
import { CustomerInventoryDetailsService } from "../../service/customer-inventory-details.service";
@Component({
    selector: "app-customer-inventory-details",
    templateUrl: "./customer-inventory-details.component.html",
    styleUrls: ["./customer-inventory-details.component.css"],
    standalone: false
})
export class CustomerInventoryDetailsComponent implements OnInit {
    @Input() dialogId: string;
    inventoryDetailData: any;
    oldMAC: any = "";
    newMAC: any = "";
    oldSerial: any = "";
    newSerial: any = "";
    constructor(
        @Inject(MAT_DIALOG_DATA) public inventoryData: any,
        public CustomerInventoryDetailsService: CustomerInventoryDetailsService
    ) { }

    ngOnInit(): void {
        // this.inventoryData.subscribe(value => {
        // console.log("value", value);
        // if (value.inventoryData != "") {
        if (this.inventoryData != "") {
            this.inventoryDetailData = this.inventoryData;
            this.oldMAC = this.inventoryDetailData?.inOutWardMACMapping[0]?.macAddress;
            this.newMAC = this.inventoryDetailData?.inOutWardMACMapping[1]
                ? this.inventoryDetailData?.inOutWardMACMapping[1]?.macAddress
                : "";
            this.oldSerial = this.inventoryDetailData?.inOutWardMACMapping[0]?.serialNumber;
            this.newSerial = this.inventoryDetailData?.inOutWardMACMapping[1]
                ? this.inventoryDetailData?.inOutWardMACMapping[1]?.serialNumber
                : "";
        } else {
            this.inventoryDetailData = {};
        }
        // });
    }
    closeDialog() {
        this.CustomerInventoryDetailsService.hide();
    }
}
