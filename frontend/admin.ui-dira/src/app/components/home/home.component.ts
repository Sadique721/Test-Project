import { ChangeDetectorRef, Component, Input, OnInit, Output, ViewChild } from "@angular/core";
import { MatSnackBar } from "@angular/material/snack-bar";
import { NavigationEnd, Router } from "@angular/router";
import { filter, Subscription } from "rxjs";
import { AppSettings } from "src/app/app-setting/config";
import { CoreService } from "src/app/app-setting/core.service";
import { LoginService } from "src/app/service/login.service";
import { SidebarService } from "src/app/service/sidebar.service";
import { StatusCheckService } from "src/app/service/status-check-service.service";
import { NavService } from "src/app/sidebar/services/nav.service";
import { BreakpointObserver } from "@angular/cdk/layout";
import { MatSidenav, MatSidenavContent } from "@angular/material/sidenav";
import { StaffService } from "src/app/service/staff.service";
import { AREA, BUILDING, CITY, COUNTRY, PINCODE, STATE, SUBAREA } from "src/app/RadiusUtils/RadiusConstants";
import {
    PARTNERS, SALES_CRMS, PRE_CUST_CONSTANTS, POST_CUST_CONSTANTS, INVOICE_SYSTEMS, PAYMENT_SYSTEMS, CREDIT_NOTES, DUNNINGS, TICKETING_SYSTEMS, TASK_SYSTEMS,
    PRODUCTS, INTEGRATION_SYSTEMS, SETTINGS, NETWORKS, INVENTORYS, WORKFLOWS, MASTERS, AUDITS, RADIUS_CONSTANTS, NOTIFICATIONS, HOME, SUBSCRIBER_MANAGEMENT, CORE_SERVICE,
    INVENTORY_AND_ASSETS, AUDIT_AND_COMPLIANCE, ADMINISTRATION, RADIUS_MANAGEMENT, LOGIN_ACCESS, REVENUE_MANAGEMENT, SUPPORT, DAIMETER,
    DAIMETER_ACCESS
} from "src/app/constants/aclConstants";

const MOBILE_VIEW = "screen and (max-width: 768px)";
const TABLET_VIEW = "screen and (min-width: 769px) and (max-width: 1024px)";
const MONITOR_VIEW = "screen and (min-width: 1024px)";
const BELOWMONITOR = "screen and (max-width: 1023px)";
import { savanaStusbar } from "src/assets/svgCode/savana-statusbar";
import { EventEmitter } from "stream";


@Component({
    selector: "app-home",
    templateUrl: "./home.component.html",
    styleUrls: ["./home.component.css"],
    standalone: false
})
export class HomeComponent implements OnInit {
    @ViewChild("leftsidenav", { static: false }) leftsidenav!: MatSidenav;
    @ViewChild("customizerRight", { static: false }) customizerRight!: MatSidenav;
    @ViewChild("content", { static: false }) content!: MatSidenavContent;
    savanaStusbar
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
    loggedInStaffDetail: any;

    // Top menu state
    activeL1: any | null = null;
    activeL2Menu: any[] = [];
    activeL3: any | null = null;
    hoveredL2: any | null = null;

    apps = [];
    isFilterNavOpen = false;
    userId;
    profileImg: string = "";
    @Input() showToggle = true;
    // @Output() toggleCollapsed = new EventEmitter<void>();


