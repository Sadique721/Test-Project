import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { IpManagementComponent } from "./ip-management.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { MatCard } from "@angular/material/card";
import { MaterialModule } from "src/app/material.module";

const routes = [{ path: "", component: IpManagementComponent, canDeactivate: [DeactivateService] }];

@NgModule({
    declarations: [IpManagementComponent],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule, MatCard, MaterialModule],
})
export class IpManagementModule { }
