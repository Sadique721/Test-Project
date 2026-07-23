import 'dart:convert';
import 'dart:developer';
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:dio/dio.dart';
import 'package:dio/dio.dart' as multi;
import 'package:dio_cache_interceptor/dio_cache_interceptor.dart';
import 'package:flutter/cupertino.dart';
import 'package:get_storage/get_storage.dart';

import '../util/logger.dart';

class ApiRequest {
  String url;

  dynamic? data;
  Map<String, dynamic>? queryParameters;
  late Response response;
  multi.FormData? formData;
  bool isFormData = false;
  late Dio dio;
  GetStorage getStorage = GetStorage();
  String? savePath;
  late CacheStore cacheStore;
  late CacheOptions cacheOptions;

  ApiRequest(
      {required this.url,
      this.data,
      this.formData,
      this.queryParameters,
      this.savePath,
      this.isFormData = false});

  Future<bool> interNetCheck() async {
    cacheStore = MemCacheStore(maxSize: 10485760, maxEntrySize: 1048576);
    cacheOptions = CacheOptions(
      store: cacheStore,
      policy: CachePolicy.request,
      hitCacheOnErrorExcept: [401, 403],
      // Use cache on network errors
      maxStale: Duration(days: 7),
      // Cache expires in 7 days
      priority: CachePriority.high,
      // hitCacheOnErrorExcept: [], // for offline behaviour
    );
    try {
      dio = new Dio();
      dio.interceptors.add(DioCacheInterceptor(options: cacheOptions));
      dio.options.followRedirects = false;

      dio.options.connectTimeout =
          const Duration(milliseconds: 380000); //3 minutes
      dio.options.receiveTimeout =
          const Duration(milliseconds: 180000); //3 minutes
      final result = await InternetAddress.lookup('google.com');
      if (result.isNotEmpty && result[0].rawAddress.isNotEmpty) {
        return true;
      } else {
        return false;
      }
    } on Exception {
      return false;
    }
  }

