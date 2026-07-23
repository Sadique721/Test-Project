import 'package:savbill/pages/dashboard/model/response/case_status_response.dart';
import 'package:savbill/pages/task_management/model/response/get_all_staff_res.dart';
import 'package:savbill/pages/task_management/task_mgmt/task_change_status/select_helper_name_dialog.dart';
import 'package:savbill/pages/task_management/task_mgmt/task_change_status/task_change_status_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import 'package:image_picker/image_picker.dart';

class TaskChangeStatusScreen extends StatefulWidget {
  @override
  _TaskChangeStatusState createState() => _TaskChangeStatusState();
}

class _TaskChangeStatusState extends State<TaskChangeStatusScreen>
    with WidgetsBindingObserver
    implements SelectParentCategoryAction {
  final taskChangeController = Get.put(TaskChangeStatusController());
  final ticketChangeFormKey = GlobalKey<FormState>();
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
    // WidgetsBinding.instance.addObserver(this);
    super.initState();
  }

  @override
  void dispose() {
    // WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<TaskChangeStatusController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(controller),
          ),
          ProgressBar(isLoader: taskChangeController.isLoading),
        ]);
      }), /**/
    );
  }

  _body(TaskChangeStatusController controller) {
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
                    key: ticketChangeFormKey,
                    autovalidateMode: autoValidateMode,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisAlignment: MainAxisAlignment.start,
                      children: [
                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                        InputTitleRequire(
                            title: Strings.old_status, require: false),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        CoustomTextField(
                            labelText: Strings.old_status,
                            textEditingController:
                            taskChangeController.caseStatusController,
                            keyboardType: TextInputType.text,
                            borderEnableColors: AppTheme.colorGrey,
                            textInputAction: TextInputAction.next,
                            onTextValidator: (String? value) {
                              return null;
                            },
                            fillColor: AppTheme.colorLightGrey,
                            borderCorner: Constant.INPUT_ROUNDED_CORNER,
                            contentPadding: const EdgeInsets.symmetric(
                                horizontal: Constant.LARGE_PADDING),
                            readOnly: true),

                        /*__________________ new status ____________________*/

                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        InputTitleRequire(
                            title: Strings.new_status, require: true),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        DropdownButtonHideUnderline(
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
                                Strings.select_case_status,
                                style: TextStyle(
                                  fontSize: AppTheme.medium,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ),
                              ),
                            ),
                            style: AppTheme.dropdownTextStyle,
                            isExpanded: false,
                            isDense: true,
                            value: controller.selectedCaseStatus,
                            items: controller.changeStatusList
                                ?.map((CaseStatusDetail value) {
                              return DropdownMenuItem<CaseStatusDetail>(
                                value: value,
                                child: Text(value.text!),
                              );
                            }).toList(),
                            onChanged: (value) {
                              controller.selectedCaseStatus =
                              value as CaseStatusDetail?;
                              controller.getResolutionReasonsChangeStatus(
                                  value!.value);
                              controller.update();
                            },
                            validator: (value) {
                              if (value == null ||
                                  controller.selectedCaseStatus == null) {
                                return Strings.select_new_status;
                              }
                              return null;
                            },
                          ),
                        ),

                        /*__________________ Call status ____________________*/

                        controller.isCall == true
                            ? Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          mainAxisAlignment: MainAxisAlignment.start,
                          children: [
                            const SizedBox(
                                height: Constant.SMALL_PADDING),
                            Row(
                              crossAxisAlignment:
                              CrossAxisAlignment.center,
                              mainAxisAlignment: MainAxisAlignment.start,
                              children: [
                                InputTitleRequire(
                                    title: "${Strings.callDetails} :",
                                    require: false),
                                Row(
                                  children: [
                                    Radio(
                                      visualDensity: const VisualDensity(
                                          horizontal: -4.0),
                                      value: 1,
                                      groupValue:
                                      controller.radioCallSelected,
                                      activeColor: AppTheme.colorPrimary,
                                      onChanged: (value) {
                                        setState(() {
                                          controller.radioCallSelected =
                                          value as int;
                                          controller.radioCallVal =
                                              Strings.connected;
                                          controller
                                              .onCallDisconnectedCall(
                                              true);
                                        });
                                      },
                                    ),
                                    CustomText(
                                      title: Strings.connected,
                                      fontSize: AppTheme.small + 2,
                                      fontWeight: FontWeight.normal,
                                      colors: AppTheme.title_dark,
                                    ),
                                  ],
                                ),
                                Row(
                                  children: [
                                    Radio(
                                      value: 2,
                                      visualDensity: const VisualDensity(
                                          horizontal: -4.0),
                                      groupValue:
                                      controller.radioCallSelected,
                                      activeColor: AppTheme.colorPrimary,
                                      onChanged: (value) {
                                        setState(() {
                                          controller.radioCallSelected =
                                          value as int;
                                          controller.radioCallVal =
                                              Strings.disconnected;
                                          controller
                                              .onCallDisconnectedCall(
                                              false);
                                        });
                                      },
                                    ),
                                    CustomText(
                                      title: Strings.disconnected,
                                      fontSize: AppTheme.small + 2,
                                      fontWeight: FontWeight.normal,
                                      colors: AppTheme.title_dark,
                                    ),
                                  ],
                                ),
                              ],
                            ),
                          ],
                        )
                            : const SizedBox.shrink(),

                        /*__________________ Select Reason  ____________________*/

                        controller.isCallDisconnected == true
                            ? Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          mainAxisAlignment: MainAxisAlignment.start,
                          children: [
                            const SizedBox(
                                height: Constant.MEDIUM_PADDING),
                            InputTitleRequire(
                                title: Strings.select_reason,
                                require: false),
                            const SizedBox(
                              height: Constant.SMALL_PADDING,
                            ),
                            DropdownButtonHideUnderline(
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
                                    Strings.select_call_disconnect_reason,
                                    style: TextStyle(
                                      fontSize: AppTheme.medium,
                                      color: AppTheme.colorIconGrey,
                                      fontFamily: AppTheme.appFontName,
                                    ),
                                  ),
                                ),
                                style: AppTheme.dropdownTextStyle,
                                isExpanded: false,
                                isDense: true,
                                value:
                                controller.selectContactFailedReason,
                                items: controller.contactFailedReasonList
                                    ?.map((String? value) {
                                  return DropdownMenuItem<String>(
                                    value: value,
                                    child: Text(value!),
                                  );
                                }).toList(),
                                onChanged: (value) {
                                  controller.selectContactFailedReason =
                                  value as String?;
                                  controller.update();
                                },
                                validator: (value) {
                                  return null;
                                },
                              ),
                            ),
                          ],
                        )
                            : const SizedBox.shrink(),

                        /*__________________ Task Close status ____________________*/

                        controller.isTicket == true
                            ? Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          mainAxisAlignment: MainAxisAlignment.start,
                          children: [
                            const SizedBox(
                                height: Constant.SMALL_PADDING),
                            Row(
                              crossAxisAlignment:
                              CrossAxisAlignment.center,
                              mainAxisAlignment: MainAxisAlignment.start,
                              children: [
                                InputTitleRequire(
                                    title: "${Strings.ticket_close} :",
                                    require: false),
                                Row(
                                  children: [
                                    Radio(
                                      value: 1,
                                      groupValue: controller
                                          .radioTicketCloseSelect,
                                      visualDensity: const VisualDensity(
                                          horizontal: -4.0),
                                      activeColor: AppTheme.colorPrimary,
                                      onChanged: (value) {
                                        setState(() {
                                          controller
                                              .radioTicketCloseSelect =
                                          value as int;
                                          controller.radioTicketVal =
                                              Strings.yes;
                                        });
                                      },
                                    ),
                                    CustomText(
                                      title: Strings.yes,
                                      fontSize: AppTheme.small + 2,
                                      fontWeight: FontWeight.normal,
                                      colors: AppTheme.title_dark,
                                    ),
                                  ],
                                ),
                                Row(
                                  children: [
                                    Radio(
                                      value: 2,
                                      groupValue: controller
                                          .radioTicketCloseSelect,
                                      visualDensity: const VisualDensity(
                                          horizontal: -4.0),
                                      activeColor: AppTheme.colorPrimary,
                                      onChanged: (value) {
                                        setState(() {
                                          controller
                                              .radioTicketCloseSelect =
                                          value as int;
                                          controller.radioTicketVal =
                                              Strings.no;
                                        });
                                      },
                                    ),
                                    CustomText(
                                      title: Strings.no,
                                      fontSize: AppTheme.small + 2,
                                      fontWeight: FontWeight.normal,
                                      colors: AppTheme.title_dark,
                                    ),
                                  ],
                                ),
                              ],
                            ),
                          ],
                        )
                            : const SizedBox.shrink(),

                        /*__________________ Root Cause  ____________________*/
                        /*(taskChangeController.selectedCaseStatus != null &&
                            taskChangeController
                                .selectedCaseStatus!.value!
                                .equalsIgnoreCase("Resolved"))
                            ? Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          mainAxisAlignment: MainAxisAlignment.start,
                          children: [
                            const SizedBox(
                                height: Constant.MEDIUM_PADDING),
                            InputTitleRequire(
                                title: Strings.root_cause,
                                require: false),
                            const SizedBox(
                              height: Constant.SMALL_PADDING,
                            ),
                            DropdownButtonHideUnderline(
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
                                    Strings.root_cause,
                                    style: TextStyle(
                                      fontSize: AppTheme.medium,
                                      color: AppTheme.colorIconGrey,
                                      fontFamily: AppTheme.appFontName,
                                    ),
                                  ),
                                ),
                                style: AppTheme.dropdownTextStyle,
                                isExpanded: false,
                                isDense: true,
                                value:
                                controller.selectedResolutionReason,
                                items: controller.resolutionReasonsList
                                    ?.map((ResolutionReasonsDataList
                                value) {
                                  return DropdownMenuItem<
                                      ResolutionReasonsDataList>(
                                    value: value,
                                    child: Text(value.name!),
                                  );
                                }).toList(),
                                onChanged: (value) {
                                  controller.selectedResolutionReason =
                                  value as ResolutionReasonsDataList?;
                                  if (value!.rootCauseResolutionMappingList !=
                                      null &&
                                      value
                                          .rootCauseResolutionMappingList!
                                          .isNotEmpty) {
                                    controller.rootCauseResolutionList
                                        ?.addAll(value
                                        .rootCauseResolutionMappingList!);
                                  }
                                  controller.update();
                                },
                                validator: (value) {
                                  // if (value == null ||
                                  //     controller.selectedResolutionReason == null) {
                                  //   return Strings.select_root_cause;
                                  // }
                                  return null;
                                },
                              ),
                            ),
                          ],
                        )
                            : const SizedBox.shrink(),*/

                        /*__________________ Resolution   ____________________*/
                        /* (taskChangeController.selectedCaseStatus != null &&
                            taskChangeController
                                .selectedCaseStatus!.value!
                                .equalsIgnoreCase("Resolved"))
                            ? Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          mainAxisAlignment: MainAxisAlignment.start,
                          children: [
                            const SizedBox(
                                height: Constant.SCREEN_PADDING),
                            InputTitleRequire(
                                title: Strings.resolution,
                                require: false),
                            const SizedBox(
                              height: Constant.SMALL_PADDING,
                            ),
                            DropdownButtonHideUnderline(
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
                                    Strings.resolution,
                                    style: TextStyle(
                                      fontSize: AppTheme.medium,
                                      color: AppTheme.colorIconGrey,
                                      fontFamily: AppTheme.appFontName,
                                    ),
                                  ),
                                ),
                                style: AppTheme.dropdownTextStyle,
                                isExpanded: false,
                                isDense: true,
                                value: controller
                                    .selectedRootCauseResolutionData,
                                items: controller.rootCauseResolutionList
                                    ?.map((RootCauseResolutionMappingList
                                value) {
                                  return DropdownMenuItem<
                                      RootCauseResolutionMappingList>(
                                    value: value,
                                    child: Text(value.rootCauseReason!),
                                  );
                                }).toList(),
                                onChanged: (value) {
                                  controller
                                      .selectedRootCauseResolutionData =
                                  value
                                  as RootCauseResolutionMappingList?;
                                  controller.rootCauseReasonId =
                                      value!.id;
                                  controller.finalResolutionId =
                                      value.resolutionId;
                                  controller.update();
                                },
                                validator: (value) {
                                  // if (value == null ||
                                  //     controller.selectedRootCauseResolutionData ==
                                  //         null) {
                                  //   return Strings.select_resolution;
                                  // }
                                  return null;
                                },
                              ),
                            ),
                          ],
                        )
                            : const SizedBox.shrink(),*/

                        (taskChangeController.selectedCaseStatus != null &&
                            taskChangeController.selectedCaseStatus!.value!
                                .equalsIgnoreCase("Follow Up"))
                            ? Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          mainAxisAlignment: MainAxisAlignment.start,
                          children: [
                            const SizedBox(
                                height: Constant.SCREEN_PADDING),
                            InputTitleRequire(
                                title: Strings.followup_date_time,
                                require: false),
                            const SizedBox(
                              height: Constant.SMALL_PADDING,
                            ),
                            CoustomTextField(
                                labelText: Strings.followup_date_time,
                                suffixIcon: Padding(
                                  padding:
                                  const EdgeInsetsDirectional.all(
                                      Constant.MEDIUM_PADDING),
                                  child: SvgPicture.asset(
                                    calendarSvg,
                                    color: AppTheme.colorBlack,
                                    width: Constant.ICON_SIZE_S,
                                    height: Constant.ICON_SIZE_S,
                                    // myIcon is a 48px-wide widget.
                                  ),
                                ),
                                textEditingController:
                                taskChangeController
                                    .followupDateTimeController,
                                borderEnableColors: AppTheme.colorBlack,
                                textInputAction: TextInputAction.next,
                                hintColor: AppTheme.colorIconGrey,
                                onTextValidator: (String? value) {
                                  if (value!.isEmpty) {
                                    return Strings
                                        .please_select_reschedule_date_time;
                                  }
                                  return null;
                                },
                                onTextFiledOnTap: () {
                                  selectDate(
                                      Strings.followup_date_time,
                                      DateTime(DateTime.now().year - 10),
                                      DateTime(DateTime.now().year + 10));
                                  // }
                                },
                                borderCorner:
                                Constant.INPUT_ROUNDED_CORNER,
                                contentPadding:
                                const EdgeInsets.symmetric(
                                    horizontal:
                                    Constant.LARGE_PADDING),
                                readOnly: true),
                          ],
                        )
                            : const SizedBox.shrink(),

                        /*__________________ Service Area   ____________________*/

                        controller.serviceAreaFlag == true
                            ? const SizedBox(height: Constant.SCREEN_PADDING)
                            : const SizedBox.shrink(),
                        controller.serviceAreaFlag == true
                            ? InputTitleRequire(
                            title: Strings.service_area, require: true)
                            : const SizedBox.shrink(),
                        controller.serviceAreaFlag == true
                            ? const SizedBox(
                          height: Constant.SMALL_PADDING,
                        )
                            : const SizedBox.shrink(),
                        controller.serviceAreaFlag == true
                            ? CoustomTextField(
                            labelText: Strings.service_area,
                            textEditingController:
                            taskChangeController.serviceAreaController,
                            keyboardType: TextInputType.text,
                            borderEnableColors: AppTheme.colorGrey,
                            textInputAction: TextInputAction.next,
                            onTextValidator: (String? value) {
                              return null;
                            },
                            borderCorner: Constant.INPUT_ROUNDED_CORNER,
                            contentPadding: const EdgeInsets.symmetric(
                                horizontal: Constant.LARGE_PADDING),
                            readOnly: true)
                            : const SizedBox.shrink(),

                        /*__________________ Helper Name   ____________________*/

                        ((taskChangeController.selectedCaseStatus != null &&
                            taskChangeController
                                .selectedCaseStatus!.value!
                                .equalsIgnoreCase("Resolved")) ||
                            (taskChangeController.selectedCaseStatus !=
                                null &&
                                taskChangeController
                                    .selectedCaseStatus!.value!
                                    .equalsIgnoreCase("Closed")))
                            ? Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          mainAxisAlignment: MainAxisAlignment.start,
                          children: [
                            const SizedBox(
                                height: Constant.SCREEN_PADDING),

                            /* InputTitleRequire(
                                title: Strings.helper_name,
                                require: false),
                            const SizedBox(
                              height: Constant.SMALL_PADDING,
                            ),
                            DropdownButtonHideUnderline(
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
                                    Strings.select_helper_name,
                                    style: TextStyle(
                                      fontSize: AppTheme.medium,
                                      color: AppTheme.colorIconGrey,
                                      fontFamily: AppTheme.appFontName,
                                    ),
                                  ),
                                ),
                                style: AppTheme.dropdownTextStyle,
                                isExpanded: false,
                                isDense: true,
                                value: controller
                                    .selectAllStaffDataList,
                                items: controller.allStaffDataList
                                    ?.map((AllStaffDataList value) {
                                  return DropdownMenuItem<
                                      AllStaffDataList>(
                                    value: value,
                                    child: Text(value.username != null
                                        ? value.username!
                                        : ""),
                                  );
                                }).toList(),
                                onChanged: (value) {
                                  controller.selectAllStaffDataList =
                                  value as AllStaffDataList?;
                                  controller.update();
                                },
                                validator: (value) {
                                  // if (value == null ||
                                  //     controller
                                  //             .selectedStaffUserServiceData ==
                                  //         null) {
                                  //   return Strings.select_helper_name;
                                  // }
                                  return null;
                                },
                              ),
                            ),*/

                            InputTitleRequire(
                                title: Strings.helper_name,
                                require: false),
                            const SizedBox(
                              height: Constant.SMALL_PADDING,
                            ),
                            CoustomTextField(
                                labelText:
                                Strings.please_select_parent_category,
                                hintColor: AppTheme.colorIconGrey,
                                textEditingController:
                                taskChangeController
                                    .helperNameController,
                                borderEnableColors:
                                AppTheme.colorIconGrey,
                                borderFocusColors: AppTheme.colorIconGrey,
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
                                borderCorner: Constant.BTN_ROUNDED_CORNER,
                                onTextValidator: (String? value) {
                                  if (value!.isEmpty) {
                                    return Strings
                                        .please_select_parent_service_area;
                                  }
                                },
                                prefixIcon: Icon(
                                  Icons.search,
                                  color: AppTheme.colorBlack,
                                ),
                                onTextFiledOnTap: () {
                                  showHelperNameSelectionDialog(
                                      Strings.helper_name);
                                },
                                readOnly: true),
                          ],
                        )
                            : const SizedBox.shrink(),

                        /*__________________ Remarks   ____________________*/

                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                        InputTitleRequire(
                            title: taskChangeController.selectedCaseStatus !=
                                null &&
                                taskChangeController
                                    .selectedCaseStatus!.value!
                                    .equalsIgnoreCase("Resolved")
                                ? Strings.resolution_description
                                : Strings.remarks,
                            require: false),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        Container(
                          decoration: BoxDecoration(
                            borderRadius: BorderRadius.circular(7.0),
                            // color: AppTheme.colorWhite,
                          ),
                          child: TextFormField(
                            controller: taskChangeController.remarksController,
                            maxLines: 3,
                            maxLength: 250,
                            style: const TextStyle(fontSize: AppTheme.medium),
                            decoration: InputDecoration(
                              hintText: Strings.remarks,
                              alignLabelWithHint: true,
                              filled: true,
                              hoverColor: Colors.white,
                              fillColor: AppTheme.colorWhite,
                              contentPadding: const EdgeInsets.all(
                                  Constant.TEXT_FIELD_CONTENT_PADDING * 1.5),
                              focusColor: Colors.transparent,
                              focusedBorder: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(
                                    Constant.BTN_ROUNDED_CORNER),
                                borderSide: BorderSide(
                                    color: AppTheme.colorPrimary, width: 1.0),
                              ),
                              enabledBorder: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(
                                    Constant.BTN_ROUNDED_CORNER),
                                borderSide: BorderSide(
                                  color: AppTheme.colorIconGrey,
                                  width: 1.0,
                                ),
                              ),
                              border: OutlineInputBorder(
                                  borderRadius: BorderRadius.circular(
                                      Constant.TEXT_FIELD_CONTENT_PADDING)),
                              isDense: true,
                              hintStyle: TextStyle(
                                  fontSize: AppTheme.medium,
                                  fontWeight: FontWeight.normal,
                                  height: 1,
                                  color: AppTheme.colorGrey),
                              errorStyle: TextStyle(
                                color: AppTheme.colorError,
                                fontWeight: FontWeight.normal,
                                fontSize: AppTheme.large - 1,
                              ),
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
                              // if (value!.isEmpty) {
                              //   return Strings.please_enter_remarks;
                              // }
                              return null;
                            },
                          ),
                        ),
                        const SizedBox(
                          height: Constant.EXTRA_LARGE_PADDING,
                        ),
                      ],
                    ),
                  ),
                ),
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
                      title: Strings.submit,
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

  titleWithRequireWidget(String title) {
    return Row(
      children: [
        CustomText(
          title: title,
          colors: AppTheme.title_dark,
          textAlign: TextAlign.start,
          fontSize: AppTheme.medium,
          fontWeight: FontWeight.normal,
        ),
        CustomText(
          title: " *",
          colors: Colors.red,
          textAlign: TextAlign.start,
          fontSize: AppTheme.medium,
          fontWeight: FontWeight.w600,
        ),
      ],
    );
  }

  _appBar() {
    return DynamicAppBar(Strings.change_status, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  validateForm() {
    if (ticketChangeFormKey.currentState!.validate()) {
      if (taskChangeController.selectedCaseStatus!.value!
          .equalsIgnoreCase("Closed")) {
        taskChangeController.caseAssignCloseRequest();
      } else if (taskChangeController.selectedCaseStatus!.value!
          .equalsIgnoreCase("Follow Up")) {
        taskChangeController.caseAssignCloseRequest();
      } else {
        // taskChangeController.caseAssignRequest();
        taskChangeController.uploadDocuments();
      }
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  Future<void> selectDate(
      String identity,
      DateTime firstDate,
      DateTime lastDate,
      ) async {
    DateTime? selectedDate;

    if (identity == Strings.followup_date_time) {
      if (taskChangeController.selectedFollowUpDate != null) {
        selectedDate = taskChangeController.selectedFollowUpDate;
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
      if (identity == Strings.followup_date_time) {
        taskChangeController.selectedFollowUpDate = picked;
        taskChangeController.nextFollowupDate =
            taskChangeController.dateOnlyFormat.format(picked).toString();
        taskChangeController.update();
        _selectDateTime();
      }
    }
  }

  Future<void> _selectDateTime() async {
    TimeOfDay? selectedDateTime = TimeOfDay.now();
    final TimeOfDay? picked = await showTimePicker(
      context: context,
      initialTime: selectedDateTime,
      builder: (BuildContext? context, Widget? child) {
        return MediaQuery(
          data: MediaQuery.of(context!).copyWith(alwaysUse24HourFormat: false),
          child: child!,
        );
      },
    );
    if (picked != null) {
      DateTime dt = DateTime(
        taskChangeController.selectedFollowUpDate!.year,
        taskChangeController.selectedFollowUpDate!.month,
        taskChangeController.selectedFollowUpDate!.day,
        picked.hour,
        picked.minute,
      );
      taskChangeController.followupDateTimeController.text =
          taskChangeController.dateFormat.format(dt);
      taskChangeController.nextFollowupTime = "${picked.hour}:${picked.minute}";
      taskChangeController.followUpScheduleDateTime =
          taskChangeController.apiDateTimeFormat.format(dt);
      taskChangeController.update();
    }
  }

  showHelperNameSelectionDialog(String from) {
    List<AllStaffDataList> item = [];
    if (from.equalsIgnoreCase(Strings.helper_name)) {
      if (taskChangeController.allStaffDataList != null &&
          taskChangeController.allStaffDataList!.isNotEmpty) {
        for (var element in taskChangeController.allStaffDataList!) {
          element.selected = false;
        }
        if (taskChangeController.selectedAllStaffDataIds.isNotEmpty) {
          for (var element in taskChangeController.allStaffDataList!) {
            for (int selElement
            in taskChangeController.selectedAllStaffDataIds) {
              if (selElement == element.id!) {
                element.selected = true;
              }
            }
          }
        }
        item.addAll(taskChangeController.allStaffDataList!);
      }
    }

    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return SelectHelperNameDialog(
              serviceAreaSelectionAction: this,
              fromFor: from,
              parentCategoryList: item);
        });
  }

  @override
  void selectParentCategoryBtnAction(
      {String? identifier, List<AllStaffDataList>? selectedItem}) {
    Get.back();
    if (identifier.toString().equalsIgnoreCase(Strings.helper_name) &&
        selectedItem != null &&
        selectedItem.isNotEmpty) {
      String serviceAreaName = "";
      taskChangeController.selectedAllStaffDataIds.clear();
      // taskChangeController.allStaffDataList!.clear();
      for (AllStaffDataList element in selectedItem) {
        taskChangeController.selectedAllStaffDataIds.add(element.id!);
        // taskChangeController.selectedSubCategoryResMappingList!
        //     .add(CaseCategoryTatMappingList(
        //   caseCategoryId: element.id,
        // ));
        serviceAreaName = "$serviceAreaName${element.username!}, ";
      }
      if (!serviceAreaName.isNullOrEmpty() &&
          serviceAreaName.contains(",") &&
          serviceAreaName.length >= 2) {
        serviceAreaName =
            serviceAreaName.substring(0, serviceAreaName.length - 2);
      }

      taskChangeController.helperNameController.text = serviceAreaName;
    }
    taskChangeController.update();
  }
}
