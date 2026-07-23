import 'dart:convert';
import 'dart:developer';
import 'dart:io';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/response/location_data_res.dart';
import 'package:savbill/pages/customer/model/response/location_lat_long_res.dart';
import 'package:savbill/pages/customer_inventory/inventory_provider.dart';
import 'package:savbill/pages/customer_inventory/request/upload_doc_inventory_req.dart';
import 'package:savbill/pages/customer_inventory/response/get_all_customer_inventory_list_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/file_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:dio/dio.dart' as dia;
import 'package:get_storage/get_storage.dart';

class DocumentUploadController extends GetxController {
  bool isLoading = false, checkBtnClickEvent = false, isChangeData = false;

  UserDetail userData = UserDetail();
  GetStorage getStorage = GetStorage();
  String? from;
  int tabIndex = 0;

  // FileDetail? fileDetail;
  List<FileDetail>? fileDetail = [];
  FileDetail? fileDetails;
  int? customerId;
  CustomerInventoryDataList? customerInventoryDataList;
  TextEditingController latController = TextEditingController();
  TextEditingController longController = TextEditingController();

  // TextEditingController fatInsideLatController = TextEditingController();
  // TextEditingController fatInsideLongController = TextEditingController();
  //
  // TextEditingController fatOutsideLatController = TextEditingController();
  // TextEditingController fatOutsideLongController = TextEditingController();
  //
  // TextEditingController onuOpticalLatController = TextEditingController();
  // TextEditingController onuOpticalLongController = TextEditingController();
  //
  // TextEditingController installationLatController = TextEditingController();
  // TextEditingController installationLongController = TextEditingController();
  //
  // TextEditingController speedTestLatController = TextEditingController();
  // TextEditingController speedTestLongController = TextEditingController();

  TextEditingController sectionNameController = TextEditingController();

  TabController? tabController;

  List<Tab> myTabs = [];
  List<String> tabs = [
    'FAT Optical Power Picture',
    'FAT Inside Picture',
    'FAT Outside Picture',
    'ONU Optical Power Picture',
    'Optical Power Range',
    'Installation Picture',
    'Speedtest Picture',
    'Smart Gadget'
  ];
  List<Map<String, String>> opticalRangeData = [
    {'label': '-15', 'value': '-15'},
    {'label': '-16', 'value': '-16'},
    {'label': '-17', 'value': '-17'},
    {'label': '-18', 'value': '-18'},
    {'label': '-19', 'value': '-19'},
    {'label': '-20', 'value': '-20'},
    {'label': '-21', 'value': '-21'},
    {'label': '-22', 'value': '-22'},
    {'label': '-23', 'value': '-23'},
    {'label': '-25', 'value': '-25'}
  ];
  List<String> requiredTabs = [];
  List<Map<String, dynamic>> allSectionsData = [];
  List<File> allFiles = [];