  Future<void> getRequest({
    Function()? beforeSend,
    Function(dynamic data)? onSuccess,
    Function(dynamic error)? onError,
  }) async {
    try {
      bool isInternet = await interNetCheck();
      if (isInternet) {
        String token = "";
        if (getStorage.hasData(Constant.USER_TOKEN)) {
          token = await getStorage.read(Constant.USER_TOKEN);
          // debugLog(token, tag: "token");
        }
        String url = this.url; //BASE_URL +
        debugLog(url, tag: "🌐 URL");

        response = await dio.get(
          url,
          queryParameters: queryParameters,
          options: Options(
            extra: {'cachePolicy': CachePolicy.refreshForceCache},
            headers: {
              "x-access-token": "",
              "Authorization": '$token',
              "requestFrom": 'gui'
            },
          ),
        );
        debugLog(response.data, tag: "✅ Response Data");

        ResponseModel responseModel;
        if (response.statusCode == 200 && response.data != null) {
          responseModel = ResponseModel(
              result: response.data,
              statusCode: response.statusCode,
              message: response.statusMessage);
        } else {
          responseModel = ResponseModel(
              result: null,
              statusCode: response.statusCode,
              message: response.statusMessage);
        }
        if (onSuccess != null) onSuccess(responseModel);
      } else {
        var responseModel = ResponseModel(
            result: null,
            statusCode: Constant.CODE_NO_INTERNET_CONNECTION,
            message: Strings.try_again);
        if (onError != null) onError(responseModel);
      }
    } catch (e) {
      debugLog(e, tag: "✅ Error $url");
      String? msg = Strings.something_wrong;
      dynamic statusCode = Constant.CODE_NO_TRY_CATCH;
      if (e is DioException) {
        if (e.response?.statusCode == 401 && e.response?.data != null) {
          // BaseResponse errorRes = BaseResponse.fromJson(jsonDecode(jsonDecode(e.response?.data)));
          BaseResponse errorRes = BaseResponse.fromJson(e.response!.data);
          if (errorRes.message!.isNotEmpty) {
            msg = errorRes.message;
          }
          if (errorRes.status != null) {
            statusCode = errorRes.status;
          }
        } else if (e.response?.statusCode == 404 && e.response?.data != null) {
          BaseResponse errorRes = BaseResponse.fromJson(e.response!.data);
          if (errorRes.msg != null && errorRes.msg!.isNotEmpty) {
            msg = errorRes.msg;
          }
          if (errorRes.status != null) {
            statusCode = errorRes.status;
          }
        } else if (e.response?.statusCode == 500) {
          BaseResponse errorRes = BaseResponse.fromJson(e.response!.data);
          if (errorRes.ERROR!.isNotEmpty) {
            msg = errorRes.ERROR;
          } else if (errorRes.error!.isNotEmpty) {
            msg = errorRes.error;
          } else {
            msg = errorRes.msg;
          }
          if (errorRes.status != null) {
            statusCode = errorRes.status;
          }
        } else if (e.response?.statusCode == 405) {
          BaseResponse errorRes = BaseResponse.fromJson(e.response!.data);
          if (errorRes.error!.isNotEmpty) {
            msg = errorRes.error;
          } else {
            msg = errorRes.msg;
          }
          if (errorRes.status != null) {
            statusCode = errorRes.status;
          }
        } else if (e.response?.data != null) {
          BaseResponse errorRes = BaseResponse.fromJson(e.response?.data);
          if (errorRes.ERROR!.isNotEmpty) {
            msg = errorRes.ERROR;
          } else if (errorRes.error!.isNotEmpty) {
            msg = errorRes.error;
          } else if (errorRes.msg!.isNotEmpty) {
            msg = errorRes.msg;
          } else if (errorRes.message!.isNotEmpty) {
            msg = errorRes.message;
          }
          if (errorRes.status != null) {
            statusCode = errorRes.status;
          }
        } else {
          dynamic data = _decodeErrorResponse(e);
          msg = data["message"];
          statusCode = data["statusCode"];
        }
      }
      var responseModel =
          ResponseModel(result: null, statusCode: statusCode, message: msg);
      if (onError != null) onError(responseModel);
    }
  }

