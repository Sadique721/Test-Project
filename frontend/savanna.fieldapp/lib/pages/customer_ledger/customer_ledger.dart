import 'package:savbill/pages/customer_ledger/customer_ladger_item_view.dart';
import 'package:savbill/pages/customer_ledger/customer_ledger_controller.dart';
import 'package:savbill/pages/customer_ledger/response/customer_ledger_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
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

class CustomerLedgerDetail extends StatefulWidget {
  @override
  _CustomerLedgerDetailState createState() => _CustomerLedgerDetailState();
}

class _CustomerLedgerDetailState extends State<CustomerLedgerDetail> {
  final customerLedgerController = Get.put(CustomerLedgerController());

  @override
  void initState() {
    super.initState();
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<CustomerLedgerController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: customerLedgerController.isLoading),
      ]);
    });
  }

  _body() {
    String? customerStatus;
    Color? statusColor = AppTheme.colorGreen;
    if(customerLedgerController.ledgerDetail != null) {
      if (customerLedgerController
          .ledgerDetail!
          .status!.equalsIgnoreCase("Active")) {
        statusColor = AppTheme.colorGreen;
        customerStatus = customerLedgerController
            .ledgerDetail!.status;
      } else if (customerLedgerController
          .ledgerDetail!
          .status!.equalsIgnoreCase("NewActivation")) {
        statusColor = AppTheme.colorBlueRView;
        customerStatus = customerLedgerController
            .ledgerDetail!.status;
      } else if (customerLedgerController
          .ledgerDetail!
          .status!.equalsIgnoreCase("Terminate")) {
        statusColor = AppTheme.colorRed;
        customerStatus = customerLedgerController
            .ledgerDetail!.status;
      }
    }
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
                          title: customerLedgerController.customerName,
                          colors: AppTheme.colorBlack,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.medium + 1,
                          fontWeight: FontWeight.w500)),
                  const SizedBox(
                    width: Constant.VERY_SMALL_PADDING,
                  ),
                  InkWell(
                    onTap: () {
                      if (customerLedgerController.filterViewOpen) {
                        customerLedgerController.filterViewOpen = false;
                      } else {
                        customerLedgerController.filterViewOpen = true;
                      }
                      customerLedgerController.update();
                    },
                    child: Container(
                        height: 38,
                        margin: const EdgeInsets.only(right: 12), //
                        child: Icon(
                          Icons.filter_alt_rounded,
                          color: customerLedgerController.isFilterApply
                              ? AppTheme.colorPrimary
                              : AppTheme.colorBlack,
                          size: 32,
                        )),
                  ),
                ],
              ),
            ),
            customerLedgerController.filterViewOpen
                ? Container(
                    width: MediaQuery.of(context).size.width,
                    margin: const EdgeInsets.only(
                      top: Constant.SMALL_PADDING,
                      left: Constant.SCREEN_PADDING,
                      right: Constant.SCREEN_PADDING,
                    ),
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
                                  textEditingController:
                                      customerLedgerController
                                          .fromDateController,
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
                                      customerLedgerController.toDateController,
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
                                        customerLedgerController.applyFilter();
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
                                        customerLedgerController.clearFilter();
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
            customerLedgerController.ledgerDetail != null
                ? Container(
                    margin: const EdgeInsets.only(
                      top: Constant.SCREEN_PADDING,
                      left: Constant.SCREEN_PADDING,
                      right: Constant.SCREEN_PADDING,
                    ),
                    width: MediaQuery.of(context).size.width,
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
                              Row(
                                crossAxisAlignment: CrossAxisAlignment.center,
                                mainAxisAlignment:
                                    MainAxisAlignment.spaceBetween,
                                children: [
                                  CustomText(
                                    title: customerLedgerController
                                        .ledgerDetail!.custname,
                                    fontSize: AppTheme.medium + 1,
                                    colors: AppTheme.title_dark,
                                    textAlign: TextAlign.start,
                                    maxLines: 2,
                                    fontWeight: FontWeight.w500,
                                  ),

                                  Padding(
                                    padding: const EdgeInsets.symmetric(
                                        horizontal: Constant.VERY_SMALL_PADDING,
                                        vertical: Constant.VERY_SMALL_PADDING),
                                    child: Container(
                                      padding: const EdgeInsets.symmetric(
                                          horizontal: Constant.SMALL_PADDING,
                                          vertical:
                                              Constant.VERY_SMALL_PADDING),
                                      decoration: BoxDecoration(
                                        borderRadius: BorderRadius.circular(
                                            Constant.LARGE_PADDING),
                                        color:statusColor,
                                      ),
                                      child: CustomText(
                                          title: customerStatus,
                                          colors: AppTheme.colorWhite,
                                          textAlign: TextAlign.start,
                                          fontSize: AppTheme.small,
                                          maxLines: 2,
                                          height: 1,
                                          fontWeight: FontWeight.w500),
                                    ),
                                  ),
                                ],
                              ),
                              Divider(
                                color: AppTheme.title_dark,
                                thickness: 0.5,
                              ),
                              RichText(
                                text: TextSpan(
                                    text: "${Strings.username} : ",
                                    style: TextStyle(
                                      color: AppTheme.title_dark,
                                      fontSize: AppTheme.small,
                                      fontWeight: FontWeight.w400,
                                    ),
                                    children: [
                                      TextSpan(
                                        text: customerLedgerController
                                            .ledgerDetail!.username,
                                        style: TextStyle(
                                          color: AppTheme.title_dark,
                                          fontSize: AppTheme.small,
                                          fontWeight: FontWeight.w600,
                                        ),
                                      ),
                                    ]),
                              ),
                              const SizedBox(
                                height: Constant.SMALL_PADDING,
                              ),
                              RichText(
                                text: TextSpan(
                                    text: "${Strings.address} : ",
                                    style: TextStyle(
                                      color: AppTheme.title_dark,
                                      fontSize: AppTheme.small,
                                      fontWeight: FontWeight.w400,
                                    ),
                                    children: [
                                      TextSpan(
                                        text: customerLedgerController
                                            .ledgerDetail!.address,
                                        style: TextStyle(
                                          color: AppTheme.lable_noramal,
                                          fontSize: AppTheme.small,
                                          fontWeight: FontWeight.w400,
                                        ),
                                      ),
                                    ]),
                              ),
                              const SizedBox(
                                height: Constant.SMALL_PADDING,
                              ),
                              RichText(
                                text: TextSpan(
                                    text: "${Strings.opening_amount} : ",
                                    style: TextStyle(
                                      color: AppTheme.lable_noramal,
                                      fontSize: AppTheme.small,
                                      fontWeight: FontWeight.w500,
                                    ),
                                    children: [
                                      TextSpan(
                                        text: "${customerLedgerController.currencySymbol} ",
                                        style: TextStyle(
                                          color: AppTheme.colorPrimary,
                                          fontSize: AppTheme.small,
                                          fontWeight: FontWeight.w400,
                                        ),
                                      ),
                                      TextSpan(
                                        text: customerLedgerController
                                            .openingAmount,
                                        style: TextStyle(
                                          color: AppTheme.title_dark,
                                          fontSize: AppTheme.small,
                                          fontWeight: FontWeight.w600,
                                        ),
                                      ),
                                    ]),
                              ),
                              const SizedBox(
                                height: Constant.SMALL_PADDING,
                              ),
                              RichText(
                                text: TextSpan(
                                    text: "${Strings.closing_balance} : ",
                                    style: TextStyle(
                                      color: AppTheme.lable_noramal,
                                      fontSize: AppTheme.small,
                                      fontWeight: FontWeight.w400,
                                    ),
                                    children: [
                                      TextSpan(
                                        text: "${customerLedgerController.currencySymbol} ",
                                        style: TextStyle(
                                          color: AppTheme.colorPrimary,
                                          fontSize: AppTheme.small,
                                          fontWeight: FontWeight.w400,
                                        ),
                                      ),
                                      TextSpan(
                                        text: customerLedgerController
                                            .closingBalance,
                                        style: TextStyle(
                                          color: AppTheme.title_dark,
                                          fontSize: AppTheme.small,
                                          fontWeight: FontWeight.w600,
                                        ),
                                      ),
                                    ]),
                              ),
                            ]),
                      ),
                    ),
                  )
                : Container(),
            Expanded(
              flex: 1,
              child: (customerLedgerController.debitCreditDetail != null &&
                      customerLedgerController.debitCreditDetail!.isNotEmpty)
                  ? Container(
                      padding:
                          const EdgeInsets.only(top: Constant.SMALL_PADDING),
                      margin: const EdgeInsets.only(
                          top: Constant.VERY_SMALL_PADDING),
                      child: ListView.builder(
                          scrollDirection: Axis.vertical,
                          itemCount: customerLedgerController
                              .debitCreditDetail!.length,
                          itemBuilder: (context, index) {
                            LedgerDebitCreditDetail item =
                                customerLedgerController
                                    .debitCreditDetail![index];
                            return CustomerLadgerViewItem(
                                item: item,
                                index: index,
                                controller: customerLedgerController);
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
    return DynamicAppBar(Strings.ledger_detail, '', AppTheme.colorPrimary,
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
      if (customerLedgerController.selectedFromDate != null) {
        selectedDate = customerLedgerController.selectedFromDate;
      } else {
        selectedDate = DateTime.now();
      }
    }

    if (identity == Strings.to_date) {
      if (customerLedgerController.selectedToDate != null) {
        selectedDate = customerLedgerController.selectedToDate;
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
        customerLedgerController.selectedFromDate = picked;
        customerLedgerController.fromDateController.text =
            customerLedgerController.dateFormat.format(picked);
        customerLedgerController.fromDate =
            customerLedgerController.apiDateFormat.format(picked);
      }
      if (identity == Strings.to_date) {
        customerLedgerController.selectedToDate = picked;
        customerLedgerController.toDateController.text =
            customerLedgerController.dateFormat.format(picked);
        customerLedgerController.toDate =
            customerLedgerController.apiDateFormat.format(picked);
      }
      customerLedgerController.update();
    }
  }
}
