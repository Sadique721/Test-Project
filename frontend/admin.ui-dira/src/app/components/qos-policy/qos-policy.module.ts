import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { QosPolicyComponent } from "./qos-policy.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { DialogModule } from "primeng/dialog";
import { MatCard, MatCardContent, MatCardModule } from "@angular/material/card";
import { MaterialModule } from "src/app/material.module";
import { AddEditQosPolicyManagmentComponent } from "./add-edit-qos-policy-managment/add-edit-qos-policy-managment.component";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialogModule } from "@angular/material/dialog";
import { ReactiveFormsModule } from "@angular/forms";
const routes = [{ path: "", component: QosPolicyComponent, canDeactivate: [DeactivateService] }];

@NgModule({
    declarations: [QosPolicyComponent, AddEditQosPolicyManagmentComponent],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule, MatCardContent, MatCardModule, MatPaginator, MatDialogModule, ReactiveFormsModule],
})
export class QosPolicyModule { }
