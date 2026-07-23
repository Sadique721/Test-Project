import { CommonModule } from "@angular/common";
import { Component, Output, EventEmitter, Input } from "@angular/core";
// import { CoreService } from 'src/app/services/core.service';
import { MatDialog } from "@angular/material/dialog";
import { MatMenuModule } from "@angular/material/menu";
import { RouterModule } from "@angular/router";
import { SharedModule } from "src/app/shared/shared.module";

import { MatToolbarModule } from "@angular/material/toolbar";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";

import { NgScrollbarModule } from "ngx-scrollbar";
import { TablerIconsModule } from "angular-tabler-icons";
import { BrandingComponent } from "../../vertical/sidebar/branding.component";
// import { navItems } from '../../vertical/sidebar/sidebar-data';
// import { TranslateService } from "@ngx-translate/core";
// import { TablerIconsModule } from 'angular-tabler-icons';
// import { BrandingComponent } from '../../vertical/sidebar/branding.component';
// import { AppSettings } from 'src/app/config';

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
  selector: "app-horizontal-header",
  templateUrl: "./header.component.html",
  standalone: true,
  imports: [
    CommonModule,
    RouterModule, // 🔥 THIS IS THE FIX FOR routerLink + queryParams
    MatToolbarModule,
    MatMenuModule,
    MatButtonModule,
    MatIconModule,
    NgScrollbarModule,
    TablerIconsModule,
    BrandingComponent
  ]
})
export class AppHorizontalHeaderComponent {
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

  @Output() optionsChange = new EventEmitter<any>();

  constructor(
    // private settings: CoreService,
    // private vsidenav: CoreService,
    public dialog: MatDialog
    // private translate: TranslateService
  ) {
    // translate.setDefaultLang("en");
  }

  //   options = this.settings.getOptions();
  options = {
    dir: "ltr",
    theme: "light",
    sidenavOpened: false,
    sidenavCollapsed: false,
    boxed: true,
    horizontal: true,
    cardBorder: false,
    activeTheme: "blue_theme",
    language: "en-us",
    navPos: "side"
  };

  openDialog() {
    // const dialogRef = this.dialog.open(AppHorizontalSearchDialogComponent);
    // dialogRef.afterClosed().subscribe(result => {
    //   console.log(`Dialog result: ${result}`);
    // });
  }

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
      subtitle: "Congratulate him"
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
      title: "eCommerce App",
      subtitle: "Buy a Product",
      link: "/apps/email/inbox"
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
      title: "Courses",
      subtitle: "Create new course",
      link: "/apps/courses"
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
