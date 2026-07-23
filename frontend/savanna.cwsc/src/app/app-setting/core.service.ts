import { Injectable, signal } from "@angular/core";
import { AppSettings, defaults } from "./config";
import { Subject } from "rxjs";

@Injectable({
  providedIn: "root"
})
export class CoreService {
  private optionsSignal = signal<AppSettings>(defaults);
  public themeChanged = new Subject<any>();

  constructor() {
    if (localStorage.getItem("appSettings")) {
      this.optionsSignal.set({
        ...JSON.parse(localStorage.getItem("appSettings")!)
      });
    }
  }

  getOptions() {
    return this.optionsSignal();
  }

  setOptions(options: Partial<AppSettings>) {
    this.optionsSignal.update(current => ({
      ...current,
      ...options
    }));
    const appSettings = this.optionsSignal();
    localStorage.setItem("appSettings", JSON.stringify(appSettings));
    this.themeChanged.next(appSettings);
  }
}
