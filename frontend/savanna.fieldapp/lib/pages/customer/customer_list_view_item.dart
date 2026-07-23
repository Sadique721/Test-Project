import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/acl_constant.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/permission_service.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/status_bg_view.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

class CustomerListViewItem extends StatelessWidget {
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
  CustomerDetail item;
  int index;
  String? custType;
  final Function()? onTapEdit;
  final Function()? onTapDelete;
  final Function()? onTapDocumentUpload;
  final Function()? onTapNearByDevice;

  // final Function()? onTapSendPaymentLink;
  final Function()? onTapAssignInventory;
  final Function()? onTapChangeStatus;

  // final Function()? onTapCustomerInvoicePayment;
  final Function()? onTapRenewPayment;
  final Function()? onTapNotes;


  CustomerListViewItem({
    Key? key,
    required this.index,
    required this.custType,
    required this.item,
    this.onTapEdit,
    this.onTapDelete,
    this.onTapDocumentUpload,
    this.onTapNearByDevice,
    // this.onTapSendPaymentLink,
    this.onTapAssignInventory,
    this.onTapChangeStatus,
    // this.onTapCustomerInvoicePayment,
    this.onTapRenewPayment,
    this.onTapNotes,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    Color? connectionModeBgColor = AppTheme.custUploadFileLight;
    String? serviceArea;
    if (item.connectionMode != null &&
        item.connectionMode == Strings.online) {
      connectionModeBgColor = AppTheme.onlineStatusBg;
    }else{
      connectionModeBgColor = AppTheme.offlineStatusBg;
    }
    /* if (item.networkDetails != null &&
        item.networkDetails!.serviceareaname != null &&
        item.networkDetails!.serviceareaname!.isNotEmpty) {
      serviceArea = item.networkDetails!.serviceareaname!;
    }*/
    return Container(
      margin: const EdgeInsets.only(
        bottom: Constant.MEDIUM_PADDING,
      ),
      child: Material(
        color: AppTheme.colorWhite,
        elevation: 0.5,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Expanded(
                    flex: 1,
                    child: Row(
                      children: [
                        CircleAvatar(
                          backgroundColor: backgroundColorArr[
                          index % backgroundColorArr.length],
                          radius: 18,
                          child: Text(
                            // !item.name!.isNullOrEmpty()
                            //     ? item.name![0].toUpperCase()
                            //     : "",
                            (item.name?.isNotEmpty ?? false) ? item.name![0].toUpperCase() : item.username![0].toUpperCase(),
                            style: TextStyle(
                                color:
                                textColorArr[index % textColorArr.length],
                                fontSize: AppTheme.medium,
                                fontWeight: FontWeight.bold),
                          ),
                        ),
                        const SizedBox(width: Constant.SMALL_PADDING),
                        Expanded(
                            child: CustomText(
                                title: item.name ?? "",
                                colors:
                                textColorArr[index % textColorArr.length],
                                textAlign: TextAlign.start,
                                fontSize: AppTheme.medium + 1,
                                maxLines: 2,
                                height: 1,
                                fontWeight: FontWeight.w500)),
                      ],
                    ),
                  ),
                  statusBgView(
                    status: item.connectionMode != null
                        ? item.connectionMode!
                        : Strings.offline,
                    bgColor: connectionModeBgColor,
                    textColor: (item.connectionMode != null &&
                        item.connectionMode! == Strings.online)
                        ? AppTheme.colorWhite
                        : AppTheme.title_dark,
                    fontSize: AppTheme.small,
                    fontWeight: FontWeight.normal,
                  ),
                ],
              ),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            cardDataRow(Strings.username, item.username ?? "-"),
            line(),
            cardDataRow(Strings.service_area, item.serviceArea ?? "-"),
            line(),
            cardDataRow(Strings.mobile_number, item.mobile ?? "-"),
            line(),
            cardDataRow(Strings.isp_name, item.mvnoName ?? "-"),
            line(),
            cardDataRow(Strings.account_number, item.acctno ?? "-"),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Row(mainAxisAlignment: MainAxisAlignment.spaceEvenly, children: [
              // add notes
              PermissionService().hasAclPermission([
                custType!.equalsIgnoreCase('Prepaid')
                    ? AclPreCustConstants.ADD_NOTES_PRE_CUST
                    : AclPostCustConstants.SEND_PAYMENT_LINK_POST_CUST
              ]) == true ?
              buttonView(
                  openTicketSVG, AppTheme.custEditLight, AppTheme.custEditDark,
                  onTapNotes!) : SizedBox.shrink(),


              PermissionService().hasAclPermission(
                  [custType!.equalsIgnoreCase('Prepaid')
                      ? AclPreCustConstants.EDIT_PRE_CUST
                      : AclPostCustConstants.EDIT_POST_CUST_LIST
                  ]) == true ? buttonView(
                  editSvg, AppTheme.custEditLight, AppTheme.custEditDark,
                  onTapEdit!) : SizedBox.shrink(),

              PermissionService().hasAclPermission(
                  [custType!.equalsIgnoreCase('Prepaid')
                      ? AclPreCustConstants.UPLOAD_DOCS_PRE_CUST
                      : AclPostCustConstants.UPLOAD_DOCUMENTS_POST_CUST_LIST
                  ]) == true ?
              buttonView(documentUploadSvg, AppTheme.custUploadFileLight,
                  AppTheme.custUploadFileDark, onTapDocumentUpload!) : SizedBox
                  .shrink(),

              /* buttonView(nearByDeviceSvg, AppTheme.custNearLocationLight,
                  AppTheme.custNearLocationDark, onTapNearByDevice!),*/
              PermissionService().hasAclPermission(
                  [custType!.equalsIgnoreCase('Prepaid')
                      ? AclPreCustConstants.PRE_CUST_NEAR_BY_DEVICE
                      : AclPostCustConstants.POST_CUST_NEAR_BY_DEVICE
                  ]) == true ?
              buttonView(nearByDeviceSvg, AppTheme.custEditLight,
                  AppTheme.custEditDark, onTapNearByDevice!) : SizedBox
                  .shrink(),
              // PermissionService().hasAclPermission(
              //     [custType!.equalsIgnoreCase('Prepaid')
              //         ? AclPreCustConstants.SEND_PAYMENT_LINK_PRE_CUST
              //         : AclPostCustConstants.SEND_PAYMENT_LINK_POST_CUST
              //     ]) == true ? buttonView(rupeeSvg, AppTheme.custPaymentLinkLight,
              //     AppTheme.custPaymentLinkDark, onTapSendPaymentLink!) : SizedBox.shrink(),


              /* buttonView(assignInventorySvg, AppTheme.custAssignInventoryLight,
                  AppTheme.custAssignInventoryDark, onTapAssignInventory!),*/

              PermissionService().hasAclPermission(
                  [custType!.equalsIgnoreCase('Prepaid')
                      ? AclPreCustConstants.CHANGE_STATUS_PRE_CUST
                      : AclPostCustConstants.CHANGE_STATUS_POST_CUST
                  ]) == true ?
              buttonView(statusSvg, AppTheme.custChangeStatusLight,
                  AppTheme.custChangeStatusDark, onTapChangeStatus!): SizedBox.shrink(),

              // buttonView(ticketPromiseToPaySvg, AppTheme.custAssignInventoryLight,
              //     AppTheme.custAssignInventoryDark, onTapCustomerInvoicePayment!),
              PermissionService().hasAclPermission(
                  [custType!.equalsIgnoreCase('Prepaid')
                      ? AclPreCustConstants.RENEW_PAYMENT_PRE_CUST
                      : AclPostCustConstants.SEND_PAYMENT_LINK_POST_CUST
                  ]) == true ?
              buttonView(customerRenewPaymentSvg, AppTheme.custDeleteLight,
                  AppTheme.custDeleteDark, onTapRenewPayment!) : SizedBox.shrink(),

            ]),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
          ],
        ),
      ),
    );
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

  cardDataRow(String? label, String? value) {
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
                    title: value!.isNotEmpty ? value : "-",
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
}
