import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { BrandingComponent } from 'src/app/components/home/vertical/sidebar/branding.component';
import { SharedModule } from 'src/app/shared/shared.module';

@Component({
    selector: 'app-logo-container',
    standalone: true,
      imports: [SharedModule,BrandingComponent],

    templateUrl: './logo-container.component.html'
})
export class LogoContainerComponent implements OnInit {
  constructor() { }
  @Input() showToggle = true;
  @Input() userName: string = "User";
  
  @Output() toggleMobileNav = new EventEmitter<void>();
  @Output() toggleCollapsed = new EventEmitter<void>();

  ngOnInit(): void { }
}