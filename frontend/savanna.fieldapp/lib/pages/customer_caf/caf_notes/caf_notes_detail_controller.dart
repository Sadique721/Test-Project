import 'dart:convert';
import 'dart:developer';
import 'dart:io';
import 'package:pdf/widgets.dart' as pw;
import 'package:path_provider/path_provider.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:savbill/pages/customer_caf/response/cust_caf_notes_res.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import '../../../theme/app_theme.dart';
import '../../../util/constant.dart';
import '../../../util/strings.dart';
import '../../../util/utils.dart';
import '../../../webservices/response_model.dart';
import '../../customer/customer_provider.dart';
import '../../customer/model/response/customer_detail_response.dart';
import '../../login/model/response/user_detail.dart';
class CafNotesDetailController extends GetxController {
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

  Future<void> createAndSavePdf(List<Map<String, dynamic>> data) async {
    final pdf = pw.Document();

    if (data.isEmpty) return;

    final headers = data.first.keys.toList();

    // Build PDF table
    pdf.addPage(
      pw.Page(
        build: (context) {
          return pw.TableHelper.fromTextArray(
            border: pw.TableBorder.all(),
            headers: headers,
            data: data.map((row) => headers.map((key) => row[key].toString()).toList()).toList(),
          );
        },
      ),
    );

    // Get platform-specific directory
    Directory dir;

    if (Platform.isAndroid) {
      if (await Permission.storage.request().isGranted) {
        dir = (await getExternalStorageDirectory())!;
      } else {
        print("Permission denied");
        return;
      }
    } else {
      // iOS - Documents directory
      dir = await getApplicationDocumentsDirectory();
    }

    // Save the file
    final file = File('${dir.path}/api_data_${DateTime.now().millisecondsSinceEpoch}.pdf');
    await file.writeAsBytes(await pdf.save());

    print('PDF saved: ${file.path}');
  }


  Future<void> getCafAllNotes({required bool isExcel}) async {
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
                 //  String? savedFilePath =  await CustomerProvider().createAndSavePdf([responseModel.result],customerDetail?.custname,customerDetail?.acctno);

                  String? savedFilePath = "";
                  if (isExcel) {
                    savedFilePath =  await CustomerProvider().createAndSaveExcelSameLayout([responseModel.result],customerDetail?.custname,customerDetail?.acctno,
                        customerDetail?.serviceareaName,"Active", customerDetail?.areaName,customerDetail?.oltName);
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
          if (responseModel.message!.isNotEmpty) {
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
          if (responseModel.message!.isNotEmpty) {
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