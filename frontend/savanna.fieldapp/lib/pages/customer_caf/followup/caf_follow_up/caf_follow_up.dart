import 'package:savbill/pages/customer_caf/followup/caf_follow_up/caf_follow_up_controller.dart';
import 'package:savbill/pages/customer_caf/followup/caf_follow_up/caf_follow_up_item_list.dart';
import 'package:savbill/pages/customer_caf/followup/caf_follow_up/model/customer_caf_follow_up_res.dart';
import 'package:savbill/pages/customer_caf/followup/close_remark_follow_up_dialog.dart';
import 'package:savbill/pages/customer_caf/followup/remark_follow_up/remark_follow_up.dart';
import 'package:savbill/pages/customer_caf/reSchedule_followup/re_schedule_followup_screen.dart';
import 'package:savbill/pages/customer_caf/schedule_followup/scheduleFollowUpScreen.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class CafFollowUp extends StatefulWidget {
  @override
  _CafFollowUpState createState() => _CafFollowUpState();
}

class _CafFollowUpState extends State<CafFollowUp>
    implements CloseFollowUpRemarkBtnAction {
  final cafFollowUpController = Get.put(CafFollowUpController());

  final shiftLocationFormKey = GlobalKey<FormState>();
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
    return GetBuilder<CafFollowUpController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: SafeArea(
            child: _body(),
          ),
        ),
        ProgressBar(isLoader: cafFollowUpController.isLoading),
      ]);
    });
  }

  _body() {
    return Container(
        padding: const EdgeInsets.all(Constant.SMALL_PADDING),
        color: AppTheme.colorBG,
        width: MediaQuery.of(context).size.width,
        child: Column(
            mainAxisSize: MainAxisSize.max,
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              Container(
                height: Constant.MENU_PROFILE_SIZE,
                padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
                child: Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Expanded(
                      child: CustomText(
                          title: cafFollowUpController.customerDetail != null
                              ? "${cafFollowUpController.customerDetail?.title?.capitalizeFirst ??""} ${cafFollowUpController.customerDetail?.custname ??""} ${Strings.followup} List"
                              : "",
                          colors: AppTheme.colorBlack,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.small + 1,
                          fontWeight: FontWeight.w500),
                    ),
                    const SizedBox(
                      width: Constant.VERY_SMALL_PADDING,
                    ),
                    InkWell(
                      onTap: () {
                        openScheduleRemarkFollowUp(Strings.reschedule);
                      },
                      child: Container(
                        padding: const EdgeInsets.only(
                            top: Constant.SMALL_PADDING,
                            bottom: Constant.SMALL_PADDING,
                            left: Constant.SMALL_PADDING,
                            right: Constant.SMALL_PADDING),
                        // height: Constant.CARD_BOTTOM_BUTTON_H,
                        alignment: Alignment.center,
                        decoration: BoxDecoration(
                          color: AppTheme.colorPrimary,
                          borderRadius: const BorderRadius.all(
                              Radius.circular(Constant.ROUNDED_CORNER)),
                        ),
                        child: Row(
                          // crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            const Padding(
                              padding: EdgeInsets.only(
                                  left: Constant.VERY_SMALL_PADDING,
                                  right: Constant.VERY_SMALL_PADDING),
                              child: Icon(
                                size: Constant.ICON_SIZE_M,
                                Icons.calendar_month,
                                color: Colors.white,
                              ),
                            ),
                            CustomText(
                              title: "${Strings.schedule} ${Strings.followup}",
                              colors: AppTheme.colorWhite,
                              fontSize: AppTheme.small - 1,
                              textAlign: TextAlign.center,
                              fontWeight: FontWeight.normal,
                            )
                          ],
                        ),
                      ),
                    ),
                  ],
                ),
              ),
              Expanded(
                flex: 1,
                child: (cafFollowUpController.cafFollowUpDataList != null &&
                        cafFollowUpController.cafFollowUpDataList.isNotEmpty)
                    ? ListView.builder(
                        shrinkWrap: true,
                        scrollDirection: Axis.vertical,
                        itemCount:
                            cafFollowUpController.cafFollowUpDataList!.length,
                        itemBuilder: (context, index) {
                          if (index ==
                              cafFollowUpController
                                  .cafFollowUpDataList?.length) {
                            if (cafFollowUpController.isShowLoadMore) {
                              return Padding(
                                padding: const EdgeInsets.all(
                                    Constant.SMALL_PADDING),
                                child: Center(
                                  child: SizedBox(
                                    width: Constant.SCREEN_PADDING,
                                    height: Constant.SCREEN_PADDING,
                                    child: CircularProgressIndicator(
                                      strokeWidth: 2.5,
                                      valueColor: AlwaysStoppedAnimation<Color>(
                                          AppTheme.colorProgress),
                                      backgroundColor: AppTheme.colorProgressBg,
                                    ),
                                  ),
                                ),
                              );
                            } else {
                              return Container();
                            }
                          } else {
                            CafFollowUpDataList item = cafFollowUpController
                                .cafFollowUpDataList[index];
                            return CafFollowUpItemList(
                              item: item,
                              index: index,
                              controller: cafFollowUpController,
                              customerDetail:
                                  cafFollowUpController.customerDetail,
                              onTapRescheduleFollowUp: () {
                                openRescheduleRemarkFollowUp(
                                    item, Strings.reschedule);
                              },
                              onTapRemarkFollowUp: () {
                                openRemarkFollowUp(item.id);
                              },
                              onTapCloseFollowUp: () {
                                showDialog(
                                    context: context,
                                    barrierDismissible: true,
                                    builder: (BuildContext context) {
                                      return CloseRemarkFollowUpDialog(
                                        pageName: 'CafCloseFollowUp',
                                        closeFollowUpRemarkBtnAction: this,
                                        itemList: item,
                                      );
                                    });
                              },
                              onTapCallFollowUp: () {
                                Utils.showSnackbar(
                                    "Call configure",
                                    "Sorry! Please configure call client first..",
                                    AppTheme.colorWhite,
                                    AppTheme.colorBlueRView);
                              },
                            );
                          }
                        })
                    : noDataFound(),
              )
            ]));
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.followup, '', AppTheme.colorPrimary, false,
        _backScreen, [], AppBar().preferredSize.height);
  }

  @override
  void closeFollowUpRemarkBtnAction(
      {String? identifier,
      TextEditingController? remarkController,
      int? followUpId}) {
    Get.back();
    if (identifier!.equalsIgnoreCase("CafCloseFollowUp")) {
      cafFollowUpController.getCloseFollowUp(
        followUpId: followUpId!,
        remark: remarkController!.text,
      );
    }
  }

  openRemarkFollowUp(int? followUpId) async {
    var result = await Get.to(CafRemarkFollowUp(), arguments: {
      Constant.CUSTOMER_DETAIL: cafFollowUpController.customerDetail,
      Constant.FOLLOW_UP_ID: followUpId,
    });
    if (result != null) {
      cafFollowUpController.getCustomerCafFollowUPData();
      cafFollowUpController.update();
    }
  }


  openScheduleRemarkFollowUp(String? scheduleType) async {
    var result = await Get.to(ScheduleFollowUpScreen(), arguments: {
      Constant.CUSTOMER_DETAIL: cafFollowUpController.customerDetail,
      Constant.SCHEDULE_TYPE: scheduleType
    });

    if (result != null && result == true) {
      cafFollowUpController.getCustomerCafFollowUPData();
      cafFollowUpController.update();

    }
  }

  openRescheduleRemarkFollowUp(
      CafFollowUpDataList? followUpData, String? scheduleType) async {
    var result = await Get.to(ReScheduleFollowUpScreen(), arguments: {
      Constant.CUSTOMER_DETAIL: cafFollowUpController.customerDetail,
      Constant.FOLLOW_UP_DATA: followUpData,
      Constant.SCHEDULE_TYPE: scheduleType
    });

    if (result != null && result == true) {
      cafFollowUpController.getCustomerCafFollowUPData();
      cafFollowUpController.update();
    }
  }
}
