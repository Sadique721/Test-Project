import 'dart:convert';
import 'dart:developer';
import 'dart:io';

//import 'package:html/dom.dart' as pw;
import 'package:intl/intl.dart';
import 'package:pdf/widgets.dart' as pw;
import 'package:path_provider/path_provider.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:savbill/pages/customer/model/request/add_edit_customer_req.dart';
import 'package:savbill/pages/customer/model/request/add_notes_req.dart';
import 'package:savbill/pages/customer/model/request/assign_inventory_req.dart';
import 'package:savbill/pages/customer/model/request/change_customer_pwd_req.dart';
import 'package:savbill/pages/customer/model/request/change_customer_status_req.dart';
import 'package:savbill/pages/customer/model/request/cust_upload_document_req.dart';
import 'package:savbill/pages/customer/model/request/cust_wallet_bal_req.dart';
import 'package:savbill/pages/customer/model/request/custmer_list_request.dart';
import 'package:savbill/pages/customer/model/request/customer_doc_approve_reject_req.dart';
import 'package:savbill/pages/customer/model/request/nearby_devices_req.dart';
import 'package:savbill/pages/customer/model/request/send_payment_link_req.dart';
import 'package:savbill/pages/customer/model/request/update_mac_serial_req.dart';
import 'package:savbill/pages/customer/model/request/verify_document_req.dart';
import 'package:savbill/pages/customer/model/response/customer_document_res.dart';
import 'package:savbill/pages/customer_caf/response/add_edit_customer_caf_req.dart';
import 'package:savbill/pages/customer_caf/response/request/cust_change_plan_caf_req.dart';
import 'package:savbill/pages/customer_inventory/request/approve_inventory_req.dart';
import 'package:savbill/pages/customer_inventory/request/external_inventory_plan_req.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/pages/task_management/model/request/cust_inventory_new_req.dart';
import 'package:savbill/pages/workflow/model/cust_audit_detail_req.dart';
import 'package:savbill/webservices/api_request.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:dio/dio.dart' as multi;

import '../../util/constant.dart';
import '../customer_inventory/request/assign_plan_inventory_by_plan_req.dart';
import 'package:syncfusion_flutter_xlsio/xlsio.dart';

