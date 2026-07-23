import { NgModule } from "@angular/core";
import { RouterModule } from "@angular/router";
import { KRAIntegrationComponent } from "./revenue-authorities-integration.component";

const routes = [
    { path: "", component: KRAIntegrationComponent }
];

@NgModule({
    imports: [
        KRAIntegrationComponent,
        RouterModule.forChild(routes)
    ]
})
export class KRAIntegrationModule { }
