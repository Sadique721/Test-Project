import 'package:savbill/pages/customer_caf/caf_notes/caf_staff_details/caf_staff_details_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class CafStaffDetails extends StatefulWidget {
  @override
  State<CafStaffDetails> createState() => _CafStaffDetailsState();
}

class _CafStaffDetailsState extends State<CafStaffDetails> {
  final cafStaffDetailsController = Get.put(CafStaffDetailsController());

  @override
  Widget build(BuildContext context) {
    return GetBuilder<CafStaffDetailsController>(builder: (controller) {
      return Stack(children: [
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body:  SafeArea(
            child: SafeArea(
              child: _body(),
            ),
          ),
        ),
        ProgressBar(isLoader: cafStaffDetailsController.isLoading),
      ]);
    });
  }

  _body() {
    return Container(
      width: MediaQuery.of(context).size.width,
      height: MediaQuery.of(context).size.height,
      margin: const EdgeInsets.only(
        top: Constant.SMALL_PADDING,
      ),
      color: AppTheme.colorBG,
      child: cafStaffDetailsController.customerDetail != null
          ? SingleChildScrollView(
        child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(
                height: Constant.VERY_SMALL_PADDING,
              ),
              basicDetailView(),
              const SizedBox(
                height: Constant.VERY_SMALL_PADDING,
              ),
              teamView(),
            ]),
      )
          : noDataFound(),
    );
  }

  basicDetailView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.basic_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: true,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.basic_details,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          shape: const Border(),
          onExpansionChanged: ((newState) {}),
          children: <Widget>[
            Container(
              width: Get.width,
              height: 1.5,
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(6),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.grey.withOpacity(0.4),
                      blurRadius: 1.5,
                      spreadRadius: 1.5,
                    ),
                  ]),
            ),
            Padding(
              padding: const EdgeInsets.only(
                  top: Constant.EXPANTABLE_ITEM_MARGIN,
                  left: Constant.EXPANTABLE_ITEM_MARGIN,
                  right: Constant.EXPANTABLE_ITEM_MARGIN,
                  bottom: 0),
              child: Container(
                alignment: Alignment.topLeft,
                padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      basicDetailItem(
                          Strings.name,
                          cafStaffDetailsController.staffDetail != null
                              ? "${cafStaffDetailsController.staffDetail?.firstname} ${cafStaffDetailsController.staffDetail?.lastname}"
                              : "-",
                          Strings.email,
                          cafStaffDetailsController.staffDetail != null
                              ? cafStaffDetailsController.staffDetail?.email
                              : "-"),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      basicDetailItem(
                          Strings.mobile,
                          cafStaffDetailsController.staffDetail != null
                              ? cafStaffDetailsController.staffDetail!.phone
                              : "-",
                          Strings.user_name,
                          cafStaffDetailsController.staffDetail != null
                              ? cafStaffDetailsController.staffDetail!.username
                              : "-"),
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      Row(
                        mainAxisSize: MainAxisSize.max,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: [
                          Expanded(
                            flex: 1,
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.start,
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                titleWidget(Strings.service_area),
                                const SizedBox(
                                    height: Constant.VERY_SMALL_PADDING - 1),
                                InkWell(
                                    child: valueWidget(Strings.click_here,
                                        AppTheme.colorBlueRView),
                                    onTap: () => showDialog(
                                        context: context,
                                        builder: (BuildContext context) {
                                          return AlertDialogHelper(
                                            title: Strings.service_area,
                                            message: cafStaffDetailsController
                                                .staffDetail
                                                ?.serviceAreasNameList
                                                ?.join("\n"),
                                            positiveBtnText: Strings.ok,
                                            positiveBtnClick: () {
                                              Get.back();
                                            },
                                            negativeBtnClick: () {},
                                          );
                                        }))
                              ],
                            ),
                          ),
                        ],
                      )
                    ]),
              ),
            )
          ],
        ),
      ),
    );
  }

  teamView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: true,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.team_list,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          shape: const Border(),
          onExpansionChanged: ((newState) {}),
          children: <Widget>[
            Container(
              width: Get.width,
              height: 1.5,
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(6),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.grey.withOpacity(0.4),
                      blurRadius: 1.5,
                      spreadRadius: 1.5,
                    ),
                  ]),
            ),
            Padding(
              padding: const EdgeInsets.only(
                  top: Constant.EXPANTABLE_ITEM_MARGIN,
                  left: Constant.EXPANTABLE_ITEM_MARGIN,
                  right: Constant.EXPANTABLE_ITEM_MARGIN,
                  bottom: 0),
              child: Container(
                alignment: Alignment.topLeft,
                padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: cafStaffDetailsController.staffDetail?.teamNameList
                      ?.map((item) {
                    return CustomText(
                      title: item,
                      colors: AppTheme.colorBlack,
                    );
                  }).toList() ??
                      [],
                ),
              ),
            )
          ],
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
              valueWidget(value1, AppTheme.title_dark),
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
              valueWidget(value2, AppTheme.title_dark),
            ],
          ),
        ),
      ],
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.staff_details, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  _backScreen() {
    Get.back();
  }

  titleWidget(String title) {
    return CustomText(
      title: title,
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w700,
      maxLines: 2,
    );
  }

  valueWidget(String? value, Color txtColors) {
    return CustomText(
      title: value!.isNotEmpty ? value : "",
      colors: txtColors,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.normal,
      maxLines: 2,
    );
  }
}
