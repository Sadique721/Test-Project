import 'package:savbill/util/hex_code.dart';
import 'package:flutter/material.dart';

import 'dark_theme.dart';
import 'light_theme.dart';

class AppTheme {
  AppTheme._();

  static ThemeData light = lightTheme;
  static ThemeData dark = darkTheme;

  static const String appFontName = 'Lufga';

  // Font Size

  static const double verySmall = 10;
  static const double small = 12;
  static const double medium = 15;
  static const double large = 18;
  static const double extraLarge = 22;
  static const double doubleExtraLarge = 24;
  static const double authTitle = 25.5;
  static const double authSubTitle = 16.5;
  static const double errorSize = 14;
  static const double errorSize2 = 16.8;

  static const double capSize = 16;
  static const double capSize1 = 14;
  static const double capSize2 = 15;
  static const double bodyText1 = 18;
  static const double bodyText2 = 18;
  static const double subtitle = 22;
  static const double title = 27;
  static const double title2 = 22;
  static const double headline = 32;
  static const double display = 42;

//
// Colors
  static Color colorPrimary = HexColor("#F7941D"); //FFB71A
  static Color colorAccent = HexColor("#F7941D");
  static Color colorStatusBar = HexColor("#6C92F4");
  static Color colorTransparent = Colors.transparent;
  static Color lightPrimary = HexColor('#F8BD0A');

  static const Color colorPrimaryTheme = Color(0xFFF7941D);
  static const Color colorAccentTheme = Color(0xFFF7941D);

  static Color colorWhite = Colors.white;
  static Color colorBlack = HexColor("#000000");
  static Color colorLightBlack = colorBlack.withOpacity(0.6);
  static Color colorIconGrey = HexColor("#999999");
  static Color colorGrey = HexColor("#8d8d8d");
  static Color colorLightGrey = HexColor("#E4E4E4");
  static Color colorDisableGray = HexColor("#ABABAB");
  static Color colorGrayTxtBg = HexColor('#E9E9E9');

  static Color colorRed = HexColor("#F4516C");
  static Color colorError = HexColor("#D32F2E");
  static Color colorGreen = HexColor("#2ED47A");

  static Color colorGreenRoundView = HexColor("#CCF6F2");
  static Color colorGreenRView = HexColor("1DC9B7");
  static Color colorBlueRoundView = HexColor("#E0F0FF");
  static Color colorBlueRView = HexColor("3A98F9");
  static Color colorRedRoundView = HexColor("#FFE0EE");
  static Color colorRedRView = HexColor("FD3A95");
  static Color colorYellowRoundView = HexColor("#FFF4DE");
  static Color colorYellowRView = HexColor("#FBA908");

  static Color colorFilterBg = HexColor("#FFF6E4");

  static Color colorYellow = HexColor("#FFFF8A");

  static Color colorYellowStart = HexColor("#F7941D");
  static Color colorYellowEnd = HexColor("#F7941D");
  static Color colorYellowBtn = HexColor("#E7A008");

  static Color colorBlackStart = HexColor("#000000");
  static Color colorBlackEnd = HexColor("#212121");

  static Color colorProgress = HexColor("#F9E8C3");
  static Color colorProgressBg = HexColor("#F7941D");

  static Color colorPositive = HexColor("#87BCBF");
  static Color colorNagative = HexColor("#D97D54");

  static Color dividerColor = HexColor("#707070");

  static Color statusUnAssignGray = HexColor('#666666');
  static Color statusAssignOrange = HexColor('#E69F08');
  static Color statusOnHold = HexColor('#F4516C');
  static Color statusClosedGreen = HexColor('#4B9D37');

  static Color statusPending = HexColor('#17A2B8');
  static Color statusApprove = HexColor('#28A745');
  static Color statusReject = HexColor('#DC3545');
  static Color buttonDisableColor = HexColor('#FCE09B');

  static Color clickLink = HexColor('#28a745');


  // static Color statusRed = HexColor('#717171');

  static Color typeIssue = HexColor('#F4516C');
  static Color typeInquiry = HexColor('#E69F08');
  static Color typeRequest = HexColor('#4B9D37');

  static Color colorBG = HexColor('#F0F3F4');
  static Color title_dark = HexColor('#334856');
  static Color lable_noramal = HexColor('#899095');
  static Color colorCardBtn = HexColor("#F6F6F6");
  static Color colorCardBg= HexColor("#FBFCFC");

  static Color useCardBg= HexColor("#DEF1C2");
  static Color unUseCardBg= HexColor("#FFE9C0");
  static Color totalCardBg= HexColor("#CAEAFF");
  static Color expantableItemBg = HexColor('#F8F8F8');

