import 'package:savbill/pages/customer/model/request/custmer_list_request.dart';
import 'package:savbill/pages/customer/model/save_all_inventory_outward_res.dart';
import 'package:savbill/pages/inventory/module/request/add_edit_bulk_consumption_req.dart';
import 'package:savbill/pages/inventory/module/request/add_edit_category_req.dart';
import 'package:savbill/pages/inventory/module/request/add_edit_inwards.dart';
import 'package:savbill/pages/inventory/module/request/add_edit_outward_req.dart';
import 'package:savbill/pages/inventory/module/request/add_edit_pop_req.dart';
import 'package:savbill/pages/inventory/module/request/add_edit_product_management_req.dart';
import 'package:savbill/pages/inventory/module/request/add_edit_warehouse_req.dart';
import 'package:savbill/pages/inventory/module/request/add_inward_mac_map_req.dart';
import 'package:savbill/pages/inventory/module/request/add_update_manufacturer_req.dart';
import 'package:savbill/pages/inventory/module/request/category_search_req.dart';
import 'package:savbill/pages/inventory/module/request/change_inward_status_req.dart';
import 'package:savbill/pages/inventory/module/request/delete_pop_req.dart';
import 'package:savbill/pages/inventory/module/request/external_group_add_edit_req.dart';
import 'package:savbill/pages/inventory/module/request/external_lite_mac_mapping_req.dart';
import 'package:savbill/pages/inventory/module/request/inventory_ownership_status_change_req.dart';
import 'package:savbill/pages/inventory/module/request/inventory_return_req_item.dart';
import 'package:savbill/pages/inventory/module/request/item_status_req_item.dart';
import 'package:savbill/pages/inventory/module/request/save_manual_mac_serial_req.dart';
import 'package:savbill/pages/inventory/module/request/warranty_status_req_item.dart';
import 'package:savbill/pages/inventory/module/response/add_product_inventory_request.dart';
import 'package:savbill/pages/inventory/module/response/bulk_cons_approve_reject_req.dart';
import 'package:savbill/pages/inventory/module/response/category_list_res.dart';
import 'package:savbill/pages/inventory/module/response/external_group_list_res.dart';
import 'package:savbill/pages/inventory/module/response/inventory_product_list_res.dart';
import 'package:savbill/pages/inventory/module/response/inward_mac_serial_item_res.dart';
import 'package:savbill/pages/inventory/module/response/view_bulk_consumption_res.dart';
import 'package:savbill/pages/inventory/module/response/view_inwards_list_res.dart';
import 'package:savbill/pages/inventory/module/response/view_outward_list_res.dart';
import 'package:savbill/pages/inventory/module/response/view_pop_inventory_res.dart';
import 'package:savbill/pages/inventory/module/response/ware_house_list_res.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/webservices/api_request.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:dio/dio.dart' as multi;

