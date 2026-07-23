import 'package:savbill/pages/dashboard/model/response/cust_plan_detail_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:intl/intl.dart';


class CafExpiredPlanListViewItem extends StatelessWidget {
  CustPlanDataList item;
  List<CustPlanDataList>? expiredPlanList;
  List<CustPlanDataList>? currentPlanList;
  int index;
  List backgroundColorArr = [
    AppTheme.colorGreenRoundView,
    AppTheme.colorRedRoundView,
    AppTheme.colorBlueRoundView,
    AppTheme.colorYellowRoundView
  ];
  List textColorArr = [
    AppTheme.colorGreenRView,
    AppTheme.colorRedRView,
    AppTheme.colorBlueRView,
    AppTheme.colorYellowRView
  ];


  Color? statusColor;
  String? displayStatus;
  UserDetail? userData;

  CafExpiredPlanListViewItem({Key? key,
    required this.index,
    required this.item,
    required this.expiredPlanList,
    required this.currentPlanList,

    this.userData})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    checkStatus(item.custPlanStatus, item.custServMappingStatus);
    return Stack(
      children: [
        Card(
          margin: const EdgeInsets.only(
              top: Constant.MEDIUM_PADDING,
              left: 0,
              right: 0,
              bottom: Constant.MEDIUM_PADDING),
          elevation: 2,
          color: AppTheme.colorWhite,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              nameRow(),
              cardDataRow(Strings.plan_name, item.planName ?? ""),
              line(),

              cardDataRow(Strings.validity, item.validity!.toInt().toString()),
              line(),
              // cardDataRow(Strings.plan_status, displayStatus!),

              Padding(
                padding: const EdgeInsets.all(Constant.MEDIUM_PADDING),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    CustomText(
                        title: Strings.plan_status,
                        colors: AppTheme.title_dark,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.small + 1,
                        height: 1,
                        fontWeight: FontWeight.w500),
                    const SizedBox(width: Constant.MEDIUM_PADDING),
                    Expanded(
                        child: Align(
                          alignment: Alignment.topRight,
                          child: Container(
                            padding: const EdgeInsets.symmetric(
                                horizontal: Constant.MEDIUM_PADDING,
                                vertical: Constant.VERY_SMALL_PADDING),
                            decoration: BoxDecoration(
                                borderRadius:
                                BorderRadius.circular(Constant.SMALL_PADDING),
                                color: statusColor),
                            child: CustomText(
                                title: displayStatus!.toUpperCase() ?? "-",
                                colors: AppTheme.colorWhite,
                                textAlign: TextAlign.end,
                                fontSize: AppTheme.small,
                                height: 1,
                                fontWeight: FontWeight.w400),
                          ),
                        ))
                  ],
                ),
              ),

              line(),
              cardDataRow(Strings.start_date,
                  getDateTimeFormat(item.dbStartDate.toString())),
              line(),
              cardDataRow(Strings.expiry_date,
                  getDateTimeFormat(item.dbEndDate.toString())),
              line(),

              cardDataRow(Strings.group, item.plangroup ?? ""),

            ],
          ),
        ),
        Positioned(
          top: Constant.MEDIUM_PADDING,
          left: Constant.SMALL_PADDING,
          child: Container(
            width: 80,
            height: Constant.VERY_SMALL_PADDING,
            color: textColorArr[index % textColorArr.length],
          ),
        ),
      ],
    );
  }

  nameRow() {
    return Padding(
        padding: const EdgeInsets.all(Constant.MEDIUM_PADDING),
        child: Row(
          children: [
            CircleAvatar(
              backgroundColor:
              backgroundColorArr[index % backgroundColorArr.length],
              radius: 15,
              child: Text(
                !item.planName!.isNullOrEmpty()
                    ? item.planName![0].toUpperCase()
                    : "",
                style: TextStyle(
                    color: textColorArr[index % textColorArr.length],
                    fontSize: AppTheme.large,
                    fontWeight: FontWeight.bold),
              ),
            ),
            const SizedBox(width: Constant.SMALL_PADDING),
            Expanded(
                child: CustomText(
                    title: item.planName!,
                    colors: textColorArr[index % textColorArr.length],
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.medium + 1,
                    maxLines: 2,
                    height: 1,
                    fontWeight: FontWeight.w500)),
          ],
        ));
  }

  cardDataRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.all(Constant.MEDIUM_PADDING),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          CustomText(
              title: label,
              colors: AppTheme.title_dark,
              textAlign: TextAlign.start,
              fontSize: AppTheme.small + 1,
              height: 1,
              fontWeight: FontWeight.w500),
          const SizedBox(width: Constant.MEDIUM_PADDING),
          Expanded(
              child: Align(
                alignment: Alignment.topRight,
                child: CustomText(
                    title: value.isNotEmpty ? value : "-",
                    colors: AppTheme.lable_noramal,
                    textAlign: TextAlign.end,
                    fontSize: AppTheme.small,
                    height: 1,
                    fontWeight: FontWeight.w400),
              ))
        ],
      ),
    );
  }

  line() {
    return SizedBox(
      width: double.infinity,
      child: Divider(
        color: Colors.grey[300],
        height: 0.5,
      ),
    );
  }

  checkStatus(String? custPlanStatus, String? custServMappingStatus) {
    String? status = custPlanStatus!.toLowerCase();
    String? statusWorkflow = custServMappingStatus != null
        ? custServMappingStatus.toLowerCase()
        : "";
    if (statusWorkflow == "new activation" || statusWorkflow == "rejected") {
      if (statusWorkflow == "new activation") {
        statusColor = AppTheme.statusApprove;
      } else {
        statusColor = AppTheme.colorRed;
      }
      displayStatus = custServMappingStatus!.toLowerCase();
    } else {
      displayStatus = custPlanStatus.toLowerCase();
      switch (status) {
        case "active":
        case "ingrace":
          return statusColor = AppTheme.statusApprove;
        case "terminate":
        case "stop":
        case "inactive":
        case "expired":
          return statusColor = AppTheme.colorRed;
        case "hold":
        case "disable":
          return statusColor = AppTheme.colorGrey;
        default:
          return;
      }
    }
    return true;
  }

}

