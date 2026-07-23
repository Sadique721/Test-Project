import 'dart:convert';
import 'dart:developer';
import 'dart:io';

import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/request/cust_upload_document_req.dart';
import 'package:savbill/pages/customer/model/response/cust_doc_verification_mode_res.dart';
import 'package:savbill/pages/customer/model/response/cust_doc_verification_res.dart';
import 'package:savbill/pages/customer/model/response/doc_sub_type_verification_res.dart';
import 'package:savbill/pages/customer/model/response/get_document_status_res.dart';
import 'package:savbill/pages/lead_management/lead_system_provider.dart';
import 'package:savbill/pages/lead_management/model/request/cms_lead_ofline_doc_upload_req.dart';
import 'package:savbill/pages/lead_management/model/view_lead_doc_list_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/file_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:dio/dio.dart' as dia;
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:intl/intl.dart';

class CreateDocLeadController extends GetxController {
  bool isLoading = false, checkBtnClickEvent = false;
  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;
  String? from = Strings.add;

  int customerId = 0, serviceAreaIdValue = 0;
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

  List<VerificationDataList>? docVerificationList = [];
  VerificationDataList? selectDocVerification;

  List<VerificationTypeModeDataList>? docVerificationTypeModeList = [];
  VerificationTypeModeDataList? selectedDocumentType;

  List<DocSubTypeDataList>? docSubTypeVerificationList = [];
  DocSubTypeDataList? selectedDocSubTypeData;

  DateTime? selectedStartDate, selectedEndDate;

  List<DocumentStatusDataList>? documentStatusList = [];
  DocumentStatusDataList? selectedDocumentStatus;

