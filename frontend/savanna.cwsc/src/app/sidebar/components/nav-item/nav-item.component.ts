import { Component, HostBinding, Input, OnChanges, Output, EventEmitter } from "@angular/core";
import { NavItem } from "./nav-item";
import { Router } from "@angular/router";
import { animate, state, style, transition, trigger } from "@angular/animations";
import { NavService } from "../../services/nav.service";
import { SharedModule } from "src/app/shared/shared.module";

@Component({
  selector: "app-nav-item",
  templateUrl: "./nav-item.component.html",
  styleUrls: [],
  standalone: true,
  imports: [SharedModule],
  animations: [
    trigger("indicatorRotate", [
      state("collapsed", style({ transform: "rotate(0deg)" })),
      state("expanded", style({ transform: "rotate(180deg)" })),
      transition("expanded <=> collapsed", animate("225ms cubic-bezier(0.4,0.0,0.2,1)"))
    ])
  ]
})
export class AppNavItemComponent implements OnChanges {
  @Output() toggleMobileLink: any = new EventEmitter<void>();
  @Output() notify: EventEmitter<boolean> = new EventEmitter<boolean>();

  @Input() isActive: boolean = false; // ADD THIS
  expanded: any = false;
  disabled: any = false;
  twoLines: any = false;
  @HostBinding("attr.aria-expanded") ariaExpanded = this.expanded;
  @Input() item: NavItem | any;
  @Input() depth: any;

  constructor(
    public navService: NavService,
    public router: Router
  ) {
    if (this.depth === undefined) {
      this.depth = 0;
    }
  }

  ngOnChanges() {
    const url = this.navService.currentUrl();
    if (this.item.link && url) {
      this.expanded = url.indexOf(`/${this.item.link}`) === 0;
      this.ariaExpanded = this.expanded;
    }
  }

  onItemSelected(item: NavItem) {
    if (!item.subMenu || !item.subMenu.length) {
      this.router.navigate([item.link]);
    }
    if (item.subMenu && item.subMenu.length) {
      this.expanded = !this.expanded;
    }
    //scroll
    window.scroll({
      top: 0,
      left: 0,
      behavior: "smooth"
    });
    if (!this.expanded) {
      if (window.innerWidth < 1024) {
        this.notify.emit();
      }
    }
  }

  onSubItemSelected(item: NavItem) {
    if (!item.subMenu || !item.subMenu.length) {
      if (this.expanded && window.innerWidth < 1024) {
        this.notify.emit();
      }
    }
  }

  isDirectlyActive(item: NavItem): boolean {
    return !!item.link && this.router.isActive(item.link, true);
  }

  isChildActive(item: NavItem): boolean {
    if (!item.subMenu) return false;
    return item.subMenu.some(child => this.isDirectlyActive(child) || this.isChildActive(child));
  }

  // ADD THIS METHOD
  shouldBeSelected(): boolean {
    return this.isActive || this.isChildActive(this.item) || this.isDirectlyActive(this.item);
  }
  isParentActive(item: any): boolean {
    if (!item.link) return false;

    return this.router.isActive(item.link, false);
  }
}
