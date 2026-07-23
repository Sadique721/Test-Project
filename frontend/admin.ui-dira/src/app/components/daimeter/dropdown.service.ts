import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";

@Injectable({
    providedIn: "root"
})
export class DropdownService {


    packetMappingData: any[] = [];
    vendorData: any[] = [];
    attributData: any[] = [];
    peerConfigList: any[] = [];
    policyList: any[] = [];

    MasterTypeList: any[] = [];
    DictionaryTypeList: any[] = [];
    VerificationModeList: any[] = [];
    CertificateTypeList: any[] = [];

    mvnoId: any

    constructor(
        private http: HttpClient,
    ) {
        this.mvnoId = localStorage.getItem("mvnoId");
    }

    getMethod(url) {
        return this.http.get(RadiusConstants.DIAMETER_PORT + url);
    }

    getpacketMappingData() {
        let url = "/packetMapping";
        this.getMethod(url).subscribe(
            (response: any) => {
                this.packetMappingData = response;
            },
            error => { }
        );
    }
    getvendorData() {
        let url = "/vendors";
        this.getMethod(url).subscribe(
            (response: any) => {
                this.vendorData = response;
            },
            error => { }
        );
    }
    getattributData() {
        let url = "/api/attributes";
        this.getMethod(url).subscribe(
            (response: any) => {
                this.attributData = response;
            },
            error => { }
        );
    }
    getpeerConfigList() {
        let url = "/api/peer-configurations";
        this.getMethod(url).subscribe(
            (response: any) => {
                this.peerConfigList = response;
            },
            error => { }
        );
    }
    getpolicyList() {
        let url = "/qos";
        this.getMethod(url).subscribe(
            (response: any) => {
                this.policyList = response;
            },
            error => { }
        );
    }
    getMasterTypeList() {
        let url = "/api/masters?type=TYPE";
        this.getMethod(url).subscribe(
            (response: any) => {
                this.MasterTypeList = this.convertObjectToDropdown(response);
            },
            error => { }
        );
    }
    getDictionaryTypeList() {
        let url = "/api/masters?type=DICTIONARY_TYPE";
        this.getMethod(url).subscribe(
            (response: any) => {
                this.DictionaryTypeList = this.convertObjectToDropdown(response);
            },
            error => { }
        );
    }
    getVerificationModeList() {
        let url = "/api/masters?type=VERIFICATION_MODE";
        this.getMethod(url).subscribe(
            (response: any) => {
                this.VerificationModeList = this.convertObjectToDropdown(response);
            },
            error => { }
        );
    }
    getCertificateTypeList() {
        let url = "/api/masters?type=CERTIFICATE_TYPE";
        this.getMethod(url).subscribe(
            (response: any) => {
                this.CertificateTypeList = this.convertObjectToDropdown(response);
            },
            error => { }
        );
    }

    convertObjectToDropdown(data: Record<string, string>) {
        return Object.entries(data).map(([key, value]) => ({
            label: value,
            value: key
        }));
    }

}
