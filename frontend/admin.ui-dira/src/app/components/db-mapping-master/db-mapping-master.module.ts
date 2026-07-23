import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { DBMappingMasterComponent } from "./db-mapping-master.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { ReactiveFormsModule } from "@angular/forms";
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatDialogModule } from "@angular/material/dialog";
import { MatCheckboxModule } from "@angular/material/checkbox";

const routes = [
  { path: "", component: DBMappingMasterComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
  declarations: [DBMappingMasterComponent],
  imports: [
     CommonModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes),
    SharedModule,
    MatCardModule,
    MatCardContent,
    MatPaginatorModule,
    MatDialogModule,
    MatCheckboxModule,
    CommonModule, RouterModule.forChild(routes), SharedModule],
})
export class DbMappingMasterModule {}
