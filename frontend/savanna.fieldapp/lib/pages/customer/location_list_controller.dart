import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/response/location_data_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class LocationListController extends GetxController {
  bool isLoading = false;
  TextEditingController searchController = TextEditingController();
  List<LocationDetail>? locationList = [];

  @override
  void onInit() {
    super.onInit();
    //getLocationData();
  }

  getLocationData() {
    String searchTxt = searchController.text.toString();
    if (searchTxt.isEmpty) {
      Utils.showSnackbar(Strings.ERROR, "Please search your text",
          AppTheme.colorWhite, AppTheme.colorRed);
      return;
    }
    isLoading = true;
    update();
    CustomerProvider().getLocationData(
      type: searchTxt,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        locationList?.clear();
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              LocationDataRes responseData = LocationDataRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.locations != null &&
                    responseData.locations!.isNotEmpty) {
                  locationList?.addAll(responseData.locations!);
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

/* */
}