  // List<int> powerOpticalRange = [-15, -16, -17, -18, -19, -20, -21, -22, -23];
  String? selectPowerOpticalRange;
  LocationLatLong? locationData;
  LocationDetail? selectedLocation;

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
      if (arguments[Constant.CUSTOMER_ID] != null) {
        customerId = arguments[Constant.CUSTOMER_ID];
      }
      if (arguments[Constant.INVENTORY_ITEMS] != null) {
        customerInventoryDataList = arguments[Constant.INVENTORY_ITEMS];
        if (customerInventoryDataList != null &&
            !customerInventoryDataList!.status!
                .equalsIgnoreCase(Constant.ACTIVE)) {
          requiredTabs.clear();
          requiredTabs.add('FAT Optical Power Picture');
          requiredTabs.add('FAT Inside Picture');
          requiredTabs.add('FAT Outside Picture');
          requiredTabs.add('ONU Optical Power Picture');
          requiredTabs.add('Optical Power Range');
          requiredTabs.add('Installation Picture');
        }
      }
    }
    update();
  }

  // void uploadAllDocuments(List<Map<String, dynamic>> uploadDocForm) {
  //   // submitted = true;
  //   allSectionsData.clear();
  //   allFiles.clear();
  //
  //   for (int tabIndex = 0; tabIndex < uploadDocForm.length; tabIndex++) {
  //     uploadDocForm[tabIndex]['sectionName'] = tabs[tabIndex];
  //     bool isOpticalPowerRange = tabs[tabIndex] == 'Optical Power Range';
  //     var sectionData = collectSectionData(uploadDocForm[tabIndex], tabIndex);
  //     bool hasFiles = sectionData != null && sectionData['files'].isNotEmpty;
  //     bool isValid = uploadDocForm[tabIndex]['valid'] ||
  //         (isOpticalPowerRange && (hasFiles || uploadDocForm[tabIndex]['opticalRange'] != null));
  //
  //     if (isValid) {
  //       if (sectionData != null) {
  //         allSectionsData.add(sectionData['section']);
  //         allFiles.addAll(sectionData['files']);
  //       }
  //     }
  //   }
  //
  //   if (allSectionsData.isNotEmpty) {
  //
  //     log("allSectionsData===>>>${jsonEncode(allSectionsData)}");
  //
  //     log("allFiles===>>>${jsonEncode(allFiles)}");
  //
  //     // uploadDocuments(allSectionsData, allFiles);
  //   }
  // }

  // Map<String, dynamic>? collectSectionData(Map<String, dynamic> formGroup, int tabIndex) {
  //   Map<String, dynamic> section = {
  //     'name': formGroup['sectionName'],
  //     'latitude': formGroup['latitude'],
  //     'longitude': formGroup['longitude'],
  //     'opticalRange': formGroup['opticalRange'],
  //     'files': <File>[]
  //   };
  //
  //   if (tabIndex < fileDetail!.length) {
  //     section['files'] = fileDetail![tabIndex];
  //   }
  //
  //   return {'section': section, 'files': section['files']};
  // }

  // Future<void> uploadDocuments(List<Map<String, dynamic>> sectionsData, List<File> allFiles) async {
  //   var uri = Uri.parse("/inwards/inventory/document/upload/");
  //   var request = http.MultipartRequest("POST", uri);
  //   request.fields['customerInventoryId'] = inventoryIdData.toString();
  //
  //   for (int i = 0; i < sectionsData.length; i++) {
  //     request.fields['sections[$i].name'] = sectionsData[i]['name'];
  //     request.fields['sections[$i].latitude'] = sectionsData[i]['latitude'];
  //     request.fields['sections[$i].longitude'] = sectionsData[i]['longitude'];
  //     request.fields['sections[$i].opticalRange'] = sectionsData[i]['opticalRange'];
  //
  //     for (File file in sectionsData[i]['files']) {
  //       var stream = http.ByteStream(DelegatingStream.typed(file.openRead()));
  //       var length = await file.length();
  //       var multipartFile = http.MultipartFile(
  //         'sections[$i].files', stream, length,
  //         filename: basename(file.path),
  //       );
  //       request.files.add(multipartFile);
  //     }
  //   }
  //
  //   try {
  //     var response = await request.send();
  //     if (response.statusCode == 200) {
  //       print("Successfully uploaded");
  //     } else {
  //       print("Error uploading: ${response.statusCode}");
  //     }
  //   } catch (e) {
  //     print("Upload failed: $e");
  //   }
  // }

  void InventroyDocumentUpload() async {
    Map<String, dynamic> map = {};
    List<dia.MultipartFile> multipartFiles = [];
    List<SectionUploadRequest> sectionList = [];
    SectionUploadRequest addSectionUpload = SectionUploadRequest();

    for (int i = 0; i < tabs.length; i++) {
      sectionList.add(SectionUploadRequest(
          name: tabs[tabController!.index],
          latitude: latController.text,
          longitude: longController.text));
    }

    InventoryFileUploadRequest request = InventoryFileUploadRequest(
        customerInventoryId: customerId,
        opticalPowerRange: tabs[tabIndex],
        sections: sectionList);

    log("InventoryFileUploadRequest==>>>${jsonEncode(request)}");

    // for (int i = 0; i < tabs.length; i++) {
    //   map['sections[$i].name'] = sectionUploadRequest.name[i]['name'];
    //   map['sections[$i].latitude'] = sectionsData[i]['latitude'];
    //   map['sections[$i].longitude'] = sectionsData[i]['longitude'];
    //   map['sections[$i].opticalRange'] = sectionsData[i]['opticalRange'];
    //
    //   for (File file in sectionsData[i]['files']) {
    //     var stream = http.ByteStream(DelegatingStream.typed(file.openRead()));
    //     var length = await file.length();
    //     var multipartFile = http.MultipartFile(
    //       'sections[$i].files', stream, length,
    //       filename: basename(file.path),
    //     );
    //     request.files.add(multipartFile);
    //   }
    // }

    if (fileDetail != null && fileDetail!.isNotEmpty) {
      for (var detail in fileDetail!) {
        if (detail.filePathLocal != null && detail.filePathLocal!.isNotEmpty) {
          File f = File(detail.filePathLocal!);
          String fileName = f.path.split('/').last;
          dia.MultipartFile multipartFile =
              await dia.MultipartFile.fromFile(f.path, filename: fileName);
          multipartFiles.add(multipartFile);
          customerInventoryDataList!.filename = fileName;
          // If you need to save file names in your data list:
        }
      }
      map["file"] = multipartFiles;
    }

    map["customerInventoryMappingList"] = jsonEncode(customerInventoryDataList);
    dia.FormData formData = dia.FormData.fromMap(map);
    isLoading = true;
    update();
    InventoryProvider().inventoryUploadDocument(
      customerId: customerId!,
      formData: formData,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Map<String, dynamic> map = responseModel.result;
            BaseResponse responseData = BaseResponse.fromJson(map);
            if (responseData.responseCode != null &&
                (responseData.responseCode == 200 ||
                    responseData.responseCode == 406)) {
              showDialog(
                context: Get.context!,
                builder: (BuildContext context) {
                  return AlertDialogHelper(
                      title: Strings.INFO,
                      message: Strings.successfully,
                      positiveBtnText: Strings.ok,
                      negativeBtnText: "",
                      positiveBtnClick: () {
                        Get.back(result: true);
                        Get.back(result: true);
                      },
                      negativeBtnClick: () {
                        Get.back();
                      });
                },
              );
            } else {
              if (responseData.responseMessage != null &&
                  responseData.responseMessage!.isNotEmpty) {
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
        handleApiError(error);
      },
    );
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
}
