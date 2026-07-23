import 'dart:convert';

import 'package:savbill/pages/lead_management/lead_system_provider.dart';
import 'package:savbill/pages/lead_management/model/lead_notes_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class LeadNotesController extends GetxController {
  bool isLoading = false;
  bool isShowLoadMore = false;
  int eventId = 0;
  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;
  ScrollController? controller;
  int page = 1;
  List<LeadNotesContent>? leadNotesContentList = [];
  LeadNotesRes? leadNotesRes;

  @override
  void onInit() {
    super.onInit();
    initPlatformState();
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (leadNotesRes != null &&
            leadNotesRes!.leadNoteList!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          leadNotesApi(eventId);
        }
      }
    });
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
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.LEAD_MASTER_ID] != null) {
        eventId = arguments[Constant.LEAD_MASTER_ID];
      }
      leadNotesApi(eventId);
    }
    update();
  }

  leadNotesApi(int? eventId) {
    PageRequest pageRequest =
        PageRequest(page: page, pageSize: Constant.PAGE_LOAD_DATA_LIMIT);
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    LeadSystemProvider().getAllLeadNotes(
      id: eventId,
      pageRequest: pageRequest,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              LeadNotesRes responseData = LeadNotesRes.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                if(responseData.leadNoteList != null ) {
                leadNotesRes = responseData;
                if (page == 1) {
                  leadNotesContentList?.clear();
                }
                if (responseData.leadNoteList?.content != null &&
                    responseData.leadNoteList!.content!.isNotEmpty) {
                  leadNotesContentList
                      ?.addAll(responseData.leadNoteList!.content!);
                }
              }else{
                  Utils.showSnackbar(Strings.INFO, responseData.message,
                      AppTheme.colorWhite, AppTheme.colorBlueRView);
                }
              } else {
                if (page == 1) {
                  leadNotesContentList?.clear();
                }
                Utils.showSnackbar(Strings.INFO, responseData.message,
                    AppTheme.colorWhite, AppTheme.colorBlueRView);
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (page == 1) {
            leadNotesContentList?.clear();
          }
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        // isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        isShowLoadMore = false;
        isLoading = false;
        if (page == 1) {
          leadNotesContentList?.clear();
        }
        _handleApiError(error);


      },
    );
  }

  _handleApiError(ResponseModel error) {
    isLoading = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (error.statusCode == Constant.CODE_NO_TRY_CATCH) {
      Utils.showSnackbar(Strings.INFO, Strings.no_data_found,
          AppTheme.colorWhite, AppTheme.colorBlueRView);
    }else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }
}
