import { ChangeDetectorRef, Component, OnInit, ViewChild } from "@angular/core";
import { MatSnackBar } from "@angular/material/snack-bar";
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterModule } from "@angular/router";
import { filter, Subscription } from "rxjs";
import { AppSettings } from "src/app/app-setting/config";
import { CoreService } from "src/app/app-setting/core.service";
import { LoginService } from "src/app/service/login.service";
// import { SidebarService } from "src/app/service/sidebar.service";
import { StatusCheckService } from "src/app/service/status-check-service.service";
import { NavService } from "src/app/sidebar/services/nav.service";
import { BreakpointObserver } from "@angular/cdk/layout";
import { MatSidenav, MatSidenavContent, MatSidenavModule } from "@angular/material/sidenav";
// import { StaffService } from "src/app/service/staff.service";
import { AREA, CITY, COUNTRY, PINCODE, STATE } from "src/app/RadiusUtils/RadiusConstants";
import { CommonModule } from "@angular/common";
import { SharedModule } from "src/app/shared/shared.module";
import { BrandingComponent } from "src/app/sidebar/components/branding/branding.component";
import { LogoContainerComponent } from "src/app/sidebar/components/logo-container/logo-container.component";
import { NgScrollbarModule } from "ngx-scrollbar";
import { HeaderComponent } from "./vertical/header/header.component";
import { AppHorizontalHeaderComponent } from "./horizontal/header/header.component";
import { AppHorizontalNavItemComponent } from "./horizontal/sidebar/nav-item/nav-item.component";
import { AppHorizontalSidebarComponent } from "./horizontal/sidebar/sidebar.component";
import { CustomizerComponent } from "src/app/sidebar/components/customizer/customizer.component";
import { AppNavItemComponent } from "./vertical/sidebar/nav-item/nav-item.component";
import { IconHome, IconUser, IconBell } from "angular-tabler-icons/icons";
// import { TablerIconsModule } from "angular-tabler-icons";
import { MatToolbarModule } from "@angular/material/toolbar";
import { MatMenuModule } from "@angular/material/menu";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { TablerIconsModule } from "angular-tabler-icons";
import { AppBreadcrumbComponent } from "src/app/shared/components/breadcrumb/breadcrumb.component";
import { MatListModule } from "@angular/material/list";
import { MatTooltipModule } from "@angular/material/tooltip";
import { MatDividerModule } from "@angular/material/divider";
import { CommondropdownService } from "src/app/service/commondropdown.service";
import { CustomerdetailsilsService } from "src/app/service/customerdetailsils.service";
import { data } from "jquery";

const MOBILE_VIEW = "screen and (max-width: 768px)";
const TABLET_VIEW = "screen and (min-width: 769px) and (max-width: 1024px)";
const MONITOR_VIEW = "screen and (min-width: 1024px)";
const BELOWMONITOR = "screen and (max-width: 1023px)";

@Component({
  selector: "app-home",
  templateUrl: "./home.component.html",
  styleUrls: ["./home.component.css"],
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    HeaderComponent,
    AppHorizontalHeaderComponent,
    AppHorizontalNavItemComponent,
    AppHorizontalSidebarComponent,
    LogoContainerComponent,
    BrandingComponent,
    CustomizerComponent,
    AppNavItemComponent,
    AppBreadcrumbComponent,
    NgScrollbarModule,
    MatToolbarModule,
    MatMenuModule,
    MatButtonModule,
    MatIconModule,
    TablerIconsModule,
    RouterLink,
    RouterLinkActive,
    MatListModule,
    MatDividerModule,
    MatTooltipModule,
    MatSidenavModule,
    MatToolbarModule
  ]
})
export class HomeComponent implements OnInit {
  @ViewChild("leftsidenav", { static: false }) leftsidenav!: MatSidenav;
  @ViewChild("customizerRight", { static: false }) customizerRight!: MatSidenav;
  @ViewChild("content", { static: false }) content!: MatSidenavContent;

  public sidenav: MatSidenav;
  resView = false;
  options = this.coreService.getOptions();
  private layoutChangesSubscription = Subscription.EMPTY;
  private isMobileScreen = false;
  private isContentWidthFixed = true;
  private isCollapsedWidthFixed = false;
  private htmlElement!: HTMLHtmlElement;

  userName = "";
  userEmailId = "";
  mainMenuList: any[] = [];
  navItems: any[] = [];

  // Top menu state
  activeL1: any | null = null;
  activeL2Menu: any[] = [];
  activeL3: any | null = null;
  hoveredL2: any | null = null;

  apps = [];
  isFilterNavOpen = false;
  userId;
  profileImg: string = "";

