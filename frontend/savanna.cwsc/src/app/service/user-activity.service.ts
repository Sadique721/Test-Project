import { Injectable } from "@angular/core";
import { Subject, Observable } from "rxjs";

@Injectable({ providedIn: "root" })
export class UserActivityService {
  private userActivity$ = new Subject<void>();

  constructor() {
    ["click", "mousemove", "keydown", "touchstart"].forEach(event => {
      document.addEventListener(event, () => this.userActivity$.next());
    });
  }

  onActivity(): Observable<void> {
    return this.userActivity$.asObservable();
  }
}
