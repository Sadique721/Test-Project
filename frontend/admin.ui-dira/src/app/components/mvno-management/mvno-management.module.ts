import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { SharedModule } from "src/app/shared/shared.module";
import { MaterialModule } from "src/app/material.module";
import { MvnoManagementComponent } from "./mvno-management.component";
import { MvnoListComponent } from "./mvno-list/mvno-list.component";
import { MvnoCreateComponent } from "./mvno-create/mvno-create.component";
import { MvnoDetailsComponent } from "./mvno-details/mvno-details.component";
import { MvnoRoutingModule } from "./mvno-management-routing.module";

@NgModule({
    declarations: [
        MvnoManagementComponent,
        MvnoListComponent,
        MvnoCreateComponent,
        MvnoDetailsComponent,
    ],
    imports: [
        CommonModule,
        SharedModule,
        MaterialModule,
        MvnoRoutingModule,
    ],
})
export class MvnoManagementModule { }
