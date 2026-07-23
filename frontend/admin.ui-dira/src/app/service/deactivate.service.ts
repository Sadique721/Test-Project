import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, RouterStateSnapshot, CanDeactivate } from "@angular/router";
import { Observable } from "rxjs";

export interface IDeactivateGuard {
    canExit: () => boolean | Promise<boolean> | Observable<boolean>;
}

@Injectable({
    providedIn: 'root'  // ✅ UNCOMMENT THIS - It's critical!
})
export class DeactivateService implements CanDeactivate<IDeactivateGuard> {
    private shouldCheckCanExit: boolean = true;

    setShouldCheckCanExit(shouldCheck: boolean) {
        this.shouldCheckCanExit = shouldCheck;
    }

    canDeactivate(
        component: IDeactivateGuard,
        route: ActivatedRouteSnapshot,
        currentState: RouterStateSnapshot,
        nextState?: RouterStateSnapshot
    ): boolean | Promise<boolean> | Observable<boolean> {

        if (!component) {
            console.warn('Component is null in canDeactivate');
            return true;
        }

        if (typeof component.canExit !== 'function') {
            console.warn('Component does not have canExit method');
            return true;
        }

        if (this.shouldCheckCanExit) {
            return component.canExit();
        } else {
            this.shouldCheckCanExit = true;
            return true;
        }
    }
}