  TextEditingController startDateController = TextEditingController();
  TextEditingController endDateController = TextEditingController();
  TextEditingController remarkController = TextEditingController();
  TextEditingController documentNumberController = TextEditingController();

  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);
  String? verificationMode;
  List<CMSLeadUploadDocumentReq>? custUploadDocumentList = [];
  FileDetail? fileDetail;

  // DocumentDetail? documentDetail;
  LeadDocContent? documentDetail;

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

      if (arguments[Constant.FROM] != null) {
        from = arguments[Constant.FROM];
      }

      if (arguments[Constant.CUSTOMER_DOCUMENT_DETAIL] != null) {
        documentDetail = arguments[Constant.CUSTOMER_DOCUMENT_DETAIL];
      }
    }
    getCustDocVerificationMode();
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

  setBtnClickEvent(bool status) {
    checkBtnClickEvent = status;
    update();
  }

  getCustDocVerificationMode() {
    docVerificationList!.clear();
    docSubTypeVerificationList!.clear();
    docVerificationTypeModeList!.clear();
    selectedDocumentType = null;
    selectedDocSubTypeData = null;
    selectDocVerification = null;
    isLoading = true;
    update();
    CustomerProvider().getCustDocVerification(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerDocVerificationRes responseData =
              CustomerDocVerificationRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  docVerificationList?.addAll(responseData.dataList!);
                  if (documentDetail != null) {
                    for (var element in docVerificationList!) {
                      if (element.value!
                          .equalsIgnoreCase(documentDetail!.mode!)) {
                        selectDocVerification = element;
                        verificationMode = element.value;
                        getDocTypeVerificationMode(
                            selectDocVerification!.value);
                      }
                    }
                  }
                }
              } else {
                if (responseData.responseMessage != null &&
                    responseData.responseMessage!.isNotEmpty) {
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
        isLoading = false;
        update();
        getDocumentStatus();
      },
      onError: (ResponseModel error) {
        getDocumentStatus();
        _handleApiError(error);
      },
    );
  }

  getDocTypeVerificationMode(String? docType) {
    docVerificationTypeModeList!.clear();
    selectedDocumentType = null;
    isLoading = true;
    update();
    CustomerProvider().getDocTypeVerificationMode(
      docType: docType,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustDocVerificationModeRes responseData =
              CustDocVerificationModeRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  docVerificationTypeModeList?.addAll(responseData.dataList!);
                  if (documentDetail != null) {
                    for (var element in docVerificationTypeModeList!) {
                      if (element.value!
                          .equalsIgnoreCase(documentDetail!.docType!)) {
                        selectedDocumentType = element;
                        getDocSubTypeVerification(element.value);
                      }
                    }
                  }
                }
              } else {
                if (responseData.responseMessage != null &&
                    responseData.responseMessage!.isNotEmpty) {
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
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  getDocSubTypeVerification(String? docType) {
    docSubTypeVerificationList!.clear();
    selectedDocSubTypeData = null;
    isLoading = true;
    update();
    LeadSystemProvider().getDocSubTypeVerification(
      verificationMode: verificationMode,
      docType: docType,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              DocSubTypeVerificationRes responseData =
              DocSubTypeVerificationRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  docSubTypeVerificationList?.addAll(responseData.dataList!);
                  if (documentDetail != null) {
                    for (var element in docSubTypeVerificationList!) {
                      if (element.text!
                          .equalsIgnoreCase(documentDetail!.docSubType!)) {
                        selectedDocSubTypeData = element;
                      }
                    }
                  }
                }
              } else {
                if (responseData.responseMessage != null &&
                    responseData.responseMessage!.isNotEmpty) {
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
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  getDocumentStatus() {
    documentStatusList!.clear();
    selectedDocumentStatus = null;
    isLoading = true;
    update();
    CustomerProvider().getDocumentStatus(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetDocumentStatusRes responseData =
              GetDocumentStatusRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  documentStatusList?.addAll(responseData.dataList!);
                  if (documentDetail != null) {
                    for (var element in documentStatusList!) {
                      if (element.displayName!
                          .equalsIgnoreCase(documentDetail!.docStatus!)) {
                        selectedDocumentStatus = element;
                      }
                    }
                  }
                }
              } else {
                if (responseData.responseMessage != null &&
                    responseData.responseMessage!.isNotEmpty) {
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
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  customerUploadDocument({bool? isUpdate}) async {
    log("1234===>>>>${selectDocVerification!.value}");
    if(selectDocVerification != null && !(selectDocVerification!.value!.equalsIgnoreCase("online"))) {
      CMSLeadUploadDocumentReq request = CMSLeadUploadDocumentReq(
        leadMasterId: customerId.toString(),
        docType: selectedDocumentType!.value,
        docSubType: selectedDocSubTypeData!.text,
        docStatus: selectedDocumentStatus!.value,
        remark: remarkController.text,
        startDate: startDateController.text,
        // mode: selectDocVerification!.value,
        // documentNumber: documentNumberController.text ?? "",
        endDate: endDateController.text,
        filename: "",
      );
      Map<String, dynamic> map = {};
      if (fileDetail != null &&
          fileDetail!.filePathLocal != null &&
          fileDetail!.filePathLocal!.isNotEmpty) {
        File f = File(fileDetail!.filePathLocal!);
        String fileName = f.path
            .split('/')
            .last;
        dia.MultipartFile multipartFile =
        await dia.MultipartFile.fromFile(f.path, filename: fileName);
        map["file"] = multipartFile;
        request.filename = fileName;
      }
      // custUploadDocumentList!.add(request);
      // log("custUploadDocumentList==>>>${jsonEncode(custUploadDocumentList)}");
      // print("RequestData ==> ${jsonEncode(custUploadDocumentList)}");
      map["docDetails"] = jsonEncode(request);
      dia.FormData formData = dia.FormData.fromMap(map);

      isLoading = true;
      update();
      LeadSystemProvider().cmsLeadUploadDocument(
        formData: formData,
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
              Utils.showSnackbar(
                  Strings.ERROR, responseModel.message!.isNotEmpty,
                  AppTheme.colorWhite, AppTheme.colorRed);
            }
          }
          isLoading = false;
          update();
        },
        onError: (ResponseModel error) {
          log("cmsLeadUploadDocument_error==>>${jsonEncode(error)}");
          _handleApiError(error);
        },
      );
    }else{
        CMSLeadUploadDocumentReq request = CMSLeadUploadDocumentReq(
          leadMasterId: customerId.toString(),
          docType: selectedDocumentType!.value,
          docSubType: selectedDocSubTypeData!.text,
          docStatus: selectedDocumentStatus!.value,
          remark: remarkController.text,
          startDate: startDateController.text,
          documentNumber: documentNumberController.text ?? "",
          endDate: endDateController.text,
          filename: "",
        );



        Map<String, dynamic> map = {};
        // if (fileDetail != null &&
        //     fileDetail!.filePathLocal != null &&
        //     fileDetail!.filePathLocal!.isNotEmpty) {
        //   File f = File(fileDetail!.filePathLocal!);
        //   String fileName = f.path
        //       .split('/')
        //       .last;
        //   dia.MultipartFile multipartFile =
        //   await dia.MultipartFile.fromFile(f.path, filename: fileName);
        //   map["file"] = multipartFile;
        //   request.filename = fileName;
        // }
        // custUploadDocumentList!.add(request);
        // log("custUploadDocumentList==>>>${jsonEncode(custUploadDocumentList)}");
        // print("RequestData ==> ${jsonEncode(custUploadDocumentList)}");
        // map["docDetails"] = jsonEncode(request);
        // dia.FormData formData = dia.FormData.fromMap(map);
        // dia.FormData formData = dia.FormData.fromMap(map );


        map["leadMasterId"] = request.leadMasterId;
        map["docType"] = request.docType;
        map["docSubType"] = request.docSubType;
        map["docStatus"] = request.docStatus;
        map["remark"] = request.remark;
        map["startDate"] = request.startDate;
        map["documentNumber"] = request.documentNumber;
        map["endDate"] = request.endDate;
        // map["filename"] = request.filename;

        dia.FormData formData = dia.FormData.fromMap(map);

        isLoading = true;
        update();
        LeadSystemProvider().cmsLeadUploadDocumentOnline(
          formData: request,
          isUpdate: isUpdate,
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
                Utils.showSnackbar(
                    Strings.ERROR, responseModel.message!.isNotEmpty,
                    AppTheme.colorWhite, AppTheme.colorRed);
              }
            }
            isLoading = false;
            update();
          },
          onError: (ResponseModel error) {
            _handleApiErrorCustom(error);
          },
        );
    }
  }

  customerUploadDocumentUpdate(LeadDocContent? item) async {
    CustUploadDocumentReq request = CustUploadDocumentReq(
      custId: customerId.toString(),
      docId: item!.docId,
      docType: selectedDocumentType!.value,
      docSubType: selectedDocSubTypeData!.text,
      docStatus: selectedDocumentStatus!.value,
      remark: remarkController.text,
      startDate: startDateController.text,
      mode: selectDocVerification!.value,
      endDate: endDateController.text,
      filename: "",
    );
    Map<String, dynamic> map = {};
    if (fileDetail != null &&
        fileDetail!.filePathLocal != null &&
        fileDetail!.filePathLocal!.isNotEmpty) {
      File f = File(fileDetail!.filePathLocal!);
      String fileName = f.path.split('/').last;
      dia.MultipartFile multipartFile =
      await dia.MultipartFile.fromFile(f.path, filename: fileName);
      map["file"] = multipartFile;
      request.filename = fileName;
    }
    print("RequestData ==> ${jsonEncode(request)}");
    isLoading = true;
    update();
    CustomerProvider().customerUploadDocumentUpdate(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Map<String, dynamic> map = responseModel.result;
            BaseResponse responseData = BaseResponse.fromJson(map);
            if ((responseData.status != null && responseData.status == 200) ||
                (responseData.responseCode != null &&
                    responseData.responseCode == 200)) {
              Get.back(result: true);
              update();
            } else if (responseData.responseCode == 406) {
              Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
                  AppTheme.colorWhite, AppTheme.colorBlueRView);
            } else {
              if (responseData.responseMessage!.isNotEmpty) {
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
        _handleApiError(error);
      },
    );
  }

  _handleApiError(ResponseModel error) {
    isLoading = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (error.statusCode == 400) {
      Utils.showSnackbar(Strings.INFO, error.message,
          AppTheme.colorWhite, AppTheme.colorBlueRView);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }

  _handleApiErrorCustom(ResponseModel error) {
    isLoading = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (error.statusCode == 400) {
      Utils.showSnackbar(Strings.INFO, error.message,
          AppTheme.colorWhite, AppTheme.colorBlueRView);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }
}