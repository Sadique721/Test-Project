import 'dart:convert';
import 'dart:developer';
import 'dart:io';
import 'package:savbill/pages/customer_inventory/response/inventory_work_flow_res.dart';
import 'package:savbill/pages/customer_invoice/pdf_screen.dart';
import 'package:savbill/pages/dashboard/savbill_care_provider.dart';
import 'package:savbill/pages/dashboard/model/response/get_ticket_tat_report_res.dart';
import 'package:savbill/pages/dashboard/model/response/show_tat_details_res.dart';
import 'package:savbill/pages/dashboard/model/response/show_ticket_etr_report_res.dart';
import 'package:savbill/pages/dashboard/model/response/ticket_detail_response.dart';
import 'package:savbill/pages/dashboard/model/response/ticket_follow_up_find_all_response.dart';
import 'package:savbill/pages/dashboard/model/response/ticket_followup_list_response.dart';
import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/pages/dashboard/savbill_caretab_controller.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_staff_detail_res.dart';
import 'package:savbill/pages/ticket_system/ticket_system_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/acl_constant.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/permission_service.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:open_file/open_file.dart';
import 'package:path/path.dart' as path;
import 'package:path_provider/path_provider.dart';
import 'package:permission_handler/permission_handler.dart';

import '../../webservices/url_constants.dart';
import '../customer_invoice/image_preview_screen.dart';
import '../pending_approvals/pending_approvals_provider.dart';
import '../ticket_system/model/response/approve_reject_ticket_res.dart';
import '../ticket_system/model/response/check_reassign_ticket_res.dart';
import '../ticket_system/ticket_management/ticket_change_problem_domain.dart';
import '../ticket_system/ticket_management/ticket_staff_assign_dialog.dart';
import '../ticket_system/ticket_management/view_ticket_controller.dart';
import 'case_assign.dart';
import 'model/response/case_assign_staff_lst.dart';

