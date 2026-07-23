import 'package:savbill/pages/customer/model/request/assign_inventory_req.dart';
import 'package:savbill/pages/customer/model/request/other_inventory_assign_req.dart';
import 'package:savbill/pages/customer_inventory/request/inventory_list_req.dart';
import 'package:savbill/pages/customer_inventory/request/replace_inventory_customer_req.dart';
import 'package:savbill/pages/customer_inventory/request/upload_doc_inventory_req.dart';
import 'package:savbill/pages/inventory/module/request/assign_non_serialize_item_req.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/webservices/api_request.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:dio/dio.dart' as multi;
import 'request/assign_inventory_end_owner.dart';

class InventoryProvider {
  // get customer inventory
  void getCustomerInventoryList({
    required InventoryListReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.customer_inventory, data: request).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void getInventoryDocumentViewCall({
    required int? inventoryId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.inventory_doc_view_list}/$inventoryId").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get inventory Item Id
  void getCustomerInventoryItemId({
    int? itemId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.inventoryItemDeleteId}${itemId}",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get inventory Item Delete
  void getCustomerInventoryItemDelete({
    int? macMappingId,
    int? customerInventoryId,
    int? customerId,
    bool? isApprove,
    int? nextStaffId,
    String? remark,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.inventoryItemDelete}?&macMappingId=$macMappingId&customerInventoryId=$customerInventoryId&customerId=$customerId&isApprove=$isApprove&nextstaff=$nextStaffId&remark=$remark")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get inventory remove
  void getRemoveInventoryById({
    int? itemId,
    int? customerInventoryId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.removeInventoryById}?&itemId=$itemId&custinventoryid=$customerInventoryId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get generate Remove Inventory Request
  void generateRemoveInventoryRequestCall({
    int? macMappingId,
    int? customerInventoryId,
    int? customerId,
    bool? isFlag,
    String? revisedCharge,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.generateRemoveInventoryRequest}?&macMappingId=$macMappingId&customerInventoryId=$customerInventoryId&customerId=$customerId&isflag=$isFlag&revisedcharge=$revisedCharge")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get temWorkFlow approval
  void getTeamWorkFlowProgress({
    required int? eventId,
    required String? eventName, //CUSTOMER_INVENTORY_ASSIGN
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url:
          "${UrlConstants.getTeamWorkApprovalProgressInventory}?entityId=$eventId&eventName=$eventName",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get temWorkFlow approval
  void getCustomerTeamWorkFlowProgress({
    required int? eventId,
    required String? eventName, //CUSTOMER_INVENTORY_ASSIGN
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url:
          "${UrlConstants.getCustomerTeamWorkApprovalProgressInventory}?entityId=$eventId&eventName=$eventName",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // approveReplaceInventory

  void customerApproveReplaceInventory({
    required List<ReplaceInventoryReq>? request,
    required bool? isApproveRequest,
    required bool? isbillAble,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.customerApproveReplaceInventory}?isApproveRequest=$isApproveRequest&billAble=$isbillAble",
            data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // workFlow Audit chart
  void inventoryWorkFlowAudit({
    required PageRequest? request,
    required String? eventName,
    int? eventId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.workFlowAuditInventroyProcess}?entityId=$eventId&eventName=$eventName",
            data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // workFlow Audit chart
  void customerInventoryWorkFlowAudit({
    required PageRequest? request,
    required String? eventName,
    int? eventId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.customerWorkFlowAuditInventory}?entityId=$eventId&eventName=$eventName",
            data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // productByMacSerial Number
  void productByMacSerialNumber({
    int? macMappingId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.productByMacSerial}?macMappingId=$macMappingId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // productByMacSerial Number
  void replacementProductMacAddress({
    int? productId,
    int? itemId,
    int? ownerId,
    String? replacement,
    required PageRequest? pagerRequest,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url:
                "${UrlConstants.replacementMacAddressIds}?productId=$productId&itemId=$itemId&ownerId=$ownerId&ownerShipType=Staff&replacementReason=$replacement",data: pagerRequest)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void assignInventoryToEndOwner({
    required AssignInventoryEndOwnerReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url: UrlConstants.get_assign_inventory_to_end_owner, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void replaceInventoryCustomerInventory({
    required int? customerId,
    required String? inventoryType,
    required String? replacementReason,
    required String? approvalRemark,
    required List<ReplaceInventoryReq>? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url: "${UrlConstants.replaceInventoryCustomer}"
                "?customerId=$customerId"
                "&inventoryType=${inventoryType?.trim().replaceAll(" ", "%20")}"
                "&replacementReason=${replacementReason?.trim().replaceAll(" ", "%20")}"
                "&approvalRemark=${approvalRemark?.trim().replaceAll(" ", "%20")}",
            data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  /// assignNonSerializedItemToCustomer

  void assignNonSerializedItemToCustomerApi({
    required OtherInventoryAssignReq? request,
    required String url,
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

  /// assignNonSerializedItemToEndOwner

  void assignNonSerializedItemToEndOwnerApi({
    required AssignNonSerializedItemReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
            url: UrlConstants.assignNonSerializedItemToEndOwner, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // for inventory Document Upload
  void inventoryUploadDocument({
    required int customerId,
    // required InventoryFileUploadRequest? request,
    required multi.FormData formData,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: UrlConstants.cust_inventory_upload_doc,
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


  // for inventory Document Download
  void inventoryDownloadDeleteDocument({
    required int? inventoryId,
    required String? fileName,
    required String? uniqueName,
    required String? sectionName,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.cust_inventory_delete_doc}$inventoryId/$fileName/$uniqueName/$sectionName/",)
        .deleteRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // for inventory Document Download
  void inventoryDownloadDocument({
    required int? customerId,
    required String? fileName,
    required String? uniqueName,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: "${UrlConstants.cust_inventory_delete_doc}$customerId/$fileName/$uniqueName",)
        .deleteRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // for inventory Document Download
  void getAllInventorySpecByItemIdCall({
    required int? itemId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.getAllInventorySpecByItemId}?itemId=$itemId",)
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  ///assignInventoryStaff

  void assignInventoryFromStaffList({
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


  ///assignEveryStaff

  void assignInventoryEveryStaff({
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



}
