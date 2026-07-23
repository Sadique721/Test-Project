import 'dart:convert';

import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/routes/app_routes.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class SplashController extends GetxController {
  bool isLoading = false;
  bool isInternetNotAvailable = false;
  GetStorage getStorage = GetStorage();


  getUserData() async {
    String strUserData ="";
   // String strNotificationData = "";
    if (getStorage.hasData(Constant.USER_DATA)) {
      strUserData=await getStorage.read(Constant.USER_DATA);
    }
    if (!strUserData.isNullOrEmpty()) {
      UserDetail user = UserDetail.fromJson(jsonDecode(strUserData));
      if (user != null) {
        moveToDashboard();
        // moveToTest();
      } else {
        moveToLogin();
      }
    } else {
      moveToLogin();
    }
  }

  void moveToLogin() async {
    Get.offAllNamed(AppRoutes.LOGIN);
  }

  void moveToDashboard() async {
    Get.offAllNamed(AppRoutes.DASHBOARD);
  }


}
