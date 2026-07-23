import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/login/login_provider.dart';
import 'package:savbill/pages/login/model/request/login_request.dart';
import 'package:savbill/pages/login/model/response/demo_graphic_mapping_res.dart';
import 'package:savbill/pages/login/model/response/get_acl_entry_res.dart';
import 'package:savbill/pages/login/model/response/role_operation_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/routes/app_routes.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:package_info_plus/package_info_plus.dart';

class LoginController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();
  bool ? isVisiblePassword= false;
  TextEditingController emailController = TextEditingController();
  TextEditingController passwordController = TextEditingController();
  List<Demographicmappingtable>? demographicmappingtable = [];
  PackageInfo? packageInfo;
  String? buildVersion;
  @override
  void onInit() async{
    super.onInit();
     packageInfo = await PackageInfo.fromPlatform();
    buildVersion = packageInfo!.version;
    update();
  }

  void moveToDashboard() async {
    Get.offAllNamed(AppRoutes.DASHBOARD);
  }



  void loginApiCall() {
    isLoading = true;
    LoginRequest _loginRequest = LoginRequest(
      username: emailController.text.toString().trim(),
      password: passwordController.text,
    );
    // log("LoginRequest => "+json.encode(_loginRequest.toString()));
    update();
    LoginProvider().loginRequest(
      loginRequest: _loginRequest,
      onSuccess: (ResponseModel responseModel) {
        log("onSuccess===>>${responseModel.statusCode}");
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            UserDetail loginResponse = UserDetail.fromJson(responseModel.result);
            if (loginResponse.status == 200) {
              if (!loginResponse.accessToken!.isNullOrEmpty()) {
                getStorage.write(Constant.USER_TOKEN, loginResponse.accessToken);
              }
              if (loginResponse.userId != null) {
                loginResponse.userName = _loginRequest.username;
                getStorage.write(Constant.USER_DATA, jsonEncode(loginResponse));
                getAclEntry();
                // roleOperation();
                // moveToDashboard();
              }
            } else {
              if (loginResponse.message!.isNotEmpty) {
                Utils.showSnackbar(Strings.ERROR, loginResponse.message,
                    AppTheme.colorWhite, AppTheme.colorRed);
              }
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  void loginGeneratedApiCall() {
    isLoading = true;
    Map<String, dynamic> data = {
      "username": emailController.text.toString().trim(),
      "password": passwordController.text.trim(),
      "otpForStaff": true
    };
    // log("LoginRequest => "+json.encode(_loginRequest.toString()));
    update();
    LoginProvider().otpGenerateValidate(
      loginRequest: data,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            BaseResponse baseResponse = BaseResponse.fromJson(responseModel.result);
            if (baseResponse.status == 200) {
              if(responseModel.result['IsOTPRequired']  == false){
                loginApiCall();
              }
            } else {
              if (baseResponse.message!.isNotEmpty) {
                Utils.showSnackbar(Strings.ERROR, baseResponse.ERROR,
                    AppTheme.colorWhite, AppTheme.colorRed);
              }
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        _handleOtpGenerateApiError(error);
      },
    );
  }

  getAclEntry() {
    isLoading = true;
    update();
    LoginProvider().aclEntry(
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              GetAclEntryRes aclEntryRes = GetAclEntryRes.fromJson(responseModel.result);
              if ((aclEntryRes.responseCode != null && aclEntryRes.responseCode == 200 ) || (aclEntryRes.status != null && aclEntryRes.status == 200)) {
                if (aclEntryRes.dataList != null &&
                    aclEntryRes.dataList!.isNotEmpty) {
                  getStorage.write(Constant.ACL_ENTRIES,jsonEncode(aclEntryRes.dataList));
                  roleOperation();
                  moveToDashboard();
                }
              } else {
                if ((aclEntryRes.responseMessage != null &&
                    aclEntryRes.responseMessage!.isNotEmpty) || (aclEntryRes.msg != null && aclEntryRes.msg!.isNotEmpty) || (aclEntryRes.message != null && aclEntryRes.message !.isNotEmpty)) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      aclEntryRes.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorRed);
                }
              }
            } on Exception catch (e) {
              log("getAclEnteryException>>> $e");
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        getDemoGraphic();
      },
      onError: (ResponseModel error) {
        getDemoGraphic();
        _handleApiError(error);
      },
    );
  }


  roleOperation() {
    isLoading = true;
    update();
    LoginProvider().getRoleOperation(
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              RoleOpertaionRes roleResponse = RoleOpertaionRes.fromJson(map);
              if ((roleResponse.responseCode != null &&
                  roleResponse.responseCode == 200) || (roleResponse.status != null &&
                  roleResponse.status == 200)) {
                if (roleResponse.dataList != null &&
                    roleResponse.dataList!.isNotEmpty) {
                  // RoleOperationDataList roleResponse = RoleOperationDataList.fromJson(jsonDecode(responseModel.result));
                  getStorage.write(Constant.ROLE_OPRATION,jsonEncode(roleResponse.dataList));
                  // update();
                }
              }
            } on Exception catch (e) {
              log("roleOperatation>>> $e");
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }


  getDemoGraphic() {
    isLoading = true;
    update();
    LoginProvider().getDemoGraphicMapping(
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetDemoGraphicMappingRes roleResponse =
              GetDemoGraphicMappingRes.fromJson(map);
              if ((roleResponse.responseCode != null &&
                  roleResponse.responseCode == 200) || (roleResponse.status != null &&
                  roleResponse.status == 200)) {
                if (roleResponse.demographicmappingtable != null &&
                    roleResponse.demographicmappingtable!.isNotEmpty) {
                  demographicmappingtable = roleResponse.demographicmappingtable!;
                  getStorage.write(Constant.DEMO_GRAPHIC_MAPPING,jsonEncode(roleResponse.demographicmappingtable));
                  Utils.masterData(demographicmappingtable);
                  // update();
                }
              } else {
                if (roleResponse.message != null &&
                    roleResponse.message!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      roleResponse.message,
                      AppTheme.colorWhite,
                      AppTheme.colorRed);
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  _handleOtpGenerateApiError(ResponseModel error) {
    isLoading = false;
    if (error.statusCode == Constant.CODE_NO_TRY_CATCH) {
      Utils.showSnackbar(Strings.ERROR," Strings.no_internet",
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }

  _handleApiError(ResponseModel error) {
    isLoading = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }
}
