import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import * as RadiusConstants from "src/app/RadiusUtils/RadiusConstants";

@Injectable({
  providedIn: "root",
})
export class StatusCheckService {
  isActiveSalesCrm = false;
  isActiveCMS = false;
  isActivePMS = false;
  isActiveTicketService = false;
  isActiveInventoryService = false;
  isActiveRevenueService = false;
  isActiveRadiusService = false;
  isActiveNotificationService = false;
  isActiveTaskManagementService = false;
  isActiveKPIService = false;
  isActiveIntegrationService = false;
  isActiveTacacs = false;
  constructor(private http: HttpClient) {}

  getCMSServiceStatus() {
    this.http
      .get(`${RadiusConstants.SAVBILL_PRODUCT_MANAGEMENT_BASE_URL}/serviceStatus`)
      .subscribe(
        (response: any) => {
          this.isActiveCMS = true;
        },
        (error: any) => {
          this.isActiveCMS = false;
        }
      );
  }

  getTicketServiceStatus() {
    this.http
      .get(`${RadiusConstants.SAVBILL_TICKET_MANAGEMENT}/serviceStatus`)
      .subscribe(
        (response: any) => {
          this.isActiveTicketService = true;
        },
        (error: any) => {
          this.isActiveTicketService = false;
        }
      );
  }

  getInventoryServiceStatus() {
    this.http
      .get(`${RadiusConstants.SAVBILL_INVENTORY_MANAGEMENT}/serviceStatus`)
      .subscribe(
        (response: any) => {
          this.isActiveInventoryService = true;
        },
        (error: any) => {
          this.isActiveInventoryService = false;
        }
      );
  }

  getRevenueServiceStatus() {
    this.http
      .get(`${RadiusConstants.SAVBILL_REVENUE_URL}/serviceStatus`)
      .subscribe(
        (response: any) => {
          this.isActiveRevenueService = true;
        },
        (error: any) => {
          this.isActiveRevenueService = false;
        }
      );
  }

  getRadiusServiceStatus() {
    this.http
      .get(`${RadiusConstants.SAVBILL_RADIUS_BASE_URL}/serviceStatus`)
      .subscribe(
        (response: any) => {
          this.isActiveRadiusService = true;
        },
        (error: any) => {
          this.isActiveRadiusService = false;
        }
      );
  }

  getNotificationServiceStatus() {
    this.http
      .get(`${RadiusConstants.SAVBILL_NOTIFICATION_BASE_URL}/serviceStatus`)
      .subscribe(
        (response: any) => {
          this.isActiveNotificationService = true;
        },
        (error: any) => {
          this.isActiveNotificationService = false;
        }
      );
  }
}
