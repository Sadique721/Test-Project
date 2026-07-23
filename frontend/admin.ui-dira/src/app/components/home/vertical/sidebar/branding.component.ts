import { Component, Input } from "@angular/core";
import { CoreService } from "src/app/app-setting/core.service";
import { savanaStusbar } from "src/assets/svgCode/savana-statusbar";

@Component({
    selector: "app-branding",
    standalone: false,
    template: `
    <mat-toolbar class="branding-toolbar">
    <img
    [src]="isCollapsed
    ? './assets/img/SAVBill.png'
    : './assets/img/SavBill PNG.png'"
     alt="logo"
    class="sidebar-logo"
     [ngClass]="isCollapsed ? 'sidebar-mini-logo' : 'sidebar-logo'" class="horizontal-col"
    [class.collapsed]="isCollapsed"
    />

</mat-toolbar>
  `,
    styles: [`
    .branding-toolbar {
      justify-content: center;
      min-height: 64px;
    }

    .sidebar-logo {
    //   width: 180px;
    //   height: auto;
    transition: all 0.3s ease;
    height: 230%;
    width: 230%;
    text-align: center;
    justify-content: center;
    margin-left: 30px;
    }
    .sidebar-mini-logo{
    height: 81%;
    margin-left: 0;
    }

    .sidebar-logo.collapsed {
      width: 45px;
    }
  `]
})
export class BrandingComponent {
    savanaStusbar
    options = this.settings.getOptions();
    @Input() isCollapsed = false;
    constructor(private settings: CoreService) { }
}
