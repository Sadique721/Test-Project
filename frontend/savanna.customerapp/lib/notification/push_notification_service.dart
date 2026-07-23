import 'dart:developer';

import 'package:savbill/routes/app_routes.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:get/get.dart';
import 'package:open_file/open_file.dart';

/// ✅ TOP‑LEVEL background notification callback
@pragma('vm:entry-point')
void notificationTapBackground(NotificationResponse notificationResponse) {
  log("onDidReceiveBackgroundNotificationResponse >> ${notificationResponse.payload}");
  if (notificationResponse.payload != null && notificationResponse.payload!.isNotEmpty) {
    OpenFile.open(notificationResponse.payload!);
  }
}

class PushNotificationService {
  Future<void> setupInteractedMessage() async {
    await Firebase.initializeApp();

    FirebaseMessaging.onMessageOpenedApp.listen((RemoteMessage message) {
      if (message.data['click_action'] == 'FLUTTER_NOTIFICATION_CLICK') {
        Get.toNamed(AppRoutes.CONTACT);
      } else {
        Get.toNamed(AppRoutes.INVENTORY_HOME);
      }
    });

    FirebaseMessaging.instance.setForegroundNotificationPresentationOptions(
      alert: true,
      badge: true,
      sound: true,
    );

    enableIOSNotifications();
    await registerNotificationListeners();
  }

  Future<void> registerNotificationListeners() async {
    final AndroidNotificationChannel channel = androidNotificationChannel();
    final FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin =
    FlutterLocalNotificationsPlugin();

    await flutterLocalNotificationsPlugin
        .resolvePlatformSpecificImplementation<
        AndroidFlutterLocalNotificationsPlugin>()
        ?.createNotificationChannel(channel);

    const AndroidInitializationSettings androidSettings =
    AndroidInitializationSettings('@mipmap/ic_launcher');

    const DarwinInitializationSettings iOSSettings = DarwinInitializationSettings(
      requestSoundPermission: false,
      requestBadgePermission: false,
      requestAlertPermission: false,
    );

    const InitializationSettings initSettings =
    InitializationSettings(android: androidSettings, iOS: iOSSettings);

    await flutterLocalNotificationsPlugin.initialize(
      initSettings,
      // Foreground tap
      onDidReceiveNotificationResponse: (NotificationResponse details) {
        log("onDidReceiveNotificationResponse >> ${details.payload}");
        if (details.payload != null && details.payload!.isNotEmpty) {
          OpenFile.open(details.payload!);
        }
      },
      // ✅ Background tap (top-level function)
      onDidReceiveBackgroundNotificationResponse: notificationTapBackground,
    );

    // Foreground messages
    FirebaseMessaging.onMessage.listen((RemoteMessage? message) async {
      if (message == null) return;

      log("onMessage>>>> ${message.data['click_action']}");
      if (message.data['click_action'] == 'FLUTTER_NOTIFICATION_CLICK') {
        Get.toNamed(AppRoutes.PLAN_DETAIL);
      } else {
        Get.toNamed(AppRoutes.LOGIN);
      }

      final RemoteNotification? notification = message.notification;
      final AndroidNotification? android = message.notification?.android;
      if (notification != null && android != null) {
        flutterLocalNotificationsPlugin.show(
          notification.hashCode,
          notification.title,
          notification.body,
          NotificationDetails(
            android: AndroidNotificationDetails(
              channel.id,
              channel.name,
              channelDescription: channel.description,
              icon: android.smallIcon,
            ),
          ),
        );
      }
    });
  }

  Future<void> enableIOSNotifications() async {
    await FirebaseMessaging.instance.setForegroundNotificationPresentationOptions(
      alert: true,
      badge: true,
      sound: true,
    );
  }

  AndroidNotificationChannel androidNotificationChannel() =>
      const AndroidNotificationChannel(
        'high_importance_channel',
        'High Importance Notifications',
        description: 'This channel is used for important notifications.',
        importance: Importance.max,
        playSound: true,
        enableLights: true,
        enableVibration: true,
        showBadge: true,
      );
}
