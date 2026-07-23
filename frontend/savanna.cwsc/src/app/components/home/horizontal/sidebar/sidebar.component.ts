import {
  Component,
  OnInit,
  Input,
  ChangeDetectorRef,
  OnChanges,
  ViewChild,
  HostListener,
  ElementRef
} from "@angular/core";
// import { navItems } from "./sidebar-data";
import { Router } from "@angular/router";
// import { NavService } from '../../../../services/nav.service';
import { MediaMatcher } from "@angular/cdk/layout";
import { SharedModule } from "src/app/shared/shared.module";
import { AppHorizontalNavItemComponent } from "./nav-item/nav-item.component";

@Component({
  selector: "app-horizontal-sidebar",
  templateUrl: "./sidebar.component.html",
  standalone: true,
  imports: [SharedModule, AppHorizontalNavItemComponent]
})
export class AppHorizontalSidebarComponent implements OnInit {
  //   navItems = navItems;
  @Input() navItems: any[] = [];
  //   navItems = [];
  parentActive = "";

  mobileQuery: MediaQueryList;
  private _mobileQueryListener: () => void;
  @ViewChild("navContainer", { static: false }) navContainer!: ElementRef;
  @ViewChild("navItemsContainer", { static: false }) navItemsContainer!: ElementRef;
  canScrollLeft = false;
  canScrollRight = false;
  showScrollButtons = false;
  private scrollAmount = 200; // pixels to scroll

  constructor(
    // public navService: NavService,
    public router: Router,
    media: MediaMatcher,
    changeDetectorRef: ChangeDetectorRef
  ) {
    this.mobileQuery = media.matchMedia("(min-width: 1100px)");
    this._mobileQueryListener = () => changeDetectorRef.detectChanges();
    this.mobileQuery.addListener(this._mobileQueryListener);
    this.router.events.subscribe(() => (this.parentActive = this.router.url.split("/")[2]));
  }

  ngOnInit(): void {
    this.parentActive = this.router.url.split("/")[2];

    this.router.events.subscribe(() => {
      this.parentActive = this.router.url.split("/")[2];
    });
  }

  ngAfterViewInit() {
    this.checkScrollability();
  }

  @HostListener("window:resize", ["$event"])
  onResize(event: any) {
    this.checkScrollability();
  }

  checkScrollability() {
    if (!this.navContainer || !this.navItemsContainer) return;

    const container = this.navContainer.nativeElement;
    const itemsContainer = this.navItemsContainer.nativeElement;

    const containerWidth = container.offsetWidth;
    const itemsWidth = itemsContainer.scrollWidth;
    const scrollLeft = container.scrollLeft;

    // Show scroll buttons if content overflows
    this.showScrollButtons = itemsWidth > containerWidth;

    // Update scroll button states
    this.canScrollLeft = scrollLeft > 0;
    this.canScrollRight = scrollLeft < itemsWidth - containerWidth;
  }

  scrollLeft() {
    const container = this.navContainer.nativeElement;
    container.scrollBy({
      left: -this.scrollAmount,
      behavior: "smooth"
    });

    setTimeout(() => {
      this.checkScrollability();
    }, 300);
  }

  scrollRight() {
    const container = this.navContainer.nativeElement;
    container.scrollBy({
      left: this.scrollAmount,
      behavior: "smooth"
    });

    setTimeout(() => {
      this.checkScrollability();
    }, 300);
  }

  ngOnDestroy() {
    // Cleanup if needed
  }

  isDirectlyActive(item: any): boolean {
    return !!item.link && this.router.isActive(item.link, true);
  }

  isChildActive(item: any): boolean {
    if (!item.subMenu) return false;
    return item.subMenu.some(child => this.isDirectlyActive(child) || this.isChildActive(child));
  }
}
