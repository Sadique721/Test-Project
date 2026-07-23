import 'dart:convert';
import 'dart:developer';
import 'dart:io';
import 'package:device_info_plus/device_info_plus.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:get_storage/get_storage.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:savbill/pages/customer/location_list.dart';
import 'package:savbill/pages/customer/location_settings_dialog.dart';
import 'package:savbill/pages/customer/model/response/location_data_res.dart';
import 'package:savbill/pages/customer_inventory/document/document_upload_controller.dart';
import 'package:savbill/pages/model/file_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/permission_service.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/file_grid_item.dart';
import 'package:savbill/widgets/image_option_dialog.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/permisstion_deny_dialog.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:geolocator/geolocator.dart';
import 'package:get/get.dart';
import 'package:image_picker/image_picker.dart';
import 'package:dio/dio.dart' as dia;
import 'package:http/http.dart' as http;
import 'package:path/path.dart';
import 'package:http_parser/http_parser.dart';

class DocumentUploadScreen extends StatefulWidget {
  @override
  _UploadDocumentState createState() => _UploadDocumentState();
}

class _UploadDocumentState extends State<DocumentUploadScreen>
    with WidgetsBindingObserver, TickerProviderStateMixin
    implements
        ImageOptionBtnAction,
        LocationBtnAction,
        PermissionDenyBtnAction {
  final uploadDocumentController = Get.put(DocumentUploadController());
  final ImagePicker imagePicker = ImagePicker();
  GeolocatorPlatform geolocatorPlatform = GeolocatorPlatform.instance;
  GetStorage getStorage = GetStorage();

  late TabController _tabController;

  // List<String> tabs = [
  //   "FAT Optical Power Picture",
  //   "FAT Inside Picture",
  //   "Optical Power Range"
  // ];
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
  List<Map<String, dynamic>> opticalRangeData = [
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



  List<TextEditingController> latitudeControllers = [];
  List<TextEditingController> longitudeControllers = [];
  List<TextEditingController> sectionControllers = [];
  List<String?> selectedOpticalRanges = [];
  List<List<PlatformFile>> selectedFiles = [];
  List<Tab> myTabs = [];
  bool submitted = false;
  String token = "";

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back(result: uploadDocumentController.isChangeData);
  }

  @override
  void initState() {
    WidgetsBinding.instance.addObserver(this);
    super.initState();
    _tabController = TabController(length: tabs.length, vsync: this);
    for (var i = 0; i < tabs.length; i++) {
      sectionControllers.add(TextEditingController(text: tabs[i]));
      latitudeControllers.add(TextEditingController());
      longitudeControllers.add(TextEditingController());
      selectedOpticalRanges.add(null);
      selectedFiles.add([]);
    }

    myTabs = <Tab>[
      Tab(
        child: CustomText(
          colors: AppTheme.title_dark,
          fontSize: AppTheme.small,
          title: Strings.fat_optical_power_picture,
          textAlign: TextAlign.center,
        ),
      ),
      Tab(
        child: CustomText(
          colors: AppTheme.title_dark,
          fontSize: AppTheme.small,
          title: Strings.fat_inside_picture,
          textAlign: TextAlign.center,
        ),
      ),
      Tab(
        child: CustomText(
          colors: AppTheme.title_dark,
          fontSize: AppTheme.small,
          title: Strings.fat_outside_picture,
          textAlign: TextAlign.center,
        ),
      ),
      Tab(
        child: CustomText(
          colors: AppTheme.title_dark,
          fontSize: AppTheme.small,
          title: Strings.onu_optical_power_picture,
          textAlign: TextAlign.center,
        ),
      ),
      Tab(
        child: CustomText(
          colors: AppTheme.title_dark,
          fontSize: AppTheme.small,
          title: Strings.optical_power_range,
          textAlign: TextAlign.center,
        ),
      ),
      Tab(
        child: CustomText(
          colors: AppTheme.title_dark,
          fontSize: AppTheme.small,
          title: Strings.installation_picture,
          textAlign: TextAlign.center,
        ),
      ),
      Tab(
        child: CustomText(
          colors: AppTheme.title_dark,
          fontSize: AppTheme.small,
          title: Strings.speed_test_picture,
          textAlign: TextAlign.center,
        ),
      ),
      Tab(
        child: CustomText(
          colors: AppTheme.title_dark,
          fontSize: AppTheme.small,
          title: Strings.smart_gadget,
          textAlign: TextAlign.center,
        ),
      ),
    ];

    if (getStorage.hasData(Constant.USER_TOKEN)) {
      token = getStorage.read(Constant.USER_TOKEN);
    }
  }

  void getCurrentLocation(int tabIndex) {
    // Get and set current location logic here
    print("Get location for tab $tabIndex");
  }

  void selectFiles(int tabIndex) async {
    final result = await FilePicker.platform
        .pickFiles(allowMultiple: true, withData: true);
    if (result != null) {
      setState(() {
        log("result  ++++++++++++==>>${result}");
        selectedFiles[tabIndex].addAll(result.files);
      });
    }
  }

  void removeFile(int tabIndex, int index) {
    setState(() {
      selectedFiles[tabIndex].removeAt(index);
    });
  }

  //
  // void uploadAllDocuments() {
  //   setState(() => submitted = true);
  //   // Upload logic here
  //   print("Uploading all documents...");
  // }

  void uploadAllDocuments() {
    setState(() => submitted = true);
    List<Map<String, dynamic>> allSectionsData = [];
    List<PlatformFile> allFiles = [];

    bool allRequiredTabsValid = true;

    for (int tabIndex = 0; tabIndex < tabs.length; tabIndex++) {
      final tabName = tabs[tabIndex];
      final isOpticalPowerRange = tabName == 'Optical Power Range';

      final hasFiles = selectedFiles[tabIndex].isNotEmpty;
      final hasOpticalRange = selectedOpticalRanges[tabIndex] != null &&
          selectedOpticalRanges[tabIndex]!.isNotEmpty;

      bool isValid = false;

      if (isOpticalPowerRange) {
        isValid = hasFiles || hasOpticalRange;
      } else {
        final latValid = latitudeControllers[tabIndex].text.trim().isNotEmpty;
        final longValid = longitudeControllers[tabIndex].text.trim().isNotEmpty;
        isValid = latValid && longValid && hasFiles;
      }

      if (uploadDocumentController.requiredTabs.contains(tabName) && !isValid) {
        allRequiredTabsValid = false;
        debugPrint("❌ Required tab \"$tabName\" is not valid. Please fill it.");
      }

      if (isValid) {
        Map<String, dynamic> sectionData = {
          "sectionName": tabName,
          "latitude": latitudeControllers[tabIndex].text.trim(),
          "longitude": longitudeControllers[tabIndex].text.trim(),
          "opticalRange": selectedOpticalRanges[tabIndex],
        };
        sectionData['files'] = selectedFiles[tabIndex];
        allSectionsData.add(sectionData);
        allFiles.addAll(selectedFiles[tabIndex]);
      } else {
         if(hasFiles) {
           Utils.showSnackbar(
             Strings.ERROR,
             "Latitude,Longitude and Image required for selected tab",
             AppTheme.colorWhite,
             AppTheme.colorRed,
           );
           return;
         } else if(latitudeControllers[tabIndex].text.trim().isNotEmpty && longitudeControllers[tabIndex].text.trim().isNotEmpty) {
           Utils.showSnackbar(
             Strings.ERROR,
             "Latitude,Longitude and Image required for selected tab",
             AppTheme.colorWhite,
             AppTheme.colorRed,
           );
           return;
         }
      }
    }
    // requiredTabs.add('FAT Optical Power Picture');
    // requiredTabs.add('FAT Inside Picture');
    // requiredTabs.add('FAT Outside Picture');
    // requiredTabs.add('ONU Optical Power Picture');
    // requiredTabs.add('Optical Power Range');
    // requiredTabs.add('Installation Picture');

    if (!allRequiredTabsValid) {
      Utils.showSnackbar(
        Strings.ERROR,
        "Please fill required sections: FAT Optical Power Picture,FAT Inside Picture,FAT Outside Picture,ONU Optical Power Picture, Optical Power Range, Installation Picture.",
        AppTheme.colorWhite,
        AppTheme.colorRed,
      );
      return;
    }

    if (allSectionsData.isNotEmpty) {
      uploadDocumentController.isLoading = true;
      uploadDocuments(allSectionsData, allFiles,
          uploadDocumentController.customerInventoryDataList!.id);
    } else {
      Utils.showSnackbar(
        Strings.ERROR,
        "No valid section data.Please complete at least one field.",
        AppTheme.colorWhite,
        AppTheme.colorRed,
      );
    }
  }

  Future<void> uploadDocuments(List<Map<String, dynamic>> sectionsData,
      List<PlatformFile> allFiles, int? inventoryId) async {
    final url = Uri.parse(UrlConstants.cust_inventory_upload_doc);
    final request = http.MultipartRequest('POST', url);
    request.headers['Authorization'] = token;
    request.headers['x-access-token'] = '';
    request.headers['Content-Type'] = 'multipart/form-data';
    // request.headers['requestFrom'] = 'gui';
    request.fields['customerInventoryId'] = inventoryId.toString();

    for (int i = 0; i < sectionsData.length; i++) {
      final section = sectionsData[i];
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

    try {
      uploadDocumentController.isLoading = true;
      final streamedResponse = await request.send();
      final response = await http.Response.fromStream(streamedResponse);
      final responseData = jsonDecode(response.body);
      log("response body => ${response.body}"); // ✅ correct
      if (response.statusCode == 200) {
        if (responseData['responseCode'] == 406 ||
            responseData['responseCode'] == 417) {
          debugPrint('❌ Error: ${responseData['responseMessage']}');
          Utils.showSnackbar(
            Strings.ERROR,
            responseData['responseMessage'],
            AppTheme.colorWhite,
            AppTheme.colorRed,
          );
        } else {
          // Utils.showSnackbar(Strings.SUCCESS, Strings.successfully,
          //     AppTheme.colorWhite, AppTheme.colorGreen);
          debugPrint('✅ Success: ${responseData['message']}');
          Get.back(result: true);
        }
      } else {
        Utils.showSnackbar(
          Strings.ERROR,
          "Failed.",
          AppTheme.colorWhite,
          AppTheme.colorRed,
        );
        debugPrint('❌ Server Error: ${response.body}');
        // Show server error UI
      }
      uploadDocumentController.isLoading = false;
      uploadDocumentController.update();
    } catch (e) {
      Utils.showSnackbar(
        Strings.ERROR,
        "Failed.",
        AppTheme.colorWhite,
        AppTheme.colorRed,
      );
      debugPrint('❌ Exception during upload: $e');
      // Show error UI
      uploadDocumentController.isLoading = false;
      uploadDocumentController.update();
    }
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    switch (state) {
      case AppLifecycleState.paused:
        return;
      case AppLifecycleState.resumed:
        if (uploadDocumentController.checkBtnClickEvent) {
          uploadDocumentController.setBtnClickEvent(false);
          // checkCameraPermission();
        }
        return;
      default:
        return;
    }
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<DocumentUploadController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(context),
          ),
          ProgressBar(isLoader: uploadDocumentController.isLoading),
        ]);
      }), /**/
    );
  }

  _body(BuildContext context) {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: Container(
        color: AppTheme.colorBG,
        width: MediaQuery.of(context).size.width,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            Container(
              margin: const EdgeInsets.symmetric(
                horizontal: 0,
              ),
              height: Constant.TABBAR_HEIGHT,
              decoration: BoxDecoration(
                color: AppTheme.colorTransparent,
                border: Border(
                    bottom: BorderSide(
                        color: AppTheme.title_dark.withOpacity(0.9),
                        width: Constant.TABBAR_BOTTOM_LINE_H)),
              ),
              child: TabBar(
                isScrollable: true,
                labelPadding: EdgeInsets.symmetric(horizontal: 10.0),
                controller: _tabController,
                tabAlignment: TabAlignment.start,
                unselectedLabelColor: AppTheme.title_dark.withOpacity(0.8),
                indicator: UnderlineTabIndicator(
                  borderSide: BorderSide(
                      width: Constant.TAB_INDICATOR_H,
                      color: AppTheme.colorPrimary),
                ),
                labelColor: AppTheme.title_dark,
                labelStyle: const TextStyle(
                    fontSize: AppTheme.large, fontWeight: FontWeight.w600),
                unselectedLabelStyle: const TextStyle(
                    fontSize: AppTheme.medium, fontWeight: FontWeight.w500),
                tabs: myTabs,
              ),
            ),
            Flexible(
              child: TabBarView(
                controller: _tabController,
                children: List.generate(tabs.length, (tabIndex) {
                  return SingleChildScrollView(
                    child: Padding(
                      padding: const EdgeInsets.all(
                        Constant.SMALL_PADDING,
                      ),
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.start,
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Padding(
                            padding: const EdgeInsets.only(
                                top: Constant.EXPANTABLE_ITEM_MARGIN,
                                left: Constant.EXPANTABLE_ITEM_MARGIN,
                                right: Constant.EXPANTABLE_ITEM_MARGIN,
                                bottom: 0),
                            child: Container(
                              alignment: Alignment.topLeft,
                              padding:
                                  const EdgeInsets.all(Constant.SMALL_PADDING),
                              child: Column(
                                  mainAxisAlignment: MainAxisAlignment.start,
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    titleWidget(Strings.section_name),
                                    const SizedBox(
                                        height: Constant.SMALL_PADDING - 2),
                                    Container(
                                      // padding: EdgeInsets.symmetric(
                                      //     vertical: Constant.SMALL_PADDING,
                                      //     horizontal: Constant.MEDIUM_PADDING),
                                      decoration: BoxDecoration(
                                          color: AppTheme.colorLightGrey,
                                          borderRadius:
                                              BorderRadius.circular(4)),
                                      child: CoustomTextField(
                                          labelText: Strings.section_name,
                                          hintColor: AppTheme.colorIconGrey,
                                          textEditingController:
                                              sectionControllers[tabIndex],
                                          borderEnableColors:
                                              AppTheme.colorIconGrey,
                                          borderFocusColors:
                                              AppTheme.colorIconGrey,
                                          textColor: AppTheme.colorBlack,
                                          keyboardType: TextInputType.text,
                                          fontSize: AppTheme.small,
                                          textInputAction: TextInputAction.next,
                                          fontWeight: FontWeight.w500,
                                          contentPadding:
                                              const EdgeInsets.symmetric(
                                                  horizontal:
                                                      Constant.MEDIUM_PADDING,
                                                  vertical:
                                                      Constant.MEDIUM_PADDING),
                                          borderCorner:
                                              Constant.BTN_ROUNDED_CORNER,
                                          onTextValidator: (String? value) {
                                            if (value!.isEmpty) {
                                              return Strings
                                                  .please_enter_first_name;
                                            }
                                            return null;
                                          },
                                          isEnable: false,
                                          onTextFiledOnTap: () {},
                                          readOnly: false),

                                      // CustomText(
                                      //   title: sectionName,
                                      //   colors: AppTheme.title_dark,
                                      //   textAlign: TextAlign.start,
                                      //   fontSize: AppTheme.small + 1,
                                      //   fontWeight: FontWeight.w400,
                                      //   maxLines: 2,
                                      // ),
                                    ),
                                    SizedBox(
                                      height: Constant.MEDIUM_PADDING,
                                    ),
                                    if (tabs[tabIndex] !=
                                        "Optical Power Range") ...[
                                      Column(
                                        children: [
                                          Row(
                                            crossAxisAlignment:
                                                CrossAxisAlignment.end,
                                            mainAxisAlignment:
                                                MainAxisAlignment.start,
                                            children: [
                                              Expanded(
                                                flex: 1,
                                                child: Column(
                                                  crossAxisAlignment:
                                                      CrossAxisAlignment.start,
                                                  mainAxisAlignment:
                                                      MainAxisAlignment.start,
                                                  children: [
                                                    // titleWidget(
                                                    //     Strings.latitude),
                                                    titleWithRequireWidget(
                                                        Strings.latitude,
                                                        uploadDocumentController.requiredTabs.contains(
                                                            tabs[tabIndex])),
                                                    const SizedBox(
                                                        height: Constant
                                                            .SMALL_PADDING),
                                                    valueWidget(
                                                        latitudeControllers[
                                                            tabIndex],
                                                        Strings.latitude),
                                                  ],
                                                ),
                                              ),
                                              SizedBox(
                                                width: Constant.SMALL_PADDING,
                                              ),
                                              Expanded(
                                                flex: 1,
                                                child: Column(
                                                  crossAxisAlignment:
                                                      CrossAxisAlignment.start,
                                                  mainAxisAlignment:
                                                      MainAxisAlignment.start,
                                                  children: [
                                                    titleWithRequireWidget(
                                                        Strings.longitude,
                                                        uploadDocumentController.requiredTabs.contains(
                                                            tabs[tabIndex])),
                                                    const SizedBox(
                                                        height: Constant
                                                            .SMALL_PADDING),
                                                    valueWidget(
                                                        longitudeControllers[
                                                            tabIndex],
                                                        Strings.longitude),
                                                  ],
                                                ),
                                              ),
                                              SizedBox(
                                                width:
                                                    Constant.VERY_SMALL_PADDING,
                                              ),
                                              Expanded(
                                                child: Row(
                                                  mainAxisAlignment:
                                                      MainAxisAlignment.center,
                                                  crossAxisAlignment:
                                                      CrossAxisAlignment.center,
                                                  children: [
                                                    InkWell(
                                                      onTap: () {
                                                        //get current location
                                                        locationPermissionStatus(
                                                            tabs[tabIndex],
                                                            tabIndex);
                                                      },
                                                      child: Material(
                                                        elevation: 1.5,
                                                        color: AppTheme
                                                            .custNearLocationLight,
                                                        shape: RoundedRectangleBorder(
                                                            borderRadius: BorderRadius
                                                                .circular(Constant
                                                                    .BTN_ROUNDED_CORNER)),
                                                        child: Container(
                                                          height: Constant
                                                              .BTN_HEIGHT_M,
                                                          width: Constant
                                                              .BTN_HEIGHT_M,
                                                          alignment:
                                                              Alignment.center,
                                                          padding: const EdgeInsets
                                                              .all(
                                                              Constant.SMALL_PADDING -
                                                                  1),
                                                          child:
                                                              SvgPicture.asset(
                                                            currentLocationSvg,
                                                            height: Constant
                                                                .ICON_SIZE,
                                                            width: Constant
                                                                .ICON_SIZE,
                                                            color: AppTheme
                                                                .custNearLocationDark,
                                                            fit: BoxFit.fill,
                                                          ),
                                                        ),
                                                      ),
                                                    ),
                                                    const SizedBox(
                                                        width: Constant
                                                            .MEDIUM_PADDING),
                                                    InkWell(
                                                      onTap: () {
                                                        openLocationListScreen();
                                                      },
                                                      child: Material(
                                                        elevation: 1.5,
                                                        color: AppTheme
                                                            .custChangeStatusLight,
                                                        shape: RoundedRectangleBorder(
                                                            borderRadius: BorderRadius
                                                                .circular(Constant
                                                                    .BTN_ROUNDED_CORNER)),
                                                        child: Container(
                                                          height: Constant
                                                              .BTN_HEIGHT_M,
                                                          width: Constant
                                                              .BTN_HEIGHT_M,
                                                          alignment:
                                                              Alignment.center,
                                                          padding: const EdgeInsets
                                                              .all(
                                                              Constant.SMALL_PADDING -
                                                                  1),
                                                          child:
                                                              SvgPicture.asset(
                                                            searchLocationSvg,
                                                            height: Constant
                                                                .ICON_SIZE,
                                                            width: Constant
                                                                .ICON_SIZE,
                                                            color: AppTheme
                                                                .custChangeStatusDark,
                                                            fit: BoxFit.fill,
                                                          ),
                                                        ),
                                                      ),
                                                    )
                                                  ],
                                                ),
                                              )
                                            ],
                                          ),
                                          const SizedBox(
                                              height: Constant.LARGE_PADDING),
                                          GestureDetector(
                                            onTap: () {
                                              checkCameraPermission(tabIndex);
                                              // selectFiles(tabIndex);
                                            },
                                            child: Row(
                                              mainAxisSize: MainAxisSize.max,
                                              crossAxisAlignment:
                                                  CrossAxisAlignment.center,
                                              mainAxisAlignment:
                                                  MainAxisAlignment.start,
                                              children: [
                                                Icon(
                                                  Icons
                                                      .add_circle_outline_rounded,
                                                  color: AppTheme.title_dark,
                                                  size: 18,
                                                ),
                                                CustomText(
                                                  title:
                                                      " ${Strings.select_file_to_upload}* :",
                                                  colors: AppTheme.title_dark,
                                                  textAlign: TextAlign.center,
                                                  fontSize: AppTheme.small + 1,
                                                  fontWeight: FontWeight.w500,
                                                ),
                                              ],
                                            ),
                                          ),
                                          const SizedBox(
                                            height: Constant.LARGE_PADDING,
                                          ),
                                          // fileViewWidget(),

                                          // ElevatedButton(
                                          //   onPressed: () => selectFiles(tabIndex),
                                          //   child: Text("Select Files to Upload"),
                                          // ),
                                          Column(
                                            children: selectedFiles[tabIndex]
                                                .asMap()
                                                .entries
                                                .map((entry) {
                                              final index = entry.key;
                                              final file = entry.value;
                                              return ListTile(
                                                title: Text(file.name),
                                                trailing: IconButton(
                                                  icon: Icon(Icons.close),
                                                  onPressed: () => removeFile(
                                                      tabIndex, index),
                                                ),
                                              );
                                            }).toList(),
                                          ),
                                        ],
                                      )
                                    ],
                                    if (tabs[tabIndex] == "Optical Power Range")
                                      Column(
                                        crossAxisAlignment:
                                            CrossAxisAlignment.center,
                                        mainAxisAlignment:
                                            MainAxisAlignment.spaceBetween,
                                        children: [
                                          const SizedBox(
                                            height: Constant.SMALL_PADDING,
                                          ),
                                          titleWithRequireWidget(
                                              "${Strings.optical_power_range}(In db)",
                                              uploadDocumentController.requiredTabs
                                                  .contains(tabs[tabIndex])),
                                          const SizedBox(
                                            height: Constant.SMALL_PADDING,
                                          ),
                                          DropdownButtonHideUnderline(
                                            child: DropdownButtonFormField(
                                              icon: SvgPicture.asset(
                                                downArrowSvg,
                                                height: Constant
                                                    .DROP_DOWN_ARROW_W_H,
                                                width: Constant
                                                    .DROP_DOWN_ARROW_W_H,
                                                color: AppTheme.colorBlack,
                                                fit: BoxFit.fill,
                                              ),
                                              decoration: Utils.ddlDecoration(),
                                              hint: Align(
                                                  alignment:
                                                      Alignment.centerLeft,
                                                  child: Text(
                                                      Strings
                                                          .select_optical_power_range,
                                                      style: TextStyle(
                                                        fontSize:
                                                            AppTheme.medium,
                                                        color: AppTheme
                                                            .colorIconGrey,
                                                        fontFamily: AppTheme
                                                            .appFontName,
                                                      ))),
                                              style: AppTheme.dropdownTextStyle,
                                              isExpanded: true,
                                              isDense: true,
                                              value: selectedOpticalRanges[
                                                  tabIndex],
                                              items: uploadDocumentController
                                                  .opticalRangeData
                                                  .map((Map<String, String>
                                                      value) {
                                                return DropdownMenuItem<String>(
                                                  value: value['label'],
                                                  child: Align(
                                                    alignment:
                                                        Alignment.centerLeft,
                                                    child: CustomText(
                                                      title: value['value'],
                                                      colors:
                                                          AppTheme.colorBlack,
                                                      textAlign:
                                                          TextAlign.start,
                                                      fontSize: AppTheme.small,
                                                      fontWeight:
                                                          FontWeight.w500,
                                                    ), //Text(value.desig!),
                                                  ),
                                                );
                                              }).toList(),
                                              onChanged: (String? value) {
                                                setState(() {
                                                  selectedOpticalRanges[
                                                      tabIndex] = value;
                                                });
                                              },
                                              validator: (value) {
                                                // if (value == null ||
                                                //     uploadDocumentController.selectPowerOpticalRange == null) {
                                                //   return Strings.select_optical_power_range;
                                                // }
                                                return null;
                                              },
                                            ),
                                          ),
                                        ],
                                      ),
                                    const SizedBox(
                                      height: Constant.MEDIUM_PADDING,
                                    ),
                                  ]),
                            ),
                          ),
                        ],
                      ),
                    ),
                  );
                }),
              ),
            ),
            // Expanded(
            //   child: TabBarView(
            //     controller: _tabController,
            //     children: List.generate(tabs.length, (tabIndex) {
            //       return SingleChildScrollView(
            //         padding: EdgeInsets.all(16),
            //         child: Column(
            //           children: [
            //             TextFormField(
            //               controller: sectionControllers[tabIndex],
            //               decoration:
            //                   InputDecoration(labelText: "Section Name"),
            //               enabled: false,
            //             ),
            //             if (tabs[tabIndex] != "Optical Power Range") ...[
            //               TextFormField(
            //                 controller: latitudeControllers[tabIndex],
            //                 decoration: InputDecoration(labelText: "Latitude"),
            //               ),
            //               TextFormField(
            //                 controller: longitudeControllers[tabIndex],
            //                 decoration: InputDecoration(labelText: "Longitude"),
            //               ),
            //               IconButton(
            //                 icon: Icon(Icons.my_location),
            //                 tooltip: "Get Current Location",
            //                 onPressed: () => getCurrentLocation(tabIndex),
            //               ),
            //               ElevatedButton(
            //                 onPressed: () => selectFiles(tabIndex),
            //                 child: Text("Select Files to Upload"),
            //               ),
            //               Column(
            //                 children: selectedFiles[tabIndex]
            //                     .asMap()
            //                     .entries
            //                     .map((entry) {
            //                   final index = entry.key;
            //                   final file = entry.value;
            //                   return ListTile(
            //                     title: Text(file.name),
            //                     trailing: IconButton(
            //                       icon: Icon(Icons.close),
            //                       onPressed: () => removeFile(tabIndex, index),
            //                     ),
            //                   );
            //                 }).toList(),
            //               ),
            //               if (submitted && selectedFiles[tabIndex].isEmpty)
            //                 Text("File is required",
            //                     style: TextStyle(color: Colors.red)),
            //             ],
            //             if (tabs[tabIndex] == "Optical Power Range")
            //               DropdownButtonFormField<String>(
            //                 value: selectedOpticalRanges[tabIndex],
            //                 items: opticalRangeData.map((opt) {
            //                   return DropdownMenuItem<String>(
            //                     value: opt['value'],
            //                     child: Text(opt['label']),
            //                   );
            //                 }).toList(),
            //                 onChanged: (val) {
            //                   setState(
            //                       () => selectedOpticalRanges[tabIndex] = val);
            //                 },
            //                 decoration: InputDecoration(
            //                     labelText: "Optical Power range (In db) *"),
            //               ),
            //           ],
            //         ),
            //       );
            //     }),
            //   ),
            // ),
            Row(
              children: [
                Expanded(
                  child: SimpleButton(
                    onTap: () {
                      uploadAllDocuments();
                    },
                    radius: 0,
                    height: Constant.BOTTOM_BTN_HEIGHT,
                    bgColors: AppTheme.colorPrimary,
                    borderColors: AppTheme.colorPrimary,
                    child: CustomText(
                      title: Strings.upload,
                      fontSize: AppTheme.medium,
                      fontWeight: FontWeight.w400,
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  validateForm() {
    if (uploadDocumentController.fileDetail != null &&
        uploadDocumentController.fileDetail!.isNotEmpty) {
      uploadDocumentController.InventroyDocumentUpload();
    } else {
      Utils.showSnackbar(Strings.ERROR, "Please select the upload file",
          AppTheme.colorWhite, AppTheme.colorRed);
    }
  }

  // fileViewWidget() {
  //   return uploadDocumentController.fileDetail != null
  //       ? FileGridItem(
  //     fileDetail: uploadDocumentController.fileDetail!,
  //     onTapItem: () {},
  //     bottomAction: fileItemAction(),
  //   )
  //       : Container();
  // }

  fileViewWidget() {
    return (uploadDocumentController.fileDetail != null &&
            uploadDocumentController.fileDetail!.isNotEmpty)
        ? ListView.builder(
            scrollDirection: Axis.vertical,
            shrinkWrap: true,
            itemCount: uploadDocumentController.fileDetail!.length,
            itemBuilder: (context, index) {
              FileDetail data = uploadDocumentController.fileDetail![index];
              return FileGridItem(
                fileDetail: data,
                onTapItem: () {},
                bottomAction: fileItemAction(index),
              );
            })
        : Container();
  }

  fileItemAction(int? index) {
    return uploadDocumentController.fileDetail != null &&
            uploadDocumentController.fileDetail!.isNotEmpty == true
        ? Align(
            alignment: Alignment.topRight,
            child: InkWell(
              onTap: () {
                uploadDocumentController.fileDetail!.removeAt(index!);
                uploadDocumentController.update();
              },
              child: Container(
                height: 22,
                width: 22,
                decoration: BoxDecoration(
                  color: AppTheme.colorRed,
                  border: Border.all(
                    color: AppTheme.colorWhite,
                  ),
                  borderRadius: BorderRadius.circular(30.0),
                ),
                child: Center(
                  child: Icon(
                    Icons.close,
                    color: AppTheme.colorWhite,
                    size: 14,
                  ),
                ),
              ),
            ))
        : Container();
  }

  // checkCameraPermission() async {
  //   PermissionService().requestCameraAndStoragePermission(
  //       onPermissionDenied: () {
  //     if (Platform.isIOS) {
  //       uploadImageOption();
  //     } else {
  //       permissionDenyDialog();
  //     }
  //   }, onPermissionSuccess: () {
  //     uploadImageOption();
  //   });
  // }

  Future<void> checkCameraPermission(int tabIndex) async {
    bool isAndroid13OrAbove = false;
    if (Platform.isAndroid) {
      var androidInfo = await DeviceInfoPlugin().androidInfo;
      isAndroid13OrAbove = androidInfo.version.sdkInt >= 33;
    }
    var storagePermission =
        isAndroid13OrAbove ? Permission.photos : Permission.storage;
    var cameraStatus = await Permission.camera.status;
    var storageStatus = await storagePermission.status;

    if (!cameraStatus.isGranted || !storageStatus.isGranted) {
      Map<Permission, PermissionStatus> statuses = await [
        Permission.camera,
        storagePermission,
      ].request();

      if (statuses[Permission.camera]!.isDenied ||
          statuses[storagePermission]!.isDenied) {
        print("❌ Camera or storage permission denied!");
        return;
      }
    }

    // ✅ Open Camera After Permission Granted
    final ImagePicker picker = ImagePicker();
    final XFile? image =
        await picker.pickImage(source: ImageSource.camera, imageQuality: 20);

    if (image != null) {
      File file = File(image.path);
      // Wrap it as a PlatformFile manually
      PlatformFile platformFile = PlatformFile(
        name: basename(file.path),
        path: file.path,
        size: await file.length(),
      );
      int fileSizeInBytes = await file.length();
      double fileSizeInMB = fileSizeInBytes / (1024 * 1024);
      debugPrint("📸 File size: ${fileSizeInMB.toStringAsFixed(2)} MB");
      setState(() {
        selectedFiles[tabIndex].add(platformFile);
        debugPrint("📸 Image captured and added: ${file.path}");
      });
      debugPrint("✅ Image Captured: ${image.path}");
    } else {
      debugPrint("❌ No Image Selected");
    }
  }

  void uploadImageOption() async {
    showDialog(
        context: Get.context!,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return ImageOptionDialog(
              imageOptionBtnAction: this,
              showFileSelect: false,
              showGallerySelect: false,
              showCameraSelect: true);
        });
  }

  void permissionDenyDialog() async {
    showDialog(
        context: Get.context!,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return PermissionDenyDialog(
              permissionDenyBtnAction: this,
              titleMsg: Strings.camera_storage_permission_denied_msg);
        });
  }

  @override
  void btnClickAction({String? btnIdentifier}) {
    Get.back();
    if (btnIdentifier!.equalsIgnoreCase(Strings.app_permission_settings)) {
      uploadDocumentController.setBtnClickEvent(true);
      openAppSettings();
    }
  }

  @override
  void imageOptionSelection({String? btnIdentifier}) {
    Get.back();
    if (btnIdentifier!.equalsIgnoreCase(Strings.take_photo)) {
      openCameraGallery(ImageSource.camera);
    } else if (btnIdentifier.equalsIgnoreCase(Strings.choose_from_gallery)) {
      openCameraGallery(ImageSource.gallery);
    } else if (btnIdentifier.equalsIgnoreCase(Strings.pdf_or_xl)) {
      openFilePicker();
    }
  }

  openFilePicker() async {
    FilePickerResult? result = await FilePicker.platform.pickFiles(
      allowMultiple: true,
      withData: true,
      withReadStream: true,
      type: FileType.custom,
      allowedExtensions: ['pdf', 'xlsx', "xls", "png", "jpg"],
    );
    if (result != null && result.files.isNotEmpty) {
      num size = await Utils.getFileSize(result.files.single.path!, 1);
      if (size <= 30000) {
        uploadDocumentController.fileDetail!.add(FileDetail(
            fileName: result.files.single.name,
            filePath: "",
            filePathLocal: result.files.single.path!,
            isFileLocal: true,
            fileType: result.files.single.extension));
      } else {
        Utils.showSnackbar(
            Strings.ERROR,
            "Your file size is very large, please select up to 30MB file size.",
            AppTheme.colorWhite,
            AppTheme.colorRed);
      }
    }
    uploadDocumentController.update();
  }

  // openCameraGallery(ImageSource source) async {
  //   try {
  //     XFile? image;
  //     image = await imagePicker.pickImage(source: source);
  //
  //     if (image != null && !image.path.isNullOrEmpty()) {
  //       num size = await Utils.getFileSize(image.path, 1);
  //       print("image picker file size : ${size}");
  //       if (size <= 30000) {
  //         uploadDocumentController.fileDetail = FileDetail(
  //             fileName: image.name,
  //             filePath: "",
  //             filePathLocal: image.path,
  //             isFileLocal: true,
  //             fileType: Strings.image);
  //       } else {
  //         Utils.showSnackbar(
  //             Strings.ERROR,
  //             "Your file size is very large, please select up to 30MB file size.",
  //             AppTheme.colorWhite,
  //             AppTheme.colorRed);
  //       }
  //     }
  //     uploadDocumentController.update();
  //   } catch (e) {
  //     print("image picker exception : $e");
  //   }
  // }

  titleWithRequireWidget(String title, bool require) {
    return Row(
      children: [
        CustomText(
          title: title,
          colors: AppTheme.title_dark,
          textAlign: TextAlign.start,
          fontSize: AppTheme.small,
          fontWeight: FontWeight.normal,
        ),
        require
            ? CustomText(
                title: " *",
                colors: Colors.red,
                textAlign: TextAlign.start,
                fontSize: AppTheme.small,
                fontWeight: FontWeight.w600,
              )
            : Container(),
      ],
    );
  }

  openCameraGallery(ImageSource source) async {
    try {
      List<XFile>? images =
          await imagePicker.pickMultiImage(); // Allow multiple image selection

      if (images != null && images.isNotEmpty) {
        List<FileDetail> fileDetails =
            []; // List to store file task_catg_detail_screen

        for (XFile image in images) {
          if (image.path.isNotEmpty) {
            num size = await Utils.getFileSize(image.path, 1);
            print("Image picker file size: ${size}");

            if (size <= 30000) {
              fileDetails.add(FileDetail(
                fileName: image.name,
                filePath: "",
                // Path will be set after uploading to the server
                filePathLocal: image.path,
                isFileLocal: true,
                fileType: Strings.image,
              ));
            } else {
              Utils.showSnackbar(
                Strings.ERROR,
                "File ${image.name} exceeds 30MB and will not be uploaded.",
                AppTheme.colorWhite,
                AppTheme.colorRed,
              );
            }
          }
        }

        if (fileDetails.isNotEmpty) {
          // Upload the valid files to the server
          uploadDocumentController.fileDetail = fileDetails;
        } else {
          Utils.showSnackbar(
            Strings.ERROR,
            "No valid files to upload. Please select files smaller than 30MB.",
            AppTheme.colorWhite,
            AppTheme.colorRed,
          );
        }
      }

      uploadDocumentController.update();
    } catch (e) {
      print("Image picker exception: $e");
    }
  }

  titleWidget(String title) {
    return CustomText(
      title: title,
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w700,
      maxLines: 1,
    );
  }

  valueWidget(TextEditingController? value, String? textLabel) {
    return CoustomTextField(
        labelText: textLabel,
        hintColor: AppTheme.colorIconGrey,
        textEditingController: value,
        borderEnableColors: AppTheme.colorIconGrey,
        borderFocusColors: AppTheme.colorIconGrey,
        textColor: AppTheme.colorBlack,
        keyboardType: TextInputType.number,
        fontSize: AppTheme.small - 1,
        textInputAction: TextInputAction.next,
        fontWeight: FontWeight.w500,
        contentPadding: const EdgeInsets.symmetric(
          horizontal: Constant.MEDIUM_PADDING,
        ),
        borderCorner: Constant.BTN_ROUNDED_CORNER,
        onTextValidator: (String? value) {
          return null;
        },
        onTextFiledOnTap: () {},
        readOnly: true);
  }

  locationPermissionStatus(String? tab, int tabIndex) async {
    if (Platform.isIOS) {
      getCurrentPosition(false, tabIndex);
    } else {
      PermissionService().requestLocationPermission(onPermissionSuccess: () {
        print("Location Service Permission approved");
        getCurrentPosition(false, tabIndex);
      }, onPermissionDenied: () async {
        print("Location Service Permission denied");
        getCurrentPosition(false, tabIndex);
      });
    }
  }

  getCurrentPosition(bool fromTryAgain, int tabIndex) async {
    bool serviceEnabled = await checkLocationService();
    if (!serviceEnabled) {
      uploadDocumentController.setBtnClickEvent(true);
      locationSettingsDialog(false, fromTryAgain);
      return false;
    }
    LocationPermission permission = await geolocatorPlatform.checkPermission();
    if (permission == LocationPermission.denied) {
      if (Platform.isIOS) {
        permission = await Geolocator.requestPermission();
        if (permission == LocationPermission.denied) {
          locationSettingsDialog(true, fromTryAgain);
          return false;
        }
      } else {
        uploadDocumentController.setBtnClickEvent(true);
        locationSettingsDialog(true, fromTryAgain);
        return false;
      }
    }
    if (permission == LocationPermission.deniedForever) {
      // for app settings
      if (Platform.isIOS) {
        permission = await Geolocator.requestPermission();
        if (permission == LocationPermission.deniedForever) {
          locationSettingsDialog(true, fromTryAgain);
          return false;
        }
      } else {
        uploadDocumentController.setBtnClickEvent(true);
        locationSettingsDialog(true, fromTryAgain);
        return false;
      }
    }

    uploadDocumentController.isLoading = true;
    uploadDocumentController.update();

    LocationSettings settings = const LocationSettings(
        accuracy: LocationAccuracy.bestForNavigation,
        timeLimit: Duration(seconds: 20));
    geolocatorPlatform
        .getCurrentPosition(
      locationSettings: settings,
    )
        .then((position) {
      if (position != null) {
        uploadDocumentController.setBtnClickEvent(false);
        uploadDocumentController.isLoading = false;

        Position currentPosition = position;

        latitudeControllers[tabIndex].text =
            currentPosition.latitude.toString();
        longitudeControllers[tabIndex].text =
            currentPosition.longitude.toString();

        uploadDocumentController.update();
      } else {
        uploadDocumentController.isLoading = false;
        uploadDocumentController.update();
        getCurrentPosition(false, tabIndex);
      }
    }).catchError((error) {
      uploadDocumentController.isLoading = false;
      uploadDocumentController.update();
      getCurrentPosition(false, tabIndex);
    });
  }

  Future<bool> checkLocationService() async {
    bool serviceEnabled;
    serviceEnabled = await geolocatorPlatform.isLocationServiceEnabled();
    if (!serviceEnabled) {
      return false;
    } else {
      return true;
    }
  }

  locationSettingsDialog(bool isAppPermission, bool fromTryAgain) {
    if (!isAppPermission || fromTryAgain) {
      showDialog(
          context: Get.context!,
          barrierDismissible: false,
          builder: (BuildContext context) {
            return LocationSettingsDialog(
                locationBtnAction: this,
                isAppPermission: isAppPermission,
                from: Constant.NEAR_BY_DEVICE);
          });
    } else if (isAppPermission && fromTryAgain) {
      showDialog(
          context: Get.context!,
          barrierDismissible: false,
          builder: (BuildContext context) {
            return LocationSettingsDialog(
                locationBtnAction: this,
                isAppPermission: isAppPermission,
                from: Constant.NEAR_BY_DEVICE);
          });
    }
  }

  openLocationListScreen() async {
    var result = await Get.to(LocationList());
    if (result != null) {
      LocationDetail data = result;
      if (data != null) {
        uploadDocumentController.selectedLocation = data;
        uploadDocumentController.update();
        uploadDocumentController.getLocationToLatLong();
      }
    }
  }

  _appBar() {
    return DynamicAppBar(Strings.upload_document, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }
}
