import 'dart:convert';
import 'dart:developer';
import 'dart:io';
import 'package:savbill/pages/customer_inventory/inventory_provider.dart';
import 'package:savbill/pages/customer_inventory/response/inventory_documentList_res.dart';
import 'package:savbill/pages/customer_invoice/image_preview_screen.dart';
import 'package:savbill/pages/customer_invoice/pdf_screen.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/ticket_system/ticket_system_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:dio/dio.dart';
import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:path/path.dart';
import 'package:path_provider/path_provider.dart';
import 'package:path/path.dart' as path;
import 'package:http/http.dart' as http;
import 'package:open_file/open_file.dart';
import 'dart:typed_data';

class TicketViewDocumentController extends GetxController {
  bool isLoading = false, checkBtnClickEvent = false, isChangeData = false;

  UserDetail userData = UserDetail();
  GetStorage getStorage = GetStorage();
  String? from;
  int tabIndex = 0;
  List<String> tabs = [
    'FAT Optical Power Picture',
    'FAT Inside Picture',
    'FAT Outside Picture',
    'ONU Optical Power Picture',
    'Optical Power Range',
    'Installation Picture',
    'Speedtest Picture'
  ];
  List<Map<String, dynamic>> opticalRangeData = [
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
  late TabController tabController;
  List<TextEditingController> latitudeControllers = [];
  List<TextEditingController> longitudeControllers = [];
  List<TextEditingController> sectionControllers = [];
  List<String?> selectedOpticalRanges = [];
  List<List<PlatformFile>> selectedFiles = [];
  List<DocumentLis>? documentList = [];
  // CustomerInventoryDataList? customerInventoryDataList;
  String? customerName = "";
  List<Tab> myTabs = [];
  bool submitted = false;
  String token = "";
  int page = 1;

  int? ticketId;

  List<Map<String, dynamic>> allSectionsData = [];

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
    initPlatformState();
  }

  setBtnClickEvent(bool status) {
    checkBtnClickEvent = status;
    update();
  }

