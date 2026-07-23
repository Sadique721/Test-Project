import 'package:savbill/pages/inventory/module/response/view_outward_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:intl/intl.dart';

class OutwardsItem extends StatelessWidget {
  OutwardDetail item;
  int index;

  final Function()? onTapEdit;
  final Function()? onTapDelete;
  final Function()? onTapMacMap;
  final Function()? onTapMacMapView;
  final Function()? onTapOutWardDetail;

  OutwardsItem({
    Key? key,
    required this.index,
    required this.item,
    this.onTapEdit,
    this.onTapDelete,
    this.onTapMacMap,
    this.onTapMacMapView,
    this.onTapOutWardDetail,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    String pName = "", outwardsDate = "";
    Color approvalStatus = AppTheme.colorGrey;
    bool isEditShowBtn = true,isAddMacAssignOutwardBtn = true,isShowMacAddressBtn = true;
    if (item.productId != null &&
        item.productId!.name != null &&
        item.productId!.name!.isNotEmpty) {
      pName = item.productId!.name!;
    }

    if (item.outwardDateTime != null && item.outwardDateTime!.isNotEmpty) {
      DateTime date = DateFormat(Constant.DATE_TIME_FORMAT_API)
          .parse(item.outwardDateTime!);
      outwardsDate =
          DateFormat("${Constant.DATE_FORMAT} ${Constant.APP_TIME_FORMAT}")
              .format(date);
    }


    if (item.approvalStatus != null && item.approvalStatus!.isNotEmpty) {
      if (item.approvalStatus!.equalsIgnoreCase(Strings.pending)) {
        approvalStatus = AppTheme.colorGrey;
      } else if (item.approvalStatus!.equalsIgnoreCase(Strings.approve)) {
        approvalStatus = AppTheme.statusClosedGreen;
      } else if (item.approvalStatus!.equalsIgnoreCase(Strings.rejected)) {
        approvalStatus = AppTheme.statusReject;
      }


      if (item.approvalStatus != Strings.pending) {
        isEditShowBtn = false;
      }

      if(item.approvalStatus!.equalsIgnoreCase(Strings.rejected) ||
          item.approvalStatus!.equalsIgnoreCase(Strings.approve) ||
      item.status!.equalsIgnoreCase(Strings.in_active.toUpperCase())){
        isAddMacAssignOutwardBtn = false;
      }

      if(item.approvalStatus!.equalsIgnoreCase(Strings.rejected)){
        isShowMacAddressBtn = false;
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
                      onTap: onTapOutWardDetail,
                      child: CustomText(
                          title: item.outwardNumber!,
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

               /* Column(
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
                  ],
                ),*/
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
                Strings.outward_date,
                outwardsDate.isNotEmpty ? outwardsDate : "-"),
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
                Strings.in_transit_qty,
                (item.inTransitQty != null)
                    ? item.inTransitQty!.toString()
                    : "-"),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
            const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                Strings.create_by,
                (item.createdBy != null && item.createdBy! .isNotEmpty) ? item.createdBy!.toString() : "-",
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

              /*buttonView(
                  editSvg,
                  (item.approvalStatus != null &&
                          item.approvalStatus!.isNotEmpty &&
                          item.approvalStatus!
                              .equalsIgnoreCase(Strings.approve))
                      ? AppTheme.colorDisableGray
                      : AppTheme.custEditLight,
                  (item.approvalStatus != null &&
                          item.approvalStatus!.isNotEmpty &&
                          item.approvalStatus!
                              .equalsIgnoreCase(Strings.approve))
                      ? AppTheme.colorWhite
                      : AppTheme.custEditDark,
                  onTapEdit!),*/

              isEditShowBtn? buttonView(
                  editSvg,
                  AppTheme.custEditLight,
                  AppTheme.custEditDark,
                  onTapEdit!) :
              buttonView(
                  editSvg,
                  AppTheme.colorDisableGray,
                  AppTheme.colorWhite,
                  null),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),


              isEditShowBtn? buttonView(
                  deleteSvg,
                  AppTheme.colorDisableGray,
                  AppTheme.colorWhite,
                  null) :
              buttonView(
                  deleteSvg,
                  AppTheme.colorDisableGray,
                  AppTheme.colorWhite,
                  null),
              // buttonView(
              //     deleteSvg,
              //     (item.approvalStatus != null &&
              //             item.approvalStatus!.isNotEmpty &&
              //             item.approvalStatus!
              //                 .equalsIgnoreCase(Strings.approve))
              //         ? AppTheme.colorDisableGray
              //         : AppTheme.custDeleteLight,
              //     (item.approvalStatus != null &&
              //             item.approvalStatus!.isNotEmpty &&
              //             item.approvalStatus!
              //                 .equalsIgnoreCase(Strings.approve))
              //         ? AppTheme.colorWhite
              //         : AppTheme.custDeleteDark,
              //     onTapDelete!),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              isAddMacAssignOutwardBtn ?
              buttonView(macMapSvg, AppTheme.custPaymentLinkLight,
                  AppTheme.custPaymentLinkDark, onTapMacMap!) :
              buttonView(macMapSvg, AppTheme.colorDisableGray,
                  AppTheme.colorWhite, null),

              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              isShowMacAddressBtn ?
              buttonView(statusSvg, AppTheme.custPaymentLinkLight,
                  AppTheme.custPaymentLinkDark, onTapMacMapView!) :
              buttonView(statusSvg, AppTheme.colorDisableGray,
                  AppTheme.colorWhite, null),
            ]),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
        ]),
      ),
    );
  }

  buttonView(String btnName, Color bgColor, Color txtColor, Function()? onTap) {
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
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisAlignment: MainAxisAlignment.start,
      children: [
        Expanded(
          flex: 2,
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
