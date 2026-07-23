import 'package:savbill/pages/dashboard/model/data_list_item.dart';
import 'package:savbill/routes/app_routes.dart';
import 'package:savbill/util/acl_constant.dart';
import 'package:savbill/util/permission_service.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class InventoryHomeController extends GetxController {
  List<ItemList> dataList = [];
  GetStorage getStorage = GetStorage();

  @override
  void onInit() {
    super.onInit();
    if(PermissionService().hasAclPermission([AclInventorys.MANUFACTURER]) == true) {
      dataList.add(ItemList(
          id: 1, title: Strings.manufacturer_management, icon: im_manufacture));
    }

    if(PermissionService().hasAclPermission([AclInventorys.PRODUCT_CATEGORY]) == true) {
      dataList.add(ItemList(
          id: 2,
          title: Strings.product_category_management_new,
          icon: im_product_category));
    }
    if(PermissionService().hasAclPermission([AclInventorys.INVEN_PRODUCT]) == true) {
      dataList.add(
          ItemList(
              id: 3, title: Strings.product_management, icon: im_category));
    }

    if(PermissionService().hasAclPermission([AclInventorys.POP]) == true) {
      dataList.add(
          ItemList(id: 4, title: Strings.pop_management, icon: im_pop));
    }
    if(PermissionService().hasAclPermission([AclInventorys.WAREHOUSE]) == true) {
      dataList.add(
          ItemList(
              id: 5, title: Strings.warehouse_management, icon: im_warehouse));
    }

    if(PermissionService().hasAclPermission([AclInventorys.INVEN_INWARDS]) == true) {
      dataList.add(
          ItemList(id: 6, title: Strings.inwards, icon: im_inwards));
    }
    if(PermissionService().hasAclPermission([AclInventorys.INVEN_OUTWARDS]) == true) {
      dataList.add(
          ItemList(id: 7, title: Strings.outwards, icon: im_outwards));
    }

    if(PermissionService().hasAclPermission([AclInventorys.EXT_ITEM]) == true) {
      dataList.add(
          ItemList(id: 9,
              title: Strings.external_item_management,
              icon: im_external_item_group));
    }

    if(PermissionService().hasAclPermission([AclInventorys.EXT_ITEM_BULK_CONSUMPTION]) == true) {
      dataList.add(
          ItemList(id: 10,
              title: Strings.bulk_consumption_management,
              icon: im_bulk_consumption));
    }
    // dataList.add(ItemList(id: 7, title: Strings.assigned_inventories, icon: im_assigned));
    if(PermissionService().hasAclPermission([AclInventorys.INVEN_REQUEST]) == true) {
      dataList.add(
          ItemList(id: 8, title: Strings.inventory_request, icon: im_pop));
    }

    if(PermissionService().hasAclPermission([AclInventorys.INVEN_DETAILS]) == true) {
      dataList.add(
          ItemList(id: 11, title: Strings.inventory_details, icon: im_pop));
    }
    update();
  }

  void moveToDashboard() async {
    Get.offAllNamed(AppRoutes.DASHBOARD);
  }
}
