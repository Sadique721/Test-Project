import {
    Component,
    EventEmitter,
    Input,
    OnInit,
    Output,
} from '@angular/core';

@Component({
    selector: 'app-logo-container',
    standalone: false,
    templateUrl: './logo-container.component.html'
})
export class LogoContainerComponent implements OnInit {
    constructor() { }
    @Input() showToggle = true;
    @Input() userName: string = "User";

    @Output() toggleMobileNav = new EventEmitter<void>();
    @Output() toggleCollapsed = new EventEmitter<void>();
    @Input() isCollapsed = false;

    ngOnInit(): void { }
}