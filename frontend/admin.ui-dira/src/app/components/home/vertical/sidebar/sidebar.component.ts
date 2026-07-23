import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from "@angular/core";
import { BrandingComponent } from "./branding.component";
// import { TablerIconsModule } from 'angular-tabler-icons';
import { MaterialModule } from "src/app/material.module";

@Component({
  selector: "app-sidebar",
  templateUrl: "./sidebar.component.html",
  standalone: false
})
export class SidebarComponent implements OnInit {
  constructor() {}
  @Input() showToggle = true;
  @Output() toggleMobileNav = new EventEmitter<void>();
  @Output() toggleCollapsed = new EventEmitter<void>();

  ngOnInit(): void {}
}
