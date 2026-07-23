import { Injectable, signal } from "@angular/core";
import { Event, NavigationEnd, Router } from "@angular/router";

@Injectable({ providedIn: "root" })
export class NavService {
    showClass: any = false;

    public currentUrl = signal<string | undefined>(undefined);

    // REMOVE the private _menuSelection signal - you don't need it
    // Keep only ONE signal for menu selection
    menuSelection = signal<{ main: string; sub?: string }>({ main: "", sub: "" });

    constructor(private router: Router) {
        this.router.events.subscribe((event: Event) => {
            if (event instanceof NavigationEnd) {
                this.currentUrl.set(event.urlAfterRedirects);
            }
        });
    }

    setMainMenu(name: string) {
        // Clear sub when main changes
        this.menuSelection.update(m => ({ ...m, main: name ?? "", sub: "" }));
    }

    setSubMenu(name: string) {
        this.menuSelection.update(m => ({ ...m, sub: name ?? "" }));
    }

    // Update this method to use the PUBLIC menuSelection signal
    setMenuSelection(main: string, sub?: string) {
        this.menuSelection.set({ main, sub: sub || '' });
    }
}
