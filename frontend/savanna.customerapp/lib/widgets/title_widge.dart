import 'package:savbill/theme/app_theme.dart';
import 'package:flutter/material.dart';

class InputTitleRequire extends StatelessWidget {
  String title;
  bool require;
  Color? colorValue;

  InputTitleRequire({
    super.key,
    required this.title,
    required this.require,
    this.colorValue,
  });

  @override
  Widget build(BuildContext context) {
    return RichText(
      maxLines: 2,
      softWrap: true,
      textAlign: TextAlign.start,
      text: TextSpan(
        text: title,
        style: TextStyle(
          fontWeight: FontWeight.normal,
          fontSize: AppTheme.small,
          color: colorValue ?? AppTheme.title_dark,
        ),
        children: [
          if (require)
            const TextSpan(
              text: " *",
              style: TextStyle(
                fontSize: AppTheme.small,
                fontWeight: FontWeight.w600,
                color: Colors.red,
              ),
            ),
        ],
      ),
    );
    /*return Row(
      children: [
        CustomText(
          title: title,
          colors: AppTheme.title_dark,
          textAlign: TextAlign.start,
          fontSize: AppTheme.small,
          fontWeight: FontWeight.normal,
        ),
        require
            ? CustomText(
                title: " *",
                colors: Colors.red,
                textAlign: TextAlign.start,
                fontSize: AppTheme.small,
                fontWeight: FontWeight.w600,
              )
            : Container(),
      ],
    );*/
  }
}
