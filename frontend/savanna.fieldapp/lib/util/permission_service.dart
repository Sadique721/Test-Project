import 'dart:convert';
import 'dart:developer';
import 'dart:io';

import 'package:savbill/pages/login/model/response/get_acl_entry_res.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:device_info_plus/device_info_plus.dart';
import 'package:get_storage/get_storage.dart';
import 'package:permission_handler/permission_handler.dart';

class PermissionService {
  Future<bool> cameraAndStoragePermission() async {
    // You can request multiple permissions at once.
    if (Platform.isIOS) {
      Map<Permission, PermissionStatus> statuses =
          await [Permission.camera, Permission.storage].request();
      if (statuses[Permission.camera] == PermissionStatus.granted &&
          statuses[Permission.storage] == PermissionStatus.granted) {
        return true;
      } else {
        return false;
      }
    } else {

      final androidInfo = await DeviceInfoPlugin().androidInfo;
      final sdkInt = androidInfo.version.sdkInt;
      if (sdkInt > 32) {
        Map<Permission, PermissionStatus> statuses =
            await [Permission.camera, Permission.photos].request();
        if (statuses[Permission.camera] == PermissionStatus.granted &&
            statuses[Permission.photos] == PermissionStatus.granted) {
          return true;
        } else {
          return false;
        }
      } else {
        Map<Permission, PermissionStatus> statuses = await [
          Permission.camera,
          Permission.storage,
        ].request();
        if (statuses[Permission.camera] == PermissionStatus.granted &&
            statuses[Permission.storage] == PermissionStatus.granted) {
          return true;
        } else {
          return false;
        }
      }
    }
  }

  Future<bool> locationPermission() async {
    Map<Permission, PermissionStatus> statuses = await [
      Permission.location,
    ].request();

    if (statuses[Permission.location] == PermissionStatus.granted) {
      return true;
    } else {
      return false;
    }
  }

  Future<bool> alwaysLocationPermission() async {
    // You can request multiple permissions at once.
    Map<Permission, PermissionStatus> statuses = await [
      Permission.locationAlways,
      Permission.locationWhenInUse
    ].request();
    if (statuses[Permission.locationAlways] == PermissionStatus.granted ||
        statuses[Permission.locationWhenInUse] == PermissionStatus.granted) {
      return true;
    } else {
      return false;
    }
  }

  Future<bool> storagePermission() async {
    // You can request multiple permissions at once.
    Map<Permission, PermissionStatus> statuses = await [
      Permission.storage,
    ].request();
    if (statuses[Permission.storage] == PermissionStatus.granted) {
      return true;
    } else {
      return false;
    }
  }

  Future<bool> contactPermission() async {
    Map<Permission, PermissionStatus> statuses =
        await [Permission.contacts].request();

    if (statuses[Permission.contacts] == PermissionStatus.granted) {
      return true;
    } else {
      return false;
    }
  }

  Future<bool> requestCameraAndStoragePermission(
      {required Function onPermissionDenied,
      required Function onPermissionSuccess}) async {
    var granted = await cameraAndStoragePermission();
    if (!granted) {
      onPermissionDenied();
    } else {
      onPermissionSuccess();
    }
    return granted;
  }

  Future<bool> requestLocationPermission(
      {required Function onPermissionDenied, required Function onPermissionSuccess}) async {

    var granted = await locationPermission();

    log("granted:::$granted");
    if (!granted) {
      onPermissionDenied();
    } else {
      onPermissionSuccess();
    }
    return granted;
  }

  Future<bool> requestAlwaysLocationPermission(
      {required Function onPermissionDenied,
      required Function onPermissionSuccess}) async {
    var granted = await alwaysLocationPermission();
    if (!granted) {
      onPermissionDenied();
    } else {
      onPermissionSuccess();
    }
    return granted;
  }

  Future<bool> requestStoragePermission(
      {required Function onPermissionDenied,
      required Function onPermissionSuccess}) async {
    var granted = await storagePermission();
    if (!granted) {
      onPermissionDenied();
    } else {
      onPermissionSuccess();
    }
    return granted;
  }

  Future<bool> requestContactPermission(
      {required Function onPermissionDenied,
      required Function onPermissionSuccess}) async {
    var granted = await contactPermission();
    if (!granted) {
      onPermissionDenied();
    } else {
      onPermissionSuccess();
    }
    return granted;
  }

  Future<bool> hasPermission(Permission permission) async {
    var permissionStatus = await permission.status;
    return permissionStatus == PermissionStatus.granted;
  }

  // bool hasAclPermission(dynamic itemCodes) {
  //   final List<GetAclDataList> rolePermissions = GetStorage().read(Constant.ACL_ENTRIES);
  //   log("rolePermissions==>${jsonEncode(rolePermissions)}");
  //   if (rolePermissions.isNotEmpty) {
  //    for (var element in rolePermissions) {
  //      if(element.code!.equalsIgnoreCase(itemCodes)){
  //        return true;
  //      }
  //    }
  //     // return rolePermissions.any((item) => itemCodes.contains(item['code']));
  //   }
  //   return;
  // }



 bool? hasAclPermission(dynamic itemCodes) {
    final rolePermissions = GetStorage().read(Constant.ACL_ENTRIES);
    if (rolePermissions != null) {
      final List<dynamic> permissions = jsonDecode(rolePermissions);
      return permissions.any((item) {
        return itemCodes!.contains(item['code']);
      });
    }
    return false;
  }
}