    constructor(
        private snack: MatSnackBar,
        public sidebarService: SidebarService,
        public loginService: LoginService,
        public statusCheckService: StatusCheckService,
        private coreService: CoreService,
        private router: Router,
        private breakpointObserver: BreakpointObserver,
        private changeDetectorRef: ChangeDetectorRef,
        private staffService: StaffService,
        private navService: NavService,
        private cdr: ChangeDetectorRef
    ) {
        this.htmlElement = document.querySelector("html")!;
        this.layoutChangesSubscription = this.breakpointObserver
            .observe([MOBILE_VIEW, TABLET_VIEW, MONITOR_VIEW, BELOWMONITOR])
            .subscribe(state => {
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
            .pipe(filter((event) => event instanceof NavigationEnd))
            .subscribe((event: NavigationEnd) => {

                // Scroll to top
                this.content?.scrollTo({ top: 0 });

                // Sync menu after navigation completes
                setTimeout(() => {
                    this.syncMenuWithRoute();
                }, 0);
            });
    }

    ngOnInit(): void {
        this.userId = localStorage.getItem("userId");
        let mvnoId = Number(localStorage.getItem("mvnoId"));
        const showMvno = mvnoId == 1 ? true : false;
        this.mainMenuList = this.getMainMenuList();
        this.mainMenuList = this.mainMenuList.map(menu => {
            menu["mainMenuName"] = menu.displayName;
            menu["subMenuName"] = "";

            // ✅ Always show Daimeter menu
            if (menu.displayName === "Diameter") {
                menu.isVisible = true;
            }
            else if (this.loginService.hasPermission(menu?.moduleName)) {
                menu.isVisible = true;
            }

            menu?.subMenu?.length &&
                menu?.subMenu?.map(subMenu => {
                    subMenu["mainMenuName"] = menu.displayName;
                    subMenu["subMenuName"] = subMenu.displayName;

                    // ✅ Always show Diameter sub-menus
                    if (menu.displayName === "Diameter") {
                        subMenu.isVisible = true;
                    }
                    else if (
                        (subMenu?.displayName === "MVNO Management" ||
                            subMenu?.displayName === "Profile Management" ||
                            subMenu?.displayName === "Organization Profile") &&
                        !showMvno
                    ) {
                        subMenu.isVisible = false;
                    }
                    else if (this.loginService.hasPermission(subMenu?.moduleName)) {
                        subMenu.isVisible = true;
                    }

                    subMenu?.subMenu?.length &&
                        subMenu?.subMenu?.map(subMenu1 => {
                            if (menu.displayName === "Diameter") {
                                subMenu1.isVisible = true;
                            }
                            else if (this.loginService.hasPermission(subMenu1?.moduleName)) {
                                subMenu1.isVisible = true;
                            }

                            subMenu1?.subMenu?.length &&
                                subMenu1?.subMenu?.map(subMenu2 => {
                                    if (menu.displayName === "Diameter") {
                                        subMenu2.isVisible = true;
                                    }
                                    else if (this.loginService.hasPermission(subMenu2?.moduleName)) {
                                        subMenu2.isVisible = true;
                                    }
                                    return subMenu2;
                                });

                            return subMenu1;
                        });

                    return subMenu;
                });

            return menu;
        });


        // this.mainMenuList = this.getMainMenuList();
        // this.mainMenuList = this.mainMenuList.map(menu => {
        //     menu["mainMenuName"] = menu.displayName;
        //     menu["subMenuName"] = "";
        //     if (this.loginService.hasPermission(menu?.moduleName)) {
        //         menu.isVisible = true;
        //     }

        //     menu?.subMenu?.length &&
        //         menu?.subMenu?.map(subMenu => {
        //             subMenu["mainMenuName"] = menu.displayName;
        //             subMenu["subMenuName"] = subMenu.displayName;

        //             if ((subMenu?.displayName == "MVNO Management" || subMenu?.displayName == "Profile Management" || subMenu?.displayName == "Organization Profile") && !showMvno) {
        //                 subMenu.isVisible = false;
        //             } else if (this.loginService.hasPermission(subMenu?.moduleName)) {
        //                 subMenu.isVisible = true;
        //             }

        //             subMenu?.subMenu?.length &&
        //                 subMenu?.subMenu?.map(subMenu1 => {
        //                     if (this.loginService.hasPermission(subMenu1?.moduleName)) {
        //                         subMenu1.isVisible = true;
        //                     }
        //                     subMenu1?.subMenu?.length &&
        //                         subMenu1?.subMenu?.map(subMenu2 => {
        //                             if (this.loginService.hasPermission(subMenu2?.moduleName)) {
        //                                 subMenu2.isVisible = true;
        //                             }
        //                             return subMenu2;
        //                         });
        //                     return subMenu1;
        //                 });
        //             return subMenu;
        //         });
        //     return menu;
        // });

        this.statusCheckService.getServiceStatus();
        this.staffService.getStaffUserData(this.userId, true).subscribe((response: any) => {
            this.loggedInStaffDetail = response;
            this.userName = response?.Staff.username || "User";
            this.userEmailId = response?.Staff?.email || "Email";
            this.profileImg = response?.Staff.profileImage;
            this.changeDetectorRef.detectChanges();
        });

        setTimeout(() => {
            this.syncMenuWithRoute();
        }, 100);
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


        this.activeL1 = item;
        this.activeL2Menu = item.subMenu || [];


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
        this.activeL2Menu = item.subMenu?.filter((sub: any) => sub.isVisible) || [];
        this.activeL3 = null;

        // Update NavService signal for breadcrumb

        this.navService.setMenuSelection(item.displayName);

        this.redirectToFirstVisibleLink(item);
        this.cdr.detectChanges();
    }

    redirectToFirstVisibleLink(item: any) {
        const getFirstVisibleLink = (menu: any[]): string | null => {
            for (const menuItem of menu) {
                if (menuItem?.isVisible && menuItem?.link) {
                    return menuItem.link;
                }

                if (menuItem?.subMenu?.length) {
                    const link = getFirstVisibleLink(menuItem.subMenu);
                    if (link) return link;
                }
            }
            return null;
        };

        const firstLink = getFirstVisibleLink(item?.subMenu || []);
        if (firstLink) {
            this.router.navigate([firstLink]);
        }
    }

    onSecondLevelClick(l2: any) {
        if (l2.link) {
            // Update NavService signal with both L1 and L2
            this.navService.setMenuSelection(
                this.activeL1?.displayName || '',
                l2.displayName
            );

            this.router.navigate([l2.link]);
        }
    }

    onThirdLevelClick(l3: any) {
        if (l3.link) {
            // Update breadcrumb with L3 name
            this.navService.setMenuSelection(
                this.activeL1?.displayName || '',
                l3.displayName  // Use L3's displayName, not L2's
            );

            this.router.navigate([l3.link]);
            this.hoveredL2 = null;
        }
    }
    syncMenuWithRoute(): void {
        const currentUrl = this.router.url;

        let matchFound = false;
        for (const l1 of this.mainMenuList) {
            // Check if L1 itself has a direct link (like Home)
            if (l1.link && currentUrl.includes(l1.link)) {
                this.activeL1 = l1;
                this.activeL2Menu = [];
                this.navService.setMenuSelection(l1.displayName);
                matchFound = true;
                break;
            }

            // Skip if no subMenu
            if (!l1.subMenu?.length) continue;

            // Check L3 items first
            for (const l2 of l1.subMenu) {
                // if (l2.isVisible === false) continue;

                if (l2.subMenu?.length) {
                    for (const l3 of l2.subMenu) {
                        // if (l3.isVisible === false) continue;

                        if (l3.link && currentUrl.includes(l3.link)) {
                            this.activeL1 = l1; // This is critical
                            this.activeL2Menu = l1.subMenu.filter((sub: any) => sub.isVisible);
                            this.navService.setMenuSelection(l1.displayName, l3.displayName);
                            matchFound = true;
                            break;
                        }
                        else {
                            if (l3.subMenu?.length) {
                                for (const l4 of l3.subMenu) {
                                    if (l4.link && currentUrl.includes(l4.link)) {
                                        this.activeL1 = l1; // This is critical
                                        this.activeL2Menu = l1.subMenu.filter((sub: any) => sub.isVisible);
                                        this.navService.setMenuSelection(l1.displayName, l3.displayName);
                                        matchFound = true;
                                        break;
                                    }

                                }
                            }
                        }
                    }
                }
            }

            // Check L2 items
            if (!matchFound) {
                for (const l2 of l1.subMenu) {
                    // if (l2.isVisible === false) continue;

                    if (l2.link && currentUrl.includes(l2.link)) {
                        this.activeL1 = l1; // This is critical
                        this.activeL2Menu = l1.subMenu.filter((sub: any) => sub.isVisible);

                        this.navService.setMenuSelection(l1.displayName, l2.displayName);
                        matchFound = true;
                        break;
                    }
                }
            }
        }


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
            return l2.subMenu.some((l3: any) =>
                l3.link && currentUrl.includes(l3.link)
            );
        }

        return false;
    }
    getMainMenuList = () => {
        const menuList = [
            {
                displayName: "Home",
                iconName: "Home.svg",
                link: "/home/dashbord",
                subMenu: [],
                moduleName: `${HOME.HOME}`,
                isVisible: false
            },
            {
                displayName: "User Profile",
                iconName: "UserProfile.svg",
                link: "",
                subMenu: [
                    {
                        displayName: "Staff Details",
                        link: "/home/mystaff/92?mystaff=StaffDetails",
                        isVisible: false
                    },
                    {
                        displayName: "Staff Receipt Management",
                        link: "/home/mystaff/92?mystaff=StaffReceipt",
                        isVisible: false
                    },
                    {
                        displayName: "Wallet",
                        link: "/home/mystaff/92?mystaff=Wallet",
                        isVisible: false
                    },
                    {
                        displayName: "Change Password",
                        link: "/home/mystaff/92?mystaff=ChangePassword",
                        isVisible: false
                    },
                ],
                isVisible: false
            },
            {
                displayName: "Partner Hub",
                iconName: "Partner.svg",
                link: "",
                subMenu: [
                    {
                        displayName: "Plans & Bundles - Partner Plan Bundle",
                        iconName: "All_Icons/03_Partner_Management/B_Plan Bundle_B.png",
                        link: "/home/planBundle",
                        subMenu: [],
                        moduleName: `${PARTNERS.PARTNER_PLAN_BUNDLE}`,
                        isVisible: false
                    },
                    {
                        displayName: "Partner Accounts",
                        iconName: "All_Icons/03_Partner_Management/A_Partner_B.png",
                        link: "/home/partner",
                        subMenu: [],
                        moduleName: `${PARTNERS.PARTNER_LIST}`,
                        isVisible: false
                    },
                    {
                        displayName: "Balance Management",
                        iconName: "All_Icons/03_Partner_Management/B_Plan Bundle_B.png",
                        link: "/home/manageBalance",
                        subMenu: [],
                        moduleName: `${PARTNERS.MANAGE_BALANCE}`,
                        isVisible: false
                    }
                ],
                moduleName: `${PARTNERS.PARTNER_HUB}`,
                isVisible: false
            },
            {
                displayName: "Lead Hub",
                iconName: "Lead_Management.svg",
                link: "",
                subMenu: [
                    {
                        displayName: "Lead Sources",
                        iconName: "All_Icons/08_Ticketing_System/A_Resolution Master_B.png",
                        link: "/home/lead-source-master",
                        subMenu: [],
                        moduleName: `${SALES_CRMS.LEAD_SOURCE_MASTER}`,
                        isVisible: false
                    },
                    {
                        displayName: "Lead Management",
                        iconName: "All_Icons/08_Ticketing_System/C_Ticket_B.png",
                        link: "/home/lead-management",
                        subMenu: [],
                        moduleName: `${SALES_CRMS.LEAD}`,
                        isVisible: false
                    },
                    {
                        displayName: "Rejection Reasons",
                        iconName: "03_Rejected-Reason_B.png",
                        link: "/home/rejected-reason-master",
                        subMenu: [],
                        moduleName: `${SALES_CRMS.REJECTED_REASON_MASTER}`,
                        isVisible: false
                    },
                    // {
                    //     displayName: "Enterprise Accounts",
                    //     iconName: "All_Icons/08_Ticketing_System/C_Ticket_B.png",
                    //     link: "/home/enterprise-lead",
                    //     subMenu: [],
                    //     moduleName: "SALES_CRMS.ENTERPRISE_LEAD",
                    //     isVisible: false
                    // }
                ],
                moduleName: `${SALES_CRMS.LEAD_HUB}`,
                isVisible: false
            },
            {
                displayName: "Subscriber Management",
                iconName: "people-community.svg",
                link: "",
                subMenu: [
                    {
                        displayName: "Prepaid",
                        iconName: "All_Icons/04_Prepaid_Customer/04_Prepaid_Customer_B.png",
                        link: "",
                        subMenu: [
                            {
                                displayName: "Prepaid - Customers",
                                iconName: "All_Icons/04_Prepaid_Customer/04_Prepaid_Customer_B.png",
                                link: "/home/customer/list/Prepaid",
                                subMenu: [],
                                moduleName: `${PRE_CUST_CONSTANTS.PRE_CUSTS_LIST}`,
                                isVisible: false
                            },
                            {
                                displayName: "Prepaid - CAF",
                                iconName: "All_Icons/04_Prepaid_Customer/B_Prepaid CAF_B.png",
                                link: "/home/customer-caf/Prepaid",
                                subMenu: [],
                                moduleName: `${PRE_CUST_CONSTANTS.PRE_CUST_CAF_LIST}`,
                                isVisible: false
                            },
                            {
                                displayName: "Leased Line Customer",
                                iconName: "All_Icons/04_Prepaid_Customer/B_Prepaid CAF_B.png",
                                link: "/home/leased-line-customer",
                                subMenu: [],
                                moduleName: "PRE_CUST_CONSTANTS.PRE_CUST_LEASED_LINE_CUST",
                                isVisible: false
                            },
                            // {
                            //     displayName: "Customer Beta",
                            //     iconName: "All_Icons/04_Prepaid_Customer/B_Prepaid CAF_B.png",
                            //     link: "/home/customer-template",
                            //     subMenu: [],
                            //     moduleName: "PRE_CUST_CONSTANTS.CUST_BETA",
                            //     isVisible: false
                            // },
                            {
                                displayName: "Prepaid - Rejected Reasons",
                                iconName: "03_Rejected-Reason_B.png",
                                link: "/home/prepaid-rejected-reason-master",
                                subMenu: [],
                                moduleName: `${PRE_CUST_CONSTANTS.PRE_CUST_REJECTED_REASON_MASTER}`,
                                isVisible: false
                            }
                        ],
                        moduleName: `${SUBSCRIBER_MANAGEMENT.PREPAID}`,
                        isVisible: false
                    },
                    {
                        displayName: "Postpaid",
                        iconName: "All_Icons/05_Postpaid_Customer/05_Postpaid_Customer_B.png",
                        link: "",
                        subMenu: [
                            {
                                displayName: "Postpaid - Customers",
                                iconName: "All_Icons/05_Postpaid_Customer/05_Postpaid_Customer_B.png",
                                link: "/home/customer/list/Postpaid",
                                subMenu: [],
                                moduleName: `${POST_CUST_CONSTANTS.POST_CUST_LIST}`,
                                isVisible: false
                            },
                            {
                                displayName: "Postpaid - CAF",
                                iconName: "img/All_Icons/05_Postpaid_Customer/B_Postpaid CAF_B.png",
                                link: "/home/customer-caf/Postpaid",
                                subMenu: [],
                                moduleName: `${POST_CUST_CONSTANTS.POST_CUST_CAF}`,
                                isVisible: false
                            }
                        ],
                        moduleName: `${SUBSCRIBER_MANAGEMENT.POSTPAID}`,
                        isVisible: false
                    }
                ],
                moduleName: `${SUBSCRIBER_MANAGEMENT.SUBSCRIBER_MANAGEMENT}`,
                isVisible: false
            },

            {
                displayName: "Revenue Management",
                iconName: "Revenue_Management.svg",
                link: "",
                subMenu: [
                    {
                        displayName: "Billing & Invoicing",
                        iconName: "All_Icons/06_Invoice_System/06_Invoice_System_B.png",
                        link: "",
                        subMenu: [
                            {
                                displayName: "Bill Template",
                                iconName: "All_Icons/06_Invoice_System/A_Bill_Template_B.png",
                                link: "/home/billtemplate",
                                subMenu: [],
                                moduleName: `${INVOICE_SYSTEMS.BILL_TEMPLATE}`,
                                isVisible: false
                            },
                            {
                                displayName: "Quick Invoice",
                                iconName: "All_Icons/06_Invoice_System/A_Bill_Template_B.png",
                                link: "/home/quickInvoice",
                                subMenu: [],
                                moduleName: `${INVOICE_SYSTEMS.QUICK_INVOICE}`,
                                isVisible: false
                            },
                            {
                                displayName: "Postpaid Bill Runs",
                                iconName: "All_Icons/06_Invoice_System/B_Bill_Run_B.png",
                                link: "",
                                subMenu: [
                                    {
                                        displayName: "Generate Bill Runs",
                                        iconName: "All_Icons/06_Invoice_System/C_Generate_Bill_B.png",
                                        link: "/home/generateBillRun",
                                        subMenu: [],
                                        moduleName: `${INVOICE_SYSTEMS.POSTPAID_GENE_BILL_RUN}`,
                                        isVisible: false
                                    },
                                    {
                                        displayName: "Bill Run Master",
                                        iconName: "All_Icons/06_Invoice_System/D_Bill-Run-Master_B.png",
                                        link: "/home/billRunMaster",
                                        subMenu: [],
                                        moduleName: `${INVOICE_SYSTEMS.POSTPAID_BILL_RUN_MASTER}`,
                                        isVisible: false
                                    },
                                    {
                                        displayName: "Invoice Master",
                                        iconName: "All_Icons/06_Invoice_System/E_Invoice-Master_B.png",
                                        link: "/home/invoiceMaster",
                                        subMenu: [],
                                        moduleName: `${INVOICE_SYSTEMS.POSTPAID_INVOICE_MASTER}`,
                                        isVisible: false
                                    }
                                ],
                                moduleName: `${INVOICE_SYSTEMS.POSTPAID_BILL_RUNS}`,
                                isVisible: false
                            },
                            {
                                displayName: "Prepaid Bill Runs",
                                iconName: "All_Icons/06_Invoice_System/B_Bill_Run_B.png",
                                link: "",
                                subMenu: [
                                    {
                                        displayName: "Invoice Master",
                                        iconName: "All_Icons/06_Invoice_System/E_Invoice-Master_B.png",
                                        link: "/home/prepaid-invoiceMaster",
                                        subMenu: [],
                                        moduleName: `${INVOICE_SYSTEMS.PREPAID_INVOICE_MASTER}`,
                                        isVisible: false
                                    }
                                ],
                                moduleName: `${INVOICE_SYSTEMS.PREPAID_BILL_RUNS}`,
                                isVisible: false
                            },
                            {
                                displayName: "Revenue Reports",
                                iconName: "All_Icons/06_Invoice_System/A_Bill_Template_B.png",
                                link: "/home/dbr-problem",
                                subMenu: [],
                                moduleName: `${INVOICE_SYSTEMS.INVOICE_REVENUE_REPORT}`,
                                isVisible: false
                            },
                            {
                                displayName: "Partner Bill Runs",
                                iconName: "All_Icons/06_Invoice_System/A_Bill_Template_B.png",
                                link: "/home/partnerbilltemplate",
                                subMenu: [],
                                moduleName: `${INVOICE_SYSTEMS.BILL_TEMPLATE}`,
                                isVisible: false
                            }
                        ],
                        moduleName: `${INVOICE_SYSTEMS.BILLING_AND_INVOICING}`,
                        isVisible: false
                    },
                    {
                        displayName: "Payments & Finance",
                        iconName: "All_Icons/07_Payment_System/07_Payment_System_B.png",
                        link: "",
                        subMenu: [
                            {
                                displayName: "Payments - Record",
                                iconName: "All_Icons/07_Payment_System/A_-Record-Payment_B.png",
                                link: "/home/recordPayment",
                                subMenu: [],
                                moduleName: `${PAYMENT_SYSTEMS.RECORD_PAYMENT}`,
                                isVisible: false
                            },
                            {
                                displayName: "Payments - Search",
                                iconName: "All_Icons/07_Payment_System/B_Search Payment_B.png",
                                link: "/home/searchPayment",
                                subMenu: [],
                                moduleName: `${PAYMENT_SYSTEMS.SEARCH_PAYMENT}`,
                                isVisible: false
                            },
                            {
                                displayName: "Credit Notes - Generate",
                                iconName: "All_Icons/07_Payment_System/A_-Record-Payment_B.png",
                                link: "/home/invoice/recordPayment",
                                subMenu: [],
                                moduleName: `${CREDIT_NOTES.GENERATE_CREDIT_NOTE}`,
                                isVisible: false
                            },
                            {
                                displayName: "Credit Notes - Search",
                                iconName: "All_Icons/07_Payment_System/B_Search Payment_B.png",
                                link: "/home/invoice/search-payment",
                                subMenu: [],
                                moduleName: `${CREDIT_NOTES.SEARCH_CREDIT_NOTE}`,
                                isVisible: false
                            }
                        ],
                        moduleName: `${PAYMENT_SYSTEMS.PAYMENTS_AND_FINANCE}`,
                        isVisible: false
                    },
                    {
                        displayName: "Collections & Dunning",
                        iconName: "All_Icons/09_Dunning-Managenment/09_Dunning-Managenment_B.png",
                        link: "",
                        subMenu: [
                            {
                                displayName: "Dunning Rules",
                                iconName: "All_Icons/09_Dunning-Managenment/A_Dunning Rule_B.png",
                                link: "/home/dunningRules",
                                subMenu: [],
                                moduleName: `${DUNNINGS.DUNNING_RULES}`,
                                isVisible: false
                            }
                        ],
                        moduleName: `${DUNNINGS.COLLECTIONS_AND_DUNNING}`,
                        isVisible: false
                    }
                ],
                moduleName: `${REVENUE_MANAGEMENT.REVENUE_MANAGEMENT}`,
                isVisible: false
            },

            {
                displayName: "Support",
                iconName: "Support.svg",
                link: "",
                subMenu: [
                    {
                        displayName: "Support & Service Desk",
                        iconName: "All_Icons/08_Ticketing_System/08_Ticket_System_B.png",
                        link: "",
                        subMenu: [
                            {
                                displayName: "TAT Tracking - Tickets",
                                iconName: "All_Icons/08_Ticketing_System/A_Resolution Master_B.png",
                                link: "/home/ticketmaster",
                                subMenu: [],
                                moduleName: `${TICKETING_SYSTEMS.TAT_TICKET}`,
                                isVisible: false
                            },
                            {
                                displayName: "Problem Domains",
                                iconName: "All_Icons/2/14_Problem-Domain(Menu)_B.png",
                                link: "/home/ticket-reason-category",
                                subMenu: [],
                                moduleName: `${TICKETING_SYSTEMS.PROBLEM_DOMAIN}`,
                                isVisible: false
                            },
                            {
                                displayName: "Sub Problem Domains",
                                iconName: "All_Icons/2/15_Sub-Problem-Domain(menu)_B.png",
                                link: "/home/ticket-reason-sub-category",
                                subMenu: [],
                                moduleName: `${TICKETING_SYSTEMS.SUB_PB_DOMAIN}`,
                                isVisible: false
                            },
                            {
                                displayName: "Root Cause Master",
                                iconName: "All_Icons/08_Ticketing_System/A_Resolution Master_B.png",
                                link: "/home/resolutionMaster",
                                subMenu: [],
                                moduleName: `${TICKETING_SYSTEMS.ROOT_CAUSE_MASTER}`,
                                isVisible: false
                            },
                            {
                                displayName: "Ticket Management",
                                iconName: "All_Icons/08_Ticketing_System/C_Ticket_B.png",
                                link: "/home/ticketManagement",
                                subMenu: [],
                                moduleName: `${TICKETING_SYSTEMS.TICKET}`,
                                isVisible: false
                            },
                            {
                                displayName: "Open Opportunities",
                                iconName: "All_Icons/08_Ticketing_System/C_Ticket_B.png",
                                link: "/home/openOpportunity",
                                subMenu: [],
                                moduleName: `${TICKETING_SYSTEMS.TICKET_OPEN_OPPORTUNITY}`,
                                isVisible: false
                            }
                        ],
                        moduleName: `${TICKETING_SYSTEMS.SUPPORT_AND_SERVICE_DESK}`,
                        isVisible: false
                    },
                    {
                        displayName: "Task & Workforce",
                        iconName: "All_Icons/08_Ticketing_System/08_Ticket_System_B.png",
                        link: "",
                        subMenu: [
                            {
                                displayName: "TAT Tracking - Tasks",
                                iconName: "All_Icons/08_Ticketing_System/A_Resolution Master_B.png",
                                link: "/home/tat-for-task",
                                subMenu: [],
                                moduleName: `${TASK_SYSTEMS.TAT_TASK}`,
                                isVisible: false
                            },
                            {
                                displayName: "Task Categories",
                                iconName: "All_Icons/2/15_Sub-Problem-Domain(menu)_B.png",
                                link: "/home/task-category",
                                subMenu: [],
                                moduleName: `${TASK_SYSTEMS.TASK_CATEGORY_DOMAIN}`,
                                isVisible: false
                            },
                            {
                                displayName: "Task Subcategories",
                                iconName: "All_Icons/2/15_Sub-Problem-Domain(menu)_B.png",
                                link: "/home/task-sub-category",
                                subMenu: [],
                                moduleName: `${TASK_SYSTEMS.TASK_SUB_CATEGORY_DOMAIN}`,
                                isVisible: false
                            },
                            {
                                displayName: "Task Management",
                                iconName: "All_Icons/08_Ticketing_System/C_Ticket_B.png",
                                link: "/home/taskManagement",
                                subMenu: [],
                                moduleName: `${TASK_SYSTEMS.TASK_DOMAIN}`,
                                isVisible: false
                            },
                            {
                                displayName: "Root Cause Master",
                                iconName: "All_Icons/08_Ticketing_System/A_Resolution Master_B.png",
                                link: "/home/resolutiontaskMaster",
                                subMenu: [],
                                moduleName: `${TASK_SYSTEMS.ROOT_CAUSE_MASTER}`,
                                isVisible: false
                            },
                            // {
                            //     displayName: "Technician Diary",
                            //     iconName: "All_Icons/09_Dunning-Managenment/A_Dunning Rule_B.png",
                            //     link: "/home/technicianDiary",
                            //     subMenu: [],
                            //     moduleName: "TECHNICIAN_DIARY_SYSTEMS.TECHNNICIAN_DIARY",
                            //     isVisible: false
                            // }
                        ],
                        moduleName: `${TASK_SYSTEMS.TASK_AND_WORKFORCE}`,
                        isVisible: false
                    }
                ],
                moduleName: `${SUPPORT.SUPPORT}`,
                isVisible: false
            },
            {
                displayName: "Core Services",
                iconName: "Core_Services.svg",
                link: "",
                subMenu: [
                    {
                        displayName: "Product Catalog",
                        iconName: "All_Icons/02_Product_Management/02_Product_Management_B_.png",
                        link: "",
                        subMenu: [
                            {
                                displayName: "Service Setup",
                                iconName: "All_Icons/02_Product_Management/A_Service_B.png",
                                link: "/home/serviceManagement",
                                subMenu: [],
                                moduleName: `${PRODUCTS.SERVICE}`,
                                isVisible: false
                            },
                            {
                                displayName: "Pricing & Charges - Tax",
                                iconName: "All_Icons/02_Product_Management/B_Tax_B.png",
                                link: "/home/taxManagement",
                                subMenu: [],
                                moduleName: `${PRODUCTS.TAX}`,
                                isVisible: false
                            },
                            {
                                displayName: "Pricing & Charges - Charges",
                                iconName: "All_Icons/02_Product_Management/C_Charge_B.png",
                                link: "/home/chargeManagement",
                                subMenu: [],
                                moduleName: `${PRODUCTS.CHARGE}`,
                                isVisible: false
                            },
                            {
                                displayName: "QoS & Policy Rules",
                                iconName: "All_Icons/02_Product_Management/E_QoS Policy_B.png",
                                link: "/home/qosPolicyManagement",
                                subMenu: [],
                                moduleName: `${PRODUCTS.QOS_POLICY}`,
                                isVisible: false
                            },
                            {
                                displayName: "QoS & Policy Rules - Time Policies",
                                iconName: "All_Icons/2/16_Time-base-policy-(menu)_B.png",
                                link: "/home/timeBasePolicy",
                                subMenu: [],
                                moduleName: `${PRODUCTS.TIME_POLICY}`,
                                isVisible: false
                            },
                            {
                                displayName: "Plans & Bundles - Plans",
                                iconName: "All_Icons/02_Product_Management/F_Plan_B.png",
                                link: "/home/planManagement",
                                subMenu: [],
                                moduleName: `${PRODUCTS.PLAN}`,
                                isVisible: false
                            },
                            {
                                displayName: "Pricing & Charges - Discounts",
                                iconName: "All_Icons/02_Product_Management/D_Discount_B.png",
                                link: "/home/discountManagement",
                                subMenu: [],
                                moduleName: `${PRODUCTS.DISCOUNT}`,
                                isVisible: false
                            },
                            {
                                displayName: "Plans & Bundles - Plan Bundle",
                                iconName: "All_Icons/03_Partner_Management/B_Plan Bundle_B.png",
                                link: "/home/planGroup",
                                subMenu: [],
                                moduleName: `${PRODUCTS.PLAN_GROUP}`,
                                isVisible: false
                            },
                            // {
                            //     displayName: "Plans & Bundles - Special Plan Mapping",
                            //     iconName: "All_Icons/02_Product_Management/F_Plan_B.png",
                            //     link: "/home/planMapping",
                            //     subMenu: [],
                            //     moduleName: "PRODUCTS.SPECIAL_PLAN_MAPPING",
                            //     isVisible: false
                            // },
                            {
                                displayName: "Voucher Management",
                                iconName: "All_Icons/02_Product_Management/E_Voucher Management_B.png",
                                link: "/home/voucherManagement",
                                subMenu: [],
                                moduleName: `${PRODUCTS.VOUCHER_MANAGEMENT}`,
                                isVisible: false
                            }
                        ],
                        moduleName: `${CORE_SERVICE.PRODUCT_CATALOG}`,
                        isVisible: false
                    },
                    // {
                    //     displayName: "Policy",
                    //     iconName: "All_Icons/02_Product_Management/02_Product_Management_B_.png",
                    //     link: "",
                    //     subMenu: [],
                    //     moduleName: "PRODUCTS.PRODUCT",
                    //     isVisible: false
                    // },
                    {
                        displayName: "Integrations",
                        iconName: "All_Icons/06_Invoice_System/06_Invoice_System_B.png",
                        link: "",
                        subMenu: [
                            {
                                displayName: "Integration Configuration",
                                iconName: "All_Icons/13_Templat_ Management/13_Template Management_B.png",
                                link: "/home/integration-configuration",
                                subMenu: [],
                                moduleName: `${INTEGRATION_SYSTEMS.INTEGRATION_SYSTEM}`,
                                isVisible: false
                            },
                            {
                                displayName: "Integration Audit",
                                iconName: "Audit _Compliance.svg",
                                link: "/home/integration-audit",
                                subMenu: [],
                                moduleName: `${SETTINGS.PAYMENT_GATEWAY_CONFIGURATION}`,
                                isVisible: false
                            },
                            {
                                displayName: "Third Party Menu",
                                iconName: "All_Icons/13_Templat_ Management/13_Template Management_B.png",
                                link: "/home/third-party-integration-menu",
                                subMenu: [],
                                moduleName: `${INTEGRATION_SYSTEMS.INTEGRATION_SYSTEM}`,
                                isVisible: false
                            },
                            {
                                displayName: "KRA-integration",
                                iconName: "All_Icons/13_Templat_ Management/13_Template Management_B.png",
                                link: "/home/KRA-integration",
                                subMenu: [],
                                moduleName: `${INTEGRATION_SYSTEMS.INTEGRATION_SYSTEM}`,
                                isVisible: false
                            }
                        ],
                        moduleName: `${CORE_SERVICE.INTEGRATIONS}`,
                        isVisible: false
                    },
                    {
                        displayName: "OCS",
                        iconName: "All_Icons/11_Network_Management/11_Network-Management_B.png",
                        link: "",
                        subMenu: [
                            {
                                displayName: "Network Device",
                                iconName: "All_Icons/11_Network_Management/A_NetworkS_B.png",
                                link: "/home/networkDevice",
                                subMenu: [],
                                moduleName: `${NETWORKS.NETWORK_DEVICE}`,
                                isVisible: false
                            },
                            {
                                displayName: "Network Map",
                                iconName: "All_Icons/14_Setting/F_MVNO_B.png",
                                link: "/home/maps",
                                subMenu: [],
                                moduleName: `${NETWORKS.NETWORK_MAP}`,
                                isVisible: false
                            },
                            {
                                displayName: "IP Management",
                                iconName: "All_Icons/14_Setting/F_MVNO_B.png",
                                link: "/home/ipManagement",
                                subMenu: [],
                                moduleName: `${NETWORKS.IP}`,
                                isVisible: false
                            }
                        ],
                        moduleName: `${CORE_SERVICE.OCS}`,
                        isVisible: false
                    }
                ],
                moduleName: `${CORE_SERVICE.CORE_SERVICE}`,
                isVisible: false
            },
            {
                displayName: "Inventory & Assets",
                iconName: "Inventory_Assets.svg",
                link: "",
                subMenu: [
                    {
                        displayName: "Manufacturer Management",
                        iconName: "All_Icons/17_Inventory_Management/03_Vendor_Management_B.png",
                        link: "/home/vendorManagement",
                        subMenu: [],
                        moduleName: `${INVENTORYS.MANUFACTURER}`,
                        isVisible: false
                    },
                    {
                        displayName: "Product Category Management",
                        iconName: "All_Icons/17_Inventory_Management/06_Product-Category_B.png",
                        link: "/home/productCategory",
                        subMenu: [],
                        moduleName: `${INVENTORYS.PRODUCT_CATEGORY}`,
                        isVisible: false
                    },
                    {
                        displayName: "Product Management",
                        iconName: "All_Icons/17_Inventory_Management/B_Product Management_B.png",
                        link: "/home/product",
                        subMenu: [],
                        moduleName: `${INVENTORYS.INVEN_PRODUCT}`,
                        isVisible: false
                    },
                    {
                        displayName: "Pop Management",
                        iconName: "All_Icons/17_Inventory_Management/07_Pop-management_B.png",
                        link: "/home/popManagement",
                        subMenu: [],
                        moduleName: `${INVENTORYS.POP}`,
                        isVisible: false
                    },
                    {
                        displayName: "Warehouse Management",
                        iconName: "All_Icons/17_Inventory_Management/C_Warehouse Management_B.png",
                        link: "/home/warehouseManagement",
                        subMenu: [],
                        moduleName: `${INVENTORYS.WAREHOUSE}`,
                        isVisible: false
                    },
                    {
                        displayName: "Inwards",
                        iconName: "All_Icons/17_Inventory_Management/E_Inwards_B.png",
                        link: "/home/inwards",
                        subMenu: [],
                        moduleName: `${INVENTORYS.INVEN_INWARDS}`,
                        isVisible: false
                    },
                    {
                        displayName: "Outwards",
                        iconName: "All_Icons/17_Inventory_Management/D_Outwards_B.png",
                        link: "/home/outwards",
                        subMenu: [],
                        moduleName: `${INVENTORYS.INVEN_OUTWARDS}`,
                        isVisible: false
                    },
                    {
                        displayName: "External Item Management",
                        iconName: "All_Icons/17_Inventory_Management/05_External-Item-Group_B.png",
                        link: "/home/externalItemManagement",
                        subMenu: [],
                        moduleName: `${INVENTORYS.EXT_ITEM}`,
                        isVisible: false
                    },
                    {
                        displayName: "Bulk Consumption Management",
                        iconName: "All_Icons/17_Inventory_Management/04_Bulk-Consumption-Screen_B.png",
                        link: "/home/bulkConsumption",
                        subMenu: [],
                        moduleName: `${INVENTORYS.EXT_ITEM_BULK_CONSUMPTION}`,
                        isVisible: false
                    },
                    {
                        displayName: "Inventory Request",
                        iconName: "All_Icons/17_Inventory_Management/02_Inventory-Request_B.png",
                        link: "/home/inventory-request",
                        subMenu: [],
                        moduleName: `${INVENTORYS.INVEN_REQUEST}`,
                        isVisible: false
                    },
                    {
                        displayName: "Inventory Details",
                        iconName: "All_Icons/17_Inventory_Management/07_Pop-management_B.png",
                        link: "/home/assignedInventory",
                        subMenu: [],
                        moduleName: `${INVENTORYS.INVEN_DETAILS}`,
                        isVisible: false
                    }
                ],
                moduleName: `${INVENTORY_AND_ASSETS.INVENTORY_AND_ASSETS}`,
                isVisible: false
            },
            {
                displayName: "Audit & Compliance",
                iconName: "Audit _Compliance.svg",
                link: "",
                subMenu: [
                    {
                        displayName: "Audit Log",
                        iconName: "All_Icons/15_Audit/A_Audit Log_B.png",
                        link: "/home/auditLog",
                        subMenu: [],
                        moduleName: `${AUDITS.AUDIT_LOG}`,
                        isVisible: false
                    },
                    {
                        displayName: "Reported Problem",
                        iconName: "All_Icons/15_Audit/A_Audit Log_B.png",
                        link: "/home/reported-problem",
                        subMenu: [],
                        moduleName: `${AUDITS.REPORTED_PROBLEM}`,
                        isVisible: false
                    }
                ],
                moduleName: `${AUDIT_AND_COMPLIANCE.AUDIT_AND_COMPLIANCE}`,
                isVisible: false
            },
            {
                displayName: "Administration",
                iconName: "Administration.svg",
                link: "",
                subMenu: [
                    {
                        displayName: "Reference Data",
                        iconName: "All_Icons/01_Master_Management/01_Master_Management.png",
                        link: "",
                        subMenu: [
                            {
                                displayName: `${COUNTRY}`,
                                iconName: "All_Icons/01_Master_Management/A_Country_B.png",
                                link: "/home/countryManagement",
                                subMenu: [],
                                moduleName: `${MASTERS.COUNTRY}`,
                                isVisible: false
                            },
                            {
                                displayName: `${STATE}`,
                                iconName: "All_Icons/01_Master_Management/B_State_B.png",
                                link: "/home/stateManagement",
                                subMenu: [],
                                moduleName: `${MASTERS.STATE}`,
                                isVisible: false
                            },
                            {
                                displayName: `${CITY}`,
                                iconName: "All_Icons/01_Master_Management/C_City_B.png",
                                link: "/home/cityManagement",
                                subMenu: [],
                                moduleName: `${MASTERS.CITY}`,
                                isVisible: false
                            },
                            {
                                displayName: `${PINCODE}`,
                                iconName: "All_Icons/01_Master_Management/D__Pincode_B.png",
                                link: "/home/pincodeManagement",
                                subMenu: [],
                                moduleName: `${MASTERS.PINCODE}`,
                                isVisible: false
                            },
                            {
                                displayName: `${AREA}`,
                                iconName: "All_Icons/01_Master_Management/E_Area_B.png",
                                link: "/home/areaManagement",
                                subMenu: [],
                                moduleName: `${MASTERS.AREA}`,
                                isVisible: false
                            },
                            {
                                displayName: `${SUBAREA}`,
                                iconName: "All_Icons/01_Master_Management/A_Country_B.png",
                                link: "/home/subareaManagement",
                                subMenu: [],
                                moduleName: `${MASTERS.SUBAREA}`,
                                isVisible: false
                            },
                            {
                                displayName: "Building Config",
                                iconName: "All_Icons/01_Master_Management/A_Country_B.png",
                                link: "/home/buildingConfigManagement",
                                subMenu: [],
                                moduleName: `${MASTERS.BUILDING_CONFIG}`,
                                isVisible: false
                            },
                            {
                                displayName: `${BUILDING}`,
                                iconName: "All_Icons/01_Master_Management/A_Country_B.png",
                                link: "/home/buildingManagement",
                                subMenu: [],
                                moduleName: `${MASTERS.BUILDINGMGMT}`,
                                isVisible: false
                            },
                            {
                                displayName: "Service Area",
                                iconName: "All_Icons/01_Master_Management/F_Service_Area_B.png",
                                link: "/home/serviceArea",
                                subMenu: [],
                                moduleName: `${MASTERS.SERVICE_AREA}`,
                                isVisible: false
                            },
                            {
                                displayName: "Financial Entities - Investment Code",
                                iconName: "All_Icons/2/11_Business-Unit(Menu)_B.png",
                                link: "/home/investmentCode",
                                subMenu: [],
                                moduleName: `${MASTERS.INVESTMENT_CODE}`,
                                isVisible: false
                            },
                            {
                                displayName: "Business Structure - Business Unit",
                                iconName: "All_Icons/2/11_Business-Unit(Menu)_B.png",
                                link: "/home/businessunit",
                                subMenu: [],
                                moduleName: `${MASTERS.BUSINESS_UNIT}`,
                                isVisible: false
                            },
                            {
                                displayName: "Business Structure - Sub Business Unit",
                                iconName: "All_Icons/2/11_Business-Unit(Menu)_B.png",
                                link: "/home/subbusinessunit",
                                subMenu: [],
                                moduleName: `${MASTERS.SUB_BUSINESS_UNIT}`,
                                isVisible: false
                            },
                            {
                                displayName: "Financial Entities - Bank",
                                iconName: "All_Icons/2/12_Bank-Management_B.png",
                                link: "/home/bankManagement",
                                subMenu: [],
                                moduleName: `${MASTERS.BANK}`,
                                isVisible: false
                            },
                            {
                                displayName: "Business Structure - Branch",
                                iconName: "All_Icons/2/13_Branch-Management-(Menu)_B.png",
                                link: "/home/branch-management",
                                subMenu: [],
                                moduleName: `${MASTERS.BRANCH}`,
                                isVisible: false
                            },
                            {
                                displayName: "Business Structure - Region",
                                iconName: "All_Icons/2/13_Branch-Management-(Menu)_B.png",
                                link: "/home/region",
                                subMenu: [],
                                moduleName: `${MASTERS.REGION}`,
                                isVisible: false
                            },
                            {
                                displayName: "Business Structure - Business Vertical",
                                iconName: "All_Icons/2/13_Branch-Management-(Menu)_B.png",
                                link: "/home/businessVertical",
                                subMenu: [],
                                moduleName: `${MASTERS.BUSINESS_VERTICALS}`,
                                isVisible: false
                            },
                            {
                                displayName: "Business Structure - Sub Business Vertical",
                                iconName: "All_Icons/2/13_Branch-Management-(Menu)_B.png",
                                link: "/home/subbusinessVertical",
                                subMenu: [],
                                moduleName: `${MASTERS.SUB_BUSINESS_VERTICAL}`,
                                isVisible: false
                            },
                            {
                                displayName: "Department",
                                iconName: "All_Icons/2/13_Branch-Management-(Menu)_B.png",
                                link: "/home/departmentManagement",
                                subMenu: [],
                                moduleName: `${MASTERS.DEPARTMENT}`,
                                isVisible: false
                            },
                            // {
                            //     displayName: "Reference Data - Location Master",
                            //     iconName: "All_Icons/02_Product_Management/E_Voucher Management_B.png",
                            //     link: "/home/location",
                            //     subMenu: [],
                            //     moduleName: "PRODUCTS.LOCATION_MASTER",
                            //     isVisible: false
                            // }
                        ],
                        moduleName: `${ADMINISTRATION.REFERENCE_DATA}`,
                        isVisible: false
                    },
                    {
                        displayName: "Workflow & Automation",
                        iconName: "All_Icons/14_Setting/14_Setting_B.png",
                        link: "",
                        subMenu: [
                            {
                                displayName: "Teams Management",
                                iconName: "All_Icons/14_Setting/A_Teams_B.png",
                                link: "/home/teams",
                                subMenu: [],
                                moduleName: `${WORKFLOWS.TEAMS}`,
                                isVisible: false
                            },
                            {
                                displayName: "TAT Metrics Management",
                                iconName: "All_Icons/14_Setting/B_Team Hierarchy_B.png",
                                link: "/home/TAT_Matrics",
                                subMenu: [],
                                moduleName: `${WORKFLOWS.TAT_METRICS}`,
                                isVisible: false
                            },
                            {
                                displayName: "Workflow Management",
                                iconName: "All_Icons/14_Setting/B_Team Hierarchy_B.png",
                                link: "/home/team-hierarchy",
                                subMenu: [],
                                moduleName: `${WORKFLOWS.WORKFLOW_LIST}`,
                                isVisible: false
                            }
                        ],
                        moduleName: `${ADMINISTRATION.WORKFLOW_AND_AUTOMATION}`,
                        isVisible: false
                    },
                    {
                        displayName: "Notifications & Communication",
                        iconName: "All_Icons/12_Notifications/12_Notifications_B.png",
                        link: "",
                        subMenu: [
                            {
                                displayName: "Email Configuration",
                                iconName: "All_Icons/12_Notifications/A_Email Configuration_B.png",
                                link: "/home/emailConfig",
                                subMenu: [],
                                moduleName: `${NOTIFICATIONS.EMAIL_CONFIG}`,
                                isVisible: false
                            },
                            {
                                displayName: "SMS Configuration",
                                iconName: "All_Icons/12_Notifications/B_SMS-Configuration_B.png",
                                link: "/home/smsConfig",
                                subMenu: [],
                                moduleName: `${NOTIFICATIONS.SMS_CONFIG}`,
                                isVisible: false
                            },
                            {
                                displayName: "Email",
                                iconName: "All_Icons/12_Notifications/C_Email_B.png",
                                link: "/home/emailNotification",
                                subMenu: [],
                                moduleName: `${NOTIFICATIONS.NOTIFICATION_EMAIL}`,
                                isVisible: false
                            },
                            {
                                displayName: "SMS",
                                iconName: "All_Icons/12_Notifications/D_SMS_B.png",
                                link: "/home/smsNotification",
                                subMenu: [],
                                moduleName: `${NOTIFICATIONS.NOTIFICATION_SMS}`,
                                isVisible: false
                            },
                            {
                                displayName: "OTP",
                                iconName: "All_Icons/12_Notifications/D_SMS_B.png",
                                link: "/home/otp",
                                subMenu: [],
                                moduleName: `${NOTIFICATIONS.NOTIFICATION_OTP}`,
                                isVisible: false
                            }
                        ],
                        moduleName: `${ADMINISTRATION.NOTIFICATIONS_AND_COMMUNICATION}`,
                        isVisible: false
                    }
                ],
                moduleName: `${ADMINISTRATION.ADMINISTRATION}`,
                isVisible: false
            },
            // {
            //     displayName: "DTV",
            //     iconName: "DTV.svg",
            //     link: "",
            //     subMenu: [
            //         {
            //             displayName: "CAS Management",
            //             iconName: "All_Icons/03_Partner_Management/B_Plan Bundle_B.png",
            //             link: "/home/casmanagement",
            //             subMenu: [],
            //             moduleName: "DTVS.CAS_MGMT",
            //             isVisible: false
            //         },
            //         {
            //             displayName: "Sector Management",
            //             iconName: "All_Icons/03_Partner_Management/A_Partner_B.png",
            //             link: "/home/sector",
            //             subMenu: [],
            //             moduleName: "DTVS.SECTOR_MGMT",
            //             isVisible: false
            //         }
            //     ],
            //     moduleName: "DTVS.DTV",
            //     isVisible: false
            // },
            {
                displayName: "Radius Management",
                iconName: "Radius_Management.svg",
                link: "",
                subMenu: [
                    {
                        displayName: `Radius Group`,
                        iconName: "All_Icons/02_AAA/A_Radius Group_B.png",
                        link: "/home/radiusgroup",
                        subMenu: [],
                        moduleName: `${RADIUS_CONSTANTS.RADIUS_GROUP}`,
                        isVisible: false
                    },
                    {
                        displayName: `Radius Client`,
                        iconName: "All_Icons/02_AAA/B_Radius Customer_B.png",
                        link: "/home/radiusclient/list",
                        subMenu: [],
                        moduleName: `${RADIUS_CONSTANTS.RADIUS_CLIENT}`,
                        isVisible: false
                    },
                    {
                        displayName: `Radius Customer`,
                        iconName: "All_Icons/02_AAA/B_Radius Customer_B.png",
                        link: "/home/radiuscustomer/list",
                        subMenu: [],
                        moduleName: `${RADIUS_CONSTANTS.RADIUS_CUST}`,
                        isVisible: false
                    },
                    {
                        displayName: `CDRs`,
                        iconName: "img/All_Icons/02_AAA/D_CDRs_B.png",
                        link: "/home/acctCdr",
                        subMenu: [],
                        moduleName: `${RADIUS_CONSTANTS.RADIUS_CDR}`,
                        isVisible: false
                    },
                    {
                        displayName: `Live Users`,
                        iconName: "All_Icons/02_AAA/E_Live Users_B.png",
                        link: "/home/liveuser",
                        subMenu: [],
                        moduleName: `${RADIUS_CONSTANTS.RADIUS_LIVE_USERS}`,
                        isVisible: false
                    },
                    {
                        displayName: `Proxy Configuration`,
                        iconName: "All_Icons/02_AAA/F_Proxy Configuration_B.png",
                        link: "/home/proxy-server",
                        subMenu: [],
                        moduleName: `${RADIUS_CONSTANTS.RADIUS_PROXY_CONFIG}`,
                        isVisible: false
                    },
                    {
                        displayName: `Dictionary`,
                        iconName: "All_Icons/02_AAA/G_Dictionary_B.png",
                        link: "/home/dictionary",
                        subMenu: [],
                        moduleName: `${RADIUS_CONSTANTS.RADIUS_DICT}`,
                        isVisible: false
                    },
                    {
                        displayName: `Authentication Audit`,
                        iconName: "All_Icons/02_AAA/H_Authentication Audit_B.png",
                        link: "/home/authResponse",
                        subMenu: [],
                        moduleName: `${RADIUS_CONSTANTS.RADIUS_AUTHEN_AUDIT}`,
                        isVisible: false
                    },
                    {
                        displayName: `CoA/DM Profile`,
                        iconName: "All_Icons/02_AAA/I_CoADM Profile_B.png",
                        link: "/home/coadm",
                        subMenu: [],
                        moduleName: `${RADIUS_CONSTANTS.RADIUS_COA_DM}`,
                        isVisible: false
                    },
                    {
                        displayName: `Radius Profile `,
                        iconName: "All_Icons/02_AAA/J_Radius Profile_B.png",
                        link: "/home/radiusProfile/list",
                        subMenu: [],
                        moduleName: `${RADIUS_CONSTANTS.RADIUS_PROFILES}`,
                        isVisible: false
                    },
                    {
                        displayName: `Device Management`,
                        iconName: "All_Icons/02_AAA/K_Device Management_B.png",
                        link: "/home/device",
                        subMenu: [],
                        moduleName: `${RADIUS_CONSTANTS.RADIUS_DEVICE}`,
                        isVisible: false
                    },
                    {
                        displayName: `DB Mapping Master`,
                        iconName: "All_Icons/02_AAA/L_DB Mapping Master_B.png",
                        link: "/home/dbMappingMaster",
                        subMenu: [],
                        moduleName: `${RADIUS_CONSTANTS.RADIUS_DB_MAPPING}`,
                        isVisible: false
                    },
                    {
                        displayName: `Driver Management`,
                        iconName: "All_Icons/02_AAA/L_DB Mapping Master_B.png",
                        link: "/home/driverManagement",
                        subMenu: [],
                        moduleName: `${RADIUS_CONSTANTS.RADIUS_DRIVER_MANAGEMENT}`,
                        isVisible: false
                    },
                    {
                        displayName: `Access Response`,
                        iconName: "All_Icons/02_AAA/L_DB Mapping Master_B.png",
                        link: "/home/access-response",
                        subMenu: [],
                        moduleName: `${RADIUS_CONSTANTS.RADIUS_ACCESS_RESPONSE}`,
                        isVisible: false
                    },
                    {
                        displayName: `VLAN Management`,
                        iconName: "All_Icons/02_AAA/H_Authentication Audit_B.png",
                        link: "/home/vlanManagement/list",
                        subMenu: [],
                        moduleName: `${RADIUS_CONSTANTS.RADIUS_VLAN_MANAGMENT}`,
                        isVisible: false
                    },
                    {
                        displayName: `IP Management`,
                        iconName: "All_Icons/02_AAA/H_Authentication Audit_B.png",
                        link: "/home/radiusipManagement",
                        subMenu: [],
                        moduleName: `${RADIUS_CONSTANTS.RADIUS_IP_MANAGEMENT}`,
                        isVisible: false
                    },
                    {
                        displayName: `Faulty Mac Management`,
                        iconName: "All_Icons/02_AAA/H_Authentication Audit_B.png",
                        link: "/home/faulty-mac",
                        subMenu: [],
                        moduleName: `${RADIUS_CONSTANTS.RADIUS_FAULTY_MAC}`,
                        isVisible: false
                    }
                ],
                moduleName: `${RADIUS_MANAGEMENT.RADIUS_MANAGEMENT}`,
                isVisible: false
            },

            {
                displayName: "Login / Access",
                iconName: "Login.svg",
                link: "",
                subMenu: [
                    {
                        displayName: "Role Management",
                        iconName: "All_Icons/14_Setting/C_Role_B.png",
                        link: "/home/radiusrole",
                        subMenu: [],
                        moduleName: `${SETTINGS.ROLE}`,
                        isVisible: false
                    },
                    {
                        displayName: "Staff Management",
                        iconName: "All_Icons/14_Setting/D_ Staff_B.png",
                        link: "/home/radiusstaff",
                        subMenu: [],
                        moduleName: `${SETTINGS.STAFF}`,
                        isVisible: false
                    },
                    // {
                    //     displayName: "My Profile",
                    //     iconName: "All_Icons/14_Setting/D_ Staff_B.png",
                    //     link: "/home/userProfile",
                    //     subMenu: [],
                    //     moduleName: `${SETTINGS.MY_PROFILE}`,
                    //     isVisible: false
                    // },
                    {
                        displayName: "Profile Management",
                        iconName: "All_Icons/14_Setting/D_ Staff_B.png",
                        link: "/home/profile",
                        subMenu: [],
                        moduleName: `${SETTINGS.PROFILE_MANAGEMENT}`,
                        isVisible: false
                    },
                    {
                        displayName: "Organization Profile",
                        iconName: "All_Icons/14_Setting/B_Team Hierarchy_B.png",
                        link: "/home/myOrganizationCustomer",
                        subMenu: [],
                        moduleName: `${SETTINGS.ORGANIZATION}`,
                        isVisible: false
                    },
                    {
                        displayName: "System Configuration",
                        iconName: "All_Icons/14_Setting/E_System_B.png",
                        link: "/home/SystemConfig",
                        subMenu: [],
                        moduleName: `${SETTINGS.SYSTEM_CONFIGURATION}`,
                        isVisible: false
                    },
                    {
                        displayName: "Template Management",
                        iconName: "All_Icons/13_Templat_ Management/13_Template Management_B.png",
                        link: "/home/Template",
                        subMenu: [],
                        moduleName: `${SETTINGS.TEMPLATE}`,
                        isVisible: false
                    },
                    {
                        displayName: "Field Template Mapping",
                        iconName: "All_Icons/13_Templat_ Management/13_Template Management_B.png",
                        link: "/home/field-temp-mapping",
                        subMenu: [],
                        moduleName: `${SETTINGS.FIELD_TEMPLATE_MAPPING}`,
                        isVisible: false
                    },
                    {
                        displayName: "Payment Gateway Configuration",
                        iconName: "All_Icons/13_Templat_ Management/13_Template Management_B.png",
                        link: "/home/payment-gateway-configuration",
                        subMenu: [],
                        moduleName: `${SETTINGS.PAYMENT_GATEWAY_CONFIGURATION}`,
                        isVisible: false
                    },
                    {
                        displayName: "Migration",
                        iconName: "All_Icons/13_Templat_ Management/13_Template Management_B.png",
                        link: "/home/migration",
                        subMenu: [],
                        moduleName: `${SETTINGS.MIGRATION}`,
                        isVisible: false
                    },
                    {
                        displayName: "Feedback Config",
                        iconName: "All_Icons/13_Templat_ Management/13_Template Management_B.png",
                        link: "/home/feedback",
                        subMenu: [],
                        moduleName: "",
                        isVisible: false
                    },
                    {
                        displayName: "Knowledge Base",
                        iconName: "All_Icons/13_Templat_ Management/13_Template Management_B.png",
                        link: "/home/knowledgebase",
                        subMenu: [],
                        moduleName: `${SETTINGS.KNOWLEDGE_BASE}`,
                        isVisible: false
                    },
                    {
                        displayName: "Schedular Management",
                        iconName: "All_Icons/13_Templat_ Management/13_Template Management_B.png",
                        link: "/home/schedular-management",
                        subMenu: [],
                        moduleName: `${SETTINGS.KNOWLEDGE_BASE}`,
                        isVisible: false
                    },
                    {
                        displayName: "MVNO Management",
                        iconName: "All_Icons/14_Setting/F_MVNO_B.png",
                        link: "/home/mvnoManagement/list",
                        subMenu: [],
                        moduleName: `${SETTINGS.ISP_MANAGEMENT}`,
                        isVisible: false
                    },
                    {
                        displayName: "Password Policy",
                        iconName: "aperture",
                        link: "/home/password-policy/list",
                        subMenu: [],
                        moduleName: "",
                        isVisible: false
                    }
                ],
                moduleName: `${LOGIN_ACCESS.LOGIN_ACCESS}`,
                isVisible: false
            },

            {
                displayName: "Diameter",
                iconName: "Login.svg",
                link: "",
                subMenu: [
                    {
                        displayName: "Vendor",
                        iconName: "All_Icons/14_Setting/D_ Staff_B.png",
                        link: "/home/diameter-vendor",
                        subMenu: [],
                        moduleName: `${DAIMETER.VENDOR}`,
                        isVisible: false
                    },
                    {
                        displayName: "Packet Managemt",
                        iconName: "All_Icons/14_Setting/D_ Staff_B.png",
                        link: "/home/diameter-packet",
                        subMenu: [],
                        moduleName: `${DAIMETER.PACKET_MANAGEMENT}`,
                        isVisible: false
                    },
                    {
                        displayName: "Attribute",
                        iconName: "All_Icons/14_Setting/B_Team Hierarchy_B.png",
                        link: "/home/diameter-attribute",
                        subMenu: [],
                        moduleName: `${DAIMETER.ATTRIBUTE}`,
                        isVisible: false
                    },
                    {
                        displayName: "Policy",
                        iconName: "All_Icons/14_Setting/C_Role_B.png",
                        link: "/home/diameter-policy",
                        subMenu: [],
                        moduleName: `${DAIMETER.POLICY}`,
                        isVisible: false
                    },
                    {
                        displayName: "Peer Configuration",
                        iconName: "All_Icons/14_Setting/E_System_B.png",
                        link: "/home/diameter-peerConfig",
                        subMenu: [],
                        moduleName: `${DAIMETER.PEER_CONFIG}`,
                        isVisible: false
                    },
                ],
                moduleName: `${DAIMETER_ACCESS.DAIMETER_ACCESS}`,
                isVisible: false
            }
        ];
        return menuList;
    };








}









