import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
import { TaskCalendarComponent } from "./task-calendar.component";
import { FullCalendarModule } from "@fullcalendar/angular";
import { MatCard, MatCardContent, MatCardTitle } from "@angular/material/card";
import { CalendarModule, DateAdapter } from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
import { MatDialogActions } from "@angular/material/dialog";
import { MatDatepicker, MatDatepickerModule } from "@angular/material/datepicker";
import { MatDialogModule } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatRadioButton } from "@angular/material/radio";
import { MatTimepickerModule } from "@angular/material/timepicker";


const routes = [{ path: "", component: TaskCalendarComponent }];

@NgModule({
    declarations: [TaskCalendarComponent],
    imports: [CommonModule, RouterModule.forChild(routes), SharedModule, FullCalendarModule, MatCard, MatCardContent,
        CalendarModule.forRoot({ provide: DateAdapter, useFactory: adapterFactory }), MatCardTitle, MatDialogActions, MatDatepickerModule, MatDialogModule, MatPaginator, MatRadioButton, MatTimepickerModule],
    exports: [TaskCalendarComponent]
})
export class TaskCalendarModule { }
