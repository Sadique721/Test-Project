import 'dart:convert';
import 'dart:developer';
import 'dart:math' as math;
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:get/get.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'notification_setting.dart';

class FirebaseCloudMessagingService extends GetxController {
  final FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin =
      FlutterLocalNotificationsPlugin();

  Future<void> initialization() async {
    await requestPermission();
    await initNotificationInfo();
  }

  //request user for notification permission
  Future<void> requestPermission() async {
    NotificationSettings settings =
        await FirebaseNotificationSetting().notificationSetting();
    if (settings.authorizationStatus == AuthorizationStatus.authorized) {
      log("User Granted Permission");
    } else if (settings.authorizationStatus ==
        AuthorizationStatus.provisional) {
      log("User Granted Provisional Permission");
    } else if (settings.authorizationStatus == AuthorizationStatus.denied) {
      log("User Denied Permission");
    }
  }

//get a device token to send notification
  Future<String?> getToken() async {
    String fcmToken = '';
    try {
      await FirebaseMessaging.instance.getToken().then((token) {
        log("FCM Device Token: $token");
        fcmToken = token!;
        return token;
      });
    } catch (e) {
      log(e.toString());
      return e.toString();
    }
    return fcmToken;
  }

  //initialize flutter local notification
  Future<void> initNotificationInfo() async {
    InitializationSettings initializationSettings =
        FirebaseNotificationSetting().initializationSettings();

    //initialization
    await flutterLocalNotificationsPlugin.initialize(initializationSettings,
        onDidReceiveNotificationResponse:
            (NotificationResponse notificationResponse) async {
      print("tapped on the notification2");
      handleNotificationTappedFormNotificationTray(
          jsonDecode(notificationResponse.payload ?? "{}"));
    });
    listenFirebaseMessages();
  }

  Future setupInteractMessage() async {
    FirebaseMessaging.onMessageOpenedApp.listen((event) async {
// Get.snackbar("",jsonEncode(event.data));
      await handleNotificationTappedFormNotificationTray(event.data);
    });
  }

  void listenFirebaseMessages() {
    FirebaseMessaging.onMessage.listen((RemoteMessage message) async {
      int notificationId = math.Random().nextInt(100000);
      log("On Message : ${message.notification?.title}/${message.notification?.body}");
      log("Remote message ${message.data}");
      log("Handling a on message ${message.data}");
      String imageUrl = message.data['image'] ?? '';

      Map<String, dynamic> notificationDetails =
          await FirebaseNotificationSetting()
              .notificationDetails(message, imageUrl);
      // await flutterLocalNotificationsPlugin
      //     .show(
      //   notificationId,
      //   message.notification?.title,
      //   message.notification?.body,
      //   notificationDetails["notificationDetails"],
      //   payload: jsonEncode(message.data),
      //   //payload: message.data['body'],
      // )
      //     .then((value) async {
      //   if (imageUrl.isNotEmpty || imageUrl != '') {
      //     await DownloadUtil.deleteFile(notificationDetails["fileName"]);
      //   }
      // });
    });
  }

  Future<void> handleNotificationTappedFormNotificationTray(
      Map<String, dynamic> notificationData) async {
    // log("Notification Tapped: $payload");

    SharedPreferences sharedPreferences = await SharedPreferences.getInstance();
    await sharedPreferences.setString("key", jsonEncode(notificationData));

    // Parse the payload JSON string to a Map

    // Extract the notification type from the payload
    String? notificationType = notificationData["notification_type"];
    if (notificationType != null) {
      switch (notificationType) {
        case 'violation_created':
          // Get.showOverlay(
          //   asyncFunction: () async {
          //     VoilationsController violationsController =
          //     Get.isRegistered<VoilationsController>()
          //         ? Get.find<VoilationsController>()
          //         : Get.put(VoilationsController());
          //     Violation? violation = await violationsController
          //         .getViolationById(notificationData['violation_id'].toString());
          //     log('Visitor $violation');
          //     if (violation != null) {
          //       Get.to(ViolationDetailPage(violation: violation));
          //     }
          //   },
          //   loadingWidget: LoadingIndicator().waitingIndicator,
          // );
          break;
        default:
          // Handle other notification types or do nothing if necessary
          break;
      }
    }
  }

  Future<void> handleNotificationTapped(String? payload) async {
    // log("Notification Tapped: $payload");

    log("here i am from the notification : $payload");

    if (payload != null && payload.isNotEmpty) {
      // Parse the payload JSON string to a Map
      Map<String, dynamic> notificationData = jsonDecode(payload);

      // Extract the notification type from the payload
      String? notificationType = notificationData["notification_type"];
      final data = jsonDecode(notificationData['data']);
      log('Notification Data ${data['visitor_id']} ');
      if (notificationType != null) {
        switch (notificationType) {
          case 'violation_created':
            // Get.showOverlay(
            //   asyncFunction: () async {
            //     VoilationsController violationsController =
            //     Get.isRegistered<VoilationsController>()
            //         ? Get.find<VoilationsController>()
            //         : Get.put(VoilationsController());
            //     Violation? violation = await violationsController
            //         .getViolationById(data['violation_id'].toString());
            //     log('Visitor $violation');
            //     if (violation != null) {
            //       Get.to(ViolationDetailPage(violation: violation));
            //     }
            //   },
            //   loadingWidget: LoadingIndicator().waitingIndicator,
            // );

            // Get.toNamed('/voilations');
            break;
          default:
            // Handle other notification types or do nothing if necessary
            break;
        }
      }
    }
  }

  @override
  void onInit() async {
    //ask for permission
    await requestPermission();
    //listen message from firebase
    await initNotificationInfo();
    super.onInit();
  }
}
