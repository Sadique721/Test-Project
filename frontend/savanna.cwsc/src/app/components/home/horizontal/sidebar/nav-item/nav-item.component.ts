import { Component, OnInit, Input, Renderer2, ElementRef } from "@angular/core";
import { Router } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";
// import { NavService } from '../../../../../services/nav.service';
// import { TablerIconsModule } from 'angular-tabler-icons';

@Component({
  selector: "app-horizontal-nav-item",
  templateUrl: "./nav-item.component.html",
  standalone: true,
  imports: [SharedModule]
})
export class AppHorizontalNavItemComponent implements OnInit {
  @Input() depth: any;
  @Input() item: any;

  constructor(
    // public navService: NavService
    public router: Router,
    private renderer: Renderer2,
    private elementRef: ElementRef
  ) {
    if (this.depth === undefined) {
      this.depth = 0;
    }
  }

  ngOnInit() {}
  onItemSelected(item: any) {
    if (!item.subMenu || !item.subMenu.length) {
      this.router.navigate([item.link]);
    }
  }

  getDepth() {
    return this.depth + 1;
  }

  onItemHover(event: any) {
    const childSubMenu = this.elementRef.nativeElement.querySelector("#childSubMenu");
    const rect = event.target.getBoundingClientRect();
    if (this.depth === 1) {
      this.renderer.setStyle(childSubMenu, "left", `${rect.left + 230}px`);
      this.renderer.setStyle(childSubMenu, "top", `${rect.bottom - 50}px`);
    } else {
      this.renderer.setStyle(childSubMenu, "left", `${rect.left}px`);
      this.renderer.setStyle(childSubMenu, "top", `${rect.bottom}px`);
    }
  }
}
