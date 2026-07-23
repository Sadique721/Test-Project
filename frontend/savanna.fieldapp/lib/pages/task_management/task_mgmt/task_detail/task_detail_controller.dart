import 'dart:convert';
import 'dart:developer';
import 'dart:io';

import 'package:savbill/pages/customer_inventory/response/inventory_work_flow_res.dart';
import 'package:savbill/pages/customer_invoice/image_preview_screen.dart';
import 'package:savbill/pages/customer_invoice/pdf_screen.dart';
import 'package:savbill/pages/dashboard/model/response/get_ticket_tat_report_res.dart';
import 'package:savbill/pages/dashboard/model/response/show_tat_details_res.dart';
import 'package:savbill/pages/dashboard/model/response/show_ticket_etr_report_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/task_management/model/response/task_followuo_list_response.dart';
import 'package:savbill/pages/task_management/model/response/view_task_detail_response.dart';
import 'package:savbill/pages/task_management/task_system_provider.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_staff_detail_res.dart';
import 'package:savbill/pages/ticket_system/ticket_system_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/acl_constant.dart';
import 'package:savbill/util/constant.dart';
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
import 'package:path_provider/path_provider.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:path/path.dart' as path;

import '../../model/response/view_task_response.dart';

class TaskDetailController extends GetxController {
  bool isLoading = false;
  TaskDetail? taskDetail;

  // List<TicketAttachments>? attachmentList = [];
  List<TaskFollowUpDetail>? followUpDetailList = [];
  List<CaseUpdateList>? caseUpdateList = [];
  List<WorkFlowAuditDataList>? workFlowAuditDataList = [];

  List<ShowTicketETRReportDataList>? showTicketETRReportList = [];
  List<GetTicketTATReportDataList>? showTicketTATReportList = [];

  int taskId = 0;
  bool isShowLoadMore = false;
  int page = 1;

  int? assignStaffParentId;

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

    // final initSetting = InitializationSettings(android: android, iOS: ios);

    // flutterLocalNotificationsPlugin!.initialize(initSetting,
    //     onDidReceiveNotificationResponse: (NotificationResponse payload) {
    //       log("onDidReceiveNotificationResponse==>${payload.payload}");
    //       if (payload.payload != null) OpenFile.open(payload.payload);
    //     }, onDidReceiveBackgroundNotificationResponse:
    //         (NotificationResponse notificationResponse) {
    //       if (notificationResponse.payload != null)
    //         OpenFile.open(notificationResponse.payload);
    //     });
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
      if (arguments[Constant.TASK_ID] != null) {
        taskId = arguments[Constant.TASK_ID];
        getTicketListData();
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

  getTicketListData() {
    isLoading = true;
    caseUpdateList!.clear();
    // attachmentList!.clear();
    update();
    TaskSystemProvider().getTaskDetail(
      id: taskId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ViewTaskDetailResponse responseData =
                  ViewTaskDetailResponse.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 0)) {
                log("message");
                taskDetail = responseData.data;
                if (taskDetail != null &&
                    taskDetail?.caseUpdateList != null &&
                    taskDetail!.caseUpdateList!.isNotEmpty) {
                  caseUpdateList = taskDetail!.caseUpdateList;
                }
                if (taskDetail != null &&
                    taskDetail?.caseDocDetails != null &&
                    taskDetail!.caseDocDetails!.isNotEmpty) {
                  // attachmentList?.addAll(taskDetail!.caseDocDetails!);
                }
                if (taskDetail?.currentAssigneeId != null) {
                  getTicketStaffDetail(taskDetail?.currentAssigneeId);
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
        getTicketFollowupDetail();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getTicketFollowupDetail();
      },
    );
  }

  getTicketFollowupDetail() {
    isLoading = true;
    update();
    TaskSystemProvider().getTaskFollowupDetail(
      id: taskId.toString(),
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TaskFollowupListResponse responseData =
                  TaskFollowupListResponse.fromJson(map);
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
        showTATDetailsCall();
      },
      onError: (ResponseModel error) {
        showTATDetailsCall();
        _handleApiError(error);
      },
    );
  }

  showTATDetailsCall() {
    isLoading = true;
    update();
    TaskSystemProvider().showTaskTATDetails(
      caseId: taskId.toString(),
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
        showETRReport();
      },
      onError: (ResponseModel error) {
        showETRReport();
        _handleApiError(error);
      },
    );
  }

  showETRReport() {
    isLoading = true;
    update();
    TaskSystemProvider().getTaskEtrReport(
      taskId: taskId,
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
        getTATReport();
      },
      onError: (ResponseModel error) {
        getTATReport();
        _handleApiError(error);
      },
    );
  }

  getTATReport() {
    isLoading = true;
    update();
    TaskSystemProvider().getTaskTATReport(
      caseId: taskId,
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
                // if (responseData.responseMessage!.isNotEmpty) {
                //   Utils.showSnackbar(
                //       Strings.ERROR,
                //       responseData.responseMessage,
                //       AppTheme.colorWhite,
                //       AppTheme.colorRed);
                // }
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

  Future<void> downloadFile(String? apiUrl, CaseDocDetails items) async {
    var url = "${apiUrl}";
    isLoading = true;
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
          Get.to(PDFScreen(pFile: file, titleBarText: Strings.task));
          isLoading = false;
        } catch (e) {
          print('Error: $e');
        }
      } else {
        final blob = response.data;
        final blobUrl = Uri.dataFromBytes(blob, mimeType: type).toString();
        await Get.to(ImagePreviewScreen(
          url: blobUrl,
          titleBarTitle: Strings.task,
        ));
        isLoading = false;
      }
    } catch (e) {
      isLoading = false;
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
                getTicketListData();
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
}
