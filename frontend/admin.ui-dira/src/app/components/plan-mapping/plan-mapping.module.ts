import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { PlanMappingComponent } from "./plan-mapping.component";
import { DeactivateService } from "src/app/service/deactivate.service";
import { FormsModule } from '@angular/forms';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatStepper, MatStepperModule } from "@angular/material/stepper";
import { MatRadioModule } from "@angular/material/radio";
import { MatDatepicker } from "@angular/material/datepicker";


const routes = [{ path: "", component: PlanMappingComponent, canDeactivate: [DeactivateService] }];

@NgModule({
    declarations: [PlanMappingComponent],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule, CommonModule,
        FormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatIconModule,
        MatTableModule,
        MatSortModule,
        MatPaginatorModule,
        MatTooltipModule, MatStepper, MatStepperModule, MatRadioModule, MatDatepicker],
})
export class PlanMappingModule { }