  Future<void> postRequest({
    Function()? beforeSend,
    Function(dynamic data)? onSuccess,
    Function(dynamic error)? onError,
  }) async {
    try {
      bool isInternet = await interNetCheck();
      if (isInternet) {
        String url = "";
        url = this.url; //BASE_URL +
        String token = "";
        if (getStorage.hasData(Constant.USER_TOKEN)) {
          token = await getStorage.read(Constant.USER_TOKEN);
        }
        debugLog(url, tag: "🌐 URL");
        // debugLog(token, tag: "token");
        if (!isFormData) {
          debugLog(jsonEncode(data), tag: "Body");
          response = await dio.post(
            url,
            data: data,
            options: Options(
              headers: {
                "x-access-token": "",
                "Content-Type": "application/json",
                "Authorization": '$token',
                "Accept": "application/json"
              },
            ),
          );
        } else {
          debugLog(formData, tag: "Body");
          // print("Request Data ==> ${this.formData}");
          multi.Dio dio = new multi.Dio();
          dio.options.connectTimeout =
              const Duration(milliseconds: 180000); //3 minutes
          dio.options.receiveTimeout = const Duration(milliseconds: 180000);
          response = await dio.post(
            url,
            data: formData,
            options: Options(
              headers: {
                "x-access-token": "",
                "Content-Type": "multipart/form-data",
                "requestFrom": 'gui',
                "Authorization": '$token',
                "Content-Length": formData != null ? formData!.length : "",
                "Host": "143.198.140.196:30080",
              },
            ),
          );
        }
        debugLog(response, tag: "✅ Response Data");
        ResponseModel responseModel;
        if (response != null &&
            response.statusCode == 200 &&
            response.data != null) {
          responseModel = ResponseModel(
              result: response.data,
              statusCode: response.statusCode,
              message: response.statusMessage);
        } else if (response != null && response.statusCode == 200) {
          responseModel = ResponseModel(
              result: null,
              statusCode: response.statusCode,
              message: response.statusMessage);
        } else {
          responseModel = ResponseModel(
              result: null,
              statusCode: response.statusCode,
              message: response.statusMessage);
        }
        if (onSuccess != null) onSuccess(responseModel);
      } else {
        var responseModel = ResponseModel(
            result: null,
            statusCode: Constant.CODE_NO_INTERNET_CONNECTION,
            message: Strings.try_again);
        if (onError != null) onError(responseModel);
      }
    } catch (e) {
      String? msg = Strings.something_wrong;
      int? statusCode = Constant.CODE_NO_TRY_CATCH;
      if (e is DioException) {
        debugLog(e.response?.data, tag: "✅ Error Data");
        if (e.response?.statusCode == 401 && e.response?.data != null) {
          BaseResponse errorRes =
              BaseResponse.fromJson(jsonDecode(e.response!.data));
          if (errorRes.message != null && errorRes.message!.isNotEmpty) {
            msg = errorRes.message;
          }
          if (errorRes.status != null) {
            statusCode = errorRes.status;
          }
        } else if (e.response?.statusCode == 400 &&
            url.contains("generatePaytmLinkAndSend")) {
          statusCode = e.response?.statusCode;
          BaseResponse errorRes = BaseResponse.fromJson(e.response!.data);
          if (errorRes.msg != null && errorRes.msg!.isNotEmpty) {
            msg = errorRes.msg;
          }
        } else if (e.response?.statusCode == 417 &&
            url.contains("generatePaytmLinkAndSend")) {
          statusCode = e.response?.statusCode;
          BaseResponse errorRes = BaseResponse.fromJson(e.response!.data);
          if (errorRes.msg != null && errorRes.msg!.isNotEmpty) {
            msg = errorRes.msg;
          }
        } else if (e.response?.statusCode == 404) {
          statusCode = e.response?.statusCode;
          BaseResponse errorRes = BaseResponse.fromJson(e.response!.data);
          if (errorRes.msg != null && errorRes.msg!.isNotEmpty) {
            msg = errorRes.msg;
          } else if (errorRes.ERROR != null && errorRes.ERROR!.isNotEmpty) {
            msg = errorRes.ERROR;
          }
        } else if (e.response?.statusCode == 400) {
          statusCode = e.response?.statusCode;
          BaseResponse errorRes = BaseResponse.fromJson(e.response!.data);
          if (errorRes.ERROR != null && errorRes.ERROR!.isNotEmpty) {
            msg = errorRes.ERROR;
          } else if (errorRes.errorMessage != null &&
              errorRes.errorMessage!.isNotEmpty) {
            msg = errorRes.errorMessage;
          }
        } else if (e.response?.statusCode == 417) {
          statusCode = e.response?.statusCode;
          BaseResponse errorRes = BaseResponse.fromJson(e.response!.data);
          if (errorRes.errorMessage != null &&
              errorRes.errorMessage!.isNotEmpty) {
            msg = errorRes.errorMessage;
          } else if (errorRes.ERROR != null && errorRes.ERROR!.isNotEmpty) {
            msg = errorRes.ERROR;
          }
        } else if (e.response?.statusCode == 500) {
          statusCode = e.response?.statusCode;
          BaseResponse errorRes = BaseResponse.fromJson(e.response?.data);
          if (errorRes.error != null && errorRes.error!.isNotEmpty) {
            msg = errorRes.error;
          } else if (errorRes.errorMessage != null &&
              errorRes.errorMessage!.isNotEmpty) {
            msg = errorRes.errorMessage;
          }
          if (errorRes.ERROR != null && errorRes.ERROR!.isNotEmpty) {
            msg = errorRes.ERROR;
          }
          if (errorRes.status != null) {
            statusCode = errorRes.status;
          }
        } else if (e.response?.data != null) {
          BaseResponse errorRes = BaseResponse.fromJson(e.response!.data);
          if (errorRes.ERROR != null && errorRes.ERROR!.isNotEmpty) {
            msg = errorRes.ERROR;
          } else if (errorRes.error != null && errorRes.error!.isNotEmpty) {
            msg = errorRes.error;
          }
          if (errorRes.status != null) {
            statusCode = errorRes.status;
          }
        } else {
          dynamic data = _decodeErrorResponse(e);
          msg = data["message"];
          statusCode = data["statusCode"];
        }
      }
      var responseModel =
          ResponseModel(result: null, statusCode: statusCode, message: msg);
      if (onError != null) onError(responseModel);
    }
  }

