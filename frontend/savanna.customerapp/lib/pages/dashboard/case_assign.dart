import 'package:savbill/pages/dashboard/case_assign_controller.dart';
import 'package:savbill/pages/dashboard/model/response/case_assign_staff_lst.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class CaseAssign extends StatefulWidget {
  @override
  _CaseAssignState createState() => _CaseAssignState();
}

class _CaseAssignState extends State<CaseAssign> {
  final caseAssignController = Get.put(CaseAssignController());
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
      child: GetBuilder<CaseAssignController>(builder: (controller) {
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

  _body(CaseAssignController controller) {
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
                          titleWithRequireWidget(Strings.staff),
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
                              decoration: InputDecoration(
                                  filled: true,
                                  contentPadding: const EdgeInsets.fromLTRB(
                                      Constant.LARGE_PADDING,
                                      0,
                                      Constant.LARGE_PADDING,
                                      0),
                                  fillColor: AppTheme.colorWhite,
                                  hintText: Strings.staff,
                                  hintStyle: AppTheme.dropdownHintStyle,
                                  labelStyle: AppTheme.dropdownLabelStyle,
                                  errorStyle: AppTheme.dropdownErrorStyle,
                                  alignLabelWithHint: true,
                                  border: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(
                                        Constant.DROP_DOWN_ROUNDED_CORNER),
                                    borderSide: BorderSide(
                                        color: AppTheme.colorBlack, width: 0.8),
                                  ),
                                  focusColor: Colors.transparent,
                                  focusedBorder: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(
                                        Constant.DROP_DOWN_ROUNDED_CORNER),
                                    borderSide: BorderSide(
                                        color: AppTheme.colorBlack, width: 0.8),
                                  ),
                                  errorMaxLines: 3),
                              style: AppTheme.dropdownTextStyle,
                              isExpanded: true,
                              isDense: true,
                              value: caseAssignController.selectedStaff,
                              items: caseAssignController.staffList!
                                  .map((CaseStaffDetail value) {
                                return DropdownMenuItem<CaseStaffDetail>(
                                  value: value,
                                  child: Text(value.fullName!),
                                );
                              }).toList(),
                              onChanged: (value) {
                                caseAssignController.selectedStaff =
                                value as CaseStaffDetail?;
                                caseAssignController.update();
                              },
                              validator: (value) {
                                if (value == null ||
                                    caseAssignController.selectedStaff ==
                                        null ||
                                    caseAssignController.selectedStaff?.id ==
                                        0) {
                                  return Strings.pelase_select_staff;
                                }
                                return null;
                              },
                            ),
                          ),

                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),

                          titleWithRequireWidget(Strings.remarks),
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
                      title: Strings.save,
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
    return DynamicAppBar(
        Strings.assign_ticket,
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
