import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class DashboardController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();
  int tabIndex = 0;
  final GlobalKey<ScaffoldState> dashKey = GlobalKey();
  String? from;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.FROM] != null) {
        from = arguments[Constant.FROM];
      }
    }
    update();
    if (from != null && from!.isNotEmpty) {
      if (from!.equalsIgnoreCase(Strings.payment_system)) {
        tabIndex = 1;
        update();
      }
    }
  }



}
