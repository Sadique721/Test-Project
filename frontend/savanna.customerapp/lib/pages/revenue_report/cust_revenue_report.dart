import 'package:savbill/pages/revenue_report/model/cust_revenue_report_res.dart';
import 'package:savbill/pages/revenue_report/revenue_report_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import 'package:intl/intl.dart';

class CustRevenueReport extends StatefulWidget {
  @override
  _CustRevenueReportState createState() => _CustRevenueReportState();
}

class _CustRevenueReportState extends State<CustRevenueReport> {
  final revenueReportController = Get.put(RevenueReportController());

  @override
  void initState() {
    super.initState();
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<RevenueReportController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: revenueReportController.isLoading),
      ]);
    });
  }

  _body() {
    return Container(
      color: AppTheme.colorBG,
      width: MediaQuery.of(context).size.width,
      child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            Container(
              padding: const EdgeInsets.only(
                  top: Constant.SCREEN_PADDING,
                  left: Constant.SCREEN_PADDING,
                  right: Constant.SCREEN_PADDING),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Expanded(
                      child: CustomText(
                          title: revenueReportController.customerName,
                          colors: AppTheme.colorBlack,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.medium + 1,
                          fontWeight: FontWeight.w500)),
                  const SizedBox(
                    width: Constant.VERY_SMALL_PADDING,
                  ),
                  InkWell(
                    onTap: () {
                      if (revenueReportController.filterViewOpen) {
                        revenueReportController.filterViewOpen = false;
                      } else {
                        revenueReportController.filterViewOpen = true;
                      }
                      revenueReportController.update();
                    },
                    child: Container(
                        height: 38,
                        margin: const EdgeInsets.only(right: 12), //
                        child: Icon(
                          Icons.filter_alt_rounded,
                          color: revenueReportController.isFilterApply
                              ? AppTheme.colorPrimary
                              : AppTheme.colorBlack,
                          size: 32,
                        )),
                  ),
                ],
              ),
            ),
            revenueReportController.filterViewOpen
                ? const SizedBox(
                    height: Constant.SMALL_PADDING,
                  )
                : Container(),
            revenueReportController.filterViewOpen
                ? Container(
                    width: MediaQuery.of(context).size.width,
                    margin: const EdgeInsets.symmetric(
                        horizontal: Constant.SCREEN_PADDING),
                    child: Material(
                      color: AppTheme.colorWhite,
                      elevation: 1.5,
                      shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(
                              Constant.BTN_ROUNDED_CORNER - 2)),
                      child: Padding(
                        padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                        child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              CoustomTextField(
                                  labelText: Strings.from_date,
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
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController: revenueReportController
                                      .formDateController,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  fontSize: AppTheme.small,
                                  fontWeight: FontWeight.w500,
                                  contentPadding: const EdgeInsets.symmetric(
                                      horizontal: Constant.MEDIUM_PADDING),
                                  borderCorner: Constant.BTN_ROUNDED_CORNER,
                                  onTextValidator: (String? value) {},
                                  onTextFiledOnTap: () {
                                    selectDate(
                                        context,
                                        Strings.from_date,
                                        DateTime(DateTime.now().year - 10),
                                        DateTime(DateTime.now().year + 10));
                                  },
                                  readOnly: true),
                              const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              ),
                              CoustomTextField(
                                  labelText: Strings.to_date,
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
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                      revenueReportController.toDateController,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  fontSize: AppTheme.small,
                                  fontWeight: FontWeight.w500,
                                  contentPadding: const EdgeInsets.symmetric(
                                      horizontal: Constant.MEDIUM_PADDING),
                                  borderCorner: Constant.BTN_ROUNDED_CORNER,
                                  onTextValidator: (String? value) {},
                                  onTextFiledOnTap: () {
                                    selectDate(
                                        context,
                                        Strings.to_date,
                                        DateTime(DateTime.now().year - 10),
                                        DateTime(DateTime.now().year + 10));
                                  },
                                  readOnly: true),
                              const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              ),
                              Row(
                                mainAxisAlignment: MainAxisAlignment.center,
                                children: [
                                  Expanded(
                                    child: SimpleButton(
                                      onTap: () {
                                        revenueReportController.applyFilter();
                                      },
                                      radius: Constant.BTN_HEIGHT_M,
                                      height: Constant.BTN_HEIGHT_M,
                                      bgColors: AppTheme.colorPrimary,
                                      child: CustomText(
                                        title: Strings.apply,
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
                                        revenueReportController.clearFilter();
                                      },
                                      radius: Constant.BTN_HEIGHT_M,
                                      height: Constant.BTN_HEIGHT_M,
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
                            ]),
                      ),
                    ),
                  )
                : Container(),
            revenueReportController.filterViewOpen
                ? const SizedBox(
                    height: Constant.MEDIUM_PADDING,
                  )
                : Container(),
            Container(
              padding: const EdgeInsets.only(
                  left: Constant.SCREEN_PADDING - 5,
                  right: Constant.SCREEN_PADDING - 5),
              child: Material(
                color: AppTheme.useCardBg,
                elevation: 0.5,
                shape: RoundedRectangleBorder(
                    borderRadius:
                        BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
                child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const SizedBox(
                        height: Constant.MEDIUM_PADDING,
                      ),
                      Padding(
                          padding: const EdgeInsets.symmetric(
                              horizontal: Constant.SMALL_PADDING),
                          child: Row(
                            mainAxisSize: MainAxisSize.max,
                            crossAxisAlignment: CrossAxisAlignment.start,
                            mainAxisAlignment: MainAxisAlignment.start,
                            children: [
                              Expanded(
                                flex: 2,
                                child: Column(
                                  mainAxisAlignment: MainAxisAlignment.start,
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    titleWidget(
                                        "${Strings.outstanding}\n${Strings.prepaid_revenue}"),
                                    const SizedBox(
                                        height:
                                            Constant.VERY_SMALL_PADDING - 1),
                                    valueWidget(
                                        revenueReportController.responseData !=
                                                null
                                            ? revenueReportController
                                                .responseData!
                                                .outstandingPending!.toStringAsFixed(2)
                                                .toString()
                                            : ""),
                                  ],
                                ),
                              ),
                              Expanded(
                                flex: 2,
                                child: Column(
                                  mainAxisAlignment: MainAxisAlignment.start,
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    titleWidget(Strings.revenue),
                                    const SizedBox(
                                        height:
                                            Constant.VERY_SMALL_PADDING - 1),
                                    valueWidget(
                                        revenueReportController.responseData !=
                                                null
                                            ? revenueReportController
                                                .responseData!.outstandingDbr
                                                .toString()
                                            : ""),
                                  ],
                                ),
                              ),
                              Expanded(
                                flex: 1,
                                child: Column(
                                  mainAxisAlignment: MainAxisAlignment.start,
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    titleWidget(Strings.cumulative_revenue_new),
                                    const SizedBox(
                                        height:
                                            Constant.VERY_SMALL_PADDING - 1),
                                    valueWidget(revenueReportController
                                        .responseData != null ? revenueReportController
                                        .responseData!.outstandingRevenue!.toStringAsFixed(2)
                                        .toString() : ""),
                                  ],
                                ),
                              ),
                            ],
                          )),
                      const SizedBox(
                        height: Constant.SMALL_PADDING,
                      ),
                    ]),
              ),
            ),
            Expanded(
              flex: 1,
              child: (revenueReportController.customerDBRList != null &&
                      revenueReportController.customerDBRList!.isNotEmpty)
                  ? Container(
                      padding: const EdgeInsets.only(
                          top: Constant.SCREEN_PADDING,
                          left: Constant.SCREEN_PADDING - 5,
                          right: Constant.SCREEN_PADDING - 5),
                      child: ListView.builder(
                          // controller: revenueReportController.controller,
                          scrollDirection: Axis.vertical,
                          itemCount: revenueReportController.customerDBRList!.length + 1,
                          itemBuilder: (context, index) {
                            if (index == revenueReportController.customerDBRList!.length) {
                              if (revenueReportController.isShowLoadMore) {
                                return Padding(
                                  padding:
                                  const EdgeInsets.all(Constant.SMALL_PADDING),
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
                              CustomerDBRPojos item = revenueReportController
                                  .customerDBRList![index];
                              return revenueReportViewItem(item: item,controller :revenueReportController);
                            }
                          }),
                    )
                  : noDataFound(),
            ),
          ]),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.revenue_report, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  Future<void> selectDate(
    BuildContext context,
    String identity,
    DateTime firstDate,
    DateTime lastDate,
  ) async {
    DateTime? selectedDate;
    if (identity == Strings.from_date) {
      if (revenueReportController.selectedFromDate != null) {
        selectedDate = revenueReportController.selectedFromDate;
      } else {
        selectedDate = DateTime.now();
      }
    }

    if (identity == Strings.to_date) {
      if (revenueReportController.selectedToDate != null) {
        selectedDate = revenueReportController.selectedToDate;
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
    if (picked != null && picked != selectedDate) {
      if (identity == Strings.from_date) {
        revenueReportController.selectedFromDate = picked;
        revenueReportController.formDateController.text =
            revenueReportController.dateFormat.format(picked);
        revenueReportController.fromDate =
            revenueReportController.apiDateFormat.format(picked);
      }
      if (identity == Strings.to_date) {
        revenueReportController.selectedToDate = picked;
        revenueReportController.toDateController.text =
            revenueReportController.dateFormat.format(picked);
        revenueReportController.toDate =
            revenueReportController.apiDateFormat.format(picked);
      }
      revenueReportController.update();
    }
  }

  revenueReportViewItem({required CustomerDBRPojos item,required RevenueReportController controller}) {
    String? startDt;
    if (item.date != null && item.date!.isNotEmpty) {
      DateTime date = DateFormat(Constant.API_DATE_FORMAT).parse(item.date!);
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
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          const SizedBox(
            height: Constant.MEDIUM_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
              Strings.date,
              startDt,
              "${controller.customerType} ${Strings.revenue}",
              (item.pendingamt != null &&
                      item.pendingamt!.toString().isNotEmpty)
                  ? item.pendingamt!.toStringAsFixed(2).toString()
                  : "-",
            ),
          ),
          const SizedBox(
            height: Constant.MEDIUM_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
              Strings.revenue,
              (item.dbr != null && item.dbr!.toString().isNotEmpty)
                  ? item.dbr.toString()
                  : "-",
              Strings.cumulative_revenue,
              (item.cummRevenue != null &&
                      item.cummRevenue!.toString().isNotEmpty)
                  ? item.cummRevenue!.toStringAsFixed(2).toString()
                  : "-",
            ),
          ),
          const SizedBox(
            height: Constant.MEDIUM_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
              Strings.remarks,
              (item.remark != null && item.remark!.toString().isNotEmpty)
                  ? item.remark.toString()
                  : "-",
              "",
              "",
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
