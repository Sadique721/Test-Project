import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

class CustInventoryDocItemView extends StatelessWidget {
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
  dynamic item;
  int index;

  final Function()? onTapDownload;
  final Function()? onTapView;
  final Function()? onTapDelete;

  CustInventoryDocItemView({
    Key? key,
    required this.index,
    required this.item,
    this.onTapDownload,
    this.onTapView,
    this.onTapDelete,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(
        top: Constant.SMALL_PADDING,
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
            Row(
                mainAxisAlignment: MainAxisAlignment.start,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
              Expanded(
                child: Padding(
                  padding: const EdgeInsets.symmetric(
                      horizontal: Constant.SMALL_PADDING),
                  child: CustomText(
                      title: item['filename']!,
                      colors: AppTheme.title_dark,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.medium + 1,
                      maxLines: 2,
                      height: 1,
                      fontWeight: FontWeight.w500),
                ),
              ),
              buttonView(pdfSvg, AppTheme.colorPrimary, AppTheme.colorWhite,
                  onTapDownload!),
              SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              buttonView(eyePasswordSvg, AppTheme.colorPrimary,
                  AppTheme.colorWhite, onTapView!),
              SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              buttonView(deleteSvg, AppTheme.colorPrimary, AppTheme.colorWhite,
                  onTapDelete!),
              SizedBox(
                width: Constant.SMALL_PADDING,
              ),
            ]),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
          ],
        ),
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

  cardDataRow(String? label, String? value) {
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
                title: value!.isNotEmpty ? value : "-",
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
}
