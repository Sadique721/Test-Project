import 'package:savbill/pages/dashboard/model/data_list_item.dart';
import 'package:savbill/routes/app_routes.dart';
import 'package:savbill/util/acl_constant.dart';
import 'package:savbill/util/permission_service.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class TicketSystemHomeController extends GetxController {
  List<ItemList> dataList = [];
  GetStorage getStorage = GetStorage();

  @override
  void onInit() {
    super.onInit();

    if(PermissionService().hasAclPermission([AclTicketingSystems.TAT_TICKET]) == true) {
      dataList.add(
          ItemList(id: 1, title: Strings.tat_for_ticket, icon: tat_for_ticket));
    }

    if(PermissionService().hasAclPermission([AclTicketingSystems.PROBLEM_DOMAIN]) == true) {
      dataList.add(
          ItemList(id: 2, title: Strings.problem_domain, icon: problem_domain));
    }

    if(PermissionService().hasAclPermission([AclTicketingSystems.SUB_PB_DOMAIN]) == true) {
      dataList.add(ItemList(
          id: 3, title: Strings.sub_problem_domain, icon: sub_problem_domain));
    }

    if(PermissionService().hasAclPermission([AclTicketingSystems.ROOT_CAUSE_MASTER]) == true) {
      dataList.add(ItemList(
          id: 4, title: Strings.root_cause_master, icon: root_cause_master));
    }

    if(PermissionService().hasAclPermission([AclTicketingSystems.TICKET]) == true) {
      dataList.add(ItemList(
          id: 5, title: Strings.ticket_management, icon: ticket_management));
    }

    update();
  }

  void moveToDashboard() async {
    Get.offAllNamed(AppRoutes.DASHBOARD);
  }
}
