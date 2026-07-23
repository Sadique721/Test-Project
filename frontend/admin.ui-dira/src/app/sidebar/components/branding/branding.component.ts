import { Component, Input } from '@angular/core';
import { CoreService } from 'src/app/app-setting/core.service';

@Component({
  selector: 'app-branding',
  imports: [],
  standalone: false,
  template: `
    <mat-toolbar>
        <img
            width="50"
            height="50"
            src="./assets/img/savbill_logo.jpg"
            alt="logo"
            style="margin-right: 10px;"
        />
        <span>{{userName}}</span>
    </mat-toolbar>
  `,
})
export class BrandingComponent {

    @Input() userName: string = "User";
    
    options = this.settings.getOptions();
  
    constructor(private settings: CoreService) {}
}
