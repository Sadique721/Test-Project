import 'dart:convert';

import 'package:dio/dio.dart';
import 'package:savbill/pages/customer/model/request/custmer_list_request.dart';
import 'package:savbill/pages/customer_caf/followup/caf_follow_up/model/reschedule_follow_up_req.dart';
import 'package:savbill/pages/lead_management/model/lead_approve_reject_req.dart';
import 'package:savbill/pages/lead_management/model/lead_save_req.dart';
import 'package:savbill/pages/lead_management/model/request/cms_lead_ofline_doc_upload_req.dart';
import 'package:savbill/pages/lead_management/view_lead_controller.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/pages/ticket_system/model/request/add_edit_problem_domain_req.dart';
import 'package:savbill/pages/ticket_system/model/request/add_edit_root_cause_req.dart';
import 'package:savbill/pages/ticket_system/model/request/add_edit_tat_ticket_req.dart';
import 'package:savbill/pages/ticket_system/model/request/sub_problem_domain_req.dart';
import 'package:savbill/pages/ticket_system/model/response/problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/model/response/root_cause_list_res.dart';
import 'package:savbill/pages/ticket_system/model/response/sub_problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/model/response/tat_ticket_list_res.dart';
import 'package:savbill/webservices/api_request.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:dio/dio.dart' as multi;

class LeadSystemProvider {
  // view lead management list

