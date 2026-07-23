import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { PrepaidRejectedReasonMasterComponent } from "./prepaid-rejected-reason-master.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialogModule } from "@angular/material/dialog";
import { MatCheckbox } from "@angular/material/checkbox";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { AddEditPrepaidRejectedReasonMasterComponent } from "./add-edit-prepaid-rejected-reason-master/add-edit-prepaid-rejected-reason-master.component";

const routes = [
  { path: "", component: PrepaidRejectedReasonMasterComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
  declarations: [PrepaidRejectedReasonMasterComponent, AddEditPrepaidRejectedReasonMasterComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes),
    SharedModule,
    MatCardContent,
    FormsModule ,
    MatCardModule,
    MatPaginator,
    MatDialogModule,
    MatCheckbox,
  ],
})
export class PrepaidRejectedReasonMasterModule {}
