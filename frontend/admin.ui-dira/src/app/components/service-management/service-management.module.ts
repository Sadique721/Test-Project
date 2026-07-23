import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { ServiceManagementComponent } from "./service-management.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatIconModule } from '@angular/material/icon';
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatDialogModule } from "@angular/material/dialog";
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { AddEditServiceDialogComponent } from "./add-edit-service-dialog/add-edit-service-dialog.component";
import { AddServiceParameterDialogComponent } from "./add-service-parameter-dialog/add-service-parameter-dialog.component";
import { FormsModule } from "@angular/forms";

const routes = [
    { path: "", component: ServiceManagementComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
    declarations: [ServiceManagementComponent,AddServiceParameterDialogComponent,AddEditServiceDialogComponent],
    imports: [FormsModule,CommonModule, MatPaginatorModule
        , MatCheckboxModule,  MatIconModule, MatCardModule, MatCardContent, MatDialogModule, RouterModule.forChild(routes), SharedModule],
})
export class ServiceManagementModule { }
