import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:savbill/pages/customer/model/response/active_product_res.dart';
import 'package:savbill/pages/customer/model/response/customer_title_res.dart';
import 'package:savbill/pages/customer/model/response/services_area_res.dart';
import 'package:savbill/pages/inventory/module/response/active_partner_list_res.dart';
import 'package:savbill/pages/inventory/module/response/bulk_consumption_inward_res.dart';
import 'package:savbill/pages/inventory/module/response/item_type_res.dart';
import 'package:savbill/pages/inventory/module/response/ownership_res.dart';
import 'package:savbill/pages/inventory/module/response/partner_list_res.dart';
import 'package:savbill/pages/inventory/module/response/staff_user_list_res.dart';
import 'package:savbill/pages/inventory/module/response/status_res.dart';
import 'package:savbill/pages/inventory/module/response/view_pop_list_res.dart';
import 'package:savbill/pages/inventory/module/response/ware_house_list_res.dart';
import 'package:savbill/pages/inventory/module/response/warranty_status_res.dart';
import 'package:savbill/pages/login/model/response/demo_graphic_mapping_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:intl/intl.dart';

mixin Utils {
  static double cm1To1Feet = 0.0328084;
  static double kg1To1lbs = 2.20462;
  static double cm1To1Inches = 0.3937;

  static var minYear = 1960;
  static var minMonth = 1;
  static var minDay = 1;
  static var customerPrepaid;
  static var customerPostpaid;

  static List<ProductDetail>? productListForFilter = [];
  static List<BulkConsumptionInward>? inwardListForFilter = [];
  static List<OwnershipDetail>? ownershipListForFilter = [];
  static List<StatusDetail>? statusListForFilter = [];
  static List<ItemTypeDetail>? itemTypeListForFilter = [];
  static List<WarrantyStatusDetail>? warrantyStatusListForFilter = [];
  static List<WareHouseDetail>? wareHouseList = [];
  static List<StaffUserDetail>? staffUserList = [];
  static List<PartnerDetail>? partnerList = [];
  static List<ActivePartnerDataList>? activePartnerList = [];
  static List<PopDetail>? popList = [];
  static List<ServicesAreaDetail>? servicesAreaList = [];
  static List<CustomerTitle>? customerTitleList = [];
  static RxList aclPermissionList = [].obs;
  // var permissionList = <dynamic>[].obs;
// get mood of screen
  static bool isLightMode(BuildContext context) {
    return Theme.of(context).brightness == Brightness.light;
  }

  GetStorage getStorage = GetStorage();


  static String getStringTranslations(String key) {
    try {
      if (key != '') {
        return key.tr;
      } else {
        return '';
      }
    } catch (e) {
      return '';
    }
  }


  static showSnackbar(title, message, colorText, colorBg) {
    Get.snackbar(title, message,
        snackPosition: SnackPosition.BOTTOM,
        backgroundColor: colorBg,
        colorText: colorText,
        duration: const Duration(seconds: 2),
        margin: const EdgeInsets.all(Constant.SMALL_PADDING));
  }




  static List<String> getSplitTime(String time) {
    return time.split(":");
  }

  static String durationToMinutesHours(int minutes) {
    var d = Duration(minutes: minutes);
    List<String> parts = d.toString().split(':');
    return '${parts[0].padLeft(2, '0')}:${parts[1].padLeft(2, '0')}';
  }

  static String changeDateFormat(String date, String dtFormat) {
    DateFormat dateFormat = DateFormat(dtFormat);
    DateTime dateTimeLocal = DateTime.parse(date).toUtc().toLocal();
    return dateFormat.format(dateTimeLocal);
  }

  static getDrawerListData() {
    var list = [
      Strings.dashboard,
      /*  Strings.master_management,
      Strings.product_management,
      Strings.partner_management,*/
      Strings.lead_management,
      Strings.prepaid_customer,
      Strings.postpaid_customer,
      // Strings.invoice_system,
      Strings.payment_system,
      Strings.credit_note,
      Strings.inventory_management,
      Strings.ticketing_system,

      Strings.network_management,
      //  Strings.setting,
      //  Strings.audit,
      Strings.logoff
    ];
    return list;
  }


  static getMonthListData() {
    var list = [
      Strings.January,
      Strings.February,
      Strings.March,
      Strings.April,
      Strings.May,
      Strings.June,
      Strings.July,
      Strings.August,
      Strings.September,
      Strings.October,
      Strings.November,
      Strings.December
    ];
    return list;
  }


  static getTitle() {
    List<String> list = [
      Strings.dr,
      Strings.mr,
      Strings.miss,
      Strings.mrs,
    ];
    return list;
  }


  static getDurationUnits() {
    List<String> list = [
      Strings.hours,
      Strings.days,
      Strings.months,
      Strings.years,
    ];
    return list;
  }

  static InputDecoration ddlDecoration({String? title, Color? fillColor,bool? disable}) {
    return InputDecoration(
      enabled: disable ?? false,
        filled: true,
        contentPadding: const EdgeInsets.fromLTRB(
            Constant.SMALL_PADDING, 0, Constant.MEDIUM_PADDING, 0),
        fillColor: fillColor ?? AppTheme.colorWhite,
        hintStyle: AppTheme.dropdownHintStyle,
        labelStyle: AppTheme.dropdownLabelStyle,
        errorStyle: AppTheme.dropdownErrorStyle,
        alignLabelWithHint: true,
        border: OutlineInputBorder(
          borderRadius:
              BorderRadius.circular(Constant.DROP_DOWN_ROUNDED_CORNER),
          borderSide: BorderSide(color: AppTheme.colorBlack, width: 0.8),
        ),
        focusColor: Colors.transparent,
        focusedBorder: OutlineInputBorder(
          borderRadius:
              BorderRadius.circular(Constant.DROP_DOWN_ROUNDED_CORNER),
          borderSide: BorderSide(color: AppTheme.colorPrimary, width: 0.8),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius:
              BorderRadius.circular(Constant.DROP_DOWN_ROUNDED_CORNER),
          borderSide: BorderSide(
            color: AppTheme.colorBlack,
            width: 1.0,
          ),
        ),
        errorMaxLines: 3);
  }

  // for the get file size
  static getFileSize(String? filepath, int decimals) async {
    File file = File(filepath!);
    int bytes = file.readAsBytesSync().lengthInBytes;
    if (bytes <= 0) return "0";
    return bytes / 1024;
  }


  static updateAclEntry(List<dynamic> aclEntries)  async {
    aclPermissionList.assignAll(aclEntries);
  }


  // Assuming 'data' is a List<Map<String, dynamic>> where each map has 'newName' and 'validationRegex' keys

  static masterData(List<Demographicmappingtable>? data) {
    if (data != null) {
      var country = data.isNotEmpty && data[0].newName != null ? data[0].newName : 'Country';
      var state = data.length > 1 && data[1].newName != null ?   data[1].newName : 'State';
      var city = data.length > 2 && data[2].newName != null ?   data[2].newName : 'City';
      var pincode = data.length > 3 && data[3].newName != null  ? data[3].newName : 'Pincode';
      var area = data.length > 4 && data[4].newName != null  ? data[4].newName : 'Area';
      var mvno = data.length > 5 && data[5].newName != null ?  data[5].newName : 'MVNO';
      var regex = data.length > 3 && data[3].validationRegex != null ?  data[3].validationRegex : 'Number';
      customerPrepaid = data.length > 6 && data[6].newName != null ? data[6].newName : 'Prepaid Customer';
      customerPostpaid = data.length > 7 && data[7].newName != null ? data[7].newName : 'Postpaid Customer';

      // Logging the values (Dart does not have console.log, use print instead)
      print('Country: $country');
      print('State: $state');
      print('City: $city');
      print('Pincode: $pincode');
      print('Area: $area');
      print('MVNO: $mvno');
      print('Regex: $regex');
      print('Customer Prepaid: $customerPrepaid');
      print('Customer Postpaid: $customerPostpaid');
    }
  }

}
