import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/lead_management/lead_system_provider.dart';
import 'package:savbill/pages/lead_management/model/view_lead_doc_list_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class ViewLeadDocController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;
  bool checkBtnClickEvent = false;
  int customerId = 0;
  List<LeadDocContent>? documentList = [];
  ViewLeadDocListRes? viewLeadDocListRes;
  int page = 1;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
    initPlatformState();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_ID] != null) {
        customerId = arguments[Constant.CUSTOMER_ID];
      }
    }
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
    getCustomerDocumentData();
  }

  getCustomerDocumentData() {
    isLoading = true;
    update();
    PageRequest pageRequest = PageRequest(page: page, pageSize: 10);
    LeadSystemProvider().viewLeadDocList(
      requestNormal: pageRequest,
      leadId: customerId,
      onSuccess: (ResponseModel responseModel) {
        documentList?.clear();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;

              if(map['custmerDocList'] is List){
                Utils.showSnackbar(
                    Strings.INFO,
                    map['message'].toString(),
                    AppTheme.colorWhite,
                    AppTheme.colorBlueRView);
              }else{
                ViewLeadDocListRes responseData = ViewLeadDocListRes.fromJson(map);
                if ((responseData.status != null && responseData.status == 200 )||
                    (responseData.responseCode != null && responseData.responseCode == 200)) {
                  if (responseData.custmerDocList!.content != null &&
                      responseData.custmerDocList!.content!.isNotEmpty) {
                    documentList?.addAll(responseData.custmerDocList!.content!);
                  }
                } else {
                  if (responseData.responseMessage!.isNotEmpty) {
                    Utils.showSnackbar(
                        Strings.ERROR,
                        responseData.responseMessage,
                        AppTheme.colorWhite,
                        AppTheme.colorRed);
                  }
                }
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
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  setBtnClickEvent(bool status) {
    checkBtnClickEvent = status;
    update();
  }



  customerUploadDocumentDelete(int? docId) async {
    isLoading = true;
    update();
    LeadSystemProvider().leadUploadDocumentDelete(
      docId: docId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Map<String, dynamic> map = responseModel.result;
            BaseResponse responseData = BaseResponse.fromJson(map);
            if ((responseData.status != null && responseData.status == 200) ||
                (responseData.responseCode != null &&
                    responseData.responseCode == 200)) {
              Get.back(result: true);
              Utils.showSnackbar(Strings.SUCCESS, responseData.message,
                  AppTheme.colorWhite, AppTheme.colorGreen);
            } else if (responseData.responseCode == 406) {
              Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
                  AppTheme.colorWhite, AppTheme.colorBlueRView);
            } else {
              if (responseData.message!.isNotEmpty) {
                Utils.showSnackbar(Strings.ERROR, responseData.message,
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
        _handleApiError(error);
      },
    );
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
