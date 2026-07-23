import { Component, Output, EventEmitter, Input, ViewEncapsulation } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { navItems } from "../sidebar/sidebar-data";
// import { TranslateService } from "@ngx-translate/core";
// import { TablerIconsModule } from 'angular-tabler-icons';
import { MaterialModule } from "src/app/material.module";
import { RouterModule } from "@angular/router";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { NgScrollbarModule } from "ngx-scrollbar";
import { CoreService } from "src/app/app-setting/core.service";
import { AppSettings } from "src/app/app-setting/config";
import { LoginService } from "src/app/service/login.service";
import { SharedModule } from "src/app/shared/shared.module";
import { MatMenuModule } from "@angular/material/menu";

import { MatToolbarModule } from "@angular/material/toolbar";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { TablerIconsModule } from "angular-tabler-icons";
import { CustomermanagementService } from "src/app/service/customermanagement.service";
import { CommondropdownService } from "src/app/service/commondropdown.service";
// import { StaffService } from "src/app/service/staff.service";

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
  standalone: true,
  imports: [
    CommonModule,
    RouterModule, // 🔥 THIS IS THE FIX FOR routerLink + queryParams
    MatToolbarModule,
    MatMenuModule,
    MatButtonModule,
    MatIconModule,
    NgScrollbarModule,SharedModule,
    TablerIconsModule
  ]
})
export class HeaderComponent {
  public userName: string = "";
  public userEmail: string = "";
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
  userId;
  userEmailId = "";
  @Output() optionsChange = new EventEmitter<AppSettings>();

  constructor(
    private settings: CoreService,
    private vsidenav: CoreService,
    public dialog: MatDialog,
    private loginService: LoginService,
    public customerManagementService: CustomermanagementService,
    public commondropdownService: CommondropdownService
    // private staffService: StaffService,
  ) {
    // translate.setDefaultLang("en");
  }

  options = this.settings.getOptions();

  ngOnInit(): void {
    //   this.userId = localStorage.getItem("userId");
    //   this.staffService.getStaffUserData(this.userId).subscribe((response: any) => {
    //     this.userName = response?.Staff.username || "User";
    //     this.userEmailId = response?.Staff?.email || "Email";
    //   });
    this.userId = this.commondropdownService.getUserId();
    this.getCustomersDetail(this.userId);
  }
  getCustomersDetail(custId) {
    const url = "/customers/" + custId;
    this.customerManagementService.getMethod(url).subscribe((response: any) => {
      let custDetails = response.customers;
      this.userName = custDetails.username || "User";
      this.userEmailId = custDetails?.email || "Email";
    });
  }
  openDialog() {}

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
}