  constructor(
    private snack: MatSnackBar,
    // public sidebarService: SidebarService,
    public loginService: LoginService,
    public statusCheckService: StatusCheckService,
    private coreService: CoreService,
    private router: Router,
    private breakpointObserver: BreakpointObserver,
    private changeDetectorRef: ChangeDetectorRef,
    public customerdetailsilsService: CustomerdetailsilsService,
    private navService: NavService,
    public commondropdownService: CommondropdownService,
    private cdr: ChangeDetectorRef
  ) {
    this.htmlElement = document.querySelector("html")!;
    this.layoutChangesSubscription = this.breakpointObserver
      .observe([MOBILE_VIEW, TABLET_VIEW, MONITOR_VIEW, BELOWMONITOR])
      .subscribe(state => {
        const saved = localStorage.getItem("optionTheme");
        if (saved) {
          this.options.theme = saved;
        }
        this.options.sidenavOpened = true;
        this.isMobileScreen = state.breakpoints[BELOWMONITOR];
        if (this.options.sidenavCollapsed == false) {
          this.options.sidenavCollapsed = state.breakpoints[TABLET_VIEW];
        }
        this.isContentWidthFixed = state.breakpoints[MONITOR_VIEW];
        this.resView = state.breakpoints[BELOWMONITOR];
      });

    this.receiveOptions(this.options);

    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        console.log("🚀 Navigation completed to:", event.urlAfterRedirects);

        // Scroll to top
        this.content?.scrollTo({ top: 0 });

        // Sync menu after navigation completes
        setTimeout(() => {
          this.syncMenuWithRoute();
        }, 0);
      });
  }

  ngOnInit(): void {
    this.userId = this.commondropdownService.getUserId();
    this.mainMenuList = this.getMainMenuList();
    this.mainMenuList = this.mainMenuList.map(menu => {
      menu["mainMenuName"] = menu.displayName;
      menu["subMenuName"] = "";
      if (true || this.loginService.hasPermission(menu?.moduleName)) {
        menu.isVisible = true;
      }

      menu?.subMenu?.length &&
        menu?.subMenu?.map(subMenu => {
          subMenu["mainMenuName"] = menu.displayName;
          subMenu["subMenuName"] = subMenu.displayName;
          if (true || this.loginService.hasPermission(subMenu?.moduleName)) {
            subMenu.isVisible = true;
          }
          return subMenu;
        });
      return menu;
    });
    this.userDetial();
    // this.statusCheckService.getServiceStatus();

    setTimeout(() => {
      console.log("🎯 Initial sync on page load");
      this.syncMenuWithRoute();
    }, 100);
  }
  userDetial() {
    const url = "/customers/" + this.userId;
    this.customerdetailsilsService.getMethod(url).subscribe((response: any) => {
      let data = { ...response.customers };
      this.userName = data.username || "User";
      this.userEmailId = data?.email || "Email";
      this.profileImg = response?.customers.profileImage;
    });
  }

  get isOver(): boolean {
    return this.isMobileScreen;
  }

  get isTablet(): boolean {
    return this.resView;
  }

  toggleFilterNav() {
    this.isFilterNavOpen = !this.isFilterNavOpen;
    this.changeDetectorRef.detectChanges();
  }

  ngOnDestroy() {
    this.layoutChangesSubscription.unsubscribe();
  }

  toggleCollapsed() {
    this.isContentWidthFixed = false;
    this.options.sidenavCollapsed = !this.options.sidenavCollapsed;
    this.resetCollapsedState();
  }

  resetCollapsedState(timer = 400) {
    setTimeout(() => this.coreService.setOptions(this.options), timer);
  }

  onSidenavClosedStart() {
    this.isContentWidthFixed = false;
  }

  onSidenavOpenedChange(isOpened: boolean) {
    this.isCollapsedWidthFixed = !this.isOver;
    this.options.sidenavOpened = isOpened;
    this.coreService.setOptions(this.options);
  }

  receiveOptions(options: AppSettings): void {
    localStorage.setItem("optionTheme", options.theme);
    this.toggleDarkTheme(options);
    this.toggleColorsTheme(options);
  }

  toggleDarkTheme(options: AppSettings) {
    if (options.theme === "dark") {
      this.htmlElement.classList.add("dark-theme");
      this.htmlElement.classList.remove("light-theme");
    } else {
      this.htmlElement.classList.remove("dark-theme");
      this.htmlElement.classList.add("light-theme");
    }
  }

  toggleColorsTheme(options: AppSettings) {
    this.htmlElement.classList.forEach(className => {
      if (className.endsWith("_theme")) {
        this.htmlElement.classList.remove(className);
      }
    });
    this.htmlElement.classList.add(options.activeTheme);
  }
  onSecondLevelSelect(item: any): void {
    console.log("onSecondLevelSelect called in home component with:", item);
    console.log("SubMenu:", item.subMenu);

    this.activeL1 = item;
    this.activeL2Menu = item.subMenu || [];

    console.log("activeL2Menu set to:", this.activeL2Menu);
    console.log("activeL2Menu length:", this.activeL2Menu.length);
    this.onFirstLevelSelect(item);
    // Force change detection
    this.changeDetectorRef.detectChanges();
  }

  onL2Hover(l2Item: any): void {
    this.hoveredL2 = l2Item;
  }

  onL2Leave(): void {
    setTimeout(() => {
      this.hoveredL2 = null;
    }, 200);
  }
  onFirstLevelSelect(item: any): void {
    this.activeL1 = item;
    this.activeL2Menu = item?.subMenu?.filter((sub: any) => sub.isVisible) || [];
    this.activeL3 = null;

    // Update NavService signal for breadcrumb

    this.navService.setMenuSelection(item.displayName);
    console.log("First level menu selected:", item);
    if (item?.subMenu[0]?.link) {
      console.log("Navigating to first submenu link:", item.subMenu[0].link);
      this.router.navigate([item.subMenu[0].link]);
    }
    if (item?.subMenu[0]?.subMenu[0]?.link) {
      console.log("Navigating to second submenu link:", item.subMenu[0].subMenu[0].link);
      this.router.navigate([item.subMenu[0].subMenu[0].link]);
    }
    this.cdr.detectChanges();
  }

  onSecondLevelClick(l2: any) {
    if (l2.link) {
      // Update NavService signal with both L1 and L2
      this.navService.setMenuSelection(this.activeL1?.displayName || "", l2.displayName);

      this.router.navigate([l2.link]);
    }
  }

  onThirdLevelClick(l3: any) {
    if (l3.link) {
      // Update breadcrumb with L3 name
      this.navService.setMenuSelection(
        this.activeL1?.displayName || "",
        l3.displayName // Use L3's displayName, not L2's
      );

      this.router.navigate([l3.link]);
      this.hoveredL2 = null;
    }
  }
  syncMenuWithRoute(): void {
    const currentUrl = this.router.url;
    console.log("🔍 syncMenuWithRoute - Current URL:", currentUrl);

    let matchFound = false;
    console.log("Main Menu List:", this.mainMenuList);
    for (const l1 of this.mainMenuList) {
      // if (!l1.subMenu?.length || l1.isVisible === false) continue;
      console.log("Checking L1:", this.mainMenuList);
      // Check L3 items first
      for (const l2 of l1.subMenu) {
        // if (l2.isVisible === false) continue;

        if (l2.subMenu?.length) {
          for (const l3 of l2.subMenu) {
            // if (l3.isVisible === false) continue;

            if (l3.link && currentUrl.includes(l3.link)) {
              console.log("✅ L3 Match - Setting activeL1 to:", l1.displayName);
              this.activeL1 = l1; // This is critical
              this.activeL2Menu = l1.subMenu.filter((sub: any) => sub.isVisible);
              console.log("Active L2 Menu:", this.activeL2Menu, this.activeL1);
              this.navService.setMenuSelection(l1.displayName, l3.displayName);
              matchFound = true;
              break;
            }
          }
        }
      }

      // Check L2 items
      if (!matchFound) {
        for (const l2 of l1.subMenu) {
          // if (l2.isVisible === false) continue;

          if (l2.link && currentUrl.includes(l2.link)) {
            console.log("✅ L2 Match - Setting activeL1 to:", l1.displayName);
            this.activeL1 = l1; // This is critical
            this.activeL2Menu = l1.subMenu.filter((sub: any) => sub.isVisible);
            console.log("Active L2 Menu:", this.activeL2Menu, this.activeL1);

            this.navService.setMenuSelection(l1.displayName, l2.displayName);
            matchFound = true;
            break;
          }
        }
      }
    }

    console.log("📋 Final activeL1:", this.activeL1?.displayName);
    console.log("📋 Final activeL2Menu:", this.activeL2Menu?.length);

    this.cdr.detectChanges();
  }

  isL2Active(l2: any): boolean {
    const currentUrl = this.router.url;

    // Check if L2 itself is active
    if (l2.link && currentUrl.includes(l2.link)) {
      return true;
    }

    // Check if any L3 child is active
    if (l2.subMenu?.length) {
      return l2.subMenu.some((l3: any) => l3.link && currentUrl.includes(l3.link));
    }

    return false;
  }
  getMainMenuList = () => {
    const menuList = [
      {
        displayName: "Home",
        iconName: "Home.svg",
        link: "/home/dashboard",
        subMenu: [],
        moduleName: "",
        isVisible: false
      },
      {
        displayName: "Customer",
        iconName: "Lead_Management.svg",
        link: "/home/customer",
        subMenu: [],
        moduleName: "",
        isVisible: false
      },
      {
        displayName: "Ticket Management",
        iconName: "Revenue_Management.svg",
        link: "/home/ticketManagement",
        subMenu: [],
        moduleName: "",
        isVisible: false
      },
      {
        displayName: "Subscription History",
        iconName: "people-community.svg",
        link: "/home/subscriptionHistory",
        subMenu: [],
        moduleName: "",
        isVisible: false
      },
      {
        displayName: "Usage History",
        iconName: "Audit _Compliance.svg",
        link: "/home/usageHistory",
        subMenu: [],
        moduleName: "",
        isVisible: false
      }
    ];
    return menuList;
  };
}
