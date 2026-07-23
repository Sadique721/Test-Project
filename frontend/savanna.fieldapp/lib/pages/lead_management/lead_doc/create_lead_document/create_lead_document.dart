import 'dart:developer';
import 'dart:io';

import 'package:savbill/pages/customer/model/response/cust_doc_verification_mode_res.dart';
import 'package:savbill/pages/customer/model/response/cust_doc_verification_res.dart';
import 'package:savbill/pages/customer/model/response/doc_sub_type_verification_res.dart';
import 'package:savbill/pages/customer/model/response/get_document_status_res.dart';
import 'package:savbill/pages/lead_management/lead_doc/create_lead_document/create_doc_lead_controller.dart';
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
import 'package:device_info_plus/device_info_plus.dart';
import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import 'package:image_picker/image_picker.dart';
import 'package:permission_handler/permission_handler.dart';

class CreateDocumentLead extends StatefulWidget {
  @override
  _CreateDocumentLeadState createState() => _CreateDocumentLeadState();
}

class _CreateDocumentLeadState extends State<CreateDocumentLead>
    with WidgetsBindingObserver
    implements ImageOptionBtnAction, PermissionDenyBtnAction {
  late AppLifecycleState appState;
  final createDocLeadController = Get.put(CreateDocLeadController());
  final createDocLeadFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;
  final ImagePicker imagePicker = ImagePicker();

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back();
  }

  @override
  void initState() {
    WidgetsBinding.instance.addObserver(this);
    super.initState();
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
        print("on pause method call");
        return;
      case AppLifecycleState.resumed:
        if (createDocLeadController.checkBtnClickEvent) {
          createDocLeadController.setBtnClickEvent(false);
          checkCameraPermission();
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
      child: GetBuilder<CreateDocLeadController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: createDocLeadController.isLoading),
        ]);
      }),
    );
  }

  _body() {
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
            Expanded(
              child: SingleChildScrollView(
                child: Padding(
                    padding: const EdgeInsets.only(
                        left: Constant.SCREEN_PADDING,
                        right: Constant.SCREEN_PADDING),
                    child: Form(
                      key: createDocLeadFormKey,
                      autovalidateMode:
                          createDocLeadController.autoValidateMode,
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.start,
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: titleWithRequireWidget(
                                    Strings.verification_mode, true),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: IgnorePointer(
                                  ignoring:
                                      createDocLeadController.documentDetail !=
                                              null
                                          ? true
                                          : false,
                                  child: DropdownButtonHideUnderline(
                                    child: DropdownButtonFormField(
                                      icon: SvgPicture.asset(
                                        downArrowSvg,
                                        height: Constant.DROP_DOWN_ARROW_W_H,
                                        width: Constant.DROP_DOWN_ARROW_W_H,
                                        color: AppTheme.colorBlack,
                                        fit: BoxFit.fill,
                                      ),
                                      decoration: Utils.ddlDecoration(),
                                      hint: Align(
                                          alignment: Alignment.centerLeft,
                                          child: Text(
                                              Strings.select_verification_mode,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ))),
                                      style: AppTheme.dropdownTextStyle,
                                      isExpanded: true,
                                      isDense: true,
                                      value: createDocLeadController
                                          .selectDocVerification,
                                      items: createDocLeadController
                                          .docVerificationList!
                                          .map((VerificationDataList value) {
                                        return DropdownMenuItem<
                                            VerificationDataList>(
                                          value: value,
                                          child: Align(
                                            alignment: Alignment.centerLeft,
                                            child: CustomText(
                                              title: value.displayName!,
                                              colors: AppTheme.colorBlack,
                                              textAlign: TextAlign.start,
                                              fontSize: AppTheme.small,
                                              fontWeight: FontWeight.w500,
                                            ), //Text(value.desig!),
                                          ),
                                        );
                                      }).toList(),
                                      onChanged: (value) {
                                        createDocLeadController
                                                .selectDocVerification =
                                            value as VerificationDataList?;
                                        createDocLeadController.update();
                                        createDocLeadController
                                            .verificationMode = value!.value;
                                        createDocLeadController
                                            .docSubTypeVerificationList!
                                            .clear();
                                        createDocLeadController
                                            .docVerificationTypeModeList!
                                            .clear();
                                        createDocLeadController
                                            .selectedDocumentType = null;
                                        createDocLeadController
                                            .selectedDocSubTypeData = null;
                                        createDocLeadController
                                            .getDocTypeVerificationMode(
                                                createDocLeadController
                                                    .selectDocVerification!
                                                    .value);
                                      },
                                      validator: (value) {
                                        if (value == null ||
                                            createDocLeadController
                                                    .selectDocVerification ==
                                                null) {
                                          return Strings
                                              .please_select_verification_mode;
                                        }
                                        return null;
                                      },
                                    ),
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: titleWithRequireWidget(
                                    Strings.document_type_new, true),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: IgnorePointer(
                                  ignoring:
                                      createDocLeadController.documentDetail !=
                                              null
                                          ? true
                                          : false,
                                  child: DropdownButtonHideUnderline(
                                    child: DropdownButtonFormField(
                                      icon: SvgPicture.asset(
                                        downArrowSvg,
                                        height: Constant.DROP_DOWN_ARROW_W_H,
                                        width: Constant.DROP_DOWN_ARROW_W_H,
                                        color: AppTheme.colorBlack,
                                        fit: BoxFit.fill,
                                      ),
                                      decoration: Utils.ddlDecoration(),
                                      hint: Align(
                                        alignment: Alignment.centerLeft,
                                        child: Text(
                                          Strings.select_document_type,
                                          style: TextStyle(
                                            fontSize: AppTheme.medium,
                                            color: AppTheme.colorIconGrey,
                                            fontFamily: AppTheme.appFontName,
                                          ),
                                        ),
                                      ),
                                      style: AppTheme.dropdownTextStyle,
                                      isExpanded: true,
                                      isDense: true,
                                      value: createDocLeadController
                                          .selectedDocumentType,
                                      items: createDocLeadController
                                          .docVerificationTypeModeList!
                                          .map((VerificationTypeModeDataList
                                              value) {
                                        return DropdownMenuItem<
                                            VerificationTypeModeDataList>(
                                          value: value,
                                          child: Align(
                                            alignment: Alignment.centerLeft,
                                            child: CustomText(
                                              title: value.displayName!,
                                              colors: AppTheme.colorBlack,
                                              textAlign: TextAlign.start,
                                              fontSize: AppTheme.small,
                                              fontWeight: FontWeight.w500,
                                            ), //Text(value.desig!),
                                          ),
                                        );
                                      }).toList(),
                                      onChanged: (value) {
                                        createDocLeadController
                                                .selectedDocumentType =
                                            value
                                                as VerificationTypeModeDataList?;
                                        createDocLeadController.update();
                                        createDocLeadController
                                            .getDocSubTypeVerification(
                                                value!.value);
                                      },
                                      validator: (value) {
                                        if (value == null ||
                                            createDocLeadController
                                                    .selectedDocumentType ==
                                                null) {
                                          return Strings
                                              .please_select_document_type;
                                        }
                                        return null;
                                      },
                                    ),
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: titleWithRequireWidget(
                                    Strings.document_sub_type_new, false),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: DropdownButtonHideUnderline(
                                  child: DropdownButtonFormField(
                                    icon: SvgPicture.asset(
                                      downArrowSvg,
                                      height: Constant.DROP_DOWN_ARROW_W_H,
                                      width: Constant.DROP_DOWN_ARROW_W_H,
                                      color: AppTheme.colorBlack,
                                      fit: BoxFit.fill,
                                    ),
                                    decoration: Utils.ddlDecoration(),
                                    hint: Align(
                                        alignment: Alignment.centerLeft,
                                        child: Text(
                                            Strings.select_document_sub_type,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ))),
                                    style: AppTheme.dropdownTextStyle,
                                    isExpanded: true,
                                    isDense: true,
                                    value: createDocLeadController
                                        .selectedDocSubTypeData,
                                    items: createDocLeadController
                                        .docSubTypeVerificationList!
                                        .map((DocSubTypeDataList value) {
                                      return DropdownMenuItem<
                                          DocSubTypeDataList>(
                                        value: value,
                                        child: Align(
                                          alignment: Alignment.centerLeft,
                                          child: CustomText(
                                            title: value.displayName!,
                                            colors: AppTheme.colorBlack,
                                            textAlign: TextAlign.start,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                          ), //Text(value.desig!),
                                        ),
                                      );
                                    }).toList(),
                                    onChanged: (value) {
                                      createDocLeadController
                                              .selectedDocSubTypeData =
                                          value as DocSubTypeDataList?;
                                      createDocLeadController.update();
                                    },
                                    validator: (value) {
                                      if (value == null ||
                                          createDocLeadController
                                                  .selectedDocSubTypeData ==
                                              null) {
                                        return Strings
                                            .please_select_document_sub_type;
                                      }
                                      return null;
                                    },
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          createDocLeadController.selectDocVerification !=
                                      null &&
                                  (createDocLeadController
                                          .selectDocVerification!.value!
                                          .equalsIgnoreCase("online") &&
                                      createDocLeadController.from!
                                          .equalsIgnoreCase(Strings.add))
                              ? Column(
                                  children: [
                                    Row(
                                      crossAxisAlignment:
                                          CrossAxisAlignment.center,
                                      mainAxisAlignment:
                                          MainAxisAlignment.spaceBetween,
                                      children: [
                                        Flexible(
                                          flex: 1,
                                          child: titleWithRequireWidget(
                                              Strings.document_no, true),
                                        ),
                                        const SizedBox(
                                          width: Constant.SMALL_PADDING,
                                        ),
                                        Flexible(
                                          flex: 2,
                                          child: CoustomTextField(
                                              labelText:
                                                  Strings.enter_document_no,
                                              hintColor: AppTheme.colorIconGrey,
                                              textEditingController:
                                                  createDocLeadController
                                                      .documentNumberController,
                                              borderEnableColors:
                                                  AppTheme.colorIconGrey,
                                              borderFocusColors:
                                                  AppTheme.colorIconGrey,
                                              textColor: AppTheme.colorBlack,
                                              keyboardType: TextInputType.text,
                                              maxLength: 12,
                                              fontSize: AppTheme.small,
                                              textInputAction:
                                                  TextInputAction.next,
                                              fontWeight: FontWeight.w500,
                                              contentPadding:
                                                  const EdgeInsets.symmetric(
                                                      horizontal: Constant
                                                          .MEDIUM_PADDING,
                                                      vertical: Constant
                                                          .MEDIUM_PADDING),
                                              borderCorner:
                                                  Constant.BTN_ROUNDED_CORNER,
                                              onTextValidator: (String? value) {
                                                if (value!.isEmpty) {
                                                  return Strings
                                                      .please_enter_document_no;
                                                }
                                                return null;
                                              },
                                              onTextFiledOnTap: () {},
                                              readOnly: false),
                                        ),
                                      ],
                                    ),
                                    const SizedBox(
                                        height: Constant.MEDIUM_PADDING),
                                  ],
                                )
                              : const SizedBox.shrink(),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: titleWithRequireWidget(
                                    Strings.document_status_new, true),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: IgnorePointer(
                                  ignoring:
                                      createDocLeadController.documentDetail !=
                                              null
                                          ? true
                                          : false,
                                  child: DropdownButtonHideUnderline(
                                    child: DropdownButtonFormField(
                                      icon: SvgPicture.asset(
                                        downArrowSvg,
                                        height: Constant.DROP_DOWN_ARROW_W_H,
                                        width: Constant.DROP_DOWN_ARROW_W_H,
                                        color: AppTheme.colorBlack,
                                        fit: BoxFit.fill,
                                      ),
                                      decoration: Utils.ddlDecoration(),
                                      hint: Align(
                                        alignment: Alignment.centerLeft,
                                        child: Text(
                                          Strings.select_document_status,
                                          style: TextStyle(
                                            fontSize: AppTheme.medium,
                                            color: AppTheme.colorIconGrey,
                                            fontFamily: AppTheme.appFontName,
                                          ),
                                        ),
                                      ),
                                      style: AppTheme.dropdownTextStyle,
                                      isExpanded: true,
                                      isDense: true,
                                      value: createDocLeadController
                                          .selectedDocumentStatus,
                                      items: createDocLeadController
                                          .documentStatusList!
                                          .map((DocumentStatusDataList value) {
                                        return DropdownMenuItem<
                                            DocumentStatusDataList>(
                                          value: value,
                                          child: Align(
                                            alignment: Alignment.centerLeft,
                                            child: CustomText(
                                              title: value.value!,
                                              colors: AppTheme.colorBlack,
                                              textAlign: TextAlign.start,
                                              fontSize: AppTheme.small,
                                              fontWeight: FontWeight.w500,
                                            ), //Text(value.desig!),
                                          ),
                                        );
                                      }).toList(),
                                      onChanged: (value) {
                                        createDocLeadController
                                                .selectedDocumentStatus =
                                            value as DocumentStatusDataList?;
                                        createDocLeadController.update();
                                      },
                                      validator: (value) {
                                        if (value == null ||
                                            createDocLeadController
                                                    .selectedDocumentStatus ==
                                                null) {
                                          return Strings
                                              .please_select_document_status;
                                        }
                                        return null;
                                      },
                                    ),
                                  ),
                                ),
                              ),
                            ],
                          ),
                          createDocLeadController.selectDocVerification !=
                                      null &&
                                  (createDocLeadController
                                      .selectDocVerification!.value!
                                      .equalsIgnoreCase("online"))
                              ? SizedBox.shrink()
                              : Column(
                                  children: [
                                    const SizedBox(
                                        height: Constant.MEDIUM_PADDING),
                                    Row(
                                      crossAxisAlignment:
                                          CrossAxisAlignment.center,
                                      mainAxisAlignment:
                                          MainAxisAlignment.spaceBetween,
                                      children: [
                                        Flexible(
                                          flex: 1,
                                          child: titleWithRequireWidget(
                                              Strings.choose_file, false),
                                        ),
                                        const SizedBox(
                                          width: Constant.SMALL_PADDING,
                                        ),
                                        Flexible(
                                          flex: 2,
                                          child: Container(
                                            width: double.infinity,
                                            height: Constant.BTN_HEIGHT_M + 5,
                                            padding: const EdgeInsets.all(
                                                Constant.SMALL_PADDING),
                                            decoration: BoxDecoration(
                                                borderRadius: BorderRadius
                                                    .circular(Constant
                                                        .DROP_DOWN_ROUNDED_CORNER),
                                                border: Border.all(
                                                    color: AppTheme.colorGrey,
                                                    width: 0.8),
                                                color: AppTheme.colorWhite),
                                            child: GestureDetector(
                                              onTap: () {
                                                checkCameraPermission();
                                              },
                                              child: Row(
                                                mainAxisSize: MainAxisSize.max,
                                                crossAxisAlignment:
                                                    CrossAxisAlignment.center,
                                                mainAxisAlignment:
                                                    MainAxisAlignment.start,
                                                children: [
                                                  Padding(
                                                    padding:
                                                        const EdgeInsets.only(
                                                            left: 0.0,
                                                            right: 8.0),
                                                    child: CustomText(
                                                      title:
                                                          " ${Strings.select_file}",
                                                      colors: AppTheme
                                                          .lable_noramal,
                                                      textAlign:
                                                          TextAlign.center,
                                                      fontSize:
                                                          AppTheme.small + 1,
                                                      fontWeight:
                                                          FontWeight.w500,
                                                    ),
                                                  ),
                                                  Icon(
                                                    Icons
                                                        .add_circle_outline_rounded,
                                                    color: AppTheme.title_dark,
                                                    size: 18,
                                                  ),
                                                ],
                                              ),
                                            ),
                                          ),
                                        ),
                                      ],
                                    ),
                                    const SizedBox(
                                      height: Constant.MEDIUM_PADDING,
                                    ),
                                    fileViewWidget(),
                                  ],
                                ),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: titleWithRequireWidget(
                                    Strings.start_date, false),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: CoustomTextField(
                                    labelText: Strings.date_format,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController:
                                        createDocLeadController
                                            .startDateController,
                                    borderEnableColors: AppTheme.colorIconGrey,
                                    borderFocusColors: AppTheme.colorIconGrey,
                                    textColor: AppTheme.colorBlack,
                                    keyboardType: TextInputType.text,
                                    maxLength: 6,
                                    fontSize: AppTheme.small,
                                    textInputAction: TextInputAction.next,
                                    fontWeight: FontWeight.w500,
                                    contentPadding: const EdgeInsets.symmetric(
                                        horizontal: Constant.MEDIUM_PADDING,
                                        vertical: Constant.MEDIUM_PADDING),
                                    borderCorner: Constant.BTN_ROUNDED_CORNER,
                                    onTextValidator: (String? value) {
                                      return null;
                                    },
                                    onTextFiledOnTap: () {
                                      selectDate(
                                          context,
                                          Strings.start_date,
                                          DateTime(DateTime.now().year - 10),
                                          DateTime(DateTime.now().year + 10));
                                    },
                                    readOnly: true),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: titleWithRequireWidget(
                                    Strings.end_date, false),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: CoustomTextField(
                                    labelText: Strings.date_format,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController:
                                        createDocLeadController
                                            .endDateController,
                                    borderEnableColors: AppTheme.colorIconGrey,
                                    borderFocusColors: AppTheme.colorIconGrey,
                                    textColor: AppTheme.colorBlack,
                                    keyboardType: TextInputType.text,
                                    maxLength: 6,
                                    fontSize: AppTheme.small,
                                    textInputAction: TextInputAction.next,
                                    fontWeight: FontWeight.w500,
                                    contentPadding: const EdgeInsets.symmetric(
                                        horizontal: Constant.MEDIUM_PADDING,
                                        vertical: Constant.MEDIUM_PADDING),
                                    borderCorner: Constant.BTN_ROUNDED_CORNER,
                                    onTextValidator: (String? value) {
                                      return null;
                                    },
                                    onTextFiledOnTap: () {
                                      selectDate(
                                          context,
                                          Strings.end_date,
                                          DateTime(DateTime.now().year - 10),
                                          DateTime(DateTime.now().year + 10));
                                    },
                                    readOnly: true),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: titleWithRequireWidget(
                                    Strings.remarks, false),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: remarksView(),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                        ],
                      ),
                    )),
              ),
            ),
            Row(
              children: [
                Expanded(
                  child: SimpleButton(
                    onTap: () {
                      validateForm();
                    },
                    radius: 0,
                    height: Constant.BOTTOM_BTN_HEIGHT,
                    bgColors: AppTheme.colorPrimary,
                    borderColors: AppTheme.colorPrimary,
                    child: CustomText(
                      title: createDocLeadController.from!
                              .equalsIgnoreCase(Strings.add)
                          ? Strings.add_document
                          : Strings.update_document,
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

  Future<void> selectDate(
    BuildContext context,
    String identity,
    DateTime firstDate,
    DateTime lastDate,
  ) async {
    DateTime? selectedDate;

    if (identity == Strings.start_date) {
      if (createDocLeadController.selectedStartDate != null) {
        selectedDate = createDocLeadController.selectedStartDate;
      } else {
        selectedDate = DateTime.now();
      }
    } else if (identity == Strings.end_date) {
      if (createDocLeadController.selectedEndDate != null) {
        selectedDate = createDocLeadController.selectedEndDate;
      } else {
        selectedDate = DateTime.now();
      }
    }

    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: selectedDate!,
      firstDate: firstDate,
      lastDate: lastDate,
      initialEntryMode: DatePickerEntryMode.calendarOnly,
      builder: (BuildContext? context, Widget? child) {
        return Theme(
          data: ThemeData.light().copyWith(
            primaryColor: AppTheme.colorPrimary,
            colorScheme: ColorScheme.light(primary: AppTheme.colorPrimary),
            buttonTheme:
                const ButtonThemeData(textTheme: ButtonTextTheme.primary),
          ),
          child: child!,
        );
      },
    );
    if (picked != null) {
      if (identity == Strings.start_date) {
        createDocLeadController.selectedStartDate = picked;
        createDocLeadController.startDateController.text =
            createDocLeadController.apiDateFormat.format(picked);
      } else if (identity == Strings.end_date) {
        createDocLeadController.selectedEndDate = picked;
        createDocLeadController.endDateController.text =
            createDocLeadController.apiDateFormat.format(picked);
      }
      createDocLeadController.update();
    }
  }

  _appBar() {
    return DynamicAppBar(
        createDocLeadController.from!.equalsIgnoreCase(Strings.add)
            ? Strings.create_lead_document
            : Strings.update_lead_document,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  validateForm() {
    if (createDocLeadFormKey.currentState!.validate()) {
      if (createDocLeadController.from!.equalsIgnoreCase(Strings.add) ||
          createDocLeadController.documentDetail == null) {
        createDocLeadController.customerUploadDocument(isUpdate : false);
      } else if (createDocLeadController.from!.equalsIgnoreCase(Strings.edit) ||
          createDocLeadController.documentDetail != null) {
        createDocLeadController.customerUploadDocument(isUpdate : true);
        // createDocLeadController.customerUploadDocumentUpdate(
        //     createDocLeadController.documentDetail);
      }
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

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

  fileViewWidget() {
    return createDocLeadController.fileDetail != null
        ? FileGridItem(
            fileDetail: createDocLeadController.fileDetail!,
            onTapItem: () {},
            bottomAction: fileItemAction(),
          )
        : Container();
  }

  fileItemAction() {
    return createDocLeadController.fileDetail != null &&
            createDocLeadController.fileDetail!.isFileLocal == true
        ? Align(
            alignment: Alignment.topRight,
            child: InkWell(
              onTap: () {
                createDocLeadController.fileDetail = null;
                createDocLeadController.update();
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

  checkCameraPermission() async {
    PermissionService().requestCameraAndStoragePermission(
        onPermissionDenied: () async {
      if (Platform.isIOS) {
        uploadImageOption();
      } else if (Platform.isAndroid) {
        final androidInfo = await DeviceInfoPlugin().androidInfo;
        if (androidInfo.version.sdkInt <= 32) {
          permissionDenyDialog();
        }
      }
    }, onPermissionSuccess: () {
      uploadImageOption();
    });
  }

  void uploadImageOption() async {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return ImageOptionDialog(
              imageOptionBtnAction: this,
              showFileSelect: true,
              showCameraSelect: false);
        });
  }

  void permissionDenyDialog() async {
    showDialog(
        context: context,
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
      createDocLeadController.setBtnClickEvent(true);
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
      allowMultiple: false,
      type: FileType.custom,
      allowedExtensions: ['pdf', 'xlsx', "xls"],
    );
    if (result != null && result.files.isNotEmpty) {
      num size = await Utils.getFileSize(result.files.single.path!, 1);
      if (size <= 500) {
        createDocLeadController.fileDetail = FileDetail(
            fileName: result.files.single.name,
            filePath: "",
            filePathLocal: result.files.single.path!,
            isFileLocal: true,
            fileType: result.files.single.extension);
      } else {
        Utils.showSnackbar(
            Strings.ERROR,
            "Your file size is very large, please select up to 500kb file size.",
            AppTheme.colorWhite,
            AppTheme.colorRed);
      }
    }
    createDocLeadController.update();
  }

  openCameraGallery(ImageSource source) async {
    try {
      XFile? image;
      image = await imagePicker.pickImage(source: source);

      if (image != null && !image.path.isNullOrEmpty()) {
        num size = await Utils.getFileSize(image.path, 1);
        print("image picker file size : ${size}");
        if (size <= 500) {
          createDocLeadController.fileDetail = FileDetail(
              fileName: image.name,
              filePath: "",
              filePathLocal: image.path,
              isFileLocal: true,
              fileType: Strings.image);
        } else {
          Utils.showSnackbar(
              Strings.ERROR,
              "Your file size is very large, please select up to 500kb file size.",
              AppTheme.colorWhite,
              AppTheme.colorRed);
        }
      }
      createDocLeadController.update();
    } catch (e) {
      print("image picker exception : $e");
    }
  }

  remarksView() {
    return Container(
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(7.0),
        color: AppTheme.colorWhite,
      ),
      child: TextFormField(
        controller: createDocLeadController.remarkController,
        maxLines: 3,
        maxLength: 250,
        style: const TextStyle(fontSize: AppTheme.medium),
        decoration: InputDecoration(
          hintText: Strings.remarks,
          alignLabelWithHint: true,
          contentPadding:
              const EdgeInsets.all(Constant.TEXT_FIELD_CONTENT_PADDING),
          focusColor: Colors.transparent,
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
            borderSide: BorderSide(color: AppTheme.colorBlueRView, width: 0.6),
          ),
          enabledBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
            borderSide: BorderSide(
              color: AppTheme.colorIconGrey,
              width: 1.0,
            ),
          ),
          border: OutlineInputBorder(
              borderRadius:
                  BorderRadius.circular(Constant.TEXT_FIELD_CONTENT_PADDING)),
          isDense: true,
          labelStyle: TextStyle(
            color: AppTheme.colorGrey,
            fontSize: AppTheme.medium,
            fontWeight: FontWeight.normal,
            height: 1,
            fontFamily: AppTheme.appFontName,
            decoration: TextDecoration.none,
          ),
          counterText: "",
        ),
        keyboardType: TextInputType.multiline,
        validator: (value) {
          return null;
        },
      ),
    );
  }
}
