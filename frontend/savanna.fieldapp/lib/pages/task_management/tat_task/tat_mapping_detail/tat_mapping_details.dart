import 'package:savbill/pages/dashboard/model/response/show_tat_details_res.dart';
import 'package:savbill/pages/task_management/tat_task/tat_mapping_detail/tat_mapping_details_controller.dart';
import 'package:savbill/pages/task_management/tat_task/tat_mapping_detail/tat_task_mapping_item.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:get/get.dart';


class TatTaskMappingDetails extends StatefulWidget {
  const TatTaskMappingDetails({super.key});

  @override
  State<TatTaskMappingDetails> createState() => _TatTaskMappingDetailsState();
}

class _TatTaskMappingDetailsState extends State<TatTaskMappingDetails> {
  final tatTaskMappingController = Get.put(TatTaskMappingController());

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
      child: GetBuilder<TatTaskMappingController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: tatTaskMappingController.isLoading),
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
                                                  title: (tatTaskMappingController
                                                      .tatTaskDetail!= null && tatTaskMappingController
                                                      .tatTaskDetail!
                                                      .name !=
                                                      null &&
                                                      tatTaskMappingController
                                                          .tatTaskDetail!
                                                          .name!
                                                          .isNotEmpty)
                                                      ? tatTaskMappingController
                                                      .tatTaskDetail!.name!
                                                      : "-",
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
                                              color: (tatTaskMappingController
                                                  .tatTaskDetail != null && tatTaskMappingController
                                                  .tatTaskDetail!
                                                  .status !=
                                                  null &&
                                                  tatTaskMappingController
                                                      .tatTaskDetail!
                                                      .status!
                                                      .isNotEmpty &&
                                                  tatTaskMappingController
                                                      .tatTaskDetail!
                                                      .status!
                                                      .equalsIgnoreCase(
                                                      Strings.active))
                                                  ? AppTheme.statusClosedGreen
                                                  : AppTheme.statusReject,
                                            ),
                                            child: CustomText(
                                                title: (tatTaskMappingController
                                                    .tatTaskDetail != null && tatTaskMappingController
                                                    .tatTaskDetail!
                                                    .status !=
                                                    null &&
                                                    tatTaskMappingController
                                                        .tatTaskDetail!
                                                        .status!
                                                        .isNotEmpty &&
                                                    tatTaskMappingController
                                                        .tatTaskDetail!
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
                                        Strings.sla_time_p1,
                                        (tatTaskMappingController
                                            .tatTaskDetail != null && tatTaskMappingController
                                            .tatTaskDetail!.slaTimep1 !=
                                            null)
                                            ? "${tatTaskMappingController.tatTaskDetail!.slaTimep1} ${tatTaskMappingController.tatTaskDetail!.sunitp1!}"
                                            : "-",
                                        Strings.sla_time_p2,
                                        (tatTaskMappingController
                                            .tatTaskDetail != null && tatTaskMappingController
                                            .tatTaskDetail!.slaTimep2 !=
                                            null)
                                            ? "${tatTaskMappingController.tatTaskDetail!.slaTimep2} ${tatTaskMappingController.tatTaskDetail!.sunitp2!}"
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
                                        Strings.sla_time_p3,
                                        (tatTaskMappingController
                                            .tatTaskDetail != null && tatTaskMappingController
                                            .tatTaskDetail!.slaTime3 !=
                                            null)
                                            ? "${tatTaskMappingController.tatTaskDetail!.slaTime3} ${tatTaskMappingController.tatTaskDetail!.sunitp3!}"
                                            : "-",
                                        Strings.response_time,
                                        (tatTaskMappingController
                                            .tatTaskDetail != null && tatTaskMappingController
                                            .tatTaskDetail!.rtime !=
                                            null)
                                            ? "${tatTaskMappingController.tatTaskDetail!.rtime} ${tatTaskMappingController.tatTaskDetail!.runit != null ? tatTaskMappingController.tatTaskDetail!.runit! : ""}"
                                            : "-",
                                      ),
                                    ),
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
                          (tatTaskMappingController.tatMatrixMappings != null &&
                              tatTaskMappingController
                                  .tatMatrixMappings!.isNotEmpty)
                              ? Padding(
                            padding: const EdgeInsets.symmetric(
                                horizontal: Constant.SMALL_PADDING),
                            child: ListView.builder(
                                shrinkWrap: true,
                                // scrollDirection: Axis.vertical,
                                physics:
                                const NeverScrollableScrollPhysics(),
                                itemCount: tatTaskMappingController
                                    .tatMatrixMappings!.length,
                                itemBuilder: (context, index) {
                                  TatMatrixMappings item =
                                  tatTaskMappingController
                                      .tatMatrixMappings![index];
                                  return TatTaskMappingItem(
                                      item: item,
                                      isShowDelete: false,
                                      onTapDelete: () {});
                                }),
                          )
                              : noDataFound(),
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
        "${tatTaskMappingController.tatTaskDetail != null ? tatTaskMappingController.tatTaskDetail!.name : "-"} ${Strings.tat_task_detail}",
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
