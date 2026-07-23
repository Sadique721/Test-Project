import 'package:savbill/pages/shift_location/approve_reject_shift_location_dialog.dart';
import 'package:savbill/pages/shift_location/create_shift_location.dart';
import 'package:savbill/pages/shift_location/customer_shift_locaiton_work_flow.dart';
import 'package:savbill/pages/shift_location/request/approve_customer_address_req.dart';
import 'package:savbill/pages/shift_location/response/new_address_shift_location_res.dart';
import 'package:savbill/pages/shift_location/shift_location_controller.dart';
import 'package:savbill/pages/shift_location/view_shift_location_item.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

import '../../util/resources.dart';

class ShiftLocation extends StatefulWidget {
  @override
  _ShiftLocationState createState() => _ShiftLocationState();
}

class _ShiftLocationState extends State<ShiftLocation>
    implements ApproveRejectShiftBtnAction {
  final shiftLocationController = Get.put(ShiftLocationController());

  final shiftLocationFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

  @override
  void initState() {
    super.initState();
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<ShiftLocationController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: shiftLocationController.isLoading),
      ]);
    });
  }

  _body() {

    return Container(
      padding: const EdgeInsets.all(Constant.SMALL_PADDING),
      child: Container(
          color: AppTheme.colorBG,
          width: MediaQuery.of(context).size.width,
          child: Container(
            color: AppTheme.colorBG,
            width: MediaQuery.of(context).size.width,
            child: SingleChildScrollView(
              child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                    Container(
                      padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
                      child: Row(
                        crossAxisAlignment: CrossAxisAlignment.center,
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Expanded(
                            child: CustomText(
                              title: shiftLocationController.customerDetail != null
                                  ? "${shiftLocationController.customerDetail?.title?.capitalizeFirst} ${shiftLocationController.customerDetail!.custname} ${Strings.my_address}"
                                  : "",
                              colors: AppTheme.colorBlack,
                              textAlign: TextAlign.start,
                              fontSize: AppTheme.medium + 1,
                              fontWeight: FontWeight.w500,
                              maxLines: 2, // allow wrapping into 2 lines
                            ),
                          ),
                          const SizedBox(
                            width: Constant.VERY_SMALL_PADDING,
                          ),
                          InkWell(
                            onTap: () {
                              if(shiftLocationController.disableShiftButton == false) {
                                addShiftLocationScreen();
                              }
                            },
                            child: Container(
                              padding: const EdgeInsets.only(
                                  top: Constant.SMALL_PADDING,
                                  bottom: Constant.SMALL_PADDING,
                                  left: Constant.SMALL_PADDING,
                                  right: Constant.SMALL_PADDING),
                              // height: Constant.CARD_BOTTOM_BUTTON_H,
                              alignment: Alignment.center,
                              decoration: BoxDecoration(
                                color: shiftLocationController.disableShiftButton == true ? AppTheme.colorDisableGray: AppTheme.colorPrimary,
                                borderRadius: const BorderRadius.all(
                                    Radius.circular(Constant.ROUNDED_CORNER)),
                              ),
                              child: Row(
                                // crossAxisAlignment: CrossAxisAlignment.center,
                                mainAxisAlignment: MainAxisAlignment.center,
                                children: [
                                  const Padding(
                                    padding: EdgeInsets.only(
                                        left: Constant.VERY_SMALL_PADDING,
                                        right: Constant.VERY_SMALL_PADDING),
                                    child: Icon(
                                      size: Constant.ICON_SIZE_M,
                                      Icons.add_circle,
                                      color: Colors.white,
                                    ),
                                  ),
                                  CustomText(
                                    title: Strings.shift_location,
                                    colors: AppTheme.colorWhite,
                                    fontSize: AppTheme.small,
                                    textAlign: TextAlign.center,
                                    fontWeight: FontWeight.normal,
                                  )
                                ],
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                    (shiftLocationController.newCustomerAddressDatList != null &&
                            shiftLocationController
                                .newCustomerAddressDatList!.isNotEmpty)
                        ? ListView.builder(
                          shrinkWrap: true,
                            scrollDirection: Axis.vertical,
                            itemCount: shiftLocationController
                                .newCustomerAddressDatList!.length,
                            itemBuilder: (context, index) {
                              NewcustomerAddress item =
                                  shiftLocationController
                                      .newCustomerAddressDatList![index];
                              return InkWell(
                                // onTap: () {
                                //   openTicketDetailScreen(item.caseId);
                                // },
                                child: ViewShiftLocationItem(
                                  item: item,
                                  controller: shiftLocationController,
                                  onTapPick: () {},
                                  onTapApprove: () {
                                    shiftLocationController
                                        .pickBtnDisableFlag = "approved";
                                    addRemarkInvoiceDialog(
                                        context,
                                        Strings.approve,
                                        shiftLocationController,
                                        item);
                                  },
                                  onTapReject: () {
                                    shiftLocationController
                                        .pickBtnDisableFlag = "Rejected";
                                    addRemarkInvoiceDialog(
                                        context,
                                        Strings.reject,
                                        shiftLocationController,
                                        item);
                                  },
                                  onTapWorkFlow: () {
                                    openShiftLocationWorkFlow(item.id);
                                  },
                                  onTapReassign: () {
                                    shiftLocationController.entityId =
                                        item.id;
                                    // item.id
                                    shiftLocationController
                                        .reassignWorkflowGetStaff(
                                            item.id, "SHIFT_LOCATION");
                                  },
                                ),
                              );
                            })
                        : noDataFound()
                  ]),
            ),
          )),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.shift_location, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  addShiftLocationScreen() async {
    var result = await Get.to(CreateShiftLocation(), arguments: {
      Constant.CUSTOMER_DETAIL: shiftLocationController.customerDetail,
      Constant.CUSTOMER_TYPE: shiftLocationController.customerType,
      Constant.CUST_TYPE: shiftLocationController.custType
    });

    if (result != null && result == true) {
      shiftLocationController.getNewAddressShiftLocation();
    }
  }

  buttonView(String btnName, Color bgColor, Color txtColor, Function()? onTap) {
    return InkWell(
      onTap: onTap,
      child: Material(
        elevation: 3.0,
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
            height: Constant.ICON_SIZE + 5,
            width: Constant.ICON_SIZE + 5,
            color: txtColor,
            fit: BoxFit.fitWidth,
          ),
        ),
      ),
    );
  }

  basicDetailItem(
      String title1, String? value1, String title2, String? value2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        Expanded(
          flex: 2,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title1),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value1),
            ],
          ),
        ),
        Expanded(
          flex: 1,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title2),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value2),
            ],
          ),
        ),
      ],
    );
  }

  titleWidget(String title) {
    return CustomText(
      title: title,
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.verySmall,
      fontWeight: FontWeight.w400,
      maxLines: 2,
    );
  }

  valueWidget(String? value) {
    return CustomText(
      title: value!.isNotEmpty ? value : "",
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.normal,
      maxLines: 2,
    );
  }

  addRemarkInvoiceDialog(BuildContext context, String? pageName,
      ShiftLocationController? controller, NewcustomerAddress? item) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return ApproveRejectShiftLocationDialog(
              pageName: pageName,
              controller: controller,
              approveRejectShiftBtnAction: this,
              approveCustomerAddressReq: ApproveCustomerAddressReq(
                addressId: item!.id,
                nextStaffId: 0,
                staffId: controller?.userDetail!.userId.toString(),
              ));
        });
  }

  openShiftLocationWorkFlow(int? eventId) async {
    var result =
        await Get.to(const CustomerShiftLocationWorkFlow(), arguments: {
      Constant.ID: eventId,
      Constant.EVENT_TYPE: "SHIFT_LOCATION"
      // Constant.
    });
    if (result != null && result == true) {
      shiftLocationController.getNewAddressShiftLocation();
      // inventoryDetailController.getTeamHierarchyApprovalFlow(eventId);
    }
  }

  @override
  void approveRejectShiftDetails(
      {String? identifier,
      TextEditingController? remarkController,
      ApproveCustomerAddressReq? approveCustomerAddressReq}) {
    Get.back();
    if (identifier != null && identifier.equalsIgnoreCase(Strings.approve)) {
      // creditNoteController.approveRejectCreditPayment(Strings.approve.toLowerCase(),paymentApproveRejectReq!,context);
      shiftLocationController.getApproveCustomerShiftLocationAddress(
          Strings.approve, approveCustomerAddressReq, context);
    } else if (identifier != null &&
        identifier.equalsIgnoreCase(Strings.reject)) {
      // creditNoteController.approveRejectCreditPayment(Strings.reject.toLowerCase(),paymentApproveRejectReq!,context);
      shiftLocationController.getApproveCustomerShiftLocationAddress(
          Strings.reject, approveCustomerAddressReq, context);
    }
  }
}