class InventoryManagementProvider {



// get manufacture list
  void getManufactureVendor({
    required bool isSearch,
    int? pageNo,
    PageRequest? requestNormal,
    CategorySearchReq? requestSearch,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = "${UrlConstants.view_vendor_manufacturer}/getAllVendor";
    if (isSearch) {
      url =
      "${UrlConstants
          .view_vendor_manufacturer}/search?page=$pageNo&pageSize=10&sortOrder=0&sortBy=id";
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


  // delete Manufacturer items
  void deleteManufacturerItem({
    required int? vendorId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.view_vendor_delete}/$vendorId",)
        .deleteRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // add edit manufacturer item
  void addEditManufactureManagement({
    required bool isAdd,
    AddUpdateManufactureReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: isAdd
            ? UrlConstants.view_vendor_add
            : UrlConstants.view_vendor_update,
        data: request)
        .postRequest_custom(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) {onError(error)}},

    );
  }

  // get product category
  void getProductCategory({
    required bool isSearch,
    int? pageNo,
    PageRequest? requestNormal,
    CategorySearchReq? requestSearch,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = UrlConstants.view_product_category;
    if (isSearch) {
      url =
      "${UrlConstants
          .view_product_category}/search?page=$pageNo&pageSize=10&sortOrder=0&sortBy=id";
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

  // get product type
  void getCategoryType({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.generic_request}PRODUCT_TYPE",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // dtvCategory
  void getDtvCategoryList({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.dtv_category,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // add edit product category
  void addEditProductCategory({
    required bool isAdd,
    AddEditCategory? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: isAdd
            ? UrlConstants.product_category_add
            : UrlConstants.product_category_edit,
        data: request)
        .postRequest_custom(
        beforeSend: () => {if (beforeSend != null) beforeSend()},
        onSuccess: (data) {
          onSuccess!(data);
        },
        onError: (error) => {if (onError != null) {onError(error)}},

    );
  }

  // delete product category
  void deleteProductCategory({
    CategoryDetail? request,
    required int? productID,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.product_category_delete}/$productID",)
        .deleteRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// view product list data
  void viewProductList({
    required bool isSearch,
    int? pageNo,
    PageRequest? requestNormal,
    CategorySearchReq? requestSearch,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = UrlConstants.view_product;
    if (isSearch) {
      url = "${UrlConstants
          .view_product}/search?page=$pageNo&pageSize=10&sortOrder=0&sortBy=id";
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

  // delete product
  void deleteProduct({
    ProductDetail? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.product_delete}/${request!.id}").deleteRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // add edit product
  void addEditProduct({
    required bool isAdd,
    AddEditProductManagementReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: isAdd ? UrlConstants.product_add : UrlConstants.product_edit,
        data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get product charge list
  void getProductCharge({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.product_direct_charge,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get product charge list
  void getAllProductCategory({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.view_product_category_all,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // getAllActiveProductCategoriesList
  void getAllActiveProductCategories({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.getAllActiveProductCategoriesList,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get product charge list
  void getAllStaffServiceArea({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.pop_staff_service_area,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

// view product list data
  void viewPopList({
    required bool isSearch,
    int? pageNo,
    PageRequest? requestNormal,
    CategorySearchReq? requestSearch,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = UrlConstants.view_pop;
    if (isSearch) {
      url =
      "${UrlConstants
          .view_pop}/search?page=$pageNo&pageSize=10&sortOrder=0&sortBy=id";
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

// view product inventory list data
  void viewPopInventoryList({
    CustomerListRequest? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.view_pop_inventory, data: request).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // view product list data
  void viewPopDetail({
    required int? popId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.view_pop}/${popId}").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // delete pop
  void deletePop({
    DeletePopReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.pop_delete, data: request).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // add edit pop
  void addEditPop({
    required bool isAdd,
    AddEditPopReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: isAdd ? UrlConstants.pop_add : UrlConstants.pop_edit,
        data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // view ware house list data
  void viewWareHouseList({
    required bool isSearch,
    int? pageNo,
    PageRequest? requestNormal,
    CategorySearchReq? requestSearch,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = UrlConstants.view_warehouse;
    if (isSearch) {
      url =
      "${UrlConstants
          .view_warehouse}/search?page=$pageNo&pageSize=10&sortOrder=0&sortBy=id";
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

  // view ware house detail
  void viewWareHouseDetail({
    required int whId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.view_warehouse}/${whId}").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // delete ware house
  void deleteWareHouse({
    WareHouseDetail? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.warehouse_delete}/${request!.id}").deleteRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // ware house type detail
  void getWareHouseType({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.generic_request}WAREHOUSE_TYPE").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // ware house all parent service area
  void getAllParentServiceArea({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.warehouse_parent_service_area).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // ware house service area to pin-code
  void getServiceAreaToPinCode({
    required List<int> saId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.service_area_to_pincode, data: saId)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // ware house service area to pin-code
  void warehouseToParentServiceArea({
    required int wId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.warehouse_to_parent_service_area}$wId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // add edit ware house
  void addEditWareHouse({
    required bool isAdd,
    AddEditWareHouseReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: isAdd
            ? UrlConstants.warehouse_add
            : UrlConstants.warehouse_edit,
        data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // view inwards list data
  void viewInwardsList({
    required bool isSearch,
    int? pageNo,
    PageRequest? requestNormal,
    CategorySearchReq? requestSearch,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = UrlConstants.view_inwards;
    if (isSearch) {
      url =
      "${UrlConstants
          .view_inwards}/search?page=$pageNo&pageSize=10&sortOrder=0&sortBy=id";
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

  // delete inwards
  void deleteInwards({
    InwardsDetail? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.delete_inwards, data: request).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get active ware house
  void getActiveWareHouse({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.get_all_active_warehouse).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // add edit inwards
  void addEditInwards({
    required bool isAdd,
    AddEditInwards? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: isAdd ? UrlConstants.add_inwards : UrlConstants.edit_inwards,
        data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // view inwards mac map data
  void viewInwardMacMap({
    required int inwardId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.view_inwards_mac_mapping}$inwardId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // view outwards mac map data
  void viewOutwardsMacMap({
    required int outwardId,
    required int ownerId,
    required String ownerType,
    required int productId,
    required PageRequest pageRequest,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants
        .get_assign_outward_mapping_item}?outwardId=$outwardId&ownerId=$ownerId&ownerType=$ownerType&productId=$productId",data: pageRequest)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // view outwards mac map data
  void getItemForOutwardMacSerialNo({
    required int? ownerId,
    required String? ownerType,
    required int? productId,
    required PageRequest? pageRequest,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants
        .get_item_for_mac_mapping_outward}?ownerId=$ownerId&ownerType=$ownerType&productId=$productId",data: pageRequest)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // view outwards mac map data
  void viewOutwardsMacMapByInwards({
    required int inwardId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: "${UrlConstants.view_outwards_mac_mapping_by_inward}$inwardId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // delete inward mac mapping
  void deleteInwardMacMap({
    int? id,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url:
        "${UrlConstants.delete_inwards_mac_mapping}/${id}")
        .deleteRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // add inward mac mapping
  void addInwardMacMap({
    AddInwardMacMapReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.add_inwards_mac_mapping, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // save Manual Mac Serial
  void saveManualMacSerial({
    SaveManualMacSerialReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.save_manual_mac_serial, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // view outwards list data
  void viewOutwardsList({
    required bool isSearch,
    int? pageNo,
    PageRequest? requestNormal,
    CategorySearchReq? requestSearch,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = UrlConstants.view_outwards;
    if (isSearch) {
      url =
      "${UrlConstants
          .view_outwards}/search?page=$pageNo&pageSize=10&sortOrder=0&sortBy=id";
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

  // delete outwards
  void deleteOutwards({
    OutwardDetail? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.delete_outwards, data: request).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get all product list for outwards
  void getAllProductList({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.products_all).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get all ware house list for outwards
  void getAllWareHouseList({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.warehouse_all_active).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get all active staff list for outwards
  void getAllActiveStaffUser({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.staff_user_all_active).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get all partner user list for outwards
  void getAllPartnerUser({
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


  void getAllTypePartnerUser({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.get_all_type_partner).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get all partner user list for outwards
  void getAllActivePartnerUser({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.all_active_partner).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get all pop data list for outwards
  void getAllPop({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.all_pop_for_outward).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get all inwards data list for outwards
  void getInwardDetailForOutward({
    required String productId,
    required String destinationId,
    required String destinationType,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url:
        "${UrlConstants
            .inwards_for_outwards}?productId=$productId&destinationId=$destinationId&destinationType=$destinationType")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // add edit inwards
  void addEditOutwards({
    required bool isAdd,
    AddEditOutwardReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: isAdd ? UrlConstants.add_outwards : UrlConstants.edit_outwards,
        data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // add edit inwards
  void outwardMacMapReq({
    required List<InwardMacSerialDataList> request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.outward_mac_mapping, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // view assigned inventory data
  void viewAssignedInventoryList({
    required int staffId,
    required bool isSearch,
    int? pageNo,
    PageRequest? requestNormal,
    CategorySearchReq? requestSearch,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url =
        "${UrlConstants.view_inwards}/getAllAssignInventories?staffId=$staffId";
    // if (isSearch) {
    //   url =
    //   "${UrlConstants
    //       .view_inwards}/searchAssignInventories?page=$pageNo&pageSize=10&sortOrder=0&sortBy=id&staffId=$staffId";
    // }
    ApiRequest(url: url, data: isSearch ? requestSearch : requestNormal)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // view assigned inventory customer data
  void viewAssignedInventoryCustomerList({
    required int staffId,
    required bool isSerialized,
    PageRequest? requestNormal,
    int? pageNumber,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = "${UrlConstants
        .view_assigned_inventory_customer}?isGetSerializedItem=$isSerialized&staffId=$staffId";
    ApiRequest(url: url, data: requestNormal)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }




  // view assigned inventory pop data
  void viewAssignedInventoryPopList({
    required int staffId,
    required bool isSerialized,
    PageRequest? requestNormal,
    int? pageNumber,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = "${UrlConstants
        .view_assigned_inventory_pop}?isGetSerializedItem=$isSerialized&staffId=$staffId";
    ApiRequest(url: url, data: requestNormal)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // view assigned inventory service ara data
  void viewAssignedInventoryServiceAreaList({
    required int staffId,
    required bool isSerialized,
    PageRequest? requestNormal,
    int? pageNumber,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = "${UrlConstants
        .view_assigned_inventory_service_area}?isGetSerializedItem=$isSerialized&staffId=$staffId";
    ApiRequest(url: url, data: requestNormal)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // view my inventory request data
  void viewRequestInventoryList({
    PageRequest? requestNormal,
    int? pageNumber,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    Map requestData = {
      "page": pageNumber,
      "pageSize": 10,
      "sortBy": "id",
      "sortOrder": 0,
    };
    // String url = "${UrlConstants.view_request_inventory_list}?page=$pageNumber&pageSize=10&sortOrder=0&sortBy=id";
    String url = UrlConstants.view_request_inventory_list;
    ApiRequest(url: url, data: requestData)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // view all inventory data
  void viewAllInventoryList({
    int? inwardId,
    String? itemStatus,
    String? itemType,
    String? ownerId,
    String? ownerType,
    String? ownership,
    int? productId,
    String? warrantyStatus,
    String? serialNumber,
    PageRequest? requestNormal,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = "${UrlConstants.all_inventory_data}searchItems?";
    if (inwardId != null) {
      url = "${url}inwardId=$inwardId&";
    } else {
      url = "${url}inwardId=&";
    }
    if (itemStatus != null && itemStatus.isNotEmpty) {
      url = "${url}itemStatus=$itemStatus&";
    } else {
      url = "${url}itemStatus=&";
    }
    if (itemType != null && itemType.isNotEmpty) {
      url = "${url}itemType=$itemType&";
    } else {
      url = "${url}itemType=&";
    }
    if (ownerId != null && ownerId.isNotEmpty) {
      url = "${url}ownerId=$ownerId&";
    } else {
      url = "${url}ownerId=&";
    }
    if (ownerType != null && ownerType.isNotEmpty) {
      url = "${url}ownerType=$ownerType&";
    } else {
      url = "${url}ownerType=&";
    }
    if (ownership != null && ownership.isNotEmpty) {
      url = "${url}ownership=$ownership&";
    } else {
      url = "${url}ownership=&";
    }
    if (productId != null) {
      url = "${url}productId=$productId&";
    } else {
      url = "${url}productId=&";
    }
    if (warrantyStatus != null && warrantyStatus.isNotEmpty) {
      url = "${url}warrantyStatus=$warrantyStatus&";
    } else {
      url = "${url}warrantyStatus=&";
    }

    if (serialNumber != null && serialNumber.isNotEmpty) {
      url = "${url}serialNumber=$serialNumber";
    } else {
      url = "${url}serialNumber=";
    }

    ApiRequest(url: url, data: requestNormal).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // Branch Service Area Detail
  void getBranchServiceAreaDetail({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.branch_by_service_area).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // approve-rejected inwards status
  void changInwardsStatus({
    required ChangeInwardStatusReq request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.inwards_approve_reject, data: request)
        .putRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // approve-rejected inwards status
  void changeExternalGroupStatus({
    required ChangeInwardStatusReq request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.external_group_approve_reject, data: request)
        .putRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // view external group list data
  void viewExternalGroupList({
    required bool isSearch,
    int? pageNo,
    PageRequest? requestNormal,
    CategorySearchReq? requestSearch,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = UrlConstants.view_external_lite;
    if (isSearch) {
      url = "${UrlConstants
          .view_external_lite}/search?page=$pageNo&pageSize=10&sortOrder=0&sortBy=id";
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

  // delete external group
  void deleteExternalGroup({
    ExternalGroupDetail? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.delete_external_lite, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // view  external group mac map data
  void viewExternalGroupMacMap({
    required int id,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.external_lite_mac_map_view}$id").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // add external group mac mapping
  void addExternalGroupMacMap({
    ExternalLiteMacMappingReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.external_lite_mac_map_save, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // add edit external group
  void addEditExternalGroup({
    required bool isAdd,
    ExternalGroupAddEditReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: isAdd
            ? UrlConstants.add_external_lite
            : UrlConstants.edit_external_lite,
        data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // getCustomerListServiceArea
  void viewOwnerExternalList({
    required bool isSearch,
    required int? serviceAreaId,
    int? pageNo,
    PageRequest? requestNormal,
    CustomerListRequest? requestSearch,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = "${UrlConstants.external_group_customer_owner_list}?serviceAreaId=$serviceAreaId";
    if (isSearch) {
      url = UrlConstants.external_group_customer_owner_list;
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


  // getCustomerListServiceArea
  void viewPartnerExternalList({
    required bool isSearch,
    required int? serviceAreaId,
    int? pageNo,
    PageRequest? requestNormal,
    CustomerListRequest? requestSearch,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = "${UrlConstants.external_group_partner_owner_list}?serviceAreaId=$serviceAreaId";
    if (isSearch) {
      url = UrlConstants.external_group_partner_owner_list;
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


  // view bulk consumption list data
  void viewBulkConsumption({
    required bool isSearch,
    int? pageNo,
    PageRequest? requestNormal,
    CustomerListRequest? requestSearch,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = UrlConstants.view_bulk_consumption;
    if (isSearch) {
      url = UrlConstants.view_bulk_consumption_search;
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

  // view bulk consumption mac map data
  void viewBulkConsumptionMacMap({
    required int id,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.view_mapping_bulk_consumption}$id")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // add edit  bulk consumption
  void addEditBulkConsumption({
    required bool isAdd,
    AddEditBulkConsumptionReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: isAdd
            ? UrlConstants.add_bulk_consumption
            : UrlConstants.edit_bulk_consumption,
        data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // view bulk consumption inwards data
  void getBulkConsumptionInwardsList({
    required int staffId,
    required int productId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url:
        "${UrlConstants
            .bulk_consumption_inwards_data}?productId=$productId&staffId=$staffId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // delete inward mac mapping
  void popMappingdelete({
    InOutWardMACMapping? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.delete_pop_mapping + request!.id!.toString())
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // delete inward mac mapping
  void inoutwardMappingDetail({
    int? id,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.inoutwards_mapping_data_im + id!.toString())
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // view all inwards data
  void getAllInwardsList({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.all_inwards).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // view all inwards data
  void getOwnershipList({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.generic_request}OWNERSHIP_TYPE").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // view all item status data
  void getItemStatusList({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.generic_request}ITEM_STATUS_MANAGEMENT")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // view all item type data
  void getItemTypeList({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.generic_request}ITEM_CONDITION_MANAGEMENT")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // view all warranty status data
  void getWarrantyStatusList({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.generic_request}ITEM_WARRANTY_MANAGEMENT")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // return request data
  void returnItemReq({
    List<InventoryReturnReqItem>? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.return_inventory_item, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // change warranty status data
  void changeWarrantyStatusReq({
    List<WarrantyStatusReqItem>? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.change_warranty_item, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // change item status data
  void changeItemStatusReq({
    List<ItemStatusReqItem>? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.change_item_status, data: request).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // change ownership status data
  void changeOwnershipStatusReq({
    List<InventoryOwnershipStatusChangeReq>? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.change_ownership_status, data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // update item type data
  void updateItemTypeReq({
    required Map<String, dynamic> request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    multi.FormData formData = multi.FormData.fromMap(request);

    ApiRequest(
        url: UrlConstants.update_item_type,
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


  // get onBehalfoff list
  void getOnBehalfOffReq({
    required String onBehalfRequest,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants
          .on_behalf_off_requester}/onbehalfoff?onBehalfOf=$onBehalfRequest",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


//getAllWareHouses List
  void getAllWareHousesReq({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.all_ware_houses,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  //getAllActiveProductCategoriesByCB
  void getAllActiveProductCategoriesByReq({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.product_all_active_product_category,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  //getAllProductsByProductByCategory
  void getAllCategoryByProductIdReq({
    required int productCategoryId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: "${UrlConstants
            .product_all_product_category}?pc_id=$productCategoryId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }



  // save request inventory
  void saveRequestInventory({
    AddProductInventoryRequest? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url:  UrlConstants.save_request_inventory,
        data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  //  Assigned Inventory Request
  void assignedInventoryRequestList({
    PageRequest? requestNormal,
    int? pageNumber,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    Map requestData = {
      "page": pageNumber,
      "pageSize": 10,
      "sortBy": "id",
      "sortOrder": 0,
    };
    String url = UrlConstants.assigned_request_inventory_list;
    // String url = UrlConstants.assigned_request_inventory_list;
    ApiRequest(url: url, data: requestData)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  //Assigned Inventory Approve Status

  void getAssignedApproveStatusReq({
    required int? assignedId,
    required String? status,
    required String? remarks,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: "${UrlConstants
            .assigned_req_invent_approve_status}?id=$assignedId&status=$status&remarks=$remarks")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // Save Forward Inventory
  void saveForwardInventory({
    required int? reqId,
    required int? forwardToReqId,
    required String? remarks,
    // SaveForwardInvReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants
          .save_forward_req_inv}?reqId=$reqId&remarks=$remarks&forwardToReqId=$forwardToReqId",
      // data: request
    )
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // delete RequestInventory
  void deleteRequestInventory({
    required int? id,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.delete_request_inventory}?id=$id")
        .deleteRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  // ware house type detail
  void getUomInventoryData({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.generic_request}UOM_TYPE").getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void getwarrantyTimeUnit({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: "${UrlConstants.generic_request}warrantyTimeUnit",
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  void getProductManufacturerList({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.product_manufacturer_all,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void getTaxesDataList({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.inventory_taxes_all,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  /// Pop Assign Inventory Api's

  void nonSerializeProductList({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.get_all_non_serialized_product_item,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void serializeProductList({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.get_all_serialized_product_item,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void getAllTeamInventory({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.getAllTeamBasedInventory,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  /// Inward Get MacAddress & Serial Number
  ///
  // get product category
  void getMacSerialNumberItemForInward({
    required int? inwardId,
    required int? productId,
    required int? ownerId,
    required String? ownerType,
    required PageRequest? pageRequest,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = "${UrlConstants
        .get_item_for_inward}?inwardId=$inwardId&productId=$productId&ownerId=$ownerId&ownerType=$ownerType";
    ApiRequest(url: url,data: pageRequest)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  ///getAvailableQtyDetailsByProductAndDestination
  void getAvailableQtyDetailsByProductAndDestination({
    required int? productId,
    required int? ownerId,
    required String? ownerType,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = "${UrlConstants
        .getAvailableQtyDetailsByProductAndDestination}?productId=$productId&ownerId=$ownerId&ownerType=$ownerType";
    ApiRequest(url: url)
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  ///getAllProductbasedOnItemType => Serialized & NonSerialized
  ///
  void getAllProductBasedOnItemType({
    required String? itemType,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = "${UrlConstants
        .get_all_product_based_item_type}?itemtype=$itemType";
    ApiRequest(url: url)
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get all partner user list
  void getAllNewPartnerUser({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.new_all_partner).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  ///getAllSerializedItemBaseOnProduct => Serialized & NonSerialized
  ///
  void getAllSerializedItemBaseOnProduct({
    required int? productId,
    required int? ownerId,
    required String? ownerType,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    String url = "${UrlConstants
        .get_all_serialized_item_base_on_product}?productId=$productId&ownerId=$ownerId&ownerType=$ownerType";
    ApiRequest(url: url)
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }



  // approveInventoryFromOwner
  void approveInventoryFromOwner({
    required String? inventoryRemark,
    required int? inventoryMappingId,
    required bool? isApproveRequest,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants
        .approveInventoryFromOwner}?inventoryApprovalRemark=$inventoryRemark&inventoryMappingId=$inventoryMappingId&isApproveRequest=$isApproveRequest")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


   /// Bulk Consumption Approve Reject

  void bulkConsumptionApproveReject({
    BulkConsApproveRejectReq? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: UrlConstants.bulk_cons_approve_reject_status,
        data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }



  /// Bulk Consumption Delete Item

  void bulkConsumptionDeleteItem({
    BulkConsumptionDetail? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: UrlConstants.bulk_consumption_delete_item,
        data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  /// Bulk Consumption Delete Item

  void fulfilmentInventoryRequest({
    int? fulFilmentId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: "${UrlConstants.inventory_request_fulfilment_by_id}?id=$fulFilmentId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }



  // get product charge list
  void getCASPackage({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
      url: UrlConstants.get_case_package_url,
    ).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }



  void saveAllInventoryRequestOutward({
    List<SaveAllInventoryOutwardRes>? request,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: UrlConstants.saveAllInventoryRequest,
        data: request)
        .postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  void inwardsDetailsById({
    required int? inwardId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: "${UrlConstants.inwards_details}/$inwardId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void outwardsDetailsById({
    required int? outwardsId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: "${UrlConstants.outwards_details}/$outwardsId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void productCategoryById({
    int? productCatId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.view_product_category}/$productCatId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  void productDetailsById({
    int? productCatId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: "${UrlConstants.view_product}/$productCatId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void externalItemDetailsById({
    required int? externalId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: "${UrlConstants.view_external_lite}/$externalId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }



  void warehouseDetailsById({
    required int? warehouseId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: "${UrlConstants.view_warehouse}/getWarhouseView/$warehouseId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  void popDetailsById({
    required int? popId,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url: "${UrlConstants.view_pop}/$popId")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

}



