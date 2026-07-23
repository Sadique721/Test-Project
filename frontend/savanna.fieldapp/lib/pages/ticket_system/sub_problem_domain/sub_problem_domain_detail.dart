import 'package:savbill/pages/ticket_system/model/response/sub_problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/sub_problem_domain/sub_problem_domain_detail_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class SubProblemDomainDetailScreen extends StatefulWidget {
  @override
  _SubProblemDomainDetailState createState() => _SubProblemDomainDetailState();
}

class _SubProblemDomainDetailState extends State<SubProblemDomainDetailScreen> {
  final subProblemDomainDetailController =
      Get.put(SubProblemDomainDetailController());

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<SubProblemDomainDetailController>(builder: (controller) {
      return Stack(children: [
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: subProblemDomainDetailController.isLoading),
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
        child: subProblemDomainDetailController.subProblemDomainDetail != null
            ? SingleChildScrollView(
                physics: const ScrollPhysics(),
                child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      basicDetailView(),
                      (subProblemDomainDetailController
                          .subCategoryGroupReason !=
                          null &&
                          subProblemDomainDetailController
                              .subCategoryGroupReason!.isNotEmpty)
                          ? const SizedBox(height: Constant.VERY_SMALL_PADDING)
                          : Container(),
                      (subProblemDomainDetailController
                          .subCategoryGroupReason !=
                          null &&
                          subProblemDomainDetailController
                              .subCategoryGroupReason!.isNotEmpty)
                          ? subCategoryReasonView()
                          : Container(),

                      (subProblemDomainDetailController
                          .ticketSubCategoryTatMappingList !=
                          null &&
                          subProblemDomainDetailController
                              .ticketSubCategoryTatMappingList!.isNotEmpty)
                          ?  const SizedBox(height: Constant.VERY_SMALL_PADDING):Container(),
                      (subProblemDomainDetailController
                          .ticketSubCategoryTatMappingList !=
                          null &&
                          subProblemDomainDetailController
                              .ticketSubCategoryTatMappingList!.isNotEmpty)
                          ? ticketSubCategoryTatMappingView()
                          : Container(),

                      const SizedBox(
                        height: Constant.MEDIUM_PADDING,
                      ),
                    ]),
              )
            : noDataFound());
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
                          subProblemDomainDetailController
                                  .subProblemDomainDetail!.subCategoryName ??
                              "-",
                          Strings.service,
                          (subProblemDomainDetailController
                                          .subProblemDomainDetail!
                                          .parentCategory !=
                                      null &&
                                  subProblemDomainDetailController
                                          .subProblemDomainDetail!
                                          .parentCategory!
                                          .categoryName !=
                                      null)
                              ? subProblemDomainDetailController
                                  .subProblemDomainDetail!
                                  .parentCategory!
                                  .categoryName
                              : "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.status,
                          subProblemDomainDetailController
                                  .subProblemDomainDetail!.status ??
                              "-",
                          "-",
                          "-"),

                    ]),
              ),
            ),
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
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Flexible(
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
        Flexible(
          flex: 1,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.end,
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

  subCategoryReasonView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.customer_quota_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: true,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.reason_detail,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          children: <Widget>[
            (subProblemDomainDetailController.subCategoryGroupReason != null &&
                    subProblemDomainDetailController
                        .subCategoryGroupReason!.isNotEmpty)
                ? Container(
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
                  )
                : Container(),
            (subProblemDomainDetailController.subCategoryGroupReason != null &&
                    subProblemDomainDetailController
                        .subCategoryGroupReason!.isNotEmpty)
                ? ListView.builder(
                    physics: const NeverScrollableScrollPhysics(),
                    scrollDirection: Axis.vertical,
                    shrinkWrap: true,
                    itemCount: subProblemDomainDetailController
                        .subCategoryGroupReason?.length,
                    itemBuilder: (context, ii) {
                      TicketSubCategoryGroupReasonMappingList? items =
                          subProblemDomainDetailController
                              .subCategoryGroupReason![ii];
                      int? lstLength = subProblemDomainDetailController
                          .subCategoryGroupReason?.length;
                      return Padding(
                        padding: EdgeInsets.only(
                            top: (ii == 0)
                                ? Constant.SMALL_PADDING
                                : Constant.EXPANTABLE_ITEM_MARGIN,
                            left: Constant.EXPANTABLE_ITEM_MARGIN,
                            right: Constant.EXPANTABLE_ITEM_MARGIN,
                            bottom: (ii == (lstLength! - 1))
                                ? Constant.EXPANTABLE_ITEM_MARGIN
                                : 0),
                        child: InkWell(
                          onTap: () async {},
                          child: Container(
                            decoration: BoxDecoration(
                              color: AppTheme.expantableItemBg,
                              border:
                                  Border.all(color: AppTheme.expantableItemBg),
                              borderRadius: const BorderRadius.all(
                                Radius.circular(3),
                              ),
                            ),
                            child: Padding(
                              padding:
                                  const EdgeInsets.all(Constant.SMALL_PADDING),
                              child: Column(
                                mainAxisAlignment: MainAxisAlignment.start,
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  basicDetailItem(Strings.reason,
                                      items.reason ?? "-", "-", "-"),
                                ],
                              ),
                            ),
                          ),
                        ),
                      );
                    })
                : Container(),
          ],
        ),
      ),
    );
  }

  ticketSubCategoryTatMappingView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.customer_quota_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: true,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.reason_detail,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          children: <Widget>[
            (subProblemDomainDetailController.ticketSubCategoryTatMappingList !=
                        null &&
                    subProblemDomainDetailController
                        .ticketSubCategoryTatMappingList!.isNotEmpty)
                ? Container(
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
                  )
                : Container(),
            (subProblemDomainDetailController.ticketSubCategoryTatMappingList !=
                        null &&
                    subProblemDomainDetailController
                        .ticketSubCategoryTatMappingList!.isNotEmpty)
                ? ListView.builder(
                    physics: const NeverScrollableScrollPhysics(),
                    scrollDirection: Axis.vertical,
                    shrinkWrap: true,
                    itemCount: subProblemDomainDetailController
                        .ticketSubCategoryTatMappingList?.length,
                    itemBuilder: (context, ii) {
                      TicketSubCategoryTatMappingList? items =
                          subProblemDomainDetailController
                              .ticketSubCategoryTatMappingList![ii];
                      int? lstLength = subProblemDomainDetailController
                          .ticketSubCategoryTatMappingList?.length;

                      String tatForTicket = "";
                      if (items.ticketTatMatrix != null &&
                          items.ticketTatMatrix!.name != null &&
                          items.ticketTatMatrix!.name!.isNotEmpty) {
                        tatForTicket = items.ticketTatMatrix!.name!;
                      }

                      return Padding(
                        padding: EdgeInsets.only(
                            top: (ii == 0)
                                ? Constant.SMALL_PADDING
                                : Constant.EXPANTABLE_ITEM_MARGIN,
                            left: Constant.EXPANTABLE_ITEM_MARGIN,
                            right: Constant.EXPANTABLE_ITEM_MARGIN,
                            bottom: (ii == (lstLength! - 1))
                                ? Constant.EXPANTABLE_ITEM_MARGIN
                                : 0),
                        child: InkWell(
                          onTap: () async {},
                          child: Container(
                            decoration: BoxDecoration(
                              color: AppTheme.expantableItemBg,
                              border:
                                  Border.all(color: AppTheme.expantableItemBg),
                              borderRadius: const BorderRadius.all(
                                Radius.circular(3),
                              ),
                            ),
                            child: Padding(
                              padding:
                                  const EdgeInsets.all(Constant.SMALL_PADDING),
                              child: Column(
                                mainAxisAlignment: MainAxisAlignment.start,
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  basicDetailItem(
                                      Strings.order,
                                      items.orderid != null
                                          ? items.orderid!.toString()
                                          : "-",
                                      Strings.tat_for_ticket,
                                      tatForTicket),
                                  const SizedBox(
                                      height: Constant.SMALL_PADDING),
                                  basicDetailItem(
                                      Strings.condition, "-", "-", "-"),
                                ],
                              ),
                            ),
                          ),
                        ),
                      );
                    })
                : Container(),
          ],
        ),
      ),
    );
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

  valueWidget(String? value) {
    return CustomText(
      title: value!.isNotEmpty ? value : "-",
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w400,
      maxLines: 2,
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        Strings.ticket_sub_problem_domain,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}
