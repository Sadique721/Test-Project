import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FieldTempMappingComponent } from "./field-temp-mapping.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { FormsModule } from "@angular/forms";
import { MatTableModule } from "@angular/material/table";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatCardModule } from "@angular/material/card";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatTabsModule } from "@angular/material/tabs";
import { MatCardContent } from "@angular/material/card";
const routes = [
    { path: "", component: FieldTempMappingComponent, canDeactivate: [DeactivateService] },
];

@NgModule({
    declarations: [FieldTempMappingComponent],
    imports: [[CommonModule, RouterModule.forChild(routes), SharedModule], FormsModule,
        MatTableModule, MatPaginatorModule, MatCardModule, MatButtonModule, MatIconModule,
        MatTabsModule, MatCardContent
    ],
})
export class FieldTempMappingModule { }
