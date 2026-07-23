import 'package:savbill/pages/ticket_system/model/response/tat_ticket_list_res.dart';
import 'package:savbill/util/constant.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

import '../../dashboard/model/response/show_tat_details_res.dart';

class TatMappingController extends GetxController {
  bool isLoading = false,
      changeData = false;

  GetStorage getStorage = GetStorage();
  List<TatMatrixMappings>? tatMatrixMappings = [];

  TatTicketDetail? tatTicketDetail;

  ShowTATDetailsData? tatDetailsData;


  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.TAT_DETAIL] != null) {
        tatTicketDetail = arguments[Constant.TAT_DETAIL];
        if (tatTicketDetail != null &&
            tatTicketDetail!.tatMatrixMappings != null &&
            tatTicketDetail!.tatMatrixMappings!.isNotEmpty) {
          tatMatrixMappings!.addAll(tatTicketDetail!.tatMatrixMappings!);
        }
      }


      if(arguments[Constant.TAT_NAME_DETAIL] != null){
        tatDetailsData = arguments[Constant.TAT_NAME_DETAIL];
        if (tatDetailsData != null &&
            tatDetailsData!.tatMatrixMappings != null &&
            tatDetailsData!.tatMatrixMappings!.isNotEmpty) {
          tatMatrixMappings!.addAll(tatDetailsData!.tatMatrixMappings!);
        }
      }
    }
    update();
  }

}
