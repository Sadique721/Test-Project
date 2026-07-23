import 'package:savbill/pages/customer_caf/schedule_followup/schedule_follow_up_controller.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
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

class ScheduleFollowUpScreen extends StatefulWidget {
  @override
  _ScheduleFollowUpState createState() => _ScheduleFollowUpState();
}

class _ScheduleFollowUpState extends State<ScheduleFollowUpScreen> {
  final scheduleFollowUpController = Get.put(ScheduleFollowUpController());
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
    return GetBuilder<ScheduleFollowUpController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: SafeArea(
            child: _body(),
          ),
        ),
        ProgressBar(isLoader: scheduleFollowUpController.isLoading),
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
                              title: Strings.followup_name, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.followup_name,
                              fillColor: AppTheme.colorGrayTxtBg,
                              textEditingController: scheduleFollowUpController
                                  .followupNameController,
                              borderEnableColors: AppTheme.colorBlack,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              keyboardType: TextInputType.text,
                              onTextValidator: (String? value) {
                                return null;
                              },
                              onTextFiledOnTap: () {},
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.LARGE_PADDING),
                              readOnly: true),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.followup_date_time, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.followup_date_time,
                              suffixIcon: Padding(
                                padding: const EdgeInsetsDirectional.all(
                                    Constant.MEDIUM_PADDING),
                                child: SvgPicture.asset(
                                  calendarSvg,
                                  color: AppTheme.colorBlack,
                                  width: Constant.ICON_SIZE_S,
                                  height: Constant.ICON_SIZE_S,
                                  // myIcon is a 48px-wide widget.
                                ),
                              ),
                              textEditingController: scheduleFollowUpController
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
                                // if (addEditInwardsController.inwardsDetail !=
                                //     null) {
                                //   print("not editable");
                                // } else {
                                selectDate(
                                    Strings.followup_date_time,
                                    DateTime(DateTime.now().year - 10),
                                    DateTime(DateTime.now().year + 10));
                                // }
                              },
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.LARGE_PADDING),
                              readOnly: true),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.remarks, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.enter_remarks,
                              textEditingController:
                              scheduleFollowUpController.remarkController,
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
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
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
                            title: Strings.schedule,
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
        "${Strings.schedule} a ${Strings.followup}",
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

  Future<void> selectDate(
    String identity,
    DateTime firstDate,
    DateTime lastDate,
  ) async {
    DateTime? selectedDate;
    if (identity == Strings.followup_date_time) {
      if (scheduleFollowUpController.selectedFollowUpDate != null) {
        selectedDate = scheduleFollowUpController.selectedFollowUpDate;
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
        scheduleFollowUpController.selectedFollowUpDate = picked;
        scheduleFollowUpController.update();
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
        scheduleFollowUpController.selectedFollowUpDate!.year,
        scheduleFollowUpController.selectedFollowUpDate!.month,
        scheduleFollowUpController.selectedFollowUpDate!.day,
        picked.hour,
        picked.minute,
      );
      scheduleFollowUpController.followupDateTimeController.text =
          scheduleFollowUpController.dateFormat.format(dt);
      scheduleFollowUpController.followUpScheduleDateTime =
          scheduleFollowUpController.apiDateTimeFormat.format(dt);
      scheduleFollowUpController.update();
    }
  }

  validateForm() {
    if (scheduleFollowUpFormKey.currentState!.validate()) {
      scheduleFollowUpController.scheduleCAFFollowUpAdd();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }
}