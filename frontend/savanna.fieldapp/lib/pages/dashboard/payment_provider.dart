import 'package:savbill/pages/customer_invoice/request/airtel_payment_req.dart';
import 'package:savbill/pages/customer_invoice/request/get_payment_req.dart';
import 'package:savbill/pages/customer_invoice/request/momo_pay_request.dart';
import 'package:savbill/pages/customer_invoice/request/selcom_pay_request.dart';
import 'package:savbill/pages/dashboard/model/request/get_all_case_request.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/webservices/api_request.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:dio/dio.dart' as multi;

class PaymentProvider {
  // get Payment Detail
  void getPaymentList({
    // required bool isSearch,
    required String url,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: url).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get customer list
  void getCustomerList({
    GetAllCaseRequest? getAllCaseRequest,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.customer_list, data: getAllCaseRequest)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get invoice list by customer
  void getInvoiceListOfCustomer({
    required String customerId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.customer_invoice_list + customerId).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get bank list
  void getBankListData({
    required String? bankType,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.bank_list_data}?banktype=$bankType").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get bank list
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

  // record payment request
  void recordPaymentRequest({
    required multi.FormData formData,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url: UrlConstants.record_payment,
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

  //  payment invoice data
  void paymentInvoiceData({
    required int id,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.invoice_mapping + id.toString()).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get payment team hierarchy
  void paymentTeamHierarchy({
    required int id,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.payment_team_hierarchy}getApprovalProgress?entityId=$id&eventName=PAYMENT")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get payment workflow detail
  void paymentWorkflowDetail({
    required int id,
    PageRequest? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.payment_workflow_detail}list?entityId=$id&eventName=PAYMENT",
            data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void downloadFile({
    required String fileUrl,
    required String savePath,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: fileUrl, savePath: savePath).downloadRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  void paymentConfig({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: "${UrlConstants.payment_config}?paymentGatewayFor=PAYMENT_GATEWAY_FOR_CWSC"
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // view all item status data
  void getPaymentModeSourceStatus({
    required String? paymentStatus,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.generic_request}${paymentStatus?.toLowerCase()}?from_cache=true")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }



  // Momo Pay Request Api Calll

  void requestOfMomoPay({
    Function()? beforeSend,
    required MomoPayRequest? request,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: UrlConstants.momoPayRequest,data:request
    ).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // selcom payment gateway request

  void requestBuyPlanUsingSelcom({
    Function()? beforeSend,
    required SelcomPayRequest? request,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: UrlConstants.selcomPayRequest,data:request
    ).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // Airtel payment gateway request

  void airtelPaymentRequest({
    Function()? beforeSend,
    required ArtelPaymentReq? request,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: UrlConstants.airtelPayRequest,data:request
    ).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // Airtel payment gateway request

  void paymentStatusRequest({
    Function()? beforeSend,
    required GetPaymentStatusReq? request,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: UrlConstants.paymentStatus,data:request
    ).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }
}
