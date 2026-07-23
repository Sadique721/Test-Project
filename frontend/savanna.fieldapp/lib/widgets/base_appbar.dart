import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class BaseAppBar extends StatelessWidget implements PreferredSizeWidget {
  final String title;
  final AppBar appBar;
  final List<Widget>? widgets;
  final VoidCallback? onBackTap;

  /// you can add more fields that meet your needs

  const BaseAppBar(
      {Key? key,
      required this.title,
      required this.appBar,
      this.widgets,
      this.onBackTap})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return AppBar(
      centerTitle: true,
      elevation: 0,
      title: CustomText(
        title: title,
        colors: AppTheme.colorWhite,
        fontSize: AppTheme.large,
        decoration: null,
      ),
      backgroundColor: AppTheme.colorPrimary,
      actions: widgets,
      automaticallyImplyLeading: true,
      leading: IconButton(
        icon: Icon(Icons.arrow_back_ios_sharp, color: AppTheme.colorWhite),
        onPressed: onBackTap,
      ),
      iconTheme: const IconThemeData(color: Colors.white),
      actionsIconTheme: const IconThemeData(color: Colors.white),
      systemOverlayStyle: SystemUiOverlayStyle(
        statusBarColor: AppTheme.colorPrimary,
        statusBarIconBrightness: Brightness.dark, // For Android (dark icons)
        statusBarBrightness: Brightness.light, // For iOS (dark icons)
      ),
    );
  }

  @override
  Size get preferredSize => Size.fromHeight(appBar.preferredSize.height);
}
