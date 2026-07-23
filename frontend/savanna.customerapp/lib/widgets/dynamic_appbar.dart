import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_svg/flutter_svg.dart';

class DynamicAppBar extends StatelessWidget implements PreferredSizeWidget {
  final String title;
  String subTitle;
  Color colors;
  Color fontColors;
  double fontSize;
  FontWeight fontWeight;
  TextAlign textAlign;
  List<Widget> actions;
  double appBarHeight;
  bool isDrawer;
  final Function() onBackTap;

  DynamicAppBar(
    this.title,
    this.subTitle,
    this.colors,
    this.isDrawer,
    this.onBackTap,
    this.actions,
    this.appBarHeight, {
    this.fontSize = AppTheme.large,
    this.fontWeight = FontWeight.normal,
    this.textAlign = TextAlign.center,
    this.fontColors = Colors.white,
    Key? key,
  }) : super(key: key);

  @override
  Size get preferredSize => Size.fromHeight(appBarHeight);

  @override
  Widget build(BuildContext context) {
    return AppBar(
      centerTitle: true,
      title: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Text(
              title,
              textAlign: textAlign,
              textScaleFactor: 1,
              softWrap: true,
              maxLines: 1,
              style: TextStyle(
                color: fontColors,
                fontSize: fontSize,
                fontWeight: fontWeight
              ),
            ),
            Visibility(
              maintainSize: false,
              maintainAnimation: true,
              maintainState: true,
              visible: subTitle.isNullOrEmpty() ? false : true,
              child: Text(
                subTitle,
                textAlign: textAlign,
                textScaleFactor: 1,
                softWrap: true,
                maxLines: 1,
                style: TextStyle(
                  color: fontColors,
                  fontSize: AppTheme.small,
                  fontWeight: fontWeight,
                ),
              ),
            ),
          ]),
      titleSpacing: 0.00,
      leading: Builder(
        builder: (BuildContext context) {
          return IconButton(
            icon: SvgPicture.asset(
              isDrawer ? menuIconSvg : backArrowSvg,
              height: isDrawer ? Constant.MENU_ICON_SIZE : Constant.ICON_SIZE,
              width: isDrawer ? Constant.MENU_ICON_SIZE : Constant.ICON_SIZE,
              color: fontColors,
              fit: BoxFit.fill,
            ),
            onPressed: onBackTap, //() => Scaffold.of(context).openDrawer(),
          );
        },
      ),
      backgroundColor: colors,
      bottomOpacity: 0.0,
      elevation: 2.0,
      automaticallyImplyLeading: false,
      actions: actions,
      systemOverlayStyle: SystemUiOverlayStyle(
        statusBarColor: colors,
        statusBarIconBrightness: Brightness.dark, // For Android (dark icons)
        statusBarBrightness: Brightness.light, // For iOS (dark icons)
      ),
    );
  }
}


