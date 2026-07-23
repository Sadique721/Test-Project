import 'package:savbill/theme/app_theme.dart';
import 'package:flutter/material.dart';

ThemeData lightTheme = ThemeData.light().copyWith(
  primaryColor: AppTheme.colorPrimaryTheme,
  visualDensity: VisualDensity.adaptivePlatformDensity,

  appBarTheme: AppBarTheme(
    color: AppTheme.colorPrimaryTheme,
    elevation: 0,
    toolbarTextStyle:ThemeData.light().textTheme.headlineSmall!,
    titleTextStyle: TextStyle(fontSize: 20,color: AppTheme.colorWhite),
    // textTheme: TextTheme(
    //   headline6: TextStyle(
    //     color: AppTheme.colorWhite,
    //     fontSize: 20,
    //   ),
    // ),
  ), colorScheme: ColorScheme.fromSwatch().copyWith(secondary: AppTheme.colorAccentTheme),
);
