// ignore_for_file: depend_on_referenced_packages
import 'package:path/path.dart' as path;
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'helper.dart';

class FirebaseNotificationSetting {
  FirebaseMessaging firebaseMessaging = FirebaseMessaging.instance;
  Future<NotificationSettings> notificationSetting() async {
    return await firebaseMessaging.requestPermission(
      alert: true,
      announcement: true,
      badge: true,
      carPlay: true,
      criticalAlert: true,
      provisional: true,

      sound: true,
    );
  }

  InitializationSettings initializationSettings() {
    //android initializationSettingAndroid
    var initializationSettingAndroid =
        const AndroidInitializationSettings('@mipmap/ic_launcher');
    const DarwinInitializationSettings initializationSettingIOS =
        DarwinInitializationSettings();

    //initializationSetting
    var initializationSettings = InitializationSettings(
        android: initializationSettingAndroid, iOS: initializationSettingIOS);
    return initializationSettings;
  }

  Future<Map<String, dynamic>> notificationDetails(
      RemoteMessage message, String imageUrl) async {
    String fileName = '';
    DefaultStyleInformation styleInformation;
    if (imageUrl.isNotEmpty || imageUrl != '') {
      fileName = path.basename(imageUrl);
      //save the file to local storage as bytes
      final bigPicture = await DownloadUtil.downloadAndSaveFile(
        imageUrl,
        fileName,
      );
      styleInformation = BigPictureStyleInformation(
        FilePathAndroidBitmap(bigPicture),
        largeIcon: FilePathAndroidBitmap(bigPicture),

        hideExpandedLargeIcon: true,
        contentTitle: message.notification!.title.toString(),
        summaryText: message.notification!.body.toString(),

        htmlFormatContent: true,
      );
    } else {
      styleInformation = BigTextStyleInformation(
        message.notification!.body.toString(),
        htmlFormatBigText: true,
        contentTitle: message.notification!.title.toString(),
        htmlFormatContent: true,
      );
    }
    DarwinNotificationDetails iosNotificationDetails =
        const DarwinNotificationDetails();
    AndroidNotificationDetails androidNotificationDetails =
        AndroidNotificationDetails(
      'com.example.arduino_bt_application',
      'com.example.arduino_bt_application',
      importance: Importance.max,
      priority: Priority.max,
      enableLights: true,
      playSound: true,
      sound: const RawResourceAndroidNotificationSound("notification_sound_android.mp3"),
      ticker: 'ticker',
      largeIcon: FilePathAndroidBitmap(imageUrl),
      styleInformation: styleInformation,
    );
    NotificationDetails notificationDetails = NotificationDetails(
        android: androidNotificationDetails, iOS: iosNotificationDetails);
    return {"notificationDetails": notificationDetails, "fileName": fileName};
  }
}
