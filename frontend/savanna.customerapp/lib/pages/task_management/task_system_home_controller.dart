
import 'package:get/get.dart';
import 'package:get/get_core/src/get_main.dart';
import 'package:get/get_state_manager/src/simple/get_controllers.dart';
import 'package:get_storage/get_storage.dart';

import '../../routes/app_routes.dart';
import '../../util/resources.dart';
import '../../util/strings.dart';
import '../dashboard/model/data_list_item.dart';
class TaskSystemHomeController extends GetxController {
  List<ItemList> dataList = [];
  GetStorage getStorage = GetStorage();

  @override
  void onInit() {
    super.onInit();
    dataList.add(
        ItemList(id: 1, title: Strings.tat_for_task, icon: tat_for_ticket));
    dataList.add(
        ItemList(id: 2, title: Strings.category_management, icon: ticket_management));
    dataList.add(ItemList(
        id: 3, title: Strings.sub_category_management, icon: ticket_management));
    dataList.add(ItemList(
        id: 4, title: Strings.task_management, icon: tat_for_ticket));
    update();
  }

  void moveToDashboard() async {
    Get.offAllNamed(AppRoutes.DASHBOARD);
  }
}
