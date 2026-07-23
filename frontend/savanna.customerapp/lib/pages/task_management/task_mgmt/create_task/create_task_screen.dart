
import 'dart:developer';
import 'dart:io';

import 'package:savbill/pages/credit_note/credit_customer_list.dart';
import 'package:savbill/pages/credit_note/response/customer_credit_res.dart';
import 'package:savbill/pages/dashboard/model/response/case_status_response.dart';
import 'package:savbill/pages/dashboard/model/response/case_type_response.dart';
import 'package:savbill/pages/model/file_detail.dart';
import 'package:savbill/pages/task_management/model/response/get_all_team_list_res.dart';
import 'package:savbill/pages/task_management/model/response/get_team_by_id_res.dart';
import 'package:savbill/pages/task_management/model/response/task_category_management_list_res.dart';
import 'package:savbill/pages/task_management/model/response/task_sub_category_data_res.dart';
import 'package:savbill/pages/task_management/task_mgmt/create_task/create_task_controller.dart';
import 'package:savbill/pages/ticket_system/model/response/create_ticket_active_service_res.dart';
import 'package:savbill/pages/ticket_system/model/response/department_type_res.dart';
import 'package:savbill/pages/ticket_system/model/response/get_reason_category_active_services_res.dart';
import 'package:savbill/pages/ticket_system/model/response/sub_problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_get_serial_number_res.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_priority_res.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_source_type_res.dart';
import 'package:savbill/pages/ticket_system/ticket_management/cust_service_area_ticket.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_resolution_reasons_res.dart';
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
import 'package:savbill/widgets/title_widge.dart';
import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:geolocator/geolocator.dart';
import 'package:get/get.dart';
import 'package:image_picker/image_picker.dart';
import 'package:permission_handler/permission_handler.dart';

class CreateTaskScreen extends StatefulWidget {
  @override
  _CreateTaskState createState() => _CreateTaskState();
}