class TicketDetailController extends GetxController
    implements TicketAssignAction {
  bool isLoading = false;
  TicketDetail? ticketDetail;
  List<TicketAttachments>? attachmentList = [];
  List<FollowUpDetail>? followUpDetailList = [];
  List<CaseUpdateList>? caseUpdateList = [];
  List<WorkFlowAuditDataList>? workFlowAuditDataList = [];
  List<TicketFollowUpFindAllDataList>? followUpFindAllTicketList = [];

  List<ShowTicketETRReportDataList>? showTicketETRReportList = [];
  List<GetTicketTATReportDataList>? showTicketTATReportList = [];

  List<ApproveRejectTicketList>? approveRejectTicketList = [];
  int ticketId = 0;
  bool isShowLoadMore = false;
  int page = 1;

  int? assignStaffParentId;
  List<CaseStaffDetail>? staffList = [];
  CaseStaffDetail? selectedStaff;

  Dio dio = Dio();
  GetStorage getStorage = GetStorage();
  String? progress = "0";

  FlutterLocalNotificationsPlugin? flutterLocalNotificationsPlugin;
  BuildContext? context;
  ShowTATDetailsData? showTATDetailsData;
  UserDetail? userDetail;

  //ACL
  bool? changeStatusAccess = false;

  @override
  void onInit() {
    super.onInit();
    initPlatformState();
    context = Get.key.currentContext;

    changeStatusAccess = PermissionService()
        .hasAclPermission([AclTicketingSystems.TICKET_CHANGE_STATUS]);
    flutterLocalNotificationsPlugin = FlutterLocalNotificationsPlugin();
    const android = AndroidInitializationSettings('mipmap/ic_launcher');

    final ios = DarwinInitializationSettings(
      requestAlertPermission: true,
      requestBadgePermission: true,
      requestSoundPermission: true,
      // onDidReceiveLocalNotification: (int id, String? title, String? body, String? payload) async {}
    );
    final initSetting = InitializationSettings(android: android, iOS: ios);

    flutterLocalNotificationsPlugin!.initialize(initSetting,
        onDidReceiveNotificationResponse: (NotificationResponse payload) {
      log("onDidReceiveNotificationResponse==>${payload.payload}");
      if (payload.payload != null) OpenFile.open(payload.payload);
    }, onDidReceiveBackgroundNotificationResponse:
            (NotificationResponse notificationResponse) {
      if (notificationResponse.payload != null)
        OpenFile.open(notificationResponse.payload);
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
      if (arguments[Constant.TICKET_ID] != null) {
        ticketId = arguments[Constant.TICKET_ID];
        getTicketListData(ticketId);
      }
    }
  }

  Future onselectedNotification(
      Map<String, dynamic> payload, BuildContext context) async {
    if (payload['isSuccess']) {
      OpenFile.open(payload['filePath']);
    } else {
      showDialog(
          context: context,
          builder: (context) => AlertDialog(
                title: const Text('Error'),
                content: Text(payload['error']),
              ));
    }
  }

  getTicketListData(int ticketId) {
    isLoading = true;
    caseUpdateList!.clear();
    attachmentList!.clear();
    update();
    SavbillCareProvider().getTicketDetail(
      id: ticketId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TicketDetailResponse responseData =
                  TicketDetailResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                ticketDetail = responseData.data;
                if (ticketDetail != null &&
                    ticketDetail?.caseUpdateList != null &&
                    ticketDetail!.caseUpdateList!.isNotEmpty) {
                  caseUpdateList?.addAll(ticketDetail!.caseUpdateList!);
                }
                if (ticketDetail != null &&
                    ticketDetail?.caseDocDetails != null &&
                    ticketDetail!.caseDocDetails!.isNotEmpty) {
                  attachmentList?.addAll(ticketDetail!.caseDocDetails!);
                }
                if (ticketDetail?.currentAssigneeId != null) {
                  getTicketStaffDetail(ticketDetail?.currentAssigneeId);
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
        getTicketFollowupDetail(ticketId);
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getTicketFollowupDetail(ticketId);
      },
    );
  }

  getTicketFollowupDetail(int ticketId) {
    isLoading = true;
    update();
    SavbillCareProvider().getTicketFollowupDetail(
      id: ticketId.toString(),
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TicketFollowupListResponse responseData =
                  TicketFollowupListResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                followUpDetailList?.clear();
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  followUpDetailList?.addAll(responseData.dataList!);
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
        getTicketFollowupFindAll(ticketId);
      },
      onError: (ResponseModel error) {
        getTicketFollowupFindAll(ticketId);
        _handleApiError(error);
      },
    );
  }

  getTicketFollowupFindAll(int ticketId) {
    isLoading = true;
    update();
    SavbillCareProvider().getTicketFollowupFindAll(
      id: ticketId.toString(),
      pageRequest: PageRequest(page: 1, pageSize: 10),
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TicketFollowupFindAllResponse responseData =
                  TicketFollowupFindAllResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                followUpFindAllTicketList?.clear();
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  followUpFindAllTicketList?.addAll(responseData.dataList!);
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
        showTATDetailsCall(ticketId);
      },
      onError: (ResponseModel error) {
        showTATDetailsCall(ticketId);
        _handleApiError(error);
      },
    );
  }

  showTATDetailsCall(int ticketId) {
    isLoading = true;
    update();
    SavbillCareProvider().showTATDetails(
      caseId: ticketId.toString(),
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ShowTATDetailsRes responseData = ShowTATDetailsRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (!responseData.data!.isNullOrEmpty()) {
                  showTATDetailsData = responseData.data;
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
        showETRReport(ticketId);
      },
      onError: (ResponseModel error) {
        showETRReport(ticketId);
        _handleApiError(error);
      },
    );
  }

  showETRReport(int ticketId) {
    isLoading = true;
    update();
    SavbillCareProvider().getTicketEtrReport(
      ticketId: ticketId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ShowTicketETRReportRes responseData =
                  ShowTicketETRReportRes.fromJson(map);
              if (responseData.responseCode == 200 ||
                  responseData.responseCode == 0) {
                showTicketETRReportList?.clear();
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  showTicketETRReportList?.addAll(responseData.dataList!);
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
        getTATReport(ticketId);
      },
      onError: (ResponseModel error) {
        getTATReport(ticketId);
        _handleApiError(error);
      },
    );
  }

  getTATReport(int ticketId) {
    isLoading = true;
    update();
    SavbillCareProvider().getTicketTATReport(
      caseId: ticketId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetTicketTATReportRes responseData =
                  GetTicketTATReportRes.fromJson(map);
              if (responseData.responseCode == 200 ||
                  responseData.responseCode == 0) {
                showTicketTATReportList?.clear();
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  showTicketTATReportList?.addAll(responseData.dataList!);
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

  Future download(String fileUrl, String fileName, BuildContext context) async {
    final dir = (await getDonwloadDirectory());
    final permissionStatus = await reguestPermission();
    // log("permissionStatus==>$permissionStatus");
    // if (permissionStatus == true) {
    final savePath = path.join(dir.path, fileName);

    final file = await startDownload(savePath, fileUrl, context);
    if (file == null) return;

    // print(savePath);
    // } else {
    //   print("Permission Deined!");
    // }
  }

  Future<Directory> getDonwloadDirectory() async {
    if (Platform.isAndroid) {
      // return await DownloadsPathProvider.downloadsDirectory;
      Directory dir = Directory('/storage/emulated/0/Download');
      return dir;
    }
    return getApplicationDocumentsDirectory();
  }

  // permission status
  Future<bool> reguestPermission() async {
    // final persmission = await PermissionHandler().checkPermissionStatus(PermissionGroup.storage);
    final permission = await Permission.storage.status;
    if (permission != PermissionStatus.granted) {
      // await PermissionHandler().requestPermissions([PermissionGroup.storage]);
      await Permission.storage.request();
    }
    // return persmission == PermissionStatus.granted;
    return permission == PermissionStatus.granted;
  }

  Future<bool> requestPermission(Permission permission) async {
    if (await permission.isGranted) {
      return true;
    } else {
      var result = await permission.request();
      if (result == PermissionStatus.granted) {
        return true;
      }
    }
    return false;
  }

  Future<Map<String, dynamic>> startDownload(
      String savePath, String urlPath, BuildContext context) async {
    String token = "";
    if (getStorage.hasData(Constant.USER_TOKEN)) {
      token = await getStorage.read(Constant.USER_TOKEN);
    }
    Map<String, String> headers = {
      'Content-type': 'application/json; charset=UTF-8',
      'Accept': 'application/json',
      'Authorization': 'Bearer $token'
    };

    Map<String, dynamic> result = {
      "isSuccess": false,
      "filePath": null,
      "error": null
    };
    try {
      final response = await dio.download(urlPath, savePath,
          onReceiveProgress: _onReceiveProgress,
          options: Options(
            headers: headers,
            responseType: ResponseType.bytes,
          ));
      result['isSuccess'] = response.statusCode == 200;
      result['filePath'] = "$savePath";
      update();
    } catch (e) {
      result['error'] = e.toString();
    } finally {
      _showNotification(result, context);
      log("filePathhk==>${result['filePath']}");
      // await OpenFile.open(result['filePath'],type:".pdf" );
    }
    return result;
  }

  _onReceiveProgress(int receive, int total) {
    if (total != -1) {
      progress = "${(receive / total * 100).toStringAsFixed(0)}%";
      update();
    }
  }

  Future _showNotification(
      Map<String, dynamic> downloadStatus, BuildContext context) async {
    const android = AndroidNotificationDetails("channelId", "download file",
        channelDescription: "channelDescription",
        priority: Priority.high,
        importance: Importance.max,
        channelShowBadge: true);

    const ios = DarwinNotificationDetails();

    const notificationDetails = NotificationDetails(android: android, iOS: ios);
    final isSuccess = downloadStatus['isSuccess'];
    log("downloadStatus===>>>${downloadStatus['filePath']}");
    log("downloadStatus===>>>${downloadStatus['isSuccess']}");

    await FlutterLocalNotificationsPlugin().show(
      0,
      isSuccess ? "Sucess" : "error",
      isSuccess ? "File Download Successful" : "File Download Faild",
      notificationDetails,
      payload: downloadStatus['filePath'],
    );
  }

  Future<void> downloadFile(String? apiUrl, TicketAttachments items) async {
    var url = "${apiUrl}";

    var fileType = items.filename!.split(".");
    String token = "";
    if (getStorage.hasData(Constant.USER_TOKEN)) {
      token = await getStorage.read(Constant.USER_TOKEN);
    }
    Map<String, String> headers = {
      'Content-type': 'application/json; charset=UTF-8',
      'Accept': 'application/octet-stream',
      'Authorization': 'Bearer $token'
    };
    final dio = Dio();
    isLoading = true;
    try {
      final response = await dio.get(
        url,
        options: Options(responseType: ResponseType.bytes, headers: headers),
      );

      var type = "application/octet-stream"; // default type
      final uint = response.data;
      final magic = uint.sublist(0, 4);

      if (magic.every((b) => b == 0xff)) {
        type = "image/jpeg";
      } else if (magic[0] == 0x89 &&
          magic[1] == 0x50 &&
          magic[2] == 0x4e &&
          magic[3] == 0x47) {
        type = "image/png";
      } else if (magic[0] == 0x47 &&
          magic[1] == 0x49 &&
          magic[2] == 0x46 &&
          magic[3] == 0x38) {
        type = "image/gif";
      } else if (magic[0] == 0xd0 &&
          magic[1] == 0xcf &&
          magic[2] == 0x11 &&
          magic[3] == 0xe0) {
        type = "application/vnd.ms-excel";
      } else if (magic[0] == 0x25 &&
          magic[1] == 0x50 &&
          magic[2] == 0x44 &&
          magic[3] == 0x46) {
        type = "application/pdf";
      } else if (magic[0] == 0xd0 &&
          magic[1] == 0xcf &&
          magic[2] == 0x11 &&
          magic[3] == 0xe0) {
        type = "application/msword";
      }

      if (fileType[fileType.length - 1] == "pdf") {
        final blob = response.data;
        final blobUrl =
            Uri.dataFromBytes(blob, mimeType: "application/pdf").toString();
        var filePathName = "${apiUrl}";
        final filename = path.basename(filePathName);

        try {
          final directory = await getApplicationDocumentsDirectory();
          final file = File('${directory.path}/$filename');
          await file.writeAsBytes(response.data, flush: true);
          update();
          Get.to(PDFScreen(pFile: file, titleBarText: Strings.ticket));
          isLoading = false;
        } catch (e) {
          print('Error: $e');
        }
      } else {
        final blob = response.data;
        final blobUrl = Uri.dataFromBytes(blob, mimeType: type).toString();
        await Get.to(ImagePreviewScreen(
          url: blobUrl,
          titleBarTitle: Strings.ticket,
        ));
        isLoading = false;
      }
    } catch (e) {
      print("Error: $e");
      // Handle error
    }
  }

  getTicketStaffDetail(int? staffId) {
    isLoading = true;
    update();
    TicketSystemProvider().getTicketStaffDetail(
      staffId: staffId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TicketStaffDetailRes responseData =
                  TicketStaffDetailRes.fromJson(map);
              if (responseData.status != null && responseData.status == 200) {
                if (responseData.staff != null) {
                  // ticketStaffDetailDialog(Get.context!, responseData.staff!);
                  assignStaffParentId = responseData.staff!.parentStaffId;
                  log("assignStaffParentId2==>${responseData.staff!.parentStaffId}");
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
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }

  closeRemarkFollowUp({int? followUpId, String? remark}) {
    isLoading = true;
    update();
    TicketSystemProvider().ticketCloseFollowUp(
      followUpId: followUpId,
      remark: remark,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                getTicketListData(ticketId);
                Utils.showSnackbar(
                    Strings.SUCCESS,
                    responseData.responseMessage,
                    AppTheme.colorWhite,
                    AppTheme.colorGreen);
              } else {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
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
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
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

  approveRejectTicket(
      {required ViewTicketController viewTicketController,
      required String? status,
      required String? remark,
      required int? caseId,
      required BuildContext context}) {
    isLoading = true;
    update();
    PendingApprovalsProvider().approveRejectTickets(
      caseId: caseId,
      isApproveRequest: status!.equalsIgnoreCase(Strings.approve.toLowerCase())
          ? true
          : false,
      remark: remark,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ApproveRejectTicketRes responseData =
                  ApproveRejectTicketRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  approveRejectTicketList?.clear();
                  approveRejectTicketList?.addAll(responseData.dataList!);
                  showAssignStaffDialog(
                      responseData.dataList!, status, context, caseId);
                } else {
                  if (status.equalsIgnoreCase(Strings.reject)) {
                    Utils.showSnackbar(Strings.SUCCESS, "Reject Successfully.",
                        AppTheme.colorWhite, AppTheme.colorGreen);
                  } else {
                    Utils.showSnackbar(
                        Strings.SUCCESS,
                        "Approved Successfully.",
                        AppTheme.colorWhite,
                        AppTheme.colorGreen);
                  }
                  getTicketListData(ticketId);
                  if (Get.isRegistered<SavbillCareTabController>()) {
                    final savbillCareTabController =
                        Get.find<SavbillCareTabController>();
                    savbillCareTabController.page = 1;
                    savbillCareTabController.update();
                    savbillCareTabController.initPlatformState();
                  }
                  viewTicketController.clearFilter();
                  // Get.back(result: true);
                  // getCreditNoteListData();
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
        handleApiError(error);
      },
    );
  }

  showAssignStaffDialog(List<ApproveRejectTicketList> item, String? staffStatus,
      BuildContext context, int? caseId) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return TicketAssignDialog(
              ticketAssignAction: this,
              itemsOrgLst: item,
              entityId: caseId,
              staffStatus: staffStatus);
        });
  }

  @override
  void ticketAssignBtnAction(
      {ApproveRejectTicketList? selectedItem,
      bool? isStaffSelected,
      int? entityId,
      String? approveRejectStatus}) {
    Get.back();
    log("entityIdselectedItem==>${entityId}");
    if (isStaffSelected == true) {
      // log("Staff is selected");
      if (approveRejectStatus!.equalsIgnoreCase(Strings.approve)) {
        // log("Staff is selected!!!!!!!!=>${Strings.approve}");
        assignStaffCreditNote(entityId, selectedItem!.id, true);
      } else if (approveRejectStatus.equalsIgnoreCase(Strings.reject)) {
        // log("Staff is selected!!!!!!!!=>${Strings.reject}");
        assignStaffCreditNote(entityId, selectedItem!.id, false);
      }
    } else {
      // log("Not Staff is selected");
      if (approveRejectStatus!.equalsIgnoreCase(Strings.approve)) {
        // log("Not Staff is selected!!!!!!!!=>${Strings.approve}");
        assignEveryStaffCreditNote(entityId: entityId, isApprovedRequest: true);
      } else if (approveRejectStatus.equalsIgnoreCase(Strings.reject)) {
        // log("Not Staff is selected!!!!!!!!=>${Strings.reject}");
        assignEveryStaffCreditNote(
            entityId: entityId, isApprovedRequest: false);
      }
    }
  }

  assignStaffCreditNote(
      int? entityId, int? nextAssignStaff, bool? isApproveRequest) {
    String apiUrl =
        "${UrlConstants.assignFromStaffTicketList}?entityId=$entityId&eventName=CASE&nextAssignStaff=$nextAssignStaff&isApproveRequest=$isApproveRequest";
    isLoading = true;
    update();
    TicketSystemProvider().assignTicketEveryStaff(
      url: apiUrl,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          // if (responseModel.result != null) {
          // getTicketSearchOption();
          try {
            getTicketListData(entityId ?? 0);
            if (Get.isRegistered<SavbillCareTabController>()) {
              final savbillCareTabController =
                  Get.find<SavbillCareTabController>();
              savbillCareTabController.page = 1;
              savbillCareTabController.update();
              savbillCareTabController.initPlatformState();
            }
            if (Get.isRegistered<ViewTicketController>()) {
              final viewTicketController = Get.find<ViewTicketController>();
              viewTicketController.clearFilter();
            }
            Utils.showSnackbar(Strings.SUCCESS, Strings.successfully,
                AppTheme.colorWhite, AppTheme.colorGreen);
          } on Exception catch (e) {
            print(e.toString());
          }

          // }
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

  assignEveryStaffCreditNote({int? entityId, bool? isApprovedRequest}) {
    String apiUrl =
        "${UrlConstants.assignFromEveryStaffTicketList}?caseId=$entityId&remark=assign to everyone from list.&isApproveRequest=$isApprovedRequest";
    isLoading = true;
    update();
    TicketSystemProvider().assignTicketEveryStaff(
      url: apiUrl,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          // if (responseModel.result != null) {
          // getTicketSearchOption();
          try {
            getTicketListData(entityId ?? 0);
            if (Get.isRegistered<SavbillCareTabController>()) {
              final savbillCareTabController =
                  Get.find<SavbillCareTabController>();
              savbillCareTabController.page = 1;
              savbillCareTabController.update();
              savbillCareTabController.initPlatformState();
            }
            if (Get.isRegistered<ViewTicketController>()) {
              final viewTicketController = Get.find<ViewTicketController>();
              viewTicketController.clearFilter();
            }
            Utils.showSnackbar(Strings.SUCCESS, Strings.successfully,
                AppTheme.colorWhite, AppTheme.colorGreen);
          } on Exception catch (e) {
            print(e.toString());
          }
          // }
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

  checkTicketReAssign(
      TicketDetail? ticketDetail, ViewTicketController viewTicketController) {
    isLoading = true;
    update();
    TicketSystemProvider().checkReAssignTicket(
      caseId: ticketDetail?.caseId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CheckReassignTicketRes responseData =
                  CheckReassignTicketRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.data != null &&
                    responseData.data!.isNotEmpty &&
                    responseData.data!.equalsIgnoreCase("true")) {
                  openChangeProblemDomainScreen(
                      ticketDetail, viewTicketController);
                } else {
                  Utils.showSnackbar(
                      Strings.INFO,
                      "Not eligible to change problem domain..",
                      AppTheme.colorBlack,
                      AppTheme.colorBlueRView);
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
        update();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }

  openChangeProblemDomainScreen(TicketDetail? ticketDetail,
      ViewTicketController viewTicketController) async {
    var result = await Get.to(() => TicketChangeProblemDomain(), arguments: {
      Constant.TICKET_DETAIL: ticketDetail,
    });
    print("openChangeProblemDomainScreen $result");
    if (result != null && result == true) {
      initPlatformState();
      // viewTicketController.clearFilter();
    }
  }

  getStaffListData(int? itemCaseId) {
    isLoading = true;
    staffList!.clear();
    update();
    SavbillCareProvider().getTicketStaffLst(
      ticketId: itemCaseId!,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CaseStaffListRes responseData = CaseStaffListRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  staffList!.addAll(responseData.dataList!);
                  openCaseAssignScreen(this, staffList);
                } else {
                  Utils.showSnackbar(
                      Strings.INFO,
                      "No staff available to assign..",
                      AppTheme.colorWhite,
                      AppTheme.colorBlueRView);
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
        handleApiError(error);
      },
    );
  }

  openCaseAssignScreen(TicketDetailController ticketDetailController,
      List<CaseStaffDetail>? staffDetail) async {
    bool chkRefresh = await Get.to(() => CaseAssign(), arguments: {
      Constant.TICKET_DETAIL: ticketDetailController.ticketDetail,
      Constant.STAFF_DETAIL: staffList,
    });

    if (chkRefresh) {
      ticketDetailController
          .getTicketListData(ticketDetailController.ticketDetail?.caseId ?? 0);
      if (Get.isRegistered<SavbillCareTabController>()) {
        final savbillCareTabController = Get.find<SavbillCareTabController>();
        savbillCareTabController.page = 1;
        savbillCareTabController.update();
        savbillCareTabController.initPlatformState();
      }
      if (Get.isRegistered<ViewTicketController>()) {
        final viewTicketController = Get.find<ViewTicketController>();
        viewTicketController.clearFilter();
      }
    }
  }
}
