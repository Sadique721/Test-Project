import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:flutter/material.dart';

class ProgressBar extends StatelessWidget {
  final double height;
  final double width;
  final bool isLoader;

  const ProgressBar({
    Key? key,
    this.height = double.infinity,
    this.width = double.infinity,
    required this.isLoader,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    if (isLoader) {
      return Container(
        height: height,
        width: width,
        color: Colors.black45,
        child: Center(
          child: Card(
            color: AppTheme.colorWhite,
            elevation: Constant.CARD_ELEVATION,
            shape: const RoundedRectangleBorder(
              borderRadius: BorderRadius.all(
                  Radius.circular(Constant.BTN_ROUNDED_CORNER)),
            ),
            child: Padding(
              padding: const EdgeInsets.all(Constant.EXTRA_LARGE_PADDING * 2),
              child: CircularProgressIndicator(
                strokeWidth: 2.5,
                valueColor: AlwaysStoppedAnimation<Color>(AppTheme.colorProgress),
                backgroundColor: AppTheme.colorProgressBg,
              ),
            ),
          ),
        ),
      );
    } else {
      return Container();
    }
  }
}
