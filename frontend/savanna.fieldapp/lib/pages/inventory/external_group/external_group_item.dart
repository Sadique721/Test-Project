import 'package:savbill/pages/inventory/module/response/external_group_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

class ExternalGroupItem extends StatelessWidget {
  ExternalGroupDetail item;
  int index;

  final Function()? onTapEdit;
  final Function()? onTapDelete;
  final Function()? onTapMacMap;
  final Function()? onTapMacMapView;
  final Function()? onTapApprove;
  final Function()? onTapReject;
  final Function()? onTapExternalDetails;

  ExternalGroupItem(
      {Key? key,
      required this.index,
      required this.item,
      this.onTapEdit,
      this.onTapDelete,
      this.onTapMacMap,
      this.onTapMacMapView,
      this.onTapApprove,
      this.onTapReject,
      this.onTapExternalDetails
      })
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    String pName = "", type = "";
    Color approvalStatus = AppTheme.colorGrey;
    bool showStatusBtn = false, showEditDeleteBtn = false, showMacMappingBtn=false;

    if (item.productId != null &&
        item.productId!.name != null &&
        item.productId!.name!.isNotEmpty) {
      pName = item.productId!.name!;
    }
    if (item.ownershipType != null && item.ownershipType!.isNotEmpty) {
      type = item.ownershipType!;
    }

