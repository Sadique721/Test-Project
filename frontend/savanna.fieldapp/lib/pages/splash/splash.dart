import 'package:savbill/pages/splash/splash_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/widgets/no_appbar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class SplashScreen extends StatefulWidget {
   const SplashScreen({Key? key}) : super(key: key);

  @override
  _SplashScreenState createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> {
  final splshController = Get.put(SplashController());

  @override
  void initState() {
    super.initState();
    Future.delayed(const Duration(seconds: 1), () {
      splshController.getUserData();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Stack(children: <Widget>[
      Container(
        // color: AppTheme.colorWhite,
        width: MediaQuery.of(context).size.width,
        height: MediaQuery.of(context).size.height,
        child: Align(
          alignment: Alignment.center,
          child: Image.asset(
            splashBg,
            // height: MediaQuery.of(context).size.height,
            // width: MediaQuery.of(context).size.width,
            fit: BoxFit.contain,
          ),
        ),
      ),
      Scaffold(
        backgroundColor: AppTheme.colorTransparent,
        appBar: NoAppBar(),
        body: SafeArea(
          child: _body(),
        ),
      )
    ]);
  }

  _body() {
    return Center(
      child: SingleChildScrollView(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // Container(
            //   margin: const EdgeInsets.symmetric(
            //     horizontal: Constant.SCREEN_PADDING * 4,
            //   ),
            //   child: Image.asset(
            //     adoptLogo,
            //     width: MediaQuery.of(context).size.width,
            //     color: AppTheme.colorWhite,
            //   ),
            // ),
          ],
        ),
      ),
    );
  }
}
