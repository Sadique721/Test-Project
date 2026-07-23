import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/pages/service_management/request/add_service_req.dart';
import 'package:savbill/pages/service_management/request/cust_service_hold_request.dart';
import 'package:savbill/pages/service_management/request/cust_stop_service_in_bulk_req.dart';
import 'package:savbill/webservices/api_request.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';


class ServiceManagementProvider {
  // send payment link to customer
  void serviceNickNameUpdate({
    required int? serviceMappingId,
    required String? nickName,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url:
        "${UrlConstants.serviceNickNameUpdate}?custServiceMappingId=$serviceMappingId&name=$nickName",)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // get  plan by serviceId
  void getPlanByServiceId({
    required int serviceId,
    required String? customerType,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: "${UrlConstants.getPlanByServiceId}/$serviceId/$customerType")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // add new service
  void addNewService({
    required AddServiceReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url:
      UrlConstants.add_New_Service,data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // DEACTIVATE_REASON_EZ_BILL
  void getDeActiveReasonService({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.generic_request}DEACTIVATE_REASON_EZ_BILL?from_cache=true")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }



  // Service Termination

  void customerServiceTermination({
    Function()? beforeSend,
    required int? serviceId,
    required int? customerId,
    required int? planMappingId,
    required int? reasonId,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.customerServiceTermination}/$serviceId?custId=$customerId&planMapId=$planMappingId&reasonId=$reasonId")
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // Service Hold with Pause

  void customerServiceHold({
    Function()? beforeSend,
    required CustomerServiceHoldReq? serviceHoldReq,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.customerServiceHold,data: serviceHoldReq)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }



  // Service Hold with start

  void customerServiceWithStart({
    Function()? beforeSend,
    required CustomerServiceHoldReq? serviceHoldReq,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.customerServiceResume,data: serviceHoldReq)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // Service Approve & Reject

  void approveRejectService({
    Function()? beforeSend,
    required int? serviceMappingId,
    required String? remark,
    required bool? isApproveRequest,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.custServiceTermination}?customerServiceMappingId=$serviceMappingId&isApproveRequest=$isApproveRequest&remarks=$remark",)
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // Service Audit Status
  void customerServiceAuditStatus({
    Function()? beforeSend,
    required int? serviceId,
    required PageRequest? pageRequest,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.custServiceStatusAudit}/$serviceId",data: pageRequest)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // Service Stop InBulk

  void customerServiceStopInBulk({
    Function()? beforeSend,
    required CustStopServiceInBulkReq? request,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.custStopServiceInBulk,data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

}