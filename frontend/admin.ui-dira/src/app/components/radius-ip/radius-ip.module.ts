import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { DeactivateService } from "src/app/service/deactivate.service";
import { RadiusIpManagementComponent } from "./radius-ip.component";
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialogModule } from "@angular/material/dialog";
import { MatChipsModule } from '@angular/material/chips';

const routes = [
    { path: "", component: RadiusIpManagementComponent, canDeactivate: [DeactivateService] }
];

@NgModule({
    declarations: [RadiusIpManagementComponent],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule, MatCardContent, MatCardModule,
        MatPaginator, MatDialogModule, MatChipsModule]
})
export class RadiusIpManagementModule { }
