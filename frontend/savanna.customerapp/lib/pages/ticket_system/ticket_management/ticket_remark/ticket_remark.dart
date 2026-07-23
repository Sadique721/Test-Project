import 'package:savbill/pages/dashboard/model/response/case_status_response.dart';
import 'package:savbill/pages/ticket_system/ticket_management/get_staff_user_service_res.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_change_status_controller.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_remark/ticket_remark_controller.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_resolution_reasons_res.dart';
import 'package:savbill/theme/app_theme.dart';
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

import '../../../model/dropdown_detail.dart';

class TicketRemarkScreen extends StatefulWidget {
  @override
  _TicketRemarkScreenState createState() => _TicketRemarkScreenState();
}

class _TicketRemarkScreenState extends State<TicketRemarkScreen>
    with WidgetsBindingObserver {
  final ticketRemarkController = Get.put(TicketRemarkController());

  final tikcetRemarkFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;
  final ImagePicker imagePicker = ImagePicker();

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back();
  }

  // _backScreen() {
  //   Get.back(result: ticketRemarkController.isChangeData);
  // }

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
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<TicketRemarkController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(controller),
          ),
          ProgressBar(isLoader: ticketRemarkController.isLoading),
        ]);
      }),
    );
  }

  _body(TicketRemarkController controller) {
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
                    key: tikcetRemarkFormKey,
                    autovalidateMode: autoValidateMode,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisAlignment: MainAxisAlignment.start,
                      children: [
                        /*__________________ remark type ____________________*/

                        const SizedBox(height: Constant.SCREEN_PADDING),
                        InputTitleRequire(
                            title: Strings.remark_type, require: true),
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
                                Strings.remark_type,
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
                            value: controller.selectRemarkType,
                            items: controller.remarkTypeList
                                ?.map((DropdownDetail value) {
                              return DropdownMenuItem<DropdownDetail>(
                                value: value,
                                child: Text(value.text!),
                              );
                            }).toList(),
                            onChanged: (value) {
                              controller.selectRemarkType =
                                  value as DropdownDetail?;
                              controller.update();
                            },
                            validator: (value) {
                              if (value == null ||
                                  controller.selectRemarkType == null) {
                                return Strings.select_remark_type;
                              }
                              return null;
                            },
                          ),
                        ),

                        /*__________________ Remarks   ____________________*/

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
                                ticketRemarkController.remarksController,
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
    return DynamicAppBar(Strings.ticket_remark, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  validateForm() {
    if (tikcetRemarkFormKey.currentState!.validate()) {
      ticketRemarkController.saveTicketFollowupCall();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }
}
