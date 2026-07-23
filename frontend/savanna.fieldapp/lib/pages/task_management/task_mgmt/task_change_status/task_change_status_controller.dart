import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/response/location_data_res.dart';
import 'package:savbill/pages/customer/model/response/location_lat_long_res.dart';
import 'package:savbill/pages/dashboard/model/request/case_assign_req.dart';
import 'package:savbill/pages/dashboard/model/response/case_status_response.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/task_management/model/response/get_all_staff_res.dart';
import 'package:savbill/pages/task_management/model/response/view_task_detail_response.dart';
import 'package:savbill/pages/task_management/task_mgmt/task_change_status/upload_doc_task.dart';
import 'package:savbill/pages/task_management/task_system_provider.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_contact_failed_call_res.dart';
import 'package:savbill/pages/ticket_system/ticket_management/get_staff_user_service_res.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_resolution_reasons_res.dart';
import 'package:savbill/pages/ticket_system/ticket_system_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:dio/dio.dart' as dia;
import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:intl/intl.dart';
import 'package:http/http.dart' as http;
import 'package:path/path.dart';
import 'package:http_parser/http_parser.dart';

class TaskChangeStatusController extends GetxController {
  bool isLoading = false,
      isChangeData = false,
      isShowLoadMore = false,
      serviceAreaFlag = false,
      helperNameFlag = false;
  List<CaseStatusDetail>? caseStatusList = [];
  List<CaseStatusDetail>? changeStatusList = [];
  CaseStatusDetail? selectedCaseStatus;
  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;
  int? serviceAreaId;
  String? nextFollowupDate;
  String? nextFollowupTime;

  // List<ActiveStaffUserList>? activeStaffUserList;
  // ActiveStaffUserList? selectedActiveStaffUserData;
  TaskDetail? taskDetail;

  // String? caseStatus = "";
  TextEditingController caseStatusController = TextEditingController();
  TextEditingController serviceAreaController = TextEditingController();
  TextEditingController remarksController = TextEditingController();
  TextEditingController followupDateTimeController = TextEditingController();
  TextEditingController helperNameController = TextEditingController();
  DateTime? selectedFollowUpDate;
  DateFormat dateFormat =
  DateFormat("${Constant.DATE_FORMAT} ${Constant.APP_TIME_FORMAT}");
  DateFormat dateOnlyFormat = DateFormat(Constant.API_DATE_FORMAT);
  DateFormat apiDateTimeFormat = DateFormat(Constant.API_DATE_TIME_FORMAT);
  String? followUpScheduleDateTime;
  List<ResolutionReasonsDataList>? resolutionReasonsList = [];
  ResolutionReasonsDataList? selectedResolutionReason;

  List<RootCauseResolutionMappingList>? rootCauseResolutionList = [];
  RootCauseResolutionMappingList? selectedRootCauseResolutionData;

  List<StaffUserServiceDataList>? getStaffUserServiceDataList = [];
  StaffUserServiceDataList? selectedStaffUserServiceData;
  int? finalResolutionId, rootCauseReasonId;


  List<AllStaffDataList>? allStaffDataList = [];
  AllStaffDataList? selectAllStaffDataList;
  List<int> selectedAllStaffDataIds = [];

  bool isCall = false, isTicket = false, isCallDisconnected = false;

  List<String>? contactFailedReasonList = [];

  String? selectContactFailedReason;

  String? changeStatusSingleMultiple = "pTicket";
  String? taskType;

  int? radioCallSelected, radioTicketCloseSelect;
  String radioCallVal = "", radioTicketVal = "";

