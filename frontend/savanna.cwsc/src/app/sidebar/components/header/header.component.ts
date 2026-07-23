import { Component, Output, EventEmitter, Input, ViewEncapsulation } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MaterialModule } from "src/app/material.module";
import { RouterModule } from "@angular/router";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { NgScrollbarModule } from "ngx-scrollbar";
import { AppSettings } from "src/app/app-setting/config";
import { CoreService } from "src/app/app-setting/core.service";
import { SharedModule } from "src/app/shared/shared.module";
import { MatToolbarModule } from "@angular/material/toolbar";
import { MatMenuModule } from "@angular/material/menu";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { TablerIconsModule } from "angular-tabler-icons";

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
  standalone: true,
  imports: [CommonModule,
    RouterModule,          // 🔥 THIS IS THE FIX FOR routerLink + queryParams
    MatToolbarModule,
    MatMenuModule,
    MatButtonModule,
    MatIconModule,
    NgScrollbarModule,
    TablerIconsModule,],
  templateUrl: "./header.component.html"
})
export class HeaderComponent {
  @Input() showToggle = true;
  @Input() toggleChecked = false;
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

  @Output() optionsChange = new EventEmitter<AppSettings>();

  constructor(
    private settings: CoreService,
    private vsidenav: CoreService,
    public dialog: MatDialog
  ) {}

  options = this.settings.getOptions();

  openDialog() {
    const dialogRef = this.dialog.open(AppSearchDialogComponent);

    dialogRef.afterClosed().subscribe(result => {
      console.log(`Dialog result: ${result}`);
    });
  }

  private emitOptions() {
    this.optionsChange.emit(this.options);
  }

  setlightDark(theme: string) {
    this.options.theme = theme;
    this.emitOptions();
  }

  changeLanguage(lang: any): void {
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

  // profiledd: profiledd[] = [
  //   {
  //     id: 1,
  //     img: '/assets/images/svgs/icon-account.svg',
  //     title: 'My Profile',
  //     subtitle: 'Account Settings',
  //     link: '/',
  //   },
  //   {
  //     id: 2,
  //     img: '/assets/images/svgs/icon-inbox.svg',
  //     title: 'My Inbox',
  //     subtitle: 'Messages & Email',
  //     link: '/apps/email/inbox',
  //   },
  //   {
  //     id: 3,
  //     img: '/assets/images/svgs/icon-tasks.svg',
  //     title: 'My Tasks',
  //     subtitle: 'To-do and Daily Tasks',
  //     link: '/apps/taskboard',
  //   },
  // ];

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
}

const menuList = [
  {
    displayName: "Dashboard",
    iconName: "aperture",
    link: "/home/dashbord",
    subMenu: [],
    isVisible: false
  },
  {
    displayName: "My Profile",
    iconName: "aperture",
    link: "/home/userProfile",
    subMenu: [],
    isVisible: false
  },
  {
    displayName: "Master Management",
    iconName: "aperture",
    link: "",
    subMenu: [
      {
        displayName: ` Management`,
        iconName: "aperture",
        link: "/home/countryManagement",
        subMenu: [],
        isVisible: false
      },
      {
        displayName: `Management`,
        iconName: "aperture",
        link: "/home/stateManagement",
        subMenu: [],
        isVisible: false
      },
      {
        displayName: `Management`,
        iconName: "aperture",
        link: "/home/cityManagement",
        subMenu: [],
        isVisible: false
      },
      {
        displayName: ` Management`,
        iconName: "aperture",
        link: "/home/pincodeManagement",
        subMenu: [],
        isVisible: false
      },
      {
        displayName: ` Management`,
        iconName: "aperture",
        link: "/home/areaManagement",
        subMenu: [],
        isVisible: false
      },
      {
        displayName: `Management`,
        iconName: "aperture",
        link: "/home/subareaManagement",
        subMenu: [],
        isVisible: false
      },
      {
        displayName: ` Management`,
        iconName: "aperture",
        link: "/home/buildingConfigManagement",
        subMenu: [],
        isVisible: false
      },
      {
        displayName: ` Management`,
        iconName: "aperture",
        link: "/home/buildingManagement",
        subMenu: [],
        isVisible: false
      },
      {
        displayName: "Service Area",
        iconName: "aperture",
        link: "/home/serviceArea",
        subMenu: [],
        isVisible: false
      },
      {
        displayName: "Investment Code",
        iconName: "aperture",
        link: "/home/investmentCode",
        subMenu: [],
        isVisible: false
      },
      {
        displayName: `Business Unit`,
        iconName: "aperture",
        link: "/home/businessunit",
        subMenu: [],
        isVisible: false
      },
      {
        displayName: `Sub Business Unit`,
        iconName: "aperture",
        link: "/home/subbusinessunit",
        subMenu: [],
        isVisible: false
      },
      {
        displayName: `Bank Management`,
        iconName: "aperture",
        link: "/home/bankManagement",
        subMenu: [],
        isVisible: false
      },
      {
        displayName: `Branch Management`,
        iconName: "aperture",
        link: "/home/branch-management",
        subMenu: [],
        isVisible: false
      },
      {
        displayName: `Region Management`,
        iconName: "aperture",
        link: "/home/region",
        subMenu: [],
        isVisible: false
      },
      {
        displayName: `Business vertical Management`,
        iconName: "aperture",
        link: "/home/businessVertical",
        subMenu: [],
        isVisible: false
      },
      {
        displayName: `Sub Business Vertical Management`,
        iconName: "aperture",
        link: "/home/subbusinessVertical",
        subMenu: [],
        isVisible: false
      },
      {
        displayName: `Department Management`,
        iconName: "aperture",
        link: "/home/departmentManagement",
        subMenu: [],
        isVisible: false
      },
      {
        displayName: `Location Master Management`,
        iconName: "aperture",
        link: "/home/location",
        subMenu: [],
        isVisible: false
      }
    ],
    isVisible: false
  },
  {
    displayName: "Product Management",
    iconName: "aperture",
    link: "",
    subMenu: [
      {
        displayName: `Service Management`,
        iconName: "aperture",
        link: "/home/serviceManagement",
        subMenu: [],
        isVisible: false
      }
    ],
    isVisible: false
  },
  {
    displayName: "Partner Management",
    iconName: "aperture",
    link: "",
    subMenu: [
      {
        displayName: `Partner Plan Bundle`,
        iconName: "aperture",
        link: "/home/planBundle",
        subMenu: [],
        isVisible: false
      }
    ],
    isVisible: false
  }
];

@Component({
  selector: "search-dialog",
  imports: [RouterModule, MaterialModule, FormsModule],
  templateUrl: "search-dialog.component.html"
})
export class AppSearchDialogComponent {
  searchText: string = "";
  navItems = menuList;

  navItemsData = menuList.filter(navitem => navitem.displayName);

  // filtered = this.navItemsData.find((obj) => {
  //   return obj.displayName == this.searchinput;
  // });
}
