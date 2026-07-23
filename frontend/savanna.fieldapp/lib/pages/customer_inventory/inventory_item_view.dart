import 'package:savbill/pages/customer/model/response/plan_service_by_customer_res.dart';
import 'package:savbill/pages/customer_inventory/inventory_detail_controller.dart';
import 'package:savbill/pages/customer_inventory/response/get_all_customer_inventory_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get_utils/get_utils.dart';

class InventoryViewItem extends StatefulWidget {
  CustomerInventoryDataList item;
  List<CustomerPlanServiceDetail>? serviceList;
  int index;
  int? userId;
  InventoryDetailController controller;
  final Function()? onTapApprove;
  final Function()? onTapReject;
  final Function()? onTapReplace;
  final Function()? onTapUpload;
  final Function()? onTapView;
  final Function()? onTapDownload;
  final Function()? onTapEdit;
  final Function()? onTapReactiveBox;
  final Function()? onTapWifiConfig;
  final Function()? onTapDeleteInventory;
  final Function()? onTapApproveProgress;
  final Function()? onTapApproveRemoveInventory;
  final Function()? onTapRejectRemoveInventory;

  InventoryViewItem(
      {Key? key,
      required this.index,
      required this.item,
      required this.userId,
      required this.serviceList,
      required this.controller,
      this.onTapApprove,
      this.onTapReject,
      this.onTapReplace,
      this.onTapUpload,
      this.onTapView,
      this.onTapDownload,
      this.onTapEdit,
      this.onTapReactiveBox,
      this.onTapWifiConfig,
      this.onTapDeleteInventory,
      this.onTapApproveProgress,
      this.onTapApproveRemoveInventory,
      this.onTapRejectRemoveInventory})
      : super(key: key);

  @override
  State<InventoryViewItem> createState() => _InventoryViewItemState();
}

