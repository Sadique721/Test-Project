import 'dart:convert';
import 'dart:developer';
import 'dart:io';
import 'package:firebase_analytics/firebase_analytics.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/services.dart';
import 'package:savbill/pages/dashboard/model/device_info.dart';
import 'package:savbill/routes/app_pages.dart';
import 'package:savbill/routes/app_routes.dart';
import 'package:savbill/services/firebase_cloud_messaging/fcm_controller.dart';
import 'package:savbill/services/firebase_cloud_messaging/initialization.dart';
import 'package:savbill/services/notification_flutter.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:device_info_plus/device_info_plus.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart' as se;
import 'package:get/get_navigation/src/root/get_material_app.dart';
import 'package:get/get_navigation/src/routes/transitions_type.dart';
import 'package:get_storage/get_storage.dart';
import 'package:package_info_plus/package_info_plus.dart';
import 'package:permission_handler/permission_handler.dart';

import 'firebase_options.dart';

String deviceToken = '';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await GetStorage.init();
  // await Firebase.initializeApp();
  await Firebase.initializeApp(options: DefaultFirebaseOptions.currentPlatform);
  await FCMInitialization().initFCM();
  SystemChrome.setPreferredOrientations([
    DeviceOrientation.portraitUp,
  ]).then((value) {
    initFirebase();
    runApp(MyApp());
  });
}

Future<void> initFirebase() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Permission.notification.isDenied.then(
    (bool value) {
      if (value) {
        Permission.notification.request();
      }
    },
  );
}

class MyApp extends StatefulWidget {
  MyApp();

  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  NotificationServices notificationServices = NotificationServices();
  DeviceInfoPlugin deviceInfoPlugin = DeviceInfoPlugin();
  GetStorage getStorage = GetStorage();
  String? deviceName,
      deviceOsVersion,
      deviceFcmToken,
      appVersion,
      deviceId,
      osType;
  DeviceInfo? di;

  void config() {
    se.SystemChrome.setPreferredOrientations(
        [se.DeviceOrientation.portraitUp, se.DeviceOrientation.portraitDown]);
  }

  @override
  void initState() {
    super.initState();
    initPlatformState();
    notificationServices.requestNotificationPermission();
    notificationServices.firebaseInit(context);
    notificationServices.isTokenRefresh();
    notificationServices.getDeviceToken().then((value) {
      deviceToken = value;
      print("device token ");
      print(value);
    });
  }

  Future<void> initPlatformState() async {
    PackageInfo packageInfo = await PackageInfo.fromPlatform();
    setState(() {
      appVersion = packageInfo.buildNumber;
    });
    try {
      if (Platform.isAndroid) {
        _readAndroidBuildData(await deviceInfoPlugin.androidInfo);
      } else if (Platform.isIOS) {
        _readIosDeviceInfo(await deviceInfoPlugin.iosInfo);
      }
    } on se.PlatformException {
      print("Platform Error ==> Failed to get platform version.");
    }
  }

  _readAndroidBuildData(AndroidDeviceInfo build) {
    String? identifier = build.id;
    var release = build.version.release;
    var manufacturer = build.manufacturer;
    var model = build.model;

    deviceName = "${manufacturer} ${model}";
    deviceOsVersion = release;
    deviceId = identifier;
    osType = Strings.android;
    setDeviceInfo();
  }

  _readIosDeviceInfo(IosDeviceInfo data) {
    var version = data.systemVersion;
    // var model = data.model;
    var identifier = data.identifierForVendor;
    var name = data.name;

    deviceName = name;
    deviceOsVersion = version;
    deviceId = identifier;
    osType = Strings.ios;
    setDeviceInfo();
  }

  setDeviceInfo() {
    di = DeviceInfo(
      deviceId: deviceId,
      deviceType: osType,
      deviceOSV: deviceOsVersion,
      deviceName: deviceName,
      appVer: appVersion,
    );
    getStorage.write(Constant.DEVICE_INFO, jsonEncode(di));
  }

  @override
  Widget build(BuildContext context) {
    config();
    FirebaseAnalytics analytics = FirebaseAnalytics.instance;
    FirebaseAnalyticsObserver observer =
        FirebaseAnalyticsObserver(analytics: analytics);
    return GetMaterialApp(
      theme: ThemeData(
        primaryColor: AppTheme.colorPrimaryTheme,
        fontFamily: AppTheme.appFontName,
        textTheme: AppTheme.textTheme,
        appBarTheme: const AppBarTheme(
          systemOverlayStyle:
              se.SystemUiOverlayStyle(statusBarBrightness: Brightness.light),
        ),
        textSelectionTheme: const TextSelectionThemeData(
          cursorColor: AppTheme.colorPrimaryTheme,
          selectionColor: AppTheme.colorPrimaryTheme,
          selectionHandleColor: AppTheme.colorPrimaryTheme,
        ),
      ),
      defaultTransition: Transition.rightToLeft,
      title: Strings.app_name,
      initialRoute: AppRoutes.SPLASH,
      navigatorObservers: <NavigatorObserver>[observer],
      getPages: AppPages.list,
      debugShowCheckedModeBanner: false,
    );
  }
}
