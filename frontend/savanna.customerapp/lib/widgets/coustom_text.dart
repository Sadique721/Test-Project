import 'package:savbill/theme/app_theme.dart';
import 'package:flutter/material.dart';

class CustomText extends StatefulWidget {
  var title;
  Color colors;
  double fontSize;
  double height;
  FontWeight fontWeight;
  TextAlign textAlign;
  int? maxLines;
  TextDecoration? decoration;
  TextOverflow? overflow;


  CustomText({
    Key? key,
    required this.title,
    this.colors = Colors.white,
    this.fontSize = AppTheme.medium,
    this.height = 1,
    this.fontWeight = FontWeight.normal,
    this.textAlign = TextAlign.center,
    this.maxLines,
    this.decoration,
    this.overflow,
  }) : super(key: key);

  @override
  State<CustomText> createState() => _CustomTextState();
}

class _CustomTextState extends State<CustomText> {
  @override
  Widget build(BuildContext context) {
    return Text(
      widget.title.toString(),
      textAlign: widget.textAlign,
      overflow: widget.overflow,
      style: TextStyle(
        color: widget.colors,
        fontSize: widget.fontSize,
        fontWeight: widget.fontWeight,
        height: widget.height,
        decoration: widget.decoration ?? null,
      ),
        textScaleFactor: 1,
      softWrap: true,
      maxLines: widget.maxLines,
    );
  }
}