class _InventoryViewItemState extends State<InventoryViewItem> {
  @override
  Widget build(BuildContext context) {
    Color? statusColor,
        replaceColor,
        downloadColor,
        editColor,
        removeColor,
        removeInventoryColor;
    bool? approveBtnDisable = false,
        rejectBtnDisable = false,
        replaceBtnDisable = true,
        viewBtnDisable = false,
        downloadBtnDisable = true,
        editBtnDisable = false,
        removeBtnDisable = false,
        approveRemoveInventoryBtnDisable = true,
        rejectRemoveInventoryBtnDisable = true;
    if (!(widget.item.hasSerial ?? false)) {
      viewBtnDisable = true;
    }
    print("serialize: ${widget.item.hasSerial}");
    if (widget.item.status!.equalsIgnoreCase(Constant.PENDING.toUpperCase())) {
      statusColor = AppTheme.statusPending;
    } else if (widget.item.status!.equalsIgnoreCase(Constant.ACTIVE)) {
      statusColor = AppTheme.statusApprove;
    } else {
      statusColor = AppTheme.statusReject;
    }

    if ((widget.item.nextApproverId != widget.userId) ||
        !(widget.item.status!.equalsIgnoreCase("PENDING"))) {
      approveBtnDisable = true;
      rejectBtnDisable = true;
    } else {
      approveBtnDisable = false;
      rejectBtnDisable = false;
    }

    if (widget.item.generateRemoveRequest == false ||
        (widget.item.nextApproverId != widget.userId &&
            widget.item.status!.equalsIgnoreCase("PENDING FOR REMOVE")) ||
        widget.item.removeRequestStatus!.equalsIgnoreCase("REJECTED")) {
      approveRemoveInventoryBtnDisable = false;
      rejectRemoveInventoryBtnDisable = false;
      removeInventoryColor = AppTheme.custChangeStatusLight;
    } else {
      approveRemoveInventoryBtnDisable = true;
      rejectRemoveInventoryBtnDisable = true;
      removeInventoryColor = AppTheme.colorPrimary;
    }

    if (widget.item.status!.equalsIgnoreCase(Strings.pending.toUpperCase()) ||
        widget.item.status!.equalsIgnoreCase(Strings.rejected.toUpperCase()) ||
        widget.item.inOutWardMACMapping!.length > 1 ||
        (widget.item.hasMac == false &&
            widget.item.hasSerial == false &&
            widget.item.hasTrackable == false) ||
        widget.item.externalItemId != null ||
        isDisableReplace(widget.item, widget.serviceList)) {
      replaceBtnDisable = false;
      replaceColor = AppTheme.custChangeStatusLight;
      setState(() {});
    } else {
      replaceBtnDisable = true;
      replaceColor = AppTheme.colorAccent;
      setState(() {});
    }

    if (widget.item.filename == null || widget.item.filename == "") {
      downloadBtnDisable = false;
      downloadColor = AppTheme.custChangeStatusLight;
      setState(() {});
    } else {
      downloadBtnDisable = true;
      downloadColor = AppTheme.colorAccent;
      setState(() {});
    }

    if (widget.item.status!.equalsIgnoreCase(Strings.pending.toUpperCase()) ||
        widget.item.status!.equalsIgnoreCase(Strings.rejected.toUpperCase()) ||
        (widget.item.inOutWardMACMapping?.length ?? 0) <= 1 ||
        (!widget.item.hasMac! &&
            !widget.item.hasSerial! &&
            !widget.item.hasTrackable!) ||
        widget.item.externalItemId != null) {
      editBtnDisable = true;
      editColor = AppTheme.custChangeStatusLight;
    } else {
      editBtnDisable = false;
      editColor = AppTheme.colorAccent;
    }

    if (widget.item.status!.equalsIgnoreCase("PENDING") ||
            widget.item.status!.equalsIgnoreCase("REJECTED") ||
            widget.item.removeRequestStatus != null
        ? ((widget.item.removeRequestStatus != null &&
                widget.item.removeRequestStatus!.equalsIgnoreCase("PENDING")) ||
            (widget.item.removeRequestStatus != null &&
                widget.item.removeRequestStatus!.equalsIgnoreCase("REJECTED")))
        : false ||
            widget.item.inOutWardMACMapping!.length > 1
        || (
                widget.item.hasMac == false &&
                widget.item.hasSerial == false &&
                widget.item.hasTrackable == false)
    ) {
      // &&
      // isDisableRemove(widget.item, widget.serviceList))) {
      removeBtnDisable = true;
      removeColor = AppTheme.custChangeStatusLight;
      // setState(() {});
    } else {
      removeBtnDisable = false;
      removeColor = AppTheme.colorAccent;
      // setState(() {});
    }
  print("removeBtnDisable: $removeBtnDisable" + "status: ${widget.item.status}");
    return Container(
      margin: const EdgeInsets.only(
        left: Constant.SCREEN_PADDING,
        right: Constant.SCREEN_PADDING,
        bottom: Constant.MEDIUM_PADDING,
      ),
      child: Material(
        color: AppTheme.colorWhite,
        elevation: 0.5,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
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
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    CustomText(
                        title: "#${widget.item.connectionNo ?? ""}",
                        colors: AppTheme.title_dark,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.small + 1,
                        fontWeight: FontWeight.bold),
                  ]),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Row(
              children: [
                Expanded(
                  child: Padding(
                    padding: const EdgeInsets.symmetric(
                        horizontal: Constant.SMALL_PADDING),
                    child: CustomText(
                      title:
                          "${Strings.service_name} : ${widget.item.serviceName ?? ""}",
                      colors: AppTheme.colorBlack,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.verySmall + 1,
                      fontWeight: FontWeight.w400,
                    ),
                  ),
                ),
                Expanded(
                  child: Padding(
                    padding: const EdgeInsets.symmetric(
                        horizontal: Constant.SMALL_PADDING),
                    child: CustomText(
                      title:
                          "${Strings.item_type} : ${widget.item.itemType ?? "-"}",
                      colors: AppTheme.colorBlack,
                      textAlign: TextAlign.center,
                      fontSize: AppTheme.verySmall + 1,
                      fontWeight: FontWeight.w400,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Row(
              children: [
                Expanded(
                  child: Padding(
                    padding: const EdgeInsets.symmetric(
                        horizontal: Constant.SMALL_PADDING),
                    child: CustomText(
                      title:
                          "${Strings.product_name} : ${widget.item.productName ?? ""}",
                      colors: AppTheme.colorBlack,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.verySmall + 1,
                      fontWeight: FontWeight.w400,
                    ),
                  ),
                ),
                Expanded(
                  child: Padding(
                    padding: const EdgeInsets.symmetric(
                        horizontal: Constant.SMALL_PADDING),
                    child: CustomText(
                      title: "${Strings.assign_qty} : ${widget.item.qty ?? ""}",
                      colors: AppTheme.colorBlack,
                      textAlign: TextAlign.center,
                      fontSize: AppTheme.verySmall + 1,
                      fontWeight: FontWeight.w400,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Row(
              children: [
                Expanded(
                  child: Padding(
                    padding: const EdgeInsets.symmetric(
                        horizontal: Constant.SMALL_PADDING),
                    child: CustomText(
                      title:
                          "${Strings.current_plan} : ${widget.item.currentPlan ?? "-"}",
                      colors: AppTheme.colorBlack,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.verySmall + 1,
                      fontWeight: FontWeight.w400,
                    ),
                  ),
                ),
                Container(
                  decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(20),
                      color: statusColor),
                  margin: const EdgeInsets.only(right: Constant.MEDIUM_PADDING),
                  padding: const EdgeInsets.symmetric(
                      vertical: Constant.SMALL_PADDING,
                      horizontal: Constant.MEDIUM_PADDING),
                  child: CustomText(
                    title: widget.item.status ?? "-",
                    colors: AppTheme.colorWhite,
                    textAlign: TextAlign.center,
                    fontSize: AppTheme.verySmall + 1,
                    fontWeight: FontWeight.w400,
                  ),
                ),
              ],
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: CustomText(
                title:
                    "${Strings.approved} By : ${widget.item.assigneeName ?? "-"}",
                colors: AppTheme.colorBlack,
                textAlign: TextAlign.start,
                fontSize: AppTheme.verySmall + 1,
                fontWeight: FontWeight.w400,
              ),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Row(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                approveBtnDisable == false
                    ? buttonView(
                        checkSvg,
                        widget.item.status!.equalsIgnoreCase(
                                Constant.PENDING.toUpperCase())
                            ? widget.item.status!.equalsIgnoreCase(
                                    Constant.REJECTED.toUpperCase())
                                ? widget.item.status!.equalsIgnoreCase(
                                        Constant.ACTIVE.toUpperCase())
                                    ? AppTheme.colorAccent
                                    : AppTheme.colorAccent
                                : AppTheme.colorAccent
                            : AppTheme.custChangeStatusLight,
                        AppTheme.colorWhite,
                        widget.onTapApprove!)
                    : buttonView(checkSvg, AppTheme.custChangeStatusLight,
                        AppTheme.colorWhite, null),
                const SizedBox(
                  width: Constant.MEDIUM_PADDING,
                ),
                rejectBtnDisable == false
                    ? buttonView(
                        cancelSvg,
                        widget.item.status!.equalsIgnoreCase(
                                Constant.PENDING.toUpperCase())
                            ? widget.item.status!.equalsIgnoreCase(
                                    Constant.REJECTED.toUpperCase())
                                ? widget.item.status!.equalsIgnoreCase(
                                        Constant.ACTIVE.toUpperCase())
                                    ? AppTheme.colorAccent
                                    : AppTheme.colorAccent
                                : AppTheme.colorAccent
                            : AppTheme.custChangeStatusLight,
                        AppTheme.colorWhite,
                        rejectBtnDisable == false ? widget.onTapReject! : null)
                    : buttonView(cancelSvg, AppTheme.custChangeStatusLight,
                        AppTheme.colorWhite, null),
                const SizedBox(
                  width: Constant.MEDIUM_PADDING,
                ),
                viewBtnDisable == false
                    ? buttonView(fileDownloadSvg, AppTheme.colorAccent,
                        AppTheme.colorWhite, widget.onTapUpload)
                    : buttonView(
                        fileDownloadSvg,
                        AppTheme.custChangeStatusLight,
                        AppTheme.colorWhite,
                        null),
                const SizedBox(
                  width: Constant.MEDIUM_PADDING,
                ),
                viewBtnDisable == false
                    ? buttonView(pdfSvg, AppTheme.colorAccent,
                        AppTheme.colorWhite, widget.onTapView)
                    : buttonView(pdfSvg, AppTheme.custChangeStatusLight,
                        AppTheme.colorWhite, null),
                const SizedBox(
                  width: Constant.MEDIUM_PADDING,
                ),
                buttonView(statusSvg, replaceColor, AppTheme.colorWhite,
                    replaceBtnDisable == true ? widget.onTapReplace : null),
                const SizedBox(
                  width: Constant.MEDIUM_PADDING,
                ),
                buttonView(editSvg, editColor, AppTheme.colorWhite,
                    editBtnDisable == false ? widget.onTapEdit! : null),
                const SizedBox(
                  width: Constant.MEDIUM_PADDING,
                ),
              ],
            ),
            const SizedBox(
              height: Constant.MEDIUM_PADDING,
            ),
            Row(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                buttonView(
                    deleteSvg,
                    removeColor,
                    // widget.item.status!
                    //         .equalsIgnoreCase(Constant.ACTIVE.toUpperCase())
                    //     ? widget.item.status!.equalsIgnoreCase(
                    //             Constant.PENDING.toUpperCase())
                    //         ? widget.item.status!.equalsIgnoreCase(
                    //                 Constant.REJECTED.toUpperCase())
                    //             ? AppTheme.colorAccent
                    //             : AppTheme.colorAccent
                    //         : AppTheme.colorAccent
                    //     : AppTheme.custChangeStatusLight,
                    AppTheme.colorWhite,
                    removeBtnDisable == false
                        ? widget.onTapDeleteInventory!
                        : null),
                const SizedBox(
                  width: Constant.MEDIUM_PADDING,
                ),
                buttonView(
                    checkSvg,
                    removeInventoryColor,
                    AppTheme.colorWhite,
                    approveRemoveInventoryBtnDisable == true
                        ? widget.onTapApproveRemoveInventory
                        : null),
                const SizedBox(
                  width: Constant.MEDIUM_PADDING,
                ),
                buttonView(
                    cancelSvg,
                    removeInventoryColor,
                    AppTheme.colorWhite,
                    rejectRemoveInventoryBtnDisable == true
                        ? widget.onTapRejectRemoveInventory
                        : null),
                const SizedBox(
                  width: Constant.MEDIUM_PADDING,
                ),
                buttonView(pdfSvg, downloadColor, AppTheme.colorWhite,
                    downloadBtnDisable == true ? widget.onTapDownload : null),
                const SizedBox(
                  width: Constant.MEDIUM_PADDING,
                ),
                buttonView(assignInventorySvg, AppTheme.colorAccent,
                    AppTheme.colorWhite, widget.onTapApproveProgress!),
                const SizedBox(
                  width: Constant.MEDIUM_PADDING,
                ),
                if (!widget.controller.isCustCaf && widget.controller.nmsEnable
                    // && widget.controller.fiberHomeEnable
                    )
                  Row(
                    children: [
                      buttonView(wifiConfig, AppTheme.colorAccent,
                          AppTheme.colorWhite, widget.onTapWifiConfig!),
                      const SizedBox(
                        width: Constant.MEDIUM_PADDING,
                      ),
                    ],
                  )
              ],
            ),
            const SizedBox(height: Constant.SMALL_PADDING),
          ],
        ),
      ),
    );
  }
}

isDisableReplace(CustomerInventoryDataList? item,
    List<CustomerPlanServiceDetail>? serviceList) {
  CustomerPlanServiceDetail? service = serviceList!.firstWhereOrNull(
      (element) => element.connectionNo == item!.connectionNo);
  if (service != null &&
      (service.custPlanStatus!.toLowerCase().equalsIgnoreCase("active") ||
          service.custPlanStatus!.toLowerCase().equalsIgnoreCase("ingrace"))) {
    return false;
  } else {
    return true;
  }
}

isDisableRemove(CustomerInventoryDataList? item,
    List<CustomerPlanServiceDetail>? serviceList) {
  CustomerPlanServiceDetail? service = serviceList!
      .firstWhere((element) => element.connectionNo == item!.connectionNo);

  if (service != null &&
      (service.custPlanStatus!.toLowerCase().equalsIgnoreCase("inactive") ||
          service.custPlanStatus!.toLowerCase().equalsIgnoreCase("disable") ||
          service.custPlanStatus!.toLowerCase().equalsIgnoreCase("suspend") ||
          service.custPlanStatus!.toLowerCase().equalsIgnoreCase("stop") ||
          service.custPlanStatus!
              .toLowerCase()
              .equalsIgnoreCase("terminate"))) {
    return false;
  } else {
    return true;
  }
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
