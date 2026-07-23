import 'dart:convert';
import 'dart:developer';
import 'dart:io';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer_caf/response/cust_caf_notes_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';

class CustomerNotesDetailController extends GetxController {
  List<CafNoteContent>? customerNotesDetails = [];
  List<CafNoteContent>? customerAllNotes = [];
  bool isLoading = false;
  GetStorage getStorage = GetStorage();
  CustomerDetail? customerDetail;
  UserDetail? userDetail;
  @override
  void onInit() {
    super.onInit();
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
    getArgumentData();
  }
  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_DETAIL] != null) {
        customerDetail = arguments[Constant.CUSTOMER_DETAIL];
      }
      log("customerDetail==>${customerDetail!.id}");
      getCafNotes();
    }
  }

  getCustomerAllNotes({required bool isExcel}) async {
    isLoading = true;
    update();
    CustomerProvider().getCustomerAllNotes(
      custId : customerDetail!.id,
      onSuccess: (ResponseModel responseModel) async {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustCAFAllNotesRes responseData =
              CustCAFAllNotesRes.fromJson(map);
              if (responseData.status == 200 &&
                  responseData.status != null) {
                if (responseData.customerNotesList != null && responseData.customerNotesList!.isNotEmpty) {
                 // customerAllNotes = responseData.customerNotesList!.content;
                 // String? savedFilePath =  await CustomerProvider().createAndSavePdf([responseModel.result],customerDetail?.custname,customerDetail?.acctno);

                  String? savedFilePath = "";
                  if (isExcel) {
                     savedFilePath =  await CustomerProvider().createAndSaveExcelSameLayout([responseModel.result],customerDetail?.custname,customerDetail?.acctno,
                         customerDetail?.serviceareaName, "Active", customerDetail?.areaName,customerDetail?.oltName);
                  } else {
                     savedFilePath =  await CustomerProvider().createAndSavePdf([responseModel.result],customerDetail?.custname,customerDetail?.acctno,customerDetail?.serviceareaName,"Active", customerDetail?.areaName,customerDetail?.oltName);
                  }

                  if (savedFilePath != null) {
                    print('PDF created and saved successfully!');
                    if (Platform.isIOS) {
                      Utils.showSnackbar(Strings.SUCCESS, "File saved to → On My iPhone → ${Strings.app_name} folder.",
                          AppTheme.colorWhite, AppTheme.colorGreen);
                    } else {
                      Utils.showSnackbar(Strings.SUCCESS, "File Downloaded Successfully Please Open Download Folder!!",
                          AppTheme.colorWhite, AppTheme.colorGreen);
                    }
                  }
                }else{
                  Utils.showSnackbar(
                      Strings.INFO,
                      responseData.message,
                      AppTheme.colorWhite,
                      AppTheme.colorRed);
                }
              } else {
                Utils.showSnackbar(
                    Strings.ERROR,
                    responseData.message,
                    AppTheme.colorWhite,
                    AppTheme.colorRed);
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!,
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


  getCafNotes() {
    isLoading = true;
    update();
    CustomerProvider().getCustomerNotes(
      custId : customerDetail!.id,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustCAFNotesRes responseData =
              CustCAFNotesRes.fromJson(map);
              if (responseData.status == 200 &&
                  responseData.status != null) {
                if (responseData.customerNotesList != null && responseData.customerNotesList!.content!.isNotEmpty) {
                  customerNotesDetails = responseData.customerNotesList!.content;
                }else{
                  Utils.showSnackbar(
                      Strings.INFO,
                      responseData.message,
                      AppTheme.colorWhite,
                      AppTheme.colorRed);
                }
              } else {
                Utils.showSnackbar(
                    Strings.ERROR,
                    responseData.message,
                    AppTheme.colorWhite,
                    AppTheme.colorRed);
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!,
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
    }
    update();
  }
}