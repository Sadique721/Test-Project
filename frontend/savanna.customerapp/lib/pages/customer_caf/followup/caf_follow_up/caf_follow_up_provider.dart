import 'package:savbill/pages/customer_caf/followup/caf_follow_up/model/reschedule_follow_up_req.dart';
import 'package:savbill/webservices/api_request.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';

class CafFollowUpProvider {
  // Get Partner Service Data
  void getCustomerCafFollowUp({
    required int customerId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.getCustomerCafFollowUp}?customerId=${customerId}")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // Get Close Follow Up
  void getCloseFollowUp({
    required int? followUpId,
    required String? remark,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.getCloseFollowUp}?followUpId=$followUpId&remarks=$remark")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // Get Close Follow Up
  void getCafFollowUpRemark({
    required int? followUpId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.getCafFollowUpRemark}/$followUpId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }



  // add Remarks Follow Up
  void addRemarkFollowUp({
    required int? cafFollowUpId,
    required String? remark,
    required int? mvnoId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {

    Map<String, dynamic> request = {
      "cafFollowUpId": cafFollowUpId,
      "remark": remark,
      "mvnoId": mvnoId,
    };

    ApiRequest(url: UrlConstants.addCafFollowUpRemark,data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // reschedule Follow up
  void rescheduleFollowUpCreate({
    required int? cafFollowUpId,
    required String? remark,
    required RescheduleFollowUpReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {

    ApiRequest(url: "${UrlConstants.rescheduleFollowUp}?followUpId=$cafFollowUpId&remarks=$remark",data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }



  // add Remarks Follow Up
  void postCloseCAF({
    required int? cafID,
    required int? rejectReasonId,
    required int? rejectSubReasonId,
    required String? remark,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {

    Map<String, dynamic> request = {
      "cafId": cafID,
      "rejectReasonId": rejectReasonId,
      "rejectSubReasonId": rejectSubReasonId,
      "remark": remark,
    };

    ApiRequest(url: UrlConstants.postCloseCaf,data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }



  // reschedule Follow up
  void generateNameOfTheCafFollowUp({
    required int? cafFollowUpId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {

    ApiRequest(url: "${UrlConstants.generateNameOfTheCafFollowUp}/$cafFollowUpId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }



  // Save Schedule Follow Up

  void scheduleCAFFollowUpSave({
    required String? leadFollowUpName,
    required String? followUpDatetime,
    required String? remark,
    required bool? isMissedCall,
    required dynamic leadMasterId,
    required int? customerId,
    required int? staffUserId,
    required int? mvNoId,
    required bool? isSend,
    required String? status,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {

    Map<String, dynamic> request = {
      "id": "",
      "followUpName": leadFollowUpName,
      "followUpDatetime": followUpDatetime,
      "remarks": remark,
      "isMissed": isMissedCall,
      "leadMasterId": leadMasterId,
      "customersId": customerId,
      "staffUserId": staffUserId,
      "mvnoId": mvNoId,
      "isSend": isSend,
      "status": status,
    };
    ApiRequest(url: UrlConstants.scheduleFollowUpSave,data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }
}
