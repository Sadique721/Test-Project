import 'package:savbill/pages/customer/model/response/services_area_res.dart';
import 'package:savbill/pages/network_management/model/response/device_list_res.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:get/get.dart';

class DeviceDetailController extends GetxController {
  bool isLoading = false;
  DeviceDetail? deviceDetail;
  String title = "", serviceAreaName = "";

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.DEVICE_DETAIL] != null) {
        deviceDetail = arguments[Constant.DEVICE_DETAIL];
        if (deviceDetail != null) {
          if (deviceDetail!.name != null && deviceDetail!.name!.isNotEmpty) {
            title = deviceDetail!.name!;
          }
          String strAreaName = "";
          if (deviceDetail!.serviceAreaNameList != null &&
              deviceDetail!.serviceAreaNameList!.isNotEmpty) {
            for (ServicesAreaDetail element
                in deviceDetail!.serviceAreaNameList!) {
              strAreaName = "$strAreaName${element.name!}, ";
            }

            if (!strAreaName.isNullOrEmpty() &&
                strAreaName.contains(",") &&
                strAreaName.length >= 2) {
              serviceAreaName =
                  strAreaName.substring(0, strAreaName.length - 2);
            }
          }
        }
      }
    }
    update();
  }
}