  String token = "";
  LocationLatLong? locationData;
  LocationDetail? selectedLocation;
  String? selectPowerOpticalRange;
  List<Map<String, String>> opticalRangeData = [
    {'label': '-15', 'value': '-15'},
    {'label': '-16', 'value': '-16'},
    {'label': '-17', 'value': '-17'},
    {'label': '-18', 'value': '-18'},
    {'label': '-19', 'value': '-19'},
    {'label': '-20', 'value': '-20'},
    {'label': '-21', 'value': '-21'},
    {'label': '-22', 'value': '-22'},
    {'label': '-23', 'value': '-23'}
  ];
  bool checkBtnClickEvent = false;
  TextEditingController latController = TextEditingController();
  TextEditingController longController = TextEditingController();
  List<Map<String, dynamic>>? allFilesSectionData;
  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.TASK_DETAIL] != null) {
        taskDetail = arguments[Constant.TASK_DETAIL];
        if (taskDetail != null &&
            taskDetail!.caseStatus != null &&
            taskDetail!.caseStatus!.isNotEmpty) {
          caseStatusController.text = taskDetail!.caseStatus!;
        }
        if (taskDetail != null &&
            taskDetail!.serviceAreaName != null &&
            taskDetail!.serviceAreaName!.isNotEmpty) {
          serviceAreaController.text = taskDetail!.serviceAreaName!;
        }
        if (taskDetail != null && taskDetail!.serviceAreaId != null) {
          serviceAreaId = taskDetail!.serviceAreaId!;
        }
      }

      if (arguments[Constant.TASK_TYPE] != null) {
        taskType = arguments[Constant.TASK_TYPE];
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

    if (getStorage.hasData(Constant.USER_TOKEN)) {
      token = getStorage.read(Constant.USER_TOKEN);
    }

    getCaseStatusListData();
  }

  getCaseStatusListData() {
    isLoading = true;
    caseStatusList?.clear();
    update();
    TaskSystemProvider().getTaskCaseStatusList(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CaseStatusResponse responseData =
              CaseStatusResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  caseStatusList = responseData.dataList!;
                  // for (var element in responseData.dataList!) {
                  //   if (taskDetail!.currentAssigneeId == null &&
                  //       caseStatusController.text
                  //           .equalsIgnoreCase("Resolved") &&
                  //       element.value!.equalsIgnoreCase("Closed")) {
                  //     caseStatusList?.add(element);
                  //   } else if (taskDetail!.currentAssigneeId != null &&
                  //       (!element.status!
                  //           .equalsIgnoreCase("Raise and Close"))) {
                  //     caseStatusList?.add(element);
                  //   }
                  // }

                  log("status===>${taskDetail!.caseStatus}");
                  if (taskDetail!.caseStatus!.equalsIgnoreCase("Open")) {
                    final validStatusesForOpen = ["In Progress"];
                    changeStatusList = caseStatusList!
                        .where((element) =>
                        validStatusesForOpen.contains(element.value))
                        .toList();
                  }else if (taskDetail!.caseStatus!.equalsIgnoreCase("In Progress")){
                    final validStatusesForInProgress = ["Resolved", "Rejected", "Done", "On-Hold", "On Hold", "Cancelled"];
                    changeStatusList = caseStatusList!
                        .where((element) => validStatusesForInProgress.contains(element.value))
                        .toList();
                  }else if(taskDetail!.caseStatus!.equalsIgnoreCase("Re-Open") || taskDetail!.caseStatus!.equalsIgnoreCase("Re Open")){
                    final validStatusesForReOpen = ["In Progress"];
                    changeStatusList = caseStatusList!
                        .where((element) =>
                        validStatusesForReOpen.contains(element.value))
                        .toList();
                  }else if(taskDetail!.caseStatus!.equalsIgnoreCase("Resolved")){
                    final validStatusesForResolved = ["Re-Open", "Re Open", "Done"];
                    changeStatusList = caseStatusList!
                        .where((element) =>
                        validStatusesForResolved.contains(element.value))
                        .toList();
                  }else if(taskDetail!.caseStatus!.equalsIgnoreCase("Rejected")){
                    final validStatusesForRejected = ["Re-Open", "Re Open", "Cancelled"];
                    changeStatusList = caseStatusList!
                        .where((element) =>
                        validStatusesForRejected.contains(element.value))
                        .toList();
                  }else if(taskDetail!.caseStatus!.equalsIgnoreCase("On Hold") || taskDetail!.caseStatus!.equalsIgnoreCase("On-Hold")){
                    final validStatusesForOnHold = ["In Progress", "Cancelled"];
                    changeStatusList = caseStatusList!
                        .where((element) =>
                        validStatusesForOnHold.contains(element.value))
                        .toList();
                  }else if(taskDetail!.caseStatus!.equalsIgnoreCase("Cancelled")){
                    final validStatusesForCancelled = ["Discarded"];
                    changeStatusList = caseStatusList!
                        .where((element) =>
                        validStatusesForCancelled.contains(element.value))
                        .toList();
                  }else{
                    changeStatusList = caseStatusList;
                  }
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
        isLoading = false;
        // checkTicketHelperName();
        update();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
        // checkTicketHelperName();
      },
    );
  }

  getResolutionReasonsChangeStatus(String? statusValue) {
    // selectedActiveStaffUserData = null;
    getAllStaffWithoutPagination();
    changeStatusSelection(statusValue);
    if (statusValue!.equalsIgnoreCase("Resolved")) {
      // checkTicketResolutionReasons();
      openTaskUploadScreen(Strings.upload,userDetail!.userId!,);
    } else if (statusValue.equalsIgnoreCase("Closed")) {
      isCall = true;
      isTicket = true;
      checkTicketResolutionReasons();
    } else {
      isCall = false;
      isTicket = false;
      isCallDisconnected = false;
    }
  }

  setBtnClickEvent(bool status) {
    checkBtnClickEvent = status;
    update();
  }

  openTaskUploadScreen(String? from, int? customerId) async {
    var result = await Get.to(UploadDocTask(), arguments: {
      Constant.FROM: from,
      Constant.CUSTOMER_ID: customerId,
    });
    if (result != null) {
      log("openCustInventoryDocumentUploadScreen===>>$result");
      allFilesSectionData = result;
    }
  }


  getLocationToLatLong() {
    isLoading = true;
    update();
    CustomerProvider().getLocationToLatLong(
      placeId: selectedLocation!.placeId!,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              LocationLatLongRes responseData =
              LocationLatLongRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.location != null) {
                  locationData = responseData.location;
                  latController.text = responseData.location!.latitude!;
                  longController.text = responseData.location!.longitude!;
                }
              } else {
                if (responseData.error!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.error,
                      AppTheme.colorWhite, AppTheme.colorRed);
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

  checkTicketResolutionReasons() {
    isLoading = true;
    resolutionReasonsList!.clear();
    selectedResolutionReason = null;
    update();
    TaskSystemProvider().viewTaskResolutionReasonsTickets(
      id: taskDetail!.caseCategoryId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TicketResolutionReasonsRes responseData =
              TicketResolutionReasonsRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  resolutionReasonsList?.addAll(responseData.dataList!);
                }
              } else if (responseData.responseCode == 404) {
                // Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
                //     AppTheme.colorWhite, AppTheme.colorBlueRView);
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
        isLoading = false;
        update();
        // getActiveStaffUser();
      },
      onError: (ResponseModel error) {
        // getActiveStaffUser();
        handleApiError(error);
      },
    );
  }

  // getActiveStaffUser() {
  //   isLoading = true;
  //   activeStaffUserList?.clear();
  //   update();
  //   TicketSystemProvider().getAllActiveStaffUser(
  //     onSuccess: (ResponseModel responseModel) {
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           try {
  //             Map<String, dynamic> map = responseModel.result;
  //             GetActiveStaffUserRes responseData =
  //                 GetActiveStaffUserRes.fromJson(map);
  //             if ((responseData.responseCode != null &&
  //                     responseData.responseCode == 200) ||
  //                 (responseData.status != null && responseData.status == 200)) {
  //               if (responseData.staffUserlist != null &&
  //                   responseData.staffUserlist!.isNotEmpty) {
  //                 activeStaffUserList = responseData.staffUserlist!
  //                     .where(
  //                         (element) => element.businessUnitIdsList!.isNotEmpty)
  //                     .toList();
  //               }
  //             } else {
  //               if (responseData.responseMessage!.isNotEmpty) {
  //                 Utils.showSnackbar(
  //                     Strings.ERROR,
  //                     responseData.responseMessage,
  //                     AppTheme.colorWhite,
  //                     AppTheme.colorRed);
  //               }
  //             }
  //           } on Exception catch (e) {
  //             print(e.toString());
  //           }
  //         }
  //       } else {
  //         if (responseModel.message!.isNotEmpty) {
  //           Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
  //               AppTheme.colorWhite, AppTheme.colorRed);
  //         }
  //       }
  //       isLoading = false;
  //       update();
  //     },
  //     onError: (ResponseModel error) {
  //       handleApiError(error);
  //     },
  //   );
  // }

  checkTicketHelperName() {
    isLoading = true;
    getStaffUserServiceDataList!.clear();
    selectedStaffUserServiceData = null;
    update();
    TicketSystemProvider().getAllStaffUserByServiceArea(
      serviceId: serviceAreaId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetStaffUserServiceRes responseData =
              GetStaffUserServiceRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  getStaffUserServiceDataList?.addAll(responseData.dataList!);
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
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }

  getAllStaffWithoutPagination() {
    isLoading = true;
    allStaffDataList!.clear();
    selectAllStaffDataList = null;
    update();
    TaskSystemProvider().getAllStaffWithoutPagination(
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetAllStaffRes responseData =
              GetAllStaffRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  allStaffDataList?.addAll(responseData.dataList!);
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
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }

  onCallDisconnectedCall(bool? event) {
    if (event == false) {
      isCallDisconnected = true;
      isTicket = true;
      isLoading = true;
      contactFailedReasonList!.clear();
      update();
      TicketSystemProvider().getContactFailedCall(
        onSuccess: (ResponseModel responseModel) {
          isLoading = false;
          if (responseModel.statusCode == 200) {
            if (responseModel.result != null) {
              try {
                Map<String, dynamic> map = responseModel.result;
                TicketContactFailedCallRes responseData =
                TicketContactFailedCallRes.fromJson(map);
                if ((responseData.responseCode != null &&
                    responseData.responseCode == 200) ||
                    (responseData.status != null &&
                        responseData.status == 200)) {
                  if (responseData.contactFailed != null &&
                      responseData.contactFailed!.isNotEmpty) {
                    contactFailedReasonList =
                        responseData.contactFailed![0].split(",");
                  }
                } else {
                  if (responseData.msg!.isNotEmpty) {
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
              Utils.showSnackbar(
                  Strings.ERROR,
                  responseModel.message!.isNotEmpty,
                  AppTheme.colorWhite,
                  AppTheme.colorRed);
            }
          }
          isLoading = false;
          update();
        },
        onError: (ResponseModel error) {
          handleApiError(error);
        },
      );
    } else {
      isCallDisconnected = false;
      isTicket = false;
      Utils.showSnackbar(Strings.INFO, Strings.under_development,
          AppTheme.colorWhite, AppTheme.colorBlueRView);
    }
  }

  caseAssignRequest() {
    isLoading = true;
    update();
    Map<String, dynamic> map = {};
    CaseAssignReq caseAssignReq = CaseAssignReq(
      remark: remarksController.text,
      status: selectedCaseStatus!.text,
      remarkType: "Change status",
      ticketId: taskDetail?.caseId,
      helperName: helperNameController.text,
      finalResolutionId: finalResolutionId,
      rootCauseReasonId: rootCauseReasonId,
    );

    log("caseAssignRequest==>${jsonEncode(caseAssignReq)}");

    map["caseUpdate"] = jsonEncode(caseAssignReq);
    dia.FormData formData = dia.FormData.fromMap(map);
    TaskSystemProvider().taskCaseAssignRequest(
      formData: formData,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                Get.back(result: true);
                // Get.back(result: true);
              } else if (responseData.responseCode == 406) {
                Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorBlueRView);
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
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }

  uploadDocuments() async {
    final url = Uri.parse(UrlConstants.task_case_assign_update);
    final request = http.MultipartRequest('POST', url);

    CaseAssignReq caseAssignReq = CaseAssignReq(
      remark: remarksController.text,
      status: selectedCaseStatus!.text,
      remarkType: "Change status",
      ticketId: taskDetail?.caseId,
      helperName: helperNameController.text,
      finalResolutionId: finalResolutionId,
      rootCauseReasonId: rootCauseReasonId,
    );
    // map["caseUpdate"] = jsonEncode(caseAssignReq);

    request.headers['Authorization'] = token;
    request.headers['x-access-token'] = '';
    request.headers['Content-Type'] = 'multipart/form-data';
    // request.headers['requestFrom'] = 'gui';
    request.fields['caseUpdate'] = jsonEncode(caseAssignReq);

    if(allFilesSectionData != null && allFilesSectionData!.isNotEmpty) {
      for (int i = 0; i < allFilesSectionData!.length; i++) {
        final section = allFilesSectionData![i];
        request.fields['sections[$i].name'] = section['sectionName'] ?? '';
        request.fields['sections[$i].latitude'] = section['latitude'] ?? '';
        request.fields['sections[$i].longitude'] = section['longitude'] ?? '';
        request.fields['sections[$i].opticalRange'] =
            section['opticalRange'] ?? '';
        final List<PlatformFile> sectionFiles = section['files'] ?? [];
        for (PlatformFile file in sectionFiles) {
          if (file.path != null) {
            request.files.add(await http.MultipartFile.fromPath(
              'sections[$i].files',
              file.path!,
              filename: basename(file.name),
              contentType: MediaType('image', 'jpg'), // or 'png'
            ));
          }
        }
      }
    }

    try {
      isLoading = true;
      final streamedResponse = await request.send();
      final response = await http.Response.fromStream(streamedResponse);
      final responseData = jsonDecode(response.body);
      log("response body => ${response.body}"); // ✅ correct
      if (response.statusCode == 200) {
        isLoading = false;
        if (responseData['responseCode'] == 406 ||
            responseData['responseCode'] == 417) {
          debugPrint('❌ Error: ${responseData['responseMessage']}');
          // Show error snackbar/toast/dialog here
        }else{
          debugPrint('✅ Success: ${responseData['message']}');
          Get.back(result: true);
          // Refresh list, close dialog, show success toast/snackbar here
        }
      } else {
        isLoading = false;
        debugPrint('❌ Server Error: ${response.body}');
        // Show server error UI
      }
    } catch (e) {
      isLoading = false;
      debugPrint('❌ Exception during upload: $e');
      // Show error UI
    }
  }

  caseAssignCloseRequest() {
    isLoading = true;
    update();
    Map<String, dynamic> map = {};
    CaseAssignNewReq caseAssignReq = CaseAssignNewReq(
      remark: remarksController.text,
      status: selectedCaseStatus!.text,
      remarkType: "Change status",
      ticketId: taskDetail?.caseId,
      helperName: helperNameController.text,
      callStatus: radioCallSelected == 1 ? "true" : "false",
      deacivateReason: selectContactFailedReason ?? "",
      isClosed: radioTicketCloseSelect == 1 ? "true" : "false",
      nextFollowupDate: (nextFollowupDate != null || nextFollowupDate != "")
          ? nextFollowupDate
          : "",
      nextFollowupTime: (nextFollowupTime != null || nextFollowupTime != "")
          ? nextFollowupTime
          : "",
      caseFeedbackRel: null,
    );

    log("caseAssignNewReq===>${jsonEncode(caseAssignReq)}");

    map["caseUpdate"] = jsonEncode(caseAssignReq);
    dia.FormData formData = dia.FormData.fromMap(map);
    TaskSystemProvider().taskCaseAssignRequest(
      formData: formData,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                Get.back(result: true);
              } else if (responseData.responseCode == 406) {
                Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorBlueRView);
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
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(Strings.INFO, error.message, AppTheme.colorWhite,
          AppTheme.colorBlueRView);
    }
    update();
  }

  void changeStatusSelection(String? statusValue) {
    isCall = false;
    if (changeStatusSingleMultiple!.equalsIgnoreCase("pTicket")) {
      String? oldStatus = caseStatusController.text;
      if (!oldStatus.equalsIgnoreCase("Resolved")) {
        if (statusValue!.equalsIgnoreCase("Closed")) {
          Utils.showSnackbar(
              Strings.INFO,
              " marked closed only after the resolved status.",
              AppTheme.colorWhite,
              AppTheme.colorBlueRView);
          selectedCaseStatus!.text = "";
        }
      }
    }
  }
}
