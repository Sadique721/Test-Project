import 'dart:developer';
import 'package:savbill/pages/task_management/model/response/get_team_by_id_res.dart';
import 'package:savbill/pages/task_management/task_mgmt/task_assign/task_case_assign_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

import '../../model/response/get_all_team_list_res.dart';

class TaskCaseAssign extends StatefulWidget {
  @override
  _TaskCaseAssignState createState() => _TaskCaseAssignState();
}

class _TaskCaseAssignState extends State<TaskCaseAssign> {
  final caseAssignController = Get.put(TaskCaseAssignController());
  final caseAssignFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;


  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back(result: false);
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<TaskCaseAssignController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(controller),
          ),
          ProgressBar(isLoader: caseAssignController.isLoading),
        ]);
      }), /**/
    );
  }

  _body(TaskCaseAssignController controller) {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: Container(
        color: AppTheme.colorBG,
        width: MediaQuery
            .of(context)
            .size
            .width,
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
                    key: caseAssignFormKey,
                    autovalidateMode: autoValidateMode,
                    child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          const SizedBox(
                            height: Constant.SCREEN_PADDING +
                                Constant.SMALL_PADDING,
                          ),
                          titleWithRequireWidget(Strings.team,true),
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
                                  Strings.select_a_team,
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
                              value: caseAssignController.selectedTeam,
                              items: caseAssignController.teamList!
                                  .map((AllTeamDataList value) {
                                return DropdownMenuItem<AllTeamDataList>(
                                  value: value,
                                  child: Text(value.displayName!),
                                );
                              }).toList(),
                              onChanged: (value) {
                                caseAssignController.selectedTeam =
                                value as AllTeamDataList?;
                                log("getByTeamIds===>${(value!.id!)}");
                                caseAssignController.selectedTeamByIdData = null;
                                caseAssignController.getByTeamIds(value.id!);
                                caseAssignController.update();
                              },
                              validator: (value) {
                                if (value == null ||
                                    caseAssignController.selectedTeam ==
                                        null ||
                                    caseAssignController.selectedTeam?.id ==
                                        0) {
                                  return Strings.pelase_select_team;
                                }
                                return null;
                              },
                            ),
                          ),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),

                          titleWithRequireWidget(Strings.staff,false),
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
                                  Strings.select_a_staff,
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
                              value: caseAssignController.selectedTeamByIdData,
                              items: caseAssignController.teamByIdList!
                                  .map((TeamByIdDataList value) {
                                return DropdownMenuItem<TeamByIdDataList>(
                                  value: value,
                                  child: Text(value.displayName!),
                                );
                              }).toList(),
                              onChanged: (value) {
                                caseAssignController.selectedTeamByIdData =
                                value as TeamByIdDataList?;
                                caseAssignController.update();
                              },
                              validator: (value) {
                                return null;
                              },
                            ),
                          ),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),

                          titleWithRequireWidget(Strings.remarks,true),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          Container(
                            decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(7.0),
                              // color: AppTheme.colorWhite,
                            ),
                            child: TextFormField(
                              controller:
                              caseAssignController.remarksController,
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
                                if (value!.isEmpty) {
                                  return Strings.please_enter_remarks;
                                }
                                return null;
                              },
                            ),
                          ),
                          const SizedBox(
                            height: Constant.EXTRA_LARGE_PADDING,
                          ),
                        ]),
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
                      title: Strings.assign_staff,
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

  titleWithRequireWidget(String title,bool require) {
    return Row(
      children: [
        CustomText(
          title: title,
          colors: AppTheme.title_dark,
          textAlign: TextAlign.start,
          fontSize: AppTheme.medium,
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

  _appBar() {
    return DynamicAppBar(
        Strings.assign_task,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  validateForm() {
    if (caseAssignFormKey.currentState!.validate()) {
      caseAssignController.caseAssignRequest();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }


}