import 'dart:developer';

import 'package:savbill/pages/customer_inventory/customer_inventory_team_work_flow.dart';
import 'package:savbill/pages/customer_inventory/response/get_all_customer_inventory_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:get/get.dart';

import '../../util/resources.dart';

class CustomerEditInventoryApproveRejectDialog extends StatefulWidget {
  String? pageName;
  CustomerInventoryDataList? item;
  int? staffUserId;
  final CustomerEditInventoryApproveRejectBtnAction?
      editInventoryApproveRejectBtnAction;

  CustomerEditInventoryApproveRejectDialog(
      {Key? key,
      this.pageName,
      this.editInventoryApproveRejectBtnAction,
      this.item,
      this.staffUserId})
      : super(key: key);

  @override
  _CustomerEditInventoryApproveRejectState createState() =>
      _CustomerEditInventoryApproveRejectState();
}

class _CustomerEditInventoryApproveRejectState
    extends State<CustomerEditInventoryApproveRejectDialog> {
  TextEditingController controller = TextEditingController();

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return contentBox(context, widget.pageName!, widget.item);
  }

  contentBox(
      BuildContext context, String title, CustomerInventoryDataList? item) {
    bool? approveDisable = false, rejectDisable = false;

    log("inOutwardMacMapping==>${item!.inOutWardMACMapping![0].currentApproveId}");
    log("staffUserId==>${widget.staffUserId}");

    if (item.inOutWardMACMapping![0].currentApproveId != null) {
      if (item.inOutWardMACMapping![0].currentApproveId == widget.staffUserId) {
        approveDisable = true;
        rejectDisable = true;
        setState(() {});
      } else {
        approveDisable = false;
        rejectDisable = false;
        setState(() {});
      }
    }

    return Padding(
      padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
      child: Stack(
        children: [
          AlertDialog(
            insetPadding: const EdgeInsets.only(
              top: Constant.SCREEN_PADDING * 2,
            ),
            contentPadding: const EdgeInsets.only(
              top: Constant.SCREEN_PADDING,
            ),
            clipBehavior: Clip.antiAliasWithSaveLayer,
            backgroundColor: AppTheme.colorPrimary,
            shape: const RoundedRectangleBorder(
                borderRadius:
                    BorderRadius.all(Radius.circular(Constant.SMALL_PADDING))),
            content: Container(
              width: MediaQuery.of(context).size.width,
              color: AppTheme.colorWhite,
              child: Column(
                  mainAxisSize: MainAxisSize.min,
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Container(
                      color: AppTheme.colorPrimary,
                      padding: const EdgeInsets.symmetric(
                          horizontal: Constant.SCREEN_PADDING,
                          vertical: Constant.MEDIUM_PADDING),
                      child: Align(
                        alignment: Alignment.centerLeft,
                        child: CustomText(
                          title: title,
                          colors: AppTheme.title_dark,
                          fontSize: AppTheme.large,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                    ),
                    const SizedBox(height: Constant.SMALL_PADDING),
                    Padding(
                      padding: const EdgeInsets.symmetric(
                          horizontal: Constant.SMALL_PADDING,
                          vertical: Constant.VERY_SMALL_PADDING),
                      child: basicDetailItem(
                          Strings.old_serial_no,
                          widget.item!.inOutWardMACMapping!.isNotEmpty
                              ? widget
                                  .item!.inOutWardMACMapping![0].serialNumber
                              : "-",
                          Strings.old_mac_address,
                          widget.item!.inOutWardMACMapping!.isNotEmpty
                              ? widget.item!.inOutWardMACMapping![0].macAddress
                              : "-"),
                    ),
                    // reviewEditor(),
                    const SizedBox(height: Constant.SMALL_PADDING),
                    Padding(
                      padding: const EdgeInsets.symmetric(
                          horizontal: Constant.SMALL_PADDING,
                          vertical: Constant.VERY_SMALL_PADDING),
                      child: basicDetailItem(
                          Strings.new_serial_number,
                          widget.item!.inOutWardMACMapping!.isNotEmpty
                              ? widget
                                  .item!.inOutWardMACMapping![1].serialNumber
                              : "-",
                          Strings.new_mac_address,
                          widget.item!.inOutWardMACMapping!.isNotEmpty
                              ? widget.item!.inOutWardMACMapping![1].macAddress
                              : "-"),
                    ),
                    const SizedBox(height: Constant.SMALL_PADDING),
                    Row(
                      crossAxisAlignment: CrossAxisAlignment.center,
                      mainAxisAlignment: MainAxisAlignment.end,
                      children: [
                        Padding(
                          padding: const EdgeInsets.only(
                              top: Constant.SCREEN_PADDING,
                              bottom: Constant.SCREEN_PADDING),
                          child: buttonView(
                              checkSvg,
                              approveDisable == false
                                  ? AppTheme.colorAccent
                                  : AppTheme.custChangeStatusLight,
                              AppTheme.colorWhite,
                              approveDisable == false
                                  ? () {
                                      widget
                                          .editInventoryApproveRejectBtnAction!
                                          .customerEditInventoryApproveRejectStatus(
                                              identifier: Strings.approve,
                                              item: widget.item);
                                    }
                                  : null),
                        ),
                        const SizedBox(
                          width: Constant.MEDIUM_PADDING,
                        ),
                        Padding(
                          padding: const EdgeInsets.only(
                              top: Constant.SCREEN_PADDING,
                              bottom: Constant.SCREEN_PADDING),
                          child: buttonView(
                              cancelSvg,
                              rejectDisable == false
                                  ? AppTheme.colorAccent
                                  : AppTheme.custChangeStatusLight,
                              AppTheme.colorWhite,
                              rejectDisable == false
                                  ? () {
                                      widget
                                          .editInventoryApproveRejectBtnAction!
                                          .customerEditInventoryApproveRejectStatus(
                                              identifier: Strings.reject,
                                              item: widget.item);
                                    }
                                  : null),
                        ),
                        const SizedBox(
                          width: Constant.MEDIUM_PADDING,
                        ),
                        Padding(
                          padding: const EdgeInsets.only(
                              top: Constant.SCREEN_PADDING,
                              bottom: Constant.SCREEN_PADDING),
                          child: buttonView(assignInventorySvg,
                              AppTheme.colorAccent, AppTheme.colorWhite, () {
                            if (item.inOutWardMACMapping!.isNotEmpty &&
                                item.inOutWardMACMapping![1].id != null) {
                              Get.back();
                              openInventoryProgressScreen(
                                  item.inOutWardMACMapping![1].id);
                            }
                          }),
                        ),
                        const SizedBox(
                          width: Constant.MEDIUM_PADDING,
                        ),
                      ],
                    ),
                  ]),
            ),
          ),
          Positioned(
            child: GestureDetector(
              onTap: () {
                Get.back();
              },
              child: Align(
                alignment: Alignment.topRight,
                child: Icon(Icons.close, color: AppTheme.colorWhite),
              ),
            ),
          ),
        ],
      ),
    );
  }

  openInventoryProgressScreen(int? eventId) async {
    var result = await Get.to(const CustomerTeamWorkInventoryFlow(),
        arguments: {
          Constant.ID: eventId,
          Constant.EVENT_TYPE: "CUSTOMER_INVENTORY_ASSIGN"
        });
    if (result != null && result == true) {
      // inventoryDetailController.getTeamHierarchyApprovalFlow(eventId);
    }
  }

  basicDetailItem(
      String title1, String? value1, String title2, String? value2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        Expanded(
          flex: 2,
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
          child: Padding(
            padding: const EdgeInsets.only(right: Constant.SMALL_PADDING),
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

  buttonView(String btnName, Color bgColor, Color txtColor, Function()? onTap) {
    return InkWell(
      onTap: onTap,
      child: Material(
        elevation: 1.0,
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
            height: Constant.ICON_SIZE,
            width: Constant.ICON_SIZE,
            color: txtColor,
            fit: BoxFit.fill,
          ),
        ),
      ),
    );
  }
}

abstract class CustomerEditInventoryApproveRejectBtnAction {
  void customerEditInventoryApproveRejectStatus({
    String identifier,
    CustomerInventoryDataList? item,
    // TextEditingController remarkController,
  });
}
