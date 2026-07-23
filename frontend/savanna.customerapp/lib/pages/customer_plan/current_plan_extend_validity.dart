import 'dart:developer';

import 'package:savbill/pages/customer_plan/current_plan_extend_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class CurrentPlanExtendValidity extends StatefulWidget {
  @override
  _CurrentPlanExtendValidityState createState() =>
      _CurrentPlanExtendValidityState();
}

class _CurrentPlanExtendValidityState extends State<CurrentPlanExtendValidity>
    with WidgetsBindingObserver {
  final currentPlanExtendController = Get.put(CurrentPlanExtendController());
  final currentPlanExtendFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back(result: false);
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
        print("on resume method call");
        // if (createCreditController.checkBtnClickEvent) {
        //   createCreditController.setBtnClickEvent(false);
        // }
        return;
      default:
        return;
    }
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<CurrentPlanExtendController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(controller),
          ),
          ProgressBar(isLoader: currentPlanExtendController.isLoading),
        ]);
      }), /**/
    );
  }

  _body(CurrentPlanExtendController controller) {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: Container(
        color: AppTheme.colorBG,
        width: MediaQuery.of(context).size.width,
        child: Column(
            mainAxisSize: MainAxisSize.max,
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
                      key: currentPlanExtendFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          const SizedBox(height: Constant.SCREEN_PADDING),

                          /*_______________ Start Date ______________________________*/
                          InputTitleRequire(
                              title:
                                  "${Strings.downtime} ${Strings.start_date}",
                              require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText:
                                  "${Strings.enter_extension} ${Strings.start_date}",
                              suffixIcon: Padding(
                                padding: const EdgeInsetsDirectional.all(
                                    Constant.MEDIUM_PADDING),
                                child: SvgPicture.asset(
                                  calendarSvg,
                                  color: AppTheme.colorAccent,
                                  width: Constant.ICON_SIZE_S,
                                  height: Constant.ICON_SIZE_S,
                                  // myIcon is a 48px-wide widget.
                                ),
                              ),
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController:
                                  controller.downTimeStartDateController,
                              borderEnableColors: AppTheme.colorIconGrey,
                              borderFocusColors: AppTheme.colorIconGrey,
                              textColor: AppTheme.colorBlack,
                              fontSize: AppTheme.small,
                              fontWeight: FontWeight.w500,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.MEDIUM_PADDING),
                              borderCorner: Constant.BTN_ROUNDED_CORNER,
                              onTextValidator: (String? value) {
                                if(value!.isEmpty){
                                  return Strings.down_start_date;
                                }
                              },
                              onTextFiledOnTap: () {
                                selectDate(
                                    context,
                                    Constant.extend_start_Date,
                                    DateTime(DateTime.now().year - 10),
                                    DateTime(DateTime.now().year + 10));
                              },
                              readOnly: true),

                          /*_______________ End Date ______________________________*/

                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: "${Strings.downtime} ${Strings.end_date}",
                              require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText:
                                  "${Strings.enter_extension} ${Strings.end_date}",
                              suffixIcon: Padding(
                                padding: const EdgeInsetsDirectional.all(
                                    Constant.MEDIUM_PADDING),
                                child: SvgPicture.asset(
                                  calendarSvg,
                                  color: AppTheme.colorAccent,
                                  width: Constant.ICON_SIZE_S,
                                  height: Constant.ICON_SIZE_S,
                                  // myIcon is a 48px-wide widget.
                                ),
                              ),
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController:
                                  controller.downTimeEndDateController,
                              borderEnableColors: AppTheme.colorIconGrey,
                              borderFocusColors: AppTheme.colorIconGrey,
                              textColor: AppTheme.colorBlack,
                              fontSize: AppTheme.small,
                              fontWeight: FontWeight.w500,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.MEDIUM_PADDING),
                              borderCorner: Constant.BTN_ROUNDED_CORNER,
                              onTextValidator: (String? value) {
                                if(value!.isEmpty){
                                  return Strings.down_end_date;
                                }
                              },
                              onTextFiledOnTap: () {
                                selectDate(
                                    context,
                                    Constant.extend_end_Date,
                                    DateTime(DateTime.now().year - 10),
                                    DateTime(DateTime.now().year + 10));
                              },
                              readOnly: true),
                          Column(
                            children: [
                              const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              ),
                              CustomText(
                                  title:
                                      "${Strings.total} ${Strings.downtime} ${Strings.day}s: ${totalDownTimeDays() ?? 0}",
                                  colors: AppTheme.colorBlack,
                                  textAlign: TextAlign.start,
                                  fontSize: AppTheme.medium + 1,
                                  fontWeight: FontWeight.w500),
                              const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              ),
                            ],
                          ),

                          /*___________________ Remarks___________________________*/

                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.remarks, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          Container(
                            decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(8.0),
                              color: AppTheme.colorWhite,
                            ),
                            child: TextFormField(
                              controller: controller.remarksController,
                              maxLines: 4,
                              maxLength: 250,
                              style: const TextStyle(fontSize: AppTheme.medium),
                              decoration: InputDecoration(
                                hintText: Strings.enter_remarks,
                                alignLabelWithHint: true,
                                contentPadding: const EdgeInsets.all(
                                    Constant.TEXT_FIELD_CONTENT_PADDING),
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
                                if(value!.isEmpty){
                                  return Strings.please_select_remark;
                                }
                                return null;
                              },
                            ),
                          ),
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
                      child: CustomText(
                        title: Strings.save,
                        fontSize: AppTheme.medium,
                        fontWeight: FontWeight.w400,
                      ),
                    ),
                  ),
                  const SizedBox(width: 2,),
                  Expanded(
                    child: SimpleButton(
                      onTap: () {
                        Get.back();
                      },
                      radius: 0,
                      height: Constant.BOTTOM_BTN_HEIGHT,
                      bgColors: AppTheme.colorPrimary,
                      borderColors: AppTheme.colorPrimary,
                      child: CustomText(
                        title: Strings.cancel,
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
    return DynamicAppBar(Strings.extend_validity, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  validateForm() {
    if (currentPlanExtendFormKey.currentState!.validate()) {
      if(currentPlanExtendController.remarksController.text.isNotEmpty) {
        currentPlanExtendController.extendCurrentPlanValidity();
      }
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }


  Future<void> selectDate(
      BuildContext context,
      String identity,
      DateTime firstDate,
      DateTime lastDate,
      ) async {
      DateTime? selectedDate;
      if (identity == Constant.extend_start_Date) {
        if (currentPlanExtendController.selectedExtendFromDate != null) {
          selectedDate = currentPlanExtendController.selectedExtendFromDate;
        } else {
          selectedDate = DateTime.now();
        }
      }

      if (identity == Constant.extend_end_Date) {
        if (currentPlanExtendController.selectedExtendToDate != null) {
          selectedDate = currentPlanExtendController.selectedExtendToDate;
        } else {
          selectedDate = DateTime.now();
        }
      }

    final DateTime? picked = await showDatePicker(
      context: context,
      // initialDate: selectedDate!,
      initialDate: DateTime.now(),
      firstDate: DateTime.now().subtract(const Duration(days: 0)),
      // firstDate: firstDate,
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
      if (identity == Constant.extend_start_Date) {
        currentPlanExtendController.selectedExtendFromDate = picked;
        currentPlanExtendController.update();
        _selectDateTime(identity);
      }
      if (identity == Constant.extend_end_Date) {
        currentPlanExtendController.selectedExtendToDate = picked;
        currentPlanExtendController.update();
        _selectDateTime(identity);
      }
    }
  }


  Future<void> _selectDateTime(String identity) async {
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
      if (identity == Constant.extend_start_Date) {
        DateTime dt = DateTime(
          currentPlanExtendController.selectedExtendFromDate!.year,
          currentPlanExtendController.selectedExtendFromDate!.month,
          currentPlanExtendController.selectedExtendFromDate!.day,
          picked.hour,
          picked.minute,
        );
        currentPlanExtendController.downTimeStartDateController.text =
            currentPlanExtendController.dateFormat.format(dt);
        currentPlanExtendController.startDateTime =
            currentPlanExtendController.apiDateTimeFormat.format(dt.toLocal());
        log("selectedDownTimeStartDate=>${currentPlanExtendController.startDateTime}");
      }

      if (identity == Constant.extend_end_Date){
        DateTime dt = DateTime(
          currentPlanExtendController.selectedExtendToDate!.year,
          currentPlanExtendController.selectedExtendToDate!.month,
          currentPlanExtendController.selectedExtendToDate!.day,
          picked.hour,
          picked.minute,
        );
        currentPlanExtendController.downTimeEndDateController.text =
            currentPlanExtendController.dateFormat.format(dt);
        currentPlanExtendController.endDateTime =
            currentPlanExtendController.apiDateTimeFormat.format(dt.toLocal());

        log("selectedDownTimeEndDate=>${currentPlanExtendController.endDateTime}");
      }
      currentPlanExtendController.update();
    }

  }



  // Future<void> selectDate(
  //   BuildContext context,
  //   String identity,
  //   DateTime firstDate,
  //   DateTime lastDate,
  // ) async {
  //   DateTime? selectedDate;
  //   if (identity == Constant.extend_start_Date) {
  //     if (currentPlanExtendController.selectedExtendFromDate != null) {
  //       selectedDate = currentPlanExtendController.selectedExtendFromDate;
  //     } else {
  //       selectedDate = DateTime.now();
  //     }
  //   }
  //
  //
  //   if (identity == Constant.extend_end_Date) {
  //     if (currentPlanExtendController.selectedExtendToDate != null) {
  //       selectedDate = currentPlanExtendController.selectedExtendToDate;
  //     } else {
  //       selectedDate = DateTime.now();
  //     }
  //   }
  //
  //   final DateTime? picked = await showDatePicker(
  //     context: context,
  //     initialDate: DateTime.now(),
  //     firstDate: DateTime.now().subtract(const Duration(days: 0)),
  //     lastDate: lastDate,
  //     initialEntryMode: DatePickerEntryMode.calendarOnly,
  //     builder: (BuildContext? context, Widget? child) {
  //       return Theme(
  //         data: ThemeData.light().copyWith(
  //           primaryColor: AppTheme.colorPrimary,
  //           colorScheme: ColorScheme.light(primary: AppTheme.colorPrimary),
  //           buttonTheme:
  //               const ButtonThemeData(textTheme: ButtonTextTheme.primary),
  //         ),
  //         child: child!,
  //       );
  //     },
  //   );
  //
  //   if (picked != null && picked != selectedDate) {
  //     if (identity == Constant.extend_start_Date) {
  //       currentPlanExtendController.selectedExtendFromDate = picked;
  //       currentPlanExtendController.downTimeStartDateController.text =
  //           currentPlanExtendController.dateFormat.format(picked);
  //       currentPlanExtendController.selectedDownTimeStartDate =
  //           currentPlanExtendController.apiDateTimeFormat.format(picked);
  //
  //
  //       log("selectedDownTimeStartDate=>${currentPlanExtendController.selectedDownTimeStartDate}");
  //     }
  //     if (identity == Constant.extend_end_Date) {
  //       currentPlanExtendController.selectedExtendToDate = picked;
  //       currentPlanExtendController.downTimeEndDateController.text =
  //           currentPlanExtendController.dateFormat.format(picked);
  //       currentPlanExtendController.selectedDownTimeEndDate =
  //           currentPlanExtendController.apiDateTimeFormat.format(picked);
  //       log("selectedDownTimeEndDate=>${currentPlanExtendController.selectedDownTimeEndDate}");
  //     }
  //     currentPlanExtendController.update();
  //   }
  // }

  totalDownTimeDays() {
    if(currentPlanExtendController.selectedExtendFromDate != null && currentPlanExtendController.selectedExtendToDate != null) {
      DateTime todayDate = DateTime.parse(
          currentPlanExtendController.selectedExtendFromDate.toString());
      DateTime endDay = DateTime.parse(
          currentPlanExtendController.selectedExtendToDate.toString());
      var remainingDays = todayDate.difference(endDay).inDays;
      return (remainingDays-1).toString();
    }
  }
}
