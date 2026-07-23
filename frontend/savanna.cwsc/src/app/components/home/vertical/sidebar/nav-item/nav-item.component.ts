import { Component, HostBinding, Input, OnChanges, Output, EventEmitter } from "@angular/core";
import { Router } from "@angular/router";
import { animate, state, style, transition, trigger } from "@angular/animations";
import { NavService } from "src/app/sidebar/services/nav.service";
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
  @Output() notify = new EventEmitter();
  @Output() secondLevelSelect = new EventEmitter();
  @Output() firstLevelSelect = new EventEmitter();
  @Input() item: any;
  @Input() depth: number = 0;
  @Input() isActive: boolean = false; // ADD THIS

  expanded = false;
  @HostBinding("attr.aria-expanded") ariaExpanded = this.expanded;

  constructor(
    public navService: NavService,
    public router: Router
  ) {}

  ngOnChanges(): void {
    const url = this.navService.currentUrl();
    if (
      this.item?.subMenu?.length &&
      ((this.item?.link?.trim() && (url?.indexOf(`/${this.item.link}`) ?? -1) === 0) ||
        this.isChildActive(this.item))
    ) {
      this.expanded = true;
      this.ariaExpanded = this.expanded;
    }
  }

  onItemSelected(item: any): void {
    console.log("Item clicked:", item);

    // L1 click - emit to parent to show L2 in top menu
    if (this.depth === 0) {
      console.log("Emitting firstLevelSelect for:", item.displayName);
      this.firstLevelSelect.emit(item);

      // If has link and NO children, navigate directly
      if (item.link && item.link.trim() !== "" && (!item.subMenu || item.subMenu.length === 0)) {
        this.router.navigate([item.link]);
      }

      if (window.innerWidth < 1024) {
        this.notify.emit(true);
      }
      return;
    }

    if (this.depth === 1 && item?.subMenu?.length) {
      console.log("Emitting secondLevelSelect for:", item.displayName);
      this.secondLevelSelect.emit(item);
    }

    // L2 or deeper - just navigate
    if (item.link && item.link.trim() !== "") {
      this.router.navigate([item.link]);
      if (window.innerWidth < 1024) {
        this.notify.emit(true);
      }
    }
  }

  isDirectlyActive(item: any): boolean {
    if (!item?.link || item.link.trim() === "") return false;
    const currentUrl = this.router.url;
    return currentUrl.includes(item.link);
  }

  isChildActive(item: any): boolean {
    if (!item?.subMenu || !item.subMenu.length) return false;
    const currentUrl = this.router.url;

    // Check all L2 children
    for (const child of item.subMenu) {
      // Check if L2 link matches
      if (child.link && currentUrl.includes(child.link)) {
        return true;
      }

      // Check L3 children of this L2
      if (child.subMenu?.length) {
        for (const l3 of child.subMenu) {
          if (l3.link && currentUrl.includes(l3.link)) {
            return true;
          }
        }
      }
    }

    return false;
  }

  // ADD THIS METHOD
  shouldBeSelected(): boolean {
    return this.isActive || this.isChildActive(this.item) || this.isDirectlyActive(this.item);
  }
}