String getDateTimeFormat(String? startEndExpiryDate) {
  DateTime date =
  DateFormat(Constant.DATE_TIME_FORMAT_API).parse(startEndExpiryDate!);
  return DateFormat(Constant.API_DATE_TIME_FORMAT).format(date);
}

String remainingDuration(String endDate) {
  DateTime todayDate = DateTime.now();
  DateTime endDay = DateTime.parse(endDate);
  var remainingDays = todayDate.difference(endDay);
  return remainingDays.toString();
}


isPromiseToPayInExpired(int? customerServiceMappingId,
    List<CustPlanDataList> expiredPlanList,
    List<CustPlanDataList>currentPlanList) {
  if ((expiredPlanList != null && expiredPlanList.isNotEmpty) ||
      (currentPlanList != null && currentPlanList.isNotEmpty)
  ) {
    return (expiredPlanList
        .where((element) =>
    element.customerServiceMappingId == customerServiceMappingId).isEmpty
        && currentPlanList
            .where((element) =>
        element.customerServiceMappingId == customerServiceMappingId).isEmpty);
  }
  return false;
}

getSerialNumber(CustPlanDataList? item) {
  String? serialNumber;
  if(item!.customerInventorySerialnumberDtos!.isNotEmpty || item.customerInventorySerialnumberDtos !=null ) {
    for (var element in item.customerInventorySerialnumberDtos!) {
      if (element.primary == true) {
        serialNumber = element.serialNumber;
      }
    }
    return serialNumber;
  }
}

buttonView(String btnName, Color bgColor, Color txtColor, Function() onTap) {
  return InkWell(
    onTap: onTap,
    child: Material(
      elevation: 1.5,
      color: bgColor,
      shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
      child: Container(
        height: Constant.BTN_HEIGHT_M - 5,
        width: Constant.BTN_HEIGHT_M - 5,
        alignment: Alignment.center,
        padding: const EdgeInsets.all(Constant.SMALL_PADDING - 1),
        child: SvgPicture.asset(
          btnName,
          height: Constant.ICON_SIZE,
          width: Constant.ICON_SIZE,
          color: txtColor,
          fit: BoxFit.fill,
        ),
      ),
    ),
  );
}


// hasOperationPermission(classId, operationId, accessIdForAllOpreation,
//     UserDetail userData) {
//   // var RoleAdmin = userData.getItem("userRoles");
//   var roleAdmin = userData.userRoles;
//   var permissionList = [];
//   let permissionList = localStorage.getItem("userRoleOperationPermission");
//   if (permissionList.length > 0) {
//     this.permissionList =
//         JSON.parse(localStorage.getItem("userRoleOperationPermission"));
//   }
//   if (RoleAdmin === "1") {
//     return true;
//   }
//   if (this.permissionList.length > 0) {
//     for (let permission of this.permissionList) {
//       let isPersmissionList = permission.operations.filter(
//           item =>
//           (item.opid === accessIdForAllOpreation || item.opid ===operationId) &&
//           item.classid === classId
//   );
//   if (isPersmissionList.length != 0) {
//   // console.log("true");
//   return true;
//   }
//   }
//   return false;
//   } else {
//   this.messageService.add({
//   severity: "error",
//   summary: "Restriction",
//   detail: "Sorry you have not privilege to any operation!",
//   icon: "far fa-times-circle",
//   });
//   }
// }