  Future<void> putRequest({
    Function()? beforeSend,
    Function(dynamic data)? onSuccess,
    Function(dynamic error)? onError,
  }) async {
    try {
      bool isInternet = await interNetCheck();
      if (isInternet) {
        String url = "";
        url = this.url;
        String token = "";
        if (getStorage.hasData(Constant.USER_TOKEN)) {
          token = await getStorage.read(Constant.USER_TOKEN);
        }
        debugLog(url, tag: "🌐 URL");
        debugLog(jsonEncode(data), tag: "Body");
        response = await dio.put(
          url,
          data: data,
          options: Options(
            headers: {
              "x-access-token": "",
              "Content-Type": "application/json",
              "Authorization": '$token'
            },
          ),
        );
        debugLog(response.data, tag: "✅ Response Data");
        ResponseModel responseModel;
        if (response.statusCode == 200 && response.data != null) {
          responseModel = ResponseModel(
              result: response.data,
              statusCode: response.statusCode,
              message: response.statusMessage);
        } else {
          responseModel = ResponseModel(
              result: null,
              statusCode: response.statusCode,
              message: response.statusMessage);
        }
        if (onSuccess != null) onSuccess(responseModel);
      } else {
        var responseModel = ResponseModel(
            result: null,
            statusCode: Constant.CODE_NO_INTERNET_CONNECTION,
            message: Strings.try_again);
        if (onError != null) onError(responseModel);
      }
    } catch (e) {
      debugLog(e, tag: "✅ Error Data");
      String? msg = Strings.something_wrong;
      int? statusCode = Constant.CODE_NO_TRY_CATCH;
      if (e is DioException) {
        if (e.response?.statusCode == 401 && e.response?.data != null) {
          BaseResponse errorRes =
              BaseResponse.fromJson(jsonDecode(e.response?.data));

          if (errorRes.message!.isNotEmpty) {
            msg = errorRes.message;
          }
          if (errorRes.status != null) {
            statusCode = errorRes.status;
          }
        } else if (e.response?.data != null) {
          BaseResponse errorRes = BaseResponse.fromJson(e.response?.data);
          if (errorRes.ERROR!.isNotEmpty) {
            msg = errorRes.ERROR;
          }
          if (errorRes.status != null) {
            statusCode = errorRes.status;
          }
        } else {
          dynamic data = _decodeErrorResponse(e);
          msg = data["message"];
          statusCode = data["statusCode"];
        }
      }
      var responseModel =
          ResponseModel(result: null, statusCode: statusCode, message: msg);
      if (onError != null) onError(responseModel);
    }
  }

