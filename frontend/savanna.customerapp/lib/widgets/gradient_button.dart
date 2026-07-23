import 'package:savbill/util/constant.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class GradientButton extends StatelessWidget {
  Widget child;
  Color startColor;
  Color endColor;
  Function() onTap;
  double radius;
  double topPadding;
  final double? width;
  final double? height;
  final bool shadow;

  GradientButton({
    Key? key,
    required this.child,
    required this.startColor,
    required this.endColor,
    required this.onTap,
    this.radius = Constant.BTN_ROUNDED_CORNER,
    this.topPadding = Constant.MEDIUM_PADDING,
    this.width,
    this.height,
    this.shadow = true,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        height: (height != null) ? height : Constant.INPUT_TEXT_SHADOW_HEIGHT,
        width: (width != null) ? width : MediaQuery.of(context).size.width,
        padding: EdgeInsets.only(
          left: Constant.SMALL_PADDING,
          right: Constant.SMALL_PADDING,
          top: topPadding,
          bottom: topPadding,
        ),
        alignment: Alignment.center,
        decoration: BoxDecoration(
            borderRadius: BorderRadius.all(
              Radius.circular(radius),
            ),
            boxShadow: shadow
                ? <BoxShadow>[
                    BoxShadow(
                        color: Colors.grey.shade200,
                        offset: const Offset(2, 4),
                        blurRadius: 5,
                        spreadRadius: 2)
                  ]
                : null,
            gradient: LinearGradient(
                begin: Alignment.centerLeft,
                end: Alignment.centerRight,
                colors: [startColor, endColor])),
        child: child,
      ),
    );
  }
}
