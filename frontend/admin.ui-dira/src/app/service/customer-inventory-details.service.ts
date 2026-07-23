import { Injectable } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { CustomerInventoryDetailsComponent } from "../components/customer-inventory-details/customer-inventory-details.component";
declare var $: any;
@Injectable({
    providedIn: "root"
})
export class CustomerInventoryDetailsService {
    constructor(public dialog: MatDialog) { }
    show(data: any) {
        // open modal specified by id
        // $("#" + id).modal("show");
        this.dialog.open(CustomerInventoryDetailsComponent, {
            width: "900px",
            data: data
        });
    }

    hide(id?: string) {
        // close modal specified by id
        // $("#" + id).modal("hide");
        this.dialog.closeAll();
    }
}
