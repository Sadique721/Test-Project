import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { RadiusAccessResponseManagementComponent } from "./radius-access-response-managemnt.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { MatCardContent, MatCardModule } from "@angular/material/card";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialogModule } from "@angular/material/dialog";

const routes = [
    {
        path: "",
        component: RadiusAccessResponseManagementComponent,
        canDeactivate: [DeactivateService],
    },
];

@NgModule({
    declarations: [RadiusAccessResponseManagementComponent],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule, MatCardContent, MatCardModule, MatPaginator, MatDialogModule],
})
export class RadiusAccessResponseManagementModule { }