  Future<void> putRequest_custom({
    Function()? beforeSend,
    Function(dynamic data)? onSuccess,
    Function(dynamic error)? onError,
  }) async {
    try {
      bool isInternet = await interNetCheck();
      if (isInternet) {
        String url = "";
        url = this.url;
        String token = "";
        if (getStorage.hasData(Constant.USER_TOKEN)) {
          token = await getStorage.read(Constant.USER_TOKEN);
        }
        debugLog(url, tag: "🌐 URL");
        debugLog(jsonEncode(data), tag: "Body");
        response = await dio.put(
          url,
          data: data,
          options: Options(
            headers: {
              "x-access-token": "",
              "Content-Type": "application/json",
              "Authorization": '$token'
            },
          ),
        );
        debugLog(response.data, tag: "✅ Response Data");
        ResponseModel responseModel;
        if (response.statusCode == 200) {
          if (onSuccess != null) onSuccess(response.data);
        } else {
          responseModel = ResponseModel(
              result: null,
              statusCode: response.statusCode,
              message: response.statusMessage);
          if (onError != null) onError(responseModel);
        }
      } else {
        var responseModel = ResponseModel(
            result: null,
            statusCode: Constant.CODE_NO_INTERNET_CONNECTION,
            message: Strings.try_again);
        if (onError != null) onError(responseModel);
      }
    } catch (e) {
      debugLog(e, tag: "✅ Error Data");
      String? msg = Strings.something_wrong;
      int? statusCode = Constant.CODE_NO_TRY_CATCH;

      if (e is DioException) {
        if (e.response?.statusCode == 401 && e.response?.data != null) {
          BaseResponse errorRes =
              BaseResponse.fromJson(jsonDecode(e.response?.data));

          if (errorRes.message!.isNotEmpty) {
            msg = errorRes.message;
          }
          if (errorRes.status != null) {
            statusCode = errorRes.status;
          }
        } else if (e.response?.data != null) {
          BaseResponse errorRes = BaseResponse.fromJson(e.response?.data);
          if (errorRes.ERROR!.isNotEmpty) {
            msg = errorRes.ERROR;
          }
          if (errorRes.status != null) {
            statusCode = errorRes.status;
          }
        } else {
          dynamic data = _decodeErrorResponse(e);
          msg = data["message"];
          statusCode = data["statusCode"];
        }
      }
      var responseModel =
          ResponseModel(result: null, statusCode: statusCode, message: msg);
      if (onError != null) onError(responseModel);
    }
  }

  Future<void> deleteRequest({
    Function()? beforeSend,
    Function(dynamic data)? onSuccess,
    Function(dynamic error)? onError,
  }) async {
    try {
      bool isInternet = await interNetCheck();
      if (isInternet) {
        String token = "";
        if (getStorage.hasData(Constant.USER_TOKEN)) {
          token = await getStorage.read(Constant.USER_TOKEN);
        }
        String url = this.url; //BASE_URL +
        print("URL ==> $url");
        response = await dio.delete(
          url,
          queryParameters: queryParameters,
          options: Options(
            headers: {"x-access-token": "", "Authorization": '$token'},
          ),
        );
        debugLog(response.data, tag: "✅ Response Data");
        ResponseModel responseModel;
        if (response != null &&
            response.statusCode == 200 &&
            response.data != null) {
          responseModel = ResponseModel(
              result: response.data,
              statusCode: response.statusCode,
              message: response.statusMessage);
        } else {
          responseModel = ResponseModel(
              result: null,
              statusCode: response.statusCode,
              message: response.statusMessage);
        }
        if (onSuccess != null) onSuccess(responseModel);
      } else {
        var responseModel = ResponseModel(
            result: null,
            statusCode: Constant.CODE_NO_INTERNET_CONNECTION,
            message: Strings.try_again);
        if (onError != null) onError(responseModel);
      }
    } catch (e) {
      debugLog(e, tag: "✅ Error Data");
      String? msg = Strings.something_wrong;
      int? statusCode = Constant.CODE_NO_TRY_CATCH;
      if (e is DioError) {
        if (e.response?.statusCode == 401 && e.response?.data != null) {
          BaseResponse errorRes =
              BaseResponse.fromJson(jsonDecode(e.response?.data));
          if (errorRes.message!.isNotEmpty) {
            msg = errorRes.message;
          }
          if (errorRes.status != null) {
            statusCode = errorRes.status;
          }
        } else {
          dynamic data = _decodeErrorResponse(e);
          msg = data["message"];
          statusCode = data["statusCode"];
        }
      }
      var responseModel =
          ResponseModel(result: null, statusCode: statusCode, message: msg);
      if (onError != null) onError(responseModel);
    }
  }

