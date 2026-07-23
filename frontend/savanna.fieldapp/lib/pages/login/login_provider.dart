import 'package:dio/dio.dart';
import 'package:dio_cache_interceptor/dio_cache_interceptor.dart';
import 'package:get_storage/get_storage.dart';
import 'package:savbill/pages/login/model/request/login_request.dart';
import 'package:savbill/pages/login/model/response/get_acl_entry_res.dart';
import 'package:savbill/webservices/api_request.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';

import '../../util/constant.dart';
import '../../util/logger.dart';

class LoginProvider {
  void loginRequest({
    LoginRequest? loginRequest,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.login, data: loginRequest).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  void otpGenerateValidate({
    Map<dynamic, dynamic>? loginRequest,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.generateOtpValidate, data: loginRequest).postRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  void aclEntry({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(url: UrlConstants.getAclEntry,).getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }


  //role operation

  // Get Device Port Availability Data
  void getRoleOperation({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url:
        UrlConstants.role_operation)
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  // get Demo Graphic Mapping
  void getDemoGraphicMapping({
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url:
        UrlConstants.get_demo_graphic_mapping)
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }

  void getSystemConfigurationData({
    required String type,
    Function()? beforeSend,
    Function(ResponseModel responseModel)? onSuccess,
    Function(ResponseModel error)? onError,
  }) {
    ApiRequest(
        url:
        "${UrlConstants.system_configuration}getConfigurationByName?name=$type")
        .getRequest(
      beforeSend: () => {if (beforeSend != null) beforeSend()},
      onSuccess: (data) {
        onSuccess!(data);
      },
      onError: (error) => {if (onError != null) onError(error)},
    );
  }
}
