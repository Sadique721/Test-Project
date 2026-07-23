import { Component, Input } from "@angular/core";
import { CoreService } from "src/app/app-setting/core.service";
import { SharedModule } from "src/app/shared/shared.module";

@Component({
  selector: "app-branding",
  standalone: true,
  imports: [SharedModule],
  template: `
    <mat-toolbar>
      <img
        width="50"
        height="50"
        src="./assets/img/savbill_logo.jpg"
        alt="logo"
        style="margin-right: 10px;"
      />
      <span>Savbill</span>
    </mat-toolbar>
  `
})
export class BrandingComponent {
  @Input() userName!: string;
  options = this.settings.getOptions();
  constructor(private settings: CoreService) {}
}