class CustomerProvider {
  // get getCustomerDropDownList
  void getCustomerDropDownList({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.customer_drop_down_list,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get Building Detail
  void getBuildingDetails({
    required int custId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.getBuildingAndSubareaNamesDetails}/$custId",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get customer offline online
  void getCustomerCheckStatus({
    required Map<String, List<String>?> usernameList,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.customer_online_offline, data: usernameList)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // getStaffUserSearch
  void getStaffUserSearch({
    CustomerListRequest? customerListRequest,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.staff_user_search, data: customerListRequest)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get customer list
  void getCustomerList({
    required String type,
    required bool isSearch,
    CustomerListRequest? customerListRequest,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    //Postpaid, Prepaid
    String url = "${UrlConstants.customer_list}/$type?orgcusttype=false";
    if (isSearch) {
      url = "${UrlConstants.customer_search}/$type";
    }
    print("type -> $type");
    log("type==>${type}");
    log("urlSEarch==>${url}");
    log("request=>${jsonEncode(customerListRequest)}");
    ApiRequest(url: url, data: customerListRequest).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get network Detail
  void getNetworkDetails({
    required int custId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.getCustomerNetworkDetails}?customerId=$custId",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get All Services By Service Area Id
  void getServicePlanModeServiceAreaListPlanCategory({
    required int? serviceAreaId,
    required int? custId,
    required String? plantype,
    required int? currPlanId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.service_planmode_serviceAreaId}?planCategory=NORMAL&serviceAreaId=${serviceAreaId}&planmode=NORMAL&custId=${custId}&plantype=${plantype}&currPlanId=${currPlanId}")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get active product list
  void getCustomerServiceManagementCaf({
    required int? customerId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.plan_service_by_cust}$customerId?isNotChangePlan=false&status=NewActivation")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void getCustomerPlanDetail({
    required int? planId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.cust_postpaid_plan}$planId").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get Active plan list
  void getCustomerActivePlanList({
    required int? planId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url: "${UrlConstants.activePlanList}/$planId?isNotChangePlan=true")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void getCustomerNotes({
    required int custId,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.get_cust_caf_notes}/$custId",
    ).getRequest(
      onSuccess: (data) => onSuccess?.call(data),
      onError: (error) => onError?.call(error),
    );
  }

  void getCustomerAllNotes({
    required int custId,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.get_cust_all_notes}/$custId",
    ).getRequest(
      onSuccess: (data) => onSuccess?.call(data),
      onError: (error) => onError?.call(error),
    );
  }

  void getStaffUser({
    required int? Id,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.get_staff_user}/$Id",
    ).getRequest(
      onSuccess: (data) => onSuccess?.call(data),
      onError: (error) => onError?.call(error),
    );
  }

  void customerCafDeActivePlan(
      {CustChangePlanCafReq? custChangePlanReq,
      Function()? beforeSend,
      Function(ResponseModel responseModel)? onSuccess,
      Function(ResponseModel error)? onError,
      required}) {
    ApiRequest(url: UrlConstants.deactivatePlan, data: custChangePlanReq)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get customer Detail
  void getCustomerDetail({
    required int customerId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.customer_detail}/$customerId",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get customer quota Detail
  void getCustomerQuotaDetail({
    required int customerId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.customer_quota}/$customerId",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get area Detail
  void getAreaDetail({
    required int areaId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.area_detail}/$areaId",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get area Detail
  void getSubAreaFromArea({
    required int? areaId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.get_subArea_from_area}?areaId=$areaId",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// send payment link to customer
  void sendPaymentLinkCustomer({
    required SendPaymentLinkReq sendPaymentLinkReq,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.send_payment_link}${sendPaymentLinkReq.custId}",
            data: sendPaymentLinkReq)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get customer status type
  void getCustomerStatusType({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.generic_request}custStatus",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// change customer status
  void changeCustomerStatus({
    required ChangeCustomerStatusReq changeCustomerStatusReq,
    required String? remark,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.change_cust_req}/${changeCustomerStatusReq.id!}?status=${changeCustomerStatusReq.status!}&remark=${remark ?? 'undefined'}",
            data: changeCustomerStatusReq)
        .putRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get active product list
  void getChargeByType({
    required String type,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.charge_by_type + type.toString()).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get Customer charge by Id and type
  void getCustomerChargeByIdAndType({
    required String type,
    required int? serviceId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.charge_by_type}${type.toString()}?serviceId=$serviceId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get active product list
  void getCustomerService({
    required int customerId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.plan_service_by_cust + customerId.toString()}?isAllRequired=true&isNotChangePlan=false")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void getWifiConfig({
    required dynamic data,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.getWifiConfig, data: data).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void saveNMSWifiConfig({
    required dynamic data,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.NMSWifiConfig, data: data).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get Caf active product list
  void getCustomerCafServiceManagement({
    required int customerId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.plan_service_by_cust}$customerId?status=NewActivation&isNotChangePlan=true")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get active product list
  void getCustomerServiceManagement({
    required int customerId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.plan_service_by_cust}$customerId?isAllRequired=true&isNotChangePlan=true")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get Inventory Type list
  void getInventoryType({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.get_inventory_type).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get nature list
  void getNature({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.get_nature).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get active product list
  void getActiveProductList({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.get_active_product).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get inwards list
  void getInwardsList({
    required int productId,
    required int userId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.inwards_by_product_staff}?productId=$productId&staffId=$userId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get outwards list
  void getMacOutwardsList({
    required int id,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.get_mac_outwards}=$id").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get nearby devices
  void getNearByDevices({
    NearbyDevicesReq? nearbyDevicesReq,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.nearby_devices, data: nearbyDevicesReq)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// delete customer
  void deleteCustomer({
    required int customerId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.delete_customer}/$customerId",
    ).deleteRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get customer document
  void getCustomerDocument({
    required int customerId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.customer_document}/$customerId",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// verify customer document
  void verifyCustomerDocument({
    VerifyDocumentRequest? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.verify_customer_document, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get customer title
  void getCustomerTitle({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.customer_title).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get parent customer list
  void getParentCustomerList({
    required String type,
    required bool isSearch,
    CustomerListRequest? customerListRequest,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    //Postpaid, Prepaid
    String url = UrlConstants.parent_customer_list;
    /* if (isSearch) {
      url = UrlConstants.customer_search;
    }*/
    ApiRequest(url: "$url/$type", data: customerListRequest).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) {
        if (onError != null) {
          onError(error);
        }
      },
    );
  }

// get customer status
  void getCustomerStatus({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.customer_status).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get staffs By Service Area Id
  void getStaffsByServiceAreaId({
    required int? serviceAreaID,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = UrlConstants.staffsByServiceAreaId;
    ApiRequest(url: "$url/$serviceAreaID").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// Get Department List
  void getCustomerDepartmentList({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.deparment_list).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get customer category
  void getCustomerCategory({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.customer_category).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get location list
  void getLocationData({
    required String type,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.location_list}$type").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get location to lat-long
  void getLocationToLatLong({
    required String placeId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.location_to_latlong}$placeId").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get all partner list
  void getAllPartner({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.all_partner).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get pay mode list
  void getPaymentMode({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.payment_mode).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get New Services Area data
  void getNewServiceAreaData({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.service_area_new).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get New Services Area CAF data
  void getNewServiceAreaCAFData({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.service_area_caf_new).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void getSubAreaNew({
    int? serviceArea,
    PageRequest? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    var url =
        "${UrlConstants.sub_area_with_pagination}?page=${request!.page}&pageSize=${request.pageSize}";
    if (serviceArea != null) {
      url += "&area=$serviceArea";
    }
    ApiRequest(url: url).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void searchSubAreaNew({
    required Map<String, dynamic> body,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.sub_area_new, data: body).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// sub area
  void getSubArea({
    CustomerListRequest? customerListRequest,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.sub_area).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get PinCode ALl
  void getPinCodeALl({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.get_pincode_all).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get Area All
  void getAreaAll({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.get_area_all).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// building management
  void getBuildingManagement({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.building_mgmt).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// building Refrence All
  void getBuildingReferenceAll({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.buildingReferenceAll).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// building Refrence All
  void getBuildingMgmtNumbers({
    Function()? beforeSend,
    required int? buildingMgmtId,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.get_building_mgmt_numbers}?buildingMgmtId=$buildingMgmtId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get Area Id From SubArea Id
  void getAreaIdFromSubAreaId({
    required int? subAreaId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.get_area_id_from_sub_area_id}?subAreaId=$subAreaId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get Servic Area Id By Pincode
  void getServicAreaIdByPincode({
    required int? pinCodeId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.getServic_AreaIdBy_Pincode}?pincodeid=$pinCodeId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// getBuildingMgmt
  void getBuildingMgmt({
    required String? entityName,
    required int? entityId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.get_building_mgmt}?entityname=$entityName&entityid=$entityId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get pincode to area
  void getPincodeToAreaForCreateCustomer({
    required int id,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.pincode_detail}/$id").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get All Branches By Service Area Id
  void getAllBranchesByServiceAreaId({
    required List<int>? serviceAreaId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url: UrlConstants.getAllBranchesByServiceAreaId,
            data: serviceAreaId)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get pincode to area
  void getPincodeToAreaData({
    required int id,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.pincode_detail}/$id").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get services area detail
  void getServiceAreaDetail({
    required int id,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.service_area_detail}/$id").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get All Services By Service Area Id
  void getServicePlanModeServiceAreaList({
    required int? serviceAreaId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.service_planmode_serviceAreaId}?planmode=NORMAL&serviceAreaId=$serviceAreaId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get All Services By Service Area Id
  void getAllServicesByServiceAreaId({
    required List<int>? serviceAreaId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url: UrlConstants.getAllServicesByServiceAreaId,
            data: serviceAreaId)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get pin code data
  void getPincodeToArea({
    required int pincodeid,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.pincode_to_area + pincodeid.toString())
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get pin code data
  void getPincodeData({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.get_all_pincode).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get all city
  void getAllCity({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.get_all_city).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get all state
  void getAllState({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.get_all_state).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get all state
  void getAllCountry({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.get_all_country).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get bill to
  void getBillToData({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.bill_to).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get all plan group
  void getPlanGroup({
    required String planMode,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.plan_group + planMode).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get all plan group
  void getChangePlanGroupLst({
    required int custId,
    required String planMode,
    required String planCategory,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.plan_group}$planMode&planCategory=$planCategory&custId=$custId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get plan service
  void getPlanService({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.plan_service).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get Post paid plan
  void getPostpaidPlan({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.postpaid_plan).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get Post paid plan
  void getAllCharge({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.all_charge).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get Charget By Id
  void getChargeById({
    required int? chargeID,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.chargeById}/${chargeID}").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get customer is exist
  void checkCustomerExist({
    required String username,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.check_exist_customer}/$username")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void addCustomerRequest({
    required AddEditCustomerReq request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.add_customer, data: request).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void addCafCustomerRequest({
    required AddEditCustomerCafReq request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.add_customer, data: request).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void editCustomerRequest({
    required int customerId,
    required AddEditCustomerReq request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.edit_customer}/$customerId", data: request)
        .putRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void updateCustomerBasicDetailsRequest({
    required int? id,
    required AddEditCustomerReq request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.update_customer}/$id", data: request)
        .putRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void changeCustomerPassword({
    required ChangeCustomerPasswordReq request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.change_customer_password, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void getCustomerWalletBal({
    required CustomerWalletReq request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.customer_wallet_bal, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void getBuildingAndSubAreaName({
    required int? customerId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.BuildingAndSubareaNames}/$customerId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// assign customer inventory
  void assignInventory({
    required AssignInventoryReq request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.assign_inventory, data: request).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// assign other inventory request
  void assignOtherInventory({
    required AssignInventoryReq request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.assign_inventory, data: request).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

//get valley type
  void getValleyType({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.valley_type,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

//inside valley data
  void insideValleyData({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.inside_valley,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

//outside valley data
  void outsideValleyData({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.outside_valley,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// Customer Type
  void getCustomerType({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.customer_type,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// Customer sector
  void getCustomerSector({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.customer_sector,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get customer sub type
  void getCustomerSubType({
    required String type,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.customer_sub_type + type).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

//get promise to pay remarks
  void getPromiseToPayRemarks({
    required int customerId,
    required int? graceDays,
    required String? promiseToRemarks,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.promise_to_pay_remarks}$customerId/${'promise_to_pay_remarks'}/${'graceDays'}?promise_to_pay_remarks=$promiseToRemarks")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get Payment Owner List
  void getPaymentOwnerListService({
    required int id,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.payment_owner_change_plan}/$id").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get parent customer list
  void getParentStaffList({
    PageRequest? pageRequest,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.get_staff_user_list, data: pageRequest)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) {
        if (onError != null) {
          onError(error);
        }
      },
    );
  }

// get Mac Address list by product
  void getProductMacAddressList({
    // required int? productId,
    // required int? ownerId,
    // required String ownerName,
    required CustAssigninwardNewReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.get_product_mac_address_list, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get Mac Address list by product
  void getCustProductMacAddressList({
    // required int? productId,
    // required int? ownerId,
    // required String ownerName,
    required CustAssigninwardNewReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.get_product_mac_address_list, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get active product list
  void getProductByProductAssignInventory({
    required int serviceId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.assign_product_by_product_inventory}?serviceId=$serviceId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// Assign Other Inventory  (getAllProductByServiceIdInventory)
  void getAllProductByServiceIdInventory({
    required int serviceId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.assign_product_by_service_id_inventory}?serviceId=$serviceId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// post updateItemMacAndSerial
  void updateMacSerial({
    UpdateMacSerialReq? updateMacSerialReq,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.updateItemMacAndSerial}?itemId=${updateMacSerialReq!.itemId}&macAddress=${updateMacSerialReq.macAddress}&serialNumber=${updateMacSerialReq.serialNumber}")
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// getAllCustomerInventoryList
  void getAllCustomerInventoryList({
    required int custId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.getAllCustomerInventoryListUrl}?custId=$custId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// getApproveInventory
  void getApproveRequestInventory({
    required int? nextStaffId,
    required String? remark,
    required ApproveInventoryReq request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.approveInventory}?isApproveRequest=${request.approveReq}&nextstaff=$nextStaffId&remark=$remark",
            data: request.requestApproveId)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// getReactivateBoxResponse
  void getInventoryReactivateBox({
    required ApproveInventoryReq request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url: UrlConstants.inventoryReactivateBox,
            data: request.requestApproveId)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// getAllProductForNonTrackableProductCategory
  void getAllProductForNonTrackableProductCategory({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.get_product_for_non_trackable_product_category)
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// getAllProductForNonTrackableProductCategory
  void getNonTrackableProductQty({
    required int productId,
    required int ownerId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.getNonTrackableProductQtyUrl}?productId=$productId&ownerId=$ownerId&ownerType=Staff")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// getAllPlanInventoryIdOnPlanId
  void getAllPlanInventoryIdApi({
    required int planId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.getAllPlanInventoryIdOnPlanId}/planId?planId=$planId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// getProductCategoryByPlanId
  void getProductCategoryByPlanIdCall({
    required int categoryId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.getProductCategoryByPlanId}?mappingId=$categoryId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

//getProductByPlanId
  void getProductByPlanIdCall({
    required int mappingId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.getProductByPlanId}?mappingId=$mappingId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

//getItemBasedOnProductType
  void getItemBasedOnProductType({
    required String? ownerId,
    required String? planId,
    required String? productId,
    required String? planGroupId,
    required String? productCategoryId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.getItemBasedOnProductType}?ownerType=Staff&ownerid=$ownerId&planId=$planId&productId=$productId&planGroupId=$planGroupId&productCategoryId=$productCategoryId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

//getProductMappingDetails
  void getProductMappingDetailsCall({
    required String? planId,
    required String? productId,
    required String? planGroupId,
    required String? productCategoryId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.getProductMappingDetails}?planGroupId=$planGroupId&planId=$planId&productCategoryId=$productCategoryId&productId=$productId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// AssignPlanInventoryByPlan
  void assignPlanInventoryByPlanCall({
    required AssignPlanInventoryByPlanReq request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.assign_inventory, data: request).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// External Inventory By Plan
  void externalPlanInventoryByPlanCall({
    required ExternalInventoryPlanReq request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.assign_inventory, data: request).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get active product list
  void getExternalItemGroupByCustomer({
    required int customerId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.getExternalItemGroupAllProductsByCustomer}?custId=${customerId.toString()}")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// getAllExternalItemProductAndStaff
  void getAllExternalItemProductAndStaff({
    required int? customerId,
    required int? productId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.getAllExternalItemGroupByProductAndStaff}?productId=$productId&ownerId=${customerId.toString()}")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// getAllMACMappingByExternalId
  void getAllMACMappingByExternalId({
    required int? externalId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.getAllMACMappingByExternalId}?external_id=$externalId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// getAllCustomerInventoryDetailsHistory
  void getAllCustomerInventoryDetailsHistory({
    required int? customerId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.getAllCustomerInventoryDetailsHistory}?custId=$customerId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get customer Document verification

  void getCustDocVerification({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.customer_document_verification).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get customer Document verification mode offline
  void getDocTypeVerificationMode({
    required String? docType,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.customer_document_verification_cpm}?mode=${docType}")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void getDocSubTypeVerification({
    required String? docType,
    required String? verificationMode,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.cust_document_sub_type}?custdocsubtype=${docType}&mode=$verificationMode")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void getDocumentStatus({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.cust_document_status).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void customerUploadDocument({
    required multi.FormData formData,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url: UrlConstants.customer_upload_document,
            formData: formData,
            isFormData: true)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void customerUploadDocumentUpdate({
    required CustUploadDocumentReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.customer_upload_document_Update, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void customerUploadDocumentDelete({
    required DocumentDetail? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.customer_upload_document_Delete, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  ///Audit Details
  void customerAuditDetail({
    required CustAuditDetailReq? request,
    required int? custId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url: "${UrlConstants.customer_audit_details}/$custId",
            data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  ///Dunning Management
  void customerDunningDetail({
    required CustomerListRequest? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.customer_dunning_management, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  /// Dunning Status change

  void dunningStatusChange({
    required int? custId,
    required bool dunningStatus,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.customer_dunning_status_change}?custId=$custId&dunningStatus=$dunningStatus")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  ///Notificaiton Management
  void customerNotificationDetail({
    required CustomerListRequest? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url: UrlConstants.customer_notification_management, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  ///Notification Status Change

  void notificationStatusChange({
    required int? custId,
    required bool notificationStatus,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.customer_notification_status_change}?custId=$custId&notificationStatus=$notificationStatus")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get partner Detail
  void getPartnerDetail({
    required int partnerId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.get_partner_detail}/$partnerId",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get Offer Price With Tax
  void getOfferPriceWithTax({
    required int? planId,
    required double? discount,
    required String? planGroupId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url:
          "${UrlConstants.getOfferPriceWithTax}/?planIds=$planId&discount=$discount&planGroupId=$planGroupId",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get partner Detail
  void getCustomerDocumentApproveRejected({
    required int? documentId,
    required bool? isApproveRequest,
    required String? remarks,
    required CustomerDocApproveRejectReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.approve_upload_customer_doc}?docId=$documentId&remarks=$remarks&isApproveRequest=$isApproveRequest",
            data: request)
        .putRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// close caf reject reason
  void getRejectReasonCaf({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.rejectReasonCaf).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  ///  customer CAF P
  void approveCustomerCAF({
    required int? custCafID,
    required int? nextStaffID,
    required String? approveFlag,
    required String? remark,
    required int? staffID,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    Map<String, dynamic> request = {
      "custcafId": custCafID,
      "nextStaffId": nextStaffID,
      "flag": approveFlag,
      "remark": remark,
      "staffId": staffID,
    };
    ApiRequest(url: UrlConstants.approveCustomerCAF, data: request).putRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  ///  customer reactivate Service
  void reactivateService({
    required int? custID,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.reactivateService}?custId=$custID")
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  ///  customer document pending
  void customerDocumentPending({
    required int? custID,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.isCustomerDocPending}/$custID").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void getAllTeamListRequest({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.get_all_team_list).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }
// customer Invoice Details

  void getCustomerInvoiceDetail({
    required int customerId,
    required int id,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.invoiceDetails}/$id/$customerId",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get find Service AreaByBranchId
  void getFindServiceAreaByBranchId({
    required int? serviceAreaId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.getFindServiceAreaByBranchId}?BranchId=${serviceAreaId ?? "NaN"}")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get parent customer list
  void getPaymentCustomerOwnerList({
    required String type,
    PageRequest? pageRequest,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    //Postpaid, Prepaid
    String url = UrlConstants.payment_owner_list;
    /* if (isSearch) {
      url = UrlConstants.customer_search;
    }*/
    ApiRequest(url: "$url?product=$type", data: pageRequest).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) {
        if (onError != null) {
          onError(error);
        }
      },
    );
  }

// Customer Invoice Payment Link
  void customerInvoicePaymentLink({
    required int customerId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.customerInvoicePaymentLink}/$customerId",
    ).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// Customer RenewPayment Link
  void customerRenewPaymentLink({
    required int customerId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.customerGeneratePaymentLinkForRenew}/$customerId",
    ).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void addNoteApi({
    required AddNotesReq addNotesReq,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.add_notes, data: addNotesReq).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get customer Feasibility
  void getCustomerFeasibility({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.caf_feasibility).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get All Change Type List
  void getAllChangeTypeList({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.changePlan_Type).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// get All Change Type List
  void getBillingCycleList({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.changePlan_BillingCycle).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// Future<String?> createAndSavePdf(List<Map<String, dynamic>> data) async {
//   final pdf = pw.Document();
//
//   if (data.isEmpty) return null ;
//
//   // Step 1: Extract inner maps from 'customerNotesList'
//
//
//   // Step 1: Cast responseModel.result to Map<String, dynamic>
//   final Map<String, dynamic> resultMap = Map<String, dynamic>.from(data);
//
// // Step 2: Extract the list from 'customerNotesList'
//   final List<dynamic> rawList = resultMap['customerNotesList'] ?? [];
//
// // Step 3: Convert each item to Map<String, dynamic>
//   final List<Map<String, dynamic>> noteList = rawList
//       .map((e) => Map<String, dynamic>.from(e as Map))
//       .toList();
//
//   // Build PDF table
//   pdf.addPage(
//     pw.Page(
//       build: (context) {
//         return pw.TableHelper.fromTextArray(
//           border: pw.TableBorder.all(),
//           headers: headers,
//           data: noteList
//               .map((row) => headers.map((key) => row[key].toString()).toList())
//               .toList(),
//         );
//       },
//     ),
//   );
//
//   // Get platform-specific directory
//   Directory dir;
//
//   if (Platform.isAndroid) {
//     if (await Permission.storage.request().isGranted) {
//       dir = (await getExternalStorageDirectory())!;
//     } else {
//       print("Permission denied");
//       return null;
//     }
//   } else {
//     // iOS - Documents directory
//     dir = await getApplicationDocumentsDirectory();
//   }
//
//   // Save the file
//   final file = File('${dir.path}/api_data_${DateTime.now().millisecondsSinceEpoch}.pdf');
//   await file.writeAsBytes(await pdf.save());
//
//   print('PDF saved: ${file.path}');
//   return file.path;
//
// }



Future<String?> createAndSaveExcelSameLayout(
    List<Map<String, dynamic>> data,
    String? customerName,
    String? acctNo,
    String? serviceArea ,
    String? acctStatus ,
    String? areaName,
    String? OLTName,
    ) async {

  if (data.isEmpty) return null;

  final Map<String, dynamic> resultMap = data.first;
  final List<dynamic> rawList = resultMap['customerNotesList'] ?? [];

  final List<Map<String, dynamic>> noteList =
  rawList.map((e) => Map<String, dynamic>.from(e)).toList();

  if (noteList.isEmpty) return null;

  // Fields to show in Excel
  final List<String> fields = ['notes', 'createdBy', 'createdOn'];

  final Map<String, String> titles = {
    'notes': 'Notes',
    'createdBy': 'Created By',
    'createdOn': 'Created Date & Time',
  };

  // CREATE EXCEL
  final Workbook workbook = Workbook();
  final Worksheet sheet = workbook.worksheets[0];

  int row = 1;

  // ------------------------------- //
  //  TOP HEADER SECTION LIKE PDF   //
  // ------------------------------- //

  // Customer Name (Left)
  sheet.getRangeByName("A1:D1").merge();
  final headerLeft = sheet.getRangeByName("A1");
  headerLeft.setText("Customer Name: $customerName");
  headerLeft.cellStyle.backColor = '#FFFFFF';   // plain background
  headerLeft.cellStyle.hAlign = HAlignType.left;
  headerLeft.cellStyle.borders.all.lineStyle = LineStyle.none;

// Customer Notes (Center)
  sheet.getRangeByName("E1:F1").merge();
  final headerCenter = sheet.getRangeByName("E1");
  headerCenter.setText("Customer Notes");
  headerCenter.cellStyle.backColor = '#FFFFFF';   // plain background
  headerCenter.cellStyle.hAlign = HAlignType.center;
  headerCenter.cellStyle.borders.all.lineStyle = LineStyle.none;

// Account Number (Right)
  sheet.getRangeByName("G1:J1").merge();
  final headerRight = sheet.getRangeByName("G1");
  headerRight.cellStyle.backColor = '#FFFFFF';   // plain background
  headerRight.setText("Acc No: $acctNo");
  headerRight.cellStyle.hAlign = HAlignType.right;
  headerRight.cellStyle.borders.all.lineStyle = LineStyle.none;


  // Service Area (Left)
  sheet.getRangeByName("A2:C2").merge();
  final headerLeftSecondRow = sheet.getRangeByName("A2");
  headerLeftSecondRow.setText("Service Area: $serviceArea");
  headerLeftSecondRow.cellStyle.hAlign = HAlignType.left;
  headerLeftSecondRow.cellStyle.borders.all.lineStyle = LineStyle.none;

  //Account Status
  sheet.getRangeByName("D2:E2").merge();
  final statusCell = sheet.getRangeByName("D2");
  statusCell.setText("Account Status: $acctStatus");
  statusCell.cellStyle.hAlign = HAlignType.center;
  statusCell.cellStyle.borders.all.lineStyle = LineStyle.none;

  // FAT (Center)
  sheet.getRangeByName("F2:G2").merge();
  final headerCenterSecondRow = sheet.getRangeByName("F2");
  headerCenterSecondRow.setText("FAT: $areaName");
  headerCenterSecondRow.cellStyle.hAlign = HAlignType.center;
  headerCenterSecondRow.cellStyle.borders.all.lineStyle = LineStyle.none;

  // OLT (Right)
  sheet.getRangeByName("H2:J2").merge();
  final headerRightSecondRow = sheet.getRangeByName("H2");
  headerRightSecondRow.setText("OLT: $OLTName");
  headerRightSecondRow.cellStyle.hAlign = HAlignType.right;
  headerRightSecondRow.cellStyle.borders.all.lineStyle = LineStyle.none;

  row += 4;

  // ------------------------------- //
  //         TABLE HEADERS          //
  // ------------------------------- //

  int colIndex = 1;
  for (var key in fields) {
    final cell = sheet.getRangeByIndex(row, colIndex);
    cell.setText(titles[key]);
    cell.cellStyle.hAlign = HAlignType.center;
    cell.cellStyle.borders.all.lineStyle = LineStyle.none;
    colIndex++;
  }

  row++;

  // ------------------------------- //
  //            TABLE ROWS           //
  // ------------------------------- //

  for (var item in noteList) {
    int c = 1;

    for (var key in fields) {
      var value = item[key];

      if (key == 'createdOn' && value != null) {
        try {
          final dt = DateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(value.toString());
          value = DateFormat("dd-MM-yyyy hh:mm a").format(dt);
        } catch (_) {}
      }

      final cell = sheet.getRangeByIndex(row, c);
      cell.setText(value?.toString() ?? "");
      cell.cellStyle.borders.all.lineStyle = LineStyle.none;
      cell.cellStyle.hAlign = HAlignType.center;
      c++;
    }

    row++;
  }

  // Auto-fit columns
  for (int i = 1; i <= fields.length; i++) {
    sheet.autoFitColumn(i);
  }

  // SAVE FILE
  Directory dir;
  if (Platform.isIOS) {
    dir = await getApplicationDocumentsDirectory();
  } else {
    dir = Directory("/storage/emulated/0/Download");
  }

  final file = File(
    '${dir.path}/customer_notes_${DateTime.now().millisecondsSinceEpoch}.xlsx',
  );

  await file.writeAsBytes(workbook.saveAsStream());
  workbook.dispose();

  return file.path;
}


  Future<String?> createAndSavePdf(List<Map<String, dynamic>> data,
      String? customerName, String? acctNo,String? serviceArea , String? acctStatus , String? areaName, String? OLTName,) async {

    final pdf = pw.Document();

    if (data.isEmpty) return null;

    // Step 1: Extract result from first item in data
    final Map<String, dynamic> resultMap = data.first;

    // Step 2: Extract 'customerNotesList' from resultMap
    final List<dynamic> rawList = resultMap['customerNotesList'] ?? [];

    // Step 3: Convert each item in list to Map<String, dynamic>
    final List<Map<String, dynamic>> noteList =
        rawList.map((e) => Map<String, dynamic>.from(e as Map)).toList();

    if (noteList.isEmpty) return null;

    // Step 4: Choose which fields to display (keys from the noteList maps)
    final List<String> fieldsToDisplay = [
      'notes',
      'createdBy',
      'createdOn'
    ]; // Replace with actual keys

    // Step 5: Define custom headers for the selected fields
    final Map<String, String> customHeaders = {
      'notes': 'Notes',
      'createdBy': 'Created By',
      'createdOn': 'Created Date & Time',
    };

    // Step 4: Extract headers
    final headers =
        fieldsToDisplay.map((key) => customHeaders[key] ?? key).toList();

    final DateFormat dateFormat = DateFormat('dd-MM-yyyy');

    final dataRows = noteList.map((row) {
      return fieldsToDisplay.map((key) {
        final value = row[key];

        if (key == 'createdOn' && value != null) {
          try {
            final parsedDate = DateFormat(Constant.DATE_TIME_FORMAT_API)
                .parse(value.toString()); //DateTime.tryParse(value.toString());
            return parsedDate != null
                ? DateFormat(
                        "${Constant.DATE_FORMAT} ${Constant.APP_TIME_FORMAT}")
                    .format(parsedDate)
                : value.toString();
          } catch (e) {
            return value.toString();
          }
        }
        return value?.toString() ?? '';
      }).toList();
    }).toList();

    pdf.addPage(
      pw.Page(
        build: (context) {
          return pw.Column(
            crossAxisAlignment: pw.CrossAxisAlignment.stretch,
            children: [
              // Row: Customer Name (left), Title (center), Account Number (right)
              pw.Row(
                mainAxisAlignment: pw.MainAxisAlignment.spaceBetween,
                children: [
                  pw.Text(
                    'Customer Name: $customerName',
                    style: pw.TextStyle(fontSize: 12),
                  ),
                  pw.Expanded(
                    child: pw.Center(
                      child: pw.Text(
                        'Customer Notes',
                        style: pw.TextStyle(
                          fontSize: 12,
                          fontWeight: pw.FontWeight.bold,
                        ),
                      ),
                    ),
                  ),
                  pw.Text(
                    'Acc No: $acctNo',
                    style: pw.TextStyle(fontSize: 12),
                  ),
                ],
              ),
              pw.SizedBox(height: 20),

              // ----------------- SECOND HEADER ROW -----------------
              pw.Row(
                mainAxisAlignment: pw.MainAxisAlignment.spaceBetween,
                children: [
                  pw.Text(
                    'Service Area: $serviceArea',
                    style: pw.TextStyle(fontSize: 12),
                  ),
                  pw.Text(
                    'Account Status: $acctStatus',
                    style: pw.TextStyle(fontSize: 12),
                  ),
                  pw.Text(
                    'FAT: $areaName',
                    style: pw.TextStyle(fontSize: 12),
                  ),
                  pw.Text(
                    'OLT: $OLTName',
                    style: pw.TextStyle(fontSize: 12),
                  ),
                ],
              ),

              pw.SizedBox(height: 20),

              // Table section
              pw.TableHelper.fromTextArray(
                border: pw.TableBorder.all(),
                headers: headers,
                data: dataRows,
                headerAlignment: pw.Alignment.centerLeft,
                cellAlignment: pw.Alignment.centerLeft,
              ),
            ],
          );
        },
      ),
    );

    Directory directory;
    if (Platform.isIOS) {
      directory = await getApplicationDocumentsDirectory();

      final files = directory.listSync(recursive: false);
      for (final entity in files) {
        if (entity is File &&
            (entity.path.endsWith('.bak') ||
                entity.path.endsWith('.gs') ||
                entity.path.endsWith('.tmp'))) {
          await entity.delete();
        }
      }
    } else {
      directory = Directory("/storage/emulated/0/Download");
    }

    //final file = File("${dir.path}/$filename");
    var file = File(
        '${directory.path}/customer_notes_${DateTime.now().millisecondsSinceEpoch}.pdf');
    await file.writeAsBytes(await pdf.save());

    print('PDF saved: ${file.path}');
    return file.path;
  }

  void getSystemConfiguration({
    required String type,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.system_configuration}getConfigurationByName?name=$type")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }
}
