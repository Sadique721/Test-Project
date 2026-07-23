import 'dart:convert';
import 'package:savbill/pages/lead_management/lead_system_provider.dart';
import 'package:savbill/pages/lead_management/model/all_rejected_reason_lead_res.dart';
import 'package:savbill/pages/lead_management/model/reassign_lead_res.dart';
import 'package:savbill/pages/lead_management/model/view_lead_response.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:dropdown_search/dropdown_search.dart';
import 'package:flutter/cupertino.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class CloseLeadController extends GetxController {
  bool isLoading = false,
      isShowLoadMore = false;

  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;

  LeadMasterListData? leadViewContentData;

  List<RejectedContent> allRejectedReasonContentList = [];
  RejectedContent? selectedRejectedContentData;
  List<RejectSubReasonDtoList> rejectedSubReasonDtoList = [];
  RejectSubReasonDtoList? selectedRejectedSubReasonDto;
  final selectStaffDropDownKey = GlobalKey<DropdownSearchState>();
  TextEditingController remarkController = TextEditingController();
  String? pageTitle = Strings.closeLead;


  List<ReassignLeadDataList>? reassignLeadDataList = [];
  ReassignLeadDataList? selectedReassignLeadData;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.LEAD_MASTER_ID] != null) {
        leadViewContentData = arguments[Constant.LEAD_MASTER_ID];
      }

      if (arguments[Constant.PAGE_TITLE] != null) {
        pageTitle = arguments[Constant.PAGE_TITLE];
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
    if (pageTitle!.equalsIgnoreCase(Strings.closeLead)) {
      getAllRejectedReasonList();
    } else if (pageTitle!.equalsIgnoreCase(Strings.reassignLead)) {
      getReassignLead();
    }
  }

  getAllRejectedReasonList() {
    isLoading = true;
    allRejectedReasonContentList.clear();
    selectedRejectedContentData = null;
    update();
    LeadSystemProvider().allRejectedReasonLead(
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              AllRejectedReasonLeadRes responseData =
              AllRejectedReasonLeadRes.fromJson(map);
              if (responseData.responseCode == 200 ||
                  responseData.status == 200) {
                if (responseData.rejectReasonList!.content!.isNotEmpty) {
                  allRejectedReasonContentList =
                  responseData.rejectReasonList!.content!;
                }
              } else {
                Utils.showSnackbar(Strings.ERROR, responseData.message,
                    AppTheme.colorWhite, AppTheme.colorRed);
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }


  getReassignLead() {
    isLoading = true;
    update();
    LeadSystemProvider().getReassignLead(
      leadId: leadViewContentData!.id,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ReassignLeadRes responseData = ReassignLeadRes.fromJson(map);
              if (responseData.responseCode == 200 ||
                  responseData.status == 200) {
                if (responseData.dataList != null || responseData.dataList!.isNotEmpty) {
                  reassignLeadDataList = responseData.dataList!;
                }
              } else {
                Utils.showSnackbar(Strings.ERROR, responseData.message,
                    AppTheme.colorWhite, AppTheme.colorRed);
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty) {
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

  saveCloseLead() {
    isLoading = true;
    update();
    LeadSystemProvider().saveCloseLead(
      leadMasterId: leadViewContentData!.id,
      rejectReasonId: selectedRejectedContentData?.id,
      rejectSubReasonId: selectedRejectedSubReasonDto?.id,
      remark: remarkController.text,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          try {
            Map<String, dynamic> map = responseModel.result;
            BaseResponse responseData = BaseResponse.fromJson(map);
            if (responseData.status == 200) {
              Get.back(result: true);
              Utils.showSnackbar(Strings.SUCCESS, responseData.message,
                  AppTheme.colorWhite, AppTheme.colorGreen);
            } else {
              Utils.showSnackbar(Strings.ERROR, responseData.message,
                  AppTheme.colorWhite, AppTheme.colorRed);
            }
          } on Exception catch (e) {
            print(e.toString());
          }
        } else {
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }


  updateLeadReassign() {

    isLoading = true;
    update();
    LeadSystemProvider().updateLeadReassign(
      leadMasterId:leadViewContentData!.id,
      status:leadViewContentData!.leadStatus,
      remark:remarkController.text,
      assignee:selectedReassignLeadData?.id,

      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 0)) {
                Get.back(result: true);
              } else {
                Utils.showSnackbar(Strings.ERROR, responseData.message,
                    AppTheme.colorWhite, AppTheme.colorRed);
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        update();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }


  handleApiError(ResponseModel error) {
    isLoading = false;
    isShowLoadMore = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }

  _handleApiError(ResponseModel error) {
    isLoading = false;
    isShowLoadMore = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(Strings.ERROR, "No staff available to assign..",
          AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }
}
