import { Component } from "@angular/core";
import { DomSanitizer, Title } from "@angular/platform-browser";
import { Router, NavigationEnd, ActivatedRoute, Data, RouterModule } from "@angular/router";
import { filter, map, mergeMap } from "rxjs/operators";
import { NavService } from "src/app/sidebar/services/nav.service";
import { savanaStusbar } from "src/assets/svgCode/savana-statusbar";
import { CoreService } from "src/app/app-setting/core.service";
import { CommonModule } from "@angular/common";
import { MatToolbarModule } from "@angular/material/toolbar";
import { MatButtonModule } from "@angular/material/button";
import { MatMenuModule } from "@angular/material/menu";
import { MatIconModule } from "@angular/material/icon";
import { NgScrollbarModule } from "ngx-scrollbar";
import { TablerIconsModule } from "angular-tabler-icons";

@Component({
  selector: "app-breadcrumb",
  standalone: true,
  imports: [
    CommonModule,
    RouterModule, // 🔥 THIS IS THE FIX FOR routerLink + queryParams
    MatToolbarModule,
    MatMenuModule,
    MatButtonModule,
    MatIconModule,
    NgScrollbarModule,
    TablerIconsModule
  ],
  templateUrl: "./breadcrumb.component.html",
  styleUrls: []
})
export class AppBreadcrumbComponent {
  pageInfo: Data | any = Object.create(null);
  savanaStusbar;
  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private titleService: Title,
    private navService: NavService,
    private sanitizer: DomSanitizer,
    private CoreService: CoreService
  ) {
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .pipe(map(() => this.activatedRoute))
      .pipe(
        map(route => {
          while (route.firstChild) {
            route = route.firstChild;
          }
          return route;
        })
      )
      .pipe(filter(route => route.outlet === "primary"))
      .pipe(mergeMap(route => route.data))
      // tslint:disable-next-line - Disables all
      .subscribe(event => {
        // tslint:disable-next-line - Disables all
        this.titleService.setTitle("Savbill BSS");
        this.pageInfo = event;
      });
  }

  ngOnInit() {
    this.CoreService.themeChanged.subscribe(() => {
      setTimeout(() => this.updateSvgColor(), 0);
    });
    this.updateSvgColor();
  }

  updateSvgColor() {
    const color = getComputedStyle(document.body).getPropertyValue("--mat-sys-primary").trim();
    const colorWithOpacity = this.applyOpacityToCssVar(color, 0.4);
    const svgColored = savanaStusbar.replace(/fill="#FFE5C0"/g, `fill="${colorWithOpacity}"`);
    // const base64Svg = btoa(svgColored);
    const encodedSvg = encodeURIComponent(svgColored).replace(/'/g, "%27").replace(/"/g, "%22");
    this.savanaStusbar = `url("data:image/svg+xml,${encodedSvg}#${Date.now()}")`;
  }

  applyOpacityToCssVar(color: string, opacity: number): string {
    color = color.trim();

    if (color.startsWith("#")) {
      const rgb = this.hexToRgb(color);
      if (rgb) return `rgba(${rgb.r}, ${rgb.g}, ${rgb.b}, ${opacity})`;
    } else if (color.startsWith("rgb(")) {
      const nums = color.match(/\d+/g);
      if (nums && nums.length >= 3) {
        return `rgba(${nums[0]}, ${nums[1]}, ${nums[2]}, ${opacity})`;
      }
    }
    return color;
  }

  hexToRgb(hex: string): { r: number; g: number; b: number } | null {
    const cleanHex = hex.replace("#", "");
    const bigint = parseInt(cleanHex, 16);
    if (cleanHex.length === 6) {
      return {
        r: (bigint >> 16) & 255,
        g: (bigint >> 8) & 255,
        b: bigint & 255
      };
    }
    return null;
  }
}
