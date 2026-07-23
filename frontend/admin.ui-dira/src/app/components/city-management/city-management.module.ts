import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { CityManagementComponent } from "./city-management.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialogModule } from "@angular/material/dialog";
import { ReactiveFormsModule } from "@angular/forms";
import { AddEditCityManagmentComponent } from "./add-edit-city-managment/add-edit-city-managment.component";

const routes = [
    { path: "", component: CityManagementComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
    declarations: [CityManagementComponent, AddEditCityManagmentComponent],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule, MatCardContent, MatCardModule,
        MatPaginator, MatDialogModule, ReactiveFormsModule,
    ],

})
export class CityManagementModule { }
