import 'package:savbill/pages/customer_caf/close_caf/close_caf_status_controller.dart';
import 'package:savbill/pages/customer_caf/response/reject_reason_caf_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class CloseCafStatusScreen extends StatefulWidget {
  @override
  _CloseCafStatusState createState() => _CloseCafStatusState();
}

class _CloseCafStatusState extends State<CloseCafStatusScreen> {
  final closeCafStatusController = Get.put(CloseCafStatusController());
  final scheduleFollowUpFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

  @override
  void initState() {
    super.initState();
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<CloseCafStatusController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: closeCafStatusController.isLoading),
      ]);
    });
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
                      key: scheduleFollowUpFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          const SizedBox(
                            height: Constant.SCREEN_PADDING +
                                Constant.SMALL_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.rejected_reason,
                              require: true),
                          const SizedBox(height: Constant.SMALL_PADDING),
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
                                  Strings.select_rejected_reason_caf,
                                  style: TextStyle(
                                    fontSize: AppTheme.small,
                                    color: AppTheme.colorIconGrey,
                                    fontFamily: AppTheme.appFontName,
                                  ),
                                ),
                              ),
                              style: AppTheme.dropdownTextStyle,
                              isExpanded: false,
                              isDense: true,
                              value: closeCafStatusController.selectRejectedReason,
                              items: closeCafStatusController.closeCafContentList
                                  ?.map((CloseCafContentList value) {
                                return DropdownMenuItem<CloseCafContentList>(
                                  value: value,
                                  child: Text(
                                    value.name!,
                                    style: TextStyle(
                                      fontSize: AppTheme.small + 1,
                                      color: AppTheme.colorBlack,
                                      fontFamily: AppTheme.appFontName,
                                    ),
                                  ),
                                );
                              }).toList(),
                              onChanged: (value) {
                                closeCafStatusController.selectRejectedReason =
                                    value as CloseCafContentList?;
                                closeCafStatusController.rejectSubReasonDtoList!.clear();
                                closeCafStatusController.selectedRejectedSubReason = null;
                                if (closeCafStatusController.selectRejectedReason!
                                        .rejectSubReasonDtoList ==
                                    null) {
                                  closeCafStatusController.isRejectedSubReason
                                      .value = false;
                                } else {
                                  if (closeCafStatusController.selectRejectedReason!
                                      .rejectSubReasonDtoList!.isNotEmpty) {
                                    closeCafStatusController.isRejectedSubReason
                                        .value = true;
                                    closeCafStatusController.rejectSubReasonDtoList!
                                        .addAll(closeCafStatusController
                                            .selectRejectedReason!
                                            .rejectSubReasonDtoList!);
                                    closeCafStatusController.update();
                                  } else {
                                    closeCafStatusController.isRejectedSubReason
                                        .value = false;
                                  }
                                }
                                closeCafStatusController.update();
                              },
                              validator: (value) {
                                if(value == null){
                                  return Strings.select_rejected_reason_caf;
                                }
                                return null;
                              },
                            ),
                          ),
                          closeCafStatusController.isRejectedSubReason.value == true
                              ? Column(
                                  mainAxisSize: MainAxisSize.max,
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  mainAxisAlignment: MainAxisAlignment.start,
                                  children: [
                                    const SizedBox(
                                        height: Constant.MEDIUM_PADDING),
                                    InputTitleRequire(
                                        title: Strings.rejected_sub_reason,
                                        require: true),
                                    const SizedBox(height: Constant.SMALL_PADDING),
                                    DropdownButtonHideUnderline(
                                      child: DropdownButtonFormField(
                                        icon: SvgPicture.asset(
                                          downArrowSvg,
                                          height:
                                              Constant.DROP_DOWN_ARROW_W_H,
                                          width: Constant.DROP_DOWN_ARROW_W_H,
                                          color: AppTheme.colorBlack,
                                          fit: BoxFit.fill,
                                        ),
                                        decoration: Utils.ddlDecoration(),
                                        hint: Align(
                                          alignment: Alignment.centerLeft,
                                          child: Text(
                                            Strings
                                                .select_rejected_sub_reason_caf,
                                            style: TextStyle(
                                              fontSize: AppTheme.small,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily:
                                                  AppTheme.appFontName,
                                            ),
                                          ),
                                        ),
                                        style: AppTheme.dropdownTextStyle,
                                        isExpanded: false,
                                        isDense: true,
                                        value: closeCafStatusController
                                            .selectedRejectedSubReason,
                                        items: closeCafStatusController
                                            .rejectSubReasonDtoList
                                            ?.map((RejectSubReasonDtoList
                                                value) {
                                          return DropdownMenuItem<
                                              RejectSubReasonDtoList>(
                                            value: value,
                                            child: Text(
                                              value.name!,
                                              style: TextStyle(
                                                fontSize: AppTheme.small + 1,
                                                color: AppTheme.colorBlack,
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ),
                                            ),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          closeCafStatusController
                                                  .selectedRejectedSubReason =
                                              value
                                                  as RejectSubReasonDtoList?;
                                        },
                                        validator: (value) {
                                          if(value == null){
                                            return Strings.select_rejected_sub_reason_caf;
                                          }
                                          return null;
                                        },
                                      ),
                                    ),
                                  ],
                                )
                              : const SizedBox.shrink(),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          InputTitleRequire(
                              title: Strings.remarks,
                              require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.enter_remarks,
                              textEditingController:
                                  closeCafStatusController.remarkController,
                              maxLines: 2,
                              maxLength: 250,
                              borderEnableColors: AppTheme.colorBlack,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              keyboardType: TextInputType.multiline,
                              onTextValidator: (String? value) {
                                if (value!.isEmpty) {
                                  return Strings.please_enter_remarks;
                                }
                                return null;
                              },
                              onTextFiledOnTap: () {},
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.LARGE_PADDING),
                              readOnly: false),
                          const SizedBox(height: Constant.MEDIUM_PADDING),

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
                      child: Row(
                        crossAxisAlignment: CrossAxisAlignment.center,
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          const Padding(
                            padding: EdgeInsets.only(
                                left: Constant.SMALL_PADDING,
                                right: Constant.SMALL_PADDING),
                            child: Icon(
                              size: Constant.ICON_SIZE_M,
                              Icons.check_circle,
                              color: Colors.white,
                            ),
                          ),
                          CustomText(
                            title: Strings.apply,
                            fontSize: AppTheme.medium,
                            fontWeight: FontWeight.w400,
                          ),
                        ],
                      ),
                    ),
                  ),
                ],
              ),
            ]),
      ),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        "${Strings.close_caf}",
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
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


  validateForm() {
    if (scheduleFollowUpFormKey.currentState!.validate()) {
      closeCafStatusController.postCloseCAFCall();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }
}