  Future<void> downloadRequest({
    Function()? beforeSend,
    Function(dynamic data)? onSuccess,
    Function(dynamic error)? onError,
  }) async {
    try {
      bool isInternet = await interNetCheck();
      if (isInternet) {
        print("URL ==> $url");
        response = await dio.download(
          url,
          savePath,
        );
        debugLog(response.data, tag: "✅ Response Data");
        ResponseModel responseModel;
        if (response != null &&
            response.statusCode == 200 &&
            response.data != null) {
          responseModel = ResponseModel(
              result: response.data,
              statusCode: response.statusCode,
              message: response.statusMessage);
        } else {
          responseModel = ResponseModel(
              result: null,
              statusCode: response.statusCode,
              message: response.statusMessage);
        }
        if (onSuccess != null) onSuccess(responseModel);
      } else {
        var responseModel = ResponseModel(
            result: null,
            statusCode: Constant.CODE_NO_INTERNET_CONNECTION,
            message: Strings.try_again);
        if (onError != null) onError(responseModel);
      }
    } catch (e) {
      debugLog(e, tag: "✅ Error Data");
      String? msg = Strings.something_wrong;
      int? statusCode = Constant.CODE_NO_TRY_CATCH;
      if (e is DioError) {
        if (e.response?.statusCode == 401 && e.response?.data != null) {
          BaseResponse errorRes =
              BaseResponse.fromJson(jsonDecode(e.response?.data));
          if (errorRes.message!.isNotEmpty) {
            msg = errorRes.message;
          }
          if (errorRes.status != null) {
            statusCode = errorRes.status;
          }
        } else {
          dynamic data = _decodeErrorResponse(e);
          msg = data["message"];
          statusCode = data["statusCode"];
        }
      }
      var responseModel =
          ResponseModel(result: null, statusCode: statusCode, message: msg);
      if (onError != null) onError(responseModel);
    }
  }

  Future<void> postRequest_custom({
    Function()? beforeSend,
    Function(dynamic data)? onSuccess,
    Function(dynamic error)? onError,
  }) async {
    try {
      bool isInternet = await interNetCheck();
      if (isInternet) {
        String url = "";
        url = this.url; //BASE_URL +
        String token = "";
        if (getStorage.hasData(Constant.USER_TOKEN)) {
          token = await getStorage.read(Constant.USER_TOKEN);
        }
        print("URL ==> $url");
        if (!isFormData) {
          // print("Request Data ==> ${jsonEncode(this.data)}");
          debugPrint("Request Data ==> ${jsonEncode(this.data)}");
          response = await dio.post(
            url,
            data: data,
            options: Options(
              headers: {
                "x-access-token": "",
                "Content-Type": "application/json",
                "Authorization": '$token',
              },
            ),
          );
        } else {
          //  print("Request Data ==> ${this.formData}");
          debugLog(formData, tag: "Body");
          multi.Dio dio = new multi.Dio();
          dio.options.connectTimeout =
              const Duration(milliseconds: 180000); //3 minutes
          dio.options.receiveTimeout = const Duration(milliseconds: 180000);
          response = await dio.post(
            url,
            data: formData,
            options: Options(
              headers: {
                "x-access-token": "",
                "Content-Type": "multipart/form-data",
                "requestFrom": 'gui',
                "Authorization": '$token',
                "Content-Length": formData != null ? formData!.length : "",
                "Host": "143.198.140.196:30080",
              },
            ),
          );
        }
        debugLog(response.data, tag: "✅ Response Data");
        ResponseModel responseModel;
        if (response != null &&
            response.statusCode == 200 &&
            response.data != null) {
          responseModel = ResponseModel(
              result: response.data,
              statusCode: response.statusCode,
              message: response.statusMessage);
        } else if (response.statusCode == 200) {
          responseModel = ResponseModel(
              result: null,
              statusCode: response.statusCode,
              message: response.statusMessage);
        } else {
          responseModel = ResponseModel(
              result: null,
              statusCode: response.statusCode,
              message: response.statusMessage);
        }
        if (onSuccess != null) onSuccess(responseModel);
      } else {
        var responseModel = ResponseModel(
            result: null,
            statusCode: Constant.CODE_NO_INTERNET_CONNECTION,
            message: Strings.try_again);
        if (onError != null) onError(responseModel);
      }
    } catch (e) {
      debugLog(e, tag: "✅ Error Data");
      log("Error response tryCatchPart ==> ${e}");
      String? msg = Strings.something_wrong;
      int? statusCode = Constant.CODE_NO_TRY_CATCH;
      if (e is DioError) {
        if (e.response?.statusCode == 401 && e.response?.data != null) {
          BaseResponse errorRes =
              BaseResponse.fromJson(jsonDecode(e.response?.data));
          if (errorRes.message!.isNotEmpty) {
            msg = errorRes.message;
          }
          if (errorRes.status != null) {
            statusCode = errorRes.status;
          }
        } else if (e.response?.statusCode == 400 &&
            url.contains("generatePaytmLinkAndSend")) {
          statusCode = e.response?.statusCode;
          BaseResponse errorRes = BaseResponse.fromJson(e.response!.data);
          if (errorRes.msg!.isNotEmpty) {
            msg = errorRes.msg;
          }
        } else if (e.response?.statusCode == 417 &&
            url.contains("generatePaytmLinkAndSend")) {
          statusCode = e.response?.statusCode;
          BaseResponse errorRes = BaseResponse.fromJson(e.response!.data);
          if (errorRes.msg!.isNotEmpty) {
            msg = errorRes.msg;
          }
        } else {
          dynamic data = _decodeErrorResponse(e);
          msg = data["message"];
          statusCode = data["statusCode"];
        }
      }
      var responseModel =
          ResponseModel(result: null, statusCode: statusCode, message: msg);
      if (onError != null) onError(responseModel);
    }
  }

