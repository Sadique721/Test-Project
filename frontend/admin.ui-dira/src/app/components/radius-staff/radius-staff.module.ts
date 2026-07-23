import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RadiusStaffComponent } from "./radius-staff.component";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { DeactivateService } from "src/app/service/deactivate.service";
import { FormsModule } from "@angular/forms";
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialogModule } from "@angular/material/dialog";
import { MatCheckbox } from "@angular/material/checkbox";
import { SelectorDialogModule } from "../common/selector-dialog/selector-dialog.module";

const routes = [{ path: "", component: RadiusStaffComponent, canDeactivate: [DeactivateService] }];

@NgModule({
    declarations: [RadiusStaffComponent],
    imports: [CommonModule, FormsModule, RouterModule.forChild(routes), SharedModule, MatCardContent, MatCardModule,
        MatPaginator, MatDialogModule, MatCheckbox, SelectorDialogModule],
})
export class RadiusStaffModule { }
