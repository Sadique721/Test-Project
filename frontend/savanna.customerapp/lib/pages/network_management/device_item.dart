import 'package:savbill/pages/network_management/model/response/device_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

class DeviceItem extends StatelessWidget {
  DeviceDetail item;

  final Function()? onTapPortBind;
  final Function()? onTapUpdateLocation;
  final Function()? onTapDelete;
  final Function()? onTapDetail;

  DeviceItem(
      {Key? key,
      required this.item,
      this.onTapPortBind,
      this.onTapUpdateLocation,
      this.onTapDelete,
      this.onTapDetail})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
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
            child: basicDetailItem(
              Strings.name,
              (item.name != null && item.name!.isNotEmpty) ? item.name! : "-",
              Strings.device_type,
              (item.devicetype != null && item.devicetype!.isNotEmpty)
                  ? item.devicetype!
                  : "-",
            ),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: titleWidget(Strings.status),
          ),
          const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
          Container(
            margin: const EdgeInsets.symmetric(
                horizontal: Constant.VERY_SMALL_PADDING),
            padding: const EdgeInsets.symmetric(
                horizontal: Constant.SMALL_PADDING,
                vertical: Constant.VERY_SMALL_PADDING),
            decoration: BoxDecoration(
              color: item.status!.equalsIgnoreCase(Strings.active)
                  ? AppTheme.colorGreen
                  : AppTheme.colorRed,
              borderRadius: const BorderRadius.all(Radius.circular(20)),
            ),
            child: CustomText(
              title: (item.status != null && item.status!.isNotEmpty)
                  ? "${item.status}"
                  : "-",
              colors:
                  AppTheme.colorWhite,
              textAlign: TextAlign.start,
              fontSize: AppTheme.small,
              fontWeight: FontWeight.normal,
              maxLines: 1,
            ),
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: Row(mainAxisAlignment: MainAxisAlignment.end, children: [
              buttonView(bindPortSvg, AppTheme.custEditLight,
                  AppTheme.custEditDark, onTapPortBind!),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              buttonView(locationUpdateSvg, AppTheme.custUploadFileLight,
                  AppTheme.custUploadFileDark, onTapUpdateLocation!),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              buttonView(deleteSvg, AppTheme.custDeleteLight,
                  AppTheme.custDeleteDark, onTapDelete!),
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
          child: InkWell(
            onTap: title1.equalsIgnoreCase(Strings.name) ? onTapDetail : null,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                titleWidget(title1),
                const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                valueWidget(
                    value1,
                    title1.equalsIgnoreCase(Strings.name)
                        ? AppTheme.colorPrimary
                        : AppTheme.title_dark),
              ],
            ),
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
              valueWidget(value2, AppTheme.title_dark),
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
}
