import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/task_management/task_mgmt/task_etr/task_etr_controller.dart';
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

class TaskETRScreen extends StatefulWidget {
  @override
  _TaskETRScreenState createState() => _TaskETRScreenState();
}

class _TaskETRScreenState extends State<TaskETRScreen>
    with WidgetsBindingObserver {
  final taskEtrController = Get.put(TaskETRController());
  final ticketETRFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

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
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<TaskETRController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(controller),
          ),
          ProgressBar(isLoader: taskEtrController.isLoading),
        ]);
      }), /**/
    );
  }

  _body(TaskETRController controller) {
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
                    key: ticketETRFormKey,
                    autovalidateMode: autoValidateMode,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisAlignment: MainAxisAlignment.start,
                      children: [
                        /*__________________ message mode status ____________________*/

                        const SizedBox(height: Constant.SCREEN_PADDING),
                        InputTitleRequire(
                            title: Strings.messageMode, require: true),
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
                                Strings.select_message_mode,
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
                            value: controller.selectMessageMode,
                            items: controller.messageModeList
                                ?.map((DropdownDetail value) {
                              return DropdownMenuItem<DropdownDetail>(
                                value: value,
                                child: Text(value.text!),
                              );
                            }).toList(),
                            onChanged: (value) {
                              controller.selectMessageMode =
                                  value as DropdownDetail?;
                              if (controller.selectMessageMode!.text!
                                  .endsWith(Strings.dynamic)) {
                                controller.showRemark = true;
                              } else {
                                controller.showRemark = false;
                              }
                              controller.update();
                            },
                            validator: (value) {
                              if (value == null ||
                                  controller.selectMessageMode == null) {
                                return Strings.select_message_mode;
                              }
                              return null;
                            },
                          ),
                        ),

                        /*__________________ Date  ____________________*/

                        const SizedBox(height: Constant.SCREEN_PADDING),
                        InputTitleRequire(title: Strings.date, require: true),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        CoustomTextField(
                            labelText: Strings.ddMMYYYY_format,
                            hintColor: AppTheme.colorIconGrey,
                            textEditingController:
                                taskEtrController.dateController,
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
                              if (value!.isEmpty) {
                                return Strings.please_enter_date;
                              } else {
                                return null;
                              }
                            },
                            onTextFiledOnTap: () {
                              selectDate(
                                  context,
                                  Strings.dob_date,
                                  DateTime.now(),
                                  DateTime(
                                      DateTime.now().year,
                                      DateTime.now().month + 1,
                                      DateTime.now().year));
                            },
                            readOnly: true),

                        /*__________________ Time   ____________________*/

                        const SizedBox(height: Constant.SCREEN_PADDING),
                        InputTitleRequire(
                            title: Strings.api_time, require: true),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        CoustomTextField(
                            labelText: Strings.time_format,
                            hintColor: AppTheme.colorIconGrey,
                            textEditingController:
                                taskEtrController.timeController,
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
                              if (value!.isEmpty) {
                                return Strings.please_enter_time;
                              } else {
                                return null;
                              }
                            },
                            onTextFiledOnTap: () {
                              selectTime();
                            },
                            readOnly: true),

                        /*__________________ Remarks   ____________________*/

                        controller.showRemark
                            ? Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                mainAxisAlignment: MainAxisAlignment.start,
                                children: [
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
                                          taskEtrController.remarkController,
                                      maxLines: 3,
                                      maxLength: 250,
                                      style: const TextStyle(
                                          fontSize: AppTheme.medium),
                                      decoration: InputDecoration(
                                        hintText: Strings.remarks,
                                        alignLabelWithHint: true,
                                        filled: true,
                                        hoverColor: Colors.white,
                                        fillColor: AppTheme.colorWhite,
                                        contentPadding: const EdgeInsets.all(
                                            Constant.TEXT_FIELD_CONTENT_PADDING *
                                                1.5),
                                        focusColor: Colors.transparent,
                                        focusedBorder: OutlineInputBorder(
                                          borderRadius: BorderRadius.circular(
                                              Constant.BTN_ROUNDED_CORNER),
                                          borderSide: BorderSide(
                                              color: AppTheme.colorPrimary,
                                              width: 1.0),
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
                                                Constant
                                                    .TEXT_FIELD_CONTENT_PADDING)),
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
                              )
                            : const SizedBox.shrink(),

                        /*__________________ Notification Type  ____________________*/

                        const SizedBox(height: Constant.SCREEN_PADDING),
                        InputTitleRequire(
                            title: Strings.notification_type, require: true),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            checkSmsNotificationWidget(),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            checkEmailNotificationNotificationWidget(),
                          ],
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ),
            Container(
              padding: const EdgeInsets.all(Constant.SMALL_PADDING),
              margin: const EdgeInsets.all(Constant.SMALL_PADDING),
              child: Row(
                children: [
                  Expanded(
                    child: SimpleButton(
                      onTap: () {
                        validateForm();
                      },
                      radius: Constant.BTN_HEIGHT_M,
                      height: Constant.BTN_HEIGHT_M + 5,
                      bgColors: AppTheme.colorPrimary,
                      child: CustomText(
                        title: Strings.submit,
                        fontSize: AppTheme.medium,
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                  ),
                  const SizedBox(
                    width: Constant.LARGE_PADDING,
                  ),
                  Expanded(
                    child: SimpleButton(
                      onTap: () {
                        taskEtrController.clearTaskETRData();
                      },
                      radius: Constant.BTN_HEIGHT_M,
                      height: Constant.BTN_HEIGHT_M + 5,
                      bgColors: AppTheme.colorBlack,
                      borderColors: AppTheme.colorBlack,
                      child: CustomText(
                        title: Strings.clear,
                        fontSize: AppTheme.medium,
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                  ),
                ],
              ),
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
    return DynamicAppBar(Strings.task_ETR, '', AppTheme.colorPrimary, false,
        _backScreen, [], AppBar().preferredSize.height);
  }

  validateForm() {
    if (ticketETRFormKey.currentState!.validate()) {
      if (taskEtrController.smsNotification == true ||
          taskEtrController.eMailNotification == true) {
        taskEtrController.sendTaskETRCustomerCall();
      } else {
        Utils.showSnackbar(Strings.INFO, Strings.select_notification_type_msg,
            AppTheme.colorWhite, AppTheme.colorBlueRView);
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

    if (identity == Strings.dob_date) {
      if (taskEtrController.selectEtrDate != null) {
        selectedDate = taskEtrController.selectEtrDate;
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
      if (identity == Strings.dob_date) {
        taskEtrController.selectEtrDate = picked;
        taskEtrController.dateController.text =
            taskEtrController.apiDateFormat.format(picked);
      }
      taskEtrController.update();
    }
  }

  Future<void> selectTime() async {
    TimeOfDay? selectedDateTime = TimeOfDay.now();
    final TimeOfDay? picked = await showTimePicker(
      context: context,
      initialTime: selectedDateTime,
      initialEntryMode: TimePickerEntryMode.dial,
      builder: (BuildContext? context, Widget? child) {
        return MediaQuery(
          data: MediaQuery.of(context!).copyWith(alwaysUse24HourFormat: true),
          child: child!,
        );
      },
    );

    if (picked != null) {
      TimeOfDay dt = TimeOfDay(hour: picked.hour, minute: picked.minute);

      taskEtrController.selectErtTime = picked;
      final localizations = MaterialLocalizations.of(context);
      String formattedTime =
          localizations.formatTimeOfDay(dt, alwaysUse24HourFormat: true);

      taskEtrController.timeController.text = formattedTime;

      // taskEtrController.timeController.text = dt.fromDateTime();
      taskEtrController.update();
    }
  }

  checkSmsNotificationWidget() {
    return Container(
      margin: const EdgeInsets.fromLTRB(Constant.SMALL_PADDING, 0, 0, 0),
      child: Row(
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            SizedBox(
              width: 20,
              child: Checkbox(
                value: taskEtrController.smsNotification,
                activeColor: AppTheme.colorPrimary,
                onChanged: (value) {
                  taskEtrController.smsNotification =
                      !taskEtrController.smsNotification;
                  taskEtrController.update();
                },
              ),
            ),
            const SizedBox(width: Constant.SMALL_PADDING),
            InkWell(
                child: Center(
                  child: CustomText(
                    title: Strings.sms,
                    textAlign: TextAlign.start,
                    colors: AppTheme.colorBlack,
                    fontSize: AppTheme.medium,
                    fontWeight: FontWeight.w400,
                  ),
                ),
                onTap: () {
                  taskEtrController.smsNotification =
                      !taskEtrController.smsNotification;
                  taskEtrController.update();
                }),
          ]),
    );
  }

  checkEmailNotificationNotificationWidget() {
    return Container(
      margin: const EdgeInsets.fromLTRB(Constant.SMALL_PADDING, 0, 0, 0),
      child: Row(
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            SizedBox(
              width: 20,
              child: Checkbox(
                value: taskEtrController.eMailNotification,
                activeColor: AppTheme.colorPrimary,
                onChanged: (value) {
                  taskEtrController.eMailNotification =
                      !taskEtrController.eMailNotification;
                  taskEtrController.update();
                },
              ),
            ),
            const SizedBox(width: Constant.SMALL_PADDING),
            InkWell(
                child: Center(
                  child: CustomText(
                    title: Strings.email,
                    textAlign: TextAlign.start,
                    colors: AppTheme.colorBlack,
                    fontSize: AppTheme.medium,
                    fontWeight: FontWeight.w400,
                  ),
                ),
                onTap: () {
                  taskEtrController.eMailNotification =
                      !taskEtrController.eMailNotification;
                  taskEtrController.update();
                }),
          ]),
    );
  }
}
