
import 'package:savbill/pages/task_management/model/response/tat_task_list_res.dart';
import 'package:savbill/util/constant.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

import '../../../dashboard/model/response/show_tat_details_res.dart';

class TatTaskMappingController extends GetxController {
  bool isLoading = false,
      changeData = false;

  GetStorage getStorage = GetStorage();
  List<TatMatrixMappings>? tatMatrixMappings = [];

  TatTaskListDetails? tatTaskDetail;


  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if(arguments[Constant.TAT_TASK_DETAIL] != null){
        tatTaskDetail = arguments[Constant.TAT_TASK_DETAIL];
        if (tatTaskDetail != null &&
            tatTaskDetail!.tatMatrixMappings != null &&
            tatTaskDetail!.tatMatrixMappings!.isNotEmpty) {
          tatMatrixMappings!.addAll(tatTaskDetail!.tatMatrixMappings!);
        }
      }
    }
    update();
  }

}