  static Color onlineStatusBg= HexColor("#28A745");
  static Color offlineStatusBg= HexColor("#EEEFF0");

  static Color custEditLight= HexColor("#E9FFE1");
  static Color custEditDark= HexColor("#61D88A");
  static Color custDeleteLight= HexColor("#FDF1EF");
  static Color custDeleteDark= HexColor("#F54535");
  static Color custUploadFileLight= HexColor("#E4F9FF");
  static Color custUploadFileDark= HexColor("#52ADF2");
  static Color custNearLocationLight= HexColor("#E6EDC4");
  static Color custNearLocationDark= HexColor("#9E9413");
  static Color custPaymentLinkLight= HexColor("#FFEBB9");
  static Color custPaymentLinkDark= HexColor("#F5B239");
  static Color custAssignInventoryLight= HexColor("#DEE0FA");
  static Color custAssignInventoryDark= HexColor("#4863C4");
  static Color custChangeStatusLight= HexColor("#FFE7D4");
  static Color custChangeStatusDark= HexColor("#EBA282");
  static Color colorWhatsCard = HexColor("#BDFBC0");
  static Color colorCardWhiteBtn = HexColor("#FBE7E2");

  // static TextTheme textTheme = const TextTheme(
  //   headline4: display1,
  //   headline5: headline1,
  //   headline6: title1,
  //   subtitle2: subtitle1,
  //   bodyText2: body2,
  //   bodyText1: body1,
  //   caption: caption,
  // );
  static TextTheme textTheme = const TextTheme(
    headlineLarge: display1,
    headlineMedium: headline1,
    headlineSmall: title1,
    titleMedium: subtitle1,
    bodyMedium: body2,
    bodyLarge: body1,
    bodySmall: caption,
  );

  static TextStyle textStyle(
      {fontWeight = FontWeight.normal,
      fontSize = medium,
      color = Colors.black}) {
    return TextStyle(
        fontFamily: appFontName,
        fontWeight: fontWeight,
        fontSize: fontSize,
        color: color,
        height: 1);
  }

  static const TextStyle display1 = TextStyle(
    // h4 -> display1
    fontFamily: appFontName,
    fontWeight: FontWeight.normal,
    fontSize: display,
  );

  static const TextStyle headline1 = TextStyle(
    // h5 -> headline
    fontFamily: appFontName,
    fontWeight: FontWeight.normal,
    fontSize: headline,
  );

  static const TextStyle title1 = TextStyle(
    // h6 -> title
    fontFamily: appFontName,
    fontWeight: FontWeight.normal,
    fontSize: title,
  );

  static const TextStyle subtitle1 = TextStyle(
    fontFamily: appFontName,
    fontWeight: FontWeight.normal,
    fontSize: subtitle,
  );

  static const TextStyle body2 = TextStyle(
    fontFamily: appFontName,
    fontWeight: FontWeight.bold,
    fontSize: bodyText2,
  );

  static const TextStyle body1 = TextStyle(
    fontFamily: appFontName,
    fontWeight: FontWeight.normal,
    fontSize: bodyText1,
  );

  static const TextStyle caption = TextStyle(
    // Caption -> caption
    fontFamily: appFontName,
    fontWeight: FontWeight.normal,
    fontSize: capSize1,
  );

  // dropdown text style
  static TextStyle dropdownTextStyle = TextStyle(
    color: AppTheme.colorBlack,
    fontSize: AppTheme.medium,
    fontWeight: FontWeight.w400,
    height: 1,
    fontFamily: AppTheme.appFontName,
    decoration: TextDecoration.none,
  );

  // dropdown error style
  static TextStyle dropdownErrorStyle = TextStyle(
    color: AppTheme.colorError,
    fontWeight: FontWeight.normal,
    fontSize: AppTheme.errorSize2,
  );

  // dropdown label style
  static TextStyle dropdownLabelStyle = TextStyle(
    color: AppTheme.colorBlack,
    fontSize: AppTheme.medium,
    fontWeight: FontWeight.normal,
    height: 1,
    fontFamily: AppTheme.appFontName,
    decoration: TextDecoration.none,
  );

  // dropdown hint style
  static TextStyle dropdownHintStyle = TextStyle(
    fontSize: AppTheme.small,
    fontWeight: FontWeight.normal,
    height: 1,
    color: AppTheme.colorIconGrey,
    fontFamily: AppTheme.appFontName,
    decoration: TextDecoration.none,
  );
}
