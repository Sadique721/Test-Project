
import 'dart:developer';

import 'package:savbill/pages/dashboard/model/response/show_tat_details_res.dart';
import 'package:savbill/pages/ticket_system/tat_ticket/tat_ticket_mapping_item.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:get/get.dart';
import '../../../../widgets/coustom_text.dart';
import '../task_detail/task_detail_controller.dart';

class TatNameTaskMapDetail extends StatefulWidget {
  @override
  _TatNameTaskDetailState createState() => _TatNameTaskDetailState();
}

class _TatNameTaskDetailState extends State<TatNameTaskMapDetail> {
  final tatMappingController = Get.put(TaskDetailController());

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
      child: GetBuilder<TaskDetailController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: tatMappingController.isLoading),
        ]);
      }),
    );
  }

  _body() {

    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: SingleChildScrollView(
        child: SizedBox(
          width: MediaQuery.of(context).size.width,
          child: Column(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const SizedBox(
                  height: Constant.SCREEN_PADDING,
                ),
                Stack(
                  children: <Widget>[
                    Container(
                      width: double.infinity,
                      margin: const EdgeInsets.fromLTRB(10, 20, 10, 20),
                      padding: const EdgeInsets.only(bottom: 12),
                      decoration: BoxDecoration(
                        border:
                        Border.all(color: AppTheme.title_dark, width: 1),
                        borderRadius: BorderRadius.circular(5),
                        shape: BoxShape.rectangle,
                      ),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          const SizedBox(
                            height: Constant.SCREEN_PADDING,
                          ),
                          Padding(
                            padding: const EdgeInsets.symmetric(
                                horizontal: Constant.SCREEN_PADDING),
                            child: Material(
                              color: AppTheme.colorWhite,
                              elevation: 0.5,
                              shape: RoundedRectangleBorder(
                                  borderRadius: BorderRadius.circular(
                                      Constant.BTN_ROUNDED_CORNER)),
                              child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    Padding(
                                      padding: const EdgeInsets.symmetric(
                                          horizontal: Constant.SMALL_PADDING),
                                      child: Row(
                                        mainAxisAlignment:
                                        MainAxisAlignment.spaceBetween,
                                        children: [
                                          Expanded(
                                              child: CustomText(
                                                  title: (tatMappingController
                                                      .showTATDetailsData != null && tatMappingController
                                                      .showTATDetailsData!
                                                      .name!.isNotEmpty) ?tatMappingController
                                                      .showTATDetailsData!
                                                      .name :
                                                  "-",
                                                  colors: AppTheme.title_dark,
                                                  textAlign: TextAlign.start,
                                                  fontSize: AppTheme.small,
                                                  maxLines: 2,
                                                  height: 1,
                                                  fontWeight: FontWeight.w500)),
                                          Container(
                                            padding: const EdgeInsets.symmetric(
                                                horizontal:
                                                Constant.SMALL_PADDING,
                                                vertical: Constant
                                                    .VERY_SMALL_PADDING),
                                            decoration: BoxDecoration(
                                              borderRadius:
                                              BorderRadius.circular(
                                                  Constant.LARGE_PADDING),
                                              color: (tatMappingController
                                                  .showTATDetailsData != null && tatMappingController
                                                  .showTATDetailsData!
                                                  .status !=
                                                  null &&
                                                  tatMappingController
                                                      .showTATDetailsData!
                                                      .status!
                                                      .isNotEmpty &&
                                                  tatMappingController
                                                      .showTATDetailsData!
                                                      .status!
                                                      .equalsIgnoreCase(
                                                      Strings.active))
                                                  ? AppTheme.statusClosedGreen
                                                  : AppTheme.statusReject,
                                            ),
                                            child: CustomText(
                                                title: (tatMappingController
                                                    .showTATDetailsData != null && tatMappingController
                                                    .showTATDetailsData!
                                                    .status !=
                                                    null &&
                                                    tatMappingController
                                                        .showTATDetailsData!
                                                        .status!
                                                        .isNotEmpty &&
                                                    tatMappingController
                                                        .showTATDetailsData!
                                                        .status!
                                                        .equalsIgnoreCase(
                                                        Strings.active))
                                                    ? Strings.active
                                                    : Strings.in_active,
                                                colors: AppTheme.colorWhite,
                                                textAlign: TextAlign.start,
                                                fontSize: AppTheme.small,
                                                maxLines: 2,
                                                height: 1,
                                                fontWeight: FontWeight.w500),
                                          )
                                        ],
                                      ),
                                    ),
                                    Divider(
                                      color: AppTheme.title_dark,
                                      thickness: 0.5,
                                      height: Constant.MEDIUM_PADDING,
                                    ),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    Padding(
                                      padding: const EdgeInsets.symmetric(
                                          horizontal: Constant.SMALL_PADDING),
                                      child: basicDetailItem(
                                        Strings.response_time,
                                        (tatMappingController.showTATDetailsData != null && tatMappingController.showTATDetailsData!.tatMatrixMappings!=
                                            null)
                                            ? "${tatMappingController.showTATDetailsData!.tatMatrixMappings![0].mtime3}"
                                            : "-",
                                        Strings.sla_time_p1,
                                        (tatMappingController.showTATDetailsData != null && tatMappingController.showTATDetailsData!
                                            .slaTimep1 !=
                                            null)
                                            ? "${tatMappingController.showTATDetailsData!.slaTimep1} ${tatMappingController.showTATDetailsData!.sunitp1!}"
                                            : "-",
                                      ),
                                    ),
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    Padding(
                                      padding: const EdgeInsets.symmetric(
                                          horizontal: Constant.SMALL_PADDING),
                                      child: basicDetailItem(
                                        Strings.sla_time_p2,
                                        (tatMappingController.showTATDetailsData != null && tatMappingController.showTATDetailsData!
                                            .slaTimep2 !=
                                            null)
                                            ? "${tatMappingController.showTATDetailsData!.slaTimep2} ${tatMappingController.showTATDetailsData!.sunitp2!}"
                                            : "-",
                                        Strings.sla_time_p3,
                                        (tatMappingController.showTATDetailsData != null && tatMappingController.showTATDetailsData!
                                            .slaTimep2 !=
                                            null)
                                            ? "${tatMappingController.showTATDetailsData!.slaTime3} ${tatMappingController.showTATDetailsData!.sunitp3!}"
                                            : "-",
                                      ),
                                    ),
                                    // Padding(
                                    //   padding: const EdgeInsets.symmetric(
                                    //       horizontal: Constant.SMALL_PADDING),
                                    //   child: basicDetailItem(
                                    //     Strings.sla_time_p3,
                                    //     (tatMappingController.showTATDetailsData != null && tatMappingController.showTATDetailsData!
                                    //         .slaTime3 !=
                                    //         null)
                                    //         ? "${tatMappingController.showTATDetailsData!.slaTime3} ${tatMappingController.showTATDetailsData!.sunitp3!}"
                                    //         : "-",
                                    //     Strings.response_time,
                                    //     (tatMappingController
                                    //         .showTATDetailsData != null && tatMappingController
                                    //         .showTATDetailsData!.rtime !=
                                    //         null)
                                    //         ? "${tatMappingController.showTATDetailsData!.rtime} ${tatMappingController.showTATDetailsData!.runit!}"
                                    //         : "-",
                                    //   ),
                                    // ),
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                  ]),
                            ),
                          ),
                        ],
                      ),
                    ),
                    Positioned(
                      left: 50,
                      top: 10,
                      child: Container(
                        padding: const EdgeInsets.only(
                            bottom: 3, left: 3, right: 3, top: 3),
                        color: Colors.white,
                        child: CustomText(
                          title: Strings.basic_details,
                          colors: AppTheme.title_dark,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.medium,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(
                  height: Constant.SCREEN_PADDING,
                ),
                Stack(
                  children: <Widget>[
                    Container(
                      width: double.infinity,
                      margin: const EdgeInsets.fromLTRB(10, 20, 10, 20),
                      // padding: const EdgeInsets.only(bottom: 10, left: 10, right: 10,top: 10),
                      decoration: BoxDecoration(
                        border:
                        Border.all(color: AppTheme.title_dark, width: 1),
                        borderRadius: BorderRadius.circular(5),
                        shape: BoxShape.rectangle,
                      ),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          const SizedBox(
                            height: Constant.SCREEN_PADDING,
                          ),
                          (tatMappingController.showTATDetailsData!.tatMatrixMappings != null &&
                              tatMappingController.showTATDetailsData!.tatMatrixMappings!.isNotEmpty)
                              ? Padding(
                            padding: const EdgeInsets.symmetric(
                                horizontal: Constant.SMALL_PADDING),
                            child: ListView.builder(
                                shrinkWrap: true,
                                // scrollDirection: Axis.vertical,
                                physics:
                                const NeverScrollableScrollPhysics(),
                                itemCount: tatMappingController.
                                showTATDetailsData?.tatMatrixMappings!.length,
                                itemBuilder: (context, index) {
                                  TatMatrixMappings item =
                                  tatMappingController.
                                  showTATDetailsData!.tatMatrixMappings![index];
                                  return TatTicketMappingItem(
                                      item: item,
                                      isShowDelete: false,
                                      onTapDelete: () {});
                                }),
                          )
                              : SizedBox(child: noDataFound(),height: 100,)
                        ],
                      ),
                    ),
                    Positioned(
                      left: 50,
                      top: 10,
                      child: Container(
                        padding: const EdgeInsets.only(
                            bottom: 3, left: 3, right: 3, top: 3),
                        color: Colors.white,
                        child: CustomText(
                          title: Strings.tat_mapping_detail,
                          colors: AppTheme.title_dark,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.medium,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                    ),
                  ],
                ),
              ]),
        ),
      ),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        Strings.tat_detail,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  buttonView(String btnName, Color bgColor, Color txtColor) {
    return InkWell(
      // onTap: onTap,
      child: Material(
        elevation: 1.5,
        color: bgColor,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
        child: Container(
          height: Constant.BTN_HEIGHT_M - 10,
          width: Constant.BTN_HEIGHT_M - 10,
          alignment: Alignment.center,
          padding: const EdgeInsets.all(Constant.SMALL_PADDING - 1),
          child: SvgPicture.asset(
            btnName,
            height: Constant.ICON_SIZE,
            width: Constant.ICON_SIZE,
            color: txtColor,
            fit: BoxFit.fill,
          ),
        ),
      ),
    );
  }

  basicDetailItem(
      String title1, String? value1, String title2, String? value2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.center,
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

  valueWidget(String? value) {
    return CustomText(
      title: value!.isNotEmpty ? value : "-",
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.normal,
      maxLines: 2,
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
}