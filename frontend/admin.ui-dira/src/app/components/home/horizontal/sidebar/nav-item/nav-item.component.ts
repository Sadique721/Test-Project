import { Component, OnInit, Input, Renderer2, ElementRef } from "@angular/core";
import { Router } from "@angular/router";
// import { NavService } from '../../../../../services/nav.service';
// import { TablerIconsModule } from 'angular-tabler-icons';

@Component({
    selector: "app-horizontal-nav-item",
    templateUrl: "./nav-item.component.html",
    standalone: false
})
export class AppHorizontalNavItemComponent implements OnInit {
    @Input() depth: any;
    @Input() item: any;

    constructor(
        // public navService: NavService
        public router: Router,
        private renderer: Renderer2,
        private elementRef: ElementRef
    ) {
        if (this.depth === undefined) {
            this.depth = 0;
        }
    }

    ngOnInit() { }
    onItemSelected(item: any) {
        if (!item.subMenu || !item.subMenu.length) {
            this.router.navigate([item.link]);
        }
    }

    getDepth() {
        return this.depth + 1;
    }

    onItemHover(event: any) {
        const childSubMenu = this.elementRef.nativeElement.querySelector("#childSubMenu");
        if (!childSubMenu) return;

        const rect = event.target.getBoundingClientRect();

        if (this.depth === 0) {
            // First level: dropdown opens below the menu item
            this.renderer.setStyle(childSubMenu, "left", `${rect.left}px`);
            this.renderer.setStyle(childSubMenu, "top", `${rect.bottom}px`);
        } else {
            // Second level and beyond: submenu opens to the side
            this.renderer.setStyle(childSubMenu, "left", `${rect.right + 5}px`);
            this.renderer.setStyle(childSubMenu, "top", `${rect.top}px`);
        }


        // const childSubMenu = this.elementRef.nativeElement.querySelector("#childSubMenu");
        // const rect = event.target.getBoundingClientRect();
        // if (this.depth === 1) {
        //     this.renderer.setStyle(childSubMenu, "left", `${rect.left + 230}px`);
        //     this.renderer.setStyle(childSubMenu, "top", `${rect.bottom - 50}px`);

        // }
        // else {
        //     this.renderer.setStyle(childSubMenu, "left", `${rect.left}px`);
        //     this.renderer.setStyle(childSubMenu, "top", `${rect.bottom}px`);
        // }
    }
}
