import { Component, Output, EventEmitter, Input, ViewEncapsulation } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { navItems } from "../sidebar/sidebar-data";
// import { TranslateService } from "@ngx-translate/core";
// import { TablerIconsModule } from 'angular-tabler-icons';
import { MaterialModule } from "src/app/material.module";
import { ActivatedRoute, Data, NavigationEnd, Router, RouterModule } from "@angular/router";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { NgScrollbarModule } from "ngx-scrollbar";
import { CoreService } from "src/app/app-setting/core.service";
import { AppSettings } from "src/app/app-setting/config";
import { LoginService } from "src/app/service/login.service";
import { StaffService } from "src/app/service/staff.service";
import { savanaStusbar } from "src/assets/svgCode/savana-statusbar";
import { NavService } from "src/app/sidebar/services/nav.service";
import { filter, map, mergeMap } from "rxjs/operators";
import { Title } from "@angular/platform-browser";




interface notifications {
    id: number;
    img: string;
    title: string;
    subtitle: string;
}

interface profiledd {
    id: number;
    img: string;
    title: string;
    subtitle: string;
    link: string;
}

interface apps {
    id: number;
    img: string;
    title: string;
    subtitle: string;
    link: string;
}

interface quicklinks {
    id: number;
    title: string;
    link: string;
}

@Component({
    selector: "app-header",
    templateUrl: "./header.component.html",
    styleUrls: ['./header.component.css'],
    standalone: false
})
export class HeaderComponent {
    public userName: string = '';
    public userEmail: string = '';
    @Input() showToggle = true;
    @Input() toggleChecked = false;
    @Input() isMini = false;
    savanaStusbar
    @Output() toggleMobileNav = new EventEmitter<void>();
    @Output() toggleMobileFilterNav = new EventEmitter<void>();
    @Output() toggleCollapsed = new EventEmitter<void>();

    showFiller = false;

    public selectedLanguage: any = {
        language: "English",
        code: "en",
        type: "US",
        icon: "/assets/images/flag/icon-flag-en.svg"
    };

    public languages: any[] = [
        {
            language: "English",
            code: "en",
            type: "US",
            icon: "/assets/images/flag/icon-flag-en.svg"
        },
        {
            language: "Español",
            code: "es",
            icon: "/assets/images/flag/icon-flag-es.svg"
        },
        {
            language: "Français",
            code: "fr",
            icon: "/assets/images/flag/icon-flag-fr.svg"
        },
        {
            language: "German",
            code: "de",
            icon: "/assets/images/flag/icon-flag-de.svg"
        }
    ];
    userId;
    userEmailId = "";
    @Output() optionsChange = new EventEmitter<AppSettings>();
    pageInfo: Data | any = Object.create(null);


    constructor(
        private settings: CoreService,
        private vsidenav: CoreService,
        public dialog: MatDialog,
        private loginService: LoginService,
        private staffService: StaffService,
        public navService: NavService,
        private router: Router,
        private activatedRoute: ActivatedRoute,
        private titleService: Title,
        private CoreService: CoreService

    ) {
        this.router.events
            .pipe(filter((event) => event instanceof NavigationEnd))
            .pipe(map(() => this.activatedRoute))
            .pipe(
                map((route) => {
                    while (route.firstChild) {
                        route = route.firstChild;
                    }
                    return route;
                })
            )
            .pipe(filter((route) => route.outlet === 'primary'))
            .pipe(mergeMap((route) => route.data))
            // tslint:disable-next-line - Disables all
            .subscribe((event) => {
                // tslint:disable-next-line - Disables all
                this.titleService.setTitle('Savbill BSS');
                this.pageInfo = event;
            });
        // translate.setDefaultLang("en");
    }

    options = this.settings.getOptions();

    ngOnInit(): void {
        this.userId = localStorage.getItem("userId");
        this.staffService.getStaffUserData(this.userId).subscribe((response: any) => {
            this.userName = response?.Staff.username || "User";
            this.userEmailId = response?.Staff?.email || "Email";
        });
        // ********************
        this.CoreService.themeChanged.subscribe(() => {
            setTimeout(() => this.updateSvgColor(), 0);
        });
        this.updateSvgColor();
    }
    openDialog() { }

    private emitOptions() {
        this.optionsChange.emit(this.options);
    }

    setlightDark(theme: string) {
        this.options.theme = theme;
        this.emitOptions();
    }

    changeLanguage(lang: any): void {
        // this.translate.use(lang.code);
        this.selectedLanguage = lang;
    }