    if (item.approvalStatus != null && item.approvalStatus!.isNotEmpty) {
      if (item.approvalStatus!.equalsIgnoreCase(Strings.pending)) {
        approvalStatus = AppTheme.colorGrey;
      } else if (item.approvalStatus!.equalsIgnoreCase(Strings.approve)) {
        approvalStatus = AppTheme.statusClosedGreen;
      } else if (item.approvalStatus!.equalsIgnoreCase(Strings.rejected)) {
        approvalStatus = AppTheme.statusReject;
      }
      if (item.approvalStatus!.equalsIgnoreCase(Strings.pending) &&
          (item.inTransitQty != null && item.inTransitQty != 0) &&
          (item.totalMacSerial != null && item.totalMacSerial != 0) &&
          (item.inTransitQty == item.totalMacSerial)) {
        showStatusBtn = true;
      }

      if (item.approvalStatus!.equalsIgnoreCase(Strings.pending)) {
        showEditDeleteBtn = true;
        showMacMappingBtn = true;
      }else{
        showEditDeleteBtn = false;
        showMacMappingBtn = false;
      }
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
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Expanded(
                    child: InkWell(
                      onTap: onTapExternalDetails,
                      child: CustomText(
                          title: item.externalItemGroupNumber!,
                          colors: AppTheme.colorPrimary,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.small,
                          maxLines: 2,
                          height: 1,
                          fontWeight: FontWeight.w500),
                    )),
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: Constant.VERY_SMALL_PADDING,vertical: Constant.VERY_SMALL_PADDING),
                  child: Container(
                    padding: const EdgeInsets.symmetric(
                        horizontal:
                        Constant.SMALL_PADDING,
                        vertical: Constant
                            .VERY_SMALL_PADDING),
                    decoration: BoxDecoration(
                      borderRadius:
                      BorderRadius.circular(Constant.LARGE_PADDING),
                      color: (item.status != null &&
                          item.status!.isNotEmpty &&
                          item.status!.equalsIgnoreCase(Strings.active))
                          ? AppTheme.statusClosedGreen
                          : AppTheme.statusReject,),
                    child: CustomText(
                        title: (item.status != null &&
                            item.status!.isNotEmpty &&
                            item.status!.equalsIgnoreCase(Strings.active))
                            ? Strings.active
                            : Strings.in_active,
                        colors: AppTheme.colorWhite,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.small,
                        maxLines: 2,
                        height: 1,
                        fontWeight: FontWeight.w500),
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: Constant.VERY_SMALL_PADDING,vertical: Constant.VERY_SMALL_PADDING),
                  child: Container(
                    padding: const EdgeInsets.symmetric(
                        horizontal:
                        Constant.SMALL_PADDING,
                        vertical: Constant
                            .VERY_SMALL_PADDING),
                    decoration: BoxDecoration(
                      borderRadius:
                      BorderRadius.circular(Constant.LARGE_PADDING),
                      color: (item.approvalStatus != null &&
                          item.approvalStatus!.isNotEmpty &&
                          item.approvalStatus!.equalsIgnoreCase(Strings.approve))
                          ? AppTheme.statusClosedGreen
                          : AppTheme.colorGrey,),
                    child: CustomText(
                        title: (item.approvalStatus != null &&
                            item.approvalStatus!.isNotEmpty)
                            ? item.approvalStatus
                            : "",
                        colors:  AppTheme.colorWhite,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.small,
                        maxLines: 2,
                        height: 1,
                        fontWeight: FontWeight.w500),
                  ),
                ),

                /*Column(
                    crossAxisAlignment: CrossAxisAlignment.end,
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      CustomText(
                          title: (item.status != null &&
                                  item.status!.equalsIgnoreCase(Strings.active))
                              ? Strings.active
                              : Strings.in_active,
                          colors: (item.status != null &&
                                  item.status!.equalsIgnoreCase(Strings.active))
                              ? AppTheme.statusClosedGreen
                              : AppTheme.statusReject,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.small,
                          maxLines: 2,
                          height: 1,
                          fontWeight: FontWeight.w500),
                      (item.approvalStatus != null &&
                              item.approvalStatus!.isNotEmpty)
                          ? const SizedBox(
                              height: 1.5,
                            )
                          : Container(),
                      (item.approvalStatus != null &&
                              item.approvalStatus!.isNotEmpty)
                          ? CustomText(
                              title: (item.approvalStatus != null &&
                                      item.approvalStatus!.isNotEmpty)
                                  ? item.approvalStatus
                                  : "",
                              colors: approvalStatus,
                              textAlign: TextAlign.start,
                              fontSize: AppTheme.small,
                              maxLines: 2,
                              height: 1,
                              fontWeight: FontWeight.w500)
                          : Container(),
                    ]),*/
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
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                Strings.product,
                pName.isNotEmpty ? pName : "-",
                Strings.type,
                (type.isNotEmpty) ? type : "-"),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                Strings.qty,
                item.qty != null ? item.qty!.toString() : "-",
                Strings.aval_qty,
                item.unusedQty != null ? item.unusedQty!.toString() : "-"),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                Strings.in_transit_qty,
                item.inTransitQty != null ? item.inTransitQty!.toString() : "-",
                "",
                ""),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: Row(mainAxisAlignment: MainAxisAlignment.end, children: [
              showEditDeleteBtn
                  ? buttonView(editSvg, AppTheme.custEditLight,
                      AppTheme.custEditDark, onTapEdit!)
                  : Container(),
              showEditDeleteBtn
                  ? const SizedBox(
                      width: Constant.SMALL_PADDING,
                    )
                  : Container(),
              showEditDeleteBtn
                  ? buttonView(deleteSvg, AppTheme.custDeleteLight,
                      AppTheme.custDeleteDark, onTapDelete!)
                  : Container(),
              showEditDeleteBtn
                  ? const SizedBox(
                      width: Constant.SMALL_PADDING,
                    )
                  : Container(),
              showMacMappingBtn? buttonView(macMapSvg, AppTheme.custPaymentLinkLight,
                  AppTheme.custPaymentLinkDark, onTapMacMap!): Container(),
              showMacMappingBtn ? const SizedBox(
                width: Constant.SMALL_PADDING,
              ): Container(),
              buttonView(statusSvg, AppTheme.custChangeStatusLight,
                  AppTheme.custChangeStatusDark, onTapMacMapView!),
              showStatusBtn
                  ? const SizedBox(
                      width: Constant.SMALL_PADDING,
                    )
                  : Container(),
              showStatusBtn
                  ? buttonView(checkSvg, AppTheme.custEditLight,
                      AppTheme.custEditDark, onTapApprove!)
                  : Container(),
              showStatusBtn
                  ? const SizedBox(
                      width: Constant.SMALL_PADDING,
                    )
                  : Container(),
              showStatusBtn
                  ? buttonView(cancelSvg, AppTheme.custDeleteLight,
                      AppTheme.custDeleteDark, onTapReject!)
                  : Container(),
            ]),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
        ]),
      ),
    );
  }

  buttonView(String btnName, Color bgColor, Color txtColor, Function() onTap) {
    return InkWell(
      onTap: onTap,
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
