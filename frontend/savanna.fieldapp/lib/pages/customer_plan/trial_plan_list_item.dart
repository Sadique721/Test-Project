import 'package:savbill/pages/dashboard/model/response/cust_plan_detail_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

class TrialPlanListViewItem extends StatelessWidget {
  CustPlanDataList item;
  int index;
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

  final Function()? onTapExtendTrial;
  final Function()? onTapNotification;
  final Function()? onTapDelete;

  TrialPlanListViewItem({Key? key, required this.index, required this.item,this.onTapExtendTrial,
  this.onTapNotification,
  this.onTapDelete})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        Card(
          margin: const EdgeInsets.only(
              top: Constant.MEDIUM_PADDING,
              left: 0,
              right: 0,
              bottom: Constant.MEDIUM_PADDING),
          elevation: 2,
          color: AppTheme.colorWhite,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              nameRow(),
              cardDataRow(Strings.plan_group, item.planGroupName ?? ""),
              line(),
              cardDataRow(Strings.validity, item.validity!.toInt().toString()),
              line(),
              cardDataRow(Strings.start_date, item.dbStartDate.toString()),
              line(),
              cardDataRow(Strings.expiry_date, item.dbExpiryDate.toString()),
              line(),

              Padding(
                padding: const EdgeInsets.all(Constant.MEDIUM_PADDING),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  crossAxisAlignment: CrossAxisAlignment.center,
                  children: [
                    const SizedBox(width: Constant.MEDIUM_PADDING),
                    buttonView(extendTrialSvg, item.custPlanStatus != "STOP" ? AppTheme.colorPrimaryTheme : AppTheme.colorGrayTxtBg, AppTheme.colorWhite,
                        item.custPlanStatus != "STOP" ? onTapExtendTrial! : (){}),
                    const SizedBox(width: Constant.SMALL_PADDING,),
                    buttonView(notificationSvg, item.custPlanStatus != "STOP" ? AppTheme.colorPrimaryTheme : AppTheme.colorGrayTxtBg, AppTheme.colorWhite,
                        item.custPlanStatus != "STOP" ? onTapNotification! : (){}),
                    const SizedBox(width: Constant.SMALL_PADDING,),
                    buttonView(deleteSvg, item.custPlanStatus != "STOP" ? AppTheme.colorPrimaryTheme : AppTheme.colorGrayTxtBg, AppTheme.colorWhite,
                        item.custPlanStatus != "STOP" ? onTapDelete! : (){}),
                  ],
                ),
              )
            ],
          ),
        ),
        Positioned(
          top: Constant.MEDIUM_PADDING,
          left: Constant.SMALL_PADDING,
          child: Container(
            width: 80,
            height: Constant.VERY_SMALL_PADDING,
            color: textColorArr[index % textColorArr.length],
          ),
        ),
      ],
    );
  }

  nameRow() {
    return Padding(
        padding: const EdgeInsets.all(Constant.MEDIUM_PADDING),
        child: Row(
          children: [
            CircleAvatar(
              backgroundColor:
              backgroundColorArr[index % backgroundColorArr.length],
              radius: 15,
              child: Text(
                !item.planName!.isNullOrEmpty()
                    ? item.planName![0].toUpperCase()
                    : "",
                style: TextStyle(
                    color: textColorArr[index % textColorArr.length],
                    fontSize: AppTheme.large,
                    fontWeight: FontWeight.bold),
              ),
            ),
            const SizedBox(width: Constant.SMALL_PADDING),
            Expanded(
                child: CustomText(
                    title: item.planName!,
                    colors: textColorArr[index % textColorArr.length],
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.medium + 1,
                    maxLines: 2,
                    height: 1,
                    fontWeight: FontWeight.w500)),
          ],
        ));
  }

  cardDataRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.all(Constant.MEDIUM_PADDING),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          CustomText(
              title: label,
              colors: AppTheme.title_dark,
              textAlign: TextAlign.start,
              fontSize: AppTheme.small + 1,
              height: 1,
              fontWeight: FontWeight.w500),
          const SizedBox(width: Constant.MEDIUM_PADDING),
          Expanded(
              child: Align(
                alignment: Alignment.topRight,
                child: CustomText(
                    title: value.isNotEmpty ? value : "-",
                    colors: AppTheme.lable_noramal,
                    textAlign: TextAlign.end,
                    fontSize: AppTheme.small,
                    height: 1,
                    fontWeight: FontWeight.w400),
              ))
        ],
      ),
    );
  }

  line() {
    return SizedBox(
      width: double.infinity,
      child: Divider(
        color: Colors.grey[300],
        height: 0.5,
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