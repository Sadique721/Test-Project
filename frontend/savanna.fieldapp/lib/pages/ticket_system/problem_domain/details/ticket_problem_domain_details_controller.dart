import 'dart:convert';

import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/ticket_system/model/response/problem_domain_list_res.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class TicketProblemDomainController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();

  UserDetail? userDetail;
  // int? productCategoryId;
  // String? problemDomainName;

  ProblemDomainDetail? problemDomainDetail;


  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      // if (arguments[Constant.PRODUCT_ID] != null) {
      //   productCategoryId = arguments[Constant.PRODUCT_ID];
      // }
      if (arguments[Constant.PROBLEM_DOMAIN_DETAILS] != null) {
        problemDomainDetail = arguments[Constant.PROBLEM_DOMAIN_DETAILS];
      }
    }
    update();

    initPlatformState();
  }

  Future<void> initPlatformState() async {
    String strUserData = "";
    if (getStorage.hasData(Constant.USER_DATA)) {
      strUserData = await getStorage.read(Constant.USER_DATA);
    }

    if (!strUserData.isNullOrEmpty()) {
      userDetail = UserDetail.fromJson(jsonDecode(strUserData));
      update();
    }
  }

}