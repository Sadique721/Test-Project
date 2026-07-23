import { Component, ElementRef, OnInit, ViewChild } from "@angular/core";
import { UntypedFormGroup } from "@angular/forms";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";
import { Iprofile } from "src/app/components/model/acct-profile";
import { LoginService } from "src/app/service/login.service";
import { AclClassConstants } from "src/app/constants/aclClassConstants";
import { AclConstants } from "src/app/constants/aclOperationConstants";
import { RADIUS_CONSTANTS } from "src/app/constants/aclConstants";
import { ActivatedRoute, NavigationEnd, Router } from "@angular/router";
import { filter } from "rxjs";

@Component({
    selector: "app-vlan-profile",
    templateUrl: "./vlan-profile.component.html",
    styleUrls: ["./vlan-profile.component.css"],
    standalone: false
})
export class VlanProfileComponent implements OnInit {
    submitted = false;
    searchSubmitted = false;
    //   acctProfileForm: FormGroup;
    searchProfileForm: UntypedFormGroup;
    authenticationMode: any[] = [];
    //Client Group datah
    proxyServerData: any = [];
    mappingMasterData: any = [];
    profileData: any = [];
    //Used and required for pagination
    totalRecords: number;
    currentPage = 1;
    itemsPerPage = RadiusConstants.ITEMS_PER_PAGE;

    pageLimitOptions = RadiusConstants.pageLimitOptions;
    showItemPerPage: any;
    searchkey: string;

    editProfileData: Iprofile;
    editProfileId: number;
    editMode: boolean = false;
    status = [{ label: "Active" }, { label: "Inactive" }];
    status1 = [{ label: "Enable" }, { label: "Disable" }];
    type = [];
    coA = [{ label: "None" }, { label: "CoA" }, { label: "DM" }];
    showProfile: boolean = false;
    mvnoData: any;
    loggedInUser: any;
    mvnoId: any;
    modalToggle: boolean = true;
    filteredMappingList: Array<any> = [];
    filteredCoADMProfileList: Array<any> = [];
    filteredProxyServerList: Array<any> = [];
    deviceDriverList: Array<any> = [];
    accessData: any = JSON.parse(localStorage.getItem("accessData"));

    @ViewChild("profileName") usernameRef: ElementRef;
    showMacAttribute: boolean = false;
    showMACAuth: boolean = false;

    AclClassConstants;
    AclConstants;
    public loginService: LoginService;
    createAccess: any;
    editAccess: any;
    deleteAccess: any;
    isShowMenu = true;
    isShowCreateView = true;
    isShowListView = true;
    isShowBulkView = true;
    isShowAudit = true;

    constructor(
        loginService: LoginService, private router: Router,
        private route: ActivatedRoute
    ) {
        this.loginService = loginService;
        this.AclClassConstants = AclClassConstants;
        this.AclConstants = AclConstants;

        this.createAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_VLAN_MANAGMENT_CREATE);
        this.deleteAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_VLAN_MANAGMENT_DELETE);
        this.editAccess = loginService.hasPermission(RADIUS_CONSTANTS.RADIUS_VLAN_MANAGMENT_EDIT);
    }

    ngOnInit(): void {
        const childUrlSegment = this.route.firstChild.snapshot.url[0].path;
        if (childUrlSegment === "list") {
            this.isShowMenu = true;
            this.isShowListView = true;
            this.isShowCreateView = false;
            this.isShowBulkView = false;
            this.isShowAudit = false;
        } else if (childUrlSegment === "create" || childUrlSegment === "edit") {
            this.isShowMenu = true;
            this.isShowCreateView = true;
            this.isShowListView = false;
            this.isShowBulkView = false;
            this.isShowAudit = false;
        } else if (childUrlSegment === "bulkAdd") {
            this.isShowMenu = true;
            this.isShowCreateView = false;
            this.isShowListView = false;
            this.isShowBulkView = true;
            this.isShowAudit = false;
        } else if (childUrlSegment === "audit") {
            this.isShowMenu = true;
            this.isShowCreateView = false;
            this.isShowListView = false;
            this.isShowBulkView = false;
            this.isShowAudit = true;
        } else {
            this.isShowMenu = false;
            this.isShowCreateView = false;
            this.isShowListView = false;
            this.isShowBulkView = false;
            this.isShowAudit = false;
        }

        this.setActiveTabByUrl(this.router.url);

        // Also update tab when navigating using back/forward browser buttons
        this.router.events
            .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
            .subscribe((event) => {
                this.setActiveTabByUrl(event.urlAfterRedirects);
            });
    }

    activeTabIndex = 0;
    onTabChange(event: any) {
        switch (event.index) {
            case 0:
                this.router.navigate(['/home/vlanManagement/list']);
                break;
            case 1:
                this.router.navigate(['/home/vlanManagement/bulkAdd']);
                break;
            case 2:
                this.router.navigate(['/home/vlanManagement/audit']);
                break;
        }
    }

    private setActiveTabByUrl(url: string) {
        if (url.includes('/home/vlanManagement/list')) {
            this.activeTabIndex = 0;
        } else if (url.includes('/home/vlanManagement/bulkAdd')) {
            this.activeTabIndex = 1;
        } else if (url.includes('/home/vlanManagement/audit')) {
            this.activeTabIndex = 2;
        }
    }


}