  void viewLeadManagementList({
    PageRequest? requestNormal,
    // CustomerListRequest? requestSearch,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url =
        "${UrlConstants.lead_master_list}/all?page=${requestNormal!.page}&pageSize=${requestNormal.pageSize}";
    ApiRequest(url: url, data: requestNormal).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void viewLeadSearchList({
    PageRequest? requestNormal,
    CustomerNewListRequest? requestSearch,
    required bool isSearch,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = "${UrlConstants.lead_master_list}/search";
    ApiRequest(url: url, data: requestSearch).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // void viewLeadSearchStatusList({
  //   PageRequest? requestNormal,
  //   CustomerNewListRequest? requestSearch,
  //   String? convertedDateFrom,
  //   String? convertedDateTo,
  //   required bool isSearch,
  //   Function()? beforeSend,
  //   Function(ResponseModel responseModel)? onSuccess,
  //   Function(ResponseModel error)? onError,
  // }) {
  //   String url = "${UrlConstants.lead_master_list}/search?fromConvertedDate=$convertedDateFrom&toConvertedDate=$convertedDateTo";
  //   ApiRequest(url: url, data: requestSearch).postRequest(
  //     beforeSend: () => {if (beforeSend != null) beforeSend()},
  //     onSuccess: (data) {
  //       onSuccess!(data);
  //     },
  //     onError: (error) => {if (onError != null) onError(error)},
  //   );
  // }

  void viewLeadSearchStatusList({
    PageRequest? requestNormal,
    CustomerNewListRequest? requestSearch,
    required String url,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: url, data: requestSearch).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void addNotesLeadMaster({
    required int? leadMasterId,
    required String? notes,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    Map<String, dynamic> request = {
      "id": 0,
      "leadMasterId": leadMasterId,
      "notes": notes,
    };
    String url = UrlConstants.lead_add_notes;

    ApiRequest(url: url, data: request).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void allLeadRejectedReason({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.leadAllRejectedReasonList).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // approve reject ticket
  void approveRejectLeads({
    required LeadApproveRejectReq? request,
    Function()? beforeSend,
    Function(dynamic responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.lead_approve_reject, data: request).putRequest_custom(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        print("ResponseModel Data ==> ${jsonEncode(data)}");
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  ///F
  void assignLeadStaff({
    required String url,
    required LeadApproveRejectReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: url, data: request).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  /// All Rejected Reason Lead List

  void allRejectedReasonLead({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.allRejectedReasonLead).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // Save Close Lead

  void saveCloseLead({
    required int? leadMasterId,
    required int? rejectReasonId,
    required int? rejectSubReasonId,
    required String? remark,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    Map<String, dynamic> request = {
      "leadMasterId": leadMasterId,
      "rejectReasonId": rejectReasonId,
      "rejectSubReasonId": rejectSubReasonId,
      "remark": remark,
    };
    String url = UrlConstants.saveCloseLead;
    ApiRequest(url: url, data: request).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // ReOpen Lead

  void reOpenLead({
    Function()? beforeSend,
    required int? leadId,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.reOpenLead}/$leadId").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // Reassign Lead

  void getReassignLead({
    Function()? beforeSend,
    required int? leadId,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.reassignLead}?leadMasterId=$leadId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // Reassign Lead

  void updateLeadReassign({
    Function()? beforeSend,
    required int? leadMasterId,
    required String? status,
    required String? remark,
    required int? assignee,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    Map<String, dynamic> request = {
      "leadMasterId": leadMasterId,
      "status": status,
      "remark": remark,
      "remarkType": "LeadChangeAssignee",
      "assignee": assignee,
    };
    ApiRequest(url: UrlConstants.updateLeadAssignee, data: request).putRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // lead status

  void getLeadDetailsById({
    required int? eventId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.leadDetailById}?leadId=$eventId",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get Lead Service Area
  void getLeadServiceArea({
    required int? serviceAreaId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.leadServiceArea}/$serviceAreaId",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get Lead Status

  void getLeadStatusProgress({
    required int? buId,
    required int? mvNoId,
    required int? nextTeamMappingId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url:
          "${UrlConstants.leadStatusProgress}?buId=$buId&mvnoId=$mvNoId&nextTeamHierarchyMappingId=$nextTeamMappingId",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get all lead audit list

  void getAllLeadAudit({
    required int? id,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.allLeadAudit}/$id",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get all lead Notes list
  void getAllLeadNotes({
    required int? id,
    required PageRequest pageRequest,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url:
          "${UrlConstants.allLeadNotes}/$id?page=${pageRequest.page}&pageSize=${pageRequest.pageSize}",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // generate Lead No.
  void generateLeadNo({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.generateLeadNo,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get all lead Notes list
  void getRequireServiceType({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.requiredServiceType,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get Lead Type
  void getLeadTypeCall({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.leadType,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get Lead Origin Type
  void leadOriginTypes({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.lead_origin_types,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get Lead Source type
  void leadSourceType({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.lead_source_list,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get Lead Feasibility
  void leadFeasibility({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.lead_feasibility_list,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get Lead Customer Gender Types
  void leadCustomerGenderType({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.leadCustomerGenderType,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get Lead Service Type Types
  void leadServiceType({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.leadServiceType,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get customer list
  void getExistingCustomerList({
    required String type,
    required bool isSearch,
    CustomerListRequest? customerListRequest,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    //Postpaid, Prepaid
    String url = UrlConstants.customer_search;
    if (isSearch) {
      url = UrlConstants.customer_search;
    }
    ApiRequest(url: "$url/$type", data: customerListRequest).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get lead source partner list
  void getLeadSourcePartner({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.getLeadSourcePartnerList).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get lead source Staff Users list
  void getLeadSourceStaffUsers({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.getLeadSourceStaffUsersList).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get lead source service Area list
  void getLeadSourceServiceArea({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.getLeadSourceServiceAreaList).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get lead source Branch list
  void getLeadSourceBranch({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.getLeadSourceBranchList).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get lead source customer list
  void getLeadSourceCustomer({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.getLeadSourceCustomerList).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// lead create

  void saveCreateLead({
    required LeadSaveReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.saveLead, data: request).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // lead update

  void updateCreateLead({
    required LeadSaveReq? request,
    required int? leadId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.updateLead}/$leadId", data: request)
        .putRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // check existing lead with name

  void checkLeadExistByName({
    required String username,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.checkExistingLeadByName}/$username")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void leadToCAFConvertCustomer({
    required LeadSaveReq request,
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

  void leadFollowUpAllList({
    required int? leadFollowUpId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.leadFollowUpAllList}/$leadFollowUpId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void generateNameLeadFollowUp({
    required int? leadFollowUpId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.generateNameFollowUP}/$leadFollowUpId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void leadFollowUpSave({
    required dynamic followUpId,
    required String? leadFollowUpName,
    required String? followUpDatetime,
    required String? remarks,
    required bool? isMissedCall,
    required int? leadMasterId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    Map<String, dynamic> request = {
      "id": followUpId,
      "followUpName": leadFollowUpName,
      "followUpDatetime": followUpDatetime,
      "remarks": remarks,
      "isMissed": isMissedCall,
      "leadMasterId": leadMasterId,
    };
    ApiRequest(url: UrlConstants.leadFollowUpSave, data: request).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void reScheduleFollowUpRemarks({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.leadReScheduleFollowUpRemarks).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void leadReScheduleFollowUpData({
    required dynamic followUpId,
    required String? leadFollowUpName,
    required String? followUpDatetime,
    required String? remarks,
    required String? selectedReasonReschedule,
    required bool? isMissedCall,
    required int? leadMasterId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    Map<String, dynamic> request = {
      "id": "",
      "followUpName": leadFollowUpName,
      "followUpDatetime": followUpDatetime,
      "remarks": selectedReasonReschedule,
      "isMissed": isMissedCall,
      "leadMasterId": leadMasterId,
      "remarksTemp": remarks,
    };
    ApiRequest(
            url:
                "${UrlConstants.leadReSchedulefollowUp}?followUpId=$followUpId&remarks=$remarks",
            data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void leadCloseFollowUp({
    required int? followUpId,
    required String? remark,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.leadCloseFollowUp}?followUpId=$followUpId&remarks=$remark")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void getLeadFollowUpRemark({
    required int? followUpId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.leadFollowUpRemark}/$followUpId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void saveLeadFollowUpRemark({
    required int? followUpId,
    required String? followUpRemark,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    Map<String, dynamic> request = {
      "leadFollowUpId": followUpId,
      "remark": followUpRemark,
    };
    ApiRequest(url: UrlConstants.leadFollowUpSaveRemark, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void ticketReScheduleFollowUpData({
    required int? ticketFollowUpId,
    required String? leadFollowUpName,
    required String? followUpDatetime,
    required String? remarks,
    required String? caseNumber,
    required String? status,
    required String? selectedReasonReschedule,
    required bool? isMissedCall,
    required int? mvNoId,
    required bool? isSend,
    required int? caseID,
    required int? staffUserId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    Map<String, dynamic> request = {
      "id": null,
      "followUpName": leadFollowUpName,
      "followUpDatetime": followUpDatetime,
      "remarks": selectedReasonReschedule,
      "isMissed": isMissedCall,
      "caseId": caseID,
      "caseNumber": caseNumber,
      "staffUserId": staffUserId,
      "mvnoId": mvNoId,
      "isSend": isSend,
      "status": status,
      "remarksTemp": remarks,
    };
    ApiRequest(
            url:
                "${UrlConstants.reScheduleTicketFollowUp}?followUpId=$ticketFollowUpId&remarks=$remarks",
            data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void viewLeadDocList({
    PageRequest? requestNormal,
    int? leadId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url =
        "${UrlConstants.cpmLeadDocList}/$leadId?page=${requestNormal!.page}&pageSize=${requestNormal.pageSize}";
    ApiRequest(url: url, data: requestNormal).getRequest(
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
                "${UrlConstants.customer_sub_type}custdocsubtype_${docType}_$verificationMode")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void cmsLeadUploadDocument({
    required multi.FormData formData,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url: UrlConstants.cpmLeadDocSave,
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

  void cmsLeadUploadDocumentOnline({
    required CMSLeadUploadDocumentReq? formData,
    required bool? isUpdate,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url: "${UrlConstants.cpmLeadUploadDocOnline}?isUpdate=$isUpdate",
            data: formData,
            isFormData: false)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void leadUploadDocumentDelete({
    required int? docId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.cpmLeadDocDelete}/$docId",
    ).deleteRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }
}
