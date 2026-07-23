import 'package:savbill/pages/connection_history/connection_history_controller.dart';
import 'package:savbill/pages/connection_history/connection_history_item_view.dart';
import 'package:savbill/pages/connection_history/response/connection_history_res.dart';
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

class ConnectionHistory extends StatefulWidget {
  @override
  _ConnectionHistoryState createState() => _ConnectionHistoryState();
}

class _ConnectionHistoryState extends State<ConnectionHistory> {
  final connectionHistoryController = Get.put(ConnectionHistoryController());

  @override
  void initState() {
    super.initState();
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<ConnectionHistoryController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: connectionHistoryController.isLoading),
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
                 Expanded(child:  CustomText(
                      title: connectionHistoryController.customerName,
                      colors: AppTheme.colorBlack,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.medium + 1,
                      fontWeight: FontWeight.w500)),
                  const SizedBox(
                    width: Constant.VERY_SMALL_PADDING,
                  ),
                  InkWell(
                    onTap: () {
                      if (connectionHistoryController.filterViewOpen) {
                        connectionHistoryController.filterViewOpen = false;
                      } else {
                        connectionHistoryController.filterViewOpen = true;
                      }
                      connectionHistoryController.update();
                    },
                    child: Container(
                        height: 38,
                        margin: const EdgeInsets.only(right: 12), //
                        child: Icon(
                          Icons.filter_alt_rounded,
                          color: connectionHistoryController.isFilterApply
                              ? AppTheme.colorPrimary
                              : AppTheme.colorBlack,
                          size: 32,
                        )),
                  ),
                ],
              ),
            ),
            connectionHistoryController.filterViewOpen
                ? const SizedBox(
                    height: Constant.SMALL_PADDING,
                  )
                : Container(),
            connectionHistoryController.filterViewOpen
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
                                  labelText: Strings.document_no,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                      connectionHistoryController
                                          .frameIpController,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  fontSize: AppTheme.small,
                                  fontWeight: FontWeight.w500,
                                  contentPadding: const EdgeInsets.symmetric(
                                      horizontal: Constant.MEDIUM_PADDING),
                                  borderCorner: Constant.BTN_ROUNDED_CORNER,
                                  onTextValidator: (String? value) {},
                                  onTextFiledOnTap: () {},
                                  readOnly: false),
                              const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              ),
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
                                  textEditingController:
                                      connectionHistoryController
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
                                      connectionHistoryController
                                          .toDateController,
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
                                        connectionHistoryController
                                            .applyFilter();
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
                                        connectionHistoryController
                                            .clearFilter();
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
            connectionHistoryController.filterViewOpen
                ? const SizedBox(
                    height: Constant.MEDIUM_PADDING,
                  )
                : Container(),
            Expanded(
              flex: 1,
              child: (connectionHistoryController.contentData != null &&
                      connectionHistoryController.contentData!.isNotEmpty)
                  ? ListView.builder(
                      controller: connectionHistoryController.controller,
                      scrollDirection: Axis.vertical,
                      itemCount:
                          connectionHistoryController.contentData!.length + 1,
                      itemBuilder: (context, index) {
                        if (index ==
                            connectionHistoryController.contentData?.length) {
                          if (connectionHistoryController.isShowLoadMore) {
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
                          Content item =
                              connectionHistoryController.contentData![index];
                          return ConnectionHistoryViewItem(
                              item: item, index: index);
                        }
                      })
                  : noDataFound(),
            ),
          ]),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.connection_history, '', AppTheme.colorPrimary,
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
      if (connectionHistoryController.selectedFromDate != null) {
        selectedDate = connectionHistoryController.selectedFromDate;
      } else {
        selectedDate = DateTime.now();
      }
    }

    if (identity == Strings.to_date) {
      if (connectionHistoryController.selectedToDate != null) {
        selectedDate = connectionHistoryController.selectedToDate;
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
        connectionHistoryController.selectedFromDate = picked;
        connectionHistoryController.formDateController.text =
            connectionHistoryController.dateFormat.format(picked);
        connectionHistoryController.fromDate =
            connectionHistoryController.apiDateFormat.format(picked);
      }
      if (identity == Strings.to_date) {
        connectionHistoryController.selectedToDate = picked;
        connectionHistoryController.toDateController.text =
            connectionHistoryController.dateFormat.format(picked);
        connectionHistoryController.toDate =
            connectionHistoryController.apiDateFormat.format(picked);
      }
      connectionHistoryController.update();
    }
  }
}
