import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/customer/model/request/custmer_list_request.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/pages/task_management/model/request/add_edit_category_request.dart';
import 'package:savbill/pages/task_management/model/request/add_edit_task_sub_cat_req.dart';
import 'package:savbill/pages/task_management/model/request/add_edit_tat_task_req.dart';
import 'package:savbill/pages/task_management/model/response/task_category_management_list_res.dart';
import 'package:savbill/pages/task_management/model/response/task_sub_category_mgmt_res.dart';
import 'package:savbill/pages/task_management/model/response/tat_task_list_res.dart';
import 'package:savbill/webservices/api_request.dart';
import '../../webservices/response_model.dart';
import '../../webservices/url_constants.dart';
import 'package:dio/dio.dart' as multi;

class TaskSystemProvider {
  // view tat for task
  void viewTatForTask({
    required bool isSearch,
    PageRequest? requestNormal,
    CustomerListRequest? requestSearch,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = UrlConstants.view_tat_task_list;
    if (isSearch) {
      url = UrlConstants.view_tat_task_list_searchAll;
    }
    ApiRequest(url: url, data: isSearch ? requestSearch : requestNormal)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // for inventory Document Download
  void taskDownloadDeleteDocument({
    required int? taskId,
    required String? fileName,
    required String? uniqueName,
    required String? sectionName,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.task_doc_delete_document}$taskId/$fileName/$uniqueName/$sectionName/",)
        .deleteRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // get pick ticket
  void pickTask({
    required int caseId,
    required int staffId,
    required String remarks,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url:
        "${UrlConstants.assign_pick_task}?caseId=$caseId&remark=$remarks&staffId=$staffId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void getTaskDocumentViewCall({
    required int? taskId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.task_documentList}/$taskId").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // for the create task
  void addEditCaseTaskRequest({
    required bool isAdd,
    required multi.FormData formData,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: UrlConstants.create_task,
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

  // add edit tat for task
  void addEditTatForTicket({
    required bool isAdd,
    AddEditTatTaskReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: isAdd ? UrlConstants.add_tat_task : UrlConstants.edit_tat_task,
        data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // delete tat for task
  void deleteTatForTask({
    TatTaskListDetails? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.delete_tat_task, data: request).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // view task category list
  void viewTaskCategoryList({
    required bool isSearch,
    PageRequest? requestNormal,
    CustomerListRequest? requestSearch,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = UrlConstants.task_case_category;
    if (isSearch) {
      url = UrlConstants.task_case_category_searchAll;
    }
    ApiRequest(url: url, data: isSearch ? requestSearch : requestNormal)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }
  // delete Task Category
  void deleteTaskCategory({
    TaskCategoryMgmtDataList? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.task_case_category_delete, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // get All Active Reason Category
  void getAllActiveReasonCategory({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.task_all_active_reason_category).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get CaseCategory/all  Task Management
  void getAllCaseCategory({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.task_case_category_all).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }
  // add edit category management
  void addEditCategoryMgmt({
    required bool isAdd,
    AddEditCategoryReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: isAdd
            ? UrlConstants.task_add_case_category_save
            : UrlConstants.task_add_case_category_update,
        data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  /// Get task Search By Status
  void getSearchByStatusCall({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.task_search_status).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  /// Get task Case Category  By Id
  void getTaskCaseCategoryById({
    required int? caseCategoryId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.task_case_category_by_id}/$caseCategoryId").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // view task sub category list
  void viewTaskSubCategoryList({
    required bool isSearch,
    CustomerListRequest? requestSearch,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = UrlConstants.view_task_subCategory_list;
    if (isSearch) {
      url = UrlConstants.search_task_subCategory_list;
    }
    ApiRequest(url: url, data: isSearch ? requestSearch : requestSearch)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }




  // view task sub category list
  void addEditTaskSubCategory({
    required bool isAddEdit,
    AddEditTaskSubCategoryReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = "${UrlConstants.task_case_sub_category}/save";
    if (isAddEdit) {
      url = url = "${UrlConstants.task_case_sub_category}/update";
    }
    ApiRequest(url: url, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  /// Get task sub Category  By Id
  void getTaskCaseSubCategoryById({
    required int? caseSubCategoryId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.task_case_sub_category}/$caseSubCategoryId").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // delete Task Sub Category
  void deleteTaskSubCategory({
    TaskSubCategoryDataList? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.task_case_sub_category}/delete", data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }



  // view Task list
  void viewTaskList({
    required bool isSearch,
    PageRequest? requestNormal,
    CustomerListRequest? requestSearch,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = UrlConstants.view_task;
    if (isSearch) {
      url = "${UrlConstants.view_task}/case/search";
    }

    ApiRequest(url: url, data: isSearch ? requestSearch : requestNormal)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get link ticket
  void linkTask({
    required int caseId,
    required List<int>?linkTicketIds,
    // required int linkTicketId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url:
        "${UrlConstants.link_task}?taskId=$caseId",data: linkTicketIds)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // view Task list
  void viewTaskListDate({
    required bool isSearch,
    PageRequest? requestNormal,
    CustomerListRequest? requestSearch,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = UrlConstants.get_tickets_detail;
    if (isSearch) {
      url = "${UrlConstants.get_tickets_detail}/case/search";
    }

    ApiRequest(url: url, data: isSearch ? requestSearch : requestNormal)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // get case status
  void getTaskCaseStatusList({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.getTaskCaseStatus).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }



  // get task detail
  void getTaskDetail({
    int? id,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.view_task}/$id").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // get task detail
  void getTaskFollowupDetail({
    String? id,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.get_task_followup_detail}/$id")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // show TAT Report
  void getTaskTATReport({
    int? caseId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.getTaskTATAuditDetail}?caseId=$caseId").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // show Document
  void getTaskDocument({
    int? caseId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.task_documentList}/$caseId").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // show TAT Details
  void showTaskTATDetails({
    String? caseId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.showTaskTatDetails}?caseId=$caseId").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // show ETR Report Details
  void getTaskEtrReport({
    int? taskId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.getTaskETRReport}/$taskId",data: {}).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }




  // get task workflow detail
  void taskWorkflowDetail({
    required int id,
    PageRequest? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url:
        "${UrlConstants.task_workflow_detail}list?entityId=$id&eventName=CASE",
        data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // case all team member
  void getTaskStaffLst({
    required int taskId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.assign_task_staff_list}$taskId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }




  // Get Ticket Follow UP Remark
  void getAllTaskTeamNameByStaffId({
    required int? staffId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {

    ApiRequest(url: "${UrlConstants.getAllTaskTeamNameByStaffId}/$staffId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }



  // case assign request
  void taskCaseAssignRequest({
    required multi.FormData formData,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: UrlConstants.task_case_assign_update, formData: formData, isFormData: true)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // Get Ticket Follow UP Remark
  void getAllStaffWithoutPagination({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {

    ApiRequest(url: UrlConstants.getAllStaff)
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }



  void viewTaskResolutionReasonsTickets({
    required int? id,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.viewTaskResolutionReasonSubCategory}/$id").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // for task update document
  void taskUpdateDocument({
    required int caseId,
    required multi.FormData formData,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: UrlConstants.task_upload_document + caseId.toString(),
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


  // get All Sub Category
  void getAllTaskSubCategory({
    required int? categoryId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.task_all_sub_category}?caseCategoryId=$categoryId").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


}
