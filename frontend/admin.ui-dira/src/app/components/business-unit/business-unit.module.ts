import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";

import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { BusinessUnitComponent } from "./business-unit.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { MatCardModule } from "@angular/material/card";
import { MatPaginator } from "@angular/material/paginator";
import { MatDialogModule } from "@angular/material/dialog";

const routes = [{ path: "", component: BusinessUnitComponent, canDeactivate: [DeactivateService] }];

@NgModule({
    declarations: [BusinessUnitComponent],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule, MatCardModule, MatPaginator, MatDialogModule],
})
export class BusinessUnitModule { }
