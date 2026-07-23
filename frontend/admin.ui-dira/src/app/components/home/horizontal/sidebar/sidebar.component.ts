import {
    Component,
    OnInit,
    Input,
    ChangeDetectorRef,
    OnChanges,
    ViewChild,
    HostListener,
    ElementRef,
    AfterViewInit,
    AfterViewChecked,
    OnDestroy,
    SimpleChanges
} from "@angular/core";
// import { navItems } from "./sidebar-data";
import { Router } from "@angular/router";
// import { NavService } from '../../../../services/nav.service';
import { MediaMatcher } from "@angular/cdk/layout";
import { StaffService } from "src/app/service/staff.service";


@Component({
    selector: "app-horizontal-sidebar",
    templateUrl: "./sidebar.component.html",
    standalone: false
})
export class AppHorizontalSidebarComponent implements OnInit, OnChanges, AfterViewInit, AfterViewChecked, OnDestroy {
    //   navItems = navItems;
    @Input() navItems: any[] = [];
    //   navItems = [];
    parentActive = "";
    @Input() isMini = false;
    mobileQuery: MediaQueryList;
    private _mobileQueryListener: () => void;
    @ViewChild("navContainer", { static: false }) navContainer!: ElementRef;
    @ViewChild("navItemsContainer", { static: false }) navItemsContainer!: ElementRef;
    canScrollLeft = false;
    canScrollRight = false;
    showScrollButtons = false;
    private scrollAmount = 200; // pixels to scroll
    userId;
    public userName: string = '';
    public userEmail: string = '';
    userEmailId = "";

    // Tracking variables for scroll check
    private scrollCheckPending = false;
    private previousNavItemsLength = 0;
    private scrollListener: (() => void) | null = null;

    constructor(
        // public navService: NavService,
        public router: Router,
        media: MediaMatcher,
        private changeDetectorRef: ChangeDetectorRef,
        private staffService: StaffService,

    ) {
        this.mobileQuery = media.matchMedia("(min-width: 1100px)");
        this._mobileQueryListener = () => this.changeDetectorRef.detectChanges();
        this.mobileQuery.addListener(this._mobileQueryListener);
        this.router.events.subscribe(() => (this.parentActive = this.router.url.split("/")[2]));
    }

    ngOnInit(): void {
        this.parentActive = this.router.url.split("/")[2];

        this.router.events.subscribe(() => {
            this.parentActive = this.router.url.split("/")[2];
        });

        this.userId = localStorage.getItem("userId");
        this.staffService.getStaffUserData(this.userId).subscribe((response: any) => {
            this.userName = response?.Staff.username || "User";
            this.userEmailId = response?.Staff?.email || "Email";
        });
    }

    ngOnChanges(changes: SimpleChanges): void {
        // When navItems changes, schedule a scrollability check after DOM updates
        if (changes['navItems']) {
            this.scrollCheckPending = true;
        }
    }

    ngAfterViewInit() {
        // Wait for DOM to fully render before checking scrollability
        setTimeout(() => {
            this.checkScrollability();
            this.setupScrollListener();
        }, 100);
    }

    ngAfterViewChecked() {
        // Check scrollability after view updates if pending or if navItems length changed
        if (this.scrollCheckPending || this.navItems?.length !== this.previousNavItemsLength) {
            this.scrollCheckPending = false;
            this.previousNavItemsLength = this.navItems?.length || 0;
            // Use setTimeout to avoid ExpressionChangedAfterItHasBeenCheckedError
            setTimeout(() => {
                this.checkScrollability();
            }, 50);
        }
    }

    private setupScrollListener(): void {
        if (this.navContainer?.nativeElement && !this.scrollListener) {
            this.scrollListener = () => this.checkScrollability();
            this.navContainer.nativeElement.addEventListener('scroll', this.scrollListener);
        }
    }

    @HostListener("window:resize", ["$event"])
    onResize(event: any) {
        // Delay check to allow layout to settle
        setTimeout(() => {
            this.checkScrollability();
        }, 100);
    }

    checkScrollability() {
        if (!this.navContainer?.nativeElement || !this.navItemsContainer?.nativeElement) return;

        const container = this.navContainer.nativeElement;
        const itemsContainer = this.navItemsContainer.nativeElement;

        const containerWidth = container.offsetWidth;
        const itemsWidth = itemsContainer.scrollWidth;
        const scrollLeft = Math.round(container.scrollLeft);

        // Calculate the maximum scroll position with a small tolerance for floating point issues
        const maxScroll = itemsWidth - containerWidth;
        const tolerance = 2; // pixels

        // Content overflows if items width exceeds container width (with tolerance)
        const hasOverflow = itemsWidth > containerWidth + tolerance;

        // Update scroll button states
        const newCanScrollLeft = hasOverflow && scrollLeft > tolerance;
        const newCanScrollRight = hasOverflow && scrollLeft < maxScroll - tolerance;
        const newShowScrollButtons = hasOverflow;

        // Only update if values changed to avoid unnecessary change detection cycles
        if (this.canScrollLeft !== newCanScrollLeft ||
            this.canScrollRight !== newCanScrollRight ||
            this.showScrollButtons !== newShowScrollButtons) {

            this.canScrollLeft = newCanScrollLeft;
            this.canScrollRight = newCanScrollRight;
            this.showScrollButtons = newShowScrollButtons;

            // Trigger change detection to update the view
            this.changeDetectorRef.detectChanges();
        }
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
        // Clean up scroll listener
        if (this.scrollListener && this.navContainer?.nativeElement) {
            this.navContainer.nativeElement.removeEventListener('scroll', this.scrollListener);
            this.scrollListener = null;
        }
        // Clean up media query listener
        if (this.mobileQuery && this._mobileQueryListener) {
            this.mobileQuery.removeListener(this._mobileQueryListener);
        }
    }

    isDirectlyActive(item: any): boolean {
        return !!item.link && this.router.isActive(item.link, true);
    }

    isChildActive(item: any): boolean {
        if (!item.subMenu) return false;
        return item.subMenu.some(child => this.isDirectlyActive(child) || this.isChildActive(child));
    }
}
