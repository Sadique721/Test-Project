import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { RejectedReasonMasterComponent } from "./rejected-reason-master.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialogModule } from "@angular/material/dialog";
import { MatCheckbox } from "@angular/material/checkbox";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { AddEditRejectedReasonMasterComponent } from "./add-edit-rejected-reason-master/add-edit-rejected-reason-master.component";

const routes = [
  { path: "", component: RejectedReasonMasterComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
  declarations: [RejectedReasonMasterComponent, AddEditRejectedReasonMasterComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes),
    SharedModule,
    MatCardContent,
    FormsModule,
    MatCardModule,
    MatPaginator,
    MatDialogModule,
    MatCheckbox,
  ],
})
export class RejectedReasonMasterModule {}
