import 'dart:developer';
import 'package:savbill/pages/change_discount/change_customer_approve_reject_discount_dialog.dart';
import 'package:savbill/pages/change_discount/change_discount_controller.dart';
import 'package:savbill/pages/change_discount/request/discount_approve_reject_req.dart';
import 'package:savbill/pages/change_discount/request/discount_update_req.dart';
import 'package:savbill/pages/change_discount/response/change_discount_list.dart';
import 'package:savbill/pages/customer_inventory/inventory_team_work_flow.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_svg/svg.dart';
import 'package:get/get.dart';
import 'package:intl/intl.dart';

class ChangeDiscountItemView extends StatelessWidget
    implements DiscountApproveRejectBtnAction {
  DiscountDetails item;
  int index;
  CustomerDiscountController customerDiscountController;

  ChangeDiscountItemView(
      {Key? key,
      required this.index,
      required this.item,
      required this.customerDiscountController})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    // String startDt = item.startDate!, endDt = item.endDate!;
    // String? startDt="", endDt = "";
    //
    // if (item.discountExpiryDate != null) {
    //   DateTime date = DateFormat(Constant.API_DATE_FORMAT)
    //       .parse(item.discountExpiryDate ?? "");
    //   customerDiscountController.currentDiscountExpiryDate.text =
    //       DateFormat(Constant.DATE_FORMAT).format(date);
    // }

    // if (item.newDiscountExpiryDate != null) {
    //   DateTime date = DateFormat(Constant.API_DATE_FORMAT)
    //       .parse(item.newDiscountExpiryDate!);
    //   customerDiscountController.newDiscountExpiryDate.text =
    //       DateFormat(Constant.DATE_FORMAT).format(date);
    // }

    customerDiscountController.discountUpdateData = DiscountUpdateData(
      id: item.id,
      custId: customerDiscountController.customerId,
      connectionNo:  item.connectionNo,
      serviceName: item.serviceName,
      serviceId: item.serviceId,
      invoiceType: item.invoiceType,
      discount: item.discount.toString(),
      status: item.status,
      discountType: item.discountType,
      newDiscount: item.newDiscount.toString(),
      remarks:item.remarks,
      newDiscountType: customerDiscountController
            .selectedNewDiscountType!.text,
      discountExpiryDate: customerDiscountController.currentDateTime,
      newDiscountExpiryDate: customerDiscountController.newCurrentDateTime,
    );

    String? currentDiscountExpiry;
    if (item.discountType == Strings.recurring) {
      if (item.discountExpiryDate != null) {
        currentDiscountExpiry = item.discountExpiryDate.toString();
      }
    } else {
      currentDiscountExpiry = "-";
    }

    if (item.remarks != null) {
      customerDiscountController.remarksController =
          TextEditingController(text: item.remarks.toString());
    }

    if (item.nextTeamHierarchyMappingId == null ||
        item.nextStaff != null ||
        (item.status!.equalsIgnoreCase(Strings.active) &&
            item.discountFlowInProcess != "yes") ||
        (item.status!.equalsIgnoreCase(Strings.active.toLowerCase()) &&
            item.discountFlowInProcess != "yes")) {
      customerDiscountController.isPickButton = true;
      // customerDiscountController.update();
    } else {
      customerDiscountController.isPickButton = false;
      // customerDiscountController.update();
    }

    if (item.nextStaff == null ||
        item.nextStaff != customerDiscountController.userDetail!.userId) {
      customerDiscountController.isApproveButton = true;
      customerDiscountController.isRejectedButton = true;
      // customerDiscountController.update();
    } else {
      customerDiscountController.isApproveButton = false;
      customerDiscountController.isRejectedButton = false;
      // customerDiscountController.update();
    }

    if (item.nextStaff != customerDiscountController.userDetail!.userId ||
        item.nextTeamHierarchyMappingId == null) {
      customerDiscountController.isReassignShiftLocation = true;
      // customerDiscountController.update();
    } else {
      customerDiscountController.isReassignShiftLocation = false;
      // customerDiscountController.update();
    }
    if (!(item.nextTeamHierarchyMappingId == null && item.nextStaff == null)) {
      customerDiscountController.isDisable.value = true;
    } else {
      customerDiscountController.isDisable.value = false;
    }

    return Card(
      margin: EdgeInsets.symmetric(
        vertical: index == 0 ? 0 : Constant.MEDIUM_PADDING,
        horizontal: Constant.SCREEN_PADDING,
      ),
      elevation: 2,
      color: AppTheme.colorLightGrey,
      child: Padding(
        padding: const EdgeInsets.symmetric(
          vertical: Constant.SMALL_PADDING,
          horizontal: Constant.SMALL_PADDING,
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              color: AppTheme.colorLightGrey,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  CustomText(
                      title: item.serviceName != null &&
                              item.serviceName!.isNotEmpty
                          ? item.serviceName!.toString()
                          : "-",
                      colors: AppTheme.title_dark,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.small + 1,
                      fontWeight: FontWeight.w500),
                  const SizedBox(
                    height: Constant.SMALL_PADDING,
                  ),
                  Divider(
                    color: AppTheme.title_dark,
                    height: 1,
                  ),
                  const SizedBox(
                    height: Constant.SMALL_PADDING,
                  ),
                  detailItem(
                    Strings.connection_no,
                    item.connectionNo ?? "-",
                    Strings.current_discount_type,
                    item.discountType ?? Strings.onetime,
                  ),
                  const SizedBox(
                    height: Constant.SMALL_PADDING,
                  ),
                  // detailItem(
                  //     Strings.discount_expiry_date,
                  //     currentDiscountExpiry,
                  //     Strings.current_discount,
                  //     item.discount != null ? item.discount.toString() : "-"),

                  Row(
                    // mainAxisSize: MainAxisSize.max,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      Expanded(
                        flex: 1,
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.start,
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            titleWidget(
                              Strings.current_discount,
                            ),
                            const SizedBox(height: Constant.SMALL_PADDING),
                            valueWidget(item.discount != null
                                ? item.discount.toString()
                                : "-"),
                          ],
                        ),
                      ),
                      Expanded(
                        flex: 1,
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.start,
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            titleWidget(
                              Strings.discount_expiry_date,
                            ),
                            const SizedBox(
                                height: Constant.VERY_SMALL_PADDING - 1),
                            customerDiscountController
                                            .selectedNewDiscountType !=
                                        null &&
                                    customerDiscountController
                                        .selectedNewDiscountType!.text!
                                        .equalsIgnoreCase(Strings.recurring)
                                ? CoustomTextField(
                                    labelText:
                                        Strings.current_discount_expiry_date,
                                    // initialValue: (item.discountExpiryDate != null &&
                                    //     item.discountExpiryDate!.isNotEmpty)
                                    //     ? item.discountExpiryDate.toString()
                                    //     : "",
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
                                    fontSize: AppTheme.small,
                                    textColor: AppTheme.colorBlack,
                                    textEditingController:
                                        customerDiscountController
                                            .currentDiscountExpiryDate,
                                    borderEnableColors: AppTheme.colorBlack,
                                    textInputAction: TextInputAction.next,
                                    hintColor: AppTheme.colorIconGrey,
                                    onTextValidator: (String? value) {
                                      if (value!.isEmpty) {
                                        return Strings
                                            .current_discount_expiry_date;
                                      }
                                      return null;
                                    },
                                    onTextFiledOnTap: () {
                                      customerDiscountController.isDisable.value ==
                                              true
                                          ? null
                                          : selectDate(
                                              context,
                                              Strings
                                                  .current_discount_expiry_date,
                                              DateTime(
                                                  DateTime.now().year - 10),
                                              DateTime(
                                                  DateTime.now().year + 10));
                                          customerDiscountController.update();
                                    },
                                    borderCorner: Constant.INPUT_ROUNDED_CORNER,
                                    contentPadding: const EdgeInsets.symmetric(
                                        horizontal: Constant.SMALL_PADDING),
                                    fillColor: customerDiscountController
                                                .isDisable.value ==
                                            true
                                        ? AppTheme.colorGrayTxtBg
                                        : AppTheme.colorWhite,
                                    readOnly: customerDiscountController
                                                .isDisable.value ==
                                            false
                                        ? false
                                        : true)
                                : const SizedBox.shrink(),
                          ],
                        ),
                      )

                      // : Expanded(
                      //     flex: 1,
                      //     child: Column(
                      //       mainAxisAlignment: MainAxisAlignment.start,
                      //       crossAxisAlignment: CrossAxisAlignment.start,
                      //       children: [
                      //         titleWidget(
                      //           Strings.discount_expiry_date,
                      //         ),
                      //         const SizedBox(
                      //             height: Constant.VERY_SMALL_PADDING - 1),
                      //         customerDiscountController
                      //                         .selectedNewDiscountType !=
                      //                     null &&
                      //                 customerDiscountController
                      //                     .selectedNewDiscountType!.text!
                      //                     .equalsIgnoreCase(
                      //                         Strings.recurring)
                      //             ? CustomText(
                      //                 title: dateFormatChange(item.discountExpiryDate ?? ""),
                      //                 colors: AppTheme.lable_noramal,
                      //                 textAlign: TextAlign.start,
                      //                 fontSize: AppTheme.small + 1,
                      //                 fontWeight: FontWeight.w400,
                      //                 maxLines: 2,
                      //               )
                      //             : const SizedBox.shrink(),
                      //       ],
                      //     ),
                      //   ),
                    ],
                  ),
                  const SizedBox(
                    height: Constant.SMALL_PADDING,
                  ),
                  Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Expanded(
                        flex: 1,
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            titleWithRequireWidget(Strings.new_discount, true),
                            const SizedBox(
                              height: Constant.SMALL_PADDING,
                            ),
                            CoustomTextField(
                              focusNode: customerDiscountController.focusNode,
                              initialValue: (item.newDiscount != null &&
                                      item.newDiscount!.isNotEmpty)
                                  ? item.newDiscount.toString()
                                  : "",
                              labelText: Strings.discount,
                              // textEditingController: TextEditingController(text: item.newDiscount.toString()),
                              keyboardType: TextInputType.text,
                              borderEnableColors: AppTheme.colorBlack,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              onTextValidator: (String? value) {
                                return null;
                              },
                              onChanged: (value) {
                                if (value.isEmpty) {
                                  customerDiscountController
                                      .discountList![index].newDiscount = "0.0";
                                } else {
                                  customerDiscountController
                                      .discountList![index]
                                      .newDiscount = value.toString();
                                }
                                customerDiscountController.update();
                              },
                              inputFormatters: [
                                FilteringTextInputFormatter.allow(
                                    RegExp(r'^(\d+)?\.?\d{0,2}')),
                                //FilteringTextInputFormatter.allow(RegExp('[0-9]+')),
                              ],
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.MEDIUM_PADDING),
                              fillColor:
                                  customerDiscountController.isDisable.value ==
                                          true
                                      ? AppTheme.colorGrayTxtBg
                                      : AppTheme.colorWhite,
                              readOnly:
                                  !(item.nextTeamHierarchyMappingId == null &&
                                          item.nextStaff == null)
                                      ? true
                                      : false,
                            ),
                          ],
                        ),
                      ),
                      const SizedBox(
                        width: Constant.SMALL_PADDING,
                      ),
                      Expanded(
                        flex: 1,
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            titleWithRequireWidget(
                                "${Strings.new_txt} ${Strings.discount_type}",
                                true),
                            const SizedBox(
                              height: Constant.SMALL_PADDING,
                            ),
                            IgnorePointer(
                              ignoring:
                                  customerDiscountController.isDisable.value ==
                                          true
                                      ? true
                                      : false,
                              child: DropdownButtonHideUnderline(
                                child: DropdownButtonFormField(
                                  icon: SvgPicture.asset(
                                    downArrowSvg,
                                    height: Constant.DROP_DOWN_ARROW_W_H,
                                    width: Constant.DROP_DOWN_ARROW_W_H,
                                    color: AppTheme.colorBlack,
                                    fit: BoxFit.fill,
                                  ),
                                  decoration: Utils.ddlDecoration(),
                                  hint: Align(
                                    alignment: Alignment.centerLeft,
                                    child: Text(
                                      Strings.discount_type,
                                      style: TextStyle(
                                        fontSize: AppTheme.small,
                                        color: AppTheme.colorIconGrey,
                                        fontFamily: AppTheme.appFontName,
                                      ),
                                    ),
                                  ),
                                  style: AppTheme.dropdownTextStyle,
                                  isExpanded: false,
                                  isDense: true,
                                  value: customerDiscountController
                                      .selectedNewDiscountType,
                                  items: customerDiscountController
                                      .newDiscountList
                                      ?.map((DropdownDetail value) {
                                    return DropdownMenuItem<DropdownDetail>(
                                      value: value,
                                      child: Text(value.text!),
                                    );
                                  }).toList(),
                                  onChanged: (value) {
                                    customerDiscountController
                                            .selectedNewDiscountType =
                                        value as DropdownDetail?;
                                    customerDiscountController.update();
                                  },
                                  validator: (value) {
                                    if (value == null ||
                                        customerDiscountController
                                                .selectedNewDiscountType ==
                                            null) {
                                      return Strings
                                          .please_select_discount_type;
                                    }
                                    return null;
                                  },
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(
                    height: Constant.MEDIUM_PADDING,
                  ),
                ],
              ),
            ),
            Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Expanded(
                  flex: 1,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      titleWithRequireWidget(Strings.remarks, false),
                      const SizedBox(
                        height: Constant.SMALL_PADDING,
                      ),
                      CoustomTextField(
                        labelText: Strings.enter_remarks,
                        initialValue:
                            (item.remarks != null && item.remarks!.isNotEmpty)
                                ? item.remarks.toString()
                                : "",
                        // textEditingController:
                        //     customerDiscountController.remarksController,
                        keyboardType: TextInputType.text,
                        borderEnableColors: AppTheme.colorBlack,
                        textInputAction: TextInputAction.next,
                        hintColor: AppTheme.colorIconGrey,
                        onTextValidator: (String? value) {
                          return null;
                        },
                        onChanged: (value) {
                          if (value.isEmpty) {
                            customerDiscountController
                                .discountList![index].remarks = "";
                          } else {
                            customerDiscountController.discountList![index]
                                .remarks = value.toString();
                          }
                          customerDiscountController.update();
                        },
                        borderCorner: Constant.INPUT_ROUNDED_CORNER,
                        contentPadding: const EdgeInsets.symmetric(
                            horizontal: Constant.MEDIUM_PADDING),
                        fillColor:
                            customerDiscountController.isDisable.value == true
                                ? AppTheme.colorGrayTxtBg
                                : AppTheme.colorWhite,
                        readOnly: !(item.nextTeamHierarchyMappingId == null &&
                                item.nextStaff == null)
                            ? true
                            : false,
                      ),
                    ],
                  ),
                ),
                const SizedBox(
                  width: Constant.MEDIUM_PADDING,
                ),
                customerDiscountController.selectedNewDiscountType != null &&
                        customerDiscountController
                            .selectedNewDiscountType!.text!
                            .equalsIgnoreCase(Strings.recurring)
                    ? Expanded(
                        flex: 1,
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            titleWithRequireWidget(
                                Strings.new_discount_date, false),
                            const SizedBox(
                              height: Constant.SMALL_PADDING,
                            ),
                            CoustomTextField(
                                labelText: Strings.new_discount_date,
                                // initialValue: (item.newDiscountExpiryDate !=
                                //             null &&
                                //         item.newDiscountExpiryDate!.isNotEmpty)
                                //     ? item.newDiscountExpiryDate.toString()
                                //     : "",
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
                                fontSize: AppTheme.small,
                                textColor: AppTheme.colorBlack,
                                textEditingController:
                                    customerDiscountController
                                        .newDiscountExpiryDate,
                                borderEnableColors: AppTheme.colorBlack,
                                textInputAction: TextInputAction.next,
                                hintColor: AppTheme.colorIconGrey,
                                onTextValidator: (String? value) {
                                  if (value!.isEmpty) {
                                    return Strings.please_select_inward_date;
                                  }
                                  return null;
                                },
                                onTextFiledOnTap: () {
                                  customerDiscountController.isDisable.value ==
                                          true
                                      ? null
                                      : selectDate(
                                          context,
                                          Strings.new_discount_expiry_date,
                                          DateTime(DateTime.now().year - 10),
                                          DateTime(DateTime.now().year + 10));
                                  customerDiscountController.update();
                                },
                                borderCorner: Constant.INPUT_ROUNDED_CORNER,
                                contentPadding: const EdgeInsets.symmetric(
                                    horizontal: Constant.SMALL_PADDING),
                                fillColor: customerDiscountController
                                            .isDisable.value ==
                                        true
                                    ? AppTheme.colorGrayTxtBg
                                    : AppTheme.colorWhite,
                                readOnly: customerDiscountController
                                            .isDisable.value ==
                                        false
                                    ? false
                                    : true),
                          ],
                        ),
                      )
                    : const SizedBox.shrink(),
              ],
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  vertical: Constant.SMALL_PADDING,
                  horizontal: Constant.SMALL_PADDING),
              child: Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  crossAxisAlignment: CrossAxisAlignment.end,
                  children: [
                    customerDiscountController.isPickButton == true
                        ? buttonView(pickTicketSvg, AppTheme.colorDisableGray,
                            AppTheme.colorWhite, null)
                        : buttonView(pickTicketSvg, AppTheme.colorPrimary,
                            AppTheme.colorWhite, () {
                            customerDiscountController
                                .pickUpCustomerDiscount(item.id);
                          }),
                    const SizedBox(
                      width: Constant.SMALL_PADDING,
                    ),
                    customerDiscountController.isApproveButton == true
                        ? buttonView(checkSvg, AppTheme.colorDisableGray,
                            AppTheme.colorWhite, null)
                        : buttonView(checkSvg, AppTheme.colorPrimary,
                            AppTheme.colorWhite, () {
                            customerDiscountController.entityId = item.id;
                            addRemarkDiscountDialog(
                                context, Strings.approve, item);
                          }),
                    const SizedBox(
                      width: Constant.SMALL_PADDING,
                    ),
                    customerDiscountController.isRejectedButton == true
                        ? buttonView(cancelSvg, AppTheme.colorDisableGray,
                            AppTheme.colorWhite, null)
                        : buttonView(cancelSvg, AppTheme.colorPrimary,
                            AppTheme.colorWhite, () {
                            customerDiscountController.entityId = item.id;
                            addRemarkDiscountDialog(
                                context, Strings.reject, item);
                          }),
                    const SizedBox(
                      width: Constant.SMALL_PADDING,
                    ),
                    buttonView(assignInventorySvg, AppTheme.colorAccent,
                        AppTheme.colorWhite, () {
                      openDiscountStatus(item.id);
                    }),
                    const SizedBox(
                      width: Constant.SMALL_PADDING,
                    ),
                    customerDiscountController.isReassignShiftLocation == true
                        ? buttonView(assignSvg, AppTheme.colorDisableGray,
                            AppTheme.colorWhite, null)
                        : buttonView(assignSvg, AppTheme.colorPrimary,
                            AppTheme.colorWhite, () {
                            customerDiscountController.entityId = item.id;
                            customerDiscountController.reassignWorkflowGetStaff(
                                item.id, "CUSTOMER_DISCOUNT");
                          })
                  ]),
            ),
          ],
        ),
      ),
    );
  }


  Future<void> selectDate(
      BuildContext context,
      String identity,
      DateTime firstDate,
      DateTime lastDate,
      ) async {
    DateTime? selectedDate;
    if (identity == Strings.current_discount_expiry_date) {
      if (customerDiscountController.selectedCurrentDiscountDateTime != null) {
        selectedDate = customerDiscountController.selectedCurrentDiscountDateTime;
      } else {
        selectedDate = DateTime.now();
      }
    }else if (identity == Strings.new_discount_expiry_date) {
      if (customerDiscountController.selectedNewDiscountDateTime != null) {
        selectedDate = customerDiscountController.selectedNewDiscountDateTime;
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
    if (picked != null) {
      if (identity == Strings.current_discount_expiry_date) {
        customerDiscountController.selectedCurrentDiscountDateTime = picked;
        customerDiscountController.update();
        await selectDateTime(context);
      }else if (identity == Strings.new_discount_expiry_date) {
        customerDiscountController.selectedNewDiscountDateTime = picked;
        customerDiscountController.update();
        await selectCurrentDateTime(context);
      }
    }
  }

  Future<void> selectDateTime(BuildContext context) async {
    TimeOfDay? selectedDateTime = TimeOfDay.now();
    final TimeOfDay? picked = await showTimePicker(
      context: context,
      initialTime: selectedDateTime,
      builder: (BuildContext? context, Widget? child) {
        return MediaQuery(
          data: MediaQuery.of(context!).copyWith(alwaysUse24HourFormat: false),
          child: child!,
        );
      },
    );
    if (picked != null) {
      DateTime dt = DateTime(
        customerDiscountController.selectedCurrentDiscountDateTime!.year,
        customerDiscountController.selectedCurrentDiscountDateTime!.month,
        customerDiscountController.selectedCurrentDiscountDateTime!.day,
        picked.hour,
        picked.minute,
      );
      customerDiscountController.currentDiscountExpiryDate.text =
          customerDiscountController.dateFormat.format(dt);
      customerDiscountController.currentDateTime = customerDiscountController.apiDateStandardFormat.format(dt);
      customerDiscountController.update();
    }
  }

  openDiscountStatus(int? eventId) async {
    var result = await Get.to(const InventoryTeamWorkFlow(), arguments: {
      Constant.ID: eventId,
      Constant.EVENT_TYPE: "CUSTOMER_DISCOUNT"
      // Constant.
    });
    if (result != null && result == true) {
      // inventoryDetailController.getTeamHierarchyApprovalFlow(eventId);
    }
  }

  Future<void> selectCurrentDateTime(BuildContext context) async {
    TimeOfDay? selectedDateTime = TimeOfDay.now();
    final TimeOfDay? picked = await showTimePicker(
      context: context,
      initialTime: selectedDateTime,
      builder: (BuildContext? context, Widget? child) {
        return MediaQuery(
          data: MediaQuery.of(context!).copyWith(alwaysUse24HourFormat: false),
          child: child!,
        );
      },
    );
    if (picked != null) {
      DateTime dt = DateTime(
        customerDiscountController.selectedNewDiscountDateTime!.year,
        customerDiscountController.selectedNewDiscountDateTime!.month,
        customerDiscountController.selectedNewDiscountDateTime!.day,
        picked.hour,
        picked.minute,
      );
      customerDiscountController.newDiscountExpiryDate.text =
          customerDiscountController.dateFormat.format(dt);
      customerDiscountController.newCurrentDateTime = customerDiscountController.apiDateStandardFormat.format(dt);
      customerDiscountController.update();
    }
  }

  detailItem(String title1, String? value1, String title2, String? value2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisAlignment: MainAxisAlignment.start,
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

  titleWithRequireWidget(String title, bool require) {
    return Row(
      children: [
        CustomText(
          title: title,
          colors: AppTheme.title_dark,
          textAlign: TextAlign.start,
          fontSize: AppTheme.small,
          fontWeight: FontWeight.normal,
        ),
        require
            ? CustomText(
                title: " *",
                colors: Colors.red,
                textAlign: TextAlign.start,
                fontSize: AppTheme.small,
                fontWeight: FontWeight.w600,
              )
            : Container(),
      ],
    );
  }

  buttonView(String btnName, Color bgColor, Color txtColor, Function()? onTap) {
    return InkWell(
      onTap: onTap,
      child: Material(
        elevation: 3.0,
        color: bgColor,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
        child: Container(
          height: Constant.BTN_HEIGHT_M - 5,
          width: Constant.BTN_HEIGHT_M - 5,
          alignment: Alignment.center,
          padding: const EdgeInsets.all(Constant.SMALL_PADDING - 1),
          child: SvgPicture.asset(
            btnName,
            height: Constant.ICON_SIZE + 5,
            width: Constant.ICON_SIZE + 5,
            color: txtColor,
            fit: BoxFit.fitWidth,
          ),
        ),
      ),
    );
  }

  addRemarkDiscountDialog(
      BuildContext context, String? pageName, DiscountDetails item) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (context) {
          return ChangeDiscountApproveRejectDialog(
              pageName: pageName,
              discountApproveRejectBtnAction: this,
              discountApproveRejectReq: DiscountApproveRejectReq(
                custPackageId: item.id,
                nextStaffId: 0,
                flag: pageName!.equalsIgnoreCase(Strings.approve)
                    ? Strings.approved.toLowerCase()
                    : Strings.rejected,
                staffId:
                    customerDiscountController.userDetail!.userId.toString(),
              ));
        });
  }

  dateFormatChange(String dateFormat) {
    DateTime date = DateFormat(Constant.API_DATE_FORMAT).parse(dateFormat);
    return DateFormat(Constant.DATE_FORMAT).format(date);
  }

  @override
  void discountApproveRejectStatus(
      {String? identifier,
      TextEditingController? remarkController,
      DiscountApproveRejectReq? discountApproveRejectReq,
      BuildContext? context}) {
    Get.back();
    if (identifier != null && identifier.equalsIgnoreCase(Strings.approve)) {
      customerDiscountController.approveRejectDiscountChange(
          discountApproveRejectReq!, Strings.approve.toLowerCase(), context!);
    } else if (identifier != null &&
        identifier.equalsIgnoreCase(Strings.reject)) {
      customerDiscountController.approveRejectDiscountChange(
          discountApproveRejectReq!, Strings.reject.toLowerCase(), context!);
    }
  }
}