  Future<void> initPlatformState() async {
    String strUserData = "";
    if (getStorage.hasData(Constant.USER_DATA)) {
      strUserData = await getStorage.read(Constant.USER_DATA);
    }
    if (!strUserData.isNullOrEmpty()) {
      userData = UserDetail.fromJson(jsonDecode(strUserData));
      update();
    }
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.FROM] != null) {
        from = arguments[Constant.FROM];
      }
      if (arguments[Constant.INVENTORY_ID] != null) {
        ticketId = arguments[Constant.INVENTORY_ID];
      }
      if (arguments[Constant.CUSTOMER_NAME] != null) {
        customerName = arguments[Constant.CUSTOMER_NAME];
      }
      if (ticketId != null) {
        getTicketDocumentList(ticketId);
      }
    }
    update();
  }

  getTicketDocumentList(int? ID) {
    documentList?.clear();
    isLoading = true;
    update();
    TicketSystemProvider().getTicketDocumentViewCall(
      ticketId: ID!,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              InventoryDocumentListRes responseData =
              InventoryDocumentListRes.fromJson(map);
              if (responseData.responseCode == 200 ||
                  responseData.status == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  documentList?.addAll(responseData.dataList!);
                }
              } else if (responseData.responseCode == 204) {
                Get.back(result: true);
                if (responseData.responseMessage != null &&
                    responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorRed);
                }
              } else if (responseData.responseCode == 404) {
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

  downloadFile(String? apiUrl, FileDetails items) async {
    isLoading = true;
    var url = "${apiUrl}";
    log("download fileurl==>>${apiUrl}");

    var fileType = items.fileName!.split(".");
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
        isLoading = true;
        try {
          final directory = await getApplicationDocumentsDirectory();
          final file = File('${directory.path}/$filename');
          await file.writeAsBytes(response.data, flush: true);
          // update();
          Get.to(PDFScreen(pFile: file, titleBarText: Strings.view_document));
          update();
          isLoading = false;
        } catch (e) {
          print('Error: $e');
        }
      } else {
        isLoading = true;
        final blob = response.data;
        final blobUrl = Uri.dataFromBytes(blob, mimeType: type).toString();
        await Get.to(() => ImagePreviewScreen(
          url: blobUrl,
          titleBarTitle: Strings.view_document,
        ));
        isLoading = false;
      }
    } catch (e) {
      print("Error: $e");
      // Handle error
    }
  }

  showInventoryDocData(
      String fileName,
      String uniqueName,
      String? networkPathUrl
      ) async {
    final fileType = fileName.split('.');
    isLoading = true;
    var url = "$networkPathUrl";
    String token = "";
    if (getStorage.hasData(Constant.USER_TOKEN)) {
      token = await getStorage.read(Constant.USER_TOKEN);
    }
    Map<String, String> headers = {
      'Content-type': 'application/json; charset=UTF-8',
      'Accept': 'application/json',
      'Authorization': 'Bearer $token'
    };
    log("Url===>>>${url}");

    try {
      final response = await http.get(Uri.parse("$url$ticketId/$fileName/$uniqueName/"), headers: headers,);

      if (response.statusCode == 200) {
        final Uint8List uint8list = response.bodyBytes;
        String type = "application/octet-stream"; // Default type
        final List<int> magic = uint8list.sublist(0, 4);

        // Determine file type based on magic bytes
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

        // Save the file locally and open it
        final tempDir = await getTemporaryDirectory();
        final filePath = "${tempDir.path}/$fileName";
        final file = File(filePath);

        await file.writeAsBytes(uint8list);

        // Open the file with an appropriate app
        final result = await OpenFile.open(file.path);
        if (result.type != ResultType.done) {
          log("Could not open file: ${result.message}");
        }
      } else if (response.statusCode == 404) {
        log("File Not Found");
      } else {
        log("Something went wrong!");
      }
    } catch (error) {
      log("An error occurred: $error");
    }
  }

  ticketDocumentDownload(
      String? uniqueName, String? sectionName, String? networkPathUrl,) async {
    isLoading = true;
    var url = "$networkPathUrl";
    final filename = basename(url);
    String token = "";
    if (getStorage.hasData(Constant.USER_TOKEN)) {
      token = await getStorage.read(Constant.USER_TOKEN);
    }
    Map<String, String> headers = {
      'Content-type': 'application/json; charset=UTF-8',
      'Accept': 'application/json',
      'Authorization': 'Bearer $token'
    };


    var fileName = uniqueName!.split(".");

    final response = await http
        .get(Uri.parse("$url/$ticketId/$uniqueName/$sectionName/"), headers: headers,);
    final bytes = response.bodyBytes;

   // var directory;
    Directory directory;
    if (Platform.isIOS) {
      directory = await getApplicationDocumentsDirectory();
    } else {
      directory = Directory("/storage/emulated/0/Download");
    }
    var file = File('${directory.path}/${customerName}_${fileName[0] ?? "-"}.png');
    file.writeAsBytes(bytes, flush: true);


    if (Platform.isIOS) {
      Utils.showSnackbar(Strings.SUCCESS, "File saved to → On My iPhone → ${Strings.app_name} folder.",
          AppTheme.colorWhite, AppTheme.colorGreen);
    } else {
      Utils.showSnackbar(Strings.SUCCESS, "File Downloaded Successfully Please Open Download Folder!!",
          AppTheme.colorWhite, AppTheme.colorGreen);
    }

    isLoading = false;
    // return pFile;
  }

  inventoryDocumentDelete(String? fileName, String? uniqueName,String? sectionName) {
    isLoading = true;
    update();
    TicketSystemProvider().ticketDownloadDeleteDocument(
      ticketId: ticketId,
      fileName: fileName,
      uniqueName: uniqueName,
      sectionName: sectionName,
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
                      responseData.responseCode == 200)) {
                Get.back(result: true);
                // Utils.showSnackbar(
                //     Strings.SUCCESS,
                //     responseData.responseMessage,
                //     AppTheme.colorWhite,
                //     AppTheme.colorGreen);
                getTicketDocumentList(ticketId);

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
      Utils.showSnackbar(Strings.INFO, error.message, AppTheme.colorWhite,
          AppTheme.colorBlueRView);
    }
    update();
  }
}