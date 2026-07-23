import 'package:savbill/pages/pending_approvals/model/response/approval_pending_customer_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

class PACustomerItem extends StatelessWidget {
  ApprovalPendingCustomer item;


  final Function()? onTapApprove;
  final Function()? onTapReject;
  final Function()? onTapName;

  PACustomerItem({
    Key? key,
    required this.item,
    this.onTapApprove,
    this.onTapReject,
    this.onTapName,
  }) : super(key: key);


  @override
  Widget build(BuildContext context) {
    String name = "", mobile = "";
    List backgroundColorArr = [
      AppTheme.colorGreenRoundView,
      AppTheme.colorRedRoundView,
      AppTheme.colorBlueRoundView,
      AppTheme.colorYellowRoundView
    ];
    List textColorArr = [
      AppTheme.colorGreenRView,
      AppTheme.colorRedRView,
      AppTheme.colorBlueRView,
      AppTheme.colorYellowRView
    ];
    if (item.firstname != null && item.firstname!.isNotEmpty) {
      name = item.firstname!;
      if (item.lastname != null && item.lastname!.isNotEmpty) {
        name = "$name ${item.lastname!}";
      }
    }

    if (item.countryCode != null && item.countryCode!.isNotEmpty) {
      mobile = item.countryCode!;
      if (item.mobile != null && item.mobile!.isNotEmpty) {
        mobile = "$mobile ${item.mobile!}";
      }
    } else {
      if (item.mobile != null && item.mobile!.isNotEmpty) {
        mobile = item.mobile!;
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
          // Padding(
          //   padding:
          //       const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
          //   child: basicDetailItem(
          //       Strings.name,
          //       (name.isNotEmpty) ? name : "-",
          //       Strings.username,
          //       (item.username != null && item.username!.isNotEmpty)
          //           ? item.username
          //           : "-"),
          // ),

          Padding(
            padding: const EdgeInsets.symmetric(
                horizontal: Constant.SMALL_PADDING),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Expanded(
                  flex: 3,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      titleWidget(Strings.name,),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      InkWell(
                        onTap: onTapName,
                        child: CustomText(
                            title: (name.isNotEmpty) ? name : "-",
                            colors: AppTheme.colorPrimary,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.small + 1,
                            maxLines: 2,
                            height: 1,
                            fontWeight: FontWeight.w500),
                      ),
                    ],
                  ),
                ),
                Expanded(
                  flex: 2,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      titleWidget(Strings.username,),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      valueWidget((item.username != null && item.username!.isNotEmpty)
                                  ? item.username
                                  : "-"),
                    ],
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                Strings.mobile,
                mobile.isNotEmpty ? mobile : "-",
                Strings.create_date,
                (item.createdate != null && item.createdate!.isNotEmpty)
                    ? item.createdate
                    : "-"),
          ),
          const SizedBox(
            height: Constant.MEDIUM_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: Row(mainAxisAlignment: MainAxisAlignment.end, children: [
              buttonView(checkSvg, AppTheme.custEditLight,
                  AppTheme.custEditDark, onTapApprove!),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              buttonView(cancelSvg, AppTheme.custDeleteLight,
                  AppTheme.custDeleteDark, onTapReject!),
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
          flex: 3,
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
          flex: 2,
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