  // dynamic _decodeErrorResponse(dynamic e) {
  //   dynamic data = {
  //     "statusCode": Constant.CODE_NO_TRY_CATCH,
  //     "message": Strings.something_wrong
  //   };
  //   if (e is DioError) {
  //     if (e.type == DioErrorType.receiveTimeout) {
  //       print(e.response);
  //       final response = e.response;
  //       try {
  //         if (response != null && response.data != null) {
  //           final Map responseData =
  //               json.decode(response.data as String) as Map;
  //           data["message"] = response.statusCode.toString() +
  //               " : " +
  //               responseData['message'];
  //           data["statusCode"] = response.statusCode;
  //         }
  //       } catch (e) {
  //         data["message"] =
  //             Strings.server_communication_msg + " :${response?.statusCode}";
  //       }
  //     } else if (e.type == DioErrorType.connectionTimeout ||
  //         e.type == DioErrorType.receiveTimeout ||
  //         e.type == DioErrorType.sendTimeout) {
  //       data["message"] = Strings.request_timeout;
  //       data["statusCode"] = 408;
  //     } else if (e.error is SocketException) {
  //       data["message"] = Strings.no_internet;
  //     }
  //   }
  //   return data;
  // }

  dynamic _decodeErrorResponse(dynamic e) {
    dynamic data = {
      "statusCode": Constant.CODE_NO_TRY_CATCH,
      "message": Strings.something_wrong
    };
    if (e is DioError) {
      if (e.type == DioExceptionType.badResponse) {
        print(e.response);
        final response = e.response;
        try {
          if (response != null && response.data != null) {
            final Map responseData =
                json.decode(response.data as String) as Map;
            data["message"] = response.statusCode.toString() +
                " : " +
                responseData['message'];
            data["statusCode"] = response.statusCode;
          }
        } catch (e) {
          data["message"] =
              "${Strings.server_communication_msg} :${response?.statusCode}";
        }
      } else if (e.type == DioExceptionType.connectionTimeout ||
          e.type == DioExceptionType.receiveTimeout ||
          e.type == DioExceptionType.sendTimeout) {
        data["message"] = Strings.request_timeout;
        data["statusCode"] = 408;
      } else if (e.error is SocketException) {
        data["message"] = Strings.no_internet;
      }
    }
    return data;
  }
}