    notifications: notifications[] = [
        {
            id: 1,
            img: "/assets/images/profile/user-1.jpg",
            title: "Roman Joined thes Team!",
            subtitle: "Congratulate him"
        },
        {
            id: 2,
            img: "/assets/images/profile/user-2.jpg",
            title: "New message received",
            subtitle: "Salma sent you new message"
        },
        {
            id: 3,
            img: "/assets/images/profile/user-3.jpg",
            title: "New Payment received",
            subtitle: "Check your earnings"
        },
        {
            id: 4,
            img: "/assets/images/profile/user-4.jpg",
            title: "Jolly completed tasks",
            subtitle: "Assign her new tasks"
        },
        {
            id: 5,
            img: "/assets/images/profile/user-5.jpg",
            title: "Roman Joined the Team!",
            subtitle: "Congratulatse him"
        }
    ];

    apps: apps[] = [
        {
            id: 1,
            img: "/assets/images/svgs/icon-dd-chat.svg",
            title: "Chat Application",
            subtitle: "Messages & Emails",
            link: "/apps/chat"
        },
        {
            id: 2,
            img: "/assets/images/svgs/icon-dd-cart.svg",
            title: "Todo App",
            subtitle: "Completed task",
            link: "/apps/todo"
        },
        {
            id: 3,
            img: "/assets/images/svgs/icon-dd-invoice.svg",
            title: "Invoice App",
            subtitle: "Get latest invoice",
            link: "/apps/invoice"
        },
        {
            id: 4,
            img: "/assets/images/svgs/icon-dd-date.svg",
            title: "Calendar App",
            subtitle: "Get Dates",
            link: "/apps/calendar"
        },
        {
            id: 5,
            img: "/assets/images/svgs/icon-dd-mobile.svg",
            title: "Contact Application",
            subtitle: "2 Unsaved Contacts",
            link: "/apps/contacts"
        },
        {
            id: 6,
            img: "/assets/images/svgs/icon-dd-lifebuoy.svg",
            title: "Tickets App",
            subtitle: "Create new ticket",
            link: "/apps/tickets"
        },
        {
            id: 7,
            img: "/assets/images/svgs/icon-dd-message-box.svg",
            title: "Email App",
            subtitle: "Get new emails",
            link: "/apps/email/inbox"
        },
        {
            id: 8,
            img: "/assets/images/svgs/icon-dd-application.svg",
            title: "Conatct List",
            subtitle: "Create new contact",
            link: "/apps/contact-list"
        }
    ];

    quicklinks: quicklinks[] = [
        {
            id: 1,
            title: "Pricing Page",
            link: "/theme-pages/pricing"
        },
        {
            id: 2,
            title: "Authentication Design",
            link: "/authentication/login"
        },
        {
            id: 3,
            title: "Register Now",
            link: "/authentication/side-register"
        },
        {
            id: 4,
            title: "404 Error Page",
            link: "/authentication/error"
        },
        {
            id: 5,
            title: "Notes App",
            link: "/apps/notes"
        },
        {
            id: 6,
            title: "Employee App",
            link: "/apps/employee"
        },
        {
            id: 7,
            title: "Todo Application",
            link: "/apps/todo"
        },
        {
            id: 8,
            title: "Treeview",
            link: "/theme-pages/treeview"
        }
    ];


    // **********************


    updateSvgColor() {
        const color = getComputedStyle(document.body)
            .getPropertyValue('--mat-sys-primary')
            .trim();
        const colorWithOpacity = this.applyOpacityToCssVar(color, 0.4);
        const svgColored = savanaStusbar.replace(/fill="#FFE5C0"/g, `fill="${colorWithOpacity}"`);
        // const base64Svg = btoa(svgColored);
        const encodedSvg = encodeURIComponent(svgColored)
            .replace(/'/g, '%27')
            .replace(/"/g, '%22');
        this.savanaStusbar = `url("data:image/svg+xml,${encodedSvg}#${Date.now()}")`;

    }

    applyOpacityToCssVar(color: string, opacity: number): string {
        color = color.trim();

        if (color.startsWith('#')) {
            const rgb = this.hexToRgb(color);
            if (rgb) return `rgba(${rgb.r}, ${rgb.g}, ${rgb.b}, ${opacity})`;
        } else if (color.startsWith('rgb(')) {
            const nums = color.match(/\d+/g);
            if (nums && nums.length >= 3) {
                return `rgba(${nums[0]}, ${nums[1]}, ${nums[2]}, ${opacity})`;
            }
        }
        return color;
    }

    hexToRgb(hex: string): { r: number, g: number, b: number } | null {
        const cleanHex = hex.replace('#', '');
        const bigint = parseInt(cleanHex, 16);
        if (cleanHex.length === 6) {
            return {
                r: (bigint >> 16) & 255,
                g: (bigint >> 8) & 255,
                b: bigint & 255,
            };
        }
        return null;
    }
}
