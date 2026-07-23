import 'dart:developer';
import 'package:savbill/pages/workflow/cust_notification_detail_controller.dart';
import 'package:savbill/pages/workflow/model/cust_notification_detail_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:flutter_switch/flutter_switch.dart';
import 'package:get/get.dart';
import 'package:intl/intl.dart';

class CustomerNotificationDetail extends StatefulWidget {
  @override
  _CustomerNotificationDetailState createState() => _CustomerNotificationDetailState();
}

class _CustomerNotificationDetailState extends State<CustomerNotificationDetail> {
  final notificationController = Get.put(NotificationDetailController());
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
    return GetBuilder<NotificationDetailController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: SafeArea(
            child: _body(),
          ),
        ),
        ProgressBar(isLoader: notificationController.isLoading),
      ]);
    });
  }

  _body() {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: SingleChildScrollView(
        child: Container(
          padding: const EdgeInsets.all(Constant.SMALL_PADDING),
          child: Container(
            color: AppTheme.colorBG,
            width: MediaQuery.of(context).size.width,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const SizedBox(
                  height: Constant.SMALL_PADDING,
                ),
                Align(
                  alignment: Alignment.centerLeft,
                  child: CustomText(
                      title:
                      "${notificationController.customerDetail!.title} ${notificationController.customerDetail!.username} ${Strings.notification_details}",
                      colors: AppTheme.colorBlack,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.medium,
                      fontWeight: FontWeight.w500),
                ),
                const SizedBox(
                  height: Constant.MEDIUM_PADDING,
                ),
                Align(
                  alignment: Alignment.centerLeft,
                  child: Row(
                    children: [
                      CustomText(
                          title: "${Strings.status} :",
                          colors: AppTheme.colorBlack,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.medium,
                          fontWeight: FontWeight.w600),
                      const SizedBox(
                        width: Constant.MEDIUM_PADDING,
                      ),
                      FlutterSwitch(
                        showOnOff: true,
                        value: notificationController.isCheckNotificationStatus!,
                        width: 100.0,
                        // height: 35.0,
                        valueFontSize: AppTheme.medium,
                        // toggleSize: 40.0,
                        // borderRadius: 30.0,
                        // activeIcon: CustomText(
                        //   title: Strings.active,
                        //   textAlign: TextAlign.center,
                        //   fontSize: AppTheme.medium,
                        //   colors: AppTheme.colorBlack,
                        //   fontWeight: FontWeight.w600,
                        //
                        // ),
                        activeText: Strings.in_active,
                        // inactiveIcon: CustomText(
                        //   title: Strings.in_active,
                        //   textAlign: TextAlign.center,
                        //   fontSize: AppTheme.medium,
                        //   colors: AppTheme.colorBlack,
                        //   fontWeight: FontWeight.w600,
                        // ),
                        inactiveText: Strings.active,
                        inactiveTextColor: AppTheme.colorWhite,
                        inactiveColor: AppTheme.colorGrey,
                        activeColor: AppTheme.colorPrimaryTheme,
                        activeTextFontWeight: FontWeight.w600,
                        inactiveTextFontWeight: FontWeight.w500,
                        onToggle: (val) {
                          log("isCheckValue$val");
                          if(val == true){
                            notificationController.changeNotificationStatus(val);
                          }else{
                            notificationController.changeNotificationStatus(val);
                          }
                          notificationController.isCheckNotificationStatus = val;
                          notificationController.update();
                        },
                      )
                    ],
                  ),
                ),
                const SizedBox(
                  height: Constant.MEDIUM_PADDING,
                ),
                Container(
                  padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                  child: (notificationController.notificationContentList != null &&
                      notificationController.notificationContentList!.isNotEmpty)
                      ? ListView.builder(
                      scrollDirection: Axis.vertical,
                      shrinkWrap: true,
                      controller: notificationController.controller,
                      itemCount:
                      notificationController.notificationContentList!.length +
                          1,
                      itemBuilder: (context, index) {
                        if (index ==
                            notificationController
                                .notificationContentList?.length) {
                          if (notificationController.isShowLoadMore) {
                            return Padding(
                              padding: const EdgeInsets.all(
                                  Constant.SMALL_PADDING),
                              child: Center(
                                child: SizedBox(
                                  width: Constant.SCREEN_PADDING,
                                  height: Constant.SCREEN_PADDING,
                                  child: CircularProgressIndicator(
                                    strokeWidth: 2.5,
                                    valueColor:
                                    AlwaysStoppedAnimation<Color>(
                                        AppTheme.colorProgress),
                                    backgroundColor:
                                    AppTheme.colorProgressBg,
                                  ),
                                ),
                              ),
                            );
                          } else {
                            return Container();
                          }
                        } else {
                          NotificationContent item = notificationController
                              .notificationContentList![index];
                          return notificationDetailsItem(item: item);
                        }
                      })
                      : noDataFound(),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.notification_details, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  notificationDetailsItem({required NotificationContent item}) {
    String? startDt;
    if (item.messageDate != null && item.messageDate!.isNotEmpty) {
      DateTime date =
      DateFormat(Constant.DATE_TIME_FORMAT_API).parse(item.messageDate!);
      startDt = DateFormat(Constant.DATE_FORMAT).format(date);
    }
    return Container(
      margin: const EdgeInsets.only(
        bottom: Constant.MEDIUM_PADDING,
      ),
      child: Material(
        color: AppTheme.colorWhite,
        elevation: 0.5,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
        child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
          const SizedBox(
            height: Constant.MEDIUM_PADDING,
          ),
          Padding(
            padding:
            const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
              Strings.event,
              (item.eventName != null && item.eventName!.isNotEmpty)
                  ? item.eventName
                  : "-",
              Strings.dunning_date,
              startDt ?? "",
            ),
          ),
          const SizedBox(
            height: Constant.MEDIUM_PADDING,
          ),
        ]),
      ),
    );
  }

  basicDetailItem(
      String title1, String? value1, String title2, String? value2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Expanded(
          flex: 1,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title1),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value1),
            ],
          ),
        ),
        Expanded(
          flex: 1,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title2),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value2),
            ],
          ),
        ),
      ],
    );
  }

  titleWidget(String title) {
    return CustomText(
      title: title,
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.verySmall,
      fontWeight: FontWeight.w400,
      maxLines: 2,
    );
  }

  valueWidget(String? value) {
    return CustomText(
      title: value!.isNotEmpty ? value : "",
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.normal,
      maxLines: 2,
    );
  }
}
