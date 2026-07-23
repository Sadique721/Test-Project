
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:get/get.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:url_launcher/url_launcher_string.dart';

import '../../util/resources.dart';

class Contact extends StatefulWidget {
  @override
  _ContactState createState() => _ContactState();
}

class _ContactState extends State<Contact> {
  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: Container(
        decoration: BoxDecoration(
          color: AppTheme.colorWhite,
          // image: const DecorationImage(
          //   fit: BoxFit.cover,
          //   image: AssetImage(
          //     dashboardBgYellow,
          //   ),
          // ),
        ),
        child: Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorTransparent,
            appBar: _appBar(),
            body: _body(),
          ),
        ]),
      ),
    );
  }

  _body() {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: SingleChildScrollView(
        child: Container(
          width: MediaQuery.of(context).size.width,
          padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
          child: Column(
            children: [
              Card(
                margin: const EdgeInsets.only(
                    top: Constant.MEDIUM_PADDING,
                    bottom: Constant.MEDIUM_PADDING),
                elevation: 4,
                shadowColor: AppTheme.colorPrimary,
                color: AppTheme.colorWhite,
                child: Container(
                  decoration: BoxDecoration(
                    gradient: LinearGradient(
                        begin: Alignment.topLeft,
                        end: Alignment.bottomRight,
                        colors: <Color>[
                          AppTheme.colorPrimary.withOpacity(0.7),
                          AppTheme.colorWhite
                        ]),
                    borderRadius: BorderRadius.circular(Constant.SMALL_PADDING),
                    // color: AppTheme.colorWhite,
                    // boxShadow: [
                    //   BoxShadow(
                    //     color: AppTheme.colorPrimary.withOpacity(1),
                    //     blurRadius: 5,
                    //     offset: Offset(4, 5),
                    //   )
                    // ],
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const SizedBox(height: Constant.MEDIUM_PADDING),
                      Padding(
                        padding: const EdgeInsets.only(
                            left: Constant.MEDIUM_PADDING),
                        child: InkWell(
                          onTap: () => _launchURL(
                              "https://savannafibre.co.tz/about.html"),
                          child: CustomText(
                            title: Strings.here_to_help,
                            colors: AppTheme.title_dark,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.large,
                            height: 1,
                            fontWeight: FontWeight.w700,
                          ),
                        ),
                      ),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      Row(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          _customIcon(FontAwesomeIcons.phone, Colors.white,
                              Colors.red),
                          const SizedBox(width: Constant.SMALL_PADDING),
                          Padding(
                            padding: const EdgeInsets.symmetric(vertical:Constant.VERY_SMALL_PADDING),
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                CustomText(
                                  title: Strings.call_us,
                                  colors: AppTheme.title_dark,
                                  overflow: TextOverflow.ellipsis,
                                  textAlign: TextAlign.start,
                                  fontSize: AppTheme.large,
                                  fontWeight: FontWeight.w800,
                                ),
                                SizedBox(
                                  height: Constant.SMALL_PADDING,
                                ),
                                CustomText(
                                  title: Strings.availablity,
                                  colors: AppTheme.title_dark,
                                  textAlign: TextAlign.start,
                                  fontSize: AppTheme.medium,
                                  fontWeight: FontWeight.w400,
                                ),
                                SizedBox(
                                  height: Constant.SMALL_PADDING,
                                ),
                                _contactDetail(Strings.vodacom, Strings.vodacom_no),
                                _contactDetail(Strings.airtel, Strings.airtel_no),
                                _contactDetail(
                                    Strings.telesales, Strings.telesales_no),
                                _contactDetail(
                                    Strings.whatsapp, Strings.whatsapp_no),
                              ],
                            ),
                          )
                        ],
                      ),
                      Row(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          _customIcon(FontAwesomeIcons.envelope, Colors.white,
                              Colors.red),
                          const SizedBox(width: Constant.SMALL_PADDING),
                          Padding(
                            padding: const EdgeInsets.symmetric(vertical:Constant.VERY_SMALL_PADDING),
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                CustomText(
                                  title: Strings.email_us,
                                  colors: AppTheme.title_dark,
                                  overflow: TextOverflow.ellipsis,
                                  textAlign: TextAlign.start,
                                  fontSize: AppTheme.large,
                                  fontWeight: FontWeight.w800,
                                ),
                                SizedBox(
                                  height: Constant.SMALL_PADDING,
                                ),
                                CustomText(
                                  title: Strings.write_an_mail,
                                  colors: AppTheme.title_dark,
                                  textAlign: TextAlign.start,
                                  fontSize: AppTheme.medium,
                                  fontWeight: FontWeight.w400,
                                ),
                                SizedBox(height: Constant.VERY_SMALL_PADDING),
                                InkWell(
                                  onTap: () {
                                    _launchMailClient(
                                        Strings.email_of_company);
                                  },
                                  child: CustomText(
                                    title: Strings.email_of_company,
                                    colors: AppTheme.title_dark,
                                    textAlign: TextAlign.start,
                                    fontSize: AppTheme.medium,
                                    fontWeight: FontWeight.w600,
                                  ),
                                ),
                              ],
                            ),
                          ),
                          const SizedBox(width: Constant.SMALL_PADDING),
                        ],
                      ),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      Row(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          _customIcon(FontAwesomeIcons.locationDot,
                              Colors.white, Colors.red),
                          const SizedBox(width: Constant.SMALL_PADDING),
                          Padding(
                            padding: const EdgeInsets.symmetric(vertical:Constant.VERY_SMALL_PADDING),
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                CustomText(
                                  title: Strings.head_office,
                                  colors: AppTheme.title_dark,
                                  overflow: TextOverflow.ellipsis,
                                  textAlign: TextAlign.start,
                                  fontSize: AppTheme.large,
                                  fontWeight: FontWeight.w800,
                                ),
                                SizedBox(
                                  height: Constant.SMALL_PADDING,
                                ),
                                CustomText(
                                  title: Strings.address_of_company,
                                  colors: AppTheme.title_dark,
                                  textAlign: TextAlign.start,
                                  fontSize: AppTheme.medium,
                                  fontWeight: FontWeight.w400,
                                ),
                              ],
                            ),
                          ),
                          const SizedBox(width: Constant.SMALL_PADDING),
                        ],
                      ),
                      SizedBox(
                        height: Constant.SMALL_PADDING,
                      ),
                    ],
                  ),
                ),
              ),
              Card(
                margin: const EdgeInsets.only(
                    top: Constant.MEDIUM_PADDING,
                    bottom: Constant.MEDIUM_PADDING),
                elevation: 4,
                shadowColor: AppTheme.colorPrimary.withOpacity(0.6),
                color: AppTheme.colorWhatsCard,
                child: Stack(
                  children: [
                  ClipRRect( borderRadius: BorderRadius.circular(12),
                    child: Image.asset(
                      contactBG,
                      // fit: BoxFit.cover,
                    ),
                  ),
                    Positioned(
                      top: 20,
                      left: 15,
                      child: CustomText(
                        title: Strings.chat_availiblity,
                        colors: AppTheme.colorBlack,
                        fontSize: AppTheme.large,
                        fontWeight: FontWeight.bold,
                      ),
                    ),


                    Positioned(
                      top: 50,
                      left: 32,
                      child: CustomText(
                        title: Strings.team_for_help,
                        colors: AppTheme.colorGrey,
                        fontSize: AppTheme.medium,
                      ),
                    ),

                    Positioned(
                      top: 70,
                      left: 32,
                      child: CustomText(
                        title: Strings.wsp_us_at,
                        colors: AppTheme.colorGrey,
                        fontSize: AppTheme.medium,
                      ),
                    ),

                    // Button Overlay
                    Positioned(
                      bottom: 10,
                      left: 10,
                      child: ElevatedButton(
                        onPressed: () {
                          _launchURL(
                              "https://api.whatsapp.com/send/?phone=%2B255699999500&text&type=phone_number&app_absent=0");
                        },
                        style: ElevatedButton.styleFrom(
                          backgroundColor: AppTheme.colorWhite,
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(25),
                          ),
                        ),
                        child: CustomText(title: "Chat Now",
                             colors: AppTheme.colorBlack,fontWeight: FontWeight.bold,),
                      ),
                    ),
                  ],
                ),
              ),
              SizedBox(
                height: Constant.MEDIUM_PADDING,
              ),
              Align(
                alignment: Alignment.bottomCenter,
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    _socialIcon(
                        FontAwesomeIcons.facebook,
                        AppTheme.colorBlueRView,
                        'https://www.facebook.com/savannafibretz'),
                    _socialIcon(FontAwesomeIcons.xTwitter, AppTheme.colorBlack,
                        'https://x.com/savannafibretz'),
                    _socialIcon(FontAwesomeIcons.instagram, AppTheme.colorGrey,
                        'https://www.instagram.com/savannafibretz/'),
                    _socialIcon(FontAwesomeIcons.youtube, AppTheme.colorRed,
                        'https://www.youtube.com/@savannafibretz'),
                  ],
                ),
              ),
              SizedBox(
                height: Constant.SMALL_PADDING,
              )
            ],
          ),
        ),
      ),
    );
  }

  Widget _socialIcon(IconData icon, Color color, String url) {
    return GestureDetector(
      onTap: () => _launchURL(url),
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 8.0),
        child: CircleAvatar(
          backgroundColor: color,
          radius: 20,
          child: FaIcon(icon, color: Colors.white, size: 20),
        ),
      ),
    );
  }

  Widget _customIcon(IconData icon, Color backgroundColor, Color iconColor) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 8.0),
      child: CircleAvatar(
        backgroundColor: AppTheme.colorWhite,
        radius: Constant.MEDIUM_PADDING,
        child: FaIcon(
          icon,
          color: AppTheme.colorRed,
          size: Constant.MEDIUM_PADDING - 1,
        ),
      ),
    );
  }

  void _launchURL(String url) async {
    final Uri uri = Uri.parse(url);
    if (!await launchUrl(uri, mode: LaunchMode.externalApplication)) {
      debugPrint("Could not launch $url");
    }
  }

  void _launchMailClient(String targetEmail) async {
    String mailUrl = 'mailto:$targetEmail';
    try {
      await launchUrlString(mailUrl);
    } catch (e) {
      await Clipboard.setData(ClipboardData(text: targetEmail));
    }
  }

  Widget _contactDetail(String label, String number) {
    return Padding(
      padding: const EdgeInsets.only(bottom: Constant.SMALL_PADDING),
      child: Row(
        children: [
          CustomText(
            title: "$label: ",
            colors: AppTheme.title_dark,
            fontSize: AppTheme.medium - 1,
            fontWeight: FontWeight.w400,
            textAlign: TextAlign.start,
          ),
          GestureDetector(
            onTap: () => _launchCaller(number),
            child: CustomText(
              title: number,
              colors: Colors.black,
              fontSize: AppTheme.medium - 1,
              fontWeight: FontWeight.w600,
              decoration: TextDecoration.underline,
              textAlign: TextAlign.start,
            ),
          ),
        ],
      ),
    );
  }

  _launchCaller(String number) async {
    final url = "tel:$number";
    if (await canLaunch(url)) {
      await launch(url);
    } else {
      throw 'Could not launch $url';
    }
  }

  _appBar() {
    return DynamicAppBar(Strings.contact, '', AppTheme.colorPrimary, false,
        _backScreen, [], AppBar().preferredSize.height);
  }
}
