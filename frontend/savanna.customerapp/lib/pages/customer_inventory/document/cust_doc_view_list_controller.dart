import 'dart:developer';
import 'dart:io';
import 'dart:typed_data';
import 'package:savbill/pages/customer_inventory/inventory_provider.dart';
import 'package:savbill/pages/customer_inventory/response/get_all_customer_inventory_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:http/http.dart' as http;
import 'package:open_file/open_file.dart';
import 'package:path_provider/path_provider.dart';
import 'package:path/path.dart';

class CustDocViewListController extends GetxController {
  bool isLoading = false;
  int page = 1;
  bool isShowLoadMore = false;
  GetStorage getStorage = GetStorage();
  dynamic previewUrl;

  int? custDocumentViewId;
  CustomerInventoryDataList? customerInventoryDataList;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_DOCUMENT_VIEW_ID] != null) {
        custDocumentViewId = arguments[Constant.CUSTOMER_DOCUMENT_VIEW_ID];
      }

      if (arguments[Constant.INVENTORY_FILE_DATA] != null) {
        customerInventoryDataList = arguments[Constant.INVENTORY_FILE_DATA];
      }
    }
  }

  inventoryDocumentDownload(
      String? imgName, String? networkPathUrl, String? customerName) async {
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

    final response = await http
        .get(Uri.parse("$url$custDocumentViewId/$imgName"), headers: headers,);
    final bytes = response.bodyBytes;
    // var dir = await getApplicationDocumentsDirectory();
    var directory;
    if (Platform.isIOS) {
      directory = await getDownloadsDirectory();
    } else {
      directory = "/storage/emulated/0/Download/";
    }
    var file = File('$directory/$customerName$filename.png');
    await file.writeAsBytes(bytes, flush: true);
    Utils.showSnackbar(
        Strings.SUCCESS,
        "File Downloaded Successfully Please Open Download Folder!!",
        AppTheme.colorWhite,
        AppTheme.colorGreen);
    isLoading = false;
    // return pFile;
  }

  inventoryDocumentDelete(String? fileName, String? uniqueName) {
    isLoading = true;
    update();
    InventoryProvider().inventoryDownloadDocument(
      customerId: custDocumentViewId,
      fileName: fileName,
      uniqueName: uniqueName,
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
                Utils.showSnackbar(
                    Strings.SUCCESS,
                    responseData.responseMessage,
                    AppTheme.colorWhite,
                    AppTheme.colorGreen);

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


  Future<void> showInventoryDocData(
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
      final response = await http.get(Uri.parse("$url$custDocumentViewId/$uniqueName"), headers: headers,);

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

  handleApiError(ResponseModel error) {
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

  _handleApiError(ResponseModel error) {
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
