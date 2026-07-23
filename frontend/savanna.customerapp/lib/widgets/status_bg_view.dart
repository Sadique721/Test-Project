import 'package:savbill/util/constant.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class statusBgView extends StatelessWidget {
  String status;
  Color bgColor;
  Color textColor;
  double fontSize;
  FontWeight fontWeight;

  statusBgView({
    Key? key,
    required this.status,
    required this.bgColor,
    required this.textColor,
    required this.fontSize,
    required this.fontWeight,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
          color: bgColor),
      child: Padding(
        padding: const EdgeInsets.symmetric(
            horizontal: Constant.STATUS_PADDING_TB,
            vertical: Constant.STATUS_PADDING_LR),
        child: CustomText(
          title: status,
          colors: textColor,
          fontSize: fontSize,
          fontWeight: fontWeight,
        ),
      ),
    );
  }
}
