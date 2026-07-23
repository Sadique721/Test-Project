import 'dart:convert';
import 'dart:io';

import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/file_detail.dart';
import 'package:savbill/pages/task_management/task_system_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:dio/dio.dart' as dia;
import 'package:get_storage/get_storage.dart';

class TaskUploadDocumentController extends GetxController {
  bool isLoading = false, checkBtnClickEvent = false, isChangeData = false;

  UserDetail userData = UserDetail();
  GetStorage getStorage = GetStorage();
  String? from;

  FileDetail? fileDetail;
  int? taskId;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
    initPlatformState();
  }

  Future<void> initPlatformState() async {
    String strUserData = "";
    if (getStorage.hasData(Constant.USER_DATA)) {
      strUserData = await getStorage.read(Constant.USER_DATA);
    }
    if (!strUserData.isNullOrEmpty()) {
      userData = UserDetail.fromJson(jsonDecode(strUserData));
      update();
    }
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.FROM] != null) {
        from = arguments[Constant.FROM];
      }
      if (arguments[Constant.TASK_ID] != null) {
        taskId = arguments[Constant.TASK_ID];
      }
    }
    update();
  }

  setBtnClickEvent(bool status) {
    checkBtnClickEvent = status;
    update();
  }

  void taskDocumentUpload() async {
    Map<String, dynamic> map = {};
    if (fileDetail != null &&
        fileDetail!.filePathLocal != null &&
        fileDetail!.filePathLocal!.isNotEmpty) {
      File f = File(fileDetail!.filePathLocal!);
      String fileName = f.path.split('/').last;
      dia.MultipartFile multipartFile =
      await dia.MultipartFile.fromFile(f.path, filename: fileName);
      map["file"] = multipartFile;
    }
    dia.FormData formData = dia.FormData.fromMap(map);
    isLoading = true;
    update();
    TaskSystemProvider().taskUpdateDocument(
      caseId: taskId!,
      formData: formData,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Map<String, dynamic> map = responseModel.result;
            BaseResponse responseData = BaseResponse.fromJson(map);
            if (responseData.responseCode != null &&
                (responseData.responseCode == 200 ||
                    responseData.responseCode == 406)) {
              showDialog(
                context: Get.context!,
                builder: (BuildContext context) {
                  return AlertDialogHelper(
                      title: Strings.INFO,
                      message: Strings.successfully,
                      positiveBtnText: Strings.ok,
                      negativeBtnText: "",
                      positiveBtnClick: () {
                        Get.back(result: true);
                        Get.back(result: true);
                      },
                      negativeBtnClick: () {
                        Get.back();
                      });
                },
              );
            } else {
              if (responseData.responseMessage != null &&
                  responseData.responseMessage!.isNotEmpty) {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
              }
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }

  handleApiError(ResponseModel error) {
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