import { Component, Input } from '@angular/core';

@Component({
    selector: 'view-detail-card',
    templateUrl: './view-detail-card.component.html',
    styleUrl: './view-detail-card.component.css',
    standalone: true
})

export class ViewDetailCardComponent {

    @Input() title: string = ""
    @Input() value: string = ""

}
