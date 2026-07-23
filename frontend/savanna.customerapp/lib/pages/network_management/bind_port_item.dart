import 'package:savbill/pages/network_management/model/response/bind_port_device_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';

class BindPortItem extends StatelessWidget {
  BindPortDeviceDetail item;

  BindPortItem({Key? key, required this.item}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    String text1 = "", text2 = "";

    if (item.portType!.equalsIgnoreCase("IN")) {
      if (item.inBind != null && item.inBind!.isNotEmpty) {
        text1 = item.inBind!;
      }
      if (item.outBind != null && item.outBind!.isNotEmpty) {
        text2 = item.outBind!;
      }
    } else {
      if (item.outBind != null && item.outBind!.isNotEmpty) {
        text1 = item.outBind!;
      }
      if (item.inBind != null && item.inBind!.isNotEmpty) {
        text2 = item.inBind!;
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
            child: basicDetailItem(
              item.portType!.equalsIgnoreCase("IN")
                  ? Strings.in_port
                  : Strings.out_port,
              (text1.isNotEmpty) ? text1 : "-",
              Strings.parent_device,
              (item.parentDeviceName != null &&
                      item.parentDeviceName!.isNotEmpty)
                  ? item.parentDeviceName!
                  : "-",
            ),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                item.portType!.equalsIgnoreCase("IN")
                    ? Strings.out_port
                    : Strings.in_port,
                (text2.isNotEmpty) ? text2 : "-",
                "",
                ""),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
        ]),
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