class _CreateTaskState extends State<CreateTaskScreen>
    with WidgetsBindingObserver
    implements
        ImageOptionBtnAction,
        PermissionDenyBtnAction,
        CustServiceAreaAction {
  final addTaskController = Get.put(CreateTaskController());
  final addTicketFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;
  final ImagePicker imagePicker = ImagePicker();


  File? _image;
  Position? _location;

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<CreateTaskController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: addTaskController.isLoading),
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
                      key: addTicketFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          const SizedBox(height: Constant.SCREEN_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.title_task, require: true),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: CoustomTextField(
                                    labelText: Strings.case_title,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController:
                                    addTaskController.caseTitleController,
                                    borderEnableColors: AppTheme.colorIconGrey,
                                    borderFocusColors: AppTheme.colorIconGrey,
                                    textColor: AppTheme.colorBlack,
                                    keyboardType: TextInputType.text,
                                    fontSize: AppTheme.small,
                                    textInputAction: TextInputAction.next,
                                    fontWeight: FontWeight.w500,
                                    contentPadding: const EdgeInsets.symmetric(
                                        horizontal: Constant.MEDIUM_PADDING,
                                        vertical: Constant.MEDIUM_PADDING),
                                    borderCorner: Constant.BTN_ROUNDED_CORNER,
                                    onTextValidator: (String? value) {
                                      if (value!.isEmpty) {
                                        return Strings.enter_case_title;
                                      }
                                      return null;
                                    },
                                    onTextFiledOnTap: () {},
                                    readOnly: false),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.SMALL_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.team, require: true),
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
                                        child: Text(Strings.select_a_team,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ))),
                                    style: AppTheme.dropdownTextStyle,
                                    isExpanded: true,
                                    isDense: true,
                                    value: addTaskController.selectedAllTeamList,
                                    items: addTaskController.allTeamList!
                                        .map((AllTeamDataList value) {
                                      return DropdownMenuItem<AllTeamDataList>(
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
                                      addTaskController.selectedAllTeamList =
                                      value as AllTeamDataList?;
                                      addTaskController.getByTeamIds(value!.id!);
                                      addTaskController.update();
                                    },
                                    validator: (value) {
                                      if (value == null ||
                                          addTaskController
                                              .selectedAllTeamList ==
                                              null) {
                                        return Strings.please_select_team;
                                      }
                                      return null;
                                    },
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.SMALL_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.staff, require: false),
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
                                        child: Text(Strings.select_a_staff,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ))),
                                    style: AppTheme.dropdownTextStyle,
                                    isExpanded: true,
                                    isDense: true,
                                    value: addTaskController.selectedTeamByIdList,
                                    items: addTaskController.teamByIdList!
                                        .map((TeamByIdDataList value) {
                                      return DropdownMenuItem<TeamByIdDataList>(
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
                                      addTaskController.selectedTeamByIdList =
                                      value as TeamByIdDataList?;
                                      addTaskController.update();
                                    },
                                    validator: (value) {
                                      return null;
                                    },
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.SMALL_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.type, require: true),
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
                                        child: Text(Strings.select_type,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ))),
                                    style: AppTheme.dropdownTextStyle,
                                    isExpanded: true,
                                    isDense: true,
                                    value: addTaskController.selectedCaseType,
                                    items: addTaskController.caseTypeList!
                                        .map((CaseTypeDetail value) {
                                      return DropdownMenuItem<CaseTypeDetail>(
                                        value: value,
                                        child: Align(
                                          alignment: Alignment.centerLeft,
                                          child: CustomText(
                                            title: value.text!,
                                            colors: AppTheme.colorBlack,
                                            textAlign: TextAlign.start,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                          ), //Text(value.desig!),
                                        ),
                                      );
                                    }).toList(),
                                    onChanged: (value) {
                                      addTaskController.selectedCaseType =
                                      value as CaseTypeDetail?;
                                      addTaskController.update();
                                    },
                                    validator: (value) {
                                      if (value == null ||
                                          addTaskController
                                              .selectedCaseType ==
                                              null) {
                                        return Strings.enter_case_type;
                                      }
                                      return null;
                                    },
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.SMALL_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.task_category, require: true),
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
                                        child: Text(Strings.select_task_category,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ))),
                                    style: AppTheme.dropdownTextStyle,
                                    isExpanded: true,
                                    isDense: true,
                                    value: addTaskController.selectTaskCategoryData,
                                    items: addTaskController.taskCategoryList!
                                        .map((TaskCategoryMgmtDataList value) {
                                      return DropdownMenuItem<TaskCategoryMgmtDataList>(
                                        value: value,
                                        child: Align(
                                          alignment: Alignment.centerLeft,
                                          child: CustomText(
                                            title: value.categoryName!,
                                            colors: AppTheme.colorBlack,
                                            textAlign: TextAlign.start,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                          ), //Text(value.desig!),
                                        ),
                                      );
                                    }).toList(),
                                    onChanged: (value) {
                                      addTaskController.selectTaskCategoryData =
                                      value as TaskCategoryMgmtDataList?;
                                      addTaskController.selectedCaseStatus = null;
                                      addTaskController.caseStatusList!.clear();
                                      addTaskController.getAllTaskSubCategoryData(value!.categoryId);
                                      addTaskController.update();
                                    },
                                    validator: (value) {
                                      if (value == null ||
                                          addTaskController
                                              .selectTaskCategoryData ==
                                              null) {
                                        return Strings.please_select_task_category;
                                      }
                                      return null;
                                    },
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.SMALL_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.task_sub_category, require: true),
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
                                        child: Text(Strings.select_sub_task_category,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ))),
                                    style: AppTheme.dropdownTextStyle,
                                    isExpanded: true,
                                    isDense: true,
                                    value: addTaskController.selectedTaskSubCategoryData,
                                    items: addTaskController.taskSubCategoryList!
                                        .map((TaskSubCategoryDataList value) {
                                      return DropdownMenuItem<TaskSubCategoryDataList>(
                                        value: value,
                                        child: Align(
                                          alignment: Alignment.centerLeft,
                                          child: CustomText(
                                            title: value.subCategoryName!,
                                            colors: AppTheme.colorBlack,
                                            textAlign: TextAlign.start,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                          ), //Text(value.desig!),
                                        ),
                                      );
                                    }).toList(),
                                    onChanged: (value) {
                                      addTaskController.selectedTaskSubCategoryData =
                                      value as TaskSubCategoryDataList?;
                                      addTaskController.update();
                                    },
                                    validator: (value) {
                                      if (value == null ||
                                          addTaskController
                                              .selectedTaskSubCategoryData ==
                                              null) {
                                        return Strings.pleae_select_sub_task_category;
                                      }
                                      return null;
                                    },
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.SMALL_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.priority, require: true),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: IgnorePointer(
                                  ignoring: true,
                                  child: DropdownButtonHideUnderline(
                                    child: DropdownButtonFormField(
                                      icon: SvgPicture.asset(
                                        downArrowSvg,
                                        height: Constant.DROP_DOWN_ARROW_W_H,
                                        width: Constant.DROP_DOWN_ARROW_W_H,
                                        color: AppTheme.colorBlack,
                                        fit: BoxFit.fill,
                                      ),
                                      decoration: Utils.ddlDecoration(
                                        fillColor: Colors.black12,
                                      ),
                                      hint: Align(
                                          alignment: Alignment.centerLeft,
                                          child: Text(Strings.priority,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,

                                                fontFamily:
                                                AppTheme.appFontName,
                                              ))),
                                      style: AppTheme.dropdownTextStyle,
                                      isExpanded: true,
                                      isDense: true,
                                      value: addTaskController
                                          .selectedTicketPriority,
                                      items: addTaskController
                                          .ticketPriorityList!
                                          .map((TicketPriority value) {
                                        return DropdownMenuItem<TicketPriority>(
                                          value: value,
                                          child: Align(
                                            alignment: Alignment.centerLeft,
                                            child: CustomText(
                                              title: value.text!,
                                              colors: AppTheme.colorBlack,
                                              textAlign: TextAlign.start,
                                              fontSize: AppTheme.small,
                                              fontWeight: FontWeight.w500,
                                            ),
                                          ),
                                        );
                                      }).toList(),
                                      onChanged: (value) {
                                        addTaskController
                                            .selectedTicketPriority =
                                        value as TicketPriority?;
                                        addTaskController.update();
                                      },
                                      validator: (value) {
                                        if (value == null ||
                                            addTaskController
                                                .selectedTicketPriority ==
                                                null) {
                                          return Strings.select_priority;
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
                                child: InputTitleRequire(
                                    title: Strings.status, require: true),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: IgnorePointer(
                                  ignoring:
                                  addTaskController.ticketDetail != null
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
                                      decoration: Utils.ddlDecoration(
                                          fillColor: addTaskController
                                              .ticketDetail !=
                                              null
                                              ? Colors.black12
                                              : AppTheme.colorWhite),
                                      hint: Align(
                                          alignment: Alignment.centerLeft,
                                          child: Text(Strings.status,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,
                                                fontFamily:
                                                AppTheme.appFontName,
                                              ))),
                                      style: AppTheme.dropdownTextStyle,
                                      isExpanded: true,
                                      isDense: true,
                                      value: addTaskController
                                          .selectedCaseStatus,
                                      items: addTaskController.caseStatusList!
                                          .map((CaseStatusDetail value) {
                                        return DropdownMenuItem<
                                            CaseStatusDetail>(
                                          value: value,
                                          child: Align(
                                            alignment: Alignment.centerLeft,
                                            child: CustomText(
                                              title: addTaskController.ticketDetail != null
                                                  ? value.text!.equalsIgnoreCase("Open")
                                                  ? value.text!.equalsIgnoreCase("Follow Up")
                                                  ? "In Progress"
                                                  : "In Progress"
                                                  : value.text!
                                                  : value.text!,
                                              colors: AppTheme.colorBlack,
                                              textAlign: TextAlign.start,
                                              fontSize: AppTheme.small,
                                              fontWeight: FontWeight.w500,
                                            ),
                                          ),
                                        );
                                      }).toList(),
                                      onChanged: (value) {
                                        addTaskController.selectedCaseStatus =
                                        value as CaseStatusDetail?;

                                        addTaskController
                                            .rootCauseResolutionList!
                                            .clear();
                                        addTaskController
                                            .rootCauseResolutionList!
                                            .clear();
                                        addTaskController
                                            .resolutionReasonsList!
                                            .clear();

                                        addTaskController.followUpScheduleDate= null;
                                        addTaskController.followUpScheduleTime= null;
                                        addTaskController.selectedRootCauseResolution= null;
                                        addTaskController
                                            .selectedResolutionReason = null;

                                        if (addTaskController
                                            .selectedCaseStatus !=
                                            null &&
                                            addTaskController
                                                .selectedCaseStatus!.value!
                                                .equalsIgnoreCase(
                                                "Raise and Close")) {
                                          addTaskController
                                              .checkTicketResolutionReasons();
                                        }
                                        addTaskController.update();
                                      },
                                      validator: (value) {
                                        if (addTaskController.ticketDetail ==
                                            null &&
                                            (value == null ||
                                                addTaskController
                                                    .selectedCaseStatus ==
                                                    null)) {
                                          return Strings.select_status;
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
                          addTaskController.selectedCaseStatus != null &&
                              addTaskController.selectedCaseStatus!.value!
                                  .equalsIgnoreCase("Raise and Close")
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
                                    child: InputTitleRequire(
                                        title: Strings.root_cause,
                                        require: true),
                                  ),
                                  const SizedBox(
                                    width: Constant.SMALL_PADDING,
                                  ),
                                  Flexible(
                                    flex: 2,
                                    child: IgnorePointer(
                                      ignoring: addTaskController
                                          .ticketDetail !=
                                          null
                                          ? true
                                          : false,
                                      child: DropdownButtonHideUnderline(
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
                                          decoration: Utils.ddlDecoration(
                                              fillColor: addTaskController
                                                  .ticketDetail !=
                                                  null
                                                  ? Colors.black12
                                                  : AppTheme.colorWhite),
                                          hint: Align(
                                              alignment:
                                              Alignment.centerLeft,
                                              child:
                                              Text(Strings.root_cause,
                                                  style: TextStyle(
                                                    fontSize: AppTheme
                                                        .medium,
                                                    color: AppTheme
                                                        .colorIconGrey,
                                                    fontFamily: AppTheme
                                                        .appFontName,
                                                  ))),
                                          style:
                                          AppTheme.dropdownTextStyle,
                                          isExpanded: true,
                                          isDense: true,
                                          value: addTaskController
                                              .selectedResolutionReason,
                                          items: addTaskController
                                              .resolutionReasonsList!
                                              .map(
                                                  (ResolutionReasonsDataList?
                                              value) {
                                                return DropdownMenuItem<
                                                    ResolutionReasonsDataList>(
                                                  value: value,
                                                  child: Align(
                                                    alignment:
                                                    Alignment.centerLeft,
                                                    child: CustomText(
                                                      title: value!.name,
                                                      colors:
                                                      AppTheme.colorBlack,
                                                      textAlign:
                                                      TextAlign.start,
                                                      fontSize:
                                                      AppTheme.small,
                                                      fontWeight:
                                                      FontWeight.w500,
                                                    ),
                                                  ),
                                                );
                                              }).toList(),
                                          onChanged: (value) {
                                            addTaskController
                                                .selectedResolutionReason =
                                            value
                                            as ResolutionReasonsDataList?;
                                            addTaskController
                                                .rootCauseResolutionList!
                                                .clear();
                                            if (addTaskController
                                                .selectedResolutionReason !=
                                                null) {
                                              addTaskController
                                                  .rootCauseResolutionList!
                                                  .addAll(addTaskController
                                                  .selectedResolutionReason!
                                                  .rootCauseResolutionMappingList!);
                                            }
                                            addTaskController.update();
                                          },
                                          validator: (value) {
                                            if (addTaskController
                                                .ticketDetail ==
                                                null &&
                                                (value == null ||
                                                    addTaskController
                                                        .selectedResolutionReason ==
                                                        null)) {
                                              return Strings
                                                  .select_root_cause;
                                            }
                                            return null;
                                          },
                                        ),
                                      ),
                                    ),
                                  ),
                                ],
                              ),
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
                                    child: InputTitleRequire(
                                        title: Strings.resolution,
                                        require: true),
                                  ),
                                  const SizedBox(
                                    width: Constant.SMALL_PADDING,
                                  ),
                                  Flexible(
                                    flex: 2,
                                    child: IgnorePointer(
                                      ignoring: addTaskController
                                          .ticketDetail !=
                                          null
                                          ? true
                                          : false,
                                      child: DropdownButtonHideUnderline(
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
                                          decoration: Utils.ddlDecoration(
                                              fillColor: addTaskController
                                                  .ticketDetail !=
                                                  null
                                                  ? Colors.black12
                                                  : AppTheme.colorWhite),
                                          hint: Align(
                                              alignment:
                                              Alignment.centerLeft,
                                              child:
                                              Text(Strings.resolution,
                                                  style: TextStyle(
                                                    fontSize: AppTheme
                                                        .medium,
                                                    color: AppTheme
                                                        .colorIconGrey,
                                                    fontFamily: AppTheme
                                                        .appFontName,
                                                  ))),
                                          style:
                                          AppTheme.dropdownTextStyle,
                                          isExpanded: true,
                                          isDense: true,
                                          value: addTaskController
                                              .selectedRootCauseResolution,
                                          items: addTaskController
                                              .rootCauseResolutionList!
                                              .map(
                                                  (RootCauseResolutionMappingList
                                              value) {
                                                return DropdownMenuItem<
                                                    RootCauseResolutionMappingList>(
                                                  value: value,
                                                  child: Align(
                                                    alignment:
                                                    Alignment.centerLeft,
                                                    child: CustomText(
                                                      title: value
                                                          .rootCauseReason,
                                                      colors:
                                                      AppTheme.colorBlack,
                                                      textAlign:
                                                      TextAlign.start,
                                                      fontSize:
                                                      AppTheme.small,
                                                      fontWeight:
                                                      FontWeight.w500,
                                                    ),
                                                  ),
                                                );
                                              }).toList(),
                                          onChanged: (value) {
                                            addTaskController
                                                .selectedRootCauseResolution =
                                            value
                                            as RootCauseResolutionMappingList?;
                                            addTaskController.update();
                                          },
                                          validator: (value) {
                                            return null;
                                          },
                                        ),
                                      ),
                                    ),
                                  ),
                                ],
                              ),
                              const SizedBox(
                                  height: Constant.MEDIUM_PADDING),
                            ],
                          )
                              : const SizedBox.shrink(),

                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          addTaskController.selectedCaseStatus != null &&
                              addTaskController.selectedCaseStatus!.value!
                                  .equalsIgnoreCase("Follow Up")
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
                                    child: InputTitleRequire(
                                        title: Strings.followup_date_time,
                                        require: true),
                                  ),
                                  const SizedBox(
                                    width: Constant.SMALL_PADDING,
                                  ),
                                  Flexible(
                                    flex: 2,
                                    child: CoustomTextField(
                                        labelText:
                                        Strings.followup_date_time,
                                        suffixIcon: Padding(
                                          padding:
                                          const EdgeInsetsDirectional
                                              .all(Constant
                                              .MEDIUM_PADDING),
                                          child: SvgPicture.asset(
                                            calendarSvg,
                                            color: AppTheme.colorBlack,
                                            width: Constant.ICON_SIZE_S,
                                            height: Constant.ICON_SIZE_S,
                                            // myIcon is a 48px-wide widget.
                                          ),
                                        ),
                                        textEditingController:
                                        addTaskController
                                            .followupDateTimeController,
                                        borderEnableColors:
                                        AppTheme.colorGrey,
                                        textInputAction:
                                        TextInputAction.next,
                                        hintColor: AppTheme.colorIconGrey,
                                        onTextValidator: (String? value) {
                                          if (value!.isEmpty) {
                                            return Strings
                                                .select_followup_date_time;
                                          }
                                          return null;
                                        },
                                        onTextFiledOnTap: () {
                                          // if (addEditInwardsController.inwardsDetail !=
                                          //     null) {
                                          //   print("not editable");
                                          // } else {
                                          selectDate(
                                              Strings.followup_date_time,
                                              DateTime(
                                                  DateTime.now().year -
                                                      10),
                                              DateTime(
                                                  DateTime.now().year +
                                                      10));
                                          // }
                                        },
                                        borderCorner:
                                        Constant.INPUT_ROUNDED_CORNER,
                                        contentPadding:
                                        const EdgeInsets.symmetric(
                                            horizontal: Constant
                                                .LARGE_PADDING),
                                        readOnly: true),
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
                                child: InputTitleRequire(
                                    title: Strings.remarks, require: true),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: CoustomTextField(
                                    labelText: Strings.remarks,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController:
                                    addTaskController.remarksController,
                                    borderEnableColors: AppTheme.colorIconGrey,
                                    borderFocusColors: AppTheme.colorIconGrey,
                                    textColor: AppTheme.colorBlack,
                                    keyboardType: TextInputType.text,
                                    fontSize: AppTheme.small,
                                    textInputAction: TextInputAction.done,
                                    fontWeight: FontWeight.w500,
                                    contentPadding: const EdgeInsets.symmetric(
                                        horizontal: Constant.MEDIUM_PADDING,
                                        vertical: Constant.MEDIUM_PADDING),
                                    borderCorner: Constant.BTN_ROUNDED_CORNER,
                                    onTextValidator: (String? value) {
                                      if (value!.isEmpty) {
                                        return Strings.please_enter_remarks;
                                      }
                                      return null;
                                    },
                                    onTextFiledOnTap: () {},
                                    readOnly: false),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          GestureDetector(
                            onTap: () {
                              checkCameraPermission();
                              // _showCaptureDialog();
                            },
                            child: Row(
                              mainAxisSize: MainAxisSize.max,
                              crossAxisAlignment: CrossAxisAlignment.center,
                              mainAxisAlignment: MainAxisAlignment.start,
                              children: [
                                Icon(
                                  Icons.add_circle_outline_rounded,
                                  color: AppTheme.title_dark,
                                  size: 18,
                                ),
                                CustomText(
                                  title: " ${Strings.select_file} :",
                                  colors: AppTheme.title_dark,
                                  textAlign: TextAlign.center,
                                  fontSize: AppTheme.small + 1,
                                  fontWeight: FontWeight.w500,
                                ),
                              ],
                            ),
                          ),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          fileViewWidget(),
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
                        title: addTaskController.from
                            .equalsIgnoreCase(Strings.edit)
                            ? Strings.update_ticket
                            : Strings.save,
                        fontSize: AppTheme.medium,
                        fontWeight: FontWeight.w400,
                      ),
                    ),
                  ),
                ],
              ),
            ]),
      ),
    );
  }

  _appBar() {
    return DynamicAppBar(
        addTaskController.from.equalsIgnoreCase(Strings.edit)
            ? Strings.update_task
            : Strings.create_task,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  validateForm() {
    if (addTicketFormKey.currentState!.validate()) {
      addTaskController.createTaskApiCall();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  checkCameraPermission() async {
    PermissionService().requestCameraAndStoragePermission(
        onPermissionDenied: () {
          if (Platform.isIOS) {
            uploadImageOption();
          } else {
            permissionDenyDialog();
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
              showCameraSelect: true);
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
      addTaskController.setBtnClickEvent(true);
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
      if (size <= 5000) {
        addTaskController.fileDetail = FileDetail(
            fileName: result.files.single.name,
            filePath: "",
            filePathLocal: result.files.single.path!,
            isFileLocal: true,
            fileType: result.files.single.extension);
      } else {
        Utils.showSnackbar(
            Strings.ERROR,
            "Your file size is very large, please select up to 5000kb file size.",
            AppTheme.colorWhite,
            AppTheme.colorRed);
      }
    }
    addTaskController.update();
  }

  openCameraGallery(ImageSource source) async {
    try {
      XFile? image;
      image = await imagePicker.pickImage(source: source);

      if (image != null && !image.path.isNullOrEmpty()) {
        num size = await Utils.getFileSize(image.path, 1);
        print("image picker file size : ${size}");
        if (size <= 5000) {
          addTaskController.fileDetail = FileDetail(
              fileName: image.name,
              filePath: "",
              filePathLocal: image.path,
              isFileLocal: true,
              fileType: Strings.image);
        } else {
          Utils.showSnackbar(
              Strings.ERROR,
              "Your file size is very large, please select up to 5000kb file size.",
              AppTheme.colorWhite,
              AppTheme.colorRed);
        }
      }
      addTaskController.update();
    } catch (e) {
      print("image picker exception : $e");
    }
  }

  fileViewWidget() {
    return addTaskController.fileDetail != null
        ? FileGridItem(
      fileDetail: addTaskController.fileDetail!,
      onTapItem: () {},
      bottomAction: fileItemAction(),
    )
        : Container();
  }

  fileItemAction() {
    return addTaskController.fileDetail != null &&
        addTaskController.fileDetail!.isFileLocal == true
        ? Align(
        alignment: Alignment.topRight,
        child: InkWell(
          onTap: () {
            addTaskController.fileDetail = null;
            addTaskController.update();
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

  openParentCustomerScreen() async {
    var result = await Get.to(CreditCustomerList(), arguments: {});
    if (result != null) {
      CustomerCreditList data = result;
      if (data != null) {
        addTaskController.selectedCust = data;
        log("openParentCustomerScreen>> ${data.id}");
        // addTaskController.getCreditInvoiceListData(data.id!);
        addTaskController.servicesAreaList!.clear();
        addTaskController.selectedServicesArea!.clear();
        addTaskController.customerController.text = data.name!;
        addTaskController.getCustomerDetail();
        addTaskController.update();
      }
    }
  }

  showServicesAreaSelectionDialog(String from) {
    List<GetActiveServiceDataList> item = [];

    List<SerialNumberDataList> serialItem = [];

    if (from.equalsIgnoreCase(Strings.service)) {
      if (addTaskController.servicesAreaList != null &&
          addTaskController.servicesAreaList!.isNotEmpty) {
        for (var element in addTaskController.servicesAreaList!) {
          element.selected = false;
        }
        if (addTaskController.selectedServicesArea!.isNotEmpty) {
          for (var element in addTaskController.servicesAreaList!) {
            for (GetActiveServiceDataList selElement
            in addTaskController.selectedServicesArea!) {
              if (selElement.id == element.id!) {
                element.selected = true;
              }
            }
          }
        }
        item.addAll(addTaskController.servicesAreaList!);
      }
    } else if (from.equalsIgnoreCase(Strings.serial_no)) {
      if (addTaskController.getSerialNumberDataList != null &&
          addTaskController.getSerialNumberDataList!.isNotEmpty) {
        for (var element in addTaskController.getSerialNumberDataList!) {
          element.selected = false;
        }
        if (addTaskController.selectedSerialNumberDataList!.isNotEmpty) {
          for (var element in addTaskController.getSerialNumberDataList!) {
            for (SerialNumberDataList selElement
            in addTaskController.selectedSerialNumberDataList!) {
              if (selElement.id == element.id!) {
                element.selected = true;
              }
            }
          }
        }
        serialItem.addAll(addTaskController.getSerialNumberDataList!);
      }
    }

    for (var element in item) {
      addTaskController.selectedServiceIDs!.add(element.id!);
      addTaskController.update();
    }
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return CustServiceAreaDialog(
            serviceAreaAction: this,
            fromFor: from,
            itemsOrgLst: item,
            serialItemsOrgLst: serialItem,
          );
        });
  }

  @override
  void serviceAreaBtnAction(
      {String? identifier, List<GetActiveServiceDataList>? selectedItem}) {
    Get.back();
    if (identifier.toString().equalsIgnoreCase(Strings.service) &&
        selectedItem != null &&
        selectedItem.isNotEmpty) {
      String serviceAreaName = "";
      String custServiceId = "";
      addTaskController.selectedServicesArea!.clear();
      addTaskController.servicesAreaList!.clear();
      for (GetActiveServiceDataList element in selectedItem) {
        addTaskController.selectedServicesArea!.add(element);
        serviceAreaName = "$serviceAreaName${element.serviceName!}, ";
        custServiceId = "$custServiceId${element.id!},";
      }

      addTaskController.selectedServicesArea!.forEach((element) {
        addTaskController.serviceIDS.add(element.id!);
      });

      if (!serviceAreaName.isNullOrEmpty() &&
          serviceAreaName.contains(",") &&
          serviceAreaName.length >= 2) {
        serviceAreaName =
            serviceAreaName.substring(0, serviceAreaName.length - 2);
      }
      addTaskController.serviceController.text = serviceAreaName;
      addTaskController
          .getSerialNumberTicket(addTaskController.serviceIDS.join(","));
      addTaskController.getTicketReasonCategoryByActiveServices(
          addTaskController.serviceIDS);
    }
    addTaskController.update();
  }

  @override
  void serialNoBtnAction(
      {String? identifier, List<SerialNumberDataList>? selectedSerialItem}) {
    Get.back();
    if (identifier.toString().equalsIgnoreCase(Strings.service) &&
        selectedSerialItem != null &&
        selectedSerialItem.isNotEmpty) {
      String serialNumber = "";
      String custSerialNumberId = "";
      addTaskController.selectedSerialNumberDataList!.clear();
      for (SerialNumberDataList element in selectedSerialItem) {
        addTaskController.selectedSerialNumberDataList!.add(element);
        serialNumber = "$serialNumber${element.serialNumber!}, ";
        custSerialNumberId = "$custSerialNumberId${element.id!},";
      }
      addTaskController.custSerialNumberController.text = serialNumber;
    }
    addTaskController.update();
  }

  Future<void> selectDate(
      String identity,
      DateTime firstDate,
      DateTime lastDate,
      ) async {
    DateTime? selectedDate;
    if (identity == Strings.followup_date_time) {
      if (addTaskController.selectedFollowUpDate != null) {
        selectedDate = addTaskController.selectedFollowUpDate;
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
        addTaskController.selectedFollowUpDate = picked;
        addTaskController.update();
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
        addTaskController.selectedFollowUpDate!.year,
        addTaskController.selectedFollowUpDate!.month,
        addTaskController.selectedFollowUpDate!.day,
        picked.hour,
        picked.minute,
      );
      addTaskController.followupDateTimeController.text =
          addTaskController.dateFormat.format(dt);
      addTaskController.followUpScheduleDate = addTaskController.apiDateFormat.format(dt);
      addTaskController.followUpScheduleTime = addTaskController.apiTimeFormat.format(dt);
      addTaskController.update();
    }
  }


  Future<void> _captureImageAndLocation() async {
    // Request permissions
    await Permission.camera.request();
    await Permission.locationWhenInUse.request();

    if (await Permission.camera.isGranted &&
        await Permission.locationWhenInUse.isGranted) {
      // Pick image
      final pickedFile =
      await ImagePicker().pickImage(source: ImageSource.camera);
      if (pickedFile != null) {
        setState(() {
          _image = File(pickedFile.path);
        });
      }

      // Get location
      Position position = await Geolocator.getCurrentPosition(
          desiredAccuracy: LocationAccuracy.high);
      setState(() {
        _location = position;
      });

      print("Image path: ${_image?.path}");
      print("Location: ${_location?.latitude}, ${_location?.longitude}");
    } else {
      print('Permissions denied');
    }
  }
}