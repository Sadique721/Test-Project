import 'package:savbill/pages/customer/customer_detail.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer_inventory/inventory_team_work_flow.dart';
import 'package:savbill/pages/dashboard/model/response/payment_list_response.dart';
import 'package:savbill/pages/dashboard/model/response/payment_status_data.dart';
import 'package:savbill/pages/dashboard/payment_invoice_detail.dart';
import 'package:savbill/pages/dashboard/payment_tab_controller.dart';
import 'package:savbill/pages/dashboard/record_payment.dart';
import 'package:savbill/pages/dashboard/view_list_item_payment.dart';
import 'package:savbill/pages/pending_approvals/model/request/payment_approve_reject_req.dart';
import 'package:savbill/pages/pending_approvals/payments/pa_payment_status_dialog.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/acl_constant.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/permission_service.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/list_loader.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/permisstion_deny_dialog.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import 'package:permission_handler/permission_handler.dart';

class PaymentTab extends StatefulWidget {
  PaymentTab({Key? key}) : super(key: key);

  @override
  _PaymentTabState createState() => _PaymentTabState();
}

class _PaymentTabState extends State<PaymentTab>
    with TickerProviderStateMixin, WidgetsBindingObserver
    implements PermissionDenyBtnAction, PaymentStatusBtnAction {
  final paymentTabController = Get.put(PaymentTabController());

  @override
  void initState() {
    WidgetsBinding.instance.addObserver(this);
    super.initState();
    paymentTabController.initPlatformState();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    switch (state) {
      case AppLifecycleState.paused:
        print("on pause method call");
        return;
      case AppLifecycleState.resumed:
        print("on resume method call");
        if (paymentTabController.checkBtnClickEvent) {
          paymentTabController.setBtnClickEvent(false);
          checkPermissionAndDownload();
        }
        return;
      default:
        return;
    }
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<PaymentTabController>(builder: (controller) {
      return Stack(children: <Widget>[
        paymentTabController.isLoading
            ? Padding(
                padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
                child: ListView.separated(
                  itemCount: 5,
                  itemBuilder: (context, index) => const ListLoader(),
                  separatorBuilder: (context, index) =>
                      const SizedBox(height: Constant.SCREEN_PADDING),
                ),
              )
            : Container(
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
                            CustomText(
                                title: Strings.payment_summary,
                                colors: AppTheme.colorBlack,
                                textAlign: TextAlign.start,
                                fontSize: AppTheme.medium + 1,
                                fontWeight: FontWeight.w500),
                            Row(children: [
                              InkWell(
                                onTap: () {
                                  if (paymentTabController.filterViewOpen) {
                                    paymentTabController.filterViewOpen = false;
                                  } else {
                                    if (paymentTabController.isLoadFilterData ==
                                        false) {
                                      paymentTabController
                                          .getCustomerListData();
                                    } else {
                                      paymentTabController.filterViewOpen =
                                          true;
                                    }
                                  }
                                  paymentTabController.update();
                                },
                                child: Container(
                                    height: 38,
                                    margin: const EdgeInsets.only(right: 12), //
                                    child: Icon(
                                      Icons.filter_alt_rounded,
                                      color: paymentTabController.isFilterApply
                                          ? AppTheme.colorPrimary
                                          : AppTheme.colorBlack,
                                      size: 32,
                                    )),
                              ),
                              PermissionService().hasAclPermission(
                                          [AclPaymentSystems.RECORD_PAYMENT]) ==
                                      true
                                  ? Material(
                                      color: AppTheme.colorWhite,
                                      elevation: 2,
                                      shape: RoundedRectangleBorder(
                                          borderRadius:
                                              BorderRadius.circular(20)),
                                      child: InkWell(
                                        onTap: () {
                                          openRecordPaymentScreen();
                                        },
                                        child: Container(
                                          decoration: BoxDecoration(
                                            color: AppTheme.colorPrimary,
                                            borderRadius:
                                                const BorderRadius.all(
                                                    Radius.circular(20)),
                                          ),
                                          padding: const EdgeInsets.all(6),
                                          child: Icon(
                                            Icons.add,
                                            color: AppTheme.colorWhite,
                                            size: 22,
                                          ),
                                        ),
                                      ),
                                    )
                                  : SizedBox.shrink(),
                            ])
                          ],
                        ),
                      ),
                      const SizedBox(
                        height: Constant.MEDIUM_PADDING,
                      ),
                      paymentTabController.filterViewOpen
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
                                  padding: const EdgeInsets.all(
                                      Constant.SMALL_PADDING),
                                  child: Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      // DropdownButtonHideUnderline(
                                      //   child: DropdownButtonFormField(
                                      //     icon: SvgPicture.asset(
                                      //       downArrowSvg,
                                      //       height:
                                      //           Constant.DROP_DOWN_ARROW_W_H,
                                      //       width: Constant.DROP_DOWN_ARROW_W_H,
                                      //       color: AppTheme.colorBlack,
                                      //       fit: BoxFit.fill,
                                      //     ),
                                      //     decoration: InputDecoration(
                                      //         filled: true,
                                      //         contentPadding:
                                      //             const EdgeInsets.fromLTRB(
                                      //                 Constant.LARGE_PADDING,
                                      //                 0,
                                      //                 Constant.LARGE_PADDING,
                                      //                 0),
                                      //         fillColor: AppTheme.colorWhite,
                                      //         hintText: Strings.select_a_customer,
                                      //         hintStyle:AppTheme.dropdownHintStyle,
                                      //         labelText: Strings.select_a_customer,
                                      //         labelStyle:
                                      //             AppTheme.dropdownLabelStyle,
                                      //         errorStyle:
                                      //             AppTheme.dropdownErrorStyle,
                                      //         alignLabelWithHint: true,
                                      //         border: OutlineInputBorder(
                                      //           borderRadius: BorderRadius
                                      //               .circular(Constant
                                      //                   .DROP_DOWN_ROUNDED_CORNER),
                                      //           borderSide: BorderSide(
                                      //               color:
                                      //                   AppTheme.colorIconGrey,
                                      //               width: 0.8),
                                      //         ),
                                      //         focusColor: Colors.transparent,
                                      //         focusedBorder: OutlineInputBorder(
                                      //           borderRadius: BorderRadius
                                      //               .circular(Constant
                                      //                   .DROP_DOWN_ROUNDED_CORNER),
                                      //           borderSide: BorderSide(
                                      //               color:
                                      //                   AppTheme.colorIconGrey,
                                      //               width: 0.8),
                                      //         ),
                                      //         enabledBorder: OutlineInputBorder(
                                      //           borderRadius: BorderRadius
                                      //               .circular(Constant
                                      //                   .DROP_DOWN_ROUNDED_CORNER),
                                      //           borderSide: BorderSide(
                                      //             color: AppTheme.colorIconGrey,
                                      //             width: 1.0,
                                      //           ),
                                      //         ),
                                      //         errorMaxLines: 3),
                                      //     style: AppTheme.dropdownTextStyle,
                                      //     isExpanded: true,
                                      //     isDense: true,
                                      //     value: paymentTabController
                                      //         .selectedCustomer,
                                      //     items: paymentTabController
                                      //         .customerList!
                                      //         .map((CustomerDetail value) {
                                      //       return DropdownMenuItem<
                                      //           CustomerDetail>(
                                      //         value: value,
                                      //         child: CustomText(title: value.name!,colors: AppTheme.title_dark,),
                                      //       );
                                      //     }).toList(),
                                      //     onChanged: (value) {
                                      // paymentTabController
                                      //     .selectedCustomer =
                                      // value as CustomerDetail?;
                                      // paymentTabController.update();
                                      //     },
                                      //     validator: (value) {
                                      //       return null;
                                      //     },
                                      //   ),
                                      // ),
                                      DropdownButtonHideUnderline(
                                        child: DropdownButtonFormField(
                                          icon: SvgPicture.asset(
                                            downArrowSvg,
                                            height:
                                                Constant.DROP_DOWN_ARROW_W_H,
                                            width: Constant.DROP_DOWN_ARROW_W_H,
                                            color: AppTheme.colorBlack,
                                            fit: BoxFit.fill,
                                          ),
                                          decoration: Utils.ddlDecoration(),
                                          hint: Align(
                                              alignment: Alignment.centerLeft,
                                              child: CustomText(
                                                  title:
                                                      Strings.select_a_customer,
                                                  colors:
                                                      AppTheme.colorIconGrey)),
                                          style: AppTheme.dropdownTextStyle,
                                          isExpanded: true,
                                          isDense: true,
                                          value: paymentTabController
                                              .selectedCustomer,
                                          items: paymentTabController
                                              .customerList!
                                              .map((CustomerDetail value) {
                                            return DropdownMenuItem<
                                                CustomerDetail>(
                                              value: value,
                                              child: Align(
                                                alignment: Alignment.centerLeft,
                                                child: CustomText(
                                                  title: value.name!,
                                                  colors: AppTheme.colorBlack,
                                                  textAlign: TextAlign.start,
                                                  fontSize: AppTheme.small,
                                                  fontWeight: FontWeight.w500,
                                                ), //Text(value.desig!),
                                              ),
                                            );
                                          }).toList(),
                                          onChanged: (value) {
                                            paymentTabController
                                                    .selectedCustomer =
                                                value as CustomerDetail?;
                                            paymentTabController.update();
                                          },
                                          validator: (value) {
                                            return null;
                                          },
                                        ),
                                      ),
                                      const SizedBox(
                                        height: Constant.SMALL_PADDING,
                                      ),
                                      Row(children: [
                                        Expanded(
                                          child: CoustomTextField(
                                              labelText: Strings.pay_from_date,
                                              suffixIcon: Padding(
                                                padding:
                                                    const EdgeInsetsDirectional
                                                        .all(Constant
                                                            .MEDIUM_PADDING),
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
                                                  paymentTabController
                                                      .payFormDateController,
                                              borderEnableColors:
                                                  AppTheme.colorIconGrey,
                                              borderFocusColors:
                                                  AppTheme.colorIconGrey,
                                              textColor: AppTheme.colorBlack,
                                              fontSize: AppTheme.small,
                                              fontWeight: FontWeight.w500,
                                              contentPadding:
                                                  const EdgeInsets.symmetric(
                                                      horizontal: Constant
                                                          .MEDIUM_PADDING),
                                              borderCorner:
                                                  Constant.BTN_ROUNDED_CORNER,
                                              onTextValidator:
                                                  (String? value) {},
                                              onTextFiledOnTap: () {
                                                selectDate(
                                                    context,
                                                    Strings.pay_from_date,
                                                    DateTime(
                                                        DateTime.now().year -
                                                            10),
                                                    DateTime(
                                                        DateTime.now().year +
                                                            10));
                                              },
                                              readOnly: true),
                                        ),
                                        const SizedBox(
                                          width: Constant.SMALL_PADDING,
                                        ),
                                        Expanded(
                                          child: CoustomTextField(
                                              labelText: Strings.pay_to_date,
                                              suffixIcon: Padding(
                                                padding:
                                                    const EdgeInsetsDirectional
                                                        .all(Constant
                                                            .MEDIUM_PADDING),
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
                                                  paymentTabController
                                                      .payToDateController,
                                              borderEnableColors:
                                                  AppTheme.colorIconGrey,
                                              borderFocusColors:
                                                  AppTheme.colorIconGrey,
                                              textColor: AppTheme.colorBlack,
                                              fontSize: AppTheme.small,
                                              fontWeight: FontWeight.w500,
                                              contentPadding:
                                                  const EdgeInsets.symmetric(
                                                      horizontal: Constant
                                                          .MEDIUM_PADDING),
                                              borderCorner:
                                                  Constant.BTN_ROUNDED_CORNER,
                                              onTextValidator:
                                                  (String? value) {},
                                              onTextFiledOnTap: () {
                                                selectDate(
                                                    context,
                                                    Strings.pay_to_date,
                                                    DateTime(
                                                        DateTime.now().year -
                                                            10),
                                                    DateTime(
                                                        DateTime.now().year +
                                                            10));
                                              },
                                              readOnly: true),
                                        ),
                                      ]),
                                      const SizedBox(
                                        height: Constant.SMALL_PADDING,
                                      ),
                                      // DropdownButtonHideUnderline(
                                      //   child: DropdownButtonFormField(
                                      //     icon: SvgPicture.asset(
                                      //       downArrowSvg,
                                      //       height:
                                      //           Constant.DROP_DOWN_ARROW_W_H,
                                      //       width: Constant.DROP_DOWN_ARROW_W_H,
                                      //       color: AppTheme.colorBlack,
                                      //       fit: BoxFit.fill,
                                      //     ),
                                      //     decoration: InputDecoration(
                                      //         filled: true,
                                      //         contentPadding:
                                      //             const EdgeInsets.fromLTRB(
                                      //                 Constant.LARGE_PADDING,
                                      //                 0,
                                      //                 Constant.LARGE_PADDING,
                                      //                 0),
                                      //         fillColor: AppTheme.colorWhite,
                                      //         hintText: Strings.payment_status,
                                      //         hintStyle:
                                      //             AppTheme.dropdownHintStyle,
                                      //         labelStyle:
                                      //             AppTheme.dropdownLabelStyle,
                                      //         errorStyle:
                                      //             AppTheme.dropdownErrorStyle,
                                      //         alignLabelWithHint: true,
                                      //         border: OutlineInputBorder(
                                      //           borderRadius: BorderRadius
                                      //               .circular(Constant
                                      //                   .DROP_DOWN_ROUNDED_CORNER),
                                      //           borderSide: BorderSide(
                                      //               color:
                                      //                   AppTheme.colorIconGrey,
                                      //               width: 0.8),
                                      //         ),
                                      //         focusColor: Colors.transparent,
                                      //         focusedBorder: OutlineInputBorder(
                                      //           borderRadius: BorderRadius
                                      //               .circular(Constant
                                      //                   .DROP_DOWN_ROUNDED_CORNER),
                                      //           borderSide: BorderSide(
                                      //               color:
                                      //                   AppTheme.colorIconGrey,
                                      //               width: 0.8),
                                      //         ),
                                      //         enabledBorder: OutlineInputBorder(
                                      //           borderRadius: BorderRadius
                                      //               .circular(Constant
                                      //                   .DROP_DOWN_ROUNDED_CORNER),
                                      //           borderSide: BorderSide(
                                      //             color: AppTheme.colorIconGrey,
                                      //             width: 1.0,
                                      //           ),
                                      //         ),
                                      //         errorMaxLines: 3),
                                      //     style: AppTheme.dropdownTextStyle,
                                      //     isExpanded: true,
                                      //     isDense: true,
                                      //     value: paymentTabController
                                      //         .selectedPaymentStatus,
                                      //     items: paymentTabController
                                      //         .paymentStatusList!
                                      //         .map((PaymentStatus value) {
                                      //       return DropdownMenuItem<
                                      //           PaymentStatus>(
                                      //         value: value,
                                      //         child: Text(value.status!),
                                      //       );
                                      //     }).toList(),
                                      //     onChanged: (value) {
                                      //       paymentTabController
                                      //               .selectedPaymentStatus =
                                      //           value as PaymentStatus?;
                                      //       paymentTabController.update();
                                      //     },
                                      //     validator: (value) {
                                      //       return null;
                                      //     },
                                      //   ),
                                      // ),
                                      DropdownButtonHideUnderline(
                                        child: DropdownButtonFormField(
                                          icon: SvgPicture.asset(
                                            downArrowSvg,
                                            height:
                                                Constant.DROP_DOWN_ARROW_W_H,
                                            width: Constant.DROP_DOWN_ARROW_W_H,
                                            color: AppTheme.colorBlack,
                                            fit: BoxFit.fill,
                                          ),
                                          decoration: Utils.ddlDecoration(),
                                          hint: Align(
                                              alignment: Alignment.centerLeft,
                                              child: CustomText(
                                                  title: Strings
                                                      .select_a_pay_status,
                                                  colors:
                                                      AppTheme.colorIconGrey)),
                                          style: AppTheme.dropdownTextStyle,
                                          isExpanded: true,
                                          isDense: true,
                                          value: paymentTabController
                                              .selectedPaymentStatus,
                                          items: paymentTabController
                                              .paymentStatusList!
                                              .map((PaymentStatus value) {
                                            return DropdownMenuItem<
                                                PaymentStatus>(
                                              value: value,
                                              child: Align(
                                                alignment: Alignment.centerLeft,
                                                child: CustomText(
                                                  title: value.label!,
                                                  colors: AppTheme.colorBlack,
                                                  textAlign: TextAlign.start,
                                                  fontSize: AppTheme.small,
                                                  fontWeight: FontWeight.w500,
                                                ), //Text(value.desig!),
                                              ),
                                            );
                                          }).toList(),
                                          onChanged: (value) {
                                            paymentTabController
                                                    .selectedPaymentStatus =
                                                value as PaymentStatus?;
                                            paymentTabController.update();
                                          },
                                          validator: (value) {
                                            return null;
                                          },
                                        ),
                                      ),
                                      const SizedBox(
                                        height: Constant.SMALL_PADDING,
                                      ),
                                      Row(
                                        children: [
                                          Expanded(
                                            child: CoustomTextField(
                                                labelText: Strings.cheque_no,
                                                hintColor:
                                                    AppTheme.colorIconGrey,
                                                textEditingController:
                                                    paymentTabController
                                                        .chequeNoController,
                                                borderEnableColors:
                                                    AppTheme.colorIconGrey,
                                                borderFocusColors:
                                                    AppTheme.colorIconGrey,
                                                textColor: AppTheme.colorBlack,
                                                fontSize: AppTheme.small,
                                                fontWeight: FontWeight.w500,
                                                contentPadding:
                                                    const EdgeInsets.symmetric(
                                                        horizontal: Constant
                                                            .MEDIUM_PADDING),
                                                borderCorner:
                                                    Constant.BTN_ROUNDED_CORNER,
                                                keyboardType:
                                                    TextInputType.number,
                                                maxLines: 1,
                                                maxLength: 6,
                                                onTextValidator:
                                                    (String? value) {},
                                                onTextFiledOnTap: () {},
                                                readOnly: false),
                                          ),
                                          const SizedBox(
                                            width: Constant.SMALL_PADDING,
                                          ),
                                          Expanded(
                                            child: CoustomTextField(
                                                labelText: Strings.invoice_no,
                                                hintColor:
                                                    AppTheme.colorIconGrey,
                                                textEditingController:
                                                    paymentTabController
                                                        .invoiceNoController,
                                                borderEnableColors:
                                                    AppTheme.colorIconGrey,
                                                borderFocusColors:
                                                    AppTheme.colorIconGrey,
                                                textColor: AppTheme.colorBlack,
                                                fontSize: AppTheme.small,
                                                fontWeight: FontWeight.w500,
                                                contentPadding:
                                                    const EdgeInsets.symmetric(
                                                        horizontal: Constant
                                                            .MEDIUM_PADDING),
                                                borderCorner:
                                                    Constant.BTN_ROUNDED_CORNER,
                                                keyboardType:
                                                    TextInputType.text,
                                                maxLines: 1,
                                                onTextValidator:
                                                    (String? value) {},
                                                onTextFiledOnTap: () {},
                                                readOnly: false),
                                          ),
                                        ],
                                      ),
                                      const SizedBox(
                                        height: Constant.SMALL_PADDING,
                                      ),
                                      Row(
                                        mainAxisAlignment:
                                            MainAxisAlignment.center,
                                        children: [
                                          Expanded(
                                            child: SimpleButton(
                                              onTap: () {
                                                paymentTabController
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
                                                paymentTabController
                                                    .clearFilter();
                                                paymentTabController
                                                        .isClearStaffApproveId =
                                                    true;
                                                paymentTabController
                                                    .getPaymentListData();
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
                                    ],
                                  ),
                                ),
                              ),
                            )
                          : Container(),
                      paymentTabController.filterViewOpen
                          ? const SizedBox(
                              height: Constant.MEDIUM_PADDING,
                            )
                          : Container(),
                      Expanded(
                        flex: 1,
                        child: (paymentTabController.paymentsList != null &&
                                paymentTabController.paymentsList!.isNotEmpty)
                            ? ListView.builder(
                                scrollDirection: Axis.vertical,
                                itemCount:
                                    paymentTabController.paymentsList!.length,
                                itemBuilder: (context, index) {
                                  if (index ==
                                      paymentTabController
                                          .paymentsList?.length) {
                                    if (paymentTabController.isShowLoadMore) {
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
                                    PaymentDetail item = paymentTabController
                                        .paymentsList![index];
                                    return ViewListItemPayment(
                                        index: index,
                                        item: item,
                                        userId: paymentTabController
                                            .userDetail!.userId!,
                                        controller: paymentTabController,
                                        isShowBtn: true,
                                        onApproveTap: () {
                                          paymentTabController.entityId =
                                              paymentTabController
                                                  .paymentsList![index].id;
                                          showApproveRejectConfirmDialog(
                                              Strings.approve,
                                              paymentTabController
                                                  .paymentsList![index]);
                                        },
                                        onRejectTap: () {
                                          paymentTabController.entityId =
                                              paymentTabController
                                                  .paymentsList![index].id;
                                          showApproveRejectConfirmDialog(
                                              Strings.reject,
                                              paymentTabController
                                                  .paymentsList![index]);
                                        },
                                        onDownloadTap: () async {
                                          paymentTabController.downloadId =
                                              item.id;
                                          paymentTabController.update();
                                          // checkPermissionAndDownload();
                                          paymentTabController.fileDownloading(
                                              Strings.payment,
                                              UrlConstants.payment_receipt_url +
                                                  paymentTabController
                                                      .downloadId
                                                      .toString(),
                                              item.customerName);
                                        },
                                        onAuditStatusTap: () {
                                          openPaymentStatusScreen(item.id!);
                                        },
                                        onTapReassignPayment: () {
                                          // openInvoiceDetailScreen(item.id!);
                                          paymentTabController.entityId =
                                              paymentTabController
                                                  .paymentsList![index].id;
                                          paymentTabController
                                              .reassignWorkflowGetStaff(
                                                  paymentTabController
                                                      .paymentsList![index].id,
                                                  "PAYMENT");
                                        },
                                        onCustomerTap: () {
                                          openCustomerDetailScreen(item.custId);
                                        });
                                  }
                                })
                            : noDataFound(),
                      ),
                    ]),
              ),
        ProgressBar(isLoader: paymentTabController.isLoadingProgress)
      ]);
    });
  }

  // openPaymentStatusScreen(int id) async {
  //   Get.to(PaymentAudit(), arguments: {
  //     Constant.ID: id,
  //   });
  // }

  openPaymentStatusScreen(int? eventId) async {
    var result = await Get.to(const InventoryTeamWorkFlow(), arguments: {
      Constant.ID: eventId,
      Constant.EVENT_TYPE: "PAYMENT"
      // Constant.
    });
    if (result != null && result == true) {
      // inventoryDetailController.getTeamHierarchyApprovalFlow(eventId);
    }
  }

  openInvoiceDetailScreen(int id) async {
    Get.to(PaymentInvoiceDetail(), arguments: {
      Constant.ID: id,
    });
  }

  openRecordPaymentScreen() async {
    bool chkRefresh = await Get.to(RecordPayment(), arguments: {
      Constant.FROM: Strings.payment,
      // Constant.CUSTOMER_DETAIL: customerPaymentListController.customerDetail,
      // Constant.CUSTOMER_LIST: paymentTabController.customerList,
    });
    // if (chkRefresh) {
    if (chkRefresh == true) {
      Utils.showSnackbar(Strings.SUCCESS, "Payment created successfully.",
          AppTheme.colorWhite, AppTheme.colorGreen);
      paymentTabController.clearFilter();
      paymentTabController.getPaymentListData();
      paymentTabController.update();
    }
  }

  openCustomerDetailScreen(int? customerId) async {
    Get.to(CustomerDetailScreen(), arguments: {
      Constant.CUSTOMER_ID: customerId,
    });
  }

  Future<void> selectDate(
    BuildContext context,
    String identity,
    DateTime firstDate,
    DateTime lastDate,
  ) async {
    DateTime? selectedDate;
    if (identity == Strings.pay_from_date) {
      if (paymentTabController.selectedPayFromDate != null) {
        selectedDate = paymentTabController.selectedPayFromDate;
      } else {
        selectedDate = DateTime.now();
      }
    }

    if (identity == Strings.pay_to_date) {
      if (paymentTabController.selectedPayToDate != null) {
        selectedDate = paymentTabController.selectedPayToDate;
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
      if (identity == Strings.pay_from_date) {
        paymentTabController.selectedPayFromDate = picked;
        paymentTabController.payFormDateController.text =
            paymentTabController.dateFormat.format(picked);
        paymentTabController.selectedPayFromDateApi =
            paymentTabController.apiDateFormat.format(picked);
      }
      if (identity == Strings.pay_to_date) {
        paymentTabController.selectedPayToDate = picked;
        paymentTabController.payToDateController.text =
            paymentTabController.dateFormat.format(picked);
        paymentTabController.selectedPayToDateApi =
            paymentTabController.apiDateFormat.format(picked);
      }
      paymentTabController.update();
    }
  }

  showApproveRejectConfirmDialog(String type, PaymentDetail detail) {
    showDialog(
        context: context,
        barrierDismissible: false,
        builder: (BuildContext context) {
          return PAPaymentStatusDialog(
            from: type,
            paymentStatusBtnAction: this,
            paymentApproveRejectReq: PaymentApproveRejectReq(
                idlist: detail.id,
                customerid: detail.custId,
                paymode: detail.paymode,
                paystatus: detail.status,
                paytodate: detail.paymentdate,
                referenceno: detail.referenceno),
          );
        });
  }

  checkPermissionAndDownload() async {
    final status = await Permission.storage.request();
    if (status.isGranted) {
      paymentTabController.downloadFile();
    } else {
      permissionDenyDialog();
    }
  }

  void permissionDenyDialog() async {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return PermissionDenyDialog(
              permissionDenyBtnAction: this,
              titleMsg: Strings.file_storage_permission_denied_msg);
        });
  }

  noDataFound() {
    return const NoDataFound();
  }

  @override
  void btnClickAction({String? btnIdentifier}) {
    Get.back();
    if (btnIdentifier!.equalsIgnoreCase(Strings.app_permission_settings)) {
      paymentTabController.setBtnClickEvent(true);
      openAppSettings();
    }
  }

  @override
  void paymentStatusBtnAction(
      {String? identifier, PaymentApproveRejectReq? request}) {
    Get.back();
    if (request != null && identifier != null && identifier.isNotEmpty) {
      paymentTabController.approveRejectPayment(identifier, request);
    }
  }
}
